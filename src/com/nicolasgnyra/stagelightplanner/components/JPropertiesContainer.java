package com.nicolasgnyra.stagelightplanner.components;

import com.nicolasgnyra.stagelightplanner.Orientation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * JPropertiesContainer Class:
 * Container in which a form containing a JStageElement's editable values.
 *
 * Date: 2016-09-25
 *
 * @author Nicolas Gnyra
 * @version 1.0
 */
public class JPropertiesContainer extends JPanel {
    private JTextArea titleLabel;       // large text displayed at the top of the container
    private JPanel attributesPanel;     // subtitles displayed under the main title
    private JForm propertiesPanel;      // form containing values that can be edited

    // default font for labels (by default, JTextAreas use a fixed-width font
    private Font defaultLabelFont = UIManager.getLookAndFeelDefaults().getFont("Label.font");

    /**
     * JPropertiesContainer() Constructor:
     * Creates a new instance of the JPropertiesContainer class.
     *
     * Input: None.
     *
     * Process: Creates the default components to display.
     *
     * Output: None.
     */
    public JPropertiesContainer() {

        // call superclass constructor with new BorderLayout
        super(new BorderLayout());

        // create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());

        // create subtitles panel & set layout to box (linear) layout
        attributesPanel = new JPanel();
        attributesPanel.setLayout(new BoxLayout(attributesPanel, BoxLayout.Y_AXIS));

        // create title label and set necessary values
        titleLabel = new JTextArea();
        titleLabel.setLineWrap(true);
        titleLabel.setWrapStyleWord(true);
        titleLabel.setEnabled(false);
        titleLabel.setFont(defaultLabelFont.deriveFont(Font.BOLD, 24));
        titleLabel.setDisabledTextColor(Color.black);
        titleLabel.setBackground(null);
        titleLabel.setAlignmentY(JLabel.BOTTOM_ALIGNMENT);

        // add labels to header panel
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(attributesPanel, BorderLayout.CENTER);

        // create form & set insets
        propertiesPanel = new JForm();
        propertiesPanel.setBorder(new EmptyBorder(8, 3, 8, 3));

        // create scrollpane for jframe
        JScrollPane scroller = new JScrollPane(propertiesPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // remove scrollpane border
        scroller.setBorder(BorderFactory.createEmptyBorder());

        // add components to main container
        add(headerPanel, BorderLayout.NORTH);
        add(scroller, BorderLayout.CENTER);

        // reset title
        setDefaultTitle();
    }

    /**
     * showProperties(JStageElement) Method:
     * Shows the properties of the specified JStageElement.
     *
     * Input: Element of which to show the properties.
     *
     * Process: Clears the form and adds the necessary fields.
     *
     * Output: None.
     *
     * @param element Element of which to show the properties.
     */
    void showProperties(JStageElement element) {

        // reset title
        setDefaultTitle();

        // empty properties form
        propertiesPanel.removeAll();

        // show properties based on sender element
        if (element instanceof JLight)
            showLightProperties((JLight)element);
        else if (element instanceof JBatten)
            showBattenProperties((JBatten)element);
        else if (element instanceof JDraggableLabel)
            showLabelProperties((JDraggableLabel)element);

        // add vertical glue to stick components to top of form
        propertiesPanel.addVerticalGlue();

        // revalidate & repaint
        revalidate();
        repaint();
    }

    /**
     * showBattenProperties(JBatten) Method:
     * Shows the editable properties for a batten.
     *
     * Input: Batten of which properties will be shown.
     *
     * Process: Creates necessary fields in the form component.
     *
     * Output: None.
     *
     * @param batten Batten to use
     */
    private void showBattenProperties(final JBatten batten) {

        // set title
        setTitle("Batten", new String[0]);

        // add fields
        propertiesPanel.<Double>addNumberField("Height (meters):", batten.getHeightFromFloor() / 100f, value -> batten.setHeightFromFloor((int)(value * 100)), 1, 50, 0.5, 2);
        propertiesPanel.<Double>addNumberField("Length (meters):", batten.getLength() / 100f, value -> batten.setLength((int)(value * 100)), 0.20, 100, 0.20, 2);
        propertiesPanel.addComboBoxField("Orientation:", new String[] { "Horizontal", "Vertical" }, new Orientation[] { Orientation.HORIZONTAL, Orientation.VERTICAL }, batten.getOrientation(), batten::setOrientation);

    }

    /**
     * showLightProperties(JLight) Method:
     * Shows the editable properties for a light.
     *
     * Input: Light of which properties will be shown.
     *
     * Process: Creates necessary fields in the form component.
     *
     * Output: None.
     *
     * @param light Light to use
     */
    private void showLightProperties(final JLight light) {

        // set title
        setTitle("Light", new String[] { light.getModel().getDisplayName(), "Beam Angle: " + (light.getModel().getFieldAngle() > 0 ? light.getModel().getFieldAngle() : light.getModel().getFieldAngleMin() + " - " + light.getModel().getFieldAngleMax()) + "°" });

        // add fields
        propertiesPanel.addNumberField("Rotation (degrees):", light.getRotation(), value -> light.setRotation(value.floatValue()), -360.00, 360.00, 11.25, 2);
        propertiesPanel.addNumberField("Angle (degrees):", light.getAngle(), value -> light.setAngle(value.floatValue()), (int)(light.getFieldAngle() / 2 - 90), (int)(90 - light.getFieldAngle() / 2), 11.25, 2);

        // if light field angle is range, add field angle input
        if (light.getModel().isFieldAngleRange())
            propertiesPanel.addNumberField("Field Angle (degrees):", light.getFieldAngle(), value -> light.setFieldAngle((float)((double)value)), light.getModel().getFieldAngleMin(), light.getModel().getFieldAngleMax(), 0.1, 1);

        // add rest of fields
        propertiesPanel.addColorField("Beam Color:", light.getBeamColor(), light::setBeamColor);
        propertiesPanel.addSlider("Beam Intensity: ", light.getBeamIntensity(), 0, 100, 10, 20, light::setBeamIntensity);
        propertiesPanel.addTextField("Connection ID:", light.getConnectionId(), light::setConnectionId);
    }

    /**
     * showLabelProperties(JDraggableLabel) Method:
     * Shows the editable properties for a label.
     *
     * Input: Label of which properties will be shown.
     *
     * Process: Creates necessary fields in the form component.
     *
     * Output: None.
     *
     * @param label Label to use
     */
    private void showLabelProperties(JDraggableLabel label) {

        // set title
        setTitle("Label", new String[0]);

        // java doesn't like int[] when using generics, so use Integer[] instead
        Integer[] values = new Integer[] { 8, 9, 10, 11, 12, 14, 16, 18, 20, 24, 28, 32 };
        String[] strValues = new String[values.length];

        // get string equivalent of int values
        for (int i = 0; i < values.length; i++)
            strValues[i] = Integer.toString(values[i]);

        // add font size field
        propertiesPanel.addComboBoxField("Font Size:", strValues, values, label.getFontSize(), label::setFontSize);

        // get all available font family names
        String[] fontFamilies = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

        // add combo box for font family
        propertiesPanel.addComboBoxField("Font Family:", fontFamilies, fontFamilies, label.getFontFamily(), label::setFontFamily);

        // add text field
        propertiesPanel.addTextField("Text:", label.getText(), label::setText, true);

    }

    /**
     * setDefaultTitle() Method:
     * Sets the default container title.
     *
     * Input: None.
     *
     * Process: Sets the title to a "no items selected" type message.
     *
     * Output: None.
     */
    private void setDefaultTitle() {
        setTitle("Nothing selected", new String[] { "Click an element on the screen to change its properties." });
    }

    /**
     * setTitle(String, String[]) Method:
     * Sets the title and subtitle(s) of the container.
     *
     * Input: Main title & subtitle(s) of the container.
     *
     * Process: Sets the title label's text and adds labels according to the specified additional strings.
     *
     * Output: None.
     *
     * @param mainTitle Main title string.
     * @param attributes Additional subtitle strings.
     */
    private void setTitle(String mainTitle, String[] attributes) {

        // set main title
        titleLabel.setText(mainTitle);

        // remove all subtitles
        attributesPanel.removeAll();

        // iterate through additional subtitles
        for (String attr : attributes) {

            // create label & set necessary values
            JTextArea attrLabel = new JTextArea(attr);
            attrLabel.setLineWrap(true);
            attrLabel.setWrapStyleWord(true);
            attrLabel.setEnabled(false);
            attrLabel.setDisabledTextColor(Color.darkGray);
            attrLabel.setBackground(null);
            attrLabel.setFont(defaultLabelFont);

            // add label to subtitles panel
            attributesPanel.add(attrLabel);
        }

        // revalidate attributes panel
        attributesPanel.revalidate();
    }
}
