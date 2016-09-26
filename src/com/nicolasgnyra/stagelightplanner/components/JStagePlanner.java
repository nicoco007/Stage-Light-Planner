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
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JStagePlanner extends JPanel implements MouseListener, MouseMotionListener, DropTargetListener {
    private static final int cellSize = 10;
    private static final int largeCellMultiplier = 10;
    private static final int cmPerCell = 20;
    private static final double pxPerCm = (double) cellSize / cmPerCell;

    private JScrollPane scroller;
    private JLayeredPane drawingPane;       // graphics container
    private Point dragOrigin = null;

    private int acceptableDnDActions = DnDConstants.ACTION_COPY;
    private JPropertiesContainer propertiesContainer;

    private float zoom = 1.0f;
    private final float minZoom = 0.25f;
    private final float maxZoom = 5.0f;
    private float paintedZoom;

    private boolean showLightOutlines = true;

    private boolean hasUnsavedChanges = false;

    public JStagePlanner(JPropertiesContainer propertiesContainer) {
        super(new BorderLayout());

        setOpaque(true);

        drawingPane = new DrawingPane(1600, 1200);
        drawingPane.setBackground(Color.white);
        drawingPane.addMouseListener(this);
        drawingPane.addMouseMotionListener(this);

        scroller = new JScrollPane(drawingPane, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        JPanel bottomToolbar = new JPanel();
        bottomToolbar.setLayout(new BoxLayout(bottomToolbar, BoxLayout.X_AXIS));

        JCheckBox showOutlinesCheckBox = new JCheckBox("Show beam outlines", showLightOutlines);
        showOutlinesCheckBox.addItemListener((e) -> {
            showLightOutlines = e.getStateChange() == ItemEvent.SELECTED;
            repaint();
        });

        bottomToolbar.add(showOutlinesCheckBox);

        bottomToolbar.add(Box.createHorizontalGlue());

        String[] zoomChoices = {
                "25%",
                "50%",
                "100%",
                "200%",
                "300%",
                "400%"
        };

        final JComboBox<String> zoomComboBox = new JComboBox<>(zoomChoices);
        zoomComboBox.setEditable(true);
        zoomComboBox.addActionListener((e) -> {
            String selection = (String)zoomComboBox.getSelectedItem();

            Pattern pattern = Pattern.compile("^([0-9.]+)%?$");
            Matcher matcher = pattern.matcher(selection);

            if (matcher.matches()) {
                zoom = Math.max(minZoom, Math.min(Float.parseFloat(matcher.group(1)) / 100f, maxZoom));
                getStageElements().forEach(JStageElement::reposition);
                repaint();
            }

            zoomComboBox.setSelectedItem(Math.round(zoom * 100) + "%");
        });

        zoomComboBox.setSelectedItem(zoom * 100 + "%");

        bottomToolbar.add(zoomComboBox);

        add(bottomToolbar, BorderLayout.PAGE_END);
        add(scroller, BorderLayout.CENTER);

        new DropTarget(drawingPane, acceptableDnDActions, this, true);

        this.propertiesContainer = propertiesContainer;
    }

    void addStageElement(JStageElement stageElement) {
        setHasUnsavedChanges(true);
        stageElement.setParent(this);
        drawingPane.add(stageElement);
        stageElement.requestInnerFocus();
        stageElement.repaint();
        stageElement.reposition();
        repaint();
    }

    void addBatten(JBatten batten) {
        addStageElement(batten);
        drawingPane.setLayer(batten, DrawingPane.BATTEN_LAYER);
    }

    void addFixture(JFixture fixture) {
        addStageElement(fixture);
        drawingPane.setLayer(fixture, DrawingPane.FIXTURE_LAYER);
    }

    void addLabel(JDraggableLabel label) {
        addStageElement(label);
        drawingPane.setLayer(label, DrawingPane.LABEL_LAYER);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        setCursor(new Cursor(Cursor.MOVE_CURSOR));
        dragOrigin = e.getPoint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        dragOrigin = null;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        for (Component comp : getComponents())
            comp.dispatchEvent(e);

        if (dragOrigin != null) {
            int dx = dragOrigin.x - e.getX();
            int dy = dragOrigin.y - e.getY();

            Rectangle view = scroller.getViewport().getViewRect();
            view.x += dx;
            view.y += dy;

            drawingPane.scrollRectToVisible(view);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        for (Component comp : getComponents())
            comp.dispatchEvent(e);
    }

    int getCellSize() {
        return Math.round(cellSize);
    }

    JScrollPane getScroller() {
        return scroller;
    }

    public JLayeredPane getDrawingPane() {
        return drawingPane;
    }

    float getZoom() {
        return zoom;
    }

    @Override
    public void dragEnter(DropTargetDragEvent e) {
        if (!isAcceptableDrag(e)) {
            e.rejectDrag();
            return;
        }

        e.acceptDrag(acceptableDnDActions);
    }

    @Override
    public void dragOver(DropTargetDragEvent e) {
        if (!isAcceptableDrag(e)) {
            e.rejectDrag();
            return;
        }

        e.acceptDrag(acceptableDnDActions);
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent e) {
        if (!isAcceptableDrag(e)) {
            e.rejectDrag();
            return;
        }

        e.acceptDrag(acceptableDnDActions);
    }

    @Override
    public void dragExit(DropTargetEvent e) { }

    @Override
    public void drop(DropTargetDropEvent e) {
        if (!e.isDataFlavorSupported(StageElementTransferable.getObjectFlavor()) || (e.getSourceActions() & acceptableDnDActions) == 0) {
            e.rejectDrop();
            return;
        }

        try {
            e.acceptDrop(acceptableDnDActions);

            Point dragLocation = e.getLocation();

            Object data = e.getTransferable().getTransferData(StageElementTransferable.getObjectFlavor());

            int x = (int)(dragLocation.x / zoom);
            int y = (int)(dragLocation.y / zoom);

            if (data instanceof LightDefinition) {
                addFixture(new JLight(x, y, ((LightDefinition)data).clone()));
            } else if (data instanceof String) {
                addLabel(new JDraggableLabel(x, y, "Text"));
            } else {
                addBatten(new JBatten(x, y, 500, Orientation.HORIZONTAL, 400));
            }

            e.dropComplete(true);
        } catch (UnsupportedFlavorException | IOException ex) {
            System.out.println(ex.getMessage());
            e.dropComplete(false);
        }
    }

    private boolean isAcceptableDrag(DropTargetDragEvent e) {
        return e.isDataFlavorSupported(StageElementTransferable.getObjectFlavor()) && (e.getSourceActions() & acceptableDnDActions) != 0;
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

    private class DrawingPane extends JLayeredPane {
        static final int BATTEN_LAYER = 0;
        static final int FIXTURE_LAYER = 1;
        static final int LABEL_LAYER = 2;

        private Dimension size;     // size without zoom

        private DrawingPane(int width, int height) {

            // call super constructor
            super();

            // disable layout manager
            setLayout(null);

            // define size
            size = new Dimension(width, height);

            // set size
            setPreferredSize(size);

        }

        @Override
        public void setPreferredSize(Dimension preferredSize) {

            // set size based on zoom
            super.setPreferredSize(new Dimension(Math.round(preferredSize.width * zoom), Math.round(preferredSize.height * zoom)));

        }

        @Override
        protected void paintComponent(Graphics g) {
            System.out.println("paintComponent " + getClass().getCanonicalName());

            // check zoom
            if (zoom < minZoom || zoom > maxZoom) {
                System.out.println("Zoom out of range!");
                return;
            }

            // set size
            setPreferredSize(size);

            // call super method
            super.paintComponent(g);

            // set color to light gray
            g.setColor(Color.lightGray);

            // draw small cells
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

            getStageElements().forEach(stageElement -> {
                if (stageElement instanceof JLight) {

                    // cast fixture to light
                    JLight light = (JLight) stageElement;

                    // get batten on top of which the light currently is
                    JBatten overlappingBatten = light.getOverlappingBatten();

                    // if the batten was found
                    if (overlappingBatten != null) {

                        PaintHelper.drawBeam((Graphics2D) g, light.getX(), light.getY(), light.getWidth(), light.getHeight(), light.getFieldAngle(), (int) (overlappingBatten.getHeightFromFloor() * getZoom()), light.getBeamColor(), light.getBeamIntensity(), light.getRotation(), light.getAngle(), showLightOutlines);

                    }

                }

            });

            // revalidate to update scrollbars
            revalidate();

        }
    }

    private ArrayList<JStageElement> getStageElements() {
        ArrayList<JStageElement> stageElements = new ArrayList<>();

        for (Component comp : drawingPane.getComponents())
            if (comp instanceof JStageElement)
                stageElements.add((JStageElement) comp);

        return stageElements;
    }

    private ArrayList<JFixture> getFixtures() {
        ArrayList<JFixture> fixtures = new ArrayList<>();

        for (Component comp : drawingPane.getComponents())
            if (comp instanceof JFixture)
                fixtures.add((JFixture)comp);

        return fixtures;
    }

    ArrayList<JBatten> getBattens() {
        ArrayList<JBatten> battens = new ArrayList<>();

        for (Component comp : drawingPane.getComponents())
            if (comp instanceof JBatten)
                battens.add((JBatten) comp);

        return battens;
    }

    public StagePlan getStagePlan() {
        return new StagePlan(getStageElements());
    }

    public void setStagePlan(StagePlan plan) {
        for (JStageElement stageElement : plan.getStageElements()) {
            if (stageElement instanceof JBatten) {
                addBatten((JBatten)stageElement);
            } else if (stageElement instanceof JFixture) {
                addFixture((JFixture)stageElement);
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
}
