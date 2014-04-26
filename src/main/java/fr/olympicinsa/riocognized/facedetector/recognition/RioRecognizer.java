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
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_core.MatVector;
import static com.googlecode.javacv.cpp.opencv_core.cvConvertScale;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvSize;
import static com.googlecode.javacv.cpp.opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_INTER_AREA;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvEqualizeHist;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvResize;
import fr.olympicinsa.riocognized.facedetector.FaceDetector;

import fr.olympicinsa.riocognized.facedetector.OpenCV;
import fr.olympicinsa.riocognized.facedetector.csv.FaceDBReader;
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
    private int x = 60;
    private int y = 60;

    private MatVector imagesDB;
    private int[] athletes;
    private FaceRecognizer eigenRecognizer;

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
        log.info("Create LBPH Recognizer (2, 8, 8, 8, 200)");
        this.eigenRecognizer = createLBPHFaceRecognizer(2, 8, 8, 8, 200);
    }
    /**
     * Switch to an other algorithm
     */
    public void changeRecognizer(int nRec) {
        switch (nRec) {
            case 0:
                log.info("Create LBPH FaceRecognizer (1,8,8,8,100)");
                eigenRecognizer = createLBPHFaceRecognizer(1, 8, 8, 8, 100);
                break;
            case 1:
                log.info("Create Fischer Recognizer");
                ;
                eigenRecognizer = createFisherFaceRecognizer();
                break;
            case 2:
                log.info("Create EigenRecognizer");
                eigenRecognizer = createEigenFaceRecognizer();
                break;
            case 3:
                log.info("Create Eigen Recognizer (" + EIGEN_SIZE + " / " + THREASHOLD);
                this.eigenRecognizer = createEigenFaceRecognizer(EIGEN_SIZE, THREASHOLD);
                break;
            case 4:
                log.info("Create LBPH Recognizer (2, 8, 8, 8, 200)");
                this.eigenRecognizer = createLBPHFaceRecognizer(2, 8, 8, 8, 200);
        }
        train();

    }

    /**
     * Initialize FaceRecognizer loading images stored in csv file
     */
    public void init() {
        imagesDB = new MatVector(faceDatabase.getFaces().size());
        athletes = new int[faceDatabase.getFaces().size()];

        int counter = 0;
        String label;

        IplImage img;
        IplImage grayImg;

        for (String[] face : faceDatabase.getFaces()) {
            try {
                img = cvLoadImage(face[0], CV_LOAD_IMAGE_GRAYSCALE);
                label = face[1];
                log.debug("Read image(" + counter + ") in " + face[0]);
                grayImg = toFittedGray(img, x, y);
                imagesDB.put(counter, grayImg);
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
        eigenRecognizer.train(imagesDB, athletes);
    }

    /**
     * Try to recognize a face, using configured database
     *
     * @param image IplImage croped face to recognized
     * @return int of recognized Athlete id
     */
    public int predictedLabel(IplImage image) {
        double distance[] = new double[1];
        int result = 0;
        int[] athlete = new int[1];
        try {
            log.info("Test image is converting FittedGrey scale");
            IplImage greyPredictImage = toFittedGray(image, x, y);
            log.info("Try to predict ...");
            //result = eigenRecognizer.predict(image);
            eigenRecognizer.predict(greyPredictImage, athlete, distance);

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
        eigenRecognizer.save(savePath);
        log.info("Face Recognizer saved to" + savePath);
    }

    /**
     *
     * @param path String of path to saved FaceRecognizer
     */
    public void load(String path) {
        eigenRecognizer.load(path);
        log.info("Face Recognizer loaded from " + path);
    }

    /**
     * Images should be resized and grayscaled before eigenfaces. Then, egalized
     * its histogram
     *
     * @param image IplImage of byte image to fit for ACP
     * @param x int of new image width
     * @param y int of new image height
     * @return IplImage of grayscale fitted image
     */
    public IplImage toFittedGray(IplImage image, int x, int y) {

        // Create empty image
        final IplImage resized = cvCreateImage(cvSize(x, y), IPL_DEPTH_8U, 1);
        final IplImage gray = cvCreateImage(cvSize(image.width(), image.height()), IPL_DEPTH_8U, 1);
        log.debug("Convert to Grayscale");
        cvConvertScale(image, gray, 1. / 255, 0);
        log.debug("Resizing image");
        cvResize(gray, resized, CV_INTER_AREA);
        log.debug("Egalisation");
        cvEqualizeHist(resized, resized);

        /* Write gray scale resized image
         BufferedImage write = resized.getBufferedImage();
         try {
         ImageIO.write(write, "jpg", new File("/opt/openCV/testIpl"+image.toString()+".jpg"));
         } catch (IOException e) {
            
         }
         */
        return resized;
    }
}
