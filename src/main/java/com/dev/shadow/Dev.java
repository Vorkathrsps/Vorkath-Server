package com.dev.shadow;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.player.save.PSaveSerializers;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Dev {

    public Dev() {
        try {
         // called on server start
        RegionImpl.regioncode();
        ChainImpl.BUILDER = owner -> {
            ChainImpl chain = new ChainImpl();
            chain.setOwner(owner);
            if (owner instanceof Entity) {
                ((Entity)owner).chains.add(chain);
            }
            return chain;
        };
        PSaveSerializers.init();
        } catch (Throwable e) { }
    }

}
