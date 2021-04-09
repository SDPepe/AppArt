package ch.epfl.sdp.appart.user;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;


/**
 * The functionality of this class is to store a database of
 * accepted and trusted university email providers. The class
 * can check the syntax of an email for its validity and then
 * check whether the email provider is a Swiss university.
 */
public class UniversityEmailChecker {

    /* add here new trusted university email providers */
    private static final String[] universityEmailDatabase = {"epfl.ch", "unil.ch", "unige.ch", "unibas.ch", "unibe.ch"};

    private static final Set<String> universityEmailDatabaseSet = new HashSet<>(Arrays.asList(universityEmailDatabase));

    /**
     * checker for university email
     *
     * @param email the users email
     * @return true if the given email provider is in the trusted university providers accepted by AppArt, false otherwise
     */
    public static boolean has(String email) {
        if (email == null) {
            throw new IllegalArgumentException("ERROR - email parameter was null");
        }

        /* checks email syntax */
        if (!UniversityEmailChecker.emailSyntaxIsValid(email)) {
            return false;
        }

        String[] split = email.split("@");
        String emailProvider = split[split.length - 1];
        return universityEmailDatabaseSet.contains(emailProvider);
    }


    /**
     * email syntax checker
     *
     * @param email the users email
     * @return true if the syntax of the email is correct, false otherwise
     */
    public static boolean emailSyntaxIsValid(String email) {
        if (email == null) {
            throw new IllegalArgumentException("ERROR - email parameter was null");
        }

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +  //part before @
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        return pat.matcher(email).matches();
    }

}
