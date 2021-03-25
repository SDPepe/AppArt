package ch.epfl.sdp.appart.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.database.Database;
import dagger.hilt.android.AndroidEntryPoint;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

/**
 * This class manages the UI of the user profile and calls
 * firebase class in order to manage the updated information.
 */
@AndroidEntryPoint
public class UserProfileActivity extends AppCompatActivity {

    /* temporary user */
    private User sessionUser;

    /* Firestore database instance for db updates */
    @Inject
    Database db;

    /* UI components */
    private Button modifyButton;
    private Button doneButton;
    private Button backButton;
    private EditText nameView;
    private EditText ageView;
    private EditText phoneNumberView;
    private Spinner genderView;
    private TextView uniAccountClaimer;
    private TextView emailView;
    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        /* UI components initialisation */
        this.modifyButton = findViewById(R.id.modifyButton);
        this.doneButton =  findViewById(R.id.doneButton);
        this.backButton = findViewById(R.id.backButton);
        this.nameView =  findViewById(R.id.nameText);
        this.ageView = findViewById(R.id.ageText);
        this.emailView =  findViewById(R.id.emailText);
        this.phoneNumberView = findViewById(R.id.phoneNumberText);
        this.genderView =  findViewById(R.id.genderView);
        this.genderView.setEnabled(ageView.isEnabled());
        this.uniAccountClaimer = findViewById(R.id.uniAccountClaimer);
        this.imageView = findViewById(R.id.imageView);

        /* retrieve session user copy for use info */
        this.sessionUser = getSessionUserFromDB();

        /* set attributes of session user to the UI components */
        getAndSetCurrentAttributes();

    }


    /**
     * close activity, called by back button
     */
    public void back(View view) {
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
     * saves updated user information, called by the done button
     */
    public void doneEditing(View view) {

        /* set updated attributes from UI components to AppUser */
        setNewAttributes();

        /* disable editing text in all UI components*/
        enableDisableEntries();

        /* update the view with new attributes */
        getAndSetCurrentAttributes();

        this.modifyButton.setVisibility(View.VISIBLE);
        this.doneButton.setVisibility(View.GONE);

        /* if true the update in firestore was correctly executed */
        //boolean updateIsDone = setSessionUserToDB();
    }

    /**
     * @return a copy of the user stored in firestore db as an instance of AppUser
     */
    private User getSessionUserFromDB() {
        //CompletableFuture<User> updatedUserFuture = db.getUser("cNiw54I9VXJOepwpmIqA"); // find actual user document ID on firestore
        //return updatedUserFuture.join();
        return this.sessionUser = new AppUser("1", "carlo.musso@epfl.ch"); // HARD-CODED
    }

    /**
     * sets the updated user information to the firestore database
     * @return true if the update was correctly completed, false otherwise
     */
    private boolean setSessionUserToDB() {
        CompletableFuture<Boolean> updatedUserFuture = db.updateUser(this.sessionUser);
        return updatedUserFuture.join();
    }

    /**
     * enables and disables UI components to edit
     */
    private void enableDisableEntries() {
        this.nameView.setEnabled(!this.nameView.isEnabled());
        this.ageView.setEnabled(!this.ageView.isEnabled());
        this.genderView.setEnabled(!this.genderView.isEnabled());
        this.backButton.setEnabled(!this.backButton.isEnabled());
        this.phoneNumberView.setEnabled(!this.phoneNumberView.isEnabled());
        // email is never enabled so easily
    }

    /**
     * sets the new attributes to the session User
     */
    private void setNewAttributes(){
        String ageString = ((EditText) findViewById(R.id.ageText)).getText().toString().trim();
        if (ageString != null && !ageString.equals("")) {
            this.sessionUser.setAge(Integer.parseInt(ageString));
        }

        this.sessionUser.setName(((TextView) findViewById(R.id.nameText)).getText().toString());
        this.sessionUser.setGender(Gender.ALL.get(((Spinner) findViewById(R.id.genderView)).getSelectedItemPosition()));
        this.sessionUser.setPhoneNumber(((EditText) findViewById(R.id.phoneNumberText)).getText().toString().trim());
    }

    /**
     * sets the current session User attributes to the UI components
     */
    private void getAndSetCurrentAttributes() {
        this.nameView.setText(this.sessionUser.getName());
        this.emailView.setText(this.sessionUser.getUserEmail());
        this.uniAccountClaimer.setText((this.sessionUser.hasUniversityEmail() ? getString(R.string.uniAccountClaimer) : getString(R.string.nonUniAccountClaimer)));
        if (this.sessionUser.getAge() != 0) {
            this.ageView.setText(String.valueOf(this.sessionUser.getAge()));
        }
        if (this.sessionUser.getPhoneNumber() != null) {
            this.phoneNumberView.setText(this.sessionUser.getPhoneNumber());
        }
        if (this.sessionUser.getGender() != null) {
            this.genderView.setSelection(this.sessionUser.getGender().ordinal());
        }
        setPictureToImageComponent();
    }

    /**
     * sets the user profile picture (or default gender picture) to the ImageView component
     */
    private void setPictureToImageComponent() {
        if (this.sessionUser.getProfileImage() == null) {
            int id;
            if (this.sessionUser.getGender() == Gender.MALE) {
                id = R.drawable.user_example_male;
            } else if (this.sessionUser.getGender() == Gender.FEMALE) {
                id = R.drawable.user_example_female;
            } else {
                id = R.drawable.user_example_no_gender;
            }
            Drawable iconImage = ResourcesCompat.getDrawable(getResources(), id, null);
            this.imageView.setImageDrawable(iconImage);
        } //   else { set actual user-specific profile picture }


    }


}
