package com.nicolasgnyra.stagelightplanner.components;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;

/**
 * JColorChooserButton Class:
 * A button that opens a color chooser dialog when clicked and shows the currently selected color as a square on the button.
 *
 * Date: 2016-09-26
 *
 * @author Nicolas Gnyra
 * @version 1.0
 */
class JColorChooserButton extends JButton {

    private Color current = null;                                               // currently selected color
    private final List<ColorChangedListener> listeners = new ArrayList<>();     // change listeners

    /**
     * JColorChooserButton(Color) Constructor:
     * Creates a new instance of the JColorChooserButton class with the specified starting color.
     *
     * Input: Initial color.
     *
     * Process: Creates a JColorChooser dialog that is displayed when the button is pressed.
     *
     * Output: None.
     *
     * @param initialColor Initial chooser color.
     */
    public JColorChooserButton(final Color initialColor) {

        // set selected color to specified color
        setSelectedColor(initialColor);

        // add action listener
        addActionListener(arg0 -> {

            // set initial color to current color if color has been chosen in the past, else set to the specified value
            JColorChooser chooser = new JColorChooser(current != null ? current : initialColor);

            // get chooser panels
            AbstractColorChooserPanel[] panels = chooser.getChooserPanels();

            // only show HSV panel
            chooser.setChooserPanels(new AbstractColorChooserPanel[] { panels[1] });

            // create dialog
            JDialog dialog = JColorChooser.createDialog(null, "Choose a color", true, chooser, e -> setSelectedColor(chooser.getColor()), null);

            // show dialog
            dialog.setVisible(true);

        });

    }

    /**
     * setSelectedColor(Color) Method:
     * Sets the current color to the specified color.
     *
     * Input: New color.
     *
     * Process: Sets the color, repaints the icon, and calls change listeners.
     *
     * Output: None.
     *
     * @param newColor New color.
     */
    public void setSelectedColor(Color newColor) {

        // do nothing if the new color is undefined
        if (newColor == null)
            return;

        // set color to specified color
        current = newColor;

        // create & set icon
        setIcon(createIcon(current, 16, 16));

        // repaint the button
        repaint();

        // call all change listeners
        for (ColorChangedListener listener : listeners)
            listener.colorChanged(newColor);

    }

    public Color getSelectedColor() {
        return current;
    }

    /**
     * addColorChangedListener(ColorChangedListener) Method:
     * Adds a color changed listener to the listeners list.
     *
     * Input: Listener to add.
     *
     * Process: Add the specified listener to the list of listeners.
     *
     * Output: None.
     *
     * @param listener Listener to add.
     */
    @SuppressWarnings("WeakerAccess")
    public void addColorChangedListener(ColorChangedListener listener) {
        listeners.add(listener);
    }

    /**
     * createIcon(Color, int, int) Method:
     * Creates an ImageIcon rectangle with the specified color, width, and height.
     *
     * Input: Color & size
     *
     * Process: Creates a buffered image, draws on it, and converts it to an icon.
     *
     * Output: Generated ImageIcon.
     *
     * @param color Main color of the icon
     * @param width Width of the icon
     * @param height Height of the icon
     * @return The generated icon.
     */
    private static ImageIcon createIcon(Color color, int width, int height) {

        // create new RGB image
        BufferedImage image = new BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_RGB);

        // create graphics instance from image
        Graphics2D graphics = image.createGraphics();

        // set color to specified color
        graphics.setColor(color);

        // fill the rectangle
        graphics.fillRect(0, 0, width, height);

        // set XOR mode to gray (overlapping colors turn to gray)
        graphics.setXORMode(Color.gray);

        // draw the border
        graphics.drawRect(0, 0, width - 1, height - 1);

        // flush image
        image.flush();

        // return image as ImageIcon
        return new ImageIcon(image);
    }

    /**
     * ColorChangedListener Interface:
     * Interface used as a change listener for the JColorChooserButton component.
     *
     * Date: 2016-09-26
     *
     * @author Nicolas Gnyra
     * @version 1.0
     */
    public interface ColorChangedListener {
        void colorChanged(Color newColor);
    }
}