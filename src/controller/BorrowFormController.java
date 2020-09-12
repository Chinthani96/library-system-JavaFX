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
import util.BorrowDetails;
import util.BorrowTM;
import util.MembersTM;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

public class BorrowFormController {

    public TextField txtBorrowId;
    public TextField txtDate;
    public ComboBox<MembersTM> cmbMemberIds;
    public TextField txtName;
    public TextField txtTitle;
    public ComboBox<BooksTM> cmbISBNs;
    public TableView<BorrowTM> tblBorrowDetails;
    public Button btnAdd;
    public Button btnSave;
    public Button btnBack;
    public AnchorPane root;
    public TextField txtAuthor;
    public static ArrayList<BorrowDetails> borrowDetails = new ArrayList();
    public static ArrayList<BooksTM> tempBooks = new ArrayList<>();
//    public String id;

    public void initialize(){
        //basic initializations
        btnSave.setDisable(true);

        //mapping columns
        tblBorrowDetails.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("borrowId"));
        tblBorrowDetails.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("isbn"));
        tblBorrowDetails.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("title"));
        tblBorrowDetails.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("author"));

        loadAllMembers();
        loadAllBooks();
        loadBorrowDetails();

        LocalDate today = LocalDate.now();
        txtDate.setText(String.valueOf(today));

        cmbMemberIds.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MembersTM>() {
            @Override
            public void changed(ObservableValue<? extends MembersTM> observable, MembersTM oldValue, MembersTM selectedMember) {
                if(selectedMember==null){
                    return;
                }
                generateBorrowId();
                txtName.setText(selectedMember.getMemberName());
            }
        });

        cmbISBNs.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<BooksTM>() {
            @Override
            public void changed(ObservableValue<? extends BooksTM> observable, BooksTM oldValue, BooksTM selectedBook) {
                if(selectedBook==null){
                    return;
                }
                txtTitle.setText(selectedBook.getTitle());
                txtAuthor.setText(selectedBook.getAuthor());
            }
        });

        tblBorrowDetails.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<BorrowTM>() {
            @Override
            public void changed(ObservableValue<? extends BorrowTM> observable, BorrowTM oldValue, BorrowTM selectedDetail) {
                if(selectedDetail==null){
                    return;
                }
                btnAdd.setText("Update");
            }
        });
    }

    public void btnAdd_OnAction(ActionEvent actionEvent) {
        incrementId();
//        loadAllBooks();
        btnSave.setDisable(false);
        ObservableList<BorrowTM> borrowDetails = tblBorrowDetails.getItems();
        BooksTM selectedBook = cmbISBNs.getSelectionModel().getSelectedItem();
        String borrowId = txtBorrowId.getText();
        String isbn = selectedBook.getISBN();
        String title = selectedBook.getTitle();
        String author  = selectedBook.getAuthor();

        if(btnSave.getText().equals("Update")){
            int selectedIndex = tblBorrowDetails.getSelectionModel().getSelectedIndex();
            borrowDetails.set(selectedIndex,new BorrowTM(borrowId,isbn,title,author));
        }
        else {
            borrowDetails.add(new BorrowTM(borrowId,isbn, title, author));
        }
    }

    @SuppressWarnings("Duplicates")
    public void btnSave_OnAction(ActionEvent actionEvent) {
        ObservableList<BorrowTM> BorrowDetails = tblBorrowDetails.getItems();
        ObservableList<BooksTM> books = cmbISBNs.getItems();


        for(BorrowTM borrowDetail:BorrowDetails){
            String borrowId = borrowDetail.getBorrowId();
            String isbn = borrowDetail.getIsbn();
            String memberId = cmbMemberIds.getSelectionModel().getSelectedItem().getMemberId();
            String date = txtDate.getText();


            try {
                PreparedStatement pst = DBConnection.getInstance().getConnection().prepareStatement("INSERT INTO Borrow VALUES (?,?,?,?)");
                pst.setObject(1,borrowId);
                pst.setObject(2,memberId);
                pst.setObject(3,isbn);
                pst.setObject(4,date);
                int affectedRows = pst.executeUpdate();

                if(affectedRows>0){
                    new Alert(Alert.AlertType.INFORMATION,"Entry added Successfully.",ButtonType.OK).show();
                }
                else{
                    new Alert(Alert.AlertType.ERROR,"Error adding entry to the database",ButtonType.OK).show();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    private void loadAllMembers(){
        try {
            Statement stm = DBConnection.getInstance().getConnection().createStatement();
            String sql = "SELECT * FROM Member";
            ResultSet rst = stm.executeQuery(sql);

            ObservableList<MembersTM> members = cmbMemberIds.getItems();
            members.clear();

            while(rst.next()){
                String memberId = rst.getString(1);
                String memberName = rst.getString(2);
                String address = rst.getString(3);
                members.add(new MembersTM(memberId, memberName, address));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @SuppressWarnings("Duplicates")
    private void loadAllBooks(){
        try {
            Statement stm = DBConnection.getInstance().getConnection().createStatement();
            ResultSet rst = stm.executeQuery("SELECT * FROM Book");

            ObservableList<BooksTM> books = cmbISBNs.getItems();
            books.clear();

            while (rst.next()){
                String isbn = rst.getString(1);
                String title = rst.getString(2);
                String author = rst.getString(3);
                String edition = rst.getString(4);
                books.add(new BooksTM(isbn,title,author,edition));
                tempBooks.add(new BooksTM(isbn,title,author,edition));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("Duplicates")
    private void loadBorrowDetails(){
        try {
            Statement stm = DBConnection.getInstance().getConnection().createStatement();
            ResultSet rst = stm.executeQuery("SELECT * FROM Borrow");
            borrowDetails.clear();;

            while(rst.next()){
                String borrowId = rst.getString(1);
                String memberId = rst.getString(2);
                String isbn = rst.getString(3);
                String date = rst.getString(4);
                borrowDetails.add(new BorrowDetails(borrowId,memberId,isbn,date));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void generateBorrowId(){
        if(borrowDetails.size()==0){
            txtBorrowId.setText("B001");
        }
        else{
            BorrowDetails lastBorrowDetails = BorrowFormController.borrowDetails.get(BorrowFormController.borrowDetails.size() - 1);
            String lastId = lastBorrowDetails.getBorrowId();
            int val = Integer.parseInt(lastId.substring(1,4));

//            val++;
            if(val<10){
                //id="B00"+val;
                txtBorrowId.setText("B00"+val);
            }
            else if(val<100){
//                id="B0"+val;
                txtBorrowId.setText("B0"+val);
            }
            else{
//                id="B"+val;
                txtBorrowId.setText("B"+val);
            }
        }
    }
    private void incrementId(){
        String borrowId = txtBorrowId.getText();
        int val = Integer.parseInt(borrowId.substring(1,4));

        System.out.println(val);
        val++;
        if(val<10){
            System.out.println();
            txtBorrowId.setText("B00"+val);
        }
        else if(val<100){
            txtBorrowId.setText("B0"+val);
        }
        else{
            txtBorrowId.setText("B"+val);
        }
    }

    @SuppressWarnings("Duplicates")
    public void btnBack_OnAction(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/MainForm2.fxml"));
        Scene mainScene = new Scene(root);
        Stage mainStage = (Stage)this.root.getScene().getWindow();
        mainStage.setScene(mainScene);
        mainStage.centerOnScreen();
    }
}


//    ObservableList<BorrowTM> details = tblBorrowDetails.getItems();
//
//        if(borrowDetails.size()==0){
//                if(details.size()==0) {
//                txtBorrowId.setText("B001");
//                }
//                else{
//                BorrowDetails lastDetail = borrowDetails.get(borrowDetails.size() - 1);
//                String lastId = lastDetail.getBorrowId();
//                System.out.println(lastId);
//                int val = Integer.parseInt(lastId.substring(1,4));
//                val++;
//                if(val<10){
//        txtBorrowId.setText("B00"+val);
//        }
//        else if(val<100){
//        txtBorrowId.setText("B0"+val);
//        }
//        else{
//        txtBorrowId.setText("B"+val);
//        }
//        }
//        }
//        else{
//        BorrowDetails lastDetail = borrowDetails.get(borrowDetails.size() - 1);
//        String lastId = lastDetail.getBorrowId();
//        String temp ="";
//        System.out.println(lastId);
//        int val = Integer.parseInt(lastId.substring(1,4));
//        val++;
//        if(val<10){
//        txtBorrowId.setText("B00"+val);
//        }
//        else if(val<100){
//        txtBorrowId.setText("B0"+val);
//        }
//        else{
//        txtBorrowId.setText("B"+val);
//        }
//        }

