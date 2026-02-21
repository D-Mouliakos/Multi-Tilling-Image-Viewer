package mutiViewer;

/**
 * @author Dimitris Mouliakos
 */


import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.*;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.Collections;

import enums.ResizeMode;
import enums.MoveDirection;


/**
 * Main application controller for the Multi Image Viewer.
 *
 * Responsibilities:
 * - Builds and manages the main UI
 * - Maintains the collection of displayed images
 * - Calculates dynamic grid layout based on window size and image count
 * - Handles user interaction (add, remove, move, select images)
 *
 * Layout strategy:
 * Images are arranged in a resolution-aware grid that automatically
 * adapts to window resizing and display DPI differences.
 *
 * Rendering model:
 * Each image is an independent DynamicImage component that manages
 * its own scaling, zooming, and interaction state.
 */
public class MutiViewer {
    
    //==========================================================================
    // VARIABLES
    //==========================================================================
    /** Container storing all active image viewer components */
    final static List<DynamicImage> images = new ArrayList<>();

    /** Index of currently selected image (-1 when none selected) */
    private static int selectedIndex = -1;

    /** Maximum number of images allowed per row */
    private static int maxImagesPerLine = 3;

    /** Current number of images loaded in the viewer */
    private static int imageNum = 0;
    
    /** Actual number of columns currently used in layout */
    private static int columns;

    /** Calculated width for each image panel */
    private static int sizeX;

    /** Calculated height for each image panel */
    private static int sizeY;
    
    
    //==========================================================================
    // CONSTRUCTOR
    //==========================================================================
    
    /**
     * Initializes scaling system and builds UI.
     */
    public void MutiViewer(){    
        UIScaler.initialize(); 
        UIBuilder();
    }
    
    
    //==========================================================================
    // BUILDS THE USER INTERFACE (WITH IT'S LISTENERS)
    //==========================================================================
    
    /**
     * Creates the main application window, UI controls, and event listeners.
     *
     * The UI uses absolute positioning combined with resolution-aware scaling
     * to maintain consistent layout across different display configurations.
     */
    private void UIBuilder(){
        Toolkit.getDefaultToolkit().setDynamicLayout(true);
        
        // Main application frame
        frame = new JFrame("MUTI Image Viewer");
        frame.setSize(UIScaler.scaleWidth(960, ResizeMode.RESOLUTION), UIScaler.scaleHeight(540, ResizeMode.RESOLUTION));
        frame.setLayout(null); // allows absolute positioning
        frame.getContentPane().setBackground(new Color(80, 80, 80)); // dark gray
        frame.setMinimumSize(new Dimension(UIScaler.scaleWidth(640, ResizeMode.RESOLUTION),UIScaler.scaleHeight(480, ResizeMode.RESOLUTION)));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Root layered container
        layer = new JLayeredPane();
        layer.setSize(UIScaler.scaleWidth(960, ResizeMode.RESOLUTION),UIScaler.scaleHeight(540, ResizeMode.RESOLUTION));
        frame.add(layer);
        
        // Background panel
        JPanel panel = new JPanel();
        panel.setSize(UIScaler.scaleWidth(960, ResizeMode.RESOLUTION), UIScaler.scaleHeight(540, ResizeMode.RESOLUTION));
        panel.setBackground(new Color(80, 80, 80)); // dark gray
        panel.setOpaque(true);
        layer.add(panel, Integer.valueOf(0));
        

        
        // Menu bar image
        ImageIcon menuBar = new ImageIcon(getClass().getResource("/resources/Menu_Bar.png"));
        menuBarLabel = new JLabel();
        menuBarLabel.setLocation(0, 0);
        menuBarLabel.setSize(UIScaler.getResolutionX(), UIScaler.scaleHeight(menuBar.getIconHeight(), ResizeMode.RESOLUTION));
        
        // Scales the image to fit the window & resolution
        Image scaled = menuBar.getImage().getScaledInstance(menuBarLabel.getWidth(),
        menuBarLabel.getHeight(),Image.SCALE_SMOOTH);
        
        // Applies the scaled image into the JLabel
        menuBarLabel.setIcon(new ImageIcon(scaled));
        layer.add(menuBarLabel, Integer.valueOf(2));
        
        
        
        //==================================================================
        // CONTROL BUTTONS
        //==================================================================
        // ADD_IMAGE button
        ImageButton addButton = new ImageButton(
            "Add Image",
           "/resources/Button_N.png",
            "/resources/Button_N.png",
            "/resources/Button_L.png",
            "Opens File Explorer to add images"
        );
        addButton.setLocation(UIScaler.scaleWidth(10, ResizeMode.RESOLUTION), 0);
        layer.add(addButton, Integer.valueOf(5) );
        
        
        
        // REMOVE_IMAGE Button
        ImageButton removeButton = new ImageButton(
            "Remove Image",
            "/resources/Button_N.png",
            "/resources/Button_N.png",
            "/resources/Button_L.png",
            "Removes selected image from Viewer"
        );
        removeButton.setLocation(UIScaler.scaleWidth(160, ResizeMode.RESOLUTION), 0);
        layer.add(removeButton, Integer.valueOf(5) );
        
        
        
        // MOVE_IMAGE_lEFT Button
        ImageButton moveLeftButton = new ImageButton(
            "<<  Move Left",
            "/resources/Button_N.png",
            "/resources/Button_N.png",
            "/resources/Button_L.png",
            "Moves selected image Left"
        );
        moveLeftButton.setLocation(UIScaler.scaleWidth(600, ResizeMode.RESOLUTION), 0);
        layer.add(moveLeftButton, Integer.valueOf(5) );
        
        
        
        // MOVE_IMAGE_RIGHT Button
        ImageButton moveRightButton = new ImageButton(
            "Move Right  >>",
            "/resources/Button_N.png",
            "/resources/Button_N.png",
            "/resources/Button_L.png",
            "Moves selected image Right"
        );
        moveRightButton.setLocation(UIScaler.scaleWidth(750, ResizeMode.RESOLUTION), 0);
        layer.add(moveRightButton, Integer.valueOf(5) );
        
        
        
        //==================================================================
        // COLUMN CONTROL
        //==================================================================
        JLabel columnText = new JLabel();
        columnText.setText("Columns");
        columnText.setLocation(UIScaler.scaleWidth(375, ResizeMode.RESOLUTION), 0);
        columnText.setSize(UIScaler.scaleWidth(75, ResizeMode.RESOLUTION), UIScaler.scaleHeight(30, ResizeMode.RESOLUTION));
        columnText.setForeground(Color.WHITE);
        columnText.setFont(new Font("Arial", Font.BOLD, UIScaler.scale(14, ResizeMode.RESOLUTION)));
        layer.add(columnText, Integer.valueOf(5));
        
        // Adds SUBTRACT_COLUMNS Button
        ImageButton reduceColButton = new ImageButton(
            "",
            "/resources/Subtract_Col_N.png",
            "/resources/Subtract_Col_N.png",
            "/resources/Subtract_Col_L.png",
            "Reduces the Maximum number of images in a row"
        );
        reduceColButton.setLocation(UIScaler.scaleWidth(450, ResizeMode.RESOLUTION), 0);
        layer.add(reduceColButton, Integer.valueOf(5) );
        
        // Adds ADD_COLUMNS Button
        ImageButton addColButton = new ImageButton(
            "",
            "/resources/Add_Col_N.png",
            "/resources/Add_Col_N.png",
            "/resources/Add_Col_L.png",
            "Increase the Maximum number of images in a row"
        );
        addColButton.setLocation(UIScaler.scaleWidth(507, ResizeMode.RESOLUTION), 0);
        layer.add(addColButton, Integer.valueOf(5) );
        
        // Adds COLUMN_NUMBER_FIELD image
        ImageIcon columnNumImage = new ImageIcon(getClass().getResource("/resources/Num_Field.png"));
        
        int x = UIScaler.scale(columnNumImage.getIconWidth(), ResizeMode.RESOLUTION);
        int y = UIScaler.scale(columnNumImage.getIconHeight(), ResizeMode.RESOLUTION);
        Image scaledNormal = columnNumImage.getImage().getScaledInstance(x,y,Image.SCALE_SMOOTH);
        columnNumImage = new ImageIcon(scaledNormal);
        
        JLabel leftArrow = new JLabel();
        leftArrow.setSize(columnNumImage.getIconWidth(), columnNumImage.getIconHeight());
        leftArrow.setLocation(UIScaler.scaleWidth(477, ResizeMode.RESOLUTION), 0);
        leftArrow.setIcon(columnNumImage);
        layer.add(leftArrow, Integer.valueOf(4));
        
        // Adds the COLUMNS'_NUMBER text-field
        JLabel columnNum = new JLabel();
        columnNum.setText("3");
        columnNum.setLocation(UIScaler.scaleWidth(480, ResizeMode.RESOLUTION), UIScaler.scaleHeight(6, ResizeMode.RESOLUTION));
        columnNum.setSize(UIScaler.scaleWidth(24, ResizeMode.RESOLUTION), UIScaler.scaleHeight(17, ResizeMode.RESOLUTION));
        columnNum.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255), 1));
        columnNum.setHorizontalAlignment(SwingConstants.CENTER);
        columnNum.setForeground(Color.BLACK);
        columnNum.setFont(new Font("Arial", Font.BOLD, UIScaler.scale(14, ResizeMode.RESOLUTION)));
        layer.add(columnNum, Integer.valueOf(5));

        
        
        frame.setVisible(true);

        
        
        //==================================================================
        // BUTTON ACTIONS
        //==================================================================
        // Listener for "Add Image" button
        addButton.setOnClick(() -> {
            addImage();
        });
        
        // Listener for "Remove Image" button
        removeButton.setOnClick(() -> {
            System.out.println("Remove Button Clicked!");
            removeImage();
        });
        
        // Listener for "Reduce Columns" button
        reduceColButton.setOnClick(() -> {
            maxImagesPerLine = Math.max(1, maxImagesPerLine - 1);
            columnNum.setText(String.valueOf(maxImagesPerLine));
            calcImageGrid(0);
            imageTiler();
            imageTilerFix();
        });
        
        // Listener for "Increase Collumns" button
        addColButton.setOnClick(() -> {
            maxImagesPerLine = Math.min(6, maxImagesPerLine + 1);
            columnNum.setText(String.valueOf(maxImagesPerLine));
            calcImageGrid(0);
            imageTiler();
            imageTilerFix();
        });
        
        // Listener for "Move Image Left" button
        moveLeftButton.setOnClick(() -> {
            moveSelected(MoveDirection.LEFT);
        });
        
        // Listener for "Move Image Right" button
        moveRightButton.setOnClick(() -> {
            moveSelected(MoveDirection.RIGHT);
        });
        
        
        //==================================================================
        // WINDOW RESIZE HANDLING
        //==================================================================
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {

                // reads windows size
                int width = frame.getComponent(0).getWidth();
                int height = frame.getComponent(0).getHeight();
                
                layer.setSize(width,height);
                panel.setSize(width,height);
                
                // Updates UIScaler about Window Size
                UIScaler.setCurrentWindowSize(width,height);

                // Recalculates the images' grid 
                imageTiler();
                layer.revalidate();
                layer.repaint();
            }
        });
        
        // Listener for when application's window state changes (Maximized,Normal mode)
        // Rescales the images' grid according the windows state
        frame.addWindowStateListener(e-> {
            // Timer implimented to allow heavy processes to end before this starts
            new Timer(50, ev -> {
                
                ((Timer)ev.getSource()).stop();
                
                int oldState = e.getOldState();
                int newState = e.getNewState();
            
                // To Determine Window State & Reduce Data Noise
                boolean oldPureMax = oldState == JFrame.MAXIMIZED_BOTH;
                boolean newPureMax = newState == JFrame.MAXIMIZED_BOTH;
                boolean oldPureNorm = oldState == JFrame.NORMAL;
                boolean newPureNorm = newState == JFrame.NORMAL;
            
                // Recalculates the images' grid when window state has changed
                if((oldPureNorm && newPureMax) || (oldPureMax && newPureNorm)){
                    imageTilerFix();
                }
            }).start();
        });
    }
    
    
    //==========================================================================
    // METHODS & OPPERATIONS
    //========================================================================== 
    
    /**
     * Opens file dialog and loads selected images into viewer.
     */
    public  void addImage(){
        // calls Windows File Chooser for user to choose files
        FileChooser chooser = new FileChooser();  
        
        File[] files = chooser.showDialog();

        if (files.length == 0) {
            System.out.println("No files selected.");
        } else {
            System.out.println("Selected files:");
            int index = 0;
            
            // Calculates the new Image Grid Sizes to include the new number of images on screen
            calcImageGrid(files.length);
            
            // adds the chossen files/images into the "images" list
            for (File f : files) {
                System.out.println(f.getAbsolutePath()); // use this path in your app
                
                DynamicImage img = new DynamicImage(images.size(), this::selectImage, files[index].getAbsolutePath(),sizeX,sizeY);//) 250, 350);
                
                // Adds the current image into the "images" list
                images.add(img);
                imageNum++;
                layer.add(img, Integer.valueOf(1));
                index++;
            }
            
            // recalculates and refits the images
            imageTiler();
            imageTilerFix();
        }
    }
    
    
    /**
     * Removes currently selected image from viewer.
     */
    public  void removeImage(){
        if(images.isEmpty() || selectedIndex == -1) return;
        
        JScrollPane pane = images.get(selectedIndex);
        layer.remove(pane);
        images.remove(selectedIndex);
        imageNum--;
        
        // Adds new IDs on each image item according its now place on the list
        for(int i = 0; i<images.size();i++ ){
            images.get(i).setID(i);
        }
        selectedIndex = -1;
        
        // recalculates the images' grid and refits the images
        calcImageGrid(0);
        imageTiler();
        imageTilerFix();
        
        layer.revalidate();
        layer.repaint();
    }
    
    
    /**
     * Calculates optimal grid cell size based on:
     * - window resolution
     * - number of images
     * - column limit
     */
    private  void calcImageGrid(int value){
        columns = Math.min(Math.max(1, imageNum+value), maxImagesPerLine);
        sizeX = UIScaler.getResolutionX() / Math.min(Math.max(imageNum+value,1),columns);
        int menuBarHeight = menuBarLabel.getHeight();
        sizeY = (UIScaler.getResolutionY() - UIScaler.scaleHeight(menuBarHeight,ResizeMode.RESOLUTION)) /
                Math.max(((imageNum+value + columns - 1) / columns),1);
    }
    
    
    // Opens Windows File Chooser Window for user to open images
    public  void fileChooser() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "gif"));

        int result = chooser.showOpenDialog(null); // null = no parent window

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            System.out.println("Selected: " + selectedFile.getAbsolutePath());
        } else {
            System.out.println("User canceled");
        }
    }
    
    
    /**
     * Positions and scales all images based on current grid configuration.
     */
    public  void imageTiler(){
        for (int i = 0; i < images.size(); i++) {
            int startX = UIScaler.scaleWidth(sizeX * (i -((i / columns) * columns)), ResizeMode.WINDOW);
            int startY = UIScaler.scaleHeight((sizeY * (i / columns)), ResizeMode.WINDOW)+30;
            
            images.get(i).setLocation(startX,startY);
            images.get(i).scalePanel(UIScaler.scaleWidth(sizeX, ResizeMode.WINDOW),
                    UIScaler.scaleHeight(sizeY, ResizeMode.WINDOW));
        }
    }
    
    
    /**
     * Applies final adjustments after layout or window state changes.
     */
    public  void imageTilerFix(){
        for (int i = 0; i < images.size(); i++) {
            images.get(i).adjustImagePanel(UIScaler.scaleWidth(sizeX, ResizeMode.WINDOW),
                    UIScaler.scaleHeight(sizeY, ResizeMode.WINDOW));
        }
    }
    
    
    /**
     * Handles selection state logic.
     */
    void selectImage(int index) {
        if(selectedIndex == index){
            selectedIndex = -1;
        }else{
            selectedIndex = index;
        }
        
        // apply the chages to images
        for (int i = 0; i < images.size(); i++) {
            images.get(i).setSelected(i == selectedIndex);
        }
    }
    
    
    /**
     * Moves selected image left or right in logical order.
     */
    public void moveSelected(MoveDirection dir) {
        if (selectedIndex < 0 || selectedIndex >= images.size()) return;

        int newIndex = selectedIndex + dir.delta();

        // prevent out-of-bounds
        if (newIndex < 0 || newIndex >= images.size()) return;

        // Swaps ID between the implicated Images
        images.get(selectedIndex).setID(newIndex);
        images.get(newIndex).setID(selectedIndex);
    
        // swap elements
        Collections.swap(images, selectedIndex, newIndex);

        // update selection
        selectedIndex = newIndex;

        // update selection visuals
        imageTiler();
    }

    
    //==========================================================================
    // MAIN
    //==========================================================================
    public static void main(String[] args) {
        MutiViewer viewer = new MutiViewer();
        
        viewer.MutiViewer();
    }
    
    
    private static javax.swing.JFrame frame;
    private static javax.swing.JLayeredPane layer;
    private static javax.swing.JLabel menuBarLabel;
}