package me.justinlane.util;

import java.nio.file.Path;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import java.util.ArrayList;
import java.util.List;

import me.justinlane.enums.SearchTypeEnum;
import me.justinlane.util.UsefulFunc;

public class Task extends SwingWorker<Void, Void> {

  private List<List<String>> results;
  private SearchTypeEnum searchType;
  private int searchTypeInt;
  private List<Path> paths;
  private int numberOfFiles;
  private int currenFileNum = 0;
  private int previousFileNum = 0;
  private long timeToProcess = 0;

  public Task() {
    this.results = new ArrayList<List<String>>();
    this.searchType = SearchTypeEnum.ALL;
    this.searchTypeInt = 3;
    this.paths = UsefulFunc.getFiles("All");
    this.numberOfFiles = paths.size();    
  }
  
  public Task(String brand, SearchTypeEnum searchType) {
    this.results = new ArrayList<List<String>>();
    this.searchType = searchType;
    this.searchTypeInt = searchType.getSearchInt();
    this.paths = UsefulFunc.getFiles(brand);
    this.numberOfFiles = this.paths.size();
  }

  /**
   * Returns searchTypeInt.
   */  
  public int getSearchTypeInt() {
    return this.searchTypeInt;
  }
  
  /**
   * Returns results.
   */  
  public List<List<String>> getResults() {
    return this.results;
  }  
  
  /**
   * Returns paths.
   */  
  public List<Path> getPaths() {
    return this.paths;
  }
  
  /**
   * Returns numberOfFiles.
   */
  public int getNumberOfFiles() {
    return this.numberOfFiles;
  }
  
  /**
   * Returns the number of the file currently being processed.
   */
  public int getCurrentFileNum() {
    return this.currenFileNum;
  }

  /**
   *
   */
  public long getTimeToProcess() {
    return this.timeToProcess;
  }

  /**
   * Base method meant to overriden.
   */
  public List<List<String>> processFile(Path filePath){
    return new ArrayList<List<String>>();
  };

  /**
   * Loops through result of <code>getFilePaths</code> and in a background
   * thread and calls <code>processFile</code> on each result.
   *
   * @return null
   */  
  @Override
  public Void doInBackground() {
    this.results = new ArrayList<List<String>>();
    int progress = 0;
    setProgress(0);
        
    while (progress < this.numberOfFiles && !isCancelled()) { 
      for (Path filePath : this.paths) {
        if (!isCancelled()) {
          long timeStart = System.currentTimeMillis();
          List<List<String>> processed = processFile(filePath);
          if (processed.size() > 0) {
            this.results.addAll(processed);
          }
          this.currenFileNum += 1;
          progress += 1;
          setProgress(100 * progress / this.numberOfFiles);
          try {
              Thread.sleep(100);
              this.timeToProcess = System.currentTimeMillis() - timeStart;
          } catch (InterruptedException ignore) {}
        } else {
          break;
        }
      }
    }

    return null;
  }

}
