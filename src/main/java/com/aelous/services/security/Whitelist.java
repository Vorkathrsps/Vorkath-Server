package com.aelous.services.security;

import com.aelous.services.discord.DiscordBot;
import com.aelous.services.discord.channel.DiscordChannel;
import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import org.apache.commons.compress.utils.Lists;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ynneh | 04/04/2022 - 12:33
 * <https://github.com/drhenny>
 */
public class Whitelist {


    public static final String ADMINISTRATORS = "./data/discord/administrators.json", MODERATORS = "./data/discord/moderators.json";

    public static List<Long> administrators = new ArrayList<>(), moderators = new ArrayList<>();

    public static void save() {
        writeToFile(ADMINISTRATORS, administrators);
        writeToFile(MODERATORS, moderators);
    }

    public static void add(String uid, boolean admin) {
        if (!uid.startsWith("<@!")) {
            DiscordBot.sendMessage("Invalid UID, try @ing the person you want added to the permissions list..", DiscordChannel.DEFAULT);
            return;
        }
        try {
            long id = Long.valueOf(uid.replaceAll("<@!", "").replaceAll(">", ""));
            System.err.println("ID to add="+id);
            administrators.add(id);
            writeToFile(admin ? ADMINISTRATORS : MODERATORS, admin ? administrators : moderators);
            DiscordBot.sendMessage("Success! added <@!" + id + "> to " + (admin ? "Administrator" : "Moderator") + " list..", DiscordChannel.DEFAULT);
        } catch (Exception e) {
            DiscordBot.sendMessage("Error adding UID to permissions list.. check log for more information.", DiscordChannel.DEFAULT);
            e.printStackTrace();
        }
    }

    public static void remove(String uid) {
        try {
            long id = Long.parseLong(uid.replaceAll("<@!", "").replaceAll(">", ""));
            boolean isAdmin = administrators.stream().anyMatch(n -> n == id);
            boolean isMod = moderators.stream().anyMatch(n -> n == id);
            if (isAdmin || isMod) {
                writeToFile((isAdmin ? ADMINISTRATORS : MODERATORS), isAdmin ? administrators : moderators);
                return;
            }
            DiscordBot.sendMessage("Ughhh.. "+uid+" isn't on any staff list? or try format the name correctly ty xoxo", DiscordChannel.ADMIN);
        } catch (Exception e) {
            DiscordBot.sendMessage("Error processing removing staff.. invalid name!", DiscordChannel.ADMIN);
        }
    }

    public static void writeToFile(String fileLocation, List<Long> ids) {
        try {
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileLocation));
            JsonObject list = new JsonObject();
            JsonArray array = new JsonArray();
            array.addAll(ids);
            list.put("ids", array);
            Jsoner.serialize(list, writer);
            writer.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void showList() {
        readList(true);
        readList(false);
        StringBuilder sb = new StringBuilder();
        sb.append("Administrators:");
        sb.append("\n");
        for (Long a : administrators) {
            sb.append("<@!").append(a).append(">\n");
        }
        sb.append("\n\n");
        boolean hasMods = moderators.size() > 0;
        sb.append("Moderators:");
        sb.append("\n");
        if (hasMods) {
            for (Long a : moderators) {
                sb.append("<@!").append(a).append(">\n");
            }
        } else
            sb.append("N/A");
        DiscordBot.sendMessage(sb.toString(), DiscordChannel.DEFAULT);

    }

    public static void readList(boolean admin) {
        try {
            Reader reader = Files.newBufferedReader(Paths.get(admin ? ADMINISTRATORS : MODERATORS));

            JsonObject parser = (JsonObject) Jsoner.deserialize(reader);

            JsonArray names = (JsonArray) parser.get("ids");

            names.forEach(a -> {
                if (admin) {
                    administrators = Lists.newArrayList();
                    administrators.add(Long.parseLong(a.toString()));
                } else {
                    moderators = Lists.newArrayList();
                    moderators.add(Long.parseLong(a.toString()));
                }
            });
            reader.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
