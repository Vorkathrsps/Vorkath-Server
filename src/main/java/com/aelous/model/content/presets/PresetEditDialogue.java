package com.aelous.model.content.presets;

import com.aelous.model.entity.player.InputScript;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.items.Item;
import com.aelous.utility.Color;
import com.aelous.utility.Utils;

import java.util.Arrays;
import java.util.Objects;

import static com.aelous.model.content.presets.PresetManager.ILLEGAL_ITEMS;

/**
 * Description
 *
 * @author Patrick van Elderen | dinsdag 21 mei 2019 (CEST) : 10:14
 * @see <a href="https://github.com/Patrick9-10-1995">Github profile</a>
 */
public class PresetEditDialogue extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        send(DialogueType.OPTION, "Select option", "Change name", "Copy current setup", "Delete preset", "Cancel");
        setPhase(0);
    }

    @Override
    public void select(int option) {
        if (isPhase(0)) {
            setPhase(1);
            final int presetIndex = player.getPresetIndex();
            stop();
            switch (option) {
                case 1 -> {
                    player.setNameScript("Enter a new name for your preset below.", new InputScript() {

                        @Override
                        public boolean handle(Object value) {
                            String title = (String) value;
                            title = Utils.formatText(title);

                            if (!Utils.isValidName(title)) {
                                player.message("Invalid name for preset. Please enter characters only.");
                                player.setCurrentPreset(null);
                                player.getPresetManager().open();
                                return false;
                            }

                            if (player.getPresets()[presetIndex] != null) {
                                player.getPresets()[presetIndex].setName(title);
                                player.message("The presets name has been updated.");
                                player.getPresetManager().open();
                            }
                            return true;
                        }
                    });
                }
                case 2 -> {
                    Item[] inventory = player.inventory().copyValidItemsArray();
                    for (Item item : inventory) {
                        if (Arrays.stream(ILLEGAL_ITEMS).anyMatch(id -> id == item.getId())) {
                            player.message(Color.RED.wrap("You cannot create presets which contain illegal items."));
                            return;
                        }
                    }
                    Item[] equipment = player.getEquipment().copyValidItemsArray();
                    for (Item item : equipment) {
                        if (Arrays.stream(ILLEGAL_ITEMS).anyMatch(id -> id == item.getId())) {
                            player.message(Color.RED.wrap("You cannot create presets which contain illegal items."));
                            return;
                        }
                    }
                    for (Item t : Utils.concat(inventory, equipment)) {
                        if (t.noted()) {
                            player.message("You cannot create presets which contain noted items.");
                            return;
                        }
                    }
                    player.getPresets()[presetIndex].setInventory(inventory);
                    player.getPresets()[presetIndex].setEquipment(equipment);
                    player.getPresets()[presetIndex].setRunePouch(player.getRunePouch().stream().filter(Objects::nonNull).map(Item::copy).toArray(Item[]::new));

                    //Update stats
                    int[] stats = new int[7];
                    for (int i = 0; i < stats.length; i++) {
                        stats[i] = player.getSkills().xpLevel(i);
                    }

                    // Update stats
                    player.getPresets()[presetIndex].setStats(stats);
                    player.getPresets()[presetIndex].setSpellbook(player.getSpellbook());

                    player.message("You have updated your preset.");
                    player.getPresetManager().open();
                }
                case 3 -> {
                    player.getPresets()[presetIndex] = null;
                    player.setCurrentPreset(null);
                    player.setLastPreset(null);
                    player.message("The preset has been deleted.");
                    player.getPresetManager().open();
                }
                case 4 -> stop();
            }
        }
    }
}
