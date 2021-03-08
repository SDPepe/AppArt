package ch.epfl.sdp.appart;

import java.util.List;

public interface Database {

  public List<Card> getCards();
  public boolean putCard(Card card);

}
