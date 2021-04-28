package ch.epfl.sdp.appart.utils.serializers;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
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

        return user;
    }
}
