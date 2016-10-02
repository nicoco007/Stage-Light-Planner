package com.nicolasgnyra.stagelightplanner.helpers;

import com.nicolasgnyra.stagelightplanner.LightShape;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

/**
 * PaintHelper Class:
 * Contains static methods used to paint components & shapes.
 *
 * Date: 2016-09-27
 *
 * @author Nicolas Gnyra
 * @version 1.0
 */
public class PaintHelper {

    /**
     * getRegularPolygon(int, int, int, int, int) Method:
     * Gets a regular polygon with the specified amount of sides, x pos, y pos, width and height.
     *
     * Input: Sides, X/Y pos, dimensions.
     *
     * Process: Calls overloads methods with default angle of 0.
     *
     * Output: Regular polygon with specified amount of sides.
     *
     * @param sides Amount of sides
     * @param startX X position
     * @param startY Y position
     * @param width Width
     * @param height Height
     * @return Regular polygon with specified amount of sides.
     */
    public static Polygon getRegularPolygon(int sides, int startX, int startY, int width, int height) {
        return getRegularPolygon(sides, startX, startY, width, height, 0);
    }

    /**
     * getRegularPolygon(int, int, int, int, int, double) Method:
     * Gets a regular polygon with the specified amount of sides, x pos, y pos, width, height, and angle.
     *
     * Input: Sides, X/Y pos, dimensions, angle.
     *
     * Process: Using sine/cosine methods, creates a polygon with specified width & height and translates and rotates it
     *
     * Output: Regular polygon with specified amount of sides.
     *
     * @param sides Amount of sides
     * @param startX X position
     * @param startY Y position
     * @param width Width
     * @param height Height
     * @param angle Angle
     * @return Regular polygon with specified amount of sides.
     */
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

        // iterate through side count
        for (int i = 0; i < sides; i++) {

            // get x and y coordinates of point
            double x = width / 2 * Math.cos(i * theta + angle) + width / 2;
            double y = height / 2 * Math.sin(i * theta + angle) + height / 2;

            // set array values
            xpoints[i] = x;
            ypoints[i] = y;

            // set max values
            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);

        }

        // create polygon
        Polygon polygon = new Polygon();

        // iterate through sides
        for (int i = 0; i < sides; i++) {

            // stretch and translate point
            xpoints[i] = startX + stretchPoint(xpoints[i], width, minX, maxX);
            ypoints[i] = startY + stretchPoint(ypoints[i], height, minY, maxY);

            // add point to polygon
            polygon.addPoint((int)Math.round(xpoints[i]), (int)Math.round(ypoints[i]));

        }

        // return polygon
        return polygon;
    }

    /**
     * stretchPoint(double, double, double, double) Method:
     * Stretches a point so the shape it is connected to properly fills the specified bounds.
     *
     * Input: 1D point, size ("width"), min, max, where 0 <= min <= max <= size
     *
     * Process: Calculates necessary displacement in "left" and "right" directions, and applies it to the point.
     *
     * Output: Displaced point.
     *
     * @param point 1D point
     * @param size "Width"
     * @param min Minimum value
     * @param max Maximum value
     * @return Displaced point.
     */
    private static double stretchPoint(double point, double size, double min, double max) {

        // check point isn't already at bounds
        if (point > 0 && point < size) {

            // calculate displacement
            double displMin = Math.round(min * (point - min) / (size - min));
            double displMax = Math.round((size - max) * (max - point) / max);

            // apply displacement, make sure everything is bounds
            point = Math.max(0, Math.min(point - min + displMin, size));
            point = Math.max(0, Math.min(point + size - max - displMax, size));

        }

        // return displaced point
        return point;
    }

    /**
     * drawShape(Graphics, LightShape, int, int) Method:
     * Draws a shape with the specified width/height.
     *
     * Input: Graphics instance, coordinates, dimensions, shape.
     *
     * Process: Draws based on specified shape.
     *
     * Output: None.
     *
     * @param g Graphics instance
     * @param shape Shape to draw
     * @param x X position
     * @param y Y position
     * @param width Width of shape
     * @param height Height of shape
     */
    public static void drawShape(Graphics g, LightShape shape, int x, int y, int width, int height) {

        // draw based on shape
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

    /**
     * scaleFont(String, Dimension, Graphics) Method:
     * Scale font to (roughly) fit the specified bounds.
     *
     * Input: Text, bounds, graphics instance.
     *
     * Process: Using the notion that "font height = font size", calculates the size of the font based on string width.
     *
     * Output: Scaled font.
     *
     * @param text Text
     * @param dimension Bounds
     * @param g Graphics instance
     * @return Scaled font.
     */
    private static Font scaleFont(String text, Dimension dimension, Graphics g) {
        Font font = g.getFont().deriveFont((float) dimension.height);
        int strWidth = g.getFontMetrics(font).stringWidth(text);
        float fontSize = (float) dimension.width / strWidth * dimension.height;
        return g.getFont().deriveFont(fontSize);
    }

    /**
     * drawScaledString(Graphics2D, String, int, int, int, int, int) Method:
     * Draws a scaled string in the specified bounds.
     *
     * Input: Graphics instance, text, coordinates, bounding box, padding.
     *
     * Process: Scales the font appropriately and draws a centered string.
     *
     * Output: None.
     *
     * @param g2d Graphics instance
     * @param text Text
     * @param x X position
     * @param y Y position
     * @param width Bounding box width
     * @param height Bounding box height
     * @param padding Padding (approximate)
     */
    public static void drawScaledString(Graphics2D g2d, String text, int x, int y, int width, int height, int padding) {
        g2d.setFont(scaleFont(text, new Dimension(width - padding * 2, height - padding * 2), g2d));

        FontMetrics fm = g2d.getFontMetrics();

        g2d.drawString(text, x + (width - fm.stringWidth(text)) / 2, y + (height - fm.getHeight()) / 2 + fm.getAscent());
    }

    /**
     * getHueBasedOnBackgroundColor(Color) Method:
     * Gets a hue (black or white) based on the luminance of the specified color (for contrast).
     *
     * Input: Background color.
     *
     * Process: Calculates luminance and returns black or white based on that.
     *
     * Output: Black or white depending on contrast needed.
     *
     * @param bgColor Background color
     * @return Black or white depending on contrast needed.
     */
    public static Color getHueBasedOnBackgroundColor(Color bgColor) {

        // luminance (similar to brightness) calculation based on HDTV standards (http://www.itu.int/rec/R-REC-BT.709)
        double luminance = (bgColor.getRed() * 0.2126d + bgColor.getGreen() * 0.7152d + bgColor.getBlue() * 0.0722d) / 255;

        // return color based on luminance (W3C recommends this formula)
        return luminance > Math.sqrt(1.05 * 0.05) - 0.05 ? Color.black : Color.white;

    }

    /**
     * getBeamRect(int, double, double) Method:
     * Gets the rectangle of the ellipse of the beam of the light with the specified height, field angle and angle.
     *
     * Input: Batten height, field angle, angle.
     *
     * Process: Calculates beam location & size using on laws of sines and cosines.
     *
     * Output: Beam rectangle.
     *
     * @param battenHeight Height of the batten on which the light is
     * @param fieldAngle Field angle of the light
     * @param angle Angle of the light
     * @return Beam rectangle
     */
    public static Rectangle getBeamRect(int battenHeight, double fieldAngle, double angle) {

        // create ellipse rectangle (container)
        Rectangle rect = new Rectangle();

        // get length of beam at closest and farthest edges of the beam oval on the floor using the law of sines
        double b1 = battenHeight / Math.sin(Math.PI / 2 - Math.toRadians(angle - fieldAngle / 2));
        double b2 = battenHeight / Math.sin(Math.PI / 2 - Math.toRadians(angle + fieldAngle / 2));

        // set width of ellipse based on the two above lengths and the law of cosines
        rect.width = (int) Math.round(Math.sqrt(Math.pow(b1, 2) + Math.pow(b2, 2) - 2 * b1 * b2 * Math.cos(Math.toRadians(fieldAngle))));

        // set height based only on beam angle (this doesn't change with the angle of the light)
        rect.height = (int) Math.round((2 * battenHeight * Math.sin(Math.toRadians(fieldAngle) / 2)) / Math.sin(Math.PI / 2 - Math.toRadians(fieldAngle) / 2));

        // get x location from start of beam ellipse (Pythagorean theorem)
        rect.x = (int) Math.round(Math.sqrt(Math.pow(b1, 2) - Math.pow(battenHeight, 2)));

        // make x negative if the angle is below 0 (square root is always positive)
        if (angle - fieldAngle / 2 < 0) rect.x = -rect.x;

        // return the rectangle
        return rect;

    }

    /**
     * drawBeam(Graphics2D, int, int, int, int, int, double, int Color, int, double, double, boolean) Method:
     * Draws the beam of the light with the specified characteristics.
     *
     * Input: Graphics instance, light coordinates & size, field angle, batten height, beam color, beam intensity,
     * rotation, angle, whether to show outlines or not.
     *
     * Process: Gets the beam rectangle, transforms (rotates) it, and draws it.
     *
     * Output: None.
     *
     * @param g2d Graphics instance.
     * @param lightX Light X position
     * @param lightY Light Y position
     * @param lightWidth Light width
     * @param lightHeight Light height
     * @param fieldAngle Light field angle
     * @param battenHeight Batten height
     * @param beamColor Beam color
     * @param beamIntensity Beam intensity
     * @param rotation Light rotation
     * @param angle Light angle
     * @param showLightOutlines Whether to show light outlines or not
     */
    public static void drawBeam(Graphics2D g2d, int lightX, int lightY, int lightWidth, int lightHeight, double fieldAngle, int battenHeight, Color beamColor, int beamIntensity, double rotation, double angle, boolean showLightOutlines) {

        // get the color of the light
        Color color = new Color(beamColor.getRed(), beamColor.getGreen(), beamColor.getBlue(), 128 * beamIntensity / 100);

        // get the beam rectangle
        Rectangle rect = getBeamRect(battenHeight, fieldAngle, angle);

        // create & rotate transform according to user input
        AffineTransform transform = new AffineTransform();
        transform.rotate(-Math.toRadians(rotation) + Math.PI / 2, lightX + lightWidth / 2, lightY + lightHeight / 2);

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

    }
}
