package com.taobao.entity;

/**
 * 商品分类实体类
 */
public class Category {
    private Long id;
    private String name;
    private Long parentId;

    public Category() {}

    public Category(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getter and Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
}
