package me.justinlane.util;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import me.justinlane.gui.ErrorPopUp;
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
   * @param path type Path representing the path and file name to write
   * @param data a list containing a lists of strings to write 
   */
  public static void writeFile(Path path, List<List<String>> data) {
    try {
      CSVWriter.writeLines(path, data);
    } catch (IOException e) {
      ErrorPopUp.showError("Write Error", "Path not found.");
      e.printStackTrace();
    }
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
      ErrorPopUp.showError("Write Error", "Path not found.");
      e.printStackTrace();
    }
  }
  
  /**
   * Uses simplecsv.CSVReader to read and parse CSV files.
   *
   * @param path a Path object representing the path and file name to read
   * @return a list containing a lists of strings representing the data 
   */
  public static List<List<String>> readFile(Path path) {
    List<List<String>> data = new ArrayList<List<String>>();
    try {
      data.addAll(CSVReader.readLines(path));
    } catch (FileNotFoundException e) {
      ErrorPopUp.showError("Read Error", "File not found.");
    } finally {
      return data;
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
      ErrorPopUp.showError("Read Error", "File not found.");
    } finally {
      return data;
    }
  }

  /**
   * Takes a filename and returns the extension.
   *
   * @param fileName a string representing a file name.
   * @return a string representing the extension or
   *      an empty string.
   * @see <a href:https://www.baeldung.com/java-file-extension />
   */
  public static Optional<String> getExtension(String filename) {
    return Optional.ofNullable(filename)
      .filter(f -> f.contains("."))
      .map(f -> f.substring(filename.lastIndexOf(".") + 1));
  }
  
  /**
   * Uses brand to obtain the path to that brand's folder.
   * The path is walked and results are filtered to store
   * only those brands matching the correct suffix.
   *
   * @param brand a string indicating the users chosen brand
   * @return a list of file paths as type Path
   */
  public static List<Path> getFiles(String brand) {
    final Brands brands = new Brands();
    final List<String> VALID_BRANDS = Arrays.asList(brands.getBrandSuffix(brand));
    Path rootDir = brands.getBrandDir(brand);
    List<Path> files = new ArrayList<Path>();
    List<String> validPaths = new ArrayList<String>();
    
    if (brand.equals("All")) {
      validPaths.add(Paths.get("//VSP021320/GSC-Pub/BOM Squad/BOM-Smart Parts",
                               "Canada").toString());
      
      brands.getBrandDirs().forEach( (k, v) -> {
        if (!k.equals("All") || !k.equals("Sony") || !k.equals("Toshiba")) {
          validPaths.add(v.toString());
        }
      });
    }
    
    try {
      Files.walkFileTree(rootDir, new SimpleFileVisitor<Path>() {
        
        @Override
        public FileVisitResult preVisitDirectory(Path dir,
                BasicFileAttributes attrs) {
          boolean worthVisiting = true;
          if (brand.equals("All")) {
            if (dir != rootDir) {
              for (int i = 0; i < validPaths.size(); i++) {
                if (dir.toString().startsWith(validPaths.get(i))) {
                  worthVisiting = true;
                  break;
                } else if (i == validPaths.size() - 1 && !dir.toString().startsWith(validPaths.get(i))) {
                  worthVisiting = false;
                }
              }
            }
          }
            
          return (worthVisiting) ? FileVisitResult.CONTINUE : FileVisitResult.SKIP_SUBTREE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
          String fileName = file.getFileName().toString();

          if (getExtension(fileName).get().equals("csv") &&
              VALID_BRANDS.contains(getFileSuffix(fileName))) {
            files.add(file);
          }
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException e) {
            ErrorPopUp.showError("File Not Found",
                                 String.format("%s was not found.", 
                                               file.getFileName().toString()));
            return FileVisitResult.CONTINUE;
        }
      });
    } catch (IOException e) {
        ErrorPopUp.showError("File Not Found",
                             String.format("%s was not found.", 
                                           rootDir.getFileName().toString()));
    }
    
    return files;
  }

  /**
   * Returns the last three characters of a given file.  This does
   * not include the file extension.
   *
   * @param fileName a string of the complete file path
   * @return a string with the three letter brand suffix
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
