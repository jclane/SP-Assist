package me.justinlane.gui;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.ProgressMonitor;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingWorker;

import me.justinlane.enums.SearchTypeEnum;
import me.justinlane.util.Brands;
import me.justinlane.util.FindBadClassesTask;
import me.justinlane.util.SearchTask;
import me.justinlane.util.Task;
import me.justinlane.simplecsv.CSVReader;
import me.justinlane.layout.SpringUtilities;

/**
 * The initial screen seen by the user when the application is opened.
 *
 * @author Justin Lane
 * @version 4.2
 * @since 4.0
 */
public class MainMenu implements ActionListener, PropertyChangeListener {

  JFrame frame;
  JCheckBoxMenuItem editableCheckBox;
  JLabel searchTypeLabel;
  JLabel brandLabel;
  JLabel searchFieldLabel;
  JComboBox<String> searchTypeOptionBox;
  JComboBox<String> brandOptionBox;
  JTextField searchField;
  JButton searchButton;
  Task searchTask;
  ProgressMonitor pm; 
  
  /**
   * Creates and displays the application GUI.
   */  
  public void createAndShowGUI() {
    frame = new JFrame();
    frame.setLayout(new BorderLayout(5, 2));
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       
    JMenuBar menuBar = new JMenuBar();
    JMenu fileMenu = new JMenu("File");    
    menuBar.add(fileMenu);
    JMenuItem findBadClasses = new JMenuItem("Find Bad Classes");
    findBadClasses.addActionListener(this);
    fileMenu.add(findBadClasses);
    editableCheckBox = new JCheckBoxMenuItem("Allow Edits");
    fileMenu.add(editableCheckBox);
    frame.setJMenuBar(menuBar);    
       
    searchTypeLabel = new JLabel("Search:");
    String[] searchTypeOptions = {"Part Number", "Part Description", 
                              "Part Class", "All"};
    searchTypeOptionBox = new JComboBox<String>(searchTypeOptions);

    brandLabel = new JLabel("Brand:");
    String[] brandOptions = {"Acer", "Apple", "Asus", "Dell/Alienware",
                             "Hewlett Packard", "Lenovo", "Samsung", "Sony", 
                             "Toshiba", "iBuyPower", "MSI", "CypberPowerPC", 
                             "Razer", "Canada", "All"};
    brandOptionBox = new JComboBox<String>(brandOptions);

    JLabel searchFieldLabel = new JLabel("Find:");
    searchField = new JTextField(10);

    TitledBorder titledBorder = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), "Main Menu");
    titledBorder.setTitleJustification(TitledBorder.CENTER);

    JPanel panel = new JPanel(new SpringLayout());

    panel.setBorder(titledBorder);
    panel.add(searchTypeLabel);
    panel.add(searchTypeOptionBox);
    panel.add(brandLabel);
    panel.add(brandOptionBox);
    panel.add(searchFieldLabel);
    panel.add(searchField);

    SpringUtilities.makeCompactGrid(panel,
                                    3, 2,
                                    6, 6, 
                                    6, 6);
    
    JPanel buttonPanel = new JPanel();
    searchButton = new JButton("Search");
    searchButton.addActionListener(this);
    buttonPanel.add(searchButton);
    
    frame.getContentPane().add(panel, BorderLayout.PAGE_START);
    frame.getContentPane().add(buttonPanel, BorderLayout.PAGE_END);
    frame.pack();
    frame.setVisible(true);
  }
  
  /**
   * Overrides the actionPerformed event and listens for the action command
   * "Find Bad Classes" or "Search", after which the relevant method is called.
   * 
   * @param e the action event that triggered this
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    switch (e.getActionCommand()) {
      case "Find Bad Classes": 
          this.searchTask = new FindBadClassesTask();
          if (this.searchTask.getNumberOfFiles() > 0) {
            this.searchTask.addPropertyChangeListener(this);
            this.pm = new ProgressMonitor(this.frame, "Searching...",
                              "", 0, 100);
            this.pm.setMillisToPopup(0);
            this.pm.setMillisToDecideToPopup(0);            
            this.searchTask.execute();
          }            
          break;
      case "Search":
          Pattern searchQuery = Pattern.compile(searchField.getText(), Pattern.CASE_INSENSITIVE);
          String brand = brandOptionBox.getSelectedItem()
                                       .toString();
          SearchTypeEnum searchType = SearchTypeEnum.valueOf(searchTypeOptionBox.getSelectedItem()
                                                              .toString()
                                                              .toUpperCase()
                                                              .replace(" ", "_"));
          
          this.searchTask = new SearchTask(searchQuery, brand, searchType);
          if (this.searchTask.getNumberOfFiles() > 0) {
            this.searchTask.addPropertyChangeListener(this);
            this.pm = new ProgressMonitor(this.frame, "Searching...",
                              "", 0, 100);
            this.pm.setMillisToPopup(500);
            this.pm.setMillisToDecideToPopup(500);            
            this.searchTask.execute();
          }
          break;
      default:
          ErrorPopUp.showError("Invalid Option",
                               String.format("%s is not a valid option.",
                               e.getActionCommand()));
    }
  }
  
  /**
   * Overrides the propertyChange event and listens for changes to the 
   * progress property.  When triggered this will update
   * the progressMonitor.
   *
   * @param evt is the property change event that triggered this
   */  
  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getPropertyName().equals("progress")) {
      int progress = (int) evt.getNewValue();
      this.pm.setProgress(progress);
      this.pm.setNote(String.format("Now searching file %s of %s", 
                                    String.valueOf(this.searchTask.getCurrentFileNum()), 
                                    String.valueOf(this.searchTask.getNumberOfFiles()))
                                    );
      if (this.pm.isCanceled() || this.searchTask.isDone()) {
        if (this.pm.isCanceled()) {
            this.searchTask.cancel(true);
        } else {
            this.pm.close();
            ResultsScreen resultsScreen = new ResultsScreen(this.frame, ModalityType.APPLICATION_MODAL, this.searchTask.getResults(), this.editableCheckBox.getState());
        }
      }
    }
  }
    
}