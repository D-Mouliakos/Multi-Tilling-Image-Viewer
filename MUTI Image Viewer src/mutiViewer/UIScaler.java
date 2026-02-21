package mutiViewer;

/**
 * @author Dimitris Mouliakos
 */


import java.awt.*;
import enums.ResizeMode;
import static enums.ResizeMode.RESOLUTION;
import static enums.ResizeMode.WINDOW;


/**
 * UIScaler
 *
 * Utility class responsible for scaling UI element sizes based on:
 * 1) Screen resolution
 * 2) Current window size
 *
 * This allows UI components to maintain consistent visual proportions
 * across different monitor resolutions and window resizing scenarios.
 *
 * Typical usage:
 * - Call initialize() once at application startup
 * - Call setCurrentWindowSize() whenever the window is resized
 * - Use scaleWidth(), scaleHeight(), or scale() when sizing UI elements
 */
public class UIScaler {

    //==========================================================================
    // VARIABLES
    //==========================================================================

    // BASE RESOLUTION DATA
    /** Base Resolution upon UI images are build in */
    private static int BASE_WIDTH = 1920;
    private static int BASE_HEIGHT = 1080;
    
    // ACTUAL RESOLUTION DATA
    /** Detected screen width in pixels */
    private static int resolutionX;

    /** Detected screen height in pixels */
    private static int resolutionY;

    
    // RESOLUTION-BASED SCALING
    /** Horizontal scaling factor relative to screen resolution */
    private static double resScaleX;

    /** Vertical scaling factor relative to screen resolution */
    private static double resScaleY;

    /** Uniform scaling factor (minimum of X and Y to preserve proportions) */
    private static double resScale;

    
    // WINDOW-BASED SCALING
    /** Horizontal scaling factor based on current window width */
    private static double windowScaleX;

    /** Vertical scaling factor based on current window height */
    private static double windowScaleY;

    
    // STATE CONTROL
    /** Ensures the scaler is initialized before use */
    private static boolean initialized = false;

    
    //==========================================================================
    // CONSTRUCTOR
    //==========================================================================
    
    /**
     * Initializes the scaler using the system's current screen resolution.
     * Must be called once before any scaling operations are performed.
     */
    public static void initialize() {
        
        // read systems screen resolution
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        resolutionX = screen.width;
        resolutionY = screen.height;
        
        // Calculate resolution scaling factors
        resScaleX = (double)screen.width / (double) BASE_WIDTH;
        resScaleY = (double)screen.height / (double) BASE_HEIGHT;
        
        // Use uniform scale to preserve proportions
        resScale = Math.min(resScaleX, resScaleY);

        initialized = true;
    }

    
    //==========================================================================
    // SCALING METHODS
    //==========================================================================
    
    /**
     * Scales a width value according to the selected resize mode.
     *
     * @param value original width
     * @param mode scaling mode (RESOLUTION or WINDOW)
     * @return scaled width
     */
    public static int scaleWidth(int value, ResizeMode mode){
        checkInit();
       
        return (int) (switch (mode) {
            case RESOLUTION -> (int)(value * resScaleX);
            case WINDOW -> (int)(value * windowScaleX);
            default -> value;
        });
    }

    
    /**
     * Scales a height value according to the selected resize mode.
     *
     * @param value original height
     * @param mode scaling mode (RESOLUTION or WINDOW)
     * @return scaled height
     */
    public static int scaleHeight(int value, ResizeMode mode){
        checkInit();

        return (int) (switch (mode) {
            case RESOLUTION -> (int)(value * resScaleY);
            case WINDOW -> (int)(value * windowScaleY);
            default -> value;
        });
    }
    
    
    /**
     * Scales a value uniformly while preserving aspect ratio.
     * Useful for font sizes, icons, and square UI elements.
     *
     * @param value original size
     * @param mode scaling mode (RESOLUTION or WINDOW)
     * @return scaled value
     */
    public static int scale(int value, ResizeMode mode){
        checkInit();
   
        return (int) (switch (mode) {
            case RESOLUTION -> (int)(value * resScale);
            case WINDOW -> (int)(value * Math.min(windowScaleX, windowScaleY));
            default -> value;
        });
    }
    
    
    /**
     * Ensures the scaler has been initialized before use.
     */
    private static void checkInit() {
        if (!initialized) {
            throw new IllegalStateException("UIScaler not initialized.");
        }
    }
    
    
    //==========================================================================
    // GETTERS
    //==========================================================================

    /** @return detected screen width */
    public static int getResolutionX(){
        return resolutionX;
    }
    
    /** @return detected screen height */
    public static int getResolutionY(){
        return resolutionY;
    }
    
    
    //==========================================================================
    // SETTERS
    //==========================================================================
    
    /**
     * Updates scaling factors based on the current window size.
     *
     * This should be called whenever the application window is resized.
     * The resulting values represent how large the window is relative
     * to the full screen resolution.
     *
     * @param width current window width
     * @param height current window height
     */
    public static void setCurrentWindowSize(int width, int height){
        windowScaleX = (double)width / (double) resolutionX;
        windowScaleY = (double)height / (double) resolutionY;
    }
}