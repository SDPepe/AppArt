package ch.epfl.sdp.appart;

import org.junit.Before;
import org.junit.Test;

import ch.epfl.sdp.appart.database.firebaselayout.FirebaseLayout;
import ch.epfl.sdp.appart.utils.StoragePathBuilder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class StoragePathBuilderTest {

    private final String dummyId = "1234";
    private final String dummyFile = "1234.jpg";

    private final String adsPath =  FirebaseLayout.ADS_DIRECTORY +
                                    FirebaseLayout.SEPARATOR +
                                    dummyId +
                                    FirebaseLayout.SEPARATOR +
                                    dummyFile;

    private final String cardsPath =    FirebaseLayout.CARDS_DIRECTORY +
                                        FirebaseLayout.SEPARATOR +
                                        dummyId +
                                        FirebaseLayout.SEPARATOR +
                                        dummyFile;

    private final String usersPath =    FirebaseLayout.USERS_DIRECTORY +
                                        FirebaseLayout.SEPARATOR +
                                        dummyId +
                                        FirebaseLayout.SEPARATOR +
                                        dummyFile;

    @Test
    public void checkCorrectPathComputed() {

        String adsPathWithDummyId = new StoragePathBuilder()
                .toAdsStorageDirectory()
                .toDirectory(dummyId)
                .withFile(dummyFile);

        String cardsPathWithDummyId = new StoragePathBuilder()
                .toCardsStorageDirectory()
                .toDirectory(dummyId)
                .withFile(dummyFile);

        String usersPathWithDummyId = new StoragePathBuilder()
                .toUsersStorageDirectory()
                .toDirectory(dummyId)
                .withFile(dummyFile);


        String usersPathWithDummyIdFromRoot = new StoragePathBuilder()
                .fromRoot()
                .toUsersStorageDirectory()
                .toDirectory(dummyId)
                .withFile(dummyFile);

        assertEquals("ads path is well formed", adsPath, adsPathWithDummyId);
        assertEquals("cards path is well formed", cardsPath, cardsPathWithDummyId);
        assertEquals("users path is well formed", usersPath, usersPathWithDummyId);
        assertEquals("users path from root is well formed", "/" + usersPath, usersPathWithDummyIdFromRoot);
    }

}
