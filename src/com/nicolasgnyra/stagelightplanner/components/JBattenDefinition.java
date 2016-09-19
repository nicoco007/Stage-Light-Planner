package com.nicolasgnyra.stagelightplanner.components;

import com.nicolasgnyra.stagelightplanner.transferables.StageElementTransferable;

import java.awt.*;
import java.awt.datatransfer.Transferable;

public class JBattenDefinition extends JStageElementDefinition {
    public JBattenDefinition() {
        super();
        setToolTipText("Batten");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D)g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.gray);
        g2d.fillRect(0, 20, 49, 10);
    }

    @Override
    Transferable getTransferable() {
        return new StageElementTransferable(null);
    }
}
