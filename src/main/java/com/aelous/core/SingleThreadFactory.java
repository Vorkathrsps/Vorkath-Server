package com.aelous.core;

import java.util.concurrent.ThreadFactory;

public class SingleThreadFactory implements ThreadFactory {

    private final String nameFormat;

    public SingleThreadFactory(String nameFormat) {
        this.nameFormat = nameFormat;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setName(nameFormat);
        return t;
    }
}
