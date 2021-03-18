package ch.epfl.sdp.appart.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import ch.epfl.sdp.appart.R;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;


/**
 * This class manages the UI of the user profile. Here what gets updated is the AppUser instance of
 * the logged in User (session User). Not the actual User in firebase.
 */
public class UserProfileActivity extends AppCompatActivity {

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

        this.modifyButton = findViewById(R.id.modifyButton);
        this.doneButton =  findViewById(R.id.doneButton);
        this.backButton = findViewById(R.id.backButton);
        this.nameView =  findViewById(R.id.nameText);
        this.ageView = findViewById(R.id.ageText);
        this.emailView =  findViewById(R.id.emailText);
        this.phoneNumberView = findViewById(R.id.phoneNumberText);
        this.genderView =  findViewById(R.id.genderView);
        this.genderView.setEnabled(ageView.isEnabled());
        this.uniAccountClaimer = findViewById(R.id.uniAccounClaimer);
        this.imageView = findViewById(R.id.imageView);

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
     * saves user information in the sessionUser (AppUser) instance,
     * called by the done button
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

        /* HERE THE FIREBASE CLASS SHOULD BE CALLED AND ALL
         MODIFIED ATTRIBUTES SHOULD BE MODIFIED IN DATABASE */
        /* the below line will be deleted once tests are provided */
        printAttributesTest();

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
            LoginActivity.sessionUser.setAge(Integer.parseInt(ageString));
        }

        LoginActivity.sessionUser.setName(((TextView) findViewById(R.id.nameText)).getText().toString());
        LoginActivity.sessionUser.setGender(Gender.ALL.get(((Spinner) findViewById(R.id.genderView)).getSelectedItemPosition()));
        LoginActivity.sessionUser.setPhoneNumber(((EditText) findViewById(R.id.phoneNumberText)).getText().toString().trim());
    }

    /**
     * sets the current session User attributes to the UI components
     */
    private void getAndSetCurrentAttributes() {
        this.nameView.setText(LoginActivity.sessionUser.getName());
        this.emailView.setText(LoginActivity.sessionUser.getUserEmail());
        this.uniAccountClaimer.setText((LoginActivity.sessionUser.hasUniversityEmail() ? getString(R.string.uniAccountClaimer) : getString(R.string.nonUniAccountClaimer)));
        if (LoginActivity.sessionUser.getAge() != 0) {
            this.ageView.setText(String.valueOf(LoginActivity.sessionUser.getAge()));
        }
        if (LoginActivity.sessionUser.getPhoneNumber() != null) {
            this.phoneNumberView.setText(LoginActivity.sessionUser.getPhoneNumber());
        }
        if (LoginActivity.sessionUser.getGender() != null) {
            this.genderView.setSelection(LoginActivity.sessionUser.getGender().ordinal());
            /* done inside this if for the moment since only gender pictures are available now */
            setPictureToImageComponent();
        }
    }

    /**
     * sets the user profile picture (or default gender picture) to the ImageView component
     */
    private void setPictureToImageComponent() {
        if (LoginActivity.sessionUser.getProfileImage() == null) {
            int id;
            if (LoginActivity.sessionUser.getGender() == Gender.MALE) {
                id = R.drawable.user_example_male;
            } else if (LoginActivity.sessionUser.getGender() == Gender.FEMALE) {
                id = R.drawable.user_example_female;
            } else {
                id = R.drawable.user_example_no_gender;
            }
            Drawable iconImage = ResourcesCompat.getDrawable(getResources(), id, null);
            this.imageView.setImageDrawable(iconImage);
        } // else { set actual user-specific profile picture }


    }

    /* DELETE THIS METHOD ONCE REAL TESTS ARE DONE */
    private void printAttributesTest(){
        System.out.println("\n*\n********************************************************************************************************************************************");
        System.out.println("********************************************************************************************************************************************");
        System.out.println();
        System.out.println("  User email : " + LoginActivity.sessionUser.getUserEmail());
        System.out.println("  User name : " + LoginActivity.sessionUser.getName());
        System.out.println("  User phone number : " + LoginActivity.sessionUser.getPhoneNumber());
        System.out.println("  User age : " + LoginActivity.sessionUser.getAge());
        System.out.println("  User gender : " + LoginActivity.sessionUser.getGender());
        System.out.println();
        System.out.println("********************************************************************************************************************************************");
        System.out.println("********************************************************************************************************************************************\n");
    }


}
