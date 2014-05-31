package fr.olympicinsa.riocognized.facedetector.detection;

import fr.olympicinsa.riocognized.facedetector.tools.ImageConvertor;
import fr.olympicinsa.riocognized.facedetector.tools.OpenCV;
import fr.olympicinsa.riocognized.facedetector.tools.Treatment;
import static fr.olympicinsa.riocognized.facedetector.tools.Treatment.resize;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Rect;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_core.Size;
import static org.bytedeco.javacpp.opencv_core.rectangle;
import static org.bytedeco.javacpp.hel
import static org.bytedeco.javacpp.opencv_highgui.imread;
import static org.bytedeco.javacpp.opencv_highgui.imwrite;
import static org.bytedeco.javacpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;
import org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier;

public class FaceDetector {

    public static Logger log = Logger.getLogger(FaceDetector.class);

    public final static String DEBUG_OUTPUT_FILE = "detected";
    public final static String CASCADE_FRONTAL_ALT = "haarcascade_frontalface_alt.xml";
    public final static String CASCADE_FRONTAL_DEFAULT = "haarcascade_frontalface_default.xml";
    public final static String CASCADE_PROFILEFACE = "haarcascade_profileface.xml";
    public final static String CASCADE_FRONTAL_ALT2 = "haarcascade_frontalface_alt2.xml";
    public final static String CASCADE_EYES = "haarcascade_eye.xml";
    public final static int MAX_WIDTH = 500;
    public Size minSize = new Size(40, 40);
    public Size maxSize = new Size(400, 400);
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
            frontalDetector = new CascadeClassifier(openCV.getLibraryPath() + CASCADE_FRONTAL_ALT);
            profileDetector = new CascadeClassifier(openCV.getLibraryPath() + CASCADE_PROFILEFACE);
            eyesDetector = new CascadeClassifier(openCV.getLibraryPath() + CASCADE_EYES);
        } catch (Exception e) {
            log.error("Can't create FaceDetector");
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
        Mat image = imread(imageURL);

        MatOfRect faceDetections = new MatOfRect();
        frontalDetector.detectMultiScale(image, faceDetections, 1.05, 3, 0, minSize, maxSize);

        int detected = faceDetections.toArray().length;

        //Frame recognized athlete faces
        for (Rect rect : faceDetections.toArray()) {
            rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                new Scalar(0, 255));
        }

        //Write new file
        imwrite(output, image);

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
        Mat imageR = resize(image, MAX_WIDTH);
        MatOfRect faceDetections = new MatOfRect();
        log.info("FaceDetector use : " + CASCADE_FRONTAL_ALT);
        frontalDetector.detectMultiScale(imageR, faceDetections, 1.1, 3, 0
            //| CV_HAAR_FIND_BIGGEST_OBJECT
            //|CV_HAAR_DO_ROUGH_SEARCH
            | CV_HAAR_DO_CANNY_PRUNING //| CV_HAAR_SCALE_IMAGE
            , minSize, maxSize);
        int detected = faceDetections.toArray().length;
        if (detected == 0) {
            log.info("No face found");
            log.info("FaceDetector try with another filter");
            log.info("FaceDetector use : " + CASCADE_FRONTAL_DEFAULT);
            frontalDetector.load(openCV.getLibraryPath() + CASCADE_FRONTAL_DEFAULT);
            frontalDetector.detectMultiScale(imageR, faceDetections, 1.1, 3, 0
                //| CV_HAAR_FIND_BIGGEST_OBJECT
                //|CV_HAAR_DO_ROUGH_SEARCH
                | CV_HAAR_DO_CANNY_PRUNING //| CV_HAAR_SCALE_IMAGE
                , minSize, maxSize);
            //eyesDetector.detectMultiScale(imageR, faceDetections, 1.1, 2, 0, new Size(5, 5), new Size(20, 20));
            detected = faceDetections.toArray().length;
        }
        this.facesDetected = detected;
        if (detected > 0) {
            for (Rect rect : faceDetections.toArray()) {
                rectangle(imageR, new Point(rect.x(), rect.y()), new Point(rect.x() + rect.width(), rect.y() + rect.height()),
                    new Scalar((double)0, (double)255));
            }
            //showResult(imageR);
            try {
                String debug = File.createTempFile(DEBUG_OUTPUT_FILE, ".jpg").getAbsolutePath();
                imwrite(debug, imageR);
                log.info("Faces detected writed to: " + debug);
            } catch (IOException e) {
                log.error("Can't write debug face detected image");
            }
        }

        //Write new file
        imwrite(output, imageR);

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
            rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                new Scalar(0, 255));
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
            Rect rectCrop = new Rect(rect.x(), rect.y(), rect.width(), rect.height());
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
        Rect faceRect = null;
        //hasEyes(imageR);
        log.info("FaceDetector use : " + CASCADE_FRONTAL_ALT);
        frontalDetector.detectMultiScale(imageR, faceDetections, 1.1, 3, 0
            //| CV_HAAR_FIND_BIGGEST_OBJECT
            //|CV_HAAR_DO_ROUGH_SEARCH
            | CV_HAAR_DO_CANNY_PRUNING //| CV_HAAR_SCALE_IMAGE
            , minSize, maxSize);
        int detected = faceDetections.toArray().length;
        if (detected == 0) {
            log.info("No face found");
            log.info("FaceDetector try with another filter");
            log.info("FaceDetector use : " + CASCADE_FRONTAL_DEFAULT);
            frontalDetector.load(openCV.getLibraryPath() + CASCADE_FRONTAL_DEFAULT);
            frontalDetector.detectMultiScale(imageR, faceDetections, 1.1, 3, 0
                //| CV_HAAR_FIND_BIGGEST_OBJECT
                //|CV_HAAR_DO_ROUGH_SEARCH
                | CV_HAAR_DO_CANNY_PRUNING //| CV_HAAR_SCALE_IMAGE
                , minSize, maxSize);
            //eyesDetector.detectMultiScale(imageR, faceDetections, 1.1, 2, 0, new Size(5, 5), new Size(20, 20));
            detected = faceDetections.toArray().length;
        }
        this.facesDetected = detected;
        if (detected > 0) {
            for (Rect rect : faceDetections.toArray()) {
                rectangle(imageR, new Point(rect.x(), rect.y()), new Point(rect.x() + rect.width(), rect.y() + rect.height()),
                    new Scalar(0, 255));
            }
            
           /*
            * For debugging purpose. Write temp file with all detected face in image
            */
            //showResult(imageR);
            try {
                String debug = File.createTempFile(DEBUG_OUTPUT_FILE, ".jpg").getAbsolutePath();
                imwrite(debug, imageR);
                log.info("Faces detected writed to: " + debug);
            } catch (IOException e) {
                log.error("Can't write debug face detected image");
            }
            
            /**/
            
            if (detected > 1) {
                log.info("Select face using eyes");
                for (Rect rect : faceDetections.toArray()) {
                    if (faceHasEyes(rect, imageR)) {
                        faceRect = rect;
                    }
                }
                if (faceRect == null) {
                    faceRect = faceDetections.toArray()[0];
                }
            } else {
                faceRect = faceDetections.toArray()[0];
            }

            Rect rectCrop = new Rect(faceRect.x(), faceRect.y(), faceRect.width(), faceRect.height());
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
        Point center = new Point(facesArray.x + facesArray.width() * 0.5, facesArray.y() + facesArray.height * 0.5);
        Mat face = frame.clone();
        Mat faceROI = face.submat(facesArray);
        MatOfRect eyes = new MatOfRect();

        eyesDetector.detectMultiScale(faceROI, eyes, 1.1, 1, 0, new Size(5, 5), new Size(20, 20));
        log.info("EyesDetector detect :" + eyes.toArray().length);
        Rect[] eyesArray = eyes.toArray();
        for (int j = 0; j < eyesArray.length; j++) {
            Point center1 = new Point(facesArray.x() + eyesArray[j].x() + eyesArray[j].width() * 0.5, facesArray.y + eyesArray[j].y + eyesArray[j].height() * 0.5);
            int radius = (int) Math.round((eyesArray[j].width() + eyesArray[j].height()) * 0.25);
            circle(face, center1, radius, new Scalar(255, 0, 0), 4, 2, 0);
        }
        if (eyesArray.length > 0) {
            //Highgui.imwrite(DEBUG_OUTPUT_FILE + eyesArray[0].x + ".jpg", face);
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
            Point center1 = new Point(eyesArray[j].x + eyesArray[j].width * 0.5, eyesArray[j].y + eyesArray[j].height * 0.5);
            int radius = (int) Math.round((eyesArray[j].width + eyesArray[j].height) * 0.25);
            Core.circle(faceROI, center1, radius, new Scalar(255, 0, 0), 4, 2, 0);
        }
        //Highgui.imwrite("EYES_" + DEBUG_OUTPUT_FILE + ".jpg", faceROI);
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
