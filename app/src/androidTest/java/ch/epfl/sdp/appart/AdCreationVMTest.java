package ch.epfl.sdp.appart;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import ch.epfl.sdp.appart.adcreation.AdCreationViewModel;
import ch.epfl.sdp.appart.database.Database;
import ch.epfl.sdp.appart.hilt.FireBaseModule;
import ch.epfl.sdp.appart.hilt.LoginModule;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.scrolling.PricePeriod;
import ch.epfl.sdp.appart.scrolling.ad.Ad;
import ch.epfl.sdp.appart.user.AppUser;
import ch.epfl.sdp.appart.user.User;
import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UninstallModules({LoginModule.class, FireBaseModule.class})
@HiltAndroidTest
public class AdCreationVMTest {

    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @BindValue
    LoginService ls = mock(LoginService.class);

    @BindValue
    Database db = mock(Database.class);

    private Ad ad;
    private User user;
    private AdCreationViewModel vm;
    private CompletableFuture<String> dbRes;

    @Before
    public void setup(){
        user = new AppUser("id", "test@appart.ch");
        dbRes = new CompletableFuture<>();
        when(ls.getCurrentUser()).thenReturn(user);

        vm = new AdCreationViewModel(db, ls);
        vm.setTitle("title");
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
        dbRes.complete("adId");
        when(db.putAd(ad)).thenReturn(dbRes);
        assertTrue(vm.confirmCreation().get());
    }

    @Test
    public void confirmCreationCompletesExceptionally() throws ExecutionException, InterruptedException {
        dbRes.completeExceptionally(new IllegalStateException());
        when(db.putAd(ad)).thenReturn(dbRes);
        assertFalse(vm.confirmCreation().get());
    }

}
