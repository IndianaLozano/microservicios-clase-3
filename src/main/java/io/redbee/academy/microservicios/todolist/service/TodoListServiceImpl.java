package io.redbee.academy.microservicios.todolist.service;

import io.redbee.academy.microservicios.todolist.model.TodoItem;
import io.redbee.academy.microservicios.todolist.model.TodoList;
import io.redbee.academy.microservicios.todolist.repository.TodoListRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TodoListServiceImpl implements TodoListService {

    private final TodoListRepository repository;

    public TodoListServiceImpl(TodoListRepository repository) {
        this.repository = repository;
    }

    @Override
    public TodoList nuevaTodoList() {
        TodoList nuevaTodoList = new TodoList();
        this.repository.saveTodoList(nuevaTodoList);
        return nuevaTodoList;
    }


    @Override
    public TodoList buscarLista(String listId) {
        return this.repository.getTodoList(listId);
    }

    @Override
    public TodoList agregarItem(String listId, String descripcion) {
        TodoList todoList = this.repository.getTodoList(listId);
        TodoItem todoItem = new TodoItem(descripcion);
        todoList.addItem(todoItem);
        this.repository.saveTodoList(todoList);
        return todoList;
    }

    @Override
    public TodoList cambiarEstadoItem(String listId, String itemId) {
        TodoList todoList = this.repository.getTodoList(listId);
        List<TodoItem> todoItems = todoList.getItems().stream()
                .map(item -> {
                    if (item.getId().equals(itemId))
                        item.cambiarFinalizada();
                    return item;
                })
                .collect(Collectors.toList());

        todoList = new TodoList(todoList.getId(), todoItems);
        this.repository.saveTodoList(todoList);

        return todoList;
    }

    @Override
    public TodoList eliminarItem(String listId, String itemId) {
        TodoList todoList = this.repository.getTodoList(listId);
        List<TodoItem> todoItems = todoList.getItems().stream()
                .filter(item -> !item.getId().equals(itemId))
                .collect(Collectors.toList());

        todoList = new TodoList(todoList.getId(), todoItems);
        this.repository.saveTodoList(todoList);

        return todoList;
    }
}
