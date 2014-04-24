/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.olympicinsa.riocognized.facedetector.recognition;

import com.googlecode.javacv.cpp.opencv_contrib.FaceRecognizer;
import static com.googlecode.javacv.cpp.opencv_contrib.createEigenFaceRecognizer;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_core.MatVector;
import static com.googlecode.javacv.cpp.opencv_core.cvConvertScale;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvMinMaxLoc;
import static com.googlecode.javacv.cpp.opencv_core.cvSize;
import static com.googlecode.javacv.cpp.opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_INTER_AREA;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvEqualizeHist;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvResize;
import static com.sun.org.apache.xalan.internal.lib.ExsltDatetime.date;
import fr.olympicinsa.riocognized.facedetector.FaceDetector;
import fr.olympicinsa.riocognized.facedetector.ImageConvertor;

import fr.olympicinsa.riocognized.facedetector.OpenCV;
import fr.olympicinsa.riocognized.facedetector.Riocognized;
import fr.olympicinsa.riocognized.facedetector.csv.FaceDBReader;
import org.apache.log4j.Logger;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import org.opencv.core.Mat;


/**
 *
 * @author alex
 */
public class RioRecognizer {

    public static Logger LOGGER = Logger.getLogger(Riocognized.class);

    private final double THREASHOLD = 10.0;

    private final FaceDBReader faceDatabase;
    private final FaceRecognizer eigenRecognizer;
    private final String savePath;
    private final FaceDetector faceDetector;
    private final OpenCV opencv;
    // To scall faces
    private final int F = 2;
    private int x = 100;
    private int y = 100;

    private MatVector imagesDB;
    private int[] athletes;

    public RioRecognizer(FaceDBReader db, String out) {
        this.opencv = OpenCV.getInstance();
        this.faceDetector = new FaceDetector();
        this.faceDatabase = db;
        this.savePath = out;
        this.eigenRecognizer = createEigenFaceRecognizer(10, THREASHOLD);
    }

    public void init() {
        imagesDB = new MatVector(faceDatabase.getFaces().size());
        athletes = new int[faceDatabase.getFaces().size()];

        int counter = 0;
        String label;

        IplImage img;
        IplImage grayImg;

        for (String[] face : faceDatabase.getFaces()) {
            try {
                img = cvLoadImage(face[0],CV_LOAD_IMAGE_GRAYSCALE);
                label = face[1];
                System.out.println("Read image(" + counter + ") in " + face[0]);
                LOGGER.info("Image loaded :"  + face[0]);
                grayImg = toFittedGray(img, x, y);

                imagesDB.put(counter, grayImg);

                athletes[counter] = new Integer(label);
                
                counter++;
            } catch (Exception e) {
                System.err.println("Can't read image(" + counter + ") in " + face[0]);
            }
        }
    }

    public void train() {
        if (imagesDB.size() < 1) {
            init();
        }
        for (int i : athletes) {
            System.out.println("Athlete #" + i);
        }
        eigenRecognizer.train(imagesDB, athletes);
    }

    public int predictedLabel(IplImage image) {
        double distance[] = new double[1];
        int result = 0;
        int[] athlete = new int[1];
        try {
            //BufferedImage cropedBuffered = ImageConvertor.matToBufferedImage(image);
            System.err.println("To IPL");
            //IplImage croped = IplImage.createFrom(cropedBuffered);
             System.err.println("To FittedGrey scale");
            IplImage greyPredictImage = toFittedGray(image, x, y);
            System.err.println("Try to predict");
            eigenRecognizer.predict(greyPredictImage);
        } catch (Exception e) {
            System.err.println("Can't detect any face");
            e.printStackTrace();
            return 0;
        }
        if ((athlete[0] != 0) && (athlete[0] <= athletes.length)) {
            System.err.println("Ath:" + athlete[0]);
            System.err.println("Dist :" + distance[0]);
            return athlete[0];
            
        }
        return 0;
    }

    public void save() {
        eigenRecognizer.save(savePath);
        LOGGER.info("Face Recognizer saved to" + savePath);
    }

    public void load(String path) {
        eigenRecognizer.load(path);
        LOGGER.info("Face Recognizer loaded from " + savePath);
    }

    /**
     * Images should be resized and grayscaled before eignefaces. Then, egalized
     * its histogram
     *
     * @param image
     * @param x
     * @param y
     * @return
     */
    public IplImage toFittedGray(IplImage image, int x, int y) {
        CvPoint minloc = new CvPoint();
        CvPoint maxloc = new CvPoint();
        double[] minVal = new double[1];
        double[] maxVal = new double[1];
        if (minVal[0] < -1e30) {
            minVal[0] = -1e30;
        }
        if (maxVal[0] > 1e30) {
            maxVal[0] = 1e30;
        }
        if (maxVal[0] - minVal[0] == 0.0f) {
            maxVal[0] = minVal[0] + 0.001;  // remove potential divide by zero errors.
        }   
        System.err.println(minVal[0] +"  "+maxVal[0] );
        // Convert the format
        final IplImage resized = cvCreateImage(cvSize(x, y), IPL_DEPTH_8U, 1);
        final IplImage gray = cvCreateImage(cvSize(image.width(), image.height()), IPL_DEPTH_8U, 1);
        System.err.println("Gray");
        //cvCvtColor(image, gray, CV_BGR2GRAY);
        cvConvertScale(image, gray, 1./255,0);
        System.err.println("Resizing");
        cvResize(gray, resized, CV_INTER_AREA);
        System.err.println("Egalisation");
        cvEqualizeHist(resized, resized);
        BufferedImage write = resized.getBufferedImage();
        try {
            ImageIO.write(write, "jpg", new File("/opt/openCV/testIpl"+image.toString()+".jpg"));
        } catch (IOException e) {
            
        }
        return resized;
    }
}
