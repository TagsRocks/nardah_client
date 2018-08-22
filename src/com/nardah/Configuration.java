package com.nardah;

/**
 * Handles all configuration settings for the client.
 *
 * @author Daniel
 */
public class Configuration {

    public static final String DISCORD_LINK = "https://discord.gg/5m9ctJk";
    public static final String WEBSITE_LINK = "http://nardah.com/";

    static boolean reporterror = true;


    public static boolean USING_OSBUDDY = false;

    static String errorname = "";

    public static final String SPRITE_FILE_NAME = "main_file_sprites";

    /** The current NPC bits. */
    static final int NPC_BITS = 16;

    /** State of client dumping it's resources. */
    public static boolean DUMP_RESOURCES = false;

    /** Display client data. */
    static boolean CLIENT_DATA = false;

    /** State of client being in debug mode. */
    public static boolean DEBUG_MODE = false;

    /** Debug the interfaces. */
    static boolean DEBUG_INTERFACES = false;

    /** State of client enabling RSA encryption.  */
    static boolean ENABLE_RSA = true;

    /** The name of the client.   */
    public final static String NAME = "Nardah";

    /**  The prefix of the client. */
    final static String PREFIX = "";

    /** The character folder path.  */
    static final String CHAR_PATH = Utility.findcachedir() + "Character";

    /** All the announcements which will be displayed on the loginscreen. */
    public final static String[] ANNOUNCEMENT = {"Welcome to Nardah", "We are currently in beta mode."};

    /** The current displayed loading message. */
    static String LOADING_MESSAGE;

    /** The loading screen messages. */
    final static String[] LOADING_MESSAGES = {
            "You can teleport around the map by clicking on the world map.",
            "Voting will be very rewarding to your account's progression.",
            "Need help? Ask any staff member or join the 'help' chan channel.",
            "Being part of a clan will make your progress a lot faster!",
            "Donating helps us keep the lights on!"
    };

    /** The left nav bar links. */
    public static final String[] LEFT_NAV_LINKS = {"https://www.nardah.com", "https://www.nardah.com/community", "https://discord.gg/nwnqKYr"};

    /** The right nav bar links. */
    public static final String[] RIGHT_NAV_LINKS = {"https://www.nardah.com/vote", "https://www.nardah.com/store/", "https://www.nardah.com/scores/"};

    /** The IP address client will be connecting to. */
    public static Connection CONNECTION = Connection.DEVELOPMENT;

    /** The current version of the cache. */
    public static final int CACHE_VERSION = 4;

    /** The current version of the client. */
    public static final int CLIENT_VERSION = 6;

    /** The current game version. */
    public static final int GAME_VERSION = CLIENT_VERSION + CACHE_VERSION;

}
