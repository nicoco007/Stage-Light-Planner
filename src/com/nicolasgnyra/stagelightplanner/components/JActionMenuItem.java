package com.nicolasgnyra.stagelightplanner.components;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * JActionMenuItem Class:
 * A JMenuItem with an action listener in the constructor and an optional accelerator key combination for
 * easy single liners
 *
 * Date: 2016-09-26
 *
 * @author Nicolas Gnyra
 * @version 1.0
 */
public class JActionMenuItem extends JMenuItem {

    /**
     * JActionMenuItem(String, ActionListener) Constructor:
     * Creates a new JMenuItem with the specified text and action listener.
     *
     * Input: Menu item text, action listener.
     *
     * Process: None.
     *
     * Output: A new instance of the JActionMenuItem class.
     *
     * @param text Menu item text
     * @param actionListener Action listener
     */
    public JActionMenuItem(String text, ActionListener actionListener) {
        this(text, actionListener, null);
    }

    /**
     * JActionMenuItem(String, ActionListener) Constructor:
     * Creates a new JMenuItem with the specified text and action listener.
     *
     * Input: Menu item text, action listener, shortcut.
     *
     * Process: None.
     *
     * Output: A new instance of the JActionMenuItem class.
     *
     * @param text Menu item text
     * @param actionListener Action listener
     * @param accelerator Key combination shortcut (e.g. CTRL + S)
     */
    public JActionMenuItem(String text, ActionListener actionListener, KeyStroke accelerator) {
        super(text);
        addActionListener(actionListener);
        setAccelerator(accelerator);
    }

}
