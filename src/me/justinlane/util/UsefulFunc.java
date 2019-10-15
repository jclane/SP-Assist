package me.justinlane.util;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import me.justinlane.simplecsv.CSVWriter;
import me.justinlane.simplecsv.CSVReader;

/**
 * This class contains useful functions routinely used by this 
 * application.  I should probably split it up.
 *
 * @author Justin Lane
 * @version 4.0
 * @since 4.0
 */
public class UsefulFunc {
  
  /**
   * Returns a list of valid part classes.
   *
   * @return a list containing all valid part classes as strings
   */  
  public static List<String> getValidClasses() {
    final List<String> VALID_CLASSES = new ArrayList<String>(
        Arrays.asList(
        "AC", "AD", "ANT", "AUBD", "AUDIO", "BAT", "BLUE",
        "BRA", "BRD", "CAB", "CAM", "CARD", "CD", "CMOS",
        "CORD", "COS", "CRBD", "DCBD", "DCJK", "DOCKIT",
        "DVD", "FAN", "FDD", "FLD", "GK", "HDD", "HEAT SYNC",
        "INV", "IOB", "IOP", "KB", "LAN", "LCD", "LDBD", "LK",
        "MEM", "MIC", "MOD", "OBRD", "OTHER", "PEN", "PROC",
        "PWBD", "PWR", "REM", "SCRD", "TABMB", "TUBRD", "USBD",
        "VBRD", "VGBD", "WIR")
    );
    
    return VALID_CLASSES;
  }
  
  /**
   * Checks if provided partClass is in the getValidClasses
   * ArrayList.
   * 
   * @param partClass a string representing a part class to check if valid
   * @return a boolean value indicating if partClass is in getValidClasses
   */
  public static boolean classIsValid(String partClass) {  
    return getValidClasses().contains(partClass);
  }
  
  /**
   * Uses simplecsv.CSVWriter to write data to file.
   * 
   * @param path a string representing the path and file name to write
   * @param data a list containing a lists of strings to write 
   */
  public static void writeFile(String path, List<List<String>> data) {
    try {
      CSVWriter.writeLines(path, data);
    } catch (IOException e) {
      System.out.println(e);
      e.printStackTrace();
    }
  }
  
  /**
   * Uses simplecsv.CSVReader to read and parse CSV files.
   *
   * @param path a string representing the path and file name to read
   * @return a list containing a lists of strings representing the data 
   */
  public static List<List<String>> readFile(String path) {
    List<List<String>> data = new ArrayList<List<String>>();
    try {
      data.addAll(CSVReader.readLines(path));
    } catch (FileNotFoundException e) {
      System.out.println("File not found");
    } finally {
      return data;
    }
  }
  
  /**
   * Uses brand to obtain the path to that brand's folder.
   * The path is walked and results are filtered to store
   * only those brands matching the correct suffix.
   *
   * @param brand a string indicating the users chosen brand
   * @return a list of file paths as strings
   */
  public static List<String> getFiles(String brand) {
    final Brands brands = new Brands();
    Path rootDir = brands.getBrandDir(brand);
    List<String> files = new ArrayList<String>();
    try (Stream<Path> paths = Files.walk(rootDir)) {
      paths.filter(
          p -> Arrays.asList(brands.getBrandSuffix(brand))
                                      .contains(getFileSuffix(p.getFileName().toString())))
                                      .map(p -> p.toString())
                                      .forEach(files::add);
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return files;
  }

  /**
   * Returns the last three characters of a given file.  This does
   * not include the file extension.
   *
   * @param fileName a string of the complete file path
   * @return a three brand suffix
   */   
  public static String getFileSuffix(String fileName) {
    Pattern pattern = Pattern.compile("(?<=-)([A-Z]*)(?=\\.)");
    Matcher matcher = pattern.matcher(fileName);
    if (matcher.find()) {
      return matcher.group(1);
    }
    
    return "XXX";
  }  
  
}