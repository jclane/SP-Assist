package me.justinlane.gui.event;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;

/**
 * Listens for double click on jtable.
 * 
 * @author tmn (stackoverflow.com)
 * @author Justin Lane
 * @version 4.1
 * @since 4.0
 */
public class ResultTableMouseListener extends MouseAdapter {
  
  private JTable table;
  
  /**
   * Constructor.
   */  
  public ResultTableMouseListener() {
    this.table = new JTable();
  }
  
  /**
   * Constructor.
   *
   * @param table JTable that this is listening too
   */
  public ResultTableMouseListener(JTable table) {
    this.table = table;
  }
      
  /**
   * When user clicks on any row in column one of resultsTable the 
   * contents of that row will be copied to the clipboard.
   *
   * @see <a href=https://stackoverflow.com/questions/25917580/make-jtable-cell-editor-value-be-selectable-but-not-editable />
   */
  @Override
  public void mouseClicked(MouseEvent e) {
    if (e.getClickCount() == 2) {
      Point p = e.getPoint();
      int col = table.columnAtPoint(p);
      if (col == 0) {
        int row = table.rowAtPoint(p);
        Object value = table.getValueAt(row, col);
        StringSelection stringSelection = new StringSelection(value.toString());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
      }
    }
  }

}