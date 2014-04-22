package fr.olympicinsa.riocognized.facedetector;

import org.opencv.core.Core;

public class OpenCV {

    private static String libraryPath;
    private static OpenCV INSTANCE = null;

    /**
     * Load OpenCV JNI Native Library
     */
    public static void loadLibrary() {
        try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            //System.load(libraryPath + "libopencv_java248.so");
        } catch (Exception e) {
            System.out.println("classPath=" + System.getProperty("java.library.path"));
        }
    }

    /**
     * OpenCV constructor
     *
     * @param libraryURL String url path to library
     *
     */
    private OpenCV(String libraryURL) {
        OpenCV.libraryPath = libraryURL;
        //Load OpenCV library
        try {
            loadLibrary();
        } catch (Exception e) {
            System.err.println("Can't initialize OpenCV");
        }
    }

    /**
     * OpenCV default constructor
     */
    private OpenCV() {
        OpenCV.libraryPath = "/opt/openCV/";
        //Load OpenCV library
        try {
            loadLibrary();
        } catch (Exception e) {
            System.err.println("Can't initialize OpenCV");
        }
    }

    /**
     * OpenCV default accessor
     *
     * @return OpenCV Instance
     */
    public static OpenCV getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new OpenCV();
        }
        return INSTANCE;
    }

    /**
     * OpenCV default accessor
     *
     * @return OpenCV Instance
     */
    public static OpenCV getInstance(String libURL) {
        if (INSTANCE == null) {
            INSTANCE = new OpenCV(libURL);
        }
        return INSTANCE;
    }

    /**
     * libraryPath getter
     *
     * @return String of library path
     */
    public String getLibraryPath() {
        return OpenCV.libraryPath;
    }
}
