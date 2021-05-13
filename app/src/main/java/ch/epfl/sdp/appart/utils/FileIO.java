package ch.epfl.sdp.appart.utils;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.function.Supplier;

/**
 * This is a util class for file operations.
 */
public class FileIO {

    //Prevent the construction of this class, as all the methods are static.
    private FileIO() {
    }

    /**
     * Reads a map object from the path. The Map object has String as keys
     * and object as values.
     *
     * @param path the path to the file that holds the map
     * @return the map resulting from file reading, or null if the operation
     * fails
     */
    public static Map<String, Object> readMapObject(String path) {
        FileInputStream fis;
        Map<String, Object> map;
        try {
            fis = new FileInputStream(path);
            ObjectInputStream ois = new ObjectInputStream(fis);
            //noinspection unchecked
            map = (Map<String, Object>) ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        return map;
    }

    /**
     * This method creates a directory pointed to by path, and performs
     * already exists in case the directory already exists.
     *
     * @param path          the path to the directory we want to create
     * @param alreadyExists function that will be performed in case the
     *                      directory already exists
     * @return a boolean that indicates if the operation succeeded or not
     */
    public static boolean createFolder(String path,
                                       Supplier<Boolean> alreadyExists) {
        File file = new File(path);
        if (!file.exists()) {
            return file.mkdirs();
        } else {
            //We can just remove the extra photos because FileOutputStream
            // overwrites the whole file
            return alreadyExists.get();
        }
    }

    /**
     * Writes a map object on disk to the file pointed to by path. The map
     * object it writes has String as keys and Object as values.
     *
     * @param path the path to the file in which we want to write the map.
     * @param map  the map object we want to write
     * @return a boolean that indicates if the operation succeeded or not.
     */
    public static boolean writeMapObject(String path,
                                         Map<String, Object> map) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(path);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(map);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * This performs the deletion of the directory that dir points to.
     *
     * @param dir the file representing the directory we want to delete
     */
    public static void deleteDir(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (final File file : files) {
                deleteDir(file);
            }
        }
        dir.delete();
    }

    /**
     * This saves a bitmap to a file.
     *
     * @param bitmap the bitmap we want to save
     * @param path   the path to the file in which we want to store the bitmap
     * @return a boolean that indicates if the operation succeeded or not.
     */
    public static boolean saveBitmap(Bitmap bitmap, String path) {
        File photo = new File(path);

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(photo);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
