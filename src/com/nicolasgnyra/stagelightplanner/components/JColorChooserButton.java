package com.nicolasgnyra.stagelightplanner.components;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;

public class JColorChooserButton extends JButton {
    private Color current = null;
    private List<ColorChangedListener> listeners = new ArrayList<>();

    public JColorChooserButton(Color initialColor) {

        // set selected color to specified color
        setSelectedColor(initialColor);

        addActionListener(arg0 -> {

            // set initial color to current color if color has been chosen in the past, else set to the specified value
            JColorChooser chooser = new JColorChooser(current != null ? current : initialColor);

            // get chooser panels
            AbstractColorChooserPanel[] panels = chooser.getChooserPanels();

            // only show HSV panel
            chooser.setChooserPanels(new AbstractColorChooserPanel[] { panels[1] });

            // create dialog
            JDialog dialog = JColorChooser.createDialog(null, "Choose a color", true, chooser, e -> setSelectedColor(chooser.getColor()), null);

            // show dialog
            dialog.setVisible(true);

        });

    }

    public Color getSelectedColor() {
        return current;
    }

    public void setSelectedColor(Color newColor) {
        if (newColor == null) return;

        current = newColor;
        setIcon(createIcon(current, 16, 16));
        repaint();

        for (ColorChangedListener l : listeners) {
            l.colorChanged(newColor);
        }
    }

    public void addColorChangedListener(ColorChangedListener listener) {
        listeners.add(listener);
    }

    public static  ImageIcon createIcon(Color main, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(main);
        graphics.fillRect(0, 0, width, height);
        graphics.setXORMode(Color.DARK_GRAY);
        graphics.drawRect(0, 0, width-1, height-1);
        image.flush();
        return new ImageIcon(image);
    }

    public interface ColorChangedListener {
        void colorChanged(Color newColor);
    }
}