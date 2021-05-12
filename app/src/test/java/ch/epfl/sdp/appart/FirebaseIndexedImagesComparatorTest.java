package ch.epfl.sdp.appart;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import ch.epfl.sdp.appart.utils.FirebaseIndexedImagesComparator;

public class FirebaseIndexedImagesComparatorTest {

    private final FirebaseIndexedImagesComparator comparator = new FirebaseIndexedImagesComparator();
    private final List<String> expected = Arrays.asList("cOvenTio_al0.jpg", "cOvenTio_al1.jpg", "cOvenTio_al2.jpg", "cOvenTio_al3.jpg");

    @Test
    public void sortingIndexedImagesWorks() {
        List<String> test = new ArrayList<>();
        test.addAll(expected);
        Collections.shuffle(test);
        Collections.sort(test, comparator);
        assertEquals("images must be sorted", expected, test);
    }

    @Test
    public void comparingNonConventionalIndexedImagesThrowsIllegalState() {
        assertThrows(IllegalStateException.class, () -> comparator.compare("dummy1", "dummy2"));
    }

}
