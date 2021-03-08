package ch.epfl.sdp.appart;

import android.media.Image;
import ch.epfl.sdp.appart.user.User;

public class Card {
  private final int id;
  private final User owner;
  private String title;
  private String address;
  private String price;
  private Image image;

  public Card(int id, User owner) {
    this.id = id;
    this.owner = owner;

    //default parameters....
    this.title = "APPARTAMENT";
    this.address = "SWISS";
    this.price = "100fr/month";
    //this.image;
  }

  public Card(int id, User owner, String title, String address, String price, Image image) {
    this.id = id;
    this.owner = owner;
    this.title = title;
    this.address = address;
    this.price = price;
    this.image = image;
  }

  // Getter Setter
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getPrice() {
    return price;
  }

  public void setPrice(String price) {
    this.price = price;
  }

  public Image getImage() {
    return image;
  }

  public void setImage(Image image) {
    this.image = image;
  }

}
