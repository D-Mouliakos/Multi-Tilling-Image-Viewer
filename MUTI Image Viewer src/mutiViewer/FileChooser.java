package mutiViewer;

/**
 * @author Dimitris Mouliakos
 */


import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;


/**
 * Provides a native operating system file selection dialog
 * and converts selected image files into BufferedImage objects.
 *
 * Design choice:
 * Uses AWT FileDialog instead of JFileChooser to leverage the
 * platform-native file explorer for better OS integration and UX.
 *
 * Intended usage:
 * - Allow user to select one or multiple image files
 * - Load selected files into memory for display in the viewer
 */
public class FileChooser {

    /**
     * Invisible parent frame required by AWT FileDialog.
     * The dialog is modal relative to this frame.
     */
    private Frame parent;

    
    //==========================================================================
    // CONSTRUCTOR
    //==========================================================================
    
    /**
     * Initializes the file chooser with a minimal parent frame.
     * No UI is displayed for this frame — it exists only to
     * anchor the native file dialog.
     */
    public FileChooser() {
        parent = new Frame(); // needed for FileDialog
    }

    
    //==========================================================================
    // METHODS & OPPERATIONS
    //==========================================================================
    
    /**
     * Opens the native file selection dialog.
     *
     * Behavior:
     * - Allows multiple file selection
     * - Returns an empty array if the user cancels
     *
     * @return Array of selected File objects (never null)
     */
    public File[] showDialog() {
        FileDialog dialog = new FileDialog(parent, "Select files", FileDialog.LOAD);
        dialog.setMultipleMode(true); // allow multi-selection
        dialog.setVisible(true);

        File[] files = dialog.getFiles(); // returns an array of selected files
        return files != null ? files : new File[0];
    }


    /**
     * Loads selected files as BufferedImage objects.
     *
     * Processing notes:
     * - Files that cannot be decoded as images are skipped
     * - Errors are ignored to prevent UI interruption
     *
     * This method is designed for fast batch loading of images
     * into the viewer pipeline.
     *
     * @return List of successfully loaded images
     */
    public List<BufferedImage> getSelectedImages() {
        File[] files = showDialog();
        List<BufferedImage> images = new ArrayList<>();
        for (File f : files) {
            try {
                BufferedImage img = ImageIO.read(f);
                if (img != null) images.add(img);
            } catch (Exception ignored) {
                // Failed image reads are skipped intentionally
            }
        }
        return images;
    }
}