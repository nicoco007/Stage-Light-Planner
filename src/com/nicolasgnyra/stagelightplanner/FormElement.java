package com.nicolasgnyra.stagelightplanner;

import javax.swing.*;

public class FormElement <T extends JComponent> {
    private JLabel label;
    private T component;

    public FormElement(JLabel label, T component) {
        this.label = label;
        this.component = component;
    }

    public T getComponent() {
        return component;
    }

    public void setVisible(boolean visible) {
        label.setVisible(visible);
        component.setVisible(visible);
    }
}
