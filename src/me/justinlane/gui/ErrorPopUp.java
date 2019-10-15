package me.justinlane.gui;

import javax.swing.JOptionPane;

/**
 * A simple class for show an error message popup.
 *
 * @author Justin Lane
 * @version 4.2
 * @since 4.2
 */
public class ErrorPopUp extends JOptionPane {
  
 /**
  * Opens a dialog window with information indicating an error has
  * occured.
  *
  * @param title string used as the title for the dialog window 
  * @param msg string with details of the error that occured
  */
  public static void showError(String title, String msg) {
      
      showMessageDialog(null, 
                        title, 
                        msg, 
                        JOptionPane.ERROR_MESSAGE);
  
  }
  
}