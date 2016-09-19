/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.nicolasgnyra.stagelightplanner.helpers;

import javax.swing.*;
import javax.swing.SpringLayout;
import java.awt.*;

public class SpringHelper {
    private static SpringLayout.Constraints getComponentConstraints(int row, int col, Container parent, int cols) {

        // get container layout
        SpringLayout layout = (SpringLayout) parent.getLayout();

        // get component
        Component c = parent.getComponent(row * cols + col);

        // return component constraints
        return layout.getConstraints(c);

    }

    public static void makeCompactVerticalGrid(Container parent, int rows, int cols, int initialX, int initialY, int xPad, int yPad) {

        // check that parent is actually using a SpringLayout
        if (!(parent.getLayout() instanceof SpringLayout))
            throw new IllegalArgumentException("Container must have a SpringLayout");

        if (rows == 0 || cols == 0)
            return;

        // get parent layout
        SpringLayout layout = (SpringLayout) parent.getLayout();

        // get amount of components
        int componentCount = rows * cols;

        Spring width = Spring.constant(0);

        for (int i = 0; i < componentCount; i++) {
            SpringLayout.Constraints cons = layout.getConstraints(parent.getComponent(i));

            width = Spring.max(cons.getWidth(), width);
        }

        //Apply the new width/height Spring. This forces all the
        //components to have the same size.
        for (int i = 0; i < componentCount; i++) {
            SpringLayout.Constraints cons = layout.getConstraints(parent.getComponent(i));

            cons.setWidth(width);
        }

        // create variables for previous constraints
        SpringLayout.Constraints lastCons = null;

        // iterate through all components
        for (int i = 0; i < componentCount; i++) {

            // get current component's constraints
            SpringLayout.Constraints cons = layout.getConstraints(parent.getComponent(i));

            // create X variable, with default value of initial X
            Spring x = Spring.constant(initialX);

            // if we have previous constraints and this is not the first column, set to last constraints' eastern point (X + width)
            if (i % cols > 0 && lastCons != null)
                x = Spring.sum(lastCons.getConstraint(SpringLayout.EAST), Spring.constant(xPad));

            // set component's X position
            cons.setX(x);

            // set last constraints
            lastCons = cons;
        }

        // get initial Y position
        Spring y = Spring.constant(initialY);

        // iterate through rows
        for (int r = 0; r < rows; r++) {

            // set height (minimum 0)
            Spring height = Spring.constant(0);

            // iterate through columns
            for (int c = 0; c < cols; c++) {

                // set height to maximum between currently saved minimum height and component height
                height = Spring.max(height, getComponentConstraints(r, c, parent, cols).getHeight());

            }

            for (int c = 0; c < cols; c++) {

                // get component constraints
                SpringLayout.Constraints constraints = getComponentConstraints(r, c, parent, cols);

                // set Y position
                constraints.setY(y);

                // set height
                constraints.setHeight(height);

            }

            // set current Y to sum between height of row and Y padding
            y = Spring.sum(y, Spring.sum(height, Spring.constant(yPad)));

        }

        // get parent constraints
        SpringLayout.Constraints pCons = layout.getConstraints(parent);

        if (lastCons != null)
            pCons.setConstraint(SpringLayout.EAST, lastCons.getConstraint(SpringLayout.EAST));
    }
}
