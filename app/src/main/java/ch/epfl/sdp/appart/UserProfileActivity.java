package ch.epfl.sdp.appart;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

import java.util.concurrent.CompletableFuture;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.firebaselayout.FirebaseLayout;
import ch.epfl.sdp.appart.database.local.LocalDatabaseService;
import ch.epfl.sdp.appart.glide.visitor.GlideBitmapLoader;
import ch.epfl.sdp.appart.glide.visitor.GlideImageViewLoader;
import ch.epfl.sdp.appart.user.Gender;
import ch.epfl.sdp.appart.user.User;
import ch.epfl.sdp.appart.user.UserViewModel;
import ch.epfl.sdp.appart.utils.ActivityCommunicationLayout;
import ch.epfl.sdp.appart.utils.DatabaseSync;
import ch.epfl.sdp.appart.utils.UIUtils;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * This class manages the UI of the user profile, the user may edit
 * its personal information from here. This class then interacts
 * with the user view model which updates the firestore database.
 */
@AndroidEntryPoint
public class UserProfileActivity extends AppCompatActivity {

    /* temporary user */
    private Pair<User, Boolean> sessionUser;

    /* User ViewModel */
    UserViewModel mViewModel;

    @Inject
    DatabaseService database;
    @Inject
    LocalDatabaseService localdb;

    /* UI components */
    private Button modifyButton;
    private Button doneButton;
    private Button changeImageButton;
    private Button removeImageButton;
    private EditText nameEditText;
    private EditText ageEditText;
    private EditText phoneNumberEditText;
    private Spinner genderSpinner;
    private TextView uniAccountClaimer;
    private TextView emailTextView;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        /* User ViewModel initialization */
        mViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        initUIComponents();
        modifyButton.setVisibility(View.INVISIBLE);

        /* get user from database from user ID */
        CompletableFuture<Void> userRes = mViewModel.getCurrentUser();
        // if user successfully fetched from DB, save it locally
        userRes.exceptionally(e -> {
            // failed to fetch -> prevent updated (we're probably offline)
            modifyButton.setVisibility(View.GONE);
            return null;
        });
        userRes.thenAccept(res ->
                DatabaseSync.saveCurrentUserToLocalDB(this, database, localdb,
                        mViewModel.getUser().getValue().first.getUserId()));
        mViewModel.getUser().observe(this, this::setSessionUserToLocal);
    }

    private void initUIComponents() {
        this.modifyButton = findViewById(R.id.editProfile_UserProfile_button);
        this.doneButton = findViewById(R.id.doneEditing_UserProfile_button);
        this.removeImageButton = findViewById(R.id.removeImage_UserProfile_button);
        this.changeImageButton = findViewById(R.id.editImage_UserProfile_button);
        this.nameEditText = findViewById(R.id.name_UserProfile_editText);
        this.ageEditText = findViewById(R.id.age_UserProfile_editText);
        this.emailTextView = findViewById(R.id.emailText_UserProfile_textView);
        this.phoneNumberEditText = findViewById(R.id.phoneNumber_UserProfile_editText);
        this.genderSpinner = findViewById(R.id.gender_UserProfile_spinner);
        this.genderSpinner.setEnabled(ageEditText.isEnabled());
        this.uniAccountClaimer = findViewById(R.id.uniAccountClaimer_UserProfile_textView);
        this.imageView = findViewById(R.id.profilePicture_UserProfile_imageView);

        this.imageView.setEnabled(false);
        this.removeImageButton.setVisibility(View.GONE);
        this.changeImageButton.setVisibility(View.GONE);
    }

    /**
     * edit user info, called by edit profile button
     */
    public void editProfile(View view) {
        this.modifyButton.setVisibility(View.GONE);
        this.doneButton.setVisibility(View.VISIBLE);
        this.changeImageButton.setVisibility(View.VISIBLE);
        if (!this.sessionUser.first.hasDefaultProfileImage()) {
            this.removeImageButton.setVisibility(View.VISIBLE);
        } else {
            this.removeImageButton.setVisibility(View.INVISIBLE);
        }

        /* enable editing in all UI components */
        enableDisableEntries();
    }

    /**
     * removes the profile image from database and sets default user icon
     * called by the remove button under imageView
     */
    public void removeProfileImage(View view) {
        mViewModel.deleteImage(this.sessionUser.first.getProfileImagePathAndName());
        this.sessionUser.first.setDefaultProfileImage();
        imageView.setImageResource(android.R.color.transparent);
        this.removeImageButton.setVisibility(View.INVISIBLE);
    }

    /**
     * changes the profile image of the user
     * called by the imageView button
     */
    public void changeProfileImage(View view) {
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra(ActivityCommunicationLayout.PROVIDING_ACTIVITY_NAME,
                ActivityCommunicationLayout.USER_PROFILE_ACTIVITY);
        startActivityForResult(intent, 1);
    }


    /**
     * manages the output of camera activity and updates the profile image
     * in firestore and firebase storage using user view-model
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                this.doneButton.setEnabled(false);
                Uri profileUri =
                        data.getParcelableExtra(ActivityCommunicationLayout.PROVIDING_IMAGE_URI);
                mViewModel.setUri(profileUri);
                imageView.setImageURI(profileUri);
                mViewModel.deleteImage(this.sessionUser.first.getProfileImagePathAndName());

                StringBuilder imagePathInDb = new StringBuilder();
                imagePathInDb
                        .append(FirebaseLayout.USERS_DIRECTORY)
                        .append(FirebaseLayout.SEPARATOR)
                        .append(this.sessionUser.first.getUserId())
                        .append(FirebaseLayout.SEPARATOR)
                        .append(FirebaseLayout.PROFILE_IMAGE_NAME)
                        .append(System.currentTimeMillis())
                        .append(FirebaseLayout.JPEG);
                // TODO: support more image formats

                this.sessionUser.first.setProfileImagePathAndName(imagePathInDb.toString());
                mViewModel.updateImage(this.sessionUser.first.getUserId());
                mViewModel.getUpdateImageConfirmed().observe(this, this::imageUpdateResult);
            }
        }
    }

    /**
     * saves updated user information in firestore database, called by the done button
     */
    public void doneEditing(View view) {

        /* set updated attributes from UI components to AppUser */
        setNewAttributes();

        /* disable editing text in all UI components*/
        enableDisableEntries();
        setSessionUserToDatabase(view);

        this.modifyButton.setVisibility(View.VISIBLE);
        this.doneButton.setVisibility(View.GONE);
        this.removeImageButton.setVisibility(View.GONE);
        this.changeImageButton.setVisibility(View.GONE);
    }

    /**
     * @param user sets the value of the current user to the session user object
     */
    private void setSessionUserToLocal(Pair<User, Boolean> user) {
        this.sessionUser = user;
        modifyButton.setVisibility(View.VISIBLE);

        /* set attributes of session user to the UI components */
        getAndSetCurrentAttributes();
    }

    /**
     * sets the updated user information to the firestore database and to the local database
     */
    private void setSessionUserToDatabase(View view) {
        mViewModel.updateUser(this.sessionUser.first);
        CompletableFuture<Void> saveRes = DatabaseSync.saveCurrentUserToLocalDB(this, database,
                localdb, mViewModel.getUser().getValue().first.getUserId());
        saveRes.exceptionally(e -> {
            Log.d("USER", "Failed to save user locally");
            return null;
        });
        mViewModel.getUpdateUserConfirmed().observe(this, this::informationUpdateResult);
    }

    private void imageUpdateResult(Boolean updateResult) {
        if (!updateResult) {
            UIUtils.makeSnakeForUserUpdateFailed(this.doneButton,
                    R.string.updateUserImageFailedInDB);
        }
        this.doneButton.setEnabled(true);
    }

    private void informationUpdateResult(Boolean updateResult) {
        if (updateResult) {
            /* update the view with new attributes */
            getAndSetCurrentAttributes();
        } else {
            UIUtils.makeSnakeForUserUpdateFailed(this.doneButton, R.string.updateUserFailedInDB);
        }
    }

    /**
     * enables and disables UI components when edit
     */
    private void enableDisableEntries() {
        this.nameEditText.setEnabled(!this.nameEditText.isEnabled());
        this.ageEditText.setEnabled(!this.ageEditText.isEnabled());
        this.genderSpinner.setEnabled(!this.genderSpinner.isEnabled());
        this.phoneNumberEditText.setEnabled(!this.phoneNumberEditText.isEnabled());
        /* email is never enabled since another process is required to edit it */
    }

    /**
     * sets the new attributes to the session User (local)
     */
    private void setNewAttributes() {
        String ageString =
                ((EditText) findViewById(R.id.age_UserProfile_editText)).getText().toString().trim();
        if (!ageString.equals("")) {
            this.sessionUser.first.setAge(Integer.parseInt(ageString));
        } else {
            this.sessionUser.first.setAge(0);
        }

        this.sessionUser.first.setName(((TextView) findViewById(R.id.name_UserProfile_editText)).getText().toString());
        this.sessionUser.first.setPhoneNumber(((EditText) findViewById(R.id.phoneNumber_UserProfile_editText)).getText().toString().trim());

        this.sessionUser.first.setGender(Gender.ALL.get(((Spinner) findViewById(R.id.gender_UserProfile_spinner)).getSelectedItemPosition()).name());

        /* LOOKS USELESS because of method naming: in case the user changed gender
           gender the gender is locally updated above. This method then updates the correct
           correct default user icon accordingly to the newly selected gender */
        if (this.sessionUser.first.hasDefaultProfileImage()) {
            this.sessionUser.first.setDefaultProfileImage();
        }
    }

    /**
     * sets the current session User attributes to the UI components
     */
    private void getAndSetCurrentAttributes() {

        User user = this.sessionUser.first;

        this.nameEditText.setText(user.getName());
        this.emailTextView.setText(user.getUserEmail());
        this.uniAccountClaimer.setText((user.hasUniversityEmail() ?
                getString(R.string.uniAccountClaimer) : getString(R.string.nonUniAccountClaimer)));
        if (user.getAge() != 0) {
            this.ageEditText.setText(String.valueOf(user.getAge()));
        }
        if (user.getPhoneNumber() != null) {
            this.phoneNumberEditText.setText(user.getPhoneNumber());
        }
        if (user.getGender() != null) {
            this.genderSpinner.setSelection(Gender.valueOf(user.getGender()).ordinal());
        }
        setPictureToImageComponent();
    }

    /**
     * sets the user profile picture (or default gender picture) to the ImageView component
     */
    private void setPictureToImageComponent() {
        boolean isLocal = this.sessionUser.second;
        if(isLocal) {
            Bitmap profilePic = BitmapFactory.decodeFile(this.sessionUser.first.getProfileImagePathAndName());
            Glide.with(this).load(profilePic).into(imageView);
        } else {
            database.accept(new GlideImageViewLoader(this, imageView,
                    this.sessionUser.first.getProfileImagePathAndName()));
        }



    }

}
