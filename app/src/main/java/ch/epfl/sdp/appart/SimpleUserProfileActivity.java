package ch.epfl.sdp.appart;

import static android.widget.Toast.makeText;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import ch.epfl.sdp.appart.utils.PermissionRequest;
import javax.inject.Inject;

import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

import java.util.List;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.local.LocalDatabaseService;
import ch.epfl.sdp.appart.glide.visitor.GlideImageViewLoader;
import ch.epfl.sdp.appart.user.User;
import ch.epfl.sdp.appart.user.UserViewModel;
import ch.epfl.sdp.appart.utils.ActivityCommunicationLayout;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SimpleUserProfileActivity extends AppCompatActivity {

    /* temporary user */
    private Pair<User, Boolean> advertiserUser;

    /* User ViewModel */
    UserViewModel mViewModel;

    @Inject
    DatabaseService database;
    @Inject
    LocalDatabaseService localdb;

    /* UI components */
    private EditText nameText;
    private EditText ageText;
    private EditText phoneNumberText;
    private EditText genderText;
    private TextView uniAccountClaimer;
    private TextView emailTextView;
    private ImageView imageView;
    private Button contactButton;

    private final static int PHONE_CALL_PERMISSION_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_user_profile);

        /* User ViewModel initialization */
        mViewModel = new ViewModelProvider(this).get(UserViewModel.class);


        /* UI components initialisation */
        this.nameText = findViewById(R.id.name_SimpleUserProfile_editText);
        this.ageText = findViewById(R.id.age_SimpleUserProfile_editText);
        this.emailTextView = findViewById(R.id.emailText_SimpleUserProfile_textView);
        this.phoneNumberText = findViewById(R.id.phoneNumber_SimpleUserProfile_editText);
        this.genderText = findViewById(R.id.gender_SimpleUserProfile_editText);
        this.uniAccountClaimer = findViewById(R.id.uniAccountClaimer_SimpleUserProfile_textView);
        this.imageView = findViewById(R.id.profilePicture_SimpleUserProfile_imageView);
        contactButton = findViewById(R.id.contact_SimpleUserProfile_button);

        contactButton.setVisibility(View.INVISIBLE);
        String advertiserId =
                getIntent().getStringExtra(ActivityCommunicationLayout.PROVIDING_USER_ID);

        /* get user from database from user ID */
        mViewModel.getUser(advertiserId)
                .exceptionally(e -> {
                    Log.d("SIMPLEUSER", "Failed to get the user");
                    return null;
                });
        mViewModel.getUser().observe(this, this::setAdUserToLocal);
    }

    /**
     * Contact announcer.
     */
    public void openEmailOrPhone(View view){
        if(!advertiserUser.first.getPhoneNumber().isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("How did you prefer contact the announcer ?");
            builder.setPositiveButton("Contact via Email", (dialog, which) -> onEmail());
            builder.setNeutralButton("Contact via phone number", (dialog, which) -> onCall());
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            onEmail();
        }
    }

    private void onEmail() {


        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL,  new String[]{advertiserUser.first.getUserEmail()});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Rent apartment");
        try {
            startActivity(intent);
        } catch (Exception e) {
            makeText(this, "Error open email, try again",Toast.LENGTH_SHORT).show();
        }
    }
    private void onCall() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.CALL_PHONE},
                    PHONE_CALL_PERMISSION_CODE);
        } else {
            startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:"+advertiserUser.first.getPhoneNumber())));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PHONE_CALL_PERMISSION_CODE:
                if(PermissionRequest.checkPermission(grantResults)){
                    onCall();
                }else{
                    makeText(this, "Permission Not Granted",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
    

    /**
     * @param user sets the value of the current user to the session user object
     */
    private void setAdUserToLocal(Pair<User, Boolean> user) {
        this.advertiserUser = user;
        contactButton.setVisibility(View.VISIBLE);
        /* set attributes of session user to the UI components */
        getAndSetCurrentAttributes();
    }

    /**
     * sets the current session User attributes to the UI components
     */
    private void getAndSetCurrentAttributes() {

        User user = this.advertiserUser.first;

        this.nameText.setText(user.getName());
        this.emailTextView.setText(user.getUserEmail());
        this.uniAccountClaimer.setText((user.hasUniversityEmail() ?
                getString(R.string.uniAccountClaimer) : getString(R.string.nonUniAccountClaimer)));
        if (user.getAge() != 0) {
            this.ageText.setText(String.valueOf(user.getAge()));
        }
        if (user.getPhoneNumber() != null) {
            this.phoneNumberText.setText(user.getPhoneNumber());
        }
        if (user.getGender() != null) {
            this.genderText.setText(user.getGender());
        }
        setPictureToImageComponent();
    }

    /**
     * sets the user profile picture (or default gender picture) to the ImageView component
     */
    private void setPictureToImageComponent() {


        boolean isLocal = this.advertiserUser.second;
        if(isLocal) {
            Bitmap profilePic = BitmapFactory.decodeFile(this.advertiserUser.first.getProfileImagePathAndName());
            Glide.with(this).load(profilePic).into(imageView);
        } else {
            database.accept(new GlideImageViewLoader(this, imageView,
                    this.advertiserUser.first.getProfileImagePathAndName()));
        }
    }

}