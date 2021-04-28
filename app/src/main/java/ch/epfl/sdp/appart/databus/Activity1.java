package ch.epfl.sdp.appart.databus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.hilt.databus.annotations.IntegerDataBus;
import ch.epfl.sdp.appart.hilt.databus.annotations.StringDataBus;
import ch.epfl.sdp.appart.hilt.databus.annotations.UriListDataBus;
import dagger.hilt.android.AndroidEntryPoint;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

@AndroidEntryPoint
public class Activity1 extends AppCompatActivity {

    @IntegerDataBus
    @Inject
    DataBus<Integer> integerBus;

    @StringDataBus
    @Inject
    DataBus<String> stringBus;

    @UriListDataBus
    @Inject
    DataBus<List<Uri>> uriListBus;

    @UriListDataBus
    @Inject
    PrivateDataBus<List<Uri>> uriPrivateDataBus;

    private PrivateDataBusToken token =
            PrivateDataBusTokenFactory.makeToken(Activity1.class, Activity2.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);
        Button gotoActivity2Button = findViewById(R.id.goto_activity_2_button);

        /**
         * Solution 1 : basic bus
         */
        integerBus.setData(1234);
        stringBus.setData("coucou !");
        uriListBus.setData(Arrays.asList(Uri.parse("abc"), Uri.parse("1234"), Uri.parse("cool")));

        /**
         * Solution 2 : private bus
         */
        uriPrivateDataBus.setData(token, Arrays.asList(Uri.parse("so"), Uri.parse("private")));

        gotoActivity2Button.setOnClickListener(v -> {
            Intent intent = new Intent(this, Activity2.class);
            startActivity(intent);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}