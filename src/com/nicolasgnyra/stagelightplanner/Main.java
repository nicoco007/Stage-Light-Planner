package com.nicolasgnyra.stagelightplanner;

import com.nicolasgnyra.stagelightplanner.windows.PlannerWindow;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.out.println("Failed to set look and feel to system theme: " + ex.getMessage());
            ex.printStackTrace();
        }

        PlannerWindow window = new PlannerWindow();

        window.setSize(new Dimension(1280, 720));
        window.setMinimumSize(new Dimension(800, 600));

        window.setVisible(true);
    }
}
