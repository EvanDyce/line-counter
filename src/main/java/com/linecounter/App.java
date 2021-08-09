package com.linecounter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.nio.file.Paths;
import java.lang.Thread.*;
import java.lang.reflect.Array;
import java.math.BigInteger;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import org.apache.commons.io.FileUtils;


class App {
    @Parameter(names={"--exclude", "-e"})
    List<String> excludeEnding = new ArrayList<>();
    @Parameter(names={"--find", "-f"})
    List<String> findEnding = new ArrayList<>();
    @Parameter(names={"--path", "-p"})
    String path;
    @Parameter(names={"--linecount", "-lc"})
    boolean showLineCount = false;
    @Parameter(names={"--size", "-s"})
    boolean showSize = false;

    private int totalLines;
    private int totalFileCount;
    private int totalSize;
    private String rootDirectoryPath;
    private ArrayList<File> files;
    private HashMap<String, ArrayList<Integer>> fileTypeMap;
    

    public App() {
        this.rootDirectoryPath = null;
        this.files = new ArrayList<>();
        this.totalLines = 0;
        this.totalFileCount = 0;
        this.totalSize = 0;
        this.fileTypeMap = new HashMap<>();
    }

    // parses the files from the list
    // checks which ones are being searchec for or excluded.
    // updates the hashmap accordingly
    private void parseFiles() throws IOException {
        for (File file : this.files) {
            int currentFileLines = lineCount(file.getAbsolutePath());

            int index = file.getAbsolutePath().indexOf(".") + 1;
            String ending = file.getAbsolutePath().substring(index);

            if (ending.indexOf("\\") > 0) {
                ending = ending.substring(ending.indexOf("\\")+1);
            }
            
            // check if the ending is in the list of endings looking for
            if (this.findEnding.contains(ending)) {
                // if the ending is already in the hashmap
                if (this.fileTypeMap.containsKey(ending)) {
                    ArrayList<Integer> values = new ArrayList<>();
                    // adds one to the file count
                    values.add(this.fileTypeMap.get(ending).get(0)+1);
                    values.add(this.fileTypeMap.get(ending).get(1) + currentFileLines);
                    values.add(this.fileTypeMap.get(ending).get(2) + (int)file.length());
                    this.fileTypeMap.put(ending, values);
                    this.totalFileCount++;
                    this.totalSize += (int)file.length();
                } else {
                    // ending is not already in hashmap
                    // insert it with the default values
                    ArrayList<Integer> values = new ArrayList<>();
                    values.add(1);
                    values.add(currentFileLines);
                    values.add((int)file.length());
                    this.fileTypeMap.put(ending, values);
                    this.totalFileCount++;
                    this.totalSize += (int)file.length();
                }
                this.totalLines += currentFileLines;
                continue;

            //if the ending is not in the list of files we are looking for
            } else {
                // if there are entries in find ending we know that ours is not 
                // in it so we can just continue as ours is not meant to be found
                if (this.findEnding.size() > 0 || this.excludeEnding.contains(ending)) continue;
                // no find was specified and our file is not in the exclude. 
                // add it to the map
                else {
                    if (this.fileTypeMap.containsKey(ending)) {
                        ArrayList<Integer> values = new ArrayList<>();
                        values.add(this.fileTypeMap.get(ending).get(0) + 1);
                        values.add(this.fileTypeMap.get(ending).get(1) + currentFileLines);
                        values.add(this.fileTypeMap.get(ending).get(2) + (int)file.length());
                        this.fileTypeMap.put(ending, values);
                        this.totalLines += currentFileLines;
                        this.totalFileCount++;
                        this.totalSize += (int)file.length();
                    } else {
                        ArrayList<Integer> values = new ArrayList<>();
                        values.add(1);
                        values.add(currentFileLines);
                        values.add((int)file.length());
                        this.fileTypeMap.put(ending, values);
                        this.totalLines += currentFileLines;
                        this.totalFileCount++;
                        this.totalSize += (int)file.length();
                    }
                }
            }
        }
    }

    private void displayOutput() {
        if (this.fileTypeMap.keySet().size() == 0) {
            System.out.println("No files with ending specified could be found");
        }
        for (String key : this.fileTypeMap.keySet()) {
            ArrayList<Integer> values = this.fileTypeMap.get(key);
            String output = key + "\nNumber of Files: " + values.get(0);

            if (this.showLineCount) {
                output += "\nNumber of Lines: " + values.get(1);
            }

            if (this.showSize) {
                output += "\nSize: " + this.sizeFormatter(BigInteger.valueOf(values.get(2)));
            }

            System.out.println(output + "\n");
        }

        System.out.println("\n\nTotal Files: " + this.totalFileCount);

        if (this.showLineCount) {
            System.out.println("Total Line Count: " + this.totalLines);
        }

        if (this.showSize) {
            System.out.println("Total Size: " + this.sizeFormatter(BigInteger.valueOf(this.totalSize)));
        }
        System.out.println();
    }

    private void populateFiles(File file) {
        if (file.isDirectory()) {
            if (file.getName().contains("AppData") || file.getName().contains(".")) return;
            if (file.listFiles() != null) {
                for (File temp : file.listFiles()) {
                    this.populateFiles(temp);
                }
            }

        } else {
            this.files.add(file);
        }
    }

    private static int lineCount(String file) throws IOException  {
        int lines = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))){
            while (reader.readLine() != null) lines++;
        }
        return lines;
    }

    private String sizeFormatter(BigInteger bytes) {
        return FileUtils.byteCountToDisplaySize(bytes);
    }
   

    public static void main(String[] args) throws IOException {
        App app = new App();
        File root = null;

        JCommander.newBuilder()
            .addObject(app)
            .build()
            .parse(args);
        
        if (app.path == null) {
            System.out.println("No path provided. Searching current directory.\n");
            try {
                Thread.sleep(800);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            root = new File(System.getProperty("user.dir"));
        } else {
            root = new File(app.path);
        }
        
        app.populateFiles(root);
        try {
            app.parseFiles();
        } catch (FileNotFoundException fException) {
            System.out.println("Unable to access all files.");
            return;
        }
        app.displayOutput();
    }

    public ArrayList<File> getFilesList() {
        return this.files;
    }

    public int getLineCount() {
        return this.totalLines;
    }

    public String getRootDirectory() {
        return this.rootDirectoryPath;
    }

    public void setRootDirectoryPath(String path) {
        this.rootDirectoryPath = path;
    }
}
