package com.nicolasgnyra.stagelightplanner.components;

import com.nicolasgnyra.stagelightplanner.LightDefinition;
import com.nicolasgnyra.stagelightplanner.Orientation;
import com.nicolasgnyra.stagelightplanner.StagePlan;
import com.nicolasgnyra.stagelightplanner.helpers.PaintHelper;
import com.nicolasgnyra.stagelightplanner.transferables.StageElementTransferable;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JStagePlanner Class:
 * The main planner panel that contains all stage elements.
 *
 * Date: 2016-09-27
 *
 * @author Nicolas Gnyra
 * @version 1.0
 */
public class JStagePlanner extends JPanel implements MouseListener, MouseMotionListener, DropTargetListener {

    private static final int cellSize = 10;             // cell size, in px
    private static final int largeCellMultiplier = 10;  // large cell size, multiplier of cellSize

    private JScrollPane scrollPane;     // drawing pane scroll pane
    private JLayeredPane drawingPane;   // graphics container
    private Point dragOrigin = null;    // drag origin (for scroll pane)

    private int acceptableDnDActions = DnDConstants.ACTION_COPY;    // acceptable drag and drop actions
    private JPropertiesContainer propertiesContainer;               // properties container

    private float zoom = 1.0f;              // current zoom
    private final float minZoom = 0.25f;    // minimum zoom
    private final float maxZoom = 5.0f;     // maximum zoom

    private boolean showLightOutlines = true;   // show dotted lines and outlines on plan

    private boolean hasUnsavedChanges = false;  // whether we have unsaved changes or not

    /**
     * JStagePlanner(JPropertiesContainer) Constructor:
     * Creates a new instance of the JStagePlanner class with the specified JPropertiesContainer instance.
     *
     * Input: JPropertiesContainer instance to be used.
     *
     * Process: Creates all necessary components & draws background.
     *
     * Output: A new instance of the JStagePlanner class.
     *
     * @param propertiesContainer JPropertiesContainer instance to be used.
     */
    public JStagePlanner(JPropertiesContainer propertiesContainer) {

        // initialize with new border layout
        super(new BorderLayout());

        // create drawing pane
        drawingPane = new DrawingPane(1600, 1200);
        drawingPane.setBackground(Color.white);
        drawingPane.addMouseListener(this);
        drawingPane.addMouseMotionListener(this);

        // create scroll pane
        scrollPane = new JScrollPane(drawingPane, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        // create bottom toolbar & set layout
        JPanel bottomToolbar = new JPanel();
        bottomToolbar.setLayout(new BoxLayout(bottomToolbar, BoxLayout.X_AXIS));

        // create check box for show outlines & add listener
        JCheckBox showOutlinesCheckBox = new JCheckBox("Show beam outlines", showLightOutlines);
        showOutlinesCheckBox.addItemListener((e) -> {

            // if checked, show outlines & repaint
            showLightOutlines = e.getStateChange() == ItemEvent.SELECTED;
            repaint();

        });

        // set predefined zoom choices
        String[] zoomChoices = {
                "25%",
                "50%",
                "100%",
                "200%",
                "300%",
                "400%"
        };

        // create combobox, allow it to be edited, and add an action listener
        final JComboBox<String> zoomComboBox = new JComboBox<>(zoomChoices);
        zoomComboBox.setEditable(true);
        zoomComboBox.addActionListener((e) -> {

            // get the selected item
            String selection = (String)zoomComboBox.getSelectedItem();

            // create pattern to check that input is valid
            Pattern pattern = Pattern.compile("^([0-9.]+)%?$");
            Matcher matcher = pattern.matcher(selection);

            // check if string matches
            if (matcher.matches()) {

                // set zoom to parsed float
                zoom = Math.max(minZoom, Math.min(Float.parseFloat(matcher.group(1)) / 100f, maxZoom));

                // reposition all stage elements
                getStageElements().forEach(JStageElement::reposition);

                // repaint
                repaint();

            }

            // set selected item with '%'
            zoomComboBox.setSelectedItem(Math.round(zoom * 100) + "%");
        });

        // set selected item to zoom
        zoomComboBox.setSelectedItem(zoom * 100 + "%");

        // add elements to bottom toolbar
        bottomToolbar.add(showOutlinesCheckBox);
        bottomToolbar.add(Box.createHorizontalGlue());
        bottomToolbar.add(zoomComboBox);

        // add elements to main container
        add(bottomToolbar, BorderLayout.PAGE_END);
        add(scrollPane, BorderLayout.CENTER);

        // define drop target
        new DropTarget(drawingPane, acceptableDnDActions, this, true);

        // set properties container
        this.propertiesContainer = propertiesContainer;

    }

    /**
     * addStageElement(JStageElement) Method:
     * Adds a JStageElement instance to the drawing pane.
     *
     * Input: JStageElement to add.
     *
     * Process: Set unsaved changes to true, set stage element parent, add to drawing pane, request inner focus, repaint, reposition
     *
     * Output: None.
     *
     * @param stageElement JStageElement to add.
     */
    private void addStageElement(JStageElement stageElement) {

        // set unsaved changes to true
        setHasUnsavedChanges(true);

        // set parent
        stageElement.setParent(this);

        // add to drawing pane
        drawingPane.add(stageElement);

        // request focus, reposition, repaint
        stageElement.requestInnerFocus();
        stageElement.reposition();

        // repaint this
        repaint();

    }

    /**
     * addBatten(JBatten) Method:
     * Add a JBatten to the drawing pane.
     *
     * Input: JBatten to add.
     *
     * Process: Adds the JBatten & sets the layer.
     *
     * Output: None.
     *
     * @param batten JBatten to add.
     */
    void addBatten(JBatten batten) {
        addStageElement(batten);
        drawingPane.setLayer(batten, DrawingPane.BATTEN_LAYER);
    }

    /**
     * addLight(JLight) Method:
     * Add a JLight to the drawing pane.
     *
     * Input: JLight to add.
     *
     * Process: Adds the JLight & sets the layer.
     *
     * Output: None.
     *
     * @param light JLight to add.
     */
    void addLight(JLight light) {
        addStageElement(light);
        drawingPane.setLayer(light, DrawingPane.FIXTURE_LAYER);
    }

    /**
     * addLabel(JDraggableLabel) Method:
     * Add a JDraggableLabel to the drawing pane.
     *
     * Input: JDraggableLabel to add.
     *
     * Process: Adds the JDraggableLabel & sets the layer.
     *
     * Output: None.
     *
     * @param label JDraggableLabel to add.
     */
    void addLabel(JDraggableLabel label) {
        addStageElement(label);
        drawingPane.setLayer(label, DrawingPane.LABEL_LAYER);
    }

    /**
     * mousePressed(MouseEvent) Method:
     * Sets the cursor & begins dragging.
     *
     * Input: Mouse event.
     *
     * Process: Sets the cursor & begins dragging.
     *
     * Output: None.
     *
     * @param e Mouse event.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        setCursor(new Cursor(Cursor.MOVE_CURSOR));
        dragOrigin = e.getPoint();
    }

    /**
     * mouseReleased(MouseEvent) Method:
     * Sets the cursor & stops dragging.
     *
     * Input: Mouse event.
     *
     * Process: Sets the cursor & stops dragging.
     *
     * Output: None.
     *
     * @param e Mouse event.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        dragOrigin = null;
    }

    /**
     * mouseDragged(MouseEvent) Method:
     * Occurs when the mouse is dragged over the component.
     *
     * Input: Mouse event.
     *
     * Process: Gets the delta X and Y values and adds them to the viewport location.
     *
     * Output: None.
     *
     * @param e Mouse event.
     */
    @Override
    public void mouseDragged(MouseEvent e) {

        // dispatch event to children (this prevents mouse-going-too-fast-for-child-to-keep-track issues)
        for (Component comp : getComponents())
            comp.dispatchEvent(e);

        // check if we're dragging
        if (dragOrigin != null) {

            // get delta values
            int dx = dragOrigin.x - e.getX();
            int dy = dragOrigin.y - e.getY();

            // get view rectangle and add delta
            Rectangle view = scrollPane.getViewport().getViewRect();
            view.x += dx;
            view.y += dy;

            // scroll to new view
            drawingPane.scrollRectToVisible(view);

        }

    }

    /**
     * mouseMoved(MouseEvent) Method:
     * Fired when the mouse moves above the component.
     *
     * Input: Mouse event.
     *
     * Process: Dispatches event to children.
     *
     * Output: None.
     *
     * @param e Mouse event.
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        for (Component comp : getComponents())
            comp.dispatchEvent(e);
    }

    /**
     * dragEnter(DropTargetDragEvent) Method:
     * Fired when a drag element enters this component.
     *
     * Input: Drop target drag event.
     *
     * Process: Checks if the drag is valid & accepts it if so.
     *
     * Output: None.
     *
     * @param e Drop target drag event.
     */
    @Override
    public void dragEnter(DropTargetDragEvent e) {
        if (!isAcceptableDrag(e)) {
            e.rejectDrag();
            return;
        }

        e.acceptDrag(acceptableDnDActions);
    }

    /**
     * dragOver(DropTargetDragEvent) Method:
     * Fired when a drag element is moved over this component.
     *
     * Input: Drop target drag event.
     *
     * Process: Checks if the drag is valid & accepts it if so.
     *
     * Output: None.
     *
     * @param e Drop target drag event.
     */
    @Override
    public void dragOver(DropTargetDragEvent e) {
        if (!isAcceptableDrag(e)) {
            e.rejectDrag();
            return;
        }

        e.acceptDrag(acceptableDnDActions);
    }

    /**
     * dragOver(DropTargetDragEvent) Method:
     * Fired when a drag element is on top of this component and changes drop mode.
     *
     * Input: Drop target drag event.
     *
     * Process: Checks if the drag is valid & accepts it if so.
     *
     * Output: None.
     *
     * @param e Drop target drag event.
     */
    @Override
    public void dropActionChanged(DropTargetDragEvent e) {
        if (!isAcceptableDrag(e)) {
            e.rejectDrag();
            return;
        }

        e.acceptDrag(acceptableDnDActions);
    }

    /**
     * drop(DropTargetDropEvent) Method:
     * Occurs when something is dropped on this component.
     *
     * Input: Drop target drop event.
     *
     * Process: Checks if the drag is acceptable, and adds the corresponding component if so.
     *
     * Output: None.
     *
     * @param e Drop target drop event.
     */
    @Override
    public void drop(DropTargetDropEvent e) {

        // check if the data flavor is supported; if not, reject the drop
        if (!e.isDataFlavorSupported(StageElementTransferable.getObjectFlavor()) || (e.getSourceActions() & acceptableDnDActions) == 0) {
            e.rejectDrop();
            return;
        }

        // wrap in try/catch
        try {

            // accept the drop
            e.acceptDrop(acceptableDnDActions);

            // get the drag location
            Point dragLocation = e.getLocation();

            // get data
            Object data = e.getTransferable().getTransferData(StageElementTransferable.getObjectFlavor());

            // get coordinates
            int x = (int)(dragLocation.x / zoom);
            int y = (int)(dragLocation.y / zoom);

            // add element based on data class
            if (data instanceof LightDefinition)
                addLight(new JLight(x, y, ((LightDefinition)data).clone()));
            else if (data instanceof String)
                addLabel(new JDraggableLabel(x, y, "Text"));
            else
                addBatten(new JBatten(x, y, 500, Orientation.HORIZONTAL, 400));

            // say drop is complete
            e.dropComplete(true);
        } catch (UnsupportedFlavorException | IOException ex) {

            // print stack trace and say drop has failed
            ex.printStackTrace();
            e.dropComplete(false);

        }
    }

    /**
     * isAcceptableDrag(DropTargetDragEvent) Method:
     * Checks if a drag is acceptable or not.
     *
     * Input: Drop target drag event.
     *
     * Process: Checks if the data flavor & the source action are acceptable.
     *
     * Output: Whether the above check succeeded or failed.
     *
     * @param e Drop target drag event.
     * @return Whether the data flavor & the source action are acceptable or not.
     */
    private boolean isAcceptableDrag(DropTargetDragEvent e) {
        return e.isDataFlavorSupported(StageElementTransferable.getObjectFlavor()) && (e.getSourceActions() & acceptableDnDActions) != 0;
    }

    /**
     * DrawingPane Class:
     * A JLayeredPane that contains all the JStageElements.
     *
     * Date: 2016-09-27
     *
     * @author Nicolas Gnyra
     * @version 1.0
     */
    private class DrawingPane extends JLayeredPane {

        // self explanatory
        static final int BATTEN_LAYER = 0;
        static final int FIXTURE_LAYER = 1;
        static final int LABEL_LAYER = 2;

        private Dimension size;     // size without zoom

        /**
         * DrawingPane(width, height) Constructor:
         * Creates a new instance of the DrawingPane class with the specified dimensions.
         *
         * Input: Dimensions.
         *
         * Process: Sets the layout & preferred size.
         *
         * @param width Width
         * @param height Height
         */
        private DrawingPane(int width, int height) {

            // call super constructor
            super();

            // disable layout manager
            setLayout(null);

            // define size
            size = new Dimension(width, height);

        }

        /**
         * getPreferredSize() Method:
         * Returns the preferred size of the drawing pane, with zoom applied.
         *
         * Input: None.
         *
         * Process: Multiplies preferred size by zoom.
         *
         * Output: Preferred size.
         *
         * @return Preferred size.
         */
        @Override
        public Dimension getPreferredSize() {

            return new Dimension(Math.round(size.width * zoom), Math.round(size.height * zoom));

        }

        /**
         * paintComponent(Graphics) Method:
         * Paints the drawing pane & the light beams.
         *
         * Input: Graphics instance.
         *
         * Process: Draws cells & light beams if the light is on top of a batten.
         *
         * Output: None.
         *
         * @param g Graphics instance.
         */
        @Override
        protected void paintComponent(Graphics g) {

            // check zoom
            if (zoom < minZoom || zoom > maxZoom) {
                System.out.println("Zoom out of range!");
                return;
            }

            // call super method
            super.paintComponent(g);

            // set color to light gray
            g.setColor(Color.lightGray);

            // draw small cells only if zoom is >= 50% (lags too much otherwise)
            if (zoom >= 0.5f)
                for (int i = 0; i <= getWidth(); i += (int)(cellSize * zoom))
                    for (int j = 0; j <= getHeight(); j += (int)(cellSize * zoom))
                        g.drawRect(i, j, i + (int)(cellSize * zoom), j + (int)(cellSize * zoom));

            // set color to darker gray
            g.setColor(Color.gray);

            // draw large cells
            for (int i = 0; i <= getWidth(); i += (int)(cellSize * zoom) * largeCellMultiplier)
                for (int j = 0; j <= getHeight(); j += (int)(cellSize * zoom) * largeCellMultiplier)
                    g.drawRect(i, j, i + (int)(cellSize * zoom) * largeCellMultiplier, j + (int)(cellSize * zoom) * largeCellMultiplier);

            // iterate through stage elements
            getLights().forEach(light -> {

                // get batten on top of which the light currently is
                JBatten overlappingBatten = light.getOverlappingBatten();

                // if the batten was found
                if (overlappingBatten != null) {

                    PaintHelper.drawBeam((Graphics2D) g, light.getX(), light.getY(), light.getWidth(), light.getHeight(), light.getFieldAngle(), (int) (overlappingBatten.getHeightFromFloor() * getZoom()), light.getBeamColor(), light.getBeamIntensity(), light.getRotation(), light.getAngle(), showLightOutlines);

                }

            });

            // revalidate to update scroll bars
            revalidate();

        }
    }

    /**
     * getStageElements() Method:
     * Retrieves all JStageElement components from the drawing pane.
     *
     * Input: None.
     *
     * Process: Iterates through all components and fins the JStageElement instances.
     *
     * Output: List of JStageElements contained in the drawing pane.
     *
     * @return List of JStageElements contained in the drawing pane.
     */
    private ArrayList<JStageElement> getStageElements() {
        ArrayList<JStageElement> stageElements = new ArrayList<>();

        for (Component comp : drawingPane.getComponents())
            if (comp instanceof JStageElement)
                stageElements.add((JStageElement) comp);

        return stageElements;
    }

    /**
     * getBattens() Method:
     * Retrieves all JBatten components from the drawing pane.
     *
     * Input: None.
     *
     * Process: Iterates through all components and fins the JBatten instances.
     *
     * Output: List of JBattens contained in the drawing pane.
     *
     * @return List of JBattens contained in the drawing pane.
     */
    ArrayList<JBatten> getBattens() {
        ArrayList<JBatten> battens = new ArrayList<>();

        for (Component comp : drawingPane.getComponents())
            if (comp instanceof JBatten)
                battens.add((JBatten) comp);

        return battens;
    }

    /**
     * getLights() Method:
     * Retrieves all JLight components from the drawing pane.
     *
     * Input: None.
     *
     * Process: Iterates through all components and fins the JLight instances.
     *
     * Output: List of JLights contained in the drawing pane.
     *
     * @return List of JLights contained in the drawing pane.
     */
    ArrayList<JLight> getLights() {
        ArrayList<JLight> lights = new ArrayList<>();

        for (Component comp : drawingPane.getComponents())
            if (comp instanceof JLight)
                lights.add((JLight) comp);

        return lights;
    }

    /**
     * setStagePlan(StagePlan) Method:
     * Adds all stage plan components to the drawing pane.
     *
     * Input: Stage plan.
     *
     * Process: Iterates through stage plan elements and adds them to the drawing pane.
     *
     * Output: None.
     *
     * @param plan Stage plan.
     */
    public void setStagePlan(StagePlan plan) {
        for (JStageElement stageElement : plan.getStageElements()) {
            if (stageElement instanceof JBatten) {
                addBatten((JBatten) stageElement);
            } else if (stageElement instanceof JLight) {
                addLight((JLight) stageElement);
            } else if (stageElement instanceof JDraggableLabel) {
                addLabel((JDraggableLabel) stageElement);
            } else {
                System.out.println("Unknown stage element: " + stageElement.getClass());
            }
        }
    }

    public void setHasUnsavedChanges(boolean hasUnsavedChanges) {
        this.hasUnsavedChanges = hasUnsavedChanges;
    }

    public boolean hasUnsavedChanges() {
        return hasUnsavedChanges;
    }

    int getCellSize() {
        return cellSize;
    }

    JScrollPane getScrollPane() {
        return scrollPane;
    }

    public JLayeredPane getDrawingPane() {
        return drawingPane;
    }

    float getZoom() {
        return zoom;
    }

    public StagePlan getStagePlan() {
        return new StagePlan(getStageElements());
    }

    JPropertiesContainer getPropertiesContainer() {
        return propertiesContainer;
    }

    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }

    @Override
    public void dragExit(DropTargetEvent e) { }
}
