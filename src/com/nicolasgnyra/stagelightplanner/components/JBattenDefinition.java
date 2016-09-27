package com.nicolasgnyra.stagelightplanner.components;

import com.nicolasgnyra.stagelightplanner.transferables.StageElementTransferable;

import java.awt.*;
import java.awt.datatransfer.Transferable;

/**
 * JBattenDefinition Class:
 * Drawable element to be used in the fixture container for drag-and-drop purposes.
 *
 * Date: 2016-09-26
 *
 * @author Nicolas Gnyra
 * @version 1.0
 */
public class JBattenDefinition extends JStageElementDefinition {

    /**
     * JBattenDefinition() Constructor:
     * Creates a new instanceof the JBattenDefinition class.
     *
     * Input: None.
     *
     * Process: Sets the tooltip text to 'Batten'.
     *
     * Output: A new instance of the JBattenDefinition class.
     */
    public JBattenDefinition() {
        super();
        setToolTipText("Batten");
    }

    /**
     * paintComponent(Graphics) Method:
     * Called when the component needs to be repainted.
     *
     * Input: Graphics class.
     *
     * Process: Fills a slim gray horizontal rectangle that represents a batten.
     *
     * Output: None.
     *
     * @param g Graphics class used to draw the window.
     */
    @Override
    protected void paintComponent(Graphics g) {

        // call superclass method
        super.paintComponent(g);

        // cast graphics to 2D graphics
        Graphics2D g2d = (Graphics2D)g;

        // enable anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // fill gray rectangle
        g2d.setColor(Color.gray);
        g2d.fillRect(0, (getHeight() - 10) / 2, getWidth() - 1, 10);

    }

    /**
     * getTransferable() Method:
     * Returns the transferable that should be used when the component is dropped onto the planner.
     *
     * Input: None.
     *
     * Process: None.
     *
     * Output: New StageElementTransferable with the transfer data (in this case, nothing).
     *
     * @return New StageElementTransferable with the transfer data.
     */
    @Override
    Transferable getTransferable() {
        return new StageElementTransferable(null);
    }
}
