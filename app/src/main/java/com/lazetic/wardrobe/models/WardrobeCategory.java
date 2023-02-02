package com.lazetic.wardrobe.models;

import java.util.ArrayList;
import java.util.List;

public enum WardrobeCategory {
    ACCESSORY("Accessory"),
    TOP("Top"),
    BOTTOM("Bottom"),
    SHOE("Shoe");

    private final String name;

    private WardrobeCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static WardrobeCategory getByName(String name) {
        return WardrobeCategory.valueOf(name);
    }

    public static List<String> getWardrobeCategories() {
        List<String> wardrobeCategories = new ArrayList<>();
        wardrobeCategories.add("");
        for (WardrobeCategory m : WardrobeCategory.values()) {
            wardrobeCategories.add(m.getName());
        }
        return wardrobeCategories;
    }
}
