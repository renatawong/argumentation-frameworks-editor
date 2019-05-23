
package io;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * Creates an image filter for exporting images
 * @author Renata Wong
 */
public class FrameworkImageFilter extends FileFilter {

    @Override
    public boolean accept(File file) {
        return (file.isDirectory() ||
                file.getName().toLowerCase().endsWith(".gif") ||
                file.getName().toLowerCase().endsWith(".jpg") ||
                file.getName().toLowerCase().endsWith(".png"));
    }

    @Override
    public String getDescription() {
        return "Abstract Argumentation Framework Image (*.gif, *.jpg, *.png)";
    }

}