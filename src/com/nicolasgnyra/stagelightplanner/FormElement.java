package com.nicolasgnyra.stagelightplanner;

import javax.swing.*;

/**
 * FormElement Class:
 * A class that wraps a form element (label + input).
 *
 * Date: 2016-09-27
 *
 * @author Nicolas Gnyra
 * @version 1.0
 *
 * @param <T> Component type
 */
public class FormElement <T extends JComponent> {
    private final JLabel label;   // field label
    private final T component;    // field input

    /**
     * FormElement(JLabel, T) Constructor:
     * Creates a new instance of the FormElement class.
     *
     * Input: JLabel & JComponent that create the field.
     *
     * Process: Sets values.
     *
     * Output: A new instance of the FormElement class.
     *
     * @param label JLabel
     * @param component JComponent
     */
    public FormElement(JLabel label, T component) {
        this.label = label;
        this.component = component;
    }

    /**
     * setVisible(boolean) Method:
     * Sets both the label and component's visibility.
     *
     * Input: Whether to show or hide the components.
     *
     * Process: Sets the visibility of both components.
     *
     * Output: Visible/invisible components.
     *
     * @param visible Whether to show or hide the components.
     */
    public void setVisible(boolean visible) {
        label.setVisible(visible);
        component.setVisible(visible);
    }

    public T getComponent() {
        return component;
    }
}
