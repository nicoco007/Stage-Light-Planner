package com.nicolasgnyra.stagelightplanner.components;

import com.nicolasgnyra.stagelightplanner.helpers.PaintHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.geom.Rectangle2D;

public class JDraggableLabel extends JStageElement {
    private String text;
    private int fontSize;
    private String fontFamily;

    public JDraggableLabel(int x, int y, String text) {
        this(x, y, text, Color.black, 12, UIManager.getLookAndFeelDefaults().getFont("Label.font").getFontName());
    }

    public JDraggableLabel(int x, int y, String text, Color color, int fontSize, String fontFamily) {
        super(x, y, 50, 50, color);
        this.text = text;
        this.fontSize = fontSize;
        this.fontFamily = fontFamily;
    }

    @Override
    protected void paintElement(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        setFont(new Font(fontFamily, Font.PLAIN, fontSize));

        FontMetrics fm = g2d.getFontMetrics();

        String[] lines = text.split("\n");

        int width = 0;
        int height = 0;

        for (String line : lines) {
            Rectangle2D stringBounds = fm.getStringBounds(line, g);
            width = Math.max(width, (int) stringBounds.getWidth());
            height += stringBounds.getHeight();
        }

        this.width = width + parent.getCellSize();
        this.height = height + parent.getCellSize();

        reposition();

        g2d.setColor(color);

        for (int i = 0; i < lines.length; i++) {
            g2d.scale(parent.getZoom(), parent.getZoom());
            Rectangle2D stringBounds = fm.getStringBounds(lines[i], g);
            g2d.drawString(lines[i], (int) (getWidth() / parent.getZoom() - stringBounds.getWidth()) / 2, (int) (((getHeight() / parent.getZoom() - stringBounds.getHeight() * (lines.length - i)) / 2) + fm.getAscent() + (int)(stringBounds.getHeight() * i / 2)));
            g2d.scale(1f / parent.getZoom(), 1f / parent.getZoom());
        }

        if (hasInnerFocus()) {
            g2d.setColor(Color.gray);
            g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 2 }, 0));
            g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        }
    }

    void setText(String text) {
        this.text = text;
        repaint();
    }

    public String getText() {
        return text;
    }

    void setFontSize(int size) {
        this.fontSize = size;
        repaint();
    }

    public int getFontSize() {
        return fontSize;
    }

    void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
        repaint();
    }

    public String getFontFamily() {
        return fontFamily;
    }

    @Override
    protected void innerFocusLost(FocusEvent e) {
        super.innerFocusLost(e);

        if (text.trim().isEmpty())
            removeSelf();
    }
}
