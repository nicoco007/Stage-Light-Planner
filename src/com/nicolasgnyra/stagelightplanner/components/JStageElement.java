package com.nicolasgnyra.stagelightplanner.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * JStageElement Class:
 * An abstract draggable stage element.
 *
 * Date: 2016-09-27
 *
 * @author Nicolas Gnyra
 * @version 1.0
 */
public abstract class JStageElement extends JComponent implements MouseListener, MouseMotionListener, KeyListener {

    protected boolean dragging = false;                         // whether the component is currently being dragged
    private Point relativeMouseLocation = new Point();          // relative mouse position at start of drag
    protected JStagePlanner parent = new JStagePlanner(null);   // parent stage planner

    protected int width = 0;    // element width
    protected int height = 0;   // element height
    protected int x = 0;        // element x coord
    protected int y = 0;        // element y coord
    protected Color color;      // element color

    private boolean focus = false;      // whether the component is currently focused or not
    private final JPopupMenu popupMenu; // popup menu

    /**
     * paintElement(Graphics) Method:
     * Called to paint the contents of a JStageElement.
     *
     * Input: Graphics instance.
     *
     * Process: Depends on the extended class.
     *
     * Output: None.
     *
     * @param g Graphics instance.
     */
    protected abstract void paintElement(Graphics g);

    /**
     * JStageElement(int, int, int, int, Color) Constructor:
     * Creates a new instance of the JStageElement class.
     *
     * Input: Coordinates, size, color.
     *
     * Process: Sets values, adds listeners, creates popup menu.
     *
     * Output: New instance of the JStageElement class with the specified values.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param width Width
     * @param height Height
     * @param color Color
     */
    JStageElement(int x, int y, int width, int height, Color color) {

        // set values
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;

        // reposition component
        reposition();

        // define as focusable
        setFocusable(true);

        // set background to specified color
        setBackground(color);

        // add listeners
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);

        // set cursor to hand
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // create popup menu
        popupMenu = new JPopupMenu();

        // create items
        JMenuItem cloneItem = new JActionMenuItem("Clone", e -> addClone());
        JMenuItem deleteItem = new JActionMenuItem("Delete", e -> removeSelf());

        // add items to menu
        popupMenu.add(cloneItem);
        popupMenu.add(deleteItem);

    }

    /**
     * propertyUpdated() Method:
     * Triggered when a property gets updated (color, position, etc.)
     *
     * Input: None.
     *
     * Process: Sets unsaved changes to "true" and repaints the parent.
     *
     * Output: None.
     */
    protected void propertyUpdated() {
        parent.setHasUnsavedChanges(true);
        parent.repaint();
    }

    /**
     * setSize(int, int) Method:
     * Sets the size of the component and applies parent zoom.
     *
     * Input: Dimensions.
     *
     * Process: Multiplies size by zoom and rounds it off to the ceiling cell size.
     *
     * Output: None.
     *
     * @param width Width
     * @param height Height
     */
    @Override
    public void setSize(int width, int height) {
        super.setSize((int) (Math.ceil(width * parent.getZoom() / (parent.getCellSize() * parent.getZoom())) * parent.getCellSize() * parent.getZoom()), (int) (Math.ceil(height * parent.getZoom() / (parent.getCellSize() * parent.getZoom())) * parent.getCellSize() * parent.getZoom()));
    }

    /**
     * setLocation(int, int) Method:
     * Sets the location of the component, with zoom applied.
     *
     * Input: X/Y coordinates.
     *
     * Process: Restricts coordinates to bounds & applies zoom.
     *
     * Output: None.
     *
     * @param x X coordinate
     * @param y Y coordinate
     */
    @Override
    public void setLocation(int x, int y) {
        double boundX = Math.max(0, Math.min(x * parent.getZoom(), parent.getDrawingPane().getWidth() - getWidth()));
        double boundY = Math.max(0, Math.min(y * parent.getZoom(), parent.getDrawingPane().getHeight() - getHeight()));
        super.setLocation((int) (Math.round(boundX / (parent.getCellSize() * parent.getZoom())) * parent.getCellSize() * parent.getZoom()), (int) (Math.round(boundY / (parent.getCellSize() * parent.getZoom())) * parent.getCellSize() * parent.getZoom()));
    }

    /**
     * reposition() Method:
     * Trigger a resizing & repositioning of the component.
     *
     * Input: None.
     *
     * Process: Calls setSize() and setLocation().
     *
     * Output: None.
     */
    void reposition() {
        setSize(width, height);
        setLocation(x, y);
    }

    /**
     * paintComponent(Graphics) Method:
     * Paints the JStageElement.
     *
     * Input: Graphics instance.
     *
     * Process: Calls the superclass method, paints the component, and calls reposition().
     *
     * Output: None.
     *
     * @param g Graphics instance.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        paintElement(g);

        reposition();
    }

    /**
     * requestInnerFocus() Method:
     * Requests inner focus in the parent stage planner.
     *
     * Input: None.
     *
     * Process: Removes focus from all components and adds focus to this one.
     *
     * Output: None.
     */
    void requestInnerFocus() {

        // iterate through all components and remove focus if it's a JStageElement
        for (Component comp : getParent().getComponents()) {
            if (comp instanceof JStageElement) {
                JStageElement stageElement = (JStageElement) comp;
                stageElement.innerFocusLost(new FocusEvent(this, FocusEvent.FOCUS_LOST));
            }
        }

        // call innerFocusGained() and request focus in window
        innerFocusGained(new FocusEvent(this, FocusEvent.FOCUS_GAINED));
        requestFocusInWindow();

        // show properties in properties container
        parent.getPropertiesContainer().showProperties(this);

    }

    /**
     * mousePressed(MouseEvent) Method:
     * Called when the mouse is pressed on the component.
     *
     * Input: Mouse event.
     *
     * Process: Show popup if necessary, and start the dragging process.
     *
     * Output: None.
     *
     * @param e Mouse event.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger())
            popupMenu.show(e.getComponent(), e.getX(), e.getY());

        setCursor(new Cursor(Cursor.MOVE_CURSOR));
        requestInnerFocus();
        dragging = true;
        relativeMouseLocation = e.getPoint();
    }

    /**
     * mouseReleased(MouseEvent) Method:
     * Called when the mouse is released on the component.
     *
     * Input: Mouse event.
     *
     * Process: Show popup if necessary, and stop the dragging process.
     *
     * Output: None.
     *
     * @param e Mouse event.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger())
            popupMenu.show(e.getComponent(), e.getX(), e.getY());

        setCursor(new Cursor(Cursor.HAND_CURSOR));
        dragging = false;
    }

    /**
     * mouseDragged(MouseEvent) Method:
     * Fired when the mouse is being dragged on the component.
     *
     * Input: Mouse event.
     *
     * Process: Drags the component and moves the viewport if necessary.
     *
     * Output: None.
     *
     * @param e Mouse event.
     */
    @Override
    public void mouseDragged(MouseEvent e) {

        // check that we're dragging the component
        if (dragging) {

            // set unsaved changes to true
            parent.setHasUnsavedChanges(true);

            // get drawing pane, viewport, scrolled view, and view location on screen
            JLayeredPane drawingPane = parent.getDrawingPane();
            Rectangle view = parent.getScrollPane().getViewport().getViewRect();
            Rectangle scrolledView = (Rectangle)view.clone();
            Point viewLocation = parent.getScrollPane().getViewport().getLocationOnScreen();

            // get x and y coordinates
            int x = view.x + (e.getXOnScreen() - viewLocation.x - relativeMouseLocation.x);
            int y = view.y + (e.getYOnScreen() - viewLocation.y - relativeMouseLocation.y);

            // add or remove 10 px from scrolled view x if outside boundaries
            if (e.getLocationOnScreen().x > viewLocation.x + view.width)
                scrolledView.x += 10;
            else if (e.getLocationOnScreen().x <= viewLocation.x)
                scrolledView.x -= 10;

            // add or remove 10 px from scrolled view y if outside boundaries
            if (e.getLocationOnScreen().y > viewLocation.y + view.height)
                scrolledView.y += 10;
            else if (e.getLocationOnScreen().y <= viewLocation.y)
                scrolledView.y -= 10;

            // set stage element's position
            this.x = (int) (x / parent.getZoom());
            this.y = (int) (y / parent.getZoom());

            // scroll drawing pane
            drawingPane.scrollRectToVisible(scrolledView);

            // reposition the stage element
            reposition();

            // repaint parent
            parent.repaint();

        }

    }

    /**
     * innerFocusGained(FocusEvent) Method:
     * Called when inner focus is gained.
     *
     * Input: Focus event.
     *
     * Process: Sets focus to true and repaints.
     *
     * Output: None.
     *
     * @param e Focus event.
     */
    protected void innerFocusGained(FocusEvent e) {
        focus = true;
        repaint();
    }

    /**
     * innerFocusLost(FocusEvent) Method:
     * Called when inner focus is lost.
     *
     * Input: Focus event.
     *
     * Process: Sets focus to false and repaints.
     *
     * Output: None.
     *
     * @param e Focus event.
     */
    protected void innerFocusLost(FocusEvent e) {
        focus = false;
        repaint();
    }

    /**
     * keyPressed(KeyEvent) Method:
     * Fired when a key is pressed while the component is focused.
     *
     * Input: Key event.
     *
     * Process: If the pressed key is delete, removes self from parent.
     *
     * Output: None.
     *
     * @param e Key event.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DELETE)
            removeSelf();
    }

    /**
     * removeSelf() Method:
     * Removes this component from parent.
     *
     * Input: None.
     *
     * Process: Removes this component from the parent & repaints it.
     *
     * Output: None.
     */
    protected void removeSelf() {
        getParent().remove(this);
        parent.repaint();
    }

    /**
     * addClone() Method:
     * Creates and adds a clone of this component to the parent.
     *
     * Input: None.
     *
     * Process: Clones this component & adds it to the parent.
     *
     * Output: None.
     */
    private void addClone() {

        // clone the JStageElement based on its child class and move 2 cells down and to the right
        if (this instanceof JBatten) {

            JBatten batten = (JBatten) this;
            parent.addBatten(new JBatten(
                    batten.getGridX() + parent.getCellSize() * 2,
                    batten.getGridY() + parent.getCellSize() * 2,
                    batten.getLength(),
                    batten.getOrientation(),
                    batten.getHeightFromFloor()
            ));

        } else if (this instanceof JLight) {

            JLight light = (JLight) this;
            parent.addLight(new JLight(
                    light.getGridX() + parent.getCellSize() * 2,
                    light.getGridY() + parent.getCellSize() * 2,
                    light.getModel().clone(),
                    light.getBeamColor(),
                    light.getRotation(),
                    light.getAngle(),
                    light.getFieldAngle(),
                    light.getConnectionId(),
                    light.getBeamIntensity()
            ));

        } else if (this instanceof JDraggableLabel) {

            JDraggableLabel label = (JDraggableLabel) this;
            parent.addLabel(new JDraggableLabel(
                    label.getGridX() + parent.getCellSize() * 2,
                    label.getGridY() + parent.getCellSize() * 2,
                    label.getText(),
                    label.getColor(),
                    label.getFontSize(),
                    label.getFontFamily()
            ));

        }

    }

    void setParent(JStagePlanner parent) {
        this.parent = parent;
    }

    public int getGridX() {
        return x;
    }

    public int getGridY() {
        return y;
    }

    public Color getColor() {
        return color;
    }

    boolean hasInnerFocus() {
        return focus;
    }

    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }

    @Override
    public void mouseMoved(MouseEvent e) { }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyReleased(KeyEvent e) { }
}