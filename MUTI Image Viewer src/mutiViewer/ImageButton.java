package mutiViewer;

/**
 * @author Dimitris Mouliakos
 */


import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

import enums.ResizeMode;


/**
 * Image-based button component built on JLabel.
 *
 * Features:
 * - Displays different images for normal, hover, and pressed states
 * - Scales images according to application resolution using UIScaler
 * - Supports a simple callback action on click
 * - Allows text overlay centered on the image
 *
 * Design intent:
 * This component provides a lightweight alternative to JButton
 * with full visual control through images, suitable for custom UI themes.
 */
public class ImageButton extends JLabel {
    
    //==========================================================================
    // VARIABLES
    //==========================================================================
    /** Icon displayed during normal state */
    private ImageIcon normalIcon;

    /** Icon displayed when cursor is over the button */
    private ImageIcon hoverIcon;

    /** Icon displayed while mouse button is pressed */
    private ImageIcon pressedIcon;

    /** Callback executed when button is clicked */
    private Runnable action;

    
    //==========================================================================
    // CONSTRUCTOR
    //==========================================================================
    
    /**
     * Creates an image-based button with optional hover and pressed states.
     *
     * @param text        Text displayed over the button image
     * @param normalPath  Resource path for normal state image (required)
     * @param hoverPath   Resource path for hover image (optional)
     * @param pressedPath Resource path for pressed image (optional)
     * @param comment     Tooltip text shown on hover
     */
    public ImageButton(String text,String normalPath, String hoverPath, String pressedPath,String comment) {
        
        // Load images from application resources
        normalIcon = new ImageIcon(getClass().getResource(normalPath));
        hoverIcon = hoverPath != null ? new ImageIcon(getClass().getResource(hoverPath)) : null;
        pressedIcon = pressedPath != null ? new ImageIcon(getClass().getResource(pressedPath)) : null;
        
        // Determine scaled size based on resolution-aware UI scaling
        int sizeX = UIScaler.scaleWidth(normalIcon.getIconWidth(),ResizeMode.RESOLUTION);
        int sizeY = UIScaler.scaleHeight(normalIcon.getIconHeight(),ResizeMode.RESOLUTION);
        
        // Resize images to maintain consistent UI appearance across resolutions
        Image scaledNormal = normalIcon.getImage().getScaledInstance(sizeX, sizeY, Image.SCALE_SMOOTH);
        Image scaledHoverIcon = hoverIcon != null 
                ? hoverIcon.getImage().getScaledInstance(sizeX, sizeY, Image.SCALE_SMOOTH)
                : null;
        Image scaledPressIcon = pressedIcon != null
                ? pressedIcon.getImage().getScaledInstance(sizeX, sizeY, Image.SCALE_SMOOTH)
                : null;
        
        // Replace original icons with scaled versions
        normalIcon = new ImageIcon(scaledNormal);
        hoverIcon = scaledHoverIcon != null ? new ImageIcon(scaledHoverIcon) : null;
        pressedIcon = scaledPressIcon != null ? new ImageIcon(scaledPressIcon) : null;

        // Component sizing matches scaled image size
        setSize(sizeX, sizeY);
        
        // Configure text overlay appearance & comment
        setText(text);
        setForeground(Color.WHITE);
        setFont(new Font("Arial", Font.BOLD, UIScaler.scale(14, ResizeMode.RESOLUTION)));
        setToolTipText(comment);
        
        // Center text relative to icon
        setHorizontalTextPosition(SwingConstants.CENTER);
        setVerticalTextPosition(SwingConstants.CENTER);
        
        // Center text over image
        setIcon(normalIcon);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        

        // mouse interaction handler controling visual states and click actions
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                if (hoverIcon != null)
                    setIcon(new ImageIcon(scaledHoverIcon));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setIcon(normalIcon);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (pressedIcon != null)
                    setIcon(pressedIcon);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                setIcon(normalIcon);

                if (action != null && contains(e.getPoint())) {
                    action.run(); // trigger callback
                }
            }
        });
    }
    
    
    //==========================================================================
    // METHODS & OPPERATIONS
    //==========================================================================
    
    /**
     * Assigns an action to be executed when the button is clicked 
     * 
     * @param action runnable callback representing button behavior
     */
    public void setOnClick(Runnable action) {
        this.action = action;
    }
}