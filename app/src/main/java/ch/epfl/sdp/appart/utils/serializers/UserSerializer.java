package ch.epfl.sdp.appart.utils.serializers;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import ch.epfl.sdp.appart.database.firebaselayout.UserLayout;
import ch.epfl.sdp.appart.user.AppUser;
import ch.epfl.sdp.appart.user.User;

public class UserSerializer implements Serializer<User> {

    @Override
    public Map<String, Object> serialize(User data) {
        Map<String, Object> docData = new HashMap<>();
        docData.put(UserLayout.AGE, data.getAge());
        docData.put(UserLayout.EMAIL, data.getUserEmail());
        docData.put(UserLayout.GENDER, data.getGender());
        docData.put(UserLayout.NAME, data.getName());
        docData.put(UserLayout.PHONE, data.getPhoneNumber());
        docData.put(UserLayout.PICTURE, data.getProfileImage());
        docData.put(UserLayout.AD_IDS, data.getAdsIds());
        docData.put(UserLayout.FAVORITE_IDS, new ArrayList<>(data.getFavoritesIds()));
        return docData;
    }

    @Override
    public User deserialize(String id, Map<String, Object> data) {
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
            user.setProfileImage((String) rawPfpRef); //WARNING WAS "profilePicture" before not matching our actual
        }

        Object rawAdsIds = data.get(UserLayout.AD_IDS);
        if (rawAdsIds !=  null) {
            for (String adId : (List<String>) rawAdsIds)
                user.addAdId(adId);
        }

        Object rawFavoriteIds = data.get(UserLayout.FAVORITE_IDS);
        if (rawFavoriteIds !=  null) {
            List<String> favoriteIds = (List<String>) rawFavoriteIds;
            Log.d("favorites", "User has " + favoriteIds.size() + " favorites");
            for (int i = 0; i < favoriteIds.size(); ++i)
                user.addFavorite(favoriteIds.get(i));
        }

        return user;
    }
}
