/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.olympicinsa.riocognized.facedetector.csv;

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

    public void writeFile() {
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(pathDB), ';');
            writer.writeAll(faces);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String[]> getFaces() {
        return this.faces;
    }

    public void setList(List<String[]> faces) {
        this.faces = faces;
    }

    public void addFace(String[] face) {
        this.faces.add(face);
    }

    public void deleteFace(String[] face) {
        this.faces.remove(face);
    }

    public void clearFaces() {
        this.faces.clear();
    }

}
