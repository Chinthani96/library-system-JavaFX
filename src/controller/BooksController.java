package controller;

import db.DBConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import util.BooksTM;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BooksController {
    public Button btnBack;
    public TextField txtName;
    public TextField txtISBN;
    public TextField txtAuthor;
    public TableView<BooksTM> tblBooks;
    public Button btnSave;
    public Button btnDelete;
    public AnchorPane root;
    public TextField txtEdition;

    public void initialize(){
        //basic initializations
        btnDelete.setDisable(true);

        //mapping columns
        tblBooks.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("ISBN"));
        tblBooks.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("title"));
        tblBooks.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("author"));
        tblBooks.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("edition"));

        //loading the books from the database
        loadBooks();

        //functionalities when a book from the combo box is selected.
        tblBooks.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<BooksTM>() {
            @Override
            public void changed(ObservableValue<? extends BooksTM> observable, BooksTM oldValue, BooksTM selectedBook) {
                if(selectedBook==null){
                    return;
                }

                btnSave.setText("Update");
                btnSave.setDisable(false);
                btnDelete.setDisable(false);

                txtName.setText(selectedBook.getTitle());
                txtISBN.setText(selectedBook.getISBN());
                txtAuthor.setText(selectedBook.getAuthor());
                txtEdition.setText(selectedBook.getEdition());
            }
        });
    }

    @SuppressWarnings("Duplicates")
    public void btnBack_OnAction(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/MainForm2.fxml"));
        Scene mainscene = new Scene(root);
        Stage mainStage = (Stage)this.root.getScene().getWindow();
        mainStage.setScene(mainscene);
        mainStage.centerOnScreen();
        mainStage.show();
    }

    @SuppressWarnings("Duplicates")
    public void btnSave_OnAction(ActionEvent actionEvent) {
        System.out.println(actionEvent);
        String ISBN = txtISBN.getText();
        String name = txtName.getText();
        String author = txtAuthor.getText();
        String edition = txtEdition.getText();

        if(btnSave.getText().equals("Update")){
            btnSave.setText("Save");
            BooksTM selectedBook = tblBooks.getSelectionModel().getSelectedItem();
            try {
                PreparedStatement pst = DBConnection.getInstance().getConnection().prepareStatement("UPDATE Book SET isbn=?,title=?,author=?,edition=? WHERE isbn='" + selectedBook.getISBN() + "'");
                pst.setObject(1,ISBN);
                pst.setObject(2,name);
                pst.setObject(3,author);
                pst.setObject(4,edition);
                int affectedRows = pst.executeUpdate();
                loadBooks();
                if(affectedRows>0){
                    new Alert(Alert.AlertType.INFORMATION,"Book updated successfully",ButtonType.OK).show();
                }
                else{
                    new Alert(Alert.AlertType.ERROR,"Update failed!",ButtonType.OK).show();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        else{
            try {
                PreparedStatement pst = DBConnection.getInstance().getConnection().prepareStatement("INSERT INTO Book VALUES (?,?,?,?)");
                pst.setObject(1,ISBN);
                pst.setObject(2,name);
                pst.setObject(3,author);
                pst.setObject(4,edition);
                int affectedRows = pst.executeUpdate();
                loadBooks();

                if(affectedRows>0){
                    new Alert(Alert.AlertType.INFORMATION,"Book added successfully",ButtonType.OK).show();
                }
                else{
                    new Alert(Alert.AlertType.ERROR,"Failed to add book",ButtonType.OK).show();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        btnDelete.setDisable(true);

        txtEdition.clear();
        txtISBN.clear();
        txtName.clear();
        txtAuthor.clear();
    }

    @SuppressWarnings("Duplicates")
    public void btnDelete_OnAction(ActionEvent actionEvent) {
        BooksTM selectedBook = tblBooks.getSelectionModel().getSelectedItem();


        try {
            PreparedStatement pst = DBConnection.getInstance().getConnection().prepareStatement("DELETE FROM Book WHERE isbn='" + selectedBook.getISBN() + "'");
            int affectedRows = pst.executeUpdate();
            loadBooks();

            if (affectedRows > 0) {
                new Alert(Alert.AlertType.INFORMATION,"Book Deleted Successfully!",ButtonType.OK).show();
            }
            else{
                new Alert(Alert.AlertType.ERROR,"Failed to delete!",ButtonType.OK).show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        btnSave.setText("Save");
        btnSave.setDisable(true);
        btnDelete.setDisable(true);

        txtEdition.clear();
        txtISBN.clear();
        txtName.clear();
        txtAuthor.clear();

    }

    private void loadBooks(){
        try {
            Statement stm = DBConnection.getInstance().getConnection().createStatement();
            ResultSet rst = stm.executeQuery("Select * from Book");

            ObservableList<BooksTM> books= tblBooks.getItems();
            books.clear();

            while(rst.next()){
                String ISBN = rst.getString(1);
                String title = rst.getString(2);
                String author = rst.getString(3);
                String edition  = rst.getString(4);

                books.add(new BooksTM(ISBN,title,author,edition));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
