package com.nicolasgnyra.stagelightplanner.components;

import com.nicolasgnyra.stagelightplanner.helpers.PaintHelper;
import com.nicolasgnyra.stagelightplanner.transferables.StageElementTransferable;

import java.awt.*;
import java.awt.datatransfer.Transferable;

/**
 * JLabelDefinition Class:
 * A drag-and-drop label destined to be used on a stage planner instance.
 *
 * Date: 2016-09-26
 *
 * @author Nicolas Gnyra
 * @version 1.0
 */
public class JLabelDefinition extends JStageElementDefinition {

    /**
     * JLabelDefinition() Constructor:
     * Creates a new instance of the JLabelDefinition class.
     *
     * Input: None.
     *
     * Process: Sets the component's tooltip to 'Label'.
     *
     * Output: A new instance of the JLabelDefinition class.
     */
    public JLabelDefinition() {
        super();
        setToolTipText("Label");
    }

    /**
     * paintComponent(Graphics) Method:
     * Called when the component needs to be repainted.
     *
     * Input: Graphics class.
     *
     * Process: Draws a scaled string that reads 'Label'.
     *
     * Output: None.
     *
     * @param g Graphics class used to draw the window.
     */
    @Override
    protected void paintComponent(Graphics g) {

        // call superclass method
        super.paintComponent(g);

        // cast graphics to 2D graphics, enable anti-aliasing, and draw 'Label'.
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        PaintHelper.drawScaledString(g2d, "Label", getWidth(), getHeight(), 0);

    }

    /**
     * getTransferable() Method:
     * Returns the transferable that should be used when the component is dropped onto the planner.
     *
     * Input: None.
     *
     * Process: None.
     *
     * Output: New StageElementTransferable with the transfer data (in this case, the text to display).
     *
     * @return New StageElementTransferable with the transfer data.
     */
    @Override
    Transferable getTransferable() {
        return new StageElementTransferable("Text");
    }
}
