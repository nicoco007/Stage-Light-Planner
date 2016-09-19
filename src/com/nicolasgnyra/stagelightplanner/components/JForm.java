package com.nicolasgnyra.stagelightplanner.components;

import com.nicolasgnyra.stagelightplanner.JComboBoxItem;
import com.nicolasgnyra.stagelightplanner.helpers.GridBagLayoutHelper;
import com.nicolasgnyra.stagelightplanner.helpers.SpringHelper;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.util.function.Consumer;

public class JForm extends JPanel {
    public JForm() {
        super(new GridBagLayout());
    }

    private int getRow() {
        return getComponentCount() / 2;
    }

    private void addLabel(String text) {
        add(new JLabel(text, JLabel.LEADING), GridBagLayoutHelper.getGridBagLayoutConstraints(0, getRow(), GridBagConstraints.EAST, 1, 1, 0, 0, false, false, new Insets(5, 5, 5, 5)));
    }

    public void addTextField(String label, String value, Consumer<String> onUpdate) {
        addTextField(label, value, onUpdate, false);
    }

    public void addTextField(String label, String value, Consumer<String> onUpdate, boolean multiline) {
        addLabel(label);

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
    }

    public void addNumberField(String label, double value, Consumer<Double> onUpdate, double min, double max, double step, int decimalPlaces) {
        addLabel(label);

        JSpinner input = new JSpinner(new SpinnerNumberModel(value, min, max, step));

        if (decimalPlaces > 0) {
            JSpinner.NumberEditor numberEditor = new JSpinner.NumberEditor(input, "0." + new String(new char[decimalPlaces]).replace('\0', '0'));
            numberEditor.getTextField().setColumns(6);
            input.setEditor(numberEditor);
        }

        add(input, GridBagLayoutHelper.getGridBagLayoutConstraints(1, getRow(), GridBagConstraints.CENTER, 1, 1, 0.5f, 0, true, false));

        input.addChangeListener((e) -> onUpdate.accept((double)input.getValue()));

        revalidate();
    }

    public void addColorField(String label, Color color, Consumer<Color> onUpdate) {
        addLabel(label);

        JColorChooserButton input = new JColorChooserButton(color);

        add(input, GridBagLayoutHelper.getGridBagLayoutConstraints(1, getRow(), GridBagConstraints.CENTER, 1, 1, 0.5f, 0, true, false));

        input.addColorChangedListener(onUpdate::accept);

        revalidate();
    }

    public <T> void addComboBoxField(String label, String[] labels, T[] values, T selectedValue, Consumer<T> onUpdate) {
        if (labels.length != values.length)
            throw new IllegalArgumentException("Both arrays must have the same amount of items.");

        addLabel(label);

        JComboBox<JComboBoxItem> input = new JComboBox<>();

        for (int i = 0; i < values.length; i++) {
            input.addItem(new JComboBoxItem<>(labels[i], values[i]));

            if (values[i].equals(selectedValue))
                input.setSelectedIndex(i);
        }

        input.setPrototypeDisplayValue(new JComboBoxItem("", values[0]));

        add(input, GridBagLayoutHelper.getGridBagLayoutConstraints(1, getRow(), GridBagConstraints.CENTER, 1, 1, 0.5f, 0, true, false));

        input.addActionListener(e -> {
            @SuppressWarnings("unchecked")
            T value = ((JComboBoxItem<T>)input.getSelectedItem()).getValue();
            onUpdate.accept(value);
        });

        revalidate();
    }

    public void addVerticalGlue() {
        GridBagLayoutHelper.addVerticalGlue(this, getRow());
    }
}
