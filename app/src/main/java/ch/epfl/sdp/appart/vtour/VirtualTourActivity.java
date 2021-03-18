package ch.epfl.sdp.appart.vtour;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.sdp.appart.R;

public class VirtualTourActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vtour);
    }

    public void goBack(View view){
        finish();
    }

}
