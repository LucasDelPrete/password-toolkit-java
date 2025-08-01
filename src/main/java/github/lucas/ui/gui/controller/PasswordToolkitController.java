package github.lucas.ui.gui.controller;

import github.lucas.core.common.PasswordRequirements;
import github.lucas.core.pass_strength.PasswordFeedback;
import github.lucas.core.pass_strength.PasswordStrength;
import github.lucas.core.pass_strength.PasswordStrengthAnalyzer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PasswordToolkitController implements Initializable {
    private final static String GREEN = "#2ecc71";
    private final static String ORANGE = "#e67e22";
    private final static String RED = "#e74c3c";

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        passStrengthTextField.textProperty().addListener((obs, oldText, newText) -> {
            PasswordFeedback feedback = PasswordStrengthAnalyzer.analyzePassword(newText);
            updateStrengthLabels(feedback);
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

}
