package ch.epfl.sdp.appart;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;
import ch.epfl.sdp.appart.user.AppUser;
import ch.epfl.sdp.appart.user.User;
import ch.epfl.sdp.appart.user.UserViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SimpleUserProfileActivity extends AppCompatActivity {

    /* temporary user */
    private User advertiserUser;

    /* User ViewModel */
    UserViewModel mViewModel;

    /* UI components */
    private EditText nameText;
    private EditText ageText;
    private EditText phoneNumberText;
    private EditText genderText;
    private TextView uniAccountClaimer;
    private TextView emailTextView;
    private ImageView imageView;

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

        String advertiserId = getIntent().getStringExtra("advertiserId");

        /* get user from database from user ID */
        mViewModel.getUser(advertiserId);
        mViewModel.getUser().observe(this, this::setAdUserToLocal);
    }

    /**
     * closes activity when back button pressed on phone
     */
    @Override
    public void onBackPressed() { finish(); }

    /**
     *  closes activity when back button pressed on UI
     */
    public void contactAdUser(View view) {
        // TODO: send message to user
    }

    /**
     *
     * @param user sets the value of the current user to the session user object
     */
    private void setAdUserToLocal(User user){
        this.advertiserUser = user;

        /* set attributes of session user to the UI components */
        getAndSetCurrentAttributes();
    }

    /**
     * sets the current session User attributes to the UI components
     */
    private void getAndSetCurrentAttributes() {
        this.nameText.setText(this.advertiserUser.getName());
        this.emailTextView.setText(this.advertiserUser.getUserEmail());
        this.uniAccountClaimer.setText((this.advertiserUser.hasUniversityEmail() ? getString(R.string.uniAccountClaimer) : getString(R.string.nonUniAccountClaimer)));
        if (this.advertiserUser.getAge() != 0) {
            this.ageText.setText(String.valueOf(this.advertiserUser.getAge()));
        }
        if (this.advertiserUser.getPhoneNumber() != null) {
            this.phoneNumberText.setText(this.advertiserUser.getPhoneNumber());
        }
        if (this.advertiserUser.getGender() != null) {
            this.genderText.setText(this.advertiserUser.getGender());
        }
        setPictureToImageComponent();
    }

    /**
     * sets the user profile picture (or default gender picture) to the ImageView component
     */
    private void setPictureToImageComponent() {
        String[] verifier = this.advertiserUser.getProfileImage().split(":");
        if (verifier[0].equals("userIcon")){
            int id = Integer.parseInt(verifier[1]);
            Drawable iconImage = ResourcesCompat.getDrawable(getResources(), id, null);
            this.imageView.setImageDrawable(iconImage);
        } else {
            // TODO: set actual user-specific profile picture with sessionUser.getProfileImage()
        }
    }


}