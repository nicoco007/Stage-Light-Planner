package com.nicolasgnyra.stagelightplanner;

public class JComboBoxItem<T> {
    private String label;
    private T value;

    public JComboBoxItem(String label, T value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return label;
    }
}
