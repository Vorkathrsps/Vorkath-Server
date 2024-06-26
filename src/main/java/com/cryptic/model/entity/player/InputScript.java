package com.cryptic.model.entity.player;

/**
 * @author Ynneh | 06/04/2022 - 16:20
 * <https://github.com/drhenny>
 */
@FunctionalInterface
public interface InputScript<E> {

    boolean handle(E value);

}
