package ch.epfl.sdp.appart.datapass;

import androidx.appcompat.app.AppCompatActivity;
import ch.epfl.sdp.appart.R;
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

    @Inject
    DataTransfer transferService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);
        Button gotoActivity2Button = findViewById(R.id.goto_activity_2_button);
        gotoActivity2Button.setOnClickListener(v -> {
            Intent intent = new Intent(this, Activity2.class);
            startActivity(intent);
        });

        transferService.registerContainerList(Activity1.class, Uri.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DataContainer<List<Uri>> container = transferService.getRegisteredContainer(Activity1.class);
        if (!container.isDirty()) {
            List<Uri> result = container.getData();
            Uri r = result.get(0);
        }

    }
}