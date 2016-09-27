package com.nicolasgnyra.stagelightplanner;

/**
 * ComboBoxItem Class:
 * A label/value pair for use in combo boxes.
 *
 * Date: 2016-09-27
 *
 * @author Nicolas Gnyra
 * @version 1.0
 *
 * @param <T> Value type
 */
public class ComboBoxItem<T> {
    private final String label;   // string displayed in combo box
    private final T value;        // value of the item

    /**
     * ComboBoxItem(String, T) Method:
     * Creates a new instance of the ComboBoxItem class.
     *
     * Input: Label & value.
     *
     * Process: Sets values.
     *
     * Output: A new instance of the ComboBoxItem class.
     *
     * @param label String displayed in combo box
     * @param value Value of the item
     */
    public ComboBoxItem(String label, T value) {
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
