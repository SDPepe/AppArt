package ch.epfl.sdp.appart.datapass;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import ch.epfl.sdp.appart.R;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class Activity2 extends AppCompatActivity {

    @Inject
    DataTransfer transferService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        DataContainer<List<Uri>> container = transferService.getRegisteredContainer(Activity1.class);
        List<Uri> uris = Arrays.asList(Uri.parse("abc"), Uri.parse("1234"), Uri.parse("coucou"));
        container.setData(uris);

    }
}