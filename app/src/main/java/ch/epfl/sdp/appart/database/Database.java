package ch.epfl.sdp.appart.database;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.sdp.appart.glide.visitor.DatabaseVisitorHost;
import ch.epfl.sdp.appart.scrolling.ad.Ad;
import ch.epfl.sdp.appart.scrolling.card.Card;

public interface Database extends DatabaseVisitorHost {

    CompletableFuture<List<Card>> getCards();
    CompletableFuture<String> putCard(Card card);
    CompletableFuture<Boolean> updateCard(Card card);
    CompletableFuture<Ad> getAd(String id);

}
