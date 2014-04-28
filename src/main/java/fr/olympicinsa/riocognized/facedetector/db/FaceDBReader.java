/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.olympicinsa.riocognized.facedetector.db;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import fr.olympicinsa.riocognized.facedetector.exception.FaceDBException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author alex
 */
public class FaceDBReader {

    public static Logger log = Logger.getLogger(FaceDBReader.class);
    private static String pathDB;
    private List faces;

    public FaceDBReader() {
        faces = new ArrayList<>();
    }

    /**
     * FaceDBReader Constructor
     *
     * @param faceDB String of path to csv file
     */
    public FaceDBReader(String faceDB) {
        pathDB = faceDB;
        faces = new ArrayList<>();
        File file = new File(pathDB);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            faces = readFile(pathDB);
        } catch (IOException | FaceDBException e) {
            log.error("Can't read/create faceDB csv");
        }
    }

    /**
     * Read line form csv file
     *
     * @param file
     * @return List of String[] containing face info
     * @throws FaceDBException
     */
    public static List readFile(String file) throws FaceDBException {

        try {
            FileReader fileReader = new FileReader(file);
            CSVReader csvReader = new CSVReader(fileReader, ';');
            List content = csvReader.readAll();
            csvReader.close();
            return content;
        } catch (IOException e) {
            throw new FaceDBException("Can't find csv file", e);
        }
    }

    /**
     * Write csv file with faceDB list to pathDB
     *
     * @throws FaceDBException
     */
    public void writeFile() throws FaceDBException {
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(pathDB), ';');
            writer.writeAll(faces);
            writer.close();
        } catch (IOException e) {
            throw new FaceDBException("Can't write csv file", e);
        }
    }

    /**
     *
     * @return List of faces
     */
    public List<String[]> getFaces() {
        return this.faces;
    }

    /**
     * Set faces list
     *
     * @param faces List of faces
     */
    public void setList(List<String[]> faces) {
        this.faces = faces;
    }

    /**
     * Add face to faceDB list
     *
     * @param face String[] w/h athlete face {path, id}
     */
    public void addFace(String[] face) {
        this.faces.add(face);
    }

    /**
     * Delete athletes face from faceDB list
     *
     * @param face String[] of athlete face {path, id}
     */
    public void deleteFace(String[] face) {
        this.faces.remove(face);
    }

    /**
     * Clear facdDB list
     */
    public void clearFaces() {
        this.faces.clear();
    }

}
