package com.nicolasgnyra.stagelightplanner.helpers;

import com.nicolasgnyra.stagelightplanner.LightShape;

import java.awt.*;

public class PaintHelper {
    public static Polygon getRegularPolygon(int sides, int width, int height) {
        return getRegularPolygon(sides, width, height, 0);
    }

    public static Polygon getRegularPolygon(int sides, int width, int height, double angle) {

        // add to angle so flat side of poly is on bottom
        angle += Math.PI / sides + Math.PI / 2;

        // angle between each side of the polygon
        double theta = 2 * Math.PI / sides;

        // min/max values
        double minX = Integer.MAX_VALUE;
        double maxX = Integer.MIN_VALUE;
        double minY = Integer.MAX_VALUE;
        double maxY = Integer.MIN_VALUE;

        // polygon points
        double[] xpoints = new double[sides];
        double[] ypoints = new double[sides];

        for (int i = 0; i < sides; i++) {
            double x = width / 2 * Math.cos(i * theta + angle) + width / 2;
            double y = height / 2 * Math.sin(i * theta + angle) + height / 2;

            xpoints[i] = x;
            ypoints[i] = y;

            if (x < minX) minX = x;
            if (x > maxX) maxX = x;
            if (y < minY) minY = y;
            if (y > maxY) maxY = y;
        }

        Polygon polygon = new Polygon();

        for (int i = 0; i < sides; i++) {
            xpoints[i] = stretchPoints(xpoints[i], width, minX, maxX);
            ypoints[i] = stretchPoints(ypoints[i], height, minY, maxY);

            polygon.addPoint((int)Math.round(xpoints[i]), (int)Math.round(ypoints[i]));
        }

        return polygon;
    }

    private static double stretchPoints(double point, double size, double min, double max) {
        if (point > 0 && point < size) {
            double minXdispl = min * (point - min) / (size - min);
            double maxXdispl = (size - max) * (max - point) / max;

            point = point - min + minXdispl;
            point = point + (size - max) - maxXdispl;
        }

        return point;
    }
    
    public static void drawShape(Graphics g, LightShape shape, int width, int height) {
        switch(shape) {
            case TRIANGLE:
                g.fillPolygon(PaintHelper.getRegularPolygon(3, width, height));
                break;
            case SQUARE:
                g.fillRect(0, 0, width, height);
                break;
            case CIRCLE:
                g.fillOval(0, 0, width, height);
                break;
            case DIAMOND:
                g.fillPolygon(PaintHelper.getRegularPolygon(4, width, height, Math.PI / 4));
                break;
            case PENTAGON:
                g.fillPolygon(PaintHelper.getRegularPolygon(5, width, height));
                break;
            case HEXAGON:
                g.fillPolygon(PaintHelper.getRegularPolygon(6, width, height));
                break;
            case HEPTAGON:
                g.fillPolygon(PaintHelper.getRegularPolygon(7, width, height));
                break;
            case OCTAGON:
                g.fillPolygon(PaintHelper.getRegularPolygon(8, width, height));
                break;
            case NONAGON:
                g.fillPolygon(PaintHelper.getRegularPolygon(9, width, height));
                break;
            case DECAGON:
                g.fillPolygon(PaintHelper.getRegularPolygon(10, width, height));
                break;
        }
    }

    private static Font scaleFont(String text, Dimension dimension, Graphics g) {
        Font font = g.getFont().deriveFont((float) dimension.height);
        int strWidth = g.getFontMetrics(font).stringWidth(text);
        float fontSize = (float) dimension.width / strWidth * dimension.height;
        return g.getFont().deriveFont(fontSize);
    }

    public static void drawScaledString(Graphics2D g2d, String text, int width, int height, int padding) {
        g2d.setFont(scaleFont(text, new Dimension(width - padding * 2, height - padding * 2), g2d));

        FontMetrics fm = g2d.getFontMetrics();

        g2d.drawString(text, (width - fm.stringWidth(text)) / 2, (height - fm.getHeight()) / 2 + fm.getAscent());
    }

    public static Color getHueBasedOnBackgroundColor(Color bgColor) {

        // luminance (similar to brightness) calculation based on HDTV standards (http://www.itu.int/rec/R-REC-BT.709)
        double luminance = (bgColor.getRed() * 0.2126d + bgColor.getGreen() * 0.7152d + bgColor.getBlue() * 0.0722d) / 255;

        // return color based on luminance (W3C recommends this formula)
        return luminance > Math.sqrt(1.05 * 0.05) - 0.05 ? Color.black : Color.white;

    }
}
