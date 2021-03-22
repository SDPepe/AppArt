package ch.epfl.sdp.appart.hilt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;
import javax.inject.Singleton;

import ch.epfl.sdp.appart.BuildConfig;
import ch.epfl.sdp.appart.login.FirebaseLoginService;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.login.MockLoginService;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class LoginModule {

    /*
    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface EmulatorLoginService {}

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface CloudLoginService {}

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MockedLoginService {}



    @MockedLoginService
    @Singleton
    @Binds
    public abstract LoginService bindMockedLoginService(MockLoginService mockLoginService);
    */

    /*
    @Provides
    public static LoginService provideLoginService(
            // Potential dependencies of this type
    ) {
        return new FirebaseLoginService();
    }*/

    @Singleton
    @Binds
    public abstract LoginService bindCloudLoginService(FirebaseLoginService firebaseLoginService);

}
