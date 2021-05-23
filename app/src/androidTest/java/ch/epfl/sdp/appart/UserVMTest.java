package ch.epfl.sdp.appart;

import android.graphics.Bitmap;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.MockDatabaseService;
import ch.epfl.sdp.appart.database.local.LocalDatabase;
import ch.epfl.sdp.appart.database.local.LocalDatabaseService;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.login.MockLoginService;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.user.AppUser;
import ch.epfl.sdp.appart.user.User;
import ch.epfl.sdp.appart.user.UserViewModel;
import kotlin.NotImplementedError;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserVMTest {

    @Rule
    public InstantTaskExecutorRule instantTaskRule = new InstantTaskExecutorRule();

    DatabaseService db = new MockDatabaseService();
    LoginService ls = new MockLoginService();
    LocalDatabaseService localDatabaseService = new MockLocalDBServiceForUserVM();

    private UserViewModel vm;

    @Before
    public void init() {
        vm = new UserViewModel(db, ls, localDatabaseService);
    }

    @Test
    public void putAndGetUserTest() {
        User user = new AppUser("testId", "test@email.ch");
        vm.putUser(user);
        assertTrue(vm.getPutUserConfirmed().getValue());
        vm.getUser("testId");
        assertEquals(user, vm.getUser().getValue());
    }


}

class MockLocalDBServiceForUserVM implements LocalDatabaseService {

    public MockLocalDBServiceForUserVM() {}

    @Override
    public User getCurrentUser() {
        throw new NotImplementedError();
    }

    @Override
    public CompletableFuture<Void> writeCompleteAd(String adId, String cardId, Ad ad, User user, List<Bitmap> adPhotos, List<Bitmap> panoramas, Bitmap profilePic) {
        throw new NotImplementedError();
    }

    @Override
    public CompletableFuture<List<Card>> getCards() {
        throw new NotImplementedError();
    }

    @Override
    public CompletableFuture<Ad> getAd(String adId) {
        throw new NotImplementedError();
    }

    @Override
    public CompletableFuture<User> getUser(String wantedUserID) {
        CompletableFuture<User> result = new CompletableFuture<>();
        result.complete(new AppUser("testId", "test@email.ch"));
        return result;
    }

    @Override
    public void cleanFavorites() {
        throw new NotImplementedError();
    }

    @Override
    public void removeCard(String cardId) {
        throw new NotImplementedError();
    }

    @Override
    public CompletableFuture<Void> setCurrentUser(User currentUser, Bitmap profilePic) {
        throw new NotImplementedError();
    }

    @Override
    public User loadCurrentUser() {
        throw new NotImplementedError();
    }

    @Override
    public CompletableFuture<List<String>> getPanoramasPaths(String adID) {
        throw new NotImplementedError();
    }
}