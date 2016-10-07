package com.nicolasgnyra.stagelightplanner;

import com.nicolasgnyra.stagelightplanner.windows.PlannerWindow;

import javax.swing.*;
import java.awt.*;

/**
 * Main Class:
 * Class that initializes the StageLightPlanner program.
 *
 * Date: 2016-09-27
 *
 * @author Nicolas Gnyra
 * @version 1.0
 */
public class Main {

    /**
     * main(String[]) Method:
     * Called when the program is started. Initializes the program.
     *
     * Input: Command-line arguments.
     *
     * Process: Sets the look and feel to system default, then shows a new instance of the PlannerWindow class.
     *
     * Output: The program's main window is shown.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {

        // try to set the system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.out.println("Failed to set look and feel to system theme: " + ex.getMessage());
            ex.printStackTrace();
        }

        // create window
        PlannerWindow window = new PlannerWindow();

        // set window size & min size
        window.setSize(new Dimension(1280, 720));
        window.setMinimumSize(new Dimension(800, 600));

        // display window
        window.setVisible(true);

    }

}
