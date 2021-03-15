package ch.epfl.sdp.appart;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.sdp.appart.scrolling.card.Card;

public interface Database {

    CompletableFuture<List<Card>> getCards();

    CompletableFuture<String> putCard(Card card);

    CompletableFuture<Boolean> updateCard(Card card);

}
