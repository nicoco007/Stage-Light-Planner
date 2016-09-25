package com.nicolasgnyra.stagelightplanner.windows;

import com.nicolasgnyra.stagelightplanner.FormElement;
import com.nicolasgnyra.stagelightplanner.LightDefinition;
import com.nicolasgnyra.stagelightplanner.LightShape;
import com.nicolasgnyra.stagelightplanner.components.JForm;
import com.nicolasgnyra.stagelightplanner.helpers.GridBagLayoutHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

class FixtureEditorDialog extends JDialog {
    private JTable table;
    private DefaultTableModel tableModel;
    private ArrayList<LightDefinition> lightDefinitions;
    private JForm form = new JForm();

    FixtureEditorDialog(ArrayList<LightDefinition> existingLights) {
        super();

        setTitle("Fixture Editor");
        setModal(true);

        JPanel contentPane = new JPanel(new BorderLayout());

        JPanel leftContainer = new JPanel(new GridBagLayout());

        tableModel = new DefaultTableModel(new String[] { "Light" }, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setPreferredSize(new Dimension(200, 300));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadFixture(table.getSelectedRow());
            }
        });

        leftContainer.add(table, GridBagLayoutHelper.getGridBagLayoutConstraints(0, 0, GridBagConstraints.CENTER, 2, 1, 1.0f, 1.0f, true, true));
        leftContainer.add(new JButton(new AbstractAction("+") {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewFixture();
            }
        }), GridBagLayoutHelper.getGridBagLayoutConstraints(0, 1, GridBagConstraints.EAST, 1, 1, 1.0f, 0, false, false));

        leftContainer.add(new JButton(new AbstractAction("-") {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeSelectedFixture();
            }
        }), GridBagLayoutHelper.getGridBagLayoutConstraints(1, 1, GridBagConstraints.EAST, 1, 1, 0, 0, false, false));

        form.setPreferredSize(new Dimension(500, 300));

        contentPane.add(leftContainer, BorderLayout.WEST);
        contentPane.add(form, BorderLayout.CENTER);
        contentPane.add(new JLabel("Fixture modifications will not be applied to existing plans.", JLabel.CENTER), BorderLayout.SOUTH);

        setContentPane(contentPane);

        lightDefinitions = existingLights;

        reloadFixtureList();

        setMinimumSize(new Dimension(800, 400));

        pack();
    }

    private void loadFixture(int index) {

        // check the row exists
        if (index >= table.getRowCount() || index < 0) {
            form.empty();
            return;
        }

        final LightDefinition selection = lightDefinitions.get(index);

        form.empty();
        form.addTextField("Name:", selection.getDisplayName(), value -> {
            selection.setDisplayName(value);
            updateFixtureNameInList(index, value);
        });
        form.addTextField("Label:", selection.getLabel(), selection::setLabel);
        form.addColorField("Display Color:", selection.getDisplayColor(), selection::setDisplayColor);
        form.addComboBoxField("Shape:", new String[] { "Circle", "Square", "Triangle", "Diamond", "Pentagon", "Hexagon", "Heptagon", "Octagon", "Nonagon", "Decagon" }, new LightShape[] { LightShape.CIRCLE, LightShape.SQUARE, LightShape.TRIANGLE, LightShape.DIAMOND, LightShape.PENTAGON, LightShape.HEXAGON, LightShape.HEPTAGON, LightShape.OCTAGON, LightShape.NONAGON, LightShape.DECAGON }, selection.getShape(), selection::setShape);

        boolean fieldAngleIsRange = selection.getFieldAngle() == 0 && selection.getFieldAngleMin() > 0 && selection.getFieldAngleMax() > 0;
        FormElement<JCheckBox> checkBox = form.addCheckBox("Field angle is a range", fieldAngleIsRange, value -> {});
        FormElement<JSpinner> fieldAngleSpinner = form.addNumberField("Field Angle:", selection.getFieldAngle(), value -> selection.setFieldAngle(value.floatValue()), 0, 90, 0.1, 1);
        FormElement<JSpinner> fieldAngleMinSpinner = form.addNumberField("Field Angle (min):", selection.getFieldAngleMin(), value -> selection.setFieldAngleMin(value.floatValue()), 0, 90, 0.1, 1);
        FormElement<JSpinner> fieldAngleMaxSpinner = form.addNumberField("Field Angle (max):", selection.getFieldAngleMax(), value -> selection.setFieldAngleMax(value.floatValue()), 0, 90, 0.1, 1);

        checkBox.getComponent().addActionListener(e -> {
            boolean selected = checkBox.getComponent().isSelected();

            fieldAngleSpinner.setVisible(!selected);
            fieldAngleMinSpinner.setVisible(selected);
            fieldAngleMaxSpinner.setVisible(selected);

            fieldAngleSpinner.getComponent().setValue(selected ? 0 : 50);
            fieldAngleMinSpinner.getComponent().setValue(selected ? 30 : 0);
            fieldAngleMaxSpinner.getComponent().setValue(selected ? 50 : 0);
        });

        fieldAngleMinSpinner.getComponent().addChangeListener(e -> {
            double minValue = ((Number)fieldAngleMinSpinner.getComponent().getValue()).doubleValue();
            JSpinner maxSpinner = fieldAngleMaxSpinner.getComponent();

            if (((Number)maxSpinner.getValue()).doubleValue() < minValue)
                maxSpinner.setValue(minValue);
        });

        fieldAngleMaxSpinner.getComponent().addChangeListener(e -> {
            double maxValue = ((Number)fieldAngleMaxSpinner.getComponent().getValue()).doubleValue();
            JSpinner minSpinner = fieldAngleMinSpinner.getComponent();

            if (((Number)minSpinner.getValue()).doubleValue() > maxValue)
                minSpinner.setValue(maxValue);

        });

        for (ActionListener listener : checkBox.getComponent().getActionListeners())
            listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));

        form.addVerticalGlue();
        revalidate();
        repaint();
    }

    private void addNewFixture() {
        addFixtureToList(new LightDefinition("New Light", "Light", LightShape.SQUARE, Color.black, 40.0f));
        table.setRowSelectionInterval(lightDefinitions.size() - 1, lightDefinitions.size() - 1);
    }

    private void removeSelectedFixture() {
        int index = table.getSelectedRow();
        removeFixtureFromList(index);
    }

    private void reloadFixtureList() {
        tableModel.setNumRows(0);

        for (LightDefinition lightDefinition : lightDefinitions)
            tableModel.addRow(new String[] { lightDefinition.getDisplayName() });

        tableModel.fireTableDataChanged();
    }

    private void addFixtureToList(LightDefinition lightDefinition) {
        lightDefinitions.add(lightDefinition);

        tableModel.addRow(new String[] { lightDefinition.getDisplayName() });

        tableModel.fireTableRowsInserted(lightDefinitions.size() - 1, lightDefinitions.size() - 1);
    }

    private void removeFixtureFromList(int index) {
        lightDefinitions.remove(index);

        tableModel.removeRow(index);
        tableModel.fireTableRowsDeleted(index, index);
    }

    private void updateFixtureNameInList(int index, String name) {
        tableModel.setValueAt(name, index, 0);

        tableModel.fireTableRowsUpdated(index, index);
    }
}
