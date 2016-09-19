package com.nicolasgnyra.stagelightplanner.components;

import com.nicolasgnyra.stagelightplanner.LightDefinition;
import com.nicolasgnyra.stagelightplanner.layouts.WrapLayout;

import javax.swing.*;
import java.awt.*;

public class JFixturesContainer extends JPanel {
    private JPanel container;

    public JFixturesContainer() {
        super(new BorderLayout());

        setOpaque(true);

        container = new JPanel(new WrapLayout(WrapLayout.LEFT));

        JScrollPane scroller = new JScrollPane(container);

        add(scroller, BorderLayout.CENTER);
    }

    public void clear() {
        container.removeAll();
    }

    public void addElement(JStageElementDefinition stageElementDefinition) {
        container.add(stageElementDefinition);
        container.revalidate();
        container.repaint();
    }

    public void addLight(LightDefinition def) {
        addElement(new JLightDefinition(def));
    }
}
