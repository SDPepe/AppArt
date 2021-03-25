package ch.epfl.sdp.appart.scrolling;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum PricePeriod {

    DAY, WEEK, MONTH;

    public static final List<PricePeriod> ALL = Collections
            .unmodifiableList(Arrays.asList(values()));

    // attribute COUNT tells us the number of enum values
    public final static int COUNT = 3;

}
