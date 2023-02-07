package com.aelous.utility.test.generic;

import com.aelous.GameServer;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;

/**
 * @author Jak/shadowrs
 * @version 6/6/2020
 */
public class JavaNullablityTest {
    @SuppressWarnings("UnnecessaryBoxing")
    public static void main(String[] args) {
         try {
        GameServer.properties(); // static {} block init
        // now fuck about

        Player player = new Player();
        player.setUsername("saving_test_app_startup");
        player.setNewPassword("");

        // instance test
        Player stored = null;

            stored = Player.getAttribTypeOr(player, AttributeKey.BGS_GFX_GOLD, null, Player.class, () -> player);

        System.out.printf("aye %s%n", stored);

        player.putAttrib(AttributeKey.BGS_GFX_GOLD, player);
        stored = Player.getAttribTypeOr(player, AttributeKey.BGS_GFX_GOLD, null, Player.class, () -> player);
        System.out.printf("aye 2 %s%n", stored);

        // int test primitive
        int t1 = Player.getAttribTypeOr(player, AttributeKey.BGS_GFX_GOLD, null, Integer.class, () -> 1);
        System.out.printf("aye 3 %s%n", t1);

        player.putAttrib(AttributeKey.BGS_GFX_GOLD, 2);
        t1 = Player.getAttribTypeOr(player, AttributeKey.BGS_GFX_GOLD, null, Integer.class, () -> 0);
        System.out.printf("aye 4 %s%n", t1);

        // boxed primitive test
        Integer t2 = Player.getAttribTypeOr(player, AttributeKey.BGS_GFX_GOLD, null, Integer.class, () -> Integer.valueOf(0));
        System.out.printf("aye 5 %s%n", t2);

        player.putAttrib(AttributeKey.BGS_GFX_GOLD, Integer.valueOf(3));
        t2 = Player.getAttribTypeOr(player, AttributeKey.BGS_GFX_GOLD, null, Integer.class, () -> Integer.valueOf(0));
        System.out.printf("aye 6 %s%n", t2);


        player.putAttrib(AttributeKey.BGS_GFX_GOLD, 4);
        t2 = Player.getAttribTypeOr(player, AttributeKey.BGS_GFX_GOLD, null, int.class, () -> 0);
        System.out.printf("aye 7 %s%n", t2);


        Class<?> i1 = int.class;
        Class<?> i2 = Integer.class;
        Object temp = i1;
        System.out.println("isp1 "+temp.getClass().isPrimitive());
        temp = i2;
        System.out.println("isp2 "+temp.getClass().isPrimitive());
        int i3 = 0;
        temp = i3;
        Integer i4 = 0;

        System.out.println("isp3 "+temp.getClass().isPrimitive());

        temp = i4;
        System.out.println("isp4 "+temp.getClass().isPrimitive());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
