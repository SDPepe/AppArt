package ch.epfl.sdp.appart.utils.serializers;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        if (data.get(UserLayout.AGE) != null) {
            user.setAge((long) data.get(UserLayout.AGE));
        }

        if (data.get(UserLayout.GENDER) != null) {
            user.setGender((String) data.get(UserLayout.GENDER));
        }

        if (data.get(UserLayout.NAME) != null) {
            user.setName((String) data.get(UserLayout.NAME));
        }

        if (data.get(UserLayout.PHONE) != null) {
            user.setPhoneNumber((String) data.get(UserLayout.PHONE));
        }

        if (data.get(UserLayout.PICTURE) != null) {
            user.setProfileImage((String) data.get(UserLayout.PICTURE)); //WARNING WAS "profilePicture" before not matching our actual
        }

        if (data.get(UserLayout.AD_IDS) !=  null) {
            for (String adId : (List<String>) data.get(UserLayout.AD_IDS))
                user.addAdId(adId);
        }

        if (data.get(UserLayout.FAVORITE_IDS) !=  null) {
            for (String favId: (List<String>) data.get(UserLayout.FAVORITE_IDS))
                user.addFavorite(favId);
        }

        return user;
    }
}
