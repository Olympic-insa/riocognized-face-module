/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.olympicinsa.riocognized.facedetector.db;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author alex
 */
public class FaceDBReader {

    private static String pathDB;
    private List faces;


    public FaceDBReader() {
        faces = new ArrayList<String>();
    }

    /**
     * FaceDBReader Constructor
     * 
     * @param faceDB String of path to csv file
     */
    public FaceDBReader(String faceDB) {
        pathDB = faceDB;
        faces = new ArrayList<String>();
        File file = new File(pathDB); 
        try {
            if (!file.exists()) file.createNewFile();
            faces = readFile(pathDB);
        } catch (IOException e) {
            System.err.println("Can't read/create faceDB csv");
        }
    }

    /**
     * Read line form csv file
     * 
     * @param file
     * @return List of String[] containing face info 
     */
    public static List readFile(String file) {

        try {
            FileReader fileReader = new FileReader(file);
            CSVReader csvReader = new CSVReader(fileReader, ';');
            List content = csvReader.readAll();
            csvReader.close();
            return content;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Write csv file with faceDB list to pathDB
     */
    public void writeFile() {
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(pathDB), ';');
            writer.writeAll(faces);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
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
