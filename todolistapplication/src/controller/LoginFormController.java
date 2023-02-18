package controller;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFormController {
    public AnchorPane root;
    public TextField txtUsername;
    public TextField txtPassword;

    public static String enteredUsername;
    public static String enteredUserID;

    public void lblCreateNewAccountOnAction(MouseEvent mouseEvent) throws IOException {
        Parent parent = FXMLLoader.load(this.getClass().getResource("../view/CreateNewAccountForm.fxml"));
        Scene scene = new Scene(parent);

        Stage primaryStage = (Stage)this.root.getScene().getWindow();

        primaryStage.setScene(scene);
        primaryStage.setTitle("Create new Account");
        primaryStage.centerOnScreen();
    }

    public void btnLoginOnAction(ActionEvent actionEvent) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from user where username = ? and password = ?;");

            preparedStatement.setObject(1 , username);
            preparedStatement.setObject(2,password);

            ResultSet resultSet = preparedStatement.executeQuery();

            boolean isExist = resultSet.next();

            if(isExist){

                enteredUserID = resultSet.getString(1);
                enteredUsername = resultSet.getString(2);

                Parent parent = FXMLLoader.load(this.getClass().getResource("../view/ToDoForm.fxml"));
                Scene scene = new Scene(parent);

                Stage primaryStage = (Stage)this.root.getScene().getWindow();

                primaryStage.setScene(scene);
                primaryStage.setTitle("To Do Form");
                primaryStage.centerOnScreen();
            }
            else{
                new Alert(Alert.AlertType.ERROR,"Invalid User Name or Password").showAndWait();
                txtUsername.clear();
                txtPassword.clear();

                txtUsername.requestFocus();
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
