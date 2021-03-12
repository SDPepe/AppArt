package ch.epfl.sdp.appart;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.*;

public class LoginTest {

    @Test
    public void loginTest() throws InterruptedException {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        CountDownLatch createSignal = new CountDownLatch(1);
        auth.useEmulator("10.0.2.2", 9099);
        auth.createUserWithEmailAndPassword("antoine.de.gendt@gmail.com", "Password1234").addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                Log.d("LOGIN", "Success");
            }
            else {
                Log.d("LOGIN", "Failure");
                String message = task.getException().getMessage();
                Log.d("LOGIN", message);
            }
            createSignal.countDown();
        });
        createSignal.await();
        FirebaseUser user = auth.getCurrentUser();
        String mail = user.getEmail();
        assertThat(user.getEmail(), is("blabla"));

        CountDownLatch deleteSignal = new CountDownLatch(1);
        user.delete().addOnCompleteListener(task -> {
            deleteSignal.countDown();
        });
        deleteSignal.await();

        user = auth.getCurrentUser();
        assertEquals(null, user);


    }
}
