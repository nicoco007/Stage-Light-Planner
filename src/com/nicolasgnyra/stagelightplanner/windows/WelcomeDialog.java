package com.nicolasgnyra.stagelightplanner.windows;

import com.nicolasgnyra.stagelightplanner.components.JActionButton;
import com.nicolasgnyra.stagelightplanner.helpers.GridBagLayoutHelper;

import javax.swing.*;
import java.awt.*;

/**
 * WelcomeDialog Class:
 * A simple window that is shown when the program is started to prompt the user to create a new stage plan or open an existing one.
 *
 * Date: 2016-09-27
 *
 * @author Nicolas Gnyra
 * @version 1.0
 */
class WelcomeDialog extends JDialog {

    /**
     * WelcomeDialog(PlannerWindow) Constructor:
     * Creates a new instance of the WelcomeDialog class.
     *
     * Input: Parent planner window.
     *
     * Process: Sets title, adds label & buttons & actions.
     *
     * Output: A new instance of the WelcomeDialog class.
     *
     * @param owner Owner/parent window
     */
    WelcomeDialog(final PlannerWindow owner) {

        // call superclass constructor with supplied parent & as application modal
        super(owner, ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // set title & prevent resizing
        setTitle("Welcome to Stage Light Planner!");
        setResizable(false);

        // create content pane with grid bag layout
        JPanel contentPane = new JPanel(new GridBagLayout());

        // create title label, set font, and add to content pane
        JLabel titleLabel = new JLabel("Welcome to Stage Light Planner!");
        titleLabel.setFont(titleLabel.getFont().deriveFont(24f));
        contentPane.add(titleLabel, GridBagLayoutHelper.getGridBagLayoutConstraints(0, 0, GridBagConstraints.CENTER, 1, 1, 0, 0, false, false, new Insets(0, 0, 20, 0)));

        // create buttons panel & set its preferred size
        JPanel buttons = new JPanel(new GridLayout(2, 1));
        buttons.setPreferredSize(new Dimension(75 * 2, 46 * 2));

        // add "new" button
        buttons.add(new JActionButton("Create a new plan", e -> {

            // clear parent & hide this window
            owner.clear();
            setVisible(false);

        }));

        // add "open" button
        buttons.add(new JActionButton("Open an existing plan", e -> {

            // open & make sure it worked before closing the welcome window
            if (owner.open())
                setVisible(false);

        }));

        // add buttons panel to content pane
        contentPane.add(buttons, GridBagLayoutHelper.getGridBagLayoutConstraints(0, 1, GridBagConstraints.CENTER));

        // set content pane
        setContentPane(contentPane);

        // set size
        setSize(new Dimension(500, 300));

        // center window on parent
        setLocationRelativeTo(owner);

    }

}
