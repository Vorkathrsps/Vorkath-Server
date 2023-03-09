package com.aelous.model.content.presets;

import com.aelous.model.World;
import com.aelous.model.entity.player.InputScript;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.items.Item;
import com.aelous.utility.Utils;

import java.util.Arrays;
import java.util.Objects;

/**
 * The dialogue for creating a new preset
 *
 * @author Patrick van Elderen | dinsdag 21 mei 2019 (CEST) : 09:12
 * @see <a href="https://github.com/Patrick9-10-1995">Github profile</a>
 */
public class PresetCreateDialogue extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        send(DialogueType.STATEMENT, "That preset slot is empty. Create a new one in its place?");
        setPhase(0);
    }

    @Override
    public void next() {
        if (isPhase(0)) {
            send(DialogueType.OPTION, "Select option", "Yes", "Cancel");
            setPhase(1);
        }
    }

    @Override
    public void select(int option) {
        if (isPhase(1)) {
            setPhase(2);
            switch (option) {
                case 1 -> {
                    final int presetIndex = player.getPresetIndex();
                    if (player.getPresets()[presetIndex] == null) {
                        stop();
                        player.getPacketSender().sendEnterInputPrompt("Enter a name for your preset below.");
                        player.setNameScript("", new InputScript() {
                            @Override
                            public boolean handle(Object value) {
                                String presentName = (String) value;
                                presentName = Utils.formatText(presentName);

                                if (!Utils.isValidName(presentName)) {
                                    player.message("Invalid name for preset.");
                                    player.setCurrentPreset(null);
                                    player.getPresetManager().open();
                                    return false;
                                }

                                if (player.getPresets()[presetIndex] == null) {

                                    //Get stats..
                                    int[] stats = new int[7];
                                    for (int i = 0; i < stats.length; i++) {
                                        stats[i] = player.getSkills().xpLevel(i);
                                    }

                                    Item[] inventory = player.inventory().copyValidItemsArray();
                                    Item[] equipment = player.getEquipment().copyValidItemsArray();
                                    for (Item t : Utils.concat(inventory, equipment)) {
                                        if (Arrays.stream(PresetManager.ILLEGAL_ITEMS).anyMatch(id -> t.getId() == id)) {
                                            player.message("You cannot create a preset with the following item: "+ t.definition(World.getWorld()).name);
                                            return false;
                                        }

                                        if (t.noted()) {
                                            player.message("You cannot create presets which contain noted items.");
                                            return false;
                                        }
                                    }
                                    player.getPresets()[presetIndex] = new Presetable(presentName, presetIndex, inventory, equipment, stats, player.getSpellbook(), false, player.getRunePouch().stream().filter(Objects::nonNull).map(Item::copy).toArray(Item[]::new));
                                    player.setCurrentPreset(player.getPresets()[presetIndex]);
                                    player.getPresetManager().open();
                                }
                                return true;
                            }
                        });
                    }
                }
                case 2 -> stop();
            }
        }
    }
}
