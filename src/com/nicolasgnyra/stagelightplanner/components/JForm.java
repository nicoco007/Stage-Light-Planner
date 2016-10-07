package com.nicolasgnyra.stagelightplanner.components;

import com.nicolasgnyra.stagelightplanner.FormElement;
import com.nicolasgnyra.stagelightplanner.ComboBoxItem;
import com.nicolasgnyra.stagelightplanner.helpers.GridBagLayoutHelper;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * JForm Class:
 * A JPanel that acts like a form.
 *
 * Date: 2016-09-26
 *
 * @author Nicolas Gnyra
 * @version 1.0
 */
@SuppressWarnings({"Convert2Diamond", "UnusedReturnValue"}) // removes some warnings that, if corrected, trigger errors
public class JForm extends JPanel {

    private final ArrayList<ChangeListener> changeListeners = new ArrayList<>();  // list of change listeners

    /**
     * JForm() Constructor:
     * Creates a new instance of the JForm class.
     *
     * Input: None.
     *
     * Process: Calls the superclass constructor with a new GridBag layout.
     *
     * Output: A new instance of the JForm class.
     */
    public JForm() {
        super(new GridBagLayout());
    }

    /**
     * getRow() Method:
     * Returns the current row based on the amount of components & amount of columns.
     *
     * Input: None.
     *
     * Process: None.
     *
     * Output: Row number based on component and column count.
     *
     * @return Row number based on component and column count.
     */
    private int getRow() {
        return getComponentCount() / 2;
    }

    /**
     * addLabel(String) Method:
     * Adds a label to the layout.
     *
     * Input: Text to display as the label.
     *
     * Process: Creates and adds the label to the panel.
     *
     * Output: The added JLabel.
     *
     * @param text Text to display as the label.
     *
     * @return The added JLabel.
     */
    private JLabel addLabel(String text) {
        JLabel label = new JLabel(text, JLabel.LEADING);
        add(label, GridBagLayoutHelper.getGridBagLayoutConstraints(0, getRow(), GridBagConstraints.EAST, 1, 1, 0, 0, false, false, new Insets(5, 5, 5, 5)));
        return label;
    }

    /**
     * addTextField(String, String, Consumer<String>) Method:
     * Adds a text field to the form.
     *
     * Input: Label text, starting value, update consumer.
     *
     * Process: Add the label, field, and registers the consumer to the input's listener.
     *
     * Output: The added form element (label + input)
     *
     * @param text Label text
     * @param value Starting value
     * @param onUpdate Update consumer
     */
    public void addTextField(String text, String value, Consumer<String> onUpdate) {
        addTextField(text, value, onUpdate, false);
    }

    /**
     * addTextField(String, String, Consumer<String>, boolean) Method:
     * Adds a text field to the form.
     *
     * Input: Label text, starting value, update consumer, whether the input is multi-line or not.
     *
     * Process: Add the label, field, and registers the consumer to the input's listener.
     *
     * Output: The added form element (label + input)
     *
     * @param text Label text
     * @param value Starting value
     * @param onUpdate Update consumer
     */
    public FormElement<JTextComponent> addTextField(String text, String value, Consumer<String> onUpdate, boolean multiline) {

        // add the label
        JLabel label = addLabel(text);

        // create the input variable
        JTextComponent input;

        // check if we are adding a multi-line input
        if (multiline) {

            // create a text pane, and set the text, background and font
            JTextPane textPane = new JTextPane();
            textPane.setText(value);
            textPane.setBackground(null);
            textPane.setFont(UIManager.getLookAndFeelDefaults().getFont("Label.font"));

            // create a simple attribute set, set the paragraph spacing, and set the paragraph attributes of the text pane
            SimpleAttributeSet attributeSet = new SimpleAttributeSet();
            StyleConstants.setSpaceBelow(attributeSet, 5.0f);
            textPane.setParagraphAttributes(attributeSet, false);

            // add the text pane to the form in a scroll pane, with a minimum height of 100 pixels
            add(new JScrollPane(textPane) {
                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(super.getPreferredSize().width, 100);
                }
            }, GridBagLayoutHelper.getGridBagLayoutConstraints(1, getRow(), GridBagConstraints.CENTER, 1, 1, 0.5f, 0, true, false));

            // set input to created text pane
            input = textPane;

        } else {

            // set input to new JTextField
            input = new JTextField(value);

            // add text field to form
            add(input, GridBagLayoutHelper.getGridBagLayoutConstraints(1, getRow(), GridBagConstraints.CENTER, 1, 1, 0.5f, 0, true, false));

        }

        // add document listener
        input.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }

            void update() {
                onUpdate.accept(input.getText());
                valueChanged(input);
            }
        });

        // revalidate form
        revalidate();

        // return input
        return new FormElement<JTextComponent>(label, input);

    }

    /**
     * addNumberField(String, double, Consumer<Double>, double, double, double, int) Method:
     * Adds a number field to the form.
     *
     * Input: Label text, starting value, update consumer, minimum value, maximum value, step & decimal places.
     *
     * Process: Add the label, field, and registers the consumer to the input's listener.
     *
     * Output: The added form element (label + input)
     *
     * @param text Label text
     * @param value Starting value
     * @param onUpdate Update consumer
     * @param min Minimum value
     * @param max Maximum value
     * @param step Amount between steps
     * @param decimalPlaces Decimal places
     */
    public FormElement<JSpinner> addNumberField(String text, double value, Consumer<Double> onUpdate, double min, double max, double step, int decimalPlaces) {

        // add label
        JLabel label = addLabel(text);

        // create spinner with specified default, min, max, and step values
        final JSpinner spinner = new JSpinner(new SpinnerNumberModel(value, min, max, step));

        // check if we have more than 1 decimal place
        if (decimalPlaces > 0) {

            // create number editor with specified amount of decimal places
            JSpinner.NumberEditor numberEditor = new JSpinner.NumberEditor(spinner, "0." + new String(new char[decimalPlaces]).replace('\0', '0'));

            // set amount of columns
            numberEditor.getTextField().setColumns(6);

            // set spinner editor
            spinner.setEditor(numberEditor);

        }

        // add spinner to form
        add(spinner, GridBagLayoutHelper.getGridBagLayoutConstraints(1, getRow(), GridBagConstraints.CENTER, 1, 1, 0.5f, 0, true, false));

        // add change listener to spinner
        spinner.addChangeListener((e) -> {
            onUpdate.accept(((Number)spinner.getValue()).doubleValue());
            valueChanged(spinner);
        });

        // revalidate
        revalidate();

        // return input
        return new FormElement<JSpinner>(label, spinner);

    }

    /**
     * addColorField(String, Color, Consumer<Color>) Method:
     * Adds a color selection field to the form.
     *
     * Input: Label text, starting color, update consumer.
     *
     * Process: Add the label, field, and registers the consumer to the input's listener.
     *
     * Output: The added form element (label + input)
     *
     * @param text Label text
     * @param color Starting color
     * @param onUpdate Update consumer
     */
    public FormElement<JColorChooserButton> addColorField(String text, Color color, Consumer<Color> onUpdate) {

        // add label
        JLabel label = addLabel(text);

        // create color chooser button
        final JColorChooserButton colorChooser = new JColorChooserButton(color);

        // add color chooser button to form
        add(colorChooser, GridBagLayoutHelper.getGridBagLayoutConstraints(1, getRow(), GridBagConstraints.CENTER, 1, 1, 0.5f, 0, true, false));

        // add change listener
        colorChooser.addColorChangedListener(value -> {
            onUpdate.accept(value);
            valueChanged(colorChooser);
        });

        // revalidate form
        revalidate();

        // return input
        return new FormElement<JColorChooserButton>(label, colorChooser);

    }

    /**
     * addComboBoxField(String, String[], T[], T, Consumer<T>) Method:
     * Adds a combo box to the form.
     *
     * Input: Label text, labels, values, selected value, update consumer.
     *
     * Process: Add the label, field, and registers the consumer to the input's listener.
     *
     * Output: The added form element (label + input)
     *
     * @param text Label text
     * @param labels Value labels
     * @param values Values
     * @param selectedValue Currently selected value
     * @param onUpdate Update consumer
     */
    public <T> FormElement<JComboBox<ComboBoxItem<T>>> addComboBoxField(String text, String[] labels, T[] values, T selectedValue, Consumer<T> onUpdate) {

        // check if arrays are of equal length, if not throw exception
        if (labels.length != values.length)
            throw new IllegalArgumentException("Both arrays must have the same amount of items.");

        // add label
        JLabel label = addLabel(text);

        // create combo box
        final JComboBox<ComboBoxItem<T>> comboBox = new JComboBox<>();

        // iterate through values
        for (int i = 0; i < values.length; i++) {

            // add value & label to combo box
            comboBox.addItem(new ComboBoxItem<>(labels[i], values[i]));

            // if value is equal to selected value, set selected index
            if (values[i].equals(selectedValue))
                comboBox.setSelectedIndex(i);

        }

        // make combo box as small as possible (prevents form from stretching out of borders)
        comboBox.setPrototypeDisplayValue(new ComboBoxItem<>("", values[0]));

        // add combo box to form
        add(comboBox, GridBagLayoutHelper.getGridBagLayoutConstraints(1, getRow(), GridBagConstraints.CENTER, 1, 1, 0.5f, 0, true, false));

        // add action listener
        comboBox.addActionListener(e -> {
            @SuppressWarnings("unchecked")

            // get the value
            T value = ((ComboBoxItem<T>)comboBox.getSelectedItem()).getValue();

            // accept the change
            onUpdate.accept(value);
            valueChanged(comboBox);

        });

        // revalidate
        revalidate();

        // return input
        return new FormElement<JComboBox<ComboBoxItem<T>>>(label, comboBox);

    }

    /**
     * addCheckBoxField(String, boolean, Consumer<Boolean>) Method:
     * Adds a check box to the form.
     *
     * Input: Label text, starting value, update consumer.
     *
     * Process: Add the label, field, and registers the consumer to the input's listener.
     *
     * Output: The added form element (label + input)
     *
     * @param text Label text
     * @param value Starting value
     * @param onUpdate Update consumer
     */
    public FormElement<JCheckBox> addCheckBoxField(String text, boolean value, Consumer<Boolean> onUpdate) {

        // add empty label (label is next to check box)
        JLabel label = addLabel("");

        // create check box
        final JCheckBox checkBox = new JCheckBox(text, value);

        // add check box to form
        add(checkBox, GridBagLayoutHelper.getGridBagLayoutConstraints(1, getRow(), GridBagConstraints.CENTER, 1, 1, 0.5f, 0, true, false));

        // add listener
        checkBox.addActionListener(e -> {
            onUpdate.accept(checkBox.isSelected());
            valueChanged(checkBox);
        });

        // revalidate
        revalidate();

        // return input
        return new FormElement<JCheckBox>(label, checkBox);

    }

    /**
     * addSliderField(String, Integer, Integer, Integer, int, int, Consumer<Integer>) Method:
     * Adds a slider to the form.
     *
     * Input: Label text, starting value, update consumer, minimum value, maximum value, step & decimal places, minor &
     * major tick interval.
     *
     * Process: Add the label, field, and registers the consumer to the input's listener.
     *
     * Output: The added form element (label + input)
     *
     * @param text Label text
     * @param value Starting value
     * @param min Minimum value
     * @param max Maximum value
     * @param minorTickInterval Amount between small ticks
     * @param majorTickInterval Amount between large ticks
     * @param onUpdate Update consumer
     */
    public FormElement<JSlider> addSliderField(String text, Integer value, Integer min, Integer max, int minorTickInterval, int majorTickInterval, Consumer<Integer> onUpdate) {

        // add label
        JLabel label = addLabel(text);

        // create slider with default/min/max values
        JSlider slider = new JSlider(JSlider.HORIZONTAL, min, max, value);

        // set preferred size to minimum
        slider.setPreferredSize(new Dimension(0, 40));

        // set & paint ticks
        slider.setPaintLabels(true);
        slider.setPaintTicks(true);
        slider.setMinorTickSpacing(minorTickInterval);
        slider.setMajorTickSpacing(majorTickInterval);

        // add slider to form
        add(slider, GridBagLayoutHelper.getGridBagLayoutConstraints(1, getRow(), GridBagConstraints.CENTER, 1, 1, 0.5f, 0, true, false));

        // add change listener
        slider.addChangeListener(e -> {
            onUpdate.accept(slider.getValue());
            valueChanged(slider);
        });

        // revalidate
        revalidate();

        // return input
        return new FormElement<JSlider>(label, slider);

    }

    /**
     * addVerticalGlue() Method:
     * Adds vertical glue (pushes elements between to each end) at the current row.
     *
     * Input: None.
     *
     * Process: Adds vertical glue.
     *
     * Output: None.
     */
    public void addVerticalGlue() {
        GridBagLayoutHelper.addVerticalGlue(this, getRow());
    }

    /**
     * empty() Method:
     * Removes all components and revalidates & repaints the panel.
     *
     * Input: None.
     *
     * Process: Removes all components and revalidates & repaints the panel.
     *
     * Output: Empty JForm.
     */
    public void empty() {
        removeAll();
        revalidate();
        repaint();
    }

    /**
     * valueChanged(JComponent) Method:
     * Triggers a value changed event across the registered change listeners.
     *
     * Input: Source component.
     *
     * Process: Iterates through the registered listeners and triggers each of them.
     *
     * Output: None.
     *
     * @param source Source component
     */
    private void valueChanged(JComponent source) {
        for (ChangeListener changeListener : changeListeners)
            changeListener.stateChanged(new ChangeEvent(source));
    }

    /**
     * addChangeListener(ChangeListener) Method:
     * Adds a change listener to the list of change listeners.
     *
     * Input: Change listener to add.
     *
     * Process: Adds the specified change listener to the list.
     *
     * Output: New color listener in the listeners list.
     *
     * @param changeListener Change listener to add.
     */
    public void addChangeListener(ChangeListener changeListener) {
        changeListeners.add(changeListener);
    }
}
