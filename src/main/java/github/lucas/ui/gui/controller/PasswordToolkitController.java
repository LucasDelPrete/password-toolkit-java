package github.lucas.ui.gui.controller;

import github.lucas.core.common.PasswordRequirements;
import github.lucas.core.pass_breach.PasswordBreachVerifier;
import github.lucas.core.pass_strength.PasswordFeedback;
import github.lucas.core.pass_strength.PasswordStrength;
import github.lucas.core.pass_strength.PasswordStrengthAnalyzer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PasswordToolkitController implements Initializable {
    private final static String GREEN = "#2ecc71";
    private final static String ORANGE = "#e67e22";
    private final static String RED = "#e74c3c";
    private final static String BLACK = "#000000";

    @FXML
    private AnchorPane mainPane;

    @FXML
    private Label breachedPassMsg1Label;

    @FXML
    private Label breachedPassMsg2Label;

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
    private Label passBreachEmptyInputLabel;

    @FXML
    private TextField passStrengthTextField;

    @FXML
    private TextField passBreachTextField;

    @FXML
    private Label passStrengthUpperLabel;

    @FXML
    private Label unbreachedPassMsg1Label;

    @FXML
    private Label unbreachedPassMsg2Label;

    @FXML
    private Button verifyPassBreachButton;

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
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Password Verification Failed");
                alert.setContentText("There was an error verifying the password. Please try again later.");
                alert.showAndWait();
            }
        } else {
            passBreachEmptyInputLabel.setVisible(true);
        }
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

    private void resetRequirements() {
        passStrengthUpperLabel.setStyle("-fx-text-fill: " + BLACK + ";");
        passStrengthLowerLabel.setStyle("-fx-text-fill: " + BLACK + ";");
        passStrengthDigitLabel.setStyle("-fx-text-fill: " + BLACK + ";");
        passStrengthSpecialLabel.setStyle("-fx-text-fill: " + BLACK + ";");
        passStrengthLengthLabel.setStyle("-fx-text-fill: " + BLACK + ";");
        passStrengthResultLabel.setVisible(false);
    }
}
