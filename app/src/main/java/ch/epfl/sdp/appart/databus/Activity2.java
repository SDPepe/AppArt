package ch.epfl.sdp.appart.databus;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import java.util.List;

import javax.inject.Inject;

import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.hilt.databus.annotations.IntegerDataBus;
import ch.epfl.sdp.appart.hilt.databus.annotations.StringDataBus;
import ch.epfl.sdp.appart.hilt.databus.annotations.UriListDataBus;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class Activity2 extends AppCompatActivity {

    /**
     * Bellow we declare the bus we want to use
     */
    @IntegerDataBus
    @Inject
    DataBus<Integer> integerBus;

    @StringDataBus
    @Inject
    DataBus<String> stringBus;

    @UriListDataBus
    @Inject
    DataBus<List<Uri>> uriListBus;

    /**
     * Solution 2 : private databus
     */

    @UriListDataBus
    @Inject
    PrivateDataBus<List<Uri>> uriPrivateDataBus;

    private PrivateDataBusToken token =
            PrivateDataBusTokenFactory.makeToken(Activity1.class, Activity2.class);

    private Integer a;
    private String b;
    private List<Uri> c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        /**
         * Solution 1 and 2 get data
         */
        a = integerBus.getData();
        b = stringBus.getData();
        c = uriListBus.getData();

        c = uriPrivateDataBus.getData(token);

    }
}