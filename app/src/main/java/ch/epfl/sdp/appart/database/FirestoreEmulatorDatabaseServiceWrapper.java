package ch.epfl.sdp.appart.database;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.glide.visitor.GlideBitmapLoaderVisitor;
import ch.epfl.sdp.appart.glide.visitor.GlideLoaderListenerVisitor;
import ch.epfl.sdp.appart.glide.visitor.GlideLoaderVisitor;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.user.User;

public class FirestoreEmulatorDatabaseServiceWrapper implements DatabaseService {

    private final FirestoreDatabaseService db;
    private static final String LOCALHOST = "10.0.2.2";
    private static final int FIRESTORE_SERVICE_PORT = 8080;

    public FirestoreEmulatorDatabaseServiceWrapper(@NonNull FirestoreDatabaseService databaseService) {
        if (databaseService == null) {
            throw new IllegalArgumentException();
        }

        this.db = databaseService;
        this.db.useEmulator(LOCALHOST, FIRESTORE_SERVICE_PORT);
    }

    @NotNull
    @NonNull
    @Override
    public CompletableFuture<List<Card>> getCards() {
        return this.db.getCards();
    }

    @NotNull
    @NonNull
    @Override
    public CompletableFuture<String> putCard(@NotNull @NonNull Card card) {
        return this.db.putCard(card);
    }

    @NotNull
    @NonNull
    @Override
    public CompletableFuture<Boolean> updateCard(@NotNull @NonNull Card card) {
        return db.updateCard(card);
    }

    @NotNull
    @NonNull
    @Override
    public CompletableFuture<User> getUser(String userId) {
        return db.getUser(userId);
    }

    @NotNull
    @NonNull
    @Override
    public CompletableFuture<Boolean> putUser(User user) {
        return db.putUser(user);
    }

    @NotNull
    @NonNull
    @Override
    public CompletableFuture<Boolean> updateUser(User user) {
        return db.updateUser(user);
    }

    @NotNull
    @NonNull
    @Override
    public CompletableFuture<Ad> getAd(String id) {
        return db.getAd(id);
    }

    @NotNull
    @NonNull
    @Override
    public CompletableFuture<String> putAd(Ad ad) {
        return db.putAd(ad);
    }

    @Override
    public CompletableFuture<Void> clearCache() {
        return db.clearCache();
    }

    @Override
    public void accept(GlideLoaderVisitor visitor) {
        db.accept(visitor);
    }

    @Override
    public void accept(GlideBitmapLoaderVisitor visitor) {
        db.accept(visitor);
    }

    @Override
    public void accept(GlideLoaderListenerVisitor visitor) { db.accept(visitor); }
}
