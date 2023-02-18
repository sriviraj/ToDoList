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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import tm.ToDoTM;

import java.io.IOException;
import java.sql.*;
import java.util.Locale;
import java.util.Optional;

public class ToDoFormController {
    public Label lblID;
    public Label lblwelcomeNote;
    public Button btnDelete;
    public Button btnUpdate;
    public Button btnAddNewToDo;
    public Button btnLogOut;
    public Pane subroot;
    public Button btnAddToList;
    public TextField txtNewToDo;
    public AnchorPane root;
    public ListView<ToDoTM> lstTodoList;
    public TextField txtSelectedText;

    public String id;
    public Label lblCheckText;

    public void initialize(){
        lblID.setText(LoginFormController.enteredUserID);
        lblwelcomeNote.setText("Hi " + LoginFormController.enteredUsername + " welcome to To Do List");

        subroot.setVisible(false);

        lblCheckText.setVisible(false);

        loadList();

        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
        txtSelectedText.setDisable(true);

        lstTodoList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ToDoTM>() {
            @Override
            public void changed(ObservableValue<? extends ToDoTM> observable, ToDoTM oldValue, ToDoTM newValue) {
                btnDelete.setDisable(false);
                btnUpdate.setDisable(false);
                txtSelectedText.setDisable(false);

                txtSelectedText.requestFocus();

                subroot.setVisible(false);

                ToDoTM selectedItem = lstTodoList.getSelectionModel().getSelectedItem();

                if(newValue == null){
                    return;
                }
                String description = selectedItem.getDescription();
                String item_id = selectedItem.getId();

                txtSelectedText.setText(description);

                id = newValue.getId();
            }
        });
    }

    public void btnAddNewToDoOnAction(ActionEvent actionEvent) {
        subroot.setVisible(true);
        txtNewToDo.requestFocus();

        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
        txtSelectedText.setDisable(true);
    }

    public void btnLogOutOnAction(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do You want to Log Out ?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();

        if(buttonType.get().equals(ButtonType.YES)){
            Parent parent = FXMLLoader.load(getClass().getResource("../view/LoginForm.fxml"));

            Scene scene = new Scene(parent);
            Stage primaryStage = (Stage) this.root.getScene().getWindow();

            primaryStage.setScene(scene);
            primaryStage.setTitle("Login Form");
            primaryStage.centerOnScreen();

        }

    }

    public void btnAddToListOnAction(ActionEvent actionEvent){

        if(txtNewToDo.getText().trim().isEmpty()){
            lblCheckText.setVisible(true);

            txtNewToDo.requestFocus();
        }
        else{
            lblCheckText.setVisible(false);

            String id = autoGenerateID();
            String description = txtNewToDo.getText();
            String user_id = lblID.getText();

//        System.out.println(id);
//        System.out.println(description);
//        System.out.println(user_id);

            Connection connection = DBConnection.getInstance().getConnection();


            try {
                PreparedStatement preparedStatement = connection.prepareStatement("insert into todos values (?,?,?)");

                preparedStatement.setObject(1,id);
                preparedStatement.setObject(2,description);
                preparedStatement.setObject(3,user_id);

                int i = preparedStatement.executeUpdate();

                //System.out.println(i);
                subroot.setVisible(false);
            }

            catch (SQLException e){
                e.printStackTrace();
            }

            loadList();
        }
    }

    private void loadList() {
        ObservableList<ToDoTM> todos = lstTodoList.getItems();

        todos.clear();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from todos where user_id = ?");

            preparedStatement.setObject(1,LoginFormController.enteredUserID);

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){

                String id = resultSet.getString(1);
                String description = resultSet.getString(2);
                String user_id = resultSet.getString(3);

                ToDoTM toDoTM = new ToDoTM(id,description,user_id);
                todos.add(toDoTM);
            }
            lstTodoList.refresh();

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String autoGenerateID() {
        Connection connection = DBConnection.getInstance().getConnection();

        String newID = "";
        try {
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("select id from todos order by id desc limit 1");

            boolean isExist = resultSet.next();

            if(isExist){
                String oldID = resultSet.getString(1);
                int length = oldID.length();
                oldID = oldID.substring(1,length);
                int intId = Integer.parseInt(oldID);
                intId += 1;

                if(intId < 10){
                    newID =  "T00" + intId;
                }
                else if (intId < 100){
                    newID = "T0" + intId;
                }
                else{
                    newID = "T" + intId;
                }

            }else {
                newID = "T001";
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return newID;
    }

    public void btnUpdateOnAction(ActionEvent actionEvent) {
        String description = txtSelectedText.getText();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("update todos set description = ? where id = ?");

            preparedStatement.setObject(1,description);
            preparedStatement.setObject(2,id);

            System.out.println(description);
            System.out.println(id);

            preparedStatement.executeUpdate();

            loadList();
            txtSelectedText.clear();
            txtSelectedText.setDisable(true);
            btnUpdate.setDisable(true);
            btnDelete.setDisable(true);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Delete ??",ButtonType.YES,ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();

        Connection connection = DBConnection.getInstance().getConnection();

        if(buttonType.get().equals(ButtonType.YES)){
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("delete from todos where id = ?");

                preparedStatement.setObject(1,id);

                preparedStatement.executeUpdate();

                loadList();

                txtSelectedText.clear();
                btnDelete.setDisable(true);
                btnUpdate.setDisable(true);
                txtSelectedText.setDisable(true);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
