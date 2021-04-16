package ch.epfl.sdp.appart.user;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This is the gender enum for users information, since the gender
 * of a user has a small fixed number of possibilities it is clever
 * to create an enum instead of storing it as string.
 */
public enum Gender {
    NOT_SELECTED("Not selected"),
    FEMALE("Female"),
    MALE("Male"),
    OTHER("Other");

    /* gives the possibility to access enum values as a list */
    public static final List<Gender> ALL = Collections
            .unmodifiableList(Arrays.asList(values()));

    private final String name;

    Gender(String name) {
        this.name = name;
    }

    @NotNull
    public String toString() {
        return name;
    }
}
