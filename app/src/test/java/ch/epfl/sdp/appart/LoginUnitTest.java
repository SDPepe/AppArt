package ch.epfl.sdp.appart;

import org.hamcrest.MatcherAssert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import ch.epfl.sdp.appart.login.FirebaseLoginService;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.user.User;

import static org.hamcrest.Matchers.*;

public class LoginUnitTest {

    @Test
    public void CreateUserTest() {
        LoginService login = FirebaseLoginService.buildLoginService();
    }
}
