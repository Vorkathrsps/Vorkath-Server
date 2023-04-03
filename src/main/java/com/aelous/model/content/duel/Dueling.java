package com.aelous.model.content.duel;

import com.aelous.GameServer;
import com.aelous.GameConstants;
import com.aelous.model.content.EffectTimer;
import com.aelous.model.content.mechanics.Poison;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.player.*;
import com.aelous.model.items.trade.Trading;
import com.aelous.model.inter.InterfaceConstants;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;

import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.Venom;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.combat.skull.Skulling;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.items.Item;
import com.aelous.model.items.container.ItemContainer;
import com.aelous.model.items.container.inventory.Inventory;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Color;
import com.aelous.utility.SecondsTimer;
import com.aelous.utility.Utils;
import com.aelous.utility.timers.TimerKey;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.aelous.model.entity.attributes.AttributeKey.*;
import static com.aelous.utility.ItemIdentifiers.BLOOD_MONEY;

/**
 * Handles the dueling system.
 *
 * @author Professor Oak
 */
public class Dueling {

    private static final Logger logger = LogManager.getLogger(Dueling.class);
    private static final Logger stakeLogs = LogManager.getLogger("StakeLogs");
    private static final Level STAKE;

    static {
        STAKE = Level.getLevel("STAKE");
    }

    private final Player player;
    private final ItemContainer container;
    private Player opponent;
    private int configValue;

    private DuelState state = DuelState.NONE;
    private static final String currencyType = "BM";

    //Delays!!
    private final SecondsTimer button_delay = new SecondsTimer();
    private final SecondsTimer request_delay = new SecondsTimer();

    //Rules
    private boolean[] rules = new boolean[DuelRule.values().length];

    private static final int DUELING_WITH_FRAME = 6671;
    private static final int INTERFACE_ID = 6575;
    private static final int CONFIRM_INTERFACE_ID = 6412;
    private static final int SCOREBOARD_INTERFACE_ID = 6733;
    private static final int SCOREBOARD_CONTAINER = 6822;
    private static final int SCOREBOARD_USERNAME_FRAME = 6840;
    private static final int SCOREBOARD_COMBAT_LEVEL_FRAME = 6839;
    public static final int MAIN_INTERFACE_CONTAINER = 6669;
    private static final int SECOND_INTERFACE_CONTAINER = 6670;
    private static final int DUEL_STATUS_FRAME_1 = 6684;
    private static final int DUEL_STATUS_FRAME_2 = 6571;
    private static final int ITEM_LIST_1_FRAME = 6516;
    private static final int ITEM_LIST_2_FRAME = 6517;
    private static final int RULES_FRAME_START = 8242;
    private static final int RULES_CONFIG_ID = 286;
    private static final int TOTAL_WORTH_FRAME = 24234;

    // Sets the double death attrib if players died within 2 ticks of each other.
    public static void check_double_death(Player player) {
        if (in_duel(player)) {

            var partner = player.getDueling().getOpponent();
            if (partner == null) return;

            // We didn't die 3 ticks later and then set the double death to true.
            // 1st player died, we're both dead on this tick.
            //player.debug("opp ded %s and this cycle %d op-c: %d", partner.dead(), World.getWorld().cycleCount(), partner.<Integer>getAttribOr(AttributeKey.ARENA_DEATH_TICK, 0));

            if (partner.dead() && World.getWorld().cycleCount() == partner.<Integer>getAttribOr(AttributeKey.ARENA_DEATH_TICK, 0) + 3) {
                player.putAttrib(AttributeKey.STAKING_DOUBLE_DEATH, true);
                partner.putAttrib(AttributeKey.STAKING_DOUBLE_DEATH, true);
                //player.debug("DD!")
            }
        }
    }

    // If the player is NOT in a duel, nor is any duel-related interface open.
    public static boolean screen_closed(Player player) {
        return !player.getInterfaceManager().isInterfaceOpen(6575) && !player.getInterfaceManager().isInterfaceOpen(6412) && !player.getDueling().inDuel();
    }

    // Check if the player to interact with is even in/out of the duel arena. Cannot interact if we're in and they're out.
    public static boolean not_in_area(Entity entity, Entity other, String message) {
        if (entity.isPlayer() && other.isPlayer()) {
            // Establish if our target is even in a stake or not.
            var entityInStake = entity.getAsPlayer().getDueling().inDuel();
            var targetInStake = other.getAsPlayer().getDueling().inDuel();

            if ((entityInStake && !targetInStake) || (!entityInStake && targetInStake)) {
                if (message.length() > 0) {
                    entity.message(message);
                }
                return true;
            }

            if (entityInStake && targetInStake) {
                if (other != (entity.getAsPlayer()).getDueling().getOpponent()) {
                    entity.message("They are not your opponent!");
                    return true;
                }
            }
        }
        return false;
    }

    // If the stake hasn't started. If we're currently counting down.
    public static boolean stake_not_started(Entity entity, Entity other) {
        if (!entity.isPlayer() || !other.isPlayer() || !entity.getAsPlayer().getDueling().inDuel() || !other.getAsPlayer().getDueling().inDuel()) {
            return false;
        }
        return entity.getTimers().has(TimerKey.STAKE_COUNTDOWN);
    }

    // If the in_stake attribute is set.
    public static boolean in_duel(Entity player) {
        return player.isPlayer() && player.getAsPlayer().getDueling().inDuel();
    }

    public void handleSavedConfig() {
        player.getDueling().setConfigValue(0);
        for (int i = 0; i < player.getSavedDuelConfig().length; i++) {
            player.getDueling().rules[i] = player.getSavedDuelConfig()[i];
        }
        if (player.getDueling().getOpponent() != null && player.getDueling().getOpponent().getDueling() != null) {
            player.getDueling().getOpponent().getDueling().setRules(player.getDueling().getRules());
        }
        DuelRule[] duelRules = DuelRule.values();
        if (player.getSavedDuelConfig() != null) {
            for (int i = 0; i < player.getSavedDuelConfig().length; i++) {
                if (player.getSavedDuelConfig()[i]) {
                    player.getDueling().setConfigValue(player.getDueling().getConfigValue() + duelRules[i].getConfigId());
                    player.getDueling().getOpponent().getDueling().setConfigValue(player.getDueling().getConfigValue());
                }
            }
            if (opponent.getDueling().getState() == DuelState.ACCEPTED_DUEL_SCREEN) {
                opponent.getDueling().setState(DuelState.DUEL_SCREEN);
            }
            if (player.getDueling().getState() == DuelState.ACCEPTED_DUEL_SCREEN) {
                player.getDueling().setState(DuelState.DUEL_SCREEN);
            }
            player.getPacketSender().sendString(DUEL_STATUS_FRAME_1, "<col=ca0d0d>DUEL MODIFIED!");
            opponent.getPacketSender().sendString(DUEL_STATUS_FRAME_1, "<col=ca0d0d>DUEL MODIFIED!");
            player.message("Loaded previous duel settings!");
            player.getDueling().getOpponent().message("Loaded previous duel settings!");
        }
        opponent.getDueling().setConfigValue(configValue);
        player.getPacketSender().sendVarpIntSize(RULES_CONFIG_ID, configValue);
        opponent.getPacketSender().sendVarpIntSize(RULES_CONFIG_ID, configValue);
    }

    public Dueling(Player player) {
        this.player = player;
        //The container which will hold all our offered items.
        this.container = new ItemContainer(28, ItemContainer.StackPolicy.STANDARD) {

            public void onRefresh() {
                player.getInterfaceManager().openInventory(INTERFACE_ID, InterfaceConstants.REMOVE_INVENTORY_ITEM - 1);
                player.getPacketSender().sendItemOnInterface(InterfaceConstants.REMOVE_INVENTORY_ITEM, player.inventory().toArray());
                player.getPacketSender().sendItemOnInterface(MAIN_INTERFACE_CONTAINER, player.getDueling().getContainer().toArray());
                player.getPacketSender().sendItemOnInterface(SECOND_INTERFACE_CONTAINER, opponent.getDueling().getContainer().toArray());
                opponent.getPacketSender().sendItemOnInterface(MAIN_INTERFACE_CONTAINER, opponent.getDueling().getContainer().toArray());
                opponent.getPacketSender().sendItemOnInterface(SECOND_INTERFACE_CONTAINER, player.getDueling().getContainer().toArray());
            }
        };
    }

    public void process() {
        if (state == DuelState.NONE || state == DuelState.REQUESTED_DUEL) {
            //Show challenge option
            if (player.getPlayerInteractingOption() != PlayerInteractingOption.CHALLENGE) {
                player.getPacketSender().sendInteractionOption("Challenge", 1, false);
                player.getPacketSender().sendInteractionOption("null", 2, true); //Remove attack option
            }
        } else if (state == DuelState.STARTING_DUEL || state == DuelState.IN_DUEL) {
            //Show attack option
            if (player.getPlayerInteractingOption() != PlayerInteractingOption.ATTACK) {
                player.getPacketSender().sendInteractionOption("Attack", 2, true);
                player.getPacketSender().sendInteractionOption("null", 1, false); //Remove challenge option
            }
        } else {
            //Hide both options if player isn't in one of those states
            if (player.getPlayerInteractingOption() != PlayerInteractingOption.NONE) {
                player.getPacketSender().sendInteractionOption("null", 2, true);
                player.getPacketSender().sendInteractionOption("null", 1, false);
            }
        }
    }

    public void requestDuel(Player t_) {
        if (!GameServer.properties().enableDueling) {
            player.message("Dueling is currently disabled until we have a larger playerbase.");
            return;
        }

        if (player.getUsername().equalsIgnoreCase("Box test")) {
            player.message("This account can't duel other players.");
            return;
        }

        if (t_.getIronManStatus() != IronMode.NONE) {
            player.message("Your partner is an Iron man, and cannot stake.");
            return;
        }

        if (t_.getGameMode().isDarklord()) {
            player.message("Your partner is an Dark Lord, and cannot stake.");
            return;
        }

        if (state == DuelState.NONE || state == DuelState.REQUESTED_DUEL) {

            //Make sure to not allow flooding!
            if (request_delay.active()) {
                int seconds = request_delay.secondsRemaining();
                player.message("You must wait another " + (seconds == 1 ? "second" : "" + seconds + " seconds") + " before sending more duel challenges.");
                return;
            }

            if (!t_.getBankPin().hasEnteredPin() && GameServer.properties().requireBankPinOnLogin) {
                player.message("That player is currently busy.");
                return;
            }

            //The other players' current duel state.
            final DuelState t_state = t_.getDueling().getState();

            //Should we initiate the duel or simply send a request?
            boolean initiateDuel = false;

            //Update this instance...
            this.setOpponent(t_);
            this.setState(DuelState.REQUESTED_DUEL);

            //Check if target requested a duel with us...
            if (t_state == DuelState.REQUESTED_DUEL) {
                if (t_.getDueling().getOpponent() != null &&
                    t_.getDueling().getOpponent() == player) {
                    initiateDuel = true;
                }
            }

            //Initiate duel for both players with eachother?
            if (initiateDuel && opponent.<Boolean>getAttribOr(SEND_DUEL_REQUEST,false)) {
                player.getDueling().initiateDuel();
                t_.getDueling().initiateDuel();
            } else {
                askDuelSettings();
            }

            //Set the request delay to 2 seconds at least.
            request_delay.start(2);
        } else {
            player.message("You cannot request a duel right now.");
        }
    }

    public void initiateDuel() {
        //Safety we didn't have before!
        if (player.locked() || player.hp() < 1 || opponent.locked() || opponent.hp() < 1 || player.dead()
            || opponent.dead() || player == opponent || player.getIndex() == opponent.getIndex()
            || player.getDueling().inDuel() || opponent.getDueling().inDuel()) {
            player.message("You can't start a duel right now.");
            return;
        }

        //Set our duel state
        setState(DuelState.DUEL_SCREEN);
        //Set our player status
        player.setStatus(PlayerStatus.DUELING);
        //Refresh container
        container.onRefresh();
        //Update strings on interface
        player.getPacketSender().
            sendString(DUELING_WITH_FRAME, "<col=ffb000>Dueling with: <col=ffffff>" + opponent.getUsername() + "<col=ffb000>          Combat level: <col=ffffff>" + opponent.getSkills().combatLevel()).
            sendString(DUEL_STATUS_FRAME_1, "").sendString(669, "Lock Weapon").sendString(8278, "Neither player is allowed to change weapon.");

        //Send equipment on the interface..
        player.getPacketSender().sendItemOnInterface(13824, player.getEquipment().toArray());

        //Clear the container
        container.clear(true);

        var customRule = player.<Integer>getAttribOr(CUSTOM_DUEL_RULE, 0);

        if (customRule == 1) {
            ddsAndWhip();
        } else if (customRule == 2) {
            whipOnly();
        } else {
            //Reset rule toggle configs
            player.getPacketSender().sendConfig(RULES_CONFIG_ID, 0);
            opponent.getPacketSender().sendConfig(RULES_CONFIG_ID, 0);
        }
    }

    public void closeDuel() {
        if (state != DuelState.NONE) {

            //Cache the current interact
            final Player opponent = this.opponent;

            //Return all items...
            for (Item t : container.toNonNullArray()) {
                player.inventory().addAll(t);
            }

            //Refresh inventory
            player.inventory().refresh();

            //Reset all attributes...
            resetAttributes();

            //Send decline message
            player.message("Duel declined.");
            player.getInterfaceManager().close();

            //Reset/close duel for other player aswell (the cached interact)
            if (opponent != null) {
                if (opponent.getStatus() == PlayerStatus.DUELING) {
                    if (opponent.getDueling().getOpponent() != null
                        && opponent.getDueling().getOpponent() == player) {
                        opponent.getInterfaceManager().close();
                    }
                }
            }
        }
    }

    public void resetAttributes() {
        //Remove skulls
        Skulling.unskull(player);
        Skulling.unskull(opponent);

        // Remove any lock states
        opponent.unlock();
        player.unlock();

        //Allow players to walk again
        player.getMovementQueue().clear().setBlockMovement(false);
        opponent.getMovementQueue().clear().setBlockMovement(false);

        player.clearAttrib(SEND_DUEL_REQUEST);
        player.clearAttrib(CUSTOM_DUEL_RULE);
        player.clearAttrib(AttributeKey.WHIP_ONLY);
        player.clearAttrib(AttributeKey.WHIP_AND_DDS);

        opponent.clearAttrib(SEND_DUEL_REQUEST);
        opponent.clearAttrib(CUSTOM_DUEL_RULE);
        opponent.clearAttrib(AttributeKey.WHIP_ONLY);
        opponent.clearAttrib(AttributeKey.WHIP_AND_DDS);

        //Reset duel attributes
        setOpponent(null);
        setState(DuelState.NONE);

        //Reset player status if it's dueling.
        if (player.getStatus() == PlayerStatus.DUELING) {
            player.setStatus(PlayerStatus.NONE);
        }

        //Reset container..
        container.clear(true);

        //Reset rules
        //System.out.println("Reset rules");

        setRules(new boolean[DuelRule.values().length]);

        //  Arrays.fill(rules, false);

        //System.out.println("CALLED FOR RESET");

        //Clear toggles
        configValue = 0;

        player.getPacketSender().sendConfig(RULES_CONFIG_ID, 0);

        //Clear head hint
        player.getPacketSender().sendEntityHintRemoval(true);

        //Clear items on interface
        player.getPacketSender().
            clearItemOnInterface(MAIN_INTERFACE_CONTAINER).
            clearItemOnInterface(SECOND_INTERFACE_CONTAINER);
    }

    //Deposit or withdraw an item....
    public void handleItem(int id, int amount, int slot, ItemContainer from, ItemContainer to) {
        if (player.getInterfaceManager().isInterfaceOpen(INTERFACE_ID)) {
            //System.out.println("test 1 Item " + id + " amount " + amount);
            //Validate this stake action..
            if (!validate(player, opponent, PlayerStatus.DUELING, DuelState.DUEL_SCREEN, DuelState.ACCEPTED_DUEL_SCREEN)) {
                return;
            }
            Item stakeItem = new Item(id, amount);
            if (!stakeItem.rawtradable()) {
                player.message("You cannot stake that item.");
                return;
            }

            //Only allow blood money stakes
            if(stakeItem.getId() != BLOOD_MONEY) {
                player.message("You cannot stake that item.");
                return;
            }

            if (Arrays.stream(GameConstants.DONATOR_ITEMS).anyMatch(donator_item -> donator_item == stakeItem.getId())) {
                player.message("You cannot stake that item.");
                return;
            }

            for (Item bankItem : GameConstants.BANK_ITEMS) {
                if (bankItem.note().getId() == stakeItem.getId()) {
                    player.message("You can't stake spawnable items.");
                    return;
                }
                if (bankItem.getId() == stakeItem.getId()) {
                    player.message("You can't stake spawnable items.");
                    return;
                }
            }

            if (stakeItem.unnote().definition(World.getWorld()).pvpAllowed) {
                player.message("You can't gamble spawnable items.");
                return;
            }

            if (stakeItem.getValue() <= 0) {
                player.message("You can't stake spawnable items.");
                return;
            }

            //Check if the duel was previously accepted (and now modified)...
            if (state == DuelState.ACCEPTED_DUEL_SCREEN) {
                state = DuelState.DUEL_SCREEN;
            }
            if (opponent.getDueling().getState() == DuelState.ACCEPTED_DUEL_SCREEN) {
                opponent.getDueling().setState(DuelState.DUEL_SCREEN);
            }
            player.getPacketSender().sendString(DUEL_STATUS_FRAME_1, "<col=ca0d0d>DUEL MODIFIED!");
            opponent.getPacketSender().sendString(DUEL_STATUS_FRAME_1, "<col=ca0d0d>DUEL MODIFIED!");

            //Handle the item switch..
            if (state == DuelState.DUEL_SCREEN
                && opponent.getDueling().getState() == DuelState.DUEL_SCREEN) {

                //Check if the item is in the right place
                if (from.getItems()[slot] != null && from.getItems()[slot].getId() == id) {
                    //Let's not modify the amount.
                    //amount = from.getAmount(id);
                    //System.out.println("test 2 Item " + id + " amount " + amount);
                    //Make sure we can fit that amount in the duel
                    if (from instanceof Inventory) {
                        if (!stakeItem.stackable()) {
                            if (amount > container.getFreeSlots()) {
                                //System.out.println("test 3 Item " + id + " amount " + amount);
                                amount = container.getFreeSlots();
                            }
                        }
                    }

                    if (amount <= 0) {
                        return;
                    }

                    if (amount > from.count(id)) {
                        amount = from.count(id);
                    }

                    final Item item = new Item(id, amount);

                    //Do the switch!
                    if (item.getAmount() == 1) {
                        from.remove(item, slot, true, false);
                    } else {
                        from.remove(item, true);
                    }
                    to.add(item, true);
                    container.onRefresh();
                }
            } else {
                player.getInterfaceManager().close();
            }
        }
        //System.out.println("test 4 Item " + id + " amount " + amount);
    }

    public void acceptDuel() {

        //Validate this stake action..
        if (!validate(player, opponent, PlayerStatus.DUELING, DuelState.DUEL_SCREEN, DuelState.ACCEPTED_DUEL_SCREEN, DuelState.CONFIRM_SCREEN, DuelState.ACCEPTED_CONFIRM_SCREEN)) {
            return;
        }

        //Check button delay...
        if (button_delay.active()) {
            return;
        }

        //Check button delay...
        //if (!button_delay.finished()) {
        //    return;
        //}

        //Cache the interact...
        final Player interact_ = opponent;

        //Interact's current trade state.
        final DuelState t_state = interact_.getDueling().getState();

        //Check which action to take..
        if (state == DuelState.DUEL_SCREEN) {

            //Verify that the interact can receive all items first..
            int slotsRequired = getFreeSlotsRequired(player);
            if (player.inventory().getFreeSlots() < slotsRequired) {
                player.message("You need at least " + slotsRequired + " free inventory slots for this duel.");
                return;
            }

            if (rules[DuelRule.NO_MELEE.ordinal()] && rules[DuelRule.NO_RANGED.ordinal()] && rules[DuelRule.NO_MAGIC.ordinal()]) {
                player.message("You must enable at least one of the three combat styles.");
                return;
            }

            if (rules[DuelRule.NO_MOVEMENT.ordinal()] && rules[DuelRule.OBSTACLES.ordinal()]) {
                //There is a second message you can't have No Movement in an arena with obstacles, depending on which rule you select first then second, but for now this is good enough.
                player.message("You can't have obstacles if you want No Movement.");
                return;
            }

            if (rules[DuelRule.NO_FORFEIT.ordinal()] && rules[DuelRule.NO_MELEE.ordinal()]) {
                //player.message("You can't have No Forfeit and No Melee, you could run out of ammo.");
                player.getPacketSender().sendString(DUEL_STATUS_FRAME_1, "You can't have No Forfeit and No Melee, you could run out of ammo.");
                interact_.getPacketSender().sendString(DUEL_STATUS_FRAME_1, "You can't have No Forfeit and No Melee, you could run out of ammo.");
                return;
            }

            //Both are in the same state. Do the first-stage accept.
            setState(DuelState.ACCEPTED_DUEL_SCREEN);

            //Update status...
            player.getPacketSender().sendString(DUEL_STATUS_FRAME_1, "Waiting for other player..");
            interact_.getPacketSender().sendString(DUEL_STATUS_FRAME_1, "" + player.getUsername() + " has accepted.");

            //Check if both have accepted..
            if (state == DuelState.ACCEPTED_DUEL_SCREEN &&
                t_state == DuelState.ACCEPTED_DUEL_SCREEN) {

                //Technically here, both have accepted.
                //Go into confirm screen!
                player.getDueling().confirmScreen();
                interact_.getDueling().confirmScreen();
            }
        } else if (state == DuelState.CONFIRM_SCREEN) {
            //Both are in the same state. Do the second-stage accept.
            setState(DuelState.ACCEPTED_CONFIRM_SCREEN);

            //Update status...
            player.getPacketSender().sendString(DUEL_STATUS_FRAME_2, "Waiting for " + interact_.getUsername() + "'s confirmation..");
            interact_.getPacketSender().sendString(DUEL_STATUS_FRAME_2, "" + player.getUsername() + " has accepted. Do you wish to do the same?");
            //Check if both have accepted..
            if (state == DuelState.ACCEPTED_CONFIRM_SCREEN &&
                t_state == DuelState.ACCEPTED_CONFIRM_SCREEN) {
                heal_player(player);
                heal_player(opponent);

                //Decide where they will spawn in the arena..
                final boolean obstacle = rules[DuelRule.OBSTACLES.ordinal()];
                final boolean movementDisabled = rules[DuelRule.NO_MOVEMENT.ordinal()];

                Tile pos1 = getRandomSpawn(obstacle);
                Tile pos2 = getRandomSpawn(obstacle);

                //Make them spaw next to eachother
                if (movementDisabled) {
                    pos2 = pos1.copy().add(-1, 0);
                }

                player.getDueling().startDuel(pos1);
                interact_.getDueling().startDuel(pos2);
            }
        }

        button_delay.start(1);
    }

    public Tile getRandomSpawn(boolean obstacle) {
        if (obstacle) {
            return new Tile(3366 + Utils.getRandom(11), 3246 + Utils.getRandom(6));
        }
        return new Tile(3335 + Utils.getRandom(11), 3246 + Utils.getRandom(6));
    }

    private void confirmScreen() {
        //Update state
        player.getDueling().setState(DuelState.CONFIRM_SCREEN);

        //Send new interface frames
        String this_items = Trading.listItems(container);
        String interact_item = Trading.listItems(opponent.getDueling().getContainer());
        player.getPacketSender().sendString(ITEM_LIST_1_FRAME, this_items);
        player.getPacketSender().sendString(ITEM_LIST_2_FRAME, interact_item);

        //Reset all previous strings related to rules
        for (int i = 8238; i <= 8253; i++) {
            player.getPacketSender().sendString(i, "");
        }

        //Send new ones
        player.getPacketSender().sendString(8250, "Hitpoints will be restored.");
        player.getPacketSender().sendString(8238, "Boosted stats will be restored.");
        if (rules[DuelRule.OBSTACLES.ordinal()]) {
            player.getPacketSender().sendString(8239, "<col=ca0d0d>There will be obstacles in the arena.");
        }
        player.getPacketSender().sendString(8240, "");
        player.getPacketSender().sendString(8241, "");

        int ruleFrameIndex = RULES_FRAME_START;
        for (int i = 0; i < DuelRule.values().length; i++) {
            if (i == DuelRule.OBSTACLES.ordinal())
                continue;
            if (rules[i]) {
                player.getPacketSender().sendString(ruleFrameIndex, "" + DuelRule.forId(i).toString());
                ruleFrameIndex++;
            }
        }

        player.getPacketSender().sendString(DUEL_STATUS_FRAME_2, "");

        //Send new interface..
        player.getInterfaceManager().openInventory(CONFIRM_INTERFACE_ID, InterfaceConstants.INVENTORY_INTERFACE);
        player.getPacketSender().sendItemOnInterface(InterfaceConstants.REMOVE_INVENTORY_ITEM, player.inventory().toArray());
    }

    public boolean isInteract(Player p2) {
        return (opponent != null
            && opponent.equals(p2));
    }

    public boolean checkRule(int button) {
        DuelRule rule = DuelRule.forButtonId(button);
        if (rule != null) {
            var whipAndDDS = player.<Boolean>getAttribOr(AttributeKey.WHIP_AND_DDS, false);
            var whipOnly = player.<Boolean>getAttribOr(AttributeKey.WHIP_ONLY, false);

            if (whipAndDDS || whipOnly) {
                player.message(Color.RED.wrap("You can't change the rules during this duel."));
                return false;
            }
            checkRule(rule);

            return true;
        }
        return false;
    }

    private void askDuelSettings() {

        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Whip And DDS", "Whip Only", "Normal Duel", "Nevermind");
                setPhase(0);
            }

            @Override
            protected void select(int option) {
                if (isPhase(0)) {
                    if (option == 1) {
                        player.putAttrib(SEND_DUEL_REQUEST, true);
                        player.putAttrib(CUSTOM_DUEL_RULE, 1);
                        player.message("You've sent a duel challenge to " + opponent.getUsername() + "...");
                        opponent.getInterfaceManager().closeDialogue();
                        opponent.getPacketSender().sendMessage(player.getUsername() + ":duelreqddswhiponly:");
                    }
                    if (option == 2) {
                        player.putAttrib(SEND_DUEL_REQUEST, true);
                        player.putAttrib(CUSTOM_DUEL_RULE, 2);
                        player.message("You've sent a duel challenge to " + opponent.getUsername() + "...");
                        opponent.getInterfaceManager().closeDialogue();
                        opponent.getPacketSender().sendMessage(player.getUsername() + ":duelreqwhiponly:");
                    }
                    if (option == 3) {
                        player.putAttrib(SEND_DUEL_REQUEST, true);
                        player.putAttrib(CUSTOM_DUEL_RULE, 3);
                        player.message("You've sent a duel challenge to " + opponent.getUsername() + "...");
                        opponent.getInterfaceManager().closeDialogue();
                        opponent.getPacketSender().sendMessage(player.getUsername() + ":duelreqnormal:");
                    }
                    stop();
                }
            }
        });
    }

    private void ddsAndWhip() {
        player.getDueling().setConfigValue(0);
        // booleans start from 0 when duel inter closed.. assume it closes after duel ends too and on 3rd screen

        for (DuelRule duelRule : DuelRule.values()) {
            if (duelRule == DuelRule.NO_WEAPON || duelRule == DuelRule.NO_SPECIAL_ATTACKS || duelRule == DuelRule.NO_MELEE || duelRule == DuelRule.OBSTACLES) {
                continue; // skip these rules
            }

            rules[duelRule.ordinal()] = true; // everything was true here
            configValue += duelRule.getConfigId();
        }

        if (player.getDueling().getOpponent() != null && player.getDueling().getOpponent().getDueling() != null) {
            player.getDueling().getOpponent().getDueling().setRules(rules);
        }

        opponent.getDueling().setConfigValue(configValue);
        player.getPacketSender().sendVarpIntSize(RULES_CONFIG_ID, configValue);
        opponent.getPacketSender().sendVarpIntSize(RULES_CONFIG_ID, configValue);
        player.putAttrib(AttributeKey.WHIP_AND_DDS, true);
        opponent.putAttrib(AttributeKey.WHIP_AND_DDS, true);
    }

    private void whipOnly() {
        player.getDueling().setConfigValue(0);
        // booleans start from 0 when duel inter closed.. assume it closes after duel ends too and on 3rd screen

        for (DuelRule duelRule : DuelRule.values()) {
            if (duelRule == DuelRule.NO_WEAPON || duelRule == DuelRule.NO_MELEE || duelRule == DuelRule.OBSTACLES) {
                continue; // skip these rules
            }

            rules[duelRule.ordinal()] = true; // everything was true here
            configValue += duelRule.getConfigId();
        }

        if (player.getDueling().getOpponent() != null && player.getDueling().getOpponent().getDueling() != null) {
            player.getDueling().getOpponent().getDueling().setRules(rules);
        }

        opponent.getDueling().setConfigValue(configValue);
        player.getPacketSender().sendVarpIntSize(RULES_CONFIG_ID, configValue);
        opponent.getPacketSender().sendVarpIntSize(RULES_CONFIG_ID, configValue);
        player.putAttrib(AttributeKey.WHIP_ONLY, true);
        opponent.putAttrib(AttributeKey.WHIP_ONLY, true);
    }

    private void checkRule(DuelRule rule) {

        //Check if we're actually dueling..
        if (player.getStatus() != PlayerStatus.DUELING) {
            return;
        }

        //Verify stake...
        if (!validate(player, opponent, PlayerStatus.DUELING, DuelState.DUEL_SCREEN, DuelState.ACCEPTED_DUEL_SCREEN)) {
            return;
        }

        //Verify our current state..
        if (state == DuelState.DUEL_SCREEN || state == DuelState.ACCEPTED_DUEL_SCREEN) {

            //Toggle the rule..
            if (!rules[rule.ordinal()]) {
                rules[rule.ordinal()] = true;
                configValue += rule.getConfigId();

            } else {
                rules[rule.ordinal()] = false;
                configValue -= rule.getConfigId();
            }

            //Update interact's rules to match ours.
            opponent.getDueling().setConfigValue(configValue);
            opponent.getDueling().getRules()[rule.ordinal()] = rules[rule.ordinal()];

            //Send toggles for both players.
            player.getPacketSender().sendVarpIntSize(RULES_CONFIG_ID, configValue);
            opponent.getPacketSender().sendVarpIntSize(RULES_CONFIG_ID, configValue);

            //Send modify status
            if (state == DuelState.ACCEPTED_DUEL_SCREEN) {
                state = DuelState.DUEL_SCREEN;
            }
            if (opponent.getDueling().getState() == DuelState.ACCEPTED_DUEL_SCREEN) {
                opponent.getDueling().setState(DuelState.DUEL_SCREEN);
            }
            player.getPacketSender().sendString(DUEL_STATUS_FRAME_1, "<col=ca0d0d>DUEL MODIFIED!");
            opponent.getPacketSender().sendString(DUEL_STATUS_FRAME_1, "<col=ca0d0d>DUEL MODIFIED!");

            //Inform them about this "custom" rule.
            if (rule == DuelRule.LOCK_WEAPON && rules[rule.ordinal()]) {
                player.getPacketSender().sendMessage("<col=" + Color.MEDRED.getColorValue() + ">Warning! The rule 'Lock Weapon' has been enabled. You will not be able to change").sendMessage("<col=" + Color.MEDRED.getColorValue() + ">your weapon during the duel!");
                opponent.getPacketSender().sendMessage("<col=" + Color.MEDRED.getColorValue() + ">Warning! The rule 'Lock Weapon' has been enabled. You will not be able to change").sendMessage("<col=" + Color.MEDRED.getColorValue() + ">your weapon during the duel!");
            }
        }
    }

    // Heal up when entering / ending a stake.
    private void heal_player(Player player) {
        player.setPositionToFace(null); // Reset entity facing
        player.getSkills().resetStats(); //Reset all players stats
        Poison.cure(player); //Cure the player from any poisons
        Venom.cure(2, player);
        player.getTimers().cancel(TimerKey.FROZEN); //Remove frozen timer key
        player.getTimers().cancel(TimerKey.STUNNED); //Remove stunned timer key
        player.getTimers().cancel(TimerKey.TELEBLOCK); //Remove teleblock timer key
        player.getTimers().cancel(TimerKey.TELEBLOCK_IMMUNITY); //Remove the teleblock immunity timer key
        player.setRunningEnergy(100.0, true); //Set energy to 100%
        player.setSpecialActivated(false); //Disable special attack
        player.getTimers().cancel(TimerKey.COMBAT_LOGOUT); //Remove combat logout timer key
        player.setSpecialAttackPercentage(100);//Set special to 100%
        CombatSpecial.updateBar(player);
        player.getCombat().clearDamagers(); //Clear damagers
        Prayers.closeAllPrayers(player); //Disable all prayers
        player.hp(100, 0); //Set hitpoints to 100%
        EffectTimer.clearTimers(player);
    }

    private void checkCustomRuleEquipment() {
        var customRule = player.<Integer>getAttribOr(CUSTOM_DUEL_RULE, 0);

        Item weapon = player.getEquipment().get(EquipSlot.WEAPON);
        if (weapon == null) {
            return;
        }
        if (customRule == 1) {
            boolean allowedWeapons = weapon.name().toLowerCase().contains("dragon dagger") || weapon.name().toLowerCase().contains("abyssal whip");
            if (!allowedWeapons) {
                player.getEquipment().remove(weapon);
                player.inventory().addOrBank(weapon);
            }
        } else if (customRule == 2) {
            boolean isWhip = weapon.name().toLowerCase().contains("abyssal whip");
            if (!isWhip) {
                player.getEquipment().remove(weapon);
                player.inventory().addOrBank(weapon);
            }
        }
    }

    private void startDuel(Tile telePos) {
        checkCustomRuleEquipment();

        //Let's start the duel!
        boolean playerHasVengeance = player.getAttribOr(AttributeKey.VENGEANCE_ACTIVE, false);
        boolean playerHasOverload = player.getAttribOr(OVERLOAD_TASK_RUNNING, false);
        boolean opponentHasOverload = player.getAttribOr(OVERLOAD_TASK_RUNNING, false);

        if (playerHasOverload) {
            player.clearAttrib(AttributeKey.OVERLOAD_TASK_RUNNING);
            player.getTimers().cancel(TimerKey.POTION);
            player.getSkills().resetStats();
            player.getPacketSender().sendEffectTimer(0, EffectTimer.OVERLOAD);
            player.message("Your overload potion effect has diminished upon entering the duel arena.");
        }

        boolean opponentHasVengeance = opponent.getAttribOr(AttributeKey.VENGEANCE_ACTIVE, false);
        if (opponentHasVengeance) {
            opponent.clearAttrib(AttributeKey.VENGEANCE_ACTIVE);
            opponent.message("Your Vengeance has been cleared.");
        }

        player.putAttrib(AttributeKey.MAGEBANK_MAGIC_ONLY, false);
        opponent.putAttrib(AttributeKey.MAGEBANK_MAGIC_ONLY, false);

        // Disable SOTD special
        player.getTimers().cancel(TimerKey.SOTD_DAMAGE_REDUCTION);
        opponent.getTimers().cancel(TimerKey.SOTD_DAMAGE_REDUCTION);

        //Set current duel state
        setState(DuelState.STARTING_DUEL);

        //Close open interfaces
        player.getInterfaceManager().close();

        //Unequip items based on the rules set for this duel
        for (int ruleIndex = 11; ruleIndex < rules.length; ruleIndex++) {
            DuelRule rule = DuelRule.forId(ruleIndex);
            if (rules[ruleIndex]) {
                if (rule != null && rule.getEquipmentSlot() < 0)
                    continue;
                if (rule != null && player.getEquipment().get(rule.getEquipmentSlot()) != null && player.getEquipment().get(rule.getEquipmentSlot()).getId() > 0) {
                    Item item = new Item(player.getEquipment().get(rule.getEquipmentSlot()).getId(), player.getEquipment().get(rule.getEquipmentSlot()).getAmount());
                    player.getEquipment().remove(item);
                    player.inventory().addOrBank(item);
                }
            }
        }

        if (rules[DuelRule.NO_WEAPON.ordinal()] || rules[DuelRule.NO_SHIELD.ordinal()]) {
            if (player.getEquipment().get(EquipSlot.WEAPON) != null && player.getEquipment().get(EquipSlot.WEAPON).getId() > 0) {
                if (player.getEquipment().get(EquipSlot.WEAPON).isTwoHanded()) {
                    Item item = new Item(player.getEquipment().get(EquipSlot.WEAPON).getId(), player.getEquipment().get(EquipSlot.WEAPON).getAmount());
                    player.getEquipment().remove(item);
                    player.inventory().addOrBank(item);
                }
            }
        }

        //Clear items on interface
        player.getPacketSender().clearItemOnInterface(MAIN_INTERFACE_CONTAINER).clearItemOnInterface(SECOND_INTERFACE_CONTAINER);

        //Freeze the player if we cannot move during this duel
        if (rules[DuelRule.NO_MOVEMENT.ordinal()]) {
            player.getMovementQueue().clear().setBlockMovement(true);
        }

        //Send interact hints
        player.getPacketSender().sendPositionalHint(opponent.tile().copy(), 10);
        player.getPacketSender().sendEntityHint(opponent);

        if (GameServer.properties().enableLoadLastDuelPreset) {
            //System.out.println("Setting saved duel config");
            player.setSavedDuelConfig(rules);
        }

        //Teleport the player
        player.teleport(telePos);

        // Start the timer to go '3, 2, 1, go!'
        player.getTimers().register(TimerKey.STAKE_COUNTDOWN, 11);
        opponent.getTimers().register(TimerKey.STAKE_COUNTDOWN, 11);

        player.runUninterruptable(2, () -> {
            if (in_duel(player)) {
                player.forceChat("3");
                opponent.forceChat("3");
            }
        }).then(2, () -> {
            if (in_duel(player)) {
                player.forceChat("2");
                opponent.forceChat("2");
            }
        }).then(2, () -> {
            if (in_duel(player)) {
                player.forceChat("2");
                opponent.forceChat("2");
            }
        }).then(2, () -> {
            if (in_duel(player)) {
                player.forceChat("1");
                opponent.forceChat("1");
            }
        }).then(2, () -> {
            if (in_duel(player)) {
                player.forceChat("FIGHT!");
                opponent.forceChat("FIGHT!");

                //We're in a duel
                player.getDueling().setState(DuelState.IN_DUEL);
                opponent.getDueling().setState(DuelState.IN_DUEL);
            }
        });
    }

    public void onDeath() {
        //Make sure both players are in a duel..
        if (validate(player, opponent, null, DuelState.STARTING_DUEL, DuelState.IN_DUEL)) {
            player.getDueling().setState(DuelState.ENDING_DUEL);
            opponent.getDueling().setState(DuelState.ENDING_DUEL);

            //Move players home
            opponent.teleport(GameServer.properties().duelTile.copy().add(Utils.getRandom(2), Utils.getRandom(2)));
            player.teleport(GameServer.properties().duelTile.copy().add(Utils.getRandom(2), Utils.getRandom(2)));

            //Add won items to a list..
            long totalValue = 0;
            List<Item> winnings = new ArrayList<>();
            StringBuilder playerItems = new StringBuilder();
            StringBuilder interactItems = new StringBuilder();

            for (Item item : opponent.getDueling().getContainer().toArray()) {
                if (item != null) {
                    //If inv full send to bank!
                    opponent.inventory().addOrBank(item);
                    totalValue += item.getValue();
                    interactItems.append(Utils.insertCommasToNumber(String.valueOf(item.getAmount()))).append(" ").append(item.name()).append(" (id ").append(item.getId()).append("), ");
                }
            }
            for (Item item : player.getDueling().getContainer().toArray()) {
                if (item != null) {
                    opponent.inventory().addOrBank(item);
                    winnings.add(item);
                    totalValue += item.getValue();
                    playerItems.append(Utils.insertCommasToNumber(String.valueOf(item.getAmount()))).append(" ").append(item.name()).append(" (id ").append(item.getId()).append("), ");
                }
            }
            try {
                stakeLogs.log(STAKE, "Player " + opponent.getUsername() + " got " + playerItems + " from winning a duel against " + player.getUsername());
                Utils.sendDiscordInfoLog("Player " + opponent.getUsername() + " got " + playerItems + " from winning a duel against " + player.getUsername(), "stake");
                stakeLogs.log(STAKE, "Player " + opponent.getUsername() + " already had " + interactItems + " from winning a duel against " + player.getUsername());
                Utils.sendDiscordInfoLog("Player " + opponent.getUsername() + " already had " + interactItems + " from winning a duel against " + player.getUsername(), "stake");
                long plr_value = player.getDueling().getContainer().containerValue();
                long other_plr_value = opponent.getDueling().getContainer().containerValue();
                long difference;
                difference = (plr_value > other_plr_value) ? plr_value - other_plr_value : other_plr_value - plr_value;
                stakeLogs.log(STAKE, "Player " + player.getUsername() + " (lvl " + player.getSkills().combatLevel() + ") and " + opponent.getUsername() + " (lvl " + opponent.getSkills().combatLevel() + ") duel stake value difference of " + Utils.insertCommasToNumber(String.valueOf(difference)) + " " + currencyType);
                Utils.sendDiscordInfoLog("Player " + player.getUsername() + " (lvl " + player.getSkills().combatLevel() + ") and " + opponent.getUsername() + " (lvl " + opponent.getSkills().combatLevel() + ") duel stake value difference of " + Utils.insertCommasToNumber(String.valueOf(difference)) + " " + currencyType, "stake");
                if (difference > 1_000_000) {
                    stakeLogs.warn("Player " + opponent.getUsername() + " won a stake against Player " + player.getUsername() + " with a value difference of greater than 1,000,000 " + currencyType + ", this was possibly RWT.");
                    Utils.sendDiscordInfoLog(GameServer.properties().discordNotifyId + " Player " + opponent.getUsername() + " won a stake against Player " + player.getUsername() + " with a value difference of greater than 1,000,000 " + currencyType + ", this was possibly RWT.", "stake");
                }

            } catch (Exception e) {
                //The value shouldn't ever really be a string, but just in case, let's catch the exception.
                logger.catching(e);
                logger.error("Somehow there was an exception from logging dueling between player " + player.getUsername() + " and " + opponent.getUsername());
            }

            //Send interface data..
            opponent.getPacketSender().
                sendString(SCOREBOARD_USERNAME_FRAME, player.getUsername()).
                sendString(SCOREBOARD_COMBAT_LEVEL_FRAME, "" + player.getSkills().combatLevel()).
                sendString(TOTAL_WORTH_FRAME, "<col=ffff00>Total: <col=ffb000>" + Utils.insertCommasToNumber("" + totalValue + "") + " value!");

            heal_player(player);
            heal_player(opponent);

            //Send winnings onto interface
            opponent.getPacketSender().sendItemOnInterface(SCOREBOARD_CONTAINER, winnings);

            //Send the scoreboard interface
            opponent.getInterfaceManager().open(SCOREBOARD_INTERFACE_ID);

            //Send messages
            opponent.setDuelWins(opponent.getDuelWins() + 1);
            player.setDuelLosses(player.getDuelLosses() + 1);
            opponent.getPacketSender().sendMessage("You won! You have now won " + opponent.getDuelWins() + " " + Utils.pluralOrNot("duel", opponent.getDuelWins()) + ".");
            opponent.getPacketSender().sendMessage("You have lost " + opponent.getDuelLosses() + " " + Utils.pluralOrNot("duel", opponent.getDuelLosses()) + ".");
            player.message("You were defeated! You have won " + player.getDuelWins() + " " + Utils.pluralOrNot("duel", player.getDuelWins()) + ".");
            player.message("You have now lost " + player.getDuelLosses() + " " + Utils.pluralOrNot("duel", player.getDuelLosses()) + ".");

            //Reset attributes for both
            opponent.getDueling().resetAttributes();
            player.getDueling().resetAttributes();
        } else {

            player.getDueling().resetAttributes();
            player.getInterfaceManager().close();

            if (opponent != null) {
                opponent.getDueling().resetAttributes();
                opponent.getInterfaceManager().close();
            }
        }
    }

    public boolean inDuel() {
        return state == DuelState.STARTING_DUEL || state == DuelState.IN_DUEL;
    }

    public boolean endingDuel() {
        return state == DuelState.ENDING_DUEL;
    }

    /**
     * Validates a player. Basically checks that all specified params add up.
     *
     * @param player       The player
     * @param interact     The opponent
     * @param playerStatus Our current player status
     * @param duelStates   Our current dueling status
     * @return true if we can validate false otherwise
     */
    private static boolean validate(Player player, Player interact, PlayerStatus playerStatus, DuelState... duelStates) {
        //Verify player...
        if (player == null || interact == null) {
            return false;
        }

        //Make sure we have proper status
        if (playerStatus != null) {
            if (player.getStatus() != playerStatus) {
                return false;
            }

            //Make sure we're interacting with eachother
            if (interact.getStatus() != playerStatus) {
                return false;
            }
        }

        if (player.getDueling().getOpponent() == null
            || player.getDueling().getOpponent() != interact) {
            return false;
        }
        if (interact.getDueling().getOpponent() == null
            || interact.getDueling().getOpponent() != player) {
            return false;
        }

        //Make sure we have proper duel state.
        boolean found = false;
        for (DuelState duelState : duelStates) {
            if (player.getDueling().getState() == duelState) {
                found = true;
                break;
            }
        }
        if (!found) {
            return false;
        }

        //Do the same for our interact
        found = false;
        for (DuelState duelState : duelStates) {
            if (interact.getDueling().getState() == duelState) {
                found = true;
                break;
            }
        }
        return found;
    }

    private int getFreeSlotsRequired(Player player) {
        int slots = 0;

        //Count equipment that needs to be taken off
        for (int index = 11; index < player.getDueling().getRules().length; index++) {
            DuelRule rule = DuelRule.values()[index];
            if (player.getDueling().getRules()[rule.ordinal()]) {
                Item item = player.getEquipment().get(rule.getEquipmentSlot());
                if (item == null || !item.isValid()) {
                    continue;
                }
                if (!(item.stackable() && player.inventory().contains(item.getId()))) {
                    slots += rule.getInventorySpaceReq();
                }
            }
        }

        //Count inventory slots from interact's container aswell as ours
        for (Item item : container.getItems()) {
            if (item == null || !item.isValid())
                continue;
            if (!(item.stackable() && player.inventory().contains(item.getId()))) {
                slots++;
            }
        }

        for (Item item : opponent.getDueling().getContainer().getItems()) {
            if (item == null || !item.isValid())
                continue;
            if (!(item.stackable() && player.inventory().contains(item.getId()))) {
                slots++;
            }
        }

        return slots;
    }

    public SecondsTimer getButtonDelay() {
        return button_delay;
    }

    public DuelState getState() {
        return state;
    }

    public void setState(DuelState state) {
        this.state = state;
    }

    public ItemContainer getContainer() {
        return container;
    }

    public Player getOpponent() {
        return opponent;
    }

    public void setOpponent(Player opponent) {
        this.opponent = opponent;
    }

    public boolean[] getRules() {
        return rules;
    }

    public void setRules(boolean[] rules) {
        this.rules = rules;
    }

    public int getConfigValue() {
        return configValue;
    }

    public void setConfigValue(int configValue) {
        this.configValue = configValue;
    }

    public void incrementConfigValue(int configValue) {
        this.configValue += configValue;
    }
}
