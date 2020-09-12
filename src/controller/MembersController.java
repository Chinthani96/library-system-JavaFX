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

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MembersController {
    public Button btnAdd;
    public Button btnBack;
    public TextField txtMemberId;
    public TextField txtMemberName;
    public TextField txtAddress;
    public TableView<MembersTM> tblMembers;
    public Button btnDelete;
    public Button btnSave;
    public AnchorPane root;

    public void initialize(){
        //Basic Initializations
        btnAdd.requestFocus();
        btnSave.setDisable(true);
        btnDelete.setDisable(true);

        //Mapping columns
        tblMembers.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("memberId"));
        tblMembers.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("memberName"));
        tblMembers.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("address"));

        loadMembers();

        tblMembers.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MembersTM>() {
            @Override
            public void changed(ObservableValue<? extends MembersTM> observable, MembersTM oldValue, MembersTM selectedMember) {
                btnSave.setText("Update");
                btnDelete.setDisable(false);
                btnSave.setDisable(false);

                if(selectedMember==null){
                    return;
                }

                txtMemberId.setText(selectedMember.getMemberId());
                txtMemberName.setText(selectedMember.getMemberName());
                txtAddress.setText(selectedMember.getAddress());
            }
        });

    }
    public void btnAdd_OnAction(ActionEvent actionEvent) {
        btnSave.setDisable(false);
        btnAdd.setDisable(true);
        txtMemberName.requestFocus();

        ObservableList<MembersTM> members = tblMembers.getItems();


        if(members.size()==0){
            txtMemberId.setText("M001");
        }
        else{
            MembersTM lastMember = members.get(members.size() - 1);
            String lastId = lastMember.getMemberId();

            int val = Integer.parseInt(lastId.substring(1,4));
            val++;
            if(val<10){
                txtMemberId.setText("M00"+val);
            }
            else if(val<100){
                txtMemberId.setText("M0"+val);
            }
            else{
                txtMemberId.setText("M"+val);
            }
        }
    }

    @SuppressWarnings("Duplicates")
    public void btnBack_OnAction(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/MainForm2.fxml"));
        Scene mainscene = new Scene(root);
        Stage mainStage = (Stage)this.root.getScene().getWindow();
        mainStage.setScene(mainscene);
        mainStage.centerOnScreen();
//        mainStage.show();
    }

    public void btnDelete_OnAction(ActionEvent actionEvent) {

        String memberId = txtMemberId.getText();
        try {
            PreparedStatement pst = DBConnection.getInstance().getConnection().prepareStatement("Delete from member where memberId='" + memberId + "'");
            int i = pst.executeUpdate();

            if(i>0){
                System.out.println("Member deleted successfully");
                loadMembers();
            }
            else{
                new Alert(Alert.AlertType.ERROR,"Failed to delete from databases",ButtonType.OK).show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        btnSave.setText("Save");
        btnAdd.requestFocus();
        btnAdd.setDisable(false);
        btnSave.setDisable(true);
        btnDelete.setDisable(true);
        txtMemberId.clear();
        txtMemberName.clear();
        txtAddress.clear();
    }

    @SuppressWarnings("Duplicates")
    public void btnSave_OnAction(ActionEvent actionEvent) {
        String memberId = txtMemberId.getText();
        String name = txtMemberName.getText();
        String address = txtAddress.getText();

        if(btnSave.getText().equals("Update")){
            //
            try {
                btnSave.setText("Save");
                PreparedStatement pst = DBConnection.getInstance().getConnection().prepareStatement("update member SET name=?,address=? where memberId='"+memberId+"'");
                pst.setObject(1, name);
                pst.setObject(2, address);
                int affectedRows = pst.executeUpdate();

                if (affectedRows > 0) {
                    loadMembers();
                    System.out.println("Added to database successfully");
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to add member", ButtonType.OK).show();
                }


            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                PreparedStatement preparedStatement = DBConnection.getInstance().getConnection().prepareStatement("Insert into member VALUES (?,?,?)");
                preparedStatement.setObject(1,memberId);
                preparedStatement.setObject(2,name);
                preparedStatement.setObject(3,address);

                int i = preparedStatement.executeUpdate();

                if(i>0){
                    loadMembers();
                    System.out.println("Updated to database successfully");
                }
                else{
                    new Alert(Alert.AlertType.ERROR,"Failed to update member", ButtonType.OK).show();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        txtMemberId.clear();
        txtMemberName.clear();
        txtAddress.clear();
        btnSave.setDisable(true);
        btnDelete.setDisable(true);
        btnAdd.setDisable(false);
        btnAdd.requestFocus();

    }

    private void loadMembers(){
        try {
            Statement statement = DBConnection.getInstance().getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("Select * from member");

            ObservableList<MembersTM> members = tblMembers.getItems();
            members.clear();

            while(resultSet.next()){
                String memberId = resultSet.getString(1);
                String memberName = resultSet.getString(2);
                String address = resultSet.getString(3);

                members.add(new MembersTM(memberId,memberName,address));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
