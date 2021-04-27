package ch.epfl.sdp.appart.databus;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import java.util.List;

import javax.inject.Inject;

import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.hilt.annotations.IntegerDataBus;
import ch.epfl.sdp.appart.hilt.annotations.StringDataBus;
import ch.epfl.sdp.appart.hilt.annotations.StringMutableDataBus;
import ch.epfl.sdp.appart.hilt.annotations.UriListDataBus;
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

    @StringMutableDataBus
    @Inject
    ActivityMutableDataBus<String> stringMutableDataBus;

    private Integer a;
    private String b;
    private List<Uri> c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        //we retrieve the data sent by activity 1
        a = integerBus.getData();
        b = stringBus.getData();
        c = uriListBus.getData();

    }
}