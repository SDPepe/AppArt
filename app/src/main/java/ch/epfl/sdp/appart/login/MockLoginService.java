package ch.epfl.sdp.appart.login;

import android.util.Pair;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import ch.epfl.sdp.appart.user.AppUser;
import ch.epfl.sdp.appart.user.User;

public class MockLoginService implements LoginService {

    private User currentUser = null;
    private final Map<Pair<String, String>, User> users = new HashMap<>();

    private final AppUser antoine   = new AppUser("1111", "antoine@epfl.ch");
    private final AppUser lorenzo   = new AppUser("2222", "lorenzo@epfl.ch");
    private final AppUser carlo     = new AppUser("3333", "carlo@epfl.ch");
    private final AppUser filippo   = new AppUser("4444", "filippo@epfl.ch");
    private final AppUser emilien   = new AppUser("5555", "emilien@epfl.ch");
    private final AppUser quentin   = new AppUser("6666", "quentin@epfl.ch");

    //using user id as password to make it straightforward
    private final Pair<String, String> antoineEmailPassword = new Pair<>(antoine.getUserEmail(), antoine.getUserId());
    private final Pair<String, String> lorenzoEmailPassword = new Pair<>(lorenzo.getUserEmail(), lorenzo.getUserId());
    private final Pair<String, String> carloEmailPassword   = new Pair<>(carlo.getUserEmail(), carlo.getUserId());
    private final Pair<String, String> filippoEmailPassword = new Pair<>(filippo.getUserEmail(), filippo.getUserId());
    private final Pair<String, String> emilienEmailPassword = new Pair<>(emilien.getUserEmail(), emilien.getUserId());
    private final Pair<String, String> quentinEmailPassword = new Pair<>(quentin.getUserEmail(), quentin.getUserId());

    public MockLoginService() {

        users.put(antoineEmailPassword, antoine);
        users.put(lorenzoEmailPassword, lorenzo);
        users.put(filippoEmailPassword, filippo);
        users.put(emilienEmailPassword, emilien);
        users.put(carloEmailPassword, carlo);
        users.put(quentinEmailPassword, quentin);

    }

    @Override
    public CompletableFuture<User> loginWithEmail(String email, String password) {
        if (email == null || password == null) {
            throw new IllegalArgumentException("arguments cannot be null");
        }
        CompletableFuture<User> result = new CompletableFuture<>();
        for (Map.Entry<Pair<String, String>, User> entry : users.entrySet()) {
            String entryEmail = entry.getKey().first;
            String entryPassword = entry.getKey().second;
            if (email.equals(entryEmail) && password.equals(entryPassword)) {
                result.complete(entry.getValue());
                currentUser = entry.getValue();
            }
        }
        if (!result.isDone()) {
            result.completeExceptionally(new LoginServiceRequestFailedException("failed to login the user"));
        }
        return result;
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    @Override
    public CompletableFuture<Void> resetPasswordWithEmail(String email) {
        CompletableFuture<Void> result = new CompletableFuture<>();
        for (Map.Entry<Pair<String, String>, User> entry : users.entrySet()) {
            String entryEmail = entry.getKey().first;
            if (email.equals(entryEmail)) {
                result.complete(null);
                return result;
            }
        }
        result.completeExceptionally(new LoginServiceRequestFailedException("failed to find the associate email"));
        return result;
    }

    @Override
    public CompletableFuture<User> createUser(String email, String password) {
        if (email == null || password == null) {
            throw new IllegalArgumentException("arguments cannot be null");
        }
        CompletableFuture<User> result = new CompletableFuture<>();
        byte[] temporary = new byte[5]; //one more than the other to avoid collision
        new Random().nextBytes(temporary);
        String id = new String(temporary, Charset.forName("UTF-8"));

        User newUser = new AppUser(id, email);
        users.put(new Pair<>(email, password), newUser);
        result.complete(newUser);

        return result;
    }

    @Override
    public CompletableFuture<Void> updateEmailAddress(String email) {
        return null;
    }

    @Override
    public CompletableFuture<Void> updatePassword(String password) {
        return null;
    }

    @Override
    public CompletableFuture<Void> sendEmailVerification() {
        return null;
    }

    @Override
    public CompletableFuture<Void> deleteUser() {
        return null;
    }

    @Override
    public CompletableFuture<Void> reAuthenticateUser(String email, String password) {
        return null;
    }

    @Override
    public void useEmulator(String ip, int port) {

    }
}
