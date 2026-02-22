package com.cc.todo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cc.todo.entity.Todo;
import com.cc.todo.mapper.TodoMapper;
import com.cc.todo.service.TodoService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class TodoServiceImpl extends ServiceImpl<TodoMapper, Todo> implements TodoService {

    private final RedisTemplate<String, Object> redisTemplate;

    public TodoServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public List<Todo> listAll() {
        String cacheKey = "todo:list:all";
        List<Todo> cached = (List<Todo>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        List<Todo> list = baseMapper.selectList(new LambdaQueryWrapper<Todo>()
            .orderByDesc(Todo::getCreatedAt));
        
        redisTemplate.opsForValue().set(cacheKey, list, 5, TimeUnit.MINUTES);
        return list;
    }

    @Override
    public Todo getById(Long id) {
        String cacheKey = "todo:id:" + id;
        Todo cached = (Todo) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        Todo todo = baseMapper.selectById(id);
        if (todo != null) {
            redisTemplate.opsForValue().set(cacheKey, todo, 10, TimeUnit.MINUTES);
        }
        return todo;
    }

    @Override
    @Transactional
    public Todo create(Todo todo) {
        todo.setCreatedAt(LocalDateTime.now());
        todo.setUpdatedAt(LocalDateTime.now());
        if (todo.getCompleted() == null) {
            todo.setCompleted(false);
        }
        if (todo.getPriority() == null) {
            todo.setPriority("MEDIUM");
        }
        
        baseMapper.insert(todo);
        clearCache();
        return todo;
    }

    @Override
    @Transactional
    public Todo update(Long id, Todo todo) {
        Todo existing = baseMapper.selectById(id);
        if (existing == null) {
            throw new RuntimeException("Todo not found");
        }
        
        if (todo.getTitle() != null) existing.setTitle(todo.getTitle());
        if (todo.getDescription() != null) existing.setDescription(todo.getDescription());
        if (todo.getCompleted() != null) existing.setCompleted(todo.getCompleted());
        if (todo.getPriority() != null) existing.setPriority(todo.getPriority());
        if (todo.getDueDate() != null) existing.setDueDate(todo.getDueDate());
        
        existing.setUpdatedAt(LocalDateTime.now());
        baseMapper.updateById(existing);
        
        clearCache();
        return existing;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        baseMapper.deleteById(id);
        clearCache();
    }

    @Override
    public List<Todo> getByCompleted(Boolean completed) {
        return baseMapper.selectList(new LambdaQueryWrapper<Todo>()
            .eq(Todo::getCompleted, completed)
            .orderByDesc(Todo::getCreatedAt));
    }

    @Override
    public List<Todo> search(String keyword) {
        return baseMapper.selectList(new LambdaQueryWrapper<Todo>()
            .like(Todo::getTitle, keyword)
            .or()
            .like(Todo::getDescription, keyword)
            .orderByDesc(Todo::getCreatedAt));
    }

    private void clearCache() {
        redisTemplate.delete("todo:list:all");
    }
}
