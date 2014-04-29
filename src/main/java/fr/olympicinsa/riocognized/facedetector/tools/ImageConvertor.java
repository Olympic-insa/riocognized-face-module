/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.olympicinsa.riocognized.facedetector.tools;

import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvSize;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import org.apache.log4j.Logger;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

/**
 *
 * @author imane
 */
public class ImageConvertor {

    public static Logger log = Logger.getLogger(ImageConvertor.class);

    /**
     * Converts/writes a Mat into a BufferedImage.
     *
     * @param matrix Mat of type CV_8UC3 or CV_8UC1
     * @return BufferedImage of type TYPE_3BYTE_BGR or TYPE_BYTE_GRAY
     */
    public static BufferedImage matToBufferedImage(Mat matrix) {
        log.debug("****** MatToBuffered Image **********");
        log.debug("input : " + matrix.toString());
        int cols = matrix.cols();
        int rows = matrix.rows();
        int elemSize = (int) matrix.elemSize();
        byte[] data = new byte[cols * rows * elemSize];
        int type;

        matrix.get(0, 0, data);

        switch (matrix.channels()) {
            case 1:
                type = BufferedImage.TYPE_BYTE_GRAY;
                break;

            case 3:
                type = BufferedImage.TYPE_3BYTE_BGR;

                // bgr to rgb
                byte b;
                for (int i = 0; i < data.length; i = i + 3) {
                    b = data[i];
                    data[i] = data[i + 2];
                    data[i + 2] = b;
                }
                break;

            default:
                return null;
        }

        BufferedImage image = new BufferedImage(cols, rows, type);
        image.getRaster().setDataElements(0, 0, cols, rows, data);
        log.debug("type: " + type);
        log.debug("output:" + image.toString());
        log.debug("***********************************");
        return image;
    }

    /**
     * Converts/writes a BufferedImage into a Mat.
     *
     * @param image BufferedImage of type TYPE_3BYTE_BGR
     * @return Mat image of type CV_8UC3
     */
    public static Mat bufferedImagetoMat(BufferedImage image) {
        log.debug("********bufferedImageToMat *********");
        log.debug("input : " + image.toString());
        int rows = image.getWidth();
        int cols = image.getHeight();
        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        Mat mat = new Mat(cols, rows, CvType.CV_8UC3);
        mat.put(0, 0, data);
        log.debug("output : "+ mat.toString());
        log.debug("***********************************");
        return mat;
    }

    /**
     * Converts a byteArray into a Mat.
     *
     * @param byteImage byte[] containing image
     * @return Mat matImage of type CV_8UC3
     * @throws IOException
     */
    public static Mat byteArrayToMat(byte[] byteImage) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(byteImage);
        try {
            BufferedImage bImage = ImageIO.read(in);
            Mat matImage = bufferedImagetoMat(bImage);
            return matImage;
        } catch (IOException e) {
            System.err.println("Can't convert byteArray to Mat");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Converts a Mat into a byteArray.
     *
     * @param matImage Mat of type CV_8UC3
     * @return byte[] byteImage
     */
    public static byte[] matTobyteArray(Mat matImage) {
        byte[] imageInByte = null;
        BufferedImage bImage = matToBufferedImage(matImage);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bImage, "jpg", baos);
            baos.flush();
            imageInByte = baos.toByteArray();
            baos.close();
        } catch (IOException e) {
            System.err.println("Can't convert Mat to byteArray");
            e.printStackTrace();
        }
        return imageInByte;
    }

    /**
     * Converts a Mat into a IplImage (GRAY_SCALED, depht = 8, Ch = 1).
     *
     * @param matImage Mat of type CV_8UC3
     * @return IplImage iplImage (IPL_DEPTH_8U, 1 Channel)
     */
    public static IplImage matToIplImage(Mat matImage) {
        log.debug("********** matToIplImage starting **********");
        log.debug("input:" + matImage.toString());
        IplImage image8UC3 = IplImage.createFrom(ImageConvertor.matToBufferedImage(matImage));
        IplImage resized = cvCreateImage(cvSize(image8UC3.width(), image8UC3.height()), IPL_DEPTH_8U, 1);
        if (image8UC3.nChannels() > 1)
            cvCvtColor(image8UC3, resized, CV_BGR2GRAY);
        else 
            resized = image8UC3;
        log.debug("output: " + resized.toString());
        log.debug("*****************************************\n");
        return resized;
    }
}
