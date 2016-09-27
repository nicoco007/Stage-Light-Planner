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
    private ArrayList<JStageElement> stageElements; // list of stage elements

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

    public ArrayList<JStageElement> getStageElements() {
        return stageElements;
    }
}
