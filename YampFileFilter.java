package yamp4;
import java.io.*;
import javax.swing.filechooser.*;

public class YampFileFilter extends javax.swing.filechooser.FileFilter{
    
    private String extension;
    private String description;
    
    public YampFileFilter(String extension, String description) {
        this.extension = extension;
        this.description = description;
    }

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        return f.getName().endsWith(extension);
    }

    @Override
    public String getDescription() {
        return description + String.format(" (*%s)", extension);
    }
    
}
