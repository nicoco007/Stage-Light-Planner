package com.nicolasgnyra.stagelightplanner.windows;

import com.nicolasgnyra.stagelightplanner.LightDefinition;
import com.nicolasgnyra.stagelightplanner.LightShape;
import com.nicolasgnyra.stagelightplanner.components.*;
import com.nicolasgnyra.stagelightplanner.exceptions.InvalidFileVersionException;
import com.nicolasgnyra.stagelightplanner.helpers.FileHelper;
import com.nicolasgnyra.stagelightplanner.helpers.GridBagLayoutHelper;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class PlannerWindow extends JFrame {
    private File loadedFile = null;
    private JStagePlanner stagePlanner;
    private JFixturesContainer fixtureList;
    private ArrayList<LightDefinition> lightDefinitions;

    public PlannerWindow() {
        setTitle("Stage Light Planner");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        JMenu toolsMenu = new JMenu("Tools");
        JMenu helpMenu = new JMenu("Help");

        fileMenu.add(new JActionMenuItem("New", e -> clear()));
        fileMenu.add(new JActionMenuItem("Open...", e -> open()));
        fileMenu.add(new JActionMenuItem("Save", e -> save()));
        fileMenu.add(new JActionMenuItem("Save as...", e -> saveAs()));

        toolsMenu.add(new JActionMenuItem("Fixture Editor", e -> showFixtureEditor()));

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        JPanel contentPane = new JPanel(new BorderLayout(10, 5));

        JPropertiesContainer propertiesPanel = new JPropertiesContainer();
        stagePlanner = new JStagePlanner(propertiesPanel);
        fixtureList = new JFixturesContainer();

        fixtureList.setMinimumSize(new Dimension(230, 150));

        propertiesPanel.setPreferredSize(new Dimension(250, 0));

        contentPane.add(stagePlanner, BorderLayout.CENTER);
        contentPane.add(propertiesPanel, BorderLayout.LINE_END);
        contentPane.add(fixtureList, BorderLayout.PAGE_START);

        try {
            FileHelper.saveLightDefinitions(new ArrayList<>(Arrays.asList(
                    new LightDefinition("Source Four Zoom Leko", "Zoom Leko", LightShape.OCTAGON, Color.black, 27.0f, 46.0f),
                    new LightDefinition("Altman 65Q Fresnel", "Fresnel", LightShape.CIRCLE, Color.orange, 70.0f),
                    new LightDefinition("Altman 3.5Q Leko", "Leko", LightShape.SQUARE, Color.gray, 38.0f),
                    new LightDefinition("Altman 154 Scoop", "Scoop", LightShape.HEPTAGON, Color.orange, 90.0f)
            )), new File("fixtures.slpfd"));
            lightDefinitions = FileHelper.loadLightDefinitions(new File("fixtures.slpfd"));
        } catch (IOException ex) {
            System.out.println("Whoop dee doo");
        } catch (InvalidFileVersionException ex) {
            System.out.println("Nope");
        }

        reloadFixtureList();

        setContentPane(contentPane);

        validate();
        pack();
    }

    private void showFixtureEditor() {
        FixtureEditorDialog window = new FixtureEditorDialog(lightDefinitions);
        window.setVisible(true);
        reloadFixtureList();
    }

    private void reloadFixtureList() {
        fixtureList.clear();

        fixtureList.addElement(new JBattenDefinition());
        fixtureList.addElement(new JLabelDefinition());

        lightDefinitions.forEach(fixtureList::addLight);
    }

    private void clear() {
        if (stagePlanner.hasUnsavedChanges()) {
            if (JOptionPane.showOptionDialog(this, "You have unsaved changes. Are you sure you want to create a new plan?", "Unsaved changes", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, null, JOptionPane.CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                stagePlanner.getDrawingPane().removeAll();
                loadedFile = null;
            }
        }
    }

    private void open() {
        if (stagePlanner.hasUnsavedChanges()) {
            if (JOptionPane.showOptionDialog(this, "You have unsaved changes. Are you sure you want to load a plan?", "Unsaved changes", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, null, JOptionPane.CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                stagePlanner.getDrawingPane().removeAll();
                loadedFile = null;

                JFileChooser fileChooser = new JFileChooser();

                if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    File loadFrom = fileChooser.getSelectedFile();

                    try {
                        stagePlanner.setStagePlan(FileHelper.loadStagePlan(loadFrom));
                        loadedFile = loadFrom;
                    } catch (IOException | InvalidFileVersionException ex) {
                        System.out.println(ex.getMessage());
                    }
                }
            }
        }
    }

    private void save() {
        if (loadedFile == null || !loadedFile.isFile()) {
            saveAs();
        } else {
            try {
                FileHelper.saveStagePlan(stagePlanner.getStagePlan(), loadedFile);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private void saveAs() {
        JFileChooser fileChooser = new JFileChooser();

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File saveTo = fileChooser.getSelectedFile();

            try {
                FileHelper.saveStagePlan(stagePlanner.getStagePlan(), saveTo);
                loadedFile = saveTo;
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
