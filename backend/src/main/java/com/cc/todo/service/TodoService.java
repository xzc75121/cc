package com.cc.todo.service;

import com.cc.todo.entity.Todo;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface TodoService extends IService<Todo> {
    List<Todo> listAll();
    Todo getById(Long id);
    Todo create(Todo todo);
    Todo update(Long id, Todo todo);
    void delete(Long id);
    List<Todo> getByCompleted(Boolean completed);
    List<Todo> search(String keyword);
}
