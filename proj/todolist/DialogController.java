package com.proj.todolist;

import com.proj.todolist.datamodels.ToDoData;
import com.proj.todolist.datamodels.ToDoItem;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;


public class DialogController {
    @FXML
    private TextField summaryField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private DatePicker dueDatePicker;

    public ToDoItem processResults(){
        String summary = summaryField.getText().trim();
        String description = descriptionArea.getText().trim();
        LocalDate dueDate = dueDatePicker.getValue();
        ToDoItem item = new ToDoItem(summary, description, dueDate);
        ToDoData.getInstance().addToDoItem(item);
        return item;
    }

    public void fillDialogWithContent(ToDoItem item){
        summaryField.setText(item.getSummary());
        descriptionArea.setText(item.getDescription());
        dueDatePicker.setValue(item.getDueDate());
    }
}
