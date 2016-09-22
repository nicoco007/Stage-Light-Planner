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

        setSize(width, height);
        setLocation(x, y);

        setFocusable(true);

        setBackground(color);

        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);

        setCursor(new Cursor(Cursor.HAND_CURSOR));

        popupMenu = new JPopupMenu();

        JMenuItem deleteItem = new JMenuItem("Delete");

        deleteItem.addActionListener((e) -> removeSelf());

        popupMenu.add(deleteItem);

    }

    public void setParent(JStagePlanner parent) {
        this.parent = parent;
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(Math.round(width * parent.getZoom()), Math.round(height * parent.getZoom()));
    }

    @Override
    public void setLocation(int x, int y) {
        int boundX = Math.max(0, Math.min(x, parent.getDrawingPane().getWidth() - getWidth()));
        int boundY = Math.max(0, Math.min(y, parent.getDrawingPane().getHeight() - getHeight()));
        super.setLocation((int)((boundX / parent.getCellSize()) * parent.getCellSize() * parent.getZoom()), (int)(boundY / parent.getCellSize() * parent.getCellSize() * parent.getZoom()));
    }

    void reposition() {
        setSize(width, height);
        setLocation(x, y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        reposition();

        super.paintComponent(g);

        paintElement(g);
    }

    public void requestInnerFocus() {
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

            x = Math.round(Math.max(0, Math.min(x, drawingPane.getWidth() - getWidth())));
            y = Math.round(Math.max(0, Math.min(y, drawingPane.getHeight() - getHeight())));

            this.x = Math.round(x / parent.getZoom());
            this.y = Math.round(y / parent.getZoom());

            repaint();

            drawingPane.scrollRectToVisible(scrolledView);
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

    boolean hasInnerFocus() {
        return focus;
    }
}