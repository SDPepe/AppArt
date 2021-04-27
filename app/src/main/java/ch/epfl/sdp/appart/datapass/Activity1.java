package ch.epfl.sdp.appart.datapass;

import androidx.appcompat.app.AppCompatActivity;
import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.hilt.annotations.IntegerDataTransferProvider;
import ch.epfl.sdp.appart.hilt.annotations.StringDataTransferProvider;
import ch.epfl.sdp.appart.hilt.annotations.UriListDataTransferProvider;
import dagger.hilt.android.AndroidEntryPoint;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.List;

import javax.inject.Inject;

@AndroidEntryPoint
public class Activity1 extends AppCompatActivity {

    //@Inject
    //DataTransfer transferService;

    @IntegerDataTransferProvider
    @Inject
    GenericTransfer<Integer> integerTransfer;

    @StringDataTransferProvider
    @Inject
    GenericTransfer<String> stringTransfer;

    @UriListDataTransferProvider
    @Inject
    GenericTransfer<List<Uri>> uriListTransfer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);
        Button gotoActivity2Button = findViewById(R.id.goto_activity_2_button);
        gotoActivity2Button.setOnClickListener(v -> {
            Intent intent = new Intent(this, Activity2.class);
            startActivity(intent);
        });

        integerTransfer.registerContainer(Activity1.class);
        stringTransfer.registerContainer(Activity1.class);

        //transferService.registerContainerList(Activity1.class, Uri.class);
    }

    @Override
    protected void onResume() {
        super.onResume();

        DataContainer<Integer> container = integerTransfer.getRegisteredContainer(Activity1.class);
        if (!container.isDirty()) {
            int a = container.getData();
            int b = 0;
            //List<Uri> result = container.getData();
            //Uri r = result.get(0);
        }

        DataContainer<String> container2 = stringTransfer.getRegisteredContainer(Activity1.class);
        if (!container2.isDirty()) {
            String hello = container2.getData();
            int a = 0;
            //List<Uri> result = container.getData();
            //Uri r = result.get(0);
        }

    }
}