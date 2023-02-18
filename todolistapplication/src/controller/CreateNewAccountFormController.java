package controller;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.Objects;

public class CreateNewAccountFormController {
    public TextField txtUsername;
    public TextField txtEmail;
    public PasswordField txtPassword;
    public PasswordField txtConfirmPassword;
    public Button btnRegister;
    public Label lblUserID;
    public Label lblPassword;
    public Label lblConfirmPassword;
    public AnchorPane root;

    public void initialize(){
        txtUsername.setDisable(true);
        txtEmail.setDisable(true);
        txtPassword.setDisable(true);
        txtConfirmPassword.setDisable(true);
        btnRegister.setDisable(true);
        lblPassword.setVisible(false);
        lblConfirmPassword.setVisible(false);
    }

    public void btnAddNewUserOnAction(ActionEvent actionEvent) {
        txtUsername.setDisable(false);
        txtEmail.setDisable(false);
        txtPassword.setDisable(false);
        txtConfirmPassword.setDisable(false);
        btnRegister.setDisable(false);

        txtUsername.requestFocus();

        autoGenerateID();
    }

    public void autoGenerateID(){
        Connection connection = DBConnection.getInstance().getConnection();

        try {
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("select id from user order by id desc limit 1");

            boolean isExist = resultSet.next();

            if(isExist){
                String oldID = resultSet.getString(1);
                int length = oldID.length();
                String id = oldID.substring(1,length);
                int intId = Integer.parseInt(id);
                intId += 1;

                if(intId < 10){
                    lblUserID.setText("U00" + intId);
                }
                else if (intId < 100){
                    lblUserID.setText("U0" + intId);
                }
                else{
                    lblUserID.setText("U" + intId);
                }

            }else {
                lblUserID.setText("U001");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void btnRegisterOnAction(ActionEvent actionEvent) {
        String newPassword = txtPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        boolean isequal = newPassword.equals(confirmPassword);

        if(isequal){
            txtPassword.setStyle("-fx-border-color: transparent");
            txtConfirmPassword.setStyle("-fx-border-color: transparent");
            lblPassword.setVisible(false);
            lblConfirmPassword.setVisible(false);
            register();
        }else{
            txtPassword.setStyle("-fx-border-color: red");
            txtConfirmPassword.setStyle("-fx-border-color: red");
            txtPassword.requestFocus();
            lblPassword.setVisible(true);
            lblConfirmPassword.setVisible(true);
        }
    }

    public void register(){
        String id = lblUserID.getText();
        String username = txtUsername.getText();
        String email = txtEmail.getText();
        String password = txtPassword.getText();

        Connection connection = DBConnection.getInstance().getConnection();

        /*
        try {
            Statement statement = connection.createStatement();
            int i = statement.executeUpdate("insert into user values ('"+id+"','"+username+"','"+password+"' , '"+email+"')");

        } catch (SQLException e) {
            e.printStackTrace();
        }

         */
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("insert into user values (?,?,?,?)");

            preparedStatement.setObject(1,id);
            preparedStatement.setObject(2,username);
            preparedStatement.setObject(3,password);
            preparedStatement.setObject(4,email);

            int i = preparedStatement.executeUpdate();

            if(i != 0){
                new Alert(Alert.AlertType.CONFIRMATION,"Success...").showAndWait();

                Parent parent = FXMLLoader.load(this.getClass().getResource("../view/LoginForm.fxml"));

                Scene scene = new Scene(parent);

                Stage primaryStage = (Stage) this.root.getScene().getWindow();

                primaryStage.setScene(scene);
                primaryStage.setTitle("Login Form");
                primaryStage.centerOnScreen();

            }
            else{
                new Alert(Alert.AlertType.ERROR,"Something went Wrong....").showAndWait();
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

    }

    public void btnLoginPageOnAction(ActionEvent actionEvent) throws IOException {
        Parent parent = FXMLLoader.load(Objects.requireNonNull(this.getClass().getResource("../view/LoginForm.fxml")));
        Scene scene = new Scene(parent);

        Stage primaryStage = (Stage)this.root.getScene().getWindow();

        primaryStage.setScene(scene);
        primaryStage.setTitle("To Do Form");
        primaryStage.centerOnScreen();
    }
}
