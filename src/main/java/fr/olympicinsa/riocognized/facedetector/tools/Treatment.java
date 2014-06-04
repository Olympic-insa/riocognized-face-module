/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.olympicinsa.riocognized.facedetector.tools;

import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_core.CvArr;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_32F;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_32S;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_core.cvConvertScale;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvCvtScale;
import static com.googlecode.javacv.cpp.opencv_core.cvFlip;
import static com.googlecode.javacv.cpp.opencv_core.cvPow;
import static com.googlecode.javacv.cpp.opencv_core.cvSize;
import static com.googlecode.javacv.cpp.opencv_core.cvSub;
import static com.googlecode.javacv.cpp.opencv_highgui.cvSaveImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_INTER_AREA;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_INTER_NN;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_THRESH_TRUNC;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvEqualizeHist;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvResize;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvResize;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvResize;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvResize;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvResize;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvResize;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvResize;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvResize;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvResize;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvResize;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvResize;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvResize;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvResize;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvResize;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvSmooth;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvThreshold;
import static fr.olympicinsa.riocognized.facedetector.recognition.RioRecognizer.log;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.apache.log4j.Logger;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.CV_GAUSSIAN;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.equalizeHist;

/**
 *
 * @author alex
 */
public class Treatment {

    public static Logger log = Logger.getLogger(Treatment.class);

    //public static CanvasFrame canvas = new CanvasFrame("Debug");
    /**
     * Images should be resized and grayscaled before eigenfaces. Then, egalized
     * its histogram
     *
     * @param image Mat of image to treat
     * @return Mat of grayscale equalized image
     */
    public static Mat beforeDetection(Mat image) {
        Mat gray = new Mat();
        cvtColor(image, gray, COLOR_BGR2GRAY);
        equalizeHist(gray, gray);
        return gray;
    }

    public static Mat resize(Mat image, int maxW) {
        Mat resizeImage = new Mat();
        log.info("Image size: " + image.width() + "," + image.height());
        double f = (double) image.width() / (double) maxW;
        log.info("Resize factor : " + f);
        int w = (int) (image.width() / f);
        int h = (int) (image.height() / f);
        Size sz = new Size(w, h);
        log.info("Resizing image to : " + w + "," + h);
        Imgproc.resize(image, resizeImage, sz);
        return resizeImage;
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
    public static IplImage beforeRecognition(opencv_core.IplImage image, int x, int y) {

        // Create empty image
        final IplImage resized = cvCreateImage(cvSize(x, y), IPL_DEPTH_32F, 1);
        final IplImage gray = cvCreateImage(cvSize(image.width(), image.height()), IPL_DEPTH_32F, 1);

        log.debug("Convert to Grayscale");
        cvConvertScale(image, gray, 1. / 255, 0);
        log.debug("Resizing image");

        cvResize(gray, resized, CV_INTER_NN);
        //log.debug("Egalisation");
        //cvEqualizeHist(resized, resized);

        return resized;
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
    public static IplImage beforePreditionOld(opencv_core.IplImage image, int x, int y) {

        // Create empty image
        final IplImage resized = cvCreateImage(cvSize(x, y), IPL_DEPTH_32F, 1);
        final IplImage gray = cvCreateImage(cvSize(image.width(), image.height()), IPL_DEPTH_32F, 1);

        log.debug("Convert to Grayscale");
        cvConvertScale(image, gray, 1. / 255, 0);
        log.debug("Resizing image");

        cvResize(gray, resized, CV_INTER_NN);
        //log.debug("Egalisation");
        //cvEqualizeHist(resized, resized);
        //CanvasFrame canvas = new CanvasFrame("Debug");
        //canvas.showImage(resized);
        return resized;
    }

    /**
     * Images should be resized and grayscaled before eigenfaces. Then, egalized
     * its histogram
     *
     * @param img IplImage of byte image to fit for ACP
     * @param x int of new image width
     * @param y int of new image height
     * @return IplImage of grayscale fitted image
     */
    public static IplImage beforePrediction(IplImage img, int x, int y, int predict) {
        
        IplImage gf = cvCreateImage(cvSize(img.width(), img.height()), IPL_DEPTH_32F, 1);
        IplImage gr = IplImage.create(img.width(),img.height(), IPL_DEPTH_32F, 1);
        IplImage tr = IplImage.create(img.width(), img.height(), IPL_DEPTH_32F, 1);
        IplImage resized = cvCreateImage(cvSize(x, y), IPL_DEPTH_32F, 1);
        IplImage b1 = IplImage.create(img.width(), img.height(), IPL_DEPTH_32F, 1);
        IplImage b2 = IplImage.create(img.width(), img.height(), IPL_DEPTH_32F, 1);
        IplImage b3 = IplImage.create(img.width(), img.height(), IPL_DEPTH_32F, 1);
        CvArr mask = IplImage.create(0, 0, IPL_DEPTH_32F, 1);

        //gamma(img, gr, gf);

        //cvSmooth(gf, b1, CV_GAUSSIAN, 1);
        //cvSmooth(gf, b2, CV_GAUSSIAN, 7);
        //cvSub(b1, b2, b2, mask);
        //cvConvertScale(b2, gr, 127, 127);
        cvConvertScale(img, gr, 1. / 255, 0);
        cvThreshold(gr,gr,255,0,CV_THRESH_TRUNC);
        cvResize(gr, resized, CV_INTER_NN);
        if (predict == 1) {
           //canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
           //canvas.showImage(resized);
        }
        return resized;

    }

    public static void gamma(IplImage src, IplImage dst, IplImage temp) {
        cvConvertScale(src, temp, 1.0 / 255, 0);
        cvPow(temp, temp, 0.2   );
        cvConvertScale(temp, dst, 255, 0);
    }

}
