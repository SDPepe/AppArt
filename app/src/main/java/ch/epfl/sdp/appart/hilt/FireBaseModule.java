package ch.epfl.sdp.appart.hilt;

import javax.inject.Singleton;

import ch.epfl.sdp.appart.Database;
import ch.epfl.sdp.appart.FirebaseDB;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ViewModelComponent;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class FireBaseModule {

    @Singleton
    @Binds
    public abstract Database bindMyService(FirebaseDB firebaseDBImpl);
}
