package com.nicolasgnyra.stagelightplanner.windows;

import com.nicolasgnyra.stagelightplanner.LightDefinition;
import com.nicolasgnyra.stagelightplanner.components.*;
import com.nicolasgnyra.stagelightplanner.exceptions.InvalidFileVersionException;
import com.nicolasgnyra.stagelightplanner.helpers.ExceptionHelper;
import com.nicolasgnyra.stagelightplanner.helpers.FileHelper;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * PlannerWindow Class:
 * The main window of the Stage Light Planner software. Contains all elements necessary for planning a stage.
 *
 * Date: 2016-09-27
 *
 * @author Nicolas Gnyra
 * @version 1.0
 */
public class PlannerWindow extends JFrame implements WindowListener {

    private File loadedFile = null;                                                                     // currently loaded file
    private final JStagePlanner stagePlanner;                                                           // stage planner
    private final JFixturesContainer fixtureList;                                                       // fixtures container
    private ArrayList<LightDefinition> lightDefinitions = new ArrayList<LightDefinition>();             // list of light definitions
    private FileFilter stagePlanFileFilter = new FileNameExtensionFilter("Stage Plan Files", "slpsp");  // stage plan file filter

    /**
     * PlannerWindow() Constructor:
     * Creates a new instance of the PlannerWindow class.
     *
     * Input: None.
     *
     * Process: Creates all the components necessary for creating & editing stage plans.
     *
     * Output: A new instance of the PlannerWindow class.
     */
    public PlannerWindow() {

        // set title & default closing operation (prevents exiting with unsaved changes)
        setTitle("Stage Light Planner");
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        // create window menu bar
        JMenuBar menuBar = new JMenuBar();

        // create file & tools menus
        JMenu fileMenu = new JMenu("File");
        JMenu toolsMenu = new JMenu("Tools");
        JMenu helpMenu = new JMenu("Help");

        // add items to file menu
        fileMenu.add(new JActionMenuItem("New",         e -> clear(),       KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK)));
        fileMenu.add(new JActionMenuItem("Open...",     e -> open(),        KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK)));
        fileMenu.add(new JActionMenuItem("Save",        e -> save(),        KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK)));
        fileMenu.add(new JActionMenuItem("Save as...",  e -> saveAs(),      KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK | Event.SHIFT_MASK)));
        fileMenu.addSeparator();
        fileMenu.add(new JActionMenuItem("Quit",        e -> shutdown()));

        // add fixture editor to tools
        toolsMenu.add(new JActionMenuItem("Fixture Editor", e -> showFixtureEditor()));

        // add help options to help menu
        helpMenu.add(new JActionMenuItem("Quick Start", e -> showQuickStart()));
        helpMenu.addSeparator();
        helpMenu.add(new JActionMenuItem("Online Help", e -> openUrl("https://nicoco007.github.io/Stage-Light-Planner/")));
        helpMenu.add(new JActionMenuItem("Report a Bug", e -> openUrl("https://github.com/nicoco007/Stage-Light-Planner/issues")));

        // add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);

        // set window menu bar
        setJMenuBar(menuBar);

        // create content pane with border layout
        JPanel contentPane = new JPanel(new BorderLayout(10, 5));

        // create properties container & define stage planner & fixture list variables
        JPropertiesContainer propertiesPanel = new JPropertiesContainer();
        stagePlanner = new JStagePlanner(propertiesPanel);
        fixtureList = new JFixturesContainer();

        // set fixture list minimum size
        fixtureList.setMinimumSize(new Dimension(230, 150));

        // set properties panel preferred size
        propertiesPanel.setPreferredSize(new Dimension(250, 0));

        // add components to content pane
        contentPane.add(stagePlanner, BorderLayout.CENTER);
        contentPane.add(propertiesPanel, BorderLayout.LINE_END);
        contentPane.add(fixtureList, BorderLayout.PAGE_START);

        // create fixture definitions file
        File fixturesFile = new File("fixtures.slpfd");

        // if the file exists, attempt to load it
        if (fixturesFile.exists() && fixturesFile.isFile()) {
            try {
                lightDefinitions = FileHelper.loadLightDefinitions(fixturesFile);
            } catch (InvalidFileVersionException ex) {
                JOptionPane.showMessageDialog(this, "Failed to load fixtures file: File created in different version.", "Oops!", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                ExceptionHelper.showErrorDialog(this, ex);
            }
        }

        // reload the fixture list
        reloadFixtureList();

        // set the window's content pane
        setContentPane(contentPane);

        // add window listener
        addWindowListener(this);

        // pack window
        pack();

    }

    /**
     * setVisible(boolean) Method:
     * Shows or hides the window.
     *
     * Input: Whether to show or hide the window.
     *
     * Process: Calls the superclass method & if we are showing the window, show the welcome dialog.
     *
     * Output: None.
     *
     * @param b Whether to show or hide the window.
     */
    @Override
    public void setVisible(boolean b) {

        // call superclass method
        super.setVisible(b);

        // if we are showing, show the welcome dialog
        if (b) {
            WelcomeDialog welcomeDialog = new WelcomeDialog(this);
            welcomeDialog.setVisible(true);
        }

    }

    /**
     * showFixtureEditor() Method:
     * Shows the fixture editor dialog.
     *
     * Input: None.
     *
     * Process: Creates a new instance of the FixtureEditorDialog class and shows it.
     *
     * Output: None.
     */
    private void showFixtureEditor() {

        // create the dialog
        FixtureEditorDialog dialog = new FixtureEditorDialog(this, lightDefinitions);

        // show the dialog (this hangs because the modality type is set to application modal)
        dialog.setVisible(true);

        // reload the fixture list after dialog is closed
        reloadFixtureList();

    }

    /**
     * showQuickStart() Method:
     * Shows the quick start dialog.
     *
     * Input: None.
     *
     * Process: Creates a new instance of the QuickStartDialog class and shows it.
     *
     * Output: None.
     */
    private void showQuickStart() {

        // create the dialog
        QuickStartDialog dialog = new QuickStartDialog(this);

        // show the dialog
        dialog.setVisible(true);

    }

    /**
     * openUrl(String) Method:
     * Opens the specified URL.
     *
     * Input: URL to open.
     *
     * Process: Gets the desktop instance and browses to the specified URL.
     *
     * Output: Browser opened at specified URL.
     *
     * @param uri URL to open.
     */
    private void openUrl(String uri) {

        // wrap in try/catch statement
        try {

            // attempt to browse to specified URI
            Desktop.getDesktop().browse(new URI(uri));

        } catch(Exception ex) {

            // show an error dialog
            ExceptionHelper.showErrorDialog(this, ex);

        }

    }

    /**
     * reloadFixtureList() Method:
     * Reloads the fixtures present in the fixtures container.
     *
     * Input: None.
     *
     * Process: Clears the fixtures container, adds the default elements, and adds each of the light definitions.
     *
     * Output: None.
     */
    private void reloadFixtureList() {

        // clear list
        fixtureList.clear();

        // add default elements
        fixtureList.addElement(new JBattenDefinition());
        fixtureList.addElement(new JLabelDefinition());

        // load user defined lights
        lightDefinitions.forEach(fixtureList::addLight);

    }

    /**
     * clear() Method:
     * Clears the stage planner.
     *
     * Input: None.
     *
     * Process: Clears the stage planner & repaints it.
     *
     * Output: None.
     */
    void clear() {

        // check that there are no unsaved changes or that the user doesn't want to save his changes
        if (!stagePlanner.hasUnsavedChanges() || JOptionPane.showOptionDialog(this, "You have unsaved changes. Are you sure you want to create a new plan?", "Unsaved changes", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, JOptionPane.CANCEL_OPTION) == JOptionPane.YES_OPTION) {

            // remove all components & repaint
            stagePlanner.getDrawingPane().removeAll();
            stagePlanner.repaint();

            // set loaded file to nothing
            loadedFile = null;

            // set has unsaved changes to false
            stagePlanner.setHasUnsavedChanges(false);
        }

    }

    /**
     * open() Method:
     * Opens a file and loads it into the stage planner.
     *
     * Input: None.
     *
     * Process: Shows a file selection dialog and loads the selected file.
     *
     * Output: Whether the operation was successful or not.
     *
     * @return Whether the operation was successful or not.
     */
    boolean open() {

        // check that there are no unsaved changes or that the user doesn't want to save his changes
        if (!stagePlanner.hasUnsavedChanges() || JOptionPane.showOptionDialog(this, "You have unsaved changes. Are you sure you want to load a plan?", "Unsaved changes", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, null, JOptionPane.CANCEL_OPTION) == JOptionPane.OK_OPTION) {

            // create file chooser & set filter
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(stagePlanFileFilter);

            // show open dialog & make sure the user selected a file
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

                // clear the stage planner & set loaded file to nothing
                stagePlanner.getDrawingPane().removeAll();
                loadedFile = null;

                // get selected file
                File loadFrom = fileChooser.getSelectedFile();

                // attempt to load a stage plan from the specified file
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

    /**
     * save() Method:
     * Saves the current stage plan to file.
     *
     * Input: None.
     *
     * Process: Saves the stage plan if loaded file exists, prompts user to save as if not.
     *
     * Output: Whether the operation succeeded or not.
     * 
     * @return Whether the operation succeeded or not.
     */
    private boolean save() {

        // if loaded file doesn't exist, save as
        if (loadedFile == null || !loadedFile.isFile()) {

            return saveAs();

        } else {

            // try to save stage plan to loaded file
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

    /**
     * saveAs() Method:
     * Prompts the user to select a location at which the current stage plan will be saved.
     *
     * Input: None.
     *
     * Process: Prompts the user to select a target location and saves the stage plan to that file.
     *
     * Output: Whether the operation succeeded or not.
     *
     * @return Whether the operation succeeded or not.
     */
    private boolean saveAs() {

        // create file chooser & set filter
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(stagePlanFileFilter);

        // prompt user to select a target location
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {

            // get target location
            File saveTo = fileChooser.getSelectedFile();

            // try to save file to selected location
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

    /**
     * shutdown() Method:
     * Triggers a window closing event.
     *
     * Input: None.
     *
     * Process: Dispatches a window closing event to the current window.
     *
     * Output: None.
     */
    private void shutdown() {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    /**
     * windowClosing(WindowEvent) Method:
     * Checks if the stage plan has unsaved changes, and prompts the user to save if so.
     *
     * Input: Window event.
     *
     * Process: Checks if there are unsaved changes, asks the user if he/she wants to save, and saves if so.
     *
     * Output: None.
     *
     * @param e Window event.
     */
    @Override
    public void windowClosing(WindowEvent e) {

        // check if we have unsaved changes
        if (stagePlanner.hasUnsavedChanges()) {

            // show confirmation dialog
            int result = JOptionPane.showConfirmDialog(this, "There are unsaved changes. Would you like to save before exiting?", "Unsaved changes", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);

            // if the user wants to save and it fails or if he cancels, return
            if ((result == JOptionPane.YES_OPTION && !save()) || result == JOptionPane.CANCEL_OPTION)
                return;
        }

        // dispose of the window (close the program)
        dispose();
    }

    /**
     * windowClosed(WindowEvent) Method:
     * Saves fixtures list when window is being closed.
     *
     * Input: Window event.
     *
     * Process: Attempts to save light definitions to file.
     *
     * Output: None.
     *
     * @param e Window event.
     */
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
