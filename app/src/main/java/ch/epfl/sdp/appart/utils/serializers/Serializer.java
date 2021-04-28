package ch.epfl.sdp.appart.utils.serializers;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Map;

public interface Serializer<T> {
    Map<String, Object> serialize(T data);
    T deserialize(String id, Map<String, Object> serializedData);
}
