package com.nicolasgnyra.stagelightplanner.components;

import com.nicolasgnyra.stagelightplanner.LightDefinition;
import com.nicolasgnyra.stagelightplanner.layouts.WrapLayout;

import javax.swing.*;
import java.awt.*;

/**
 * JFixturesContainer Class:
 * Panel that contains fixture definitions that can be drag-and-dropped onto the stage planner.
 *
 * Date: 2016-09-26
 *
 * @author Nicolas Gnyra
 * @version 1.0
 */
public class JFixturesContainer extends JPanel {

    private final JPanel definitionContainer;     // Light definition container

    /**
     * JFixturesContainer() Constructor:
     * Creates a new instance of the JFixturesContainer class.
     *
     * Input: None.
     *
     * Process: Sets up the panel's components.
     *
     * Output: None.
     */
    public JFixturesContainer() {

        // setup JPanel with border layout
        super(new BorderLayout());

        // initialize definitions container with wrap layout
        definitionContainer = new JPanel(new WrapLayout(WrapLayout.LEFT));

        // create scroll pane from definition container, remove border & add to main panel
        JScrollPane scrollPane = new JScrollPane(definitionContainer);
        scrollPane.setBorder(null);
        add(scrollPane);

    }

    /**
     * clear() Method:
     * Clears the definitions from the container.
     *
     * Input: None.
     *
     * Process: Removes all components from the light definition container.
     *
     * Output: None.
     */
    public void clear() {
        definitionContainer.removeAll();
    }

    /**
     * addElement(JStageElementDefinition) Method:
     * Adds a JStageElementDefinition to the container.
     *
     * Input: JStageElementDefinition to add to the container.
     *
     * Process: Adds the specified element and repaints & revalidates the container.
     *
     * Output: None.
     *
     * @param stageElementDefinition JStageElementDefinition to add to the container.
     */
    public void addElement(JStageElementDefinition stageElementDefinition) {
        definitionContainer.add(stageElementDefinition);
        definitionContainer.revalidate();
        definitionContainer.repaint();
    }

    /**
     * addLight(LightDefinition) Method:
     * Adds a light definition to the container.
     *
     * Input: Light definition to add.
     *
     * Process: Calls addElement() with a new JLightDefinition.
     *
     * Output: None.
     *
     * @param def Light definition to add.
     */
    public void addLight(LightDefinition def) {
        addElement(new JLightDefinition(def));
    }

}
