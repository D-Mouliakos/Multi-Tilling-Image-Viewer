package mutiViewer;

/**
 * @author Dimitris Mouliakos
 */


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.awt.Point;
import java.util.function.Consumer;

import enums.Quality;


/**
 * Interactive image viewer component with zooming, panning, and selection support.
 *
 * Core capabilities:
 * - Displays large images efficiently inside a scrollable viewport
 * - Supports smooth zooming with dynamic quality adjustment
 * - Enables click selection and drag-based panning
 * - Maintains original image for resolution-independent rescaling
 *
 * Rendering strategy:
 * - Low-quality scaling during active zoom interaction (performance-first)
 * - Automatic high-quality rescale after zoom stabilizes
 *
 * Designed for grid-based multi-image layouts.
 */
public class DynamicImage extends JScrollPane {
    
    //==========================================================================
    // VARIABLES
    //==========================================================================
    /** Logical identifier used by container/grid manager */
    private Integer index;

    /** Selection state used for UI highlighting and external logic */
    private boolean isSelected = false;

    /** Callback triggered when image is clicked */
    private Consumer<Integer> clickCallback;

    /** Component responsible for rendering the scaled image */
    private final JLabel imageLabel = new JLabel();

    /** Original full-resolution image used as rescaling source */
    private BufferedImage originalImage;

    /** Current zoom multiplier relative to original image size */
    private double zoomLevel;

    /** Increment applied per mouse wheel step */
    private double ZOOM_STEP = 0.05;

    /** Minimum allowed zoom level to preserve visibility */
    private double minZoomLevel = 0.1;

    /** Timer used to delay high-quality rendering after zoom interaction */
    private Timer qualityTimer;
    
    /** Current viewport position for panning */
    private int posX = 0, posY = 0;

    /** Stores last mouse position during drag operations */
    private Point lastDragPoint;
    
    
    //==========================================================================
    // CONSTRUCTOR
    //==========================================================================
    
    /**
     * Creates an interactive image viewer instance.
     *
     * @param indexNum Identifier assigned by container
     * @param clickCallbackPr Callback invoked on selection
     * @param path Image file path
     * @param width Initial display width constraint
     * @param height Initial display height constraint
     */
    public DynamicImage(int indexNum, Consumer<Integer> clickCallbackPr, String path, int width, int height){
        index = indexNum;
        clickCallback = clickCallbackPr;
        
        // Configure image display label
        imageLabel.setOpaque(true);
        imageLabel.setBackground(new Color(80, 80, 80));
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);
        
        // Attach label to scrollable viewport
        setViewportView(imageLabel);
        setBackground(new Color(80, 80, 80));
        setBorder(BorderFactory.createLineBorder(new Color(50, 50, 50), 1));
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        
        try{
            // Load and initialize image
            setImage(originalImage = ImageIO.read(new File(path)), width, height);
        } catch (IOException e){
            e.printStackTrace();
        }
        
        // Initialize delayed high-quality rendering mechanism
        initTimer();
        
        
        // Mouse interaction handler for panning and selection
        MouseAdapter mouseHandler = new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e){
                lastDragPoint = SwingUtilities.convertPoint(imageLabel, e.getPoint(), getViewport());
            }
        
            @Override
            public void mouseDragged(MouseEvent e) {
                if (lastDragPoint != null) {
                    Point current = SwingUtilities.convertPoint(imageLabel, e.getPoint(), getViewport());
                    JViewport vp = getViewport();

                    Point viewPos = vp.getViewPosition();
                    int dx = lastDragPoint.x - current.x;
                    int dy = lastDragPoint.y - current.y;

                    // Clamp viewport movement within image bounds
                    posX = Math.max(0, Math.min(viewPos.x + dx, imageLabel.getWidth() - vp.getWidth()));
                    posY = Math.max(0, Math.min(viewPos.y + dy, imageLabel.getHeight() - vp.getHeight()));

                    vp.setViewPosition(new Point(posX, posY));

                    lastDragPoint = current;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                lastDragPoint = null;
                
                // Toggle selection state
                isSelected = !isSelected;
                setSelected(isSelected);
                
                // Notify container of selection change
                clickCallback.accept(index);
            }
        };
        
        
        imageLabel.addMouseListener(mouseHandler);
        imageLabel.addMouseMotionListener(mouseHandler);
        
        // Mouse wheel zoom handler
        imageLabel.addMouseWheelListener(e ->{
            // Reads Mouse wheel to calculate zoom
            if (originalImage == null) return;
            
            double zoomFactor = (e.getWheelRotation()<0)?ZOOM_STEP:-ZOOM_STEP;
            
            zoomLevel += zoomFactor;
            zoomLevel = Math.max(minZoomLevel,Math.min(zoomLevel,2.0f));
            
            // Fast render during interaction
            rescale(Quality.LOW);
            
            // Schedule high-quality render after interaction ends
            qualityTimer.restart();
        });
    }
    
    
    //==========================================================================
    // METHODS & OPPERATIONS
    //==========================================================================
    
    /**
     * Recreates a scaled image based on current zoom level.
     *
     * @param quality Determines rendering speed vs visual quality
     */
    private void rescale(Quality quality){
        if (originalImage == null) return;
        
        // calculates new size
        int w = (int) (originalImage.getWidth() * zoomLevel);
        int h = (int) (originalImage.getHeight() * zoomLevel);
        
        // creates a new memory "item" for the new/zoomed image
        BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = scaled.createGraphics();
        
        if(quality == Quality.LOW){
            // Fast scaling for interactive responsiveness
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        }
        if(quality == Quality.HIGH){
            // High-quality resampling for final image clarity
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                        RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
        }
        
        // draws the new scaled image into memory
        g2.drawImage(originalImage, 0, 0, w, h, null);
        g2.dispose();
        
        // apply the new/scaled image into the viewer
        imageLabel.setIcon(new ImageIcon(scaled));//scaledImage));
        imageLabel.setPreferredSize(new Dimension(w,h));
        imageLabel.revalidate();
    }
    
    
    /** Adjusts outer panel size within grid layout */
    public void scalePanel(int w, int h){
        setSize(w,h);
    }
    
    
    /** Adjusts image label size explicitly */
    public void adjustImagePanel(int w, int h){
        imageLabel.setSize(w,h);
    }
    
    
    /** Initializes delayed high-quality rendering timer */
    private void initTimer() {
        qualityTimer = new Timer(200, e -> {
            rescale( Quality.HIGH);
            repaint();
        });
        qualityTimer.setRepeats(false); // run only once after last zoom
    }
    
    
    /**
     * Assigns a new image and calculates initial zoom to fit container.
     */
    public void setImage(BufferedImage img, int width, int height){
        this.originalImage = img;
        
        // calculates the between images and panels height
        double ratio = (double)height/(double)originalImage.getHeight();
        this.zoomLevel = ratio;//0.5f;
        
        // Adjust minimum zoom constraint if necessary
        if (minZoomLevel > zoomLevel){
            minZoomLevel = zoomLevel;
            ZOOM_STEP = 0.025;
        }

        //scales a copy of the image
        rescale(Quality.HIGH);
    }
    
    
    //==========================================================================
    // GETTERS
    //==========================================================================
    public double getZoom(){
        return zoomLevel;
    }
    
    
    public boolean getIsSelected(){
        return isSelected;
    }
    
    
    public Point getPosition(){
        return new Point (posX, posY);
    }
    
    public int getID(){
        return index;
    }
    
    
    //==========================================================================
    // SETTERS
    //==========================================================================
    
    /**
     * Updates selection state and applies visual indicator.
     */
    public void setSelected(boolean check){
        isSelected = check;
        
        // applys the coresponding state into Label's Border
        if(isSelected){
            setBorder(BorderFactory.createLineBorder(new Color(200, 50, 50), 3));
        }else{
            setBorder(BorderFactory.createLineBorder(new Color(50, 50, 50), 1));
        }
    }
    
    
    public void setID(int id){
        index = id;
    }
    
    
    /** Restores viewport position after layout updates */
    public void setPosition(Point p){
        getViewport().setViewPosition(p);
    }
}