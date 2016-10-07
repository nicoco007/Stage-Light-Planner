package com.nicolasgnyra.stagelightplanner.helpers;

import javax.swing.*;
import java.awt.*;

/**
 * ExceptionHelper Class:
 * Contains static methods that help dealing with exceptions.
 *
 * Date: 2016-09-27
 *
 * @author Nicolas Gnyra
 * @version 1.0
 */
public class ExceptionHelper {

    /**
     * showErrorDialog(Component, Exception) Method:
     * Shows an error dialog with the details of the exception.
     *
     * Input: Parent component & exception.
     *
     * Process: Creates and shows a message dialog containing the exception's message and stack trace.
     *
     * Output: Shown error dialog.
     *
     * @param parent Dialog's parent component
     * @param ex Exception
     */
    public static void showErrorDialog(Component parent, Exception ex) {

        // create panel & set layout
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // create message text area
        JTextArea message = new JTextArea(String.format("An error of type %s occurred: %s", ex.getClass().getSimpleName(), ex.getMessage()));
        message.setLineWrap(true);
        message.setWrapStyleWord(true);
        message.setFont(UIManager.getLookAndFeelDefaults().getFont("Label.font"));
        message.setBackground(UIManager.getLookAndFeelDefaults().getColor("Label.background"));
        message.setEditable(false);

        // create stack trace text pane
        JTextPane stackTrace = new JTextPane();
        String stackTraceString = "";

        // convert stack trace to string
        for (StackTraceElement stackTraceElement : ex.getStackTrace())
            stackTraceString += stackTraceElement.toString() + "\n";

        // set stack trace text area string & prevent editing
        stackTrace.setText(stackTraceString);
        stackTrace.setEditable(false);

        // create scroll pane for stack trace & set preferred size
        JScrollPane scrollPane = new JScrollPane(stackTrace);
        scrollPane.setPreferredSize(new Dimension(600, 300));

        // add elements to panel
        panel.add(message);
        panel.add(scrollPane);

        // validate
        panel.validate();

        // show panel as dialog
        JOptionPane.showMessageDialog(parent, panel, "Error", JOptionPane.ERROR_MESSAGE);

    }

}
