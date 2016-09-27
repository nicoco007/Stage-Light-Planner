package com.nicolasgnyra.stagelightplanner.components;

import com.nicolasgnyra.stagelightplanner.LightDefinition;
import com.nicolasgnyra.stagelightplanner.helpers.PaintHelper;

import javax.swing.*;
import java.awt.*;

/**
 * JPreviewPane Class:
 * Light definition preview pane for use in the fixture editor.
 *
 * Date: 2016-09-27
 *
 * @author Nicolas Gnyra
 * @version 1.0
 */
public class JPreviewPane extends JPanel {

    private LightDefinition lightDefinition;    // light definition to display

    /**
     * JPreviewPane() Constructor:
     * Creates a new instance of the JPreviewPane class.
     *
     * Input: None.
     *
     * Process: Sets the panel's insets.
     *
     * Output: New instance of the JPreviewPane class.
     */
    public JPreviewPane() {

        // set no layout manager
        super(null);

        // create insets
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    }

    /**
     * paintComponent(Graphics) Method:
     * Paints the light definition and its beam.
     *
     * Input: Graphics instance.
     *
     * Process: If the light definition is set, displays the light and its beam.
     *
     * Output: None.
     *
     * @param g Graphics instance.
     */
    @Override
    protected void paintComponent(Graphics g) {

        // call superclass method
        super.paintComponent(g);

        // check if light definition is set
        if (lightDefinition != null) {

            // cast graphics to 2D graphics
            Graphics2D g2d = (Graphics2D) g;

            // set color to gray
            g2d.setColor(Color.gray);

            // define border margin
            int margin = 5;

            // draw border & set clip to inside border
            g2d.drawRect(margin, margin, getWidth() - margin * 2 - 1, getHeight() - margin * 2 - 1);
            g2d.setClip(margin + 1, margin + 1, getWidth() - margin * 2 - 2, getHeight() - margin * 2 - 2);

            // set color & font
            g2d.setColor(Color.lightGray);
            g2d.setFont(g2d.getFont().deriveFont(Font.BOLD));

            // get font metrics
            FontMetrics fm = g2d.getFontMetrics();

            // draw preview (title/legend)
            g2d.drawString("PREVIEW", getInsets().left, getInsets().top + fm.getAscent());

            // set size & x/y coordinates
            int size = 50;
            int x = getInsets().left + (getWidth() - getInsets().left - getInsets().right - size) / 2 - 100;
            int y = getInsets().top + (getHeight() - getInsets().top - getInsets().bottom - size) / 2;

            // enable anti-aliasing, get field angle & draw beam
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            float fieldAngle = lightDefinition.isFieldAngleRange() ? lightDefinition.getFieldAngleMax() : lightDefinition.getFieldAngle();
            PaintHelper.drawBeam(g2d, x, y, size, size, fieldAngle, 75, Color.yellow, 100, 90, 40, true);

            // set color & font
            g2d.setColor(lightDefinition.getDisplayColor());
            g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN));

            // draw shape
            PaintHelper.drawShape(g, lightDefinition.getShape(), x, y, size, size);

            // set color & draw scaled ID string
            g2d.setColor(PaintHelper.getHueBasedOnBackgroundColor(lightDefinition.getDisplayColor()));
            PaintHelper.drawScaledString(g2d, "000", x, y, size, size, 10);

            // disable anti-aliasing
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        }

    }

    /**
     * setLightDefinition(LightDefinition) Method:
     * Sets the light definition to display.
     *
     * Input: Light definition to set.
     *
     * Process: Sets the light definition & repaints the component.
     *
     * Output: None.
     *
     * @param lightDefinition Light definition to set.
     */
    public void setLightDefinition(LightDefinition lightDefinition) {
        this.lightDefinition = lightDefinition;
        repaint();
    }
}
