package com.nicolasgnyra.stagelightplanner.components;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;

/**
 * JStageElementDefinition Class:
 * A generic, abstract drag-and-drop component to be used on a JStagePlanner instance.
 *
 * Date: 2016-09-27
 *
 * @author Nicolas Gnyra
 * @version 1.0
 */
abstract class JStageElementDefinition extends JComponent implements DragGestureListener, DragSourceListener {

    abstract Transferable getTransferable();

    /**
     * JStageElementDefinition() Constructor:
     * Creates a new instance of the JStageElementDefinition class.
     *
     * Input: None.
     *
     * Process: Initializes a new drag source on this component and sets the preferred size.
     *
     * Output: A new instance of the JStageElementDefinition class.
     */
    JStageElementDefinition() {

        // create drag source & create gesture recognizer
        DragSource dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, this);

        // set preferred size
        setPreferredSize(new Dimension(50, 50));

    }

    /**
     * dragGestureRecognized(DragGestureEvent) Method:
     * Fired when a drag gesture is recognized on this component.
     *
     * Input: Drag gesture event.
     *
     * Process: Creates a transferable and initializes the drag.
     *
     * Output: None.
     *
     * @param e Drag gesture event.
     */
    @Override
    public void dragGestureRecognized(DragGestureEvent e) {

        try {

            // get transferable
            Transferable transferable = getTransferable();

            // begin drag
            e.startDrag(DragSource.DefaultCopyNoDrop, transferable, this);

        } catch (InvalidDnDOperationException ex) {

            // print error
            ex.printStackTrace();

        }

    }

    /**
     * dragEnter(DragSourceDragEvent) Method:
     * Defines what cursor to show when entering a new drag target.
     *
     * Input: Drag source drag event.
     *
     * Process: Sets the cursor based on if the drag target is valid or not.
     *
     * Output: None.
     *
     * @param e Drag source drag event.
     */
    @Override
    public void dragEnter(DragSourceDragEvent e) {

        // get drag context
        DragSourceContext context = e.getDragSourceContext();

        // set cursor to drop if action is copy, no drop if not
        if ((e.getDropAction() & DnDConstants.ACTION_COPY) != 0)
            context.setCursor(DragSource.DefaultCopyDrop);
        else
            context.setCursor(DragSource.DefaultCopyNoDrop);

    }

    /**
     * dragExit(DragSourceEvent) Method:
     * Resets the cursor to the default cursor when exiting a drag target.
     *
     * Input: Drag source event.
     *
     * Process: Resets cursor.
     *
     * Output: None.
     *
     * @param e Drag source event.
     */
    @Override
    public void dragExit(DragSourceEvent e) {

        // reset cursor
        e.getDragSourceContext().setCursor(DragSource.DefaultCopyNoDrop);

    }

    @Override
    public void dragOver(DragSourceDragEvent e) { }

    @Override
    public void dropActionChanged(DragSourceDragEvent e) { }

    @Override
    public void dragDropEnd(DragSourceDropEvent e) { }
}
