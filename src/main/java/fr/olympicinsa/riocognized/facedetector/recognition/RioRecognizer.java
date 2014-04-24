/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.olympicinsa.riocognized.facedetector.recognition;

import com.googlecode.javacv.cpp.opencv_contrib.FaceRecognizer;
import static com.googlecode.javacv.cpp.opencv_contrib.createEigenFaceRecognizer;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_core.MatVector;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_INTER_AREA;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvEqualizeHist;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvResize;
import fr.olympicinsa.riocognized.facedetector.FaceDetector;
import fr.olympicinsa.riocognized.facedetector.csv.FaceDBReader;
import org.apache.log4j.Logger;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

import org.opencv.core.Mat;

/**
 *
 * @author alex
 */
public class RioRecognizer {
    
    private static final Logger LOGGER = Logger.getLogger(RioRecognizer.class);
    
    private final int THREASHOLD = 100;
    
    private final FaceDBReader faceDatabase;
    private final FaceRecognizer eigenRecognizer;
    private final String savePath;
    private final FaceDetector faceDetector;
    
    // To scall faces
    private final int F = 2;
    private int x = 100;
    private int y = 100;
    
    private MatVector imagesDB;
    private int [] athletes;
    
    public RioRecognizer(FaceDBReader db, String out) {
        this.faceDetector = new FaceDetector();
        this.faceDatabase = db;
        this.savePath = out;
        this.eigenRecognizer = createEigenFaceRecognizer(0, THREASHOLD);
    }


    public void init() {
        imagesDB = new MatVector(faceDatabase.getFaces().size());
        athletes = new int[faceDatabase.getFaces().size()];
        
        int counter = 0;
        String label;

        BufferedImage img;
        IplImage grayImg;

        for (String [] face : faceDatabase.getFaces()) {
            try {
            img = ImageIO.read(new URL(face[0]));
            label = face[1];
            
            grayImg = toFittedGray(img, img.getWidth(), img.getHeight());

            imagesDB.put(counter, grayImg);

            athletes[counter] = new Integer(label);

            counter++;
            } catch (IOException e) {
            System.err.println("Can't read image(" + counter + ") in " + face[0]);
        }
        }
    }
    
    public void train() {
        eigenRecognizer.train(imagesDB, athletes);
    }
    
    public int predictedLabel(Mat image){
        double distance[] = new double[1];
        int result = 0;
        int[] athlete = new int[1];
        try {
            BufferedImage croped = faceDetector.cropFaceToBufferedImage(image);
            IplImage greyPredictImage = toFittedGray(croped, croped.getWidth(), croped.getHeight());
             eigenRecognizer.predict(greyPredictImage, athlete, distance);
        } catch (Exception e) {
            System.err.println("Can't detect any face");
            e.printStackTrace();
            return 0;
        }
        if ((athlete[0] != 0) && (athlete[0]<= athletes.length)) {
            return athlete[0];
        }
        return 0;
    }
    
    public void save() {
        eigenRecognizer.save(savePath);
        LOGGER.info("Face Recognizer saved to" + savePath);
    }
    
    public void load() {
        eigenRecognizer.load(savePath);
        LOGGER.info("Face Recognizer loaded from " + savePath);
    }

    /**
     * Images should be resized and grayscaled before eignefaces.
     * Then, egalized its histogram
     * 
     * @param image
     * @param x
     * @param y
     * @return 
     */
    public IplImage toFittedGray(BufferedImage image, int x, int y) {
        final IplImage iplImage = IplImage.createFrom(image);
        if (x == 0 || y == 0) {
            x = iplImage.width() / F;
            y = iplImage.height() / F;
        }
        final IplImage resized = IplImage.create(x, y, IPL_DEPTH_8U, 1);
        final IplImage gray = IplImage.create(iplImage.width(), iplImage.height(), IPL_DEPTH_8U, 1);
        cvResize(gray, resized, CV_INTER_AREA);
        cvCvtColor(iplImage, gray, CV_BGR2GRAY);
        cvEqualizeHist(resized, resized);
        return resized;
    }
}
