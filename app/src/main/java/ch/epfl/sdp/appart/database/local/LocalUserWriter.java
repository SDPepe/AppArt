package ch.epfl.sdp.appart.database.local;

import java.io.File;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import ch.epfl.sdp.appart.user.AppUser;
import ch.epfl.sdp.appart.user.User;
import ch.epfl.sdp.appart.utils.FileIO;
import ch.epfl.sdp.appart.utils.serializers.UserSerializer;

public class LocalUserWriter {

    /**
     * This method writes a user on disk. If the folder isn't created it
     * makes sure to create one.
     *
     * @param localUser the local user
     * @return a boolean that indicates if the operation succeeded or not
     */
    static boolean writeUser(User localUser, String currentUserID) {
        if (!createUserFolder(currentUserID))
            return false;
        Map<String, Object> userMap = UserSerializer.serializeLocal(localUser);
        return FileIO.writeMapObject(LocalDatabasePaths.userData(currentUserID), userMap);
    }

    /**
     * Constructs a local user from an online user. Like the local ad, the
     * only difference with the online user is the reference to the profile
     * picture. The local user refers to a file on disk rather than a
     * reference to online storage.
     *
     * @param user the original "online" user
     * @return the new local user
     */
    static User buildLocalUser(User user, String currentUserID) {
        User localUser = new AppUser(user.getUserId(), user.getUserEmail());
        if (user.getPhoneNumber() != null) {
            localUser.setPhoneNumber(user.getPhoneNumber());
        }
        if (user.getName() != null) {
            localUser.setName(user.getName());
        }

        localUser.setAge(user.getAge());

        if (user.getGender() != null) {
            localUser.setGender(user.getGender());
        }
        localUser.setProfileImagePathAndName(LocalDatabasePaths.userProfilePic(currentUserID));

        return localUser;
    }

    /**
     * This method writes the profile picture of a user on disk. Actually,
     * the write on disk is performed by loadProfilePic. The correct path is
     * given to this function. We opted to do this, this way because we do
     * not want the local database to be aware if Firebase.
     *
     * @param loadProfilePic the function that performs the write on disk of
     *                       the profile picture of the user
     * @param futureSuccess  a completable future that indicates if the
     *                       operation succeeded or not.
     */
    static void writeProfilePic(Function<String,
            CompletableFuture<Void>> loadProfilePic,
                                CompletableFuture<Void> futureSuccess,
                                String currentUserID) {

        CompletableFuture<Void> futureProfilePic =
                loadProfilePic.apply(LocalDatabasePaths.userProfilePic(currentUserID));
        futureProfilePic.thenAccept(arg -> futureSuccess.complete(null));
        futureProfilePic.exceptionally(e -> {
            futureSuccess.completeExceptionally(e);
            return null;
        });

        futureSuccess.complete(null);
    }

    /**
     * This method creates a folder for a specific user.
     *
     * @return a boolean that indicates the success of the operation
     */
    private static boolean createUserFolder(String currentUserID) {

        String userPath =
                LocalDatabasePaths.userFolder(currentUserID);
        return FileIO.createFolder(userPath, () -> {
            String profilePicPath =
                    LocalDatabasePaths.userProfilePic(currentUserID);
            File profilePic = new File(profilePicPath);
            profilePic.delete();
            return true;
        });
    }
}
