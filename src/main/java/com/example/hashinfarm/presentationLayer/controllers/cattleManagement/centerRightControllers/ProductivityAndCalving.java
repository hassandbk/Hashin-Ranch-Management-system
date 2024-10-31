package com.example.hashinfarm.presentationLayer.controllers.cattleManagement.centerRightControllers;

import com.example.hashinfarm.app.DatabaseConnection;
import com.example.hashinfarm.businessLogic.services.SelectedCattleManager;
import com.example.hashinfarm.data.DAOs.*;

import com.example.hashinfarm.data.DTOs.records.*;
import com.example.hashinfarm.data.DTOs.*;

import com.example.hashinfarm.helpers.CustomTreeCell;
import com.example.hashinfarm.presentationLayer.controllers.CattleController;
import com.example.hashinfarm.presentationLayer.controllers.cattleManagement.centerLeftControllers.AddNewCattleController;
import com.example.hashinfarm.utils.CattleDialogUtils;
import com.example.hashinfarm.utils.SplitPaneDividerEnforcer;
import com.example.hashinfarm.utils.TableColumnUtils;
import com.example.hashinfarm.utils.date.DateUtil;
import com.example.hashinfarm.utils.date.TimePickerController;
import com.example.hashinfarm.utils.logging.AppLogger;
import com.example.hashinfarm.utils.validation.MissingEndDateCellFactory;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.*;

import javafx.util.Callback;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.sql.*;
import java.text.DecimalFormat;
import java.time.*;
import java.time.format.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import javafx.scene.input.MouseEvent;


@SuppressWarnings("CallToPrintStackTrace")
public class ProductivityAndCalving {

    // Constants
    private final ReproductiveVariablesDAO reproductiveVariablesDAO = new ReproductiveVariablesDAO();
    private final int minGestationDays = 265;
    private final int maxGestationDays = 295;
    private static final DatabaseConnection dbConnection = DatabaseConnection.getInstance();
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");
    private final double minPosition = 0.1;
    private final double maxPosition = 0.5;

    // Maps
    private final Map<String, Label[]> volumeLabels = new HashMap<>();
    private final Map<String, Integer[]> stageDaysRangeMap = new HashMap<>();
    private final Map<String, StageDetails> stageDetailsMap = new HashMap<>();
    private final Map<String, String> initialValues = new HashMap<>();

    private final Map<String, String> initialValuesOffspring = new HashMap<>();
    private final double maxPosition2 = 0.7;
    private static final int MATURITY_AGE_MONTHS = 14; // Assuming cattle maturity age of 14 months
    private static final int POST_CALVING_INTERVAL = 45;
    private final BooleanProperty addingReproductiveVariables = new SimpleBooleanProperty(false);


    // Other Fields
    private int selectedCattleId = 0;
    private LocalDate selectedCattleDateOfBirth;
    private ReproductiveVariables selectedReproductiveVariable = null;
    private LocalDate originalEndDate;
    private LocalDate selectedEndDate;
    private LocalDate selectedDateProductionSessionDate;

    private CattleDAO cattleDAO;
    private AddNewCattleController addNewCattleController;
    private Stage selectionStage;
    private LactationPeriodWithSelection selectedPeriodWithSelection;
    private LactationPeriod ongoingLactationPeriod;
    private Map<Button, TextField> buttonTextFieldMap;


    private Popup breedingDatePopup;
    private String previouslySelectedBreedingDate;
    private ChangeListener<LocalDate> estrusDateChangeListener;


    // TableView and Columns
    @FXML private TableView<LactationPeriodWithSelection> lactationTableView;
    @FXML private TableColumn<LactationPeriodWithSelection, Boolean> selectionColumn;
    @FXML private TableColumn<LactationPeriodWithSelection, LocalDate> startDateColumn,endDateColumn;

    @FXML private TableView<CowTableItem> cowTableView;
    @FXML private TableColumn<CowTableItem, String> cowIdColumn, currentStageColumn, selectedStageByDateColumn, equivalentSelectedDateColumn, selectedCattlePRColumn, comparisonPerformanceColumn;
    @FXML private TableColumn<CowTableItem, Double> todayMYColumn, equivalentDayMYColumn, currentStageMilkMYColumn, selectedStageMilkMYColumn, totalDailyMYColumn, averageDailyMYColumn, relativeMYColumn;


    // Buttons for Operations
    @FXML private boolean saveButtonPressed = true;
    @FXML private Button breedingDateButton,saveButton, updateButton, modifyLactationButton, clearButton;
    @FXML private Button morningSessionButton, afternoonSessionButton, eveningSessionButton,deleteMorningSession,deleteAfternoonSession,deleteEveningSession;
    @FXML private Button morningStartTimeButton, morningEndTimeButton, afternoonStartTimeButton, afternoonEndTimeButton, eveningStartTimeButton, eveningEndTimeButton;
    @FXML private Button leftArrowButton,rightArrowButton,showChartButton;

    // Spinners and Radio Buttons
    @FXML private Spinner<Integer> estimatedGestationSpinner;
    @FXML private Spinner<Double> spinnerMorning, spinnerAfternoon, spinnerEvening;
    @FXML private RadioButton pregnantRadioBtn, notPregnantRadioBtn;

    // TextArea, VBox, Labels, and TextFields
    @FXML private Label productionStageLabel, volumeLabel1, volumeLabel2, volumeLabel3, lactationPeriodLabel, stagePeriodLabel, currentProductionStageLabel, daysSinceCalvingLabel, averageMilkYieldLabel, milkYieldLabel, targetCalvingAgeLabel, ageAtFirstCalvingLabel, projectedCalvingDate, currentPeriodLabel, ongoingLactationPeriodLabel;
    @FXML private Label selectedCattleIDLabel, currentStageLabel, selectedDateLabel, stageMYLabel, selectedDateMYLabel, totalDMYLabel, averageDMYLabel, selectedStageLabel,selectedCowRMY,selectedCowPR;
    @FXML private TextArea daysInPregnancyTextArea;
    @FXML private TextField selectedBreedingDateTextField,daysInLactationTextField, calvingIntervalTextField, currentDateTextField, lactationStartDateField;
    @FXML private TextField morningStartTimeTextField, morningEndTimeTextField, afternoonStartTimeTextField, afternoonEndTimeTextField, eveningStartTimeTextField, eveningEndTimeTextField;
    @FXML private ComboBox<String> morningQualityScore, afternoonQualityScore, eveningQualityScore;

    // Toggle Groups, DatePickers, and TableView
    @FXML private ToggleGroup pregnancyStatus;
    @FXML private DatePicker lactationEndDatePicker, calvingDate,  productionSessionDatePicker;
    @FXML private TableView<ReproductiveVariables> calvingHistoryTableView;
    @FXML private TreeView<String> stageOfPregnancy;
    @FXML private GridPane gridPaneProduction;
    @FXML private ChoiceBox<String> selectCriteriaChoiceBox,dependentCriteriaChoiceBox;
    @FXML private SplitPane splitPane;
    @FXML private VBox comparisonChartVBox;


    // FXML elements for Cattle offspring Information
    @FXML private Label  offspringIdLabel, sireIdOrDamIdLabel;
    @FXML private TextField  birthWeightTextField,dateOfBirthTextField;
    @FXML private ComboBox<String>  breedingMethodComboBox,  offspringGenderComboBox, intendedUseComboBox;
    @FXML private Slider easeOfCalvingSlider;
    @FXML private TextField sireIdOrDamIdTextField, measuredWeightTextField;
    @FXML private DatePicker lastDateWeightTakenDatePicker;
    @FXML private TableView<UnifiedOffspring> cattleTableView;
    @FXML private TableColumn<UnifiedOffspring, String> offspringIdColumn, cattleIdColumn, cattleNameColumn, genderColumn, breedingMethodColumn;
    @FXML private Button modifyOffspringDetailsButton, updateOffSpringDetailsButton;
    @FXML private Spinner<Integer> estimatedGestationSpinner2;

    // FXML elements for Breeding Attempts
    @FXML private TableView<BreedingAttempt> breedingAttemptsTableView;
    @FXML private TableColumn<BreedingAttempt, Integer> breedingAttemptIdColumn;
    @FXML private TableColumn<BreedingAttempt, String> estrusDateColumn, breedingMethodBreedingAttemptColumn, sireUsedColumn, attemptDateColumn, attemptStatusColumn;
    @FXML private Label breedingAttemptIdLabel, sireNameLabel;
    @FXML private TextField attemptNumberTextField;
    @FXML private DatePicker estrusDatePicker, attemptDatePicker;
    @FXML private ComboBox<String> breedingMethodBreedingAttemptComboBox, attemptStatusComboBox;
    @FXML private TextArea notesTextArea;
    @FXML private Button modifyBreedingAttemptButton, updateBreedingAttemptButton, sireNameButton;
    @FXML private HBox estimatedGestationPeriodHBox;
    @FXML private Spinner<Integer> estimatedGestationPeriodSpinner;
    // SplitPane and Navigation Buttons
    @FXML private SplitPane splitPaneOffSpringInfo, splitPaneBreedAttempts;
    @FXML private Button leftArrowButtonOffSpringInfo, rightArrowButtonOffSpringInfo;
    @FXML private Button leftArrowButtonBreedAttempts, rightArrowButtonBreedAttempts;




    public void initialize() {

        initializeBreedingDatePopUp();
        initializeCattleDAO();
        initializeCalvingHistory();
        initializeSelectedCattleManagerListeners();
        initializeGestationPeriod();
        initializeCalvingDateListener();
        initializePregnancyStatusListener();

        initializeCalvingHistoryTableViewListener();
        setDatesAndDateFormats();
        initializeCalvingDateDayCellFactory();
        initializeLactationPeriods();
        initializeVolumeLabels();
        initializeStageDaysRangeMap();
        initializeStageDetailsMap();
        updateLactationStartDateField();
        initializeProductionStages();

        initializeShowTimePickerButtons();
        initializeSpinners();
        initializeAllHeartbeats();
        initializeFieldChangeListeners();
        initializeEndTimeButtonListeners();
        initializeSplitPlane();
        initializeProductionData();
        initializeColumnCellValueFactoriesForProductionData();

        initializeProductionSessionDatePicker();
        initializeShowChartButtonState();


        //offspring and breedAttempt titled panes
        initializeButtons();
        initializeSplitPlane(splitPaneOffSpringInfo, leftArrowButtonOffSpringInfo, rightArrowButtonOffSpringInfo);
        initializeSplitPlane(splitPaneBreedAttempts, leftArrowButtonBreedAttempts, rightArrowButtonBreedAttempts);
        initSelectedCattleListeners();
        initializeOffspringAndBreedingAttemptTableColumns();
        setupOffspringTableSelectionListener();
        setupBreedingAttemptsTableSelectionListener();
        addFieldChangeListenersForOffspring();
        addFieldChangeListenersForBreedingAttempt();
        breedingMethodBreedingAttemptComboBox.setItems(FXCollections.observableArrayList("Natural Mating", "Artificial Insemination", "Embryo Transfer"));
        attemptStatusComboBox.setItems(FXCollections.observableArrayList("Success", "Failure", "Unknown"));
    }

    private void initializeBreedingDatePopUp() {
        breedingDatePopup = new Popup();
        breedingDatePopup.setAutoHide(true);

        breedingDateButton.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                addOutsideClickHandler(newScene);
            }
        });

        breedingDatePopup.setOnShown(event -> {
            // Check if content is not empty and is an instance of ListView
            if (!breedingDatePopup.getContent().isEmpty() && breedingDatePopup.getContent().getFirst() instanceof ListView) {
                @SuppressWarnings("unchecked")
                ListView<String> breedingDateListView = (ListView<String>) breedingDatePopup.getContent().getFirst();
                breedingDateListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        handleBreedingDateSelection(newValue);
                        breedingDatePopup.hide();
                    }
                });
            } else {
                // Handle the case where the content is not as expected
                System.err.println("Unexpected content in popup");
            }
        });
    }

    private void initializeCalvingHistoryTableViewListener() {
        calvingHistoryTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        calvingHistoryTableView
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        updateFieldsWithSelectedRecord(newValue);
                        saveButton.setDisable(true);
                        updateButton.setDisable(true);
                    }
                });

    }



    private void updateFieldsWithSelectedRecord(ReproductiveVariables record) {

        selectedBreedingDateTextField.setText(record.getBreedingDate().toString());

        calvingDate.setValue(record.getCalvingDate());

        Integer calvingIntervalValue = record.getCalvingInterval();
        calvingIntervalTextField.setText(calvingIntervalValue > 0 ? String.valueOf(calvingIntervalValue) : "");
        estimatedGestationSpinner.getValueFactory().setValue(record.getGestationPeriod());

        if (record.equals(calvingHistoryTableView.getItems().getLast()) && record.getCalvingDate() == null) {
            populateTreeView();
            // Calculate days since breeding
            long daysSinceBreeding = ChronoUnit.DAYS.between(record.getBreedingDate(), LocalDate.now());
            // Update the TreeView to highlight the current stage of pregnancy
            highlightSubStages((int) daysSinceBreeding);
        }
        selectedReproductiveVariable = record;
    }

    private void initializeCattleDAO() {
        cattleDAO = new CattleDAO();
    }

    private void setDatesAndDateFormats() {
        setDatePickerFormat(lactationEndDatePicker);
        setDatePickerFormat(calvingDate);
        setDatePickerFormat(productionSessionDatePicker);
        setDatePickerFormat(estrusDatePicker);
        setDatePickerFormat(attemptDatePicker);
        calvingDate.valueProperty()
                .addListener((observable, oldValue, newValue) -> computeCalvingInterval(newValue, oldValue));

        currentDateTextField.setText(String.valueOf(LocalDate.now()));

    }
    private void  initializeAllHeartbeats() {
        initializeHeartbeatAnimation(morningStartTimeButton);
        initializeHeartbeatAnimation(morningEndTimeButton);
        initializeHeartbeatAnimation(afternoonStartTimeButton);
        initializeHeartbeatAnimation(afternoonEndTimeButton);
        initializeHeartbeatAnimation(eveningStartTimeButton);
        initializeHeartbeatAnimation(eveningEndTimeButton);
    }


    private void initializeCalvingHistory() {
        createReproductiveTableColumns();

        SelectedCattleManager selectedCattleManager = SelectedCattleManager.getInstance();

        // Listen to changes in the entire Cattle object
        selectedCattleManager.selectedCattleProperty().addListener((observable, oldCattle, newCattle) -> {
            if (newCattle == null || newCattle.getCattleId() == 0) return;

            selectedCattleId = newCattle.getCattleId();  // Get the selected cattle ID
            loadCalvingHistoryForCattle(selectedCattleId);
            updateCalvingHistoryData();

            // Optionally, set age at first calving if Date of Birth is needed
            LocalDate dateOfBirth = newCattle.getDateOfBirth();
            if (dateOfBirth != null) {
                selectedCattleDateOfBirth = dateOfBirth;  // Update Date of Birth
                setAgeAtFirstCalving();  // Calculate age at first calving
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
                                updatePregnancyStatus(selectedBreedingDateTextField.getText());

                            } catch (NumberFormatException e) {
                                // Handle errors gracefully
                            }
                        });


        estimatedGestationPeriodHBox.visibleProperty().bind(addingReproductiveVariables);
        estimatedGestationPeriodSpinner.visibleProperty().bind(addingReproductiveVariables);

        estimatedGestationPeriodSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(minGestationDays, maxGestationDays, initialGestationDays));
        estimatedGestationSpinner2.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(minGestationDays, maxGestationDays, initialGestationDays));

    }

    private void initializeCalvingDateListener() {
        calvingDate.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                List<LocalDate> allBreedingDates = getAllBreedingDatesForCattle(selectedCattleId);
                List<LocalDate> allCalvingDates = getAllCalvingDatesForCattle(selectedCattleId);
                // Convert selectedBreedingDateTextField.getText() to LocalDate
                LocalDate selectedBreedingDate = LocalDate.parse(selectedBreedingDateTextField.getText(), DateTimeFormatter.ISO_LOCAL_DATE);
                if (selectedReproductiveVariable == null) { // No existing record selected
                    if (selectedBreedingDateTextField.getText() == null) {
                        saveButton.setDisable(true);
                        updateButton.setDisable(true);
                    } else {
                        saveButton.setDisable(false);
                        updateButton.setDisable(true);
                    }
                } else {


                    // Check if breeding date exists in allBreedingDates
                    boolean breedingDateExists = allBreedingDates.contains(selectedBreedingDate);

                    if (breedingDateExists && allCalvingDates.contains(newValue)) {
                        saveButton.setDisable(true);
                        updateButton.setDisable(true);
                    } else {
                        saveButton.setDisable(true);
                        updateButton.setDisable(false);
                    }
                }

                Optional<String> validationMessage = DateUtil.validateCalvingDate(newValue, selectedBreedingDate, minGestationDays, maxGestationDays);
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









    @FXML
    private void handleBreedingDateButton() {
        try {
            List<BreedingAttempt> successfulBreedingAttempts = BreedingAttemptDAO.getBreedingAttemptsByCattleId(selectedCattleId)
                    .stream()
                    .filter(attempt -> "Success".equals(attempt.attemptStatus()))
                    .toList();

            ObservableList<String> breedingDates = FXCollections.observableArrayList(
                    successfulBreedingAttempts.stream()
                            .map(BreedingAttempt::attemptDate)
                            .collect(Collectors.toList())
            );

            ListView<String> breedingDateListView = new ListView<>(breedingDates);
            breedingDateListView.setPrefWidth(selectedBreedingDateTextField.getWidth());

            // Update previouslySelectedBreedingDate with the text field's value
            if (selectedBreedingDateTextField != null) {
                previouslySelectedBreedingDate = selectedBreedingDateTextField.getText();
            }

            // Highlight the previously selected date if it exists in the list
            if (previouslySelectedBreedingDate != null) {
                // Ensure the value exists in the list before selecting
                if (breedingDates.contains(previouslySelectedBreedingDate)) {
                    breedingDateListView.getSelectionModel().select(previouslySelectedBreedingDate);
                } else {
                    // If the previously selected date is not in the list, clear the selection
                    breedingDateListView.getSelectionModel().clearSelection();
                }
            }

            breedingDateListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    selectedBreedingDateTextField.setText(newValue);
                    previouslySelectedBreedingDate = newValue; // Track the selected date
                    breedingDatePopup.hide();
                }
            });

            breedingDateListView.setPrefHeight(Math.min(breedingDates.size() * 28, 140));

            breedingDatePopup.getContent().clear();
            breedingDatePopup.getContent().add(breedingDateListView);

            // Calculate the position below the selectedBreedingDateTextField
            Window window = selectedBreedingDateTextField.getScene().getWindow();
            Point2D point = selectedBreedingDateTextField.localToScene(0.0, 0.0);
            double x = window.getX() + point.getX() + selectedBreedingDateTextField.getScene().getX();
            double y = window.getY() + point.getY() + selectedBreedingDateTextField.getScene().getY() + selectedBreedingDateTextField.getHeight();

            breedingDatePopup.show(window, x, y);

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle error
        }
    }



    private void closeBreedingDatePopup() {
        if (breedingDatePopup != null) {
            breedingDatePopup.hide();
        }
    }

    private void addOutsideClickHandler(Scene scene) {
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            Window window = breedingDatePopup.getOwnerWindow();
            if (breedingDatePopup.isShowing() &&
                    (event.getSceneX() < window.getX() || event.getSceneX() > window.getX() + window.getWidth() ||
                            event.getSceneY() < window.getY() || event.getSceneY() > window.getY() + window.getHeight())) {
                closeBreedingDatePopup();
            }
        });
    }


    private void handleBreedingDateSelection(String selectedBreedingDate) {
        LocalDate breedingDate = LocalDate.parse(selectedBreedingDate, DateTimeFormatter.ISO_LOCAL_DATE);

        // Find the record matching the selected breeding date
        ReproductiveVariables selectedRecord = calvingHistoryTableView.getItems()
                .stream()
                .filter(record -> breedingDate.equals(record.getBreedingDate()))
                .findFirst()
                .orElse(null);

        if (selectedRecord != null) {
            updateFieldsWithSelectedRecord(selectedRecord);
            calvingHistoryTableView.getSelectionModel().select(selectedRecord);
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "The breeding date does not exist in the calving history. Would you like to add it?", ButtonType.OK, ButtonType.CANCEL);
            alert.setHeaderText(null);
            alert.setTitle("Breeding Date Not Found");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // Add the new breeding date with default values
                    ReproductiveVariables newRecord = new ReproductiveVariables();
                    newRecord.setBreedingDate(breedingDate);
                    newRecord.setGestationPeriod(283); // default gestation period
                    newRecord.setCalvingDate(null); // default calving date
                    newRecord.setCalvingInterval(0); // default calving interval

                    // Save the new record to the database
                    reproductiveVariablesDAO.addReproductiveVariableAndGetId(newRecord);

                    // Update the table view
                    calvingHistoryTableView.getItems().add(newRecord);
                    updateFieldsWithSelectedRecord(newRecord);
                    calvingHistoryTableView.getSelectionModel().select(newRecord);
                }
            });
        }
    }


    private void initializeSelectedCattleManagerListeners() {
        SelectedCattleManager selectedCattleManager = SelectedCattleManager.getInstance();

        // Listen to changes in the entire Cattle object
        selectedCattleManager
                .selectedCattleProperty()
                .addListener(
                        (observable, oldCattle, newCattle) -> {
                            if (newCattle != null && newCattle.getCattleId() != 0) {
                                selectedCattleId = newCattle.getCattleId();  // Get the selected cattle ID
                                selectedCattleDateOfBirth = newCattle.getDateOfBirth(); // Get Date of Birth

                                // Call methods that depend on the cattle ID and Date of Birth
                                updateLactationStartDateField();
                                productionSessionDatePicker.setValue(LocalDate.now());  // Set current date
                            } else {
                                // Handle case when no valid cattle is selected (e.g., reset fields)
                                selectedCattleId = 0;
                                selectedCattleDateOfBirth = null;
                            }
                        }
                );
    }

    private void initializeCalvingDateDayCellFactory() {
        calvingDate.setDayCellFactory(
                param ->
                        new DateCell() {
                            @Override
                            public void updateItem(LocalDate item, boolean empty) {
                                super.updateItem(item, empty);
                                // Check if lastBreedingDate is null, disable and style all cells
                                if (selectedBreedingDateTextField.getText() == null) {
                                    setDisable(true);
                                    setStyle("-fx-background-color: #ffc0cb;");
                                    return; // Early exit if breeding date is null
                                }

                                if (item != null && selectedBreedingDateTextField.getText() != null) {
                                    // Convert selectedBreedingDateTextField.getText() to LocalDate
                                    LocalDate selectedBreedingDate = LocalDate.parse(selectedBreedingDateTextField.getText(), DateTimeFormatter.ISO_LOCAL_DATE);
                                    LocalDate minDate = selectedBreedingDate.plusDays(minGestationDays);
                                    LocalDate maxDate = selectedBreedingDate.plusDays(maxGestationDays);
                                    if (item.isBefore(minDate) || item.isAfter(maxDate)) {
                                        setDisable(true);
                                        setStyle("-fx-background-color: #ffc0cb;");
                                    }
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
                                    String breedingDate = selectedBreedingDateTextField.getText();
                                    daysSinceCalvingLabel.setText("N/A");
                                    if (breedingDate != null) {
                                        pregnantRadioBtn.setDisable(false);
                                        pregnantRadioBtn.setSelected(true);
                                        daysInPregnancyTextArea.setText(DateUtil.calculateDays(LocalDate.parse(selectedBreedingDateTextField.getText(), DateTimeFormatter.ISO_LOCAL_DATE)));

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
        if (selectedBreedingDateTextField != null && selectedBreedingDateTextField.getText() != null) {
            LocalDate lastBreeding = LocalDate.parse(selectedBreedingDateTextField.getText(), DateTimeFormatter.ISO_LOCAL_DATE);
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
        List<ReproductiveVariables> records = calvingHistoryTableView.getItems();
        ReproductiveVariables lastRecord = records.isEmpty() ? null : records.getLast();

        // Clear fields
        selectedBreedingDateTextField.setText(null);
        calvingDate.setValue(null);
        calvingIntervalTextField.clear();
        projectedCalvingDate.setText("");
        targetCalvingAgeLabel.setText("N/A");
        daysSinceCalvingLabel.setText("N/A");
        clearTreeView();

        if (lastRecord != null) {
            selectedReproductiveVariable = lastRecord;
            selectedBreedingDateTextField.setText(lastRecord.getBreedingDate().toString());
            calvingDate.setValue(lastRecord.getCalvingDate());
            calvingIntervalTextField.setText(
                    lastRecord.getCalvingInterval() > 0
                            ? String.valueOf(lastRecord.getCalvingInterval())
                            : "");
            estimatedGestationSpinner
                    .getValueFactory()
                    .setValue(lastRecord.getGestationPeriod() != 0 ? lastRecord.getGestationPeriod() : 283);

            if (lastRecord.getCalvingDate() == null) {
                populateTreeView();
                long daysSinceBreeding = ChronoUnit.DAYS.between(lastRecord.getBreedingDate(), LocalDate.now());
                highlightSubStages((int) daysSinceBreeding);
            }

            // Select the last record in the table
            int lastIndex = records.size() - 1;
            calvingHistoryTableView.getSelectionModel().select(lastIndex);
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


    private void updatePregnancyStatus(String breedingDate) {
        if (breedingDate != null) {
            try {
                LocalDate breedingDateAsLocalDate = LocalDate.parse(breedingDate, DateTimeFormatter.ISO_LOCAL_DATE);
                int gestationPeriodInDays = estimatedGestationSpinner.getValue();
                long estimatedGestationInMonths = ChronoUnit.MONTHS.between(
                        breedingDateAsLocalDate.plusDays(1), breedingDateAsLocalDate.plusDays(gestationPeriodInDays));

                pregnantRadioBtn.setDisable(true);
                if (ChronoUnit.MONTHS.between(breedingDateAsLocalDate, LocalDate.now()) <= estimatedGestationInMonths) {
                    pregnantRadioBtn.setDisable(false);
                    notPregnantRadioBtn.setDisable(false);
                    pregnantRadioBtn.setSelected(true);
                    notPregnantRadioBtn.setSelected(false);
                    daysInPregnancyTextArea.setText(DateUtil.calculateDays(breedingDateAsLocalDate));
                } else {
                    pregnantRadioBtn.setDisable(false);
                    notPregnantRadioBtn.setDisable(false);
                    notPregnantRadioBtn.setSelected(true);
                    pregnantRadioBtn.setSelected(false);
                    String message =
                            "Cow is likely not pregnant. "
                                    + DateUtil.calculateDays(breedingDateAsLocalDate)
                                    + " have passed since breeding.";
                    daysInPregnancyTextArea.setText(message);
                    clearTreeView();
                }
            } catch (DateTimeParseException e) {
                // Handle invalid date format
                System.err.println("Invalid breeding date format: " + breedingDate);
                // You might want to display an error message to the user
            }
        } else {
            daysInPregnancyTextArea.setText("");
        }
    }

    private void clearTreeView() {
        stageOfPregnancy.setRoot(null);
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

            // Check if both lastBreedingDate and calvingDate are set
            if (selectedBreedingDateTextField.getText() != null && calvingDate.getValue() != null) {
                createSelectionWindow(herdNames);
            }

            // Check if the save button was pressed
            if (saveButtonPressed) {
                // Check if lastBreedingDate is set and calvingDate is not set
                if (selectedBreedingDateTextField.getText()  != null && calvingDate.getValue() == null) {
                    // Add reproductive variables
                    ReproductiveVariables reproductiveVariables = getReproductiveVariables(LocalDate.parse(selectedBreedingDateTextField.getText()) , estimatedGestationSpinner.getValue());
                    int reproductiveVariableId = reproductiveVariablesDAO.addReproductiveVariableAndGetId(reproductiveVariables);
                    if (reproductiveVariableId != -1) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Reproductive Data added successfully.");
                        loadCalvingHistoryForCattle(selectedCattleId);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to add reproductive data.");
                    }
                }
            }
        } catch (SQLException e) {
            handleHerdRetrievalError(e);
            e.printStackTrace();
        }
    }



    private void createSelectionWindow(List<String> herdNames) {
        LocalDate breedingDate = LocalDate.parse(selectedBreedingDateTextField.getText());
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
                    openAddCattleFormSaveCattleAndUpdateReproductiveData(selectedHerdName, calveDate, breedingDate, gestationPeriod);

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
                            addNewCattleController.setStage(pair.getKey()); // Set the stage reference
                            addNewCattleController.initData(selectedHerd.id(), selectedCattleId, dateOfBirth);

                            addNewCattleController.setCattleAdditionCallback((successFlag, failureReason, newCattleId) -> {
                                if (successFlag) {
                                    showAlert(Alert.AlertType.INFORMATION, "Success", "Cattle added successfully.");
                                    // Add reproductive variables here
                                    ReproductiveVariables reproductiveVariables = getReproductiveVariables(breedingDate, gestationPeriod);
                                    int reproductiveVariableId = reproductiveVariablesDAO.addReproductiveVariableAndGetId(reproductiveVariables);
                                    if (reproductiveVariableId != -1) {
                                        // Add lactation period
                                        boolean lactationPeriodSuccess = LactationPeriodDAO.addLactationPeriod(selectedCattleId, dateOfBirth);
                                        if (lactationPeriodSuccess) {
                                            clearReproductiveData();
                                            showAlert(Alert.AlertType.INFORMATION, "Success", "Cattle, Reproductive data, and Lactation Period added successfully.");
                                            loadCalvingHistoryForCattle(selectedCattleId);
                                            loadLactationPeriodsForSelectedCattle();
                                        } else {
                                            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save Lactation Period data.");
                                            // Rollback the added cattle and reproductive variables
                                            CattleDAO.deleteCattleById(newCattleId);
                                            ReproductiveVariablesDAO.deleteReproductiveVariable(reproductiveVariableId);
                                        }
                                    } else {
                                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to save Reproductive data.");
                                        // Rollback the added cattle
                                        CattleDAO.deleteCattleById(newCattleId);
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


    public void updateReproductiveData() throws SQLException {
        LocalDate breedingDate = LocalDate.parse(selectedBreedingDateTextField.getText()) ;
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
        selectedReproductiveVariable.setBreedingDate(LocalDate.parse(selectedBreedingDateTextField.getText()));
        boolean success = reproductiveVariablesDAO.updateReproductiveVariable(selectedReproductiveVariable);
        if (success) {
            clearReproductiveData();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Breeding date updated successfully.");
            loadCalvingHistoryForCattle(selectedCattleId);
        } else {
            handleUpdateFailure();
        }
    }


    private void handleCalvingDateChange() throws SQLException {
        LocalDate originalDateOfBirth = null;

        try {
            originalDateOfBirth = selectedReproductiveVariable.getCalvingDate();
            LocalDate newCalvingDate = calvingDate.getValue();
            selectedReproductiveVariable.setCalvingDate(newCalvingDate);

            // Check if a new calving date is being set for the first time
            if (originalDateOfBirth == null) {
                boolean lactationPeriodAdded = LactationPeriodDAO.addLactationPeriod(selectedCattleId, newCalvingDate);
                if (!lactationPeriodAdded) {
                    showAlert(Alert.AlertType.ERROR, "Update Failed", "Failed to add new Lactation Period.");
                    return;
                }
                showAlert(Alert.AlertType.INFORMATION, "Update Successful", "New Lactation Period added successfully.");
            } else {
                // Original calving date exists, proceed to update it
                Cattle cattleToUpdate = CattleDAO.findCattleByBirthdateAndDamId(originalDateOfBirth, selectedCattleId);
                if (cattleToUpdate != null) {
                    boolean dateOfBirthUpdated = CattleDAO.updateCattleDateOfBirth(cattleToUpdate.getCattleId(), newCalvingDate);

                    if (dateOfBirthUpdated) {
                        boolean reproductiveVariableUpdated = reproductiveVariablesDAO.updateReproductiveVariable(selectedReproductiveVariable);
                        if (!reproductiveVariableUpdated) {
                            showAlert(Alert.AlertType.ERROR, "Update Failed", "Failed to update Reproductive data." + "\n Reverting changes.");
                            handleDateOfBirthUpdateFailure(originalDateOfBirth);
                            return;
                        }

                        // Update the existing lactation period
                        updateLactationPeriod(originalDateOfBirth);

                        showAlert(Alert.AlertType.INFORMATION, "Update Successful", "Cattle date of birth, Reproductive data, Lactation Start Date, and Production Session Dates updated successfully.");
                    } else {
                        handleUpdateFailure("Failed to update Cattle date of birth.", originalDateOfBirth);
                        return;
                    }
                }
            }
            // Load calving history and lactation periods regardless of whether we added or updated
            loadCalvingHistoryForCattle(selectedCattleId);
            loadLactationPeriodsForSelectedCattle();
        } catch (SQLException e) {
            handleSqlException(originalDateOfBirth, e);
        }
    }


    private void updateLactationPeriod(LocalDate originalDateOfBirth) throws SQLException {

        try {
            int lactationPeriodID= LactationPeriodDAO.getLactationIdByCattleIdAndStartDate(selectedCattleId, originalDateOfBirth);
            if (lactationPeriodID == 0) {
                boolean lactationPeriodAdded = LactationPeriodDAO.addLactationPeriod(selectedCattleId, calvingDate.getValue());
                if (!lactationPeriodAdded) {
                    handleUpdateFailure("Failed to add a new lactation period.",originalDateOfBirth);
                    return;
                }
            } else {

                boolean updatedLactationPeriodStartDate = LactationPeriodDAO.updateLactationPeriodStartDate(lactationPeriodID, calvingDate.getValue());
                if (!updatedLactationPeriodStartDate) {
                    handleUpdateFailure("Failed to update Lactation Start Date.",originalDateOfBirth);
                    return;
                }
            }

            List<ProductionSession> productionSessions = ProductionSessionDAO.getAllProductionSessions();
            boolean hasSessionsToUpdate = productionSessions.stream()
                    .anyMatch(session -> session.getCattleID() == selectedCattleId && session.getLactationPeriodID() == lactationPeriodID);

            if (!hasSessionsToUpdate) {
                showAlert(Alert.AlertType.WARNING, "No Production Sessions Found", "No production sessions found for the specified cattle and lactation period. Skipping session date updates.");
                return;
            }

            boolean updateLactationSession = ProductionSessionDAO.updateSessionDates(selectedCattleId, lactationPeriodID, calvingDate.getValue());
            if (!updateLactationSession) {
                handleUpdateFailure("Failed to update Production Session Dates.",originalDateOfBirth);
            }
        } catch (SQLException e) {
            handleUpdateFailure("An error occurred while updating lactation period: " + e.getMessage(),originalDateOfBirth);
        }
    }

    private void handleUpdateFailure(String message,LocalDate originalDateOfBirth) throws SQLException {
        showAlert(Alert.AlertType.ERROR, "Update Failed", message + " Reverting changes.");
        handleDateOfBirthUpdateFailure(originalDateOfBirth);
        selectedReproductiveVariable.setCalvingDate(originalDateOfBirth);
        reproductiveVariablesDAO.updateReproductiveVariable(selectedReproductiveVariable);
    }

    private void handleDateOfBirthUpdateFailure(LocalDate originalDateOfBirth) throws SQLException {
        Cattle cattleToUpdate = CattleDAO.findCattleByBirthdateAndDamId(originalDateOfBirth, selectedCattleId);
        boolean dateOfBirthReverted = false;
        if (cattleToUpdate != null) {
            dateOfBirthReverted = CattleDAO.updateCattleDateOfBirth(cattleToUpdate.getCattleId(), originalDateOfBirth);
        }
        if (dateOfBirthReverted) {
            showAlert(Alert.AlertType.ERROR, "Revert Successful", "Failed to update cattle's date of birth and Reproductive data. Changes have been reverted.");
            AppLogger.error("Failed to update cattle's date of birth.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Revert Failed", "Failed to revert date of birth change. Manual intervention required.");
            AppLogger.error("Failed to revert date of birth change.");
        }
    }

    private void handleSqlException(LocalDate originalDateOfBirth, SQLException e) throws SQLException {
        showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while updating records: " + e.getMessage() + ". Reverting changes.");
        handleDateOfBirthUpdateFailure(originalDateOfBirth);
        try {
            int lactationPeriodID = LactationPeriodDAO.getLactationIdByCattleIdAndStartDate(selectedCattleId, originalDateOfBirth);
            LactationPeriodDAO.updateLactationPeriodStartDate(lactationPeriodID, originalDateOfBirth);
            ProductionSessionDAO.updateSessionDates(selectedReproductiveVariable.getCattleID(), lactationPeriodID, originalDateOfBirth);
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to handle SQL exception while reverting changes.", ex);
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

    private void openAddCattleFormSaveCattleAndUpdateReproductiveData(String selectedHerdName, LocalDate dateOfBirth, LocalDate breedingDate, int gestationPeriod) {
        try {
            Herd selectedHerd = HerdDAO.getHerdByName(selectedHerdName);
            if (selectedHerd != null) {
                openForm("/com/example/hashinfarm/homePanels/homeCenterPanelViews/cattleManagement/centerLeftViews/addCattle.fxml",
                        "Add Cattle to Herd",
                        pair -> {
                            FXMLLoader loader = pair.getValue();

                            addNewCattleController = loader.getController();
                            addNewCattleController.setStage(pair.getKey()); // Set the stage reference
                            addNewCattleController.initData(selectedHerd.id(), selectedCattleId, dateOfBirth);

                            addNewCattleController.setCattleAdditionCallback((successFlag, failureReason, newCattleId) -> {
                                if (successFlag) {
                                    showAlert(Alert.AlertType.INFORMATION, "Success", "Cattle added successfully.");

                                    // Retrieve the original reproductive variable details
                                    ReproductiveVariables originalReproductiveVariables = selectedReproductiveVariable;

                                    // Add reproductive variables here
                                    ReproductiveVariables reproductiveVariables = getReproductiveVariables(breedingDate, gestationPeriod);
                                    boolean reproductiveSuccess = reproductiveVariablesDAO.updateReproductiveVariable(reproductiveVariables);

                                    if (reproductiveSuccess) {
                                        // Add lactation period
                                        boolean lactationPeriodSuccess = LactationPeriodDAO.addLactationPeriod(selectedCattleId, dateOfBirth);
                                        if (lactationPeriodSuccess) {
                                            clearReproductiveData();
                                            showAlert(Alert.AlertType.INFORMATION, "Success", "Cattle, Reproductive data, and Lactation Period added successfully.");
                                            loadCalvingHistoryForCattle(selectedCattleId);
                                            loadLactationPeriodsForSelectedCattle();
                                            pair.getKey().close(); // Close the stage after successful addition and update
                                        } else {
                                            // Revert to original reproductive variable details before updating
                                            reproductiveVariablesDAO.updateReproductiveVariable(originalReproductiveVariables);

                                            // Update added cattle date of birth to use the reproductive variable original calving date
                                            LocalDate originalCalvingDate = originalReproductiveVariables.getCalvingDate();

                                            CattleDAO.updateCattleDateOfBirth(newCattleId, originalCalvingDate);


                                            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save Lactation Period data. Reverted to original reproductive data.");
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




    private void handleHerdRetrievalError(SQLException e) {
        AppLogger.error("Error retrieving herds", e);
        showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve herds. See logs for details.");
    }

    private void handleFormError(String title, Exception e) {
        AppLogger.error("Error opening form: " + title, e);
        showAlert(Alert.AlertType.ERROR, "Error", "Failed to load form: " + title + ". See logs for details.");
    }



    private void createReproductiveTableColumns() {
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

                                            // Check if end date is present
                                            LocalDate CalvingDate = reproductiveVariables.getCalvingDate();
                                            if (CalvingDate != null) {

                                                handleDeletion(reproductiveVariables);
                                            } else {

                                                if(ReproductiveVariablesDAO.deleteReproductiveVariable(reproductiveVariables.getReproductiveVariableID())){
                                                    loadCalvingHistoryForCattle(selectedCattleId);
                                                    showAlert(Alert.AlertType.INFORMATION, "Success", "reproductive Record deleted successfully.");
                                                }else{
                                                    showAlert(Alert.AlertType.ERROR, "Failure", "Failed to delete reproductive Record.");
                                                }


                                            }
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
        selectedBreedingDateTextField.setText(null);
        calvingIntervalTextField.setText("");
        calvingDate.setValue(null);
        estimatedGestationSpinner.getValueFactory().setValue(283);
        clearTreeView();
        projectedCalvingDate.setText("");
        daysSinceCalvingLabel.setText("");
        targetCalvingAgeLabel.setText("");
        selectedReproductiveVariable = null;
        saveButton.setDisable(true);
        updateButton.setDisable(true);
        saveButtonPressed = true;
    }


    public void handleDeletion(ReproductiveVariables reproductiveVariables) {
        ReproductiveVariablesDAO reproductiveVariablesDAO = new ReproductiveVariablesDAO();
        LocalDate calvingDate = reproductiveVariables.getCalvingDate();
        int reproductiveVariableID = reproductiveVariables.getReproductiveVariableID();
        Connection conn = null;

        try {
            conn = dbConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            if (confirmAction()) {

                boolean proceedWithDeletion = true;


                if (calvingDate != null) {

                    proceedWithDeletion = checkAndDeleteAssociatedCattle(conn, selectedCattleId, calvingDate);
                }

                if (proceedWithDeletion) {

                    if (calvingDate != null && !checkAndDeleteLactationPeriod(conn, selectedCattleId, calvingDate)) {
                        conn.rollback();

                        return;
                    }

                    if (reproductiveVariablesDAO.deleteReproductiveVariable(conn, reproductiveVariableID)) {
                        conn.commit();
                        showSuccessAlert();
                        loadCalvingHistoryForCattle(selectedCattleId);
                        clearReproductiveData();
                    } else {
                        conn.rollback();
                        showFailureAlert("Failed to delete reproductive variable.");
                    }
                } else {
                    conn.rollback();

                }
            }
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            showFailureAlert("An error occurred: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
//                    conn.setAutoCommit(true);
                    conn.close();

                } catch (SQLException closeEx) {

                    closeEx.printStackTrace();
                }
            }
        }
    }

    private boolean checkAndDeleteAssociatedCattle(Connection conn, int selectedCattleId, LocalDate calvingDate) {
        try {

            Cattle cattle = CattleDAO.findCattleByBirthdateAndDamId(conn, calvingDate, selectedCattleId);
            if (cattle != null) {
                CattleDAO.deleteCattleById(conn, cattle.getCattleId());

            }
        } catch (SQLException e) {
            e.printStackTrace();
            showFailureAlert("An error occurred while deleting associated cattle: " + e.getMessage());
            return false;
        }
        return true;
    }

    private boolean checkAndDeleteLactationPeriod(Connection conn, int cattleId, LocalDate calvingDate) {
        try {

            int lactationPeriodId = LactationPeriodDAO.getLactationIdByCattleIdAndStartDate(conn, cattleId, calvingDate);
            if (lactationPeriodId != -1) {
                if (!checkAndDeleteProductionSessions(conn, lactationPeriodId)) {
                    return false;
                }
                LactationPeriodDAO.deleteLactationPeriod(conn, lactationPeriodId);

            }
        } catch (SQLException e) {
            e.printStackTrace();
            showFailureAlert("An error occurred while deleting lactation period: " + e.getMessage());
            return false;
        }
        return true;
    }

    private boolean checkAndDeleteProductionSessions(Connection conn, int lactationPeriodId) {
        try {

            List<ProductionSession> productionSessions = ProductionSessionDAO.getProductionSessionsByLactationPeriodId(conn, lactationPeriodId);
            if (!productionSessions.isEmpty()) {
                for (ProductionSession session : productionSessions) {
                    if (!ProductionSessionDAO.deleteProductionSession(conn, session.getSessionID())) {
                        showFailureAlert("Failed to delete production session.");
                        return false;
                    }
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }





    // Helper method for showing confirmation dialogs
    private boolean confirmAction() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to delete the " + "reproductive variable" + "?");
        return confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    // Helper method for showing success/failure alerts
    private void showSuccessAlert() {
        showAlert(Alert.AlertType.INFORMATION, "Success", "Reproductive variable" + " deleted successfully.");
    }

    // Helper method for failure alerts (shortcut)
    private void showFailureAlert(String errorMessage) {
        showAlert(Alert.AlertType.ERROR, "Error", errorMessage);
    }


    // Helper method for showing success/failure alerts
    private void showAlert(Alert.AlertType alertType, String title, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(contentText);
        alert.showAndWait();
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





    //ACCORDIONS CODE SECTION


    private void initializeButtons() {
        modifyOffspringDetailsButton.setDisable(true);
        updateOffSpringDetailsButton.setDisable(true);
        modifyBreedingAttemptButton.setDisable(true);
        updateBreedingAttemptButton.setDisable(true);
    }

    private void initializeSplitPlane(SplitPane splitpane, Button leftArrowButton, Button rightArrowButton) {
        SplitPaneDividerEnforcer dividerEnforcer = new SplitPaneDividerEnforcer(minPosition, maxPosition2);
        dividerEnforcer.enforceConstraints(splitpane);
        CattleController cattleController = new CattleController();
        leftArrowButton.setOnAction(event -> cattleController.animateSplitPane(minPosition, splitpane, minPosition, maxPosition2, leftArrowButton, rightArrowButton));
        rightArrowButton.setOnAction(event -> cattleController.animateSplitPane(maxPosition2, splitpane, minPosition, maxPosition2, leftArrowButton, rightArrowButton));
        splitpane.getDividers().getFirst().positionProperty().addListener((obs, oldPos, newPos) -> cattleController.updateButtonsPosition(newPos.doubleValue(), splitpane, leftArrowButton, rightArrowButton));

    }
    private void initSelectedCattleListeners() {
        SelectedCattleManager selectedCattleManager = SelectedCattleManager.getInstance();

        // Listen for changes to the entire Cattle object
        selectedCattleManager.selectedCattleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.getCattleId() != 0) {
                selectedCattleId = newValue.getCattleId();  // Get the new cattle ID from the Cattle object
                try {
                    loadOffspringData();
                    loadBreedingAttemptsData();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else {
                // Handle case when no cattle is selected or invalid cattle ID
                selectedCattleId = 0;  // Reset selectedCattleId or other UI components as needed
            }
        });
    }


    private void initializeOffspringAndBreedingAttemptTableColumns() {
        // Initialize cell value factories for offspring table columns
        setStringCellFactoryAndAlign(offspringIdColumn, cellData -> String.valueOf(cellData.getValue().offspringId()));
        setStringCellFactoryAndAlign(cattleIdColumn, cellData -> String.valueOf(cellData.getValue().cattleId()));
        setStringCellFactoryAndAlign(cattleNameColumn, cellData -> cellData.getValue().cattleName());
        setStringCellFactoryAndAlign(genderColumn, cellData -> cellData.getValue().gender());
        setStringCellFactoryAndAlign(breedingMethodColumn, cellData -> cellData.getValue().breedingMethod());

        // Initialize cell value factories for breeding attempt table columns
        setIntegerCellFactoryAndAlign(breedingAttemptIdColumn, cellData -> cellData.getValue().breedingAttemptId());
        setStringCellFactoryAndAlign(estrusDateColumn, cellData -> cellData.getValue().estrusDate());
        setStringCellFactoryAndAlign(breedingMethodBreedingAttemptColumn, cellData -> cellData.getValue().breedingMethod());
        setStringCellFactoryAndAlign(sireUsedColumn, cellData -> String.valueOf(cellData.getValue().sireId()));
        setStringCellFactoryAndAlign(attemptDateColumn, cellData -> cellData.getValue().attemptDate());
        setStringCellFactoryAndAlign(attemptStatusColumn, cellData -> cellData.getValue().attemptStatus());
    }

    // Method for columns with String values
    private <T> void setStringCellFactoryAndAlign(TableColumn<T, String> column, Function<TableColumn.CellDataFeatures<T, String>, String> mapper) {
        column.setCellValueFactory(cellData -> new SimpleStringProperty(mapper.apply(cellData)));
        TableColumnUtils.centerAlignColumn(column);
    }

    // Method for columns with Integer values
    private <T> void setIntegerCellFactoryAndAlign(TableColumn<T, Integer> column, Function<TableColumn.CellDataFeatures<T, Integer>, Integer> mapper) {
        column.setCellValueFactory(cellData -> new SimpleIntegerProperty(mapper.apply(cellData)).asObject());
        TableColumnUtils.centerAlignColumn(column);
    }





    //OFFSPRING DATA
// Method to load Offspring data
    private void loadOffspringData() throws SQLException {
        try {
            List<Cattle> cattleList = CattleDAO.getProgenyByCattleId(selectedCattleId);
            List<UnifiedOffspring> unifiedOffspringList = new ArrayList<>();

            for (Cattle cattle : cattleList) {
                if (OffspringDAO.isOffspringOfSelectedCattle(cattle.getCattleId())) {
                    UnifiedOffspring unifiedOffspring = OffspringDAO.getTheOffspringDetailsByItsCattleId(cattle.getCattleId());
                    if (unifiedOffspring != null) {
                        unifiedOffspringList.add(unifiedOffspring);
                    }
                }else {
                    // Create a new UnifiedOffspring entry as needed
                    UnifiedOffspring newOffspring = new UnifiedOffspring(
                            0, // offspringId
                            String.valueOf(cattle.getCattleId()), // cattleId
                            cattle.getName(),
                            cattle.getGender(),
                            "", // breedingMethod
                            0.0, // birthWeight
                            1, // easeOfCalving
                            283, // gestationLength
                            0.0, // measuredWeight
                            null, // lastDateWeightTaken
                            "", // dateOfBirth
                            "", // intendedUse
                            "" // sireId
                    );

                    OffspringDAO.insertOffspring(newOffspring);
                    unifiedOffspringList.add(newOffspring);
                }
            }

            ObservableList<UnifiedOffspring> tableData = FXCollections.observableArrayList(unifiedOffspringList);
            cattleTableView.setItems(tableData);

            if (tableData.isEmpty()) {
                handleClearOffspringFields();
            } else {
                if (cattleTableView.getSelectionModel().isEmpty()) {
                    cattleTableView.getSelectionModel().selectFirst();
                }
                populateFieldsWithSelectedOffspring();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while loading offspring data.");
        }
    }


    // Method to populate fields with selected Offspring
    private void populateFieldsWithSelectedOffspring() {
        UnifiedOffspring selectedOffspring = cattleTableView.getSelectionModel().getSelectedItem();
        if (selectedOffspring != null) {
            modifyOffspringDetailsButton.setDisable(false);
            storeInitialValues(selectedOffspring);
            populateFields(selectedOffspring);
            updateSireIdOrDamIdLabel(selectedOffspring.gender());
            dateOfBirthTextField.setText(selectedOffspring.dateOfBirth());
            updateOffSpringDetailsButton.setDisable(true);
        }
    }

    // Method to store initial values
    private void storeInitialValues(UnifiedOffspring offspring) {
        initialValuesOffspring.put("birthWeight", getStringValue(offspring.birthWeight()));
        initialValuesOffspring.put("easeOfCalving", getStringValue(offspring.easeOfCalving()));
        initialValuesOffspring.put("gestationLength", getStringValue(offspring.gestationLength()));
        initialValuesOffspring.put("measuredWeight", getStringValue(offspring.measuredWeight()));
        initialValuesOffspring.put("lastDateWeightTaken", getStringValue(offspring.lastDateWeightTaken()));
        initialValuesOffspring.put("intendedUse", getStringValue(offspring.intendedUse()));
        initialValuesOffspring.put("gender", getStringValue(offspring.gender()));
        initialValuesOffspring.put("breedingMethod", getStringValue(offspring.breedingMethod()));
    }

    // Method to populate fields
    private void populateFields(UnifiedOffspring offspring) {
        offspringIdLabel.setText(String.valueOf(offspring.offspringId()));
        birthWeightTextField.setText(getStringValue(offspring.birthWeight()));
        easeOfCalvingSlider.setValue(getSliderValue(offspring.easeOfCalving()));
        sireIdOrDamIdTextField.setText(getStringValue(offspring.sireId()));
        estimatedGestationSpinner2.getValueFactory().setValue(offspring.gestationLength());
        measuredWeightTextField.setText(getStringValue(offspring.measuredWeight()));
        lastDateWeightTakenDatePicker.setValue(offspring.lastDateWeightTaken());
        intendedUseComboBox.setValue(getStringValue(offspring.intendedUse()));
        offspringGenderComboBox.setValue(getStringValue(offspring.gender()));
        breedingMethodComboBox.setValue(getStringValue(offspring.breedingMethod()));
    }



    // Method to update Sire ID or Dam ID label
    private void updateSireIdOrDamIdLabel(String gender) {
        if (gender != null) {
            switch (gender.toLowerCase()) {
                case "male":
                    sireIdOrDamIdLabel.setText("(Male)");
                    break;
                case "female":
                    sireIdOrDamIdLabel.setText("(Female)");
                    break;
                default:
                    sireIdOrDamIdLabel.setText("Unknown ID");
                    break;
            }
        } else {
            sireIdOrDamIdLabel.setText("Unknown ID");
        }
    }

    // Utility methods
    private String getStringValue(Object value) {
        return value != null ? String.valueOf(value) : "";
    }

    private double getSliderValue(Number value) {
        return value != null ? value.doubleValue() : 0;
    }


    //Checking Changes and Validations
// Method to check for changes in Offspring details
    private void checkForOffspringDetailChanges() {
        boolean hasChanges = !birthWeightTextField.getText().equals(initialValuesOffspring.get("birthWeight")) ||
                easeOfCalvingSlider.getValue() != Double.parseDouble(initialValuesOffspring.get("easeOfCalving")) ||
                estimatedGestationSpinner2.getValue() != Integer.parseInt(initialValuesOffspring.get("gestationLength")) ||
                !measuredWeightTextField.getText().equals(initialValuesOffspring.get("measuredWeight")) ||
                (lastDateWeightTakenDatePicker.getValue() != null && !lastDateWeightTakenDatePicker.getValue().toString().equals(initialValuesOffspring.get("lastDateWeightTaken"))) ||
                (intendedUseComboBox.getValue() != null && !intendedUseComboBox.getValue().equals(initialValuesOffspring.get("intendedUse"))) ||
                (offspringGenderComboBox.getValue() != null && !offspringGenderComboBox.getValue().equals(initialValuesOffspring.get("gender"))) ||
                (breedingMethodComboBox.getValue() != null && !breedingMethodComboBox.getValue().equals(initialValuesOffspring.get("breedingMethod")));

        updateOffSpringDetailsButton.setDisable(!hasChanges);
    }

    // Method to check if Offspring fields have changed
    private boolean offspringFieldsHaveChanged() {
        UnifiedOffspring selectedOffspring = cattleTableView.getSelectionModel().getSelectedItem();
        return !birthWeightTextField.getText().equals(String.valueOf(selectedOffspring.birthWeight())) ||
                easeOfCalvingSlider.getValue() != selectedOffspring.easeOfCalving() ||
                !Objects.equals(estimatedGestationSpinner2.getValue(), selectedOffspring.gestationLength()) ||
                !measuredWeightTextField.getText().equals(String.valueOf(selectedOffspring.measuredWeight())) ||
                (lastDateWeightTakenDatePicker.getValue() != null && !lastDateWeightTakenDatePicker.getValue().equals(selectedOffspring.lastDateWeightTaken())) ||
                (intendedUseComboBox.getValue() != null && !intendedUseComboBox.getValue().equals(selectedOffspring.intendedUse())) ||
                (offspringGenderComboBox.getValue() != null && !offspringGenderComboBox.getValue().equals(selectedOffspring.gender())) ||
                (breedingMethodComboBox.getValue() != null && !breedingMethodComboBox.getValue().equals(selectedOffspring.breedingMethod()));
    }

    // Method to check if Offspring fields are populated
    private boolean offspringFieldsArePopulated() {
        UnifiedOffspring selectedOffspring = cattleTableView.getSelectionModel().getSelectedItem();
        return birthWeightTextField.getText().equals(String.valueOf(selectedOffspring.birthWeight())) &&
                easeOfCalvingSlider.getValue() == selectedOffspring.easeOfCalving() &&
                Objects.equals(estimatedGestationSpinner2.getValue(), selectedOffspring.gestationLength()) &&
                measuredWeightTextField.getText().equals(String.valueOf(selectedOffspring.measuredWeight())) &&
                (lastDateWeightTakenDatePicker.getValue() != null && lastDateWeightTakenDatePicker.getValue().equals(selectedOffspring.lastDateWeightTaken())) &&
                (intendedUseComboBox.getValue() != null && intendedUseComboBox.getValue().equals(selectedOffspring.intendedUse())) &&
                (offspringGenderComboBox.getValue() != null && offspringGenderComboBox.getValue().equals(selectedOffspring.gender())) &&
                (breedingMethodComboBox.getValue() != null && breedingMethodComboBox.getValue().equals(selectedOffspring.breedingMethod()));
    }


    // Method to check if fields are valid for Offspring
    private boolean areFieldsValidForOffspring() {
        try {
            // Check if all fields are filled and valid
            Double.parseDouble(birthWeightTextField.getText());
            estimatedGestationSpinner2.getValue();
            Double.parseDouble(measuredWeightTextField.getText());

            // Additional validation can be added as needed
            return !birthWeightTextField.getText().isEmpty() &&
                    !measuredWeightTextField.getText().isEmpty() &&
                    lastDateWeightTakenDatePicker.getValue() != null &&
                    intendedUseComboBox.getValue() != null &&
                    offspringGenderComboBox.getValue() != null &&
                    breedingMethodComboBox.getValue() != null;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    //Handling Actions
    // Method to handle modification of Offspring details
    @FXML
    private void modifyOffspringDetails() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Modify Offspring Details");
        alert.setHeaderText(null);

        if (offspringFieldsArePopulated() && !offspringFieldsHaveChanged()) {
            showActionDialog(alert, this::handleDeleteOffspring);
        } else if (offspringFieldsHaveChanged()) {
            showActionDialog(alert, this::populateFieldsWithSelectedOffspring,this::handleDeleteOffspring);
        }
    }

    // Method to update Offspring details
    @FXML
    private void updateOffSpringDetails() {
        UnifiedOffspring selectedOffspring = cattleTableView.getSelectionModel().getSelectedItem();

        if (selectedOffspring == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an offspring from the table.");
            return;
        }

        if (!areFieldsValidForOffspring()) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please ensure all fields are correctly filled.");
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Update");
        confirmationAlert.setHeaderText("You are about to update the offspring details.");
        confirmationAlert.setContentText("Do you want to proceed with the update?");

        ButtonType confirmButton = new ButtonType("Yes");
        ButtonType cancelButton = new ButtonType("No");
        confirmationAlert.getButtonTypes().setAll(confirmButton, cancelButton);

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == confirmButton) {
            try {
                // Create updated UnifiedOffspring object with the current field values
                UnifiedOffspring updatedUnifiedOffspring = new UnifiedOffspring(
                        selectedOffspring.offspringId(),
                        selectedOffspring.cattleId(),
                        selectedOffspring.cattleName(),
                        offspringGenderComboBox.getValue(),
                        breedingMethodComboBox.getValue(),
                        Double.parseDouble(birthWeightTextField.getText()),
                        (int) easeOfCalvingSlider.getValue(),
                        estimatedGestationSpinner2.getValue(),
                        Double.parseDouble(measuredWeightTextField.getText()),
                        lastDateWeightTakenDatePicker.getValue(),
                        selectedOffspring.dateOfBirth(),
                        intendedUseComboBox.getValue(),
                        selectedOffspring.sireId()
                );

                // Check if the gender or gestation length has changed
                boolean genderChanged = !offspringGenderComboBox.getValue().equals(initialValuesOffspring.get("gender"));
                boolean gestationLengthChanged = !estimatedGestationSpinner2.getValue().toString().equals(initialValuesOffspring.get("gestationLength"));

                // Update the database tables if necessary
                if (genderChanged) {
                    if (!updateCattleTableGender(Integer.parseInt(selectedOffspring.cattleId()), offspringGenderComboBox.getValue())) {
                        return; // Early return if updating gender failed
                    }
                }

                if (gestationLengthChanged) {
                    if (!updateReproductiveVariablesTable(Integer.parseInt(selectedOffspring.cattleId()), estimatedGestationSpinner2.getValue())) {
                        return; // Early return if updating gestation length failed
                    }
                }

                // Update offspring details in the Offspring table
                OffspringDAO.updateOffspring(updatedUnifiedOffspring);

                // Reload data to reflect changes
                loadOffspringData();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Offspring details updated successfully.");
            } catch (NumberFormatException | SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while updating the offspring details.");
            }
        } else {
            // User chose not to update
            showAlert(Alert.AlertType.INFORMATION, "Cancelled", "Update cancelled by the user.");
        }
    }



    private boolean updateCattleTableGender(int cattleId, String newGender) {
        try {
            boolean success = CattleDAO.updateCattleGender(cattleId, newGender);
            if (!success) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Failed to update cattle gender. Please check if the cattle ID is correct.");
            }
            return success;
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while updating the cattle gender.");
            return false;
        }
    }

    private boolean updateReproductiveVariablesTable(int cattleId, int newGestationLength) {
        try {
            boolean success = ReproductiveVariablesDAO.updateGestationPeriod(cattleId, newGestationLength);
            if (!success) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Failed to update gestation period. Please check if the cattle ID is correct.");
            }
            return success;
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while updating the gestation period.");
            return false;
        }
    }

    // Method to handle deletion of Offspring
    private void handleDeleteOffspring() {
        Optional<ButtonType> result = showConfirmationAlert("Delete Offspring", "Are you sure you want to delete this offspring?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            UnifiedOffspring selectedOffspring = cattleTableView.getSelectionModel().getSelectedItem();
            if (selectedOffspring != null) {
                try {
                    OffspringDAO.deleteOffspringById(selectedOffspring.offspringId());
                    cattleTableView.getItems().remove(selectedOffspring);

                    showAlert(Alert.AlertType.INFORMATION, "Success", "Offspring deleted successfully.");

                    loadOffspringData();

                    if (!cattleTableView.getItems().isEmpty()) {
                        cattleTableView.getSelectionModel().selectFirst();
                        populateFieldsWithSelectedOffspring();
                    } else {
                        handleClearOffspringFields();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while deleting the offspring.");
                }
            }
        }
    }


    //Setting Up Listeners
    // Method to set up table selection listener for Offspring
    private void setupOffspringTableSelectionListener() {
        cattleTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                populateFieldsWithSelectedOffspring();
            }
        });
    }

    // Method to add listeners for field changes
    private void addFieldChangeListenersForOffspring() {
        birthWeightTextField.textProperty().addListener((observable, oldValue, newValue) -> checkForOffspringDetailChanges());
        easeOfCalvingSlider.valueProperty().addListener((observable, oldValue, newValue) -> checkForOffspringDetailChanges());
        estimatedGestationSpinner2.valueProperty().addListener((observable, oldValue, newValue) -> checkForOffspringDetailChanges());
        measuredWeightTextField.textProperty().addListener((observable, oldValue, newValue) -> checkForOffspringDetailChanges());
        lastDateWeightTakenDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> checkForOffspringDetailChanges());
        intendedUseComboBox.valueProperty().addListener((observable, oldValue, newValue) -> checkForOffspringDetailChanges());
        offspringGenderComboBox.valueProperty().addListener((observable, oldValue, newValue) -> checkForOffspringDetailChanges());
        breedingMethodComboBox.valueProperty().addListener((observable, oldValue, newValue) -> checkForOffspringDetailChanges());
    }

    // Method to clear Offspring fields
    private void handleClearOffspringFields() {
        birthWeightTextField.clear();
        easeOfCalvingSlider.setValue(0);
        estimatedGestationSpinner2.getValueFactory().setValue(283);
        measuredWeightTextField.clear();
        lastDateWeightTakenDatePicker.setValue(null);
        intendedUseComboBox.setValue(null);
        offspringGenderComboBox.setValue(null);
        breedingMethodComboBox.setValue(null);
        modifyOffspringDetailsButton.setDisable(true);
        updateOffSpringDetailsButton.setDisable(true);
    }





    //BREEDING ATTEMPT
    // Loading and Populating Data
    private void loadBreedingAttemptsData() throws SQLException {
        try {
            List<BreedingAttempt> breedingAttemptsList = BreedingAttemptDAO.getBreedingAttemptsByCattleId(selectedCattleId);
            ObservableList<BreedingAttempt> tableData = FXCollections.observableArrayList(breedingAttemptsList);

            breedingAttemptsTableView.setItems(tableData);
            if (!tableData.isEmpty()) {
                breedingAttemptsTableView.getSelectionModel().selectFirst();
                populateFieldsWithSelectedBreedingAttempt();
            } else {
                handleClearFieldsForBreedingAttempt();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while loading breeding attempts.");
        }
    }

    // Method to populate fields with selected Breeding Attempt data
    private void populateFieldsWithSelectedBreedingAttempt() {
        BreedingAttempt selectedAttempt = breedingAttemptsTableView.getSelectionModel().getSelectedItem();
        if (selectedAttempt != null) {
            // Remove the listener if it exists
            if (estrusDateChangeListener != null) {
                estrusDatePicker.valueProperty().removeListener(estrusDateChangeListener);
            }
            modifyBreedingAttemptButton.setDisable(false);
            updateBreedingAttemptButton.setDisable(true);
            updateBreedingAttemptButton.setText("Update");
            initializeEstrusDatePicker(estrusDatePicker, selectedCattleId, selectedAttempt);
            initializeAttemptDatePicker(attemptDatePicker,selectedAttempt);
            updateBreedingAttemptDetails(selectedAttempt);
            updateSireDetails(selectedAttempt);
        }
    }

    private void updateBreedingAttemptDetails(BreedingAttempt attempt) {
        breedingAttemptIdLabel.setText(String.valueOf(attempt.breedingAttemptId()));
        estrusDatePicker.setValue(LocalDate.parse(attempt.estrusDate()));
        breedingMethodBreedingAttemptComboBox.setValue(attempt.breedingMethod());
        attemptNumberTextField.setText(computeAttempts());
        attemptDatePicker.setValue(LocalDate.parse(attempt.attemptDate()));
        attemptStatusComboBox.setValue(attempt.attemptStatus());
        notesTextArea.setText(attempt.notes());
    }

    private void updateSireDetails(BreedingAttempt attempt) {
        if (attempt.sireId() != 0) {
            try {
                Cattle cattle = CattleDAO.getCattleByID(attempt.sireId());
                sireNameLabel.setText(Objects.requireNonNull(cattle).getName());
                sireNameButton.setText(Objects.requireNonNull(cattle).getTagId());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            sireNameLabel.setText("N/A");
        }
    }



    //Validation & Population
    // Method to check if Breeding Attempt fields are populated
    private boolean breedingAttemptFieldsArePopulated() {
        return estrusDatePicker.getValue() != null &&
                !sireNameLabel.getText().isEmpty() &&
                breedingMethodBreedingAttemptComboBox.getValue() != null;
    }

    // Method to validate fields for updating Breeding Attempts
    private boolean areFieldsValidForBreedingAttempt() {
        try {
            // Check for non-null values for all required fields
            boolean isEstrusDateValid = estrusDatePicker.getValue() != null;
            boolean isBreedingMethodValid = breedingMethodBreedingAttemptComboBox.getValue() != null;
            boolean isAttemptDateValid = attemptDatePicker.getValue() != null;
            boolean isAttemptStatusValid = attemptStatusComboBox.getValue() != null;

            // Check if breeding method is Natural Mating
            String currentBreedingMethod = breedingMethodBreedingAttemptComboBox.getValue();
            boolean isNaturalMating = "Natural Mating".equals(currentBreedingMethod);

            // Validate sireNameLabel based on breeding method
            boolean isSireNameLabelValid = !isNaturalMating || !sireNameLabel.getText().isEmpty();

            // Combine all validation checks
            return isEstrusDateValid &&
                    isSireNameLabelValid &&  // Only apply validation for sireNameLabel if Natural Mating
                    isBreedingMethodValid &&
                    isAttemptDateValid &&
                    isAttemptStatusValid;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    //Handling Fields
    // Method to handle clearing fields for Breeding Attempt
    private void handleClearFieldsForBreedingAttempt() {
        breedingAttemptIdLabel.setText("N/A");
        estrusDatePicker.setValue(null);
        sireNameLabel.setText("N/A");
        breedingMethodBreedingAttemptComboBox.setValue(null);
        attemptNumberTextField.setText("N/A");
        attemptDatePicker.setValue(null);
        attemptStatusComboBox.setValue(null);
        notesTextArea.clear();
        sireNameButton.setText("Select Sire");
        updateBreedingAttemptButton.setText("Save");
        modifyBreedingAttemptButton.setDisable(true);
        updateBreedingAttemptButton.setDisable(true);

        initializeEstrusDatePickerForNewEntry(estrusDatePicker);
        // Set selected attempt to null
        breedingAttemptsTableView.getSelectionModel().clearSelection();

        // Ensure the listener is defined and add it
        if (estrusDateChangeListener == null) {
            estrusDateChangeListener = (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    attemptDatePicker.setDayCellFactory(null);
                    updateAttemptDatePicker(attemptDatePicker, newValue);
                }
            };
        }
        estrusDatePicker.valueProperty().addListener(estrusDateChangeListener);


    }
    private void updateAttemptDatePicker(DatePicker attemptDatePicker, LocalDate newEstrusDate) {
        LocalDate currentDate = LocalDate.now();
        LocalDate maxDate = newEstrusDate.plusDays(23).isAfter(currentDate) ? currentDate : newEstrusDate.plusDays(23);

        configureDatePicker(attemptDatePicker, newEstrusDate, maxDate);
    }
    // Method to check for changes or validate fields based on button text
    private void checkForBreedingAttemptDetailChanges() {
        String estrusDateValue = estrusDatePicker.getValue() != null ? estrusDatePicker.getValue().toString() : null;
        String sireUsedValue = sireNameLabel.getText() != null ? sireNameLabel.getText() : null;
        String breedingMethodValue = breedingMethodBreedingAttemptComboBox.getValue() != null ? breedingMethodBreedingAttemptComboBox.getValue() : null;
        String attemptDateValue = attemptDatePicker.getValue() != null ? attemptDatePicker.getValue().toString() : null;
        String attemptStatusValue = attemptStatusComboBox.getValue() != null ? attemptStatusComboBox.getValue() : null;
        String notesValue = notesTextArea.getText() != null ? notesTextArea.getText() : null;

        String selectedEstrusDate = getSelectedBreedingAttemptValue("estrusDate");
        String selectedSireId = getSelectedBreedingAttemptValue("sireId");
        String selectedBreedingMethod = getSelectedBreedingAttemptValue("breedingMethod");
        String selectedAttemptDate = getSelectedBreedingAttemptValue("attemptDate");
        String selectedAttemptStatus = getSelectedBreedingAttemptValue("attemptStatus");
        String selectedNotes = getSelectedBreedingAttemptValue("notes");

        boolean hasChanges =
                !Objects.equals(estrusDateValue, selectedEstrusDate) ||
                        !Objects.equals(sireUsedValue, selectedSireId) ||
                        !Objects.equals(breedingMethodValue, selectedBreedingMethod) ||
                        !Objects.equals(attemptDateValue, selectedAttemptDate) ||
                        !Objects.equals(attemptStatusValue, selectedAttemptStatus) ||
                        !Objects.equals(notesValue, selectedNotes);

        boolean areFieldsValid = areFieldsValidForBreedingAttempt();

        String buttonText = updateBreedingAttemptButton.getText();
        if ("Update".equals(buttonText)) {
            // Enable the button if there are changes
            updateBreedingAttemptButton.setDisable(!hasChanges);
        } else if ("Save".equals(buttonText)) {
            // Enable the button if all fields are valid
            updateBreedingAttemptButton.setDisable(!areFieldsValid);
        }
    }

    // Helper method to retrieve selected Breeding Attempt value based on property name
    private String getSelectedBreedingAttemptValue(String propertyName) {
        BreedingAttempt selectedAttempt = breedingAttemptsTableView.getSelectionModel().getSelectedItem();
        if (selectedAttempt == null) return "";

        return switch (propertyName) {
            case "estrusDate" -> selectedAttempt.estrusDate();
            case "sireId" -> getSireName(selectedAttempt.sireId());
            case "breedingMethod" -> selectedAttempt.breedingMethod();
            case "attemptDate" -> selectedAttempt.attemptDate();
            case "attemptStatus" -> selectedAttempt.attemptStatus();
            case "notes" -> selectedAttempt.notes();
            default -> "";
        };
    }

    // Helper method to check if Breeding Attempt fields have changed
    private boolean breedingAttemptFieldsHaveChanged() {
        BreedingAttempt selectedAttempt = breedingAttemptsTableView.getSelectionModel().getSelectedItem();
        if (selectedAttempt == null) return false;

        String currentBreedingMethod = breedingMethodBreedingAttemptComboBox.getValue();
        boolean isNaturalMating = "Natural Mating".equals(currentBreedingMethod);

        boolean sireNameLabelChanged = !isNaturalMating || !sireNameLabel.getText().equals(getSireName(selectedAttempt.sireId()));

        return !estrusDatePicker.getValue().toString().equals(selectedAttempt.estrusDate()) ||
                !sireNameLabelChanged ||  // Include sireNameLabel change check
                !currentBreedingMethod.equals(selectedAttempt.breedingMethod()) ||
                !attemptDatePicker.getValue().toString().equals(selectedAttempt.attemptDate()) ||
                !attemptStatusComboBox.getValue().equals(selectedAttempt.attemptStatus()) ||
                !notesTextArea.getText().equals(selectedAttempt.notes());
    }



    //Update and Delete Operations:
    // Method to handle the modification of a Breeding Attempt
    @FXML
    private void modifyBreedingAttempt() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Modify Breeding Attempt");
        alert.setHeaderText(null);

        if (breedingAttemptFieldsArePopulated() && !breedingAttemptFieldsHaveChanged()) {
            showActionDialog(alert, "Delete Record", this::handleClearFieldsForBreedingAttempt, this::handleDeleteBreedingAttempt);
        } else if (breedingAttemptFieldsHaveChanged()) {
            showActionDialog(alert, "Reset Fields", this::handleClearFieldsForBreedingAttempt, this::populateFieldsWithSelectedBreedingAttempt);
        }else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a breeding attempt from the table.");
        }
    }



    // Method to handle deletion of a Breeding Attempt
    private void handleDeleteBreedingAttempt() {
        Optional<ButtonType> result = showConfirmationAlert("Delete Breeding Attempt", "Are you sure you want to delete this breeding attempt?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            BreedingAttempt selectedAttempt = breedingAttemptsTableView.getSelectionModel().getSelectedItem();
            if (selectedAttempt != null) {
                try {
                    BreedingAttemptDAO.deleteBreedingAttemptById(selectedAttempt.breedingAttemptId());
                    breedingAttemptsTableView.getItems().remove(selectedAttempt);
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Breeding attempt deleted successfully.");
                    loadBreedingAttemptsData();

                    if (!breedingAttemptsTableView.getItems().isEmpty()) {
                        breedingAttemptsTableView.getSelectionModel().selectFirst();
                        populateFieldsWithSelectedBreedingAttempt();
                    } else {
                        handleClearFieldsForBreedingAttempt();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while deleting breeding attempt.");
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a breeding attempt from the table.");
            }
        }
    }

    // Method to handle sire tag selection
    @FXML
    private void handleSireTagSelection() {
        List<Cattle> cattleList;
        try {
            cattleList = CattleDAO.getCattleForGender("Male");
            CattleDialogUtils.handleCattleTagID(sireNameButton, cattleList, null, sireNameButton, null, sireNameLabel);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Helper method to get the name of the sire based on sire ID
    private String getSireName(int sireId) {
        String sireName = "N/A";
        if (sireId != 0) {
            try {
                Cattle cattle = CattleDAO.getCattleByID(sireId);
                if (cattle != null) {
                    sireName = cattle.getName();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return sireName;
    }

    // Utility and Listener Setup:
// Method to compute the attempt number
    private String computeAttempts() {
        BreedingAttempt selectedAttempt = breedingAttemptsTableView.getSelectionModel().getSelectedItem();
        if (selectedAttempt != null) {
            int cattleId = selectedAttempt.cattleId();
            LocalDate estrusDate = LocalDate.parse(selectedAttempt.estrusDate());
            LocalDate attemptDate = LocalDate.parse(selectedAttempt.attemptDate());

            List<BreedingAttempt> breedingAttempts;
            try {
                breedingAttempts = BreedingAttemptDAO.getBreedingAttemptsByCattleIdAndEstrusDate(cattleId, estrusDate);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            int attemptNumber = 0;
            for (BreedingAttempt attempt : breedingAttempts) {
                LocalDate currentAttemptDate = LocalDate.parse(attempt.attemptDate());
                if (!currentAttemptDate.isAfter(attemptDate)) {
                    attemptNumber++;
                }
            }

            return ordinal(attemptNumber) + " Attempt";
        }
        return "N/A";
    }


    private String ordinal(int number) {
        String[] suffixes = {"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
        if (number % 100 >= 11 && number % 100 <= 13) {
            return number + "th";
        } else {
            return number + suffixes[number % 10];
        }
    }

    // Method to set up table selection listener for Breeding Attempts
    private void setupBreedingAttemptsTableSelectionListener() {
        breedingAttemptsTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                populateFieldsWithSelectedBreedingAttempt();
            }
        });
    }

    // Method to add field change listeners for Breeding Attempt
    private void addFieldChangeListenersForBreedingAttempt() {
        estrusDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Check if attemptDatePicker is not null and clear its value if necessary
            if (attemptDatePicker.getValue() != null) {
                attemptDatePicker.setValue(null);
            }
            // Call the method to check for other breeding attempt detail changes
            checkForBreedingAttemptDetailChanges();
        });
        sireNameLabel.textProperty().addListener((observable, oldValue, newValue) -> checkForBreedingAttemptDetailChanges());
        breedingMethodBreedingAttemptComboBox.valueProperty().addListener((observable, oldValue, newValue) -> checkForBreedingAttemptDetailChanges());
        notesTextArea.textProperty().addListener((observable, oldValue, newValue) -> checkForBreedingAttemptDetailChanges());
        attemptDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> checkForBreedingAttemptDetailChanges());
        attemptStatusComboBox.valueProperty().addListener((observable, oldValue, newValue) -> checkForBreedingAttemptDetailChanges());
        estimatedGestationPeriodSpinner.valueProperty().addListener((observable, oldValue, newValue) -> checkForBreedingAttemptDetailChanges());
    }




    @FXML
    private void updateBreedingAttempt() {

        if (Objects.equals(updateBreedingAttemptButton.getText(), "Save")) {
            showSaveDialog();
            return;
        }
        if (Objects.equals(updateBreedingAttemptButton.getText(), "Update")) {
            BreedingAttempt selectedAttempt = breedingAttemptsTableView.getSelectionModel().getSelectedItem();

            if (selectedAttempt == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a breeding attempt from the table.");
                return;
            }

            if (areFieldsValidForBreedingAttempt()) {
                showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please ensure all fields are correctly filled.");
                return;
            }

            Optional<ButtonType> result = showConfirmationAlert("Update Record", "Are you sure you want to update the breeding attempt details?");
            if (result.isPresent() && result.get() == ButtonType.OK) {
                int sireId;
                String sireNameText = sireNameLabel.getText();
                String sireTagText = sireNameButton.getText();
                if (sireNameText == null || sireNameText.equals("N/A") || sireNameText.isEmpty()) {
                    sireId = 0;
                } else {
                    try {
                        Cattle cattle = CattleDAO.getCattleByTagAndName(sireTagText, sireNameText);
                        sireId = Objects.requireNonNull(cattle).getCattleId();

                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }


                }


                try {
                    BreedingAttempt updatedAttempt = new BreedingAttempt(
                            selectedAttempt.breedingAttemptId(),
                            selectedAttempt.cattleId(),
                            estrusDatePicker.getValue().toString(),
                            breedingMethodBreedingAttemptComboBox.getValue(),
                            sireId,
                            notesTextArea.getText(),
                            attemptDatePicker.getValue().toString(),
                            attemptStatusComboBox.getValue()
                    );

                    BreedingAttemptDAO.updateBreedingAttempt(updatedAttempt);
                    loadBreedingAttemptsData();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Breeding attempt details updated successfully.");
                } catch (NumberFormatException | SQLException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while updating breeding attempt details.");
                }
            }
        }

    }
    private void showSaveDialog() {
        Alert saveAlert = new Alert(Alert.AlertType.CONFIRMATION);
        saveAlert.setTitle("Save Breeding Attempt");
        saveAlert.setHeaderText(null);
        saveAlert.setContentText("Do you want to save the new breeding attempt details?");

        ButtonType saveButton = new ButtonType("Save");
        ButtonType clearButton = new ButtonType("Clear");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        saveAlert.getButtonTypes().setAll(saveButton, clearButton, cancelButton);

        Optional<ButtonType> result = saveAlert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == saveButton) {
                saveBreedingAttempt();
            } else if (result.get() == clearButton) {
                handleClearFieldsForBreedingAttempt();
            }
        }
    }

    private void saveBreedingAttempt() {
        if (!areFieldsValidForBreedingAttempt()) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please ensure all Mandatory fields are correctly filled.");
            return;
        }

        try {
            int sireId;
            String sireNameText = sireNameLabel.getText();
            String sireTagText = sireNameButton.getText();
            if (sireNameText == null || sireNameText.equals("N/A") || sireNameText.isEmpty()) {
                sireId = 0;
            } else {
                Cattle cattle = CattleDAO.getCattleByTagAndName(sireTagText, sireNameText);
                sireId = Objects.requireNonNull(cattle).getCattleId();
            }

            BreedingAttempt newAttempt = new BreedingAttempt(
                    0,
                    selectedCattleId,
                    estrusDatePicker.getValue().toString(),
                    breedingMethodBreedingAttemptComboBox.getValue(),
                    sireId,
                    notesTextArea.getText(),
                    attemptDatePicker.getValue().toString(),
                    attemptStatusComboBox.getValue()
            );

            BreedingAttemptDAO.saveBreedingAttempt(newAttempt);
            loadBreedingAttemptsData();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Breeding attempt saved successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while saving breeding attempt details.");
        }
    }



    private Optional<ButtonType> showConfirmationAlert(String title, String content) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle(title);
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText(content);
        return confirmationAlert.showAndWait();
    }




    public void initializeEstrusDatePickerForNewEntry(DatePicker estrusDatePicker) {
        try {
            LocalDate currentDate = LocalDate.now();

            // Fetch the cattle's birthdate and determine maturity date
            Cattle cattle = CattleDAO.getCattleByID(selectedCattleId);
            if (cattle == null) {
                throw new SQLException("Cattle not found for ID: " + selectedCattleId);
            }
            LocalDate maturityDate = cattle.getDateOfBirth().plusMonths(MATURITY_AGE_MONTHS);


            // Fetch existing estrus cycles and disable specific dates
            List<LocalDate> disabledDateRanges = getDisabledDateRanges(selectedCattleId);

            // Configure the date picker
            configureDatePicker(estrusDatePicker, maturityDate, currentDate, disabledDateRanges);

        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
            // Handle the error, e.g., show an error message to the user
        }
    }

    private List<LocalDate> getDisabledDateRanges(int cattleId) throws SQLException {
        List<LocalDate> disabledRanges = new ArrayList<>();

        List<BreedingAttempt> breedingAttempts = BreedingAttemptDAO.getBreedingAttemptsByCattleId(cattleId);
        for (BreedingAttempt attempt : breedingAttempts) {

            if ("Success".equals(attempt.attemptStatus())) {
                LocalDate breedingDate =LocalDate.parse(attempt.attemptDate());
                LocalDate estrusDate = LocalDate.parse(attempt.estrusDate());

                ReproductiveVariables reproductiveVariables = reproductiveVariablesDAO.getReproductiveVariableByCattleIdAndBreedingDate(cattleId, breedingDate);

                if (reproductiveVariables == null) {
                    handleMissingReproductiveVariables(cattleId, breedingDate);
                }

                if (Objects.requireNonNull(reproductiveVariables).getCalvingDate() != null) {
                    LocalDate endDate = reproductiveVariables.getCalvingDate().plusDays(POST_CALVING_INTERVAL);
                    disabledRanges.add(estrusDate);
                    disabledRanges.add(endDate);
                } else {
                    LocalDate endDate = estrusDate.plusDays(24); // Default estrus cycle length
                    disabledRanges.add(estrusDate);
                    disabledRanges.add(endDate);
                }

            }
        }

        return disabledRanges;
    }



    private boolean isDateInDisabledRanges(LocalDate date, List<LocalDate> disabledRanges) {
        for (int i = 0; i < disabledRanges.size(); i += 2) {
            LocalDate start = disabledRanges.get(i);
            LocalDate end = disabledRanges.get(i + 1);
            if (!date.isBefore(start) && !date.isAfter(end)) {
                return true;
            }
        }
        return false;
    }

    // Method to initialize DatePicker for an existing breeding attempt
    public void initializeEstrusDatePicker(DatePicker estrusDatePicker, int cattleId, BreedingAttempt selectedAttempt) {
        try {
            LocalDate minDate = calculateMinimumEstrusDate(cattleId, selectedAttempt);
            LocalDate maxDate = calculateMaximumDate(cattleId, selectedAttempt, minDate);
            configureDatePicker(estrusDatePicker, minDate, maxDate);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public LocalDate calculateMinimumEstrusDate(int cattleId, BreedingAttempt selectedAttempt) throws SQLException {
        LocalDate currentEstrusDate = LocalDate.parse(selectedAttempt.estrusDate());

        LocalDate previousEstrusDate = BreedingAttemptDAO.getPreviousEstrusDate(selectedCattleId, currentEstrusDate);

        return calculateMinDateBasedOnPreviousEstrus(cattleId, previousEstrusDate);
    }

    private LocalDate calculateMinDateBasedOnPreviousEstrus(int cattleId, LocalDate previousEstrusDate) throws SQLException {
        if (previousEstrusDate == null) {
            return calculateMinDateForNoPreviousEstrus(cattleId);
        }

        List<BreedingAttempt> previousAttempts = BreedingAttemptDAO.getBreedingAttemptsByCattleIdAndEstrusDate(cattleId, previousEstrusDate);
        Optional<BreedingAttempt> successfulAttempt = previousAttempts.stream()
                .filter(attempt -> "Success".equals(attempt.attemptStatus()))
                .findFirst();

        if (successfulAttempt.isPresent()) {
            LocalDate breedingDate = LocalDate.parse(successfulAttempt.get().attemptDate());
            return calculateMinDateBasedOnSuccessfulAttempt(cattleId, breedingDate);
        } else {
            return previousEstrusDate.plusDays(24); // Handle case where no successful breeding attempt found
        }
    }

    private LocalDate calculateMinDateForNoPreviousEstrus(int cattleId) throws SQLException {
        Cattle cattle = CattleDAO.getCattleByID(cattleId);
        if (cattle == null) {
            throw new SQLException("Cattle not found for ID: " + cattleId);
        }
        return cattle.getDateOfBirth().plusMonths(MATURITY_AGE_MONTHS);
    }

    private LocalDate calculateMinDateBasedOnSuccessfulAttempt(int cattleId, LocalDate breedingDate) throws SQLException {
        ReproductiveVariables reproductiveVariables = reproductiveVariablesDAO.getReproductiveVariableByCattleIdAndBreedingDate(cattleId, breedingDate);

        if (reproductiveVariables == null) {
            handleMissingReproductiveVariables(cattleId, breedingDate);
            //The time between heat cycles (called the estrous cycle) is usually around 21 days, but can range from 18 to 24 days.
            return breedingDate.plusDays(24); // Use a default minimum based on previous estrus date successful attempt
        }

        if (reproductiveVariables.getCalvingDate() != null) {
            return reproductiveVariables.getCalvingDate().plusDays(POST_CALVING_INTERVAL);
        } else {
            return calculateMinDateBasedOnNextAttemptOrEstimatedCalving(cattleId, breedingDate, reproductiveVariables);
        }
    }

    private LocalDate calculateMinDateBasedOnNextAttemptOrEstimatedCalving(int cattleId, LocalDate breedingDate, ReproductiveVariables reproductiveVariables) throws SQLException {
        ReproductiveVariables nextAttempt = reproductiveVariablesDAO.getNextBreedingAttempt(cattleId, breedingDate);
        if (nextAttempt != null) {
            //The time between heat cycles (called the estrous cycle) is usually around 21 days, but can range from 18 to 24 days.
            return nextAttempt.getBreedingDate().minusDays(24);
        } else {
            LocalDate estimatedCalvingDate = breedingDate.plusDays(reproductiveVariables.getGestationPeriod());
            return estimatedCalvingDate.plusDays(POST_CALVING_INTERVAL);
        }
    }


    private void handleMissingReproductiveVariables(int cattleId, LocalDate breedingDate) {
        // Add missing reproductive variables with default values
        ReproductiveVariables missingReproductiveVariables = new ReproductiveVariables();
        missingReproductiveVariables.setCattleID(cattleId);
        int gestationPeriod = estimatedGestationPeriodSpinner.getValue();

        addingReproductiveVariables.set(true);

        missingReproductiveVariables.setBreedingDate(breedingDate);
        missingReproductiveVariables.setGestationPeriod(gestationPeriod);
        int reproductiveVariableId = reproductiveVariablesDAO.addReproductiveVariableAndGetId(missingReproductiveVariables);

        if (reproductiveVariableId != -1) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "This missing Reproductive Data added successfully.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add reproductive data.");
        }
        addingReproductiveVariables.set(false);
    }


    public LocalDate calculateMaximumDate(int cattleId, BreedingAttempt selectedAttempt, LocalDate minDate) throws SQLException {
        LocalDate currentEstrusDate = LocalDate.parse(selectedAttempt.estrusDate());
        LocalDate previousEstrusDate = BreedingAttemptDAO.getPreviousEstrusDate(cattleId, currentEstrusDate);

        return calculateMaxDateBasedOnNextEstrus(cattleId, previousEstrusDate, minDate, currentEstrusDate);
    }

    private LocalDate calculateMaxDateBasedOnNextEstrus(int cattleId, LocalDate previousEstrusDate, LocalDate minDate, LocalDate currentEstrusDate) throws SQLException {
        LocalDate nextEstrusDate = BreedingAttemptDAO.getSubsequentEstrusDate(cattleId, currentEstrusDate);


        if(nextEstrusDate== null && previousEstrusDate == null){
            int reasonableMonths = 6;  // Set this value based on your requirements
            return minDate.plusMonths(reasonableMonths);
        }else {
            if (nextEstrusDate!= null) {
                return calculateMaxDateForBasedOnNextEstrus(cattleId, nextEstrusDate, minDate);
            }else return minDate.plusMonths(6);
        }


    }

    private LocalDate calculateMaxDateForBasedOnNextEstrus(int cattleId, LocalDate nextEstrusDate, LocalDate minDate) throws SQLException {

        List<BreedingAttempt> nextAttempts = BreedingAttemptDAO.getBreedingAttemptsByCattleIdAndEstrusDate(cattleId, nextEstrusDate);
        Optional<BreedingAttempt> successfulAttempt = nextAttempts.stream()
                .filter(attempt -> "Success".equals(attempt.attemptStatus()))
                .findFirst();

        return successfulAttempt.map(breedingAttempt -> LocalDate.parse(breedingAttempt.estrusDate())).orElseGet(() -> minDate.plusMonths(3));


    }
    // Private helper method to set the DayCellFactory for the DatePicker
    private void configureDatePicker(DatePicker datePicker, LocalDate minDate, LocalDate maxDate) {
        Callback<DatePicker, DateCell> dayCellFactory = dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);

                if (item.isBefore(minDate) || item.isAfter(maxDate)) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                    setTooltip(new Tooltip("Date outside the valid range"));
                }
            }
        };

        datePicker.setDayCellFactory(dayCellFactory);
    }
    private void configureDatePicker(DatePicker datePicker, LocalDate minDate, LocalDate maxDate, List<LocalDate> disabledDates) {
        datePicker.setDayCellFactory(cellDatePicker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                boolean isDisabled = empty || date.isBefore(minDate) || date.isAfter(maxDate) || isDateInDisabledRanges(date, disabledDates);
                setDisable(isDisabled);

                if (isDisabled) {
                    setStyle("-fx-background-color: #ffc0cb;"); // Pink color for disabled cells
                } else {
                    setStyle(""); // Reset style for enabled cells
                }
            }
        });
    }
    public void initializeAttemptDatePicker(DatePicker attemptDatePicker, BreedingAttempt selectedAttempt) {

        try {
            // Fetch all existing breeding attempts for the specified cattle
            List<BreedingAttempt> filteredAttempts = BreedingAttemptDAO.getBreedingAttemptsByCattleIdAndEstrusDate(
                    selectedCattleId, LocalDate.parse(selectedAttempt.estrusDate()));

            LocalDate currentDate = LocalDate.now();
            LocalDate selectedEstrusDate = LocalDate.parse(selectedAttempt.estrusDate());
            LocalDate selectedAttemptDate = LocalDate.parse(selectedAttempt.attemptDate());

            // Sort the filtered attempts by attemptDate in ascending order
            filteredAttempts.sort(Comparator.comparing(attempt -> LocalDate.parse(attempt.attemptDate())));

            // Initialize previous and subsequent attempts
            BreedingAttempt previousAttempt = null;
            BreedingAttempt subsequentAttempt = null;

            // Find the index of the selected attempt
            int selectedIndex = -1;
            for (int i = 0; i < filteredAttempts.size(); i++) {
                BreedingAttempt attempt = filteredAttempts.get(i);
                LocalDate attemptDate = LocalDate.parse(attempt.attemptDate());

                if (attemptDate.isEqual(selectedAttemptDate)) {
                    selectedIndex = i;
                    break;
                }
            }

            // Determine the previous and subsequent attempts
            if (selectedIndex != -1) {
                if (selectedIndex > 0) {
                    previousAttempt = filteredAttempts.get(selectedIndex - 1);
                }

                if (selectedIndex < filteredAttempts.size() - 1) {
                    subsequentAttempt = filteredAttempts.get(selectedIndex + 1);
                }
            }

            // Configure the DatePicker based on the positions of previous and subsequent attempts
            LocalDate minDate;
            LocalDate maxDate;

            if (filteredAttempts.size() == 1) {
                // Only one attempt
                minDate = selectedEstrusDate;
                maxDate = currentDate.isAfter(selectedEstrusDate.plusDays(23)) ? currentDate : selectedEstrusDate.plusDays(23);
            } else if (previousAttempt == null && subsequentAttempt != null) {
                // First attempt
                minDate = selectedEstrusDate;
                maxDate = LocalDate.parse(subsequentAttempt.attemptDate()).minusDays(1);
            } else if (previousAttempt != null && subsequentAttempt != null) {
                // Middle attempt
                minDate = LocalDate.parse(previousAttempt.attemptDate()).plusDays(1);
                maxDate = LocalDate.parse(subsequentAttempt.attemptDate()).minusDays(1);
            } else if (previousAttempt != null) {
                // Last attempt
                minDate = LocalDate.parse(previousAttempt.attemptDate()).plusDays(1);
                maxDate = selectedEstrusDate.plusDays(23).isAfter(currentDate) ? currentDate : selectedEstrusDate.plusDays(23);
            } else {
                // Fallback case
                minDate = selectedEstrusDate;
                maxDate = currentDate;
            }

            // Configure the DatePicker with the calculated min and max dates
            configureDatePicker(attemptDatePicker, minDate, maxDate);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while initializing the Attempt Date Picker.");
        }
    }


    private void showActionDialog(Alert alert, String button2Text, Runnable onClear, Runnable onButton2) {
        alert.setContentText("Choose an action:");
        ButtonType button1 = new ButtonType("Clear");
        ButtonType button2 = new ButtonType(button2Text);
        dialogButton(alert, onClear, onButton2, button1, button2);
    }
    private void showActionDialog(Alert alert, Runnable onButton1, Runnable onButton2) {
        alert.setContentText("Choose an action:");
        ButtonType button1 = new ButtonType("Reset");
        ButtonType button2 = new ButtonType("Delete");
        dialogButton(alert, onButton1, onButton2, button1, button2);
    }

    private void dialogButton(Alert alert, Runnable onButton1, Runnable onButton2, ButtonType button1, ButtonType button2) {
        ButtonType button3 = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(button1, button2, button3);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == button1) {
                onButton1.run();
            } else if (result.get() == button2) {
                onButton2.run();
            }
        }
    }

    private void showActionDialog(Alert alert, Runnable onButton1) {
        alert.setContentText("Choose an action:");
        ButtonType button1 = new ButtonType("Delete Record");
        ButtonType button2 = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(button1, button2);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == button1) {
                onButton1.run();
            }
        }
    }








    //LACTATION PERIOD CODE SECTION

    private void initializeLactationPeriods() {
        // Disable the date picker initially if no cattle is selected
        if (selectedCattleId == 0) {
            lactationEndDatePicker.setDisable(true);
        }

        // Add listener for when the entire selected cattle changes
        SelectedCattleManager.getInstance().selectedCattleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                handleCattleSelection(newValue);  // Pass the new cattle object to handle the selection change
            }
        });
    }

    private void handleCattleSelection(Cattle selectedCattle) {
        selectedCattleId = selectedCattle.getCattleId();           // Set cattle ID
        String selectedCattleGender = selectedCattle.getGender();         // Set cattle gender

        // Check if the cattle is female and has a valid ID
        if (selectedCattleId != 0 && "Female".equals(selectedCattleGender)) {
            clearLactationFieldsOnCattleIdChange();           // Clear fields when ID changes
            initializeLactationTableColumns();                // Set up the table columns
            initializeTableViewSelectionListener();           // Add listener for table selections
            loadLactationPeriodsForSelectedCattle();          // Load data based on selected cattle
            initializeLactationEndDatePicker();               // Initialize the lactation end date picker
        }
    }




    private void initializeLactationTableColumns() {
        selectionColumn.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        selectionColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectionColumn));

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        startDateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                cellData.getValue().getLactationPeriod().startDate()));
        startDateColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.format(dateFormatter));
                    setAlignment(Pos.CENTER);
                }
            }
        });

        endDateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                cellData.getValue().getLactationPeriod().endDate()));
        endDateColumn.setCellFactory(new MissingEndDateCellFactory(dateFormatter));

    }


    private void loadLactationPeriodsForSelectedCattle() {
        if (selectedCattleId == 0) {
            lactationTableView.getItems().clear();
            lactationTableView.refresh();  // Ensure the table view is refreshed
            return;
        }

        try {
            // Clear the current table items
            lactationTableView.getItems().clear();

            List<LactationPeriod> lactationPeriods = LactationPeriodDAO.getLactationPeriodsByCattleId(selectedCattleId);

            // Sort lactation periods by start date (most recent first)
            lactationPeriods.sort(Comparator.comparing(LactationPeriod::startDate).reversed());

            LocalDate currentDate = LocalDate.now();

            // Populate table with lactation periods
            ObservableList<LactationPeriodWithSelection> observableList = FXCollections.observableArrayList(
                    lactationPeriods.stream()
                            .map(LactationPeriodWithSelection::new)
                            .collect(Collectors.toList())
            );
            lactationTableView.setItems(observableList);
            lactationTableView.refresh();  // Ensure the table view is refreshed

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

                if (mostRecentPeriod.endDate() == null) {
                    long daysSinceStart = ChronoUnit.DAYS.between(mostRecentPeriod.startDate(), currentDate);
                    if (daysSinceStart <= 365) {
                        // Set the label text to "start date - now"
                        currentPeriodLabel.setText(mostRecentPeriod.startDate() + " - now");
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
            lactationStartDateField.setText(String.valueOf(period.startDate()));
            lactationEndDatePicker.setValue(period.endDate() != null ? period.endDate() : null);
            originalEndDate = period.endDate(); // Set the original end date
            selectedEndDate = originalEndDate; // Initially, selected end date is the same as original

            if(lactationEndDatePicker.getValue() == null){
                clearButton.setDisable(true);
            }

        } else {
            lactationStartDateField.clear();
            originalEndDate = null;
            selectedEndDate = null;
            milkYieldLabel.setText("");
            averageMilkYieldLabel.setText("");
            productionSessionDatePicker.setValue(null);
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
                lactationPeriods.sort(Comparator.comparing(LactationPeriod::startDate).reversed());

                // Find the most recent lactation period
                Optional<LactationPeriod> mostRecentPeriod = lactationPeriods.stream().findFirst();

                mostRecentPeriod.ifPresent(period -> {
                    // Check if the most recent lactation period is within 365 days of the current date
                    if (ChronoUnit.DAYS.between(period.startDate(), currentDate) <= 365) {
                        ongoingLactationPeriod = period;
                        populateFieldsBasedOnDateAndLactationPeriod(LocalDate.now(), ongoingLactationPeriod.lactationPeriodID());
                        checkExistingSessionsAndUpdateButton(LocalDate.now(), ongoingLactationPeriod.lactationPeriodID());
                        // Display the start date of the ongoing lactation period if it exists
                        ongoingLactationPeriodLabel.setText(ongoingLactationPeriod.startDate().toString());

                        if (selectedPeriodWithSelection != null && selectedPeriodWithSelection.getLactationPeriod() != null) {
                            LocalDate selectedStartDate = selectedPeriodWithSelection.getLactationPeriod().startDate();

                            // Check if the selected period matches the ongoing period
                            if (selectedStartDate.equals(ongoingLactationPeriod.startDate())) {
                                LocalDate startDate = ongoingLactationPeriod.startDate();
                                long totalDays = Duration.between(startDate.atStartOfDay(), currentDate.atStartOfDay()).toDays();
                                productionSessionDatePicker.setValue(currentDate);
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


                                List<ProductionSession> productionSessionsBySelectedStage;
                                try {
                                    productionSessionsBySelectedStage = ProductionSessionDAO.getProductionSessionsByLactationIdAndDateRange(ongoingLactationPeriod.lactationPeriodID(), startDate, LocalDate.now());
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }

                                List<ProductionSession> productionSessionsByDate;
                                try {
                                    productionSessionsByDate = ProductionSessionDAO.getProductionSessionsByLactationIdAndDate(ongoingLactationPeriod.lactationPeriodID(), LocalDate.now());
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }

                                double lactationStageMilkYield = calculateTotalYield(productionSessionsBySelectedStage);
                                double todayYield = calculateTotalYield(productionSessionsByDate);
                                double averageDailyYield = calculateAverageDailyYield(lactationStageMilkYield, startDate, LocalDate.now());

                                milkYieldLabel.setText(String.valueOf(todayYield));
                                averageMilkYieldLabel.setText(String.valueOf(averageDailyYield));


                                // Update UI elements based on the production stage
                                updateVolumeLabels(stage);
                                updateStagePeriodLabel(stage);

                                // If the selected period is ongoing, enable fields
                                setFieldsDisabled(false);

                                clearAndDisableChoiceBoxes();
                                selectCriteriaChoiceBox.setDisable(false);
                            } else {
                                // If the selected period is not ongoing, disable fields
                                setFieldsDisabled(true);
                                productionSessionDatePicker.setValue(null);
                                clearAndDisableChoiceBoxes();
                                selectCriteriaChoiceBox.setDisable(true);

                            }
                        }



                        productionSessionDatePicker.valueProperty().addListener((observables, oldDate, newDate) -> {
                            if (newDate != null) {
                                selectedDateProductionSessionDate = newDate;

                                // Check if ongoingLactationPeriod is not null
                                if (ongoingLactationPeriod != null) {
                                    long totalDays = Duration.between(ongoingLactationPeriod.startDate().atStartOfDay(), selectedDateProductionSessionDate.atStartOfDay()).toDays();
                                    String stage = calculateProductionStage((int) totalDays);
                                    productionStageLabel.setText(stage);
                                    updateVolumeLabels(stage);

                                    refreshUI();

                                    if (selectCriteriaChoiceBox.getSelectionModel().getSelectedItem() != null &&
                                            dependentCriteriaChoiceBox.getSelectionModel().getSelectedItem() != null) {
                                        compareCattleProduction(); // Call your method to compare cattle production
                                    } else {
                                        clearAndDisableChoiceBoxes();
                                        dependentCriteriaChoiceBox.setDisable(true);
                                    }
                                } else {
                                    // Handle the case where ongoingLactationPeriod is null
                                    productionStageLabel.setText("No lactation period data available");
                                    updateVolumeLabels("Unknown");
                                    clearAndDisableChoiceBoxes();
                                    dependentCriteriaChoiceBox.setDisable(true);
                                }
                            }
                        });



                    }
                });

                // If no ongoing period is found, disable fields
                if (ongoingLactationPeriod == null) {
                    setFieldsDisabled(true);
                    ongoingLactationPeriodLabel.setText(""); // Clear the label if no ongoing period
                }

                // Set up the date cell factory for the productionSessionDatePicker
                setUpDateCellFactory();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

    }

    private void setUpDateCellFactory() {
        productionSessionDatePicker.setDayCellFactory(datePicker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    return;
                }

                if (ongoingLactationPeriod != null) {
                    // Ongoing period exists
                    setDisable(item.isBefore(ongoingLactationPeriod.startDate()) || item.isAfter(LocalDate.now()));
                } else {
                    // No ongoing period - disable all dates
                    setDisable(true);
                }

                // Add styling for disabled dates (optional)
                if (isDisabled()) {
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });
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
                LocalDate minDate = selectedPeriod.getLactationPeriod().startDate();
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
                .sorted(Comparator.comparing(LactationPeriod::startDate).reversed())
                .toList();

        LocalDate selectedStartDate = selectedPeriod.getLactationPeriod().startDate();
        LocalDate maxEndDate;

        int selectedIndex = lactationPeriods.indexOf(selectedPeriod.getLactationPeriod());

        if (selectedIndex == 0 || lactationPeriods.size() == 1) {
            // If the selected period is the most recent or there's only one period, maximum end date is one year after start date
            maxEndDate = selectedStartDate.plusYears(1);
        } else {
            // If there are multiple periods and the selected one is not the most recent
            LactationPeriod nextPeriod = lactationPeriods.get(selectedIndex - 1);
            LocalDate nextStartDate = nextPeriod.startDate();

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
        if (newEndDate != null && newEndDate.isBefore(selectedPeriod.startDate())) {
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
                // Create a new LactationPeriod instance with the updated end date
                LactationPeriod updatedPeriod = new LactationPeriod(
                        selectedPeriod.lactationPeriodID(),
                        selectedPeriod.cattleID(),
                        selectedPeriod.startDate(),
                        newEndDate
                );

                // Call DAO to update the record in the database
                LactationPeriodDAO.updateLactationPeriodEndDate(updatedPeriod.lactationPeriodID(), newEndDate);
                loadLactationPeriodsForSelectedCattle();

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
                        LactationPeriodDAO.updateLactationPeriodEndDate(selectedPeriodWithSelection.getLactationPeriod().lactationPeriodID(), null);
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

    public void initializeProductionStages() {
        initializeStageDaysRangeMap();
        initializeVolumeLabels();
        setFieldsDisabled(true);

        productionStageLabel.textProperty().addListener((observable, oldValue, newValue) -> setFieldsDisabled(newValue == null || (!newValue.equals("Colostrum Stage")
                && !newValue.equals("Transition Stage")
                && !newValue.equals("Peak Milk Harvesting")
                && !newValue.equals("Mid-Lactation")
                && !newValue.equals("Late Lactation")
                && !newValue.equals("Dry Period"))));
    }

    private void initializeStageDaysRangeMap() {
        stageDaysRangeMap.put("Colostrum Stage", new Integer[]{0, 5});
        stageDaysRangeMap.put("Transition Stage", new Integer[]{6, 15});
        stageDaysRangeMap.put("Peak Milk Harvesting", new Integer[]{16, 60});
        stageDaysRangeMap.put("Mid-Lactation", new Integer[]{61, 150});
        stageDaysRangeMap.put("Late Lactation", new Integer[]{151, 305});
        stageDaysRangeMap.put("Dry Period", new Integer[]{306, -1});
    }

    private void initializeVolumeLabels() {
        volumeLabels.put("Colostrum Stage", new Label[]{volumeLabel1, volumeLabel2, volumeLabel3});
        volumeLabels.put("Transition Stage", new Label[]{volumeLabel1, volumeLabel2, volumeLabel3});
        volumeLabels.put("Peak Milk Harvesting", new Label[]{volumeLabel1, volumeLabel2, volumeLabel3});
        volumeLabels.put("Mid-Lactation", new Label[]{volumeLabel1, volumeLabel2, volumeLabel3});
        volumeLabels.put("Late Lactation", new Label[]{volumeLabel1, volumeLabel2, volumeLabel3});
        volumeLabels.put("Dry Period", new Label[]{volumeLabel1, volumeLabel2, volumeLabel3});
    }

    private void setFieldsDisabled(boolean disabled) {
        for (Node node : gridPaneProduction.getChildren()) {
            node.setDisable(disabled);
        }
        productionSessionDatePicker.setDisable(disabled);
    }

    private String calculateProductionStage(int daysInLactation) {
        String productionStage = "Dry Period"; // Default to Dry Period if no match is found

        for (Map.Entry<String, Integer[]> entry : stageDaysRangeMap.entrySet()) {
            String stage = entry.getKey();
            Integer[] range = entry.getValue();
            int startDay = range[0];
            int endDay = range[1];

            if (endDay == -1) {
                if (daysInLactation >= startDay) {
                    productionStage = stage;
                    break;
                }
            } else {
                if (daysInLactation >= startDay && daysInLactation <= endDay) {
                    productionStage = stage;
                    break;
                }
            }
        }

        return productionStage;
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

    private String getVolumeLabelText(String stage) {
        return switch (stage) {
            case "Colostrum Stage" -> "First Milk Volume ";
            case "Transition Stage" -> "Adjustment Volume ";
            case "Peak Milk Harvesting" -> "Highest Volume ";
            case "Mid-Lactation" -> "Regular Volume ";
            case "Late Lactation" -> "Lowering Volume ";
            case "Dry Period" -> "No Milk Volume ";
            default -> "";
        };
    }

    private void updateVolumeLabels(String stage) {
        Label[] volumeLabelsArray = volumeLabels.get(stage);

        if (volumeLabelsArray != null) {
            String volumeLabelText = getVolumeLabelText(stage);

            volumeLabelsArray[0].setText(volumeLabelText + " : (Morning)");
            volumeLabelsArray[1].setText(volumeLabelText + " : (Afternoon)");
            volumeLabelsArray[2].setText(volumeLabelText + " : (Evening)");
        } else {
            volumeLabel1.setText("N/A : (Morning)");
            volumeLabel2.setText("N/A : (Afternoon)");
            volumeLabel3.setText("N/A : (Evening)");
        }
    }

    private void clearLactationFieldsOnCattleIdChange() {
        lactationPeriodLabel.setText("N/A");
        daysInLactationTextField.clear();
        currentProductionStageLabel.setText("N/A");
        productionStageLabel.setText("Production Stage");
        currentPeriodLabel.setText("N/A");
        stagePeriodLabel.setText("N/A");
        updateVolumeLabels("");
    }

    private void initializeSpinners() {
        SpinnerValueFactory<Double> morningValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 60.0, 0.01);
        SpinnerValueFactory<Double> afternoonValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 60.0, 0.01);
        SpinnerValueFactory<Double> eveningValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 60.0, 0.01);

        spinnerMorning.setValueFactory(morningValueFactory);
        spinnerAfternoon.setValueFactory(afternoonValueFactory);
        spinnerEvening.setValueFactory(eveningValueFactory);
    }

    private void initializeProductionSessionDatePicker() {
        productionSessionDatePicker.setValue(LocalDate.now());
        selectedDateProductionSessionDate = productionSessionDatePicker.getValue();
    }



    private void populateFieldsBasedOnDateAndLactationPeriod(LocalDate selectedDate, int lactationPeriodId) {
        try {
            List<ProductionSession> sessions = ProductionSessionDAO.getProductionSessionsByDateAndLactationPeriodId(selectedDate, lactationPeriodId);
            Map<String, List<ProductionSession>> categorizedSessions = ProductionSessionDAO.categorizeSessionsByTimeOfDay(sessions);
            populateFXMLFields(categorizedSessions);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateFXMLFields(Map<String, List<ProductionSession>> categorizedSessions) {
        clearFields();

        populateFields(categorizedSessions.getOrDefault("Morning", new ArrayList<>()), "Morning");
        populateFields(categorizedSessions.getOrDefault("Afternoon", new ArrayList<>()), "Afternoon");
        populateFields(categorizedSessions.getOrDefault("Evening", new ArrayList<>()), "Evening");

        updateButtonText(categorizedSessions, "Morning", deleteMorningSession, true);
        updateButtonText(categorizedSessions, "Afternoon", deleteAfternoonSession, true);
        updateButtonText(categorizedSessions, "Evening", deleteEveningSession, true);

        morningSessionButton.setDisable(true);
        afternoonSessionButton.setDisable(true);
        eveningSessionButton.setDisable(true);

        storeInitialValues();
    }

    private void populateFields(List<ProductionSession> sessions, String sessionType) {
        if (sessions.isEmpty()) {
            return;
        }

        ProductionSession session = sessions.getFirst();

        switch (sessionType) {
            case "Morning":
                morningStartTimeTextField.setText(session.getStartTime().toLocalDateTime().toLocalTime().format(timeFormatter));
                morningEndTimeTextField.setText(session.getEndTime().toLocalDateTime().toLocalTime().format(timeFormatter));
                morningQualityScore.setValue(session.getQualityScore());
                spinnerMorning.getValueFactory().setValue(session.getProductionVolume());
                break;
            case "Afternoon":
                afternoonStartTimeTextField.setText(session.getStartTime().toLocalDateTime().toLocalTime().format(timeFormatter));
                afternoonEndTimeTextField.setText(session.getEndTime().toLocalDateTime().toLocalTime().format(timeFormatter));
                afternoonQualityScore.setValue(session.getQualityScore());
                spinnerAfternoon.getValueFactory().setValue(session.getProductionVolume());
                break;
            case "Evening":
                eveningStartTimeTextField.setText(session.getStartTime().toLocalDateTime().toLocalTime().format(timeFormatter));
                eveningEndTimeTextField.setText(session.getEndTime().toLocalDateTime().toLocalTime().format(timeFormatter));
                eveningQualityScore.setValue(session.getQualityScore());
                spinnerEvening.getValueFactory().setValue(session.getProductionVolume());
                break;
            default:
                throw new IllegalArgumentException("Invalid session type: " + sessionType);
        }

        morningSessionButton.setDisable(true);
        afternoonSessionButton.setDisable(true);
        eveningSessionButton.setDisable(true);

        storeInitialValues();
    }

    private void storeInitialValues() {
        initialValues.put("morningStartTime", morningStartTimeTextField.getText());
        initialValues.put("morningEndTime", morningEndTimeTextField.getText());
        initialValues.put("morningQualityScore", String.valueOf(morningQualityScore.getValue()));
        initialValues.put("spinnerMorning", String.valueOf(spinnerMorning.getValue()));

        initialValues.put("afternoonStartTime", afternoonStartTimeTextField.getText());
        initialValues.put("afternoonEndTime", afternoonEndTimeTextField.getText());
        initialValues.put("afternoonQualityScore", String.valueOf(afternoonQualityScore.getValue()));
        initialValues.put("spinnerAfternoon", String.valueOf(spinnerAfternoon.getValue()));

        initialValues.put("eveningStartTime", eveningStartTimeTextField.getText());
        initialValues.put("eveningEndTime", eveningEndTimeTextField.getText());
        initialValues.put("eveningQualityScore", String.valueOf(eveningQualityScore.getValue()));
        initialValues.put("spinnerEvening", String.valueOf(spinnerEvening.getValue()));
    }

    private void initializeFieldChangeListeners() {
        // Listener for morning session fields
        addChangeListener(morningStartTimeTextField.textProperty(), "morningStartTime", "Morning");
        addChangeListener(morningEndTimeTextField.textProperty(), "morningEndTime", "Morning");
        addChangeListener(morningQualityScore.valueProperty(), "morningQualityScore", "Morning");
        addChangeListener(spinnerMorning.valueProperty(), "spinnerMorning", "Morning");

        // Listener for afternoon session fields
        addChangeListener(afternoonStartTimeTextField.textProperty(), "afternoonStartTime", "Afternoon");
        addChangeListener(afternoonEndTimeTextField.textProperty(), "afternoonEndTime", "Afternoon");
        addChangeListener(afternoonQualityScore.valueProperty(), "afternoonQualityScore", "Afternoon");
        addChangeListener(spinnerAfternoon.valueProperty(), "spinnerAfternoon", "Afternoon");

        // Listener for evening session fields
        addChangeListener(eveningStartTimeTextField.textProperty(), "eveningStartTime", "Evening");
        addChangeListener(eveningEndTimeTextField.textProperty(), "eveningEndTime", "Evening");
        addChangeListener(eveningQualityScore.valueProperty(), "eveningQualityScore", "Evening");
        addChangeListener(spinnerEvening.valueProperty(), "spinnerEvening", "Evening");

    }
    private void initializeEndTimeButtonListeners() {
        // Create tooltips
        Tooltip morningTooltip = new Tooltip("Please provide the Morning start time first.");
        Tooltip afternoonTooltip = new Tooltip("Please provide the Afternoon start time first.");
        Tooltip eveningTooltip = new Tooltip("Please provide the Evening start time first.");

        // Set tooltips and disable end time buttons initially based on the current values of start time text fields
        setEndTimeButtonStateAndTooltip(morningStartTimeTextField, morningEndTimeButton, morningTooltip);
        setEndTimeButtonStateAndTooltip(afternoonStartTimeTextField, afternoonEndTimeButton, afternoonTooltip);
        setEndTimeButtonStateAndTooltip(eveningStartTimeTextField, eveningEndTimeButton, eveningTooltip);

        // Add listeners to start time text fields
        morningStartTimeTextField.textProperty().addListener((observable, oldValue, newValue) -> setEndTimeButtonStateAndTooltip(morningStartTimeTextField, morningEndTimeButton, morningTooltip));

        afternoonStartTimeTextField.textProperty().addListener((observable, oldValue, newValue) -> setEndTimeButtonStateAndTooltip(afternoonStartTimeTextField, afternoonEndTimeButton, afternoonTooltip));

        eveningStartTimeTextField.textProperty().addListener((observable, oldValue, newValue) -> setEndTimeButtonStateAndTooltip(eveningStartTimeTextField, eveningEndTimeButton, eveningTooltip));
    }

    private void setEndTimeButtonStateAndTooltip(TextField startTimeTextField, Button endTimeButton, Tooltip tooltip) {
        boolean isEmpty = isStartTimeEmpty(startTimeTextField);
        endTimeButton.setDisable(isEmpty);

        if (isEmpty) {
            // Install the tooltip on the parent container
            Tooltip.install(endTimeButton.getParent(), tooltip);
            endTimeButton.getParent().setOnMouseEntered(event -> {
                if (endTimeButton.isDisabled()) {
                    Bounds bounds = endTimeButton.localToScreen(endTimeButton.getBoundsInLocal());
                    tooltip.show(endTimeButton, bounds.getMinX(), bounds.getMaxY());
                }
            });
            endTimeButton.getParent().setOnMouseExited(event -> tooltip.hide());
        } else {
            Tooltip.uninstall(endTimeButton.getParent(), tooltip);
        }
    }

    private boolean isStartTimeEmpty(TextField startTimeTextField) {
        String startTime = startTimeTextField.getText();
        return startTime == null || startTime.trim().isEmpty();
    }



    private void addChangeListener(ObservableValue<?> property, String fieldName, String sessionType) {
        property.addListener((observable, oldValue, newValue) -> {
            if (newValue instanceof String || newValue instanceof Number) {
                handleFieldChange(fieldName, sessionType);
            }
        });
    }

    private void handleFieldChange(String fieldName, String sessionType) {
        boolean changed = fieldChanged(fieldName, sessionType);
        switch (sessionType) {
            case "Morning":
                if (changed) {
                    updateButtonTextOnChange(deleteMorningSession);
                } else {
                    try {
                        updateDeleteButtonStateAndText(sessionType, deleteMorningSession);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                morningSessionButton.setDisable(!changed);
                break;
            case "Afternoon":
                if (changed) {
                    updateButtonTextOnChange(deleteAfternoonSession);
                } else {
                    try {
                        updateDeleteButtonStateAndText(sessionType, deleteAfternoonSession);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                afternoonSessionButton.setDisable(!changed);
                break;
            case "Evening":
                if (changed) {
                    updateButtonTextOnChange(deleteEveningSession);
                } else {
                    try {
                        updateDeleteButtonStateAndText(sessionType, deleteEveningSession);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                eveningSessionButton.setDisable(!changed);
                break;
            default:
                throw new IllegalArgumentException("Invalid session type: " + sessionType);
        }
    }

    private void updateButtonTextOnChange(Button button) {
        if ("Delete".equals(button.getText())) {
            button.setText("Modify");
        } else {
            button.setText("Clear");
        }
        button.setDisable(false);
    }

    private boolean fieldChanged(String fieldName, String sessionType) {
        String currentValue = getCurrentValue(fieldName, sessionType);
        String initialValue = initialValues.get(fieldName);
        return !Objects.equals(initialValue, currentValue);
    }

    @FXML
    private void handleAddOrUpdateProductionSession(ActionEvent event) {
        Button sourceButton = (Button) event.getSource();
        String sessionType = getSessionTypeFromButton(sourceButton);

        if (sourceButton.getText().equals("Save")) {
            saveNewSession(sessionType);
        } else if (sourceButton.getText().equals("Update")) {
            updateExistingSession(sessionType);
        }
    }

    private void saveNewSession(String sessionType) {
        if (isSessionInputComplete(sessionType)) {
            showAlertWithHighlight(sessionType);
            return;
        }

        try {
            ProductionSession newSession = createProductionSessionFromInput(sessionType);
            ProductionSessionDAO.saveProductionSession(newSession);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Production session saved successfully.");
            refreshUI();

            if (selectCriteriaChoiceBox.getSelectionModel().getSelectedItem() != null &&
                    dependentCriteriaChoiceBox.getSelectionModel().getSelectedItem() != null) {
                compareCattleProduction(); // Call your method to compare cattle production
            } else {
                clearAndDisableChoiceBoxes();
                dependentCriteriaChoiceBox.setDisable(true);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save production session: " + e.getMessage());
        }
    }

    private void updateExistingSession(String sessionType) {
        if (isSessionInputComplete(sessionType)) {
            showAlertWithHighlight(sessionType);
            return;
        }

        try {
            Map<String, String> currentValues = getCurrentValues(sessionType);
            Map<String, String> initialValues = getInitialValues(sessionType);

            if (mapsEqual(currentValues, initialValues)) {
                showAlert(Alert.AlertType.INFORMATION, "Information", "No changes detected.");
                return;
            }

            LocalDateTime initialStartTime = parseDateTime(initialValues.get("startTime"));
            LocalDateTime initialEndTime = parseDateTime(initialValues.get("endTime"));
            ProductionSession existingSession = findExistingSessionByTime(initialStartTime, initialEndTime);

            if (existingSession != null) {
                updateSession(existingSession, currentValues);
                ProductionSessionDAO.updateProductionSession(existingSession);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Production session updated successfully.");
                refreshUI();

                if (selectCriteriaChoiceBox.getSelectionModel().getSelectedItem() != null &&
                        dependentCriteriaChoiceBox.getSelectionModel().getSelectedItem() != null) {
                    compareCattleProduction(); // Call your method to compare cattle production
                } else {
                    clearAndDisableChoiceBoxes();
                    dependentCriteriaChoiceBox.setDisable(true);
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to find existing session.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update production session: " + e.getMessage());
            e.printStackTrace();
        } catch (DateTimeParseException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to parse time: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isSessionInputComplete(String sessionType) {
        return !switch (sessionType) {
            case "Morning" ->
                    !morningStartTimeTextField.getText().isEmpty() && !morningEndTimeTextField.getText().isEmpty();
            case "Afternoon" ->
                    !afternoonStartTimeTextField.getText().isEmpty() && !afternoonEndTimeTextField.getText().isEmpty();
            case "Evening" ->
                    !eveningStartTimeTextField.getText().isEmpty() && !eveningEndTimeTextField.getText().isEmpty();
            default -> false;
        };
    }

    private void refreshUI() {
        populateFieldsBasedOnDateAndLactationPeriod(selectedDateProductionSessionDate, ongoingLactationPeriod.lactationPeriodID());
        checkExistingSessionsAndUpdateButton(selectedDateProductionSessionDate, ongoingLactationPeriod.lactationPeriodID());
    }

    private String getSessionTypeFromButton(Button button) {
        if (button == morningSessionButton) {
            return "Morning";
        } else if (button == afternoonSessionButton) {
            return "Afternoon";
        } else if (button == eveningSessionButton) {
            return "Evening";
        }
        return null;
    }

    public void showModifyDialog(String sessionType, Button clickedButton) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Modify Session");
        alert.setHeaderText(null);
        alert.initStyle(StageStyle.UNDECORATED);

        // Set a more descriptive and clear instruction
        alert.setContentText("What would you like to do with this session?");

        ButtonType restoreButtonType = new ButtonType("Restore");
        ButtonType deleteButtonType = new ButtonType("Delete");
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(restoreButtonType, deleteButtonType, cancelButtonType);

        // Apply CSS styles to buttons
        DialogPane dialogPane = alert.getDialogPane();

        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/styles.css")).toExternalForm());
        // Set the style ID for the dialog pane
        dialogPane.setId("dark-blue");
        // Apply style ID to buttons
        Button restoreButton = (Button) dialogPane.lookupButton(restoreButtonType);
        restoreButton.setId("set-button");

        Button deleteButton = (Button) dialogPane.lookupButton(deleteButtonType);
        deleteButton.setId("delete-button");

        Button cancelButton = (Button) dialogPane.lookupButton(cancelButtonType);
        cancelButton.setId("cancel-button");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == restoreButtonType) {
                restoreSessionFields(sessionType);
                clickedButton.setText("Delete");
            } else if (result.get() == deleteButtonType) {
                deleteSession(sessionType);
            }
        }
    }

    private void restoreSessionFields(String sessionType) {
        switch (sessionType) {
            case "Morning":
                morningStartTimeTextField.setText(initialValues.get("morningStartTime"));
                morningEndTimeTextField.setText(initialValues.get("morningEndTime"));
                morningQualityScore.setValue(initialValues.get("morningQualityScore"));
                spinnerMorning.getValueFactory().setValue(Double.valueOf(initialValues.get("spinnerMorning")));
                break;
            case "Afternoon":
                afternoonStartTimeTextField.setText(initialValues.get("afternoonStartTime"));
                afternoonEndTimeTextField.setText(initialValues.get("afternoonEndTime"));
                afternoonQualityScore.setValue(initialValues.get("afternoonQualityScore"));
                spinnerAfternoon.getValueFactory().setValue(Double.valueOf(initialValues.get("spinnerAfternoon")));
                break;
            case "Evening":
                eveningStartTimeTextField.setText(initialValues.get("eveningStartTime"));
                eveningEndTimeTextField.setText(initialValues.get("eveningEndTime"));
                eveningQualityScore.setValue(initialValues.get("eveningQualityScore"));
                spinnerEvening.getValueFactory().setValue(Double.valueOf(initialValues.get("spinnerEvening")));
                break;
            default:
                throw new IllegalArgumentException("Invalid session type: " + sessionType);
        }
    }

    private void clearSessionFields(String sessionType) {
        switch (sessionType) {
            case "Morning":
                morningStartTimeTextField.clear();
                morningEndTimeTextField.clear();
                morningQualityScore.setValue(null);
                spinnerMorning.getValueFactory().setValue(0.00);
                break;
            case "Afternoon":
                afternoonStartTimeTextField.clear();
                afternoonEndTimeTextField.clear();
                afternoonQualityScore.setValue(null);
                spinnerAfternoon.getValueFactory().setValue(0.00);
                break;
            case "Evening":
                eveningStartTimeTextField.clear();
                eveningEndTimeTextField.clear();
                eveningQualityScore.setValue(null);
                spinnerEvening.getValueFactory().setValue(0.00);
                break;
            default:
                throw new IllegalArgumentException("Invalid session type: " + sessionType);
        }
    }

    private String getCurrentValue(String fieldName, String sessionType) {
        return switch (sessionType) {
            case "Morning" -> switch (fieldName) {
                case "morningStartTime" -> morningStartTimeTextField.getText();
                case "morningEndTime" -> morningEndTimeTextField.getText();
                case "morningQualityScore" -> String.valueOf(morningQualityScore.getValue());
                case "spinnerMorning" -> String.valueOf(spinnerMorning.getValue());
                default -> throw new IllegalArgumentException("Invalid field name: " + fieldName);
            };
            case "Afternoon" -> switch (fieldName) {
                case "afternoonStartTime" -> afternoonStartTimeTextField.getText();
                case "afternoonEndTime" -> afternoonEndTimeTextField.getText();
                case "afternoonQualityScore" -> String.valueOf(afternoonQualityScore.getValue());
                case "spinnerAfternoon" -> String.valueOf(spinnerAfternoon.getValue());
                default -> throw new IllegalArgumentException("Invalid field name: " + fieldName);
            };
            case "Evening" -> switch (fieldName) {
                case "eveningStartTime" -> eveningStartTimeTextField.getText();
                case "eveningEndTime" -> eveningEndTimeTextField.getText();
                case "eveningQualityScore" -> String.valueOf(eveningQualityScore.getValue());
                case "spinnerEvening" -> String.valueOf(spinnerEvening.getValue());
                default -> throw new IllegalArgumentException("Invalid field name: " + fieldName);
            };
            default -> throw new IllegalArgumentException("Invalid session type: " + sessionType);
        };
    }

    private ProductionSession createProductionSessionFromInput(String sessionType) {
        int sessionID = 0;
        int lactationPeriodID = ongoingLactationPeriod.lactationPeriodID();
        int cattleID = selectedCattleId;
        Timestamp startTime;
        Timestamp endTime;
        String qualityScore;
        double productionVolume;

        switch (sessionType) {
            case "Morning":
                startTime = Timestamp.valueOf(selectedDateProductionSessionDate.atTime(LocalTime.parse(morningStartTimeTextField.getText(), timeFormatter)));
                endTime = Timestamp.valueOf(selectedDateProductionSessionDate.atTime(LocalTime.parse(morningEndTimeTextField.getText(), timeFormatter)));
                qualityScore = morningQualityScore.getValue();
                productionVolume = spinnerMorning.getValue();
                break;
            case "Afternoon":
                startTime = Timestamp.valueOf(selectedDateProductionSessionDate.atTime(LocalTime.parse(afternoonStartTimeTextField.getText(), timeFormatter)));
                endTime = Timestamp.valueOf(selectedDateProductionSessionDate.atTime(LocalTime.parse(afternoonEndTimeTextField.getText(), timeFormatter)));
                qualityScore = afternoonQualityScore.getValue();
                productionVolume = spinnerAfternoon.getValue();
                break;
            case "Evening":
                startTime = Timestamp.valueOf(selectedDateProductionSessionDate.atTime(LocalTime.parse(eveningStartTimeTextField.getText(), timeFormatter)));
                endTime = Timestamp.valueOf(selectedDateProductionSessionDate.atTime(LocalTime.parse(eveningEndTimeTextField.getText(), timeFormatter)));
                qualityScore = eveningQualityScore.getValue();
                productionVolume = spinnerEvening.getValue();
                break;
            default:
                throw new IllegalArgumentException("Invalid session type: " + sessionType);
        }

        return new ProductionSession(sessionID, lactationPeriodID, cattleID, startTime, endTime, qualityScore, productionVolume);
    }

    private ProductionSession findExistingSessionByTime(LocalDateTime startTime, LocalDateTime endTime) {
        try {
            List<ProductionSession> sessions = ProductionSessionDAO.getProductionSessionsByDateAndLactationPeriodId(selectedDateProductionSessionDate, ongoingLactationPeriod.lactationPeriodID());
            for (ProductionSession session : sessions) {
                if (session.getStartTime().toLocalDateTime().equals(startTime) && session.getEndTime().toLocalDateTime().equals(endTime)) {
                    return session;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void checkExistingSessionsAndUpdateButton(LocalDate selectedDate, int lactationPeriodId) {
        try {
            List<ProductionSession> sessions = ProductionSessionDAO.getProductionSessionsByDateAndLactationPeriodId(selectedDate, lactationPeriodId);
            Map<String, List<ProductionSession>> categorizedSessions = ProductionSessionDAO.categorizeSessionsByTimeOfDay(sessions);

            updateButtonText(categorizedSessions, "Morning", morningSessionButton, false);
            updateButtonText(categorizedSessions, "Afternoon", afternoonSessionButton, false);
            updateButtonText(categorizedSessions, "Evening", eveningSessionButton, false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> getCurrentValues(String sessionType) {
        Map<String, String> values = new HashMap<>();
        switch (sessionType) {
            case "Morning":
                values.put("startTime", morningStartTimeTextField.getText());
                values.put("endTime", morningEndTimeTextField.getText());
                values.put("qualityScore", morningQualityScore.getValue());
                values.put("productionVolume", String.valueOf(spinnerMorning.getValue()));
                break;
            case "Afternoon":
                values.put("startTime", afternoonStartTimeTextField.getText());
                values.put("endTime", afternoonEndTimeTextField.getText());
                values.put("qualityScore", afternoonQualityScore.getValue());
                values.put("productionVolume", String.valueOf(spinnerAfternoon.getValue()));
                break;
            case "Evening":
                values.put("startTime", eveningStartTimeTextField.getText());
                values.put("endTime", eveningEndTimeTextField.getText());
                values.put("qualityScore", eveningQualityScore.getValue());
                values.put("productionVolume", String.valueOf(spinnerEvening.getValue()));
                break;
            default:
                throw new IllegalArgumentException("Invalid session type: " + sessionType);
        }
        return values;
    }

    private Map<String, String> getInitialValues(String sessionType) {
        Map<String, String> values = new HashMap<>();
        values.put("startTime", initialValues.get(sessionType.toLowerCase() + "StartTime"));
        values.put("endTime", initialValues.get(sessionType.toLowerCase() + "EndTime"));
        values.put("qualityScore", initialValues.get(sessionType.toLowerCase() + "QualityScore"));
        values.put("productionVolume", initialValues.get("spinner" + sessionType));
        return values;
    }

    private boolean mapsEqual(Map<String, String> map1, Map<String, String> map2) {
        if (map1.size() != map2.size()) return false;
        for (String key : map1.keySet()) {
            if (!Objects.equals(map1.get(key), map2.get(key))) {
                return false;
            }
        }
        return true;
    }

    private LocalDateTime parseDateTime(String timeString) {
        return LocalDateTime.of(selectedDateProductionSessionDate, LocalTime.parse(timeString, ProductivityAndCalving.timeFormatter));
    }

    private void updateSession(ProductionSession session, Map<String, String> values) {
        LocalDateTime currentStartTime = parseDateTime(values.get("startTime"));
        LocalDateTime currentEndTime = parseDateTime(values.get("endTime"));
        session.setStartTime(Timestamp.valueOf(currentStartTime));
        session.setEndTime(Timestamp.valueOf(currentEndTime));
        session.setQualityScore(values.get("qualityScore"));
        session.setProductionVolume(Double.parseDouble(values.get("productionVolume")));
    }
    private void updateButtonText(Map<String, List<ProductionSession>> categorizedSessions, String sessionType, Button button, boolean forDeleteButton) {
        if (categorizedSessions.containsKey(sessionType) && !categorizedSessions.get(sessionType).isEmpty()) {
            if (forDeleteButton) {
                button.setText("Delete");
                button.setDisable(false);
            } else {
                button.setText("Update");
                button.setDisable(true);
            }
        } else {
            if (forDeleteButton) {
                button.setText("Clear");
                button.setDisable(true);
            } else {
                button.setText("Save");
                button.setDisable(true);
            }
        }
    }

    private void updateDeleteButtonStateAndText(String sessionType, Button button) throws SQLException {
        List<ProductionSession> sessions = ProductionSessionDAO.getProductionSessionsByDateAndLactationPeriodId(selectedDateProductionSessionDate, ongoingLactationPeriod.lactationPeriodID());

        boolean hasExistingSession = sessions.stream()
                .anyMatch(session -> isSessionOfType(session, sessionType));

        if (hasExistingSession) {
            button.setText("Delete");
            button.setDisable(false);
        } else {
            button.setText("Clear");
            button.setDisable(true);
        }
    }

    private boolean isSessionOfType(ProductionSession session, String sessionType) {
        LocalDateTime startTime = session.getStartTime().toLocalDateTime();
        LocalTime timeOfDay = startTime.toLocalTime();

        return switch (sessionType) {
            case "Morning" -> timeOfDay.isBefore(LocalTime.NOON);
            case "Afternoon" -> timeOfDay.isAfter(LocalTime.NOON) && timeOfDay.isBefore(LocalTime.of(16, 0));
            case "Evening" -> timeOfDay.isAfter(LocalTime.of(16, 0));
            default -> throw new IllegalArgumentException("Invalid session type: " + sessionType);
        };
    }

    private void showAlertWithHighlight(String sessionType) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation Error");
        alert.setHeaderText("Please ensure all fields are filled out correctly.");
        alert.setContentText("StartTime and EndTime are mandatory fields.");

        // Highlight the specific field(s) based on sessionType
        switch (sessionType) {
            case "Morning":
                if (morningStartTimeTextField.getText().isEmpty()) {
                    highlightField(morningStartTimeTextField);
                }
                if (morningEndTimeTextField.getText().isEmpty()) {
                    highlightField(morningEndTimeTextField);
                }
                break;
            case "Afternoon":
                if (afternoonStartTimeTextField.getText().isEmpty()) {
                    highlightField(afternoonStartTimeTextField);
                }
                if (afternoonEndTimeTextField.getText().isEmpty()) {
                    highlightField(afternoonEndTimeTextField);
                }
                break;
            case "Evening":
                if (eveningStartTimeTextField.getText().isEmpty()) {
                    highlightField(eveningStartTimeTextField);
                }
                if (eveningEndTimeTextField.getText().isEmpty()) {
                    highlightField(eveningEndTimeTextField);
                }
                break;
        }

        alert.showAndWait();
    }

    public void highlightField(TextField field) {
        field.setStyle("-fx-background-color: white;");

        FadeTransition fadeInTransition = new FadeTransition(javafx.util.Duration.millis(1000), field);
        fadeInTransition.setFromValue(1.0);
        fadeInTransition.setToValue(0.7);
        fadeInTransition.setCycleCount(1);

        FadeTransition fadeOutTransition = new FadeTransition(javafx.util.Duration.millis(1000), field);
        fadeOutTransition.setFromValue(0.7);
        fadeOutTransition.setToValue(1.0);
        fadeOutTransition.setCycleCount(1);

        fadeInTransition.setOnFinished(event -> {
            field.setStyle("-fx-background-color: lightcoral;");
            fadeOutTransition.play();
        });

        fadeOutTransition.setOnFinished(event -> {
            field.setStyle("-fx-background-color: white;");
            fadeInTransition.play();
        });

        SequentialTransition sequentialTransition = new SequentialTransition(fadeInTransition, fadeOutTransition);
        sequentialTransition.setCycleCount(5);
        sequentialTransition.play();

        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                sequentialTransition.stop();
                field.setStyle("-fx-background-color: white;");
            }
        });

        field.setOnMouseClicked(event -> {
            sequentialTransition.stop();
            field.setStyle("-fx-background-color: white;");
        });
    }

    private void clearFields() {
        morningStartTimeTextField.clear();
        morningEndTimeTextField.clear();
        afternoonStartTimeTextField.clear();
        afternoonEndTimeTextField.clear();
        eveningStartTimeTextField.clear();
        eveningEndTimeTextField.clear();

        morningQualityScore.setValue(null);
        afternoonQualityScore.setValue(null);
        eveningQualityScore.setValue(null);

        spinnerMorning.getValueFactory().setValue(0.0);
        spinnerAfternoon.getValueFactory().setValue(0.0);
        spinnerEvening.getValueFactory().setValue(0.0);
    }

    public void initializeShowTimePickerButtons() {
        buttonTextFieldMap = new HashMap<>();
        buttonTextFieldMap.put(morningStartTimeButton, morningStartTimeTextField);
        buttonTextFieldMap.put(afternoonStartTimeButton, afternoonStartTimeTextField);
        buttonTextFieldMap.put(eveningStartTimeButton, eveningStartTimeTextField);
        buttonTextFieldMap.put(morningEndTimeButton, morningEndTimeTextField);
        buttonTextFieldMap.put(afternoonEndTimeButton, afternoonEndTimeTextField);
        buttonTextFieldMap.put(eveningEndTimeButton, eveningEndTimeTextField);
    }

    @FXML
    public void showTimePicker(ActionEvent event) {
        Button button = (Button) event.getSource();
        TextField associatedTextField = buttonTextFieldMap.get(button);

        if (associatedTextField != null) {
            Stage stage = (Stage) button.getScene().getWindow();
            showTimePickerStage(stage, button, associatedTextField);
        }
    }

    private void showTimePickerStage(Stage stage, Button button, TextField associatedTextField) {
        try {
            LactationPeriod selectedLactationPeriod = lactationTableView.getSelectionModel().getSelectedItem().getLactationPeriod();
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/hashinfarm/homePanels/homeCenterPanelViews/cattleManagement/centerRightViews/cattleDetailMoreButtons/timePicker.fxml"));
            Parent root = loader.load();

            // Get the controller and set the stage and source button
            TimePickerController controller = loader.getController();
            Stage timePickerStage = new Stage(StageStyle.UNDECORATED);
            controller.setStage(timePickerStage);
            controller.setLactationPeriodStartDate(selectedLactationPeriod);
            controller.setSelectedProductionSession(selectedDateProductionSessionDate);

            Button[] buttons = { morningStartTimeButton, morningEndTimeButton,
                    afternoonStartTimeButton, afternoonEndTimeButton,
                    eveningStartTimeButton, eveningEndTimeButton };

            controller.setButtons(buttons);
            controller.setSessionType(button);

            // Determine initial time to display in the timePicker
            String currentText = associatedTextField.getText();
            int[] timeParts = new int[3];
            if (!currentText.isEmpty()) {
                parseTimeToVariables(currentText, timeParts);
            } else {
                String startTimeText = determineStartTimeText(button);
                if (!startTimeText.isEmpty()) {
                    parseTimeToVariables(startTimeText, timeParts);
                } else {
                    timeParts = null;
                }
            }

            controller.setInitialTimeParts(timeParts);

            // Set minimum time if the end time picker is being opened
            if (button == morningEndTimeButton && !morningStartTimeTextField.getText().isEmpty()) {
                controller.setMinimumTime(parseTimeStringToLocalTime(morningStartTimeTextField.getText()));
            } else if (button == afternoonEndTimeButton && !afternoonStartTimeTextField.getText().isEmpty()) {
                controller.setMinimumTime(parseTimeStringToLocalTime(afternoonStartTimeTextField.getText()));
            } else if (button == eveningEndTimeButton && !eveningStartTimeTextField.getText().isEmpty()) {
                controller.setMinimumTime(parseTimeStringToLocalTime(eveningStartTimeTextField.getText()));
            }

            // Set maximum time if the start time picker is being opened
            if (button == morningStartTimeButton && !morningEndTimeTextField.getText().isEmpty()) {
                controller.setMaximumTime(parseTimeStringToLocalTime(morningEndTimeTextField.getText()));
            } else if (button == afternoonStartTimeButton && !afternoonEndTimeTextField.getText().isEmpty()) {
                controller.setMaximumTime(parseTimeStringToLocalTime(afternoonEndTimeTextField.getText()));
            } else if (button == eveningStartTimeButton && !eveningEndTimeTextField.getText().isEmpty()) {
                controller.setMaximumTime(parseTimeStringToLocalTime(eveningEndTimeTextField.getText()));
            }

            // Set callback to update the associated TextField
            controller.setTimeSelectedCallback(selectedTime -> associatedTextField.setText(selectedTime.format(timeFormatter)));

            // Create and style the close button
            Button closeButton = new Button("X");
            closeButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 50%; -fx-padding: 0; -fx-alignment: center;");
            closeButton.setMinSize(20, 20);
            closeButton.setMaxSize(20, 20);

            // Ensure the button is perfectly round
            closeButton.setShape(new Circle(10));

            // Add the close button to the VBox
            VBox rootVBox = (VBox) root;
            HBox closeButtonContainer = new HBox();
            closeButtonContainer.setAlignment(Pos.TOP_RIGHT);
            closeButtonContainer.getChildren().add(closeButton);

            // Insert the close button container at the top of the VBox
            rootVBox.getChildren().addFirst(closeButtonContainer);

            // Set the close action
            closeButton.setOnAction(event -> timePickerStage.close());

            // Set the stage properties
            timePickerStage.initModality(Modality.WINDOW_MODAL);
            timePickerStage.initOwner(stage);
            timePickerStage.setScene(new Scene(rootVBox));

            // Calculate the position of the new stage relative to the button
            timePickerStage.setX(button.localToScreen(button.getBoundsInLocal()).getMinX());
            timePickerStage.setY(button.localToScreen(button.getBoundsInLocal()).getMaxY());

            // Show the stage
            timePickerStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private String determineStartTimeText(Button button) {
        if (button == morningEndTimeButton) {
            return morningStartTimeTextField.getText();
        } else if (button == afternoonEndTimeButton) {
            return afternoonStartTimeTextField.getText();
        } else if (button == eveningEndTimeButton) {
            return eveningStartTimeTextField.getText();
        }
        return "";
    }


    public static void parseTimeToVariables(String timeStr, int[] timeParts) {
        if (timeStr == null || timeStr.isBlank() || timeParts.length != 3) {
            throw new IllegalArgumentException("Invalid input or timeParts array size");
        }

        // Remove leading/trailing whitespaces
        timeStr = timeStr.strip();

        // Check for valid length (exactly 8 characters)
        if (timeStr.length() != 8) {
            throw new IllegalArgumentException("Invalid time format: " + timeStr);
        }

        // Extract hour, minute, and meridian (considering fixed width)
        String hour = timeStr.substring(0, 2);
        String minute = timeStr.substring(3, 5);
        String meridian = timeStr.substring(6).toUpperCase();

        // Validate format (digits and AM/PM)
        if (!hour.matches("\\d{2}") || !minute.matches("\\d{2}") || !(meridian.equals("AM") || meridian.equals("PM"))) {
            throw new IllegalArgumentException("Invalid time format: " + timeStr);
        }

        // Convert hour and minute to integers
        int hours = Integer.parseInt(hour);
        int minutes = Integer.parseInt(minute);

        // Validate time values
        if (!(1 <= hours && hours <= 12 && 0 <= minutes && minutes <= 59)) {
            throw new IllegalArgumentException("Invalid time value: " + timeStr);
        }

        // Assign values to timeParts array
        timeParts[0] = hours;
        timeParts[1] = minutes;
        timeParts[2] = meridian.equals("AM") ? 0 : 1; // Store AM/PM as 0
    }
    private LocalTime parseTimeStringToLocalTime(String timeStr) {
        int[] timeParts = new int[3];
        parseTimeToVariables(timeStr, timeParts);
        int hour = timeParts[0];
        int minute = timeParts[1];
        boolean isPM = timeParts[2] == 1;
        if (isPM && hour != 12) {
            hour += 12;
        } else if (!isPM && hour == 12) {
            hour = 0;
        }
        return LocalTime.of(hour, minute);
    }

    private void initializeHeartbeatAnimation(Button button) {
        Timeline timeline = new Timeline(
                new KeyFrame(javafx.util.Duration.ZERO,
                        new KeyValue(button.scaleXProperty(), 1),
                        new KeyValue(button.scaleYProperty(), 1),
                        new KeyValue(button.opacityProperty(), 1)),
                new KeyFrame(javafx.util.Duration.seconds(0.25),
                        new KeyValue(button.scaleXProperty(), 1.1),
                        new KeyValue(button.scaleYProperty(), 1.1),
                        new KeyValue(button.opacityProperty(), 0.8)),
                new KeyFrame(javafx.util.Duration.seconds(0.5),
                        new KeyValue(button.scaleXProperty(), 1),
                        new KeyValue(button.scaleYProperty(), 1),
                        new KeyValue(button.opacityProperty(), 1))
        );

        timeline.setAutoReverse(true);
        timeline.setCycleCount(Timeline.INDEFINITE);

        button.setOnMouseEntered(event -> timeline.play());
        button.setOnMouseExited(event -> timeline.stop());
    }

    @FXML
    private void handleDeleteProductionSession(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String buttonText = clickedButton.getText();
        String sessionType = getSessionTypeFromButtonForDeletion(clickedButton);

        if ("Modify".equals(buttonText)) {
            showModifyDialog(sessionType, clickedButton);
        } else if ("Clear".equals(buttonText)) {
            clearSessionFields(Objects.requireNonNull(sessionType));
            clickedButton.setDisable(true);
        } else if (sessionType != null) {
            boolean confirmDelete = showConfirmationDialog("Are you sure you want to delete the " + sessionType + " session?");
            if (confirmDelete) {
                deleteSession(sessionType);
            }
        }
    }

    private String getSessionTypeFromButtonForDeletion(Button button) {
        if (button == deleteMorningSession) {
            return "Morning";
        } else if (button == deleteAfternoonSession) {
            return "Afternoon";
        } else if (button == deleteEveningSession) {
            return "Evening";
        }
        return null;
    }

    private void deleteSession(String sessionType) {
        try {
            Map<String, String> initialValues = getInitialValues(sessionType);

            LocalDateTime startTime = parseDateTime(initialValues.get("startTime"));
            LocalDateTime endTime = parseDateTime(initialValues.get("endTime"));

            ProductionSession session = findExistingSessionByTime(startTime, endTime);
            if (session != null) {
                boolean deleted = ProductionSessionDAO.deleteProductionSession(session.getSessionID());
                if (deleted) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Production session deleted successfully.");
                    refreshUI();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete production session.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to find session for deletion.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete production session: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean showConfirmationDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }



    public static class HerdNotFoundException extends Exception {
        public HerdNotFoundException(String message) {
            super(message);
        }
    }





    public void initializeProductionData() {
        // Initialize ChoiceBoxes with options
        selectCriteriaChoiceBox.setItems(FXCollections.observableArrayList(
                "Feed Type", "Solution Type", "Breed System", "Breed Type", "Animal Class"));

        // Add listener to selectCriteriaChoiceBox
        selectCriteriaChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                enableDependentCriteriaChoiceBox(newValue);
            }
        });

        // Add listener to dependentCriteriaChoiceBox if needed
        dependentCriteriaChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                compareCattleProduction();
            }
        });


    }

    private void enableDependentCriteriaChoiceBox(String selectedCriteria) {
        switch (selectedCriteria) {
            case "Feed Type":
                dependentCriteriaChoiceBox.setItems(FXCollections.observableArrayList("Dry Matter (DM)", "AS-Fed (AF)"));
                dependentCriteriaChoiceBox.setDisable(false);
                break;
            case "Solution Type":
                dependentCriteriaChoiceBox.setItems(FXCollections.observableArrayList("Equivalent Livestock System (ELS)", "Multiple Livestock System (MLS)"));
                dependentCriteriaChoiceBox.setDisable(false);
                break;
            case "Breed System":
                dependentCriteriaChoiceBox.setItems(FXCollections.observableArrayList("Straightbred", "2-way-x", "3-way-x"));
                dependentCriteriaChoiceBox.setDisable(false);
                break;
            case "Breed Type":
                dependentCriteriaChoiceBox.setItems(FXCollections.observableArrayList("Dual-purpose", "Bos Taurus", "Bos Indicus", "Dairy"));
                dependentCriteriaChoiceBox.setDisable(false);
                break;
            case "Animal Class":
                dependentCriteriaChoiceBox.setItems(FXCollections.observableArrayList("Grow/Finish", "Lactating", "Dry"));
                dependentCriteriaChoiceBox.setDisable(false);
                break;
            default:
                dependentCriteriaChoiceBox.setItems(FXCollections.emptyObservableList());
                dependentCriteriaChoiceBox.setDisable(true);
                break;
        }
    }

    private void initializeColumnCellValueFactoriesForProductionData() {
        cowIdColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().cattleID()));
        currentStageColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().currentStage()));
        selectedStageByDateColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().selectedStageByDate()));
        equivalentSelectedDateColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().equivalentSelectedDate()));
        todayMYColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().todayMY()));
        equivalentDayMYColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().equivalentDayMY()));
        currentStageMilkMYColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().currentStageMilkMY()));
        selectedStageMilkMYColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().selectedStageMilkMY()));
        totalDailyMYColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().totalDailyMY()));
        averageDailyMYColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().averageDailyMY()));
        relativeMYColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().relativeMY()));
        selectedCattlePRColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().performanceRating()));
        comparisonPerformanceColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().comparisonPerformance()));

        // Center-align the cells if TableColumnUtils is available
        TableColumnUtils.centerAlignColumn(cowIdColumn);
        TableColumnUtils.centerAlignColumn(currentStageColumn);
        TableColumnUtils.centerAlignColumn(selectedStageByDateColumn);
        TableColumnUtils.centerAlignColumn(equivalentSelectedDateColumn);
        TableColumnUtils.centerAlignColumn(todayMYColumn);
        TableColumnUtils.centerAlignColumn(equivalentDayMYColumn);
        TableColumnUtils.centerAlignColumn(currentStageMilkMYColumn);
        TableColumnUtils.centerAlignColumn(selectedStageMilkMYColumn);
        TableColumnUtils.centerAlignColumn(totalDailyMYColumn);
        TableColumnUtils.centerAlignColumn(averageDailyMYColumn);
        TableColumnUtils.centerAlignColumn(relativeMYColumn);
        TableColumnUtils.centerAlignColumn(selectedCattlePRColumn);
        TableColumnUtils.centerAlignColumn(comparisonPerformanceColumn);
    }



    private String getSelectedCriteria() {
        String selectedCriteria = selectCriteriaChoiceBox.getValue();
        if (selectedCriteria != null && !selectedCriteria.isEmpty()) {
            return selectedCriteria;
        } else {
            return "";
        }
    }

    private String getSelectedValue(String selectedCriteria) {
        String selectedValue = null;

        // Determine which choice box to get value from based on selectedCriteria
        if ("Feed Type".equals(selectedCriteria) || "Solution Type".equals(selectedCriteria) ||
                "Breed System".equals(selectedCriteria) || "Breed Type".equals(selectedCriteria) ||
                "Animal Class".equals(selectedCriteria)) {
            selectedValue = dependentCriteriaChoiceBox.getValue();
        }

        return selectedValue;
    }

    private void compareCattleProduction() {
        try {
            String selectedCriteria = getSelectedCriteria();
            String selectedValue = getSelectedValue(selectedCriteria);
            List<FilteredCattle> cattleList = cattleDAO.getAllCattleBySelectedCriteria(selectedCriteria, selectedValue);

            List<FilteredCattle> filteredCattleList = new ArrayList<>();
            for (FilteredCattle cow : cattleList) {
                int cattleID = cow.cattleID(); // Access the cattleID field directly

                List<LactationPeriod> lactationPeriods = LactationPeriodDAO.getLactationPeriodsByCattleId(cattleID);

                boolean ongoingLactationFound = false;
                for (LactationPeriod period : lactationPeriods) {
                    if (isOngoingLactationPeriod(period)) {
                        ongoingLactationFound = true;
                        break;
                    }
                }

                if (ongoingLactationFound) {
                    filteredCattleList.add(cow);
                }
            }

            updateComparisonTableView(filteredCattleList);

        } catch (SQLException e) {
            e.printStackTrace(); // Implement proper error handling
        }
    }

    // Helper method to determine if a lactation period is ongoing
    private boolean isOngoingLactationPeriod(LactationPeriod period) {
        if (period.startDate() != null && period.endDate() == null) {
            long daysBetween = ChronoUnit.DAYS.between(period.startDate(), LocalDate.now());
            return daysBetween < 365;
        }
        return false;
    }


    private List<CattleYieldData> cattleYieldData = new ArrayList<>();
    private void updateComparisonTableView(List<FilteredCattle> filteredCattleList) throws SQLException {
        List<CowTableItem> cowTableItems = new ArrayList<>();
        List<CattleYieldData> cattleYieldDataList = new ArrayList<>();

        String selectedStage = determineSelectedLactationStage(ongoingLactationPeriod.startDate(), selectedDateProductionSessionDate);

        LocalDate startDate, endDate;
        String selectedCattleCurrentStage = determineCurrentLactationStage(ongoingLactationPeriod.startDate());

        startDate = determineStageStartDate(ongoingLactationPeriod, selectedStage);
        if (selectedStage.equals(selectedCattleCurrentStage)) {
            endDate = LocalDate.now();
        } else {
            endDate = determineStageEndDate(ongoingLactationPeriod, selectedStage);
        }

        List<ProductionSession> productionSessionsBySelectedStage = ProductionSessionDAO.getProductionSessionsByLactationIdAndDateRange(ongoingLactationPeriod.lactationPeriodID(), startDate, endDate);
        List<ProductionSession> productionSessionsByFromStartToDate = ProductionSessionDAO.getProductionSessionsByLactationIdAndDateRange(ongoingLactationPeriod.lactationPeriodID(), ongoingLactationPeriod.startDate(), LocalDate.now());
        List<ProductionSession> productionSessionsByDate = ProductionSessionDAO.getProductionSessionsByLactationIdAndDate(ongoingLactationPeriod.lactationPeriodID(), selectedDateProductionSessionDate);

        double stageMilkYield = calculateTotalYield(productionSessionsBySelectedStage);
        double todayYield = calculateTotalYield(productionSessionsByDate);
        double totalDailyYield = calculateTotalYield(productionSessionsByFromStartToDate);
        double averageDailyYield = calculateAverageDailyYield(stageMilkYield, startDate, endDate);
        double selectedCattleRelativeDailyYield = Double.parseDouble(decimalFormat.format(todayYield / averageDailyYield));

        int daysElapsed = (int) Duration.between(startDate.atStartOfDay(), selectedDateProductionSessionDate.atStartOfDay()).toDays();
        stageMYLabel.setText(String.valueOf(stageMilkYield));
        selectedDateMYLabel.setText(String.valueOf(todayYield));
        totalDMYLabel.setText(String.valueOf(totalDailyYield));
        averageDMYLabel.setText(String.valueOf(averageDailyYield));
        selectedStageLabel.setText(selectedStage);
        selectedCattleIDLabel.setText(String.valueOf(selectedCattleId));
        currentStageLabel.setText(selectedCattleCurrentStage);
        selectedDateLabel.setText(String.valueOf(selectedDateProductionSessionDate));
        selectedCowRMY.setText(String.valueOf(selectedCattleRelativeDailyYield));
        selectedCowPR.setText(determinePerformanceRating(selectedCattleRelativeDailyYield));

        // For the selected cattle
        ObservableList<Double> selectedCattleDailyYields = FXCollections.observableArrayList(Collections.nCopies(daysElapsed, 0.0));

        CattleYieldData selectedCattleYieldData = new CattleYieldData(selectedCattleId, startDate, endDate, selectedCattleDailyYields);
        cattleYieldDataList.add(selectedCattleYieldData);

        updateComparisonTableViewForSelectedCattle(filteredCattleList, cowTableItems, cattleYieldDataList, daysElapsed, selectedStage,selectedCattleRelativeDailyYield);

        cowTableView.getItems().setAll(cowTableItems);
        cattleYieldData = cattleYieldDataList;

    }

    private void updateComparisonTableViewForSelectedCattle(List<FilteredCattle> filteredCattleList, List<CowTableItem> cowTableItems, List<CattleYieldData> cattleYieldDataList, int daysElapsed, String selectedStage,double selectedCattleRelativeDailyYield) throws SQLException {
        for (FilteredCattle cow : filteredCattleList) {
            int cattleID = cow.cattleID();

            if (cattleID == selectedCattleId) {
                continue;
            }

            LactationPeriod ongoingPeriod = getOngoingLactationPeriod(cattleID);
            if (ongoingPeriod == null) {
                continue;
            }

            String currentStage = determineCurrentLactationStage(ongoingPeriod.startDate());
            LocalDate startDate,endDate;
            startDate = determineStageStartDate(ongoingPeriod, selectedStage);
            if (selectedStage.equals(currentStage)) {
                endDate = LocalDate.now();
            } else {
                endDate = determineStageEndDate(ongoingPeriod, selectedStage);
            }

            ObservableList<Double> dailyYields = FXCollections.observableArrayList(Collections.nCopies(daysElapsed, 0.0));

            CattleYieldData cattleYieldData = new CattleYieldData(cattleID, startDate, endDate, dailyYields);
            cattleYieldDataList.add(cattleYieldData);

            LocalDate startDate2 = determineStageStartDate(ongoingPeriod, currentStage);
            LocalDate endDate2 = determineStageEndDate(ongoingPeriod, currentStage);

            LocalDate estimatedDate = estimateProductionDate(startDate, daysElapsed);

            List<ProductionSession> productionSessionsBySelectedStage = ProductionSessionDAO.getProductionSessionsByLactationIdAndDateRange(ongoingPeriod.lactationPeriodID(), startDate, endDate);
            List<ProductionSession> productionSessionsByNow = ProductionSessionDAO.getProductionSessionsByLactationIdAndDateRange(ongoingPeriod.lactationPeriodID(), ongoingPeriod.startDate(), LocalDate.now());
            List<ProductionSession> productionSessionsByCurrentStage = ProductionSessionDAO.getProductionSessionsByLactationIdAndDateRange(ongoingPeriod.lactationPeriodID(), startDate2, endDate2);
            List<ProductionSession> productionSessionsByDate = ProductionSessionDAO.getProductionSessionsByLactationIdAndDate(ongoingPeriod.lactationPeriodID(), estimatedDate);
            List<ProductionSession> todayMilkYield = ProductionSessionDAO.getProductionSessionsByLactationIdAndDate(ongoingPeriod.lactationPeriodID(), LocalDate.now());
            double todayMY = calculateTotalYield(todayMilkYield);

            double stageMilkYield = calculateTotalYield(productionSessionsBySelectedStage);
            double currentStageMilkMY = calculateTotalYield(productionSessionsByCurrentStage);
            double dateDailyYield = calculateTotalYield(productionSessionsByDate);
            double totalDailyYield = calculateTotalYield(productionSessionsByNow);
            double averageDailyYield = calculateAverageDailyYield(stageMilkYield, startDate, endDate);
            double relativeDailyYield = Double.parseDouble(decimalFormat.format(dateDailyYield / averageDailyYield));
            String performanceRating = determinePerformanceRating(relativeDailyYield);
            String selectedCattlePerformanceRating = determineSelectedCattleComparisonRating(relativeDailyYield, selectedCattleRelativeDailyYield);

            cowTableItems.add(new CowTableItem(
                    String.valueOf(cattleID),
                    currentStage,
                    selectedStage,
                    estimatedDate.toString(),
                    todayMY,
                    dateDailyYield,
                    currentStageMilkMY,
                    stageMilkYield,
                    totalDailyYield,
                    averageDailyYield,
                    relativeDailyYield,
                    performanceRating,
                    selectedCattlePerformanceRating
            ));
        }
    }

    @FXML
    private void showChart() throws SQLException {
        populateDailyYields(cattleYieldData);
        createLineChart(cattleYieldData);
    }

    private void populateDailyYields(List<CattleYieldData> cattleYieldDataList) throws SQLException {
        for (CattleYieldData cattleYieldData : cattleYieldDataList) {
            LocalDate startDate = cattleYieldData.startDate();
            LocalDate endDate = cattleYieldData.endDate();
            int lactationPeriodId = Objects.requireNonNull(getOngoingLactationPeriod(cattleYieldData.cattleID())).lactationPeriodID();

            // Fetch all production sessions for the date range
            List<ProductionSession> productionSessions = ProductionSessionDAO.getProductionSessionsByLactationIdAndDateRange(lactationPeriodId, startDate, endDate);

            // Process each date in the range
            ObservableList<Double> dailyYields = cattleYieldData.dailyYields();
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                final LocalDate currentDate = date; // effectively final variable for lambda

                // Filter production sessions for the current date
                List<ProductionSession> filteredSessions = productionSessions.stream()
                        .filter(session -> {
                            LocalDate sessionDate = session.getStartTime().toLocalDateTime().toLocalDate();
                            return sessionDate.equals(currentDate);
                        })
                        .collect(Collectors.toList());

                double totalYield = calculateTotalYield(filteredSessions);
                int index = (int) ChronoUnit.DAYS.between(startDate, date);

                if (index >= 0 && index < dailyYields.size()) {
                    dailyYields.set(index, totalYield);
                }
            }
        }
    }




    private void createLineChart(List<CattleYieldData> cattleYieldDataList) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);

        xAxis.setLabel("Index of Days Considered");
        yAxis.setLabel("Milk Yield");

        for (CattleYieldData cattleYieldData : cattleYieldDataList) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Cow " + cattleYieldData.cattleID() + " (" +
                    cattleYieldData.startDate() + " to " + cattleYieldData.endDate() + ")");

            int index = 0;
            for (Double yield : cattleYieldData.dailyYields()) {
                series.getData().add(new XYChart.Data<>(String.valueOf(index), yield));
                index++;
            }

            lineChart.getData().add(series);
        }

        comparisonChartVBox.getChildren().clear();
        comparisonChartVBox.getChildren().add(lineChart);
    }




    private String determineSelectedCattleComparisonRating(double comparedCattleYield, double selectedCattleYield) {
        double difference = (comparedCattleYield - selectedCattleYield);

        // Define a HashMap with performance rating scale
        Map<String, Double[]> comparisonRatings = new HashMap<>();
        comparisonRatings.put("Poor", new Double[]{0.2, Double.POSITIVE_INFINITY});
        comparisonRatings.put("Inferior", new Double[]{0.05, 0.19});
        comparisonRatings.put("Comparable", new Double[]{-0.04, 0.05});
        comparisonRatings.put("Better", new Double[]{-0.19, -0.04});
        comparisonRatings.put("Superior", new Double[]{Double.NEGATIVE_INFINITY, -0.2});

        // Find the first matching range
        for (Map.Entry<String, Double[]> entry : comparisonRatings.entrySet()) {
            Double[] range = entry.getValue();
            if (difference >= range[0] && difference < range[1]) { // Use '<' for exclusive upper bound
                return entry.getKey();
            }
        }

        // Default return value if no range is matched
        return "Unknown";
    }



    private double calculateTotalYield(List<ProductionSession> productionSessions) {
        double totalYield = 0;
        for (ProductionSession session : productionSessions) {
            totalYield += session.getProductionVolume();
        }
        return Double.parseDouble(decimalFormat.format(totalYield));
    }

    private double calculateAverageDailyYield(double stageMilkYield,LocalDate startDate,LocalDate endDate ) {
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        return Double.parseDouble(decimalFormat.format(stageMilkYield / totalDays));
    }


    private String determinePerformanceRating(double relativeDailyMilkYield) {
        // Performance rating thresholds (modify as needed)
        final Map<String, Double[]> performanceRanges = new HashMap<>();
        performanceRanges.put("Excellent", new Double[]{1.2, Double.POSITIVE_INFINITY});
        performanceRanges.put("Good", new Double[]{1.0, 1.2});
        performanceRanges.put("Average", new Double[]{0.8, 1.0});
        performanceRanges.put("Below Average", new Double[]{0.6, 0.8});
        performanceRanges.put("Poor", new Double[]{0.0, 0.6});

        for (Map.Entry<String, Double[]> entry : performanceRanges.entrySet()) {
            String rating = entry.getKey();
            Double lowerBound = entry.getValue()[0];
            Double upperBound = entry.getValue()[1];

            if (lowerBound <= relativeDailyMilkYield && relativeDailyMilkYield < upperBound) {
                return rating;
            }
        }

        // Default rating if yield falls outside all categories
        return "Very Poor";
    }


    public LactationPeriod getOngoingLactationPeriod(int cattleID) throws SQLException {
        List<LactationPeriod> lactationPeriods = LactationPeriodDAO.getLactationPeriodsByCattleId(cattleID);
        for (LactationPeriod period : lactationPeriods) {
            if (isOngoingLactationPeriod(period)) {
                return period;
            }
        }
        return null;
    }


    public LocalDate determineStageStartDate(LactationPeriod lactationPeriod, String selectedStage) {
        Integer[] stageRange = stageDaysRangeMap.get(selectedStage);
        int stageStartDay = stageRange[0];
        return lactationPeriod.startDate().plusDays(stageStartDay);
    }


    public LocalDate determineStageEndDate(LactationPeriod lactationPeriod, String selectedStage) {
        Integer[] stageRange = stageDaysRangeMap.get(selectedStage);
        int stageEndDay = stageRange[1];
        if (stageEndDay == -1) {
            stageEndDay=365;
        }
        return lactationPeriod.startDate().plusDays(stageEndDay);
    }
    public String determineCurrentLactationStage(LocalDate LactationStartDate) {
        long totalDays = Duration.between(LactationStartDate.atStartOfDay(), LocalDate.now().atStartOfDay()).toDays();
        return calculateProductionStage((int) totalDays);
    }
    public String determineSelectedLactationStage(LocalDate lactationStartDate,LocalDate productionDate) {
        long totalDays = Duration.between(lactationStartDate.atStartOfDay(), productionDate.atStartOfDay()).toDays();
        return calculateProductionStage((int) totalDays);
    }
    public LocalDate estimateProductionDate(LocalDate lactationStartDate, int daysElapsed) {
        return lactationStartDate.plusDays(daysElapsed);
    }

    private void clearAndDisableChoiceBoxes() {
        selectCriteriaChoiceBox.getSelectionModel().clearSelection();
        dependentCriteriaChoiceBox.getSelectionModel().clearSelection();
        dependentCriteriaChoiceBox.setDisable(true);
        cowTableView.getItems().clear();
        cowTableView.setPlaceholder(new Label("No Criteria selected"));

        selectedCattleIDLabel.setText("N/A");
        currentStageLabel.setText("N/A");
        selectedDateLabel.setText("N/A");
        selectedStageLabel.setText("N/A");
        stageMYLabel.setText("N/A");
        selectedDateMYLabel.setText("N/A");
        totalDMYLabel.setText("N/A");
        averageDMYLabel.setText("N/A");
        selectedCowRMY.setText("N/A");
        selectedCowPR.setText("N/A");

        comparisonChartVBox.getChildren().clear();
    }

    private void initializeSplitPlane() {
        SplitPaneDividerEnforcer dividerEnforcer = new SplitPaneDividerEnforcer(minPosition, maxPosition);
        dividerEnforcer.enforceConstraints(splitPane);
        CattleController cattleController = new CattleController();
        leftArrowButton.setOnAction(event -> cattleController.animateSplitPane(minPosition,splitPane,minPosition,maxPosition,leftArrowButton,rightArrowButton));
        rightArrowButton.setOnAction(event -> cattleController.animateSplitPane(maxPosition,splitPane,minPosition,maxPosition,leftArrowButton,rightArrowButton));
        splitPane.getDividers().getFirst().positionProperty().addListener((obs, oldPos, newPos) -> cattleController.updateButtonsPosition(newPos.doubleValue(),splitPane,leftArrowButton,rightArrowButton));

    }
    private void initializeShowChartButtonState() {
        // Initially disable the showChartButton
        showChartButton.setDisable(true);

        // Add listeners to both ChoiceBoxes
        selectCriteriaChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateChartButtonState());
        dependentCriteriaChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateChartButtonState());
        dependentCriteriaChoiceBox.disableProperty().addListener((observable, oldValue, newValue) -> updateChartButtonState());
    }

    private void updateChartButtonState() {
        boolean selectCriteriaSelected = selectCriteriaChoiceBox.getValue() != null;
        boolean dependentCriteriaEnabled = !dependentCriteriaChoiceBox.isDisable() && dependentCriteriaChoiceBox.getValue() != null;

        showChartButton.setDisable(!(selectCriteriaSelected && dependentCriteriaEnabled));
    }



}