package com.nicolasgnyra.stagelightplanner;

import com.nicolasgnyra.stagelightplanner.components.JStageElement;

import java.util.ArrayList;

/**
 * StagePlan Class:
 * Contains information on a stage plan. Currently only contains list of stage elements.
 *
 * Date: 2016-09-27
 *
 * @author Nicolas Gnyra
 * @version 1.0
 */
public class StagePlan {
    private final ArrayList<JStageElement> stageElements; // list of stage elements

    /**
     * StagePlan(ArrayList<JStageElement>) Constructor:
     * Creates a new instance of the StagePlan class with the specified stage elements.
     *
     * Input: List of stage elements.
     *
     * Process: Sets internal list to specified list.
     *
     * Output: A new instance of the StagePlan class.
     *
     * @param stageElements List of stage elements.
     */
    public StagePlan(ArrayList<JStageElement> stageElements) {
        this.stageElements = stageElements;
    }

    /**
     * equals(Object) Method:
     * Checks whether the supplied object is equal to this instance.
     *
     * Input: Object to compare.
     *
     * Process: Compares all properties of this class.
     *
     * Output: Whether the objects are equal or not.
     *
     * @param obj Object to compare.
     * @return Whether the objects are equal or not.
     */
    @Override
    public boolean equals(Object obj) {

        // check if instance of same class
        if (!(obj instanceof StagePlan))
            return false;

        // cast
        StagePlan otherStagePlan = (StagePlan) obj;

        // check if array size is the same
        if (getStageElements().size() != otherStagePlan.getStageElements().size())
            return false;

        // iterate through all elements and see if they are in the other object
        for (JStageElement stageElement : getStageElements())
            if (!otherStagePlan.getStageElements().contains(stageElement))
                return false;

        // return true
        return true;
    }

    public ArrayList<JStageElement> getStageElements() {
        return stageElements;
    }

    @Override
    public String toString() {
        return String.format("[stageElements=%s]", stageElements);
    }
}
