package fr.olympicinsa.riocognized.facedetector;

import static fr.olympicinsa.riocognized.facedetector.Riocognized.log;
import fr.olympicinsa.riocognized.facedetector.db.FaceDBReader;
import fr.olympicinsa.riocognized.facedetector.detection.FaceDetector;
import fr.olympicinsa.riocognized.facedetector.exception.FaceDBException;
import fr.olympicinsa.riocognized.facedetector.recognition.RioRecognizer;
import fr.olympicinsa.riocognized.facedetector.tools.ImageConvertor;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import static java.lang.System.exit;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.bytedeco.javacpp.opencv_core.Mat;
import static org.bytedeco.javacpp.opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE;
import static org.bytedeco.javacpp.opencv_highgui.imread;
import static org.bytedeco.javacpp.opencv_highgui.imread;
import static org.bytedeco.javacpp.opencv_highgui.imwrite;

public class Test {

    public static Logger log = Logger.getLogger(Test.class);

    public static void main(String[] args) {

        DOMConfigurator.configure(Thread.currentThread().getContextClassLoader().getResource("log4j.xml"));

        log.info("Demarrage de Riocognized");
        DateFormat dateFormat = new SimpleDateFormat("hhmmss-dd-MM-yy");
        Date date = new Date();
        String dateString = dateFormat.format(date);
        Mat image;
        String athletePath;
//        String file = "/opt/openCV/athleteDB/51/face1.jpg";
//        String id = "145";
//        try {
//        FaceDBReader faces = new FaceDBReader("/opt/openCV/athleteDB/faces1.csv");
//            ArrayList<String[]> list = (ArrayList)faces.readFile("/opt/openCV/athleteDB/faces.csv");
//            String[] toDelete = {file, id};
//            Iterator it = list.iterator();
//            while (it.hasNext()) {
//                String[] i = (String[])it.next();
//                if (i[0].equals(file)) {
//                    it.remove();
//                }
//            }
//            log.info(toDelete);
//            log.info(list.get(0));
//            log.info(list.get(0)[0]);
//            log.info(list.get(0)[1]);
//            list.remove(toDelete);
//            list.trimToSize();
//            log.info(list.toString());
//            faces.setList(list);
//            faces.writeFile();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        //System.load("/opt/openCV/libopencv_java248.so");
        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        //Test FaceDBReader
        try {
            log.info("Testing FaceDBReader");
            FaceDBReader faceDB = new FaceDBReader("/opt/openCV/faces.csv");
            for (String[] faces : faceDB.getFaces()) {
                log.debug(faces[0] + ";" + faces[1]);
            }
            List<String[]> data = new ArrayList<>();
            data.add(new String[]{"India", "New Delhi"});
            data.add(new String[]{"United States", "Washington D.C"});
            data.add(new String[]{"Germany", "Berlin"});
            faceDB.setList(data);
            faceDB.addFace(new String[]{"Germany", "Berlin"});

            faceDB.writeFile();

            log.info("Test FaceDB OK !");
            // Test RioRecognizer
            log.info("Testing RioRecognizer");
            String path = "/opt/openCV/test.yml";
            //Load faceDB
            faceDB = new FaceDBReader("/opt/openCV/athleteDB/faces.csv");
            //Create  Recognizor
            log.info("Create Recognizer");
            RioRecognizer recognizor = new RioRecognizer(faceDB, path);
            //Store faces
            log.info("Read CSV en load images");
            recognizor.init();
            log.info("Create Recognizer Eigenfaces (PCA)");
            recognizor.train();
            log.info("Save Recognizer Eigenfaces");
            recognizor.save();

            Mat toTest = imread("/opt/openCV/2.jpg", CV_LOAD_IMAGE_GRAYSCALE);

            /* Write just grayscaled  loaded test image 
             BufferedImage write = toTest.getBufferedImage();
             try {
             ImageIO.write(write, "jpg", new File("/opt/openCV/testIpl.jpg"));
             } catch (IOException e)            
             }
             */
            int athlete = recognizor.predictedLabel(toTest);
            System.out.println("\nAthlete recognized : " + athlete + "\n");
        } catch (FaceDBException e) {
            log.error("Can't read/create faceDB csv");
            exit(0);
        }

        log.info("Testing Riocognized FaceDetector");
        String imageParam = (args.length > 0) ? args[0] : "/opt/openCV/image.jpg";

        try {
            athletePath = Test.class.getResource(imageParam).getPath();
        } catch (Exception e) {
            athletePath = "/opt/openCV/2.jpg";
            log.info("Utilisation du fichier : " + athletePath);
        }

        log.info("Test Image Conversion");
        try {
            FaceDetector faceDetector = new FaceDetector();
            image = imread(athletePath);

            //Test Mat to BufferedImage
            BufferedImage bimage = ImageConvertor.matToBufferedImage(image);
            File outputfile = new File("/opt/openCV/image_matToBuff.jpg");
            ImageIO.write(bimage, "jpg", outputfile);
            log.info("Mat to Buffered Image works !");

            //Test Mat to Byte
            byte[] Bimage = ImageConvertor.matTobyteArray(image);
            ByteArrayInputStream bis = new ByteArrayInputStream(Bimage);
            BufferedImage imageFull = ImageIO.read(bis);
            outputfile = new File("/opt/openCV/image_matToByte.jpg");
            ImageIO.write(imageFull, "jpg", outputfile);
            log.info("Mat to Byte works !");

            //Test Byte to Mat
            String outputBM = "/opt/openCV/image_byteToMat.jpg";
            Mat mat = ImageConvertor.byteArrayToMat(Bimage);
            imwrite(outputBM, image);
            log.info("Byte to Mat works !");

            //Test Face Detection
            log.info("Test Face Detection");
            String output = "/opt/open CV/athletes_detected_" + dateString + ".jpg";
            log.info("Result will be written in : " + output + " ....");

            try {
                int detected = faceDetector.detectFaces(athletePath, output);
                log.info("Detected " + detected + " athletes !");
                //crop face
                Mat crop = faceDetector.cropFaceToMat(image);
                imwrite("/opt/openCV/face_" + dateString + ".jpg", crop);

            } catch (Exception e) {
                log.error("Error processiong detection");
            }

        } catch (IOException e) {
            log.error("Error in covnersion function");
            e.printStackTrace();
        }
    }

    public static void debugIplImage(Mat i) {
        log.info("****** Debug Image **********");
        log.info("Depht : " + i.depth());
        log.info("CVsize : " + i.cvSize().toString());
        log.info("String : " + i.toString());
        log.info("****************************");
    }
}
