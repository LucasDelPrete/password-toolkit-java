package github.lucas.ui.gui;

import github.lucas.core.pass_generation.Credential;
import github.lucas.persistence.jsonPersistence.JsonPersistence;
import github.lucas.ui.gui.controller.PasswordToolkitController;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class PasswordToolkit extends Application {

    private final Map<String, Credential> passwordDatabase = new HashMap<>();
    private final File saveFile = new File("passwords.json");

    @Override
    public void start(Stage stage) throws Exception {
        loadDataFromJson();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/PasswordToolkit.fxml"));
        Parent root = fxmlLoader.load();

        PasswordToolkitController controller = fxmlLoader.getController();
        controller.initData(passwordDatabase);

        stage.setTitle("Password Toolkit");
        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        saveDataToJson();
    }

    public void saveDataToJson() {
        try {
            JsonPersistence.saveToFile(passwordDatabase, saveFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadDataFromJson() {
        try {
            if (saveFile.exists()) {
                Map<String, Credential> loaded = JsonPersistence.loadFromFile(saveFile);
                if (loaded != null) {
                    passwordDatabase.clear();
                    passwordDatabase.putAll(loaded);
                    System.out.println("Loaded data from JSON file.");
                }
            } else {
                System.out.println("No existing JSON file found — starting with empty database.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch();
    }
}
