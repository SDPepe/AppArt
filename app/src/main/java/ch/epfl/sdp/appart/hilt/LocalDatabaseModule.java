package ch.epfl.sdp.appart.hilt;

import android.content.Context;

import javax.inject.Singleton;

import ch.epfl.sdp.appart.database.local.LocalDatabase;
import ch.epfl.sdp.appart.login.FirebaseLoginService;
import ch.epfl.sdp.appart.login.LoginService;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class LocalDatabaseModule {

    @Singleton
    @Binds
    public abstract LocalDatabase bindLocalDatabaseService(LocalDatabase localDatabase);

    @Singleton
    @Provides
    public static String provideAppFolder(@ApplicationContext Context context) {
        return context.getFilesDir().getPath();
    }

}
