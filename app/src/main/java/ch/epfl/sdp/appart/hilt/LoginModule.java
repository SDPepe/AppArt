package ch.epfl.sdp.appart.hilt;

import javax.inject.Singleton;

import ch.epfl.sdp.appart.login.FirebaseLoginService;
import ch.epfl.sdp.appart.login.LoginService;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class LoginModule {

    @Singleton
    @Binds
    public abstract LoginService bindCloudLoginService(FirebaseLoginService firebaseLoginService);

}
