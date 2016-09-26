package com.nicolasgnyra.stagelightplanner.components;

import com.nicolasgnyra.stagelightplanner.LightDefinition;
import com.nicolasgnyra.stagelightplanner.helpers.PaintHelper;

import javax.swing.*;
import java.awt.*;

public class JPreviewPane extends JPanel {
    private LightDefinition lightDefinition;

    public JPreviewPane() {
        super(null);

        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (lightDefinition != null) {
            Graphics2D g2d = (Graphics2D) g;

            g2d.setColor(Color.gray);

            int margin = 5;

            g2d.drawRect(margin, margin, getWidth() - margin * 2 - 1, getHeight() - margin * 2 - 1);
            g2d.setClip(margin + 1, margin + 1, getWidth() - margin * 2 - 2, getHeight() - margin * 2 - 2);

            g2d.setColor(Color.lightGray);
            g2d.setFont(g2d.getFont().deriveFont(Font.BOLD));

            FontMetrics fm = g2d.getFontMetrics();

            g2d.drawString("PREVIEW", getInsets().left, getInsets().top + fm.getAscent());

            int size = 50;
            int x = getInsets().left + (getWidth() - getInsets().left - getInsets().right - size) / 2 - 100;
            int y = getInsets().top + (getHeight() - getInsets().top - getInsets().bottom - size) / 2;

            float fieldAngle = lightDefinition.isFieldAngleRange() ? lightDefinition.getFieldAngleMax() : lightDefinition.getFieldAngle();
            PaintHelper.drawBeam(g2d, x, y, size, size, fieldAngle, 75, Color.yellow, 100, 90, 40, true);

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(lightDefinition.getDisplayColor());
            g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN));

            PaintHelper.drawShape(g, lightDefinition.getShape(), x, y, size, size);

            g2d.setColor(PaintHelper.getHueBasedOnBackgroundColor(lightDefinition.getDisplayColor()));
            PaintHelper.drawScaledString(g2d, "000", x, y, size, size, 10);

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        }
    }

    public void setLightDefinition(LightDefinition lightDefinition) {
        this.lightDefinition = lightDefinition;
        repaint();
    }
}
