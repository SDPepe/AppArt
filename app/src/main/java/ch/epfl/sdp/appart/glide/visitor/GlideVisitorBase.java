package ch.epfl.sdp.appart.glide.visitor;

import android.content.Context;

public abstract class GlideVisitorBase {
    protected final Context context;

    GlideVisitorBase(Context context) {
        if (context == null) throw new IllegalArgumentException("context cannot be null");
        this.context = context;
    }
}
