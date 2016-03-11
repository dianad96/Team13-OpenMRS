package org.openmrs.mobile.activities;

/**
 * Created by Diana on 09/03/2016.
 */
public class Food {

    String name = null;
    String calories = null;
    boolean selected = false;

    public Food(String code, String name, boolean selected) {
        super();
        this.name = code;
        this.calories = name;
        this.selected = selected;
    }

    public String getCalories() {
        return calories;
    }
    public void setCalories(String code) {
        this.calories = code;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}