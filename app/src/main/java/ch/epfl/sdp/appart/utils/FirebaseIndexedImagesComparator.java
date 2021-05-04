package ch.epfl.sdp.appart.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FirebaseIndexedImagesComparator implements Comparator<String> {

    //this pattern will match a conventional name of images with its index followed by
    //a dot and its file extension
    private final Pattern pattern = Pattern.compile("^[a-zA-Z]+\\d+\\.[a-zA-Z]+$");

    @Override
    public int compare(String o1, String o2) {

        checkFirebaseConventionFormat(o1);
        checkFirebaseConventionFormat(o2);
        return Integer.compare(getIndex(o1), getIndex(o2));
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }


    private int getIndex(String s) {

        Matcher matcher = pattern.matcher(s);
        List<String> matches = new ArrayList<>();
        while (matcher.find()) {
            for (int i = 0; i < matcher.groupCount(); i++) {
                matches.add(matcher.group(i));
            }
        }

        String shouldBeTheIndex = matcher.group(2);
        int index = Integer.parseInt(shouldBeTheIndex);
        return index;

    }

    private void checkFirebaseConventionFormat(String s) {
        if (!pattern.matcher(s).matches()) {
            throw new IllegalStateException("malformed image path string : " + s);
        }
    }

}
