package com.example.chads.to_dolist;

/**
 * Created by chads on 2018-03-01.
 */

public class Task {
    private int id;
    private String title;
    private String description;
    private String date;
    private int priority;
    private Boolean completed;

    public Task(int id, String title, String description, String date, int priority, Boolean completed){
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.priority = priority;
        this.completed = completed;
    }

    public Task(String title, String description, String date, int priority){
        this.id = -1;
        this.title = title;
        this.description = description;
        this.date = date;
        this.priority = priority;
        this.completed = false;
    }

    public Task(){}

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", priority='" + priority + '\'' +
                ", completed=" + completed +
                '}';
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Boolean isCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

}
