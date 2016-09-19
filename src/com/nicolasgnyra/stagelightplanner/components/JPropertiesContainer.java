package com.nicolasgnyra.stagelightplanner.components;

import com.nicolasgnyra.stagelightplanner.ConnectionType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class JPropertiesContainer extends JPanel {
    private JForm propertiesPanel;
    private JPanel attributesPanel;
    private JTextArea titleLabel;
    private Font defaultLabelFont = UIManager.getLookAndFeelDefaults().getFont("Label.font");

    public JPropertiesContainer() {
        super(new BorderLayout());

        JPanel panel = new JPanel(new BorderLayout());
        attributesPanel = new JPanel();
        attributesPanel.setLayout(new BoxLayout(attributesPanel, BoxLayout.Y_AXIS));

        titleLabel = new JTextArea();
        titleLabel.setLineWrap(true);
        titleLabel.setWrapStyleWord(true);
        titleLabel.setEnabled(false);
        titleLabel.setFont(defaultLabelFont.deriveFont(Font.BOLD, 24));
        titleLabel.setDisabledTextColor(Color.black);
        titleLabel.setBackground(null);
        titleLabel.setAlignmentY(JLabel.BOTTOM_ALIGNMENT);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(attributesPanel, BorderLayout.CENTER);

        add(panel, BorderLayout.NORTH);

        propertiesPanel = new JForm();
        propertiesPanel.setBorder(new EmptyBorder(8, 3, 8, 3));
        JScrollPane scroller = new JScrollPane(propertiesPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.setBorder(BorderFactory.createEmptyBorder());

        add(scroller, BorderLayout.CENTER);

        setDefaultTitle();
    }

    void showProperties(JStageElement element) {
        setDefaultTitle();
        propertiesPanel.removeAll();

        if (element instanceof JLight)
            showLightProperties((JLight)element);
        else if (element instanceof JBatten)
            showBattenProperties((JBatten)element);
        else if (element instanceof JDraggableLabel)
            showLabelProperties((JDraggableLabel)element);

        propertiesPanel.addVerticalGlue();

        revalidate();
        repaint();
    }

    private void showBattenProperties(final JBatten batten) {
        setTitle("Batten", new String[0]);
        propertiesPanel.addNumberField("Height (meters):", batten.getHeightFromFloor() / 100f, value -> batten.setHeightFromFloor((int)(value * 100)), 1, 50, 0.5, 2);
        propertiesPanel.addNumberField("Length (meters):", batten.getLength() / 100f, value -> batten.setLength((int)(value * 100)), 0.20, 100, 0.20, 2);
    }

    private void showLightProperties(final JLight light) {
        setTitle("Light", new String[] { light.getModel().getDisplayName(), "Beam Angle: " + (light.getModel().getFieldAngle() > 0 ? light.getModel().getFieldAngle() : light.getModel().getFieldAngleMin() + " - " + light.getModel().getFieldAngleMax()) + "°" });
        propertiesPanel.addNumberField("Rotation (degrees):", light.getRotation(), value -> light.setRotation((int)((double)value)), -360.00, 360.00, 11.25, 2);
        propertiesPanel.addNumberField("Angle (degrees):", light.getAngle(), value -> light.setAngle((float)((double)value)), (int)(light.getFieldAngle() / 2 - 90), (int)(90 - light.getFieldAngle() / 2), 11.25, 2);

        if (light.getModel().getFieldAngle() == 0) {
            propertiesPanel.addNumberField("Field Angle (degrees):", light.getFieldAngle(), value -> light.setFieldAngle((float)((double)value)), light.getModel().getFieldAngleMin(), light.getModel().getFieldAngleMax(), 1.0f, 1);
        }

        propertiesPanel.addColorField("Beam Color:", light.getBeamColor(), light::setBeamColor);

        propertiesPanel.addComboBoxField("Connection type:", new String[] { "DMX", "Dimmer Outlet" }, new ConnectionType[] { ConnectionType.DMX, ConnectionType.DIMMER }, light.getConnectionType(), light::setConnectionType);
        propertiesPanel.addTextField("Connection ID:", light.getConnectionId(), light::setConnectionId);
    }

    private void showLabelProperties(JDraggableLabel label) {
        setTitle("Label", new String[0]);

        // java doesn't like int[] when using generics, so use Integer[] instead
        Integer[] values = new Integer[] { 8, 9, 10, 11, 12, 14, 16, 18, 20, 24, 28, 32 };
        String[] strValues = new String[values.length];

        for (int i = 0; i < values.length; i++)
            strValues[i] = Integer.toString(values[i]);

        propertiesPanel.addComboBoxField("Font Size:", strValues, values, label.getFontSize(), label::setFontSize);

        String[] fontFamilies = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

        propertiesPanel.addComboBoxField("Font Family:", fontFamilies, fontFamilies, label.getFontFamily(), label::setFontFamily);

        propertiesPanel.addTextField("Text:", label.getText(), label::setText, true);
    }

    private void setDefaultTitle() {
        setTitle("Nothing selected", new String[] { "Click an element on the screen to change its properties." });
    }

    private void setTitle(String mainTitle, String[] attributes) {
        titleLabel.setText(mainTitle);
        attributesPanel.removeAll();

        for (String attr : attributes) {
            JTextArea attrLabel = new JTextArea(attr);
            attrLabel.setLineWrap(true);
            attrLabel.setWrapStyleWord(true);
            attrLabel.setEnabled(false);
            attrLabel.setDisabledTextColor(Color.darkGray);
            attrLabel.setBackground(null);
            attrLabel.setFont(defaultLabelFont);
            attributesPanel.add(attrLabel);
        }

        attributesPanel.revalidate();
    }
}
