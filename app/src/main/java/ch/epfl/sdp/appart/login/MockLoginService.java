package ch.epfl.sdp.appart.login;


import androidx.core.util.Pair;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

import ch.epfl.sdp.appart.login.exceptions.LoginServiceRequestFailedException;
import ch.epfl.sdp.appart.login.exceptions.MockLoginServiceException;
import ch.epfl.sdp.appart.user.AppUser;
import ch.epfl.sdp.appart.user.User;

@Singleton
public class MockLoginService implements LoginService {

    private final Map<Pair<String, String>, User> users = new HashMap<>();
    private final AppUser antoine = new AppUser("1111", "antoine@epfl.ch");
    private final AppUser lorenzo = new AppUser("2222", "lorenzo@epfl.ch");
    private final AppUser carlo = new AppUser("3333", "carlo@epfl.ch");
    private final AppUser filippo = new AppUser("4444", "filippo@epfl.ch");
    private final AppUser emilien = new AppUser("5555", "emilien@epfl.ch");
    private final AppUser quentin = new AppUser("6666", "quentin@epfl.ch");
    //using user id as password to make it straightforward
    private final Pair<String, String> antoineEmailPassword = new Pair<>(antoine.getUserEmail(), antoine.getUserId());
    private final Pair<String, String> lorenzoEmailPassword = new Pair<>(lorenzo.getUserEmail(), lorenzo.getUserId());
    private final Pair<String, String> carloEmailPassword = new Pair<>(carlo.getUserEmail(), carlo.getUserId());
    private final Pair<String, String> filippoEmailPassword = new Pair<>(filippo.getUserEmail(), filippo.getUserId());
    private final Pair<String, String> emilienEmailPassword = new Pair<>(emilien.getUserEmail(), emilien.getUserId());
    private final Pair<String, String> quentinEmailPassword = new Pair<>(quentin.getUserEmail(), quentin.getUserId());
    private User currentUser = null;

    @Inject
    public MockLoginService() {
        users.put(antoineEmailPassword, antoine);
        users.put(lorenzoEmailPassword, lorenzo);
        users.put(filippoEmailPassword, filippo);
        users.put(emilienEmailPassword, emilien);
        users.put(carloEmailPassword, carlo);
        users.put(quentinEmailPassword, quentin);
    }

    @Nullable
    private User findMatchingUserWithPasswordAndEmail(String email, String password) {
        User result = null;
        for (Map.Entry<Pair<String, String>, User> entry : users.entrySet()) {
            String entryEmail = entry.getKey().first;
            String entryPassword = entry.getKey().second;
            if (email.equals(entryEmail) && password.equals(entryPassword)) {
                result = entry.getValue();
            }
        }
        return result;
    }

    @Override
    public CompletableFuture<User> loginWithEmail(String email, String password) {
        if (email == null) throw new IllegalArgumentException("email cannot be null");
        if (password == null) throw new IllegalArgumentException("password cannot be null");

        currentUser = findMatchingUserWithPasswordAndEmail(email, password);
        CompletableFuture<User> result = new CompletableFuture<>();
        if (currentUser == null) {
            result.completeExceptionally(new LoginServiceRequestFailedException("failed to login the user"));
        } else {
            result.complete(currentUser);
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
        result.completeExceptionally(new LoginServiceRequestFailedException("failed to find the associated email"));
        return result;
    }

    @Override
    public CompletableFuture<User> createUser(String email, String password) {
        if (email == null || password == null)
            throw new IllegalArgumentException("arguments cannot be null");
        if (currentUser != null)
            throw new IllegalStateException("the current user cannot be set !");

        CompletableFuture<User> result = new CompletableFuture<>();
        byte[] temporary = new byte[5]; //one more than the other to avoid collision
        new Random().nextBytes(temporary);
        String id = new String(temporary, StandardCharsets.UTF_8);

        User newUser = new AppUser(id, email);
        users.put(new Pair<>(email, password), newUser);

        loginWithEmail(email, password)
                .thenApply(r -> result.complete(newUser))
                .exceptionally(result::completeExceptionally);

        return result;
    }


    @Override
    public CompletableFuture<Void> updateEmailAddress(String email) {
        if (email == null) throw new IllegalArgumentException("email cannot be null");
        if (getCurrentUser() == null) throw new IllegalStateException("current user must be set");
        CompletableFuture<Void> result = new CompletableFuture<>();

        for (Map.Entry<Pair<String, String>, User> entry : users.entrySet()) {
            if (entry.getValue().equals(currentUser)) {
                users.remove(entry.getKey()); //remove the old mapping
                users.put(new Pair<>(email, entry.getKey().second), currentUser); //refresh the new mapping
                currentUser.setUserEmail(email);
                result.complete(null);
                return result;
            }
        }
        result.completeExceptionally(new MockLoginServiceException("failed to retrieve the main user in the mock login service"));
        return result;
    }

    @Override
    public CompletableFuture<Void> updatePassword(String password) {
        if (password == null) throw new IllegalArgumentException("password cannot be null");
        if (getCurrentUser() == null) throw new IllegalStateException("current user must be set");
        CompletableFuture<Void> result = new CompletableFuture<>();

        for (Map.Entry<Pair<String, String>, User> entry : users.entrySet()) {
            String entryPassword = entry.getKey().second;
            if (entry.getValue().equals(currentUser)) {
                users.remove(entry.getKey()); //remove the old mapping
                users.put(new Pair<>(currentUser.getUserEmail(), password), currentUser); //refresh the new mapping
                result.complete(null);
                return result;
            }
        }
        result.completeExceptionally(new MockLoginServiceException("failed to retrieve the main user in the mock login service"));
        return result;
    }

    @Override
    public CompletableFuture<Void> sendEmailVerification() {
        if (getCurrentUser() == null)
            throw new IllegalStateException("current user must be set when sending verification mail");
        CompletableFuture<Void> result = new CompletableFuture<>();
        result.complete(null);
        return result;
    }

    @Override
    public CompletableFuture<Void> deleteUser() {
        if (currentUser == null)
            throw new IllegalStateException("current user must be set when deleting it");
        CompletableFuture<Void> result = new CompletableFuture<>();

        Pair<String, String> keyToRemove = null;
        for (Map.Entry<Pair<String, String>, User> entry : users.entrySet()) {
            if (entry.getValue().equals(currentUser)) {
                keyToRemove = entry.getKey();
            }
        }

        if (keyToRemove != null) {
            users.remove(keyToRemove);
        }

        currentUser = null;
        result.complete(null);

        return result;
    }

    @Override
    public CompletableFuture<Void> reAuthenticateUser(String email, String password) {
        if (email == null) throw new IllegalArgumentException("password cannot be null");
        if (password == null) throw new IllegalArgumentException("password cannot be null");
        if (getCurrentUser() == null)
            throw new IllegalStateException("current user must be set when reAuthentication");

        //note : this function will never fail beyond this point
        CompletableFuture<Void> result = new CompletableFuture<>();
        result.complete(null);

        return result;
    }


}
