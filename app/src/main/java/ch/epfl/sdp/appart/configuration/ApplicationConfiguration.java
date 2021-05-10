package ch.epfl.sdp.appart.configuration;

import javax.inject.Inject;
import javax.inject.Singleton;

import ch.epfl.sdp.appart.MainActivity;

@Singleton
public class ApplicationConfiguration {

    //whether or not we are demoing
    private boolean demoMode;

    @Inject
    public ApplicationConfiguration() {}

    /**
     * MainActivity is here to enforce the fact that the app must be configure from the
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
     * @param
     * @param
     * @return
     */
    public Class<?> demoModeSelector(Class<?> normal, Class<?> demo) {
        if (demoMode) {
            return demo;
        } else {
            return normal;
        }
    }

}
