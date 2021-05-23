package ch.epfl.sdp.appart;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.MockDatabaseService;
import ch.epfl.sdp.appart.database.local.LocalDatabaseService;
import ch.epfl.sdp.appart.database.local.MockLocalDatabase;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.login.MockLoginService;
import ch.epfl.sdp.appart.user.AppUser;
import ch.epfl.sdp.appart.user.User;
import ch.epfl.sdp.appart.user.UserViewModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserVMTest {

    @Rule
    public InstantTaskExecutorRule instantTaskRule = new InstantTaskExecutorRule();

    DatabaseService db = new MockDatabaseService();
    LoginService ls = new MockLoginService();
    LocalDatabaseService localDatabaseService = new MockLocalDatabase();

    private UserViewModel vm;

    @Before
    public void init() {
        vm = new UserViewModel(db, ls, localDatabaseService);
    }

    @Test
    public void putAndGetUserTest() {
        User user = new AppUser("vetterli-id", "vetterli@epfl.ch");
        vm.putUser(user);
        assertTrue(vm.getPutUserConfirmed().getValue());
        vm.getUser("vetterli-id");
        assertEquals(user, vm.getUser().getValue());
    }

    @Test
    public void currentUserNullTest() {
        assertTrue(vm.getCurrentUser().isCompletedExceptionally());
    }


}