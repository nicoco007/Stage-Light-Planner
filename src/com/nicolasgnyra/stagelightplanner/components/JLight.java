package com.nicolasgnyra.stagelightplanner.components;

import com.nicolasgnyra.stagelightplanner.*;
import com.nicolasgnyra.stagelightplanner.helpers.PaintHelper;

import java.awt.*;

public class JLight extends JFixture {
    private LightDefinition model;
    private float rotation;
    private float angle;
    private float fieldAngle;
    private Color beamColor;
    private ConnectionType connectionType;
    private String connectionId;

    JLight(int x, int y, LightDefinition model) {
        this(x, y, model, new Color(255, 255, 0), 0.0f, 0.0f);
    }

    public JLight(int x, int y, LightDefinition model, Color beamColor, float rotation, float angle) {
        this(x, y, model, beamColor, rotation, angle, model.getFieldAngle() > 0 ? model.getFieldAngle() : model.getFieldAngleMax(), ConnectionType.DMX, "");
    }

    public JLight(int x, int y, LightDefinition model, Color beamColor, float rotation, float angle, float fieldAngle, ConnectionType connectionType, String connectionId) {
        super(x, y, 30, 30, model.getDisplayColor());
        this.model = model;
        this.beamColor = beamColor;
        this.rotation = rotation;
        this.angle = angle;
        this.fieldAngle = fieldAngle;
        this.connectionType = connectionType;
        this.connectionId = connectionId;
    }

    JBatten getOverlappingBatten() {
        JBatten overlappingBatten = null;

        for (JBatten batten : parent.getBattens()) {
            if (Math.max(batten.getX(), getX()) < Math.min(batten.getX() + batten.getWidth(), getX() + getWidth()) && Math.max(batten.getY(), getY()) < Math.min(batten.getY() + batten.getHeight(), getY() + getHeight())) {
                overlappingBatten = batten;
                break;
            }
        }

        return overlappingBatten;
    }

    @Override
    protected void paintElement(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (getOverlappingBatten() != null) {
            g2d.setColor(color);
            setToolTipText(null);
        } else {
            g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 128));
            setToolTipText("Place this light on a batten to see the beam.");
        }

        PaintHelper.drawShape(g, model.getShape(), getWidth(), getHeight());

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    }

    public LightDefinition getModel() {
        return model;
    }

    public float getRotation() {
        return rotation;
    }

    void setRotation(int rotation) {
        this.rotation = rotation;
        parent.repaint();
    }

    public float getAngle() {
        return angle;
    }

    void setAngle(float angle) {
        this.angle = angle;
        parent.repaint();
    }

    public Color getBeamColor() {
        return beamColor;
    }

    void setBeamColor(Color beamColor) {
        this.beamColor = beamColor;
    }

    public float getFieldAngle() {
        return fieldAngle;
    }

    void setFieldAngle(float fieldAngle) {
        this.fieldAngle = fieldAngle;
    }

    public ConnectionType getConnectionType() {
        return connectionType;
    }

    void setConnectionType(ConnectionType connectionType) {
        this.connectionType = connectionType;
    }

    public String getConnectionId() {
        return connectionId;
    }

    void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }
}
