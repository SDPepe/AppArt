package ch.epfl.sdp.appart;

import android.net.Uri;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.ad.ContactInfo;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.FirestoreDatabaseService;
import ch.epfl.sdp.appart.database.FirestoreEmulatorDatabaseServiceWrapper;
import ch.epfl.sdp.appart.database.firebaselayout.AdLayout;
import ch.epfl.sdp.appart.database.firebaselayout.FirebaseLayout;
import ch.epfl.sdp.appart.hilt.DatabaseModule;
import ch.epfl.sdp.appart.hilt.LoginModule;
import ch.epfl.sdp.appart.login.FirebaseEmulatorLoginServiceWrapper;
import ch.epfl.sdp.appart.login.FirebaseLoginService;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.ad.PricePeriod;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.user.AppUser;
import ch.epfl.sdp.appart.user.User;
import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;

@UninstallModules({DatabaseModule.class, LoginModule.class})
@HiltAndroidTest
public class DatabaseTest {

    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @BindValue
    final
    DatabaseService db = new FirestoreEmulatorDatabaseServiceWrapper(new FirestoreDatabaseService());

    @BindValue
    final
    LoginService loginService = new FirebaseEmulatorLoginServiceWrapper(new FirebaseLoginService());

    User globalUser = null;
    FirestoreEmulatorDatabaseServiceWrapper database;

    @Before
    public void setup() {
        database = (FirestoreEmulatorDatabaseServiceWrapper) db;
        db.clearCache().join();
        System.out.println("Cache cleared");
    }

    public void verifyCard(Card retrievedCard, String city, long price, String ownerID) {
        assertThat(retrievedCard.getCity(), is(city));
        assertThat(retrievedCard.getPrice(), is(price));
        assertThat(retrievedCard.getUserId(), is(ownerID));
    }

    public void verifyUser(User retrievedUser, long age, String name, String id, String email, String phoneNB) {
        assertThat(retrievedUser.getUserEmail(), is(email));
        assertThat(retrievedUser.getName(), is(name));
        assertThat(retrievedUser.getUserId(), is(id));
        assertThat(retrievedUser.getAge(), is(age));
        assertThat(retrievedUser.getUserId(), is(id));
        assertThat(retrievedUser.getPhoneNumber(), is(phoneNB));
    }

    public void addingUsersAndUpdateTest() {
        String email = "fakeemail@testappart.ch";
        String userId = "userTestID";
        String name = "TestName";
        long age = 22;
        String phoneNb = "000000";

        User user = new AppUser(userId, email);
        user.setAge(age);
        user.setName(name);
        user.setPhoneNumber(phoneNb);

        db.putUser(user).join();

        User retrievedUser = db.getUser(user.getUserId()).join();

        verifyUser(retrievedUser, age, name, userId, email, phoneNb);

/*
        email = "newFakeEmail@testappart.ch";
        name = "TestName2";
        age = 30;

        user.setAge(age);
        user.setName(name);
        user.setUserEmail(email);

        db.updateUser(user, Uri.parse("/")).join();

        retrievedUser = db.getUser(user.getUserId()).join();

        verifyUser(retrievedUser, age, name, userId, email, phoneNb);
        */
        globalUser = retrievedUser;
    }

    public void verifyAd(Ad retrievedAd, String title, String street, String city, String desc, long price, String advertiserId, ContactInfo contactInfo, PricePeriod pricePeriod, boolean hasVRTour) {
        assertThat(retrievedAd.getTitle(), is(title));
        assertThat(retrievedAd.getStreet(), is(street));
        assertThat(retrievedAd.getAdvertiserId(), is(advertiserId));
        assertThat(retrievedAd.getCity(), is(city));
        assertThat(retrievedAd.getDescription(), is(desc));
        assertThat(retrievedAd.getPrice(), is(price));
        assertThat(retrievedAd.getPricePeriod(), is(pricePeriod));
        assertThat(retrievedAd.hasVRTour(), is(hasVRTour));
    }


    private static void writeCopy(InputStream stream, File copy) throws IOException {
        byte[] bytes = new byte[stream.available()];
        stream.read(bytes);
        new FileOutputStream(copy).write(bytes);
    }

    public void addingAdAndGetTest() throws IOException {
        Ad.AdBuilder builder = new Ad.AdBuilder();

        String title = "Test ad";
        String street = "Fake street";
        String city = "Lausanne";
        String desc = "Fake description";
        long price = 1000;
        ContactInfo contactInfo = new ContactInfo(globalUser.getUserEmail(), globalUser.getPhoneNumber(), globalUser.getName());
        PricePeriod pricePeriod = PricePeriod.MONTH;
        ArrayList<String> photoIds = new ArrayList<>();
        boolean hasVRTour = true;

        //https://mondwan.blogspot.com/2017/03/loading-test-resource-with-class-loader.html
        InputStream input = InstrumentationRegistry.getInstrumentation().getContext().getResources().getAssets().open("panorama_test.jpg");
        File copy = new File(InstrumentationRegistry.getInstrumentation().getTargetContext().getCacheDir() + "/panorama_test.jpg");
        writeCopy(input, copy);
        List<Uri> uriList = new ArrayList<>();
        uriList.add(Uri.fromFile(copy));

        loginService.signInAnonymously().join();

        String path = copy.getPath();
        photoIds.add(path);

        builder.withTitle(title);
        builder.withStreet(street);
        builder.withAdvertiserId(globalUser.getUserId());
        builder.withCity(city);
        builder.withDescription(desc);
        builder.withPrice(price);
        builder.withPicturesReferences(photoIds);
        builder.withPricePeriod(pricePeriod);
        builder.hasVRTour(hasVRTour);

        Ad ad = builder.build();
        String adId = db.putAd(ad, uriList, uriList).join();

        List<Card> retrievedCards = this.db.getCards().join();
        assertThat(retrievedCards.size(), is(1));
        Card card = retrievedCards.get(0);

        verifyCard(card, city, price, globalUser.getUserId());

        Ad retrievedAd = db.getAd(card.getAdId()).join();
        verifyAd(retrievedAd, title, street, city, desc, price, globalUser.getUserId(), contactInfo, pricePeriod, hasVRTour);

        database.removeFromStorage(database.getStorageReference(
                FirebaseLayout.ADS_DIRECTORY + FirebaseLayout.SEPARATOR + adId + "photo0.jpeg"));
    }

    public void updateCardTest() {
        List<Card> cards = db.getCards().join();
        assertThat(cards.size(), is(1));

        Card card = cards.get(0);
        String newCity = "New City";
        boolean newVRTour = false;

        card.setCity("New City");
        card.setVRTour(newVRTour);

        db.updateCard(card).join();

        cards = db.getCards().join();
        assertThat(cards.size(), is(1));

        Card retrievedCard = cards.get(0);
        assertThat(retrievedCard.getCity(), is(newCity));
        assertThat(retrievedCard.hasVRTour(), is(newVRTour));

    }

    public void getCardsFilterTest(){
        List<Card> cards = db.getCardsFilter("New City").join();
        assertThat(cards.size(), is(1));
    }

    @Test
    public void databaseTest() throws IOException {
        addingUsersAndUpdateTest();
        addingAdAndGetTest();
        updateCardTest();
        getCardsFilterTest();
    }
}
