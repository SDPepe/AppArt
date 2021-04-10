package ch.epfl.sdp.appart.scrolling;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Enum to specify whether a price of an apartment in an ad is for a day, a week or a month
 */
public enum PricePeriod {

    DAY("day"), WEEK("week"), MONTH("month");

    public static final List<PricePeriod> ALL = Collections
            .unmodifiableList(Arrays.asList(values()));

    public final static int COUNT = 3;
    private final String name;

    PricePeriod(String name) {
        this.name = name;
    }

    @NotNull
    public String toString() {
        return name;
    }

}
