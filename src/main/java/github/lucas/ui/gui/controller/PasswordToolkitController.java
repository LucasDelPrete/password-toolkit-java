package github.lucas.ui.gui.controller;

import github.lucas.core.common.PasswordRequirements;
import github.lucas.persistence.security.EncryptedPersistence;
import github.lucas.core.pass_breach.PasswordBreachVerifier;
import github.lucas.core.pass_generation.Credential;
import github.lucas.core.pass_generation.PasswordGenerator;
import github.lucas.core.pass_strength.PasswordFeedback;
import github.lucas.core.pass_strength.PasswordStrength;
import github.lucas.core.pass_strength.PasswordStrengthAnalyzer;
import github.lucas.ui.gui.PasswordToolkit;
import github.lucas.ui.gui.utils.DialogUtils;
import github.lucas.ui.gui.utils.SaveFileUtils;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
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

    private File saveFile;
    private PasswordToolkit mainApp;

    public void initData(Map<String, Credential> passwordDatabase, File saveFile, PasswordToolkit mainApp) {
        this.passwordDatabase = passwordDatabase;
        this.saveFile = saveFile;
        this.mainApp = mainApp;

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
                    openEditPopup(selectedSite.toLowerCase());
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
    private void handleChangePassword() {
        Optional<PasswordChangeData> optData = promptForPasswordChange();

        if (optData.isPresent()){
            PasswordChangeData data = optData.get();
            if (!data.newPassword().equals(data.confirmPassword())) {
                DialogUtils.showError("Mismatch error", "Passwords do not match.");
                return;
            }

            try {
                EncryptedPersistence.changePassword(saveFile, data.currentPassword(), data.newPassword());

                SaveFileUtils.EncryptionParams params = SaveFileUtils.loadDataEncrypted(passwordDatabase, saveFile, data.newPassword());
                mainApp.setEncryptionParams(params.salt(), params.secretKey());
                refreshList();

                DialogUtils.showAlert("Password changed successfully.");
            } catch (Exception e) {
                DialogUtils.showError("Change failure","Failed to change password. Make sure the current password is correct.");
            }
        }
    }

    @FXML
    private void handleExit() {
        Platform.exit();
    }

    @FXML
    private void handleAbout() {
        DialogUtils.showAlert("Password Toolkit v1.0\nCreated by Lucas");
    }

    private record PasswordChangeData(String currentPassword, String newPassword, String confirmPassword) {}
    private Optional<PasswordChangeData> promptForPasswordChange() {
        Dialog<PasswordChangeData> dialog = new Dialog<>();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Please enter your current and new password");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        PasswordField currentPass = new PasswordField();
        currentPass.setPromptText("Current password");

        PasswordField newPass = new PasswordField();
        newPass.setPromptText("New password");

        PasswordField confirmPass = new PasswordField();
        confirmPass.setPromptText("Confirm new password");

        // Layout using GridPane
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Current Password:"), 0, 0);
        grid.add(currentPass, 1, 0);
        grid.add(new Label("New Password:"), 0, 1);
        grid.add(newPass, 1, 1);
        grid.add(new Label("Confirm Password:"), 0, 2);
        grid.add(confirmPass, 1, 2);

        dialog.getDialogPane().setContent(grid);

        Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);

        ChangeListener<String> listener = (obs, oldVal, newVal) -> okButton.setDisable(
                currentPass.getText().trim().isEmpty() ||
                        newPass.getText().trim().isEmpty() ||
                        confirmPass.getText().trim().isEmpty()
        );

        currentPass.textProperty().addListener(listener);
        newPass.textProperty().addListener(listener);
        confirmPass.textProperty().addListener(listener);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new PasswordChangeData(currentPass.getText(), newPass.getText(), confirmPass.getText());
            }
            return null;
        });

        return dialog.showAndWait();
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
