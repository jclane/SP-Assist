package me.justinlane.simplecsv;

import java.io.FileWriter;
import java.io.IOException;

import java.nio.file.Path;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple CSV writer.
 *
 * @author Justin Lane
 * @version 4.0
 * @since 4.0
 */
public class CSVWriter {
  
  /**
   * Creates *.CSV file with data as the content using delimit as a delimiter.
   * 
   * @param filePath a Path indicating the path and file name
   * @param data a list of lists with string data to save
   * @param delimit a string use as a delimiter
   */ 
  public static void writeLines(Path filePath, List<List<String>> data, String delimit) 
       throws IOException {
    FileWriter fw = new FileWriter(filePath.toFile());
    for (List<String> row : data) {
      fw.append(String.join(delimit, row));
      fw.append("\n");
    }
    fw.flush();
    fw.close();
  }  
   
  /**
   * Creates *.CSV file with data as the content using comma as a delimiter.
   * 
   * @param filePath a string indicating the path and file name
   * @param data a list of lists with string data to save
   */
  public static void writeLines(Path filePath, List<List<String>> data) 
      throws IOException {
    writeLines(filePath, data, ",");
  }   
   
   
  /**
   * Creates *.CSV file with data as the content using delimit as a delimiter.
   * 
   * @param filePath a string indicating the path and file name
   * @param data a list of lists with string data to save
   * @param delimit a string use as a delimiter
   */ 
  public static void writeLines(String filePath, List<List<String>> data, String delimit) 
       throws IOException {
    FileWriter fw = new FileWriter(filePath);
    for (List<String> row : data) {
      fw.append(String.join(delimit, row));
      fw.append("\n");
    }
    fw.flush();
    fw.close();
  }
  
  /**
   * Creates *.CSV file with data as the content using comma as a delimiter.
   * 
   * @param filePath a string indicating the path and file name
   * @param data a list of lists with string data to save
   */
  public static void writeLines(String filePath, List<List<String>> data) 
      throws IOException {
    writeLines(filePath, data, ",");
  }
}