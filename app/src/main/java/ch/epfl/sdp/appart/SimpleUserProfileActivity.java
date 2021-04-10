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
import java.util.concurrent.CompletableFuture;
import ch.epfl.sdp.appart.user.AppUser;
import ch.epfl.sdp.appart.user.Gender;
import ch.epfl.sdp.appart.user.User;

public class SimpleUserProfileActivity extends AppCompatActivity {

    /* temporary user */
    private User sessionUser;

    /* UI components */
    private Button backButton;
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

        /* UI components initialisation */
        this.backButton = findViewById(R.id.back_SimpleUserProfile_button);
        this.nameText = findViewById(R.id.name_SimpleUserProfile_editText);
        this.ageText = findViewById(R.id.age_SimpleUserProfile_editText);
        this.emailTextView = findViewById(R.id.emailText_SimpleUserProfile_textView);
        this.phoneNumberText = findViewById(R.id.phoneNumber_SimpleUserProfile_editText);
        this.genderText = findViewById(R.id.gender_SimpleUserProfile_editText);
        this.uniAccountClaimer = findViewById(R.id.uniAccountClaimer_SimpleUserProfile_textView);
        this.imageView = findViewById(R.id.profilePicture_SimpleUserProfile_imageView);

        /* retrieve session user copy for use info */
        getAdUserFromDatabase();

        /* set attributes of session user to the UI components */
        getAndSetCurrentAttributes();
    }


    /**
     *  closes activity when back button pressed on UI
     */
    public void goBack(View view) { finish(); }

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
     * retrieves the current user information from database
     * and stores it in the session user instance
     */
    private void getAdUserFromDatabase() {
        this.sessionUser = new AppUser("1", "marie.bernard@gmail.com");
        this.sessionUser.setName("Marie Bernard");
        this.sessionUser.setAge(25);
        this.sessionUser.setGender(Gender.FEMALE.name());
        this.sessionUser.setPhoneNumber("+41 7666666666");
        // TODO: get actual ad user from database
    }

    /**
     * sets the current session User attributes to the UI components
     */
    private void getAndSetCurrentAttributes() {
        this.nameText.setText(this.sessionUser.getName());
        this.emailTextView.setText(this.sessionUser.getUserEmail());
        this.uniAccountClaimer.setText((this.sessionUser.hasUniversityEmail() ? getString(R.string.uniAccountClaimer) : getString(R.string.nonUniAccountClaimer)));
        if (this.sessionUser.getAge() != 0) {
            this.ageText.setText(String.valueOf(this.sessionUser.getAge()));
        }
        if (this.sessionUser.getPhoneNumber() != null) {
            this.phoneNumberText.setText(this.sessionUser.getPhoneNumber());
        }
        if (this.sessionUser.getGender() != null) {
            this.genderText.setText(this.sessionUser.getGender());
        }
        setPictureToImageComponent();
    }

    /**
     * sets the user profile picture (or default gender picture) to the ImageView component
     */
    private void setPictureToImageComponent() {
        if (this.sessionUser.getProfileImage() == null) {
            int id;
            if (this.sessionUser.getGender().equals(Gender.MALE.name())) {
                id = R.drawable.user_example_male;
            } else if (this.sessionUser.getGender().equals(Gender.FEMALE.name())) {
                id = R.drawable.user_example_female;
            } else {
                id = R.drawable.user_example_no_gender;
            }
            Drawable iconImage = ResourcesCompat.getDrawable(getResources(), id, null);
            this.imageView.setImageDrawable(iconImage);
        } //   else { set actual user-specific profile picture }
    }


}