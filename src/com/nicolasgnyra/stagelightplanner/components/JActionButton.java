package com.nicolasgnyra.stagelightplanner.components;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * JActionButton Class:
 * A JButton that has an action listener in the constructor for simple single liners.
 *
 * Date: 2016-09-26
 *
 * @author Nicolas Gnyra
 * @version 1.0
 */
public class JActionButton extends JButton {

    /**
     * JActionButton(String, ActionListener) Constructor:
     * Creates a new instance of the JActionButton class with the specified text and action listener.
     *
     * Input: Button text & action listener.
     *
     * Process: Calls superclass constructor & adds action listener.
     *
     * Output: A new instance of the JActionButton class.
     *
     * @param text Button text
     * @param actionListener Action listener
     */
    public JActionButton(String text, ActionListener actionListener) {
        super(text);
        addActionListener(actionListener);
    }
}
