package ch.epfl.sdp.appart.hilt;

import javax.inject.Singleton;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.FirestoreDatabaseService;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class DatabaseModule {

    @Singleton
    @Binds
    public abstract DatabaseService bindMyService(FirestoreDatabaseService database);
}
