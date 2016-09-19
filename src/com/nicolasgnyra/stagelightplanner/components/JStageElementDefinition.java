package com.nicolasgnyra.stagelightplanner.components;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;

public abstract class JStageElementDefinition extends JComponent implements DragGestureListener, DragSourceListener {
    public JStageElementDefinition() {
        DragSource dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, this);

        setPreferredSize(new Dimension(50, 50));
    }

    @Override
    public void dragGestureRecognized(DragGestureEvent e) {
        try {
            Transferable transferable = getTransferable();

            e.startDrag(DragSource.DefaultCopyNoDrop, transferable, this);
        } catch (InvalidDnDOperationException ex) {
            System.out.println(ex.getMessage());
        }
    }

    abstract Transferable getTransferable();

    @Override
    public void dragEnter(DragSourceDragEvent e) {
        DragSourceContext context = e.getDragSourceContext();

        if ((e.getDropAction() & DnDConstants.ACTION_COPY) != 0)
            context.setCursor(DragSource.DefaultCopyDrop);
        else
            context.setCursor(DragSource.DefaultCopyNoDrop);
    }

    @Override
    public void dragOver(DragSourceDragEvent e) { }

    @Override
    public void dropActionChanged(DragSourceDragEvent e) { }

    @Override
    public void dragExit(DragSourceEvent e) {

        // reset cursor
        e.getDragSourceContext().setCursor(DragSource.DefaultCopyNoDrop);

    }

    @Override
    public void dragDropEnd(DragSourceDropEvent e) { }
}
