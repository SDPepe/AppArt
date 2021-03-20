package ch.epfl.sdp.appart.hilt;

import javax.inject.Singleton;

import ch.epfl.sdp.appart.database.Database;
import ch.epfl.sdp.appart.database.FirebaseDB;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class FireBaseModule {

    @Singleton
    @Binds
    public abstract Database bindMyService(FirebaseDB firebaseDBImpl);
}
