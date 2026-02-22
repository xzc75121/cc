package com.cc.todo.controller;

import com.cc.todo.entity.Todo;
import com.cc.todo.service.TodoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/todos")
@CrossOrigin(origins = "*")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> listAll() {
        List<Todo> todos = todoService.listAll();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", todos);
        response.put("total", todos.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        Todo todo = todoService.getById(id);
        Map<String, Object> response = new HashMap<>();
        if (todo != null) {
            response.put("success", true);
            response.put("data", todo);
        } else {
            response.put("success", false);
            response.put("message", "Todo not found");
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/completed/{completed}")
    public ResponseEntity<Map<String, Object>> getByCompleted(@PathVariable Boolean completed) {
        List<Todo> todos = todoService.getByCompleted(completed);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", todos);
        response.put("total", todos.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> search(@RequestParam String keyword) {
        List<Todo> todos = todoService.search(keyword);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", todos);
        response.put("total", todos.size());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Todo todo) {
        Todo created = todoService.create(todo);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", created);
        response.put("message", "Todo created successfully");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody Todo todo) {
        Todo updated = todoService.update(id, todo);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", updated);
        response.put("message", "Todo updated successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        todoService.delete(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Todo deleted successfully");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Map<String, Object>> toggle(@PathVariable Long id) {
        Todo todo = todoService.getById(id);
        if (todo == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Todo not found");
            return ResponseEntity.ok(response);
        }
        
        Todo updateTodo = new Todo();
        updateTodo.setCompleted(!todo.getCompleted());
        Todo updated = todoService.update(id, updateTodo);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", updated);
        response.put("message", "Todo toggled successfully");
        return ResponseEntity.ok(response);
    }
}
