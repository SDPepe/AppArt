package ch.epfl.sdp.appart.virtualtour;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.vr.sdk.widgets.pano.VrPanoramaView;

import ch.epfl.sdp.appart.R;

public class VirtualTourActivity extends AppCompatActivity {

    private VrPanoramaView vrView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vtour);
        vrView = (VrPanoramaView) findViewById(R.id.vrPanoramaView);
        VrPanoramaView.Options options = new VrPanoramaView.Options();
        options.inputType = VrPanoramaView.Options.TYPE_MONO;
        Bitmap panoramaBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.panorama_test);
        vrView.loadImageFromBitmap(panoramaBitmap, options);
    }

    @Override
    protected void onPause() {
        vrView.pauseRendering();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        vrView.resumeRendering();
    }

    @Override
    protected void onDestroy() {
        vrView.shutdown();
        super.onDestroy();
    }

    public void goBack(View view){
        finish();
    }

}
