package ch.epfl.sdp.appart.user;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * The functionality of this class is to store a database of
 * accepted and trusted university email providers.
 */
public class UniversityEmailDatabase {

    /* add here new trusted university email providers */
    private static final String[] universityEmailDatabase = {"epfl.ch", "unil.ch", "unige.ch"};

    private static final Set<String> universityEmailDatabaseSet = new HashSet<String>(Arrays.asList(universityEmailDatabase));

    /**
     * @param email the user's email
     * @return true if the given email provider is in the trusted university providers accpeted by the application, false otherwise
     */
    public static boolean has(String email) {
        if (email == null) {
            throw new IllegalArgumentException("ERROR - email parameter was null");
        }
        String[] split = email.split("@");
        String emailProvider = split[split.length-1];
        return universityEmailDatabaseSet.contains(emailProvider);
    }

}
