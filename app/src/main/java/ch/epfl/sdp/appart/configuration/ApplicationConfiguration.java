package ch.epfl.sdp.appart.configuration;

import javax.inject.Inject;
import javax.inject.Singleton;

import ch.epfl.sdp.appart.MainActivity;

@Singleton
public class ApplicationConfiguration {

    //whether or not we are demoing
    private static boolean demoMode;

    @Inject
    public ApplicationConfiguration() {}

    /**
     * MainActivity is here to enforce the fact that the app must be configure at the
     * main activity only.
     * @param mainActivity
     * @param demoMode
     */
    public void setDemoMode(MainActivity mainActivity, boolean demoMode) {
        this.demoMode = demoMode;
    }

    /**
     * Returns if we are demo-ing or not.
     * @return
     */
    public boolean isDemoMode() {
        return demoMode;
    }

    /**
     * Allows to select at runtime which class to launch between the normal and
     * the demo one.
     * @param normal
     * @param demo
     * @param <T>
     * @param <U>
     * @return
     */
    public static <T, U> Class<?> demoModeSelector(Class<T> normal, Class<U> demo) {
        if (demoMode) {
            return demo;
        } else {
            return normal;
        }
    }

}
