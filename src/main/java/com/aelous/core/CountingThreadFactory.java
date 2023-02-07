package com.aelous.core;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author lare96 <http://github.com/lare96>
 */
public class CountingThreadFactory implements ThreadFactory {

    private final AtomicLong counter = new AtomicLong();
    private final String nameFormat;

    public CountingThreadFactory(String nameFormat) {
        this.nameFormat = nameFormat;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setName(nameFormat + "-" + counter.incrementAndGet());
        return t;
    }
}
