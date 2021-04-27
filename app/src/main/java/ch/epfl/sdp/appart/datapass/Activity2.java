package ch.epfl.sdp.appart.datapass;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.hilt.annotations.IntegerDataTransferProvider;
import ch.epfl.sdp.appart.hilt.annotations.StringDataTransferProvider;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class Activity2 extends AppCompatActivity {

    //@Inject
    //DataTransfer transferService;

    @IntegerDataTransferProvider
    @Inject
    GenericTransfer<Integer> integerTransfer;

    @StringDataTransferProvider
    @Inject
    GenericTransfer<String> stringTransfer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        DataContainer<Integer> container = integerTransfer.getRegisteredContainer(Activity1.class);
        container.setData(1);

        DataContainer<String> container2 = stringTransfer.getRegisteredContainer(Activity1.class);
        container2.setData("coucou !");

        //DataContainer<List<Uri>> container = transferService.getRegisteredContainer(Activity1.class);
        //List<Uri> uris = Arrays.asList(Uri.parse("abc"), Uri.parse("1234"), Uri.parse("coucou"));
        //container.setData(uris);

    }
}