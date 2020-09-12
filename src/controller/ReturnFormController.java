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
import util.MembersTM;
import util.ReturnTM;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class ReturnFormController {
    public AnchorPane root;
    public ComboBox<MembersTM> cmbMembers;
    public TextField txtMemberName;
    public TextField txtReturnDate;
    public TableView<ReturnTM> tblReturn;
    public Button btnReturn;
    public Button btnBack;
    public Button btnSelectAll;
    public ArrayList<ReturnTM> returningBooks = new ArrayList<>();
    public ArrayList<String> memberIds = new ArrayList<>();
    public LocalDate today;
    public TextField txtFee;

    @SuppressWarnings("Duplicates")
    public void initialize(){
        //basic initializations
        today = LocalDate.now();
        txtReturnDate.setText(String.valueOf(today));
        loadMembers();
        loadReturnTable();

        //map columns
        tblReturn.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("borrowId"));
        tblReturn.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("isbn"));
        tblReturn.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("title"));

        //when the combo box is selected
        cmbMembers.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MembersTM>() {
            @Override
            public void changed(ObservableValue<? extends MembersTM> observable, MembersTM oldValue, MembersTM selectedMember) {
                txtMemberName.setText(selectedMember.getMemberName());
                int i;

                ObservableList<ReturnTM> tableItems = tblReturn.getItems();
                tableItems.clear();

                for(ReturnTM book:returningBooks){

                    i = returningBooks.indexOf(book);

                    if(memberIds.get(i).equals(selectedMember.getMemberId())){
                        tableItems.add(book);
                        System.out.println("loaded to the table");
                    }
                }
            }
        });
    }
    public void btnReturn_OnAction(ActionEvent actionEvent) {
        ObservableList<ReturnTM> returnBooks = tblReturn.getItems();
        ReturnTM selectedBook = tblReturn.getSelectionModel().getSelectedItem();

        String borrowId = selectedBook.getBorrowId();
        String date = txtReturnDate.getText();

        try {
            PreparedStatement pst = DBConnection.getInstance().getConnection().prepareStatement("INSERT INTO `Return` VALUES(?,?)");
            pst.setObject(1,borrowId);
            pst.setObject(2,date);
            int affectedRows = pst.executeUpdate();

            if(affectedRows>0){
                new Alert(Alert.AlertType.INFORMATION,"Book has been returned successfully",ButtonType.OK).show();
            }
            else{
                new Alert(Alert.AlertType.ERROR,"Return Unsuccessful",ButtonType.OK);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        calculateFee();
        returnBooks.clear();
    }
    @SuppressWarnings("Duplicates")
    public void btnBack_OnAction(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/MainForm2.fxml"));
        Scene mainScene = new Scene(root);
        Stage mainStage = (Stage)this.root.getScene().getWindow();
        mainStage.setScene(mainScene);
        mainStage.centerOnScreen();
    }
    public void btnSelectAll_OnAction(ActionEvent actionEvent) {
        tblReturn.getSelectionModel().selectAll();
        System.out.println("All is selected");
    }
    @SuppressWarnings("Duplicates")
    public void loadMembers(){
        ObservableList<MembersTM> members = cmbMembers.getItems();
        members.clear();
        try {
            Statement stm = DBConnection.getInstance().getConnection().createStatement();
            ResultSet rst = stm.executeQuery("SELECT id,name,phone_num FROM Member\n" +
                    "INNER JOIN Borrow B on Member.id = B.m_id\n" +
                    "LEFT OUTER JOIN `Return` R on B.borrow_id = R.borrow_id GROUP BY id");

            /*SELECT id,name,phone_num FROM Member\n" +
            "INNER JOIN Borrow B on Member.id = B.m_id\n" +
                    "LEFT OUTER JOIN `Return` R on B.borrow_id = R.borrow_id GROUP BY id*/

            while(rst.next()){
                String m_id = rst.getString(1);
                String name = rst.getString(2);
                String phone_num = rst.getString(3);

                members.add(new MembersTM(m_id, name, phone_num));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @SuppressWarnings("Duplicates")
    public void loadReturnTable(){
        try {
            Statement stm = DBConnection.getInstance().getConnection().createStatement();
            ResultSet rst = stm.executeQuery("SELECT * FROM Book\n" +
                    "INNER JOIN Borrow B on Book.isbn = B.isbn\n" +
                    "LEFT OUTER JOIN `Return` R on B.borrow_id = R.borrow_id");

            while(rst.next()){
                String isbn = rst.getString(1);
                String title = rst.getString(2);
                String borrowId = rst.getString("borrow_id");

                String memberId = rst.getString("m_id");

                memberIds.add(memberId);
                returningBooks.add(new ReturnTM(borrowId,isbn,title));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void calculateFee(){
        ReturnTM selectedBook = tblReturn.getSelectionModel().getSelectedItem();
        String borrowId = selectedBook.getBorrowId();
        String borrowDate="";

        try {
            Statement stm = DBConnection.getInstance().getConnection().createStatement();
            ResultSet rst = stm.executeQuery("SELECT B.date FROM Borrow B WHERE B.borrow_id='"+borrowId+"'");

            while(rst.next()){
                borrowDate= rst.getString(1);
            }
            String returnDate = txtReturnDate.getText();

            Date date1 = new SimpleDateFormat("dd-mm-yyyy").parse(borrowDate);
            Date date2 = new SimpleDateFormat("dd-mm-yyyy").parse(returnDate);

            int days = (int) (date2.getDate() - date1.getTime());

            if(days>9){
                double fee=0;
                int difference = days-14;
                fee = difference*10;
                txtFee.setText(String.valueOf(fee));
            }
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
    }
}
