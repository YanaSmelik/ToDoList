package com.proj.todolist.datamodels;

import java.time.LocalDate;

public class ToDoItem {
    private String summary;
    private String description;
    private LocalDate dueDate;

    public ToDoItem(String summary, String description, LocalDate dueDate) {
        this.summary = summary;
        this.description = description;
        this.dueDate = dueDate;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}
