package ch.epfl.sdp.appart.utils.serializers;

import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import ch.epfl.sdp.appart.database.firebaselayout.UserLayout;
import ch.epfl.sdp.appart.user.AppUser;
import ch.epfl.sdp.appart.user.User;

public class UserSerializerTest {
    public UserSerializer serializer = new UserSerializer();

    @Test
    public void serializeTest() {
        Random random = new Random();
        for (int i = 0; i < 1000; ++i) {
            User user = new AppUser("", generateRandomString(10, random));
            user.setAge(random.nextLong());
            user.setGender(random.nextBoolean() ? "MALE" : "FEMALE");
            user.setName(generateRandomString(10, random));
            user.setPhoneNumber(generateRandomString(10, random));
            user.setProfileImagePathAndName(generateRandomString(10, random));


            Map<String, Object> serializedUser = serializer.serialize(user);
            Assert.assertEquals(serializedUser.get(UserLayout.AGE), user.getAge());
            Assert.assertEquals(serializedUser.get(UserLayout.EMAIL), user.getUserEmail());
            Assert.assertEquals(serializedUser.get(UserLayout.GENDER), user.getGender());
            Assert.assertEquals(serializedUser.get(UserLayout.NAME), user.getName());
            Assert.assertEquals(serializedUser.get(UserLayout.PHONE), user.getPhoneNumber());
            Assert.assertEquals(serializedUser.get(UserLayout.PICTURE), user.getProfileImagePathAndName());
        }
    }

    @Test
    public void deserializeTest() {
        Random random = new Random();
        for (int i = 0; i < 1000; ++i) {
            Map<String, Object> serializedUser = new HashMap<>();
            serializedUser.put(UserLayout.AGE, random.nextLong());
            serializedUser.put(UserLayout.EMAIL, generateRandomString(10, random));
            serializedUser.put(UserLayout.GENDER, random.nextBoolean() ? "MALE" : "FEMALE");
            serializedUser.put(UserLayout.NAME, generateRandomString(10, random));
            serializedUser.put(UserLayout.PHONE, generateRandomString(10, random));
            serializedUser.put(UserLayout.PICTURE, generateRandomString(10, random));

            User user = serializer.deserialize("", serializedUser);
            Assert.assertEquals(serializedUser.get(UserLayout.AGE), user.getAge());
            Assert.assertEquals(serializedUser.get(UserLayout.EMAIL), user.getUserEmail());
            Assert.assertEquals(serializedUser.get(UserLayout.GENDER), user.getGender());
            Assert.assertEquals(serializedUser.get(UserLayout.NAME), user.getName());
            Assert.assertEquals(serializedUser.get(UserLayout.PHONE), user.getPhoneNumber());
            Assert.assertEquals(serializedUser.get(UserLayout.PICTURE), user.getProfileImagePathAndName());
        }
    }

    @Test
    public void serializeThenDeserializeTest() {
        Random random = new Random();
        for (int i = 0; i < 1000; ++i) {
            User user = new AppUser("", generateRandomString(10, random));
            user.setAge(random.nextLong());
            user.setGender(random.nextBoolean() ? "MALE" : "FEMALE");
            user.setName(generateRandomString(10, random));
            user.setPhoneNumber(generateRandomString(10, random));
            user.setProfileImagePathAndName(generateRandomString(10, random));
            Map<String, Object> serializedUser = serializer.serialize(user);
            User deserializedUser = serializer.deserialize("", serializedUser);
            Assert.assertEquals(deserializedUser.getAge(), user.getAge());
            Assert.assertEquals(deserializedUser.getUserEmail(), user.getUserEmail());
            Assert.assertEquals(deserializedUser.getGender(), user.getGender());
            Assert.assertEquals(deserializedUser.getName(), user.getName());
            Assert.assertEquals(deserializedUser.getPhoneNumber(), user.getPhoneNumber());
            Assert.assertEquals(deserializedUser.getProfileImagePathAndName(), user.getProfileImagePathAndName());
        }
    }

    private String generateRandomString(int length, Random rand) {
        byte[] bytes = new byte[length];
        rand.nextBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}