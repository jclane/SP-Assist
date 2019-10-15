package me.justinlane.util;

import java.io.File;
import java.io.IOException;

import java.lang.InterruptedException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import me.justinlane.gui.ErrorPopUp;
import me.justinlane.util.UsefulFunc;

/**
 * Used to search all files on the shared drive for invalid 
 * part classes.
 * 
 * @author Justin Lane
 * @version 4.0
 * @since 4.0
 */
public class FindBadClassesTask extends Task {
    
  /**
   * Constructor.
   */
  public FindBadClassesTask() {
    super();
  }
  
  /**
   * Loops through lines of <code>filePath</code> where <code>classIsValid</code>
   * is called. If it returns <code>false</code> then the line, file path, 
   * file name, and the creator of the file is added to a list.
   * 
   * @param filePath string representing the complete file path
   * @returns processed as a list of file with invalid classes
   */
  @Override
  public List<List<String>> processFile(String filePath) {
    List<List<String>> processed = new ArrayList<List<String>>();
    List<List<String>> csvFile = UsefulFunc.readFile(filePath);
    csvFile.removeIf(el -> el.size() < 3);
    for (List<String> line : csvFile) {
      if (!UsefulFunc.classIsValid(line.get(0))) {
        Path path = Paths.get(filePath);
        String fileName = path.getFileName().toString();
        String creator = "";
        
        try {
          creator += Files.getOwner(path).toString();
        } catch (IOException e) {
          ErrorPopUp.showError("File Not Found",
                               String.format("%s could not be found!",
                               fileName));
        }
        
        line.add(0, path.getParent().toString());
        line.add(1, fileName);          
        line.add(creator);
        processed.add(line.subList(0, 5));
      }

    }
      
    return processed;    
  }
  
  /**
   * Once <code>doInBackground</code> is done this will save the results 
   * to a *.CSV file and progress will be set to 0.
   */  
  @Override
  public void done() {
        
    if (this.getResults().size() > 0) {
      UsefulFunc.writeFile("BAD_CLASSES.csv", this.getResults());
    }
    
    setProgress(0);    
  }
  
}