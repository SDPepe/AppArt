package ch.epfl.sdp.appart.hilt;

import ch.epfl.sdp.appart.Database;
import ch.epfl.sdp.appart.FirebaseDB;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class DataBaseServiceHiltModule {
    @Binds
    public abstract Database bindMyService(FirebaseDB firebaseDBImpl);
}
