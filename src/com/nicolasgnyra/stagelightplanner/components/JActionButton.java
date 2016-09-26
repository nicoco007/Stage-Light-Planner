package com.nicolasgnyra.stagelightplanner.components;

import javax.swing.*;
import java.awt.event.ActionListener;

public class JActionButton extends JButton {
    public JActionButton(String text, ActionListener actionListener) {
        super(text);
        addActionListener(actionListener);
    }
}
