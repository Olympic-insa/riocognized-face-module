package fr.olympicinsa.riocognized.facedetector.detection;

import static com.googlecode.javacv.cpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;
import static com.googlecode.javacv.cpp.opencv_objdetect.CV_HAAR_DO_ROUGH_SEARCH;
import static com.googlecode.javacv.cpp.opencv_objdetect.CV_HAAR_FIND_BIGGEST_OBJECT;
import static com.googlecode.javacv.cpp.opencv_objdetect.CV_HAAR_SCALE_IMAGE;
import fr.olympicinsa.riocognized.facedetector.tools.OpenCV;
import fr.olympicinsa.riocognized.facedetector.tools.ImageConvertor;
import fr.olympicinsa.riocognized.facedetector.tools.Treatment;
import static fr.olympicinsa.riocognized.facedetector.tools.Treatment.resize;
import static fr.olympicinsa.riocognized.facedetector.tools.Treatment.showResult;
import java.awt.image.BufferedImage;
import static java.lang.System.exit;
import java.util.Date;
import org.apache.log4j.Logger;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class FaceDetector {

    public static Logger log = Logger.getLogger(FaceDetector.class);

    public final static String DEBUG_OUTPUT_FILE = "detected.jpg";
    public final static String CASCADE_FRONTAL_ALT = "haarcascade_frontalface_alt.xml";
    public final static String CASCADE_FRONTAL_DEFAULT = "haarcascade_frontalface_default.xml";
    public final static String CASCADE_PROFILEFACE = "haarcascade_profileface.xml";
    public final static String CASCADE_FRONTAL_ALT2 = "haarcascade_frontalface_alt2.xml";
    public final static String CASCADE_EYES = "haarcascade_eye.xml";
    public final static int MAX_WIDTH = 500;
    public Size minSize = new Size(30, 30);
    public Size maxSize = new Size(500, 500);
    private int facesDetected;
    private CascadeClassifier frontalDetector;
    private CascadeClassifier profileDetector;
    private CascadeClassifier eyesDetector;
    private static OpenCV openCV;

    /**
     * Face Detector constructor
     *
     */
    public FaceDetector() {
        openCV = OpenCV.getInstance();
        //Load Haar Cascade Classifier
        try {
            log.info("FaceDetector use : " + CASCADE_FRONTAL_DEFAULT);
            frontalDetector = new CascadeClassifier(openCV.getLibraryPath() + CASCADE_FRONTAL_DEFAULT);
            profileDetector = new CascadeClassifier(openCV.getLibraryPath() + CASCADE_PROFILEFACE);
            eyesDetector = new CascadeClassifier(openCV.getLibraryPath() + CASCADE_EYES);
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
        Mat imageR = resize(image, MAX_WIDTH);
        MatOfRect faceDetections = new MatOfRect();
        Rect faceRect = new Rect();
        //hasEyes(imageR);
        frontalDetector.detectMultiScale(imageR, faceDetections, 1.1, 3, 0
            //| CV_HAAR_FIND_BIGGEST_OBJECT
            //|CV_HAAR_DO_ROUGH_SEARCH
            | CV_HAAR_DO_CANNY_PRUNING
            | CV_HAAR_SCALE_IMAGE, minSize, maxSize);
        int detected = faceDetections.toArray().length;
        if (detected == 0) {
            log.info("FaceDetector try Profile");
            log.info("FaceDetector use : " + CASCADE_PROFILEFACE);
            eyesDetector.detectMultiScale(imageR, faceDetections, 1.1, 2, 0, new Size(5, 5), new Size(20, 20));
            detected = faceDetections.toArray().length;
        }
        this.facesDetected = detected;
        if (detected > 0) {
            for (Rect rect : faceDetections.toArray()) {
                Core.rectangle(imageR, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0, 255, 0));
            }
            //showResult(imageR);
            Highgui.imwrite(DEBUG_OUTPUT_FILE, imageR);
            if (detected > 1) {
                log.info("Select face using eyes");
                for (Rect rect : faceDetections.toArray()) {
                    if (faceHasEyes(rect, imageR)) {
                        faceRect = rect;
                    }
                }
            }
            faceRect = faceDetections.toArray()[0];
            Rect rectCrop = new Rect(faceRect.x, faceRect.y, faceRect.width, faceRect.height);
            Mat cropImage = new Mat(imageR, rectCrop);
            Mat grayScaled = Treatment.beforeDetection(cropImage);
            return grayScaled;
        } else {
            return null;
        }
    }
    
    /**
     * Detected eyes present in Mat image in a specified zone
     *
     * @param frame Mat of type CV_8UC3 or CV_8UC1
     * @param facesArray Rect where to find eyes in the frame
     * @return boolean true if detected
     */
    public boolean faceHasEyes(Rect facesArray, Mat frame) {
        Point center = new Point(facesArray.x + facesArray.width * 0.5, facesArray.y + facesArray.height * 0.5);
        //Core.ellipse(frame, center, new Size(facesArray.width * 0.5, facesArray.height * 0.5), 0, 0, 360, new Scalar(255, 0, 255), 4, 8, 0);

        Mat faceROI = frame.submat(facesArray);
        MatOfRect eyes = new MatOfRect();

        eyesDetector.detectMultiScale(faceROI, eyes, 1.1, 1, 0, new Size(5, 5), new Size(20, 20));
        log.info("EyesDetector detect :" + eyes.toArray().length);
        Rect[] eyesArray = eyes.toArray();
        for (int j = 0; j < eyesArray.length; j++) {
            Point center1 = new Point(facesArray.x + eyesArray[j].x + eyesArray[j].width * 0.5, facesArray.y + eyesArray[j].y + eyesArray[j].height * 0.5);
            int radius = (int) Math.round((eyesArray[j].width + eyesArray[j].height) * 0.25);
            Core.circle(frame, center1, radius, new Scalar(255, 0, 0), 4, 2, 0);
            Highgui.imwrite(DEBUG_OUTPUT_FILE + eyesArray[j].x + ".jpg", frame);
        }
        if (eyesArray.length > 0) {
            log.info("EyesDetected = true");
            return true;
        }
        log.info("EyesDetected = false");
        return false;
    }
    
    /**
     * Detected eyes present in Mat image
     *
     * @param frame Mat of type CV_8UC3 or CV_8UC1
     * @return boolean true if detected
     */
    public boolean hasEyes(Mat frame) {
        Mat faceROI = frame;
        MatOfRect eyes = new MatOfRect();

        eyesDetector.detectMultiScale(faceROI, eyes, 1.1, 1, 0, new Size(5, 5), new Size(20, 20));
        log.info("EyesDetector detect :" + eyes.toArray().length);
        Rect[] eyesArray = eyes.toArray();
        for (int j = 0; j < eyesArray.length; j++) {
            Point center1 = new Point(eyesArray[j].x + eyesArray[j].width * 0.5,  eyesArray[j].y + eyesArray[j].height * 0.5);
            int radius = (int) Math.round((eyesArray[j].width + eyesArray[j].height) * 0.25);
            Core.circle(frame, center1, radius, new Scalar(255, 0, 0), 4, 2, 0);
        }
        Highgui.imwrite("EYES_" + DEBUG_OUTPUT_FILE + ".jpg", frame);
        if (eyesArray.length > 0) {
            log.info("Image EyesDetected = true");
            return true;
        }
        log.info("Image EyesDetected = false");
        return false;
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
