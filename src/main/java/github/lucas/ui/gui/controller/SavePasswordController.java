package github.lucas.ui.gui.controller;

import github.lucas.core.pass_generation.Credential;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import github.lucas.ui.gui.utils.DialogUtils;
import java.net.URL;
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
    private Label editingWarning1Label;

    @FXML
    private Label editingWarning2Label;

    @FXML
    private TextField siteNameTextField;

    @FXML
    private TextField usernameTextField;

    @FXML
    private Button deleteRecordButton;

    private Map<String, Credential> passwordDatabase;
    private String originalSite;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            mainPane.requestFocus();
            mainPane.setFocusTraversable(true);
            mainPane.setOnMousePressed(e -> mainPane.requestFocus());

            Scene scene = saveRecordButton.getScene();
            if (scene != null) {
                scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    if (event.getCode() == KeyCode.ESCAPE) {
                        ((Stage) saveRecordButton.getScene().getWindow()).close();
                    }
                });
            }
        });
        siteNameTextField.textProperty().addListener((obs, oldText, newText) -> {
            siteNameDisplayLabel.setText(newText);

            boolean found = passwordDatabase.keySet()
                    .stream()
                    .anyMatch(k -> k.equalsIgnoreCase(newText));

            if (found) {
                originalSite = newText;
            } else {
                originalSite = null;
            }
        });
        siteNameTextField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (wasFocused && !isNowFocused) {
                String website = siteNameTextField.getText().trim().toLowerCase();
                if (passwordDatabase.containsKey(website)) {
                    editingWarning1Label.setVisible(true);
                    editingWarning2Label.setVisible(true);
                } else {
                    editingWarning1Label.setVisible(false);
                    editingWarning2Label.setVisible(false);
                }
            }
        });
        siteNameTextField.setOnAction(e -> saveRecordButton.fire());
        usernameTextField.setOnAction(e -> saveRecordButton.fire());
        passwordTextField.setOnAction(e -> saveRecordButton.fire());
    }

    @FXML
    void saveRecord(ActionEvent event) {
        String site = siteNameTextField.getText().trim();
        String siteKey = site.toLowerCase();
        String username = usernameTextField.getText().trim();
        String password = passwordTextField.getText().trim();

        if (!site.isEmpty() && !username.isEmpty() && !password.isEmpty()) {
            incompleteRecordLabel.setVisible(false);

            if (originalSite == null) {
                originalSite = site;
                passwordDatabase.put(site, new Credential(username, password));
                ((Stage) saveRecordButton.getScene().getWindow()).close();
            } else {
                Credential credential = passwordDatabase.get(originalSite.toLowerCase());
                if (credential != null && (!username.equals(credential.getUsername()) || !password.equals(credential.getPassword()))) {
                    if (DialogUtils.showConfirmation("Edit record", "This website record already exists, are you sure you want to edit it?")) {
                        credential.setUsername(username);
                        credential.setPassword(password);
                        ((Stage) saveRecordButton.getScene().getWindow()).close();
                    }
                } else {
                    ((Stage) saveRecordButton.getScene().getWindow()).close();
                }
            }
        } else {
            incompleteRecordLabel.setVisible(true);
        }
    }

    @FXML
    void deleteRecord(ActionEvent event) {
        if (DialogUtils.showConfirmation("Delete record", "Are you sure you want to delete this record?")) {
            passwordDatabase.remove(siteNameTextField.getText().trim().toLowerCase());
            ((Stage) saveRecordButton.getScene().getWindow()).close();
        }
    }

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
}
