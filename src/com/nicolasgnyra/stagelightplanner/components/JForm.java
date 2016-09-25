package com.nicolasgnyra.stagelightplanner.components;

import com.nicolasgnyra.stagelightplanner.FormElement;
import com.nicolasgnyra.stagelightplanner.JComboBoxItem;
import com.nicolasgnyra.stagelightplanner.helpers.GridBagLayoutHelper;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.util.function.Consumer;

@SuppressWarnings("Convert2Diamond")
public class JForm extends JPanel {
    public JForm() {
        super(new GridBagLayout());
    }

    private int getRow() {
        return getComponentCount() / 2;
    }

    private JLabel addLabel(String text) {
        JLabel label = new JLabel(text, JLabel.LEADING);
        add(label, GridBagLayoutHelper.getGridBagLayoutConstraints(0, getRow(), GridBagConstraints.EAST, 1, 1, 0, 0, false, false, new Insets(5, 5, 5, 5)));
        return label;
    }

    public void addTextField(String label, String value, Consumer<String> onUpdate) {
        addTextField(label, value, onUpdate, false);
    }

    public FormElement<JTextComponent> addTextField(String text, String value, Consumer<String> onUpdate, boolean multiline) {
        JLabel label = addLabel(text);

        JTextComponent input;

        if (multiline) {
            JTextPane textArea = new JTextPane();
            textArea.setText(value);
            textArea.setBackground(null);
            textArea.setFont(new JTextField().getFont());

            SimpleAttributeSet attributeSet = new SimpleAttributeSet();
            StyleConstants.setSpaceBelow(attributeSet, 5.0f);
            textArea.setParagraphAttributes(attributeSet, false);

            add(new JScrollPane(textArea) {
                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(super.getPreferredSize().width, 100);
                }
            }, GridBagLayoutHelper.getGridBagLayoutConstraints(1, getRow(), GridBagConstraints.CENTER, 1, 1, 0.5f, 0, true, false));

            input = textArea;
        } else {
            input = new JTextField(value);

            add(input, GridBagLayoutHelper.getGridBagLayoutConstraints(1, getRow(), GridBagConstraints.CENTER, 1, 1, 0.5f, 0, true, false));
        }

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
            }
        });

        revalidate();

        return new FormElement<JTextComponent>(label, input);
    }

    public FormElement<JSpinner> addNumberField(String text, double value, Consumer<Double> onUpdate, double min, double max, double step, int decimalPlaces) {
        JLabel label = addLabel(text);

        final JSpinner spinner = new JSpinner(new SpinnerNumberModel(value, min, max, step));

        if (decimalPlaces > 0) {
            JSpinner.NumberEditor numberEditor = new JSpinner.NumberEditor(spinner, "0." + new String(new char[decimalPlaces]).replace('\0', '0'));
            numberEditor.getTextField().setColumns(6);
            spinner.setEditor(numberEditor);
        }

        add(spinner, GridBagLayoutHelper.getGridBagLayoutConstraints(1, getRow(), GridBagConstraints.CENTER, 1, 1, 0.5f, 0, true, false));

        spinner.addChangeListener((e) -> onUpdate.accept(((Number)spinner.getValue()).doubleValue()));

        revalidate();

        return new FormElement<JSpinner>(label, spinner);
    }

    public FormElement<JColorChooserButton> addColorField(String text, Color color, Consumer<Color> onUpdate) {
        JLabel label = addLabel(text);

        final JColorChooserButton colorChooser = new JColorChooserButton(color);

        add(colorChooser, GridBagLayoutHelper.getGridBagLayoutConstraints(1, getRow(), GridBagConstraints.CENTER, 1, 1, 0.5f, 0, true, false));

        colorChooser.addColorChangedListener(onUpdate::accept);

        revalidate();

        return new FormElement<JColorChooserButton>(label, colorChooser);
    }

    public <T> FormElement<JComboBox<JComboBoxItem<T>>> addComboBoxField(String text, String[] labels, T[] values, T selectedValue, Consumer<T> onUpdate) {
        if (labels.length != values.length)
            throw new IllegalArgumentException("Both arrays must have the same amount of items.");

        JLabel label = addLabel(text);

        final JComboBox<JComboBoxItem<T>> comboBox = new JComboBox<>();

        for (int i = 0; i < values.length; i++) {
            comboBox.addItem(new JComboBoxItem<>(labels[i], values[i]));

            if (values[i].equals(selectedValue))
                comboBox.setSelectedIndex(i);
        }

        comboBox.setPrototypeDisplayValue(new JComboBoxItem<>("", values[0]));

        add(comboBox, GridBagLayoutHelper.getGridBagLayoutConstraints(1, getRow(), GridBagConstraints.CENTER, 1, 1, 0.5f, 0, true, false));

        comboBox.addActionListener(e -> {
            @SuppressWarnings("unchecked")
            T value = ((JComboBoxItem<T>)comboBox.getSelectedItem()).getValue();
            onUpdate.accept(value);
        });

        revalidate();

        return new FormElement<JComboBox<JComboBoxItem<T>>>(label, comboBox);
    }

    public FormElement<JCheckBox> addCheckBox(String text, boolean value, Consumer<Boolean> onUpdate) {
        JLabel label = addLabel("");

        final JCheckBox checkBox = new JCheckBox(text, value);

        checkBox.addActionListener(e -> onUpdate.accept(checkBox.isSelected()));

        add(checkBox, GridBagLayoutHelper.getGridBagLayoutConstraints(1, getRow(), GridBagConstraints.CENTER, 1, 1, 0.5f, 0, true, false));

        revalidate();

        return new FormElement<JCheckBox>(label, checkBox);
    }

    public FormElement<JSlider> addSlider(String text, Integer value, Integer min, Integer max, int minorTickInterval, int majorTickInterval, Consumer<Integer> onUpdate) {
        JLabel label = addLabel(text);

        JSlider slider = new JSlider(JSlider.HORIZONTAL, min, max, value);

        slider.addChangeListener(e -> onUpdate.accept(slider.getValue()));

        slider.setPreferredSize(new Dimension(0, 40));
        slider.setPaintLabels(true);
        slider.setPaintTicks(true);
        slider.setMinorTickSpacing(minorTickInterval);
        slider.setMajorTickSpacing(majorTickInterval);

        add(slider, GridBagLayoutHelper.getGridBagLayoutConstraints(1, getRow(), GridBagConstraints.CENTER, 1, 1, 0.5f, 0, true, false));

        revalidate();

        return new FormElement<JSlider>(label, slider);
    }

    public void addVerticalGlue() {
        GridBagLayoutHelper.addVerticalGlue(this, getRow());
    }

    public void empty() {
        removeAll();
        revalidate();
        repaint();
    }
}
