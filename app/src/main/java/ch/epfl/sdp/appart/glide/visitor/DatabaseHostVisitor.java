package ch.epfl.sdp.appart.glide.visitor;

/**
 * Accepts the different visitors
 */
public interface DatabaseHostVisitor {
    /**
     * Inject the GlideLoaderVisitor into the instance.
     *
     * @param visitor the GlideLoader we want to inject
     */
    void accept(GlideLoaderVisitor visitor);

    void accept(GlideBitmapLoaderVisitor visitor);

    void accept(GlideLoaderListenerVisitor visitor);
}
