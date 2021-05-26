package ch.epfl.sdp.appart;

import static android.widget.Toast.makeText;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import ch.epfl.sdp.appart.ad.AdCreationViewModel;
import ch.epfl.sdp.appart.ad.PricePeriod;
import ch.epfl.sdp.appart.filter.FilterViewModel;
import ch.epfl.sdp.appart.scrolling.ScrollingViewModel;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.utils.ActivityCommunicationLayout;
import com.google.android.material.snackbar.Snackbar;
import dagger.hilt.android.AndroidEntryPoint;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;

@AndroidEntryPoint
public class FilterActivity extends AppCompatActivity {


  FilterViewModel mViewModel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_filter);
    mViewModel = new ViewModelProvider(this).get(FilterViewModel.class);

    Button confirmBtn = findViewById(R.id.confirm_Filter_button);
    confirmBtn.setOnClickListener(v -> applyFilter());

    Button resetBtn = findViewById(R.id.clear_Filter_button);
    resetBtn.setOnClickListener(v -> clearFilter());

  }
  private boolean setValues() {
    String min = getContentOfEditText(R.id.value_min_price__Filter_editText);
    String max = getContentOfEditText(R.id.value_max_price_Filter_editText);
    if(!min.isEmpty() && !max.isEmpty()){
      mViewModel.setMin(Integer.parseInt(min));
      mViewModel.setMax(Integer.parseInt(max));
    }

    String city = getContentOfEditText(R.id.location_Filter_editText);
    String range = getContentOfEditText(R.id.value_range_Filter_editText);
    if(!city.isEmpty() && !range.isEmpty()){
      mViewModel.setCity(city);
      mViewModel.setRange(Float.parseFloat(range));
      return true;
    }
    return false;
  }
  private String getContentOfEditText(int id) {
    return ((EditText) findViewById(id)).getText().toString();
  }

  private void applyFilter(){
    if(setValues()) {
      CompletableFuture<Boolean> result = mViewModel.confirmFilter();
      result.thenAccept(completed -> {
        if (completed) {
          Intent resultIntent = new Intent();
          resultIntent
              .putExtra(ActivityCommunicationLayout.PROVIDING_SIZE, mViewModel.getCards().size());
          int count = 0;
          for (Card i : mViewModel.getCards()) {
            resultIntent
                .putExtra(ActivityCommunicationLayout.PROVIDING_CARD_ID + count, i.getAdId());
            count++;
          }
          setResult(RESULT_OK, resultIntent);
          finish();
        } else {
          makeText(this, "Error in the query, try again!", Toast.LENGTH_SHORT).show();
        }
      });
    } else {
      makeText(this, "Set price and location filter!", Toast.LENGTH_SHORT).show();
    }
  }
  private void clearFilter(){
    Intent resultIntent = new Intent();
    resultIntent.putExtra(ActivityCommunicationLayout.PROVIDING_SIZE, 0);
    setResult(RESULT_OK, resultIntent);
    finish();
  }
}
