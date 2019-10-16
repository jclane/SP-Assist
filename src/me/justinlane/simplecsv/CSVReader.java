package me.justinlane.simplecsv;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.nio.file.Path;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * A simple CSV parser.
 *
 * @author Justin Lane
 * @version 4.0
 * @since 4.0
 */
public class CSVReader {
  
  /**
   * This will ignore delimiters found within quotes and 
   * remove the quote characters.
   * 
   * @param str the string to check for double quotes
   * @param delimiter the string to use as a delimiter   
   * @see <a href=https://stackoverflow.com/a/2120714 />
   */  
  private static List<String> handleQuotes(String str, String delimiter) {
    List<String> result = new ArrayList<String>();
    int start = 0;
    boolean inQuotes = false;
    for (int current = 0; current < str.length(); current++) {
        if (str.charAt(current) == '\"') inQuotes = !inQuotes; // toggle state
        boolean atLastChar = (current == str.length() - 1);
        if(atLastChar) result.add(str.substring(start));
        else if (str.charAt(current) == delimiter.charAt(0) && !inQuotes) {
            result.add(str.substring(start, current).replace(delimiter, ""));
            start = current + 1;
        }
    }
    return result;
  }  
  
  /** 
   * Reads filePath using delimit as the delimiter.
   * 
   * @param filePath the path to the file to be read as type Path
   * @param delimit the string to use a as delimiter
   * @return a list of lists with string values from the file
   */
  public static List<List<String>> readLines(Path filePath, String delimit) throws IOException {
    Scanner scanner = new Scanner(filePath);
    List<List<String>> lines = new ArrayList<List<String>>();
    while (scanner.hasNextLine()) {
      List<String> columnData = new ArrayList<String>();
      for (String data : handleQuotes(scanner.nextLine(), delimit)) {
        columnData.add(data); 
      }    
      lines.add(columnData);        
    }
    scanner.close();
    
    return lines;
  }  
  
  /**
   * Reads filePath using commas as the delimiter.
   * 
   * @param filePath the path to the file to be read as type Path
   * @return a list of lists with string values from the file
   */
  public static List<List<String>> readLines(Path filePath) throws IOException {
    return readLines(filePath, ",");
  }
  
  /** 
   * Reads filePath using delimit as the delimiter.
   * 
   * @param filePath the string representing the path to a file
   * @param delimit the string to use a as delimiter
   * @return a list of lists with string values from the file
   */
  public static List<List<String>> readLines(String filePath, String delimit) throws FileNotFoundException {
    Scanner scanner = new Scanner(new File(filePath));
    List<List<String>> lines = new ArrayList<List<String>>();
    while (scanner.hasNextLine()) {
      List<String> columnData = new ArrayList<String>();
      for (String data : handleQuotes(scanner.nextLine(), delimit)) {
        columnData.add(data); 
      }    
      lines.add(columnData);        
    }
    scanner.close();
    
    return lines;
  }  
  
  /**
   * Reads filePath using commas as the delimiter.
   * 
   * @param filePath path to file
   * @return a list of lists with string values from the file
   */
  public static List<List<String>> readLines(String filePath) throws FileNotFoundException {
    return readLines(filePath, ",");
  }

}