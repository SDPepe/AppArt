package ch.epfl.sdp.appart.databus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.hilt.annotations.IntegerDataBus;
import ch.epfl.sdp.appart.hilt.annotations.StringDataBus;
import ch.epfl.sdp.appart.hilt.annotations.StringMutableDataBus;
import ch.epfl.sdp.appart.hilt.annotations.UriListDataBus;
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

    @StringMutableDataBus
    @Inject
    ActivityMutableDataBus<String> stringMutableDataBus;

    private MutableLiveData<String> stringMutableData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);
        Button gotoActivity2Button = findViewById(R.id.goto_activity_2_button);

        //we fill the busses with non-trivial data
        integerBus.setData(1234);
        stringBus.setData("coucou !");
        uriListBus.setData(Arrays.asList(Uri.parse("abc"), Uri.parse("1234"), Uri.parse("cool")));

        //setting ownership of the bus by Activity1
        stringMutableDataBus.bind(Activity1.class, stringMutableData);

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