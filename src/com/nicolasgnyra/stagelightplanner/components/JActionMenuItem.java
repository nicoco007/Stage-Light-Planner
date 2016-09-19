package com.nicolasgnyra.stagelightplanner.components;

import javax.swing.*;
import java.awt.event.ActionListener;

public class JActionMenuItem extends JMenuItem {
    public JActionMenuItem(String text, ActionListener actionListener) {
        super(text);
        addActionListener(actionListener);
    }
}
