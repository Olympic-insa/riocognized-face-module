/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.olympicinsa.riocognized.facedetector;

import java.io.*;
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
    public BufferedImage matToBufferedImage(Mat matrix) {
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
     * @param im BufferedImage of type TYPE_3BYTE_BGR 
     * @return image Mat of type CV_8UC3 
     */
     public Mat BufferedImagetoMat(BufferedImage im) {
        // Convert INT to BYTE
         im = new BufferedImage(im.getWidth(), im.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
        // Convert bufferedimage to byte array
         byte[] pixels = ((DataBufferByte) im.getRaster().getDataBuffer()).getData();
        // Create a Matrix the same size of image
        Mat image = new Mat(im.getHeight(), im.getWidth(), CvType.CV_8UC3);
        // Fill Matrix with image values
        image.put(0, 0, pixels);

        return image;
    }
     
     /**
     * Converts a byteArray into a Mat.
     * @param byteImage byteImage of type byteArray
     * @return matImage Mat of type CV_8UC3 
     */
     public Mat byteArrayToMat(byte[] byteImage) {
        ByteArrayInputStream in = new ByteArrayInputStream(byteImage);
        BufferedImage bImage = ImageIO.read(in);
        Mat matImage = BufferedImagetoMat(bImage);
        return matImage;
    }
     
      /**
     * Converts a Mat into a byteArray.
     * @param matImage Mat of type CV_8UC3 
     * @return byteImage byteImage of type byteArray
     
     */
     public byte[] MatTobyteArray(Mat matImage) {
        BufferedImage bImage = matToBufferedImage(matImage);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(bImage,out);
        byte[] byteImage = out.toByteArray();
        out.close();
        return byteImage;
    }

}
