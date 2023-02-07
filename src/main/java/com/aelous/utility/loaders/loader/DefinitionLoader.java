package com.aelous.utility.loaders.loader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;

/**
 * An abstract class which handles the loading
 * of some sort of definition-related file.
 *
 * @author Professor Oak
 */
public abstract class DefinitionLoader implements Runnable {

    private static final Logger logger = LogManager.getLogger(DefinitionLoader.class);

    public abstract void load() throws Exception;

    public abstract String file();

    @Override
    public void run() {
        String file = file();
        try {
            long start = System.currentTimeMillis();
            load();
            long elapsed = System.currentTimeMillis() - start;
            logger.info("Loaded definitions for {}. It took {}ms.", file, elapsed);
        } catch (Throwable e) {
            logger.fatal(new ParameterizedMessage("Could not load definition for {}", file), e);
        }
    }
}
