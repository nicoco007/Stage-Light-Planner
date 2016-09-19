package com.nicolasgnyra.stagelightplanner;

import com.nicolasgnyra.stagelightplanner.components.JStageElement;

import java.util.ArrayList;

public class StagePlan {
    private ArrayList<JStageElement> stageElements;

    public StagePlan(ArrayList<JStageElement> stageElements) {
        this.stageElements = stageElements;
    }

    public ArrayList<JStageElement> getStageElements() {
        return stageElements;
    }
}
