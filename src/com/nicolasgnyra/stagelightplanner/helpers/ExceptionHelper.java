package com.nicolasgnyra.stagelightplanner.helpers;

import javax.swing.*;
import java.awt.*;

public class ExceptionHelper {
    public static void showErrorDialog(Component parent, Exception ex) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JTextArea message = new JTextArea(String.format("An error of type %s occurred: %s", ex.getClass().getSimpleName(), ex.getMessage()));
        message.setLineWrap(true);
        message.setWrapStyleWord(true);
        message.setFont(UIManager.getLookAndFeelDefaults().getFont("Label.font"));
        message.setBackground(UIManager.getLookAndFeelDefaults().getColor("Label.background"));
        message.setEditable(false);

        JTextPane stackTrace = new JTextPane();
        String stackTraceString = "";

        for (StackTraceElement stackTraceElement : ex.getStackTrace())
            stackTraceString += stackTraceElement.toString() + "\n";

        stackTrace.setText(stackTraceString);
        stackTrace.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(stackTrace);
        scrollPane.setPreferredSize(new Dimension(600, 300));

        panel.add(message);
        panel.add(scrollPane);
        panel.validate();

        JOptionPane.showMessageDialog(parent, panel, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
