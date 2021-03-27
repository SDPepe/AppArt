package ch.epfl.sdp.appart;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import ch.epfl.sdp.appart.adcreation.AdCreationViewModel;
import ch.epfl.sdp.appart.database.Database;
import ch.epfl.sdp.appart.database.MockDataBase;
import ch.epfl.sdp.appart.hilt.FireBaseModule;
import ch.epfl.sdp.appart.hilt.LoginModule;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.login.MockLoginService;
import ch.epfl.sdp.appart.scrolling.PricePeriod;
import ch.epfl.sdp.appart.user.User;
import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@UninstallModules({LoginModule.class, FireBaseModule.class})
@HiltAndroidTest
public class AdCreationVMTest {

    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @BindValue
    LoginService ls = new MockLoginService();
    @BindValue
    Database db = new MockDataBase();

    private AdCreationViewModel vm;
    private CompletableFuture<String> dbRes;
    private User u;

    @Before
    public void init(){
        ls.loginWithEmail("lorenzo@epfl.ch", "2222");
        vm = new AdCreationViewModel(db, ls);
        vm.setStreet("street");
        vm.setCity("city");
        vm.setPrice("1000");
        vm.setPricePeriod(PricePeriod.MONTH);
        vm.setDescription("description");
        vm.setPhotosRefs(new ArrayList<>());
        vm.setVRTourEnable(true);
    }

    @Test
    public void confirmCreationWorksForGoodValues() throws ExecutionException, InterruptedException {
        vm.setTitle("title");
        assertTrue(vm.confirmCreation().get());
    }

    @Test
    public void confirmCreationCompletesExceptionally() throws ExecutionException, InterruptedException {
        vm.setTitle("failing");
        assertFalse(vm.confirmCreation().get());
    }

}
