package ch.epfl.sdp.appart.utils;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class allows to compare the indexed images name retrieved from firebase.
 * For example :
 * Panorama0.jpg < Panorama1.jpg
 * Picture2.jpg < Picture3.jpg
 */
public class FirebaseIndexedImagesComparator implements Comparator<String> {

    //this pattern will match a conventional name of images with its index followed by
    //a dot and its file extension
    private final Pattern pattern = Pattern.compile("^[a-zA-Z]+\\d+\\.[a-zA-Z]+$");
    //this pattern aims to extract the index inside the string
    private final Pattern numberPattern = Pattern.compile("\\d+");

    @Override
    public int compare(String o1, String o2) {
        return Integer.compare(getIndex(o1), getIndex(o2));
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }

    /**
     * This methods will check that the string matches the conventional
     * format of firebase images names. Then it will isolate the index inside
     * of it.
     * @param s String from which the index will be extracted
     * @throws IllegalStateException thrown if it fails to extract the index.
     * @return int the index
     */
    private int getIndex(String s) {
        checkFirebaseConventionFormat(s);
        Matcher matcher = numberPattern.matcher(s);
        if (!matcher.find()) {
            throw new IllegalStateException("failed to match over normally well formed string");
        }
        String index = matcher.group();
        return Integer.parseInt(index);
    }

    /**
     * This method aims to check if the string has the conventional format.
     * @param s the string to be checked
     * @throws IllegalStateException thrown if the string does not match the format.
     */
    private void checkFirebaseConventionFormat(String s) {
        if (!pattern.matcher(s).matches()) {
            throw new IllegalStateException("malformed image path string : " + s);
        }
    }

}
