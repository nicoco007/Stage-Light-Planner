package com.nicolasgnyra.stagelightplanner.components;

import com.nicolasgnyra.stagelightplanner.LightDefinition;
import com.nicolasgnyra.stagelightplanner.helpers.PaintHelper;
import com.nicolasgnyra.stagelightplanner.transferables.StageElementTransferable;

import java.awt.*;
import java.awt.datatransfer.Transferable;

public class JLightDefinition extends JStageElementDefinition {
    private final LightDefinition definition;

    public JLightDefinition(LightDefinition definition) {
        super();
        this.definition = definition;
        setToolTipText(definition.getDisplayName());
    }

    /**
     * paintComponent(Graphics) Method:
     * Called when the component needs to be repainted.
     *
     * Input: Graphics class.
     *
     * Process: Draws a representation of the light definition with the label (short name) of the light on top.
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

        // enable anti-aliasing, set the color to display color, and draw the shape
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(definition.getDisplayColor());
        PaintHelper.drawShape(g2d, definition.getShape(), 0, 0, getWidth(), getHeight());

        // set the color & font & draw the label
        g2d.setColor(PaintHelper.getHueBasedOnBackgroundColor(definition.getDisplayColor()));
        g2d.setFont(g2d.getFont().deriveFont(50.0f));
        PaintHelper.drawScaledString(g2d, definition.getLabel(), 0, 0, getWidth(), getHeight(), 5);
    }

    /**
     * getTransferable() Method:
     * Returns the transferable that should be used when the component is dropped onto the planner.
     *
     * Input: None.
     *
     * Process: None.
     *
     * Output: New StageElementTransferable with the transfer data (in this case, the light definition).
     *
     * @return New StageElementTransferable with the transfer data.
     */
    @Override
    Transferable getTransferable() {
        return new StageElementTransferable(definition);
    }
}
