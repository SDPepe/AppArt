package ch.epfl.sdp.appart.scrolling;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum PricePeriod {

    DAY("day"), WEEK("week"), MONTH("month");

    public static final List<PricePeriod> ALL = Collections
            .unmodifiableList(Arrays.asList(values()));
    // attribute COUNT tells us the number of enum values
    public final static int COUNT = 3;
    private String name;

    PricePeriod(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

}
