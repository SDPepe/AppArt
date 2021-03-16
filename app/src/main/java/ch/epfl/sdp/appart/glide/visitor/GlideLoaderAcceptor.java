package ch.epfl.sdp.appart.glide.visitor;

/**
 * Accepts the different visitors
 */
public interface GlideLoaderAcceptor {
    /**
     * Inject the GlideLoaderVisitor into the instance.
     * @param visitor the GlideLoader we want to inject
     */
    void accept(GlideLoaderVisitor visitor);
}
