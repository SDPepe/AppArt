package ch.epfl.sdp.appart.scrolling;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Enum to specify whether a price of an apartment in an ad is for a day, a week or a month
 */
public enum PricePeriod {

    DAY("day"), WEEK("week"), MONTH("month");

    private String name;

    PricePeriod(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

    public static final List<PricePeriod> ALL = Collections
            .unmodifiableList(Arrays.asList(values()));

    public final static int COUNT = 3;

}
