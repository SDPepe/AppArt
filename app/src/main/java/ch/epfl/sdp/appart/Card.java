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
  private boolean hidePrice;
  private boolean hideAddress;

  public Card(int id, User owner, String title, String address, String price, Image image,
              boolean hidePrice, boolean hideAddress) {
    if (owner == null || title == null || address == null || price == null || image == null)
      throw new IllegalArgumentException("Argument is null!");

    this.id = id;
    this.owner = owner;
    this.title = title;
    this.address = address;
    this.price = price;
    this.image = image;
    this.hidePrice = hidePrice;
    this.hideAddress = hideAddress;
  }

  // Getter Setter
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    nullChecker(title);
    this.title = title;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    nullChecker(address);
    this.address = address;
  }

  public String getPrice() {
    return price;
  }

  public void setPrice(String price) {
    nullChecker(price);
    this.price = price;
  }

  public Image getImage() {
    return image;
  }

  public void setImage(Image image) {
    nullChecker(image);
    this.image = image;
  }

  public Boolean hidePrice(){ return hidePrice; }

  public void setHidePrice(boolean b){ this.hidePrice = b; }

  public Boolean hideAddress(){ return hideAddress; }

  public void setHideAddress(boolean b){ this.hideAddress = b; }

  private void nullChecker(Object o){
    if (o == null)
      throw new IllegalArgumentException("Argument is null!");
  }
}
