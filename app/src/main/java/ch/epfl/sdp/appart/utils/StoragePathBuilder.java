package ch.epfl.sdp.appart.utils;

import ch.epfl.sdp.appart.database.firebaselayout.AdLayout;
import ch.epfl.sdp.appart.database.firebaselayout.CardLayout;
import ch.epfl.sdp.appart.database.firebaselayout.FirebaseLayout;

/**
 * One time use only object that allows building path for the database.
 * This will avoid having lengthy addition of layouts that can leak implementation
 * details like the use of firebaseService
 */
public class StoragePathBuilder {
    
    private final StringBuilder stringBuilder = new StringBuilder();

    public StoragePathBuilder fromRoot() {
        stringBuilder.append(FirebaseLayout.SEPARATOR);
        return this;
    }

    public StoragePathBuilder toAdsStorageDirectory() {
        stringBuilder.append(FirebaseLayout.ADS_DIRECTORY);
        stringBuilder.append(FirebaseLayout.SEPARATOR);
        return this;
    }

    public StoragePathBuilder toCardsStorageDirectory() {
        stringBuilder.append(FirebaseLayout.CARDS_DIRECTORY);
        stringBuilder.append(FirebaseLayout.SEPARATOR);
        return this;
    }

    public StoragePathBuilder toUsersStorageDirectory() {
        stringBuilder.append(FirebaseLayout.USERS_DIRECTORY);
        stringBuilder.append(FirebaseLayout.SEPARATOR);
        return this;
    }

    public StoragePathBuilder toDirectory(String directoryName) {
        stringBuilder.append(directoryName);
        stringBuilder.append(FirebaseLayout.SEPARATOR);
        return this;
    }

    public String withFile(String fileName) {
        stringBuilder.append(fileName);
        return stringBuilder.toString();
    }
    
}
