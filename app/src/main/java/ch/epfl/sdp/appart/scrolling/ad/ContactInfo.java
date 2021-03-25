package ch.epfl.sdp.appart.scrolling.ad;

import android.appwidget.AppWidgetHost;

public class ContactInfo {
    public final String userEmail;
    public final String userPhoneNumber;
    public final String name;

    public ContactInfo(String userEmail, String userPhoneNumber, String name) {
        this.userEmail = userEmail;
        this.userPhoneNumber = userPhoneNumber;
        this.name = name;
    }
}
