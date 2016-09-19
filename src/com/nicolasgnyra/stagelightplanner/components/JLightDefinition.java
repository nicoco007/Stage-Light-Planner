package com.nicolasgnyra.stagelightplanner.components;

import com.nicolasgnyra.stagelightplanner.LightDefinition;
import com.nicolasgnyra.stagelightplanner.helpers.PaintHelper;
import com.nicolasgnyra.stagelightplanner.transferables.StageElementTransferable;

import java.awt.*;
import java.awt.datatransfer.Transferable;

public class JLightDefinition extends JStageElementDefinition {
    private LightDefinition definition;

    public JLightDefinition(LightDefinition definition) {
        super();
        this.definition = definition;
        setToolTipText(definition.getDisplayName());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D)g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(definition.getDisplayColor());
        PaintHelper.drawShape(g2d, definition.getShape(), getWidth(), getHeight());

        g2d.setColor(PaintHelper.getHueBasedOnBackgroundColor(definition.getDisplayColor()));
        g2d.setFont(g2d.getFont().deriveFont(50.0f));

        PaintHelper.drawScaledString(g2d, definition.getLabel(), getWidth(), getHeight(), 2);
    }

    @Override
    Transferable getTransferable() {
        return new StageElementTransferable(definition);
    }
}
