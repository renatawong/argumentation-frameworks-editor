
package io;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * Filtering of framework data files for opening and saving
 * @author Renata Wong
 */
public class FrameworkFileFilter extends FileFilter {

    @Override
    public boolean accept(File file) {
        return (file.isDirectory() ||
                file.getName().toLowerCase().endsWith(".aaf") ||
                file.getName().toLowerCase().endsWith(".net"));
    }

    @Override
    public String getDescription() {
        return "Abstract Argumentation Framework (*.aaf, *.net)";
    }
}
