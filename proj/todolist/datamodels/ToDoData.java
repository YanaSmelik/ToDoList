package com.proj.todolist.datamodels;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

public class ToDoData {

    private static ToDoData instance = new ToDoData();
    private static String fileName = "ToDoListItems.txt";
    private ObservableList<ToDoItem> toDoItems;
    private DateTimeFormatter formatter;

    private ToDoData(){
        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    }

    public static ToDoData getInstance(){
        return instance;
    }

    public ObservableList<ToDoItem> getToDoItems() {
        return toDoItems;
    }

    public void addToDoItem(ToDoItem toDoItem){
        toDoItems.add(toDoItem);
    }

    public void loadToDoItems() throws IOException {
        toDoItems = FXCollections.observableArrayList();
        Path path = Paths.get(fileName);
        BufferedReader br = Files.newBufferedReader(path);
        String input;
        try{
            while((input = br.readLine()) != null){
                String[] itemPieces = input.split("\t");
                String summary = itemPieces[0];
                String description = itemPieces[1];
                String dueDateString = itemPieces[2];
                LocalDate dueDate = LocalDate.parse(dueDateString, formatter);
                ToDoItem toDoItem = new ToDoItem(summary, description, dueDate);
                toDoItems.add(toDoItem);
            }
        }finally {
            if(br != null){
                br.close();
            }
        }
    }

    public void storeToDoItems() throws IOException{
        Path path = Paths.get(fileName);
        BufferedWriter bw = Files.newBufferedWriter(path);
        try{
            Iterator<ToDoItem> iterator = toDoItems.iterator();
            while(iterator.hasNext()){
                ToDoItem item = iterator.next();
                bw.write(String.format("%s\t%s\t%s", item.getSummary(), item.getDescription(), item.getDueDate().format(formatter)));
                bw.newLine();
            }
        }finally {
            if(bw !=null){
                bw.close();
            }
        }
    }

    public void deleteToDoItemFromList(ToDoItem item){
        toDoItems.remove(item);
    }
}
