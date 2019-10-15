package me.justinlane.gui;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import me.justinlane.util.UsefulFunc;

/**
 * Table model for the results screen table.
 *
 * @author Justin Lane
 * @version 4.0
 * @since 4.0
 */
public class ResultsScreenTableModel extends AbstractTableModel {
  
  private String[] columnNames;
  private List<List<String>> results;
  private boolean editable;
    
  /**
   * Sets the results of the table.
   * 
   * @param results the list of lists representing the table data
   */
  public ResultsScreenTableModel(List<List<String>> resultsList, boolean editable) {
    super();
    results = resultsList;
    this.editable = editable;
    columnNames = new String[]{"Path", "File Name", "Class", "Part Number", "Description"};
  }    

  /**
   * {@inheritDoc}
   */
  @Override  
  public String getColumnName(int col) {
        return columnNames[col].toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getColumnCount() {
    return columnNames.length;
  }

  /**
   * {@inheritDoc}
   */
  @Override  
  public int getRowCount() {
    return results.size();
  }

  /**
   * {@inheritDoc}
   */
  @Override  
  public Object getValueAt(int row, int col) {
    return results.get(row).get(col);
  }
  
  /**
   * {@inheritDoc}
   */
  @Override  
  public Class getColumnClass(int c) {
      return getValueAt(0, c).getClass();
  }  
  
  /**
   * Sets the value in the table at <code>row</code> and <code>col</code>.
   * Additionally, <code>saveChangesToFile</code> is called to save the changes
   * to the appropriate *.CSV file.
   * 
   * @param row integer representing the row number in the table
   * @param col integer representing the column number in the table
   */  
  @Override
  public void setValueAt(Object newValue, int row, int col) {
    List<String> line = results.get(row);
    List<String> oldLine = new ArrayList<String>();
    oldLine.addAll(line);
    
    switch (col) {
      case 1:
          line.set(1, newValue.toString());
          break;
      case 2:
          line.set(2, newValue.toString());
          break;
      case 3:
          line.set(3, newValue.toString());
          break;
      case 4:
          line.set(4, newValue.toString());
          break;
    }
    
    saveChangesToFile(oldLine, line);
    fireTableCellUpdated(row, col);
  }  


  /**
   * Returns whether the cell at <code>row</code> and <code>col</code> is editable.
   * More specifically, if the <code>editable</code> property of a given cell is set
   * to false or <code>col</code> is less than 2 this will return <code>false</code>.
   * Otherwise, it returns <code>true</code>.
   * 
   * @param row integer representing the row number in the table
   * @param col integer representing the column number in the table
   */
  @Override  
  public boolean isCellEditable(int row, int col) {
    if (!editable || col < 2) {
        return false;
    }
    
    return true;
  }
    
  /**
   * Saves changes made in the table to the appropriate *.CSV file.
   *
   * @param oldValues the list of original string values of the row
   * @param newValues the list of new string values of the row
   */
  public void saveChangesToFile(List<String> oldValues, List<String> newValues) {
    Path filePath = Paths.get(oldValues.get(0), oldValues.get(1));
    List<String> oldLine = new ArrayList<String>(Arrays.asList(oldValues.get(2), oldValues.get(3), oldValues.get(4)));  
    List<String> newLine = new ArrayList<String>(Arrays.asList(newValues.get(2), newValues.get(3), newValues.get(4)));   
    List<List<String>> lines = UsefulFunc.readFile(filePath.toString());
    
    if (lines.contains(oldLine)) {
      int index = lines.indexOf(oldLine);
      lines.set(index, newLine);
      UsefulFunc.writeFile(filePath.toString(), lines);
    }
    
  }  
       
}