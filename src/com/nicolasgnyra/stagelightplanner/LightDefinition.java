package com.nicolasgnyra.stagelightplanner;

import java.awt.*;

/**
 * LightDefinition Class:
 * Contains information on a stage light.
 *
 * Date: 2016-09-27
 *
 * @author Nicolas Gnyra
 * @version 1.0
 */
public class LightDefinition implements Cloneable {
    private String displayName  = "";                   // display name
    private String label        = "";                   // label (short name)
    private LightShape shape    = LightShape.SQUARE;    // display shape
    private Color displayColor  = Color.black;          // display color
    private float fieldAngle    = 0.0f;                 // field angle (if fixed)
    private float fieldAngleMin = 0.0f;                 // minimum field angle (if dynamic)
    private float fieldAngleMax = 0.0f;                 // maximum field angle (if dynamic)

    /**
     * LightDefinition(String, String, LightShape, Color, float) Method:
     * Creates a new light definition with a fixed field angle.
     *
     * Input: Display name, shape, and color, short name, field angle.
     *
     * Process: Sets values.
     *
     * Output: A new instance of the LightDefinition class.
     *
     * @param displayName Display name
     * @param label Short name
     * @param shape Display shape
     * @param displayColor Display color
     * @param fieldAngle Field angle
     */
    public LightDefinition(String displayName, String label, LightShape shape, Color displayColor, float fieldAngle) {
        this.displayName = displayName;
        this.label = label;
        this.shape = shape;
        this.displayColor = displayColor;
        this.fieldAngle = fieldAngle;
    }

    /**
     * LightDefinition(String, String, LightShape, Color, float) Method:
     * Creates a new light definition with a dynamic field angle.
     *
     * Input: Display name, shape, and color, short name, field angle range.
     *
     * Process: Sets values.
     *
     * Output: A new instance of the LightDefinition class.
     *
     * @param displayName Display name
     * @param label Short name
     * @param shape Display shape
     * @param displayColor Display color
     * @param fieldAngleMin Minimum field angle
     * @param fieldAngleMax Maximum field angle
     */
    public LightDefinition(String displayName, String label, LightShape shape, Color displayColor, float fieldAngleMin, float fieldAngleMax) {
        this.displayName = displayName;
        this.label = label;
        this.shape = shape;
        this.displayColor = displayColor;
        this.fieldAngleMin = fieldAngleMin;
        this.fieldAngleMax = fieldAngleMax;
    }

    /**
     * clone() Method:
     * Creates a clone/copy of this class.
     *
     * Input: None.
     *
     * Process: Calls the clone() method of the Cloneable interface in a try/catch statement.
     *
     * Output: A clone of this class.
     *
     * @return A clone of this class.
     */
    @Override
    public LightDefinition clone() {

        try {

            // return casted clone
            return (LightDefinition) super.clone();

        } catch (CloneNotSupportedException ex) {

            // this should never happen since we implemented the Cloneable interface
            ex.printStackTrace();

        }

        // return this if it doesn't work for some reason
        return this;

    }

    /**
     * isFieldAngleRange() Method:
     * Checks whether the light has a fixed or dynamic field angle.
     *
     * Input: None.
     *
     * Process: None.
     *
     * Output: Returns true if field angle is 0 and minimum/maximum field angles are valid, false otherwise.
     *
     * @return True if field angle is 0 and minimum/maximum field angles are valid, false otherwise
     */
    public boolean isFieldAngleRange() {
        return getFieldAngle() == 0 && 0 < getFieldAngleMin() && getFieldAngleMin() <= getFieldAngleMax() && getFieldAngleMax() <= 180;
    }

    /**
     * equals(Object) Method:
     * Checks whether the supplied object is equal to this instance.
     *
     * Input: Object to compare.
     *
     * Process: Compares all properties of this class.
     *
     * Output: Whether the objects are equal or not.
     *
     * @param obj Object to compare.
     * @return Whether the objects are equal or not.
     */
    @Override
    public boolean equals(Object obj) {

        // check type
        if (!(obj instanceof LightDefinition))
            return false;

        // cast
        LightDefinition otherLightDefinition = (LightDefinition) obj;

        // check variables
        return getDisplayName().equals(otherLightDefinition.getDisplayName()) &&
                getLabel().equals(otherLightDefinition.getLabel()) &&
                getShape().equals(otherLightDefinition.getShape()) &&
                getDisplayColor().equals(otherLightDefinition.getDisplayColor()) &&
                getFieldAngle() == otherLightDefinition.getFieldAngle() &&
                getFieldAngleMin() == otherLightDefinition.getFieldAngleMin() &&
                getFieldAngleMax() == otherLightDefinition.getFieldAngleMax();
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public LightShape getShape() {
        return shape;
    }

    public void setShape(LightShape shape) {
        this.shape = shape;
    }

    public Color getDisplayColor() {
        return displayColor;
    }

    public void setDisplayColor(Color displayColor) {
        this.displayColor = displayColor;
    }

    public float getFieldAngle() {
        return fieldAngle;
    }

    public void setFieldAngle(float fieldAngle) {
        this.fieldAngle = fieldAngle;
    }

    public float getFieldAngleMin() {
        return fieldAngleMin;
    }

    public void setFieldAngleMin(float fieldAngleMin) {
        this.fieldAngleMin = fieldAngleMin;
    }

    public float getFieldAngleMax() {
        return fieldAngleMax;
    }

    public void setFieldAngleMax(float fieldAngleMax) {
        this.fieldAngleMax = fieldAngleMax;
    }

    @Override
    public String toString() {
        return String.format("%s[displayName=%s,label=%s,displayColor=%s,fieldAngle=%f,fieldAngleMin=%f,fieldAngleMax=%f]", getClass().getCanonicalName(), displayName, label, displayColor, fieldAngle, fieldAngleMin, fieldAngleMax);
    }
}
