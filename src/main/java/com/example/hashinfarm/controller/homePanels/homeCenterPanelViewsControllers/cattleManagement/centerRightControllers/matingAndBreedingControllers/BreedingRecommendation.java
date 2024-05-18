package com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.centerRightControllers.matingAndBreedingControllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class BreedingRecommendation {

  private final Map<String, List<CheckBox>> categoryToCheckBoxes = new HashMap<>();
  private final ObservableList<String> selectedTraitsList = FXCollections.observableArrayList();
  @FXML public Accordion traitsAccordion;
  @FXML public ListView<String> traitsListView;
  @FXML public ScrollPane accordionScrollPane;
  @FXML
  public CheckBox milkProductionPaneCkB,
      reproductivePerformancePaneCkB,
      diseaseResistancePaneCkB,
      muscleStructurePaneCkB,
      feedEfficiencyPaneCkB,
      dispositionAndTemperamentPaneCkB,
      adaptabilityToClimatePaneCkB,
      growthRatePaneCkB,
      grassFedAdaptabilityPaneCkB,
      hornedOrPolledPaneCkB,
      coatColorPaneCkB,
      calfVigorPaneCkB;

  @FXML
  private void initialize() {
    initializeAccordion();
    initializeListView();
  }

  private void initializeListView() {
    traitsListView.setItems(selectedTraitsList);
  }

  private void initializeAccordion() {
    // Clear existing panes and checkbox mappings
    traitsAccordion.getPanes().clear();
    categoryToCheckBoxes.clear();

    // Map to store checkbox and its corresponding category
    Map<CheckBox, String> checkboxToCategory = new HashMap<>();
    checkboxToCategory.put(milkProductionPaneCkB, "Milk Production Traits");
    checkboxToCategory.put(reproductivePerformancePaneCkB, "Reproductive Performance Traits");
    checkboxToCategory.put(diseaseResistancePaneCkB, "Disease Resistance Traits");
    checkboxToCategory.put(muscleStructurePaneCkB, "Muscle Structure Traits");
    checkboxToCategory.put(feedEfficiencyPaneCkB, "Feed Efficiency Traits");
    checkboxToCategory.put(dispositionAndTemperamentPaneCkB, "Disposition and Temperament Traits");
    checkboxToCategory.put(adaptabilityToClimatePaneCkB, "Adaptability to Climate Traits");
    checkboxToCategory.put(growthRatePaneCkB, "Growth Rate Traits");
    checkboxToCategory.put(grassFedAdaptabilityPaneCkB, "Grass-Fed Adaptability Traits");
    checkboxToCategory.put(hornedOrPolledPaneCkB, "Horned Traits");
    checkboxToCategory.put(coatColorPaneCkB, "Coat Color Traits");
    checkboxToCategory.put(calfVigorPaneCkB, "Calf Vigor Traits");

    // Add event listeners to checkboxes
    checkboxToCategory.forEach(
        (checkbox, category) ->
            checkbox
                .selectedProperty()
                .addListener(
                    (observable, oldValue, newValue) -> {
                      if (newValue) {
                        addTitledPane(category);
                      } else {
                        removeTitledPane(category);
                      }
                    }));
  }

  private void addTitledPane(String category) {

    // Collapse all existing panes
    traitsAccordion.getPanes().forEach(pane -> pane.setExpanded(false));

    TitledPane titledPane = new TitledPane();
    titledPane.setText(category);

    if (category.equals("Horned Traits")) {
      // Special handling for "Horned Traits"
      GridPane gridPane = new GridPane();
      gridPane.setHgap(10);
      gridPane.setVgap(10);

      List<CheckBox> hornedCheckBoxes = createCheckBoxesForCategory("Horned Traits");
      List<CheckBox> polledCheckBoxes = createCheckBoxesForCategory("Polled Traits");

      // Add title labels for each list
      Label hornedLabel = new Label("Horned Traits:");
      Label polledLabel = new Label("Polled Traits:");

      gridPane.add(hornedLabel, 0, 0);
      gridPane.add(polledLabel, 1, 0);

      // Add horned checkboxes below the horned label
      for (int i = 0; i < hornedCheckBoxes.size(); i++) {
        CheckBox hornedCheckBox = hornedCheckBoxes.get(i);
        gridPane.add(hornedCheckBox, 0, i + 1);

        // Add event listener to update ListView
        hornedCheckBox
            .selectedProperty()
            .addListener(
                (observable, oldValue, newValue) -> {
                  String trait = hornedCheckBox.getText();
                  if (newValue) {
                    // Add trait to the ListView
                    selectedTraitsList.add(trait);
                  } else {
                    // Remove trait from the ListView
                    selectedTraitsList.remove(trait);
                  }
                });
      }

      // Add polled checkboxes below the polled label
      for (int i = 0; i < polledCheckBoxes.size(); i++) {
        CheckBox polledCheckBox = polledCheckBoxes.get(i);
        gridPane.add(polledCheckBox, 1, i + 1);

        // Add event listener to update ListView
        polledCheckBox
            .selectedProperty()
            .addListener(
                (observable, oldValue, newValue) -> {
                  String trait = polledCheckBox.getText();
                  if (newValue) {
                    // Add trait to the ListView
                    selectedTraitsList.add(trait);
                  } else {
                    // Remove trait from the ListView
                    selectedTraitsList.remove(trait);
                  }
                });
      }

      titledPane.setContent(gridPane);
    } else {
      // Default handling for other categories
      List<CheckBox> categoryCheckBoxes = createCheckBoxesForCategory(category);

      // Store CheckBoxes for the category
      categoryToCheckBoxes.put(category, categoryCheckBoxes);

      // Create VBox to hold CheckBoxes
      VBox content = new VBox();
      content.getChildren().addAll(categoryCheckBoxes);

      titledPane.setContent(content);

      // Add event listeners to checkboxes
      categoryCheckBoxes.forEach(
          checkBox ->
              checkBox
                  .selectedProperty()
                  .addListener(
                      (observable, oldValue, newValue) -> {
                        String trait = checkBox.getText();
                        if (newValue) {
                          // Add trait to the ListView
                          selectedTraitsList.add(trait);
                        } else {
                          // Remove trait from the ListView
                          selectedTraitsList.remove(trait);
                        }
                      }));
    }

    // Add TitledPane to the Accordion
    traitsAccordion.getPanes().add(titledPane);
  }

  private void removeTitledPane(String category) {
    // Remove TitledPane from the Accordion based on category
    TitledPane removedPane = null;
    for (TitledPane titledPane : traitsAccordion.getPanes()) {
      if (titledPane.getText().equals(category)) {
        removedPane = titledPane;
        break;
      }
    }

    if (removedPane != null) {
      // Remove traits associated with the category from the ListView
      VBox content = (VBox) removedPane.getContent();
      content
          .getChildren()
          .forEach(
              node -> {
                if (node instanceof CheckBox) {
                  String trait = ((CheckBox) node).getText();
                  selectedTraitsList.remove(trait);
                }
              });

      // Remove CheckBoxes for the category
      categoryToCheckBoxes.remove(category);

      // Remove TitledPane from the Accordion
      traitsAccordion.getPanes().remove(removedPane);
    }
  }

  private List<CheckBox> createCheckBoxesForCategory(String category) {
    List<CheckBox> checkBoxes = new ArrayList<>();
    getTraitsForCategory(category)
        .forEach(
            trait -> {
              CheckBox checkBox = new CheckBox(trait);
              checkBoxes.add(checkBox);
            });
    return checkBoxes;
  }

  private List<String> getTraitsForCategory(String category) {
    // Implement this method to return traits for each category
    // Use the provided traits or customize as needed
    return switch (category) {
      case "Coat Color Traits" ->
          List.of(
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
              "Rich Red, Adaptable, Good Foraging");

      case "Milk Production Traits" ->
          List.of(
              "Yield: High milk producer for abundant supply.",
              "Quality: Rich butterfat content for premium milk.",
              "Nutrients: Optimal protein levels, ensuring nutrition.",
              "Duration: Long lactation period, reliable output.",
              "Composition: Balanced milk components for health.",
              "Sustainability: Consistent, sustainable milk volume.",
              "Efficiency: Maximizes feed-to-milk conversion.",
              "Resilience: Robust production in varied conditions.",
              "Adaptability: Maintains high yield in diverse practices.",
              "Health: Promotes continuous milk production.");

      case "Reproductive Performance Traits" ->
          List.of(
              "Easy Calving",
              "Carries Calves to Term",
              "Gets Pregnant Easily",
              "Stays Fertile for a Long Time",
              "Calves Born Without Problems",
              "Reaches Maturity Early",
              "Efficient Breeding Over Time",
              "Healthy and Productive Breeding",
              "Long-Lasting Breeding Capability",
              "Quick Recovery After Giving Birth");

      case "Disease Resistance Traits" ->
          List.of(
              "Strong Immune System", "Resistant to Common Illnesses",
              "Built-in Resistance to Specific Diseases", "Generally Healthy Overall",
              "Rarely Affected by Respiratory Issues", "Stands Up Well to Foot and Mouth Diseases",
              "Resilient Against Common Parasites", "Generally Disease-Free",
              "Robust Health and Vigor", "Low Susceptibility to Infections");

      case "Feed Efficiency Traits" ->
          List.of(
              "Converts Feed into Growth Effectively", "Efficient at Grazing",
              "Utilizes Roughage Well", "Gains Weight Easily on Pasture",
              "Efficient Energy Use", "Processes Feed Effectively",
              "Utilizes Available Forages Efficiently", "Good at Turning Grass into Body Weight",
              "Thrives on a Variety of Feeds", "Maintains Weight Easily on Natural Diet");

      case "Disposition and Temperament Traits" ->
          List.of(
              "Calm and Easygoing",
              "Not Easily Agitated",
              "Friendly and Approachable",
              "Gentle and Easy to Handle",
              "Patient with Handling Procedures",
              "Good Mothering Instincts",
              "Easy to Work With",
              "Not Prone to Spookiness",
              "Generally Relaxed Demeanor",
              "Gets Along Well with Other Cattle");

      case "Growth Rate Traits" ->
          List.of(
              "Healthy Birth Weight", "Grows Well After Weaning",
              "Gains Weight Steadily as a Calf", "Reaches Target Weight Quickly",
              "Good Growth During the Yearling Phase", "Thrives in a Feedlot Environment",
              "Efficient Feed Conversion", "Consistent and Reliable Growth",
              "Above-Average Weight Gain", "Demonstrates a Strong Growth Curve");

      case "Calf Vigor Traits" ->
          List.of(
              "Quick Standing After Birth", "Demonstrates Robust Suckling Ability",
              "Exhibits Vigorous Movement and Activity", "Displays Alertness and Curiosity",
              "Resistant to Common Diseases in Early Life", "Forms a Strong Bond with the Mother",
              "Easily Adapts to the Herd Environment", "Maintains a Healthy Coat and Appearance",
              "Thrives with Minimal Intervention", "Shows Resilience During Environmental Changes");

      case "Grass-Fed Adaptability Traits" ->
          List.of(
              "Demonstrates Natural Grazing Behavior", "Efficiently Utilizes Various Forages",
              "Thrives on Low-Energy Grass-Based Diets", "Resistant to Bloat While on Pasture",
              "Adapts Well to Diverse Pasture Types",
                  "Utilizes Natural Grasses for Optimal Nutrition",
              "Maintains Body Condition on Pasture Alone",
                  "Shows Resilience in Changing Grass Availability",
              "Flourishes with Minimal Supplemental Feed",
                  "Exhibits a Preference for Grazing over Concentrates");
      case "Horned Traits" ->
          List.of(
              "Long, Curved Horns",
              "Twisted Horn Shape",
              "Forward-Pointing Horns",
              "Wide, Sweeping Horns",
              "Short, Sharp Horns",
              "C-Shaped Horns",
              "Spiral-Shaped Horns",
              "Upturned Horns",
              "Symmetrical Horns",
              "V-Shaped Horns");

      case "Polled Traits" ->
          List.of(
              "No Visible Horns",
              "Smooth Poll Head",
              "Polled Genetics",
              "Naturally Polled",
              "Hornless Breed",
              "Genetic Polled",
              "Naturally No Horns",
              "Smooth-Headed",
              "Polled Trait",
              "No Horn Growth");

      case "Adaptability to Climate Traits" ->
          List.of(
              "Heat Tolerance: Thrives in high temperatures.",
              "Cold Tolerance: Adaptable to colder climates.",
              "Humidity Resistance: Flourishes in humid conditions.",
              "Parasite Resistance: Naturally defends against parasites.",
              "Foraging Ability: Efficient foraging in adversity.",
              "Water Utilization: Effective use of water resources.",
              "Temperature Regulation: Regulates body temperature effectively.",
              "Climate Resilience: Endures various climate stressors.",
              "Terrain Adaptation: Thrives in different landscapes.",
              "Resource Efficiency: Maximizes productivity, minimal impact.");

      default -> new ArrayList<>();
    };
  }
}
