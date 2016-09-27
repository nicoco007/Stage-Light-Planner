package com.nicolasgnyra.stagelightplanner.components;

import com.nicolasgnyra.stagelightplanner.Orientation;

import java.awt.*;

/**
 * JBatten Class:
 * A draggable & resizeable batten on which lights are placed.
 *
 * Date: 2016-09-26
 *
 * @author Nicolas Gnyra
 * @version 1.0
 */
public class JBatten extends JStageElement {

    private static final int thickness = 10;                    // thickness of the batten, in pixels
    private int heightFromFloor = 100;                          // default height of the batten, in cm
    private int length = 0;                                     // length of the batten, in cm
    private Orientation orientation = Orientation.HORIZONTAL;   // orientation of the batten

    /**
     * JBatten(int, int, int, Orientation, int) Constructor:
     * Creates a new instance of the JBatten class with the specified values.
     *
     * Input: Coordinates, length, orientation, height from floor.
     *
     * Process: Calls superclass constructor & sets class values.
     *
     * Output: A new instance of the JBatten class.
     *
     * @param x X position on grid
     * @param y Y position on grid
     * @param length Length of the batten
     * @param orientation Orientation of the batten
     * @param heightFromFloor Height of the batten off the floor
     */
    public JBatten(int x, int y, int length, Orientation orientation, int heightFromFloor) {
        super(x, y, orientation == Orientation.HORIZONTAL ? length : thickness, orientation == Orientation.VERTICAL ? length : thickness, Color.darkGray);
        this.length = length;
        this.heightFromFloor = heightFromFloor;
        this.orientation = orientation;
    }

    /**
     * paintElement(Graphics) Method:
     * Called when the stage element needs to be painted.
     *
     * Input: Graphics class.
     *
     * Process: Calculates the size of the batten & fills in according to focus state.
     *
     * Output: None.
     *
     * @param g Graphics class used to draw the window.
     */
    @Override
    protected void paintElement(Graphics g) {

        // cast graphics to 2D graphics
        Graphics2D g2d = (Graphics2D)g;

        // set width & height of the batten
        width = orientation == Orientation.HORIZONTAL ? length : thickness;
        height = orientation == Orientation.VERTICAL ? length : thickness;

        // set color based on focus state
        g2d.setColor(hasInnerFocus() ? getBackground().brighter() : getBackground());

        // fill background
        g2d.fillRect(0, 0, getWidth(), getHeight());

    }

    public Orientation getOrientation() {
        return orientation;
    }

    void setOrientation(Orientation orientation) {
        this.orientation = orientation;
        propertyUpdated();
    }

    public int getLength() {
        return length;
    }

    void setLength(int length) {
        this.length = length;
        propertyUpdated();
    }

    public int getHeightFromFloor() {
        return heightFromFloor;
    }

    void setHeightFromFloor(int heightFromFloor) {
        this.heightFromFloor = heightFromFloor;
        propertyUpdated();
    }
}
