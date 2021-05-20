package ch.epfl.sdp.appart.utils.serializers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.sdp.appart.database.firebaselayout.AdLayout;
import ch.epfl.sdp.appart.database.firebaselayout.CardLayout;
import ch.epfl.sdp.appart.database.firebaselayout.UserLayout;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.user.AppUser;
import ch.epfl.sdp.appart.user.User;

public class UserSerializer {

    //To prevent construction
    private UserSerializer() {
    }

    public static Map<String, Object> serialize(User data) {
        Map<String, Object> docData = new HashMap<>();
        docData.put(UserLayout.AGE, data.getAge());
        docData.put(UserLayout.EMAIL, data.getUserEmail());
        docData.put(UserLayout.GENDER, data.getGender());
        docData.put(UserLayout.NAME, data.getName());
        docData.put(UserLayout.PHONE, data.getPhoneNumber());
        docData.put(UserLayout.PICTURE, data.getProfileImagePathAndName());
        docData.put(UserLayout.FAVORITE_IDS, new ArrayList<String>(data.getFavoritesIds()));
        return docData;
    }

    public static User deserialize(String id, Map<String, Object> data) {
        AppUser user = new AppUser(id, (String) data.get(UserLayout.EMAIL));

        Object rawAge = data.get(UserLayout.AGE);
        if (rawAge != null) {
            user.setAge((long) rawAge);
        }

        Object rawGender = data.get(UserLayout.GENDER);
        if (rawGender != null) {
            user.setGender((String) rawGender);
        }

        Object rawName = data.get(UserLayout.NAME);
        if (rawName != null) {
            user.setName((String) rawName);
        }

        Object rawPhoneNumber = data.get(UserLayout.PHONE);
        if (rawPhoneNumber != null) {
            user.setPhoneNumber((String) rawPhoneNumber);
        }

        Object rawPfpRef = data.get(UserLayout.PICTURE);
        if (rawPfpRef != null) {
            user.setProfileImagePathAndName((String) rawPfpRef); //WARNING WAS "profilePicture" before not matching our actual
        }

        Object rawFavorites = data.get(UserLayout.FAVORITE_IDS);
        if (rawFavorites != null) {
            for (String fav : (List<String>) rawFavorites) {
                user.addFavorite(fav);
            }
        }

        return user;
    }

    public static Map<String, Object> serializeLocal(User data) {
        Map<String, Object> docData = new HashMap<>();
        docData.put(UserLayout.AGE, data.getAge());
        docData.put(UserLayout.EMAIL, data.getUserEmail());
        docData.put(UserLayout.GENDER, data.getGender());
        docData.put(UserLayout.NAME, data.getName());
        docData.put(UserLayout.PHONE, data.getPhoneNumber());
        docData.put(UserLayout.PICTURE, data.getProfileImagePathAndName());
        docData.put(UserLayout.ID, data.getUserId());
        docData.put(UserLayout.FAVORITE_IDS, new ArrayList<String>(data.getFavoritesIds()));
        return docData;
    }

    public static User deserializeLocal(Map<String, Object> data) {

        AppUser user = new AppUser((String)data.get(UserLayout.ID), (String) data.get(UserLayout.EMAIL));

        Object rawAge = data.get(UserLayout.AGE);
        if (rawAge != null) {
            user.setAge((long) rawAge);
        }

        Object rawGender = data.get(UserLayout.GENDER);
        if (rawGender != null) {
            user.setGender((String) rawGender);
        }

        Object rawName = data.get(UserLayout.NAME);
        if (rawName != null) {
            user.setName((String) rawName);
        }

        Object rawPhoneNumber = data.get(UserLayout.PHONE);
        if (rawPhoneNumber != null) {
            user.setPhoneNumber((String) rawPhoneNumber);
        }

        Object rawPfpRef = data.get(UserLayout.PICTURE);
        if (rawPfpRef != null) {
            user.setProfileImagePathAndName((String) rawPfpRef); //WARNING WAS "profilePicture" before not matching our actual
        }

        Object rawFavorites = data.get(UserLayout.FAVORITE_IDS);
        if (rawFavorites != null) {
            for (String fav : (List<String>) rawFavorites) {
                user.addFavorite(fav);
            }
        }

        return user;
    }
}
