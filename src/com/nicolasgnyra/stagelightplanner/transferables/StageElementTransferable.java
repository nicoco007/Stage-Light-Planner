package com.nicolasgnyra.stagelightplanner.transferables;

import com.nicolasgnyra.stagelightplanner.components.JLightDefinition;

import java.awt.datatransfer.*;
import java.io.IOException;

public class StageElementTransferable implements Transferable, ClipboardOwner {
    private Object data;

    public StageElementTransferable(Object data) {
        this.data = data;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] { getObjectFlavor() };
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(getObjectFlavor());
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (flavor.equals(getObjectFlavor())) {
            return data;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    public static DataFlavor getObjectFlavor() {
        DataFlavor objectFlavor = null;

        try {
            objectFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + JLightDefinition.class.getName() + "\"");
        } catch (ClassNotFoundException ex) {
            System.out.println(ex.getMessage());
        }

        return objectFlavor;
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) { }
}
