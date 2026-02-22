CREATE DATABASE IF NOT EXISTS todo_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE todo_db;

CREATE TABLE IF NOT EXISTS todo (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    description TEXT COMMENT '描述',
    completed TINYINT DEFAULT 0 COMMENT '是否完成 0-否 1-是',
    priority VARCHAR(20) DEFAULT 'MEDIUM' COMMENT '优先级 LOW/MEDIUM/HIGH',
    due_date DATETIME COMMENT '截止日期',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
    INDEX idx_completed (completed),
    INDEX idx_priority (priority),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='待办事项表';
