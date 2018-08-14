package com.nardah;

import javax.imageio.ImageIO;

import static com.nardah.ParticleDefinition.RANDOM;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Handles all utility type methods for the client.
 *
 * @author Daniel
 */
public class Utility {

    /**
     * Formats digits for integers.
     */
    public static String formatDigits(final int amount) {
        return NumberFormat.getInstance().format(amount);
    }

    /**
     * Formats digits for longs.
     */
    public static String formatDigits(final long amount) {
        return NumberFormat.getInstance().format(amount);
    }

    /**
     * Formats digits for doubles.
     */
    public static String formatDigits(final double amount) {
        return NumberFormat.getInstance().format(amount);
    }

    /**
     * Formats booleans.
     */
    public static String formatBoolean(final boolean flag) {
        return flag ? "<col=64E838>On" : "<col=ED3E3E>Off";
    }

    /**
     * dGets the prefix of a boolean.
     */
    public static String booleanPrefix(boolean flag) {
        return "" + (flag ? "<col=4DE024>" : "<col=D61E30>");
    }

    /**
     * Formats the player name.
     */
    public static String formatName(final String name) {
        if (name.length() > 0) {
            char first = name.charAt(0);
            StringBuilder fixed = new StringBuilder("" + Character.toUpperCase(first));
            for (int index = 1; index < name.length(); index++) {
                char character = name.charAt(index);
                if (character == '_' || character == ' ') {
                    character = ' ';
                    fixed.append(character);
                    if (index + 1 < name.length() && name.charAt(index + 1) >= 'a' && name.charAt(index + 1) <= 'z') {
                        fixed.append(Character.toUpperCase(name.charAt(index + 1)));
                        index++;
                    }
                } else {
                    fixed.append(character);
                }
            }
            return fixed.toString();
        } else {
            return name;
        }
    }

    public static String getRank(int rank) {
        switch (rank) {
            case 0:
                return "Moderator";
            case 1:
                return "Administrator";
            case 2:
                return "Owner";
            case 3:
                return "Developer";
        }
        return "Player";
    }

    public static String getDropColor(int type) {
        if (!Settings.ITEM_RARITY_COLOR)
            return "<col=FF9040>";
        if (type == 0)
            return "<col=FF9040>";
        if (type == 1)
            return "<col=ed322f>";
        if (type == 2)
            return "<col=1f81dd>";
        return "<col=FF9040>";
    }

    public static int dropColor(int type) {
        if (!Settings.ITEM_RARITY_COLOR)
            return 0xffffff;
        if (type == 0)
            return 0xffffff;
        if (type == 1)
            return 0xed322f;
        if (type == 2)
            return 0x1f81dd;
        return 0xffffff;
    }

    public static int getProgressColor(int percent) {
        if (percent <= 15) {
            return 0x808080;
        }
        if (percent <= 45) {
            return 0x7f7f00;
        }
        if (percent <= 65) {
            return 0x999900;
        }
        if (percent <= 75) {
            return 0xb2b200;
        }
        if (percent <= 90) {
            return 0x007f00;
        }
        return 31744;
    }

    /**
     * Formats booleans.
     */
    public static int getPrefix(final boolean flag) {
        return flag ? 0x64E838 : 0xED3E3E;
    }

    /**
     * Gets the date of server.
     */
    public static String getDate() {
        return new SimpleDateFormat("EE MMM dd yyyy").format(new Date());
    }

    /**
     * Gets the current server time and formats it
     *
     * @return the formatted current server time.
     */
    public static String getTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
        return dateFormat.format(new Date());
    }

    /** Gets the time based off a long. */
    public static String getTime(long period) {
        return new SimpleDateFormat("m:ss").format(System.currentTimeMillis() - period);
    }

    /**
     * Checks if the player is a staff member.
     *
     * @param right The right of the player.
     * @return The player being a staff member.
     */
    public static boolean staff(int right) {
        return right > 0 && right < 5;
    }

    /**
     * Capitalize each letter after .
     */
    public static String capitalizeSentence(final String string) {
        StringBuilder sb = new StringBuilder(string);
        try {
            int pos = 0;
            boolean capitalize = true;
            while (pos < sb.length()) {
                if (sb.charAt(pos) == '.') {
                    capitalize = true;
                } else if (capitalize && !Character.isWhitespace(sb.charAt(pos))) {
                    sb.setCharAt(pos, Character.toUpperCase(sb.charAt(pos)));
                    capitalize = false;
                }
                pos++;
            }
            return sb.toString();
        } finally {
            sb.setLength(0);
            sb = null;
        }
    }

    /**
     * Checks if the player has a valid connection.
     *
     * @return valid connection.
     */
    public static boolean validConnection() {
        try {
            final URL url = new URL("http://www.google.com");
            final URLConnection conn = url.openConnection();
            conn.connect();
            return true;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            return false;
        }
    }

    public static void reporterror(String s) {
        System.err.println("Error: " + s);
    }

    /**
     * A or an
     */
    public static String getAOrAn(String nextWord) {
        String s = "a";
        char c = nextWord.toUpperCase().charAt(0);
        if (c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U') {
            s = "an";
        }
        return s;
    }

    public static String findcachedir() {
        String cacheLoc = System.getProperty("user.home") + "/NR/"; //live

        File cacheDir = new File(cacheLoc);
        if(!cacheDir.exists()) {
            cacheDir.mkdir();
        }
        return cacheLoc;
    }

    public static String secondDir() {
        File file = new File("c:/OSR.Cache/");
        if (!file.exists() && file.mkdir())
            Client.virgin = true;
        return file.toString();
    }

    public static final String intToKOrMilLongName(int i) {
        String s = "" + i;
        for (int k = s.length() - 3; k > 0; k -= 3)
            s = s.substring(0, k) + "," + s.substring(k);
        if (s.length() > 8)
            s = "<col=475154>" + s.substring(0, s.length() - 8) + " million <col=ffffff>(" + s + ")";
        else if (s.length() > 4)
            s = "<col=65535>" + s.substring(0, s.length() - 4) + "K <col=ffffff>(" + s + ")";
        return " " + s;
    }

    public static final String methodR(int j) {
        if (j >= 0 && j < 10000)
            return "" + j;
        if (j >= 10000 && j < 10000000)
            return j / 1000 + "K";
        if (j >= 10000000 && j < 999999999)
            return j / 1000000 + "M";
        if (j >= 999999999)
            return "*";
        else
            return "?";
    }

    public static String getFileNameWithoutExtension(String fileName) {
        File tmpFile = new File(fileName);
        tmpFile.getName();
        int whereDot = tmpFile.getName().lastIndexOf('.');
        if (0 < whereDot && whereDot <= tmpFile.getName().length() - 2) {
            return tmpFile.getName().substring(0, whereDot);
        }
        return "";
    }

    /**
     * Loads an image via website.
     *
     * @param name The name of the website.
     * @return The image.
     */
    public static Image loadURLImage(String name) {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        try {
            img = ImageIO.read(new URL(name));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return img;
    }

    public static void launchURL(String url) {
        String osName = System.getProperty("os.name");
        try {
            if (osName.startsWith("Mac OS")) {
                Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
                Method openURL = fileMgr.getDeclaredMethod("openURL", String.class);
                openURL.invoke(null, url);
            } else if (osName.startsWith("Windows"))
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            else {
                String[] browsers = {"firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape", "safari"};
                String browser = null;
                for (int count = 0; count < browsers.length && browser == null; count++)
                    if (Runtime.getRuntime().exec(new String[]{"which", browsers[count]}).waitFor() == 0)
                        browser = browsers[count];
                if (browser == null) {
                    throw new Exception("Could not find web browser");
                } else
                    Runtime.getRuntime().exec(new String[]{browser, url});
            }
        } catch (Exception e) {
            System.out.println("Failed to open URL.");
        }
    }

    public static String getFormattedTime(int time) {
        int seconds = time / 50;

        if (seconds < 60)
            return "0:" + seconds + "";
        else {
            int mins = seconds / 60;
            int remainderSecs = seconds - (mins * 60);
            if (mins < 60) {
                return mins + ":" + (remainderSecs < 10 ? "0" : "") + remainderSecs + "";
            } else {
                int hours = mins / 60;
                int remainderMins = mins - (hours * 60);
                return (hours < 10 ? "0" : "") + hours + "h " + (remainderMins < 10 ? "0" : "") + remainderMins + "m " + (remainderSecs < 10 ? "0" : "") + remainderSecs + "s";
            }
        }
    }

    public static String[] getTweets(String username, int count) {
        try {
            URL url = new URL("http://api.twitter.com/1/statuses/user_timeline/" + username + ".xml?count=" + count);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String line = "";
            String[] tweets = new String[count];
            int index = 0;
            while ((line = in.readLine()) != null) {
                if (line.contains("<text>")) {
                    if (index < count) {
                        line = line.replace("<text>", "");
                        line = line.replace("</text>", "");
                        line = line.replace("    ", "");
                        tweets[index] = line;
                        index++;
                    }
                }
            }
            in.close();
            if (tweets[0] == null || index == 0) {
                tweets[0] = "There are no tweets!";
            }
            return tweets;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void deleteFile(File directory) {
        if (directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                if (file.getName().equalsIgnoreCase("Character"))
                    continue;
                file.delete();
            }
        }
    }

    public static String getCurrentVersion(String url) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(url)));
            String version = br.readLine();
            br.close();
            return version;
        } catch (Exception e) {
            return "0.1";
        }
    }

    public static String getNewestVersion(String url) {
        try {
            URL tmp = new URL(url);
            BufferedReader br = new BufferedReader(new InputStreamReader(tmp.openStream()));
            return br.readLine();
        } catch (Exception e) {
            return "-1";
        }
    }

    public static int random(int bound) {
        return random(0, bound, false);
    }

    public static int random(int lowerBound, int upperBound) {
        return random(lowerBound, upperBound, false);
    }


    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static int random(int lowerBound, int upperBound, boolean inclusive) {
        if (lowerBound >= upperBound) {
            throw new IllegalArgumentException("The lower bound cannot be larger than or equal to the upper bound!");
        }

        return lowerBound + RANDOM.nextInt(upperBound - lowerBound) + (inclusive ? 1 : 0);
    }


    public static <T> T randomElement(T[] array) {
        return array[(int) (RANDOM.nextDouble() * array.length)];
    }

}
