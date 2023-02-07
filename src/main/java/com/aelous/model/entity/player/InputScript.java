package com.aelous.model.entity.player;

/**
 * @author Ynneh | 06/04/2022 - 16:20
 * <https://github.com/drhenny>
 */
public interface InputScript<E> {

    public boolean handle(E value);

}
