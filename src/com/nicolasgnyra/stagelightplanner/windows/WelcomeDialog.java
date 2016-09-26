package com.nicolasgnyra.stagelightplanner.windows;

import com.nicolasgnyra.stagelightplanner.components.JActionButton;
import com.nicolasgnyra.stagelightplanner.helpers.GridBagLayoutHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

class WelcomeDialog extends JDialog {
    WelcomeDialog(final PlannerWindow owner) {

        // call superclass constructor with supplied parent & as application modal
        super(owner, ModalityType.APPLICATION_MODAL);

        setTitle("Welcome to Stage Light Planner!");
        setResizable(false);

        JPanel contentPane = new JPanel(new GridBagLayout());

        JLabel titleLabel = new JLabel("Welcome to Stage Light Planner!");
        titleLabel.setFont(titleLabel.getFont().deriveFont(24f));
        contentPane.add(titleLabel, GridBagLayoutHelper.getGridBagLayoutConstraints(0, 0));

        JPanel buttons = new JPanel(new GridLayout(2, 1));
        buttons.setPreferredSize(new Dimension(150, 92));

        buttons.add(new JActionButton("Create a new plan", e -> {
            owner.clear();
            setVisible(false);
        }));

        buttons.add(new JActionButton("Open a plan", e -> {
            if (owner.open())
                setVisible(false);
        }));

        contentPane.add(buttons, GridBagLayoutHelper.getGridBagLayoutConstraints(0, 1, GridBagConstraints.CENTER));

        setContentPane(contentPane);

        setSize(new Dimension(500, 300));

        // center window on parent
        setLocationRelativeTo(owner);

    }
}
