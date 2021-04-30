package ch.epfl.sdp.appart.ad;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

/**
 * Custom ImageView that detects scaling gestures.
 * <p>
 * Pinch gestures can be used to modify the zoom level of the image in the ImageView.
 */
public class ResizableImageView extends androidx.appcompat.widget.AppCompatImageView implements View.OnTouchListener {

    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;

    public ResizableImageView(Context context, AttributeSet attrs){
        super(context, attrs);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener(this));
    }

    /**
     *  On touch, let the gesture detector deal with it.
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return super.onTouchEvent(event);
    }

    /**
     * When a touch event is detected, check with gesture detector if it is a pinch gesture.
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mScaleDetector.onTouchEvent(ev);
        return true;
    }

    /**
     * Custom listener to detect scaling gestures.
     */
    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        ResizableImageView view;

        ScaleListener(ResizableImageView view){
            this.view = view;
        }

        /**
         *  If a scale gesture is detected, modify the scaling factor of the image. The scaling
         *  factor is clipped between 1 and 5 to avoid zoom levels that are too extreme.
         */
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
