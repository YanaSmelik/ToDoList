package com.proj.todolist;

import com.proj.todolist.datamodels.ToDoData;
import com.proj.todolist.datamodels.ToDoItem;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;


import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

public class Controller {


    @FXML
    private TextArea descriptionTextArea;
    @FXML
    private ListView<ToDoItem> toDoListView;
    @FXML
    private Label dueDateLabel;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private ContextMenu toDoItemContextMenu;
    @FXML
    private ToggleButton filterToggleButton;
    private FilteredList<ToDoItem> filteredList;
    private Predicate<ToDoItem> allToDoItems;
    private Predicate<ToDoItem> todayToDoItems;


    public void initialize() {
        toDoItemContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                ToDoItem item = toDoListView.getSelectionModel().getSelectedItem();
                deleteToDoItem(item);
            }
        });
        toDoItemContextMenu.getItems().addAll(deleteMenuItem);
        MenuItem editMenuItem = new MenuItem("Edit");
        editMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                ToDoItem item = toDoListView.getSelectionModel().getSelectedItem();
                editToDoItem(item);
            }
        });
        toDoItemContextMenu.getItems().addAll(editMenuItem);
        toDoListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ToDoItem>() {
            @Override
            public void changed(ObservableValue<? extends ToDoItem> observableValue, ToDoItem toDoItem, ToDoItem t1) {
                if (t1 != null) {
                    ToDoItem item = toDoListView.getSelectionModel().getSelectedItem();
                    descriptionTextArea.setText(item.getDescription());
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("d MMM, yy");
                    dueDateLabel.setText(df.format(item.getDueDate()));
                }
            }
        });
        allToDoItems = new Predicate<ToDoItem>() {
            @Override
            public boolean test(ToDoItem toDoItem) {
                return true;
            }
        };
        todayToDoItems = new Predicate<ToDoItem>() {
            @Override
            public boolean test(ToDoItem toDoItem) {
                return (toDoItem.getDueDate().equals(LocalDate.now()));
            }
        };
        filteredList = new FilteredList<>(ToDoData.getInstance().getToDoItems(), allToDoItems);

        SortedList<ToDoItem> sortedList = new SortedList<ToDoItem>(filteredList,
                new Comparator<ToDoItem>() {
            @Override
            public int compare(ToDoItem item1, ToDoItem item2) {
                return item1.getDueDate().compareTo(item2.getDueDate());
            }
        });
        toDoListView.setItems(sortedList);
        toDoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        toDoListView.getSelectionModel().selectFirst();
        toDoListView.setCellFactory(new Callback<ListView<ToDoItem>, ListCell<ToDoItem>>() {
            @Override
            public ListCell<ToDoItem> call(ListView<ToDoItem> toDoItemListView) {
                ListCell<ToDoItem> cell = new ListCell<>() {
                    @Override
                    protected void updateItem(ToDoItem toDoItem, boolean empty) {
                        super.updateItem(toDoItem, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(toDoItem.getSummary());
                            if (toDoItem.getDueDate().equals(LocalDate.now())) {
                                setTextFill(Color.CRIMSON);
                            } else if (toDoItem.getDueDate().equals(LocalDate.now().plusDays(1))) {
                                setTextFill(Color.GREEN);
                            } else if (toDoItem.getDueDate().isBefore(LocalDate.now())) {
                                setTextFill(Color.SLATEGREY);
                            }
                        }
                    }
                };
                cell.emptyProperty().addListener(
                        (obs, wasEmpty, isEmpty) -> {
                            if (isEmpty) {
                                cell.setContextMenu(null);
                            } else {
                                cell.setContextMenu(toDoItemContextMenu);
                            }
                        }
                );
                return cell;
            }
        });
    }

    public void showAddToDoItemDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Add To Do Item");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("addToDoItemDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            DialogController controller = fxmlLoader.getController();
            ToDoItem newToDoItem = controller.processResults();
            toDoListView.getSelectionModel().select(newToDoItem);
        }
    }

    public void handleKeyPressed(KeyEvent keyEvent){
        ToDoItem selectedItem = toDoListView.getSelectionModel().getSelectedItem();
        if(selectedItem != null){
            if (keyEvent.getCode().equals(KeyCode.DELETE)) {
                deleteToDoItem(selectedItem);
            }
        }
    }

    @FXML
    public void handleClickListView() {
        ToDoItem item = toDoListView.getSelectionModel().getSelectedItem();
        descriptionTextArea.setText(item.getDescription());
        dueDateLabel.setText(item.getDueDate().toString());
    }

    public void deleteToDoItem(ToDoItem item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete To Do Item");
        alert.setHeaderText("Delete To Do Item: " + item.getSummary() + "?");
        alert.setContentText("You won't be able to restore the To Do Item");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            ToDoData.getInstance().deleteToDoItemFromList(item);
        }
    }

    public void editToDoItem(ToDoItem item) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Edit To Do Item");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("addToDoItemDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.APPLY);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        DialogController controller = fxmlLoader.getController();
        controller.fillDialogWithContent(item);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.APPLY) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Edit To Do Item");
            alert.setHeaderText("Save changes to " + item.getSummary() + " To Do Item?");
            Optional<ButtonType> resultAlert = alert.showAndWait();
            if (resultAlert.isPresent() && resultAlert.get() == ButtonType.OK) {
                ToDoData.getInstance().deleteToDoItemFromList(item);
                ToDoItem newToDoItem = controller.processResults();
                toDoListView.getSelectionModel().select(newToDoItem);
            }
        }
    }

    @FXML
    public void handleFilterButton(){
        if(filterToggleButton.isSelected()){
            filteredList.setPredicate(todayToDoItems);
            if(filteredList.isEmpty()){
                descriptionTextArea.clear();
                dueDateLabel.setText("");
            }
        }else{
            filteredList.setPredicate(allToDoItems);
        }
    }

    @FXML
    public void handleExit(){
        Platform.exit();
    }
}
