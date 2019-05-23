
package io;

import edu.uci.ics.jung.graph.DirectedGraph;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import visualization.Argument;
import visualization.Attack;
import visualization.FrameworkViewer;




/**
 * FileSelector for opening and saving framework data files
 * @author Renata Wong
 */
public class FrameworkFileSelector extends JFileChooser {

    FrameworkFileFilter filter = new FrameworkFileFilter();
    FrameworkImageFilter imageFilter = new FrameworkImageFilter();
    

    /**
     * Constructor
     */
    public FrameworkFileSelector() {}


    /**
     * Opens dialog for choosing framework data files
     * @return status: 0 for "OK", 1 for "CANCEL"
     */
    public int openFile() {

        setCurrentDirectory(new File("."));
        setFileFilter(filter);
        setApproveButtonText("Open Framework");
        setDialogTitle("Open Framework");

        int result = showOpenDialog(this);

        if(result == JFileChooser.APPROVE_OPTION) {

            try {
                File file = getSelectedFile();
                String extension = file.toString();
                extension = extension.substring(extension.lastIndexOf("."), extension.length());

                if(extension.equals(".aaf") | extension.equals(".net"))
                    return 0;
                else return 2;
            } catch (Exception ex) {
                return 2;
            }

        }
        else if(result == JFileChooser.CANCEL_OPTION)
            return 1;
        else return 0;
    }



    /**
     * Saves the content of the framework into a file
     * @return status: 0 for "save", 1 for "cancel", 2 for "error"
     */
    public int saveFile () {

        File file = null;

        setCurrentDirectory(new File("."));
        setFileFilter(filter);
        setApproveButtonText("Save Framework");
        setDialogTitle("Save Framework");
        
        setSelectedFile(file);

        int result = showSaveDialog(this);

        if(result == JFileChooser.CANCEL_OPTION) {
            return 1;
        }
        else if(result == JFileChooser.APPROVE_OPTION) {
            file = getSelectedFile();
            if(file.exists()) {
                int response = JOptionPane.showConfirmDialog(null,
                        "Overwrite existing file?","Confirm Overwrite",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if(response == JOptionPane.CANCEL_OPTION) return 1;
            }
            String extension = file.toString();
            try {
                extension = extension.substring(extension.lastIndexOf("."), extension.length());
                return writeFile(file, FrameworkViewer.getFileContent(extension));
            } catch (Exception e) {
                return 2;
            }

        } else {
            return 0;
        }
    }


    
  /**
   * Writes the framework data into a file
   * @param file framework data file name (to be chosen)
   * @param dataString content
   * @return status of the operation: 0 for "OK", 2 for "error"
   */
  public static int writeFile(File file, String dataString) {

      try {
          PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
          out.print(dataString);
          out.flush();
          out.close();
      } catch (IOException ioe) {
          return 2;
      }
      return 0;
  }




  /**
   * Control method for exporting the image of a framework
   * @param graph the underlying graph
   * @return 0 if export approved, 1 if export was cancelled, 2 if error occurred
   */
  public int exportImage(DirectedGraph<Argument, Attack> graph) {

        File file = null;

        setCurrentDirectory(new File("."));
        setFileFilter(imageFilter);
        setApproveButtonText("Export Framework");
        setDialogTitle("Export Framework as Image");

        setSelectedFile(file);

        int result = showSaveDialog(this);

        if(result == JFileChooser.CANCEL_OPTION) {
            return 1;
        } else if(result == JFileChooser.APPROVE_OPTION) {
            file = getSelectedFile();
            if(file.exists()) {
                int response = JOptionPane.showConfirmDialog(null,
                        "Overwrite existing file?","Confirm Overwrite",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if(response == JOptionPane.CANCEL_OPTION) return 1;
            }

            int width = FrameworkViewer.getVisualizationViewer().getGraphLayout().getSize().width;
            int height = FrameworkViewer.getVisualizationViewer().getGraphLayout().getSize().height;
            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
            Graphics2D graphics = img.createGraphics();
            graphics.fillRect(0, 0, width, height);
            FrameworkViewer.getVisualizationViewer().setBounds(0, 0, width, height);
            FrameworkViewer.getVisualizationViewer().paint(graphics);

            boolean transfer = false;

            String extension = file.toString();
            extension = extension.substring(extension.lastIndexOf(".")+1, extension.length());

            try {
                transfer = ImageIO.write(img, extension, file);
            } catch (IOException ex) {
                Logger.getLogger(FrameworkFileSelector.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(transfer) return 0;
            else return 2;
        } else {
            return 0;
        }
    }


}