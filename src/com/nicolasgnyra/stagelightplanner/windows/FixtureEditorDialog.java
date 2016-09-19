package com.nicolasgnyra.stagelightplanner.windows;

import com.nicolasgnyra.stagelightplanner.LightDefinition;
import com.nicolasgnyra.stagelightplanner.LightShape;
import com.nicolasgnyra.stagelightplanner.components.JForm;
import com.nicolasgnyra.stagelightplanner.helpers.GridBagLayoutHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
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

        leftContainer.add(table, GridBagLayoutHelper.getGridBagLayoutConstraints(0, 0, GridBagConstraints.CENTER, 2, 1));
        leftContainer.add(new JButton(new AbstractAction("+") {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewFixure();
            }
        }), GridBagLayoutHelper.getGridBagLayoutConstraints(0, 1, GridBagConstraints.EAST));

        leftContainer.add(new JButton(new AbstractAction("-") {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeSelectedFixture();
            }
        }), GridBagLayoutHelper.getGridBagLayoutConstraints(1, 1, GridBagConstraints.WEST));

        contentPane.add(leftContainer, BorderLayout.WEST);
        contentPane.add(form);

        setContentPane(contentPane);

        lightDefinitions = existingLights;

        reloadFixtureList();

        pack();
    }

    private void loadFixture(int index) {
        LightDefinition selection = lightDefinitions.get(index);
        form.removeAll();
        form.addTextField("Name:", selection.getDisplayName(), value -> {
            selection.setDisplayName(value);
            updateFixtureNameInList(index, value);
        });
        form.addTextField("Label:", selection.getLabel(), selection::setLabel);
        form.addVerticalGlue();
        revalidate();
        repaint();
    }

    private void addNewFixure() {
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

        tableModel.fireTableRowsUpdated(0, lightDefinitions.size() - 1);
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
    }
}
