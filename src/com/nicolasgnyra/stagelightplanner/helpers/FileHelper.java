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

/**
 * FileHelper Class:
 * Contains static methods for saving and loading files.
 *
 * Date: 2016-09-27
 *
 * @author Nicolas Gnyra
 * @version 1.0
 */
public class FileHelper {

    private static final byte lightDefinitionVersion = 1;   // light definition file version
    private static final byte stagePlanVersion = 1;         // stage plan file version

    // these values SHOULD NOT change between version
    private static final byte STAGE_ELEMENT_NONE = 0;       // stage element ID
    private static final byte STAGE_ELEMENT_BATTEN = 1;     // batten ID
    private static final byte STAGE_ELEMENT_LIGHT = 2;      // light ID
    private static final byte STAGE_ELEMENT_LABEL = 3;      // label ID

    /**
     * saveLightDefinitions(ArrayList<LightDefinition>, File) Method:
     * Saves the light definitions in the array list to the specified file.
     *
     * Input: Lights list and target file.
     *
     * Process: Converts lights list to bytes and saves them to the file.
     *
     * Output: None.
     *
     * @param lights List of lights.
     * @param file Target file.
     * @throws IOException File saving exception.
     */
    public static void saveLightDefinitions(ArrayList<LightDefinition> lights, File file) throws IOException {

        // if file doesn't have the right extension, add it
        if(!file.getName().toLowerCase().endsWith(".slpfd"))
            file = new File(file.getAbsolutePath() + ".slpfd");

        // create writer
        FileStreamWriter writer = new FileStreamWriter(file);

        // write version
        writer.write(lightDefinitionVersion);

        // write list size
        writer.writeInt(lights.size());

        // iterate through lights & write each
        for (LightDefinition light : lights)
            writeLightDefinition(writer, light);

        // close writer
        writer.close();

    }

    /**
     * loadLightDefinitions(File) Method:
     * Loads light definitions from specified file.
     *
     * Input: Source file.
     *
     * Process: Reads the light definitions from the specified file.
     *
     * Output: List of light definitions.
     *
     * @param file Source file.
     * @return List of light definitions.
     * @throws IOException File loading exception.
     * @throws InvalidFileVersionException Attempting to load an older file.
     */
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

    /**
     * writeLightDefinition(FileStreamWriter, LightDefinition) Method:
     * Writes a light definition to the specified file writer.
     *
     * Input: File stream writer, light definition.
     *
     * Process: Writes all values to file.
     *
     * Output: None.
     *
     * @param writer File stream writer.
     * @param light Light definition.
     * @throws IOException File saving exception.
     */
    private static void writeLightDefinition(FileStreamWriter writer, LightDefinition light) throws IOException {

        // write display name
        writer.writeInt(light.getDisplayName().length());
        writer.writeString(light.getDisplayName());

        // write label
        writer.writeInt(light.getLabel().length());
        writer.writeString(light.getLabel());

        // write shape
        writer.writeInt(light.getShape().ordinal());

        // write color
        writer.write(light.getDisplayColor().getRed());
        writer.write(light.getDisplayColor().getGreen());
        writer.write(light.getDisplayColor().getBlue());

        // write field angle
        writer.writeFloat(light.getFieldAngle());
        writer.writeFloat(light.getFieldAngleMin());
        writer.writeFloat(light.getFieldAngleMax());

    }

    /**
     * readLightDefinition(FileStreamReader) Method:
     * Reads a light definition from the specified file stream reader.
     *
     * Input: File stream reader.
     *
     * Process: Reads all values and creates a new LightDefinition instance.
     *
     * Output: Read light definition.
     *
     * @param reader File stream reader
     * @return Read light definition.
     * @throws IOException File loading exception.
     */
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

    /**
     * saveStagePlan(StagePlan, File) Method:
     * Saves the specified stage plan to the specified file.
     *
     * Input: Stage plan, target file.
     *
     * Process: Saves light definitions & actual stage elements.
     *
     * Output: None.
     *
     * @param stagePlan Stage plan
     * @param file Target file
     * @throws IOException File saving exception
     */
    public static void saveStagePlan(StagePlan stagePlan, File file) throws IOException {

        // if file doesn't have the right extension, add it
        if(!file.getName().toLowerCase().endsWith(".slpsp"))
            file = new File(file.getAbsolutePath() + ".slpsp");

        // create writer
        FileStreamWriter writer = new FileStreamWriter(file);

        // write version
        writer.write(stagePlanVersion);

        // create light definitions list
        ArrayList<LightDefinition> lightDefinitions = new ArrayList<>();

        // get light definitions from all lights
        stagePlan.getStageElements().forEach(stageElement -> {
            if (stageElement instanceof JLight) {
                JLight light = (JLight)stageElement;

                if (!lightDefinitions.contains(light.getModel()))
                    lightDefinitions.add(light.getModel());
            }
        });

        // write amount of light definitions
        writer.writeInt(lightDefinitions.size());

        // write all light definitions
        for (LightDefinition lightDefinition : lightDefinitions)
            writeLightDefinition(writer, lightDefinition);

        // write amount of stage elements
        writer.writeInt(stagePlan.getStageElements().size());

        // iterate through stage elements
        for (JStageElement stageElement : stagePlan.getStageElements()) {

            // write coordinates
            writer.writeInt(stageElement.getGridX());
            writer.writeInt(stageElement.getGridY());

            // write based on class type
            if (stageElement instanceof JBatten) {

                // cast as JBatten & write type
                JBatten batten = (JBatten) stageElement;
                writer.write(STAGE_ELEMENT_BATTEN);

                // write heigth, length, orientation
                writer.writeInt(batten.getHeightFromFloor());
                writer.writeInt(batten.getLength());
                writer.writeInt(batten.getOrientation().ordinal());

            } else if (stageElement instanceof JLight) {

                // cast as JLight & write type
                JLight light = (JLight) stageElement;
                writer.write(STAGE_ELEMENT_LIGHT);

                // write index of light definition
                writer.writeInt(lightDefinitions.indexOf(light.getModel()));

                // write field angle
                writer.writeFloat(light.getFieldAngle());

                // write color
                writer.write(light.getBeamColor().getRed());
                writer.write(light.getBeamColor().getGreen());
                writer.write(light.getBeamColor().getBlue());

                // write angles
                writer.writeFloat(light.getRotation());
                writer.writeFloat(light.getAngle());

                // write connection ID
                writer.writeInt(light.getConnectionId().length());
                writer.writeString(light.getConnectionId());

                // write beam intensity
                writer.writeInt(light.getBeamIntensity());

            } else if (stageElement instanceof JDraggableLabel) {

                // cast as JDraggableLabel & write type
                JDraggableLabel label = (JDraggableLabel) stageElement;
                writer.write(STAGE_ELEMENT_LABEL);

                // write text
                writer.writeInt(label.getText().length());
                writer.writeString(label.getText());

                // write color
                writer.write(label.getColor().getRed());
                writer.write(label.getColor().getGreen());
                writer.write(label.getColor().getBlue());

                // write font size
                writer.writeInt(label.getFontSize());

                // write font family
                writer.writeInt(label.getFontFamily().length());
                writer.writeString(label.getFontFamily());

            } else {

                // write generic stage element id
                writer.write(STAGE_ELEMENT_NONE);

            }

        }

        // close writer
        writer.close();

    }

    /**
     * loadStagePlan(File) Method:
     * Loads a stage plan from the specified file.
     *
     * Input: Source file.
     *
     * Process: Checks version, loads light definitions & stage elements.
     *
     * Output: Loaded stage plan.
     *
     * @param file Source file.
     * @return Loaded stage plan.
     * @throws IOException File load exception
     * @throws InvalidFileVersionException File too old
     */
    public static StagePlan loadStagePlan(File file) throws IOException, InvalidFileVersionException {

        // create reader
        FileStreamReader reader = new FileStreamReader(file);

        // get file version
        byte fileVersion = reader.readByte();

        // check file version
        if (fileVersion != stagePlanVersion)
            throw new InvalidFileVersionException("Invalid file version.");

        // get light definition count & create list
        int lightDefinitionCount = reader.readInt();
        ArrayList<LightDefinition> lightDefinitions = new ArrayList<>();

        // read light definitions
        for (int i = 0; i < lightDefinitionCount; i++)
            lightDefinitions.add(readLightDefinition(reader));

        // get stage element count & create list
        int stageElementCount = reader.readInt();
        ArrayList<JStageElement> stageElements = new ArrayList<>();

        // read all stage elements
        for (int i = 0; i < stageElementCount; i++) {

            // get coordinates
            int x = reader.readInt();
            int y = reader.readInt();

            // get type
            byte type = reader.readByte();

            // switch based on type
            switch (type) {
                case STAGE_ELEMENT_BATTEN:

                    // read values
                    int heightFromFloor = reader.readInt();
                    int length = reader.readInt();
                    Orientation orientation = Orientation.values()[reader.readInt()];

                    // add element
                    stageElements.add(new JBatten(x, y, length, orientation, heightFromFloor));

                    break;

                case STAGE_ELEMENT_LIGHT:

                    // get light definition
                    LightDefinition lightDefinition = lightDefinitions.get(reader.readInt());

                    // read values
                    float fieldAngle = reader.readFloat();
                    Color beamColor = new Color(reader.readByte() & 0xFF, reader.readByte() & 0xFF, reader.readByte() & 0xFF);
                    float rotation = reader.readFloat();
                    float angle = reader.readFloat();
                    String connectionId = reader.readString(reader.readInt());
                    int beamIntensity = reader.readInt();

                    // add element
                    stageElements.add(new JLight(x, y, lightDefinition, beamColor, rotation, angle, fieldAngle, connectionId, beamIntensity));

                    break;

                case STAGE_ELEMENT_LABEL:

                    // read values
                    String text = reader.readString(reader.readInt());
                    Color color = new Color(reader.readByte() & 0xFF, reader.readByte() & 0xFF, reader.readByte() & 0xFF);
                    int fontSize = reader.readInt();
                    String fontFamily = reader.readString(reader.readInt());

                    // add element
                    stageElements.add(new JDraggableLabel(x, y, text, color, fontSize, fontFamily));

                    break;

            }

        }

        // close reader
        reader.close();

        // return stage plan
        return new StagePlan(stageElements);

    }

}
