package ch.epfl.sdp.appart.login;

import ch.epfl.sdp.appart.AppArtApplication;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;

@Module
@InstallIn(AppArtApplication.class)
public abstract class LoginServiceModule {

    @Binds
    public abstract LoginService bindLoginService(FirebaseLoginService loginServiceImpl);
}
