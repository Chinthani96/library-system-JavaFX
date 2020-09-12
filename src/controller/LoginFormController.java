package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginFormController {

    public AnchorPane root;

    //TODO
    @SuppressWarnings("Duplicates")
    public void btnLogin_OnAction(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/MainForm2.fxml"));
        Scene mainScene = new Scene(root);
        Stage mainStage = (Stage)this.root.getScene().getWindow();
        mainStage.setScene(mainScene);
        mainStage.centerOnScreen();
    }
}
