package com.nicolasgnyra.stagelightplanner.components;

import com.nicolasgnyra.stagelightplanner.Orientation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class JBatten extends JStageElement implements MouseListener {
    private static final int thickness = 10;
    private int heightFromFloor = 100;

    private Orientation orientation = Orientation.HORIZONTAL;
    private int length = 0;

    public JBatten(int x, int y, int length, Orientation orientation, int heightFromFloor) {
        super(x, y, orientation == Orientation.HORIZONTAL ? length : thickness, orientation == Orientation.VERTICAL ? length : thickness, Color.darkGray);
        this.length = length;
        this.heightFromFloor = heightFromFloor;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
    }

    public Orientation getOrientation() {
        return orientation;
    }

    void setOrientation(Orientation orientation) {
        this.orientation = orientation;
        repaint();
    }

    public int getLength() {
        return length;
    }

    void setLength(int length) {
        this.length = length;
        repaint();
    }

    public int getHeightFromFloor() {
        return heightFromFloor;
    }

    void setHeightFromFloor(int heightFromFloor) {
        this.heightFromFloor = heightFromFloor;
        parent.repaint();
    }

    @Override
    protected void paintElement(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;

        width = orientation == Orientation.HORIZONTAL ? length : thickness;
        height = orientation == Orientation.VERTICAL ? length : thickness;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(isFocused() ? getBackground().brighter() : getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}
