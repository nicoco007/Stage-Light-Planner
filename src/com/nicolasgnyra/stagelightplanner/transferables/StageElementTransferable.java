package com.nicolasgnyra.stagelightplanner.transferables;

import com.nicolasgnyra.stagelightplanner.components.JLightDefinition;

import java.awt.datatransfer.*;
import java.io.IOException;

/**
 * StageElementTransferable Class:
 * A transferable for drag-and-drop interaction between a JFixturesContainer and a JStagePlanner.
 *
 * Date: 2016-09-27
 *
 * @author Nicolas Gnyra
 * @version 1.0
 */
public class StageElementTransferable implements Transferable, ClipboardOwner {

    private Object data; // transfer data

    /**
     * StageElementTransferable(Object) Constructor:
     * Creates a new instance of the StageElementTransferable class.
     *
     * Input: Transfer data.
     *
     * Process: Sets value.
     *
     * Output: A new instance of the StageElementTransferable class.
     *
     * @param data Transfer data.
     */
    public StageElementTransferable(Object data) {
        this.data = data;
    }

    /**
     * getTransferDataFlavors() Method:
     * Retrieves the DataFlavor instance that represents this transferable.
     *
     * Input: None.
     *
     * Process: Returns the value given by the getObjectFlavor method.
     *
     * Output: Array of this transferable's data flavors.
     *
     * @return Array of this transferable's data flavors.
     */
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] { getObjectFlavor() };
    }

    /**
     * isDataFlavorSupported(DataFlavor) Method:
     * Checks if the specified data flavor is supported by the transferable.
     *
     * Input: DataFlavor to check.
     *
     * Process: Compares the specified DataFlavor with this transferable's supported flavors.
     *
     * Output: Whether the data flavor is supported or not.
     *
     * @param flavor DataFlavor to check.
     * @return Whether the data flavor is supported or not.
     */
    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(getObjectFlavor());
    }

    /**
     * getTransferData(DataFlavor) Method:
     * Returns the transferable's data.
     *
     * Input: Retrieval flavor.
     *
     * Process: If the data flavor is supported, return the transfer data.
     *
     * Output: Transfer data.
     *
     * @param flavor Retrieval flavor.
     * @return Transfer data.
     * @throws UnsupportedFlavorException Thrown if the specified data flavor is not supported
     */
    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        if (flavor.equals(getObjectFlavor())) {
            return data;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    /**
     * getObjectFlavor() Method:
     * Returns the supported object flavor (a class data flavor).
     *
     * Input: None.
     *
     * Process: Attempts to create a data flavor from the supported classes.
     *
     * Output: Supported data flavor.
     *
     * @return Supported data flavor.
     */
    public static DataFlavor getObjectFlavor() {

        // create empty data flavor
        DataFlavor objectFlavor = null;

        // attempt to create the data flavor
        try {
            objectFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + JLightDefinition.class.getName() + "\"");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        // return the data flavor
        return objectFlavor;
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) { }
}
