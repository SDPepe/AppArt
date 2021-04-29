package ch.epfl.sdp.appart.ad;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

public class ResizableImageView extends androidx.appcompat.widget.AppCompatImageView implements View.OnTouchListener {

    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;

    public ResizableImageView(Context context, AttributeSet attrs){
        super(context, attrs);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener(this));
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mScaleDetector.onTouchEvent(ev);
        return true;
    }

    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        ResizableImageView view;

        ScaleListener(ResizableImageView view){
            this.view = view;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(1.0f, Math.min(mScaleFactor, 5.0f));

            view.setScaleX(mScaleFactor);
            view.setScaleY(mScaleFactor);
            return true;
        }
    }


}
