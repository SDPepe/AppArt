package ch.epfl.sdp.appart.filter;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.location.geocoding.GoogleGeocodingService;
import ch.epfl.sdp.appart.location.place.locality.Locality;
import ch.epfl.sdp.appart.location.place.locality.LocalityFactory;
import ch.epfl.sdp.appart.location.place.locality.MalformedLocalityException;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.user.User;
import dagger.hilt.android.lifecycle.HiltViewModel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import javax.inject.Inject;

@HiltViewModel
public class FilterViewModel extends ViewModel {

  private List<Card> cardsFilter;
  final DatabaseService database;
  final GoogleGeocodingService geocodingService;
  private int min;
  private int max;
  private Locality city;
  private float range;



  @Inject
  public FilterViewModel(DatabaseService database, GoogleGeocodingService geocodingService) {
    this.database = database;
    this.geocodingService = geocodingService;
  }

  public CompletableFuture<Boolean> confirmFilter(){
      CompletableFuture<List<Card>> queriedCards = database.getCardsFilterPrice(min, max);
      return queriedCards.thenApply(cardList -> {
        List<Card> cardsFilterBuffer = new ArrayList<>();
        for (Card c : cardList){
          System.out.println("--------- ");
          System.out.println("card " + c.getCity() );
          //remove postal code
          String [] l = c.getCity().split(" ", 2);
          try {
            Locality location = LocalityFactory.makeLocality(l[1]);
            float dist;
            dist = geocodingService.getDistance(location, city).get();
            System.out.println("dist=" + dist +" - "+ l[1] );
            //conversion in kilometers
            dist = dist/1000;
            if(dist <= range){
              cardsFilterBuffer.add(c);
            }
          } catch (ExecutionException e) {
            e.printStackTrace();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }catch(MalformedLocalityException e){
            e.printStackTrace();
          }catch (Exception e){
            e.printStackTrace();
          }
        }
        cardsFilter = cardsFilterBuffer;
        return true;
      }).exceptionally(e -> false);
  }

  public List<Card> getCards() {
    return cardsFilter;
  }

  // setters
  public void setMin(int i) { min = i; }

  public void setMax(int i) { max = i; }


  public void setCity(String s) {
    city = LocalityFactory.makeLocality(s);
  }

  public void setRange(float i) {
    range = i;
  }





}
