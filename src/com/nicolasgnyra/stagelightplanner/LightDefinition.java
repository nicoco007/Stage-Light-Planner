package com.nicolasgnyra.stagelightplanner;

import java.awt.*;

public class LightDefinition extends FixtureDefinition implements Cloneable {
    private String displayName  = "";
    private String label        = "";
    private LightShape shape    = LightShape.SQUARE;
    private Color displayColor  = Color.black;
    private float fieldAngle    = 0.0f;
    private float fieldAngleMin = 0.0f;
    private float fieldAngleMax = 0.0f;

    public LightDefinition() { }

    public LightDefinition(String displayName, String label, LightShape shape, Color displayColor, float fieldAngle) {
        this.displayName = displayName;
        this.label = label;
        this.shape = shape;
        this.displayColor = displayColor;
        this.fieldAngle = fieldAngle;
    }

    public LightDefinition(String displayName, String label, LightShape shape, Color displayColor, float fieldAngleMin, float fieldAngleMax) {
        this.displayName = displayName;
        this.label = label;
        this.shape = shape;
        this.displayColor = displayColor;
        this.fieldAngleMin = fieldAngleMin;
        this.fieldAngleMax = fieldAngleMax;
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

    @Override
    public LightDefinition clone() {

        try {

            return (LightDefinition) super.clone();

        } catch (CloneNotSupportedException ex) {

            // this should never happen since we implemented the Cloneable interface
            System.out.println(ex.getMessage());

        }

        return this;

    }
}
