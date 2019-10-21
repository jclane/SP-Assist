package me.justinlane.util;

import java.lang.InterruptedException;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import me.justinlane.enums.SearchTypeEnum;
import me.justinlane.util.UsefulFunc;

/** 
 * This class handles the actual searching of files.
 *
 * @author Justin Lane
 * @version 4.2
 * @since 4.0
 */
public class SearchTask extends Task {

  private Pattern query;
  
  /**
   * Constructor.
   * 
   * @param query (required) Pattern representing what to search for
   * @param brand (required) String representing which brand we're searching
   * @param searchType (required) SearchTypeEnum representing what type of search to perform
   */  
  public SearchTask(Pattern query, String brand, SearchTypeEnum searchType) {
    super(brand, searchType);
    this.query = query;
  }
  
  /**
   * Returns query.
   */
  public Pattern getQuery() {
    return this.query;
  }
      
  /**
   * Checks if query can be found in lineToCompare depending on 
   * the searchTypeInt provided.
   * 
   * <ol>
   *    <li>0 = Part Class</li>
   *    <li>1 = Part Number</li>
   *    <li>2 = Part Description</li>
   *    <li>3 = All of the above</li>
   * </ol>
   *
   * @param searchTypeInt an interger indicating which part of lineToCompare to search
   * @param query a pattern indicating what to search for
   * @param lineToCompare a list of string values to be checked
   * @return a boolean indicating if query was found in lineToCompare
   */
  private static boolean queryFound(int searchTypeInt, Pattern query, List<String> lineToCompare) {
    if (searchTypeInt < 3) {
      Matcher m = query.matcher(lineToCompare.get(searchTypeInt).toString());
      return m.find();
    } else {
      Matcher m = query.matcher(lineToCompare.toString());
      return m.find();
    }

  }  

  /**
   * Uses UsefulFunc.readFile to to get the lines from the file.  Then for each line, 
   * queryFound is called to check if the lines contains the query.  If so the file 
   * path and line are added to the results list, which is later returned.
   *
   * @param query a pattern indicating what to search for
   * @param brand a string indicating the brand the user has chosen
   * @param searchType a string indicating which part of the list of the list to search
   * @return results a list containing strings including the file path and specific line
   *                  that matched the query
   */
  @Override
  public List<List<String>> processFile(Path filePath) {
    List<List<String>> results = new ArrayList<List<String>>();
    List<List<String>> csvFile = UsefulFunc.readFile(filePath);
    csvFile.removeIf(el -> el.size() < 3);
    for (List<String> line : csvFile) {
      if (queryFound(getSearchTypeInt(), getQuery(), line)) {
        line.add(0, filePath.getParent().toString());
        line.add(1, filePath.getFileName().toString());
        results.add(line);
      }
    }
    return results;
  }
    
  /**
   * {@inheritDoc}
   */  
  @Override
  public void done() {
    setProgress(0);
  }

}