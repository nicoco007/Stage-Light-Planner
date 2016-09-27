package com.nicolasgnyra.stagelightplanner.components;

import com.nicolasgnyra.stagelightplanner.*;
import com.nicolasgnyra.stagelightplanner.helpers.PaintHelper;

import java.awt.*;

/**
 * JLight Class:
 * A draggable stage element that has a rotation, angle, and beam.
 */
public class JLight extends JStageElement {

    private LightDefinition model;  // light definition used as a "model"
    private float rotation;         // light rotation (flat on screen)
    private float angle;            // light angle (in/out of screen)
    private float fieldAngle;       // field angle (if the definition field angle is a range)
    private Color beamColor;        // beam color
    private String connectionId;    // connection ID (DMX, plug, etc.)
    private int beamIntensity;      // beam intensity, from 0 to 100

    /**
     * JLight(int, int, LightDefinition) Method:
     * Creates a new instance of the JLight class with the specified x/y coordinates, light definition, and default values.
     *
     * Input: Coordinates & light definition.
     *
     * Process: Calls the constructor with specified & default values.
     *
     * Output: New instance of the JLight class with specified & default values.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param model Light definition
     */
    JLight(int x, int y, LightDefinition model) {
        this(x, y, model, new Color(255, 255, 0), 0.0f, 0.0f, model.getFieldAngle() > 0 ? model.getFieldAngle() : model.getFieldAngleMax(), "", 100);
    }

    /**
     * JLight(int, int, LightDefinition) Method:
     * Creates a new instance of the JLight class with the specified x/y coordinates, light definition, angles, field angle, connection ID, and intensity.
     *
     * Input: Coordinates, light definition, beam color, rotation, angle, field angle, connection ID, beam intensity.
     *
     * Process: Calls the superclass constructor & sets values.
     *
     * Output: New instance of the JLight class with specified values.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param model Light definition
     * @param beamColor Color of the beam
     * @param rotation Flat rotation of the light
     * @param angle Angle of the light
     * @param fieldAngle Field angle of the light
     * @param connectionId Connection ID
     * @param beamIntensity Intensity of the beam
     */
    public JLight(int x, int y, LightDefinition model, Color beamColor, float rotation, float angle, float fieldAngle, String connectionId, int beamIntensity) {
        super(x, y, 30, 30, model.getDisplayColor());
        this.model = model;
        this.beamColor = beamColor;
        this.rotation = rotation;
        this.angle = angle;
        this.fieldAngle = fieldAngle;
        this.connectionId = connectionId;
        this.beamIntensity = beamIntensity;
    }

    /**
     * getOverlappingBatten() Method:
     * Gets the batten on which this light is placed.
     *
     * Input: None.
     *
     * Process: Iterates through all battens and finds the first one that it intersects with.
     *
     * Output: Batten on which the light is placed.
     *
     * @return Batten on which the light is placed.
     */
    JBatten getOverlappingBatten() {

        // define batten as null
        JBatten overlappingBatten = null;

        // iterate through battens
        for (JBatten batten : parent.getBattens()) {

            // check if the bounding boxes overlap, and if so, set the batten to the current batten and exit the for loop
            if (Math.max(batten.getX(), getX()) < Math.min(batten.getX() + batten.getWidth(), getX() + getWidth()) && Math.max(batten.getY(), getY()) < Math.min(batten.getY() + batten.getHeight(), getY() + getHeight())) {
                overlappingBatten = batten;
                break;
            }
        }

        // return the found (maybe) batten
        return overlappingBatten;

    }

    /**
     * paintElement(Graphics) Method:
     * Called when the stage element needs to be painted.
     *
     * Input: Graphics class.
     *
     * Process: Draws the light's shape with the defined color, the connection ID, and the tooltip.
     *
     * Output: None.
     *
     * @param g Graphics class used to draw the window.
     */
    @Override
    protected void paintElement(Graphics g) {

        // cast graphics to 2D graphics
        Graphics2D g2d = (Graphics2D)g;

        // check if we have an overlapping batten
        if (getOverlappingBatten() != null) {

            // set color to defined color & set tooltip to light info
            g2d.setColor(color);
            setToolTipText("<html><p>" + model.getDisplayName() + "</p><p>Field angle: " + getFieldAngle() + "°</p><p>Connection: " + getConnectionId() + "</p></html>");

        } else {

            // set color to semi-transparent version of defined color & tell user to place light on batten through tooltips
            g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 128));
            setToolTipText("Place this light on a batten to see the beam.");

        }

        // enable anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // draw the light's shapae
        PaintHelper.drawShape(g, getModel().getShape(), getWidth(), getHeight());

        // check if there is a batten
        if (getOverlappingBatten() != null) {

            // set color based on background color & draw connection ID
            g2d.setColor(PaintHelper.getHueBasedOnBackgroundColor(color));
            PaintHelper.drawScaledString(g2d, connectionId, getWidth(), getHeight(), 5);

        }

        // disable antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

    }

    public LightDefinition getModel() {
        return model;
    }

    public float getRotation() {
        return rotation;
    }

    void setRotation(float rotation) {
        this.rotation = rotation;
        propertyUpdated();
    }

    public float getAngle() {
        return angle;
    }

    void setAngle(float angle) {
        this.angle = angle;
        propertyUpdated();
    }

    public Color getBeamColor() {
        return beamColor;
    }

    void setBeamColor(Color beamColor) {
        this.beamColor = beamColor;
        propertyUpdated();
    }

    public float getFieldAngle() {
        return fieldAngle;
    }

    void setFieldAngle(float fieldAngle) {
        this.fieldAngle = fieldAngle;
        propertyUpdated();
    }

    public String getConnectionId() {
        return connectionId;
    }

    void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
        propertyUpdated();
    }

    public int getBeamIntensity() {
        return beamIntensity;
    }

    void setBeamIntensity(int beamIntensity) {
        this.beamIntensity = beamIntensity;
        propertyUpdated();
    }
}
