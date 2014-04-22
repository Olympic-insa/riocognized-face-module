/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.olympicinsa.riocognized.facedetector;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

/**
 *
 * @author imane
 */
public class ImageConvertor {

    /**
     * Converts/writes a Mat into a BufferedImage.
     *
     * @param matrix Mat of type CV_8UC3 or CV_8UC1
     * @return BufferedImage of type TYPE_3BYTE_BGR or TYPE_BYTE_GRAY
     */
    public static BufferedImage matToBufferedImage(Mat matrix) {
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

        return image;
    }

    /**
     * Converts/writes a BufferedImage into a Mat.
     *
     * @param image BufferedImage of type TYPE_3BYTE_BGR
     * @return image Mat of type CV_8UC3
     */
     public static Mat BufferedImagetoMat(BufferedImage image) {
        int rows = image.getWidth();
        int cols = image.getHeight();
        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        Mat mat = new Mat(cols, rows, CvType.CV_8UC3);
        mat.put(0, 0, data);
        return mat;
    }
   

    /**
     * Converts a byteArray into a Mat.
     *
     * @param byteImage byteImage of type byteArray
     * @return matImage Mat of type CV_8UC3
     */
    public static Mat byteArrayToMat(byte[] byteImage) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(byteImage);
        try {
            BufferedImage bImage = ImageIO.read(in);
            Mat matImage = BufferedImagetoMat(bImage);
            return matImage;
        } catch (IOException e) {
            e.printStackTrace();
            
        }
        return null;
    }

    /**
     * Converts a Mat into a byteArray.
     *
     * @param matImage Mat of type CV_8UC3
     * @return byteImage byteImage of type byteArray
     */
    public static byte[] MatTobyteArray(Mat matImage) {
        BufferedImage bImage = matToBufferedImage(matImage);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(bImage, "Buffered", out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] byteImage = out.toByteArray();
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return byteImage;
    }

}
