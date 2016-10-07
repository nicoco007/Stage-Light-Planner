package com.nicolasgnyra.stagelightplanner.windows;

import com.nicolasgnyra.stagelightplanner.helpers.ExceptionHelper;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * QuickStartDialog Class:
 * Contains the necessary commands to show a quick start guide to the user.
 *
 * Date: 2016-10-07
 *
 * @author Nicolas Gnyra
 * @version 1.0
 */
public class QuickStartDialog extends JDialog {

    /**
     * QuickStartDialog(Window) Constructor:
     * Creates a new instance of the QuickStartDialog class.
     *
     * Input: Parent/owner window on top of which this dialog will be centered.
     *
     * Process: Creates the necessary components.
     *
     * Output: A new instance of the QuickStartDialog class.
     *
     * @param owner Parent/owner window on top of which this dialog will be centered.
     */
    public QuickStartDialog(Window owner) {

        // call superclass constructor
        super(owner);

        // set size, prevent resizing, and set title
        setSize(800, 600);
        setResizable(false);
        setTitle("Quick Start");

        // create content pane
        JPanel contentPane = new JPanel(new BorderLayout());

        // create title label, set font to bigger font, add margin, and add to content pane
        JLabel titleLabel = new JLabel("Quick Start", JLabel.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(32f));
        titleLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.add(titleLabel, BorderLayout.NORTH);

        // wrap in try/catch statement
        try {

            // get image stream from resources
            InputStream res = QuickStartDialog.class.getResourceAsStream("main-diag.png");

            // get image from stream
            BufferedImage image = ImageIO.read(res);

            // create label & set icon
            JLabel label = new JLabel(new ImageIcon(image));

            // add to content pane
            contentPane.add(label, BorderLayout.CENTER);

        } catch (IOException ex) {

            // show error dialog
            ExceptionHelper.showErrorDialog(this, ex);

        }

        // create JTextArea with explanation
        JTextArea descriptionLabel = new JTextArea("To start creating a plan, simply drag-and-drop elements from the " +
                "Fixture List onto the Planning Area. The gray bar icon is a batten, and the Label allows you to " +
                "place arbitrary text anywhere on the plan. To edit a fixture, batten, or label's properties, click " +
                "on it; its properties will be displayed in the Properties Sidebar.");

        // set font, wrapping, & background, prevent editing, and add margin
        descriptionLabel.setFont(titleLabel.getFont().deriveFont(16f));
        descriptionLabel.setLineWrap(true);
        descriptionLabel.setWrapStyleWord(true);
        descriptionLabel.setBackground(null);
        descriptionLabel.setEnabled(false);
        descriptionLabel.setDisabledTextColor(Color.black);
        descriptionLabel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // add to content pane
        contentPane.add(descriptionLabel, BorderLayout.SOUTH);

        // set content pane
        setContentPane(contentPane);

        // validate
        validate();

        // center in parent
        setLocationRelativeTo(owner);

    }

}
