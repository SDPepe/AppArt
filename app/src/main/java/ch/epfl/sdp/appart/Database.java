package ch.epfl.sdp.appart;

import ch.epfl.sdp.appart.user.User;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.sdp.appart.scrolling.card.Card;

public interface Database {

    CompletableFuture<List<Card>> getCards();

    CompletableFuture<String> putCard(Card card);

    CompletableFuture<Boolean> updateCard(Card card);

    CompletableFuture<User> getUser(String userId);

    CompletableFuture<Boolean> putUser(User user);

    CompletableFuture<Boolean> updateUser(User user);

}
