package com.summer.demo.module.emoji;

import java.util.List;

public class Emoji {

    private int id;
    private String name;
    private String description;
    private String root;
    private String version;
    private List<EmojiItem> items;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<EmojiItem> getItems() {
        return items;
    }

    public void setItems(List<EmojiItem> items) {
        this.items = items;
    }
}
