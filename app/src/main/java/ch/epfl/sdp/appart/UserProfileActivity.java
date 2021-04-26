package ch.epfl.sdp.appart;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;
import ch.epfl.sdp.appart.user.Gender;
import ch.epfl.sdp.appart.user.User;
import ch.epfl.sdp.appart.user.UserViewModel;
import ch.epfl.sdp.appart.utils.UIUtils;
import dagger.hilt.android.AndroidEntryPoint;

import static ch.epfl.sdp.appart.utils.UIUtils.makeSnakeForUserUpdateFailed;

/**
 * This class manages the UI of the user profile, the user may edit
 * its personal information from here. This class then interacts
 * with the user view model which updates the firestore database.
 */
@AndroidEntryPoint
public class UserProfileActivity extends AppCompatActivity {

    /* temporary user */
    private User sessionUser;

    /* User ViewModel */
    UserViewModel mViewModel;

    /* UI components */
    private Button modifyButton;
    private Button doneButton;
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

        /* UI components initialisation */
        this.modifyButton = findViewById(R.id.modifyButton);
        this.doneButton = findViewById(R.id.doneButton);
        this.nameEditText = findViewById(R.id.name_UserProfile_editText);
        this.ageEditText = findViewById(R.id.age_UserProfile_editText);
        this.emailTextView = findViewById(R.id.emailText_UserProfile_textView);
        this.phoneNumberEditText = findViewById(R.id.phoneNumber_UserProfile_editText);
        this.genderSpinner = findViewById(R.id.gender_UserProfile_spinner);
        this.genderSpinner.setEnabled(ageEditText.isEnabled());
        this.uniAccountClaimer = findViewById(R.id.uniAccountClaimer_UserProfile_textView);
        this.imageView = findViewById(R.id.profilePicture_UserProfile_imageView);

        /* get user from database from user ID */
        mViewModel.getCurrentUser();
        mViewModel.getUser().observe(this, this::setSessionUserToLocal);

        mViewModel.getUri().observe(this, this::setProfileImage);
    }


    /**
     * closes activity when back button pressed on UI
     */
    public void goBack(View view) {
        finish();
    }

    /**
     * closes activity when back button pressed on phone
     */
    @Override
    public void onBackPressed() {
        finish();
    }

    /**
     * edit user info, called by edit profile button
     */
    public void editProfile(View view) {
        this.modifyButton.setVisibility(View.GONE);
        this.doneButton.setVisibility(View.VISIBLE);

        /* enable editing in all UI components */
        enableDisableEntries();
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
    }

    /**
     *
     * @param user sets the value of the current user to the session user object
     */
    private void setSessionUserToLocal(User user){
        this.sessionUser = user;

        /* set attributes of session user to the UI components */
        getAndSetCurrentAttributes();
    }

    /**
     * sets the updated user information to the firestore database
     */
    private void setSessionUserToDatabase(View view) {
      mViewModel.updateUser(this.sessionUser);
      mViewModel.getUpdateCardConfirmed().observe(this, this::informationUpdateResult);
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
        String ageString = ((EditText) findViewById(R.id.age_UserProfile_editText)).getText().toString().trim();
        if (!ageString.equals("")) {
            this.sessionUser.setAge(Integer.parseInt(ageString));
        }

        this.sessionUser.setName(((TextView) findViewById(R.id.name_UserProfile_editText)).getText().toString());
        this.sessionUser.setGender(Gender.ALL.get(((Spinner) findViewById(R.id.gender_UserProfile_spinner)).getSelectedItemPosition()).name());
        this.sessionUser.setPhoneNumber(((EditText) findViewById(R.id.phoneNumber_UserProfile_editText)).getText().toString().trim());
    }

    /**
     * sets the current session User attributes to the UI components
     */
    private void getAndSetCurrentAttributes() {
        this.nameEditText.setText(this.sessionUser.getName());
        this.emailTextView.setText(this.sessionUser.getUserEmail());
        this.uniAccountClaimer.setText((this.sessionUser.hasUniversityEmail() ? getString(R.string.uniAccountClaimer) : getString(R.string.nonUniAccountClaimer)));
        if (this.sessionUser.getAge() != 0) {
            this.ageEditText.setText(String.valueOf(this.sessionUser.getAge()));
        }
        if (this.sessionUser.getPhoneNumber() != null) {
            this.phoneNumberEditText.setText(this.sessionUser.getPhoneNumber());
        }
        if (this.sessionUser.getGender() != null) {
            this.genderSpinner.setSelection(Gender.valueOf(this.sessionUser.getGender()).ordinal());
        }
        setPictureToImageComponent();
    }

    /**
     * sets the user profile picture (or default gender picture) to the ImageView component
     */
    private void setPictureToImageComponent() {
        String[] verifier = this.sessionUser.getProfileImage().split(":");
        if (verifier[0].equals("userIcon")){
            int id = Integer.parseInt(verifier[1]);
            Drawable iconImage = ResourcesCompat.getDrawable(getResources(), id, null);
            this.imageView.setImageDrawable(iconImage);
        } else {
            // TODO: set actual user-specific profile picture with sessionUser.getProfileImage()
        }
    }

    /**
     * Set user profile image
     */
    private void setProfileImage(Uri uri){
        imageView.setImageURI(uri);
    }


}
