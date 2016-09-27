package com.nicolasgnyra.stagelightplanner.helpers;

import com.nicolasgnyra.stagelightplanner.LightShape;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

public class PaintHelper {
    public static Polygon getRegularPolygon(int sides, int startX, int startY, int width, int height) {
        return getRegularPolygon(sides, startX, startY, width, height, 0);
    }

    public static Polygon getRegularPolygon(int sides, int startX, int startY, int width, int height, double angle) {

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

        System.out.println(sides);

        for (int i = 0; i < sides; i++) {
            xpoints[i] = startX + stretchPoint(xpoints[i], width, minX, maxX);
            ypoints[i] = startY + stretchPoint(ypoints[i], height, minY, maxY);

            polygon.addPoint((int)Math.round(xpoints[i]), (int)Math.round(ypoints[i]));
        }

        return polygon;
    }

    private static double stretchPoint(double point, double size, double min, double max) {
        if (point > 0 && point < size) {
            double minDispl = min * (point - min) / (size - min);
            double maxDispl = (size - max) * (max - point) / max;

            point = point - min + minDispl;
            point = point + (size - max) - maxDispl;
        }

        return point;
    }
    
    public static void drawShape(Graphics g, LightShape shape, int width, int height) {
        drawShape(g, shape, 0, 0, width, height);
    }

    public static void drawShape(Graphics g, LightShape shape, int x, int y, int width, int height) {
        switch(shape) {
            case TRIANGLE:
                g.fillPolygon(PaintHelper.getRegularPolygon(3, x, y, width, height));
                break;
            case SQUARE:
                g.fillRect(x, y, width, height);
                break;
            case CIRCLE:
                g.fillOval(x, y, width, height);
                break;
            case DIAMOND:
                g.fillPolygon(PaintHelper.getRegularPolygon(4, x, y, width, height, Math.PI / 4));
                break;
            case PENTAGON:
                g.fillPolygon(PaintHelper.getRegularPolygon(5, x, y, width, height));
                break;
            case HEXAGON:
                g.fillPolygon(PaintHelper.getRegularPolygon(6, x, y, width, height));
                break;
            case HEPTAGON:
                g.fillPolygon(PaintHelper.getRegularPolygon(7, x, y, width, height));
                break;
            case OCTAGON:
                g.fillPolygon(PaintHelper.getRegularPolygon(8, x, y, width, height));
                break;
            case NONAGON:
                g.fillPolygon(PaintHelper.getRegularPolygon(9, x, y, width, height));
                break;
            case DECAGON:
                g.fillPolygon(PaintHelper.getRegularPolygon(10, x, y, width, height));
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
        drawScaledString(g2d, text, 0, 0, width, height, padding);
    }

    public static void drawScaledString(Graphics2D g2d, String text, int x, int y, int width, int height, int padding) {
        g2d.setFont(scaleFont(text, new Dimension(width - padding * 2, height - padding * 2), g2d));

        FontMetrics fm = g2d.getFontMetrics();

        g2d.drawString(text, x + (width - fm.stringWidth(text)) / 2, y + (height - fm.getHeight()) / 2 + fm.getAscent());
    }

    public static Color getHueBasedOnBackgroundColor(Color bgColor) {

        // luminance (similar to brightness) calculation based on HDTV standards (http://www.itu.int/rec/R-REC-BT.709)
        double luminance = (bgColor.getRed() * 0.2126d + bgColor.getGreen() * 0.7152d + bgColor.getBlue() * 0.0722d) / 255;

        // return color based on luminance (W3C recommends this formula)
        return luminance > Math.sqrt(1.05 * 0.05) - 0.05 ? Color.black : Color.white;

    }

    public static Rectangle getBeamRect(int battenHeight, double fieldAngle, double angle) {

        // create ellipse rectangle (container)
        Rectangle rect = new Rectangle();

        // get length of beam at closest and farthest edges of the beam oval on the floor using the law of sines
        double b1 = battenHeight / Math.sin(Math.PI / 2 - Math.toRadians(angle - fieldAngle / 2));
        double b2 = battenHeight / Math.sin(Math.PI / 2 - Math.toRadians(angle + fieldAngle / 2));

        // set width of ellipse based on the two above lengths and the law of cosines
        rect.width = (int) Math.sqrt(Math.pow(b1, 2) + Math.pow(b2, 2) - 2 * b1 * b2 * Math.cos(Math.toRadians(fieldAngle)));

        // set height based only on beam angle (this doesn't change with the angle of the light)
        rect.height = (int) Math.round((2 * battenHeight * Math.sin(Math.toRadians(fieldAngle) / 2)) / Math.sin(Math.PI / 2 - Math.toRadians(fieldAngle) / 2));

        // get x location from start of beam ellipse (Pythagorean theorem)
        rect.x = (int) Math.sqrt(Math.pow(b1, 2) - Math.pow(battenHeight, 2));

        // make x negative if the angle is below 0 (square root is always positive)
        if (angle - fieldAngle / 2 < 0) rect.x = -rect.x;

        // return the rectangle
        return rect;

    }

    public static void drawBeam(Graphics2D g2d, int lightX, int lightY, int lightWidth, int lightHeight, double fieldAngle, int battenHeight, Color beamColor, int beamIntensity, double rotation, double angle, boolean showLightOutlines) {

        // get the color of the light
        Color color = new Color(beamColor.getRed(), beamColor.getGreen(), beamColor.getBlue(), 128 * beamIntensity / 100);

        // get the beam rectangle
        Rectangle rect = getBeamRect(battenHeight, fieldAngle, angle);

        // create & rotate transform according to user input
        AffineTransform transform = new AffineTransform();
        transform.rotate(-Math.toRadians(rotation) + Math.PI / 2, lightX + lightWidth / 2, lightY + lightHeight / 2);

        // enable anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // set color according to user input (add 50% opacity)
        g2d.setColor(color);

        // get transformed shape
        Shape transformedEllipse = transform.createTransformedShape(new Ellipse2D.Double(lightX + rect.x + lightWidth / 2, lightY + rect.y + lightHeight / 2 - rect.height / 2, rect.width, rect.height));

        // fill sized & rotate ellipse
        g2d.fill(transformedEllipse);

        // check if user wants to show outlines
        if (showLightOutlines) {

            // save previous stroke
            Stroke previousStroke = g2d.getStroke();

            // set color to darker version of beam color
            g2d.setColor(color.darker());

            // draw transformed ellipse (outline)
            g2d.draw(transformedEllipse);

            // set stroke to dotted line
            g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 5 }, 0));

            // draw lines from center of light to beam ellipse
            g2d.draw(transform.createTransformedShape(new Line2D.Double(lightX + lightWidth / 2, lightY + lightHeight / 2, lightX + rect.x + lightWidth / 2 + rect.width / 2, lightY + rect.y + lightHeight / 2 - rect.height / 2)));
            g2d.draw(transform.createTransformedShape(new Line2D.Double(lightX + lightWidth / 2, lightY + lightHeight / 2, lightX + rect.x + lightWidth / 2 + rect.width / 2, lightY + rect.y + lightHeight / 2 + rect.height / 2)));

            // reset stroke
            g2d.setStroke(previousStroke);

        }

        // restore anti-aliasing setting to default
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_DEFAULT);

    }
}
