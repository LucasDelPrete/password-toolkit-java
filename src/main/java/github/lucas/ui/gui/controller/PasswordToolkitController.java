package github.lucas.ui.gui.controller;

import github.lucas.core.common.PasswordRequirements;
import github.lucas.core.pass_breach.PasswordBreachVerifier;
import github.lucas.core.pass_generation.Credential;
import github.lucas.core.pass_generation.PasswordGenerator;
import github.lucas.core.pass_strength.PasswordFeedback;
import github.lucas.core.pass_strength.PasswordStrength;
import github.lucas.core.pass_strength.PasswordStrengthAnalyzer;
import github.lucas.ui.gui.utils.DialogUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class PasswordToolkitController implements Initializable {
    private final static String GREEN = "#2ecc71";
    private final static String ORANGE = "#e67e22";
    private final static String RED = "#e74c3c";
    private final static String BLACK = "#000000";

    @FXML
    private HBox searchBox;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private Label breachedPassMsg1Label;

    @FXML
    private Label breachedPassMsg2Label;

    @FXML
    private Label saveEmptyPassLabel;

    @FXML
    private Button generatePassButton;

    @FXML
    private Label passBreachEmptyInputLabel;

    @FXML
    private TextField passBreachTextField;

    @FXML
    private TextField passGenDisplay;

    @FXML
    private Label passGenInvalidLengthLabel;

    @FXML
    private TextField passGenLengthTextField;

    @FXML
    private Label passStrengthDigitLabel;

    @FXML
    private Label passStrengthLengthLabel;

    @FXML
    private Label passStrengthLowerLabel;

    @FXML
    private Label passStrengthResultLabel;

    @FXML
    private Label passStrengthSpecialLabel;

    @FXML
    private TextField passStrengthTextField;

    @FXML
    private Label passStrengthUpperLabel;

    @FXML
    private Button saveGeneratedPassButton;

    @FXML
    private ListView<String> savedPassListView;

    @FXML
    private Label unbreachedPassMsg1Label;

    @FXML
    private Label unbreachedPassMsg2Label;

    @FXML
    private TextField searchBar;

    @FXML
    private Button clearButton;

    @FXML
    private Button verifyPassBreachButton;

    private Map<String, Credential> passwordDatabase;

    private ObservableList<String> masterData;

    private FilteredList<String> filteredData;

    public void initData(Map<String, Credential> passwordDatabase) {
        this.passwordDatabase = passwordDatabase;

        masterData = FXCollections.observableArrayList(
                passwordDatabase.keySet()
                        .stream()
                        .sorted(String.CASE_INSENSITIVE_ORDER)
                        .toList()
        );
        filteredData = new FilteredList<>(masterData, s -> true);
        savedPassListView.setItems(filteredData);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> mainPane.requestFocus());
        mainPane.setFocusTraversable(true);
        mainPane.setOnMousePressed(e -> mainPane.requestFocus());
        passStrengthTextField.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.isEmpty()) {
                PasswordFeedback feedback = PasswordStrengthAnalyzer.analyzePassword(newText);
                updateStrengthLabels(feedback);
            } else {
                resetRequirements();
            }
        });
        passBreachTextField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.isEmpty()) {
                breachedPassMsg1Label.setVisible(false);
                breachedPassMsg2Label.setVisible(false);
                unbreachedPassMsg1Label.setVisible(false);
                unbreachedPassMsg2Label.setVisible(false);
            }
        });
        savedPassListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selectedSite = savedPassListView.getSelectionModel().getSelectedItem();
                if (selectedSite != null) {
                    openEditPopup(selectedSite);
                }
            }
        });
        searchBar.textProperty().addListener((obs, oldText, newText) -> {
            clearButton.setVisible(!newText.isEmpty());
            String lowerCaseFilter = newText.toLowerCase();

            filteredData.setPredicate(item -> {
                if (newText.isEmpty()) {
                    return true;
                }
                return item.toLowerCase().contains(lowerCaseFilter);
            });
        });

        passBreachTextField.setOnAction(e -> verifyPassBreachButton.fire());
        passGenLengthTextField.setOnAction(e -> generatePassButton.fire());
        passGenDisplay.setOnAction(e -> saveGeneratedPassButton.fire());
    }

    private void openEditPopup(String siteName) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/SavePassword.fxml"));
            Parent popupRoot = fxmlLoader.load();

            SavePasswordController controller = fxmlLoader.getController();
            controller.setPasswordDatabase(passwordDatabase);
            controller.setSiteToEdit(siteName);

            Stage stage = new Stage();
            stage.setScene(new Scene(popupRoot, 400, 600));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setTitle("Edit Credentials");
            stage.initOwner(savedPassListView.getScene().getWindow());
            stage.showAndWait();

            refreshList();
        } catch (IOException e) {
            DialogUtils.showError("Opening record failed", "There was an error opening the record. Please try again later.");
        }
    }

    @FXML
    void generatePassword(ActionEvent event) {
        String input = passGenLengthTextField.getText().trim();
        int enteredLength;
        if (!input.isEmpty()) {
            passGenInvalidLengthLabel.setVisible(false);
            enteredLength = Integer.parseInt(input);
            if (enteredLength > 6 && enteredLength < 256) {
                String generatedPass = PasswordGenerator.generate(enteredLength);
                passGenDisplay.setText(generatedPass);
            } else {
                passGenInvalidLengthLabel.setVisible(true);
            }
        } else {
            passGenInvalidLengthLabel.setVisible(true);
        }
    }

    @FXML
    void saveGeneratedPassword(ActionEvent event) throws IOException {
        String password = passGenDisplay.getText().trim();
        if (!password.isEmpty()) {
            saveEmptyPassLabel.setVisible(false);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/SavePassword.fxml"));
            Parent popupRoot = fxmlLoader.load();

            SavePasswordController controller = fxmlLoader.getController();
            controller.setPassword(password);
            controller.setPasswordDatabase(passwordDatabase);

            Stage stage = new Stage();
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.setTitle("Save Password");
            stage.setScene(new Scene(popupRoot, 400, 600));
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            refreshList();
        } else {
            saveEmptyPassLabel.setVisible(true);
        }
    }

    @FXML
    void verifyPasswordBreach(ActionEvent event) {
        String enteredPass = passBreachTextField.getText().trim();

        if (!enteredPass.isEmpty()) {
            try {
                passBreachEmptyInputLabel.setVisible(false);
                boolean wasBreached = PasswordBreachVerifier.checkPassword(enteredPass);
                if (wasBreached) {
                    breachedPassMsg1Label.setVisible(true);
                    breachedPassMsg2Label.setVisible(true);
                    unbreachedPassMsg1Label.setVisible(false);
                    unbreachedPassMsg2Label.setVisible(false);
                } else {
                    unbreachedPassMsg1Label.setVisible(true);
                    unbreachedPassMsg2Label.setVisible(true);
                    breachedPassMsg1Label.setVisible(false);
                    breachedPassMsg2Label.setVisible(false);
                }

            } catch (Exception e) {
                DialogUtils.showError("Password Verification Failed", "There was an error verifying the password. Please try again later.");
            }
        } else {
            passBreachEmptyInputLabel.setVisible(true);
        }
    }

    @FXML
    private void clearSearch() {
        searchBar.clear();
    }

    private void updateStrengthLabels(PasswordFeedback feedback) {
        List<PasswordRequirements> requirements = feedback.getMissingRequirements();
        PasswordStrength strength = feedback.getStrength();
        if (requirements.contains(PasswordRequirements.UPPER)) {
            setMissingRequirement(passStrengthUpperLabel);
        } else {
            setFulfilledRequirement(passStrengthUpperLabel);
        }
        if (requirements.contains(PasswordRequirements.LOWER)) {
            setMissingRequirement(passStrengthLowerLabel);
        } else {
            setFulfilledRequirement(passStrengthLowerLabel);
        }
        if (requirements.contains(PasswordRequirements.DIGIT)) {
            setMissingRequirement(passStrengthDigitLabel);
        } else {
            setFulfilledRequirement(passStrengthDigitLabel);
        }
        if (requirements.contains(PasswordRequirements.SPECIAL)) {
            setMissingRequirement(passStrengthSpecialLabel);
        } else {
            setFulfilledRequirement(passStrengthSpecialLabel);
        }
        if (requirements.contains(PasswordRequirements.SIZE)) {
            setMissingRequirement(passStrengthLengthLabel);
        } else {
            setFulfilledRequirement(passStrengthLengthLabel);
        }
        passStrengthResultLabel.setVisible(true);
        passStrengthResultLabel.setText(strength.toString());
        if (strength.equals(PasswordStrength.HIGH)) {
            passStrengthResultLabel.setStyle("-fx-text-fill: " + GREEN + ";");
        } else if (strength.equals(PasswordStrength.MEDIUM)) {
            passStrengthResultLabel.setStyle("-fx-text-fill: " + ORANGE + ";");
        } else {
            passStrengthResultLabel.setStyle("-fx-text-fill: " + RED + ";");
        }
    }

    private void setMissingRequirement(Label label) {
        label.setStyle("-fx-text-fill: " + RED + ";");
    }

    private void setFulfilledRequirement(Label label) {
        label.setStyle("-fx-text-fill: " + GREEN + ";");
    }

    private void refreshList() {
        List<String> sortedSites = new ArrayList<>(passwordDatabase.keySet());
        sortedSites.sort(String.CASE_INSENSITIVE_ORDER);
        masterData.setAll(sortedSites);
    }

    private void resetRequirements() {
        passStrengthUpperLabel.setStyle("-fx-text-fill: " + BLACK + ";");
        passStrengthLowerLabel.setStyle("-fx-text-fill: " + BLACK + ";");
        passStrengthDigitLabel.setStyle("-fx-text-fill: " + BLACK + ";");
        passStrengthSpecialLabel.setStyle("-fx-text-fill: " + BLACK + ";");
        passStrengthLengthLabel.setStyle("-fx-text-fill: " + BLACK + ";");
        passStrengthResultLabel.setVisible(false);
    }
}
