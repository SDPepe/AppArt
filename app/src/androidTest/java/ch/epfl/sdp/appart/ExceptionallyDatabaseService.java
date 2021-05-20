package ch.epfl.sdp.appart;

import android.net.Uri;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.MockDatabaseService;
import ch.epfl.sdp.appart.database.exceptions.DatabaseServiceException;
import ch.epfl.sdp.appart.glide.visitor.GlideBitmapLoaderVisitor;
import ch.epfl.sdp.appart.glide.visitor.GlideLoaderListenerVisitor;
import ch.epfl.sdp.appart.glide.visitor.GlideLoaderVisitor;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.user.User;

/**
 * MockDatabaseService used when there's need to check for actions happening on
 * failing future result.
 */
public class ExceptionallyDatabaseService implements DatabaseService {

    @NonNull
    @NotNull
    @Override
    public CompletableFuture<List<Card>> getCards() {
        CompletableFuture<List<Card>> res = new CompletableFuture<>();
        res.completeExceptionally(new DatabaseServiceException("Mock"));
        return res;
    }

    @NonNull
    @NotNull
    @Override
    public CompletableFuture<List<Card>> getCardsFilter(@NonNull @NotNull String location) {
        CompletableFuture<List<Card>> res = new CompletableFuture<>();
        res.completeExceptionally(new DatabaseServiceException("Mock"));
        return res;
    }

    @NonNull
    @NotNull
    @Override
    public CompletableFuture<List<Card>> getCardsFilterPrice(int min, int max) {
        CompletableFuture<List<Card>> res = new CompletableFuture<>();
        res.completeExceptionally(new DatabaseServiceException("Mock"));
        return res;
    }

    @NonNull
    @NotNull
    @Override
    public CompletableFuture<List<Card>> getCardsById(@NonNull @NotNull List<String> ids) {
        CompletableFuture<List<Card>> res = new CompletableFuture<>();
        res.completeExceptionally(new DatabaseServiceException("Mock"));
        return res;
    }

    @NonNull
    @NotNull
    @Override
    public CompletableFuture<Boolean> updateCard(@NonNull @NotNull Card card) {
        CompletableFuture<Boolean> res = new CompletableFuture<>();
        res.completeExceptionally(new DatabaseServiceException("Mock"));
        return res;
    }

    @NonNull
    @NotNull
    @Override
    public CompletableFuture<User> getUser(String userId) {
        CompletableFuture<User> res = new CompletableFuture<>();
        res.completeExceptionally(new DatabaseServiceException("Mock"));
        return res;
    }

    @NonNull
    @NotNull
    @Override
    public CompletableFuture<Boolean> putUser(User user) {
        CompletableFuture<Boolean> res = new CompletableFuture<>();
        res.completeExceptionally(new DatabaseServiceException("Mock"));
        return res;
    }

    @NonNull
    @NotNull
    @Override
    public CompletableFuture<Boolean> updateUser(User user) {
        CompletableFuture<Boolean> res = new CompletableFuture<>();
        res.completeExceptionally(new DatabaseServiceException("Mock"));
        return res;
    }

    @NonNull
    @NotNull
    @Override
    public CompletableFuture<Ad> getAd(String id) {
        CompletableFuture<Ad> res = new CompletableFuture<>();
        res.completeExceptionally(new DatabaseServiceException("Mock"));
        return res;
    }

    @NonNull
    @NotNull
    @Override
    public CompletableFuture<String> putAd(Ad ad, List<Uri> picturesUris, List<Uri> panoramasUris) {
        CompletableFuture<String> res = new CompletableFuture<>();
        res.completeExceptionally(new DatabaseServiceException("Mock"));
        return res;
    }

    @NonNull
    @NotNull
    @Override
    public CompletableFuture<Boolean> putImage(Uri uri, String imagePathAndName) {
        CompletableFuture<Boolean> res = new CompletableFuture<>();
        res.completeExceptionally(new DatabaseServiceException("Mock"));
        return res;
    }

    @NonNull
    @NotNull
    @Override
    public CompletableFuture<Boolean> deleteImage(String imagePathAndName) {
        CompletableFuture<Boolean> res = new CompletableFuture<>();
        res.completeExceptionally(new DatabaseServiceException("Mock"));
        return res;
    }

    @Override
    public CompletableFuture<Void> clearCache() {
        CompletableFuture<Void> res = new CompletableFuture<>();
        res.completeExceptionally(new DatabaseServiceException("Mock"));
        return res;
    }

    @Override
    public void accept(GlideLoaderVisitor visitor) {
        visitor.visit(new MockDatabaseService());
    }

    @Override
    public void accept(GlideBitmapLoaderVisitor visitor) {
        visitor.visit(new MockDatabaseService());
    }

    @Override
    public void accept(GlideLoaderListenerVisitor visitor) {
        visitor.visit(new MockDatabaseService());
    }
}
