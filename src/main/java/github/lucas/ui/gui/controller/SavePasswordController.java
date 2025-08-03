package github.lucas.ui.gui.controller;

import github.lucas.core.pass_generation.Credential;
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

    @FXML
    private Button deleteRecordButton;

    private Map<String, Credential> passwordDatabase;
    private String originalSite;

    public void setPassword(String password) {
        passwordTextField.setText(password);
    }

    public void setPasswordDatabase(Map<String, Credential> passwordDatabase) {
        this.passwordDatabase = passwordDatabase;
    }

    public void setSiteToEdit(String site) {
        siteNameTextField.setText(site);
        siteNameTextField.setDisable(true);
        originalSite = site;

        deleteRecordButton.setVisible(true);

        Credential credentials = passwordDatabase.get(site);
        if (credentials != null) {
            usernameTextField.setText(credentials.getUsername());
            passwordTextField.setText(credentials.getPassword());
        }
    }

    @FXML
    void saveRecord(ActionEvent event) {
        String site = siteNameTextField.getText().trim();
        String username = usernameTextField.getText().trim();
        String password = passwordTextField.getText().trim();

        if (!site.isEmpty() && !username.isEmpty() && !password.isEmpty()) {
            incompleteRecordLabel.setVisible(false);

            if (originalSite == null) {
                originalSite = site;
                passwordDatabase.put(site, new Credential(username, password));
            } else {
                Credential credential = passwordDatabase.get(originalSite);
                credential.setUsername(username);
                credential.setPassword(password);
            }

            ((Stage) saveRecordButton.getScene().getWindow()).close();

        } else {
            incompleteRecordLabel.setVisible(true);
        }
    }

    @FXML
    void deleteRecord(ActionEvent event) {
        passwordDatabase.remove(siteNameTextField.getText().trim());
        ((Stage) saveRecordButton.getScene().getWindow()).close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> mainPane.requestFocus());
        mainPane.setFocusTraversable(true);
        mainPane.setOnMousePressed(e -> mainPane.requestFocus());

        siteNameTextField.textProperty().addListener((obs, oldText, newText) -> siteNameDisplayLabel.setText(newText));
    }
}
