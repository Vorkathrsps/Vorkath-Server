package com.cryptic.annotate;

import com.cryptic.network.pipeline.Bootstrap;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark method to be called from {@link Bootstrap}.
 * @author Sharky
 * @Since July 03, 2023
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Init {
}
