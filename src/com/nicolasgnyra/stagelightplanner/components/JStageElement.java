package com.nicolasgnyra.stagelightplanner.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public abstract class JStageElement extends JComponent implements MouseListener, MouseMotionListener, KeyListener {
    protected boolean dragging = false;
    private Point relativeMouseLocation = new Point();
    protected JStagePlanner parent = new JStagePlanner(null);

    protected int width = 0;
    protected int height = 0;
    protected int x = 0;
    protected int y = 0;
    protected Color color;

    private boolean focus = false;
    private final JPopupMenu popupMenu;

    JStageElement(int x, int y, int width, int height, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;

        reposition();

        setFocusable(true);

        setBackground(color);

        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);

        setCursor(new Cursor(Cursor.HAND_CURSOR));

        popupMenu = new JPopupMenu();

        JMenuItem cloneItem = new JMenuItem("Clone");
        JMenuItem deleteItem = new JMenuItem("Delete");

        cloneItem.addActionListener(e -> addClone());

        deleteItem.addActionListener(e -> removeSelf());

        popupMenu.add(cloneItem);
        popupMenu.add(deleteItem);

    }

    protected void propertyUpdated() {
        parent.setHasUnsavedChanges(true);
        parent.repaint();
    }

    void setParent(JStagePlanner parent) {
        this.parent = parent;
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize((int) (Math.ceil(width * parent.getZoom() / (parent.getCellSize() * parent.getZoom())) * parent.getCellSize() * parent.getZoom()), (int) (Math.ceil(height * parent.getZoom() / (parent.getCellSize() * parent.getZoom())) * parent.getCellSize() * parent.getZoom()));
    }

    @Override
    public void setLocation(int x, int y) {
        double boundX = Math.max(0, Math.min(x * parent.getZoom(), parent.getDrawingPane().getWidth() - getWidth()));
        double boundY = Math.max(0, Math.min(y * parent.getZoom(), parent.getDrawingPane().getHeight() - getHeight()));
        super.setLocation((int) (Math.round(boundX / (parent.getCellSize() * parent.getZoom())) * parent.getCellSize() * parent.getZoom()), (int) (Math.round(boundY / (parent.getCellSize() * parent.getZoom())) * parent.getCellSize() * parent.getZoom()));
    }

    void reposition() {
        setSize(width, height);
        setLocation(x, y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        System.out.println("paintComponent " + getClass().getCanonicalName());

        super.paintComponent(g);

        paintElement(g);

        reposition();
    }

    void requestInnerFocus() {
        for (Component comp : getParent().getComponents()) {
            if (comp instanceof JStageElement) {
                JStageElement stageElement = (JStageElement) comp;
                stageElement.innerFocusLost(new FocusEvent(this, FocusEvent.FOCUS_LOST));
            }
        }

        innerFocusGained(new FocusEvent(this, FocusEvent.FOCUS_GAINED));
        requestFocusInWindow();

        parent.getPropertiesContainer().showProperties(this);
    }

    boolean isFocused() {
        return focus;
    }

    protected abstract void paintElement(Graphics g);

    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger())
            popupMenu.show(e.getComponent(), e.getX(), e.getY());

        setCursor(new Cursor(Cursor.MOVE_CURSOR));
        requestInnerFocus();
        dragging = true;
        relativeMouseLocation = e.getPoint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger())
            popupMenu.show(e.getComponent(), e.getX(), e.getY());

        setCursor(new Cursor(Cursor.HAND_CURSOR));
        dragging = false;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (dragging) {
            parent.setHasUnsavedChanges(true);

            JLayeredPane drawingPane = parent.getDrawingPane();
            Rectangle view = parent.getScroller().getViewport().getViewRect();
            Rectangle scrolledView = (Rectangle)view.clone();
            Point viewLocation = parent.getScroller().getViewport().getLocationOnScreen();

            int x = view.x + (e.getXOnScreen() - viewLocation.x - relativeMouseLocation.x);
            int y = view.y + (e.getYOnScreen() - viewLocation.y - relativeMouseLocation.y);

            if (e.getLocationOnScreen().x > viewLocation.x + view.width) {
                scrolledView.x += 10;
            } else if (e.getLocationOnScreen().x <= viewLocation.x) {
                scrolledView.x -= 10;
            }

            if (e.getLocationOnScreen().y > viewLocation.y + view.height) {
                scrolledView.y += 10;
            } else if (e.getLocationOnScreen().y <= viewLocation.y) {
                scrolledView.y -= 10;
            }

            this.x = (int) (x / parent.getZoom());
            this.y = (int) (y / parent.getZoom());

            drawingPane.scrollRectToVisible(scrolledView);

            reposition();

            parent.repaint();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }

    @Override
    public void mouseMoved(MouseEvent e) { }

    protected void innerFocusGained(FocusEvent e) {
        focus = true;
        repaint();
    }

    protected void innerFocusLost(FocusEvent e) {
        focus = false;
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DELETE)
            removeSelf();
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyReleased(KeyEvent e) { }

    protected void removeSelf() {
        parent.getDrawingPane().remove(this);
        parent.repaint();
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

    private void addClone() {
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
            parent.addFixture(new JLight(
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
}