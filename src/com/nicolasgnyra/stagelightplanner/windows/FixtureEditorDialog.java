package com.nicolasgnyra.stagelightplanner.windows;

import com.nicolasgnyra.stagelightplanner.FormElement;
import com.nicolasgnyra.stagelightplanner.LightDefinition;
import com.nicolasgnyra.stagelightplanner.LightShape;
import com.nicolasgnyra.stagelightplanner.components.JActionButton;
import com.nicolasgnyra.stagelightplanner.components.JForm;
import com.nicolasgnyra.stagelightplanner.components.JPreviewPane;
import com.nicolasgnyra.stagelightplanner.helpers.GridBagLayoutHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * FixtureEditorDialog Class:
 * A dialog that allows the user to create his/her own light definitions.
 *
 * Date: 2016-09-27
 *
 * @author Nicolas Gnyra
 * @version 1.0
 */
class FixtureEditorDialog extends JDialog {

    private final JTable table;                                   // list of existing definitions
    private final DefaultTableModel tableModel;                   // existing definitions table model
    private final ArrayList<LightDefinition> lightDefinitions;    // light of light definitions
    private final JForm form = new JForm();                       // editing form
    private final JPreviewPane previewPane = new JPreviewPane();  // preview pane

    /**
     * FixtureEditorDialog(Window, ArrayList<LightDefinition>) Constructor:
     * Creates a new instance of the FixtureEditorDialog class with the specified owner.
     *
     * Input: Owner & existing light definitions.
     *
     * Process: Creates the required components.
     *
     * Output: A new instance of the FixtureEditorDialog class.
     *
     * @param owner Owner window
     * @param existingLights Existing lights list
     */
    FixtureEditorDialog(Window owner, ArrayList<LightDefinition> existingLights) {

        // call superclass constructor with specified owner & application modality type
        super(owner, ModalityType.APPLICATION_MODAL);

        // set title
        setTitle("Fixture Editor");

        // create content pane with border layout & left container with grid bag layout
        JPanel contentPane = new JPanel(new BorderLayout());
        JPanel leftContainer = new JPanel(new GridBagLayout());

        // create table model & prevent cell editing
        tableModel = new DefaultTableModel(new String[] { "Light" }, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        // create table, set size, set selection mode & add selection change listener
        table = new JTable(tableModel);
        table.setPreferredSize(new Dimension(200, 300));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {

            // check that the selected value is final (i.e. user released mouse) & load selected fixture
            if (!e.getValueIsAdjusting())
                loadFixture(table.getSelectedRow());

        });

        // create add and remove buttons
        JActionButton addButton = new JActionButton("+", e -> addNewFixture());
        JActionButton removeButton = new JActionButton("-", e -> removeSelectedFixture());

        // add table & buttons to left container
        leftContainer.add(table, GridBagLayoutHelper.getGridBagLayoutConstraints(0, 0, GridBagConstraints.CENTER, 2, 1, 1.0f, 1.0f, true, true));
        leftContainer.add(addButton, GridBagLayoutHelper.getGridBagLayoutConstraints(0, 1, GridBagConstraints.EAST, 1, 1, 1.0f, 0, false, false));
        leftContainer.add(removeButton, GridBagLayoutHelper.getGridBagLayoutConstraints(1, 1, GridBagConstraints.EAST, 1, 1, 0, 0, false, false));

        // create right container & set layout to box layout on Y axis
        JPanel rightContainer = new JPanel();
        rightContainer.setLayout(new BoxLayout(rightContainer, BoxLayout.Y_AXIS));

        // set preview pane preferred height
        previewPane.setPreferredSize(new Dimension(0, 150));

        // set form preferred size & add change listener
        form.setPreferredSize(new Dimension(500, 300));
        form.addChangeListener(e -> previewPane.repaint());

        // add form & preview pane to right container
        rightContainer.add(form);
        rightContainer.add(previewPane);

        // add both containers to the content pane & add notice label
        contentPane.add(leftContainer, BorderLayout.WEST);
        contentPane.add(rightContainer, BorderLayout.CENTER);
        contentPane.add(new JLabel("Fixture modifications will not be applied to existing plans.", JLabel.CENTER), BorderLayout.SOUTH);

        // set content pane
        setContentPane(contentPane);

        // set light definitions & load them
        lightDefinitions = existingLights;
        reloadFixtureList();

        // set window minimum size
        setMinimumSize(new Dimension(800, 400));

        // pack window
        pack();

    }

    /**
     * loadFixture(int) Method:
     * Loads a fixture from the list into the form.
     *
     * Input: Fixture index.
     *
     * Process: Retrieves the selected fixture from the list and loads its information into the form.
     *
     * Output: None.
     *
     * @param index Index of the fixture in the list.
     */
    private void loadFixture(int index) {

        // empty the form
        form.empty();

        // check the row exists; if not, clear the preview pane
        if (index >= table.getRowCount() || index < 0) {
            previewPane.setLightDefinition(null);
            return;
        }

        // get the light definition
        final LightDefinition selection = lightDefinitions.get(index);

        // set preview pane definition to selection
        previewPane.setLightDefinition(selection);

        // add name text field & set update listener to update name in list as well as update fixture definition
        form.addTextField("Name:", selection.getDisplayName(), value -> {
            selection.setDisplayName(value);
            updateFixtureNameInList(index, value);
        });

        // add label, color, and shape fields
        form.addTextField("Label:", selection.getLabel(), selection::setLabel);
        form.addColorField("Display Color:", selection.getDisplayColor(), selection::setDisplayColor);
        form.addComboBoxField("Shape:", new String[] { "Circle", "Square", "Triangle", "Diamond", "Pentagon", "Hexagon", "Heptagon", "Octagon", "Nonagon", "Decagon" }, new LightShape[] { LightShape.CIRCLE, LightShape.SQUARE, LightShape.TRIANGLE, LightShape.DIAMOND, LightShape.PENTAGON, LightShape.HEXAGON, LightShape.HEPTAGON, LightShape.OCTAGON, LightShape.NONAGON, LightShape.DECAGON }, selection.getShape(), selection::setShape);

        // add check box & spinners for field angles
        FormElement<JCheckBox> checkBox = form.addCheckBox("Field angle is a range", selection.isFieldAngleRange(), value -> {});
        FormElement<JSpinner> fieldAngleSpinner = form.addNumberField("Field Angle:", selection.getFieldAngle(), value -> selection.setFieldAngle(value.floatValue()), 0, 90, 0.1, 1);
        FormElement<JSpinner> fieldAngleMinSpinner = form.addNumberField("Field Angle (min):", selection.getFieldAngleMin(), value -> selection.setFieldAngleMin(value.floatValue()), 0, 90, 0.1, 1);
        FormElement<JSpinner> fieldAngleMaxSpinner = form.addNumberField("Field Angle (max):", selection.getFieldAngleMax(), value -> selection.setFieldAngleMax(value.floatValue()), 0, 90, 0.1, 1);

        // add listener to check box
        checkBox.getComponent().addActionListener(e -> {

            // get if check box is selected
            boolean selected = checkBox.getComponent().isSelected();

            // set visibility of spinners based on selection
            fieldAngleSpinner.setVisible(!selected);
            fieldAngleMinSpinner.setVisible(selected);
            fieldAngleMaxSpinner.setVisible(selected);

            // set values based on selection
            fieldAngleSpinner.getComponent().setValue(selected ? 0 : 50);
            fieldAngleMinSpinner.getComponent().setValue(selected ? 30 : 0);
            fieldAngleMaxSpinner.getComponent().setValue(selected ? 50 : 0);

        });

        // hide/show spinners based on if field angle is a range
        fieldAngleSpinner.setVisible(!selection.isFieldAngleRange());
        fieldAngleMinSpinner.setVisible(selection.isFieldAngleRange());
        fieldAngleMaxSpinner.setVisible(selection.isFieldAngleRange());

        // add change listener to minimum spinner
        fieldAngleMinSpinner.getComponent().addChangeListener(e -> {

            // get current minimum value & max spinner
            double minValue = ((Number)fieldAngleMinSpinner.getComponent().getValue()).doubleValue();
            JSpinner maxSpinner = fieldAngleMaxSpinner.getComponent();

            // set value of max spinner if min spinner goes over it
            if (((Number)maxSpinner.getValue()).doubleValue() < minValue)
                maxSpinner.setValue(minValue);

        });

        // add change listener to maximum spinner
        fieldAngleMaxSpinner.getComponent().addChangeListener(e -> {

            // get current maximum value & min spinner
            double maxValue = ((Number)fieldAngleMaxSpinner.getComponent().getValue()).doubleValue();
            JSpinner minSpinner = fieldAngleMinSpinner.getComponent();

            // set value of min spinner if max spinner goes over it
            if (((Number)minSpinner.getValue()).doubleValue() > maxValue)
                minSpinner.setValue(maxValue);

        });

        // add vertical glue
        form.addVerticalGlue();

        // revalidate & repaint
        revalidate();
        repaint();

    }

    /**
     * addNewFixture() Method:
     * Adds a new fixture to the list.
     *
     * Input: None.
     *
     * Process: Adds a fixture to the list and selects it in the JTable.
     *
     * Output: None.
     */
    private void addNewFixture() {
        addFixtureToList(new LightDefinition("New Light", "Light", LightShape.SQUARE, Color.black, 40.0f));
        table.setRowSelectionInterval(lightDefinitions.size() - 1, lightDefinitions.size() - 1);
    }

    /**
     * removeSelectedFixture() Method:
     * Removes the fixture selected in the JTable from the list.
     *
     * Input: None.
     *
     * Process: Gets the current selected index in the JTable, and removes the fixture at that index from the list.
     *
     * Output: None.
     */
    private void removeSelectedFixture() {
        int index = table.getSelectedRow();
        removeFixtureFromList(index);
    }

    /**
     * reloadFixtureList() Method:
     * Completely empties and (re)loads the fixture list.
     *
     * Input: None.
     *
     * Process: Empties and fills the JTable with the list of light definitions.
     *
     * Output: None.
     */
    private void reloadFixtureList() {

        // clear rows
        tableModel.setNumRows(0);

        // itereate through light definitions and add them to the JTable
        for (LightDefinition lightDefinition : lightDefinitions)
            tableModel.addRow(new String[] { lightDefinition.getDisplayName() });

        // tell the table data was changed
        tableModel.fireTableDataChanged();

    }

    /**
     * addFixtureToList(LightDefinition) Method:
     * Adds the specified light definition to the table & list.
     *
     * Input: Light definition to add to the list.
     *
     * Process: Adds the light definition to the list and adds a row to the JTable.
     *
     * Output: None.
     *
     * @param lightDefinition Light definition to add to the list.
     */
    private void addFixtureToList(LightDefinition lightDefinition) {

        // add definition
        lightDefinitions.add(lightDefinition);

        // add row
        tableModel.addRow(new String[] { lightDefinition.getDisplayName() });

        // tell model row was inserted
        tableModel.fireTableRowsInserted(lightDefinitions.size() - 1, lightDefinitions.size() - 1);

    }

    /**
     * removeFixtureFromList(int) Method:
     * Removes the light definition at the specified index in the definitions list.
     *
     * Input: Index at which to remove the definition.
     *
     * Process: Removes the light definition and the row at that index.
     *
     * Output: None.
     *
     * @param index Index at which to remove the definition.
     */
    private void removeFixtureFromList(int index) {

        // remove definition
        lightDefinitions.remove(index);

        // remove row & tell model row was deleted
        tableModel.removeRow(index);
        tableModel.fireTableRowsDeleted(index, index);

    }

    /**
     * updateFixtureNameInList(int, String) Method:
     * Replaces the name of the fixture at the specified index with the specified string.
     *
     * Input: Index to update, replacement name.
     *
     * Process: sets the value of the row at the specified index to the specified string.
     *
     * Output: None.
     *
     * @param index Index to update.
     * @param name Replacement name.
     */
    private void updateFixtureNameInList(int index, String name) {

        // set value at specified index
        tableModel.setValueAt(name, index, 0);

        // tell table model row was updated
        tableModel.fireTableRowsUpdated(index, index);

    }
}
