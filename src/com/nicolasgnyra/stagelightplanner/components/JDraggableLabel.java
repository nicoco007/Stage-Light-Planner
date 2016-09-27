package com.nicolasgnyra.stagelightplanner.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.geom.Rectangle2D;

/**
 * JDraggableLabel Class:
 * A stage element that displays a user-defined string.
 *
 * Date: 2016-09-26
 *
 * @author Nicolas Gnyra
 * @version 1.0
 */
public class JDraggableLabel extends JStageElement {

    private String text;        // text to display
    private int fontSize;       // font size
    private String fontFamily;  // font family

    /**
     * JDraggableLabel(int, int, String) Constructor:
     * Creates a new JDraggableLabel with the specified x/y coordinates and text.
     *
     * Input: Coordinates & text.
     *
     * Process: Calls class constructor with defaults.
     *
     * Output: A new instance of the JDraggableLabel class.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param text Text to display
     */
    JDraggableLabel(int x, int y, String text) {
        this(x, y, text, Color.black, 12, UIManager.getLookAndFeelDefaults().getFont("Label.font").getFontName());
    }

    /**
     * JDraggableLabel(int, int, String, Color, int, String) Constructor:
     * Creates a new JDraggableLabel with the specified coordinates, text, color, font size and font family.
     *
     * Input: Coordinates, text, color, font size, font family.
     *
     * Process: Calls the superclass constructor & sets values.
     *
     * Output: New instance of the JDraggableLabel class.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param text Text to display
     * @param color Text color
     * @param fontSize Text font size
     * @param fontFamily Text font family
     */
    public JDraggableLabel(int x, int y, String text, Color color, int fontSize, String fontFamily) {
        super(x, y, 50, 50, color);
        this.text = text;
        this.fontSize = fontSize;
        this.fontFamily = fontFamily;
    }

    /**
     * paintElement(Graphics) Method:
     * Called when the stage element needs to be painted.
     *
     * Input: Graphics class.
     *
     * Process: Paints every line of text and resizes the label according to text length/height
     *
     * Output: None.
     *
     * @param g Graphics class used to draw the window.
     */
    @Override
    protected void paintElement(Graphics g) {

        // cast graphics to 2D graphics
        Graphics2D g2d = (Graphics2D) g;

        // set font to specified font
        g2d.setFont(new Font(fontFamily, Font.PLAIN, fontSize));

        // get font metrics
        FontMetrics fm = g2d.getFontMetrics();

        // get lines from text
        String[] lines = text.split("\n");

        // initialize width & height variables
        int width = 0;
        int height = 0;

        // iterate through lines
        for (String line : lines) {

            // get string bounds
            Rectangle2D stringBounds = fm.getStringBounds(line, g);

            // set width to max between current line width and previous width
            width = Math.max(width, (int) stringBounds.getWidth());

            // add current string height to total height
            height += stringBounds.getHeight();
        }

        // set width & height with padding equal to cell size
        this.width = width + parent.getCellSize();
        this.height = height + parent.getCellSize();

        // set color to specified color & enable anti-alising
        g2d.setColor(color);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // iterate through lines again
        for (int i = 0; i < lines.length; i++) {

            // scale text
            g2d.scale(parent.getZoom(), parent.getZoom());

            // get line bounds
            Rectangle2D stringBounds = fm.getStringBounds(lines[i], g);

            // draw string, centered & on current line (specified by i)
            g2d.drawString(lines[i], (int) (getWidth() / parent.getZoom() - stringBounds.getWidth()) / 2, (int) (((getHeight() / parent.getZoom() - stringBounds.getHeight() * (lines.length - i)) / 2) + fm.getAscent() + (int)(stringBounds.getHeight() * i / 2)));

            // return to normal scale
            g2d.scale(1f / parent.getZoom(), 1f / parent.getZoom());

        }

        // check if we currently have inner focus
        if (hasInnerFocus()) {

            // set color to gray
            g2d.setColor(Color.gray);

            // set stroke to dotted stroke
            g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 2 }, 0));

            // draw a rectangle around the label
            g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        }

    }

    /**
     * innerFocusLost(FocusEvent) Method:
     * Called when inner focus is lost.
     *
     * Input: Focus event.
     *
     * Process: Calls superclass method & deletes this stage element if the string is empty or whitespaces.
     *
     * Output: None.
     *
     * @param e Focus event.
     */
    @Override
    protected void innerFocusLost(FocusEvent e) {

        // call superclass method
        super.innerFocusLost(e);

        // remove self if current text is empty or whitespace
        if (text.trim().isEmpty())
            removeSelf();

    }

    void setText(String text) {
        this.text = text;
        propertyUpdated();
    }

    public String getText() {
        return text;
    }

    void setFontSize(int size) {
        this.fontSize = size;
        propertyUpdated();
    }

    public int getFontSize() {
        return fontSize;
    }

    void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
        propertyUpdated();
    }

    public String getFontFamily() {
        return fontFamily;
    }
}
