package io.redbee.academy.microservicios.todolist.examples;

import io.redbee.academy.microservicios.todolist.controller.TodoListWebController;
import io.redbee.academy.microservicios.todolist.model.TodoItem;
import io.redbee.academy.microservicios.todolist.model.TodoList;
import io.redbee.academy.microservicios.todolist.service.TodoListService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class WebControllerWithMockMvcStandaloneExample {

    TodoListService mockService = Mockito.mock(TodoListService.class);

    TodoListWebController controller = new TodoListWebController(mockService);

    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();


    @BeforeEach
    void setupMocks() {
        Mockito.when(mockService.nuevaTodoList()).thenReturn(new TodoList());
        Mockito.when(mockService.agregarItem(anyString(), anyString())).thenAnswer(
                (Answer<TodoList>) invocation -> {
                    String listId = invocation.getArgument(0, String.class);
                    String descripcion = invocation.getArgument(1, String.class);
                    List<TodoItem> items = new ArrayList<>();
                    items.add(new TodoItem(descripcion));
                    return new TodoList(listId, items);
                }
        );

    }

    @Test
    void nuevaTodoListYAgregarItem() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("todo-list"))
                .andExpect(model().attribute("todoList", hasProperty("id", isA(String.class))))
                .andExpect(model().attribute("todoList", hasProperty("items", emptyIterable())))
                .andReturn();

        TodoList nuevaTodoList = (TodoList) mvcResult.getModelAndView().getModelMap().getAttribute("todoList");


        mockMvc.perform(post("/agregarItem")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("listId", nuevaTodoList.getId())
                        .param("detalle", "mi item"))
                .andExpect(status().isOk())
                .andExpect(view().name("todo-list"))
                .andExpect(model().attribute("todoList", hasProperty("id", is(nuevaTodoList.getId()))))
                .andExpect(model().attribute("todoList", hasProperty("items", iterableWithSize(1))))
                .andExpect(model().attribute("todoList", hasProperty("items", hasItems(hasProperty("descripcion", is("mi item"))))));

    }


}