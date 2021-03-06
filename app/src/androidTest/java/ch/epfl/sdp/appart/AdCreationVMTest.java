package ch.epfl.sdp.appart;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import ch.epfl.sdp.appart.ad.AdCreationViewModel;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.MockDatabaseService;
import ch.epfl.sdp.appart.hilt.DatabaseModule;
import ch.epfl.sdp.appart.hilt.LoginModule;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.login.MockLoginService;
import ch.epfl.sdp.appart.ad.PricePeriod;
import ch.epfl.sdp.appart.user.User;
import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

@UninstallModules({LoginModule.class, DatabaseModule.class})
@HiltAndroidTest
public class AdCreationVMTest {

    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @BindValue
    LoginService ls = new MockLoginService();
    @BindValue
    DatabaseService db = new MockDatabaseService();

    private AdCreationViewModel vm;
    private CompletableFuture<String> dbRes;
    private User u;

    @Before
    public void init(){
        ls.loginWithEmail("lorenzo@epfl.ch", "2222");
        vm = new AdCreationViewModel(db, ls);
        vm.setStreet("street");
        vm.setCity("city");
        vm.setPrice(1000L);
        vm.setPricePeriod(PricePeriod.MONTH);
        vm.setDescription("description");
        vm.setVRTourEnable(true);
    }

    @Test
    public void confirmCreationWorksForGoodValues() throws ExecutionException, InterruptedException {
        vm.setTitle("title");
        assertNull(vm.confirmCreation().get());
    }

    @Test
    public void confirmCreationCompletesExceptionally() {
        vm.setTitle("failing");
        assertThrows(ExecutionException.class, () -> vm.confirmCreation().get());
    }

}
