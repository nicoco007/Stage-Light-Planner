/*
 * Copyright © 2016 Nicolas Gnyra
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nicolasgnyra.stagelightplanner.helpers;

import javax.swing.*;
import java.awt.*;

/**
 * GridBagLayoutHelper Class:
 * Contains static methods to easily add components to GridBagLayouts
 *
 * Date: 2016-09-27
 *
 * @author Nicolas Gnyra
 * @version 2.0
 */
public class GridBagLayoutHelper {

    /**
     * getGridBagLayoutConstraints(int, int) Method:
     * Adds a component to a GridBagLayout using the specified values.
     *
     * Input: X and y coordinates.
     *
     * Process: Calls the overloads method.
     *
     * Output: Generated GridBagConstraints class.
     *
     * @param x Grid column
     * @param y Grid row
     */
    public static GridBagConstraints getGridBagLayoutConstraints(int x, int y) {

        // call overloads method with default values
        return getGridBagLayoutConstraints(x, y, GridBagConstraints.CENTER, 1, 1, 0, 0, false, false, new Insets(0, 0, 0, 0));

    }

    /**
     * getGridBagLayoutConstraints(int, int, int) Method:
     * Adds a component to a GridBagLayout using the specified values.
     *
     * Input: X and y coordinates, anchor.
     *
     * Process: Calls the overloads method.
     *
     * Output: Generated GridBagConstraints class.
     *
     * @param x Grid column
     * @param y Grid row
     * @param anchor Alignment of the component
     */
    public static GridBagConstraints getGridBagLayoutConstraints(int x, int y, int anchor) {

        // call overloads method with default values
        return getGridBagLayoutConstraints(x, y, anchor, 1, 1, 0, 0, false, false, new Insets(0, 0, 0, 0));

    }

    /**
     * getGridBagLayoutConstraints(int, int, int, int, int, double, double, boolean, boolean) Method:
     * Adds a component to a GridBagLayout using the specified values.
     *
     * Input: X and y coordinates, anchor, grid width and height, horizontal and vertical weight,
     * horizontal and vertical fill.
     *
     * Process: Calls the overloads method.
     *
     * Output: Generated GridBagConstraints class.
     *
     * @param x Grid column
     * @param y Grid row
     * @param anchor Alignment of the component
     * @param gridwidth Grid width
     * @param gridheight Grid height
     * @param weightx X weight
     * @param weighty Y weight
     * @param fillHorizontal Fill horizontally to parent width
     * @param fillVertical Fill vertically to parent height
     */
    public static GridBagConstraints getGridBagLayoutConstraints(int x, int y, int anchor,
                                                                 int gridwidth, int gridheight, double weightx, double weighty,
                                                                 boolean fillHorizontal, boolean fillVertical) {

        // call overloads method with default values
        return getGridBagLayoutConstraints(x, y, anchor, gridwidth, gridheight, weightx, weighty, fillHorizontal, fillVertical, new Insets(0, 0, 0, 0));

    }

    /**
     * getGridBagLayoutConstraints(int, int, int, int, int, double, double, boolean, boolean, Insets) Method:
     * Adds a component to a GridBagLayout using the specified values.
     *
     * Input: X and y coordinates, anchor, grid width and height, horizontal and vertical weight,
     * Input: X and y coordinates, anchor, grid width and height, horizontal and vertical weight,
     * horizontal and vertical fill, and insets.
     *
     * Process: Creates new GridBagConstraints, sets the specified values, and returns it.
     *
     * Output: Generated GridBagConstraints class.
     *
     * @param x Grid column
     * @param y Grid row
     * @param anchor Alignment of the component
     * @param gridwidth Grid width
     * @param gridheight Grid height
     * @param weightx X weight
     * @param weighty Y weight
     * @param fillHorizontal Fill horizontally to parent width
     * @param fillVertical Fill vertically to parent height
     * @param insets Component margins
     */
    public static GridBagConstraints getGridBagLayoutConstraints(int x, int y, int anchor,
                                                   int gridwidth, int gridheight, double weightx, double weighty,
                                                   boolean fillHorizontal, boolean fillVertical, Insets insets) {

        // create constraints
        GridBagConstraints constraints = new GridBagConstraints();

        // set values
        constraints.anchor = anchor;
        constraints.insets = insets;
        constraints.gridheight = gridheight;
        constraints.gridwidth = gridwidth;
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.weightx = weightx;
        constraints.weighty = weighty;

        // set fill
        if (fillHorizontal && fillVertical)
            constraints.fill = GridBagConstraints.BOTH;
        else if (fillHorizontal)
            constraints.fill = GridBagConstraints.HORIZONTAL;
        else if (fillVertical)
            constraints.fill = GridBagConstraints.VERTICAL;
        else
            constraints.fill = GridBagConstraints.NONE;

        // return constraints
        return constraints;

    }

    /**
     * addVerticalGlue(Container, int) Method:
     * Adds vertical glue to the GridBagLayout at the specified row.
     *
     * Input: Container & row.
     *
     * Process: Creates constraints that will fill as much as possible and adds an empty component to fill that space.
     *
     * Output: None.
     *
     * @param container GridBagLayout container
     * @param gridy Row
     */
    public static void addVerticalGlue(Container container, int gridy) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.VERTICAL;
        constraints.gridy = gridy;
        constraints.weighty = 1.0f;

        container.add(Box.createVerticalGlue(), constraints);
    }
}
