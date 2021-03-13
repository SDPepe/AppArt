package ch.epfl.sdp.appart.hilt;

import android.app.Application;

import javax.inject.Inject;

import ch.epfl.sdp.appart.Database;
import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class HiltApplication extends Application  {}
