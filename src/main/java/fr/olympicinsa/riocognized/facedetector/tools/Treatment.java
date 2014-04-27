/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.olympicinsa.riocognized.facedetector.tools;

import com.googlecode.javacv.cpp.opencv_core;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_core.cvConvertScale;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvSize;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_INTER_AREA;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvEqualizeHist;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvResize;
import static fr.olympicinsa.riocognized.facedetector.recognition.RioRecognizer.log;
import org.opencv.core.Mat;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.equalizeHist;

/**
 *
 * @author alex
 */
public class Treatment {

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
