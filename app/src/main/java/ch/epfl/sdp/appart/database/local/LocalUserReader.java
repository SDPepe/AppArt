package ch.epfl.sdp.appart.database.local;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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
     * @return a boolean that indicates if the operation succeeded or not
     */
    private static boolean readUserFolder(File userFile,
                                          Map<String, User> idsToUser
            , Set<String> userIds) {

        String dataPath =
                new StoragePathBuilder().toDirectory(userFile.getPath()).withFile(LocalDatabasePaths.userData);
        Map<String, Object> userMap = FileIO.readMapObject(dataPath);
        if (userMap == null) return false;


        User user = UserSerializer.deserializeLocal(userMap);
        if (!idsToUser.containsKey(userMap.get(UserLayout.ID))) {
            idsToUser.put((String) userMap.get(UserLayout.ID), user);
            userIds.add(user.getUserId());
        }

        return true;
    }

    /**
     * This reads the whole user data stored for the current user. It traverses
     * all the folder with a user id as their name, more specifically all the
     * folders in the users folder.
     *
     * @return a boolean that indicates if the operation succeeded or not
     */
    static boolean readUsers(String currentUserID,
                             Map<String, User> idsToUser,
                             Set<String> userIds, Runnable onSuccess) {
        String userPath =
                LocalDatabasePaths.usersFolder(currentUserID);
        File userFolder = new File(userPath);
        boolean success = true;
        for (File folder :
                Objects.requireNonNull(userFolder.listFiles(File::isDirectory))) {
            success &= readUserFolder(folder, idsToUser, userIds);
        }
        if (success) {
            onSuccess.run();
        }
        return success;
    }
}
