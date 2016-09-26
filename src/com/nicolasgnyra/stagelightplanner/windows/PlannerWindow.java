package com.nicolasgnyra.stagelightplanner.windows;

import com.nicolasgnyra.stagelightplanner.LightDefinition;
import com.nicolasgnyra.stagelightplanner.components.*;
import com.nicolasgnyra.stagelightplanner.exceptions.InvalidFileVersionException;
import com.nicolasgnyra.stagelightplanner.helpers.ExceptionHelper;
import com.nicolasgnyra.stagelightplanner.helpers.FileHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PlannerWindow extends JFrame implements WindowListener {
    private File loadedFile = null;
    private JStagePlanner stagePlanner;
    private JFixturesContainer fixtureList;
    private ArrayList<LightDefinition> lightDefinitions;

    public PlannerWindow() {
        setTitle("Stage Light Planner");

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        JMenu toolsMenu = new JMenu("Tools");
        JMenu helpMenu = new JMenu("Help");

        fileMenu.add(new JActionMenuItem("New",         e -> clear(),       KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK)));
        fileMenu.add(new JActionMenuItem("Open...",     e -> open(),        KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK)));
        fileMenu.add(new JActionMenuItem("Save",        e -> save(),        KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK)));
        fileMenu.add(new JActionMenuItem("Save as...",  e -> saveAs(),      KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK | Event.SHIFT_MASK)));
        fileMenu.addSeparator();
        fileMenu.add(new JActionMenuItem("Quit",        e -> shutdown()));

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

        addWindowListener(this);

        validate();
        pack();
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);

        if (b) {
            WelcomeDialog welcomeDialog = new WelcomeDialog(this);
            welcomeDialog.setVisible(true);
        }
    }

    private void showFixtureEditor() {
        FixtureEditorDialog window = new FixtureEditorDialog(this, lightDefinitions);
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

    private void shutdown() {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    @Override
    public void windowClosing(WindowEvent e) {
        if (stagePlanner.hasUnsavedChanges()) {
            int result = JOptionPane.showConfirmDialog(this, "There are unsaved changes. Would you like to save before exiting?", "Unsaved changes", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if ((result == JOptionPane.YES_OPTION && !save()) || result == JOptionPane.CANCEL_OPTION)
                return;
        }

        dispose();
    }

    @Override
    public void windowClosed(WindowEvent e) {
        try {
            FileHelper.saveLightDefinitions(lightDefinitions, new File("fixtures.slpfd"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void windowOpened(WindowEvent e) { }

    @Override
    public void windowIconified(WindowEvent e) { }

    @Override
    public void windowDeiconified(WindowEvent e) { }

    @Override
    public void windowActivated(WindowEvent e) { }

    @Override
    public void windowDeactivated(WindowEvent e) { }
}
