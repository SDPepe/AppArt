package ch.epfl.sdp.appart.database.local;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import ch.epfl.sdp.appart.database.exceptions.LocalDatabaseException;
import ch.epfl.sdp.appart.database.firebaselayout.UserLayout;
import ch.epfl.sdp.appart.user.User;
import ch.epfl.sdp.appart.utils.FileIO;
import ch.epfl.sdp.appart.utils.StoragePathBuilder;
import ch.epfl.sdp.appart.utils.serializers.UserSerializer;

public class LocalUserReader {

    /**
     * This reads the folder of a user. It will load the user data from disk.
     *
     * @param userFile a file pointing to the user's directory
     */
    private static void readUserFolder(File userFile,
                                       Map<String, User> idsToUser
            , Set<String> userIds) throws LocalDatabaseException {

        String dataPath =
                new StoragePathBuilder().toDirectory(userFile.getPath()).withFile(LocalDatabasePaths.userData);
        Map<String, Object> userMap = FileIO.readMapObject(dataPath);
        if (userMap == null)
            throw new LocalDatabaseException("Could not read the following " +
                    "file : " + dataPath);


        User user = UserSerializer.deserializeLocal(userMap);
        if (!idsToUser.containsKey(userMap.get(UserLayout.ID))) {
            idsToUser.put((String) userMap.get(UserLayout.ID), user);
            userIds.add(user.getUserId());
        }
    }

    /**
     * This reads the whole user data stored for the current user. It traverses
     * all the folder with a user id as their name, more specifically all the
     * folders in the users folder. The reading on disk happens asynchronously.
     *
     * @param currentUserID the current user id
     * @param idsToUser     a map mapping user ids to users
     * @param userIds       a set of user ids
     * @return a completable future that indicates if the operation succeeded
     * or not
     */
    static CompletableFuture<Void> readUsers(String currentUserID,
                                             Map<String, User> idsToUser,
                                             Set<String> userIds) {
        String userPath =
                LocalDatabasePaths.usersFolder(currentUserID);
        File userFolder = new File(userPath);
        return CompletableFuture.runAsync(() -> {
            File[] files = userFolder.listFiles(File::isDirectory);
            if (files != null) {
                for (File folder :
                        files) {
                    try {
                        readUserFolder(folder, idsToUser, userIds);
                    } catch (LocalDatabaseException e) {
                        e.printStackTrace();
                        throw new CompletionException(e);
                    }
                }
            }

        });
    }
}
