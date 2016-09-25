package com.nicolasgnyra.stagelightplanner.windows;

import com.nicolasgnyra.stagelightplanner.LightDefinition;
import com.nicolasgnyra.stagelightplanner.components.*;
import com.nicolasgnyra.stagelightplanner.exceptions.InvalidFileVersionException;
import com.nicolasgnyra.stagelightplanner.helpers.ExceptionHelper;
import com.nicolasgnyra.stagelightplanner.helpers.FileHelper;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);

        WelcomeWindow welcomeWindow = new WelcomeWindow(this);
        welcomeWindow.setVisible(true);
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

    void clear() {
        if (!stagePlanner.hasUnsavedChanges() || JOptionPane.showOptionDialog(this, "You have unsaved changes. Are you sure you want to create a new plan?", "Unsaved changes", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, null, JOptionPane.CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            stagePlanner.getDrawingPane().removeAll();
            stagePlanner.repaint();
            loadedFile = null;
            stagePlanner.setHasUnsavedChanges(false);
        }
    }

    boolean open() {
        if (!stagePlanner.hasUnsavedChanges() || JOptionPane.showOptionDialog(this, "You have unsaved changes. Are you sure you want to load a plan?", "Unsaved changes", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, null, JOptionPane.CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            JFileChooser fileChooser = new JFileChooser();

            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                stagePlanner.getDrawingPane().removeAll();
                loadedFile = null;

                File loadFrom = fileChooser.getSelectedFile();

                try {
                    stagePlanner.setStagePlan(FileHelper.loadStagePlan(loadFrom));
                    stagePlanner.setHasUnsavedChanges(false);
                    loadedFile = loadFrom;
                    return true;
                } catch (InvalidFileVersionException ex) {
                    JOptionPane.showMessageDialog(this, "That plan was created with an older version of the program and cannot be loaded.", "Unable to load file", JOptionPane.WARNING_MESSAGE);
                } catch (Exception ex) {
                    ExceptionHelper.showErrorDialog(this, ex);
                }
            }
        }

        return false;
    }

    private boolean save() {
        if (loadedFile == null || !loadedFile.isFile()) {
            return saveAs();
        } else {
            try {
                FileHelper.saveStagePlan(stagePlanner.getStagePlan(), loadedFile);
                stagePlanner.setHasUnsavedChanges(false);
                return true;
            } catch (Exception ex) {
                ExceptionHelper.showErrorDialog(this, ex);
            }
        }

        return false;
    }

    private boolean saveAs() {
        JFileChooser fileChooser = new JFileChooser();

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File saveTo = fileChooser.getSelectedFile();

            try {
                FileHelper.saveStagePlan(stagePlanner.getStagePlan(), saveTo);
                stagePlanner.setHasUnsavedChanges(false);
                loadedFile = saveTo;
                return true;
            } catch (Exception ex) {
                ExceptionHelper.showErrorDialog(this, ex);
            }
        }

        return false;
    }
}
