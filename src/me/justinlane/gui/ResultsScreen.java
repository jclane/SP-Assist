package me.justinlane.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import me.justinlane.gui.ResultsScreenTableModel;
import me.justinlane.gui.event.ResultTableMouseListener;
import me.justinlane.util.UsefulFunc;

/**
 * This screen is shown after a Search has been completed.
 *
 * @author Justin Lane
 * @version 4.0
 * @since 4.0
 */
public class ResultsScreen implements ActionListener {
  
  List<List<String>> results;
  
  /**
   * This contructor sets the parent JFrame, modality type, and results list.
   *
   * @param parent the jframe the table will be added to
   * @param modality the modality type desired for the window
   * @param results the list of lists representing the table data
   */   
  public ResultsScreen(JFrame parent, ModalityType modality, List<List<String>> results, boolean editable) {
    this.results = results;
    JDialog window = new JDialog(parent, "Search results...", modality);
    window.setLayout(new BorderLayout(10, 10));
    window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    
    JTable resultsTable = new JTable(new ResultsScreenTableModel(results, editable));
    resizeColumnWidth(resultsTable);
    resultsTable.setRowHeight(20);
    resultsTable.putClientProperty("terminateEditOnFocusLost", true);   
    
    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
    TableColumn partClassColumn = resultsTable.getColumnModel().getColumn(2);
    partClassColumn.setCellRenderer(centerRenderer);
    JComboBox<String> comboBox = new JComboBox<String>();
    ((JLabel) comboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
    
    for (String c : UsefulFunc.getValidClasses()) {
      comboBox.addItem(c);
    }
    
    partClassColumn.setCellEditor(new DefaultCellEditor(comboBox));
      
    resultsTable.removeColumn(resultsTable.getColumn("Path"));
    resultsTable.setFillsViewportHeight(true);
    resultsTable.addMouseListener(new ResultTableMouseListener(resultsTable));
    JScrollPane scrollPane = new JScrollPane(resultsTable);
    window.add(scrollPane, BorderLayout.CENTER); 

    JButton saveButton = new JButton("Save Results");
    Dimension btnSize = new Dimension(150, 35);
    saveButton.setPreferredSize(btnSize);
    saveButton.addActionListener(this);
    JPanel buttonPanel = new JPanel();
    buttonPanel.add(saveButton);

    window.add(buttonPanel, BorderLayout.SOUTH);
    
    window.pack();
    window.setVisible(true);
  }
  
  /**
   * Listens for the "Save" action command and then saves the results to
   * a *.CSV file.
   * 
   * @param e the action event that triggered this
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    switch (e.getActionCommand()) { 
      case "Save":
          UsefulFunc.writeFile("Search_Results.csv", results);
          break;
    }   
  }
  
  /**
   * Sizes column width so it only takes up as much space as needed.
   * 
   * @param table the table to set the column sizes of
   * @see <a href=https://stackoverflow.com/questions/17627431/auto-resizing-the-jtable-column-widths />
   */  
  public void resizeColumnWidth(JTable table) {
    final TableColumnModel columnModel = table.getColumnModel();
    for (int column = 0; column < table.getColumnCount(); column++) {
        int width = 100; // Min width
        for (int row = 0; row < table.getRowCount(); row++) {
            TableCellRenderer renderer = table.getCellRenderer(row, column);
            Component comp = table.prepareRenderer(renderer, row, column);
            width = Math.max(comp.getPreferredSize().width + 1 , width);
        }
        if(width > 400)
            width=400;
        columnModel.getColumn(column).setPreferredWidth(width);
    }
  }
 
}