package ch.epfl.sdp.appart.utils;

import android.util.Log;
import android.view.View;

import androidx.annotation.StringRes;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import ch.epfl.sdp.appart.R;

public class UIUtils {

    public static void makeSnakeAndLogOnFail(View view, @StringRes int id, Throwable e) {
        Log.d("LOGIN", e.getMessage());
        Snackbar.make(view, id, BaseTransientBottomBar.LENGTH_SHORT).show();
    }
}
