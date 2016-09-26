package com.nicolasgnyra.stagelightplanner.components;

import javax.swing.*;
import java.awt.event.ActionListener;

public class JActionMenuItem extends JMenuItem {
    public JActionMenuItem(String text, ActionListener actionListener) {
        this(text, actionListener, null);
    }

    public JActionMenuItem(String text, ActionListener actionListener, KeyStroke accelerator) {
        super(text);
        addActionListener(actionListener);
        setAccelerator(accelerator);
    }
}
