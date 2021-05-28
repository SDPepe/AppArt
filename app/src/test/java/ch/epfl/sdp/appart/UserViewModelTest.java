package ch.epfl.sdp.appart;

import android.net.Uri;

import org.junit.Test;

import ch.epfl.sdp.appart.database.MockDatabaseService;
import ch.epfl.sdp.appart.database.local.LocalDatabase;
import ch.epfl.sdp.appart.database.local.LocalDatabaseService;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.login.MockLoginService;
import ch.epfl.sdp.appart.user.AppUser;
import ch.epfl.sdp.appart.user.User;
import ch.epfl.sdp.appart.user.UserViewModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserViewModelTest {


    LoginService login = new MockLoginService();

    MockDatabaseService database = new MockDatabaseService();

    LocalDatabaseService localDB = new LocalDatabase("");

    /* User ViewModel */
    UserViewModel mViewModel = new UserViewModel(database, login, localDB);

    @Test
    public void userViewModelCorrectlyUpdatesImageDatabase() {
        /* temporary user */
        User user = new AppUser("3333", "carlo@epfl.ch");
        user.setProfileImagePathAndName("users/test/path/photo.jpeg");

        assertTrue(database.getImages().contains("users/test/path/photo.jpeg"));
        Uri uri;
        mViewModel.deleteImage(user.getProfileImagePathAndName());

        assertFalse(database.getImages().contains("users/test/path/photo.jpeg"));

        String imageAndPath = "users/test/path/newPhoto.jpeg";
        user.setProfileImagePathAndName(imageAndPath);

        int databaseSize = database.getImages().size();
        mViewModel.updateImage(user.getUserId());
        assertEquals(database.getImages().size(), databaseSize + 1);
    }

    @Test
    public void userViewModelCorrectlyUpdatesUserDatabase() {
        /* temporary user */
        User user = new AppUser("test", "carlo@epfl.ch");
        mViewModel.putUser(user);
        assertEquals(user, database.getUser("test").getNow(null));

        user.setName("new Name");
        mViewModel.updateUser(user);
        assertEquals("new Name", database.getUser("test").getNow(null).getName());
    }
}