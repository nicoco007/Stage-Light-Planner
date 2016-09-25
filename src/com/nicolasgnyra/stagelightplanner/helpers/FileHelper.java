package com.nicolasgnyra.stagelightplanner.helpers;

import com.nicolasgnyra.stagelightplanner.*;
import com.nicolasgnyra.stagelightplanner.components.JBatten;
import com.nicolasgnyra.stagelightplanner.components.JDraggableLabel;
import com.nicolasgnyra.stagelightplanner.components.JLight;
import com.nicolasgnyra.stagelightplanner.components.JStageElement;
import com.nicolasgnyra.stagelightplanner.exceptions.InvalidFileVersionException;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FileHelper {

    private static final byte lightDefinitionVersion = 1;
    private static final byte stagePlanVersion = 1;

    private static final byte STAGE_ELEMENT_NONE = 0;
    private static final byte STAGE_ELEMENT_BATTEN = 1;
    private static final byte STAGE_ELEMENT_LIGHT = 2;
    private static final byte STAGE_ELEMENT_LABEL = 3;

    public static void saveLightDefinitions(ArrayList<LightDefinition> lights, File file) throws IOException {

        // if file doesn't have the right extension, add it
        if(!file.getName().toLowerCase().endsWith(".slpfd"))
            file = new File(file.getAbsolutePath() + ".slpfd");

        FileStreamWriter writer = new FileStreamWriter(file);

        writer.write(lightDefinitionVersion);

        writer.writeInt(lights.size());

        for (LightDefinition light : lights) {
            writeLightDefinition(writer, light);
        }

        writer.close();

    }

    private static void writeLightDefinition(FileStreamWriter writer, LightDefinition light) throws IOException {
        writer.writeInt(light.getDisplayName().length());
        writer.writeString(light.getDisplayName());

        writer.writeInt(light.getLabel().length());
        writer.writeString(light.getLabel());

        writer.writeInt(light.getShape().ordinal());

        writer.write(light.getDisplayColor().getRed());
        writer.write(light.getDisplayColor().getGreen());
        writer.write(light.getDisplayColor().getBlue());

        writer.writeFloat(light.getFieldAngle());
        writer.writeFloat(light.getFieldAngleMin());
        writer.writeFloat(light.getFieldAngleMax());
    }

    private static LightDefinition readLightDefinition(FileStreamReader reader) throws IOException {

        // read name, label, and shape
        String name = reader.readString(reader.readInt());
        String label = reader.readString(reader.readInt());
        LightShape shape = LightShape.values()[reader.readInt()];

        // since Java uses signed bytes (-127 to 127) and RGB uses unsigned bytes (0 to 255), add 0xFF to compensate
        // this also converts the byte to a short
        Color displayColor = new Color(reader.readByte() & 0xFF, reader.readByte() & 0xFF, reader.readByte() & 0xFF);

        // read field angle & min/max
        Float fieldAngle = reader.readFloat();
        Float fieldAngleMin = reader.readFloat();
        Float fieldAngleMax = reader.readFloat();

        // create new LightDefinition class based on if the field angle is a range or not
        if (fieldAngle > 0 && fieldAngleMin <= 0 && fieldAngleMax <= 0)
            return new LightDefinition(name, label, shape, displayColor, fieldAngle);
        else
            return new LightDefinition(name, label, shape, displayColor, fieldAngleMin, fieldAngleMax);
    }

    public static ArrayList<LightDefinition> loadLightDefinitions(File file) throws IOException, InvalidFileVersionException {

        // create reader
        FileStreamReader reader = new FileStreamReader(file);

        // read file version
        byte fileVersion = reader.readByte();

        // check file version & throw exception if invalid
        if (fileVersion != lightDefinitionVersion)
            throw new InvalidFileVersionException("Invalid file version.");

        // get light definition count
        int count = reader.readInt();

        // create lights list
        ArrayList<LightDefinition> lights = new ArrayList<>();

        // iterate through & read all light definitions
        for (int i = 0; i < count; i++)
            lights.add(readLightDefinition(reader));

        // return lights list
        return lights;

    }

    public static void saveStagePlan(StagePlan stagePlan, File file) throws IOException {

        // if file doesn't have the right extension, add it
        if(!file.getName().toLowerCase().endsWith(".slpsp"))
            file = new File(file.getAbsolutePath() + ".slpsp");

        FileStreamWriter writer = new FileStreamWriter(file);

        writer.write(stagePlanVersion);

        ArrayList<LightDefinition> lightDefinitions = new ArrayList<>();

        stagePlan.getStageElements().forEach(stageElement -> {
            if (stageElement instanceof JLight) {
                JLight light = (JLight)stageElement;

                if (!lightDefinitions.contains(light.getModel()))
                    lightDefinitions.add(light.getModel());
            }
        });

        writer.writeInt(lightDefinitions.size());

        for (LightDefinition lightDefinition : lightDefinitions) {
            writeLightDefinition(writer, lightDefinition);
        }

        writer.writeInt(stagePlan.getStageElements().size());

        for (JStageElement stageElement : stagePlan.getStageElements()) {
            writer.writeInt(stageElement.getGridX());
            writer.writeInt(stageElement.getGridY());

            if (stageElement instanceof JBatten) {
                JBatten batten = (JBatten) stageElement;
                writer.write(STAGE_ELEMENT_BATTEN);

                writer.writeInt(batten.getHeightFromFloor());
                writer.writeInt(batten.getLength());
                writer.writeInt(batten.getOrientation().ordinal());
            } else if (stageElement instanceof JLight) {
                JLight light = (JLight) stageElement;
                writer.write(STAGE_ELEMENT_LIGHT);

                writer.writeInt(lightDefinitions.indexOf(light.getModel()));

                writer.writeFloat(light.getFieldAngle());

                writer.write(light.getBeamColor().getRed());
                writer.write(light.getBeamColor().getGreen());
                writer.write(light.getBeamColor().getBlue());

                writer.writeFloat(light.getRotation());
                writer.writeFloat(light.getAngle());

                writer.writeInt(light.getConnectionId().length());
                writer.writeString(light.getConnectionId());

                writer.writeInt(light.getBeamIntensity());
            } else if (stageElement instanceof JDraggableLabel) {
                JDraggableLabel label = (JDraggableLabel) stageElement;
                writer.write(STAGE_ELEMENT_LABEL);

                writer.writeInt(label.getText().length());
                writer.writeString(label.getText());

                writer.write(label.getColor().getRed());
                writer.write(label.getColor().getGreen());
                writer.write(label.getColor().getBlue());

                writer.writeInt(label.getFontSize());

                writer.writeInt(label.getFontFamily().length());
                writer.writeString(label.getFontFamily());
            } else {
                writer.write(STAGE_ELEMENT_NONE);
            }
        }

        writer.close();

    }

    public static StagePlan loadStagePlan(File file) throws IOException, InvalidFileVersionException {

        FileStreamReader reader = new FileStreamReader(file);

        byte fileVersion = reader.readByte();

        if (fileVersion != stagePlanVersion)
            throw new InvalidFileVersionException("Invalid file version.");

        int lightDefinitionCount = reader.readInt();
        ArrayList<LightDefinition> lightDefinitions = new ArrayList<>();

        for (int i = 0; i < lightDefinitionCount; i++) {
            lightDefinitions.add(readLightDefinition(reader));
        }

        int stageElementCount = reader.readInt();
        ArrayList<JStageElement> stageElements = new ArrayList<>();

        for (int i = 0; i < stageElementCount; i++) {

            int x = reader.readInt();
            int y = reader.readInt();

            byte type = reader.readByte();

            switch (type) {
                case STAGE_ELEMENT_BATTEN:
                    int heightFromFloor = reader.readInt();
                    int length = reader.readInt();
                    Orientation orientation = Orientation.values()[reader.readInt()];

                    stageElements.add(new JBatten(x, y, length, orientation, heightFromFloor));
                    break;
                case STAGE_ELEMENT_LIGHT:
                    LightDefinition lightDefinition = lightDefinitions.get(reader.readInt());

                    float fieldAngle = reader.readFloat();
                    Color beamColor = new Color(reader.readByte() & 0xFF, reader.readByte() & 0xFF, reader.readByte() & 0xFF);
                    float rotation = reader.readFloat();
                    float angle = reader.readFloat();
                    String connectionId = reader.readString(reader.readInt());
                    int beamIntensity = reader.readInt();

                    stageElements.add(new JLight(x, y, lightDefinition, beamColor, rotation, angle, fieldAngle, connectionId, beamIntensity));
                    break;
                case STAGE_ELEMENT_LABEL:
                    String text = reader.readString(reader.readInt());
                    Color color = new Color(reader.readByte() & 0xFF, reader.readByte() & 0xFF, reader.readByte() & 0xFF);
                    int fontSize = reader.readInt();
                    String fontFamily = reader.readString(reader.readInt());

                    stageElements.add(new JDraggableLabel(x, y, text, color, fontSize, fontFamily));
                    break;
            }

        }

        reader.close();

        return new StagePlan(stageElements);

    }

}
