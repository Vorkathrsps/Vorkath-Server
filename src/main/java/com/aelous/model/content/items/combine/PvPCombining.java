package com.aelous.model.content.items.combine;

/**
 * @author Patrick van Elderen | March, 16, 2021, 14:43
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
/*public class PvPCombining extends PacketInteraction {

    private enum Combine {
        SARADOMINS_BLESSED_SWORD(12804, 11838, 12809, true),
        FROZEN_ABYSSAL_WHIP(4151, 12769, 12774),
        VOLCANIC_ABYSSAL_WHIP(4151, 12771, 12773),
        BLUE_DBOW(12757, 11235, 12766),
        GREEN_DBOW(12759, 11235, 12765),
        YELLOW_DBOW(12761, 11235, 12767),
        WHITE_DBOW(12763, 11235, 12768),
        ODIUM_WARD(12802, 11926, 12807, true),
        MALEDICTION_WARD(12802, 11924, 12806, true),
        AMULET_OF_FURY_OR(6585, 12526, 12436),
        GRANITE_MAUL_OR(12849, 4153, 12848, true),
        LIGHT_INFINITY_HAT(12530, 6918, 12419),
        LIGHT_INFINITY_TOP(12530, 6916, 12420),
        LIGHT_INFINITY_BOTTOMS(12530, 6924, 12421),
        DARK_INFINITY_HAT(12528, 6918, 12457),
        DARK_INFINITY_TOP(12528, 6916, 12458),
        DARK_INFINITY_BOTTOMS(12528, 6924, 12459),
        DRAGON_DEFENDER(12954, 20143, 19722),
        DRAGON_SCIMITAR(4587, 20002, 20000),
        DRAGON_PICKAXE(11920, 12800, 12797, true),
        AMULET_OF_TORTURE(19553, 20062, 20366),
        OCCULT_NECKLACE(12002, 20065, 19720),
        ARMADYL_GODSWORD(11802, 20068, 20368),
        BANDOS_GODSWORD(11804, 20071, 20370),
        SARADOMIN_GODSWORD(11806, 20074, 20372),
        ZAMORAK_GODSWORD(11808, 20077, 20374),
        STAFF_OF_LIGHT(11791, 13256, 22296),
        DRAGON_PLATEBODY_OR(21892, 22236, 22242),
        DRAGON_KITESHIELD_OR(21895, 22239, 22244),
        DRAGON_BOOT_OR(11840, 22231, 22234),
        NECKLACE_OF_ANGUISH_OR(19547, 22246, 22249),
        DRAGONFIRE_SHIELD(11286, 1540, 11284);

        private final int item1;
        private final int item2;
        private final int result;
        private boolean revert;

        Combine(int item1, int item2, int result) {
            this.item1 = item1;
            this.item2 = item2;
            this.result = result;
        }

        Combine(int item1, int item2, int result, boolean revert) {
            this.item1 = item1;
            this.item2 = item2;
            this.result = result;
            this.revert = revert;
        }
    }

    @Override
    public boolean handleItemOnItemInteraction(Player player, Item use, Item usedWith) {
        for (Combine combine : Combine.values()) {
            if ((use.getId() == combine.item1 || usedWith.getId() == combine.item1) && (use.getId() == combine.item2 || usedWith.getId() == combine.item2)) {
                if (Dueling.screen_closed(player)) {
                    if (player.inventory().contains(combine.item1) && player.inventory().contains(combine.item2)) {
                        player.inventory().remove(new Item(combine.item1));
                        player.inventory().remove(new Item(combine.item2));
                        player.inventory().add(new Item(combine.result));
                    } else {
                        player.message("You don't have the required supplies to do this.");
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean handleItemInteraction(Player player, Item item, int option) {
        if(option == 4) {
            for (Combine combine : Combine.values()) {
                if(item.getId() == combine.result) {
                    if(combine.revert) {
                        player.getDialogueManager().start(new Dialogue() {
                            @Override
                            protected void start(Object... parameters) {
                                //TODO get real messages from OSRS
                                send(DialogueType.ITEM_STATEMENT, new Item(combine.result), "<col=7f0000>Warning!</col>", "Lorem ipsum");
                                setPhase(0);
                            }

                            @Override
                            protected void next() {
                                if (isPhase(0)) {
                                    send(DialogueType.OPTION, "Proceed with reverting?", "Yes.", "No.");
                                    setPhase(1);
                                }
                            }

                            @Override
                            protected void select(int option) {
                                if (isPhase(1)) {
                                    if (option == 1) {
                                        if (player.inventory().contains(combine.result)) {
                                            player.inventory().remove(new Item(combine.result));
                                            player.inventory().add(new Item(combine.item2));
                                        }
                                        stop();
                                    } else if (option == 2) {
                                        stop();
                                    }
                                }
                            }
                        });
                    } else {
                        if (player.inventory().contains(combine.result)) {
                            player.inventory().remove(new Item(combine.result));
                            player.inventory().add(new Item(combine.item1));
                            player.inventory().add(new Item(combine.item2));
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

}*/
