package com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.centerRightControllers;

import com.example.hashinfarm.controller.dao.*;
import com.example.hashinfarm.controller.homePanels.homeCenterPanelViewsControllers.cattleManagement.centerLeftControllers.AddNewCattleController;
import com.example.hashinfarm.controller.records.StageDetails;
import com.example.hashinfarm.controller.records.SubStageDetails;
import com.example.hashinfarm.controller.utility.*;
import com.example.hashinfarm.model.Cattle;
import com.example.hashinfarm.model.Herd;
import com.example.hashinfarm.model.LactationPeriod;
import com.example.hashinfarm.model.ReproductiveVariables;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ProductivityAndCalving {

    private final ReproductiveVariablesDAO reproductiveVariablesDAO = new ReproductiveVariablesDAO();
    private final int minGestationDays = 265;
    private final int maxGestationDays = 295;
    private final Map<String, Label[]> volumeLabels = new HashMap<>();
    private final Map<String, Integer[]> stageDaysRangeMap = new HashMap<>();
    private final Map<String, StageDetails> stageDetailsMap = new HashMap<>();
    public Spinner<Double> spinnerMorning, spinnerAfternoon, spinnerEvening;


    @FXML
    private TableView<LactationPeriodWithSelection> lactationTableView;
    @FXML
    private TableColumn<LactationPeriodWithSelection, Boolean> selectionColumn;
    @FXML
    private TableColumn<LactationPeriodWithSelection, LocalDate> startDateColumn;
    @FXML
    private TableColumn<LactationPeriodWithSelection, LocalDate> endDateColumn;
    @FXML
    private TableColumn<LactationPeriodWithSelection, Void> actionColumn;
    private boolean saveButtonPressed = true;
    @FXML
    private Button saveButton, updateButton, modifyLactationButton,clearButton,saveUpdateProduction;
    @FXML
    private Spinner<Integer> estimatedGestationSpinner;
    @FXML
    private RadioButton pregnantRadioBtn, notPregnantRadioBtn;
    @FXML
    private TextArea healthNotesTextArea;
    @FXML
    private VBox compareCowBtn;
    @FXML
    private Label productionStageLabel,
            volumeLabel1,
            volumeLabel2,
            volumeLabel3,
            lactationPeriodLabel,
            stagePeriodLabel,
            currentProductionStageLabel,
            daysSinceCalvingLabel,
            relativeMilkYieldLabel,
            milkYieldLabel,
            targetCalvingAgeLabel,
            ageAtFirstCalvingLabel,
            projectedCalvingDate,
            currentPeriodLabel;
    @FXML
    private TextArea daysInPregnancyTextArea;
    @FXML
    private ComboBox<String> qualityScoreComboBoxMorning,
            qualityScoreComboBoxAfternoon,
            qualityScoreComboBoxEvening;
    @FXML
    private TextField daysInLactationTextField, calvingIntervalTextField,currentDateTextField,lactationStartDateField;
    @FXML
    private ToggleGroup pregnancyStatus, toggleGroup;
    @FXML
    private DatePicker lactationEndDatePicker,
             selectStartDate,
            selectEndDate,
            calvingDate,
            lastBreedingDate;
    @FXML
    private TableView<ReproductiveVariables> calvingHistoryTableView;
    @FXML
    private TreeView<String> stageOfPregnancy;
    @FXML
    private GridPane gridPaneProduction;
    private int selectedCattleId = 0;
    private LocalDate selectedCattleDateOfBirth;
    private CattleDAO cattleDAO;
    private boolean initialLogicIsMostRecent = false; // Flag for initial logic using most recent
    private ReproductiveVariables selectedReproductiveVariable = null;
    private AddNewCattleController addNewCattleController;
    private Stage selectionStage;
    private LactationPeriodWithSelection selectedPeriodWithSelection;
    private LocalDate originalEndDate; // To keep track of the original end date from the database
    private LocalDate selectedEndDate; // To keep track of the selected end date
    private LactationPeriod ongoingLactationPeriod;

    public void initialize() {

        initializeCattleDAO();
        initializeCalvingHistory();
        initializeSelectedCattleManagerListeners();
        initializeGestationPeriod();
        initializeCalvingDateListener();
        initializeBreedingDateListeners();
        initializePregnancyStatusListener();
        initializeRadioButtonInteractions();
        initializeCalvingHistoryTableViewListener();
        setDatesAndDateFormats();
        initializeCalvingDateDayCellFactory();
        initializeLastBreedingDateDayCellFactory();
        initializeLactationPeriods();
        initializeVolumeLabels();
        initializeStageDaysRangeMap();
        initializeStageDetailsMap();
        updateLactationStartDateField();
        initializeSpinners();
        initializeProductionStages();
    }

    private void initializeCattleDAO() {
        cattleDAO = new CattleDAO();
    }

    private void setDatesAndDateFormats() {
        setDatePickerFormat(lactationEndDatePicker);
        setDatePickerFormat(selectStartDate);
        setDatePickerFormat(selectEndDate);
        setDatePickerFormat(calvingDate);
        setDatePickerFormat(lastBreedingDate);
        calvingDate.valueProperty()
                .addListener((observable, oldValue, newValue) -> computeCalvingInterval(newValue, oldValue));

        currentDateTextField.setText(String.valueOf(LocalDate.now()));

    }

    private void initializeRadioButtonInteractions() {
        calvingHistoryTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    private void initializeCalvingHistoryTableViewListener() {
        calvingHistoryTableView
                .getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (observable, oldValue, newValue) -> {
                            if (newValue != null) {
                                initialLogicIsMostRecent = false;
                                updateFieldsWithSelectedRecord(newValue);
                                saveButton.setDisable(true);
                                updateButton.setDisable(true);
                            }
                        });
    }

    private void initializeCalvingHistory() {
        createTableColumns();

        SelectedCattleManager selectedCattleManager = SelectedCattleManager.getInstance();
        selectedCattleManager
                .selectedCattleIDProperty()
                .addListener(
                        (observable, oldValue, newValue) -> {
                            if (newValue == null || newValue.intValue() == 0) return;

                            selectedCattleId = newValue.intValue();
                            loadCalvingHistoryForCattle(selectedCattleId);
                            updateCalvingHistoryData();
                        });

        selectedCattleManager
                .selectedDateOfBirthProperty()
                .addListener(
                        (observable, oldValue, newValue) -> {
                            if (newValue != null) {
                                selectedCattleDateOfBirth = selectedCattleManager.getSelectedDateOfBirth();
                                setAgeAtFirstCalving();
                            }
                        });
    }

    private void initializeGestationPeriod() {
        int initialGestationDays = 283;
        estimatedGestationSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        minGestationDays, maxGestationDays, initialGestationDays));
        estimatedGestationSpinner
                .valueProperty()
                .addListener(
                        (observable, oldValue, newValue) -> {
                            updateProjectedCalvingDate();
                            try {
                                updatePregnancyStatus(lastBreedingDate.getValue());

                            } catch (NumberFormatException e) {
                                // Handle errors gracefully
                            }
                        });
    }

    private void initializeCalvingDateListener() {
        calvingDate.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                List<LocalDate> allBreedingDates = getAllBreedingDatesForCattle(selectedCattleId);
                List<LocalDate> allCalvingDates = getAllCalvingDatesForCattle(selectedCattleId);

                if (selectedReproductiveVariable == null) { // No existing record selected
                    if (lastBreedingDate.getValue() == null) {
                        saveButton.setDisable(true);
                        updateButton.setDisable(true);
                    } else {
                        saveButton.setDisable(false);
                        updateButton.setDisable(true);
                    }
                } else {
                    if (allBreedingDates.contains(lastBreedingDate.getValue()) && allCalvingDates.contains(newValue)) {
                        saveButton.setDisable(true);
                        updateButton.setDisable(true);
                    } else {
                        saveButton.setDisable(true);
                        updateButton.setDisable(false);
                    }
                }

                Optional<String> validationMessage = DateUtil.validateCalvingDate(newValue, lastBreedingDate.getValue(), minGestationDays, maxGestationDays);
                if (validationMessage.isPresent()) {
                    calvingDate.setValue(oldValue);
                    showAlert(validationMessage.get());
                } else {
                    updateDaysSinceCalving();
                    updatePregnantStatusLabel();
                }


            }
        });
    }

    private void initializeBreedingDateListeners() {
        lastBreedingDate.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                List<LocalDate> allBreedingDates = getAllBreedingDatesForCattle(selectedCattleId);
                List<LocalDate> allCalvingDates = getAllCalvingDatesForCattle(selectedCattleId);

                if (selectedReproductiveVariable == null) { // No existing record selected

                    saveButton.setDisable(false);
                    updateButton.setDisable(true);

                } else {

                    if ((allCalvingDates.contains(calvingDate.getValue()) || calvingDate.getValue() == null) && allBreedingDates.contains(newValue)) {
                        saveButton.setDisable(true);
                        updateButton.setDisable(true);
                    } else {
                        saveButton.setDisable(true);
                        updateButton.setDisable(false);
                    }
                }

                if (newValue.isAfter(LocalDate.now())) {
                    showFutureBreedingDateAlert();
                    lastBreedingDate.setValue(oldValue);
                } else {
                    updateProjectedCalvingDate();
                    if (calvingDate.getValue() != null) {
                        updatePregnantStatusLabel();
                    }
                    pregnantRadioBtn.setDisable(false);
                }
            } else {
                pregnantRadioBtn.setDisable(true);
            }
        });
    }



    private void initializeSelectedCattleManagerListeners() {
        SelectedCattleManager selectedCattleManager = SelectedCattleManager.getInstance();
        selectedCattleManager
                .selectedCattleIDProperty()
                .addListener(
                        (observable, oldValue, newValue) -> {
                            if (newValue != null && newValue.intValue() != 0) {
                                selectedCattleId = selectedCattleManager.getSelectedCattleID();
                                updateLactationStartDateField();
                            }
                        });
        selectedCattleManager
                .selectedDateOfBirthProperty()
                .addListener((observable, oldValue, newValue) -> selectedCattleDateOfBirth = newValue);
    }

    private void initializeCalvingDateDayCellFactory() {
        calvingDate.setDayCellFactory(
                param ->
                        new DateCell() {
                            @Override
                            public void updateItem(LocalDate item, boolean empty) {
                                super.updateItem(item, empty);
                                // Check if lastBreedingDate is null, disable and style all cells
                                if (lastBreedingDate.getValue() == null) {
                                    setDisable(true);
                                    setStyle("-fx-background-color: #ffc0cb;");
                                    return; // Early exit if breeding date is null
                                }

                                if (item != null && lastBreedingDate.getValue() != null) {
                                    LocalDate minDate = lastBreedingDate.getValue().plusDays(minGestationDays);
                                    LocalDate maxDate = lastBreedingDate.getValue().plusDays(maxGestationDays);
                                    if (item.isBefore(minDate) || item.isAfter(maxDate)) {
                                        setDisable(true);
                                        setStyle("-fx-background-color: #ffc0cb;");
                                    }
                                }
                            }
                        });
    }

    private void initializeLastBreedingDateDayCellFactory() {
        lastBreedingDate.setDayCellFactory(
                datePicker ->
                        new DateCell() {
                            @Override
                            public void updateItem(LocalDate item, boolean empty) {
                                super.updateItem(item, empty);
                                if (item != null) {
                                    LocalDate today = LocalDate.now();
                                    if (item.isAfter(today)) {
                                        disableCell();
                                    }


                                    if (lastBreedingDate.getValue() == null) {
                                        handleNullLastBreedingDate(item);
                                    } else if (calvingDate.getValue() == null && lastBreedingDate.getValue() != null) {
                                        LocalDate previousCalvingDate = getPreviousCalvingDate(lastBreedingDate.getValue());
                                        LocalDate minDate = calculateValidBreedingDate(previousCalvingDate, null, true);

                                        LocalDate maxDate = LocalDate.now();
                                        if (item.isBefore(minDate) || item.isAfter(maxDate)) {
                                            disableCell();
                                        }
                                    } else if (calvingDate.getValue() != null && lastBreedingDate.getValue() != null) {
                                        handleNonNullLastBreedingDate(item);
                                    }

                                }
                            }

                            private void disableCell() {
                                setDisable(true);
                                setStyle("-fx-background-color: #ffc0cb;");
                            }


                            private void handleNullLastBreedingDate(LocalDate item) {
                                handleBreedingDate(item);
                            }

                            private void handleNonNullLastBreedingDate(LocalDate item) {
                                if (!initialLogicIsMostRecent) {
                                    LocalDate previousCalvingDate = getPreviousCalvingDate(lastBreedingDate.getValue());
                                    LocalDate earliestValidBreedingDate = calculateValidBreedingDate(previousCalvingDate, calvingDate.getValue(), true);
                                    LocalDate latestValidBreedingDate = calculateValidBreedingDate(previousCalvingDate, calvingDate.getValue(), false);

                                    if (earliestValidBreedingDate == null || latestValidBreedingDate == null || item.isBefore(earliestValidBreedingDate) || item.isAfter(latestValidBreedingDate)) {
                                        disableCell();
                                    }
                                } else {
                                    handleBreedingDate(item);
                                }
                            }

                            private void handleBreedingDate(LocalDate item) {
                                LocalDate mostRecentReproductiveDate = findMostRecentReproductiveDate();
                                LocalDate minValidDate;
                                if (selectedCattleId != 0) {


                                    if (mostRecentReproductiveDate != null) {
                                        minValidDate = mostRecentReproductiveDate;
                                    } else {
                                        LocalDate dateOfBirth = cattleDAO.fetchDateOfBirth(selectedCattleId);

                                        if (dateOfBirth != null) {
                                            minValidDate = dateOfBirth.plusMonths(14);
                                        } else {
                                            // Set a minimum breeding age of 18 days from today
                                            minValidDate = LocalDate.now().plusDays(18);

                                            // Visually indicate the incomplete date of birth
                                            setStyle("-fx-background-color: #ffff99;"); // Light yellow
                                            setTooltip(new Tooltip("Please provide a date of birth for completeness."));

                                            // Optionally, display a warning alert
                                            Alert alert = new Alert(Alert.AlertType.WARNING);
                                            alert.setTitle("Incomplete Information");
                                            alert.setHeaderText(null);
                                            alert.setContentText("Please provide a date of birth for completeness.");
                                            alert.show();
                                        }
                                    }

                                    LocalDate currentDate = LocalDate.now();
                                    setDisable(item.isBefore(minValidDate) || item.isAfter(currentDate));
                                    setStyle(isDisabled() ? "-fx-background-color: #ffc0cb;" : "");
                                    initialLogicIsMostRecent = true;
                                } else {
                                    disableCell();
                                }


                            }
                        });
    }

    private void initializePregnancyStatusListener() {
        pregnancyStatus
                .selectedToggleProperty()
                .addListener(
                        (observable, oldValue, newValue) -> {
                            if (newValue != null) {
                                RadioButton selectedRadio = (RadioButton) newValue;
                                if (selectedRadio == pregnantRadioBtn) {
                                    LocalDate breedingDate = lastBreedingDate.getValue();
                                    daysSinceCalvingLabel.setText("N/A");
                                    if (breedingDate != null) {
                                        pregnantRadioBtn.setDisable(false);
                                        pregnantRadioBtn.setSelected(true);
                                        daysInPregnancyTextArea.setText(DateUtil.calculateDays(breedingDate));

                                    } else {
                                        pregnantRadioBtn.setSelected(false);
                                        notPregnantRadioBtn.setSelected(false);
                                        daysInPregnancyTextArea.setText("No breeding date selected");
                                    }
                                } else {
                                    updatePregnantStatusLabel();
                                }
                            }
                        });
    }

    private void initializeStageDetailsMap() {
        // Populate stageDetailsMap with stage details
        stageDetailsMap.put(
                "First Trimester",
                new StageDetails(
                        "First Trimester",
                        Arrays.asList(
                                new SubStageDetails("Fertilization (Day 0)", 0, 0, false),
                                new SubStageDetails("Cleavage (Days 1-7)", 1, 7, false),
                                new SubStageDetails("Implantation (Days 18-20)", 18, 20, false),
                                new SubStageDetails("Organogenesis (Days 20-50)", 20, 50, false),
                                new SubStageDetails("Placental Development(Days 0-80)", 0, 0, true))));
        stageDetailsMap.put(
                "Second Trimester",
                new StageDetails(
                        "Second Trimester",
                        Arrays.asList(
                                new SubStageDetails("Fetal Bud Stage (Days 80-90)", 80, 90, false),
                                new SubStageDetails("Fetal Lengthening (Days 90-120)", 90, 120, false),
                                new SubStageDetails("Fetal Muscle Development (Days 120-150)", 120, 150, false),
                                new SubStageDetails("Quickening (Possible, Days 120-150)", 120, 150, false),
                                new SubStageDetails("Amniotic Fluid Increase(Days 0-180", 0, 0, true))));
        stageDetailsMap.put(
                "Third Trimester",
                new StageDetails(
                        "Third Trimester",
                        Arrays.asList(
                                new SubStageDetails("Fetal Maturation (Days 180-250)", 180, 250, false),
                                new SubStageDetails("Fetal Fat Deposition (Days 200-280+)", 200, 295, false),
                                new SubStageDetails("Hair Coat Development (Days 220-250)", 220, 250, false),
                                new SubStageDetails("Colostrum Production (Days 250-280+)", 250, 295, false),
                                new SubStageDetails("Ligament Relaxation (Days 260-280+)", 260, 295, false),
                                new SubStageDetails("Udder Development (Days 260-280+)", 260, 295, false),
                                new SubStageDetails(
                                        "Behavioral Changes (Possible, Days 260-280+)", 260, 295, false),
                                new SubStageDetails("Relaxed Pelvic Ligaments (Days 270-280+)", 270, 295, false))));
    }
    public List<LocalDate> getAllBreedingDatesForCattle(int cattleId) {
        List<ReproductiveVariables> reproductiveVariablesList = reproductiveVariablesDAO.getAllReproductiveVariablesForCattle(cattleId);
        return reproductiveVariablesList.stream()
                .map(ReproductiveVariables::getBreedingDate)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<LocalDate> getAllCalvingDatesForCattle(int cattleId) {
        List<ReproductiveVariables> reproductiveVariablesList = reproductiveVariablesDAO.getAllReproductiveVariablesForCattle(cattleId);
        return reproductiveVariablesList.stream()
                .map(ReproductiveVariables::getCalvingDate)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private void populateTreeView() {
        TreeItem<String> rootItem = new TreeItem<>("Pregnancy Stages");
        rootItem.setExpanded(false);
        stageOfPregnancy.setRoot(rootItem);

        // Add main stages
        for (String stageName : stageDetailsMap.keySet()) {
            TreeItem<String> mainStage = createStage(stageName, rootItem);
            mainStage.setExpanded(true);
            for (SubStageDetails subStageDetails : stageDetailsMap.get(stageName).subStages()) {
                createSubStage(subStageDetails, mainStage);
            }
        }
        collapseAll(rootItem);
    }

    private TreeItem<String> createStage(String stageName, TreeItem<String> parent) {
        TreeItem<String> stageItem = new TreeItem<>(stageName);
        stageItem.setExpanded(true);
        InputStream inputStream = getClass().getResourceAsStream("/icons/pregnancyStatus.jpeg");
        if (inputStream != null) {
            ImageView imageView = new ImageView(new Image(inputStream));
            imageView.setFitWidth(20);
            imageView.setFitHeight(20);
            imageView.setPreserveRatio(true);
            stageItem.setGraphic(imageView);
        } else {
            System.err.println("Failed to load image resource: /icons/pregnancyStatus.jpeg");
        }

        parent.getChildren().add(stageItem);
        return stageItem;
    }

    private void createSubStage(SubStageDetails subStageDetails, TreeItem<String> parent) {
        TreeItem<String> subStageItem = new TreeItem<>(subStageDetails.name());

        Tooltip tooltip = new Tooltip("Description of " + subStageDetails.name());
        InputStream inputStream = getClass().getResourceAsStream("/icons/placeholder.png");
        if (inputStream != null) {
            ImageView placeholderGraphic = new ImageView(new Image(inputStream));
            placeholderGraphic.setFitWidth(20);
            placeholderGraphic.setFitHeight(20);
            placeholderGraphic.setPreserveRatio(true);
            Tooltip.install(placeholderGraphic, tooltip);
            subStageItem.setGraphic(placeholderGraphic);
        } else {
            System.err.println("Resource not found: /icons/placeholder.png");
        }
        parent.getChildren().add(subStageItem);
    }

    private void collapseAll(TreeItem<String> item) {
        if (item != null && !item.isLeaf()) {
            item.setExpanded(false);
            for (TreeItem<String> child : item.getChildren()) {
                collapseAll(child);
            }
        }
    }

    private void highlightSubStages(int gestationDays) {
        TreeItem<String> root = stageOfPregnancy.getRoot();
        if (root == null) {
            return;
        }

        collapseAll(root);

        TreeItem<String> mainStage = selectMainStage(gestationDays);
        if (mainStage == null) {
            return;
        }

        stageOfPregnancy
                .getSelectionModel()
                .setSelectionMode(SelectionMode.MULTIPLE); // Allow multiple selections

        // Pre-select sub_stages based on gestation days
        mainStage
                .getChildren()
                .forEach(
                        subStage -> {
                            StageDetails stageDetails = stageDetailsMap.get(mainStage.getValue());
                            if (stageDetails != null) {
                                stageDetails
                                        .subStages()
                                        .forEach(
                                                subStageDetails -> {
                                                    if (isSubStageApplicable(subStageDetails, gestationDays)) {
                                                        stageOfPregnancy.getSelectionModel().select(subStage);
                                                    }
                                                });
                            }
                        });

        stageOfPregnancy.setCellFactory(
                param -> new CustomTreeCell(gestationDays, stageDetailsMap, this::isSubStageApplicable));
    }

    private TreeItem<String> selectMainStage(int gestationDays) {
        if (gestationDays < 80) {
            return stageOfPregnancy.getRoot().getChildren().getFirst(); // First Trimester
        } else if (gestationDays < 180) {
            return stageOfPregnancy.getRoot().getChildren().get(1); // Second Trimester
        } else {
            return stageOfPregnancy.getRoot().getChildren().get(2); // Third Trimester
        }
    }

    private boolean isSubStageApplicable(SubStageDetails subStageDetails, int gestationDays) {
        if (subStageDetails.throughoutTrimester() && gestationDays >= 0) {
            // If the sub_stage is applicable throughout the trimester, return true
            return true;
        } else {
            // Check if gestation days fall within the sub_stage range
            return gestationDays >= subStageDetails.startDay()
                    && gestationDays <= subStageDetails.endDay();
        }
    }

    private void setDatePickerFormat(DatePicker datePicker) {
        DateUtil.datePickerFormat(datePicker);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid Calving Date");
        alert.setHeaderText("The selected calving date is not valid.");
        alert.setContentText(message);

        alert.showAndWait();
    }

    private void showFutureBreedingDateAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Breeding Date");
        alert.setHeaderText("The selected breeding date is a future date.");
        alert.setContentText("Breeding date cannot be in the future. Please select a valid past date.");

        alert.showAndWait();
    }

    private void updateFieldsWithSelectedRecord(ReproductiveVariables record) {

        lastBreedingDate.setValue(record.getBreedingDate());
        calvingDate.setValue(record.getCalvingDate());

        Integer calvingIntervalValue = record.getCalvingInterval();
        calvingIntervalTextField.setText(
                calvingIntervalValue > 0 ? String.valueOf(calvingIntervalValue) : "");
        estimatedGestationSpinner.getValueFactory().setValue(record.getGestationPeriod());

        if (record.equals(calvingHistoryTableView.getItems().getLast())
                && record.getCalvingDate() == null) {
            populateTreeView();
            // Calculate days since breeding
            long daysSinceBreeding = ChronoUnit.DAYS.between(record.getBreedingDate(), LocalDate.now());
            // Update the TreeView to highlight the current stage of pregnancy
            highlightSubStages((int) daysSinceBreeding);
        }
        selectedReproductiveVariable = record;

    }

    private LocalDate findMostRecentReproductiveDate() {
        List<ReproductiveVariables> reproductiveVariablesList =
                reproductiveVariablesDAO.getAllReproductiveVariablesForCattle(selectedCattleId);

        if (reproductiveVariablesList != null && !reproductiveVariablesList.isEmpty()) {
            // Extract all non-null dates into a stream
            Stream<LocalDate> allDates =
                    reproductiveVariablesList.stream()
                            .flatMap(entry -> Stream.of(entry.getCalvingDate(), entry.getBreedingDate()))
                            .filter(Objects::nonNull);

            // Find the maximum date (largest)
            Optional<LocalDate> mostRecentDate = allDates.max(Comparator.naturalOrder());

            if (mostRecentDate.isPresent()) {
                LocalDate recentDate = mostRecentDate.get();
                ReproductiveVariables mostRecentEvent = reproductiveVariablesList.stream()
                        .filter(entry -> entry.getCalvingDate() != null && entry.getCalvingDate().equals(recentDate)
                                || entry.getBreedingDate() != null && entry.getBreedingDate().equals(recentDate))
                        .findFirst()
                        .orElse(null);

                if (mostRecentEvent != null) {
                    // Checking the type of the most recent event
                    if (mostRecentEvent.getCalvingDate() != null && mostRecentEvent.getCalvingDate().equals(recentDate)) {
                        // If it's a calving, add 40 days
                        return recentDate.plusDays(40);
                    } else if (mostRecentEvent.getBreedingDate() != null && mostRecentEvent.getBreedingDate().equals(recentDate)) {
                        // If it's a breeding, add 18 days
                        return recentDate.plusDays(18);
                    }
                }
            }
        }

        return null;
    }

    private void updateLactationStartDateField() {
        LocalDate latestCalvingDate = getLatestCalvingDate(getAllCalvingDatesForCattle(selectedCattleId));
        if (latestCalvingDate != null) {
            lactationStartDateField.setText(String.valueOf(latestCalvingDate));
            // Disable all other days except the latest calving date
            } else {
            lactationStartDateField.setText(null);


        }
    }

    private LocalDate getLatestCalvingDate(List<LocalDate> calvingDates) {
        if (calvingDates == null || calvingDates.isEmpty()) {
            return null; // No calving dates found
        }
        return calvingDates.stream()
                .max(LocalDate::compareTo) // Use LocalDate::compareTo for latest date
                .get();
    }

    private void updatePregnantStatusLabel() {
        LocalDate calving = calvingDate.getValue();
        if (calving != null) {
            // Calving has occurred, update pregnant status label
            pregnantRadioBtn.setDisable(true);
            pregnantRadioBtn.setSelected(false);
            notPregnantRadioBtn.setSelected(true);

            daysInPregnancyTextArea.setText("Calved on " + calving); // Update label
        }
    }

    private void updateProjectedCalvingDate() {
        if (lastBreedingDate != null && lastBreedingDate.getValue() != null) {
            LocalDate lastBreeding = lastBreedingDate.getValue();
            int gestationPeriod = estimatedGestationSpinner.getValue();
            LocalDate targetCalvingDate = lastBreeding.plusDays(gestationPeriod);
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = targetCalvingDate.format(dateFormatter);

            Platform.runLater(() -> {
                projectedCalvingDate.setText(formattedDate);
                targetCalvingAgeLabel.setText(DateUtil.calculateAge(selectedCattleDateOfBirth, targetCalvingDate));
                setAgeAtFirstCalving();
            });
        }
    }


    private void updateCalvingHistoryData() {
        ReproductiveVariables lastRecord =
                calvingHistoryTableView.getItems().isEmpty()
                        ? null
                        : calvingHistoryTableView.getItems().getLast();

        // Set all values to null/empty initially
        lastBreedingDate.setValue(null);
        calvingDate.setValue(null);
        calvingIntervalTextField.clear();
        projectedCalvingDate.setText("");
        targetCalvingAgeLabel.setText("N/A");
        daysSinceCalvingLabel.setText("N/A");
        clearTreeView();

        // Update values based on the last record
        if (lastRecord != null) {
            // Store the selected record ID
            selectedReproductiveVariable = lastRecord;
            lastBreedingDate.setValue(lastRecord.getBreedingDate());
            calvingDate.setValue(lastRecord.getCalvingDate());
            calvingIntervalTextField.setText(
                    lastRecord.getCalvingInterval() > 0
                            ? String.valueOf(lastRecord.getCalvingInterval())
                            : "");
            estimatedGestationSpinner
                    .getValueFactory()
                    .setValue(lastRecord.getGestationPeriod() != 0 ? lastRecord.getGestationPeriod() : 283);

            // Check if the calving date is null, implying the cattle is pregnant
            if (lastRecord.getCalvingDate() == null) {
                populateTreeView();
                // Calculate days since breeding
                long daysSinceBreeding =
                        ChronoUnit.DAYS.between(lastRecord.getBreedingDate(), LocalDate.now());
                // Update the TreeView to highlight the current stage of pregnancy
                highlightSubStages((int) daysSinceBreeding);
            }
        }

        updateProjectedCalvingDate();
    }

    private void setAgeAtFirstCalving() {
        ageAtFirstCalvingLabel.setText("N/A");
        if (selectedCattleDateOfBirth != null) {
            LocalDate firstCalvingDate = fetchFirstCalvingDateForCattle(selectedCattleId);
            if (firstCalvingDate != null) {
                ageAtFirstCalvingLabel.setText(
                        DateUtil.calculateAge(selectedCattleDateOfBirth, firstCalvingDate));
            } else {
                // Handle the case where there is no calving date (e.g., the cattle has not calved yet)
                ageAtFirstCalvingLabel.setText("N/A");
            }
        } else {
            // Handle the case where the date of birth is not available
            ageAtFirstCalvingLabel.setText("Date of birth not available");
        }
    }

    // Function to fetch the first calving date for a specific cattle
    private LocalDate fetchFirstCalvingDateForCattle(int cattleId) {
        List<ReproductiveVariables> reproductiveVariablesList;
        reproductiveVariablesList =
                reproductiveVariablesDAO.getAllReproductiveVariablesForCattle(cattleId);

        if (reproductiveVariablesList != null && !reproductiveVariablesList.isEmpty()) {
            reproductiveVariablesList.sort(
                    Comparator.comparing(
                            ReproductiveVariables::getCalvingDate,
                            Comparator.nullsFirst(Comparator.naturalOrder())));
            for (ReproductiveVariables rv : reproductiveVariablesList) {
                if (rv.getCalvingDate() != null) {
                    return rv.getCalvingDate(); // Return the first non-null calving date
                }
            }
        }
        return null; // Return null if there's no calving date available
    }

    private void updateDaysSinceCalving() {
        LocalDate calving = calvingDate.getValue();
        if (calving != null) {
            daysSinceCalvingLabel.setText(DateUtil.calculateDays(calving));
            clearTreeView();
        } else {
            daysSinceCalvingLabel.setText("Calving date not provided");
        }
    }

    private void updatePregnancyStatus(LocalDate breedingDate) {

        if (breedingDate != null) {
            int gestationPeriodInDays = estimatedGestationSpinner.getValue();
            long estimatedGestationInMonths =
                    ChronoUnit.MONTHS.between(
                            breedingDate.plusDays(1), breedingDate.plusDays(gestationPeriodInDays));

            pregnantRadioBtn.setDisable(true);
            if (ChronoUnit.MONTHS.between(breedingDate, LocalDate.now()) <= estimatedGestationInMonths) {
                pregnantRadioBtn.setDisable(false);
                notPregnantRadioBtn.setDisable(false);
                pregnantRadioBtn.setSelected(true);
                notPregnantRadioBtn.setSelected(false);
                daysInPregnancyTextArea.setText(DateUtil.calculateDays(breedingDate));
            } else {
                pregnantRadioBtn.setDisable(false);
                notPregnantRadioBtn.setDisable(false);
                notPregnantRadioBtn.setSelected(true);
                pregnantRadioBtn.setSelected(false);
                String message =
                        "Cow is likely not pregnant. "
                                + DateUtil.calculateDays(breedingDate)
                                + " have passed since breeding.";
                daysInPregnancyTextArea.setText(message);
                clearTreeView();
            }
        } else {

            daysInPregnancyTextArea.setText("");
        }
    }

    private void clearTreeView() {
        stageOfPregnancy.setRoot(null);
    }

    private LocalDate getPreviousCalvingDate(LocalDate breedingDate) {
        List<ReproductiveVariables> reproductiveVariablesList =
                reproductiveVariablesDAO.getAllReproductiveVariablesForCattle(selectedCattleId);

        if (reproductiveVariablesList != null && !reproductiveVariablesList.isEmpty()) {
            reproductiveVariablesList.sort(
                    Comparator.comparing(
                            ReproductiveVariables::getCalvingDate,
                            Comparator.nullsLast(Comparator.naturalOrder())));

            LocalDate previousCalvingDate = null;

            for (ReproductiveVariables entry : reproductiveVariablesList) {
                LocalDate calvingDate = entry.getCalvingDate();

                if (calvingDate != null && calvingDate.isBefore(breedingDate)) {
                    if (previousCalvingDate == null || calvingDate.isAfter(previousCalvingDate)) {
                        previousCalvingDate = calvingDate;
                    }
                }
            }

            return previousCalvingDate;
        }

        return null;
    }

    private LocalDate calculateValidBreedingDate(
            LocalDate previousCalvingDate, LocalDate currentCalvingDate, boolean isEarliest) {
        if (currentCalvingDate != null) {
            LocalDate referenceDate =
                    isEarliest
                            ? currentCalvingDate.minusDays(maxGestationDays)
                            : currentCalvingDate.minusDays(minGestationDays);
            LocalDate date =
                    previousCalvingDate != null
                            ? previousCalvingDate.plusDays(isEarliest ? 40 : 60)
                            : cattleDAO.fetchDateOfBirth(selectedCattleId).plusMonths(14);
            return date.isAfter(referenceDate) ? date : referenceDate;
        } else {
            // If currentCalvingDate is null, we only consider the initial date without a reference date
            return previousCalvingDate != null
                    ? previousCalvingDate.plusDays(isEarliest ? 40 : 60)
                    : cattleDAO.fetchDateOfBirth(selectedCattleId).plusMonths(14);
        }
    }

    private void computeCalvingInterval(LocalDate newValue, LocalDate oldValue) {
        if (newValue == null) {
            return;
        }

        List<ReproductiveVariables> reproductiveVariablesList = reproductiveVariablesDAO.getAllReproductiveVariablesForCattle(selectedCattleId);
        // 1. No Existing Record (Empty reproductiveVariablesList)
        if (reproductiveVariablesList == null || reproductiveVariablesList.isEmpty()) {
            if (calvingDate.getValue() != null) {
                // Calculate interval from date of birth
                long days = newValue.toEpochDay() - cattleDAO.fetchDateOfBirth(selectedCattleId).toEpochDay();
                calvingIntervalTextField.setText(String.valueOf(days));
            }
            // No further processing needed
        } else {
            // 2. Existing Records (reproductiveVariablesList is not empty)
            List<ReproductiveVariables> sortedList = Objects.requireNonNull(reproductiveVariablesList).stream()
                    .filter(rv -> rv.getCalvingDate() != null)
                    .sorted(Comparator.comparing(ReproductiveVariables::getCalvingDate))
                    .toList();

            // A. Updating an Existing Record
            if (oldValue != null) {
                int currentIndex = sortedList.indexOf(sortedList.stream()
                        .filter(rv -> rv.getCalvingDate() != null && rv.getCalvingDate().equals(oldValue))
                        .findFirst().orElse(null));

                if (currentIndex > 0) {
                    LocalDate previousCalvingDate = sortedList.get(currentIndex - 1).getCalvingDate();
                    long days = newValue.toEpochDay() - (previousCalvingDate != null ? previousCalvingDate.toEpochDay() : cattleDAO.fetchDateOfBirth(selectedCattleId).toEpochDay());
                    calvingIntervalTextField.setText(String.valueOf(days));

                }
                // No further processing for update

                // B. Adding a New Record (Existing Records)
            } else {
                // Find the closest calving date before newValue (or breeding date if calving is null)
                Optional<ReproductiveVariables> previousRecord = sortedList.stream()
                        .filter(rv -> rv.getCalvingDate() != null && rv.getCalvingDate().isBefore(newValue))
                        .max(Comparator.comparing(ReproductiveVariables::getCalvingDate));

                // Use the closest calving date for calculation (if found)
                if (previousRecord.isPresent()) {
                    LocalDate previousCalvingDate = previousRecord.get().getCalvingDate();
                    long days = Objects.requireNonNull(newValue).toEpochDay() - previousCalvingDate.toEpochDay();
                    calvingIntervalTextField.setText(String.valueOf(days));

                } else {
                    // No previous calving date found, use date of birth
                    long days = Objects.requireNonNull(newValue).toEpochDay() - cattleDAO.fetchDateOfBirth(selectedCattleId).toEpochDay();
                    calvingIntervalTextField.setText(String.valueOf(days));
                }
            }
        }


    }


    public void handleAddCalfToHerd() {

        try {
            List<String> herdNames = HerdDAO.getUniqueNamesFromHerd();
            if (herdNames.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "No Herds Found", "There are no herds available.");
                return;
            }
            if (lastBreedingDate.getValue() != null && calvingDate.getValue() != null) {
                createSelectionWindow(herdNames);
            }
            if (saveButtonPressed) {
                if (lastBreedingDate.getValue() != null && calvingDate.getValue() == null) {
                    ReproductiveVariables reproductiveVariables = getReproductiveVariables(lastBreedingDate.getValue(), estimatedGestationSpinner.getValue());
                    boolean success = reproductiveVariablesDAO.addReproductiveVariable(reproductiveVariables);
                    if (success) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Reproductive Data added successfully.");
                        loadCalvingHistoryForCattle(selectedCattleId);
                    }
                }
            }

        } catch (SQLException e) {
            handleHerdRetrievalError(e);
            e.printStackTrace();
        }
    }


    private void createSelectionWindow(List<String> herdNames) {
        LocalDate breedingDate = lastBreedingDate.getValue();
        LocalDate calveDate = calvingDate.getValue();
        int gestationPeriod = estimatedGestationSpinner.getValue();
        selectionStage = new Stage();
        selectionStage.initModality(Modality.APPLICATION_MODAL);
        selectionStage.setTitle("Select Herd");
        // Create List View
        ListView<String> listView = new ListView<>();
        listView.getItems().addAll(herdNames);
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        // Create buttons
        Button okButton = new Button("OK");
        okButton.setOnAction(event -> {
            String selectedHerdName = listView.getSelectionModel().getSelectedItem();
            if (selectedHerdName != null) {
                selectionStage.close();
                if (saveButtonPressed) {
                    openAddCattleFormSaveCattleAndReproductiveData(selectedHerdName, calveDate, breedingDate, gestationPeriod);
                } else {
                    openAddCattleFormSaveCattleAndUpdateUpdateReproductiveData(selectedHerdName, calveDate, breedingDate, gestationPeriod);

                }


            } else {
                showAlert(Alert.AlertType.ERROR, "No Herd Selected", "Please select a herd.");
            }
        });
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(event -> selectionStage.close());
        Button quickAddButton = new Button("Quick Add Herd");
        quickAddButton.setOnAction(event -> {
            selectionStage.close();
            openAddNewHerdForm();
        });

        // Create HBox for buttons
        HBox buttonBox = new HBox(10); // spacing between buttons
        buttonBox.getChildren().addAll(okButton, cancelButton, quickAddButton);
        buttonBox.setPadding(new Insets(10)); // padding around buttons
        buttonBox.setAlignment(Pos.CENTER); // center-align buttons
        HBox.setHgrow(buttonBox, Priority.ALWAYS); // allow HBox to grow horizontally
        // Organize elements
        VBox root = new VBox();
        root.getChildren().addAll(listView, buttonBox); // Add HBox to VBox

        // Set up scene
        Scene scene = new Scene(root);
        selectionStage.setScene(scene);
        selectionStage.show();
    }


    private void openAddCattleFormSaveCattleAndReproductiveData(String selectedHerdName, LocalDate dateOfBirth, LocalDate breedingDate, int gestationPeriod) {
        try {
            Herd selectedHerd = HerdDAO.getHerdByName(selectedHerdName);
            if (selectedHerd != null) {
                openForm("/com/example/hashinfarm/homePanels/homeCenterPanelViews/cattleManagement/centerLeftViews/addCattle.fxml",
                        "Add Cattle to Herd",
                        pair -> {
                            FXMLLoader loader = pair.getValue();

                            addNewCattleController = loader.getController();
                            addNewCattleController.initData(selectedHerd.getId(), selectedCattleId, dateOfBirth);

                            addNewCattleController.setCattleAdditionCallback((successFlag, failureReason, newCattleId) -> {
                                if (successFlag) {
                                    showAlert(Alert.AlertType.INFORMATION, "Success", "Cattle added successfully.");
                                    // Add reproductive variables here
                                    ReproductiveVariables reproductiveVariables = getReproductiveVariables(breedingDate, gestationPeriod);
                                    boolean reproductiveSuccess = reproductiveVariablesDAO.addReproductiveVariable(reproductiveVariables);
                                    if (reproductiveSuccess) {
                                        // Add lactation period
                                        boolean lactationPeriodSuccess = LactationPeriodDAO.addLactationPeriod(selectedCattleId, dateOfBirth);
                                        if (lactationPeriodSuccess) {
                                            clearReproductiveData();
                                            showAlert(Alert.AlertType.INFORMATION, "Success", "Cattle, Reproductive data, and Lactation Period added successfully.");
                                            loadCalvingHistoryForCattle(selectedCattleId);
                                        } else {
                                            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save Lactation Period data.");
                                            CattleDAO.deleteCattleById(newCattleId);  // Rollback cattle addition
                                        }
                                    } else {
                                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to save Reproductive data.");
                                        CattleDAO.deleteCattleById(newCattleId);  // Rollback cattle addition
                                    }
                                } else {
                                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to add cattle: " + failureReason);
                                }
                            });
                        });
            } else {
                throw new HerdNotFoundException("Selected herd not found.");
            }
        } catch (SQLException | HerdNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save data: " + e.getMessage());
        }
    }


    private void openAddNewHerdForm() {
        openForm("/com/example/hashinfarm/homePanels/homeCenterPanelViews/cattleManagement/centerLeftViews/AddNewHerd.fxml",
                "Add New Herd",
                pair -> pair.getKey().setOnCloseRequest(event -> handleAddCalfToHerd()));
    }

    private void openForm(String fxmlPath, String title, Consumer<Pair<Stage, FXMLLoader>> additionalSetup) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            if (additionalSetup != null) {
                additionalSetup.accept(new Pair<>(stage, loader));
            }
            stage.show();
        } catch (IOException e) {
            handleFormError(title, e);
        }
    }


    public void updateReproductiveData() {
        LocalDate breedingDate = lastBreedingDate.getValue();
        LocalDate calvedDate = calvingDate.getValue();
        int gestationPeriod = estimatedGestationSpinner.getValue();
        saveButtonPressed = false;

        // Scenario 1: Updating Breeding Date Only
        if (calvedDate == null) {
            ReproductiveVariables reproductiveVariable = selectedReproductiveVariable;
            if (!reproductiveVariable.getBreedingDate().equals(breedingDate)) {
                reproductiveVariable.setBreedingDate(breedingDate);
                reproductiveVariable.setGestationPeriod(gestationPeriod);
                boolean success = reproductiveVariablesDAO.updateReproductiveVariable(reproductiveVariable);
                if (success) {
                    clearReproductiveData();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Breeding date updated successfully.");
                    loadCalvingHistoryForCattle(selectedCattleId);
                } else {
                    handleUpdateFailure();
                }
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Information", "No changes detected.");
            }
        }

        // Scenario 2: Updating Record with Existing Breeding Date and Adding Calving Date
        if (selectedReproductiveVariable != null && selectedReproductiveVariable.getCalvingDate() == null && calvedDate != null) {
            handleAddCalfToHerd();
        }


        // Scenario 3: Updating Record with Existing Breeding and Calving Dates
        if (selectedReproductiveVariable != null && selectedReproductiveVariable.getCalvingDate() != null && calvedDate != null) {
            boolean breedingChanged = !selectedReproductiveVariable.getBreedingDate().equals(breedingDate);
            boolean calvingChanged = !selectedReproductiveVariable.getCalvingDate().equals(calvedDate);

            if (breedingChanged || calvingChanged) {
                boolean needToAddCattleRecord = calvingChanged && !cattleRecordExistsForCalvingDate();

                if (needToAddCattleRecord) {
                    handleAddCalfToHerd();
                } else {
                    selectedReproductiveVariable.setGestationPeriod(gestationPeriod);
                    if (breedingChanged) {
                        handleBreedingDateChange();
                    }
                    if (calvingChanged) {
                        handleCalvingDateChange();
                    }
                }
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Information", "No changes detected.");
            }
        }
    }

    private void handleUpdateFailure() {
        showAlert(Alert.AlertType.ERROR, "Error", "Failed to update reproductive data.");
        AppLogger.error("Failed to update reproductive data.");
    }

    private void handleBreedingDateChange() {
        selectedReproductiveVariable.setBreedingDate(lastBreedingDate.getValue());
        boolean success = reproductiveVariablesDAO.updateReproductiveVariable(selectedReproductiveVariable);
        if (success) {
            clearReproductiveData();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Breeding date updated successfully.");
            loadCalvingHistoryForCattle(selectedCattleId);
        } else {
            handleUpdateFailure();
        }
    }

    private void handleCalvingDateChange() {
        CattleDAO cattleDAO = new CattleDAO();
        LocalDate originalDateOfBirth = cattleDAO.fetchDateOfBirth(selectedReproductiveVariable.getCattleID());
        selectedReproductiveVariable.setCalvingDate(calvingDate.getValue());
        boolean dateOfBirthUpdated = CattleDAO.updateCattleDateOfBirth(selectedReproductiveVariable.getCattleID(), calvingDate.getValue());
        boolean updatedLactationPeriodStartDate;
        if (dateOfBirthUpdated) {
            boolean reproductiveVariableUpdated = reproductiveVariablesDAO.updateReproductiveVariable(selectedReproductiveVariable);
            try {
                int lactationPeriodID = LactationPeriodDAO.getLactationIdByCattleIdAndStartDate(selectedCattleId, originalDateOfBirth);
                updatedLactationPeriodStartDate = LactationPeriodDAO.updateLactationPeriodStartDate(lactationPeriodID, calvingDate.getValue());

                if (updatedLactationPeriodStartDate) {
                    // Update production session dates
                    boolean updateLactationSession =ProductionSessionDAO.updateSessionDates(selectedReproductiveVariable.getCattleID(), lactationPeriodID, calvingDate.getValue());
                if(updateLactationSession){
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Cattle date of birth, Reproductive data, Lactation Start Date, and Production Session Dates updated successfully.");

                }


                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            if (reproductiveVariableUpdated && updatedLactationPeriodStartDate) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Cattle date of birth, Reproductive data, Lactation Start Date, and Production Session Dates updated successfully.");
                loadCalvingHistoryForCattle(selectedCattleId);
            } else {
                handleDateOfBirthUpdateFailure(originalDateOfBirth);
            }
        }
    }


    private void handleDateOfBirthUpdateFailure(LocalDate originalDateOfBirth) {
        boolean dateOfBirthReverted = CattleDAO.updateCattleDateOfBirth(selectedReproductiveVariable.getCattleID(), originalDateOfBirth);
        if (dateOfBirthReverted) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update cattle's date of birth and Reproductive data.");
            AppLogger.error("Failed to update cattle's date of birth.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to revert date of birth change.");
            AppLogger.error("Failed to revert date of birth change.");
        }
    }


    private boolean cattleRecordExistsForCalvingDate() {
        Cattle cattleToUpdate;
        try {
            cattleToUpdate = CattleDAO.findCattleByBirthdateAndDamId(selectedReproductiveVariable.getCalvingDate(), selectedCattleId);
            return cattleToUpdate != null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void openAddCattleFormSaveCattleAndUpdateUpdateReproductiveData(String selectedHerdName, LocalDate dateOfBirth, LocalDate breedingDate, int gestationPeriod) {
        try {
            Herd selectedHerd = HerdDAO.getHerdByName(selectedHerdName);
            if (selectedHerd != null) {
                openForm("/com/example/hashinfarm/homePanels/homeCenterPanelViews/cattleManagement/centerLeftViews/addCattle.fxml",
                        "Add Cattle to Herd",
                        pair -> {
                            FXMLLoader loader = pair.getValue();

                            addNewCattleController = loader.getController();
                            addNewCattleController.initData(selectedHerd.getId(), selectedCattleId, dateOfBirth);

                            addNewCattleController.setCattleAdditionCallback((successFlag, failureReason, newCattleId) -> {
                                if (successFlag) {
                                    showAlert(Alert.AlertType.INFORMATION, "Success", "Cattle added successfully.");
                                    // Add reproductive variables here
                                    ReproductiveVariables reproductiveVariables = getReproductiveVariables(breedingDate, gestationPeriod);
                                    boolean reproductiveSuccess = reproductiveVariablesDAO.updateReproductiveVariable(reproductiveVariables);

                                    if (reproductiveSuccess) {
                                        clearReproductiveData();
                                        showAlert(Alert.AlertType.INFORMATION, "Success", "Cattle and Reproductive data added and updated successfully.");
                                        loadCalvingHistoryForCattle(selectedCattleId);


                                    } else {

                                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to save Cattle and Reproductive data.");
                                        CattleDAO.deleteCattleById(newCattleId);
                                    }
                                } else {
                                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to add cattle: 3 " + failureReason);
                                }
                            });
                        });
            } else {
                throw new HerdNotFoundException("Selected herd not found.");
            }
        } catch (SQLException | HerdNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save data: " + e.getMessage());
        }
    }


    private void handleHerdRetrievalError(SQLException e) {
        AppLogger.error("Error retrieving herds", e);
        showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve herds. See logs for details.");
    }

    private void handleFormError(String title, Exception e) {
        AppLogger.error("Error opening form: " + title, e);
        showAlert(Alert.AlertType.ERROR, "Error", "Failed to load form: " + title + ". See logs for details.");
    }


    public void handleLastBreedingDateSelection() {
        LocalDate breedingDate = lastBreedingDate.getValue();
        if (breedingDate != null) {
            updatePregnancyStatus(breedingDate);
        } else {
            // No breeding date selected, clear the days in pregnancy label
            daysInPregnancyTextArea.setText("");
        }
    }

    private void createTableColumns() {
        // Construct table columns with descriptive names and data bindings
        TableColumn<ReproductiveVariables, Integer> identifierColumn = new TableColumn<>("No.");
        identifierColumn.setCellValueFactory(new PropertyValueFactory<>("ReproductiveVariableID"));
        identifierColumn.setPrefWidth(50);

        TableColumn<ReproductiveVariables, LocalDate> breedingDateColumn =
                new TableColumn<>("Breeding Date");
        breedingDateColumn.setCellValueFactory(new PropertyValueFactory<>("BreedingDate"));
        breedingDateColumn.setPrefWidth(120);

        TableColumn<ReproductiveVariables, LocalDate> calvingDateColumn =
                new TableColumn<>("Calving Date");
        calvingDateColumn.setCellValueFactory(new PropertyValueFactory<>("CalvingDate"));
        calvingDateColumn.setPrefWidth(120);

        TableColumn<ReproductiveVariables, Integer> calvingIntervalColumn =
                new TableColumn<>("Calving Interval");
        calvingIntervalColumn.setCellValueFactory(new PropertyValueFactory<>("CalvingInterval"));
        calvingIntervalColumn.setPrefWidth(92);

        TableColumn<ReproductiveVariables, Void> actionColumn =
                getReproductiveVariablesVoidTableColumn();

        // Assemble and apply columns to the table
        List<TableColumn<ReproductiveVariables, ?>> columns =
                Arrays.asList(
                        identifierColumn,
                        breedingDateColumn,
                        calvingDateColumn,
                        calvingIntervalColumn,
                        actionColumn);
        calvingHistoryTableView.getColumns().addAll(columns);

        // Set font size for consistent text display
        for (TableColumn<ReproductiveVariables, ?> column : columns) {
            column.setStyle("-fx-font-size: 10px;");
        }
    }

    @NotNull
    private TableColumn<ReproductiveVariables, Void> getReproductiveVariablesVoidTableColumn() {
        TableColumn<ReproductiveVariables, Void> actionColumn = new TableColumn<>("Action");
        actionColumn.setCellFactory(
                param ->
                        new TableCell<>() {

                            private final Button deleteButton = new Button("Delete");

                            {
                                deleteButton.setOnAction(
                                        event -> {
                                            ReproductiveVariables reproductiveVariables =
                                                    getTableView().getItems().get(getIndex());
                                            // Handle delete action
                                            handleDeletion(reproductiveVariables);
                                        });
                            }

                            @Override
                            protected void updateItem(Void item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                } else {
                                    VBox buttonsBox = new VBox(deleteButton);
                                    setGraphic(buttonsBox);
                                }
                            }
                        });
        return actionColumn;
    }

    private void loadCalvingHistoryForCattle(int cattleID) {
        // Retrieve reproductive variables data for the selected cattle
        List<ReproductiveVariables> calvingHistory =
                reproductiveVariablesDAO.getAllReproductiveVariablesForCattle(cattleID);

        // Clear existing data
        calvingHistoryTableView.getItems().clear();

        // Populate the table with the retrieved data
        calvingHistoryTableView.getItems().addAll(calvingHistory);
    }

    public void clearReproductiveData() {
        // Reset selected reproductive variable


        // Clear all fields and reset all values
        lastBreedingDate.setValue(null);
        calvingIntervalTextField.setText("");
        calvingDate.setValue(null);
        estimatedGestationSpinner.getValueFactory().setValue(283);
        clearTreeView();
        projectedCalvingDate.setText("");
        daysSinceCalvingLabel.setText("");
        targetCalvingAgeLabel.setText("");
        initializeLastBreedingDateDayCellFactory();
        selectedReproductiveVariable = null;
        saveButton.setDisable(true);
        updateButton.setDisable(true);
    }

    private void handleDeletion(ReproductiveVariables reproductiveVariables) {
        ReproductiveVariablesDAO reproductiveVariablesDAO = new ReproductiveVariablesDAO();

        // Confirmation prompt for deletion
        if (confirmAction()) {
            // Check and delete associated cattle if needed
            if (reproductiveVariables.getCalvingDate() != null) {
                if (!checkAndDeleteAssociatedCattle(reproductiveVariables.getCattleID(), reproductiveVariables.getCalvingDate())) {
                    // User canceled associated cattle deletion
                    return;
                }
            }

            // Delete the record from the database
            if (reproductiveVariablesDAO.deleteReproductiveVariable(reproductiveVariables.getReproductiveVariableID())) {
                // Handle success
                showSuccessAlert();
                loadCalvingHistoryForCattle(selectedCattleId);
                clearReproductiveData();
            } else {
                // Handle failure
                showFailureAlert();
            }
        }
    }

    private boolean checkAndDeleteAssociatedCattle(int selectedCattleId, LocalDate calvingDate) {
        try {
            Cattle cattle = CattleDAO.findCattleByBirthdateAndDamId(calvingDate, selectedCattleId);
            if (cattle != null) {
                // Prompt user about associated cattle deletion
                Alert cattleAlert = new Alert(Alert.AlertType.CONFIRMATION);
                cattleAlert.setTitle("Confirm Cattle Deletion");
                cattleAlert.setHeaderText("Cattle associated with this record will also be deleted. Continue?");
                cattleAlert.setContentText("Cattle ID: " + cattle.getCattleId() + " will be deleted.");

                Optional<ButtonType> result = cattleAlert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    // Delete associated cattle
                    CattleDAO.deleteCattleById(cattle.getCattleId());
                } else {
                    return false; // User canceled associated cattle deletion
                }
            }
        } catch (SQLException e) {
            // Handle SQL exception
            e.printStackTrace();
            return false;
        }
        return true; // No associated cattle or cattle deletion successful
    }


    // Helper method for showing confirmation dialogs
    private boolean confirmAction() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to delete the reproductive variable?");
        return confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    // Helper method for showing success/failure alerts
    private void showAlert(Alert.AlertType alertType, String title, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    // Helper method for success alerts (shortcut)
    private void showSuccessAlert() {
        showAlert(Alert.AlertType.INFORMATION, "Success", "Reproductive variable deleted successfully.");
    }

    // Helper method for failure alerts (shortcut)
    private void showFailureAlert() {
        showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete reproductive variable.");
    }

    private ReproductiveVariables getReproductiveVariables(LocalDate breedingDate, int gestationPeriod) {
        LocalDate calvingDateValue = calvingDate.getValue();
        Integer calvingInterval = null;
        String calvingIntervalText = calvingIntervalTextField.getText();
        if (calvingDateValue != null) {
            calvingInterval = Integer.parseInt(calvingIntervalText);

        }

        // Create a new ReproductiveVariables object
        ReproductiveVariables reproductiveVariables = new ReproductiveVariables();
        reproductiveVariables.setCattleID(selectedCattleId);
        reproductiveVariables.setBreedingDate(breedingDate);
        reproductiveVariables.setGestationPeriod(gestationPeriod);

        // Set calving date and interval only if provided
        if (calvingDateValue != null) {
            reproductiveVariables.setCalvingDate(calvingDateValue);
        }
        if (calvingInterval != null) {
            reproductiveVariables.setCalvingInterval(calvingInterval);
        }
        if (!saveButtonPressed) {
            reproductiveVariables.setReproductiveVariableID(selectedReproductiveVariable.getReproductiveVariableID());
        }
        return reproductiveVariables;
    }
private void initializeLactationPeriods(){
    if (selectedCattleId == 0) {
          lactationEndDatePicker.setDisable(true); // Disable the date picker if no cattle is selected
    }
    SelectedCattleManager.getInstance().selectedCattleIDProperty().addListener((observable, oldValue, newValue) -> {
        selectedCattleId = newValue != null ? newValue.intValue() : 0;
        clearLactationFieldsOnCattleIdChange();
        initializeTableColumns();
        initializeTableViewSelectionListener();
        loadLactationPeriodsForSelectedCattle();
        initializeLactationEndDatePicker();

    });
}

    private void updateStagePeriodLabel(String stage) {
        Integer[] stageRange = stageDaysRangeMap.get(stage);
        if (stageRange != null) {
            String periodLabel =
                    stageRange[0] + " - " + (stageRange[1] == -1 ? "∞" : stageRange[1]) + " days";
            stagePeriodLabel.setText(periodLabel);
        } else {
            stagePeriodLabel.setText("N/A");
        }
    }

    private String calculateProductionStage(int daysInLactation) {
        Map<Integer, String> stages = new HashMap<>();
        stages.put(0, "Colostrum Stage");
        stages.put(5, "Transition Stage");
        stages.put(15, "Peak Milk Harvesting");
        stages.put(60, "Mid-Lactation");
        stages.put(150, "Late Lactation");
        // Dry Period is the default case if no match is found
        return stages.getOrDefault(daysInLactation, "Dry Period");
    }


    private String getVolumeLabelText(String stage) {
        return switch (stage) {
            case "Colostrum Stage" -> "Colostrum Volume ";
            case "Transition Stage" -> "Transitional Volume ";
            case "Peak Milk Harvesting" -> "Peak Volume ";
            case "Mid-Lactation" -> "Regular Volume ";
            case "Late Lactation" -> "Decreasing Milk Harvesting Volume ";
            case "Dry Period" -> "Dry Period Volume ";
            default -> "";
        };
    }


    private void initializeTableColumns() {
        selectionColumn.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        selectionColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectionColumn));

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        startDateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                cellData.getValue().getLactationPeriod().getStartDate()));
        startDateColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.format(dateFormatter));
            }
        });

        endDateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                cellData.getValue().getLactationPeriod().getEndDate() != null
                        ? cellData.getValue().getLactationPeriod().getEndDate()
                        : null));

        endDateColumn.setCellFactory(new MissingEndDateCellFactory(dateFormatter));

        actionColumn.setCellFactory(createActionCellFactory());
    }

    private Callback<TableColumn<LactationPeriodWithSelection, Void>, TableCell<LactationPeriodWithSelection, Void>> createActionCellFactory() {
        return param -> new TableCell<>() {
            private final Button removeButton = new Button("Remove");

            private final HBox pane = new HBox(removeButton);

            {
                pane.setAlignment(Pos.CENTER); // Aligns buttons to the center horizontally

                removeButton.setOnAction(event -> {
                    LactationPeriodWithSelection data = getTableView().getItems().get(getIndex());
                    showDeleteConfirmationDialog(data, getTableView());
                });


            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        };
    }
    private void showDeleteConfirmationDialog(LactationPeriodWithSelection data, TableView<LactationPeriodWithSelection> tableView) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Lactation Period");
        alert.setContentText("Are you sure you want to delete this lactation period?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // User chose OK, delete the record
            try {
                LactationPeriodDAO.deleteLactationPeriod(data.getLactationPeriod().getLactationPeriodID());
                tableView.getItems().remove(data);
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle the error appropriately (show error message to user, etc.)
            }
            // Refresh the table view
            tableView.refresh();
        } else {
            // User chose CANCEL or closed the dialog, do nothing
            alert.close(); // Close the dialog
        }
    }



    private void loadLactationPeriodsForSelectedCattle() {
        if (selectedCattleId == 0) {
            lactationTableView.getItems().clear();
            return;
        }

        try {
            // Clear the current table items
            lactationTableView.getItems().clear();

            List<LactationPeriod> lactationPeriods = LactationPeriodDAO.getLactationPeriodsByCattleId(selectedCattleId);

            // Sort lactation periods by start date (most recent first)
            lactationPeriods.sort(Comparator.comparing(LactationPeriod::getStartDate).reversed());

            LocalDate currentDate = LocalDate.now();

            // Populate table with lactation periods
            ObservableList<LactationPeriodWithSelection> observableList = FXCollections.observableArrayList(
                    lactationPeriods.stream()
                            .map(LactationPeriodWithSelection::new)
                            .collect(Collectors.toList())
            );
            lactationTableView.setItems(observableList);
            // Clear or set the date picker to null if there are no records, then disable it
            if (lactationPeriods.isEmpty()) {
                lactationEndDatePicker.setValue(null);
                lactationEndDatePicker.setDisable(true);
            } else {
                lactationEndDatePicker.setDisable(false);
            }


            // If lactation periods exist, select the first (most recent) one
            if (!lactationPeriods.isEmpty()) {
                LactationPeriod mostRecentPeriod = lactationPeriods.getFirst();
                lactationTableView.getSelectionModel().select(0);
                LactationPeriodWithSelection selectedPeriod = lactationTableView.getSelectionModel().getSelectedItem();
                populateLactationFields(selectedPeriod);
                if (mostRecentPeriod.getEndDate() == null) {
                    long daysSinceStart = ChronoUnit.DAYS.between(mostRecentPeriod.getStartDate(), currentDate);
                    if (daysSinceStart <= 365) {
                        // Set the label text to "start date - now"
                        currentPeriodLabel.setText(mostRecentPeriod.getStartDate() + " - now");

                    }
                }
            } else {
                // Clear lactation information fields if no periods exist
                populateLactationFields(null);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database error
        }
    }


    private void populateLactationFields(LactationPeriodWithSelection selectedPeriod) {
        if (selectedPeriod != null) {
            LactationPeriod period = selectedPeriod.getLactationPeriod();
            lactationStartDateField.setText(String.valueOf(period.getStartDate()));
            lactationEndDatePicker.setValue(period.getEndDate() != null ? period.getEndDate() : null);
            originalEndDate = period.getEndDate(); // Set the original end date
            selectedEndDate = originalEndDate; // Initially, selected end date is the same as original
            milkYieldLabel.setText(String.valueOf(period.getMilkYield()));
            relativeMilkYieldLabel.setText(String.valueOf(period.getRelativeMilkYield()));
            if(lactationEndDatePicker.getValue() == null){
                clearButton.setDisable(true);
            }
        } else {
            lactationStartDateField.clear();
            originalEndDate = null;
            selectedEndDate = null;
            milkYieldLabel.setText("");
            relativeMilkYieldLabel.setText("");
        }
        modifyLactationButton.setDisable(true);
    }




    private void initializeTableViewSelectionListener() {
        lactationTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedPeriodWithSelection = newValue;
            populateLactationFields(newValue);
            List<LactationPeriod> lactationPeriods;
            try {
                // Retrieve all lactation periods for the selected cattle
                lactationPeriods = LactationPeriodDAO.getLactationPeriodsByCattleId(selectedCattleId);

                LocalDate currentDate = LocalDate.now();
                ongoingLactationPeriod = null;

                // Sort lactation periods by start date in descending order
                lactationPeriods.sort(Comparator.comparing(LactationPeriod::getStartDate).reversed());

                // Find the most recent lactation period
                Optional<LactationPeriod> mostRecentPeriod = lactationPeriods.stream().findFirst();

                mostRecentPeriod.ifPresent(period -> {
                    // Check if the most recent lactation period is within 365 days of the current date
                    if (ChronoUnit.DAYS.between(period.getStartDate(), currentDate) <= 365) {
                        ongoingLactationPeriod = period;

                        if (selectedPeriodWithSelection != null && selectedPeriodWithSelection.getLactationPeriod() != null) {
                            LocalDate selectedStartDate = selectedPeriodWithSelection.getLactationPeriod().getStartDate();

                            // Check if the selected period matches the ongoing period
                            if (selectedStartDate.equals(ongoingLactationPeriod.getStartDate())) {
                                LocalDate startDate = ongoingLactationPeriod.getStartDate();
                                long totalDays = Duration.between(startDate.atStartOfDay(), currentDate.atStartOfDay()).toDays();
                                lactationPeriodLabel.setText(totalDays + " days");

                                // Calculate the production stage based on total days
                                String stage = calculateProductionStage((int) totalDays);
                                Integer[] stageRange = stageDaysRangeMap.get(stage);
                                int daysInStage = (int) totalDays - stageRange[0];
                                int stageDuration = stageRange[1] - stageRange[0];
                                String progressText = daysInStage + " out of " + stageDuration;

                                daysInLactationTextField.setText(progressText);
                                currentProductionStageLabel.setText(stage);
                                productionStageLabel.setText(stage);

                                // Update UI elements based on the production stage
                                updateVolumeLabels(stage);
                                updateStagePeriodLabel(stage);

                                // If the selected period is ongoing, enable fields
                                setFieldsDisabled(false);
                            } else {
                                // If the selected period is not ongoing, disable fields
                                setFieldsDisabled(true);
                            }
                        }
                    }
                });

                // If no ongoing period is found, disable fields
                if (ongoingLactationPeriod == null) {
                    setFieldsDisabled(true);
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }



    private void clearLactationFieldsOnCattleIdChange() {
        // Clear the fields updated when the selected period matches the ongoing period
        lactationPeriodLabel.setText("N/A");
        daysInLactationTextField.clear();
        currentProductionStageLabel.setText("N/A");
        productionStageLabel.setText("Production Stage");
        currentPeriodLabel.setText("N/A");
        stagePeriodLabel.setText("N/A");
        updateVolumeLabels("");
    }



    private void initializeLactationEndDatePicker() {
        // Set the initial allowed date range
        lactationEndDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty) {
                    setDisable(true);
                    return;
                }
                LactationPeriodWithSelection selectedPeriod = lactationTableView.getSelectionModel().getSelectedItem();
                LocalDate minDate = selectedPeriod.getLactationPeriod().getStartDate();
                LocalDate maxDate = calculateMaxEndDate(selectedPeriod);
                LocalDate today = LocalDate.now();

                boolean isBeforeMinDate = date.isBefore(minDate);
                boolean isAfterMaxDate = date.isAfter(maxDate);
                boolean isFutureDate = date.isAfter(today);

                setDisable(isBeforeMinDate || isAfterMaxDate || isFutureDate);
                if (isBeforeMinDate || isAfterMaxDate || isFutureDate) {
                    setStyle("-fx-background-color: #ffe6e6;");
                }
            }
        });

        // listener to track changes in the DatePicker
        lactationEndDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> handleDatePickerChange(newValue));

        modifyLactationButton.setDisable(true);
    }

    private LocalDate calculateMaxEndDate(LactationPeriodWithSelection selectedPeriod) {
        List<LactationPeriodWithSelection> lactationPeriodsWithSelection = lactationTableView.getItems();

        // Sort lactation periods by start date (most recent first)
        List<LactationPeriod> lactationPeriods = lactationPeriodsWithSelection.stream()
                .map(LactationPeriodWithSelection::getLactationPeriod)
                .sorted(Comparator.comparing(LactationPeriod::getStartDate).reversed())
                .toList();

        LocalDate selectedStartDate = selectedPeriod.getLactationPeriod().getStartDate();
        LocalDate maxEndDate;

        int selectedIndex = lactationPeriods.indexOf(selectedPeriod.getLactationPeriod());

        if (selectedIndex == 0 || lactationPeriods.size() == 1) {
            // If the selected period is the most recent or there's only one period, maximum end date is one year after start date
            maxEndDate = selectedStartDate.plusYears(1);
        } else {
            // If there are multiple periods and the selected one is not the most recent
            LactationPeriod nextPeriod = lactationPeriods.get(selectedIndex - 1);
            LocalDate nextStartDate = nextPeriod.getStartDate();

            // Calculate one year after selected start date
            LocalDate oneYearAfterStartDate = selectedStartDate.plusYears(1);

            // If one year from the selected start date comes sooner than the next period's start date
            if (oneYearAfterStartDate.isBefore(nextStartDate)) {
                maxEndDate = oneYearAfterStartDate;
            } else {
                // If the next period's start date is closer than one year from the selected start date
                maxEndDate = nextStartDate.minusDays(1);
            }
        }

        return maxEndDate;
    }

    private void handleDatePickerChange(LocalDate newValue) {
        clearButton.setDisable(newValue == null);
        modifyLactationButton.setDisable(newValue == null);
        selectedEndDate = newValue;
    }


    public void modifyLactation() {
        LactationPeriodWithSelection selectedPeriodWithSelection = lactationTableView.getSelectionModel().getSelectedItem();

        if (selectedPeriodWithSelection == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "No Lactation Period Selected", "Please select a lactation period to modify.");
            return;
        }

        LactationPeriod selectedPeriod = selectedPeriodWithSelection.getLactationPeriod();
        LocalDate newEndDate = lactationEndDatePicker.getValue();

        // Validate the end date
        if (newEndDate != null && newEndDate.isBefore(selectedPeriod.getStartDate())) {
            showAlert(Alert.AlertType.ERROR, "Invalid Date", "End Date Before Start Date", "The end date cannot be before the start date.");
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setHeaderText("Confirm Modification");
        confirmationAlert.setContentText("Are you sure you want to modify the end date for the selected lactation period?");

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Update the lactation period object with the new end date
                selectedPeriod.setEndDate(newEndDate);

                // Call DAO to update the record in the database
                LactationPeriodDAO.updateLactationPeriodEndDate(selectedPeriod.getLactationPeriodID(), newEndDate);
                lactationTableView.refresh(); // Refresh the table to show updated data

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", "Could Not Update Lactation Period", "An error occurred while updating the lactation period. Please try again.");
            }
        } else {
            // User cancelled the modification
            confirmationAlert.close();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
    public void clearEndDate() {
        if (selectedEndDate == null) {
            // Scenario 3: No changes, original end date displayed
            return;
        }

        if (originalEndDate == null) {
            // Scenario 1: New, unsaved value (original was null)
            lactationEndDatePicker.setValue(null);
            showAlert(Alert.AlertType.INFORMATION, "Cleared end date", "The end date has been cleared.");
        } else {
            // Scenario 2: Modified, unsaved value
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Clear Lactation End Date");
            confirmationAlert.setHeaderText("Are you sure you want to:");
            confirmationAlert.setContentText("Choose an option:");

            ButtonType restoreButton = new ButtonType("Restore Original End Date");
            ButtonType clearButton = new ButtonType("Clear Selected End Date");
            confirmationAlert.getButtonTypes().setAll(restoreButton, clearButton, ButtonType.CANCEL);

            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == restoreButton) {
                    // Restore the original end date
                    lactationEndDatePicker.setValue(originalEndDate);
                    showAlert(Alert.AlertType.INFORMATION, "Restored end date", "The original end date has been restored.");
                } else if (result.get() == clearButton) {
                    // Clear the end date and update the database
                    lactationEndDatePicker.setValue(null);
                    try {
                        LactationPeriodDAO.updateLactationPeriodEndDate(selectedPeriodWithSelection.getLactationPeriod().getLactationPeriodID(), null);
                        showAlert(Alert.AlertType.INFORMATION, "Cleared end date", "The end date has been cleared and updated in the database.");

                        loadLactationPeriodsForSelectedCattle();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Database Error", "Could not update lactation period", "An error occurred while updating the lactation period. Please try again.");
                    }
                }
            }
        }

        // Reset clear button state
        modifyLactationButton.setDisable(true);
    }



    private void initializeVolumeLabels() {
        volumeLabels.put("Colostrum Stage", new Label[]{volumeLabel1, volumeLabel2, volumeLabel3});
        volumeLabels.put("Transition Stage", new Label[]{volumeLabel1, volumeLabel2, volumeLabel3});
        volumeLabels.put("Peak Milk Harvesting", new Label[]{volumeLabel1, volumeLabel2, volumeLabel3});
        volumeLabels.put("Mid-Lactation", new Label[]{volumeLabel1, volumeLabel2, volumeLabel3});
        volumeLabels.put("Late Lactation", new Label[]{volumeLabel1, volumeLabel2, volumeLabel3});
        volumeLabels.put("Dry Period", new Label[]{volumeLabel1, volumeLabel2, volumeLabel3});
    }

    private void initializeStageDaysRangeMap() {
        stageDaysRangeMap.put("Colostrum Stage", new Integer[]{0, 5});
        stageDaysRangeMap.put("Transition Stage", new Integer[]{6, 15});
        stageDaysRangeMap.put("Peak Milk Harvesting", new Integer[]{16, 60});
        stageDaysRangeMap.put("Mid-Lactation", new Integer[]{61, 150});
        stageDaysRangeMap.put("Late Lactation", new Integer[]{151, 305});
        stageDaysRangeMap.put("Dry Period", new Integer[]{306, -1});
    }




    private void updateVolumeLabels(String stage) {

        Label[] volumeLabelsArray = volumeLabels.get(stage);

        if (volumeLabelsArray != null) {
            String volumeLabelText = getVolumeLabelText(stage);

            volumeLabelsArray[0].setText(volumeLabelText + " : (Morning)");
            volumeLabelsArray[1].setText(volumeLabelText + " : (Afternoon)");
            volumeLabelsArray[2].setText(volumeLabelText + " : (Evening)");



        } else{
            volumeLabel1.setText("Unknown Stage: (Morning)");
            volumeLabel2.setText("Unknown Stage: (Afternoon)");
            volumeLabel3.setText("Unknown Stage: (Evening)");
        }
    }
    public void initializeProductionStages() {
        // Initially, enable all fields
        setFieldsDisabled(true);

        /* Listen for changes in the selected production stage label */
        productionStageLabel.textProperty().addListener((observable, oldValue, newValue) -> setFieldsDisabled(newValue == null || (!newValue.equals("Colostrum Stage")
                && !newValue.equals("Transition Stage")
                && !newValue.equals("Peak Milk Harvesting")
                && !newValue.equals("Mid-Lactation")
                && !newValue.equals("Late Lactation")
                && !newValue.equals("Dry Period"))));

    }

    private void setFieldsDisabled(boolean disabled) {
        for (Node node : gridPaneProduction.getChildren()) {
            node.setDisable(disabled);
        }
        saveUpdateProduction.setDisable(disabled); // Disable the button
    }

    public void initializeSpinners() {
        // Create individual DoubleSpinnerValueFactory for each spinner
        SpinnerValueFactory<Double> morningValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 40.0, 0.01);
        SpinnerValueFactory<Double> afternoonValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 40.0, 0.01);
        SpinnerValueFactory<Double> eveningValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 40.0, 0.01);

        // Set initial values for each spinner
        morningValueFactory.setValue(0.0);
        afternoonValueFactory.setValue(12.0);
        eveningValueFactory.setValue(20.0);

        // Set the value factory for each spinner
        spinnerMorning.setValueFactory(morningValueFactory);
        spinnerAfternoon.setValueFactory(afternoonValueFactory);
        spinnerEvening.setValueFactory(eveningValueFactory);
    }

    public void handleSaveOrUpdateProduction() {
    }

    public void addHealthAlertOrNotes() {
    }

    public void updateHealthAlertOrNotes() {
    }

    public void deleteHealthAlertOrNotes() {
    }

    public void selectStartDate() {
    }

    public void selectEndDate() {
    }

    public void compareCattleProduction() {
    }




    public static class HerdNotFoundException extends Exception {
        public HerdNotFoundException(String message) {
            super(message);
        }
    }

}
