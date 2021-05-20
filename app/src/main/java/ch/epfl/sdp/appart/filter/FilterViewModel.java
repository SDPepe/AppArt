package ch.epfl.sdp.appart.filter;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.user.User;
import dagger.hilt.android.lifecycle.HiltViewModel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;

@HiltViewModel
public class FilterViewModel extends ViewModel {

  private List<Card> cardsFilter;
  final DatabaseService database;
  private int min;
  private int max;
  /*
  private String city;
  private int range;
   */

  @Inject
  public FilterViewModel(DatabaseService database) {
    this.database = database;
  }

  public CompletableFuture<Boolean> confirmFilter(){
      CompletableFuture<List<Card>> queriedCards = database.getCardsFilterPrice(min, max);
      return queriedCards.thenApply(v -> {
        cardsFilter = v;
        return true;
      }).exceptionally(e -> false);
  }

  public List<Card> getCards() {
    return cardsFilter;
  }

  // setters
  public void setMin(int i) { min = i; }

  public void setMax(int i) { max = i; }

  /*
  public void setCity(String s) {
    city = s;
  }

  public void setRange(int i) {
    range = i;
  }

 */



}
