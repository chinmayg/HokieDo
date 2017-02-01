package org.ghotkar.helper;

/**
 * Created by cghotkar on 1/31/17.
 */

public class TodoListItem {

    private int _id;
    private String todo_title;
    private String todo_description;

    public TodoListItem() {
        todo_title = "";
        todo_description = "";
    }

    public TodoListItem(String title, String description) {
        todo_title = title;
        todo_description = description;
    }

    public TodoListItem(int id, String title, String description) {
        _id = id;
        todo_title = title;
        todo_description = description;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getTodo_title() {
        return todo_title;
    }

    public void setTodo_title(String todo_title) {
        this.todo_title = todo_title;
    }

    public String getTodo_description() {
        return todo_description;
    }

    public void setTodo_description(String todo_description) {
        this.todo_description = todo_description;
    }
}
