/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.olympicinsa.riocognized.facedetector.recognition;

import com.googlecode.javacv.cpp.opencv_contrib.FaceRecognizer;
import static com.googlecode.javacv.cpp.opencv_contrib.createEigenFaceRecognizer;
import static com.googlecode.javacv.cpp.opencv_contrib.createFisherFaceRecognizer;
import static com.googlecode.javacv.cpp.opencv_contrib.createLBPHFaceRecognizer;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_core.MatVector;
import static com.googlecode.javacv.cpp.opencv_core.cvFlip;
import static com.googlecode.javacv.cpp.opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import fr.olympicinsa.riocognized.facedetector.detection.FaceDetector;

import fr.olympicinsa.riocognized.facedetector.tools.OpenCV;
import fr.olympicinsa.riocognized.facedetector.db.FaceDBReader;
import fr.olympicinsa.riocognized.facedetector.tools.Treatment;
import org.apache.log4j.Logger;

/**
 *
 * @author alex
 */
public class RioRecognizer {

    public static Logger log = Logger.getLogger(RioRecognizer.class);
    private final double THREASHOLD = 100.0;
    private final int EIGEN_SIZE = 60;

    private final FaceDBReader faceDatabase;
    private final String savePath;
    private final FaceDetector faceDetector;
    private final OpenCV opencv;
    // To scall faces
    private final int F = 2;
    private int x = 100;
    private int y = 100;

    private MatVector imagesDB;
    private int[] athletes;
    private FaceRecognizer eigenRecognizer;

    private int[] athlete = new int[1];
    private double distance[] = new double[1];

    /**
     * Constructor for FaceRecognizer
     *
     * @param db FaceDBReader containing path and label of faces images
     * @param out String where FaceRecognizer is saved
     */
    public RioRecognizer(FaceDBReader db, String out) {
        this.opencv = OpenCV.getInstance();
        this.faceDetector = new FaceDetector();
        this.faceDatabase = db;
        this.savePath = out;
        //this.eigenRecognizer = createEigenFaceRecognizer(EIGEN_SIZE, THREASHOLD);
        log.info("Create Eigenfaces Recognizer");
        eigenRecognizer = createLBPHFaceRecognizer(2, 8, 8, 8, 200);
    }

    /**
     * Switch to an other algorithm
     */
    public void changeRecognizer(int nRec) {
        switch (nRec) {
            case 2:
                log.info("Create LBPH FaceRecognizer (1,8,8,8,100)");
                eigenRecognizer = createLBPHFaceRecognizer(1, 8, 8, 8, 100);
                break;
            case 3:
                log.info("Create Fischer Recognizer");
                eigenRecognizer = createFisherFaceRecognizer();
                break;
            case 0:
                log.info("Create EigenRecognizer");
                eigenRecognizer = createEigenFaceRecognizer();
                break;
            case 1:
                log.info("Create LBPH Recognizer (2, 8, 8, 8, 200)");
                this.eigenRecognizer = createLBPHFaceRecognizer(2, 8, 8, 8, 200);
                break;
        }
        train();

    }

    /**
     * Initialize FaceRecognizer loading images stored in csv file
     */
    public void init() {
        imagesDB = new MatVector(faceDatabase.getFaces().size() * 2);
        athletes = new int[faceDatabase.getFaces().size() * 2];

        int counter = 0;
        String label;

        IplImage img;
        IplImage grayImg;
        IplImage flipImg;

        for (String[] face : faceDatabase.getFaces()) {
            try {
                img = cvLoadImage(face[0], CV_LOAD_IMAGE_GRAYSCALE);
                flipImg = cvLoadImage(face[0], CV_LOAD_IMAGE_GRAYSCALE);
                label = face[1];
                log.debug("Read image(" + counter + ") in " + face[0]);
                grayImg = Treatment.beforePrediction(img, getX(), getY(), 0);
                flipImg = Treatment.beforePrediction(flipImg, getX(), getY(), 0);
                imagesDB.put(counter, grayImg);
                athletes[counter] = new Integer(label);
                counter++;
                cvFlip(grayImg, flipImg, 1);
                imagesDB.put(counter, flipImg);
                athletes[counter] = new Integer(label);
                counter++;
            } catch (Exception e) {
                log.error("Can't read image(" + counter + ") in " + face[0]);
            }
        }
        log.info(counter + " Faces readed");
    }

    /**
     * Train face recognizer calculating ACP
     *
     */
    public void train() {
        if (imagesDB.size() < 1) {
            init();
        }
        log.info("Train FaceRecognizer ...");
        getEigenRecognizer().train(imagesDB, athletes);
    }

    /**
     * Try to recognize a face, using configured database
     *
     * @param image IplImage croped face to recognized
     * @return int of recognized Athlete id
     */
    public int predictedLabel(IplImage image) {
        int result = 0;

        try {
            log.info("Test image is converting FittedGrey scale");
            IplImage greyPredictImage = Treatment.beforePrediction(image, getX(), getY(), 1);
            log.info("Try to predict ...");
            //result = eigenRecognizer.predict(image);
            getEigenRecognizer().predict(greyPredictImage, athlete, distance);

            if (athlete[0] != -1) {
                log.info("Ath:" + athlete[0]);
                log.info("Dist :" + distance[0]);
                result = athlete[0];
            } else {
                result = -1;
            }
        } catch (Exception e) {
            log.error("Can't detect any face");
            e.printStackTrace();
            return 0;
        }
        return result;
    }

    /**
     * Save Eigenfaces value and FaceRecognizer config to yml file
     */
    public void save() {
        getEigenRecognizer().save(savePath);
        log.info("Face Recognizer saved to" + savePath);
    }

    /**
     *
     * @param path String of path to saved FaceRecognizer
     */
    public void load(String path) {
        getEigenRecognizer().load(path);
        log.info("Face Recognizer loaded from " + path);
    }

    /**
     * Get Precision of detection
     *
     * @return double[] of precision
     */
    public double[] getPrecision() {
        if (distance != null) {
            return distance;
        } else {
            return null;
        }
    }

    /**
     * Get Result of detection
     *
     * @return int[] of recognized athletes
     */
    public int[] getResult() {
        if (athlete != null) {
            return athlete;
        } else {
            return null;
        }
    }

    /**
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * @return the eigenRecognizer
     */
    public FaceRecognizer getEigenRecognizer() {
        return eigenRecognizer;
    }
}
