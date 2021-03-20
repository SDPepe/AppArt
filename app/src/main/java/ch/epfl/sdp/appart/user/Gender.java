package ch.epfl.sdp.appart.user;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Gender {
    NOT_SELECTED, FEMALE, MALE, OTHER;

    public static final List<Gender> ALL = Collections
            .unmodifiableList(Arrays.asList(values()));

    // attribute COUNT tells us the number of enum values
    public final static int COUNT = 4;
}
