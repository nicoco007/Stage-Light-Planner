package com.nicolasgnyra.stagelightplanner.tests;

import com.nicolasgnyra.stagelightplanner.LightDefinition;
import com.nicolasgnyra.stagelightplanner.LightShape;
import com.nicolasgnyra.stagelightplanner.Orientation;
import com.nicolasgnyra.stagelightplanner.StagePlan;
import com.nicolasgnyra.stagelightplanner.components.JBatten;
import com.nicolasgnyra.stagelightplanner.components.JDraggableLabel;
import com.nicolasgnyra.stagelightplanner.components.JLight;
import com.nicolasgnyra.stagelightplanner.components.JStageElement;
import com.nicolasgnyra.stagelightplanner.exceptions.InvalidFileVersionException;
import com.nicolasgnyra.stagelightplanner.helpers.FileHelper;
import com.nicolasgnyra.stagelightplanner.helpers.PaintHelper;
import org.junit.Test;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class UnitTests {

    @Test
    public void polygonsShouldStretchToBoundsProperly() {

        assertEquals(new Rectangle(50, 50), PaintHelper.getRegularPolygon(3, 0, 0, 50, 50).getBounds());
        assertEquals(new Rectangle(50, 50), PaintHelper.getRegularPolygon(4, 0, 0, 50, 50, 22.5).getBounds());
        assertEquals(new Rectangle(50, 50), PaintHelper.getRegularPolygon(5, 0, 0, 50, 50, 45).getBounds());
        assertEquals(new Rectangle(50, 50), PaintHelper.getRegularPolygon(6, 0, 0, 50, 50, 67.5).getBounds());
        assertEquals(new Rectangle(50, 50), PaintHelper.getRegularPolygon(7, 0, 0, 50, 50, 90).getBounds());
        assertEquals(new Rectangle(50, 50), PaintHelper.getRegularPolygon(8, 0, 0, 50, 50, 112.5).getBounds());
        assertEquals(new Rectangle(50, 50), PaintHelper.getRegularPolygon(9, 0, 0, 50, 50, 135).getBounds());
        assertEquals(new Rectangle(50, 50), PaintHelper.getRegularPolygon(10, 0, 0, 50, 50, 157.5).getBounds());
        assertEquals(new Rectangle(50, 50), PaintHelper.getRegularPolygon(11, 0, 0, 50, 50, 180).getBounds());
        assertEquals(new Rectangle(50, 50), PaintHelper.getRegularPolygon(12, 0, 0, 50, 50, 202.5).getBounds());

    }

    @Test
    public void calculatedBeamSizeShouldBeCorrect() {

        // expected results were calculated on paper beforehand
        assertEquals(new Rectangle(-100, 0, 200, 200), PaintHelper.getBeamRect(100, 90, 0));
        assertEquals(new Rectangle(0, 0, 100, 83), PaintHelper.getBeamRect(100, 45, 22.5));
        assertEquals(new Rectangle(130, 0, 321, 44), PaintHelper.getBeamRect(100, 25, 65));
        assertEquals(new Rectangle(-290, 0, 225, 69), PaintHelper.getBeamRect(100, 38, -52));

    }

    @Test
    public void stagePlansShouldSaveAndLoadProperly() throws IOException, InvalidFileVersionException {

        // create elements
        JBatten batten = new JBatten(12, 56, 300, Orientation.VERTICAL, 350);
        JLight light = new JLight(87, 9, new LightDefinition("Display name", "Label", LightShape.HEPTAGON, new Color(64, 128, 192), 35.0f, 45.0f), new Color(255, 192, 64), 22.5f, 67.5f, 40.0f, "Connection ID", 80);
        JDraggableLabel label = new JDraggableLabel(33, 26, "Text", new Color(128, 64, 255), 24, "Comic Sans MS");

        // create plan
        StagePlan stagePlan = new StagePlan(new ArrayList<>(Arrays.asList(new JStageElement[] { batten, light, label })));

        // create file
        File saveLocation = new File("test.slpsp");

        // save & load & delete file
        FileHelper.saveStagePlan(stagePlan, saveLocation);
        StagePlan loadedStagePlan = FileHelper.loadStagePlan(saveLocation);
        saveLocation.delete();

        // assert
        assertEquals(stagePlan, loadedStagePlan);

    }

}