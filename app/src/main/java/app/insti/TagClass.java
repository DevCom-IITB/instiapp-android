package app.insti;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Shivam Sharma on 13-08-2018.
 */
public class TagClass {

    private String name;
    private String color;

    public TagClass(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}