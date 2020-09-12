package controller;

import com.sun.deploy.util.FXLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class MainFormController {
    public AnchorPane root;

    @SuppressWarnings("Duplicates")
    public void btnMembers_OnAction(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/Members.fxml"));
        Scene memberScene = new Scene(root);
        Stage mainStage = (Stage)this.root.getScene().getWindow();
        mainStage.setScene(memberScene);
        mainStage.centerOnScreen();
    }

    public void btnBooks_OnAction(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/Books.fxml"));
        Scene bookScene = new Scene(root);
        Stage mainStage = (Stage)this.root.getScene().getWindow();
        mainStage.setScene(bookScene);
        mainStage.centerOnScreen();
    }

    public void btnLending_OnAction(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/BorrowForm.fxml"));
        Scene borrowScene = new Scene(root);
        Stage mainStage = (Stage)this.root.getScene().getWindow();
        mainStage.setScene(borrowScene);
        mainStage.centerOnScreen();
    }

    @SuppressWarnings("Duplicates")
    public void btnReturn_OnAction(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/ReturnForm.fxml"));
        Scene returnScene  = new Scene(root);
        Stage mainStage = (Stage)this.root.getScene().getWindow();
        mainStage.setScene(returnScene);
        mainStage.centerOnScreen();
    }
}
