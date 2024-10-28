package com.example.hashinfarm.helpers;

import com.example.hashinfarm.data.records.StageDetails;
import com.example.hashinfarm.data.records.SubStageDetails;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;


import java.util.Map;
import java.util.function.BiPredicate;

public class CustomTreeCell extends TreeCell<String> {
    private final int gestationDays;
    private final Map<String, StageDetails> stageDetailsMap;
    private final BiPredicate<SubStageDetails, Integer> isSubStageApplicable;

    public CustomTreeCell(int gestationDays, Map<String, StageDetails> stageDetailsMap, BiPredicate<SubStageDetails, Integer> isSubStageApplicable) {
        this.gestationDays = gestationDays;
        this.stageDetailsMap = stageDetailsMap;
        this.isSubStageApplicable = isSubStageApplicable;
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(item);

            TreeItem<String> parent = getTreeItem().getParent();
            StageDetails stageDetails = stageDetailsMap.get(parent != null ? parent.getValue() : null);

            if (stageDetails != null) {
                SubStageDetails subStageDetails = stageDetails.subStages().stream()
                        .filter(s -> s.name().equals(item))
                        .findFirst().orElse(null);

                if (subStageDetails != null && isSubStageApplicable.test(subStageDetails, gestationDays)) {
                    setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                } else {
                    setStyle(""); // Remove any previously applied style (optional)
                }
            }
        }
    }
}