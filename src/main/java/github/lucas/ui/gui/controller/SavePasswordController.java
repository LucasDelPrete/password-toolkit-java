package github.lucas.ui.gui.controller;

import github.lucas.core.pass_strength.PasswordFeedback;
import github.lucas.core.pass_strength.PasswordStrengthAnalyzer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class SavePasswordController implements Initializable {

    @FXML
    private AnchorPane mainPane;

    @FXML
    private TextField passwordTextField;

    @FXML
    private Button saveRecordButton;

    @FXML
    private Label siteNameDisplayLabel;

    @FXML
    private Label incompleteRecordLabel;

    @FXML
    private TextField siteNameTextField;

    @FXML
    private TextField usernameTextField;

    private Map<String, Map<String, String>> passwordDatabase = new HashMap<>();

    public void setPassword(String password) {
        passwordTextField.setText(password);
    }

    public void setPasswordDatabase(Map<String, Map<String, String>> passwordDatabase){
        this.passwordDatabase = passwordDatabase;
    }

    @FXML
    void saveRecord(ActionEvent event) {
        if (!siteNameTextField.getText().trim().isEmpty() && !usernameTextField.getText().trim().isEmpty() && !passwordTextField.getText().trim().isEmpty()) {
            incompleteRecordLabel.setVisible(false);
            String site = siteNameTextField.getText().trim();
            String username = usernameTextField.getText().trim();
            String password = passwordTextField.getText();
            passwordDatabase.computeIfAbsent(site, k -> new HashMap<>())
                    .put(username, password);

            ((Stage) saveRecordButton.getScene().getWindow()).close();

        } else {
            incompleteRecordLabel.setVisible(true);
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> mainPane.requestFocus());
        mainPane.setFocusTraversable(true);
        mainPane.setOnMousePressed(e -> mainPane.requestFocus());

        siteNameTextField.textProperty().addListener((obs, oldText, newText) -> {
            siteNameDisplayLabel.setText(newText);
        });
    }
}
