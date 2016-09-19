package com.nicolasgnyra.stagelightplanner.components;

import com.nicolasgnyra.stagelightplanner.helpers.PaintHelper;
import com.nicolasgnyra.stagelightplanner.transferables.StageElementTransferable;

import java.awt.*;
import java.awt.datatransfer.Transferable;

public class JLabelDefinition extends JStageElementDefinition {
    public JLabelDefinition() {
        super();
        setToolTipText("Label");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        PaintHelper.drawScaledString(g2d, "Label", getWidth(), getHeight(), 0);
    }

    @Override
    Transferable getTransferable() {
        return new StageElementTransferable("Text");
    }
}
