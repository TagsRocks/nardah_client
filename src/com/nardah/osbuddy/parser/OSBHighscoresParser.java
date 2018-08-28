package com.nardah.osbuddy.parser;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.nardah.Client;
import com.nardah.osbuddy.OSBLoader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * @author Ethan Kyle Millard <skype:pumpklins>
 * @since Mon, May 07, 2018 @ 8:10 PM
 */
public class OSBHighscoresParser {
    
    private final String[] SKILLS = {"attack_xp", "defence_xp", "strength_xp", "hitpoints_xp", "ranged_xp", "prayer_xp", "magic_xp", "cooking_xp", "woodcutting_xp", "fletching_xp", "fishing_xp", "firemaking_xp", "crafting_xp", "smithing_xp", "mining_xp", "herblore_xp", "agility_xp", "thieving_xp", "slayer_xp", "farming_xp", "runecrafting_xp", "hunter_xp", "construction_xp"};
    private final String[] PRESTIGES = {"attack_prestiges", "defence_prestiges", "strength_prestiges", "hitpoints_prestiges", "ranged_prestiges", "prayer_prestiges", "magic_prestiges", "cooking_prestiges", "woodcutting_prestiges", "fletching_prestiges", "fishing_prestiges", "firemaking_prestiges", "crafting_prestiges", "smithing_prestiges", "mining_prestiges", "herblore_prestiges", "agility_prestiges", "thieving_prestiges", "slayer_prestiges", "farming_prestiges", "runecrafting_prestiges", "hunter_prestiges", "construction_prestiges"};
    public static final int[] EXP_FOR_LEVEL = {0, 83, 174, 276, 388, 512, 650, 801, 969, 1154, 1358, 1584, 1833, 2107, 2411, 2746, 3115, 3523, 3973, 4470, 5018, 5624, 6291, 7028, 7842, 8740, 9730, 10824, 12031, 13363, 14833, 16456, 18247, 20224, 22406, 24815, 27473, 30408, 33648, 37224, 41171, 45529, 50339, 55649, 61512, 67983, 75127, 83014, 91721, 101333, 111945, 123660, 136594, 150872, 166636, 184040, 203254, 224466, 247886, 273742, 302288, 333804, 368599, 407015, 449428, 496254, 547953, 605032, 668051, 737627, 814445, 899257, 992895, 1096278, 1210421, 1336443, 1475581, 1629200, 1798808, 1986068, 2192818, 2421087, 2673114, 2951373, 3258594, 3597792, 3972294, 4385776, 4842295, 5346332, 5902831, 6517253, 7195629, 7944614, 8771558, 9684577, 10692629, 11805606, 13034431, 200000000};
    
    public OSBHighscoresParser() {

    }

    /**
     * The method allows a user to modify the data as its being parsed.
     *
     * @param reader The {@code JsonObject} that contains all serialized information.
     */
    public void parse(JsonObject reader) {
        if (reader.has("username")) {
            OSBLoader.player_name.setText(reader.get("username").getAsString());
        }

        if (reader.has("total_level")) {
            OSBLoader.stat_total.setText(reader.get("total_level").getAsString());
        }

        if (reader.has("overall_xp")) {
            Client.totalExperience = Long.parseLong(reader.get("overall_xp").getAsString());
        }

        int attack = getLevelForExperience(Integer.parseInt(reader.get(SKILLS[0]).getAsString()));
        int defence = getLevelForExperience(Integer.parseInt(reader.get(SKILLS[1]).getAsString()));
        int strength = getLevelForExperience(Integer.parseInt(reader.get(SKILLS[2]).getAsString()));
        int hp = getLevelForExperience(Integer.parseInt(reader.get(SKILLS[3]).getAsString()));
        int ranged = getLevelForExperience(Integer.parseInt(reader.get(SKILLS[4]).getAsString()));
        int prayer = getLevelForExperience(Integer.parseInt(reader.get(SKILLS[5]).getAsString()));
        int magic = getLevelForExperience(Integer.parseInt(reader.get(SKILLS[6]).getAsString()));
        OSBLoader.stat_combat.setText(String.valueOf(calculateCombat(attack, defence, strength, hp, ranged, prayer, magic)));

        for (int i = 1; i < SKILLS.length; i++) {
            if (reader.has(SKILLS[i])) {
                Client.experience[i] = Integer.parseInt(reader.get(SKILLS[i - 1]).getAsString());
                OSBLoader.stat[i].setText(String.valueOf(getLevelForExperience(Integer.parseInt(reader.get(SKILLS[i - 1]).getAsString()))));
            }
        }
        for (int i = 1; i < PRESTIGES.length; i++) {
            if (reader.has(PRESTIGES[i])) {
                Client.prestige[i] = Integer.parseInt(reader.get(PRESTIGES[i - 1]).getAsString());
            }
        }




    }

    /**
     * The method that deserializes the file information.
     */
    public void deserialize(String username) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("http://nardah.com/osbuddy/highscores.php?username=" + username).openStream(), "UTF8"))) {
            final JsonParser parser = new JsonParser();
            final JsonElement element = parser.parse(reader);

            if (element.isJsonNull()) {
                return;
            }

            if (element.isJsonArray()) {
                final JsonArray array = element.getAsJsonArray();
                for (int index = 0; index < array.size(); index++) {
                    JsonObject data = (JsonObject) array.get(index);
                    parse(data);
                }
            } else if (element.isJsonObject()) {
                parse(element.getAsJsonObject());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /** Calculates the combat level of an actor. */
    private static double calculateCombat(int attack, int defence, int strength, int hp, int ranged, int prayer, int magic) {
        final double base_calculation = .25 * (defence + hp + Math.floor(prayer / 2));
        final double melee_calculation = .325 * (attack + strength);
        final double range_calculation = .325 * (Math.floor(ranged / 2) + ranged);
        final double magic_calculation = .325 * (Math.floor(magic / 2) + magic);
        return 0;//TODO: REMOVE TEMP.
        //return Math.floor(base_calculation + Doubles.max(melee_calculation, range_calculation, magic_calculation));
    }



    public byte getLevelForExperience(double experience) {
        if (experience >= EXP_FOR_LEVEL[98])
            return 99;
        return binarySearch(experience, 0, 98);
    }

    public int getXPForLevel(int level) {
        int points = 0;
        int output = 0;
        for (int lvl = 1; lvl <= level; lvl++) {
            points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
            if (lvl >= level) {
                return output;
            }
            output = (int) Math.floor(points / 4);
        }
        return 0;
    }

    private byte binarySearch(double experience, int min, int max) {
        final int mid = (min + max) / 2;
        final double value = EXP_FOR_LEVEL[mid];
        if (value > experience) {
            return binarySearch(experience, min, mid - 1);
        } else if (value == experience || EXP_FOR_LEVEL[mid + 1] > experience) {
            return (byte) (mid + 1);
        }
        return binarySearch(experience, mid + 1, max);
    }
}
