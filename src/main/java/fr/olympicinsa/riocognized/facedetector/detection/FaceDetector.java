package fr.olympicinsa.riocognized.facedetector.detection;

import static com.googlecode.javacv.cpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;
import static com.googlecode.javacv.cpp.opencv_objdetect.CV_HAAR_DO_ROUGH_SEARCH;
import static com.googlecode.javacv.cpp.opencv_objdetect.CV_HAAR_FIND_BIGGEST_OBJECT;
import static com.googlecode.javacv.cpp.opencv_objdetect.CV_HAAR_SCALE_IMAGE;
import fr.olympicinsa.riocognized.facedetector.tools.OpenCV;
import fr.olympicinsa.riocognized.facedetector.tools.ImageConvertor;
import fr.olympicinsa.riocognized.facedetector.tools.Treatment;
import java.awt.image.BufferedImage;
import static java.lang.System.exit;
import org.apache.log4j.Logger;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.objdetect.CascadeClassifier;

public class FaceDetector {

    public static Logger log = Logger.getLogger(FaceDetector.class);

    public final static String CASCADE_FRONTAL_DEFAULT = "haarcascade_frontalface_alt.xml";
    public final static String CASCADE_PROFILEFACE = "haarcascade_profileface.xml";
    public final static String CASCADE_FRONTALEFACE = "haarcascade_frontalface_alt2.xml";
    public Size minSize = new Size(15,15);
    public Size maxSize = new Size(500,500);
    private int facesDetected;
    private CascadeClassifier frontalDetector;
    private CascadeClassifier profileDetector;
    private static OpenCV openCV;

    /**
     * Face Detector constructor
     *
     */
    public FaceDetector() {
        openCV = OpenCV.getInstance();
        //Load Haar Cascade Classifier
        try {
            log.info("FaceDetector use : " +  CASCADE_FRONTAL_DEFAULT);
            frontalDetector = new CascadeClassifier(openCV.getLibraryPath() + CASCADE_FRONTAL_DEFAULT);
            profileDetector = new CascadeClassifier(openCV.getLibraryPath() + CASCADE_PROFILEFACE);
        } catch (Exception e) {
            log.error("Can't create FaceDetector");
            exit(0);
        }
    }

    /**
     * Detected faces present in path URL image, and return number of deteced
     * faces.
     *
     * @param imageURL path to image
     * @param output String url to save image result
     * @return int of detected faces
     */
    public int detectFaces(String imageURL, String output) {

        //Read image
        Mat image = Highgui.imread(imageURL);

        MatOfRect faceDetections = new MatOfRect();
        frontalDetector.detectMultiScale(image, faceDetections, 1.05, 3, 0, minSize, maxSize);

        int detected = faceDetections.toArray().length;

        //Frame recognized athlete faces
        for (Rect rect : faceDetections.toArray()) {
            Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                new Scalar(0, 255, 0));
        }

        //Write new file
        Highgui.imwrite(output, image);

        return detected;
    }

    /**
     * Detected faces present in Mat image, and return number of deteced faces.
     *
     * @param image Mat of type CV_8UC3 or CV_8UC1
     * @param output String url to save image result
     * @return int of detected faces
     */
    public int detectFaces(Mat image, String output) {

        MatOfRect faceDetections = new MatOfRect();
        log.info("FaceDetector param : scale=1.05-neigh=3");
        frontalDetector.detectMultiScale(image, faceDetections, 1.05, 3, 0, minSize, maxSize);
        int detected = faceDetections.toArray().length;
        this.facesDetected = detected;

        //Frame recognized athlete faces
        for (Rect rect : faceDetections.toArray()) {
            Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                new Scalar(0, 255, 0));
        }

        //Write new file
        Highgui.imwrite(output, image);

        return detected;
    }

    /**
     * Detected faces present in Mat image, and return bufferedImage.
     *
     * @param image Mat of type CV_8UC3 or CV_8UC1
     * @return int of detected faces
     */
    public BufferedImage detectFacesToBufferedImage(Mat image) {

        MatOfRect faceDetections = new MatOfRect();
        frontalDetector.detectMultiScale(image, faceDetections, 1.05, 3, 0, minSize, maxSize);
        int detected = faceDetections.toArray().length;
        this.facesDetected = detected;

        //Frame recognized athlete faces
        for (Rect rect : faceDetections.toArray()) {
            Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                new Scalar(0, 255, 0));
        }

        //Write new bufferedImage
        return ImageConvertor.matToBufferedImage(image);
    }

    /**
     * Detected faces present in Mat image, and return crop image
     *
     * @param image Mat of type CV_8UC3 or CV_8UC1
     * @return BufferedImage of crop face
     */
    public BufferedImage cropFaceToBufferedImage(Mat image) {

        MatOfRect faceDetections = new MatOfRect();
        frontalDetector.detectMultiScale(image, faceDetections, 1.05, 3, 0, minSize, maxSize);
        int detected = faceDetections.toArray().length;
        this.facesDetected = detected;
        if (faceDetections.toArray().length > 0) {
            Rect rect = faceDetections.toArray()[0];
            Rect rectCrop = new Rect(rect.x, rect.y, rect.width, rect.height);
            Mat cropImage = new Mat(image, rectCrop);
            //Write new bufferedImage
            Mat grayScaled = Treatment.beforeDetection(cropImage);
            return ImageConvertor.matToBufferedImage(grayScaled);
        } else {
            return null;
        }
    }

    /**
     * Detected faces present in Mat image, and return Mat crop image
     *
     * @param image Mat of type CV_8UC3 or CV_8UC1
     * @return Mat of detected face
     */
    public Mat cropFaceToMat(Mat image) {

        MatOfRect faceDetections = new MatOfRect();
        frontalDetector.detectMultiScale(image, faceDetections, 1.05, 3, CV_HAAR_DO_CANNY_PRUNING
            |CV_HAAR_FIND_BIGGEST_OBJECT
            //|CV_HAAR_DO_ROUGH_SEARCH
            //|CV_HAAR_DO_CANNY_PRUNING
            |CV_HAAR_SCALE_IMAGE
            , minSize, maxSize);
        int detected = faceDetections.toArray().length;
        if (detected == 0){
            log.info("FaceDetector try Profile");
            log.info("FaceDetector use : " +  CASCADE_PROFILEFACE);
            frontalDetector.load(CASCADE_PROFILEFACE);// = profileDetector;
            frontalDetector.detectMultiScale(image, faceDetections, 1.05, 3, 0, minSize, maxSize);
            detected = faceDetections.toArray().length;
        }
        this.facesDetected = detected;
        if (detected > 0) {
            Rect rect = faceDetections.toArray()[0];
            Rect rectCrop = new Rect(rect.x, rect.y, rect.width, rect.height);
            Mat cropImage = new Mat(image, rectCrop);
            Mat grayScaled = Treatment.beforeDetection(cropImage);
            return grayScaled;
        } else {
            return null;
        }
    }

    /**
     * detectedFaces getter
     *
     * @return int of detected faces number.
     */
    public int getFacesDetected() {
        return this.facesDetected;
    }

}
