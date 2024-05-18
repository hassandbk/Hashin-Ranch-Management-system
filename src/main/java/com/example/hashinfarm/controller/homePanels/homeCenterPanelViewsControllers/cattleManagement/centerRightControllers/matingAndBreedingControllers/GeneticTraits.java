package com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.centerRightControllers.matingAndBreedingControllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneticTraits {

    @FXML
    public Accordion traitsAccordion;

    @FXML
    public ListView<String> traitsListView;

    @FXML
    public ScrollPane accordionScrollPane;

    @FXML
    public TitledPane coatColorPane, hornedOrPolledPane, milkProductionPane, muscleStructurePane,
            adaptabilityToClimatePane, reproductivePerformancePane, diseaseResistancePane,
            feedEfficiencyPane, dispositionAndTemperamentPane, growthRatePane, calfVigorPane,
            grassFedAdaptabilityPane;

    @FXML
    public VBox coatColorVBox, hornedTraitsVBox, polledTraitsVBox, milkProductionVBox,
            muscleStructureVBox, adaptabilityToClimateVBox, reproductivePerformanceVBox,
            diseaseResistanceVBox, feedEfficiencyVBox, dispositionAndTemperamentVBox, growthRateVBox,
            calfVigorVBox, grassFedAdaptabilityVBox;

    private final Map<TitledPane, List<String>> selectedTraitsByCategory = new HashMap<>();

    @FXML
    public void initialize() {
        // Initialize the accordion and other components
        initializeAccordion();
        // You can add more initialization if needed
    }

    private void initializeAccordion() {
        // Initialize the accordion panes and add them to the map...

        traitsAccordion.expandedPaneProperty().addListener((obs, oldPane, newPane) -> updateListView());

        // Create checkboxes for each category
        createCheckBoxesForCategory(coatColorPane, coatColorVBox);
        createCheckBoxesForGridPane(hornedOrPolledPane, hornedTraitsVBox, polledTraitsVBox);
        createCheckBoxesForCategory(milkProductionPane, milkProductionVBox);
        createCheckBoxesForCategory(muscleStructurePane, muscleStructureVBox);
        createCheckBoxesForCategory(adaptabilityToClimatePane, adaptabilityToClimateVBox);
        createCheckBoxesForCategory(reproductivePerformancePane, reproductivePerformanceVBox);
        createCheckBoxesForCategory(diseaseResistancePane, diseaseResistanceVBox);
        createCheckBoxesForCategory(feedEfficiencyPane, feedEfficiencyVBox);
        createCheckBoxesForCategory(dispositionAndTemperamentPane, dispositionAndTemperamentVBox);
        createCheckBoxesForCategory(growthRatePane, growthRateVBox);
        createCheckBoxesForCategory(calfVigorPane, calfVigorVBox);
        createCheckBoxesForCategory(grassFedAdaptabilityPane, grassFedAdaptabilityVBox);
    }

    private void createCheckBoxesForCategory(TitledPane categoryPane, VBox vBox) {
        List<String> dummyData = getDummyDataForCategory(categoryPane);
        for (String trait : dummyData) {
            CheckBox checkBox = new CheckBox(trait);
            checkBox.setOnAction(event -> handleCheckBoxClick(categoryPane, trait, checkBox.isSelected()));
            vBox.getChildren().add(checkBox);
        }
    }
    private void createCheckBoxesForGridPane(TitledPane categoryPane, VBox hornedVBox, VBox polledVBox) {
        List<String> hornedTraits = getDummyDataForHornedTraits();
        List<String> polledTraits = getDummyDataForPolledTraits();

        for (String trait : hornedTraits) {
            CheckBox checkBox = new CheckBox(trait);
            checkBox.setOnAction(event -> handleCheckBoxClick(categoryPane, trait, checkBox.isSelected()));
            hornedVBox.getChildren().add(checkBox);
        }

        for (String trait : polledTraits) {
            CheckBox checkBox = new CheckBox(trait);
            checkBox.setOnAction(event -> handleCheckBoxClick(categoryPane, trait, checkBox.isSelected()));
            polledVBox.getChildren().add(checkBox);
        }
    }

    private List<String> getDummyDataForCategory(TitledPane categoryPane) {
        // Add more cases if needed
        return switch (categoryPane.getId()) {
            case "coatColorPane" -> List.of(
                    "Solid Black",
                    "Red with White Face",
                    "Black and White Spotted",
                    "Cream to White",
                    "Red and White or Yellow and White Patches",
                    "Golden-Red or Yellow-Gold",
                    "Red, White, or Roan",
                    "Light Gray to Almost Black",
                    "Solid Red, Black, or Mix",
                    "Multi-Colored with Long Curved Horns",
                    "Shaggy, Long Hair in Red, Black, Yellow, or Dun",
                    "Solid Red, Heat-Resistant Breed",
                    "Black with a White Belt",
                    "Silver to Dark Gray",
                    "Pale Yellow to Light Gray, Double-Muscling",
                    "Solid Red, Dual-Purpose Breed",
                    "White to Steel Gray",
                    "Mahogany Red to Black",
                    "Red and White Spotted",
                    "Solid Red, Angus and Brahman Cross",
                    "Red and Sleek",
                    "Tricolor - Red, White, Dark Brown",
                    "Various Colors, 'Beefy' Appearance",
                    "Fawn or Light Brown, High Butterfat",
                    "Light to Dark Wheat-Colored",
                    "Small-Sized with Brown Shades",
                    "Red and White like Holsteins",
                    "Light to Dark Brown with Black Muzzle",
                    "Rich Red, Adaptable, Good Foraging"
            );
            case "milkProductionPane" -> List.of(
                    "Yield: High milk producer for abundant supply.",
                    "Quality: Rich butterfat content for premium milk.",
                    "Nutrients: Optimal protein levels, ensuring nutrition.",
                    "Duration: Long lactation period, reliable output.",
                    "Composition: Balanced milk components for health.",
                    "Sustainability: Consistent, sustainable milk volume.",
                    "Efficiency: Maximizes feed-to-milk conversion.",
                    "Resilience: Robust production in varied conditions.",
                    "Adaptability: Maintains high yield in diverse practices.",
                    "Health: Promotes continuous milk production."
            );
            case "muscleStructurePane" -> List.of(
                    "Hindquarters: Strong hind muscles for power.",
                    "Shoulders: Broad, powerful shoulders for mass.",
                    "Loin: Defined, sturdy loin for structure.",
                    "Thighs: Thick, powerful thighs for strength.",
                    "Chest: Deep chest, indicating vitality.",
                    "Rump: Well-developed rump for overall strength.",
                    "Balance: Harmonious muscle development.",
                    "Endurance: Built for sustained physical activity.",
                    "Flexibility: Adaptable muscles to different conditions.",
                    "Efficiency: Optimal muscle structure for energy use."
            );
            case "adaptabilityToClimatePane" -> List.of(
                    "Heat Tolerance: Thrives in high temperatures.",
                    "Cold Tolerance: Adaptable to colder climates.",
                    "Humidity Resistance: Flourishes in humid conditions.",
                    "Parasite Resistance: Naturally defends against parasites.",
                    "Foraging Ability: Efficient foraging in adversity.",
                    "Water Utilization: Effective use of water resources.",
                    "Temperature Regulation: Regulates body temperature effectively.",
                    "Climate Resilience: Endures various climate stressors.",
                    "Terrain Adaptation: Thrives in different landscapes.",
                    "Resource Efficiency: Maximizes productivity, minimal impact."
            );
            case "reproductivePerformancePane" -> List.of(
                    "Easy Calving",
                    "Carries Calves to Term",
                    "Gets Pregnant Easily",
                    "Stays Fertile for a Long Time",
                    "Calves Born Without Problems",
                    "Reaches Maturity Early",
                    "Efficient Breeding Over Time",
                    "Healthy and Productive Breeding",
                    "Long-Lasting Breeding Capability",
                    "Quick Recovery After Giving Birth"
            );
            case "diseaseResistancePane" -> List.of(
                    "Strong Immune System",
                    "Resistant to Common Illnesses",
                    "Built-in Resistance to Specific Diseases",
                    "Generally Healthy Overall",
                    "Rarely Affected by Respiratory Issues",
                    "Stands Up Well to Foot and Mouth Diseases",
                    "Resilient Against Common Parasites",
                    "Generally Disease-Free",
                    "Robust Health and Vigor",
                    "Low Susceptibility to Infections"
            );
            case "feedEfficiencyPane" -> List.of(
                    "Converts Feed into Growth Effectively",
                    "Efficient at Grazing",
                    "Utilizes Roughage Well",
                    "Gains Weight Easily on Pasture",
                    "Efficient Energy Use",
                    "Processes Feed Effectively",
                    "Utilizes Available Forages Efficiently",
                    "Good at Turning Grass into Body Weight",
                    "Thrives on a Variety of Feeds",
                    "Maintains Weight Easily on Natural Diet"
            );
            case "dispositionAndTemperamentPane" -> List.of(
                    "Calm and Easygoing",
                    "Not Easily Agitated",
                    "Friendly and Approachable",
                    "Gentle and Easy to Handle",
                    "Patient with Handling Procedures",
                    "Good Mothering Instincts",
                    "Easy to Work With",
                    "Not Prone to Spookiness",
                    "Generally Relaxed Demeanor",
                    "Gets Along Well with Other Cattle"
            );
            case "growthRatePane" -> List.of(
                    "Healthy Birth Weight",
                    "Grows Well After Weaning",
                    "Gains Weight Steadily as a Calf",
                    "Reaches Target Weight Quickly",
                    "Good Growth During the Yearling Phase",
                    "Thrives in a Feedlot Environment",
                    "Efficient Feed Conversion",
                    "Consistent and Reliable Growth",
                    "Above-Average Weight Gain",
                    "Demonstrates a Strong Growth Curve"
            );
            case "calfVigorPane" -> List.of(
                    "Quick Standing After Birth",
                    "Demonstrates Robust Suckling Ability",
                    "Exhibits Vigorous Movement and Activity",
                    "Displays Alertness and Curiosity",
                    "Resistant to Common Diseases in Early Life",
                    "Forms a Strong Bond with the Mother",
                    "Easily Adapts to the Herd Environment",
                    "Maintains a Healthy Coat and Appearance",
                    "Thrives with Minimal Intervention",
                    "Shows Resilience During Environmental Changes"
            );
            case "grassFedAdaptabilityPane" -> List.of(
                    "Demonstrates Natural Grazing Behavior",
                    "Efficiently Utilizes Various Forages",
                    "Thrives on Low-Energy Grass-Based Diets",
                    "Resistant to Bloat While on Pasture",
                    "Adapts Well to Diverse Pasture Types",
                    "Utilizes Natural Grasses for Optimal Nutrition",
                    "Maintains Body Condition on Pasture Alone",
                    "Shows Resilience in Changing Grass Availability",
                    "Flourishes with Minimal Supplemental Feed",
                    "Exhibits a Preference for Grazing over Concentrates"
            );
            // Add more cases if needed
            default -> new ArrayList<>();
        };

    }

    private List<String> getDummyDataForHornedTraits() {
        return List.of(
                "Long, Curved Horns", "Twisted Horn Shape", "Forward-Pointing Horns",
                "Wide, Sweeping Horns", "Short, Sharp Horns", "C-Shaped Horns",
                "Spiral-Shaped Horns", "Upturned Horns", "Symmetrical Horns", "V-Shaped Horns"
        );
    }

    private List<String> getDummyDataForPolledTraits() {
        return List.of(
                "No Visible Horns", "Smooth Poll Head", "Polled Genetics", "Naturally Polled",
                "Hornless Breed", "Genetic Polled", "Naturally No Horns", "Smooth-Headed",
                "Polled Trait", "No Horn Growth"
        );
    }

    private void handleCheckBoxClick(TitledPane categoryPane, String trait, boolean isSelected) {
        if (isSelected) {
            selectedTraitsByCategory.computeIfAbsent(categoryPane, k -> new ArrayList<>()).add(trait);
        } else {
            selectedTraitsByCategory.get(categoryPane).remove(trait);
        }
        updateListView();
    }

    private void updateListView() {
        List<String> allSelectedTraits = new ArrayList<>();

        for (Map.Entry<TitledPane, List<String>> entry : selectedTraitsByCategory.entrySet()) {
            String categoryName = entry.getKey().getText();
            List<String> traits = entry.getValue();

            allSelectedTraits.add(categoryName);
            allSelectedTraits.addAll(traits);
        }

        traitsListView.getItems().setAll(allSelectedTraits);
        updateListViewStyles();
    }

    private void updateListViewStyles() {
        List<String> styledList = new ArrayList<>();

        for (Map.Entry<TitledPane, List<String>> entry : selectedTraitsByCategory.entrySet()) {
            String categoryName = entry.getKey().getText();
            List<String> traits = entry.getValue();

            // Add category label as a separate item
            Label categoryLabel = getCategoryLabel(categoryName);
            styledList.add(categoryLabel.getText());

            // Add traits under the category as separate items
            for (String trait : traits) {
                styledList.add("    • " + trait);
            }
        }

        // Apply styles to the ListView
        traitsListView.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 14;");

        // Set the items to the ListView
        traitsListView.getItems().setAll(styledList);
    }



    private Label getCategoryLabel(String categoryName) {
        Label label = new Label(categoryName);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #0077cc;");
        return label;
    }
}
