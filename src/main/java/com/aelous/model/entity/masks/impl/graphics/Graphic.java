package com.aelous.model.entity.masks.impl.graphics;

import java.util.Optional;

/**
 * Created by Bart on 10/28/2015.
 */
public class Graphic {

    private final int id;
    private final int delay;
    private final GraphicHeight height;
    private final Priority priority;

    public Graphic(int id) {
        this.id = id;
        this.height = GraphicHeight.LOW;
        this.delay = 0;
        this.priority = Priority.LOW;
    }

    public Graphic(int id, GraphicHeight height) {
        this.id = id;
        this.height = height;
        this.delay = 0;
        this.priority = Priority.LOW;
    }

    public Graphic(int id, GraphicHeight height, int delay) {
        this.id = id;
        this.height = height;
        this.delay = delay;
        this.priority = Priority.LOW;
    }

    public Graphic(int id, GraphicHeight height, int delay, Priority priority)
    {
        this.id = id;
        this.height = height;
        this.delay = delay;
        this.priority = priority;
    }

    public int id() {
        return id;
    }

    public int getDelay() {
        return this.delay;
    }

    public GraphicHeight getHeight() {
        return this.height;
    }

    public Priority getPriority() {
        return priority;
    }

    public int delay() {
        return delay;
    }

}
