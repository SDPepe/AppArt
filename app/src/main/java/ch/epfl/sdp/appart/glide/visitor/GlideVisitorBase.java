package ch.epfl.sdp.appart.glide.visitor;

import android.content.Context;

/**
 * Base class used to enforce the use of the caller context needed for Glide to work
 */
public abstract class GlideVisitorBase {
    protected final Context context; //needed for Glide

    GlideVisitorBase(Context context) {
        if (context == null) throw new IllegalArgumentException("context cannot be null");
        this.context = context;
    }
}
