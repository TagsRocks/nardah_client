package com.nardah;

import java.applet.AppletContext;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.nardah.login.LoginRenderer;
import com.nardah.login.impl.MainScreen;
import com.nardah.updater.UpdateState;
import com.nardah.updater.Updater;

public class Client extends GameApplet {
	
	
	/** Regen skill orbs commit **/
	private final float REGEN_HEALTH_TIME = 60000.0f;
    private final float REGEN_SPEC_TIME = 36000.0f;
    private long regenHealthStart = 0;
    private long regenSpecStart = 0;
    private int lastHp = 0;
    private int lastSpec = 0;
	
	
   /** Achievement shit added **/	
    public RSInterface lastAchievement = null;
    public RSInterface achievementHovered = null;
    public void UpdateAchievementHover() {
    	RSInterface.getInterfaceCache()[37405].disabledMessage = ""+achievementHovered.achievementPercent+"%";
    	float newWidth = 1.05f * (float)achievementHovered.achievementPercent;
    	RSInterface.getInterfaceCache()[37404].disabledSprite.width = (int)newWidth;
    }
	

    final int[] currencyIcons = { 256,257,258,259,260,261,262,263,264,851,852};
    private Sprite[] currencyImage = new Sprite[currencyIcons.length];

    public static ArrayList<Bubble> bubbles = new ArrayList<Bubble>();
    private static final long serialVersionUID = 5707517957054703648L;
    static ScreenMode frameMode = ScreenMode.FIXED;
    private FadingScreen fadingScreen = new FadingScreen();
    private Stopwatch chatDelay = new Stopwatch();
    private static Stopwatch loadingTime;
    private int hoverShopTile = -1;
    private final int crownAmount = 19;
    static long ping;
    public static String server = Configuration.CONNECTION.address;
    private int colorSelected = 0;
    private int colorIndex = 0;
    private int colorViewing = 0;
    private int inputLength = 10;
    private String inputMessage;
    private int playerIndex = 0;
    public int loginTick = 0;
    public int announcementTicks = 0;
    public int announcementFade = 0;
    public int announcementMovement = Client.frameWidth - 2;
    public boolean loginLoaded;
    public AccountData lastAccount;
    private static boolean shiftDown;
    private static boolean removeShiftDropOnMenuOpen;
    KeybindManager keybindManager = new KeybindManager();
    public static final SpriteCache spriteCache = new SpriteCache();
    public static boolean shiftIsDown = false;
    public int itemHover = -1;
    public int lastItemHover = -1;
    public boolean drawOpponentStats = false;
    private Sprite[] specbutton;
    public boolean specActive = false;
    public boolean specButtonHovered = false;


    /**
     * An array containing all the player's experience.
     */
    public static double[] experience = new double[24];
    public static double[] prestige = new double[24];
    private int currentSkill;
    public static long totalExperience;




    public void updateItemHoverInterface(ItemDefinition def) {
        if (itemHover > 0 && def != null) {
            String action = def.itemActions[1] == null ? "Use" : def.itemActions[1];
            /*for (int i = 0; i <= def.itemActions.length; i++) {
                if (def.itemActions[i] != null) {
                    action = def.itemActions[i];
                    continue;
                }
            }*/
            RSInterface.getInterfaceCache()[23002].disabledMessage = "@whi@" + action + " @lre@" + def.name;
            if (ItemStats.itemstats[itemHover] != null) {
                if (ItemStats.itemstats[itemHover].type == 1) {
                    RSInterface.getInterfaceCache()[23006].disabledMessage = "";
                    String bonus = "";
                    for (int i = 0; i < 5; ++i) {
                        if (ItemStats.itemstats[itemHover].attackBonus[i] > 0)
                            bonus = "+";
                        else
                            bonus = "";
                        RSInterface.getInterfaceCache()[23006].disabledMessage += "@whi@"+bonus+""+ItemStats.itemstats[itemHover].attackBonus[i]+"\\n";
                    }
                    RSInterface.getInterfaceCache()[23007].disabledMessage = "";
                    for (int i = 0; i < 5; ++i) {
                        if (ItemStats.itemstats[itemHover].defenceBonus[i] > 0)
                            bonus = "+";
                        else
                            bonus = "";
                        RSInterface.getInterfaceCache()[23007].disabledMessage += "@whi@"+bonus+""+ItemStats.itemstats[itemHover].defenceBonus[i]+"\\n";
                    }
                    RSInterface.getInterfaceCache()[23010].disabledMessage = "@whi@"+ (ItemStats.itemstats[itemHover].strengthBonus > 0 ? "+" : "") +""+ItemStats.itemstats[itemHover].strengthBonus+"\\n@whi@"+ (ItemStats.itemstats[itemHover].prayerBonus > 0 ? "+" : "") +""+ItemStats.itemstats[itemHover].prayerBonus+"";
                    RSInterface.getInterfaceCache()[23021].height = 28;
                    RSInterface.getInterfaceCache()[23021].width = 145;
                    RSInterface.getInterfaceCache()[23020].childX[1] = 80;
                    RSInterface.getInterfaceCache()[23022].disabledMessage = "Press CTRL to view stats";
                    RSInterface.getInterfaceCache()[23022].centerText = false;
                    RSInterface.getInterfaceCache()[23020].childX[2] = 10;
                } else {
                    RSInterface.getInterfaceCache()[23021].height = 28;
                    int width = newSmallFont.getTextWidth(def.name) + newSmallFont.getTextWidth(action) + 8;
                    if (width < 58)
                        width = 58;
                    RSInterface.getInterfaceCache()[23021].width = width;
                    RSInterface.getInterfaceCache()[23020].childX[1] = 8 + (width / 2);
                    RSInterface.getInterfaceCache()[23020].childX[2] = 8 + (width / 2);
                    RSInterface.getInterfaceCache()[23022].disabledMessage = "@whi@Heals: @gre@"+ItemStats.itemstats[itemHover].healAmount;
                    RSInterface.getInterfaceCache()[23022].centerText = true;
                }
            } else {
                RSInterface.getInterfaceCache()[23021].height = 16;
                RSInterface.getInterfaceCache()[23021].width = newSmallFont.getTextWidth(def.name)+ newSmallFont.getTextWidth(action) + 8;
                RSInterface.getInterfaceCache()[23020].childX[1] = 8 + (RSInterface.getInterfaceCache()[23021].width / 2);
                RSInterface.getInterfaceCache()[23022].disabledMessage = "";
            }
        }
    }
    public int getItemHoverX(boolean inventory, int dx, boolean check) {
        int x = super.mouseX - dx;
        int width = RSInterface.getInterfaceCache()[23021].width;
        if (inventory) {
            if (x > 240 - width)
                x = 240 - width;
            if (frameMode != ScreenMode.FIXED) {
                x = super.mouseX;
                if (x + width > getScreenWidth())
                    x = getScreenWidth() - width - 5;
            }
        } else {
            if (frameMode == ScreenMode.FIXED) {
                if (x + width > 460) {
                    x = 460 - width;
                }
            }
        }
        return x;
    }
    public int getItemHoverY(boolean inventory, int dy, boolean check) {
        int y = super.mouseY - dy;
        int height = check ? RSInterface.getInterfaceCache()[23021].height : 145;
        if (inventory) {
            int max = check ? 290 : 180;
            if (y > max)
                y = max;
            if (frameMode != ScreenMode.FIXED) {
                y = super.mouseY;
                if (y + height > getScreenHeight())
                    y = getScreenHeight() - height - 5;
            }
        } else {
            int max = check ? (getScreenHeight() - 240) : (getScreenHeight() - 330);
            if (y > max)
                y = max;
        }
        return y;
    }


    void colorOpen() {
        colorIndex = 1;
        RSInterface.getInterfaceCache()[56710].color = Settings.PRIVATE_MESSAGE;
        RSInterface.getInterfaceCache()[56711].color = Settings.PRIVATE_MESSAGE;
        RSInterface.getInterfaceCache()[56712].color = Settings.PLAYER_TITLE;
        RSInterface.getInterfaceCache()[56713].color = Settings.YELL;
        RSInterface.getInterfaceCache()[56714].color = Rasterizer.fogColor;
        sendString("Manage Private Message Color", 56702);
        sendString("<col=" + Integer.toHexString(Settings.PRIVATE_MESSAGE).substring(2) + ">From " + localPlayer.name + ": Hello my friend.", 56719);
        toggleConfig(836, 0);
        openInterface(56720);
    }

    public void getColorFromCoords(int type) {
        PointerInfo info = MouseInfo.getPointerInfo();
        Point point = info.getLocation();
        try {
            point = MouseInfo.getPointerInfo().getLocation();
            Robot robot = new Robot();
            Color color = robot.getPixelColor((int) point.getX(), (int) point.getY());
            if (type == 1) {
                RSInterface.getInterfaceCache()[56718].color = color.getRGB();
            } else if (type == 2) {
                colorSelected = color.getRGB();
                RSInterface.getInterfaceCache()[56719].color = colorSelected;
                handleColorGrab(color);
            }
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public int yellColor;

    public void handleColorGrab(Color col) {
        switch (openInterfaceID) {
            case 56720: {
                RSInterface.getInterfaceCache()[56710].color = colorSelected;
                RSInterface.getInterfaceCache()[56719].textColor = colorSelected;
                RSInterface.getInterfaceCache()[56719].disabledMessage = "<col=" + Integer.toHexString(colorSelected).substring(2) + ">From " + localPlayer.name + ": Hello my friend.";
                colorViewing = colorSelected;
            }
            break;
            case 56721: {
                Settings.PLAYER_TITLE = colorSelected;
                RSInterface.getInterfaceCache()[56710].color = colorSelected;
                RSInterface.getInterfaceCache()[56719].disabledMessage = "<col=" + Integer.toHexString(Settings.PLAYER_TITLE).substring(2) + ">" + localPlayer.title + " <col=ffffff>" + localPlayer.name;
                colorViewing = colorSelected;
            }
            break;
            case 56722: {
                Settings.YELL = colorSelected;
                RSInterface.getInterfaceCache()[56710].color = colorSelected;
                RSInterface.getInterfaceCache()[56719].textColor = colorSelected;
                RSInterface.getInterfaceCache()[56719].disabledMessage = "<col=" + Integer.toHexString(Settings.YELL).substring(2) + ">[Owner] " + localPlayer.name + "<col=ffffff>: Hey.";
                colorViewing = colorSelected;
            }
            break;
            case 56723: {
                RSInterface.getInterfaceCache()[56710].color = colorSelected;
                RSInterface.getInterfaceCache()[56719].disabledMessage = "";
                colorViewing = colorSelected;
            }
            break;
        }
    }

    public void sendData(int type) {
        switch (type) {

            case 1:
                Settings.PRIVATE_MESSAGE = colorViewing;
                RSInterface.getInterfaceCache()[56711].color = colorSelected;
                break;
            case 2:
                Settings.PLAYER_TITLE = colorViewing;
                RSInterface.getInterfaceCache()[56712].color = colorSelected;
                outgoing.writeOpcode(187);
                outgoing.writeLEShort(0);
                outgoing.writeDWord(Settings.PLAYER_TITLE);
                break;
            case 3:
                Settings.YELL = colorViewing;
                RSInterface.getInterfaceCache()[56713].color = colorSelected;
                outgoing.writeOpcode(187);
                outgoing.writeLEShort(1);
                outgoing.writeDWord(Settings.YELL);
                break;
            case 4:
                Rasterizer.fogColor = colorViewing;
                RSInterface.getInterfaceCache()[56714].color = colorSelected;
                break;

        }
    }

    public enum ScreenMode {
        FIXED,
        RESIZABLE,
        FULLSCREEN
    }

    private int currentGameFrame;

    private final String[] chatTitles;
    private final int[] chatColors;

    private Npc npcDisplay;

    public static int frameWidth = 765;
    public static int frameHeight = 503;
    public static int screenAreaWidth = 512;
    public static int screenAreaHeight = 334;
    public static int cameraZoom = 600;

    public static boolean showChatComponents = true;
    public static boolean showTabComponents = true;
    public static boolean changeTabArea = frameMode == ScreenMode.FIXED ? false : true;
    public static boolean changeChatArea = frameMode == ScreenMode.FIXED ? false : true;
    public static boolean transparentTabArea = false;

    public static boolean virgin;

    private Sprite gameFrameSprite, inventorySprite, chatSprite, mapSprite;

    private GraphicsBuffer gameFrameIP;

    private int modifiableXValue;

    private void prepareGameFrame() {
        setCurrentGameFrame(Settings.GAMEFRAME);
        gameFrameSprite = new Sprite(spriteCache.get(getCurrentGameFrame() == 474 ? 376 : 182));
        gameFrameIP = new GraphicsBuffer(gameFrameSprite.width, gameFrameSprite.height, getGameComponent());

        inventorySprite = new Sprite(gameFrameSprite, 516, 167, 249, 335);
        chatSprite = new Sprite(gameFrameSprite, 0, 338, 519, 165);
        mapSprite = new Sprite(gameFrameSprite, 516, 0, 249, 168);
        compass = new Sprite(spriteCache.get(getCurrentGameFrame() == 474 ? 390 : 388));

        if (frameMode == ScreenMode.FIXED) {
            gameFrameSprite.drawSprite(0, 0);
        }
    }

    int frameValueW = 765, frameValueH = 503;

    private void frameMode(int width, int height) {
        int maxWidth =(int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        int maxHeight =(int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();

        if (width == -1 || height == -1) {
            width = maxWidth;
            height = maxHeight;
        }

        if (width > maxWidth)
            width = maxWidth;
        if (height > maxHeight)
            height = maxHeight;

        if (frameValueW == width && frameValueH == height) {
            return;
        }

        if (width > 765 || height > 503) {
            frameMode = ScreenMode.RESIZABLE;
            frameWidth = width;
            frameHeight = height;
            forceWidth = forceHeight = -1;
            cameraZoom = 850;
            if (clientFrame != null) {
                clientFrame.resize(ScreenMode.RESIZABLE, width, height);
            }
            SceneGraph.viewDistance = 10;
            Settings.GAMEFRAME = 474;
            instance.rebuildFrame(false, width, height,true, false);
        } else {
            frameMode = ScreenMode.FIXED;
            frameWidth = 765;
            frameHeight = 503;
            forceWidth = forceHeight = -1;
            cameraZoom = 600;
            if (clientFrame != null) {
                clientFrame.resize(ScreenMode.FIXED, 765, 503);
            }
            SceneGraph.viewDistance = 9;
            Settings.GAMEFRAME = 474;
            instance.rebuildFrame(false, 765, 503, false, false);
            /**
             * Reset the settings
             */
            if (Client.changeChatArea) {
                Client.changeChatArea = false;
                toggleConfig(882, 0);
            }

            if (Client.transparentTabArea) {
                Client.transparentTabArea = false;
                toggleConfig(881, 0);
            }

            if (Client.changeTabArea) {
                Client.changeTabArea = false;
                toggleConfig(883, 0);
            }
        }

        frameValueW = width;
        frameValueH = height;
        updateScreen();
        showChatComponents = frameMode == ScreenMode.FIXED ? true : showChatComponents;
        showTabComponents = frameMode == ScreenMode.FIXED ? true : showTabComponents;
    }

    private void frameMode(ScreenMode screenMode) {
        int width = 765;
        int height = 503;
        if (frameMode != screenMode) {
            frameMode = screenMode;
            if (screenMode == ScreenMode.FIXED) {
                frameWidth = forceWidth = width;
                frameHeight = forceHeight = height;
                cameraZoom = 600;
                if (clientFrame != null) {
                    clientFrame.resize(screenMode, width, height);
                }
                SceneGraph.viewDistance = 9;
                changeChatArea = false;
                changeTabArea = false;
                instance.rebuildFrame(screenMode == ScreenMode.FULLSCREEN, width, height, screenMode == ScreenMode.RESIZABLE, screenMode == ScreenMode.FULLSCREEN);
            } else if (screenMode == ScreenMode.RESIZABLE) {
                width = 766;
                height = 559;
                frameWidth = width;
                frameHeight = height;
                forceWidth = forceHeight = -1;
                cameraZoom = 850;
                if (clientFrame != null) {
                    clientFrame.resize(screenMode, width, height);
                }
                SceneGraph.viewDistance = 10;
                Settings.GAMEFRAME = 474;
                instance.rebuildFrame(screenMode == ScreenMode.FULLSCREEN, width, height, screenMode == ScreenMode.RESIZABLE, screenMode == ScreenMode.FULLSCREEN);
            } else if (screenMode == ScreenMode.FULLSCREEN) {
                width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
                height = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
                cameraZoom = 400;
                if (clientFrame != null) {
                    clientFrame.resize(screenMode, width, height);
                }
                SceneGraph.viewDistance = 10;
                frameWidth = super.myWidth = width;
                frameHeight = super.myHeight = height;
                forceWidth = forceHeight = -1;
                Settings.GAMEFRAME = 474;
                instance.rebuildFrame(true, width, height, false, true);
            }
            updateScreen();
        }
        showChatComponents = screenMode == ScreenMode.FIXED ? true : showChatComponents;
        showTabComponents = screenMode == ScreenMode.FIXED ? true : showTabComponents;
    }

    private Stopwatch frameDelay = new Stopwatch();

    public static void uLinkNodes() {
        NpcDefinition.modelCache.unlinkAll();
        ItemDefinition.mruNodes2.unlinkAll();
        ItemDefinition.mruNodes1.unlinkAll();
        Player.mruNodes.unlinkAll();
    }

    private void updateScreen() {
        if (loggedIn && frameDelay.hasElapsed(2, TimeUnit.SECONDS)) {
            if (frameValueW != frameWidth || frameValueH != frameHeight) {
                frameValueW = frameWidth;
                frameValueH = frameHeight;
                outgoing.writeOpcode(150);
                outgoing.writeShort(-1);
                outgoing.writeShort(frameValueW);
                outgoing.writeShort(frameValueH);
            }
            frameDelay.reset();
        }

        screenAreaWidth = frameMode == ScreenMode.FIXED ? 512 : frameWidth;
        screenAreaHeight = frameMode == ScreenMode.FIXED ? 334 : frameHeight;
        if (gameScreenIP == null || gameScreenIP.width != screenAreaWidth || gameScreenIP.height != screenAreaHeight) {
            gameScreenIP = new GraphicsBuffer(screenAreaWidth, screenAreaHeight, getGameComponent());
        }
        setBounds();
    }

    private void refreshFrameSize() {
        if (frameWidth != (appletClient() ? getGameComponent().getWidth() : getScreenWidth())) {
            frameWidth = (appletClient() ? getGameComponent().getWidth() : getScreenWidth());
            screenAreaWidth = super.myWidth = frameWidth;
            updateScreen();
        }
        if (frameHeight != (appletClient() ? getGameComponent().getHeight() : getScreenHeight())) {
            frameHeight = (appletClient() ? getGameComponent().getHeight() : getScreenHeight());
            screenAreaHeight = super.myHeight = frameHeight;
            updateScreen();
        }
    }

    private void setBounds() {
        Rasterizer.method365(frameWidth, frameHeight);
        fullScreenTextureArray = Rasterizer.anIntArray1472;
        Rasterizer.method365(frameMode == ScreenMode.FIXED ? (chatIP != null ? chatIP.width : 519) : frameWidth, frameMode == ScreenMode.FIXED ? (chatIP != null ? chatIP.height : 165) : frameHeight);
        anIntArray1180 = Rasterizer.anIntArray1472;
        Rasterizer.method365(frameMode == ScreenMode.FIXED ? (inventoryIP != null ? inventoryIP.width : 249) : frameWidth, frameMode == ScreenMode.FIXED ? (inventoryIP != null ? inventoryIP.height : 335) : frameHeight);
        anIntArray1181 = Rasterizer.anIntArray1472;
        Rasterizer.method365(screenAreaWidth, screenAreaHeight);
        anIntArray1182 = Rasterizer.anIntArray1472;
        int ai[] = new int[9];
        for (int i8 = 0; i8 < 9; i8++) {
            int k8 = 128 + i8 * 32 + 15;
            int l8 = 600 + k8 * 3;
            int i9 = Rasterizer.anIntArray1470[k8];
            ai[i8] = l8 * i9 >> 16;
        }
        if (frameMode == ScreenMode.RESIZABLE && (frameWidth >= 760) && (frameWidth <= 1025) && (frameHeight >= 504) && (frameHeight <= 850)) {
            SceneGraph.viewDistance = 9;
            cameraZoom = 700;
        } else if (frameMode == ScreenMode.FIXED) {
            cameraZoom = 600;
        } else if (frameMode == ScreenMode.RESIZABLE && frameWidth >= 1025 && frameHeight >= 851 || frameMode == ScreenMode.FULLSCREEN) {
            SceneGraph.viewDistance = 10;
            cameraZoom = 500;
        }
        SceneGraph.method310(500, 800, screenAreaWidth, screenAreaHeight, ai);
        if (loggedIn) {
            gameScreenIP = new GraphicsBuffer(screenAreaWidth, screenAreaHeight, getGameComponent());
        }
    }

    public boolean getMousePositions() {
        if (mouseInRegion(frameWidth - (frameWidth <= 1000 ? 240 : 420), frameHeight - (frameWidth <= 1000 ? 90 : 37), frameWidth, frameHeight)) {
            return false;
        }
        if (showChatComponents) {
            if (changeChatArea) {
                if (super.mouseX > 0 && super.mouseX < 494 && super.mouseY > frameHeight - 175 && super.mouseY < frameHeight) {
                    return chatStateCheck() ? false : true;
                } else {
                    if (super.mouseX > 494 && super.mouseX < 515 && super.mouseY > frameHeight - 175 && super.mouseY < frameHeight) {
                        return false;
                    }
                }
            } else if (!changeChatArea) {
                if (super.mouseX > 0 && super.mouseX < 519 && super.mouseY > frameHeight - 175 && super.mouseY < frameHeight) {
                    return false;
                }
            }
        }
        if (mouseInRegion(frameWidth - 216, 0, frameWidth, 172)) {
            return false;
        }
        if (!changeTabArea) {
            if (super.mouseX > 0 && super.mouseY > 0 && super.mouseY < frameWidth && super.mouseY < frameHeight) {
                if (super.mouseX >= frameWidth - 242 && super.mouseY >= frameHeight - 335) {
                    return false;
                }
                return true;
            }
            return false;
        }
        if (showTabComponents) {
            if (frameWidth > 1000) {
                if (super.mouseX >= frameWidth - 420 && super.mouseX <= frameWidth && super.mouseY >= frameHeight - 37 && super.mouseY <= frameHeight || super.mouseX > frameWidth - 225 && super.mouseX < frameWidth && super.mouseY > frameHeight - 37 - 274 && super.mouseY < frameHeight) {
                    return false;
                }
            } else {
                if (super.mouseX >= frameWidth - 210 && super.mouseX <= frameWidth && super.mouseY >= frameHeight - 74 && super.mouseY <= frameHeight || super.mouseX > frameWidth - 225 && super.mouseX < frameWidth && super.mouseY > frameHeight - 74 - 274 && super.mouseY < frameHeight) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean mouseInRegion(int x1, int y1, int x2, int y2) {
        if (super.mouseX >= x1 && super.mouseX <= x2 && super.mouseY >= y1 && super.mouseY <= y2) {
            return true;
        }
        return false;
    }
    public boolean mouseInRegion2(int x, int y, int width, int height) {
        if (super.mouseX >= x && super.mouseX <= x + width && super.mouseY >= y && super.mouseY <= y + height) {
            return true;
        }
        return false;
    }

    public boolean mouseMapPosition() {
        if (super.mouseX >= frameWidth - 21 && super.mouseX <= frameWidth && super.mouseY >= 0 && super.mouseY <= 21) {
            return false;
        }
        return true;
    }

    private void drawInputField(RSInterface child, int interfaceX, int interfaceY, int x, int y, int width, int height) {
        int clickX = super.lastClickX, clickY = super.lastClickY;
        if (frameMode == ScreenMode.FIXED) {
            if (clickX >= 512 && clickY >= 169) {
                clickX -= 512;
                clickY -= 169;
            }
        }
        for (int row = 0; row < width; row += 12) {
            if (row + 12 > width) {
                row -= 12 - (width - row);
            }
            Raster.fillRectangle(x + row, y, 12, 12, 0x363227);
            for (int collumn = 0; collumn < height; collumn += 12) {
                if (collumn + 12 > height) {
                    collumn -= 12 - (height - collumn);
                }
                Raster.fillRectangle(x + row, y + collumn, 12, 12, 0x363227);
            }
        }
        for (int top = 0; top < width; top += 8) {
            if (top + 8 > width) {
                top -= 8 - (width - top);
            }
            Raster.drawHorizontalLine(x + top, y, 8, 0);
            Raster.drawHorizontalLine(x + top, y + height - 1, 8, 0);
        }
        for (int bottom = 0; bottom < height; bottom += 8) {
            if (bottom + 8 > height) {
                bottom -= 8 - (height - bottom);
            }
            Raster.drawVerticalLine(x, y + bottom, 8, 0);
            Raster.drawVerticalLine(x + width - 1, y + bottom, 8, 0);
        }
        String message = child.disabledMessage;
        if (smallFont.getTextWidth(message) > child.width - 10) {
            message = message.substring(message.length() - (child.width / 10) - 1);
        }
        if (child.displayAsterisks) {
            smallFont.drawText(true, (x + 4), child.textColor, "" + StringUtils.toAsterisks(message) + (((!child.isInFocus ? 0 : 1) & (tick % 40 < 20 ? 1 : 0)) != 0 ? "|" : ""), (y + (height / 2) + 6));
        } else {
            smallFont.drawText(true, (x + 4), child.textColor, "" + message + (((!child.isInFocus ? 0 : 1) & (tick % 40 < 20 ? 1 : 0)) != 0 ? "|" : ""), (y + (height / 2) + 6));
        }
        if (clickX >= x && clickX <= x + child.width && clickY >= y && clickY <= y + child.height) {
            if (!child.isInFocus && getInputFieldFocusOwner() != child) {
                if ((super.clickMode2 == 1 && !menuOpen)) {
                    RSInterface.currentInputFieldId = child.interfaceId;
                    setInputFieldFocusOwner(child);
                    if (child.disabledMessage != null && child.disabledMessage.equals(child.defaultInputFieldText)) {
                        child.disabledMessage = "";
                    }
                    if (child.disabledMessage == null) {
                        child.disabledMessage = "";
                    }
                }
            }
        }
    }

    private void drawProgressBar(RSInterface child, int x, int y) {
        int textColor = 0xFFFFFF;
        String percent = (int) child.percentage + "%";
        if (child.percentage > 100)
            child.percentage = 100;

        if (child.percentage >= 0 && child.percentage <= 33) {
            fadingToColor = 0xD4222E;
        } else if (child.percentage >= 34 && child.percentage <= 66) {
            fadingToColor = 0xF5793B;
        } else if (child.percentage >= 67 && child.percentage <= 99) {
            fadingToColor = 0x53D462;
        } else {
            fadingToColor = 0x0FD426;
        }

        if (!switchColor) {
            if (percentageColor != fadingToColor)
                switchColor = true;
        }

        if (switchColor) {
            steps++;
            if (steps >= 100) {
                steps = 1;
                switchColor = false;
                percentageColor = fadingToColor;
            } else {
                percentageColor = fadeColors(new Color(percentageColor), new Color(fadingToColor), steps);
            }
        }

        Raster.fillRectangle(x, y, child.width, child.height, 0x352C23);
        Raster.fillRectangle(x, y, (int) ((child.width) * .01 * child.percentage), child.height, percentageColor);
        Raster.drawRectangle(x, y, child.width, child.height, 0x16110A);

        if (child.disabledMessage != null) {
            String message = child.disabledMessage.replace("%", percent);

            if (child.interfaceId == 43700) {
                newSmallFont.drawCenteredString(message, x + (child.width / 2) - 2, y + 21, textColor, 0);
            } else {
                newSmallFont.drawCenteredString(percent, ((child.width + newRegularFont.getTextWidth(percent)) / 2) + 35, y + 15, 0xFFFFFF, 0);
            }
        }
    }

    public void setInputFieldFocusOwner(RSInterface owner) {
        for (RSInterface rsi : RSInterface.getInterfaceCache())
            if (rsi != null)
                if (rsi == owner)
                    rsi.isInFocus = true;
                else
                    rsi.isInFocus = false;
    }

    public RSInterface getInputFieldFocusOwner() {
        for (RSInterface rsi : RSInterface.getInterfaceCache())
            if (rsi != null)
                if (rsi.isInFocus)
                    return rsi;
        return null;
    }

    public boolean isFieldInFocus() {
        for (RSInterface rsi : RSInterface.getInterfaceCache())
            if (rsi != null)
                if (rsi.type == 16 && rsi.isInFocus)
                    return true;
        return false;
    }

    public void resetInputFieldFocus() {
        for (RSInterface rsi : RSInterface.getInterfaceCache())
            if (rsi != null)
                rsi.isInFocus = false;
        RSInterface.currentInputFieldId = -1;
    }

    public static final byte[] ReadFile(String s) {
        try {
            byte abyte0[];
            File file = new File(s);
            int i = (int) file.length();
            abyte0 = new byte[i];
            DataInputStream datainputstream = new DataInputStream(new BufferedInputStream(new FileInputStream(s)));
            datainputstream.readFully(abyte0, 0, i);
            datainputstream.close();
            return abyte0;
        } catch (Exception e) {
            System.out.println((new StringBuilder()).append("Read Error: ").append(s).toString());
            return null;
        }
    }

    private boolean menuHasAddFriend(int j) {
        if (j < 0)
            return false;
        int k = menuActionID[j];
        if (k >= 2000)
            k -= 2000;
        return k == 337;
    }

    public void drawChannelButtons() {
        int yOffset = frameMode == ScreenMode.FIXED ? 0 : frameHeight - 165;
        int frame = getCurrentGameFrame();
        switch (frame) {

            case 459:
                smallFont.drawText(true, 25, 0xffffff, "Public chat", 142 + yOffset);
                smallFont.drawText(true, 150, 0xffffff, "Private chat", 142 + yOffset);
                smallFont.drawText(true, 285, 0xffffff, "Trade/compete", 142 + yOffset);
                smallFont.drawText(true, 422, 0xffffff, "Open website", 147 + yOffset);
                smallFont.drawCenteredText(textColor[publicChatMode], 55, text[publicChatMode], 153 + yOffset, true);
                smallFont.drawCenteredText(textColor[privateChatMode], 180, text[privateChatMode], 153 + yOffset, true);
                smallFont.drawCenteredText(textColor[tradeMode], 320, text[tradeMode], 153 + yOffset, true);
                break;

            default:
                spriteCache.get(49).drawSprite(0, 143 + yOffset);
                switch (cButtonCPos) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                        spriteCache.get(16).drawSprite(channelButtonsX[cButtonCPos], 143 + yOffset);
                        break;
                }
                if (cButtonHPos == cButtonCPos) {
                    switch (cButtonHPos) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                            spriteCache.get(17).drawSprite(channelButtonsX[cButtonHPos], 143 + yOffset);
                            break;
                    }
                } else {
                    switch (cButtonHPos) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                            spriteCache.get(15).drawSprite(channelButtonsX[cButtonHPos], 143 + yOffset);
                            break;
                        case 6:
                            spriteCache.get(18).drawSprite(channelButtonsX[cButtonHPos], 143 + yOffset);
                            break;
                    }
                }
                for (int index = 0; index < modeNamesX.length; index++) {
                    smallFont.drawText(true, modeNamesX[index], 0xffffff, modeNames[index], modeNamesY[index] + yOffset);
                }
                smallFont.drawCenteredText(textColor[publicChatMode], 164, text[publicChatMode], 164 + yOffset, true);
                smallFont.drawCenteredText(textColor[privateChatMode], 230, text[privateChatMode], 164 + yOffset, true);
                smallFont.drawCenteredText(textColor[clanChatMode], 296, text[clanChatMode], 164 + yOffset, true);
                smallFont.drawCenteredText(textColor[tradeMode], 362, text[tradeMode], 164 + yOffset, true);
                break;
        }

    }

    private boolean chatStateCheck() {
        return messagePromptRaised || inputDialogState != 0 || clickToContinueString != null || backDialogueId != -1 || dialogueId != -1;
    }

    private void drawChatArea() {
        int yOffset = frameMode == ScreenMode.FIXED ? 0 : frameHeight - 165;

        int frame = getCurrentGameFrame();

        if (frameMode == ScreenMode.FIXED) {
            chatIP.initDrawingArea();
        }
        Rasterizer.anIntArray1472 = anIntArray1180;

        if (chatStateCheck()) {
            showChatComponents = true;
            chatSprite.drawSprite(0, yOffset);
        }

        if (showChatComponents) {
            if (changeChatArea && !chatStateCheck()) {
                Raster.drawVerticalGradient(7, 7 + yOffset, 512, 135, 0x000000, 0x5A000000);
            } else {
                chatSprite.drawSprite(0, yOffset);
            }
        }

        if (!showChatComponents || changeChatArea) {
            Raster.fillRectangle(7, frameHeight - 23, 512, 24, 0, 150);
        }

        drawChannelButtons();

        TextDrawingArea tda = regularText;

        if (super.lastClickX >= 0 && super.lastClickX <= 522 && super.lastClickY >= (frameMode == ScreenMode.FIXED ? 343 : frameHeight - 165) && super.lastClickY <= (frameMode == ScreenMode.FIXED ? 484 : frameHeight - 27)) {
            if (this.isFieldInFocus()) {
                inputString = "";
                this.resetInputFieldFocus();
            }
        }

        if (this.isFieldInFocus()) {
            inputString = "[Click chat box to enable]";
        }

        switch (frame) {

            case 459:
                break;

            default:
                if (messagePromptRaised) {
                    newBoldFont.drawCenteredString(aString1121, 259, 60 + yOffset, 0, -1);
                    newBoldFont.drawCenteredString(promptInput + "*", 259, 80 + yOffset, 128, -1);
                } else if (inputDialogState == 1) {
                    newBoldFont.drawCenteredString(inputMessage, 259, yOffset + 60, 0, -1);
                    newBoldFont.drawCenteredString(amountOrNameInput + "*", 259, 80 + yOffset, 128, -1);
                } else if (inputDialogState == 2) {
                    newBoldFont.drawCenteredString(inputMessage, 259, 60 + yOffset, 0, -1);
                    newBoldFont.drawCenteredString(amountOrNameInput + "*", 259, 80 + yOffset, 128, -1);
                } else if (clickToContinueString != null) {
                    newBoldFont.drawCenteredString(clickToContinueString, 259, 60 + yOffset, 0, -1);
                    newBoldFont.drawCenteredString("Click to continue", 259, 80 + yOffset, 128, -1);
                } else if (backDialogueId != -1) {
                    drawInterface(RSInterface.getInterfaceCache()[backDialogueId], 20, 20 + yOffset, 0);
                } else if (dialogueId != -1) {
                    drawInterface(RSInterface.getInterfaceCache()[dialogueId], 20, 20 + yOffset, 0);
                } else if (showChatComponents) {
                    int playerChatRows = -3;
                    int totalMessages = 0;
                    int shadow = changeChatArea ? 0 : -1;
                    Raster.setDrawingArea(8, 7 + yOffset, 497, 122 + yOffset);
                    for (int index = 0; index < 500; index++) {
                        if (chatMessages[index] != null) {
                            int chatType = chatTypes[index];
                            int yPos = (70 - playerChatRows * 14) + anInt1089 + 5;

                            if (140 + yOffset < yPos + yOffset || yPos + yOffset < yOffset) {
                                totalMessages++;
                                playerChatRows++;
                                continue;
                            }

                            String username = chatNames[index];
                            // String timeStamp = "";//Utility.getTime();
                            byte rights = chatPrivilages[index];
                            if (chatType == 0) {
                                if (chatTypeView == 5 || chatTypeView == 0) {
                                    newRegularFont.drawBasicString(chatMessages[index], 11, yPos + yOffset, changeChatArea ? 0xFFFFFF : 0, shadow);
                                    totalMessages++;
                                    playerChatRows++;
                                }
                            } else

                                // redo for 459
                                if ((chatType == 1 || chatType == 2) && (chatType == 1 || publicChatMode == 0 || publicChatMode == 1 && isFriendOrSelf(username))) {
                                    if (chatTypeView == 1 || chatTypeView == 0) {
                                        int xPos = 11;

                                        if (rights >= 1) {
                                            modIcons[rights - 1].drawSprite(xPos + 1, yPos - 15 + yOffset);
                                            xPos += 14;
                                        }
                                        newRegularFont.drawBasicString(chatCachedNames[index] + ":", xPos, yPos + yOffset, changeChatArea ? 0xFFFFFF : 0, shadow);
                                        xPos += newRegularFont.getTextWidth(chatCachedNames[index]) + 12;
                                        newRegularFont.drawBasicString(chatMessages[index], xPos - 8, yPos + yOffset, changeChatArea ? 0x7FA9FF : 255, shadow);
                                        totalMessages++;
                                        playerChatRows++;
                                    }
                                } else if ((chatType == 3 || chatType == 7) && (splitPrivateChat == 0 || chatTypeView == 2) && (chatType == 7 || privateChatMode == 0 || privateChatMode == 1 && isFriendOrSelf(username))) {
                                    if (chatTypeView == 2 || chatTypeView == 0) {
                                        int k1 = 11;
                                        newRegularFont.drawBasicString("From", k1, yPos + yOffset, changeChatArea ? 0 : 0xFFFFFF, shadow);
                                        k1 += tda.getTextWidth("From ");

                                        if (rights >= 1 && rights != 10) {
                                            modIcons[rights - 1].drawSprite(k1 + (rights != 10 ? 0 : -2), yPos - (rights != 10 ? 12 : 14) + yOffset);
                                            k1 += 12;
                                        }
                                        newRegularFont.drawBasicString(username + ":", k1, yPos + yOffset, changeChatArea ? 0xFFFFFF : 0, shadow);
                                        k1 += tda.getTextWidth(username) + 8;
                                        newRegularFont.drawBasicString(chatMessages[index], k1, yPos + yOffset, 0x800080, shadow);
                                        totalMessages++;
                                        playerChatRows++;
                                    }
                                } else if (chatType == 4 && (tradeMode == 0 || tradeMode == 1 && isFriendOrSelf(username))) {
                                    if (chatTypeView == 3 || chatTypeView == 0) {
                                        newRegularFont.drawBasicString(username + " " + chatMessages[index], 11, yPos + yOffset, 0x800080, shadow);
                                        totalMessages++;
                                        playerChatRows++;
                                    }
                                } else if (chatType == 5 && splitPrivateChat == 0 && privateChatMode < 2) {
                                    if (chatTypeView == 2 || chatTypeView == 0) {
                                        newRegularFont.drawBasicString(username + " " + chatMessages[index], 11, yPos + yOffset, 0x800080, shadow);
                                        totalMessages++;
                                        playerChatRows++;
                                    }
                                } else if (chatType == 6 && (splitPrivateChat == 0 || chatTypeView == 2) && privateChatMode < 2) {
                                    if (chatTypeView == 2 || chatTypeView == 0) {
                                        newRegularFont.drawBasicString("To " + username + ":", 11, yPos + yOffset, changeChatArea ? 0xFFFFFF : 0, shadow);
                                        newRegularFont.drawBasicString(chatMessages[index], 15 + tda.getTextWidth("To :" + username), yPos + yOffset, 0x800080, shadow);
                                        totalMessages++;
                                        playerChatRows++;
                                    }
                                } else if (chatType == 8 && (tradeMode == 0 || tradeMode == 1 && isFriendOrSelf(username))) {
                                    if (chatTypeView == 3 || chatTypeView == 0) {
                                        newRegularFont.drawBasicString(username + " " + chatMessages[index], 11, yPos + yOffset, 0x7e3200, shadow);
                                        totalMessages++;
                                        playerChatRows++;
                                    }
                                    if (chatType == 11 && (clanChatMode == 0)) {
                                        if (chatTypeView == 11) {
                                            newRegularFont.drawBasicString(username + " " + chatMessages[index], 11, yPos + yOffset, 0x7e3200, shadow);
                                            totalMessages++;
                                            playerChatRows++;
                                        }
                                        if (chatType == 12) {
                                            newRegularFont.drawBasicString(chatMessages[index] + "", 11, yPos + yOffset, 0x7e3200, shadow);
                                            totalMessages++;
                                        }
                                    }
                                } else if (chatType == 16) {
                                    int j2 = 40;
                                    int clanNameWidth = tda.getTextWidth(clanname);
                                    if (chatTypeView == 11 || chatTypeView == 0) {
                                        j2 += clanNameWidth;
                                        if (chatRights[index] > 0 && chatRights[index] < clanIcons.length) {
                                            clanIcons[chatRights[index]].drawSprite(j2 - 18, yPos - 10 + yOffset);
                                            j2 += clanIcons[chatRights[index]].width;
                                        }
                                        newRegularFont.drawBasicString("[", 8, yPos + yOffset, changeChatArea ? 0xFFFFFF : 0, shadow);
                                        newRegularFont.drawBasicString(clanname, 14, yPos + yOffset, changeChatArea ? 0x7FA9FF : 255, shadow);
                                        newRegularFont.drawBasicString("]", clanNameWidth + 14, yPos + yOffset, changeChatArea ? 0xFFFFFF : 0, shadow);
                                        if (chatNames[index] != null && !chatNames[index].isEmpty()) {
                                            newRegularFont.drawBasicString(chatNames[index] + ":", j2 - 17, yPos + yOffset, changeChatArea ? 0xFFFFFF : 0, shadow);
                                            j2 += tda.getTextWidth(chatNames[index]) + 7;
                                        }
                                        newRegularFont.drawBasicString(chatMessages[index], j2 - 16, yPos + yOffset, 0x800000, shadow);
                                        totalMessages++;
                                        playerChatRows++;
                                    }
                                }
                        }
                    }

                    Raster.defaultDrawingAreaSize();

                    anInt1211 = totalMessages * 14 + 7 + 5;
                    if (anInt1211 < 111) {
                        anInt1211 = 111;
                    }

                    drawScrollbar(114, anInt1211 - anInt1089 - 113, 7 + yOffset, 496, anInt1211, changeChatArea);
                    String name;
                    if (localPlayer != null && localPlayer.name != null) {
                        name = localPlayer.cachedName;
                    } else {
                        name = "";
                    }
                    Raster.setDrawingArea(8, 110 + yOffset, 509, 140 + yOffset);
                    int xPos = 0;
                    int yPos = 1;
                    if (myPrivilege == 0) {
                        newRegularFont.drawBasicString(name, 6, 137 + yOffset - 2, changeChatArea ? 0xFFFFFF : 0, changeChatArea ? 0 : -1);
                        tda.render(changeChatArea ? 0xFFFFFF : 0, ": ", 137 + yOffset - 2, 24 - 16 + tda.getTextWidth(name) - 2);
                        newRegularFont.drawBasicString(inputString + "*", 22 - 16 - 2 + tda.getTextWidth(name + ": "), 137 + yOffset - 2, changeChatArea ? 0x7FA9FF : 255, changeChatArea ? 0 : -1);
                    } else if (myPrivilege >= 1) {
                        Sprite icon = modIcons[myPrivilege - 1];
                        icon.drawSprite(10 + xPos, 121 + yPos + yOffset);
                        newRegularFont.drawBasicString(name + ":", 8 + 11, 137 + yOffset - 2, changeChatArea ? 0xFFFFFF : 0, changeChatArea ? 0 : -1);
                        newRegularFont.drawBasicString(inputString + "*", 23 - 16 + tda.getTextWidth(name + ": ") + 13, 137 + yOffset - 2, changeChatArea ? 0x7FA9FF : 255, changeChatArea ? 0 : -1);
                    }
                    Raster.defaultDrawingAreaSize();
                    for (int i = 0; i < 505; i++) {
                        int opacity = 100 - (int) (i / 5.05);
                        Raster.drawHorizontalLine(7 + i, 121 + yOffset, 1, 0, opacity + 5);
                        if (frameMode != ScreenMode.FIXED && changeChatArea) {
                            Raster.drawHorizontalLine(7 + i, 6 + yOffset, 1, 0, opacity + 5);
                        }
                    }
                }

                break;

        }

        // TODO finish 459
        if (drawOpponentStats && Settings.DRAW_OPPONENT_STATS) {
            drawInterface(RSInterface.getInterfaceCache()[23050], 366, 7 + yOffset, 0);
        }
        if (menuOpen) {
            drawMenu(0, frameMode == ScreenMode.FIXED ? 338 : 0);
        }

        if (frameMode == ScreenMode.FIXED) {
            // allows text to be shown while typing
            chatIP.drawGraphics(super.graphics, 0, 338);
        }

        // allows text to be shown on the game screen
        gameScreenIP.initDrawingArea();
        Rasterizer.anIntArray1472 = anIntArray1182;
    }

    public void startRunnable(Runnable runnable, int i) {
        if (i > 10) {
            i = 10;
        }
        super.startRunnable(runnable, i);
    }

    public Socket openSocket(int port) throws IOException {
        return new Socket(server, port);
    }

    private void processMenuClick() {
        if (activeInterfaceType != 0)
            return;
        int clickType = super.lastMetaModifier;
        if (spellSelected == 1 && super.lastClickX >= 516 && super.lastClickY >= 160 && super.lastClickX <= 765 && super.lastClickY <= 205)
            clickType = 0;
        if (menuOpen) {
            if (clickType != 1) {
                int k = super.mouseX;
                int j1 = super.mouseY;
                if (menuScreenArea == 0) {
                    k -= 4;
                    j1 -= 4;
                }
                if (menuScreenArea == 1) {
                    k -= 519;
                    j1 -= 168;
                }
                if (menuScreenArea == 2) {
                    k -= 17;
                    j1 -= 338;
                }
                if (menuScreenArea == 3) {
                    k -= 519;
                    j1 -= 0;
                }
                if (k < menuOffsetX - 10 || k > menuOffsetX + menuWidth + 10 || j1 < menuOffsetY - 10 || j1 > menuOffsetY + menuHeight + 10) {
                    menuOpen = false;
                    if (menuScreenArea == 1) {
                    }
                    if (menuScreenArea == 2)
                        redrawDialogueBox = true;
                }
            }
            if (clickType == 1) {
                int l = menuOffsetX;
                int k1 = menuOffsetY;
                int i2 = menuWidth;
                int k2 = super.lastClickX;
                int l2 = super.lastClickY;
                switch (menuScreenArea) {
                    case 0:
                        k2 -= 4;
                        l2 -= 4;
                        break;
                    case 1:
                        k2 -= 519;
                        l2 -= 168;
                        break;
                    case 2:
                        k2 -= 5;
                        l2 -= 338;
                        break;
                    case 3:
                        k2 -= 519;
                        l2 -= 0;
                        break;
                }
                int i3 = -1;
                for (int j3 = 0; j3 < menuActionRow; j3++) {
                    int k3 = k1 + 31 + (menuActionRow - 1 - j3) * 15;
                    if (k2 > l && k2 < l + i2 && l2 > k3 - 13 && l2 < k3 + 3)
                        i3 = j3;
                }
                if (i3 != -1)
                    processMenuActions(i3);
                menuOpen = false;
                if (menuScreenArea == 1) {
                }
                if (menuScreenArea == 2) {
                    redrawDialogueBox = true;
                }
            }
        } else {
            if (clickType == 1 && menuActionRow > 0) {
                int i1 = menuActionID[menuActionRow - 1];
                if (i1 == 632 || i1 == 78 || i1 == 867 || i1 == 431 || i1 == 53 || i1 == 74 || i1 == 454 || i1 == 539 || i1 == 493 || i1 == 847 || i1 == 447 || i1 == 1125) {
                    int l1 = menuActionCmd2[menuActionRow - 1];
                    int j2 = menuActionCmd3[menuActionRow - 1];
                    RSInterface class9 = RSInterface.getInterfaceCache()[j2];

                    if (Settings.SHIFT_DROP && shiftIsDown){
                        boolean hasDrop = false;
                        int dropAction = 0;
                        for (int i = 0; i < menuActionName.length; i++){
                            if (menuActionName[i] == null) {
                                continue;
                            }
                            if (menuActionName[i].contains("Drop") || menuActionName[i].contains("Destroy") || menuActionName[i].contains("Release")) {
                                hasDrop = true;
                                dropAction = i;
                            }
                        }
                        if (hasDrop) {
                            processMenuActions(dropAction);
                            return;
                        }
                    }


                    if (class9.aBoolean259 || class9.aBoolean235) {
                        aBoolean1242 = false;
                        dragCycle = 0;
                        focusedDragWidget = j2;
                        dragFromSlot = l1;
                        activeInterfaceType = 2;
                        pressX = super.lastClickX;
                        pressY = super.lastClickY;
                        if (RSInterface.getInterfaceCache()[j2].parentID == openInterfaceID)
                            activeInterfaceType = 1;
                        if (RSInterface.getInterfaceCache()[j2].parentID == backDialogueId)
                            activeInterfaceType = 3;
                        return;
                    }
                }
            }
            if (clickType == 1 && (anInt1253 == 1 || menuHasAddFriend(menuActionRow - 1)) && menuActionRow > 2) {
                clickType = 2;
            }
            if (clickType == 1 && menuActionRow > 0) {
                processMenuActions(menuActionRow - 1);
            }
            if (clickType == 2 && menuActionRow > 0) {
                determineMenuSize();
            }
            processMainScreenClick();
            processTabClick();
            processChatModeClick();

            minimapHovers();
        }
    }

    private void method22() {
        try {
            anInt985 = -1;
            aClass19_1056.removeAll();
            aClass19_1013.removeAll();
            Rasterizer.method366();
            unlinkMRUNodes();
            worldController.initToNull();
            System.gc();
            for (int i = 0; i < 4; i++)
                collisionMaps[i].method210();
            for (int l = 0; l < 4; l++) {
                for (int k1 = 0; k1 < 104; k1++) {
                    for (int j2 = 0; j2 < 104; j2++)
                        byteGroundArray[l][k1][j2] = 0;
                }
            }

            ObjectManager objectManager = new ObjectManager(byteGroundArray, intGroundArray);
            int k2 = aByteArrayArray1183.length;
            outgoing.writeOpcode(0);
            if (!aBoolean1159) {
                for (int i3 = 0; i3 < k2; i3++) {
                    int i4 = (anIntArray1234[i3] >> 8) * 64 - baseX;
                    int k5 = (anIntArray1234[i3] & 0xff) * 64 - baseY;
                    byte abyte0[] = aByteArrayArray1183[i3];
                    if (abyte0 != null)
                        objectManager.method180(abyte0, k5, i4, (anInt1069 - 6) * 8, (anInt1070 - 6) * 8, collisionMaps);
                    abyte0 = null;
                }
                for (int j4 = 0; j4 < k2; j4++) {
                    int l5 = (anIntArray1234[j4] >> 8) * 64 - baseX;
                    int k7 = (anIntArray1234[j4] & 0xff) * 64 - baseY;
                    byte abyte2[] = aByteArrayArray1183[j4];
                    if (abyte2 == null && anInt1070 < 800)
                        objectManager.method174(k7, 64, 64, l5);
                    abyte2 = null;
                }
                outgoing.writeOpcode(0);
                for (int i6 = 0; i6 < k2; i6++) {
                    byte abyte1[] = aByteArrayArray1247[i6];
                    if (abyte1 != null) {
                        int l8 = (anIntArray1234[i6] >> 8) * 64 - baseX;
                        int k9 = (anIntArray1234[i6] & 0xff) * 64 - baseY;
                        objectManager.method190(l8, collisionMaps, k9, worldController, abyte1);
                    }
                    abyte1 = null;
                }

            }
            if (aBoolean1159) {
                for (int j3 = 0; j3 < 4; j3++) {
                    for (int k4 = 0; k4 < 13; k4++) {
                        for (int j6 = 0; j6 < 13; j6++) {
                            int l7 = anIntArrayArrayArray1129[j3][k4][j6];
                            if (l7 != -1) {
                                int i9 = l7 >> 24 & 3;
                                int l9 = l7 >> 1 & 3;
                                int j10 = l7 >> 14 & 0x3ff;
                                int l10 = l7 >> 3 & 0x7ff;
                                int j11 = (j10 / 8 << 8) + l10 / 8;
                                for (int l11 = 0; l11 < anIntArray1234.length; l11++) {
                                    if (anIntArray1234[l11] != j11 || aByteArrayArray1183[l11] == null)
                                        continue;
                                    objectManager.method179(i9, l9, collisionMaps, k4 * 8, (j10 & 7) * 8, aByteArrayArray1183[l11], (l10 & 7) * 8, j3, j6 * 8);
                                    break;
                                }

                            }
                        }
                    }
                }
                for (int l4 = 0; l4 < 13; l4++) {
                    for (int k6 = 0; k6 < 13; k6++) {
                        int i8 = anIntArrayArrayArray1129[0][l4][k6];
                        if (i8 == -1)
                            objectManager.method174(k6 * 8, 8, 8, l4 * 8);
                    }
                }

                outgoing.writeOpcode(0);
                for (int l6 = 0; l6 < 4; l6++) {
                    for (int j8 = 0; j8 < 13; j8++) {
                        for (int j9 = 0; j9 < 13; j9++) {
                            int i10 = anIntArrayArrayArray1129[l6][j8][j9];
                            if (i10 != -1) {
                                int k10 = i10 >> 24 & 3;
                                int i11 = i10 >> 1 & 3;
                                int k11 = i10 >> 14 & 0x3ff;
                                int i12 = i10 >> 3 & 0x7ff;
                                int j12 = (k11 / 8 << 8) + i12 / 8;
                                for (int k12 = 0; k12 < anIntArray1234.length; k12++) {
                                    if (anIntArray1234[k12] != j12 || aByteArrayArray1247[k12] == null)
                                        continue;
                                    objectManager.method183(collisionMaps, worldController, k10, j8 * 8, (i12 & 7) * 8, l6, aByteArrayArray1247[k12], (k11 & 7) * 8, i11, j9 * 8);
                                    break;
                                }

                            }
                        }

                    }

                }

            }
            outgoing.writeOpcode(0);
            objectManager.method171(collisionMaps, worldController);
            if (Settings.FOG) {
                Rasterizer.newFogColor = getNextInteger(objectManager.fogColorList).getKey();
                objectManager.fogColorList.clear();
            }
            gameScreenIP.initDrawingArea();
            outgoing.writeOpcode(0);
            int k3 = ObjectManager.anInt145;
            if (k3 > plane)
                k3 = plane;
            if (k3 < plane - 1)
                k3 = plane - 1;
            if (lowMem)
                worldController.method275(ObjectManager.anInt145);
            else
                worldController.method275(0);
            for (int i5 = 0; i5 < 104; i5++) {
                for (int i7 = 0; i7 < 104; i7++)
                    spawnGroundItem(i5, i7);

            }

            method63();
        } catch (Exception exception) {
        }
        ObjectDefinition.mruNodes1.unlinkAll();
        if (super.clientFrame != null) {
            outgoing.writeOpcode(210);
            outgoing.writeDWord(0x3f008edd);
        }
        if (lowMem && Client.cache_dat != null) {
            int j = onDemandFetcher.getVersionCount(0);
            for (int i1 = 0; i1 < j; i1++) {
                int l1 = onDemandFetcher.getModelIndex(i1);
                if ((l1 & 0x79) == 0)
                    Model.method461(i1);
            }

        }
        System.gc();
        Rasterizer.method367(20);
        onDemandFetcher.method566();
        int k = (anInt1069 - 6) / 8 - 1;
        int j1 = (anInt1069 + 6) / 8 + 1;
        int i2 = (anInt1070 - 6) / 8 - 1;
        int l2 = (anInt1070 + 6) / 8 + 1;
        if (aBoolean1141) {
            k = 49;
            j1 = 50;
            i2 = 49;
            l2 = 50;
        }
        for (int l3 = k; l3 <= j1; l3++) {
            for (int j5 = i2; j5 <= l2; j5++)
                if (l3 == k || l3 == j1 || j5 == i2 || j5 == l2) {
                    int j7 = onDemandFetcher.resolve(0, l3, j5);
                    if (j7 != -1)
                        onDemandFetcher.method560(j7, 3);
                    int k8 = onDemandFetcher.resolve(1, l3, j5);
                    if (k8 != -1)
                        onDemandFetcher.method560(k8, 3);
                }

        }

    }

    public static AbstractMap.SimpleEntry<Integer, Integer> getNextInteger(ArrayList<Integer> values) {
        ArrayList<AbstractMap.SimpleEntry<Integer, Integer>> frequencies = new ArrayList<>();
        int maxIndex = 0;
        main:
        for (int i = 0; i < values.size(); ++i) {
            int value = values.get(i);
            for (int j = 0; j < frequencies.size(); ++j) {
                if (frequencies.get(j).getKey() == value) {
                    frequencies.get(j).setValue(frequencies.get(j).getValue() + 1);
                    if (frequencies.get(maxIndex).getValue() < frequencies.get(j).getValue()) {
                        maxIndex = j;
                    }
                    continue main;
                }
            }
            frequencies.add(new AbstractMap.SimpleEntry<Integer, Integer>(value, 1));
        }
        return frequencies.get(maxIndex);
    }

    private void unlinkMRUNodes() {
        ObjectDefinition.mruNodes1.unlinkAll();
        ObjectDefinition.mruNodes2.unlinkAll();
        NpcDefinition.modelCache.unlinkAll();
        ItemDefinition.mruNodes2.unlinkAll();
        ItemDefinition.mruNodes1.unlinkAll();
        Player.mruNodes.unlinkAll();
        Graphic.aMRUNodes_415.unlinkAll();
    }

    private void method24(int i) {
        int ai[] = minimapImage.raster;
        int j = ai.length;
        for (int k = 0; k < j; k++)
            ai[k] = 0;
        for (int l = 1; l < 103; l++) {
            int i1 = 24628 + (103 - l) * 512 * 4;
            for (int k1 = 1; k1 < 103; k1++) {
                if ((byteGroundArray[i][k1][l] & 0x18) == 0)
                    worldController.method309(ai, i1, i, k1, l);
                if (i < 3 && (byteGroundArray[i + 1][k1][l] & 8) != 0)
                    worldController.method309(ai, i1, i + 1, k1, l);
                i1 += 4;
            }
        }
        ai = null;
        int j1 = Color.WHITE.getRGB();
        int l1 = Color.RED.getRGB();
        minimapImage.method343();
        for (int i2 = 1; i2 < 103; i2++) {
            for (int j2 = 1; j2 < 103; j2++) {
                if ((byteGroundArray[i][j2][i2] & 0x18) == 0)
                    method50(i2, j1, j2, l1, i);
                if (i < 3 && (byteGroundArray[i + 1][j2][i2] & 8) != 0)
                    method50(i2, j1, j2, l1, i + 1);
            }

        }
        gameScreenIP.initDrawingArea();
        anInt1071 = 0;
        for (int k2 = 0; k2 < 104; k2++) {
            for (int l2 = 0; l2 < 104; l2++) {
                int i3 = worldController.method303(plane, k2, l2);
                if (i3 != 0) {
                    i3 = i3 >> 14 & 0x7fff;
                    int j3 = ObjectDefinition.lookup(i3).mapIcon;
                    if (j3 >= 0) {
                        int k3 = k2;
                        int l3 = l2;
                        if (j3 >= 15 && j3 <= 67) {
                            j3 -= 2;
                        } else if (j3 >= 68 && j3 <= 84) {
                            j3 -= 1;
                        }
                        aClass30_Sub2_Sub1_Sub1Array1140[anInt1071] = mapFunctions[j3];
                        anIntArray1072[anInt1071] = k3;
                        anIntArray1073[anInt1071] = l3;
                        anInt1071++;
                    }
                }
            }
        }
    }

    private void spawnGroundItem(int i, int j) {
        Deque class19 = groundArray[plane][i][j];
        if (class19 == null) {
            worldController.method295(plane, i, j);
            return;
        }
        int k = 0xfa0a1f01;
        Object obj = null;
        for (Item item = (Item) class19.reverseGetFirst(); item != null; item = (Item) class19.reverseGetNext()) {
            ItemDefinition itemDef = ItemDefinition.lookup(item.itemId);
            int l = itemDef.value;
            if (itemDef.stackable)
                l *= item.itemAmount + 1;
            // notifyItemSpawn(item, i + baseX, j + baseY);

            if (l > k) {
                k = l;
                obj = item;
            }
        }

        class19.insertTail(((Linkable) (obj)));
        Object obj1 = null;
        Object obj2 = null;
        for (Item class30_sub2_sub4_sub2_1 = (Item) class19.reverseGetFirst(); class30_sub2_sub4_sub2_1 != null; class30_sub2_sub4_sub2_1 = (Item) class19.reverseGetNext()) {
            if (class30_sub2_sub4_sub2_1.itemId != ((Item) (obj)).itemId && obj1 == null)
                obj1 = class30_sub2_sub4_sub2_1;
            if (class30_sub2_sub4_sub2_1.itemId != ((Item) (obj)).itemId && class30_sub2_sub4_sub2_1.itemId != ((Item) (obj1)).itemId && obj2 == null)
                obj2 = class30_sub2_sub4_sub2_1;
        }

        int i1 = i + (j << 7) + 0x60000000;
        worldController.method281(i, i1, ((Renderable) (obj1)), method42(plane, j * 128 + 64, i * 128 + 64), ((Renderable) (obj2)), ((Renderable) (obj)), plane, j);
    }

    private void method26(boolean flag) {
        for (int j = 0; j < npcCount; j++) {
            Npc npc = npcs[npcIndices[j]];
            int k = 0x20000000 + (npcIndices[j] << 14);
            if (npc == null || !npc.isVisible() || npc.definition.aBoolean93 != flag)
                continue;
            int l = npc.x >> 7;
            int i1 = npc.y >> 7;
            if (l < 0 || l >= 104 || i1 < 0 || i1 >= 104)
                continue;
            if (npc.size == 1 && (npc.x & 0x7f) == 64 && (npc.y & 0x7f) == 64) {
                if (anIntArrayArray929[l][i1] == anInt1265)
                    continue;
                anIntArrayArray929[l][i1] = anInt1265;
            }
            if (!npc.definition.aBoolean84)
                k += 0x80000000;
            worldController.method285(plane, npc.anInt1552, method42(plane, npc.y, npc.x), k, npc.y, (npc.size - 1) * 64 + 60, npc.x, npc, npc.aBoolean1541);
        }
    }

    private void buildInterfaceMenu(int xMin, RSInterface class9, int hoverX, int yMin, int hoverY, int scroll) {
        if (class9.type != 0 || class9.children == null || class9.isMouseoverTriggered) {
            hoverShopTile = -1;
            return;
        }
        if (hoverX < xMin || hoverY < yMin || hoverX > xMin + class9.width || hoverY > yMin + class9.height) {
            hoverShopTile = -1;
            return;
        }
        if (class9.type != 0 || class9.children == null || class9.isMouseoverTriggered)
            return;
        if (hoverX < xMin || hoverY < yMin || hoverX > xMin + class9.width || hoverY > yMin + class9.height)
            return;
        int k1 = class9.children.length;
        for (int index = 0; index < k1; index++) {
            int xBounds = class9.childX[index] + xMin;
            int yBounds = (class9.childY[index] + yMin) - scroll;
            RSInterface child = RSInterface.getInterfaceCache()[class9.children[index]];
            xBounds += child.anInt263;
            yBounds += child.anInt265;
            if ((child.hoverType >= 0 || child.textHoverColor != 0) && hoverX >= xBounds && hoverY >= yBounds && hoverX < xBounds + child.width && hoverY < yBounds + child.height)
                if (child.hoverType >= 0)
                    anInt886 = child.hoverType;
                else
                    anInt886 = child.interfaceId;
            if (child.type == 8 && hoverX >= xBounds && hoverY >= yBounds && hoverX < xBounds + child.width && hoverY < yBounds + child.height) {
                anInt1315 = child.interfaceId;
            }
            if (child.type == 0) {
                buildInterfaceMenu(xBounds, child, hoverX, yBounds, hoverY, child.scrollPosition);
                if (child.scrollMax > child.height)
                    method65(xBounds + child.width, child.height, hoverX, hoverY, child, yBounds, true, child.scrollMax);
            } else {
                if (child.atActionType == 1 && hoverX >= xBounds && hoverY >= yBounds && hoverX < xBounds + child.width && hoverY < yBounds + child.height) {
                    boolean flag = false;
                    if (child.contentType != 0)
                        flag = buildFriendsListMenu(child);
                    if (!flag && !child.tooltip.equals("")) {
                        menuActionName[menuActionRow] = child.tooltip;
                        menuActionID[menuActionRow] = 315;
                        menuActionCmd3[menuActionRow] = child.interfaceId;
                        menuActionRow++;
                    }
                }
                if (child.atActionType == 2 && spellSelected == 0 && hoverX >= xBounds && hoverY >= yBounds && hoverX < xBounds + child.width && hoverY < yBounds + child.height) {
                    String s = child.selectedActionName;
                    if (s.indexOf(" ") != -1)
                        s = s.substring(0, s.indexOf(" "));
                    menuActionName[menuActionRow] = s + " @gre@" + child.spellName;
                    menuActionID[menuActionRow] = 626;
                    menuActionCmd3[menuActionRow] = child.interfaceId;
                    menuActionRow++;
                }
                if (child.atActionType == 3 && hoverX >= xBounds && hoverY >= yBounds && hoverX < xBounds + child.width && hoverY < yBounds + child.height) {
                    menuActionName[menuActionRow] = "Close";
                    menuActionID[menuActionRow] = 200;
                    menuActionCmd3[menuActionRow] = child.interfaceId;
                    menuActionRow++;
                } else if (child.atActionType == 4 && hoverX >= xBounds && hoverY >= yBounds && hoverX < xBounds + child.width && hoverY < yBounds + child.height) {
                    menuActionName[menuActionRow] = child.tooltip;
                    menuActionID[menuActionRow] = 169;
                    menuActionCmd3[menuActionRow] = child.interfaceId;
                    menuActionRow++;
                } else if (child.atActionType == 10 && hoverX >= xBounds && hoverY >= yBounds && hoverX < xBounds + child.width && hoverY < yBounds + child.height) {
                    menuActionName[menuActionRow] = child.tooltip;
                    menuActionID[menuActionRow] = 1329;
                    menuActionCmd3[menuActionRow] = child.interfaceId;
                    menuActionRow++;
                    if (super.clickType == 2 || super.clickType == 0) {
                        if (child.interfaceId == 56716) {
                            RSInterface.getInterfaceCache()[56700].childY[24] = super.mouseY - yBounds + 88;
                            getColorFromCoords(1);
                        } else if (child.interfaceId == 56715) {
                            getColorFromCoords(2);
                        }
                    }
                } else if (child.atActionType == 5 && hoverX >= xBounds && hoverY >= yBounds && hoverX < xBounds + child.width && hoverY < yBounds + child.height) {
                    menuActionName[menuActionRow] = child.tooltip;
                    menuActionID[menuActionRow] = 646;
                    menuActionCmd3[menuActionRow] = child.interfaceId;
                    menuActionRow++;
                } else if (child.atActionType == 6 && !aBoolean1149 && hoverX >= xBounds && hoverY >= yBounds && hoverX < xBounds + child.width && hoverY < yBounds + child.height) {
                    menuActionName[menuActionRow] = child.tooltip;
                    menuActionID[menuActionRow] = 679;
                    menuActionCmd3[menuActionRow] = child.interfaceId;
                    menuActionRow++;

                    //dropdown
                } else if (child.atActionType == 69) {
                    child.hovered = false;
                    if (child.dropDown.isOpen()) {
                        child.dropDown.hover(class9, child, hoverX, hoverY, xBounds, yBounds);
                    }
                    if (hoverX >= xBounds && hoverY >= yBounds && hoverX < xBounds + child.dropDown.getWidth() && hoverY < yBounds + 24) {
                        child.hovered = true;
                        menuActionName[menuActionRow] = child.dropDown.isOpen() ? "Hide other options" : "Show other options";
                        menuActionID[menuActionRow] = 769;
                        menuActionCmd3[menuActionRow] = child.interfaceId;
                        menuActionCmd1[menuActionRow] = class9.interfaceId;
                        menuActionRow++;
                    }


                } else if (child.type == 2) {
                    int itemSlot = 0;
                    int tabAm = tabAmounts[0];
                    int tabSlot = 0;
                    int heightShift = 0;

                    int newSlot = 0;
                    if (child.contentType == 206 && settings[211] != 0) {
                        for (int tab = 0; tab < tabAmounts.length; tab++) {
                            if (tab == settings[211]) {
                                break;
                            }
                            newSlot += tabAmounts[tab];
                        }
                        itemSlot = newSlot;
                    }

                    heightLoop:
                    for (int l2 = 0; l2 < child.height; l2++) {
                        for (int i3 = 0; i3 < child.width; i3++) {
                            int j3 = xBounds + i3 * (32 + child.invSpritePadX);
                            int k3 = yBounds + l2 * (32 + child.invSpritePadY) + heightShift;
                            if (child.contentType == 206) {
                                if (settings[211] == 0) {
                                    if (itemSlot >= tabAm) {
                                        if (tabSlot + 1 < tabAmounts.length) {
                                            tabAm += tabAmounts[++tabSlot];
                                            if (tabSlot > 0 && tabAmounts[tabSlot - 1] % child.width == 0) {
                                                l2--;
                                            }
                                            heightShift += 8;
                                        }
                                        break;
                                    }
                                } else if (settings[211] <= 9) {
                                    if (itemSlot >= tabAmounts[settings[211]] + newSlot) {
                                        break heightLoop;
                                    }
                                }
                            }
                            if (itemSlot < 20) {
                                j3 += child.spritesX[itemSlot];
                                k3 += child.spritesY[itemSlot];
                            }
                            if (hoverX >= j3 && hoverY >= k3 && hoverX < j3 + 32 && hoverY < k3 + 32) {
                                mouseInvInterfaceIndex = itemSlot;
                                lastActiveInvInterface = child.interfaceId;
                                int item = child.inv[itemSlot];
                                boolean searching = (RSInterface.getInterfaceCache()[60019].disabledMessage.length() > 0 && !RSInterface.getInterfaceCache()[60019].defaultInputFieldText.equalsIgnoreCase(RSInterface.getInterfaceCache()[60019].disabledMessage)) && child.contentType == 206;
                                if (searching)
                                    item = bankInvTemp[itemSlot];
                                if (item > 0) {
                                    ItemDefinition itemDef = ItemDefinition.lookup(item - 1);
                                    boolean hasDestroyOption = false;
                                    itemHover = item - 1;
                                    if (itemHover != lastItemHover) {
                                        updateItemHoverInterface(itemDef);
                                        lastItemHover = item - 1;
                                    }

                                    if (itemSelected == 1 && child.isInventoryInterface) {
                                        if (child.interfaceId != anInt1284 || itemSlot != anInt1283) {
                                            menuActionName[menuActionRow] = "Use " + selectedItemName + " with @lre@" + itemDef.name;
                                            menuActionID[menuActionRow] = 870;
                                            menuActionCmd1[menuActionRow] = itemDef.id;
                                            menuActionCmd2[menuActionRow] = itemSlot;
                                            menuActionCmd3[menuActionRow] = child.interfaceId;
                                            menuActionRow++;
                                        }
                                    } else if (spellSelected == 1 && child.isInventoryInterface) {
                                        if ((spellUsableOn & 0x10) == 16) {
                                            menuActionName[menuActionRow] = spellTooltip +"@lre@" + itemDef.name;
                                            menuActionID[menuActionRow] = 543;
                                            menuActionCmd1[menuActionRow] = itemDef.id;
                                            menuActionCmd2[menuActionRow] = itemSlot;
                                            menuActionCmd3[menuActionRow] = child.interfaceId;
                                            menuActionRow++;
                                        }
                                    } else {
                                        if (child.isInventoryInterface) {
                                            for (int l3 = 4; l3 >= 3; l3--)
                                                if (itemDef.itemActions != null && itemDef.itemActions[l3] != null) {
                                                    menuActionName[menuActionRow] = itemDef.itemActions[l3] + " @lre@" + itemDef.name;
                                                    if (l3 == 3)
                                                        menuActionID[menuActionRow] = 493;
                                                    if (l3 == 4) {
                                                        menuActionID[menuActionRow] = 847;
                                                        hasDestroyOption = itemDef.itemActions[l3].contains("Destroy");
                                                    }
                                                    menuActionCmd1[menuActionRow] = itemDef.id;
                                                    menuActionCmd2[menuActionRow] = itemSlot;
                                                    menuActionCmd3[menuActionRow] = child.interfaceId;
                                                    menuActionRow++;
                                                } else if (l3 == 4) {
                                                    menuActionName[menuActionRow] = "Drop @lre@" + itemDef.name;
                                                    menuActionID[menuActionRow] = 847;
                                                    menuActionCmd1[menuActionRow] = itemDef.id;
                                                    menuActionCmd2[menuActionRow] = itemSlot;
                                                    menuActionCmd3[menuActionRow] = child.interfaceId;
                                                    menuActionRow++;
                                                }
                                        }
                                        if (child.usableItemInterface) {
                                            menuActionName[menuActionRow] = "Use @lre@" + itemDef.name + (Configuration.DEBUG_MODE ? " <col=65280>(<col=ffffff>" + itemDef.id + "<col=65280>)" : "");
                                            menuActionID[menuActionRow] = 447;
                                            menuActionCmd1[menuActionRow] = itemDef.id;
                                            menuActionCmd2[menuActionRow] = itemSlot;
                                            menuActionCmd3[menuActionRow] = child.interfaceId;
                                            menuActionRow++;
                                        }
                                        if (child.isInventoryInterface && itemDef.itemActions != null) {
                                            if (!hasDestroyOption && !menuOpen && Settings.SHIFT_DROP && shiftDown) {
                                                menuActionsRow("Drop @lre@" + itemDef.name, 1, 847, 2);
                                                removeShiftDropOnMenuOpen = true;
                                            }
                                            for (int i4 = 2; i4 >= 0; i4--)
                                                if (itemDef.itemActions[i4] != null) {
                                                    menuActionName[menuActionRow] = itemDef.itemActions[i4] + " @lre@" + itemDef.name;
                                                    if (i4 == 0)
                                                        menuActionID[menuActionRow] = 74;
                                                    if (i4 == 1)
                                                        menuActionID[menuActionRow] = 454;
                                                    if (i4 == 2)
                                                        menuActionID[menuActionRow] = 539;
                                                    menuActionCmd1[menuActionRow] = itemDef.id;
                                                    menuActionCmd2[menuActionRow] = itemSlot;
                                                    menuActionCmd3[menuActionRow] = child.interfaceId;
                                                    menuActionRow++;
                                                }

                                        }
                                        int amount = 0;
                                        if (itemSlot != -1) {
                                            if (searching) {
                                                amount = bankStackTemp[itemSlot];
                                            } else {
                                                amount = child.invStackSizes[itemSlot];
                                            }
                                        }
                                        if (child.actions != null) {
                                            int length = 6;
                                            if (child.parentID == 40051 || child.interfaceId == 3823) {
                                                String name = localPlayer.name;
                                                String store = name += "'s Store";
                                                String title = "";
                                                boolean inventory = child.interfaceId == 3823;

                                                if (RSInterface.getInterfaceCache()[child.parentID] != null) {
                                                    title = RSInterface.getInterfaceCache()[40002].disabledMessage;
                                                }

                                                if (store.equalsIgnoreCase(title)) {
                                                    if (inventory) {
                                                        child.actions = new String[]{"Offer 1", "Offer 5", "Offer 10", "Offer All", "Offer X"};
                                                    } else {
                                                        child.actions = new String[]{"Modify", "Remove 1", "Remove 10", "Remove All", "Remove X"};
                                                    }
                                                } else {
                                                    if (inventory) {
                                                        child.actions = new String[]{"Value", "Sell 1", "Sell 10", "Sell 100", "Sell X"};
                                                    } else {
                                                        child.actions = new String[]{"Value", "Buy 1", "Buy 10", "Buy 100", "Buy X"};
                                                    }
                                                }
                                            } else if (child.parentID == 5382) {
                                                if (amount != 0) {
                                                    child.actions = new String[]{"Withdraw-1", "Withdraw-5", "Withdraw-10", "Withdraw-All", "Withdraw-X", null, "Withdraw-All but one"};
                                                    if (modifiableXValue > 0) {
                                                        child.actions[5] = "Withdraw-" + modifiableXValue;
                                                    } else {
                                                        child.actions[5] = null;
                                                    }
                                                    if (settings[116] == 0) {
                                                        String[] newActions = new String[child.actions.length + 1];
                                                        for (int action = 0; action < newActions.length; action++) {
                                                            if (action == child.actions.length) {
                                                                newActions[action] = "Place holder";
                                                                continue;
                                                            }
                                                            newActions[action] = child.actions[action];
                                                        }
                                                        child.actions = newActions;
                                                        length = 7;
                                                    }
                                                } else {
                                                    child.actions = new String[]{"Release"};
                                                    length = 6;
                                                }
                                            }

                                            if (!searching || amount != 0) {
                                                for (int action = length; action >= 0; action--) {
                                                    if (action > child.actions.length - 1)
                                                        continue;
                                                    if (child.actions[action] != null) {
                                                        menuActionName[menuActionRow] = child.actions[action] + " @lre@" + itemDef.name;
                                                        if (action == 0)
                                                            menuActionID[menuActionRow] = 632;
                                                        if (action == 1)
                                                            menuActionID[menuActionRow] = 78;
                                                        if (action == 2)
                                                            menuActionID[menuActionRow] = 867;
                                                        if (action == 3)
                                                            menuActionID[menuActionRow] = 431;
                                                        if (action == 4)
                                                            menuActionID[menuActionRow] = 53;
                                                        if (child.parentID == 5382) {
                                                            if (child.actions[action] == null) {
                                                                if (action == 5)
                                                                    menuActionID[menuActionRow] = 291;
                                                            } else {
                                                                if (action == 5)
                                                                    menuActionID[menuActionRow] = 300;
                                                                if (action == 6)
                                                                    menuActionID[menuActionRow] = 291;
                                                            }
                                                        }
                                                        if (action == 7)
                                                            menuActionID[menuActionRow] = 968;

                                                        menuActionCmd1[menuActionRow] = itemDef.id;
                                                        menuActionCmd2[menuActionRow] = itemSlot;
                                                        menuActionCmd3[menuActionRow] = child.interfaceId;
                                                        menuActionRow++;
                                                        if (child.parentID == 40051) {
                                                            hoverShopTile = itemSlot;
                                                        } else {
                                                            hoverShopTile = -1;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        if (!searching || amount != 0) {
                                            if (!child.displayExamine)
                                                return;

                                            menuActionName[menuActionRow] = "Examine @lre@" + itemDef.name;

                                            menuActionID[menuActionRow] = 1125;
                                            menuActionCmd1[menuActionRow] = itemDef.id;
                                            menuActionCmd2[menuActionRow] = itemSlot;
                                            menuActionCmd3[menuActionRow] = child.interfaceId;
                                            menuActionRow++;
                                        }
                                    }
                                }
                            }
                            itemSlot++;
                        }
                    }
                }
            }
        }
    }

    private void menuActionsRow(String action, int index, int actionID, int row) {
        if (menuOpen)
            return;
        menuActionName[index] = action;
        menuActionID[index] = actionID;
        menuActionRow = row;
    }

    public void drawTransparentScrollBar(int x, int y, int height, int maxScroll, int pos) {
        spriteCache.get(29).drawARGBSprite(x, y, 120);
        spriteCache.get(30).drawARGBSprite(x, y + height - 16, 120);
        Raster.drawVerticalLine(x, y + 16, height - 32, 0xffffff, 64);
        Raster.drawVerticalLine(x + 15, y + 16, height - 32, 0xffffff, 64);
        int barHeight = (height - 32) * height / maxScroll;
        if (barHeight < 10) {
            barHeight = 10;
        }
        int barPos = 0;
        if (maxScroll != height) {
            barPos = (height - 32 - barHeight) * pos / (maxScroll - height);
        }
        Raster.fillRectangle(x, y + 16 + barPos, 16, 5 + y + 16 + barPos + barHeight - 5 - (y + 16 + barPos), 0xffffff, 60);
    }

    public void drawScrollbar(int height, int pos, int y, int x, int maxScroll, boolean transparent) {
        if (transparent) {
            drawTransparentScrollBar(x, y, height, maxScroll, pos);
        } else {
            scrollBar1.drawSprite(x, y);
            scrollBar2.drawSprite(x, (y + height) - 16);
            Raster.fillRectangle(x, y + 16, 16, height - 32, 0x000001);
            Raster.fillRectangle(x, y + 16, 15, height - 32, 0x3d3426);
            Raster.fillRectangle(x, y + 16, 13, height - 32, 0x342d21);
            Raster.fillRectangle(x, y + 16, 11, height - 32, 0x2e281d);
            Raster.fillRectangle(x, y + 16, 10, height - 32, 0x29241b);
            Raster.fillRectangle(x, y + 16, 9, height - 32, 0x252019);
            Raster.fillRectangle(x, y + 16, 1, height - 32, 0x000001);
            int k1 = ((height - 32) * height) / maxScroll;
            if (k1 < 8) {
                k1 = 8;
            }
            int l1 = ((height - 32 - k1) * pos) / (maxScroll - height);
            Raster.fillRectangle(x, y + 16 + l1, 16, k1, barFillColor);
            Raster.drawVerticalLine(x, y + 16 + l1, k1, 0x000001);
            Raster.drawVerticalLine(x + 1, y + 16 + l1, k1, 0x817051);
            Raster.drawVerticalLine(x + 2, y + 16 + l1, k1, 0x73654a);
            Raster.drawVerticalLine(x + 3, y + 16 + l1, k1, 0x6a5c43);
            Raster.drawVerticalLine(x + 4, y + 16 + l1, k1, 0x6a5c43);
            Raster.drawVerticalLine(x + 5, y + 16 + l1, k1, 0x655841);
            Raster.drawVerticalLine(x + 6, y + 16 + l1, k1, 0x655841);
            Raster.drawVerticalLine(x + 7, y + 16 + l1, k1, 0x61553e);
            Raster.drawVerticalLine(x + 8, y + 16 + l1, k1, 0x61553e);
            Raster.drawVerticalLine(x + 9, y + 16 + l1, k1, 0x5d513c);
            Raster.drawVerticalLine(x + 10, y + 16 + l1, k1, 0x5d513c);
            Raster.drawVerticalLine(x + 11, y + 16 + l1, k1, 0x594e3a);
            Raster.drawVerticalLine(x + 12, y + 16 + l1, k1, 0x594e3a);
            Raster.drawVerticalLine(x + 13, y + 16 + l1, k1, 0x514635);
            Raster.drawVerticalLine(x + 14, y + 16 + l1, k1, 0x4b4131);
            Raster.drawHorizontalLine(x, y + 16 + l1, 15, 0x000001);
            Raster.drawHorizontalLine(x, y + 17 + l1, 15, 0x000001);
            Raster.drawHorizontalLine(x, y + 17 + l1, 14, 0x655841);
            Raster.drawHorizontalLine(x, y + 17 + l1, 13, 0x6a5c43);
            Raster.drawHorizontalLine(x, y + 17 + l1, 11, 0x6d5f48);
            Raster.drawHorizontalLine(x, y + 17 + l1, 10, 0x73654a);
            Raster.drawHorizontalLine(x, y + 17 + l1, 7, 0x76684b);
            Raster.drawHorizontalLine(x, y + 17 + l1, 5, 0x7b6a4d);
            Raster.drawHorizontalLine(x, y + 17 + l1, 4, 0x7e6e50);
            Raster.drawHorizontalLine(x, y + 17 + l1, 3, 0x817051);
            Raster.drawHorizontalLine(x, y + 17 + l1, 2, 0x000001);
            Raster.drawHorizontalLine(x, y + 18 + l1, 16, 0x000001);
            Raster.drawHorizontalLine(x, y + 18 + l1, 15, 0x564b38);
            Raster.drawHorizontalLine(x, y + 18 + l1, 14, 0x5d513c);
            Raster.drawHorizontalLine(x, y + 18 + l1, 11, 0x625640);
            Raster.drawHorizontalLine(x, y + 18 + l1, 10, 0x655841);
            Raster.drawHorizontalLine(x, y + 18 + l1, 7, 0x6a5c43);
            Raster.drawHorizontalLine(x, y + 18 + l1, 5, 0x6e6046);
            Raster.drawHorizontalLine(x, y + 18 + l1, 4, 0x716247);
            Raster.drawHorizontalLine(x, y + 18 + l1, 3, 0x7b6a4d);
            Raster.drawHorizontalLine(x, y + 18 + l1, 2, 0x817051);
            Raster.drawHorizontalLine(x, y + 18 + l1, 1, 0x000001);
            Raster.drawHorizontalLine(x, y + 19 + l1, 16, 0x000001);
            Raster.drawHorizontalLine(x, y + 19 + l1, 15, 0x514635);
            Raster.drawHorizontalLine(x, y + 19 + l1, 14, 0x564b38);
            Raster.drawHorizontalLine(x, y + 19 + l1, 11, 0x5d513c);
            Raster.drawHorizontalLine(x, y + 19 + l1, 9, 0x61553e);
            Raster.drawHorizontalLine(x, y + 19 + l1, 7, 0x655841);
            Raster.drawHorizontalLine(x, y + 19 + l1, 5, 0x6a5c43);
            Raster.drawHorizontalLine(x, y + 19 + l1, 4, 0x6e6046);
            Raster.drawHorizontalLine(x, y + 19 + l1, 3, 0x73654a);
            Raster.drawHorizontalLine(x, y + 19 + l1, 2, 0x817051);
            Raster.drawHorizontalLine(x, y + 19 + l1, 1, 0x000001);
            Raster.drawHorizontalLine(x, y + 20 + l1, 16, 0x000001);
            Raster.drawHorizontalLine(x, y + 20 + l1, 15, 0x4b4131);
            Raster.drawHorizontalLine(x, y + 20 + l1, 14, 0x544936);
            Raster.drawHorizontalLine(x, y + 20 + l1, 13, 0x594e3a);
            Raster.drawHorizontalLine(x, y + 20 + l1, 10, 0x5d513c);
            Raster.drawHorizontalLine(x, y + 20 + l1, 8, 0x61553e);
            Raster.drawHorizontalLine(x, y + 20 + l1, 6, 0x655841);
            Raster.drawHorizontalLine(x, y + 20 + l1, 4, 0x6a5c43);
            Raster.drawHorizontalLine(x, y + 20 + l1, 3, 0x73654a);
            Raster.drawHorizontalLine(x, y + 20 + l1, 2, 0x817051);
            Raster.drawHorizontalLine(x, y + 20 + l1, 1, 0x000001);
            Raster.drawVerticalLine(x + 15, y + 16 + l1, k1, 0x000001);
            Raster.drawHorizontalLine(x, y + 15 + l1 + k1, 16, 0x000001);
            Raster.drawHorizontalLine(x, y + 14 + l1 + k1, 15, 0x000001);
            Raster.drawHorizontalLine(x, y + 14 + l1 + k1, 14, 0x3f372a);
            Raster.drawHorizontalLine(x, y + 14 + l1 + k1, 10, 0x443c2d);
            Raster.drawHorizontalLine(x, y + 14 + l1 + k1, 9, 0x483e2f);
            Raster.drawHorizontalLine(x, y + 14 + l1 + k1, 7, 0x4a402f);
            Raster.drawHorizontalLine(x, y + 14 + l1 + k1, 4, 0x4b4131);
            Raster.drawHorizontalLine(x, y + 14 + l1 + k1, 3, 0x564b38);
            Raster.drawHorizontalLine(x, y + 14 + l1 + k1, 2, 0x000001);
            Raster.drawHorizontalLine(x, y + 13 + l1 + k1, 16, 0x000001);
            Raster.drawHorizontalLine(x, y + 13 + l1 + k1, 15, 0x443c2d);
            Raster.drawHorizontalLine(x, y + 13 + l1 + k1, 11, 0x4b4131);
            Raster.drawHorizontalLine(x, y + 13 + l1 + k1, 9, 0x514635);
            Raster.drawHorizontalLine(x, y + 13 + l1 + k1, 7, 0x544936);
            Raster.drawHorizontalLine(x, y + 13 + l1 + k1, 6, 0x564b38);
            Raster.drawHorizontalLine(x, y + 13 + l1 + k1, 4, 0x594e3a);
            Raster.drawHorizontalLine(x, y + 13 + l1 + k1, 3, 0x625640);
            Raster.drawHorizontalLine(x, y + 13 + l1 + k1, 2, 0x6a5c43);
            Raster.drawHorizontalLine(x, y + 13 + l1 + k1, 1, 0x000001);
            Raster.drawHorizontalLine(x, y + 12 + l1 + k1, 16, 0x000001);
            Raster.drawHorizontalLine(x, y + 12 + l1 + k1, 15, 0x443c2d);
            Raster.drawHorizontalLine(x, y + 12 + l1 + k1, 14, 0x4b4131);
            Raster.drawHorizontalLine(x, y + 12 + l1 + k1, 12, 0x544936);
            Raster.drawHorizontalLine(x, y + 12 + l1 + k1, 11, 0x564b38);
            Raster.drawHorizontalLine(x, y + 12 + l1 + k1, 10, 0x594e3a);
            Raster.drawHorizontalLine(x, y + 12 + l1 + k1, 7, 0x5d513c);
            Raster.drawHorizontalLine(x, y + 12 + l1 + k1, 4, 0x61553e);
            Raster.drawHorizontalLine(x, y + 12 + l1 + k1, 3, 0x6e6046);
            Raster.drawHorizontalLine(x, y + 12 + l1 + k1, 2, 0x7b6a4d);
            Raster.drawHorizontalLine(x, y + 12 + l1 + k1, 1, 0x000001);
            Raster.drawHorizontalLine(x, y + 11 + l1 + k1, 16, 0x000001);
            Raster.drawHorizontalLine(x, y + 11 + l1 + k1, 15, 0x4b4131);
            Raster.drawHorizontalLine(x, y + 11 + l1 + k1, 14, 0x514635);
            Raster.drawHorizontalLine(x, y + 11 + l1 + k1, 13, 0x564b38);
            Raster.drawHorizontalLine(x, y + 11 + l1 + k1, 11, 0x594e3a);
            Raster.drawHorizontalLine(x, y + 11 + l1 + k1, 9, 0x5d513c);
            Raster.drawHorizontalLine(x, y + 11 + l1 + k1, 7, 0x61553e);
            Raster.drawHorizontalLine(x, y + 11 + l1 + k1, 5, 0x655841);
            Raster.drawHorizontalLine(x, y + 11 + l1 + k1, 4, 0x6a5c43);
            Raster.drawHorizontalLine(x, y + 11 + l1 + k1, 3, 0x73654a);
            Raster.drawHorizontalLine(x, y + 11 + l1 + k1, 2, 0x7b6a4d);
            Raster.drawHorizontalLine(x, y + 11 + l1 + k1, 1, 0x000001);
        }
    }

    private void updateNPCs(Buffer stream, int i) {
        removedMobCount = 0;
        mobsAwaitingUpdateCount = 0;
        method139(stream);
        method46(i, stream);
        method86(stream);
        for (int k = 0; k < removedMobCount; k++) {
            int l = removedMobs[k];
            if (npcs[l].time != tick) {
                npcs[l].definition = null;
                npcs[l] = null;
            }
        }

        if (stream.currentOffset != i) {
            Utility.reporterror(myUsername + " size mismatch in getnpcpos - pos:" + stream.currentOffset + " psize:" + i);
            throw new RuntimeException("eek");
        }
        for (int i1 = 0; i1 < npcCount; i1++)
            if (npcs[npcIndices[i1]] == null) {
                Utility.reporterror(myUsername + " null entry in npc list - pos:" + i1 + " size:" + npcCount);
                throw new RuntimeException("eek");
            }

    }

    private int cButtonHPos;
    private int cButtonCPos;
    private int setChannel;

    public void processChatModeClick() {
        final int yOffset = frameMode == ScreenMode.FIXED ? 0 : frameHeight - 503;

        int frame = getCurrentGameFrame();

        switch (frame) {

            case 459:
                if (super.lastMetaModifier == 1) {
                    // public
                    if (super.lastClickX >= 10 && super.lastClickX <= 97 && super.lastClickY >= yOffset + 469 && super.lastClickY <= yOffset + 497) {
                        if (frameMode != ScreenMode.FIXED) {
                            if (setChannel != 2 && frameMode != ScreenMode.FIXED) {
                                cButtonCPos = 2;
                                chatTypeView = 1;
                                redrawDialogueBox = true;
                                setChannel = 2;
                            } else {
                                showChatComponents = showChatComponents ? false : true;
                            }
                        } else {
                            cButtonCPos = 2;
                            chatTypeView = 1;
                            redrawDialogueBox = true;
                            setChannel = 2;
                        }
                        // private
                    } else if (super.lastClickX >= 139 && super.lastClickX <= 227 && super.lastClickY >= yOffset + 468 && super.lastClickY <= yOffset + 496) {
                        if (frameMode != ScreenMode.FIXED) {
                            if (setChannel != 3 && frameMode != ScreenMode.FIXED) {
                                cButtonCPos = 3;
                                chatTypeView = 2;
                                redrawDialogueBox = true;
                                setChannel = 3;
                            } else {
                                showChatComponents = showChatComponents ? false : true;
                            }
                        } else {
                            cButtonCPos = 3;
                            chatTypeView = 2;
                            redrawDialogueBox = true;
                            setChannel = 3;
                        }
                        // trade
                    } else if (super.lastClickX >= 280 && super.lastClickX <= 367 && super.lastClickY >= yOffset + 468 && super.lastClickY <= yOffset + 496) {
                        if (frameMode != ScreenMode.FIXED) {
                            if (setChannel != 5 && frameMode != ScreenMode.FIXED) {
                                cButtonCPos = 5;
                                chatTypeView = 3;
                                redrawDialogueBox = true;
                                setChannel = 5;
                            } else {
                                showChatComponents = showChatComponents ? false : true;
                            }
                        } else {
                            cButtonCPos = 5;
                            chatTypeView = 3;
                            redrawDialogueBox = true;
                            setChannel = 5;
                        }
                        // report
                    } else if (super.lastClickX >= 417 && super.lastClickX <= 503 && super.lastClickY >= yOffset + 468 && super.lastClickY <= yOffset + 496) {
                        Utility.launchURL("www.nardah.com");
                    }
                }
                break;

            default:
                if (super.mouseX >= 5 && super.mouseX <= 61 && super.mouseY >= yOffset + 482 && super.mouseY <= yOffset + 503) {
                    cButtonHPos = 0;
                    redrawDialogueBox = true;
                } else if (super.mouseX >= 71 && super.mouseX <= 127 && super.mouseY >= yOffset + 482 && super.mouseY <= yOffset + 503) {
                    cButtonHPos = 1;
                    redrawDialogueBox = true;
                } else if (super.mouseX >= 137 && super.mouseX <= 193 && super.mouseY >= yOffset + 482 && super.mouseY <= yOffset + 503) {
                    cButtonHPos = 2;
                    redrawDialogueBox = true;
                } else if (super.mouseX >= 203 && super.mouseX <= 259 && super.mouseY >= yOffset + 482 && super.mouseY <= yOffset + 503) {
                    cButtonHPos = 3;
                    redrawDialogueBox = true;
                } else if (super.mouseX >= 269 && super.mouseX <= 325 && super.mouseY >= yOffset + 482 && super.mouseY <= yOffset + 503) {
                    cButtonHPos = 4;
                    redrawDialogueBox = true;
                } else if (super.mouseX >= 335 && super.mouseX <= 391 && super.mouseY >= yOffset + 482 && super.mouseY <= yOffset + 503) {
                    cButtonHPos = 5;
                    redrawDialogueBox = true;
                } else if (super.mouseX >= 404 && super.mouseX <= 515 && super.mouseY >= yOffset + 482 && super.mouseY <= yOffset + 503) {
                    cButtonHPos = 6;
                    redrawDialogueBox = true;
                } else {
                    cButtonHPos = -1;
                    redrawDialogueBox = true;
                }

                if (super.lastMetaModifier == 1) {
                    if (super.lastClickX >= 5 && super.lastClickX <= 61 && super.lastClickY >= yOffset + 482 && super.lastClickY <= yOffset + 505) {
                        if (frameMode != ScreenMode.FIXED) {
                            if (setChannel != 0) {
                                cButtonCPos = 0;
                                chatTypeView = 0;
                                redrawDialogueBox = true;
                                setChannel = 0;
                            } else {
                                showChatComponents = showChatComponents ? false : true;
                            }
                        } else {
                            cButtonCPos = 0;
                            chatTypeView = 0;
                            redrawDialogueBox = true;
                            setChannel = 0;
                        }
                    } else if (super.lastClickX >= 71 && super.lastClickX <= 127 && super.lastClickY >= yOffset + 482 && super.lastClickY <= yOffset + 505) {
                        if (frameMode != ScreenMode.FIXED) {
                            if (setChannel != 1 && frameMode != ScreenMode.FIXED) {
                                cButtonCPos = 1;
                                chatTypeView = 5;
                                redrawDialogueBox = true;
                                setChannel = 1;
                            } else {
                                showChatComponents = showChatComponents ? false : true;
                            }
                        } else {
                            cButtonCPos = 1;
                            chatTypeView = 5;
                            redrawDialogueBox = true;
                            setChannel = 1;
                        }
                    } else if (super.lastClickX >= 137 && super.lastClickX <= 193 && super.lastClickY >= yOffset + 482 && super.lastClickY <= yOffset + 505) {
                        if (frameMode != ScreenMode.FIXED) {
                            if (setChannel != 2 && frameMode != ScreenMode.FIXED) {
                                cButtonCPos = 2;
                                chatTypeView = 1;
                                redrawDialogueBox = true;
                                setChannel = 2;
                            } else {
                                showChatComponents = showChatComponents ? false : true;
                            }
                        } else {
                            cButtonCPos = 2;
                            chatTypeView = 1;
                            redrawDialogueBox = true;
                            setChannel = 2;
                        }
                    } else if (super.lastClickX >= 203 && super.lastClickX <= 259 && super.lastClickY >= yOffset + 482 && super.lastClickY <= yOffset + 505) {
                        if (frameMode != ScreenMode.FIXED) {
                            if (setChannel != 3 && frameMode != ScreenMode.FIXED) {
                                cButtonCPos = 3;
                                chatTypeView = 2;
                                redrawDialogueBox = true;
                                setChannel = 3;
                            } else {
                                showChatComponents = showChatComponents ? false : true;
                            }
                        } else {
                            cButtonCPos = 3;
                            chatTypeView = 2;
                            redrawDialogueBox = true;
                            setChannel = 3;
                        }
                    } else if (super.lastClickX >= 269 && super.lastClickX <= 325 && super.lastClickY >= yOffset + 482 && super.lastClickY <= yOffset + 505) {
                        if (frameMode != ScreenMode.FIXED) {
                            if (setChannel != 4 && frameMode != ScreenMode.FIXED) {
                                cButtonCPos = 4;
                                chatTypeView = 11;
                                redrawDialogueBox = true;
                                setChannel = 4;
                            } else {
                                showChatComponents = showChatComponents ? false : true;
                            }
                        } else {
                            cButtonCPos = 4;
                            chatTypeView = 11;
                            redrawDialogueBox = true;
                            setChannel = 4;
                        }
                    } else if (super.lastClickX >= 335 && super.lastClickX <= 391 && super.lastClickY >= yOffset + 482 && super.lastClickY <= yOffset + 505) {
                        if (frameMode != ScreenMode.FIXED) {
                            if (setChannel != 5 && frameMode != ScreenMode.FIXED) {
                                cButtonCPos = 5;
                                chatTypeView = 3;
                                redrawDialogueBox = true;
                                setChannel = 5;
                            } else {
                                showChatComponents = showChatComponents ? false : true;
                            }
                        } else {
                            cButtonCPos = 5;
                            chatTypeView = 3;
                            redrawDialogueBox = true;
                            setChannel = 5;
                        }
                    } else if (super.lastClickX >= 404 && super.lastClickX <= 515 && super.lastClickY >= yOffset + 482 && super.lastClickY <= yOffset + 505) {
                        Utility.launchURL("www.nardah.com");
                    }
                }
                break;
        }

    }

    private void method33(int i) {
        try {
            int j = Varp.varps[i].parameter;
            if (j == 0)
                return;

            int k = settings[i];
            if (j == 1) {
                if (k == 1)
                    Rasterizer.method372(0.90000000000000002D);
                if (k == 2)
                    Rasterizer.method372(0.80000000000000004D);
                if (k == 3)
                    Rasterizer.method372(0.69999999999999996D);
                if (k == 4)
                    Rasterizer.method372(0.59999999999999998D);
                ItemDefinition.mruNodes1.unlinkAll();
                drawGameScreenSprite = true;
            }
            if (j == 3) {
                boolean flag1 = musicEnabled;
                if (k == 0) {
                    //adjustVolume(musicEnabled, 0);
                    musicEnabled = true;
                }
                if (k == 1) {
                    //adjustVolume(musicEnabled, -400);
                    musicEnabled = true;
                }
                if (k == 2) {
                    // adjustVolume(musicEnabled, -800);
                    musicEnabled = true;
                }
                if (k == 3) {
                    // adjustVolume(musicEnabled, -1200);
                    musicEnabled = true;
                }
                if (k == 4)
                    musicEnabled = false;
                if (musicEnabled != flag1 && !lowMem) {
                    if (musicEnabled) {
                        nextSong = currentSong;
                        songChanging = true;
                        onDemandFetcher.provide(2, nextSong);
                    }
                    prevSong = 0;
                }
            }
            if (j == 4) {
                if (k == 0) {
                    aBoolean848 = true;
                    //setWaveVolume(0);
                }
                if (k == 1) {
                    aBoolean848 = true;
                    //setWaveVolume(-400);
                }
                if (k == 2) {
                    aBoolean848 = true;
                    //setWaveVolume(-800);
                }
                if (k == 3) {
                    aBoolean848 = true;
                    // setWaveVolume(-1200);
                }
                if (k == 4)
                    aBoolean848 = false;
            }
            if (j == 5)
                anInt1253 = k;
            if (j == 6)
                anInt1249 = k;
            if (j == 8) {
                splitPrivateChat = k;
                redrawDialogueBox = true;
            }
        } catch (Exception e) {
        }
    }

    private void method38() {
        for (int i = -1; i < playerCount; i++) {
            int j;
            if (i == -1)
                j = myPlayerIndex;
            else
                j = playerIndices[i];
            Player player = playerArray[j];
            if (player != null && player.textCycle > 0) {
                player.textCycle--;
                if (player.textCycle == 0)
                    player.textSpoken = null;
            }
        }
        for (int k = 0; k < npcCount; k++) {
            int l = npcIndices[k];
            Npc npc = npcs[l];
            if (npc != null && npc.textCycle > 0) {
                npc.textCycle--;
                if (npc.textCycle == 0)
                    npc.textSpoken = null;
            }
        }
    }

    public StreamLoader mediaStreamLoader;

    private final int[] hitmarks562 = {31, 32, 33, 34};

    public void updateEntities() {
        try {
            int anInt974 = 0;
            for (int j = -1; j < playerCount + npcCount; j++) {
                Object obj;
                if (j == -1)
                    obj = localPlayer;
                else if (j < playerCount)
                    obj = playerArray[playerIndices[j]];
                else
                    obj = npcs[npcIndices[j - playerCount]];
                if (obj == null || !((Entity) (obj)).isVisible())
                    continue;
                if (obj instanceof Npc) {
                    NpcDefinition entityDef = ((Npc) obj).definition;
                    if (entityDef.morphisms != null)
                        entityDef = entityDef.morph();
                    if (entityDef == null)
                        continue;

                    if (Settings.DISPLAY_NAMES) {
                        npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height + 15);
                        newSmallFont.drawCenteredString(entityDef.name, spriteDrawX, spriteDrawY, 0xff683a, 50);
                    }
                }
                if (j < playerCount) {
                    int l = 40;
                    Player player = (Player) obj;
                    if (player.headIcon >= 0 || player.bountyIcon >= 0) {
                        npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height + 15);
                        if (spriteDrawX > -1) {
                            if (Settings.VALUE_ICONS && player.bountyIcon >= 0 && player.bountyIcon < 5) {
                                bountyIcons[player.bountyIcon].drawSprite(spriteDrawX - 12, spriteDrawY - 30);
                                l += 20;
                            }

                            if (player.skullIcon < 2) {
                                skullIcons[player.skullIcon].drawSprite(spriteDrawX - 13, spriteDrawY - l);
                                l += 30;
                            }
                            if (player.headIcon < 13) {
                                headIcons[player.headIcon].drawSprite(spriteDrawX - 12, spriteDrawY - l);
                                l += 18;
                            }
                        }
                    }
                    if (j >= 0 && anInt855 == 10 && anInt933 == playerIndices[j]) {
                        npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height + 15);
                        if (spriteDrawX > -1)
                            headIconsHint[player.hintIcon].drawSprite(spriteDrawX - 12, spriteDrawY - l);
                    }

                    if (Settings.DISPLAY_NAMES) {
                        npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height + 15);
                        String prefix = player.clanTag.isEmpty() ? "" : "[" + player.clanTag + "] ";
                        newSmallFont.drawCenteredString(prefix + player.name, spriteDrawX, spriteDrawY - 15, 0x13ef29, 50);
                    }
                } else {
                    NpcDefinition entityDef_1 = ((Npc) obj).definition;
                    if (entityDef_1.headIcon >= 0 && entityDef_1.headIcon < headIcons.length) {
                        npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height + 15);
                        if (spriteDrawX > -1)
                            headIcons[entityDef_1.headIcon].drawSprite(spriteDrawX - 12, spriteDrawY - 30);
                    }
                    if (anInt855 == 1 && anInt1222 == npcIndices[j - playerCount] && tick % 20 < 10) {
                        npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height + 15);
                        if (spriteDrawX > -1)
                            headIconsHint[1].drawSprite(spriteDrawX - 12, spriteDrawY - 28);
                    }
                }
                if (((Entity) (obj)).textSpoken != null && (j >= playerCount || publicChatMode == 0 || publicChatMode == 3 || publicChatMode == 1 && isFriendOrSelf(((Player) obj).name))) {
                    npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height);
                    if (spriteDrawX > -1 && anInt974 < anInt975) {
                        anIntArray979[anInt974] = boldText.method384(((Entity) (obj)).textSpoken) / 2;
                        anIntArray978[anInt974] = boldText.anInt1497;
                        anIntArray976[anInt974] = spriteDrawX;
                        anIntArray977[anInt974] = spriteDrawY;
                        anIntArray980[anInt974] = ((Entity) (obj)).textColor;
                        anIntArray981[anInt974] = ((Entity) (obj)).textEffect;
                        anIntArray982[anInt974] = ((Entity) (obj)).textCycle;
                        aStringArray983[anInt974++] = ((Entity) (obj)).textSpoken;
                        if (anInt1249 == 0 && ((Entity) (obj)).textEffect >= 1 && ((Entity) (obj)).textEffect <= 3) {
                            anIntArray978[anInt974] += 10;
                            anIntArray977[anInt974] += 5;
                        }
                        if (anInt1249 == 0 && ((Entity) (obj)).textEffect == 4)
                            anIntArray979[anInt974] = 60;
                        if (anInt1249 == 0 && ((Entity) (obj)).textEffect == 5)
                            anIntArray978[anInt974] += 5;
                    }
                }
                if (((Entity) (obj)).cycleStatus > tick) {
                    npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height + 15);
                    if (spriteDrawX > -1) {
                        int limit = 30;

                        int current = ((Entity) (obj)).currentHealth;
                        int max = ((Entity) (obj)).maximumHealth;

                        if (max == 200) {
                            limit = 160;
                        }

                        int x = limit / 2;
                        int ratio = current * limit / max;
                        if (ratio > limit) {
                            ratio = limit;
                        }

                        int drawX = spriteDrawX - x;
                        int drawY = spriteDrawY - 3;

                        if (Settings.HP_BAR == 0) {
                            Raster.fillRectangle(drawX, drawY, ratio, 5, 0x00FF00);
                            Raster.fillRectangle(drawX + ratio, drawY, limit - ratio, 5, 0xFF0000);
                        } else if (Settings.HP_BAR == 1) {
                            spriteCache.get(41).drawSprite(spriteDrawX - 28, drawY);
                            Sprite sprite = Sprite.resizeSprite(spriteCache.get(40), ratio, spriteCache.get(40).height);
                            sprite.drawSprite(spriteDrawX - 28, drawY);
                        } else if (Settings.HP_BAR == 2) {
                            Raster.fillRectangle(drawX, drawY, ratio, 5, 0x4CED84);
                            Raster.fillRectangle(drawX + ratio, drawY, limit - ratio, 5, 0xF24E4E);
                            Raster.drawRectangle(drawX, drawY, limit, 6, 0x000000);
                        } else if (Settings.HP_BAR == 3) {
                            int percent = (current * 100 / max);
                            int color = percent > 25 ? percent > 50 ? 0x00FF33 : 0xFF8800 : 0xFF0000;
                            Raster.fillRectangle(drawX, drawY, ratio, 5, color);
                            Raster.fillRectangle(drawX + ratio, drawY, limit - ratio, 5, 0x000000);
                            Raster.drawRectangle(drawX, drawY, limit, 6, 0x000000);
                        }
                    }
                }
                if (Settings.HITSPLAT == 0) {
                    for (int j1 = 0; j1 < 4; j1++) {
                        if (((Entity) (obj)).hitsLoopCycle[j1] > tick) {
                            npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height / 2);
                            if (spriteDrawX > -1) {
                                if (j1 == 1)
                                    spriteDrawY -= 20;
                                if (j1 == 2) {
                                    spriteDrawX -= 15;
                                    spriteDrawY -= 10;
                                }
                                if (j1 == 3) {
                                    spriteDrawX += 15;
                                    spriteDrawY -= 10;
                                }
                                hitMarks[((Entity) (obj)).hitMarkTypes[j1]].drawSprite(spriteDrawX - 12, spriteDrawY - 12);
                                smallFont.drawText(0, "" + ((Entity) (obj)).hitArray[j1], spriteDrawY + 5, spriteDrawX);
                                smallFont.drawText(0xffffff, "" + ((Entity) (obj)).hitArray[j1], spriteDrawY + 4, spriteDrawX - 1);
                            }
                        }
                    }
                } else if (Settings.HITSPLAT == 1) {
                    for (int j2 = 0; j2 < 4; j2++) {
                        if (((Entity) (obj)).hitsLoopCycle[j2] > tick) {
                            npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height / 2);
                            if (spriteDrawX > -1) {
                                if (j2 == 0 && ((Entity) (obj)).hitArray[j2] > 99)
                                    ((Entity) (obj)).hitMarkTypes[j2] = 3;
                                else if (j2 == 1 && ((Entity) (obj)).hitArray[j2] > 99)
                                    ((Entity) (obj)).hitMarkTypes[j2] = 3;
                                else if (j2 == 2 && ((Entity) (obj)).hitArray[j2] > 99)
                                    ((Entity) (obj)).hitMarkTypes[j2] = 3;
                                else if (j2 == 3 && ((Entity) (obj)).hitArray[j2] > 99)
                                    ((Entity) (obj)).hitMarkTypes[j2] = 3;
                                if (j2 == 1) {
                                    spriteDrawY -= 20;
                                }
                                if (j2 == 2) {
                                    spriteDrawX -= (((Entity) (obj)).hitArray[j2] > 99 ? 30 : 20);
                                    spriteDrawY -= 10;
                                }
                                if (j2 == 3) {
                                    spriteDrawX += (((Entity) (obj)).hitArray[j2] > 99 ? 30 : 20);
                                    spriteDrawY -= 10;
                                }
                                if (((Entity) (obj)).hitMarkTypes[j2] == 3) {
                                    spriteDrawX -= 8;
                                }
                                spriteCache.get(hitmarks562[((Entity) (obj)).hitMarkTypes[j2]]).draw24BitSprite(spriteDrawX - 12, spriteDrawY - 12);
                                smallFont.drawText(0xffffff, "" + ((Entity) (obj)).hitArray[j2], spriteDrawY + 3, (((Entity) (obj)).hitMarkTypes[j2] == 3 ? spriteDrawX + 7 : spriteDrawX - 1));
                            }
                        }
                    }
                } else if (Settings.HITSPLAT == 2) {
                    for (int j1 = 0; j1 < 4; j1++) {
                        if (((Entity) (obj)).hitsLoopCycle[j1] > tick) {
                            npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height / 2);
                            if (spriteDrawX > -1) {
                                switch (j1) {
                                    case 1:
                                        spriteDrawY += 20;
                                        break;
                                    case 2:
                                        spriteDrawY += 40;
                                        break;
                                    case 3:
                                        spriteDrawY += 60;
                                        break;
                                    case 4:
                                        spriteDrawY += 80;
                                        break;
                                    case 5:
                                        spriteDrawY += 100;
                                        break;
                                    case 6:
                                        spriteDrawY += 120;
                                        break;
                                }
                                Entity e = ((Entity) (obj));
                                if (e.hitmarkMove[j1] > -5)
                                    e.hitmarkMove[j1] -= 0.25;
                                if (e.hitmarkMove[j1] < -26)
                                    e.hitmarkTrans[j1] -= 5;
                                hitmarkDraw(("" + e.hitArray[j1]).length(), e.hitMarkTypes[j1], e.hitIcon[j1], e.hitArray[j1], e.hitmarkMove[j1], e.hitmarkTrans[j1]);
                            }
                        }
                    }
                }
            }
            for (int k = 0; k < anInt974; k++) {
                int k1 = anIntArray976[k];
                int l1 = anIntArray977[k];
                int j2 = anIntArray979[k];
                int k2 = anIntArray978[k];
                boolean flag = true;
                while (flag) {
                    flag = false;
                    for (int l2 = 0; l2 < k; l2++)
                        if (l1 + 2 > anIntArray977[l2] - anIntArray978[l2] && l1 - k2 < anIntArray977[l2] + 2 && k1 - j2 < anIntArray976[l2] + anIntArray979[l2] && k1 + j2 > anIntArray976[l2] - anIntArray979[l2] && anIntArray977[l2] - anIntArray978[l2] < l1) {
                            l1 = anIntArray977[l2] - anIntArray978[l2];
                            flag = true;
                        }

                }
                spriteDrawX = anIntArray976[k];
                spriteDrawY = anIntArray977[k] = l1;
                String s = aStringArray983[k];
                if (s.contains("[Click chat box to enable]")) {
                    return;
                }
                if (anInt1249 == 0) {
                    int i3 = 0xffff00;
                    if (anIntArray980[k] < 6)
                        i3 = anIntArray965[anIntArray980[k]];
                    if (anIntArray980[k] == 6)
                        i3 = anInt1265 % 20 >= 10 ? 0xffff00 : 0xff0000;
                    if (anIntArray980[k] == 7)
                        i3 = anInt1265 % 20 >= 10 ? 65535 : 255;
                    if (anIntArray980[k] == 8)
                        i3 = anInt1265 % 20 >= 10 ? 0x80ff80 : 45056;
                    if (anIntArray980[k] == 9) {
                        int j3 = 150 - anIntArray982[k];
                        if (j3 < 50)
                            i3 = 0xff0000 + 1280 * j3;
                        else if (j3 < 100)
                            i3 = 0xffff00 - 0x50000 * (j3 - 50);
                        else if (j3 < 150)
                            i3 = 65280 + 5 * (j3 - 100);
                    }
                    if (anIntArray980[k] == 10) {
                        int k3 = 150 - anIntArray982[k];
                        if (k3 < 50)
                            i3 = 0xff0000 + 5 * k3;
                        else if (k3 < 100)
                            i3 = 0xff00ff - 0x50000 * (k3 - 50);
                        else if (k3 < 150)
                            i3 = (255 + 0x50000 * (k3 - 100)) - 5 * (k3 - 100);
                    }
                    if (anIntArray980[k] == 11) {
                        int l3 = 150 - anIntArray982[k];
                        if (l3 < 50)
                            i3 = 0xffffff - 0x50005 * l3;
                        else if (l3 < 100)
                            i3 = 65280 + 0x50005 * (l3 - 50);
                        else if (l3 < 150)
                            i3 = 0xffffff - 0x50000 * (l3 - 100);
                    }
                    if (anIntArray981[k] == 0) {
                        boldText.drawText(0, s, spriteDrawY + 1, spriteDrawX);
                        boldText.drawText(i3, s, spriteDrawY, spriteDrawX);
                    }
                    if (anIntArray981[k] == 1) {
                        boldText.method386(0, s, spriteDrawX, anInt1265, spriteDrawY + 1);
                        boldText.method386(i3, s, spriteDrawX, anInt1265, spriteDrawY);
                    }
                    if (anIntArray981[k] == 2) {
                        boldText.method387(spriteDrawX, s, anInt1265, spriteDrawY + 1, 0);
                        boldText.method387(spriteDrawX, s, anInt1265, spriteDrawY, i3);
                    }
                    if (anIntArray981[k] == 3) {
                        boldText.method388(150 - anIntArray982[k], s, anInt1265, spriteDrawY + 1, spriteDrawX, 0);
                        boldText.method388(150 - anIntArray982[k], s, anInt1265, spriteDrawY, spriteDrawX, i3);
                    }
                    if (anIntArray981[k] == 4) {
                        int i4 = boldText.method384(s);
                        int k4 = ((150 - anIntArray982[k]) * (i4 + 100)) / 150;
                        Raster.setDrawingArea(spriteDrawX - 50, 0, spriteDrawX + 50, 334);
                        boldText.render(0, s, spriteDrawY + 1, (spriteDrawX + 50) - k4);
                        boldText.render(i3, s, spriteDrawY, (spriteDrawX + 50) - k4);
                        Raster.defaultDrawingAreaSize();
                    }
                    if (anIntArray981[k] == 5) {
                        int j4 = 150 - anIntArray982[k];
                        int l4 = 0;
                        if (j4 < 25)
                            l4 = j4 - 25;
                        else if (j4 > 125)
                            l4 = j4 - 125;
                        Raster.setDrawingArea(0, spriteDrawY - boldText.anInt1497 - 1, 512, spriteDrawY + 5);
                        boldText.drawText(0, s, spriteDrawY + 1 + l4, spriteDrawX);
                        boldText.drawText(i3, s, spriteDrawY + l4, spriteDrawX);
                        Raster.defaultDrawingAreaSize();
                    }
                } else {
                    boldText.drawText(0, s, spriteDrawY + 1, spriteDrawX);
                    boldText.drawText(0xffff00, s, spriteDrawY, spriteDrawX);
                }
            }
        } catch (Exception e) {
        }
    }

    private void delFriend(long username) {
        try {
            if (username == 0L) {
                return;
            }
            for (int count = 0; count < friendsCount; count++) {
                if (friendsListAsLongs[count] != username) {
                    continue;
                }
                friendsCount--;
                for (int index = count; index < friendsCount; index++) {
                    friendsList[index] = friendsList[index + 1];
                    friendsNodeIDs[index] = friendsNodeIDs[index + 1];
                    friendsListAsLongs[index] = friendsListAsLongs[index + 1];
                }
                outgoing.writeOpcode(215);
                outgoing.writeQWord(username);
                break;
            }
        } catch (RuntimeException runtimeexception) {
            Utility.reporterror("18622, " + false + ", " + username + ", " + runtimeexception.toString());
            throw new RuntimeException();
        }
    }

    public void drawSideIcons() {
        final int[] sideIconsId = {0, 1, 2, 3, 4, 5, 6, 15, 8, 9, 7, 11, 12, 13};
        final int[] sideIconsTab = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};

        final int[] iconId1 = {0, 1, 2, 3, 4, 5, 6, 15, 8, 9, 7, 11, 12, 13};
        final int[] iconX1 = {219, 189, 156, 126, 93, 62, 30, 219, 189, 156, 124, 92, 59, 28};
        final int[] iconY1 = {67, 69, 67, 69, 72, 72, 69, 28, 29, 29, 32, 30, 33, 31, 32};

        final int[] sideIconsX = {17, 49, 83, 114, 146, 180, 214, 9, 49, 82, 116, 148, 184, 216};
        final int[] sideIconsY = {9, 7, 7, 5, 2, 3, 7, 304, 306, 306, 302, 305, 303, 303, 303};

        final int[] iconId2 = {0, 1, 2, 3, 4, 5, 6, 15, 8, 9, 7, 11, 12, 13};
        final int[] iconX2 = {50, 80, 114, 143, 176, 208, 240, 273, 306, 338, 370, 402, 433, 433};
        final int[] iconY2 = {30, 32, 30, 32, 34, 34, 32, 28, 29, 29, 32, 31, 32, 32, 32};

        final int[] sideIconsX459 = {33, 56, 86, 115, 156, 183, 211, 30, 57, 85, 121, 156, 188, 212}, sideIconsY459 = {10, 6, 7, 4, 6, 5, 8, 300, 303, 304, 302, 302, 303, 303}, sideIconsId459 = {0, 1, 2, 3, 4, 5, 6, 15, 8, 9, 7, 11, 12, 13}, sideIconsTab459 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};

        int xOffset = frameMode == ScreenMode.FIXED ? 0 : frameWidth - 247;
        int yOffset = frameMode == ScreenMode.FIXED ? 0 : frameHeight - 336;

        final int frame = getCurrentGameFrame();

        if (frame == 459) {
            if (frameMode == ScreenMode.FIXED || frameMode != ScreenMode.FIXED && !changeTabArea) {
                for (int index = 0; index < sideIconsTab459.length; index++) {
                    if (tabInterfaceIDs[sideIconsTab459[index]] != -1) {
                        if (sideIconsId459[index] != -1) {
                            sideIcons[sideIconsId459[index]].drawSprite(sideIconsX459[index] + xOffset, sideIconsY459[index] + yOffset);
                        }
                    }
                }
            } else if (changeTabArea && frameWidth < 1000) {
                for (int i = 0; i < sideIconsTab459.length; i++) {
                    if (tabInterfaceIDs[sideIconsTab459[i]] != -1) {
                        if (sideIconsId459[i] != -1) {
                            sideIcons[sideIconsId459[i]].drawSprite(frameWidth - iconX1[i], frameHeight - iconY1[i]);
                        }
                    }
                }
            } else if (changeTabArea && frameWidth >= 1000) {
                for (int i = 0; i < sideIconsTab459.length; i++) {
                    if (tabInterfaceIDs[sideIconsTab459[i]] != -1) {
                        if (sideIconsId459[i] != -1) {
                            sideIcons[sideIconsId459[i]].drawSprite(frameWidth - 461 + iconX2[i], frameHeight - iconY2[i]);
                        }
                    }
                }
            }

        } else {
            if (frameMode == ScreenMode.FIXED || frameMode != ScreenMode.FIXED && !changeTabArea) {
                for (int index = 0; index < sideIconsTab.length; index++) {
                    if (tabInterfaceIDs[sideIconsTab[index]] != -1) {
                        if (sideIconsId[index] != -1) {
                            sideIcons[sideIconsId[index]].drawSprite(sideIconsX[index] + xOffset, sideIconsY[index] + yOffset);
                        }
                    }
                }
            } else if (changeTabArea && frameWidth < 1000) {
                for (int i = 0; i < sideIconsTab.length; i++) {
                    if (tabInterfaceIDs[sideIconsTab[i]] != -1) {
                        if (iconId1[i] != -1) {
                            sideIcons[iconId1[i]].drawSprite(frameWidth - iconX1[i], frameHeight - iconY1[i]);
                        }
                    }
                }
            } else if (changeTabArea && frameWidth >= 1000) {
                for (int i = 0; i < sideIconsTab.length; i++) {
                    if (tabInterfaceIDs[sideIconsTab[i]] != -1) {
                        if (iconId2[i] != -1) {
                            sideIcons[iconId2[i]].drawSprite(frameWidth - 461 + iconX2[i], frameHeight - iconY2[i]);
                        }
                    }
                }
            }
        }
    }

    private void drawRedStones() {

        final int[] redStonesX = {6, 44, 77, 110, 143, 176, 209, 6, 44, 77, 110, 143, 176, 209}, redStonesY = {0, 0, 0, 0, 0, 0, 0, 298, 298, 298, 298, 298, 298, 298}, redStonesId = {35, 39, 39, 39, 39, 39, 36, 37, 39, 39, 39, 39, 39, 38};

        final int[] stoneX1 = {226, 194, 162, 130, 99, 65, 34, 219, 195, 161, 130, 98, 65, 33};
        final int[] stoneY1 = {73, 73, 73, 73, 73, 73, 73, -1, 37, 37, 37, 37, 37, 37, 37};
        final int[] stoneX2 = {417, 385, 353, 321, 289, 256, 224, 193, 161, 130, 98, 65, 33, 33};

        int xOffset = frameMode == ScreenMode.FIXED ? 0 : frameWidth - 247;
        int yOffset = frameMode == ScreenMode.FIXED ? 0 : frameHeight - 336;

        final int frame = getCurrentGameFrame();

        if (frame == 459) {
            if (frameMode == ScreenMode.FIXED || frameMode != ScreenMode.FIXED && !changeTabArea) {
                if (tabInterfaceIDs[tabID] != -1 && tabID != 14) {
                    switch (tabID) {
                        case 0:
                            spriteCache.get(378).drawSprite(21 + xOffset, 0 + yOffset);
                            break;
                        case 1:
                            spriteCache.get(383).drawSprite(54 + xOffset, 1 + yOffset);
                            break;
                        case 2:
                            spriteCache.get(383).drawSprite(82 + xOffset, 1 + yOffset);
                            break;
                        case 3:
                            spriteCache.get(380).drawSprite(110 + xOffset, 0 + yOffset);
                            break;
                        case 4:
                            spriteCache.get(385).drawSprite(153 + xOffset, 1 + yOffset);
                            break;
                        case 5:
                            spriteCache.get(385).drawSprite(181 + xOffset, 1 + yOffset);
                            break;
                        case 6:
                            spriteCache.get(379).drawSprite(209 + xOffset, 2 + yOffset);
                            break;
                        case 7:
                            spriteCache.get(381).drawSprite(22 + xOffset, 297 + yOffset);
                            break;
                        case 8:
                            spriteCache.get(386).drawSprite(54 + xOffset, 297 + yOffset);
                            break;
                        case 9:
                            spriteCache.get(386).drawSprite(82 + xOffset, 297 + yOffset);
                            break;
                        case 10:
                            spriteCache.get(384).drawSprite(110 + xOffset, 299 + yOffset);
                            break;
                        case 11:
                            spriteCache.get(387).drawSprite(153 + xOffset, 297 + yOffset);
                            break;
                        case 12:
                            spriteCache.get(387).drawSprite(181 + xOffset, 297 + yOffset);
                            break;
                        case 13:
                            spriteCache.get(382).drawSprite(209 + xOffset, 297 + yOffset);
                            break;
                    }
                }
            } else if (changeTabArea && frameWidth < 1000) {

            } else if (changeTabArea && frameWidth >= 1000) {

            }

        } else {
            if (frameMode == ScreenMode.FIXED || frameMode != ScreenMode.FIXED && !changeTabArea) {
                if (tabInterfaceIDs[tabID] != -1 && tabID != 14) {
                    spriteCache.get(redStonesId[tabID]).drawSprite(redStonesX[tabID] + xOffset, redStonesY[tabID] + yOffset);
                }
            } else if (changeTabArea && frameWidth < 1000) {
                if (tabInterfaceIDs[tabID] != -1 && tabID != 14 && showTabComponents) {
                    if (tabID == 7) {
                        spriteCache.get(39).drawSprite(frameWidth - 226, frameHeight - 37);
                    }
                    spriteCache.get(39).drawSprite(frameWidth - stoneX1[tabID], frameHeight - stoneY1[tabID]);
                }
            } else if (changeTabArea && frameWidth >= 1000) {
                if (tabInterfaceIDs[tabID] != -1 && tabID != 14 && showTabComponents) {
                    spriteCache.get(39).drawSprite(frameWidth - stoneX2[tabID], frameHeight - 37);
                }
            }
        }
    }

    private void drawTabArea() {
        final int xOffset = frameMode == ScreenMode.FIXED ? 0 : frameWidth - 241;
        final int yOffset = frameMode == ScreenMode.FIXED ? 0 : frameHeight - 336;

        final int frame = getCurrentGameFrame();

        int invSprite = 47;

        switch (frame) {

            case 459:
                invSprite = 389;
                break;

            default:
                invSprite = 47;
                break;

        }

        if (frameMode == ScreenMode.FIXED) {
            inventoryIP.initDrawingArea();
        }
        Rasterizer.anIntArray1472 = anIntArray1181;
        if (frameMode == ScreenMode.FIXED) {
            inventorySprite.drawSprite(0, 0);
        } else if (frameMode != ScreenMode.FIXED && !changeTabArea) {
            Raster.fillRectangle(frameWidth - 217, frameHeight - 304, 195, 270, 0x3E3529, transparentTabArea ? 200 : 256);
            spriteCache.get(invSprite).drawSprite(xOffset, yOffset);
        } else {
            if (frameWidth >= 1000) {
                if (showTabComponents) {
                    Raster.fillRectangle(frameWidth - 197, frameHeight - 304, 197, 265, 0x3E3529, transparentTabArea ? 200 : 256);
                    spriteCache.get(50).drawSprite(frameWidth - 204, frameHeight - 311);
                }
                for (int x = frameWidth - 417, y = frameHeight - 37, index = 0; x <= frameWidth - 30 && index < 13; x += 32, index++) {
                    spriteCache.get(46).drawSprite(x, y);
                }
            } else if (frameWidth < 1000) {
                if (showTabComponents) {
                    Raster.fillRectangle(frameWidth - 197, frameHeight - 341, 195, 265, 0x3E3529, transparentTabArea ? 200 : 256);
                    spriteCache.get(50).drawSprite(frameWidth - 204, frameHeight - 348);
                }
                for (int x = frameWidth - 226, y = frameHeight - 73, index = 0; x <= frameWidth - 32 && index < 7; x += 32, index++) {
                    spriteCache.get(46).drawSprite(x, y);
                }
                for (int x = frameWidth - 226, y = frameHeight - 37, index = 0; x <= frameWidth - 32 && index < 7; x += 32, index++) {
                    spriteCache.get(46).drawSprite(x, y);
                }
            }
        }
        if (invOverlayInterfaceID == -1) {
            drawRedStones();
            drawSideIcons();
        }
        if (showTabComponents) {
            int x = frameMode == ScreenMode.FIXED ? 31 : frameWidth - 215;
            int y = frameMode == ScreenMode.FIXED ? 37 : frameHeight - 299;
            if (changeTabArea) {
                x = frameWidth - 197;
                y = frameWidth >= 1000 ? frameHeight - 303 : frameHeight - 340;
            }
            if (invOverlayInterfaceID != -1) {
                drawInterface(RSInterface.getInterfaceCache()[invOverlayInterfaceID], x, y, 0);
            } else if (tabInterfaceIDs[tabID] != -1) {
                drawInterface(RSInterface.getInterfaceCache()[tabInterfaceIDs[tabID]], this.getCurrentGameFrame() != 474 ? x + 5 : x, y, 0);
                if (tabID == 5 && prayerGrabbed != null) {
                	RSInterface.getInterfaceCache()[prayerGrabbed.spriteId].enabledSprite.drawTransparentSprite(frameMode == ScreenMode.FIXED ? super.mouseX - 528 : super.mouseX - 12, frameMode == ScreenMode.FIXED ? super.mouseY - 180 : super.mouseY - 12, 100);
                }
            }
		if (achievementHovered != null) {
            	int dx = frameMode == ScreenMode.FIXED ? super.mouseX - 516 : super.mouseX;
            	int dy = frameMode == ScreenMode.FIXED ? super.mouseY - 169 : super.mouseY;
            	int maxX = frameMode == ScreenMode.FIXED ? frameWidth - 516 - 145 : frameWidth - 145;
            	dx = dx > maxX ? maxX : dx;
            	drawInterface(RSInterface.getInterfaceCache()[37400], dx, dy, 0);
            }
            if (Settings.DRAW_ITEM_HOVERS && itemHover > -1 && super.mouseX > getScreenWidth() - 300) {
                if (frameMode == ScreenMode.FIXED) {
                    x = 516;
                    y = 169;
                    if (super.mouseX > x && super.mouseY > y) {
                        if (controlIsDown && ItemStats.itemstats[itemHover] != null && ItemStats.itemstats[itemHover].type == 1) {
                            drawInterface(RSInterface.getInterfaceCache()[23000], getItemHoverX(true, x, false), getItemHoverY(true, y, false), 0);
                        } else {
                            drawInterface(RSInterface.getInterfaceCache()[23020], getItemHoverX(true, x, true), getItemHoverY(true, y, true), 0);
                        }
                    }
                } else {
                    if (super.mouseX > x && super.mouseY > y) {
                        if (controlIsDown && ItemStats.itemstats[itemHover] != null && ItemStats.itemstats[itemHover].type == 1) {
                            drawInterface(RSInterface.getInterfaceCache()[23000], getItemHoverX(true, x, false), getItemHoverY(true, y, false), 0);
                        } else {
                            drawInterface(RSInterface.getInterfaceCache()[23020], getItemHoverX(true, x, true), getItemHoverY(true, y, true), 0);
                        }
                    }
                }
            }

        }
        if (menuOpen) {
            drawMenu(frameMode == ScreenMode.FIXED ? 516 : 0, frameMode == ScreenMode.FIXED ? 168 : 0);
        }
        if (frameMode == ScreenMode.FIXED) {
            inventoryIP.drawGraphics(super.graphics, 516, 168);
            gameScreenIP.initDrawingArea();
        }
        Rasterizer.anIntArray1472 = anIntArray1182;
    }

    private void method37(int j) {
        if (Settings.MOVING_TEXTURE) {
            if (Rasterizer.anIntArray1480[1] >= j) {
                Background background_1 = Rasterizer.aBackgroundArray1474s[1];
                int l = background_1.anInt1452 * background_1.anInt1453 - 1;
                int k1 = background_1.anInt1452 * tickDelta * 2;
                byte abyte1[] = background_1.aByteArray1450;
                byte abyte4[] = aByteArray912;
                for (int j2 = 0; j2 <= l; j2++)
                    abyte4[j2] = abyte1[j2 - k1 & l];

                background_1.aByteArray1450 = abyte4;
                abyte4 = null;
                aByteArray912 = abyte1;
                abyte1 = null;
                Rasterizer.method370(1);
            }
            if (Rasterizer.anIntArray1480[17] >= j) {
                Background background = Rasterizer.aBackgroundArray1474s[17];
                int k = background.anInt1452 * background.anInt1453 - 1;
                int j1 = background.anInt1452 * tickDelta * 2;
                byte abyte0[] = background.aByteArray1450;
                byte abyte3[] = aByteArray912;
                for (int i2 = 0; i2 <= k; i2++)
                    abyte3[i2] = abyte0[i2 - j1 & k];

                background.aByteArray1450 = abyte3;
                abyte3 = null;
                aByteArray912 = abyte0;
                abyte0 = null;
                Rasterizer.method370(17);
            }
            if (Rasterizer.anIntArray1480[34] >= j) {
                Background background_2 = Rasterizer.aBackgroundArray1474s[34];
                int i1 = background_2.anInt1452 * background_2.anInt1453 - 1;
                int l1 = background_2.anInt1452 * tickDelta * 2;
                byte abyte2[] = background_2.aByteArray1450;
                byte abyte5[] = aByteArray912;
                for (int k2 = 0; k2 <= i1; k2++)
                    abyte5[k2] = abyte2[k2 - l1 & i1];

                background_2.aByteArray1450 = abyte5;
                abyte5 = null;
                aByteArray912 = abyte2;
                abyte2 = null;
                Rasterizer.method370(34);
            }
            if (Rasterizer.anIntArray1480[40] >= j) {
                Background background_2 = Rasterizer.aBackgroundArray1474s[40];
                int i1 = background_2.anInt1452 * background_2.anInt1453 - 1;
                int l1 = background_2.anInt1452 * tickDelta * 2;
                byte abyte2[] = background_2.aByteArray1450;
                byte abyte5[] = aByteArray912;
                for (int k2 = 0; k2 <= i1; k2++)
                    abyte5[k2] = abyte2[k2 - l1 & i1];

                background_2.aByteArray1450 = abyte5;
                abyte5 = null;
                aByteArray912 = abyte2;
                abyte2 = null;
                Rasterizer.method370(40);
            }
            if (Rasterizer.anIntArray1480[59] >= j) {//shit alright sweety
                Background background_2 = Rasterizer.aBackgroundArray1474s[59];
                int i1 = background_2.anInt1452 * background_2.anInt1453 - 1;
                int l1 = background_2.anInt1452 * tickDelta * 2;
                byte abyte2[] = background_2.aByteArray1450;
                byte abyte5[] = aByteArray912;
                for (int k2 = 0; k2 <= i1; k2++)
                    abyte5[k2] = abyte2[k2 - l1 & i1];

                background_2.aByteArray1450 = abyte5;
                abyte5 = null;
                aByteArray912 = abyte2;
                abyte2 = null;
                Rasterizer.method370(59);
            }
        }
    }

    private void textSpokenEvent() {
        for (int i = -1; i < playerCount; i++) {
            int j;
            if (i == -1)
                j = myPlayerIndex;
            else
                j = playerIndices[i];
            Player player = playerArray[j];
            if (player.textSpoken == null) {
                return;
            }
            if (player != null && player.textCycle > 0) {
                player.textCycle--;

                if (player.textCycle == 0)
                    player.textSpoken = null;
                System.out.println("Player count " + playerCount +  " New message - " + player.textSpoken + " cycle - " + player.textCycle);
            }
        }
        for (int k = 0; k < npcCount; k++) {
            int l = npcIndices[k];
            Npc npc = npcs[l];
            if (npc.textSpoken == null) {
                return;
            }
            if (npc != null && npc.textCycle > 0) {
                npc.textCycle--;
                if (npc.textCycle == 0)
                    npc.textSpoken = null;
            }
        }
    }

    private void calcCameraPos() {
        int i = anInt1098 * 128 + 64;
        int j = anInt1099 * 128 + 64;
        int k = method42(plane, j, i) - anInt1100;
        if (xCameraPos < i) {
            xCameraPos += anInt1101 + ((i - xCameraPos) * anInt1102) / 1000;
            if (xCameraPos > i)
                xCameraPos = i;
        }
        if (xCameraPos > i) {
            xCameraPos -= anInt1101 + ((xCameraPos - i) * anInt1102) / 1000;
            if (xCameraPos < i)
                xCameraPos = i;
        }
        if (zCameraPos < k) {
            zCameraPos += anInt1101 + ((k - zCameraPos) * anInt1102) / 1000;
            if (zCameraPos > k)
                zCameraPos = k;
        }
        if (zCameraPos > k) {
            zCameraPos -= anInt1101 + ((zCameraPos - k) * anInt1102) / 1000;
            if (zCameraPos < k)
                zCameraPos = k;
        }
        if (yCameraPos < j) {
            yCameraPos += anInt1101 + ((j - yCameraPos) * anInt1102) / 1000;
            if (yCameraPos > j)
                yCameraPos = j;
        }
        if (yCameraPos > j) {
            yCameraPos -= anInt1101 + ((yCameraPos - j) * anInt1102) / 1000;
            if (yCameraPos < j)
                yCameraPos = j;
        }
        i = anInt995 * 128 + 64;
        j = anInt996 * 128 + 64;
        k = method42(plane, j, i) - anInt997;
        int l = i - xCameraPos;
        int i1 = k - zCameraPos;
        int j1 = j - yCameraPos;
        int k1 = (int) Math.sqrt(l * l + j1 * j1);
        int l1 = (int) (Math.atan2(i1, k1) * 325.94900000000001D) & 0x7ff;
        int i2 = (int) (Math.atan2(l, j1) * -325.94900000000001D) & 0x7ff;
        if (l1 < 128)
            l1 = 128;
        if (l1 > 383)
            l1 = 383;
        if (yCameraCurve < l1) {
            yCameraCurve += anInt998 + ((l1 - yCameraCurve) * anInt999) / 1000;
            if (yCameraCurve > l1)
                yCameraCurve = l1;
        }
        if (yCameraCurve > l1) {
            yCameraCurve -= anInt998 + ((yCameraCurve - l1) * anInt999) / 1000;
            if (yCameraCurve < l1)
                yCameraCurve = l1;
        }
        int j2 = i2 - xCameraCurve;
        if (j2 > 1024)
            j2 -= 2048;
        if (j2 < -1024)
            j2 += 2048;
        if (j2 > 0) {
            xCameraCurve += anInt998 + (j2 * anInt999) / 1000;
            xCameraCurve &= 0x7ff;
        }
        if (j2 < 0) {
            xCameraCurve -= anInt998 + (-j2 * anInt999) / 1000;
            xCameraCurve &= 0x7ff;
        }
        int k2 = i2 - xCameraCurve;
        if (k2 > 1024)
            k2 -= 2048;
        if (k2 < -1024)
            k2 += 2048;
        if (k2 < 0 && j2 > 0 || k2 > 0 && j2 < 0)
            xCameraCurve = i2;

//        method38();
    }

    public void drawMenu(int xOffSet, int yOffSet) {
        int xPos = menuOffsetX - (xOffSet - 4);
        int yPos = (-yOffSet + 4) + menuOffsetY;
        int menuW = menuWidth;
        int menuH = menuHeight + 1;
        redrawDialogueBox = true;
        tabAreaAltered = true;

        /* 317 */
        if (Settings.CONTEXT_MENU == 0 || Settings.CONTEXT_MENU == 637609985) {
            int menuColor = 0x5d5447;
            Raster.fillRectangle(xPos, yPos, menuW, menuH, menuColor);
            Raster.fillRectangle(xPos + 1, yPos + 1, menuW - 2, 16, 0);
            Raster.drawRectangle(xPos + 1, yPos + 18, menuW - 2, menuH - 19, 0);
            newBoldFont.drawBasicString("Choose Option", xPos + 3, yPos + 16, menuColor, 1);
            int mouseX = super.mouseX - (xOffSet);
            int mouseY = (-yOffSet) + super.mouseY;
            for (int i = 0; i < menuActionRow; i++) {
                int textY = yPos + 34 + (menuActionRow - 1 - i) * 15;
                int textColor = 0xffffff;
                if (mouseX > xPos && mouseX < xPos + menuW && mouseY > textY - 13 && mouseY < textY + 3) {
                    textColor = 0xffff00;
                }
                newBoldFont.drawBasicString(menuActionName[i], xPos + 3, textY, textColor, 1);
            }

            /* 525 */
        } else if (Settings.CONTEXT_MENU == 1) {
            Raster.fillRectangle(xPos, yPos + 2, menuW, menuH - 4, 0x706a5e);
            Raster.fillRectangle(xPos + 1, yPos + 1, menuW - 2, menuH - 2, 0x706a5e);
            Raster.fillRectangle(xPos + 2, yPos, menuW - 4, menuH, 0x706a5e);
            Raster.fillRectangle(xPos + 3, yPos + 1, menuW - 6, menuH - 2, 0x2d2822);
            Raster.fillRectangle(xPos, yPos + 2, menuW, menuH - 4, 0x2d2822);
            Raster.fillRectangle(xPos + 1, yPos + 3, menuW - 2, menuH - 6, 0x2d2822);
            Raster.fillRectangle(xPos + 2, yPos + 19, menuW - 4, menuH - 22, 0x524a3d);
            Raster.fillRectangle(xPos + 3, yPos + 20, menuW - 6, menuH - 22, 0x524a3d);
            Raster.fillRectangle(xPos + 3, yPos + 20, menuW - 6, menuH - 23, 0x2b271c);
            Raster.drawRectangle(xPos + 3, yPos + 2, menuW - 6, 1, 0x2a291b);
            Raster.drawRectangle(xPos + 2, yPos + 3, menuW - 4, 1, 0x2a261b);
            Raster.drawRectangle(xPos + 2, yPos + 4, menuW - 4, 1, 0x252116);
            Raster.drawRectangle(xPos + 2, yPos + 5, menuW - 4, 1, 0x211e15);
            Raster.drawRectangle(xPos + 2, yPos + 6, menuW - 4, 1, 0x1e1b12);
            Raster.drawRectangle(xPos + 2, yPos + 7, menuW - 4, 1, 0x1a170e);
            Raster.drawRectangle(xPos + 2, yPos + 8, menuW - 4, 2, 0x15120b);
            Raster.drawRectangle(xPos + 2, yPos + 10, menuW - 4, 1, 0x100d08);
            Raster.drawRectangle(xPos + 2, yPos + 11, menuW - 4, 1, 0x090a04);
            Raster.drawRectangle(xPos + 2, yPos + 12, menuW - 4, 1, 0x080703);
            Raster.drawRectangle(xPos + 2, yPos + 13, menuW - 4, 1, 0x090a04);
            Raster.drawRectangle(xPos + 2, yPos + 14, menuW - 4, 1, 0x070802);
            Raster.drawRectangle(xPos + 2, yPos + 15, menuW - 4, 1, 0x090a04);
            Raster.drawRectangle(xPos + 2, yPos + 16, menuW - 4, 1, 0x070802);
            Raster.drawRectangle(xPos + 2, yPos + 17, menuW - 4, 1, 0x090a04);
            Raster.drawRectangle(xPos + 2, yPos + 18, menuW - 4, 1, 0x2a291b);
            Raster.drawRectangle(xPos + 3, yPos + 19, menuW - 6, 1, 0x564943);
            int mouseX = super.mouseX - (xOffSet);
            int mouseY = (-yOffSet) + super.mouseY;
            for (int l1 = 0; l1 < menuActionRow; l1++) {
                int textY = yPos + 34 + (menuActionRow - 1 - l1) * 15;
                int hoverColor = 0x9C9589;
                if (mouseX > xPos && mouseX < xPos + menuW && mouseY > textY - 13 && mouseY < textY + 3) {
                    Raster.fillRectangle(xPos + 3, textY - 15, menuWidth - 6, 15, hoverColor, 50);
                }
                newBoldFont.drawBasicString(menuActionName[l1], xPos + 3, textY, 0xFFFFFF, 1);

            }
            newBoldFont.drawBasicString("Choose Option", xPos + 3, yPos + 16, 0xc6b895, 1);

            /* 634 */
        } else if (Settings.CONTEXT_MENU == 2) {
            Raster.drawRectangle(xPos, yPos + 2, menuW, menuH - 4, 0x706a5e, 256);
            Raster.drawRectangle(xPos + 1, yPos + 1, menuW - 2, menuH - 2, 0x706a5e, 256);
            Raster.drawRectangle(xPos + 2, yPos, menuW - 4, menuH, 0x706a5e, 200);
            Raster.drawRectangle(xPos + 3, yPos + 1, menuW - 6, menuH - 2, 0x6f695d, 256);
            Raster.drawRectangle(xPos + 2, yPos + 2, menuW - 4, menuH - 4, 0x6f695d, 256);
            Raster.drawRectangle(xPos + 1, yPos + 3, menuW - 2, menuH - 6, 0x6f695d, 256);
            Raster.drawRectangle(xPos + 2, yPos + 19, menuW - 4, menuH - 22, 0x524a3d, 256);
            Raster.drawRectangle(xPos + 3, yPos + 20, menuW - 6, menuH - 22, 0x524a3d, 256);
            Raster.fillRectangle(xPos + 3, yPos + 20, menuW - 6, menuH - 23, 0x112329, 210);
            Raster.drawRectangle(xPos + 3, yPos + 2, menuW - 6, 1, 0x2a291b);
            Raster.drawRectangle(xPos + 2, yPos + 3, menuW - 4, 1, 0x2a261b);
            Raster.drawRectangle(xPos + 2, yPos + 4, menuW - 4, 1, 0x252116);
            Raster.drawRectangle(xPos + 2, yPos + 5, menuW - 4, 1, 0x211e15);
            Raster.drawRectangle(xPos + 2, yPos + 6, menuW - 4, 1, 0x1e1b12);
            Raster.drawRectangle(xPos + 2, yPos + 7, menuW - 4, 1, 0x1a170e);
            Raster.drawRectangle(xPos + 2, yPos + 8, menuW - 4, 2, 0x15120b);
            Raster.drawRectangle(xPos + 2, yPos + 10, menuW - 4, 1, 0x100d08);
            Raster.drawRectangle(xPos + 2, yPos + 11, menuW - 4, 1, 0x090a04);
            Raster.drawRectangle(xPos + 2, yPos + 12, menuW - 4, 1, 0x080703);
            Raster.drawRectangle(xPos + 2, yPos + 13, menuW - 4, 1, 0x090a04);
            Raster.drawRectangle(xPos + 2, yPos + 14, menuW - 4, 1, 0x070802);
            Raster.drawRectangle(xPos + 2, yPos + 15, menuW - 4, 1, 0x090a04);
            Raster.drawRectangle(xPos + 2, yPos + 16, menuW - 4, 1, 0x070802);
            Raster.drawRectangle(xPos + 2, yPos + 17, menuW - 4, 1, 0x090a04);
            Raster.drawRectangle(xPos + 2, yPos + 18, menuW - 4, 1, 0x2a291b);
            Raster.drawRectangle(xPos + 3, yPos + 19, menuW - 6, 1, 0x564943);
            int mouseX = super.mouseX - (xOffSet);
            int mouseY = (-yOffSet) + super.mouseY;
            for (int l1 = 0; l1 < menuActionRow; l1++) {
                int textY = yPos + 34 + (menuActionRow - 1 - l1) * 15;
                int hoverColor = 0x6f695d;
                if (mouseX > xPos && mouseX < xPos + menuW && mouseY > textY - 13 && mouseY < textY + 3) {
                    Raster.fillRectangle(xPos + 3, textY - 15, menuWidth - 6, 15, hoverColor, 50);
                }
                newBoldFont.drawBasicString(menuActionName[l1], xPos + 3, textY, 0xABA184, 1);

            }
            newBoldFont.drawBasicString("Choose Option", xPos + 3, yPos + 16, 0xc6b895, 50);

            /* Custom Black */
        } else if (Settings.CONTEXT_MENU == 3) {
            int menuColor = 0x13120B;
            int hoverColor = 0xF5870A;
            Raster.fillRectangle(xPos, yPos, menuW - 0, menuH, menuColor, 125);
            Raster.fillRectangle(xPos + 1, yPos + 1, menuW - 2, 16, 0);
            Raster.drawRectangle(xPos + 1, yPos + 18, menuW - 2, menuH - 19, 0);
            newBoldFont.drawBasicString("Choose Option", xPos + 3, yPos + 16, 0xBAAD8A, 1);
            int mouseX = super.mouseX - (xOffSet);
            int mouseY = (-yOffSet) + super.mouseY;
            for (int i = 0; i < menuActionRow; i++) {
                int textY = yPos + 34 + (menuActionRow - 1 - i) * 15;
                int textColor = 0xBAAD8A;
                if (mouseX > xPos && mouseX < xPos + menuW && mouseY > textY - 13 && mouseY < textY + 3) {
                    Raster.fillRectangle(xPos + 3, textY - 15, menuWidth - 5, 15, hoverColor, 100);
                }
                newBoldFont.drawBasicString(menuActionName[i], xPos + 3, textY, textColor, 1);
            }
        }
    }

    private void addFriend(long l) {
        try {
            if (l == 0L)
                return;
            if (friendsCount >= 100 && anInt1046 != 1) {
                pushMessage("Your friendlist is full. Max of 100 for free users, and 200 for members", 0, "");
                return;
            }
            if (friendsCount >= 200) {
                pushMessage("Your friendlist is full. Max of 100 for free users, and 200 for members", 0, "");
                return;
            }
            String s = StringUtils.fixName(StringUtils.nameForLong(l));
            for (int i = 0; i < friendsCount; i++)
                if (friendsListAsLongs[i] == l) {
                    pushMessage(s + " is already on your friend list", 0, "");
                    return;
                }
            for (int j = 0; j < ignoreCount; j++)
                if (ignores[j] == l) {
                    pushMessage("Please remove " + s + " from your ignore list first", 0, "");
                    return;
                }

            if (s.equals(localPlayer.name)) {
                return;
            } else {
                friendsList[friendsCount] = s;
                friendsListAsLongs[friendsCount] = l;
                friendsNodeIDs[friendsCount] = 0;
                friendsCount++;
                outgoing.writeOpcode(188);
                outgoing.writeQWord(l);
                return;
            }
        } catch (RuntimeException runtimeexception) {
            Utility.reporterror("15283, " + (byte) 68 + ", " + l + ", " + runtimeexception.toString());
        }
        throw new RuntimeException();
    }

    private int method42(int i, int j, int k) {
        int l = k >> 7;
        int i1 = j >> 7;
        if (l < 0 || i1 < 0 || l > 103 || i1 > 103)
            return 0;
        int j1 = i;
        if (j1 < 3 && (byteGroundArray[1][l][i1] & 2) == 2)
            j1++;
        int k1 = k & 0x7f;
        int l1 = j & 0x7f;
        int i2 = intGroundArray[j1][l][i1] * (128 - k1) + intGroundArray[j1][l + 1][i1] * k1 >> 7;
        int j2 = intGroundArray[j1][l][i1 + 1] * (128 - k1) + intGroundArray[j1][l + 1][i1 + 1] * k1 >> 7;
        return i2 * (128 - l1) + j2 * l1 >> 7;
    }


    private static String intToKOrMil(int j) {
        if (j < 0x186a0)
            return j + "";
        if (j < 0x989680)
            return j / 1000 + "K";
        else
            return j / 0xf4240 + "M";
    }

    private void resetLogout() {
        Settings.save();
        spriteCache.clear();
        Client.loginScreenIP = null;
        Client.updaterScreenIP = null;
        try {
            if (socketStream != null) {
                socketStream.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (Settings.REMEMBER_ME) {
            AccountManager.add(myUsername, myPassword, myPrivilege);
        }
        AccountManager.saveAccount();
        socketStream = null;
        loggedIn = false;
        // myUsername = "";
        // myPassword = "";
        game_message_time = 0;
        console.openConsole = false;
        unlinkMRUNodes();
        worldController.initToNull();
        for (int i = 0; i < 4; i++) {
            collisionMaps[i].method210();
        }
        System.runFinalization();
        System.gc();
        ExpCounter.lastSkill = -1;
        resetIgnoreList();
        currentSong = -1;
        nextSong = -1;
        prevSong = 0;
        frameValueW = 765;
        frameValueH = 503;
        frameMode(ScreenMode.FIXED);
        drawOpponentStats = false;
        specActive = false;
    }

    private void resetIgnoreList() {
        ignoreCount = 0;
        Arrays.fill(ignores, 0);
    }

    private void method45() {
        aBoolean1031 = true;
        for (int j = 0; j < 7; j++) {
            characterClothing[j] = -1;
            for (int k = 0; k < IdentityKit.length; k++) {
                if (IdentityKit.kits[k].validStyle || IdentityKit.kits[k].bodyPartId != j + (characterGender ? 0 : 7))
                    continue;
                characterClothing[j] = k;
                break;
            }
        }
    }

    private void method46(int i, Buffer stream) {
        while (stream.bitPosition + 21 < i * 8) {
            int k = stream.readBits(14);
            if (k == 16383)
                break;
            if (npcs[k] == null)
                npcs[k] = new Npc();
            Npc npc = npcs[k];
            npcIndices[npcCount++] = k;
            npc.time = tick;
            int l = stream.readBits(5);
            if (l > 15)
                l -= 32;
            int i1 = stream.readBits(5);
            if (i1 > 15)
                i1 -= 32;
            int j1 = stream.readBits(1);
            npc.definition = NpcDefinition.lookup(stream.readBits(Configuration.NPC_BITS));
            int k1 = stream.readBits(1);
            if (k1 == 1)
                mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = k;
            npc.size = npc.definition.size;
            npc.rotation = npc.definition.rotation;
            npc.walkingAnimation = npc.definition.walkingAnimation;
            npc.halfTurnAnimation = npc.definition.halfTurnAnimation;
            npc.quarterClockwiseTurnAnimation = npc.definition.quarterClockwiseTurnAnimation;
            npc.quarterAnticlockwiseTurnAnimation = npc.definition.quarterAnticlockwiseTurnAnimation;
            npc.idleAnimation = npc.definition.standingAnimation;
            npc.setPos(localPlayer.smallX[0] + i1, localPlayer.smallY[0] + l, j1 == 1);
        }
        stream.finishBitAccess();
    }

    public void processGameLoop() {
        if (rsAlreadyLoaded || loadingError || genericLoadingError) {
            return;
        }

        tick++;
        if (Updater.isActive()) {
            if (Updater.get().state == UpdateState.DEFAULT_SCREEN)
                Updater.get().checkForUpdates();
            return;
        }

        if (loggedIn) {
            mainGameProcessor();
            processOnDemandQueue();
            if (Settings.SNOW) {
                processSnowflakes();
            }
        } else {
            loginRenderer.click();
        }

    }

    private void method47(boolean flag) {
        if (localPlayer.x >> 7 == destX && localPlayer.y >> 7 == destY)
            destX = 0;
        int j = playerCount;
        if (flag)
            j = 1;
        for (int l = 0; l < j; l++) {
            Player player;
            int i1;
            if (flag) {
                player = localPlayer;
                i1 = myPlayerIndex << 14;
            } else {
                player = playerArray[playerIndices[l]];
                i1 = playerIndices[l] << 14;
            }
            if (player == null || !player.isVisible())
                continue;
            player.aBoolean1699 = (lowMem && playerCount > 50 || playerCount > 200) && !flag && player.anInt1517 == player.idleAnimation;
            int j1 = player.x >> 7;
            int k1 = player.y >> 7;
            if (j1 < 0 || j1 >= 104 || k1 < 0 || k1 >= 104)
                continue;
            if (player.aModel_1714 != null && tick >= player.anInt1707 && tick < player.anInt1708) {
                player.aBoolean1699 = false;
                player.anInt1709 = method42(plane, player.y, player.x);
                worldController.method286(plane, player.y, player, player.anInt1552, player.anInt1722, player.x, player.anInt1709, player.anInt1719, player.anInt1721, i1, player.anInt1720);
                continue;
            }
            if ((player.x & 0x7f) == 64 && (player.y & 0x7f) == 64) {
                if (anIntArrayArray929[j1][k1] == anInt1265)
                    continue;
                anIntArrayArray929[j1][k1] = anInt1265;
            }
            player.anInt1709 = method42(plane, player.y, player.x);
            worldController.method285(plane, player.anInt1552, player.anInt1709, i1, player.y, 60, player.x, player, player.aBoolean1541);
        }
    }

    private boolean promptUserForInput(RSInterface class9) {
        int j = class9.contentType;
        if (anInt900 == 2) {
            if (j == 201) {
                redrawDialogueBox = true;
                inputDialogState = 0;
                messagePromptRaised = true;
                promptInput = "";
                friendsListAction = 1;
                aString1121 = "Enter name of friend to add to list";
            }
            if (j == 202) {
                redrawDialogueBox = true;
                inputDialogState = 0;
                messagePromptRaised = true;
                promptInput = "";
                friendsListAction = 2;
                aString1121 = "Enter name of friend to delete from list";
            }
        }
        if (j == 205) {
            anInt1011 = 250;
            return true;
        }
        if (j == 501) {
            redrawDialogueBox = true;
            inputDialogState = 0;
            messagePromptRaised = true;
            promptInput = "";
            friendsListAction = 4;
            aString1121 = "Enter name of player to add to list";
        }
        if (j == 502) {
            redrawDialogueBox = true;
            inputDialogState = 0;
            messagePromptRaised = true;
            promptInput = "";
            friendsListAction = 5;
            aString1121 = "Enter name of player to delete from list";
        }
        if (j == 550) {
            redrawDialogueBox = true;
            inputDialogState = 0;
            messagePromptRaised = true;
            promptInput = "";
            friendsListAction = 6;
            aString1121 = "Enter the name of the chat you wish to join";
        }

        if (j >= 300 && j <= 313) {
            int k = (j - 300) / 2;
            int j1 = j & 1;
            int i2 = characterClothing[k];
            if (i2 != -1) {
                do {
                    if (j1 == 0 && --i2 < 0)
                        i2 = IdentityKit.length - 1;
                    if (j1 == 1 && ++i2 >= IdentityKit.length)
                        i2 = 0;
                }
                while (IdentityKit.kits[i2].validStyle || IdentityKit.kits[i2].bodyPartId != k + (characterGender ? 0 : 7));
                characterClothing[k] = i2;
                aBoolean1031 = true;
            }
        }
        if (j >= 314 && j <= 323) {
            int l = (j - 314) / 2;
            int k1 = j & 1;
            int j2 = characterColor[l];
            if (k1 == 0 && --j2 < 0)
                j2 = PLAYER_BODY_RECOLOURS[l].length - 1;
            if (k1 == 1 && ++j2 >= PLAYER_BODY_RECOLOURS[l].length)
                j2 = 0;
            characterColor[l] = j2;
            aBoolean1031 = true;
        }
        if (j == 324 && !characterGender) {
            characterGender = true;
            method45();
        }
        if (j == 325 && characterGender) {
            characterGender = false;
            method45();
        }
        if (j == 326) {
            //appearance change
            outgoing.writeOpcode(101);
            outgoing.writeByte(characterGender ? 0 : 1);

            for (int i1 = 0; i1 < 7; i1++)
                outgoing.writeByte(characterClothing[i1]);

            for (int l1 = 0; l1 < 5; l1++)
                outgoing.writeByte(characterColor[l1]);

            return true;
        }

        if (j == 613)
            canMute = !canMute;
        if (j >= 601 && j <= 612) {
            clearTopInterfaces();
            if (reportAbuseInput.length() > 0) {
                outgoing.writeOpcode(218);
                outgoing.writeQWord(StringUtils.longForName(reportAbuseInput));
                outgoing.writeByte(j - 601);
                outgoing.writeByte(canMute ? 1 : 0);
            }
        }
        return false;
    }

    private void parsePlayerMasks(Buffer buffer) {
        for (int count = 0; count < mobsAwaitingUpdateCount; count++) {
            final int index = mobsAwaitingUpdate[count];
            Player player = playerArray[index];
            int mask = buffer.readUByte();
            if ((mask & 0x40) != 0) {
                mask += buffer.readUByte() << 8;
            }
            appendPlayerUpdateMask(mask, index, buffer, player);
        }
    }

    private void method50(int i, int k, int l, int i1, int j1) {
        int k1 = worldController.method300(j1, l, i);
        if (k1 != 0) {
            int l1 = worldController.method304(j1, l, i, k1);
            int k2 = l1 >> 6 & 3;
            int i3 = l1 & 0x1f;
            int k3 = k;
            if (k1 > 0)
                k3 = i1;
            int ai[] = minimapImage.raster;
            int k4 = 24624 + l * 4 + (103 - i) * 512 * 4;
            int i5 = k1 >> 14 & 0x7fff;
            ObjectDefinition class46_2 = ObjectDefinition.lookup(i5);
            if (class46_2.mapscene != -1) {
                Background background_2 = mapScenes[class46_2.mapscene];
                if (background_2 != null) {
                    int i6 = (class46_2.width * 4 - background_2.anInt1452) / 2;
                    int j6 = (class46_2.length * 4 - background_2.anInt1453) / 2;
                    background_2.drawBackground(48 + l * 4 + i6, 48 + (104 - i - class46_2.length) * 4 + j6);
                }
            } else {
                if (i3 == 0 || i3 == 2)
                    if (k2 == 0) {
                        ai[k4] = k3;
                        ai[k4 + 512] = k3;
                        ai[k4 + 1024] = k3;
                        ai[k4 + 1536] = k3;
                    } else if (k2 == 1) {
                        ai[k4] = k3;
                        ai[k4 + 1] = k3;
                        ai[k4 + 2] = k3;
                        ai[k4 + 3] = k3;
                    } else if (k2 == 2) {
                        ai[k4 + 3] = k3;
                        ai[k4 + 3 + 512] = k3;
                        ai[k4 + 3 + 1024] = k3;
                        ai[k4 + 3 + 1536] = k3;
                    } else if (k2 == 3) {
                        ai[k4 + 1536] = k3;
                        ai[k4 + 1536 + 1] = k3;
                        ai[k4 + 1536 + 2] = k3;
                        ai[k4 + 1536 + 3] = k3;
                    }
                if (i3 == 3)
                    if (k2 == 0)
                        ai[k4] = k3;
                    else if (k2 == 1)
                        ai[k4 + 3] = k3;
                    else if (k2 == 2)
                        ai[k4 + 3 + 1536] = k3;
                    else if (k2 == 3)
                        ai[k4 + 1536] = k3;
                if (i3 == 2)
                    if (k2 == 3) {
                        ai[k4] = k3;
                        ai[k4 + 512] = k3;
                        ai[k4 + 1024] = k3;
                        ai[k4 + 1536] = k3;
                    } else if (k2 == 0) {
                        ai[k4] = k3;
                        ai[k4 + 1] = k3;
                        ai[k4 + 2] = k3;
                        ai[k4 + 3] = k3;
                    } else if (k2 == 1) {
                        ai[k4 + 3] = k3;
                        ai[k4 + 3 + 512] = k3;
                        ai[k4 + 3 + 1024] = k3;
                        ai[k4 + 3 + 1536] = k3;
                    } else if (k2 == 2) {
                        ai[k4 + 1536] = k3;
                        ai[k4 + 1536 + 1] = k3;
                        ai[k4 + 1536 + 2] = k3;
                        ai[k4 + 1536 + 3] = k3;
                    }
            }
            ai = null;
        }
        k1 = worldController.method302(j1, l, i);
        if (k1 != 0) {
            int i2 = worldController.method304(j1, l, i, k1);
            int l2 = i2 >> 6 & 3;
            int j3 = i2 & 0x1f;
            int l3 = k1 >> 14 & 0x7fff;
            ObjectDefinition class46_1 = ObjectDefinition.lookup(l3);
            if (class46_1.mapscene != -1) {
                Background background_1 = mapScenes[class46_1.mapscene];
                if (background_1 != null) {
                    int j5 = (class46_1.width * 4 - background_1.anInt1452) / 2;
                    int k5 = (class46_1.length * 4 - background_1.anInt1453) / 2;
                    background_1.drawBackground(48 + l * 4 + j5, 48 + (104 - i - class46_1.length) * 4 + k5);
                }
            } else if (j3 == 9) {
                int l4 = 0xeeeeee;
                if (k1 > 0)
                    l4 = 0xee0000;
                int ai1[] = minimapImage.raster;
                int l5 = 24624 + l * 4 + (103 - i) * 512 * 4;
                if (l2 == 0 || l2 == 2) {
                    ai1[l5 + 1536] = l4;
                    ai1[l5 + 1024 + 1] = l4;
                    ai1[l5 + 512 + 2] = l4;
                    ai1[l5 + 3] = l4;
                } else {
                    ai1[l5] = l4;
                    ai1[l5 + 512 + 1] = l4;
                    ai1[l5 + 1024 + 2] = l4;
                    ai1[l5 + 1536 + 3] = l4;
                }
                ai1 = null;
            }
        }
        k1 = worldController.method303(j1, l, i);
        if (k1 != 0) {
            int j2 = k1 >> 14 & 0x7fff;
            ObjectDefinition class46 = ObjectDefinition.lookup(j2);
            if (class46.mapscene != -1) {
                Background background = mapScenes[class46.mapscene];
                if (background != null) {
                    int i4 = (class46.width * 4 - background.anInt1452) / 2;
                    int j4 = (class46.length * 4 - background.anInt1453) / 2;
                    background.drawBackground(48 + l * 4 + i4, 48 + (104 - i - class46.length) * 4 + j4);
                }
            }
        }
    }

    private static void setHighMem() {
        SceneGraph.lowMem = false;
        lowMem = false;
        ObjectManager.lowMem = false;
        ObjectDefinition.lowMem = false;
    }

    public void init() {
        loadingTime = new Stopwatch();
//        Configuration.LOADING_MESSAGE = Utility.randomElement(Configuration.LOADING_MESSAGES);
        try {
            cache_dat = new RandomAccessFile(Utility.findcachedir() + "main_file_cache.dat", "rw");
            for (int j = 0; j < 5; j++) {
                cache_idx[j] = new RandomAccessFile(Utility.findcachedir() + "main_file_cache.idx" + j, "rw");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }


        try {
            nodeID = 10;
            portOff = 0;
            setHighMem();
            isMembers = true;
            initClientFrame(frameWidth, frameHeight);
            instance = this;
        } catch (Exception exception) {
            return;
        }
    }

    public static RandomAccessFile cache_dat = null;
    public static final RandomAccessFile[] cache_idx = new RandomAccessFile[5];

    public static void main(String args[]) {
        System.out.println(Configuration.NAME + "(v" + Configuration.GAME_VERSION + ") is now starting up...");
        loadingTime = new Stopwatch();

        try {
            cache_dat = new RandomAccessFile(Utility.findcachedir() + "main_file_cache.dat", "rw");
            for (int j = 0; j < 5; j++) {
                cache_idx[j] = new RandomAccessFile(Utility.findcachedir() + "main_file_cache.idx" + j, "rw");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        if (Configuration.DEBUG_MODE) {
            System.out.println("Client Build: " + Configuration.CLIENT_VERSION + " Cache build: " + Configuration.CACHE_VERSION);
            start(args);
            return;
        }

        Client.start(new String[] { Configuration.USING_OSBUDDY ? "osb" : "norm" });
    }

    public static void start(String args[]) {
        Configuration.LOADING_MESSAGE = Utility.randomElement(Configuration.LOADING_MESSAGES);
        OSBClientFrame.load();
        ClientFrame.load();
        try {
            nodeID = 10;
            portOff = 0;
            setHighMem();
            isMembers = true;
            instance = new Client();
            if (Configuration.USING_OSBUDDY)
                clientFrame = new OSBClientFrame();
            else
                clientFrame = new ClientFrame();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Client instance;
    public static ClientFrame clientFrame;

    private void loadingStages() {
        if (lowMem && loadingStage == 2 && ObjectManager.anInt131 != plane) {
            gameScreenIP.initDrawingArea();
            newFancyFont.drawBasicString("Loading - please wait.", 5, 15, 0xffffff, 0);
            gameScreenIP.drawGraphics(super.graphics, frameMode == ScreenMode.FIXED ? 4 : 0, frameMode == ScreenMode.FIXED ? 4 : 0);
            loadingStage = 1;
            aLong824 = System.currentTimeMillis();
        }
        if (loadingStage == 1) {
            int j = method54();
            if (j != 0 && System.currentTimeMillis() - aLong824 > 0x57e40L) {
                Utility.reporterror(myUsername + " glcfb " + aLong1215 + "," + j + "," + lowMem + "," + decompressors[0] + "," + onDemandFetcher.getNodeCount() + "," + plane + "," + anInt1069 + "," + anInt1070);
                aLong824 = System.currentTimeMillis();
            }
        }
        if (loadingStage == 2 && plane != anInt985) {
            anInt985 = plane;
            method24(plane);
        }
    }

    private int method54() {
        for (int i = 0; i < aByteArrayArray1183.length; i++) {
            if (aByteArrayArray1183[i] == null && anIntArray1235[i] != -1)
                return -1;
            if (aByteArrayArray1247[i] == null && anIntArray1236[i] != -1)
                return -2;
        }
        boolean flag = true;
        for (int j = 0; j < aByteArrayArray1183.length; j++) {
            byte abyte0[] = aByteArrayArray1247[j];
            if (abyte0 != null) {
                int k = (anIntArray1234[j] >> 8) * 64 - baseX;
                int l = (anIntArray1234[j] & 0xff) * 64 - baseY;
                if (aBoolean1159) {
                    k = 10;
                    l = 10;
                }
                flag &= ObjectManager.method189(k, abyte0, l);
                abyte0 = null;
            }
        }
        if (!flag)
            return -3;
        if (aBoolean1080) {
            return -4;
        } else {
            loadingStage = 2;
            ObjectManager.anInt131 = plane;
            method22();
            outgoing.writeOpcode(121);
            return 0;
        }
    }

    private void method55() {
        for (SceneProjectile class30_sub2_sub4_sub4 = (SceneProjectile) aClass19_1013.reverseGetFirst(); class30_sub2_sub4_sub4 != null; class30_sub2_sub4_sub4 = (SceneProjectile) aClass19_1013.reverseGetNext())
            if (class30_sub2_sub4_sub4.anInt1597 != plane || tick > class30_sub2_sub4_sub4.anInt1572)
                class30_sub2_sub4_sub4.unlink();
            else if (tick >= class30_sub2_sub4_sub4.anInt1571) {
                if (class30_sub2_sub4_sub4.anInt1590 > 0) {
                    Npc npc = npcs[class30_sub2_sub4_sub4.anInt1590 - 1];
                    if (npc != null && npc.x >= 0 && npc.x < 13312 && npc.y >= 0 && npc.y < 13312)
                        class30_sub2_sub4_sub4.method455(tick, npc.y, method42(class30_sub2_sub4_sub4.anInt1597, npc.y, npc.x) - class30_sub2_sub4_sub4.anInt1583, npc.x);
                }
                if (class30_sub2_sub4_sub4.anInt1590 < 0) {
                    int j = -class30_sub2_sub4_sub4.anInt1590 - 1;
                    Player player;
                    if (j == unknownInt10)
                        player = localPlayer;
                    else
                        player = playerArray[j];
                    if (player != null && player.x >= 0 && player.x < 13312 && player.y >= 0 && player.y < 13312)
                        class30_sub2_sub4_sub4.method455(tick, player.y, method42(class30_sub2_sub4_sub4.anInt1597, player.y, player.x) - class30_sub2_sub4_sub4.anInt1583, player.x);
                }
                class30_sub2_sub4_sub4.method456(tickDelta);
                worldController.method285(plane, class30_sub2_sub4_sub4.anInt1595, (int) class30_sub2_sub4_sub4.aDouble1587, -1, (int) class30_sub2_sub4_sub4.aDouble1586, 60, (int) class30_sub2_sub4_sub4.aDouble1585, class30_sub2_sub4_sub4, false);
            }

    }

    public AppletContext getAppletContext() {
        return super.getAppletContext();
    }

    private void processOnDemandQueue() {
        do {
            OnDemandData onDemandData;
            do {

                onDemandData = onDemandFetcher.getNextNode();

                if (onDemandData == null) {
                    return;
                }

                if (onDemandData.dataType == 0) {
                    Model.method460(onDemandData.buffer, onDemandData.ID);
                    if (backDialogueId != -1)
                        redrawDialogueBox = true;
                }
                if (onDemandData.dataType == 1) {
                    Frame.load(onDemandData.ID, onDemandData.buffer);
                }
                if (onDemandData.dataType == 2 && onDemandData.ID == nextSong && onDemandData.buffer != null) {
                    // used to save midis
                }
                if (onDemandData.dataType == 3 && loadingStage == 1) {
                    for (int i = 0; i < aByteArrayArray1183.length; i++) {
                        if (anIntArray1235[i] == onDemandData.ID) {
                            aByteArrayArray1183[i] = onDemandData.buffer;
                            if (onDemandData.buffer == null)
                                anIntArray1235[i] = -1;
                            break;
                        }
                        if (anIntArray1236[i] != onDemandData.ID)
                            continue;
                        aByteArrayArray1247[i] = onDemandData.buffer;
                        if (onDemandData.buffer == null)
                            anIntArray1236[i] = -1;
                        break;
                    }

                }
            } while (onDemandData.dataType != 93 || !onDemandFetcher.method564(onDemandData.ID));
            ObjectManager.method173(new Buffer(onDemandData.buffer), onDemandFetcher);
        } while (true);
    }

    private void method60(int i) {
        RSInterface class9 = RSInterface.getInterfaceCache()[i];

        // if (class9 != null) {
        for (int element : class9.children) {//for (int j = 0; j < class9.children.length; j++) {
            if (element == -1) //(class9.children[j] == -1)
                break;
            RSInterface class9_1 = RSInterface.getInterfaceCache()[element];//[class9.children[j]];
            if (class9_1.type == 1)
                method60(class9_1.interfaceId);
            class9_1.anInt246 = 0;
            class9_1.anInt208 = 0;
        }
        // }
    }

    private void drawHeadIcon() {
        if (anInt855 != 2)
            return;
        calcEntityScreenPos((anInt934 - baseX << 7) + anInt937, anInt936 * 2, (anInt935 - baseY << 7) + anInt938);
        if (spriteDrawX > -1 && tick % 20 < 10)
            headIconsHint[1].drawSprite(spriteDrawX - 12, spriteDrawY - 28);
    }

    private void mainGameProcessor() {
        if (!loggedIn) {
            return;
        }
        if (awtFocus) {
            refreshFrameSize();
        }
        if (anInt1104 > 1)
            anInt1104--;
        if (anInt1011 > 0)
            anInt1011--;
        for (int j = 0; j < 10; j++)
            if (!parsePacket())
                break;

        loadingStages();
        method115();
        method114();
        method95();
        method38();
//        textSpokenEvent();
        tickDelta++;
        if (crossType != 0) {
            crossIndex += 20;
            if (crossIndex >= 400)
                crossType = 0;
        }
        if (atInventoryInterfaceType != 0) {
            atInventoryLoopCycle++;
            if (atInventoryLoopCycle >= 15) {
                if (atInventoryInterfaceType == 2) {
                }
                if (atInventoryInterfaceType == 3)
                    redrawDialogueBox = true;
                atInventoryInterfaceType = 0;
            }
        }
        if (activeInterfaceType != 0) {
            dragCycle++;
            if (super.mouseX > pressX + 5 || super.mouseX < pressX - 5 || super.mouseY > pressY + 5 || super.mouseY < pressY - 5)
                aBoolean1242 = true;
            if (super.clickMode2 == 0) {
                if (activeInterfaceType == 2) {
                }
                if (activeInterfaceType == 3)
                    redrawDialogueBox = true;
                activeInterfaceType = 0;
                if (aBoolean1242 && dragCycle >= 15) {
                    lastActiveInvInterface = -1;
                    processRightClick();
                    if (focusedDragWidget == 5382) {
                        Point southWest, northEast;

                        if (frameMode == ScreenMode.FIXED) {
                            southWest = new Point(56, 81);
                            northEast = new Point(101, 41);
                        } else {
                            int xOffset = (frameWidth - 237 - RSInterface.getInterfaceCache()[5292].width) / 2;
                            int yOffset = 36 + ((frameHeight - 503) / 2);
                            southWest = new Point(xOffset + 76, yOffset + 62);
                            northEast = new Point(xOffset + 117, yOffset + 22);
                        }

                        int[] slots = new int[10];

                        for (int i = 0; i < slots.length; i++) {
                            slots[i] = (40 * i) + (int) southWest.getX();
                        }

                        for (int i = 0; i < slots.length; i++) {
                            if ((super.mouseX >= slots[i]) && (super.mouseX <= (slots[i] + 41)) && (super.mouseY >= northEast.getY()) && (super.mouseY <= southWest.getY())) {
                                outgoing.writeOpcode(214);
                                outgoing.writeLEShortA(focusedDragWidget);
                                outgoing.writeNegatedByte(2);
                                outgoing.writeLEShortA(dragFromSlot);
                                outgoing.writeLEShort(i);
                                return;
                            }
                        }

                        slots = null;
                    }

                    if (lastActiveInvInterface == -1 && focusedDragWidget == 3214 && frameMode == ScreenMode.FIXED) {
                        if (super.mouseX <= 516 && super.mouseY <= 338 && super.mouseX >= 0 && super.mouseY >= 0) {
                            outgoing.writeOpcode(87);
                            outgoing.writeShortA(RSInterface.getInterfaceCache()[3214].inv[dragFromSlot] - 1);
                            outgoing.writeShort(focusedDragWidget);
                            outgoing.writeShortA(dragFromSlot);
                        }
                    } else if (lastActiveInvInterface == focusedDragWidget && mouseInvInterfaceIndex != dragFromSlot) {
                        RSInterface rsi = RSInterface.getInterfaceCache()[focusedDragWidget];
                        int j1 = 0;
                        if (settings[304] == 1 && rsi.contentType == 206) {
                            j1 = 1;
                        }
                        if (rsi.inv[mouseInvInterfaceIndex] <= 0)
                            j1 = 0;
                        if (rsi.aBoolean235) {
                            int l2 = dragFromSlot;
                            int l3 = mouseInvInterfaceIndex;
                            rsi.inv[l3] = rsi.inv[l2];
                            rsi.invStackSizes[l3] = rsi.invStackSizes[l2];
                            rsi.inv[l2] = -1;
                            rsi.invStackSizes[l2] = 0;
                        } else if (j1 == 1) {
                            int fromTab = 0;
                            int toTab = 0;
                            if (rsi.contentType == 206) {
                                for (int tab = 0, totalSlots = 0; tab < 10; tab++) {
                                    if (dragFromSlot <= totalSlots + tabAmounts[tab] - 1 && dragFromSlot >= totalSlots) {
                                        fromTab = tab;
                                    }
                                    if (mouseInvInterfaceIndex <= totalSlots + tabAmounts[tab] - 1 && mouseInvInterfaceIndex >= totalSlots) {
                                        toTab = tab;
                                    }
                                    totalSlots += tabAmounts[tab];
                                }
                            }
                            if (fromTab == toTab || rsi.contentType != 206) {
                                int i3 = dragFromSlot;
                                for (int i4 = mouseInvInterfaceIndex; i3 != i4; )
                                    if (i3 > i4) {
                                        rsi.swapInventoryItems(i3, i3 - 1);
                                        i3--;
                                    } else if (i3 < i4) {
                                        rsi.swapInventoryItems(i3, i3 + 1);
                                        i3++;
                                    }
                            }

                        } else if (j1 == 0) {
                            rsi.swapInventoryItems(dragFromSlot, mouseInvInterfaceIndex);
                        }

                        outgoing.writeOpcode(214);
                        outgoing.writeLEShortA(focusedDragWidget);
                        outgoing.writeNegatedByte(j1);
                        outgoing.writeLEShortA(dragFromSlot);
                        outgoing.writeLEShort(mouseInvInterfaceIndex);
                    }
                } else if ((anInt1253 == 1 || menuHasAddFriend(menuActionRow - 1)) && menuActionRow > 2)
                    determineMenuSize();
                else if (menuActionRow > 0)
                    processMenuActions(menuActionRow - 1);
                atInventoryLoopCycle = 10;
                super.lastMetaModifier = 0;
            }
        }
        if (SceneGraph.anInt470 != -1) {
            int k = SceneGraph.anInt470;
            int k1 = SceneGraph.anInt471;
            walk(k, k1, 0);
            SceneGraph.anInt470 = -1;
            crossX = super.lastClickX;
            crossY = super.lastClickY;
            crossType = 1;
            crossIndex = 0;
        }
        if (super.lastMetaModifier == 1 && clickToContinueString != null) {
            clickToContinueString = null;
            redrawDialogueBox = true;
            super.lastMetaModifier = 0;
        }
        processMenuClick();
        if (super.clickMode2 == 1 || super.lastMetaModifier == 1)
            anInt1213++;
        if (anInt1500 != 0 || anInt1044 != 0 || anInt1129 != 0) {
            if (anInt1501 < 0 && !menuOpen) {
                anInt1501++;
                if (anInt1501 == 0) {
                    if (anInt1500 != 0) {
                        redrawDialogueBox = true;
                    }
                    if (anInt1044 != 0) {
                    }
                }
            }
        } else if (anInt1501 > 0) {
            anInt1501--;
        }
        if (loadingStage == 2)
            method108();
        if (loadingStage == 2 && aBoolean1160)
            calcCameraPos();
        Widget.tick();
        for (int i1 = 0; i1 < 5; i1++)
            anIntArray1030[i1]++;

        method73();
        //XXX:This'll disconnect you every 50 ticks (pseudo time) not my code
        anInt1010++;
        if (anInt1010 > 50)
            outgoing.writeOpcode(0);
        try {
            if (socketStream != null && outgoing.currentOffset > 0) {
                socketStream.queueBytes(outgoing.currentOffset, outgoing.buffer);
                outgoing.currentOffset = 0;
                anInt1010 = 0;
            }
        } catch (IOException _ex) {
            dropClient();
        } catch (Exception exception) {
            resetLogout();
        }
    }

    private void method63() {
        SpawnedObject class30_sub1 = (SpawnedObject) aClass19_1179.reverseGetFirst();
        for (; class30_sub1 != null; class30_sub1 = (SpawnedObject) aClass19_1179.reverseGetNext())
            if (class30_sub1.anInt1294 == -1) {
                class30_sub1.anInt1302 = 0;
                method89(class30_sub1);
            } else {
                class30_sub1.unlink();
            }

    }

    public void initializeLoginScreen() {
        if (gameScreenIP != null) {
            return;
        }

        super.fullGameScreen = null;
        chatIP = null;
        mapIP = null;
        inventoryIP = null;
        gameScreenIP = null;
        gameScreenFrameLeft = null;
        gameScreenFrameTop = null;
        loginScreenIP = new GraphicsBuffer(765, 503, getGameComponent());
        updaterScreenIP = new GraphicsBuffer(765, 503, getGameComponent());
        Raster.reset();
        drawGameScreenSprite = true;
    }

    static void sendString(String text, int id) {
        if (RSInterface.getInterfaceCache()[id] != null) {
            RSInterface.getInterfaceCache()[id].disabledMessage = text;
            RSInterface.getInterfaceCache()[id].scrollPosition = 0;
        }
    }

    public void toggleConfig(int configId, int value) {
        anIntArray1045[configId] = value;
        if (settings[configId] != value) {
            settings[configId] = value;
            method33(configId);
        }
    }

    public void openInterface(int id) {
        method60(id);
        if (invOverlayInterfaceID != -1) {
            invOverlayInterfaceID = -1;
            tabAreaAltered = true;
        }
        if (backDialogueId != -1) {
            backDialogueId = -1;
            redrawDialogueBox = true;
        }
        if (inputDialogState != 0) {
            inputDialogState = 0;
            redrawDialogueBox = true;
        }
        openInterfaceID = id;
    }

    public void drawSmoothLoading(int value, String message) {
        for (float percent = LP; percent < (float) value; percent = (float) ((double) percent + 0.29999999999999999D)) {
            drawLoadingText((int) percent, message);
        }
        LP = value;
    }

    void drawLoadingText(int i, String s) {
        if (s != null) {//remove for loading text
            s = "";
        }
        if (titleStreamLoader == null) {
            super.drawLoadingText(i, s);
            return;
        }
        loginScreenIP.initDrawingArea();
        spriteCache.get(62).drawSprite(0, 0);
//        newRegularFont.drawCenteredString(Configuration.LOADING_MESSAGE, 355, 390, 0x948B82, 0);
        newBoldFont.drawBasicString(s, 255, 270, 0x948B82, 0);
        newBoldFont.drawBasicString(i + "%", 424, 255, 0xffffff, 0);
        loginScreenIP.drawGraphics(super.graphics, 0, 0);
    }

    private void method65(int i, int j, int k, int l, RSInterface class9, int i1, boolean flag, int j1) {
        int anInt992;
        if (aBoolean972)
            anInt992 = 32;
        else
            anInt992 = 0;
        aBoolean972 = false;
        if (k >= i && k < i + 16 && l >= i1 && l < i1 + 16) {
            class9.scrollPosition -= anInt1213 * 4;
            if (flag) {
            }
        } else if (k >= i && k < i + 16 && l >= (i1 + j) - 16 && l < i1 + j) {
            class9.scrollPosition += anInt1213 * 4;
            if (flag) {
            }
        } else if (k >= i - anInt992 && k < i + 16 + anInt992 && l >= i1 + 16 && l < (i1 + j) - 16 && anInt1213 > 0) {
            int l1 = ((j - 32) * j) / j1;
            if (l1 < 8)
                l1 = 8;
            int i2 = l - i1 - 16 - l1 / 2;
            int j2 = j - 32 - l1;
            class9.scrollPosition = ((j1 - j) * i2) / j2;
            if (flag) {
            }
            aBoolean972 = true;
        }
    }

    private boolean method66(int i, int j, int k) {
        int i1 = i >> 14 & 0x7fff;
        int j1 = worldController.method304(plane, k, j, i);
        if (j1 == -1)
            return false;
        int k1 = j1 & 0x1f;
        int l1 = j1 >> 6 & 3;
        if (k1 == 10 || k1 == 11 || k1 == 22) {
            ObjectDefinition class46 = ObjectDefinition.lookup(i1);
            int i2;
            int j2;
            if (l1 == 0 || l1 == 2) {
                i2 = class46.width;
                j2 = class46.length;
            } else {
                i2 = class46.length;
                j2 = class46.width;
            }

            walk(k, j,  2);
        } else {
            walk(k, j,  2);
        }
        crossX = super.lastClickX;
        crossY = super.lastClickY;
        crossType = 2;
        crossIndex = 0;
        return true;
    }

    private StreamLoader streamLoaderForName(int i, String s, String s1, int j, int k) {
        byte abyte0[] = null;
        int l = 5;
        try {
            if (decompressors[0] != null)
                abyte0 = decompressors[0].decompress(i);
        } catch (Exception _ex) {
        }
        if (abyte0 != null) {
            // aCRC32_930.reset();
            // aCRC32_930.update(abyte0);
            // int i1 = (int)aCRC32_930.getValue();
            // if(i1 != j)
        }
        if (abyte0 != null) {
            StreamLoader streamLoader = new StreamLoader(abyte0);
            abyte0 = null;
            return streamLoader;
        }
        int j1 = 0;
        while (abyte0 == null) {
            String s2 = "Unknown error";
            drawLoadingText(k, "Requesting " + s);
            try {
                int k1 = 0;
                DataInputStream datainputstream = openJagGrabInputStream(s1 + j);
                byte abyte1[] = new byte[6];
                datainputstream.readFully(abyte1, 0, 6);
                Buffer stream = new Buffer(abyte1);
                stream.currentOffset = 3;
                int i2 = stream.read24Int() + 6;
                int j2 = 6;
                abyte0 = new byte[i2];
                System.arraycopy(abyte1, 0, abyte0, 0, 6);

                while (j2 < i2) {
                    int l2 = i2 - j2;
                    if (l2 > 1000)
                        l2 = 1000;
                    int j3 = datainputstream.read(abyte0, j2, l2);
                    if (j3 < 0) {
                        s2 = "Length error: " + j2 + "/" + i2;
                        throw new IOException("EOF");
                    }
                    j2 += j3;
                    int k3 = (j2 * 100) / i2;
                    if (k3 != k1)
                        drawLoadingText(k, "Loading " + s + " - " + k3 + "%");
                    k1 = k3;
                }
                datainputstream.close();
                try {
                    if (decompressors[0] != null)
                        decompressors[0].method234(abyte0.length, abyte0, i);
                } catch (Exception _ex) {
                    decompressors[0] = null;
                }
                /*
                 * if(abyte0 != null) { aCRC32_930.reset();
                 * aCRC32_930.update(abyte0); int i3 =
                 * (int)aCRC32_930.getValue(); if(i3 != j) { abyte0 = null;
                 * j1++; s2 = "Checksum error: " + i3; } }
                 */
            } catch (IOException ioexception) {
                if (s2.equals("Unknown error"))
                    s2 = "Connection error";
                abyte0 = null;
            } catch (NullPointerException _ex) {
                s2 = "Null error";
                abyte0 = null;
                if (!Configuration.reporterror)
                    return null;
            } catch (ArrayIndexOutOfBoundsException _ex) {
                s2 = "Bounds error";
                abyte0 = null;
                if (!Configuration.reporterror)
                    return null;
            } catch (Exception _ex) {
                s2 = "Unexpected error";
                abyte0 = null;
                if (!Configuration.reporterror)
                    return null;
            }
            if (abyte0 == null) {
                for (int l1 = l; l1 > 0; l1--) {
                    if (j1 >= 3) {
                        drawLoadingText(k, "Game updated - please reload page");
                        l1 = 10;
                    } else {
                        drawLoadingText(k, s2 + " - Retrying in " + l1);
                    }
                    try {
                        Thread.sleep(1000L);
                    } catch (Exception _ex) {
                    }
                }

                l *= 2;
                if (l > 60)
                    l = 60;
                aBoolean872 = !aBoolean872;
            }
            abyte0 = null;

        }

        StreamLoader streamLoader_1 = new StreamLoader(abyte0);
        return streamLoader_1;
    }

    private void dropClient() {
        if (anInt1011 > 0) {
            resetLogout();
            return;
        }
        newFancyFont.drawBasicString("Connection lost", 75, 15, 0xffffff, 0);
        newFancyFont.drawBasicString("Please wait - attempting to reestablish", 5, 30, 0xffffff, 0);
        gameScreenIP.drawGraphics(super.graphics, frameMode == ScreenMode.FIXED ? 4 : 0, frameMode == ScreenMode.FIXED ? 4 : 0);
        AccountManager.saveAccount();
        Settings.save();
        minimapState = 0;
        destX = 0;
        BufferedConnection rsSocket = socketStream;
        loggedIn = false;
        loginFailures = 0;
        attemptLogin(myUsername, myPassword, true);
        console.openConsole = false;
        shiftIsDown = false;
        controlIsDown = false;
        loginRenderer.setScreen(new MainScreen());
        if (!loggedIn) {
            resetLogout();
        }
        keybindManager.save();
        try {
            rsSocket.close();
        } catch (Exception _ex) {
        }
    }

    private void setNorth() {
        anInt1278 = 0;
        anInt1131 = 0;
        anInt896 = 0;
        minimapInt1 = 0;
        minimapInt2 = 0;
        minimapInt3 = 0;
    }

    private void processMenuActions(int id) {
        if (id < 0) {
            return;
        }

        if (inputDialogState != 0) {
            inputDialogState = 0;
            redrawDialogueBox = true;
        }
        int first = menuActionCmd2[id];
        int second = menuActionCmd3[id];
        int action = menuActionID[id];
        int localPlayerIndex = menuActionCmd1[id];

        if (action >= 2000) {
            action -= 2000;
        }

        if (action == 851) {
            outgoing.writeOpcode(185);
            outgoing.writeShort(155);
        }

        if (action == 769) {
            RSInterface d = RSInterface.getInterfaceCache()[second];
            RSInterface p = RSInterface.getInterfaceCache()[localPlayerIndex];
            if (!d.dropDown.isOpen()) {
                if (p.dropDownOpen != null) {
                    p.dropDownOpen.dropDown.setOpen(false);
                }
                p.dropDownOpen = d;
            } else {
                p.dropDownOpen = null;
            }
            d.dropDown.setOpen(!d.dropDown.isOpen());
        }
        if (action == 770) {
            RSInterface d = RSInterface.getInterfaceCache()[second];
            RSInterface p = RSInterface.getInterfaceCache()[localPlayerIndex];
            if (first >= d.dropDown.getOptions().length)
                return;

            d.dropDown.setSelected(d.dropDown.getOptions()[first]);
            d.dropDown.setOpen(false);
            d.dropDown.getDrop().selectOption(first, this, d);
            p.dropDownOpen = null;
        }

        if (action == 700) {
            if (tabInterfaceIDs[14] != -1) {
                if (frameMode != ScreenMode.FIXED && changeTabArea) {
                    if (tabID == 14) {
                        showTabComponents = !showTabComponents;
                    } else {
                        showTabComponents = true;
                    }
                }
                tabID = 14;
                tabAreaAltered = true;
            }
        }

        if (action == 696) {
            setNorth();
        }
        if (action == 291) {
            outgoing.writeOpcode(140);
            outgoing.writeShortA(first);
            outgoing.writeShort(second);
            outgoing.writeShortA(localPlayerIndex);
        }

        if (action == 300) {
            outgoing.writeOpcode(141);
            outgoing.writeShortA(first);
            outgoing.writeShort(second);
            outgoing.writeShortA(localPlayerIndex);
            outgoing.writeDWord(modifiableXValue);
        }

        if (action == 474) {
            displayCounter = !displayCounter;
        }

        if (action == 1506) {
            outgoing.writeOpcode(185);
            outgoing.writeShort(5001);
            prayClicked = true;
            tabID = 5;
            tabAreaAltered = true;
            tabInterfaceIDs[5] = 17200;
        }

        if (action == 1500) {
            outgoing.writeOpcode(185);
            outgoing.writeShort(5000);
        }

        switch (action) {

            case 1315:
            case 1316:
            case 1317:
            case 1318:
            case 1319:
            case 1320:
            case 1321:
            case 879:
            case 850:
            case 475:
            case 476:
            case 477:
            case 1050:
            case 1059:
            case 1998:
                // button click
                outgoing.writeOpcode(185);
                outgoing.writeShort(action);
                break;

        }

        if (action == 104) {
            RSInterface class9_1 = RSInterface.getInterfaceCache()[second];
            spellID = class9_1.interfaceId;
            if (!autocast) {
                autocast = true;
                autoCastId = class9_1.interfaceId;
                outgoing.writeOpcode(185);
                outgoing.writeShort(class9_1.interfaceId);
            } else if (autoCastId == class9_1.interfaceId) {
                autocast = false;
                autoCastId = 0;
                outgoing.writeOpcode(185);
                outgoing.writeShort(class9_1.interfaceId);
            } else if (autoCastId != class9_1.interfaceId) {
                autocast = true;
                autoCastId = class9_1.interfaceId;
                outgoing.writeOpcode(185);
                outgoing.writeShort(class9_1.interfaceId);
            }
        }

        // item on npc
        if (action == 582) {
            Npc npc = npcs[localPlayerIndex];
            if (npc != null) {
                walk(npc.smallX[0], npc.smallY[0],  2);
                crossX = super.lastClickX;
                crossY = super.lastClickY;
                crossType = 2;
                crossIndex = 0;
                outgoing.writeOpcode(57);
                outgoing.writeShortA(anInt1285);
                outgoing.writeShortA(localPlayerIndex);
                outgoing.writeLEShort(anInt1283);
                outgoing.writeShortA(anInt1284);
            }
        }
        if (action == 234) {
            walk(first, second,  2);
            crossX = super.lastClickX;
            crossY = super.lastClickY;
            crossType = 2;
            crossIndex = 0;
            outgoing.writeOpcode(236);
            outgoing.writeLEShort(second + baseY);
            outgoing.writeShort(localPlayerIndex);
            outgoing.writeLEShort(first + baseX);
        }
        if (action == 62 && method66(localPlayerIndex, second, first)) {
            outgoing.writeOpcode(192);
            outgoing.writeShort(anInt1284);
            outgoing.writeLEShort(localPlayerIndex >> 14 & 0x7fff);
            outgoing.writeLEShortA(second + baseY);
            outgoing.writeLEShort(anInt1283);
            outgoing.writeLEShortA(first + baseX);
            outgoing.writeShort(anInt1285);
        }
        if (action == 511) {
            walk(first, second,  2);
            crossX = super.lastClickX;
            crossY = super.lastClickY;
            crossType = 2;
            crossIndex = 0;
            outgoing.writeOpcode(25);
            outgoing.writeLEShort(anInt1284);
            outgoing.writeShortA(anInt1285);
            outgoing.writeShort(localPlayerIndex);
            outgoing.writeShortA(second + baseY);
            outgoing.writeLEShortA(anInt1283);
            outgoing.writeShort(first + baseX);
        }
        if (action == 74) {
            outgoing.writeOpcode(122);
            outgoing.writeLEShortA(second);
            outgoing.writeShortA(first);
            outgoing.writeLEShort(localPlayerIndex);
            atInventoryLoopCycle = 0;
            atInventoryInterface = second;
            atInventoryIndex = first;
            atInventoryInterfaceType = 2;
            if (RSInterface.getInterfaceCache()[second].parentID == openInterfaceID)
                atInventoryInterfaceType = 1;
            if (RSInterface.getInterfaceCache()[second].parentID == backDialogueId)
                atInventoryInterfaceType = 3;
        }
        if (action == 315) {
            RSInterface rsint = RSInterface.getInterfaceCache()[second];
            boolean flag8 = true;

            if (rsint.contentType > 0) {
                flag8 = promptUserForInput(rsint);
            }

            if (Settings.click(this, second)) {
                return;
            }

            if (flag8) {

                switch (second) {

                    case 39319:
                        keybindManager.defaultBind();
                        keybindManager.update();
                        keybindManager.save();
                        pushMessage("Your keys have been reset to their default values.");
                        break;

                    case 17235:
                        tabID = 5;
                        tabAreaAltered = true;
                        tabInterfaceIDs[5] = 5608;
                        prayClicked = false;
                        break;

                    case 56763:
                        Settings.PRIVATE_MESSAGE = 0x65535;
                        Rasterizer.fogColor = 0xc8c0a8;

                        String message = "DEFAULT_TITLE";
                        outgoing.writeOpcode(142);
                        outgoing.writeByte(4 + message.length() + 1);
                        outgoing.writeDWord(667);
                        outgoing.writeString(message);
                        message = "DEFAULT_YELL";
                        outgoing.writeOpcode(142);
                        outgoing.writeByte(4 + message.length() + 1);
                        outgoing.writeDWord(668);
                        outgoing.writeString(message);
                        pushMessage("All colors have been set to their default value.", 0, "");
                        break;

                    case 56766:
                        sendData(colorIndex);
                        break;

                    case 56753:
                        colorIndex = 2;
                        RSInterface.getInterfaceCache()[56710].color = Settings.PLAYER_TITLE;
                        RSInterface.getInterfaceCache()[56711].color = Settings.PRIVATE_MESSAGE;
                        RSInterface.getInterfaceCache()[56712].color = Settings.PLAYER_TITLE;
                        RSInterface.getInterfaceCache()[56713].color = Settings.YELL;
                        RSInterface.getInterfaceCache()[56714].color = Rasterizer.fogColor;
                        sendString("Manage Player Title Color", 56702);
                        sendString("<col=" + Integer.toHexString(Settings.PLAYER_TITLE).substring(2) + ">" + localPlayer.title + " <col=FFFFFF>" + localPlayer.name, 56719);
                        toggleConfig(836, 0);
                        openInterface(56721);
                        break;
                    case 56756:
                        colorIndex = 3;
                        RSInterface.getInterfaceCache()[56710].color = Settings.YELL;
                        RSInterface.getInterfaceCache()[56711].color = Settings.PRIVATE_MESSAGE;
                        RSInterface.getInterfaceCache()[56712].color = Settings.PLAYER_TITLE;
                        RSInterface.getInterfaceCache()[56713].color = Settings.YELL;
                        RSInterface.getInterfaceCache()[56714].color = Rasterizer.fogColor;
                        sendString("Manage Yell Color", 56702);
                        sendString("<col=" + Integer.toHexString(Settings.YELL).substring(2) + ">[Owner] " + localPlayer.name + "<col=FFFFFF>: Hey.", 56719);
                        toggleConfig(836, 0);
                        openInterface(56722);
                        break;
                    case 56759:
                        colorIndex = 4;
                        RSInterface.getInterfaceCache()[56710].color = Rasterizer.fogColor;
                        RSInterface.getInterfaceCache()[56711].color = Settings.PRIVATE_MESSAGE;
                        RSInterface.getInterfaceCache()[56712].color = Settings.PLAYER_TITLE;
                        RSInterface.getInterfaceCache()[56713].color = Settings.YELL;
                        RSInterface.getInterfaceCache()[56714].color = Rasterizer.fogColor;
                        sendString("Manage Fog Color", 56702);
                        sendString("", 56719);
                        toggleConfig(836, 1);
                        openInterface(56723);
                        break;

                    case 19144:
                        sendFrame248(15106, 3213);
                        method60(15106);
                        redrawDialogueBox = true;
                        break;

                    default:
                        outgoing.writeOpcode(185);
                        outgoing.writeShort(second);
                        if (second >= 61101 && second <= 61200) {
                            int selected = second - 61101;

                            for (int ii = 0, slot = -1; ii < ItemDefinition.totalItems && slot < 100; ii++) {
                                ItemDefinition def = ItemDefinition.lookup(ii);
                                if (def.name == null || def.certTemplateID == ii - 1 || def.certID == ii - 1 || RSInterface.getInterfaceCache()[61254].disabledMessage.length() == 0 || !def.searchable) {
                                    continue;
                                }

                                if (def.name.toLowerCase().contains(RSInterface.getInterfaceCache()[61254].disabledMessage.toLowerCase())) {
                                    slot++;
                                }
                                if (slot != selected) {
                                    continue;
                                }

                                int itemId = def.id;
                                long num = Long.valueOf(RSInterface.getInterfaceCache()[61255].disabledMessage.isEmpty() ? "1" : RSInterface.getInterfaceCache()[61255].disabledMessage);

                                if (num > Integer.MAX_VALUE) {
                                    num = Integer.MAX_VALUE;
                                }

                                // custom opcode
                                outgoing.writeOpcode(149);
                                outgoing.writeShort(itemId);
                                outgoing.writeDWord((int) num);
                                outgoing.writeByte(settings[220]);
                                break;
                            }
                        }
                        break;

                }
            }
        }

        // send duel request
        if (action == 561) {
            Player player = playerArray[localPlayerIndex];
            if (player != null) {
                walk(player.smallX[0], player.smallY[0],  2);
                crossX = super.lastClickX;
                crossY = super.lastClickY;
                crossType = 2;
                crossIndex = 0;
                outgoing.writeOpcode(128);
                outgoing.writeShort(localPlayerIndex);
            }
        }
        if (action == 20) {
            Npc class30_sub2_sub4_sub1_sub1_1 = npcs[localPlayerIndex];
            if (class30_sub2_sub4_sub1_sub1_1 != null) {
                walk(class30_sub2_sub4_sub1_sub1_1.smallX[0], class30_sub2_sub4_sub1_sub1_1.smallY[0],  2);
                crossX = super.lastClickX;
                crossY = super.lastClickY;
                crossType = 2;
                crossIndex = 0;
                outgoing.writeOpcode(155);
                outgoing.writeLEShort(localPlayerIndex);
                if (this.isFieldInFocus()) {
                    this.resetInputFieldFocus();
                    inputString = "";
                }
            }
        }

        // player options 2 : report abuse : opcode 153
        //Clicked attack on a player
        if (action == 779) {
            System.out.println("Clicked attack on a player");
            Player players = playerArray[localPlayerIndex];
            if (players != null) {
                walk(players.smallX[0], players.smallY[0],  2);
                crossX = super.lastClickX;
                crossY = super.lastClickY;
                crossType = 2;
                crossIndex = 0;
                outgoing.writeOpcode(153);
                outgoing.writeLEShort(localPlayerIndex);
            }
        }
        if (action == 519)
            if (!menuOpen)
                worldController.method312(super.lastClickY - 4, super.lastClickX - 4);
            else
                worldController.method312(second - 4, first - 4);
        if (action == 1062) {
            method66(localPlayerIndex, second, first);
            outgoing.writeOpcode(228);
            outgoing.writeShortA(localPlayerIndex >> 14 & 0x7fff);
            outgoing.writeShortA(second + baseY);
            outgoing.writeShort(first + baseX);
        }
        if (action == 679 && !aBoolean1149) {
            // dialogue
            outgoing.writeOpcode(40);
            outgoing.writeShort(second);
            aBoolean1149 = true;
        }
        if (action == 431) {
            outgoing.writeOpcode(129);
            outgoing.writeLEShort(second);
            outgoing.writeShortA(localPlayerIndex);
            outgoing.writeShortA(first);
            atInventoryLoopCycle = 0;
            atInventoryInterface = second;
            atInventoryIndex = first;
            atInventoryInterfaceType = 2;
            if (RSInterface.getInterfaceCache()[second].parentID == openInterfaceID)
                atInventoryInterfaceType = 1;
            if (RSInterface.getInterfaceCache()[second].parentID == backDialogueId)
                atInventoryInterfaceType = 3;
        }

        if (action == 337 || action == 42 || action == 792 || action == 322) {
            String s = menuActionName[id];
            int k1 = s.indexOf(">");
            if (k1 != -1) {
                long l3 = StringUtils.longForName(s.substring(k1).trim());
                if (action == 337)
                    addFriend(l3);
                if (action == 42)
                    addIgnore(l3);
                if (action == 792)
                    delFriend(l3);
                if (action == 322)
                    delIgnore(l3);
            }
        }
        if (action == 53) {
            outgoing.writeOpcode(135);
            outgoing.writeLEShort(first);
            outgoing.writeShortA(second);
            outgoing.writeLEShort(localPlayerIndex);
            atInventoryLoopCycle = 0;
            atInventoryInterface = second;
            atInventoryIndex = first;
            atInventoryInterfaceType = 2;
            if (RSInterface.getInterfaceCache()[second].parentID == openInterfaceID)
                atInventoryInterfaceType = 1;
            if (RSInterface.getInterfaceCache()[second].parentID == backDialogueId)
                atInventoryInterfaceType = 3;
        }
        if (action == 539) {
            outgoing.writeOpcode(16);
            outgoing.writeShortA(localPlayerIndex);
            outgoing.writeLEShortA(first);
            outgoing.writeLEShortA(second);
            atInventoryLoopCycle = 0;
            atInventoryInterface = second;
            atInventoryIndex = first;
            atInventoryInterfaceType = 2;
            if (RSInterface.getInterfaceCache()[second].parentID == openInterfaceID)
                atInventoryInterfaceType = 1;
            if (RSInterface.getInterfaceCache()[second].parentID == backDialogueId)
                atInventoryInterfaceType = 3;
        }

        // 484 trade request : 6 duel request
        if (action == 484 || action == 6) {
            String optionName = menuActionName[id];
            int l1 = optionName.indexOf(">");
            if (l1 != -1) {
                optionName = optionName.substring(optionName.lastIndexOf(">") + 1).trim();
                String s7 = StringUtils.fixName(StringUtils.nameForLong(StringUtils.longForName(optionName)));
                boolean flag9 = false;
                for (int count = 0; count < playerCount; count++) {
                    Player players = playerArray[playerIndices[count]];

                    if (players == null || players.name == null || !players.name.equalsIgnoreCase(s7)) {
                        continue;
                    }

                    walk(players.smallX[0], players.smallY[0],  2);

                    // trade
                    if (action == 484) {
                        outgoing.writeOpcode(139);
                        outgoing.writeLEShort(playerIndices[count]);
                    }

                    // duel
                    if (action == 6) {
                        outgoing.writeOpcode(128);
                        outgoing.writeShort(playerIndices[count]);
                    }
                    flag9 = true;
                    break;
                }

                if (!flag9)
                    pushMessage("Unable to find " + s7, 0, "");
            }
        }
        if (action == 870) {
            outgoing.writeOpcode(53);
            outgoing.writeShort(first);
            outgoing.writeShortA(anInt1283);
            outgoing.writeLEShortA(localPlayerIndex);
            outgoing.writeShort(anInt1284);
            outgoing.writeLEShort(anInt1285);
            outgoing.writeShort(second);
            atInventoryLoopCycle = 0;
            atInventoryInterface = second;
            atInventoryIndex = first;
            atInventoryInterfaceType = 2;
            if (RSInterface.getInterfaceCache()[second].parentID == openInterfaceID)
                atInventoryInterfaceType = 1;
            if (RSInterface.getInterfaceCache()[second].parentID == backDialogueId)
                atInventoryInterfaceType = 3;
        }
        if (action == 847) {
            outgoing.writeOpcode(87);
            outgoing.writeShortA(localPlayerIndex);
            outgoing.writeShort(second);
            outgoing.writeShortA(first);
            atInventoryLoopCycle = 0;
            atInventoryInterface = second;
            atInventoryIndex = first;
            atInventoryInterfaceType = 2;
            if (RSInterface.getInterfaceCache()[second].parentID == openInterfaceID)
                atInventoryInterfaceType = 1;
            if (RSInterface.getInterfaceCache()[second].parentID == backDialogueId)
                atInventoryInterfaceType = 3;
        }
        if (action == 626) {
            RSInterface rsint = RSInterface.getInterfaceCache()[second];
            spellSelected = 1;
            spellID = rsint.interfaceId;
            anInt1137 = second;
            spellUsableOn = rsint.spellUsableOn;
            itemSelected = 0;
            String s4 = rsint.selectedActionName;
            if (s4.indexOf(" ") != -1)
                s4 = s4.substring(0, s4.indexOf(" "));
            String s8 = rsint.selectedActionName;
            if (s8.indexOf(" ") != -1)
                s8 = s8.substring(s8.indexOf(" ") + 1);
            spellTooltip = s4 + " " + rsint.spellName + " " + s8;
            // class9_1.sprite1.drawSprite(class9_1.anInt263, class9_1.anInt265,
            // 0xffffff);
            // class9_1.sprite1.drawSprite(200,200);
            // System.out.println("Sprite: " + class9_1.sprite1.toString());
            if (spellUsableOn == 16) {
                tabID = 3;
                tabAreaAltered = true;
            }
            return;
        }
        if (action == 78) {
            outgoing.writeOpcode(117);
            outgoing.writeLEShortA(second);
            outgoing.writeLEShortA(localPlayerIndex);
            outgoing.writeLEShort(first);
            atInventoryLoopCycle = 0;
            atInventoryInterface = second;
            atInventoryIndex = first;
            atInventoryInterfaceType = 2;
            if (RSInterface.getInterfaceCache()[second].parentID == openInterfaceID)
                atInventoryInterfaceType = 1;
            if (RSInterface.getInterfaceCache()[second].parentID == backDialogueId)
                atInventoryInterfaceType = 3;
        }

        // player option 3, attack option
        if (action == 27) {
            Player players = playerArray[localPlayerIndex];
            if (players != null) {
                walk(players.smallX[0], players.smallY[0],  2);
                crossX = super.lastClickX;
                crossY = super.lastClickY;
                crossType = 2;
                crossIndex = 0;

                // attack player opcode
                outgoing.writeOpcode(73);
                outgoing.writeLEShort(localPlayerIndex);
            }
        }
        if (action == 213) {
            walk(first, second,  2);
            crossX = super.lastClickX;
            crossY = super.lastClickY;
            crossType = 2;
            crossIndex = 0;
            outgoing.writeOpcode(79);
            outgoing.writeLEShort(second + baseY);
            outgoing.writeShort(localPlayerIndex);
            outgoing.writeShortA(first + baseX);
        }
        if (action == 632) {
            outgoing.writeOpcode(145);
            outgoing.writeShortA(second);
            outgoing.writeShortA(first);
            outgoing.writeShortA(localPlayerIndex);
            atInventoryLoopCycle = 0;
            atInventoryInterface = second;
            atInventoryIndex = first;
            atInventoryInterfaceType = 2;
            if (RSInterface.getInterfaceCache()[second].parentID == openInterfaceID)
                atInventoryInterfaceType = 1;
            if (RSInterface.getInterfaceCache()[second].parentID == backDialogueId)
                atInventoryInterfaceType = 3;
            if (this.isFieldInFocus()) {
                this.resetInputFieldFocus();
                inputString = "";
            }
        }
        if (action == 1004) {
            if (tabInterfaceIDs[10] != -1) {
                tabID = 10;
                tabAreaAltered = true;
            }
        }
        if (action == 1003) {
            clanChatMode = 2;
            redrawDialogueBox = true;
            sendPrivacyChatState();
        }
        if (action == 1002) {
            clanChatMode = 1;
            redrawDialogueBox = true;
            sendPrivacyChatState();
        }
        if (action == 1001) {
            clanChatMode = 0;
            redrawDialogueBox = true;
            sendPrivacyChatState();
        }
        if (action == 1000) {
            cButtonCPos = 4;
            chatTypeView = 11;
            redrawDialogueBox = true;
        }
        if (action == 999) {
            cButtonCPos = 0;
            chatTypeView = 0;
            redrawDialogueBox = true;
        }
        if (action == 998) {
            cButtonCPos = 1;
            chatTypeView = 5;
            redrawDialogueBox = true;
        }

        // public chat "hide" option
        if (action == 997) {
            publicChatMode = 3;
            redrawDialogueBox = true;
            sendPrivacyChatState();
        }

        // public chat "off" option
        if (action == 996) {
            publicChatMode = 2;
            redrawDialogueBox = true;
            sendPrivacyChatState();
        }

        // public chat "friends" option
        if (action == 995) {
            publicChatMode = 1;
            redrawDialogueBox = true;
            sendPrivacyChatState();
        }

        // public chat "on" option
        if (action == 994) {
            publicChatMode = 0;
            redrawDialogueBox = true;
            sendPrivacyChatState();
        }

        // public chat main click
        if (action == 993) {
            cButtonCPos = 2;
            chatTypeView = 1;
            redrawDialogueBox = true;
            sendPrivacyChatState();
        }

        // private chat "off" option
        if (action == 992) {
            privateChatMode = 2;
            redrawDialogueBox = true;
            sendPrivacyChatState();
        }

        // private chat "friends" option
        if (action == 991) {
            privateChatMode = 1;
            redrawDialogueBox = true;
            sendPrivacyChatState();
        }

        // private chat "on" option
        if (action == 990) {
            privateChatMode = 0;
            redrawDialogueBox = true;
            sendPrivacyChatState();
        }

        // private chat main click
        if (action == 989) {
            cButtonCPos = 3;
            chatTypeView = 2;
            redrawDialogueBox = true;
        }

        // trade message privacy option "off" option
        if (action == 987) {
            tradeMode = 2;
            redrawDialogueBox = true;
            sendPrivacyChatState();
        }

        // trade message privacy option "friends" option
        if (action == 986) {
            tradeMode = 1;
            redrawDialogueBox = true;
            sendPrivacyChatState();
        }
//nah ama just puts random shit lol
        // trade message privacy option "on" option
        if (action == 985) {
            tradeMode = 0;
            redrawDialogueBox = true;
            sendPrivacyChatState();
        }

        // trade message privacy option main click
        if (action == 984) {
            cButtonCPos = 5;
            chatTypeView = 3;
            redrawDialogueBox = true;
        }

        if (action == 980) {
            cButtonCPos = 6;
            chatTypeView = 4;
            redrawDialogueBox = true;
        }
        if (action == 493) {
            outgoing.writeOpcode(75);
            outgoing.writeLEShortA(second);
            outgoing.writeLEShort(first);
            outgoing.writeShortA(localPlayerIndex);
            atInventoryLoopCycle = 0;
            atInventoryInterface = second;
            atInventoryIndex = first;
            atInventoryInterfaceType = 2;
            if (RSInterface.getInterfaceCache()[second].parentID == openInterfaceID)
                atInventoryInterfaceType = 1;
            if (RSInterface.getInterfaceCache()[second].parentID == backDialogueId)
                atInventoryInterfaceType = 3;
        }
        if (action == 652) {
            walk(first, second,  2);
            crossX = super.lastClickX;
            crossY = super.lastClickY;
            crossType = 2;
            crossIndex = 0;
            outgoing.writeOpcode(156);
            outgoing.writeShortA(first + baseX);
            outgoing.writeLEShort(second + baseY);
            outgoing.writeLEShortA(localPlayerIndex);
        }
        if (action == 94) {
            walk(first, second,  2);
            crossX = super.lastClickX;
            crossY = super.lastClickY;
            crossType = 2;
            crossIndex = 0;
            outgoing.writeOpcode(181);
            outgoing.writeLEShort(second + baseY);
            outgoing.writeShort(localPlayerIndex);
            outgoing.writeLEShort(first + baseX);
            outgoing.writeShortA(anInt1137);
        }
        if (action == 646) {
            outgoing.writeOpcode(185);
            outgoing.writeShort(second);
            RSInterface class9_2 = RSInterface.getInterfaceCache()[second];
            if (class9_2.scripts != null && class9_2.scripts[0][0] == 5) {
                int i2 = class9_2.scripts[0][1];
                if (settings[i2] != class9_2.requiredValues[0]) {
                    if (class9_2.updateConfig) {
                        Settings.config(this, second);
                        settings[i2] = class9_2.requiredValues[0];
//                        System.out.println("setting: " + settings[i2] + " value=" + i2);
                        method33(i2);
                    }
                }
            }
        }
        if (action == 225) {
            Npc class30_sub2_sub4_sub1_sub1_2 = npcs[localPlayerIndex];
            if (class30_sub2_sub4_sub1_sub1_2 != null) {
                walk(class30_sub2_sub4_sub1_sub1_2.smallX[0], class30_sub2_sub4_sub1_sub1_2.smallY[0],  2);
                crossX = super.lastClickX;
                crossY = super.lastClickY;
                crossType = 2;
                crossIndex = 0;
                outgoing.writeOpcode(17);
                outgoing.writeLEShortA(localPlayerIndex);
            }
        }
        if (action == 965) {
            Npc class30_sub2_sub4_sub1_sub1_3 = npcs[localPlayerIndex];
            if (class30_sub2_sub4_sub1_sub1_3 != null) {
                walk(class30_sub2_sub4_sub1_sub1_3.smallX[0], class30_sub2_sub4_sub1_sub1_3.smallY[0],  2);
                crossX = super.lastClickX;
                crossY = super.lastClickY;
                crossType = 2;
                crossIndex = 0;
                outgoing.writeOpcode(21);
                outgoing.writeShort(localPlayerIndex);
            }
        }
        if (action == 413) {
            Npc class30_sub2_sub4_sub1_sub1_4 = npcs[localPlayerIndex];
            if (class30_sub2_sub4_sub1_sub1_4 != null) {
                walk(class30_sub2_sub4_sub1_sub1_4.smallX[0], class30_sub2_sub4_sub1_sub1_4.smallY[0],  2);
                crossX = super.lastClickX;
                crossY = super.lastClickY;
                crossType = 2;
                crossIndex = 0;
                outgoing.writeOpcode(131);
                outgoing.writeLEShortA(localPlayerIndex);
                outgoing.writeShortA(anInt1137);
            }
        }
        if (action == 200)
            clearTopInterfaces();
        if (action == 1025) {
            Npc class30_sub2_sub4_sub1_sub1_5 = npcs[localPlayerIndex];
            if (class30_sub2_sub4_sub1_sub1_5 != null) {
                NpcDefinition entityDef = class30_sub2_sub4_sub1_sub1_5.definition;
                if (entityDef.morphisms != null)
                    entityDef = entityDef.morph();
                if (entityDef != null) {
                    String s9;
                    if (entityDef.description != null)
                        s9 = new String(entityDef.description);
                    else
                        s9 = "It's a " + entityDef.name + ".";
                    pushMessage(s9, 0, "");
                }
            }
        }
        if (action == 900) {
            method66(localPlayerIndex, second, first);
            outgoing.writeOpcode(252);
            outgoing.writeLEShortA(localPlayerIndex >> 14 & 0x7fff);
            outgoing.writeLEShort(second + baseY);
            outgoing.writeShortA(first + baseX);
        }
        if (action == 412) {
            Npc class30_sub2_sub4_sub1_sub1_6 = npcs[localPlayerIndex];
            if (class30_sub2_sub4_sub1_sub1_6 != null) {
                walk(class30_sub2_sub4_sub1_sub1_6.smallX[0], class30_sub2_sub4_sub1_sub1_6.smallY[0],  2);
                crossX = super.lastClickX;
                crossY = super.lastClickY;
                crossType = 2;
                crossIndex = 0;
                outgoing.writeOpcode(72);
                outgoing.writeShortA(localPlayerIndex);
            }
        }
        if (action == 365) {
            Player class30_sub2_sub4_sub1_sub2_3 = playerArray[localPlayerIndex];
            if (class30_sub2_sub4_sub1_sub2_3 != null) {
                walk(class30_sub2_sub4_sub1_sub2_3.smallX[0], class30_sub2_sub4_sub1_sub2_3.smallY[0],  2);
                crossX = super.lastClickX;
                crossY = super.lastClickY;
                crossType = 2;
                crossIndex = 0;
                outgoing.writeOpcode(249);
                outgoing.writeShortA(localPlayerIndex);
                outgoing.writeLEShort(anInt1137);
            }
        }
        if (action == 729) {
            Player players = playerArray[localPlayerIndex];

            if (players != null) {
                walk(players.smallX[0], players.smallY[0],  2);
                crossX = super.lastClickX;
                crossY = super.lastClickY;
                crossType = 2;
                crossIndex = 0;
                // follow player opcode
                outgoing.writeOpcode(39);
                outgoing.writeLEShort(localPlayerIndex);
            }
        }

        // player option 4, trade request
        if (action == 577) {
            Player players = playerArray[localPlayerIndex];

            if (players != null) {
                walk(players.smallX[0], players.smallY[0],  2);
                crossX = super.lastClickX;
                crossY = super.lastClickY;
                crossType = 2;
                crossIndex = 0;
                // trade request opcode
                outgoing.writeOpcode(139);
                outgoing.writeLEShort(localPlayerIndex);
            }
        }
        if (action == 956 && method66(localPlayerIndex, second, first)) {
            outgoing.writeOpcode(35);
            outgoing.writeLEShort(first + baseX);
            outgoing.writeShortA(anInt1137);
            outgoing.writeShortA(second + baseY);
            outgoing.writeLEShort(localPlayerIndex >> 14 & 0x7fff);
        }
        if (action == 567) {
            walk(first, second,  2);
            crossX = super.lastClickX;
            crossY = super.lastClickY;
            crossType = 2;
            crossIndex = 0;
            outgoing.writeOpcode(23);
            outgoing.writeLEShort(second + baseY);
            outgoing.writeLEShort(localPlayerIndex);
            outgoing.writeLEShort(first + baseX);
        }
        if (action == 968) {//custom place holder packet
            outgoing.writeOpcode(145);
            outgoing.writeShortA(968);
            outgoing.writeShortA(first);
            outgoing.writeShortA(localPlayerIndex);
            atInventoryLoopCycle = 0;
            atInventoryInterface = second;
            atInventoryIndex = first;
            atInventoryInterfaceType = 2;
            if (RSInterface.getInterfaceCache()[second].parentID == openInterfaceID)
                atInventoryInterfaceType = 1;
            if (RSInterface.getInterfaceCache()[second].parentID == backDialogueId)
                atInventoryInterfaceType = 3;
            if (this.isFieldInFocus()) {
                this.resetInputFieldFocus();
                inputString = "";
            }
        }
        if (action == 867) {
            outgoing.writeOpcode(43);
            outgoing.writeLEShort(second);
            outgoing.writeShortA(localPlayerIndex);
            outgoing.writeShortA(first);
            atInventoryLoopCycle = 0;
            atInventoryInterface = second;
            atInventoryIndex = first;
            atInventoryInterfaceType = 2;
            if (RSInterface.getInterfaceCache()[second].parentID == openInterfaceID)
                atInventoryInterfaceType = 1;
            if (RSInterface.getInterfaceCache()[second].parentID == backDialogueId)
                atInventoryInterfaceType = 3;
        }
        if (action == 543) {
            outgoing.writeOpcode(237);
            outgoing.writeShort(first);
            outgoing.writeShortA(localPlayerIndex);
            outgoing.writeShort(second);
            outgoing.writeShortA(anInt1137);
            atInventoryLoopCycle = 0;
            atInventoryInterface = second;
            atInventoryIndex = first;
            atInventoryInterfaceType = 2;
            if (RSInterface.getInterfaceCache()[second].parentID == openInterfaceID)
                atInventoryInterfaceType = 1;
            if (RSInterface.getInterfaceCache()[second].parentID == backDialogueId)
                atInventoryInterfaceType = 3;
        }
        if (action == 606) {
            String s2 = menuActionName[id];
            int j2 = s2.indexOf(">");
            if (j2 != -1)
                Utility.launchURL("www.nardah.com");
        }
        if (action == 491) {
            Player class30_sub2_sub4_sub1_sub2_6 = playerArray[localPlayerIndex];
            if (class30_sub2_sub4_sub1_sub2_6 != null) {
                walk(class30_sub2_sub4_sub1_sub2_6.smallX[0], class30_sub2_sub4_sub1_sub2_6.smallY[0],  2);
                crossX = super.lastClickX;
                crossY = super.lastClickY;
                crossType = 2;
                crossIndex = 0;
                outgoing.writeOpcode(14);
                outgoing.writeShortA(anInt1284);
                outgoing.writeShort(localPlayerIndex);
                outgoing.writeShort(anInt1285);
                outgoing.writeLEShort(anInt1283);
            }
        }

        if (action == 353 || action == 354) {
            String menu = menuActionName[id];
            int k2 = menu.indexOf(">");

            if (k2 == -1) {
                return;
            }

            String name = menu.substring(k2).trim();
            String formatted_name = name.substring(1);
            outgoing.writeOpcode(142);
            outgoing.writeByte(4 + formatted_name.length() + 1);
            outgoing.writeDWord(action);
            outgoing.writeString(formatted_name);
        }

        if (action == 639) {
            String s3 = menuActionName[id];
            int k2 = s3.indexOf(">");
            if (k2 != -1) {
                long name = StringUtils.longForName(s3.substring(k2).trim());
                int k3 = -1;

                for (int index = 0; index < friendsCount; index++) {
                    if (friendsListAsLongs[index] != name)
                        continue;
                    k3 = index;
                    break;
                }

                if (k3 != -1 && friendsNodeIDs[k3] > 0) {
                    redrawDialogueBox = true;
                    inputDialogState = 0;
                    messagePromptRaised = true;
                    promptInput = "";
                    friendsListAction = 3;
                    aLong953 = friendsListAsLongs[k3];
                    aString1121 = "Enter message to send to " + friendsList[k3];
                }
            }
        }
        if (action == 454) {
            outgoing.writeOpcode(41);
            outgoing.writeShort(localPlayerIndex);
            outgoing.writeShortA(first);
            outgoing.writeShortA(second);
            atInventoryLoopCycle = 0;
            atInventoryInterface = second;
            atInventoryIndex = first;
            atInventoryInterfaceType = 2;
            if (RSInterface.getInterfaceCache()[second].parentID == openInterfaceID)
                atInventoryInterfaceType = 1;
            if (RSInterface.getInterfaceCache()[second].parentID == backDialogueId)
                atInventoryInterfaceType = 3;
        }
        if (action == 478) {
            Npc class30_sub2_sub4_sub1_sub1_7 = npcs[localPlayerIndex];
            if (class30_sub2_sub4_sub1_sub1_7 != null) {
                walk(class30_sub2_sub4_sub1_sub1_7.smallX[0], class30_sub2_sub4_sub1_sub1_7.smallY[0],  2);
                crossX = super.lastClickX;
                crossY = super.lastClickY;
                crossType = 2;
                crossIndex = 0;
                outgoing.writeOpcode(18);
                outgoing.writeLEShort(localPlayerIndex);
            }
        }
        if (action == 113) {
            method66(localPlayerIndex, second, first);
            outgoing.writeOpcode(70);
            outgoing.writeLEShort(first + baseX);
            outgoing.writeShort(second + baseY);
            outgoing.writeLEShortA(localPlayerIndex >> 14 & 0x7fff);
        }
        if (action == 872) {
            method66(localPlayerIndex, second, first);
            outgoing.writeOpcode(234);
            outgoing.writeLEShortA(first + baseX);
            outgoing.writeShortA(localPlayerIndex >> 14 & 0x7fff);
            outgoing.writeLEShortA(second + baseY);
        }
        if (action == 502) {
            method66(localPlayerIndex, second, first);
            outgoing.writeOpcode(132);
            outgoing.writeLEShortA(first + baseX);
            outgoing.writeShort(localPlayerIndex >> 14 & 0x7fff);
            outgoing.writeShortA(second + baseY);
        }
        if (action == 1125) {
            ItemDefinition definition = ItemDefinition.lookup(localPlayerIndex);
            if (definition == null) {
                return;
            }

            RSInterface rsi = RSInterface.getInterfaceCache()[second];
            String s5;
            if (rsi != null && rsi.invStackSizes[first] >= 0x186a0) {
                DecimalFormatSymbols separator = new DecimalFormatSymbols();
                separator.setGroupingSeparator(',');
                DecimalFormat formatter = new DecimalFormat("#,###,###,###", separator);
                s5 = formatter.format(rsi.invStackSizes[first]) + " itemX " + definition.name;
            } else if (definition.description != null) {
                s5 = definition.name.equalsIgnoreCase("coins") ? "Lovely money!" : definition.description;
            } else {
                s5 = "It's a " + definition.name + ".";
            }
            pushMessage(s5, 0, "");
            outgoing.writeOpcode(150);
            outgoing.writeShort(first);
            outgoing.writeShort(second);
            outgoing.writeShort(definition.id);
        }
        if (action == 169) {
        	if (controlIsDown) {
				PrayerSystem.InterfaceData grabbed = PrayerSystem.InterfaceData.searchByButton(second);
				if (grabbed != null) {
					prayerGrabbed = grabbed;
					return;
				}
			}
            outgoing.writeOpcode(185);
            outgoing.writeShort(second);
            RSInterface rsi = RSInterface.getInterfaceCache()[second];
            if (rsi.scripts != null && rsi.scripts[0][0] == 5) {
                int l2 = rsi.scripts[0][1];
                settings[l2] = 1 - settings[l2];
                method33(l2);
            }
        }
        if (action == 447) {
            itemSelected = 1;
            anInt1283 = first;
            anInt1284 = second;
            anInt1285 = localPlayerIndex;
            selectedItemName = ItemDefinition.lookup(localPlayerIndex).name;
            spellSelected = 0;
            return;
        }
        if (action == 1226) {
            int j1 = localPlayerIndex >> 14 & 0x7fff;
            ObjectDefinition class46 = ObjectDefinition.lookup(j1);
            String s10;
            if (class46.description != null)
                s10 = new String(class46.description);
            else
                s10 = "It's a " + class46.name + ".";//fk this naming
            pushMessage(s10, 0, "");
        }
        if (action == 244) {
            walk(first, second, 2);
            crossX = super.lastClickX;
            crossY = super.lastClickY;
            crossType = 2;
            crossIndex = 0;
            outgoing.writeOpcode(253);
            outgoing.writeLEShort(first + baseX);
            outgoing.writeLEShortA(second + baseY);
            outgoing.writeShortA(localPlayerIndex);
        }
        if (action == 1448) {
            ItemDefinition itemDef_1 = ItemDefinition.lookup(localPlayerIndex);
            String s6;
            if (itemDef_1.description != null) {
                s6 = itemDef_1.description;
            } else {
                s6 = "It's a " + itemDef_1.name + ".";
            }
            pushMessage(s6, 0, "");
        }
        itemSelected = 0;
        spellSelected = 0;

    }

    private void method70() {
        onTutorialIsland = 0;
        int j = (localPlayer.x >> 7) + baseX;
        int k = (localPlayer.y >> 7) + baseY;
        if (j >= 3053 && j <= 3156 && k >= 3056 && k <= 3136)
            onTutorialIsland = 1;
        if (j >= 3072 && j <= 3118 && k >= 9492 && k <= 9535)
            onTutorialIsland = 1;
        if (onTutorialIsland == 1 && j >= 3139 && j <= 3199 && k >= 3008 && k <= 3062)
            onTutorialIsland = 0;
    }

    public void run() {
        super.run();
    }

    private void build3dScreenMenu() {
        if (Settings.SPEC_BUTTON) {
            if (this.mouseInRegion2(frameMode == ScreenMode.FIXED ? 470 : frameWidth - 260, (frameMode == ScreenMode.FIXED ? 165 : 80), 38, 38)) {
                menuActionName[menuActionRow] = specActive ? "Disable special attack" : "Activate special attack";
                menuActionID[menuActionRow] = 1998;
                menuActionRow++;
                specButtonHovered = true;
                return;
            }
            specButtonHovered = false;
        }
        if (itemSelected == 0 && spellSelected == 0) {
            menuActionName[menuActionRow] = "Walk here";
            menuActionID[menuActionRow] = 519;
            menuActionCmd2[menuActionRow] = super.mouseX;
            menuActionCmd3[menuActionRow] = super.mouseY;
            menuActionRow++;
        }
        int j = -1;
        for (int k = 0; k < Model.anInt1687; k++) {
            int l = Model.anIntArray1688[k];
            int i1 = l & 0x7f;
            int j1 = l >> 7 & 0x7f;
            int k1 = l >> 29 & 3;
            int id = l >> 14 & 0x7fff;
            if (l == j)
                continue;
            j = l;
            if (k1 == 2 && worldController.method304(plane, i1, j1, l) >= 0) {
                ObjectDefinition definition = ObjectDefinition.lookup(id);

                if (definition.morphisms != null) {
                    definition = definition.method580();
                }

                if (definition == null) {
                    continue;
                }

                if (itemSelected == 1) {
                    menuActionName[menuActionRow] = "Use " + selectedItemName + " with <col=00FFFF>" + definition.name;
                    menuActionID[menuActionRow] = 62;
                    menuActionCmd1[menuActionRow] = l;
                    menuActionCmd2[menuActionRow] = i1;
                    menuActionCmd3[menuActionRow] = j1;
                    menuActionRow++;
                } else if (spellSelected == 1) {
                    if ((spellUsableOn & 4) == 4) {
                        menuActionName[menuActionRow] = spellTooltip + " <col=00FFFF>" + definition.name;
                        menuActionID[menuActionRow] = 956;
                        menuActionCmd1[menuActionRow] = l;
                        menuActionCmd2[menuActionRow] = i1;
                        menuActionCmd3[menuActionRow] = j1;
                        menuActionRow++;
                    }
                } else {
                    if (definition.type != 10949) {
                        if (definition.actions != null) {

                            for (int option = 4; option >= 0; option--)
                                if (definition.actions[option] != null) {
                                    menuActionName[menuActionRow] = definition.actions[option] + " <col=00FFFF>" + definition.name;

                                    // object option 1
                                    if (option == 0) {
                                        menuActionID[menuActionRow] = 502;
                                    }

                                    // object option 2
                                    if (option == 1) {
                                        menuActionID[menuActionRow] = 900;
                                    }

                                    // object option 3
                                    if (option == 2) {
                                        menuActionID[menuActionRow] = 113;
                                    }

                                    // object option 4
                                    if (option == 3) {
                                        menuActionID[menuActionRow] = 872;
                                    }

                                    // object option 5
                                    if (option == 4) {
                                        menuActionID[menuActionRow] = 1062;
                                    }
                                    menuActionCmd1[menuActionRow] = l;
                                    menuActionCmd2[menuActionRow] = i1;
                                    menuActionCmd3[menuActionRow] = j1;
                                    menuActionRow++;
                                }

                        }
                        menuActionName[menuActionRow] = "Examine <col=00FFFF>" + definition.name + (Configuration.DEBUG_MODE ? " <col=65280>(<col=ffffff>" + id + "<col=65280>) (<col=ffffff>" + (i1 + baseX) + "<col=65280>, <col=ffffff>" + (j1 + baseY) + "<col=65280>) (Models: <col=ffffff>" + Arrays.toString(definition.modelIds) + "<col=65280>)" : "");
                        menuActionID[menuActionRow] = 1226;
                        menuActionCmd1[menuActionRow] = definition.type << 14;
                        menuActionCmd2[menuActionRow] = i1;
                        menuActionCmd3[menuActionRow] = j1;
                        menuActionRow++;
                    }
                }
            }
            if (k1 == 1) {
                Npc npc = npcs[id];
                try {
                    if (npc.definition.size == 1 && (npc.x & 0x7f) == 64 && (npc.y & 0x7f) == 64) {
                        for (int j2 = 0; j2 < npcCount; j2++) {
                            Npc npc2 = npcs[npcIndices[j2]];
                            if (npc2 != null && npc2 != npc && npc2.definition.size == 1 && npc2.x == npc.x && npc2.y == npc.y)
                                buildAtNPCMenu(npc2.definition, npcIndices[j2], j1, i1);
                        }
                        for (int l2 = 0; l2 < playerCount; l2++) {
                            Player player = playerArray[playerIndices[l2]];
                            if (player != null && player.x == npc.x && player.y == npc.y)
                                buildAtPlayerMenu(i1, playerIndices[l2], player, j1);
                        }
                    }
                    buildAtNPCMenu(npc.definition, id, j1, i1);
                } catch (Exception e) {
                }
            }
            if (k1 == 0) {
                Player player = playerArray[id];
                if ((player.x & 0x7f) == 64 && (player.y & 0x7f) == 64) {
                    for (int k2 = 0; k2 < npcCount; k2++) {
                        Npc class30_sub2_sub4_sub1_sub1_2 = npcs[npcIndices[k2]];
                        if (class30_sub2_sub4_sub1_sub1_2 != null && class30_sub2_sub4_sub1_sub1_2.definition.size == 1 && class30_sub2_sub4_sub1_sub1_2.x == player.x && class30_sub2_sub4_sub1_sub1_2.y == player.y)
                            buildAtNPCMenu(class30_sub2_sub4_sub1_sub1_2.definition, npcIndices[k2], j1, i1);
                    }

                    for (int i3 = 0; i3 < playerCount; i3++) {
                        Player class30_sub2_sub4_sub1_sub2_2 = playerArray[playerIndices[i3]];
                        if (class30_sub2_sub4_sub1_sub2_2 != null && class30_sub2_sub4_sub1_sub2_2 != player && class30_sub2_sub4_sub1_sub2_2.x == player.x && class30_sub2_sub4_sub1_sub2_2.y == player.y)
                            buildAtPlayerMenu(i1, playerIndices[i3], class30_sub2_sub4_sub1_sub2_2, j1);
                    }

                }
                buildAtPlayerMenu(i1, id, player, j1);
            }
            if (k1 == 3) {
                Deque class19 = groundArray[plane][i1][j1];
                if (class19 != null) {
                    for (Item item = (Item) class19.getFirst(); item != null; item = (Item) class19.getNext()) {
                        ItemDefinition itemDef = ItemDefinition.lookup(item.itemId);
                        if (itemSelected == 1) {
                            menuActionName[menuActionRow] = "Use " + selectedItemName + " with <col=FF9040>" + itemDef.name;
                            menuActionID[menuActionRow] = 511;
                            menuActionCmd1[menuActionRow] = item.itemId;
                            menuActionCmd2[menuActionRow] = i1;
                            menuActionCmd3[menuActionRow] = j1;
                            menuActionRow++;
                        } else if (spellSelected == 1) {
                            if ((spellUsableOn & 1) == 1) {
                                menuActionName[menuActionRow] = spellTooltip + " <col=FF9040>" + itemDef.name;
                                menuActionID[menuActionRow] = 94;
                                menuActionCmd1[menuActionRow] = item.itemId;
                                menuActionCmd2[menuActionRow] = i1;
                                menuActionCmd3[menuActionRow] = j1;
                                menuActionRow++;
                            }
                        } else {
                            for (int j3 = 4; j3 >= 0; j3--)
                                if (itemDef.groundActions != null && itemDef.groundActions[j3] != null) {
                                    menuActionName[menuActionRow] = itemDef.groundActions[j3] + " <col=FF9040>" + itemDef.name + (Configuration.DEBUG_MODE ? " <col=65280>(<col=ffffff>" + itemDef.id + "<col=65280>)" : "");
                                    if (j3 == 0)
                                        menuActionID[menuActionRow] = 652;
                                    if (j3 == 1)
                                        menuActionID[menuActionRow] = 567;
                                    if (j3 == 2)
                                        menuActionID[menuActionRow] = 234;
                                    if (j3 == 3)
                                        menuActionID[menuActionRow] = 244;
                                    if (j3 == 4)
                                        menuActionID[menuActionRow] = 213;
                                    menuActionCmd1[menuActionRow] = item.itemId;
                                    menuActionCmd2[menuActionRow] = i1;
                                    menuActionCmd3[menuActionRow] = j1;
                                    menuActionRow++;
                                } else if (j3 == 2) {
                                    menuActionName[menuActionRow] = "Take " + Utility.getDropColor(item.type) + itemDef.name;
                                    menuActionID[menuActionRow] = 234;
                                    menuActionCmd1[menuActionRow] = item.itemId;
                                    menuActionCmd2[menuActionRow] = i1;
                                    menuActionCmd3[menuActionRow] = j1;
                                    menuActionRow++;
                                }
                            menuActionName[menuActionRow] = "Examine <col=FF9040>" + itemDef.name + (Configuration.DEBUG_MODE ? " <col=65280>(<col=ffffff>" + itemDef.id + "<col=65280>)" : "");
                            menuActionID[menuActionRow] = 1448;
                            menuActionCmd1[menuActionRow] = item.itemId;
                            menuActionCmd2[menuActionRow] = i1;
                            menuActionCmd3[menuActionRow] = j1;
                            menuActionRow++;
                        }
                    }

                }
            }
        }
    }

    public void cleanUpForQuit() {
        Configuration.reporterror = false;
        try {
            if (socketStream != null) {
                socketStream.close();
            }
        } catch (Exception _ex) {
        }
        socketStream = null;
        if (mouseDetection != null)
            mouseDetection.running = false;
        mouseDetection = null;
        onDemandFetcher.disable();
        onDemandFetcher = null;
        chatBuffer = null;
        outgoing = null;
        aStream_847 = null;
        incoming = null;
        anIntArray1234 = null;
        aByteArrayArray1183 = null;
        aByteArrayArray1247 = null;
        anIntArray1235 = null;
        anIntArray1236 = null;
        intGroundArray = null;
        byteGroundArray = null;
        worldController = null;
        collisionMaps = null;
        pathDirections = null;
        cost = null;
        bigX = null;
        bigY = null;
        leftFrame = null;
        topFrame = null;
        aByteArray912 = null;
        inventoryIP = null;
        mapIP = null;
        gameScreenIP = null;
        chatIP = null;
        gameScreenFrameLeft = null;
        gameScreenFrameTop = null;
        /* Null pointers for custom resource */
        mapBack = null;
        sideIcons = null;
        compass = null;
        hitMarks = null;
        headIcons = null;
        skullIcons = null;
        bountyIcons = null;
        headIconsHint = null;
        crosses = null;
        mapDotItem = null;
        mapDotNPC = null;
        mapDotPlayer = null;
        mapDotFriend = null;
        mapDotTeam = null;
        mapScenes = null;
        mapFunctions = null;
        anIntArrayArray929 = null;
        playerArray = null;
        playerIndices = null;
        mobsAwaitingUpdate = null;
        playerSynchronizationBuffers = null;
        removedMobs = null;
        npcs = null;
        npcIndices = null;
        groundArray = null;
        aClass19_1179 = null;
        aClass19_1013 = null;
        aClass19_1056 = null;
        menuActionCmd2 = null;
        menuActionCmd3 = null;
        menuActionID = null;
        menuActionCmd1 = null;
        menuActionName = null;
        settings = null;
        anIntArray1072 = null;
        anIntArray1073 = null;
        aClass30_Sub2_Sub1_Sub1Array1140 = null;
        minimapImage = null;
        friendsList = null;
        friendsListAsLongs = null;
        friendsNodeIDs = null;
        loginScreenIP = null;
        updaterScreenIP = null;
        multiOverlay = null;
        ObjectDefinition.nullLoader();
        NpcDefinition.clear();
        ItemDefinition.nullLoader();
        FloorDefinition.underlays = null;
        FloorDefinition.overlays = null;
        IdentityKit.kits = null;
        RSInterface.setInterfaceCache(null);
        Animation.animations = null;
        Graphic.cache = null;
        Graphic.aMRUNodes_415 = null;
        Varp.varps = null;
        super.fullGameScreen = null;
        Player.mruNodes = null;
        Rasterizer.nullLoader();
        SceneGraph.nullLoader();
        Model.nullLoader();
        Frame.nullLoader();
        System.gc();
    }

    public Component getGameComponent() {
        if (super.clientFrame != null)
            return super.clientFrame;
        else
            return this;
    }

    private void method73() {
        do {
            int key = readChar(-796);

            if (key == -1 || (Utility.staff(myPrivilege) && key == 96))
                break;
            if (console.openConsole) {
                if (key == 8 && console.consoleInput.length() > 0) {
                    console.consoleInput = console.consoleInput.substring(0, console.consoleInput.length() - 1);
                }
                if (key >= 32 && key <= 122 && console.consoleInput.length() < 80) {
                    console.consoleInput += (char) key;
                }
                if ((key == 13 || key == 10) && console.consoleInput.length() > 0) {
                    console.sendConsoleMessage(console.consoleInput);
                    console.sendConsoleCommands(console.consoleInput);
                    console.consoleInput = "";
                    redrawDialogueBox = true;
                } else if (openInterfaceID != -1 && openInterfaceID == reportAbuseInterfaceID) {
                    if (key == 8 && reportAbuseInput.length() > 0)
                        reportAbuseInput = reportAbuseInput.substring(0, reportAbuseInput.length() - 1);
                    if ((key >= 97 && key <= 122 || key >= 65 && key <= 90 || key >= 48 && key <= 57 || key == 32) && reportAbuseInput.length() < 12)
                        reportAbuseInput += (char) key;
                }
            } else if (messagePromptRaised) {
                if (key >= 32 && key <= 122 && promptInput.length() < 80) {
                    promptInput += (char) key;
                    redrawDialogueBox = true;
                }
                if (key == 8 && promptInput.length() > 0) {
                    promptInput = promptInput.substring(0, promptInput.length() - 1);
                    redrawDialogueBox = true;
                }
                if (key == 13 || key == 10) {
                    messagePromptRaised = false;
                    redrawDialogueBox = true;
                    if (friendsListAction == 1) {
                        long l = StringUtils.longForName(promptInput);
                        addFriend(l);
                    }
                    if (friendsListAction == 2 && friendsCount > 0) {
                        long l1 = StringUtils.longForName(promptInput);
                        delFriend(l1);
                    }
                    if (friendsListAction == 3 && promptInput.length() > 0) {
                        outgoing.writeOpcode(126);
                        outgoing.writeByte(0);
                        int k = outgoing.currentOffset;
                        outgoing.writeQWord(aLong953);
                        TextInput.encode(promptInput, outgoing);
                        outgoing.writeBytes(outgoing.currentOffset - k);
                        promptInput = TextInput.processText(promptInput);
                        if (Settings.PROFANITY_FILTER) {
                            promptInput = Censor.doCensor(promptInput);
                        }
                        pushMessage(promptInput, 6, StringUtils.fixName(StringUtils.nameForLong(aLong953)));

                        if (privateChatMode == 2) {
                            privateChatMode = 1;
                            sendPrivacyChatState();
                        }
                    }
                    if (friendsListAction == 4 && ignoreCount < 100) {
                        long usernameLong = StringUtils.longForName(promptInput);
                        addIgnore(usernameLong);
                    }
                    if (friendsListAction == 5 && ignoreCount > 0) {
                        long l3 = StringUtils.longForName(promptInput);
                        delIgnore(l3);
                    }
                    if (friendsListAction == 6) {
                        long l3 = StringUtils.longForName(promptInput);
                        chatJoin(l3);
                    }
                }
            } else if (inputDialogState == 1) {
                if (key >= 48 && key <= 57 && amountOrNameInput.length() < inputLength) {
                    amountOrNameInput += (char) key;
                    redrawDialogueBox = true;
                }
                if ((!amountOrNameInput.toLowerCase().contains("k") && !amountOrNameInput.toLowerCase().contains("m") && !amountOrNameInput.toLowerCase().contains("b")) && (key == 107 || key == 109) || key == 98) {
                    amountOrNameInput += (char) key;
                    redrawDialogueBox = true;
                }
                if (key == 8 && amountOrNameInput.length() > 0) {
                    amountOrNameInput = amountOrNameInput.substring(0, amountOrNameInput.length() - 1);
                    redrawDialogueBox = true;
                }
                if (key == 13 || key == 10) {
                    if (amountOrNameInput.length() > 0) {
                        if (amountOrNameInput.toLowerCase().contains("k")) {
                            amountOrNameInput = amountOrNameInput.replace("k", "000");
                        } else if (amountOrNameInput.toLowerCase().contains("m")) {
                            amountOrNameInput = amountOrNameInput.replace("m", "000000");
                        } else if (amountOrNameInput.toLowerCase().contains("b")) {
                            amountOrNameInput = amountOrNameInput.replace("b", "000000000");
                        }

                        if (Long.parseLong(amountOrNameInput) >= Integer.MAX_VALUE) {
                            amountOrNameInput = Integer.toString(Integer.MAX_VALUE);
                        }

                        int amount = Integer.parseInt(amountOrNameInput);

                        outgoing.writeOpcode(208);
                        outgoing.writeDWord(amount);

                        if (openInterfaceID == 60_000)
                            modifiableXValue = amount;
                    }
                    inputDialogState = 0;
                    redrawDialogueBox = true;
                }
            } else if (inputDialogState == 2) {
                if (key >= 32 && key <= 122 && amountOrNameInput.length() < inputLength) {
                    amountOrNameInput += (char) key;
                    redrawDialogueBox = true;
                }
                if (key == 8 && amountOrNameInput.length() > 0) {
                    amountOrNameInput = amountOrNameInput.substring(0, amountOrNameInput.length() - 1);
                    redrawDialogueBox = true;
                }
                if (key == 13 || key == 10) {
                    if (amountOrNameInput.length() > 0) {
                        outgoing.writeOpcode(60);
                        outgoing.writeQWord(StringUtils.longForName(amountOrNameInput));
                    }
                    inputDialogState = 0;
                    redrawDialogueBox = true;
                }
            } else if (backDialogueId == -1) {
                if (this.isFieldInFocus()) {
                    RSInterface rsi = this.getInputFieldFocusOwner();
                    if (rsi == null) {
                        return;
                    }
                    if (key >= 32 && key <= 122 && rsi.disabledMessage.length() < rsi.characterLimit) {
                        if (rsi.inputRegex.length() > 0) {
                            Pattern regex = Pattern.compile(rsi.inputRegex);
                            Matcher match = regex.matcher(Character.toString(((char) key)));
                            if (match.matches()) {
                                rsi.disabledMessage += (char) key;
                                redrawDialogueBox = true;
                            }
                        } else {
                            rsi.disabledMessage += (char) key;
                            redrawDialogueBox = true;
                        }
                    }
                    if (key == 8 && rsi.disabledMessage.length() > 0) {
                        rsi.disabledMessage = rsi.disabledMessage.substring(0, rsi.disabledMessage.length() - 1);
                        redrawDialogueBox = true;
                    }
                    if (rsi.updatesEveryInput && rsi.disabledMessage.length() > 0 && key != 10 && key != 13) {
                        outgoing.writeOpcode(142);
                        outgoing.writeByte(4 + rsi.disabledMessage.length() + 1);
                        outgoing.writeDWord(rsi.interfaceId);
                        outgoing.writeString(rsi.disabledMessage);
                        if (rsi.interfaceId == 61254) {
                            for (int slot = 0; slot < 100; slot++) {
                                RSInterface.getInterfaceCache()[61266 + slot].disabledSprite = new Sprite(32, 32);
                                RSInterface.getInterfaceCache()[61101 + slot].disabledMessage = "";
                            }

                            int found = 0;

                            for (int i = 0, slot = 0; i < ItemDefinition.totalItems && slot < 100; i++) {
                                ItemDefinition def = ItemDefinition.lookup(i);

                                if (def.name == null || def.certTemplateID == i - 1 || def.certID == i - 1) {
                                    continue;
                                }

                                if (def.name.toLowerCase().contains(rsi.disabledMessage.toLowerCase())) {
                                    RSInterface.getInterfaceCache()[61266 + slot].enabledSprite = ItemDefinition.getSprite(i, 1, 0);
                                    RSInterface.getInterfaceCache()[61266 + slot].disabledSprite = ItemDefinition.getSprite(i, 1, 0);
                                    RSInterface.getInterfaceCache()[61101 + slot++].disabledMessage = def.name;
                                    found++;
                                }
                            }

                            RSInterface.getInterfaceCache()[61100].scrollMax = 40 * found + 12;
                        }

                        if (rsi.interfaceId == 60019) {
                            RSInterface bank = RSInterface.getInterfaceCache()[5382];
                            Arrays.fill(bankInvTemp, 0);
                            Arrays.fill(bankStackTemp, 0);
                            int bankSlot = 0;
                            for (int slot = 0; slot < bank.inv.length; slot++) {
                                if (bank.inv[slot] - 1 > 0) {
                                    ItemDefinition def = ItemDefinition.lookup(bank.inv[slot] - 1);
                                    if (def == null || def.name == null) continue;
                                    if (def.name.toLowerCase().contains(rsi.disabledMessage.toLowerCase())) {
                                        bankInvTemp[bankSlot] = bank.inv[slot];
                                        bankStackTemp[bankSlot++] = bank.invStackSizes[slot];
                                    }
                                }
                            }
                            RSInterface.getInterfaceCache()[5385].scrollMax = (int) Math.ceil(bankSlot / 9.0) * 36;
                        }
                        inputString = "";
                        promptInput = "";
                        break;
                    } else if ((key == 10 || key == 13) && !rsi.updatesEveryInput) {
                        outgoing.writeOpcode(142);
                        outgoing.writeByte(4 + rsi.disabledMessage.length() + 1);
                        outgoing.writeDWord(rsi.interfaceId);
                        outgoing.writeString(rsi.disabledMessage);
                        inputString = "";
                        promptInput = "";
                        break;
                    }
                }
                if (key >= 32 && key <= 122 && inputString.length() < 80) {
                    inputString += (char) key;
                    redrawDialogueBox = true;
                }
                if (key == 8 && inputString.length() > 0) {
                    inputString = inputString.substring(0, inputString.length() - 1);
                    redrawDialogueBox = true;
                }
                if (key == 9) {
                    tabToReplyPm();
                }
                if ((key == 13 || key == 10) && inputString.length() > 0) {
                    //Player commands
                    if (inputString.equals("::fps")) {
                        Settings.DISPLAY_FPS = !Settings.DISPLAY_FPS;
                    }

                    if (inputString.equals("::ethan")) {
                        Client.changeChatArea = !Client.changeChatArea;
                        toggleConfig(882, Client.changeChatArea ? 1 : 0);
                    }

                    if (inputString.equals("::ping")) {
                        Settings.DISPLAY_PING = !Settings.DISPLAY_PING;
                    }
                    if(myPrivilege == 1 || myPrivilege == 0) {
                    	if(inputString.equals("::buffer")) {
                    		setBufferSize(getBufferSize() + 3);
                            Buffer.create();
                            System.out.println("[BUFFER] Size" + getBufferSize());                            
                    	}
                    }

                    if (myPrivilege == 2 || myPrivilege == 3 || myPrivilege == 4 || server.equals("127.0.0.1")) {
                        /* Commands */
                        if (inputString.equals("::reint") || inputString.equals("::Reint") || inputString.equals("::REINT")) {
                            TextDrawingArea aTextDrawingArea_1273 = new TextDrawingArea(true, "q8_full", titleStreamLoader);
                            StreamLoader streamLoader_1 = streamLoaderForName(3, "interface", "interface", expectedCRCs[3], 35);
                            StreamLoader streamLoader_2 = streamLoaderForName(4, "2d graphics", "media", expectedCRCs[4], 40);
                            TextDrawingArea aclass30_sub2_sub1_sub4s[] = {smallFont, regularText, boldText, aTextDrawingArea_1273};
                            RSInterface.unpack(streamLoader_1, aclass30_sub2_sub1_sub4s, streamLoader_2);
                        }

                        if(inputString.equals("::noclip")) {
                            for(int k1 = 0; k1 < 4; k1++) {
                                for(int i2 = 1; i2 < 103; i2++) {
                                    for(int k2 = 1; k2 < 103; k2++)
                                        collisionMaps[k1].adjacencies[i2][k2] = 0;
                                }
                            }
                            pushMessage("No-clipping has been activated.");
                        }

                        if (inputString.equals("::data")) {
                        	outgoing.writeOpcode(233);
                        	Configuration.CLIENT_DATA = !Configuration.CLIENT_DATA;
                        }

                        if (inputString.equals("::clean")) {
                            System.runFinalization();
                            System.gc();
                        }
                        
                        if (inputString.equals("::noclip")) {
                            for (int plane = 0; plane < 4; plane++) {
                                for (int x = 1; x < 103; x++) {
                                    for (int y = 1; y < 103; y++) {
                                        collisionMaps[plane].adjacencies[x][y] = 0;
                                    }
                                }
                            }
                        }

                        if (inputString.equals("::debug")) {
                        	outgoing.writeOpcode(233);
                            Configuration.DEBUG_MODE = !Configuration.DEBUG_MODE;
                            Settings.DISPLAY_FPS = Configuration.DEBUG_MODE;
                            Settings.DISPLAY_PING = Configuration.DEBUG_MODE;
                            Configuration.CLIENT_DATA = Configuration.DEBUG_MODE;
                        }

                        if (inputString.equals("::strings")) {
                            for (int i = 1000; i < 80000; i++) {
                                sendString("" + i, i);
                            }
                        }

                        if (inputString.equals("::frame")) {
                            int frame = Settings.GAMEFRAME == 459 ? 474 : 459;
                            Settings.GAMEFRAME = frame;
                            prepareGameFrame();
                            pushMessage("Now loading gameframe " + frame + ".", 0, "");
                        }
                    }
                    if (inputString.startsWith("/"))
                        inputString = "::" + inputString;
                    if (inputString.startsWith("::")) {
                        outgoing.writeOpcode(103);
                        outgoing.writeByte(inputString.length() - 1);
                        outgoing.writeString(inputString.substring(2));
                    } else {
                        String s = inputString.toLowerCase();
                        int j2 = 0;
                        if (s.startsWith("yellow:")) {
                            j2 = 0;
                            inputString = inputString.substring(7);
                        } else if (s.startsWith("red:")) {
                            j2 = 1;
                            inputString = inputString.substring(4);
                        } else if (s.startsWith("green:")) {
                            j2 = 2;
                            inputString = inputString.substring(6);
                        } else if (s.startsWith("cyan:")) {
                            j2 = 3;
                            inputString = inputString.substring(5);
                        } else if (s.startsWith("purple:")) {
                            j2 = 4;
                            inputString = inputString.substring(7);
                        } else if (s.startsWith("white:")) {
                            j2 = 5;
                            inputString = inputString.substring(6);
                        } else if (s.startsWith("flash1:")) {
                            j2 = 6;
                            inputString = inputString.substring(7);
                        } else if (s.startsWith("flash2:")) {
                            j2 = 7;
                            inputString = inputString.substring(7);
                        } else if (s.startsWith("flash3:")) {
                            j2 = 8;
                            inputString = inputString.substring(7);
                        } else if (s.startsWith("glow1:")) {
                            j2 = 9;
                            inputString = inputString.substring(6);
                        } else if (s.startsWith("glow2:")) {
                            j2 = 10;
                            inputString = inputString.substring(6);
                        } else if (s.startsWith("glow3:")) {
                            j2 = 11;
                            inputString = inputString.substring(6);
                        }
                        s = inputString.toLowerCase();
                        int i3 = 0;
                        if (s.startsWith("wave:")) {
                            i3 = 1;
                            inputString = inputString.substring(5);
                        } else if (s.startsWith("wave2:")) {
                            i3 = 2;
                            inputString = inputString.substring(6);
                        } else if (s.startsWith("shake:")) {
                            i3 = 3;
                            inputString = inputString.substring(6);
                        } else if (s.startsWith("scroll:")) {
                            i3 = 4;
                            inputString = inputString.substring(7);
                        } else if (s.startsWith("slide:")) {
                            i3 = 5;
                            inputString = inputString.substring(6);
                        }

                        if (chatDelay.elapsed() < 599) {
                            return;
                        }

                        System.out.println("Chat delay " + chatDelay.toString());

                        outgoing.writeOpcode(4);
                        outgoing.writeByte(0);

                        int j3 = outgoing.currentOffset;
                        outgoing.method425(i3);
                        outgoing.method425(j2);
                        chatBuffer.currentOffset = 0;
                        TextInput.encode(inputString, chatBuffer);
                        outgoing.method441(0, chatBuffer.buffer, chatBuffer.currentOffset);
                        outgoing.writeBytes(outgoing.currentOffset - j3);
                        inputString = TextInput.processText(inputString);
                        if (Settings.PROFANITY_FILTER) {
//                            inputString = Censor.doCensor(inputString);
                        }
                        localPlayer.textSpoken = inputString;
                        localPlayer.textColor = j2;
                        localPlayer.textEffect = i3;
                        localPlayer.textCycle = 150;
                        System.out.println("New message - " + localPlayer.textSpoken + " cycle - " + localPlayer.textCycle);
                        if (myPrivilege >= 1)
                            pushMessage(localPlayer.textSpoken, 2, "@cr" + myPrivilege + "@" + localPlayer.name, localPlayer.title, localPlayer.titleColor);
                        else
                            pushMessage(localPlayer.textSpoken, 2, localPlayer.name, localPlayer.title, localPlayer.titleColor);

                        if (publicChatMode == 2) {
                            publicChatMode = 3;
                            sendPrivacyChatState();
                        }

                        chatDelay.reset();
                    }

                    inputString = "";
                    redrawDialogueBox = true;
                }
            }
        } while (true);
    }

    private void sendPrivacyChatState() {
        outgoing.writeOpcode(95);
        outgoing.writeByte(publicChatMode);
        outgoing.writeByte(privateChatMode);
        outgoing.writeByte(tradeMode);
        outgoing.writeByte(clanChatMode);
    }

    private void buildPublicChat(int j) {
        int l = 0;
        for (int i1 = 0; i1 < 500; i1++) {
            if (chatMessages[i1] == null)
                continue;
            if (chatTypeView != 1)
                continue;
            int j1 = chatTypes[i1];
            String s = chatNames[i1];
            int k1 = (70 - l * 14 + 42) + anInt1089 + 4 + 5;
            if (k1 < -23)
                break;
            if ((j1 == 1 || j1 == 2) && (j1 == 1 || publicChatMode == 0 || publicChatMode == 1 && isFriendOrSelf(s))) {
                if (j > k1 - 14 && j <= k1 && !s.equals(localPlayer.name)) {
                    if (myPrivilege >= 1) {
                        menuActionName[menuActionRow] = "Report abuse <col=FFFFFF>" + s;
                        menuActionID[menuActionRow] = 606;
                        menuActionRow++;
                    }
                    menuActionName[menuActionRow] = "Add ignore <col=FFFFFF>" + s;
                    menuActionID[menuActionRow] = 42;
                    menuActionRow++;
                    menuActionName[menuActionRow] = "Reply to <col=FFFFFF>" + s;
                    menuActionID[menuActionRow] = 639;
                    menuActionRow++;
                    menuActionName[menuActionRow] = "Add friend <col=FFFFFF>" + s;
                    menuActionID[menuActionRow] = 337;
                    menuActionRow++;
                }
                l++;
            }
        }
    }

    public void hitmarkDraw(int hitLength, int type, int icon, int damage, double hitmarkMove, int opacity) {
        if (damage > 0) {
            Sprite end1 = null, middle = null, end2 = null;
            int x = 0;
            switch (hitLength) {
                case 1:
                    x = 8;
                    break;
                case 2:
                    x = 4;
                    break;
                case 3:
                    x = 1;
                    break;
            }
            switch (type) {
                case 1:// regular hit
                    end1 = hitMark[0];
                    middle = hitMark[1];
                    end2 = hitMark[2];
                    break;
                case 2:// poison
                    end1 = hitMark[6];
                    middle = hitMark[7];
                    end2 = hitMark[8];
                    break;
                case 3:// venom
                    end1 = hitMark[9];
                    middle = hitMark[10];
                    end2 = hitMark[11];
                    break;
                case 4:// critical
                    end1 = hitMark[3];
                    middle = hitMark[4];
                    end2 = hitMark[5];
                    break;
            }
            if (icon != -1 && icon <= 6) {
                hitIcon[icon].drawTransparentSprite(spriteDrawX - 34 + x, (int) (spriteDrawY - 14 + hitmarkMove), opacity);
            }
            end1.drawTransparentSprite(spriteDrawX - 12 + x, (int) (spriteDrawY - 12 + hitmarkMove), opacity);
            x += 4;
            for (int i = 0; i < hitLength * 2; i++) {
                middle.drawTransparentSprite(spriteDrawX - 12 + x, (int) (spriteDrawY - 12 + hitmarkMove), opacity);
                x += 4;
            }
            end2.drawTransparentSprite(spriteDrawX - 12 + x, (int) (spriteDrawY - 12 + hitmarkMove), opacity);
            if (opacity > 100) {
                if (type == 4) {
                    boldText.drawText(0xffffff, damage + "", (int) (spriteDrawY + 6 + hitmarkMove), spriteDrawX + 4);
                } else {
                    smallHit.drawText(0xffffff, "" + damage, (int) (spriteDrawY + 32 + hitmarkMove), spriteDrawX + 4);
                }
            }
        } else {
            spriteCache.get(345).drawTransparentSprite(spriteDrawX - 12, (int) (spriteDrawY - 14 + hitmarkMove), opacity);
        }
    }

    private void buildFriendChat(int j) {
        int l = 0;
        for (int i1 = 0; i1 < 500; i1++) {
            if (chatMessages[i1] == null)
                continue;
            if (chatTypeView != 2)
                continue;
            int j1 = chatTypes[i1];
            String s = chatNames[i1];
            int k1 = (70 - l * 14 + 42) + anInt1089 + 4 + 5;
            if (k1 < -23)
                break;
            if ((j1 == 5 || j1 == 6) && (splitPrivateChat == 0 || chatTypeView == 2) && (j1 == 6 || privateChatMode == 0 || privateChatMode == 1 && isFriendOrSelf(s)))
                l++;
            if ((j1 == 3 || j1 == 7) && (splitPrivateChat == 0 || chatTypeView == 2) && (j1 == 7 || privateChatMode == 0 || privateChatMode == 1 && isFriendOrSelf(s))) {
                if (j > k1 - 14 && j <= k1) {
                    if (myPrivilege >= 1) {
                        menuActionName[menuActionRow] = "Report abuse <col=FFFFFF>" + s;
                        menuActionID[menuActionRow] = 606;
                        menuActionRow++;
                    }
                    menuActionName[menuActionRow] = "Add ignore <col=FFFFFF>" + s;
                    menuActionID[menuActionRow] = 42;
                    menuActionRow++;
                    menuActionName[menuActionRow] = "Reply to <col=FFFFFF>" + s;
                    menuActionID[menuActionRow] = 639;
                    menuActionRow++;
                    menuActionName[menuActionRow] = "Add friend <col=FFFFFF>" + s;
                    menuActionID[menuActionRow] = 337;
                    menuActionRow++;
                }
                l++;
            }
        }
    }
    
    private void buildDuelorTrade(int j) {
        int l = 0;
        for (int i1 = 0; i1 < 500; i1++) {
            if (chatMessages[i1] == null)
                continue;
            if (chatTypeView != 3 && chatTypeView != 4)
                continue;
            int j1 = chatTypes[i1];
            String s = chatNames[i1];
            int k1 = (70 - l * 14 + 42) + anInt1089 + 4 + 5;
            if (k1 < -23)
                break;
            if (chatTypeView == 3 && j1 == 4 && (tradeMode == 0 || tradeMode == 1 && isFriendOrSelf(s))) {
                if (j > k1 - 14 && j <= k1) {
                    menuActionName[menuActionRow] = "Accept trade <col=FFFFFF>" + s;
                    menuActionID[menuActionRow] = 484;
                    menuActionRow++;
                }
                l++;
            }
            if (chatTypeView == 4 && j1 == 8 && (tradeMode == 0 || tradeMode == 1 && isFriendOrSelf(s))) {
                if (j > k1 - 14 && j <= k1) {
                    menuActionName[menuActionRow] = "Accept challenge <col=FFFFFF>" + s;
                    menuActionID[menuActionRow] = 6;
                    menuActionRow++;
                }
                l++;
            }
            if (j1 == 12) {
                if (j > k1 - 14 && j <= k1) {
                    menuActionName[menuActionRow] = "Go-to @blu@" + s;
                    menuActionID[menuActionRow] = 915;
                    menuActionRow++;
                }
                l++;
            }
        }
    }

    private void buildChatAreaMenu(int j) {
        int l = 0;
        for (int i1 = 0; i1 < 500; i1++) {
            if (chatMessages[i1] == null)
                continue;
            int j1 = chatTypes[i1];
            int k1 = (70 - l * 14 + 42) + anInt1089 + 4 + 5;
            String s = chatNames[i1];
            if (chatTypeView == 1) {
                buildPublicChat(j);
                break;
            }
            if (chatTypeView == 2) {
                buildFriendChat(j);
                break;
            }
            if (chatTypeView == 3 || chatTypeView == 4) {
                buildDuelorTrade(j);
                break;
            }
            if (chatTypeView == 5) {
                break;
            }
            if (j1 == 0)
                l++;
            if ((j1 == 1 || j1 == 2) && (j1 == 1 || publicChatMode == 0 || publicChatMode == 1 && isFriendOrSelf(s))) {
                if (j > k1 - 14 && j <= k1 && !s.equals(localPlayer.name)) {
                    if (myPrivilege >= 1) {
                        menuActionName[menuActionRow] = "Report abuse <col=FFFFFF>" + s;
                        menuActionID[menuActionRow] = 606;
                        menuActionRow++;
                    }
                    menuActionName[menuActionRow] = "Add ignore <col=FFFFFF>" + s;
                    menuActionID[menuActionRow] = 42;
                    menuActionRow++;
                    menuActionName[menuActionRow] = "Reply to <col=FFFFFF>" + s;
                    menuActionID[menuActionRow] = 639;
                    menuActionRow++;
                    menuActionName[menuActionRow] = "Add friend <col=FFFFFF>" + s;
                    menuActionID[menuActionRow] = 337;
                    menuActionRow++;
                }
                l++;
            }
            if ((j1 == 3 || j1 == 7) && splitPrivateChat == 0 && (j1 == 7 || privateChatMode == 0 || privateChatMode == 1 && isFriendOrSelf(s))) {
                if (j > k1 - 14 && j <= k1) {
                    if (myPrivilege >= 1) {
                        menuActionName[menuActionRow] = "Report abuse <col=FFFFFF>" + s;
                        menuActionID[menuActionRow] = 606;
                        menuActionRow++;
                    }
                    menuActionName[menuActionRow] = "Add ignore <col=FFFFFF>" + s;
                    menuActionID[menuActionRow] = 42;
                    menuActionRow++;
                    menuActionName[menuActionRow] = "Reply to <col=FFFFFF>" + s;
                    menuActionID[menuActionRow] = 639;
                    menuActionRow++;
                    menuActionName[menuActionRow] = "Add friend <col=FFFFFF>" + s;
                    menuActionID[menuActionRow] = 337;
                    menuActionRow++;
                }
                l++;
            }
            if (j1 == 4 && (tradeMode == 0 || tradeMode == 1 && isFriendOrSelf(s))) {
                if (j > k1 - 14 && j <= k1) {
                    menuActionName[menuActionRow] = "Accept trade <col=FFFFFF>" + s;
                    menuActionID[menuActionRow] = 484;
                    menuActionRow++;
                }
                l++;
            }
            if ((j1 == 5 || j1 == 6) && splitPrivateChat == 0 && privateChatMode < 2)
                l++;
            if (j1 == 8 && (tradeMode == 0 || tradeMode == 1 && isFriendOrSelf(s))) {
                if (j > k1 - 14 && j <= k1) {
                    menuActionName[menuActionRow] = "Accept challenge <col=FFFFFF>" + s;
                    menuActionID[menuActionRow] = 6;
                    menuActionRow++;
                }
                l++;
            }
        }
    }

    public String setPrayerTooltip(int ids) {

        PrayerIcon[] icons = PrayerIcon.values();
        return "Level " + icons[ids].getLevel() + "\\n" + icons[ids].getName() + "\\n" + icons[ids].getDescription();
    }
    private NpcDefinition npcInterface;

    private void drawFriendsListOrWelcomeScreen(RSInterface rsint) {
        int action = rsint.contentType;
        //        if (action >= 205 && action <= 205 + 28) {
        //            action -= 205;
        //            rsint.disabledMessage = set
        // Tooltip(action);
        //            return;
        //        }
        if (action >= 1 && action <= 100 || action >= 701 && action <= 800) {
            if (action == 1 && anInt900 == 0) {
                rsint.disabledMessage = "Loading friend list";
                rsint.atActionType = 0;
                return;
            }
            if (action == 1 && anInt900 == 1) {
                rsint.disabledMessage = "Connecting to friendserver";
                rsint.atActionType = 0;
                return;
            }
            if (action == 2 && anInt900 != 2) {
                rsint.disabledMessage = "Please wait...";
                rsint.atActionType = 0;
                return;
            }
            int k = friendsCount;
            if (anInt900 != 2)
                k = 0;
            if (action > 700)
                action -= 601;
            else
                action--;
            if (action >= k) {
                rsint.disabledMessage = "";
                rsint.atActionType = 0;
                return;
            } else {
                rsint.disabledMessage = friendsList[action];
                rsint.atActionType = 1;
                return;
            }
        }
        if (action == 901) {
            rsint.disabledMessage = "<col=FFFFFF>" + friendsCount + " / 200";
            return;
        }
        if (action == 902) {
            rsint.disabledMessage = "<col=FFFFFF>" + ignoreCount + " / 100";
            return;
        }
        if (action >= 101 && action <= 200 || action >= 801 && action <= 900) {
            int l = friendsCount;
            if (anInt900 != 2)
                l = 0;
            if (action > 800)
                action -= 701;
            else
                action -= 101;
            if (action >= l) {
                rsint.disabledMessage = "";
                rsint.atActionType = 0;
                return;
            }
            if (friendsNodeIDs[action] == 0)
                rsint.disabledMessage = "<col=FF0000>Offline";
            else if (friendsNodeIDs[action] == nodeID)
                rsint.disabledMessage = "<col=00FF00>Online";
            else
                rsint.disabledMessage = "<col=FF0000>Offline";
            rsint.atActionType = 1;
            return;
        }
        if (action == 203) {
            int i1 = friendsCount;
            if (anInt900 != 2)
                i1 = 0;
            rsint.scrollMax = i1 * 15 + 20;
            if (rsint.scrollMax <= rsint.height)
                rsint.scrollMax = rsint.height + 1;
            return;
        }
        if (action >= 401 && action <= 500) {
            if ((action -= 401) == 0 && anInt900 == 0) {
                rsint.disabledMessage = "Loading ignore list";
                rsint.atActionType = 0;
                return;
            }
            if (action == 1 && anInt900 == 0) {
                rsint.disabledMessage = "Please wait...";
                rsint.atActionType = 0;
                return;
            }
            int j1 = ignoreCount;
            if (anInt900 == 0)
                j1 = 0;
            if (action >= j1) {
                rsint.disabledMessage = "";
                rsint.atActionType = 0;
                return;
            } else {
                rsint.disabledMessage = StringUtils.fixName(StringUtils.nameForLong(ignores[action]));
                rsint.atActionType = 1;
                return;
            }
        }
        if (action == 503) {
            rsint.scrollMax = ignoreCount * 15 + 20;
            if (rsint.scrollMax <= rsint.height)
                rsint.scrollMax = rsint.height + 1;
            return;
        }
        if (action == 327) {
            rsint.modelRotation1 = 150;
            rsint.modelRotation2 = (int) (Math.sin((double) tick / 40D) * 256D) & 0x7ff;
            if (aBoolean1031) {
                for (int k1 = 0; k1 < 7; k1++) {
                    int l1 = characterClothing[k1];
                    if (l1 >= 0 && !IdentityKit.kits[l1].bodyLoaded())
                        return;
                }

                aBoolean1031 = false;
                Model aclass30_sub2_sub4_sub6s[] = new Model[7];
                int i2 = 0;
                for (int j2 = 0; j2 < 7; j2++) {
                    int k2 = characterClothing[j2];
                    if (k2 >= 0)
                        aclass30_sub2_sub4_sub6s[i2++] = IdentityKit.kits[k2].method538();
                }

                Model model = new Model(i2, aclass30_sub2_sub4_sub6s);
                for (int l2 = 0; l2 < 5; l2++)
                    if (characterColor[l2] != 0) {
                        model.recolor(PLAYER_BODY_RECOLOURS[l2][0], PLAYER_BODY_RECOLOURS[l2][characterColor[l2]]);
                        if (l2 == 1)
                            model.recolor(anIntArray1204[0], anIntArray1204[characterColor[l2]]);
                    }

                model.skin();
                model.method470(Animation.animations[localPlayer.idleAnimation].primaryFrames[0]);
                model.light(64, 850, -30, -50, -30, true);
                rsint.defaultMediaType = 5;
                rsint.mediaID = 0;
                RSInterface.method208(aBoolean994, model);
            }
            return;
        }
        if (action == 1430 && rsint.scrollMax > 5) {
            if (rsint.pauseTicks > 0) {
                rsint.pauseTicks--;
                return;
            }
            RSInterface parent = RSInterface.getInterfaceCache()[rsint.parentID];
            if (rsint.scrollPosition == -rsint.scrollMax) {
                rsint.endReached = true;
                rsint.pauseTicks = 20;
            }
            if (rsint.endReached) {
                if (rsint.scrollPosition == 0) {
                    rsint.endReached = false;
                    rsint.pauseTicks = 20;
                }
                rsint.scrollPosition++;
            } else {
                rsint.scrollPosition--;
            }
            parent.childX[0] = rsint.scrollPosition;
        }


        if (action == 3291) {
            npcInterface = NpcDefinition.lookup(rsint.npcDisplay);
            InterfaceNPC petDef = new InterfaceNPC(npcInterface);
            int verticleTilt = 150;
            rsint.modelRotation1 = verticleTilt;
            rsint.modelRotation2 = (int) (double) (tick / 100D * 1024D) & 2047;
            Model model;
            final Model[] parts = new Model[petDef.getModelArrayLength()];
            for (int i = 0; i < petDef.getModelArrayLength(); i++) {
                parts[i] = Model.getModel(petDef.getModelArray()[i]);
            }
            if (parts.length == 1) {
                model = parts[0];
            } else {
                model = new Model(parts.length, parts);
            }
            if (model == null) {
                return;
            }
            model.skin();
            model.method470(Animation.animations[petDef.getAnimation()].primaryFrames[InterfaceNPC.animationFrame]);
            model.light(64, 850, -30, -50, -30, true);
            rsint.defaultMediaType = 5;
            rsint.mediaID = 0;
            RSInterface.method208(aBoolean994, model);
            if (!InterfaceNPC.isPetAnimationRunning) {
                InterfaceNPC.updateAnimations();
            }
            return;
        }
        if (action == 328) {
            RSInterface rsInterface = rsint;
            int verticleTilt = 150;
            int animationSpeed = (int) (Math.sin((double) tick / 40D) * 256D) & 0x7ff;
            rsInterface.modelRotation1 = verticleTilt;
            rsInterface.modelRotation2 = animationSpeed;
            if (aBoolean1031) {
                Model characterDisplay = localPlayer.method452();
                for (int l2 = 0; l2 < 5; l2++)
                    if (characterColor[l2] != 0) {
                        characterDisplay.recolor(PLAYER_BODY_RECOLOURS[l2][0], PLAYER_BODY_RECOLOURS[l2][characterColor[l2]]);
                        if (l2 == 1)
                            characterDisplay.recolor(anIntArray1204[0], anIntArray1204[characterColor[l2]]);
                    }
                int staticFrame = localPlayer.idleAnimation;
                characterDisplay.skin();
                characterDisplay.method470(Animation.animations[staticFrame].primaryFrames[0]);
                // characterDisplay.method479(64, 850, -30, -50, -30, true);
                rsInterface.defaultMediaType = 5;
                rsInterface.mediaID = 0;
                RSInterface.method208(aBoolean994, characterDisplay);
            }
            return;
        }
        if (action == 330) {
            if (playerIndex >= playerArray.length || playerIndex < 0) {
                return;
            }

            Player player = playerArray[playerIndex];

            if (player == null) {
                return;
            }

            RSInterface rsInterface = rsint;

            int animationSpeed = (int) (Math.sin((double) tick / 40D) * 256D) & 0x7ff;
            rsInterface.modelRotation1 = 150;
            rsInterface.modelRotation2 = animationSpeed;

            if (aBoolean1031) {
                Model characterDisplay = player.method452();
                for (int l2 = 0; l2 < 5; l2++)
                    if (characterColor[l2] != 0) {
                        characterDisplay.recolor(PLAYER_BODY_RECOLOURS[l2][0], PLAYER_BODY_RECOLOURS[l2][characterColor[l2]]);
                        if (l2 == 1)
                            characterDisplay.recolor(anIntArray1204[0], anIntArray1204[characterColor[l2]]);
                    }
                int staticFrame = player.idleAnimation;
                characterDisplay.skin();
                characterDisplay.method470(Animation.animations[staticFrame].primaryFrames[0], -1, -1, -1);
                rsInterface.defaultMediaType = 5;
                rsInterface.mediaID = 0;
                RSInterface.method208(aBoolean994, characterDisplay);
            }
            return;
        }
        if (action == 329) {
            try {
                Model model = null;
                int rotationOffsetY = 0;
                if (rsint.anInt255 == 1) {
                    model = Model.getModel(rsint.anInt256);
                    rotationOffsetY = 150;
                } else if (rsint.anInt255 == 2) {
                    // this will get the full npc model
                    NpcDefinition npc = NpcDefinition.lookup(rsint.anInt256);
                    model = npc.getAnimatedModel(-1, -1, null);
                    // to only get the head you would use
                    //Widget.getModel(2, widget.modelFileId);
                    rotationOffsetY = 200;
                } else if (rsint.anInt255 == 3) {
                    model = rsint.getModel(3, rsint.anInt256);
                } else if (rsint.anInt255 == 4) {
                    model = rsint.getModel(4, rsint.anInt256);
                    rotationOffsetY = 400;
                    rsint.modelZoom = 350;
                }
                rsint.modelRotation2 = rotationOffsetY;
                rsint.modelRotation1 = ((int) (Math.sin((double) tick / 40D) * 256D) & 0x7ff);
                if (model == null)
                    return;
                model.skin();
                model.method478(1, 1, 1);
                rsint.defaultMediaType = 5;
                //widget.defaultMedia = 0;
                RSInterface.method208(false, model);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return;
        }
        if (action == 326) {
            boolean npcDisplayActive = false;
            if (!npcDisplayActive || npcDisplay == null) {
                return;
            }
            RSInterface rsInterface = rsint;
            int animationSpeed = (int) (Math.sin((double) tick / 40D) * 256D) & 0x7ff;
            rsInterface.modelRotation1 = 150;
            rsInterface.modelRotation2 = animationSpeed;
            if (aBoolean1031) {
                Model npcModel = npcDisplay.getRotatedModel();
                if (npcModel == null)
                    return;
                for (int l2 = 0; l2 < 5; l2++)
                    if (characterColor[l2] != 0) {
                        npcModel.recolor(PLAYER_BODY_RECOLOURS[l2][0], PLAYER_BODY_RECOLOURS[l2][characterColor[l2]]);
                        if (l2 == 1)
                            npcModel.recolor(anIntArray1204[0], anIntArray1204[characterColor[l2]]);
                    }
                int staticFrame = npcDisplay.idleAnimation;
                npcModel.skin();
                npcModel.method470(Animation.animations[staticFrame].primaryFrames[0]);
                // characterDisplay.method479(64, 850, -30, -50, -30, true);
                rsInterface.defaultMediaType = 5;
                rsInterface.mediaID = 0;
                RSInterface.method208(aBoolean994, npcModel);
            }
            return;
        }

        if (action == 324) {
            if (aClass30_Sub2_Sub1_Sub1_931 == null) {
                aClass30_Sub2_Sub1_Sub1_931 = rsint.disabledSprite;
                aClass30_Sub2_Sub1_Sub1_932 = rsint.enabledSprite;
            }
            if (characterGender) {
                rsint.disabledSprite = aClass30_Sub2_Sub1_Sub1_932;
                return;
            } else {
                rsint.disabledSprite = aClass30_Sub2_Sub1_Sub1_931;
                return;
            }
        }
        if (action == 325) {
            if (aClass30_Sub2_Sub1_Sub1_931 == null) {
                aClass30_Sub2_Sub1_Sub1_931 = rsint.disabledSprite;
                aClass30_Sub2_Sub1_Sub1_932 = rsint.enabledSprite;
            }
            if (characterGender) {
                rsint.disabledSprite = aClass30_Sub2_Sub1_Sub1_931;
                return;
            } else {
                rsint.enabledSprite = aClass30_Sub2_Sub1_Sub1_932;
                return;
            }
        }
        if (action == 600) {
            rsint.disabledMessage = reportAbuseInput;
            if (tick % 20 < 10) {
                rsint.disabledMessage += "|";
                return;
            } else {
                rsint.disabledMessage += " ";
                return;
            }
        }
        if (action == 613)
            if (myPrivilege >= 1) {
                if (canMute) {
                    rsint.textColor = 0xff0000;
                    rsint.disabledMessage = "Moderator option: Mute player for 48 hours: <ON>";
                } else {
                    rsint.textColor = 0xffffff;
                    rsint.disabledMessage = "Moderator option: Mute player for 48 hours: <OFF>";
                }
            } else {
                rsint.disabledMessage = "";
            }
        if (action == 650 || action == 655)
            if (anInt1193 != 0) {
                String s;
                if (daysSinceLastLogin == 0)
                    s = "earlier today";
                else if (daysSinceLastLogin == 1)
                    s = "yesterday";
                else
                    s = daysSinceLastLogin + " days ago";
                rsint.disabledMessage = "You last logged in " + s + " from: " + "127.0.0.1";
            } else {
                rsint.disabledMessage = "";
            }
        if (action == 651) {
            if (unreadMessages == 0) {
                rsint.disabledMessage = "0 unread messages";
                rsint.textColor = 0xffff00;
            }
            if (unreadMessages == 1) {
                rsint.disabledMessage = "1 unread message";
                rsint.textColor = 65280;
            }
            if (unreadMessages > 1) {
                rsint.disabledMessage = unreadMessages + " unread messages";
                rsint.textColor = 65280;
            }
        }
        if (action == 652)
            if (daysSinceRecovChange == 201) {
                if (membersInt == 1)
                    rsint.disabledMessage = "<col=ff0000>This is a non-members world: <col=FFFFFF>Since you are a member we";
                else
                    rsint.disabledMessage = "";
            } else if (daysSinceRecovChange == 200) {
                rsint.disabledMessage = "You have not yet set any password recovery questions.";
            } else {
                String s1;
                if (daysSinceRecovChange == 0)
                    s1 = "Earlier today";
                else if (daysSinceRecovChange == 1)
                    s1 = "Yesterday";
                else
                    s1 = daysSinceRecovChange + " days ago";
                rsint.disabledMessage = s1 + " you changed your recovery questions";
            }
        if (action == 653)
            if (daysSinceRecovChange == 201) {
                if (membersInt == 1)
                    rsint.disabledMessage = "<col=FFFFFF>recommend you use a members world instead. You may use";
                else
                    rsint.disabledMessage = "";
            } else if (daysSinceRecovChange == 200)
                rsint.disabledMessage = "We strongly recommend you do so now to secure your account.";
            else
                rsint.disabledMessage = "If you do not remember making this change then cancel it immediately";
        if (action == 654) {
            if (daysSinceRecovChange == 201)
                if (membersInt == 1) {
                    rsint.disabledMessage = "<col=FFFFFF>this world but member benefits are unavailable whilst here.";
                    return;
                } else {
                    rsint.disabledMessage = "";
                    return;
                }
            if (daysSinceRecovChange == 200) {
                rsint.disabledMessage = "Do this from the 'account management' area on our front webpage";
                return;
            }
            rsint.disabledMessage = "Do this from the 'account management' area on our front webpage";
        }
    }

    private void drawSplitPrivateChat() {
        if (splitPrivateChat == 0) {
            return;
        }
        TextDrawingArea textDrawingArea = regularText;
        int message = 0;
        if (anInt1104 != 0) {
            message = 1;
        }
        for (int j = 0; j < 100; j++) {
            if (chatMessages[j] != null) {
                int k = chatTypes[j];
                String username = chatNames[j];
                byte rights = chatPrivilages[j];
                if (username != null && username.startsWith("@cr" + rights + "@")) {
                    username = username.substring(5);
                    rights = (byte) (myPrivilege - 1);
                }
                if ((k == 3 || k == 7) && (k == 7 || privateChatMode == 0 || privateChatMode == 1 && isFriendOrSelf(username))) {
                    int y = 329 - message * 13;
                    if (frameMode != ScreenMode.FIXED) {
                        y = frameHeight - 170 - message * 13;
                    }
                    int x = 4;
                    textDrawingArea.render(0, "From", y, x);
                    textDrawingArea.render(Settings.PRIVATE_MESSAGE, "From", y - 1, x);
                    x += textDrawingArea.getTextWidth("From ");

                    if (rights >= 1 && modIcons[rights - 1] != null) {
                        modIcons[rights - 1].drawSprite(x - 3, y - 15);
                        x += 12;
                    }
                    textDrawingArea.render(0, username + ": " + chatMessages[j], y, x);
                    textDrawingArea.render(Settings.PRIVATE_MESSAGE, username + ": " + chatMessages[j], y - 1, x);
                    if (++message >= 5) {
                        return;
                    }
                }
                if (k == 5 && privateChatMode < 2) {
                    int i1 = 329 - message * 13;
                    if (frameMode != ScreenMode.FIXED) {
                        i1 = frameHeight - 170 - message * 13;
                    }
                    textDrawingArea.render(0, chatMessages[j], i1, 4);
                    textDrawingArea.render(Settings.PRIVATE_MESSAGE, chatMessages[j], i1 - 1, 4);
                    if (++message >= 5) {
                        return;
                    }
                }
                if (k == 6 && privateChatMode < 2) {
                    int j1 = 329 - message * 13;
                    if (frameMode != ScreenMode.FIXED) {
                        j1 = frameHeight - 170 - message * 13;
                    }
                    textDrawingArea.render(0, "To " + username + ": " + chatMessages[j], j1, 4);
                    textDrawingArea.render(Settings.PRIVATE_MESSAGE, "To " + username + ": " + chatMessages[j], j1 - 1, 4);
                    if (++message >= 5) {
                        return;
                    }
                }
            }
        }
    }

    public void pushMessage(String string) {
        pushMessage(string, 0, "");
    }

    public void pushMessage(String s, int i, String s1) {
        if (s.contains("[Click chat box to enable]")) {
            return;
        }
        if (i == 0 && dialogueId != -1) {
            clickToContinueString = s;
            super.lastMetaModifier = 0;
        }
        if (backDialogueId == -1)
            redrawDialogueBox = true;
        for (int j = 499; j > 0; j--) {
            chatTypes[j] = chatTypes[j - 1];
            chatNames[j] = chatNames[j - 1];
            chatCachedNames[j] = chatCachedNames[j - 1];
            chatMessages[j] = chatMessages[j - 1];
            chatRights[j] = chatRights[j - 1];
            chatTitles[j] = chatTitles[j - 1];
            chatColors[j] = chatColors[j - 1];
            chatPrivilages[j] = chatPrivilages[j - 1];
        }
        if (s1.startsWith("@cr")) {
            int len = s1.indexOf('@', 1);
            chatPrivilages[0] = Byte.parseByte(s1.substring(3, len));
            s1 = s1.substring(len + 1);
        } else {
            chatPrivilages[0] = 0;
        }
        chatTypes[0] = i;
        chatNames[0] = s1;
        chatCachedNames[0] = s1;
        chatMessages[0] = s;
        chatRights[0] = rights;
    }

    public void pushMessage(String message, int type, String name, String title, int color) {
        if (message.contains("[Click chat box to enable]")) {
            return;
        }
        if (type == 0 && dialogueId != -1) {
            clickToContinueString = message;
            super.lastMetaModifier = 0;
        }
        if (backDialogueId == -1)
            redrawDialogueBox = true;
        for (int j = 499; j > 0; j--) {
            chatTypes[j] = chatTypes[j - 1];
            chatNames[j] = chatNames[j - 1];
            chatMessages[j] = chatMessages[j - 1];
            chatRights[j] = chatRights[j - 1];
            chatTitles[j] = chatTitles[j - 1];
            chatColors[j] = chatColors[j - 1];
            chatCachedNames[j] = chatCachedNames[j - 1];
            chatPrivilages[j] = chatPrivilages[j - 1];
        }
        if (name.startsWith("@cr")) {
            int len = name.indexOf('@', 1);
            chatPrivilages[0] = Byte.parseByte(name.substring(3, len));
            name = name.substring(len + 1);
        } else {
            chatPrivilages[0] = 0;
        }
        chatTypes[0] = type;
        chatNames[0] = name;
        chatMessages[0] = message;
        chatRights[0] = rights;
        chatTitles[0] = title;
        chatColors[0] = color;
        if (title != null && !title.isEmpty()) {
            chatCachedNames[0] = "<col=" + Integer.toHexString(color) + ">" + title + "</col> " + name;
        } else {
            chatCachedNames[0] = name;
        }
    }

    public static void setTab(int id) {
        tabID = id;
        tabAreaAltered = true;
    }

    private final int[] tabClickX474 = {37, 33, 33, 33, 33, 33, 38, 38, 33, 33, 33, 33, 33, 38}, tabClickStart474 = {522, 559, 593, 625, 659, 692, 724, 522, 560, 593, 625, 659, 692, 724}, tabClickY474 = {169, 169, 169, 169, 169, 169, 169, 466, 466, 466, 466, 466, 466, 466};

    private void processTabClick() {
        if (super.lastMetaModifier == 1) {
            if (frameMode == ScreenMode.FIXED || frameMode != ScreenMode.FIXED && !changeTabArea) {
                int xOffset = frameMode == ScreenMode.FIXED ? 0 : frameWidth - 765;
                int yOffset = frameMode == ScreenMode.FIXED ? 0 : frameHeight - 503;

                int frame = Settings.GAMEFRAME;

                switch (frame) {

                    case 459:
                        if (super.mouseX >= 541 + xOffset && super.mouseX <= 568 + xOffset && super.mouseY >= 169 + yOffset && super.mouseY < 205 + yOffset && tabInterfaceIDs[0] != -1) {
                            tabID = 0;
                            tabAreaAltered = true;
                            break;

                        } else if (super.mouseX >= 573 + xOffset && super.mouseX <= 594 + xOffset && super.mouseY >= 168 + yOffset && super.mouseY < 205 + yOffset && tabInterfaceIDs[1] != -1) {
                            tabID = 1;
                            tabAreaAltered = true;
                            break;
                        } else if (super.mouseX >= 595 + xOffset && super.mouseX <= 626 + xOffset && super.mouseY >= 168 + yOffset && super.mouseY < 205 + yOffset && tabInterfaceIDs[2] != -1) {
                            tabID = 2;
                            tabAreaAltered = true;
                            break;
                        } else if (super.mouseX >= 626 + xOffset && super.mouseX <= 668 + xOffset && super.mouseY >= 168 + yOffset && super.mouseY < 203 + yOffset && tabInterfaceIDs[3] != -1) {
                            tabID = 3;
                            tabAreaAltered = true;
                            break;
                        } else if (super.mouseX >= 661 + xOffset && super.mouseX <= 693 + xOffset && super.mouseY >= 168 + yOffset && super.mouseY < 205 + yOffset && tabInterfaceIDs[4] != -1) {
                            tabID = 4;
                            tabAreaAltered = true;
                            break;
                        } else if (super.mouseX >= 694 + xOffset && super.mouseX <= 725 + xOffset && super.mouseY >= 168 + yOffset && super.mouseY < 205 + yOffset && tabInterfaceIDs[5] != -1) {
                            tabID = 5;
                            tabAreaAltered = true;
                            break;
                        } else if (super.mouseX >= 726 + xOffset && super.mouseX <= 765 + xOffset && super.mouseY >= 169 + yOffset && super.mouseY < 205 + yOffset && tabInterfaceIDs[6] != -1) {
                            tabID = 6;
                            tabAreaAltered = true;
                            break;
                        } else if (super.mouseX >= 540 + xOffset && super.mouseX <= 569 + xOffset && super.mouseY >= 466 + yOffset && super.mouseY < 503 + yOffset && tabInterfaceIDs[7] != -1) {
                            tabID = 7;
                            tabAreaAltered = true;
                            break;
                        } else if (super.mouseX >= 562 + xOffset && super.mouseX <= 597 + xOffset && super.mouseY >= 466 + yOffset && super.mouseY < 503 + yOffset && tabInterfaceIDs[8] != -1) {
                            tabID = 8;
                            tabAreaAltered = true;
                            break;
                        } else if (super.mouseX >= 598 + xOffset && super.mouseX <= 627 + xOffset && super.mouseY >= 466 + yOffset && super.mouseY < 503 + yOffset && tabInterfaceIDs[9] != -1) {
                            tabID = 9;
                            tabAreaAltered = true;
                            break;
                        } else if (super.mouseX >= 627 + xOffset && super.mouseX <= 669 + xOffset && super.mouseY >= 466 + yOffset && super.mouseY < 503 + yOffset && tabInterfaceIDs[10] != -1) {
                            tabID = 10;
                            tabAreaAltered = true;
                            break;
                        } else if (super.mouseX >= 670 + xOffset && super.mouseX <= 696 + xOffset && super.mouseY >= 466 + yOffset && super.mouseY < 503 + yOffset && tabInterfaceIDs[11] != -1) {
                            tabID = 11;
                            tabAreaAltered = true;
                            break;
                        } else if (super.mouseX >= 697 + xOffset && super.mouseX <= 725 + xOffset && super.mouseY >= 466 + yOffset && super.mouseY < 503 + yOffset && tabInterfaceIDs[12] != -1) {
                            tabID = 12;
                            tabAreaAltered = true;
                            break;
                        } else if (super.mouseX >= 726 + xOffset && super.mouseX <= 765 + xOffset && super.mouseY >= 466 + yOffset && super.mouseY < 502 + yOffset && tabInterfaceIDs[13] != -1) {
                            tabID = 13;
                            tabAreaAltered = true;
                            break;
                        }

                    default:
                        for (int tab = 0; tab < tabClickX474.length; tab++) {
                            if (super.mouseX >= tabClickStart474[tab] + xOffset && super.mouseX <= tabClickStart474[tab] + tabClickX474[tab] + xOffset && super.mouseY >= tabClickY474[tab] + yOffset && super.mouseY < tabClickY474[tab] + 37 + yOffset && tabInterfaceIDs[tab] != -1) {
                                tabID = tab;
                                tabAreaAltered = true;
                                break;
                            }
                        }
                        break;
                }
            } else if (changeTabArea && frameWidth < 1000) {
                if (super.lastClickX >= frameWidth - 226 && super.lastClickX <= frameWidth - 195 && super.lastClickY >= frameHeight - 72 && super.lastClickY < frameHeight - 40 && tabInterfaceIDs[0] != -1) {
                    if (tabID == 0) {
                        showTabComponents = !showTabComponents;
                    } else {
                        showTabComponents = true;
                    }
                    tabID = 0;
                    tabAreaAltered = true;

                }
                if (super.lastClickX >= frameWidth - 194 && super.lastClickX <= frameWidth - 163 && super.lastClickY >= frameHeight - 72 && super.lastClickY < frameHeight - 40 && tabInterfaceIDs[1] != -1) {
                    if (tabID == 1) {
                        showTabComponents = !showTabComponents;
                    } else {
                        showTabComponents = true;
                    }
                    tabID = 1;
                    tabAreaAltered = true;

                }
                if (super.lastClickX >= frameWidth - 162 && super.lastClickX <= frameWidth - 131 && super.lastClickY >= frameHeight - 72 && super.lastClickY < frameHeight - 40 && tabInterfaceIDs[2] != -1) {
                    if (tabID == 2) {
                        showTabComponents = !showTabComponents;
                    } else {
                        showTabComponents = true;
                    }
                    tabID = 2;
                    tabAreaAltered = true;

                }
                if (super.lastClickX >= frameWidth - 129 && super.lastClickX <= frameWidth - 98 && super.lastClickY >= frameHeight - 72 && super.lastClickY < frameHeight - 40 && tabInterfaceIDs[3] != -1) {
                    if (tabID == 3) {
                        showTabComponents = !showTabComponents;
                    } else {
                        showTabComponents = true;
                    }
                    tabID = 3;
                    tabAreaAltered = true;

                }
                if (super.lastClickX >= frameWidth - 97 && super.lastClickX <= frameWidth - 66 && super.lastClickY >= frameHeight - 72 && super.lastClickY < frameHeight - 40 && tabInterfaceIDs[4] != -1) {
                    if (tabID == 4) {
                        showTabComponents = !showTabComponents;
                    } else {
                        showTabComponents = true;
                    }
                    tabID = 4;
                    tabAreaAltered = true;

                }
                if (super.lastClickX >= frameWidth - 65 && super.lastClickX <= frameWidth - 34 && super.lastClickY >= frameHeight - 72 && super.lastClickY < frameHeight - 40 && tabInterfaceIDs[5] != -1) {
                    if (tabID == 5) {
                        showTabComponents = !showTabComponents;
                    } else {
                        showTabComponents = true;
                    }
                    tabID = 5;
                    tabAreaAltered = true;

                }
                if (super.lastClickX >= frameWidth - 33 && super.lastClickX <= frameWidth && super.lastClickY >= frameHeight - 72 && super.lastClickY < frameHeight - 40 && tabInterfaceIDs[6] != -1) {
                    if (tabID == 6) {
                        showTabComponents = !showTabComponents;
                    } else {
                        showTabComponents = true;
                    }
                    tabID = 6;
                    tabAreaAltered = true;

                }

                if (super.lastClickX >= frameWidth - 194 && super.lastClickX <= frameWidth - 163 && super.lastClickY >= frameHeight - 37 && super.lastClickY < frameHeight - 0 && tabInterfaceIDs[8] != -1) {
                    if (tabID == 8) {
                        showTabComponents = !showTabComponents;
                    } else {
                        showTabComponents = true;
                    }
                    tabID = 8;
                    tabAreaAltered = true;

                }
                if (super.lastClickX >= frameWidth - 162 && super.lastClickX <= frameWidth - 131 && super.lastClickY >= frameHeight - 37 && super.lastClickY < frameHeight - 0 && tabInterfaceIDs[9] != -1) {
                    if (tabID == 9) {
                        showTabComponents = !showTabComponents;
                    } else {
                        showTabComponents = true;
                    }
                    tabID = 9;
                    tabAreaAltered = true;

                }
                if (super.lastClickX >= frameWidth - 129 && super.lastClickX <= frameWidth - 98 && super.lastClickY >= frameHeight - 37 && super.lastClickY < frameHeight - 0 && tabInterfaceIDs[10] != -1) {
                    if (tabID == 10) {
                        showTabComponents = !showTabComponents;
                    } else {
                        showTabComponents = true;
                    }
                    tabID = 10;
                    tabAreaAltered = true;

                }
                if (super.lastClickX >= frameWidth - 97 && super.lastClickX <= frameWidth - 66 && super.lastClickY >= frameHeight - 37 && super.lastClickY < frameHeight - 0 && tabInterfaceIDs[11] != -1) {
                    if (tabID == 11) {
                        showTabComponents = !showTabComponents;
                    } else {
                        showTabComponents = true;
                    }
                    tabID = 11;
                    tabAreaAltered = true;

                }
                if (super.lastClickX >= frameWidth - 65 && super.lastClickX <= frameWidth - 34 && super.lastClickY >= frameHeight - 37 && super.lastClickY < frameHeight - 0 && tabInterfaceIDs[12] != -1) {
                    if (tabID == 12) {
                        showTabComponents = !showTabComponents;
                    } else {
                        showTabComponents = true;
                    }
                    tabID = 12;
                    tabAreaAltered = true;

                }
                if (super.lastClickX >= frameWidth - 33 && super.lastClickX <= frameWidth && super.lastClickY >= frameHeight - 37 && super.lastClickY < frameHeight - 0 && tabInterfaceIDs[13] != -1) {
                    if (tabID == 13) {
                        showTabComponents = !showTabComponents;
                    } else {
                        showTabComponents = true;
                    }
                    tabID = 13;
                    tabAreaAltered = true;

                }
                if (super.lastClickX >= frameWidth - 226 && super.lastClickX <= frameWidth - 195 && super.lastClickY >= frameHeight - 37 && super.lastClickY < frameHeight - 0 && tabInterfaceIDs[10] != -1) {
                    if (tabID == 7) {
                        showTabComponents = !showTabComponents;
                    } else {
                        showTabComponents = true;
                    }
                    tabID = 7;
                    tabAreaAltered = true;

                }
            } else if (changeTabArea && frameWidth >= 1000) {
                if (super.mouseY >= frameHeight - 37 && super.mouseY <= frameHeight) {
                    if (super.mouseX >= frameWidth - 417 && super.mouseX <= frameWidth - 386) {
                        if (tabID == 0) {
                            showTabComponents = !showTabComponents;
                        } else {
                            showTabComponents = true;
                        }
                        tabID = 0;
                        tabAreaAltered = true;
                    }
                    if (super.mouseX >= frameWidth - 385 && super.mouseX <= frameWidth - 354) {
                        if (tabID == 1) {
                            showTabComponents = !showTabComponents;
                        } else {
                            showTabComponents = true;
                        }
                        tabID = 1;
                        tabAreaAltered = true;
                    }
                    if (super.mouseX >= frameWidth - 353 && super.mouseX <= frameWidth - 322) {
                        if (tabID == 2) {
                            showTabComponents = !showTabComponents;
                        } else {
                            showTabComponents = true;
                        }
                        tabID = 2;
                        tabAreaAltered = true;
                    }
                    if (super.mouseX >= frameWidth - 321 && super.mouseX <= frameWidth - 290) {
                        if (tabID == 3) {
                            showTabComponents = !showTabComponents;
                        } else {
                            showTabComponents = true;
                        }
                        tabID = 3;
                        tabAreaAltered = true;
                    }
                    if (super.mouseX >= frameWidth - 289 && super.mouseX <= frameWidth - 258) {
                        if (tabID == 4) {
                            showTabComponents = !showTabComponents;
                        } else {
                            showTabComponents = true;
                        }
                        tabID = 4;
                        tabAreaAltered = true;
                    }
                    if (super.mouseX >= frameWidth - 257 && super.mouseX <= frameWidth - 226) {
                        if (tabID == 5) {
                            showTabComponents = !showTabComponents;
                        } else {
                            showTabComponents = true;
                        }
                        tabID = 5;
                        tabAreaAltered = true;
                    }
                    if (super.mouseX >= frameWidth - 225 && super.mouseX <= frameWidth - 194) {
                        if (tabID == 6) {
                            showTabComponents = !showTabComponents;
                        } else {
                            showTabComponents = true;
                        }
                        tabID = 6;
                        tabAreaAltered = true;
                    }
                    if (super.mouseX >= frameWidth - 193 && super.mouseX <= frameWidth - 163) {
                        if (tabID == 7) {
                            showTabComponents = !showTabComponents;
                        } else {
                            showTabComponents = true;
                        }
                        tabID = 7;
                        tabAreaAltered = true;
                    }
                    if (super.mouseX >= frameWidth - 162 && super.mouseX <= frameWidth - 131) {
                        if (tabID == 8) {
                            showTabComponents = !showTabComponents;
                        } else {
                            showTabComponents = true;
                        }
                        tabID = 8;
                        tabAreaAltered = true;
                    }
                    if (super.mouseX >= frameWidth - 130 && super.mouseX <= frameWidth - 99) {
                        if (tabID == 9) {
                            showTabComponents = !showTabComponents;
                        } else {
                            showTabComponents = true;
                        }
                        tabID = 9;
                        tabAreaAltered = true;
                    }
                    if (super.mouseX >= frameWidth - 98 && super.mouseX <= frameWidth - 67) {
                        if (tabID == 10) {
                            showTabComponents = !showTabComponents;
                        } else {
                            showTabComponents = true;
                        }
                        tabID = 10;
                        tabAreaAltered = true;
                    }
                    if (super.mouseX >= frameWidth - 66 && super.mouseX <= frameWidth - 45) {
                        if (tabID == 11) {
                            showTabComponents = !showTabComponents;
                        } else {
                            showTabComponents = true;
                        }
                        tabID = 11;
                        tabAreaAltered = true;
                    }
                    if (super.mouseX >= frameWidth - 31 && super.mouseX <= frameWidth) {
                        if (tabID == 12) {
                            showTabComponents = !showTabComponents;
                        } else {
                            showTabComponents = true;
                        }
                        tabID = 12;
                        tabAreaAltered = true;
                    }
                }
            }
        }
    }

    private void initializeGraphicBuffers() {
        super.fullGameScreen = null;
        loginScreenIP = null;
        updaterScreenIP = null;
        chatIP = new GraphicsBuffer(519, 165, getGameComponent());// chatback
        mapIP = new GraphicsBuffer(249, 168, getGameComponent());// mapback
        inventoryIP = new GraphicsBuffer(249, 335, getGameComponent());// inventory
        gameScreenIP = new GraphicsBuffer(512, 334, getGameComponent());// gamescreen
        gameScreenFrameLeft = new GraphicsBuffer(4, 334, getGameComponent());
        gameScreenFrameTop = new GraphicsBuffer(516, 4, getGameComponent());
        drawGameScreenSprite = true;
    }

    private void method81(Sprite sprite, int j, int k) {
        int l = k * k + j * j;
        if (l > 4225 && l < 0x15f90) {
            int i1 = minimapInt1 + minimapInt2 & 0x7ff;
            int j1 = Model.modelIntArray1[i1];
            int k1 = Model.modelIntArray2[i1];
            j1 = (j1 * 256) / (minimapInt3 + 256);
            k1 = (k1 * 256) / (minimapInt3 + 256);
        } else {
            markMinimap(sprite, k, j);
        }
    }

    private boolean isMouseWithin(int minX, int maxX, int minY, int maxY) {
        return super.mouseX >= minX && super.mouseX <= maxX && super.mouseY >= minY && super.mouseY <= maxY;
    }

    private boolean isMouseXWithin(int minX, int maxX) {
        return super.mouseX >= minX && super.mouseX <= maxX;
    }

    private boolean isMouseYWithin(int minY, int maxY) {
        return super.mouseY >= minY && super.mouseY <= maxY;
    }

    public void rightClickChatButtons() {
        switch (getCurrentGameFrame()) {
            case 459:
                if (isMouseYWithin(frameHeight - 33, frameHeight - 5)) {
                    if (isMouseXWithin(9, 98)) {
                        menuActionName[1] = "Hide public";
                        menuActionID[1] = 997;
                        menuActionName[2] = "Off public";
                        menuActionID[2] = 996;
                        menuActionName[3] = "Friends public";
                        menuActionID[3] = 995;
                        menuActionName[4] = "On public";
                        menuActionID[4] = 994;
                        menuActionName[5] = "View public";
                        menuActionID[5] = 993;
                        menuActionRow = 6;
                    } else if (isMouseXWithin(140, 225)) {
                        menuActionName[1] = "Off private";
                        menuActionID[1] = 992;
                        menuActionName[2] = "Friends private";
                        menuActionID[2] = 991;
                        menuActionName[3] = "On private";
                        menuActionID[3] = 990;
                        menuActionName[4] = "View private";
                        menuActionID[4] = 989;
                        menuActionRow = 5;
                    } else if (isMouseXWithin(279, 368)) {
                        menuActionName[1] = "Off trade";
                        menuActionID[1] = 987;
                        menuActionName[2] = "Friends trade";
                        menuActionID[2] = 986;
                        menuActionName[3] = "On trade";
                        menuActionID[3] = 985;
                        menuActionName[4] = "View trade";
                        menuActionID[4] = 984;
                        menuActionRow = 5;
                    }
                }
                break;

            default:
                if (mouseY >= frameHeight - 22 && mouseY <= frameHeight) {
                    if (isMouseXWithin(5, 61)) {
                        menuActionName[1] = "View All";
                        menuActionID[1] = 999;
                        menuActionRow = 2;
                    } else if (isMouseXWithin(71, 127)) {
                        menuActionName[1] = "View Game";
                        menuActionID[1] = 998;
                        menuActionRow = 2;
                    } else if (isMouseXWithin(137, 193)) {
                        menuActionName[1] = "Hide public";
                        menuActionID[1] = 997;
                        menuActionName[2] = "Off public";
                        menuActionID[2] = 996;
                        menuActionName[3] = "Friends public";
                        menuActionID[3] = 995;
                        menuActionName[4] = "On public";
                        menuActionID[4] = 994;
                        menuActionName[5] = "View public";
                        menuActionID[5] = 993;
                        menuActionRow = 6;
                    } else if (isMouseXWithin(203, 259)) {
                        menuActionName[1] = "Off private";
                        menuActionID[1] = 992;
                        menuActionName[2] = "Friends private";
                        menuActionID[2] = 991;
                        menuActionName[3] = "On private";
                        menuActionID[3] = 990;
                        menuActionName[4] = "View private";
                        menuActionID[4] = 989;
                        menuActionRow = 5;
                    } else if (isMouseXWithin(269, 325)) {
                        menuActionName[1] = "Off clan chat";
                        menuActionID[1] = 1003;
                        menuActionName[2] = "Friends clan chat";
                        menuActionID[2] = 1002;
                        menuActionName[3] = "On clan chat";
                        menuActionID[3] = 1001;
                        menuActionName[4] = "View clan chat";
                        menuActionID[4] = 1000;
                        menuActionRow = 5;
                    } else if (isMouseXWithin(335, 391)) {
                        menuActionName[1] = "Off trade";
                        menuActionID[1] = 987;
                        menuActionName[2] = "Friends trade";
                        menuActionID[2] = 986;
                        menuActionName[3] = "On trade";
                        menuActionID[3] = 985;
                        menuActionName[4] = "View trade";
                        menuActionID[4] = 984;
                        menuActionRow = 5;
                    } else if (isMouseXWithin(404, 515)) {
                        menuActionName[1] = "Open website";
                        menuActionID[1] = 606;
                        menuActionRow = 2;
                    }
                }
                break;
        }

    }

    public void processRightClick() {
        if (activeInterfaceType != 0) {
            return;
        }
        menuActionName[0] = "Cancel";
        menuActionID[0] = 1107;
        menuActionRow = 1;
        if (showChatComponents) {
            buildSplitPrivateChatMenu();
        }
        if (fullscreenInterfaceID != -1) {
            anInt886 = 0;
            anInt1315 = 0;
            buildInterfaceMenu(8, RSInterface.getInterfaceCache()[fullscreenInterfaceID], super.mouseX, 8, super.mouseY, 0);
            if (anInt886 != anInt1026) {
                anInt1026 = anInt886;
            }
            if (anInt1315 != anInt1129) {
                anInt1129 = anInt1315;
            }
            return;
        }
        anInt886 = 0;
        anInt1315 = 0;
        itemHover = -1;
        if (frameMode == ScreenMode.FIXED) {
            if (super.mouseX > 4 && super.mouseY > 4 && super.mouseX < 516 && super.mouseY < 338) {
                if (openInterfaceID != -1) {
                    buildInterfaceMenu(4, RSInterface.getInterfaceCache()[openInterfaceID], super.mouseX, 4, super.mouseY, 0);
                } else {
                    build3dScreenMenu();
                }
            }
        } else if (frameMode != ScreenMode.FIXED) {
            if (getMousePositions()) {
                int w = 512, h = 334;
                int x = (frameWidth / 2) - 256, y = (frameHeight / 2) - 167;
                int x2 = (frameWidth / 2) + 256, y2 = (frameHeight / 2) + 167;
                int count = !changeTabArea ? 4 : 3;
                if (frameMode != ScreenMode.FIXED) {
                    for (int i = 0; i < count; i++) {
                        if (x + w > (frameWidth - 225)) {
                            x = x - 30;
                            x2 = x2 - 30;
                            if (x < 0) {
                                x = 0;
                            }
                        }
                        if (y + h > (frameHeight - 182)) {
                            y = y - 30;
                            y2 = y2 - 30;
                            if (y < 0) {
                                y = 0;
                            }
                        }
                    }
                }
                if (openInterfaceID == 60000) {
                    if (super.mouseX > (frameWidth / 2) - 356 && super.mouseY > (frameHeight / 2) - 230 && super.mouseX < ((frameWidth / 2) + 356) && super.mouseY < (frameHeight / 2) + 230) {
                        buildInterfaceMenu((frameWidth / 2) - 356, RSInterface.getInterfaceCache()[openInterfaceID], super.mouseX, (frameHeight / 2) - 230, super.mouseY, 0);
                    } else {
                        build3dScreenMenu();
                    }
                } else if (openInterfaceID != -1 && openInterfaceID != 60000 && super.mouseX > x && super.mouseY > y && super.mouseX < x2 && super.mouseY < y2) {
                    buildInterfaceMenu(x, RSInterface.getInterfaceCache()[openInterfaceID], super.mouseX, y, super.mouseY, 0);
                } else {
                    build3dScreenMenu();
                }
            }
        }
        /*
         * if (frameMode == ScreenMode.FIXED) { if (super.mouseX > 4 &&
         * super.mouseY > 4 && super.mouseX < 516 && super.mouseY < 338) { if
         * (openInterfaceID != -1) { buildInterfaceMenu(4,
         * RSInterface.interfaceCache[openInterfaceID], super.mouseX, 4,
         * super.mouseY, 0); } else { build3dScreenMenu(); } } } else if
         * (frameMode != ScreenMode.FIXED) { if (getMousePositions()) { if
         * (super.mouseX > (frameWidth / 2) - 356 && super.mouseY > (frameHeight
         * / 2) - 230 && super.mouseX < ((frameWidth / 2) + 356) && super.mouseY
         * < (frameHeight / 2) + 230 && openInterfaceID != -1) {
         * buildInterfaceMenu((frameWidth / 2) - 356,
         * RSInterface.interfaceCache[openInterfaceID], super.mouseX,
         * (frameHeight / 2) - 230, super.mouseY, 0); } else {
         * build3dScreenMenu(); } } }
         */
        if (anInt886 != anInt1026) {
            anInt1026 = anInt886;
        }
        if (anInt1315 != anInt1129) {
            anInt1129 = anInt1315;
        }
        anInt886 = 0;
        anInt1315 = 0;
        if (!changeTabArea) {
            final int yOffset = frameMode == ScreenMode.FIXED ? 0 : frameHeight - 503;
            final int xOffset = frameMode == ScreenMode.FIXED ? 0 : frameWidth - 765;
            if (super.mouseX > 548 + xOffset && super.mouseX < 740 + xOffset && super.mouseY > 207 + yOffset && super.mouseY < 468 + yOffset) {
                if (invOverlayInterfaceID != -1) {
                    buildInterfaceMenu(548 + xOffset, RSInterface.getInterfaceCache()[invOverlayInterfaceID], super.mouseX, 207 + yOffset, super.mouseY, 0);
                } else if (tabInterfaceIDs[tabID] != -1) {
                    buildInterfaceMenu(548 + xOffset, RSInterface.getInterfaceCache()[tabInterfaceIDs[tabID]], super.mouseX, 207 + yOffset, super.mouseY, 0);
                }
            }
        } else if (changeTabArea) {
            final int yOffset = frameWidth >= 1000 ? 37 : 74;
            if (super.mouseX > frameWidth - 197 && super.mouseY > frameHeight - yOffset - 267 && super.mouseX < frameWidth - 7 && super.mouseY < frameHeight - yOffset - 7 && showTabComponents) {
                if (invOverlayInterfaceID != -1) {
                    buildInterfaceMenu(frameWidth - 197, RSInterface.getInterfaceCache()[invOverlayInterfaceID], super.mouseX, frameHeight - yOffset - 267, super.mouseY, 0);
                } else if (tabInterfaceIDs[tabID] != -1) {
                    buildInterfaceMenu(frameWidth - 197, RSInterface.getInterfaceCache()[tabInterfaceIDs[tabID]], super.mouseX, frameHeight - yOffset - 267, super.mouseY, 0);
                }
            }
        }
        if (anInt886 != anInt1048) {
            tabAreaAltered = true;
            anInt1048 = anInt886;
        }
        if (anInt1315 != anInt1044) {
            tabAreaAltered = true;
            anInt1044 = anInt1315;
        }
        anInt886 = 0;
        anInt1315 = 0;
        if (super.mouseX > 0 && super.mouseY > (frameMode == ScreenMode.FIXED ? 338 : frameHeight - 165) && super.mouseX < 490 && super.mouseY < (frameMode == ScreenMode.FIXED ? 463 : frameHeight - 40) && showChatComponents) {
            if (backDialogueId != -1) {
                buildInterfaceMenu(20, RSInterface.getInterfaceCache()[backDialogueId], super.mouseX, (frameMode == ScreenMode.FIXED ? 358 : frameHeight - 145), super.mouseY, 0);
            } else if (super.mouseY < (frameMode == ScreenMode.FIXED ? 463 : frameHeight - 40) && super.mouseX < 490) {
                buildChatAreaMenu(super.mouseY - (frameMode == ScreenMode.FIXED ? 338 : frameHeight - 165));
            }
        }
        if (backDialogueId != -1 && anInt886 != anInt1039) {
            redrawDialogueBox = true;
            anInt1039 = anInt886;
        }
        if (backDialogueId != -1 && anInt1315 != anInt1500) {
            redrawDialogueBox = true;
            anInt1500 = anInt1315;
        }
        // TODO
        if (getCurrentGameFrame() == 474 && isMouseWithin(5, 515, 481, frameHeight) || getCurrentGameFrame() == 459 && isMouseWithin(5, 506, 467, frameHeight - 5)) {
            rightClickChatButtons();
        }
        processMinimapActions();
        boolean flag = false;
        while (!flag) {
            flag = true;
            for (int j = 0; j < menuActionRow - 1; j++) {
                if (menuActionID[j] < 1000 && menuActionID[j + 1] > 1000) {
                    String s = menuActionName[j];
                    menuActionName[j] = menuActionName[j + 1];
                    menuActionName[j + 1] = s;
                    int k = menuActionID[j];
                    menuActionID[j] = menuActionID[j + 1];
                    menuActionID[j + 1] = k;
                    k = menuActionCmd2[j];
                    menuActionCmd2[j] = menuActionCmd2[j + 1];
                    menuActionCmd2[j + 1] = k;
                    k = menuActionCmd3[j];
                    menuActionCmd3[j] = menuActionCmd3[j + 1];
                    menuActionCmd3[j + 1] = k;
                    k = menuActionCmd1[j];
                    menuActionCmd1[j] = menuActionCmd1[j + 1];
                    menuActionCmd1[j + 1] = k;
                    flag = false;
                }
            }
        }
    }

    public void attemptLogin(String username, String password, boolean reconnecting) {
        Configuration.errorname = username;
        if (username.length() == 0 && password.length() == 0) {
            loginMessage1 = "Please enter your account credentials before you log in!";
            return;
        }
        if (username.length() < 3) {
            loginMessage1 = "Your username must have 3 or more characters.";
            return;
        }
        if (password.length() <= 3) {
            loginMessage1 = "Your password must have 4 or more characters.";
            return;
        }
        login(username, password, reconnecting);
    }

    public void login(String username, String password, boolean reconnecting) {
        try {
            socketStream = new BufferedConnection(this, openSocket(43594 + portOff));
            outgoing.currentOffset = 0;
            outgoing.writeByte(14);
            outgoing.writeByte(0);
            socketStream.queueBytes(2, outgoing.buffer);

            for (int i = 0; i < 8; i++) {
                socketStream.read();
            }

            int response = socketStream.read();
            int tempResponse = response;

            if (response == 0) {
                socketStream.flushInputStream(incoming.buffer, 8);
                incoming.currentOffset = 0;
                aLong1215 = incoming.readLong();
                int ai[] = new int[4];
                ai[0] = (int) (Math.random() * 99999999D);
                ai[1] = (int) (Math.random() * 99999999D);
                ai[2] = (int) (aLong1215 >> 32);
                ai[3] = (int) aLong1215;
                outgoing.currentOffset = 0;
                outgoing.writeByte(10);
                outgoing.writeDWord(ai[0]);
                outgoing.writeDWord(ai[1]);
                outgoing.writeDWord(ai[2]);
                outgoing.writeDWord(ai[3]);
                outgoing.writeDWord(ai[3]);
                outgoing.writeDWord(0);
                outgoing.writeString("");
                outgoing.writeString("");
                outgoing.writeString(username);
                outgoing.writeString(password);
                outgoing.encodeRSA(NetworkConstants.RSA_EXPONENT, NetworkConstants.RSA_MODULUS);
                aStream_847.currentOffset = 0;
                if (reconnecting) {
                    aStream_847.writeByte(18);
                } else {
                    aStream_847.writeByte(16);
                }

                aStream_847.writeByte(outgoing.currentOffset + 40);

                aStream_847.writeByte(255);

                aStream_847.writeShort(Configuration.GAME_VERSION);

                aStream_847.writeByte(lowMem ? 1 : 0);

                for (int i = 0; i < 9; i++) {
                    aStream_847.writeDWord(expectedCRCs[i]);
                }

                aStream_847.writeBytes(outgoing.buffer, outgoing.currentOffset, 0);
                outgoing.encryption = new ISAACRandomGen(ai);

                for (int i = 0; i < 4; i++) {
                    ai[i] += 50;
                }

                encryption = new ISAACRandomGen(ai);
                socketStream.queueBytes(aStream_847.currentOffset, aStream_847.buffer);
                response = socketStream.read();
            }
            if (response == 1) {
                try {
                    Thread.sleep(2000L);
                } catch (Exception _ex) {
                }
                attemptLogin(username, password, reconnecting);
                return;
            }
            if (response == 2) {
                myPrivilege = socketStream.read();
                flagged = socketStream.read() == 1;
                aLong1220 = 0L;
                anInt1022 = 0;
                mouseDetection.coordsIndex = 0;
                super.awtFocus = true;
                aBoolean954 = true;
                loggedIn = true;
                outgoing.currentOffset = 0;
                incoming.currentOffset = 0;
                opcode = -1;
                lastOpcode1 = -1;
                lastOpcode2 = -1;
                lastOpcode3 = -1;
                pktSize = 0;
                anInt1009 = 0;
                anInt1104 = 0;
                anInt1011 = 0;
                anInt855 = 0;
                menuActionRow = 0;
                menuOpen = false;
                prayClicked = false;
                super.idleTime = 0;
                for (int j1 = 0; j1 < 500; j1++) {
                    chatMessages[j1] = null;
                }
                itemSelected = 0;
                spellSelected = 0;
                loadingStage = 0;
                anInt1062 = 0;
                setNorth();
                minimapState = 0;
                anInt985 = -1;
                destX = 0;
                destY = 0;
                playerCount = 0;
                npcCount = 0;
                for (int i2 = 0; i2 < maxPlayers; i2++) {
                    playerArray[i2] = null;
                    playerSynchronizationBuffers[i2] = null;
                }
                for (int k2 = 0; k2 < 16384; k2++)
                    npcs[k2] = null;
                localPlayer = playerArray[myPlayerIndex] = new Player();
                aClass19_1013.removeAll();
                aClass19_1056.removeAll();
                for (int l2 = 0; l2 < 4; l2++) {
                    for (int i3 = 0; i3 < 104; i3++) {
                        for (int k3 = 0; k3 < 104; k3++)
                            groundArray[l2][i3][k3] = null;
                    }
                }
                aClass19_1179 = new Deque();
                fullscreenInterfaceID = -1;
                anInt900 = 0;
                friendsCount = 0;
                dialogueId = -1;
                backDialogueId = -1;
                openInterfaceID = -1;
                invOverlayInterfaceID = -1;
                anInt1018 = -1;
                aBoolean1149 = false;
                tabID = 3;
                inputDialogState = 0;
                menuOpen = false;
                messagePromptRaised = false;
                clickToContinueString = null;
                anInt1055 = 0;
                anInt1054 = -1;
                characterGender = true;
                method45();
                for (int j3 = 0; j3 < 5; j3++)
                    characterColor[j3] = 0;
                for (int l3 = 0; l3 < 5; l3++) {
                    atPlayerActions[l3] = null;
                    playerOptions[l3] = false;
                }
                sendFrame36(429, 1);
		    regenHealthStart = System.currentTimeMillis();
    regenSpecStart = System.currentTimeMillis();
                initializeGraphicBuffers();
                setBounds();
                return;
            }
            if (response == 3) {
                loginMessage1 = "";
                loginMessage2 = "Invalid username or password.";
                return;
            }
            if (response == 4) {
                loginMessage1 = "Your account has been disabled.";
                loginMessage2 = "Please check your message-center for details.";
                return;
            }
            if (response == 5) {
                loginMessage1 = "Your account is already logged in.";
                loginMessage2 = "Try again in 60 secs...";
                return;
            }
            if (response == 6) {
                loginMessage1 = Configuration.NAME + " has been updated!";
                loginMessage2 = "Please reload client.";
                return;
            }
            if (response == 7) {
                loginMessage1 = "This world is full.";
                loginMessage2 = "Please use a different world.";
                return;
            }
            if (response == 8) {
                loginMessage1 = "Unable to connect.";
                loginMessage2 = "Login server offline.";
                return;
            }
            if (response == 9) {
                loginMessage1 = "Login limit exceeded.";
                loginMessage2 = "Too many connections from your address.";
                return;
            }
            if (response == 10) {
                loginMessage1 = "Unable to connect.";
                loginMessage2 = "Bad session id.";
                return;
            }

            if (response == 11) {
                loginMessage1 = "Login server rejected session.";
                loginMessage2 = "Please try again.";
                return;
            }

            if (response == 12) {
                loginMessage1 = "You need a members account to login to this world.";
                loginMessage2 = "Please subscribe, or use a different world.";
                return;
            }
            if (response == 13) {
                loginMessage1 = "Could not complete login.";
                loginMessage2 = "Please try using a different world.";
                return;
            }
            if (response == 14) {
                loginMessage1 = "The server is being updated.";
                loginMessage2 = "Please wait 1 minute and try again.";
                return;
            }
            if (response == 15) {
                loggedIn = true;
                outgoing.currentOffset = 0;
                incoming.currentOffset = 0;
                opcode = -1;
                lastOpcode1 = -1;
                lastOpcode2 = -1;
                lastOpcode3 = -1;
                pktSize = 0;
                anInt1009 = 0;
                anInt1104 = 0;
                menuActionRow = 0;
                menuOpen = false;
                aLong824 = System.currentTimeMillis();
                return;
            }
            if (response == 16) {
                loginMessage1 = "Login attempts exceeded.";
                loginMessage2 = "Please wait 1 minute and try again.";
                return;
            }
            if (response == 17) {
                loginMessage1 = "You are standing in a members-only area.";
                loginMessage2 = "To play on this world move to a free area first";
                return;
            }
            if (response == 20) {
                loginMessage1 = "Invalid loginserver requested";
                loginMessage2 = "Please try using a different world.";
                return;
            }
            if (response == 21) {
                for (int k1 = socketStream.read(); k1 >= 0; k1--) {
                    loginMessage1 = "You have only just left another world";
                    loginMessage2 = "Your profile will be transferred in: " + k1 + " seconds";
                    loginRenderer.display();
                    try {
                        Thread.sleep(1000L);
                    } catch (Exception _ex) {
                    }
                }
                attemptLogin(username, password, reconnecting);
                return;
            }
            if (response == 22) {
                loginMessage1 = "This username is not permitted!";
                loginMessage2 = "Please try a different username.";
                return;
            }
            if (response == 23) {
                loginMessage1 = "Username must consist of 3 or more characters.";
                loginMessage2 = "Please try a different username.";
                return;
            }
            if (response == 24) {
                loginMessage1 = "You do not have sufficient permission to access this.";
                loginMessage2 = "Please try a different world.";
                return;
            }
            if (response == 25) {
                loginMessage1 = "Your account has un-authorized privileges and is";
                loginMessage2 = "now locked. Please open a ticket on forums to fix.";
                return;
            }

            if (response == 26) {
                loginMessage1 = "You have not registered on the forums yet.";
                loginMessage2 = "Please register on the forums before trying to login.";
                return;
            }

            if (response == 27) {
                loginMessage1 = "The email you entered is invalid.";
                loginMessage2 = "Please try a different email.";
                return;
            }

            if (response == -1) {
                if (tempResponse == 0) {
                    if (loginFailures < 2) {
                        try {
                            Thread.sleep(2000L);
                        } catch (Exception _ex) {
                        }
                        loginFailures++;
                        attemptLogin(username, password, reconnecting);
                        return;
                    } else {
                        loginMessage1 = "No response from loginserver";
                        loginMessage2 = "Please wait 1 minute and try again.";
                        return;
                    }
                } else {
                    loginMessage1 = "No response from server";
                    loginMessage2 = "Please try using a different world.";
                    return;
                }
            } else {
                System.out.println("response:" + response);
                loginMessage1 = "Unexpected server response";
                loginMessage2 = "Please try using a different world.";
                return;
            }
        } catch (IOException _ex) {
            loginMessage1 = "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        loginMessage2 = "Error connecting to server.";
    }

    private void walk(int targetX, int targetY, int movementType) {
        if (movementType != 0 && movementType != 1 && movementType != 2) {
            return;
        }

        // renders the minimap flag
        this.destX = targetX;
        this.destY = targetY;

        if (movementType == 0) { // regular walk
            outgoing.writeOpcode(164);
        } else if (movementType == 1) { // map walk
            outgoing.writeOpcode(248);
        } else if (movementType == 2) { // walk on command
            outgoing.writeOpcode(98);
        }

        outgoing.writeLEShort(targetX + baseX);
        outgoing.writeLEShortA(targetY + baseY);
        outgoing.writeNegatedByte(super.keyArray[5] != 1 ? 0 : 1);
    }

    private void method86(Buffer stream) {
        for (int slot = 0; slot < mobsAwaitingUpdateCount; slot++) {
            int index = mobsAwaitingUpdate[slot];
            Npc npc = npcs[index];
            int mask = stream.readUByte();
            if ((mask & 0x10) != 0) {
                int animation = stream.readLEUShort();
                if (animation == 65535) {
                    animation = -1;
                }
                int delay = stream.readUByte();
                if (animation == npc.emoteAnimation && animation != -1) {
                    int replayMode = Animation.animations[animation].replayMode;
                    if (replayMode == 1) {
                        npc.displayedEmoteFrames = 0;
                        npc.emoteTimeRemaining = 0;
                        npc.animationDelay = delay;
                        npc.currentAnimationLoops = 0;
                    }
                    if (replayMode == 2) {
                        npc.currentAnimationLoops = 0;
                    }
                } else if (animation == -1 || npc.emoteAnimation == -1 || Animation.animations[animation].forcedPriority >= Animation.animations[npc.emoteAnimation].forcedPriority) {
                    npc.emoteAnimation = animation;
                    npc.displayedEmoteFrames = 0;
                    npc.emoteTimeRemaining = 0;
                    npc.animationDelay = delay;
                    npc.currentAnimationLoops = 0;
                    npc.anInt1542 = npc.remainingPath;
                }
            }
            if ((mask & 0x80) != 0) {
                npc.graphic = stream.readUShort();
                int k1 = stream.readInt();
                npc.graphicHeight = k1 >> 16;
                npc.graphicDelay = tick + (k1 & 0xffff);
                npc.currentAnimation = 0;
                npc.anInt1522 = 0;
                if (npc.graphicDelay > tick) {
                    npc.currentAnimation = -1;
                }
                if (npc.graphic == 65535) {
                    npc.graphic = -1;
                }
            }
            if ((mask & 0x20) != 0) {
                npc.interactingEntity = stream.readUShort();
                if (npc.interactingEntity == 65535) {
                    npc.interactingEntity = -1;
                }
            }
            if ((mask & 0x1) != 0) {
                npc.textSpoken = stream.readString();
                npc.textCycle = 100;
                npc.textColor = 0;
            }
            if ((mask & 0x40) != 0) {
                int damage = stream.readUByte();
                int type = stream.readUByteA();
                int icon = stream.readUByte();
                int current = stream.readUByte();
                int max = stream.readNegUByte();

                npc.damage(type, damage, tick, icon);
                npc.cycleStatus = tick + 300;
                npc.currentHealth = current;
                npc.maximumHealth = max;
            }
            if ((mask & 0x8) != 0) {
                int damage = stream.readUByte();
                int type = stream.readUByteS();
                int icon = stream.readUByte();
                int current = stream.readUByte();
                int max = stream.readNegUByte();

                npc.damage(type, damage, tick, icon);
                npc.cycleStatus = tick + 300;
                npc.currentHealth = current;
                npc.maximumHealth = max;
            }
            if ((mask & 0x2) != 0) {
                npc.definition = NpcDefinition.lookup(stream.readLEUShortA());
                npc.size = npc.definition.size;
                npc.rotation = npc.definition.rotation;
                npc.walkingAnimation = npc.definition.walkingAnimation;
                npc.halfTurnAnimation = npc.definition.halfTurnAnimation;
                npc.quarterClockwiseTurnAnimation = npc.definition.quarterClockwiseTurnAnimation;
                npc.quarterAnticlockwiseTurnAnimation = npc.definition.quarterAnticlockwiseTurnAnimation;
                npc.idleAnimation = npc.definition.standingAnimation;
            }
            if ((mask & 0x4) != 0) {
                npc.faceX = stream.readLEUShort();
                npc.faceY = stream.readLEUShort();
            }
        }
    }

    private void buildAtNPCMenu(NpcDefinition entityDef, int i, int j, int k) {
        if (menuActionRow >= 400)
            return;
        if (entityDef.morphisms != null)
            entityDef = entityDef.morph();
        if (entityDef == null)
            return;
        if (!entityDef.aBoolean84)
            return;
        String s = entityDef.name;
        if (entityDef.combatLevel != 0)
            s = s + combatDiffColor(localPlayer.combatLevel, entityDef.combatLevel) + " (level-" + entityDef.combatLevel + ")";
        if (itemSelected == 1) {
            menuActionName[menuActionRow] = "Use <col=FF9040>" + selectedItemName + " <col=FFFFFF>with <col=FFFF00>" + s;
            menuActionID[menuActionRow] = 582;
            menuActionCmd1[menuActionRow] = i;
            menuActionCmd2[menuActionRow] = k;
            menuActionCmd3[menuActionRow] = j;
            menuActionRow++;
            return;
        }
        if (spellSelected == 1) {
            if ((spellUsableOn & 2) == 2) {
                menuActionName[menuActionRow] = spellTooltip + " <col=FFFF00>" + s;
                menuActionID[menuActionRow] = 413;
                menuActionCmd1[menuActionRow] = i;
                menuActionCmd2[menuActionRow] = k;
                menuActionCmd3[menuActionRow] = j;
                menuActionRow++;
            }
        } else {
            if (entityDef.actions != null) {
                for (int l = 4; l >= 0; l--)
                    if (entityDef.actions[l] != null && !entityDef.actions[l].equalsIgnoreCase("attack")) {
                        menuActionName[menuActionRow] = entityDef.actions[l] + " <col=FFFF00>" + s;
                        if (l == 0)
                            menuActionID[menuActionRow] = 20;
                        if (l == 1)
                            menuActionID[menuActionRow] = 412;
                        if (l == 2)
                            menuActionID[menuActionRow] = 225;
                        if (l == 3)
                            menuActionID[menuActionRow] = 965;
                        if (l == 4)
                            menuActionID[menuActionRow] = 478;
                        menuActionCmd1[menuActionRow] = i;
                        menuActionCmd2[menuActionRow] = k;
                        menuActionCmd3[menuActionRow] = j;
                        menuActionRow++;
                    }

            }
            if (entityDef.actions != null) {
                for (int i1 = 4; i1 >= 0; i1--)
                    if (entityDef.actions[i1] != null && entityDef.actions[i1].equalsIgnoreCase("attack")) {
                        char c = '\0';
                        if (!Settings.ATTACK_PRIORITY && entityDef.combatLevel > localPlayer.combatLevel)
                            c = '\u07D0';
                        menuActionName[menuActionRow] = entityDef.actions[i1] + " <col=ffff00>" + s;
                        if (i1 == 0)
                            menuActionID[menuActionRow] = 20 + c;
                        if (i1 == 1)
                            menuActionID[menuActionRow] = 412 + c;
                        if (i1 == 2)
                            menuActionID[menuActionRow] = 225 + c;
                        if (i1 == 3)
                            menuActionID[menuActionRow] = 965 + c;
                        if (i1 == 4)
                            menuActionID[menuActionRow] = 478 + c;
                        menuActionCmd1[menuActionRow] = i;
                        menuActionCmd2[menuActionRow] = k;
                        menuActionCmd3[menuActionRow] = j;
                        menuActionRow++;
                    }

            }
            menuActionName[menuActionRow] = "Examine <col=FFFF00>" + s + (Configuration.DEBUG_MODE ? " <col=65280>(<col=ffffff>" + entityDef.interfaceType + "<col=65280>)" : "");
            menuActionID[menuActionRow] = 1025;
            menuActionCmd1[menuActionRow] = i;
            menuActionCmd2[menuActionRow] = k;
            menuActionCmd3[menuActionRow] = j;
            menuActionRow++;
        }
    }

    private void buildAtPlayerMenu(int i, int j, Player player, int k) {
        if (player == localPlayer) return;
        if (menuActionRow >= 400) return;

        String s;
        String title = player.title.length() > 0 ? "<col=" + Integer.toHexString(player.titleColor) + ">" + player.title + "</col> " : "";

        String tagColor = player.clanTagColor.length() <= 0 ? "" : "<col=" + player.clanTagColor + ">";
        boolean display = tagColor.length() > 0 && Settings.DISPLAY_CLAN_TAG;
        String tag = display ? tagColor + "[" + player.clanTag + "]</col> " : "";
        String newTag = tag.contains("[]") ? "" : tag;

        if (player.skill == 0) {
            s = newTag + title + "<col=ffffff>" + player.name + "</col>" + combatDiffColor(localPlayer.combatLevel, player.combatLevel) + " (level-" + (int) player.combatLevel + ")";
        } else {
            s = newTag + title + player.name + " (skill-" + player.skill + ")";
        }

        if (itemSelected == 1) {
            menuActionName[menuActionRow] = "Use " + selectedItemName + " with <col=FFFFFF>" + s;
            menuActionID[menuActionRow] = 491;
            menuActionCmd1[menuActionRow] = j;
            menuActionCmd2[menuActionRow] = i;
            menuActionCmd3[menuActionRow] = k;
            menuActionRow++;
        } else if (spellSelected == 1) {
            if ((spellUsableOn & 8) == 8) {
                menuActionName[menuActionRow] = spellTooltip + " <col=FFFFFF>" + s;
                menuActionID[menuActionRow] = 365;
                menuActionCmd1[menuActionRow] = j;
                menuActionCmd2[menuActionRow] = i;
                menuActionCmd3[menuActionRow] = k;
                menuActionRow++;
            }
        } else {
            for (int option = 4; option >= 0; option--) {
                if (atPlayerActions[option] != null) {
                    menuActionName[menuActionRow] = atPlayerActions[option] + " <col=FFFFFF>" + s;
                    int identifier = '\0';

                    if (atPlayerActions[option].equalsIgnoreCase("attack")) {

                        if (!Settings.ATTACK_PRIORITY && player.combatLevel > localPlayer.combatLevel) {
                            identifier = '\u07D0';
                        }

                        if (localPlayer.team != 0 && localPlayer.team == player.team) {
                            identifier = '\u07D0';
                        }

                    } else if (playerOptions[option]) {
                        identifier = '\u07D0';
                    }

                    // player option 1 : duel request : opcode 128
                    if (option == 0) {
                        menuActionID[menuActionRow] = 561 + identifier;
                    }

                    // player option 2 : report abuse : opcode 153
                    if (option == 1) {
                        menuActionID[menuActionRow] = 779 + identifier;
                    }

                    // player option 3 : attack : opcode 73
                    if (option == 2) {
                        menuActionID[menuActionRow] = 27 + identifier;
                    }

                    // player option 4 : trade : opcode 139
                    if (option == 3) {
                        menuActionID[menuActionRow] = 577 + identifier;
                    }

                    // player option 5 : follow : opcode 39
                    if (option == 4) {
                        menuActionID[menuActionRow] = 729 + identifier;
                    }

                    menuActionCmd1[menuActionRow] = j;
                    menuActionCmd2[menuActionRow] = i;
                    menuActionCmd3[menuActionRow] = k;
                    menuActionRow++;
                }
            }
        }
        for (int index = 0; index < menuActionRow; index++) {
            if (menuActionID[index] == 519) {
                menuActionName[index] = "Walk here <col=FFFFFF>" + s;
                return;
            }
        }
    }

    private void method89(SpawnedObject class30_sub1) {
        int i = 0;
        int j = -1;
        int k = 0;
        int l = 0;
        if (class30_sub1.anInt1296 == 0)
            i = worldController.method300(class30_sub1.anInt1295, class30_sub1.anInt1297, class30_sub1.anInt1298);
        if (class30_sub1.anInt1296 == 1)
            i = worldController.method301(class30_sub1.anInt1295, class30_sub1.anInt1297, class30_sub1.anInt1298);
        if (class30_sub1.anInt1296 == 2)
            i = worldController.method302(class30_sub1.anInt1295, class30_sub1.anInt1297, class30_sub1.anInt1298);
        if (class30_sub1.anInt1296 == 3)
            i = worldController.method303(class30_sub1.anInt1295, class30_sub1.anInt1297, class30_sub1.anInt1298);
        if (i != 0) {
            int i1 = worldController.method304(class30_sub1.anInt1295, class30_sub1.anInt1297, class30_sub1.anInt1298, i);
            j = i >> 14 & 0x7fff;
            k = i1 & 0x1f;
            l = i1 >> 6;
        }
        class30_sub1.anInt1299 = j;
        class30_sub1.anInt1301 = k;
        class30_sub1.anInt1300 = l;
    }

    public void startUp() {
        Settings.load();
        Saving.load();
        Ping.runPing();

        try {
            spriteCache.init(Paths.get(Utility.findcachedir(), Configuration.SPRITE_FILE_NAME + ".dat").toFile(), Paths.get(Utility.findcachedir(), Configuration.SPRITE_FILE_NAME + ".idx").toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        ItemStats.readDefinitions();
        initializeLoginScreen();
        drawSmoothLoading(0, "Initializing client resources");
        keybindManager.load();
        for (int index = 0; index < 25; index++) {
            bubbles.add(new Bubble());
        }
        loginRenderer = new LoginRenderer(this);
        if (cache_dat != null) {
            for (int i = 0; i < 5; i++) {
                decompressors[i] = new Decompressor(cache_dat, cache_idx[i], i + 1);
            }
        }
        try {
            titleStreamLoader = streamLoaderForName(1, "title screen", "title", expectedCRCs[1], 25);
            smallFont = new TextDrawingArea(false, "p11_full", titleStreamLoader);
            regularText = new TextDrawingArea(false, "p12_full", titleStreamLoader);
            boldText = new TextDrawingArea(false, "b12_full", titleStreamLoader);
            newSmallFont = new RSFont(false, "p11_full", titleStreamLoader);
            newRegularFont = new RSFont(false, "p12_full", titleStreamLoader);
            newBoldFont = new RSFont(false, "b12_full", titleStreamLoader);
            newFancyFont = new RSFont(true, "q8_full", titleStreamLoader);
            smallHit = new TextDrawingArea(false, "hit_full", titleStreamLoader);
            TextDrawingArea aTextDrawingArea_1273 = new TextDrawingArea(true, "q8_full", titleStreamLoader);
            StreamLoader configArchive = streamLoaderForName(2, "config", "config", expectedCRCs[2], 30);
            StreamLoader streamLoader_1 = streamLoaderForName(3, "interface", "interface", expectedCRCs[3], 35);
            StreamLoader streamLoader_2 = streamLoaderForName(4, "2d graphics", "media", expectedCRCs[4], 40);
            StreamLoader streamLoader_4 = streamLoaderForName(7, "chat system", "wordenc", expectedCRCs[7], 50);
            this.mediaStreamLoader = streamLoader_2;

            StreamLoader streamLoader_3 = streamLoaderForName(6, "textures", "textures", expectedCRCs[6], 45);
            streamLoaderForName(8, "sound effects", "sounds", expectedCRCs[8], 55);
            byteGroundArray = new byte[4][104][104];
            intGroundArray = new int[4][105][105];
            worldController = new SceneGraph(intGroundArray);
            for (int j = 0; j < 4; j++) {
                collisionMaps[j] = new CollisionMap();
            }
            minimapImage = new Sprite(512, 512);
            StreamLoader streamLoader_6 = streamLoaderForName(5, "update list", "versionlist", expectedCRCs[5], 60);
            drawSmoothLoading(20, "Loading resources");
            onDemandFetcher = new ResourceProvider();
            onDemandFetcher.start(streamLoader_6, this);
            Frame.animationlist = new Frame[3000][0];
            Model.method459(onDemandFetcher.getModelCount(), onDemandFetcher);
            drawSmoothLoading(40, "Loading resources");
            multiOverlay = new Sprite(streamLoader_2, "overlay_multiway", 0);
            mapBack = new Background(streamLoader_2, "mapback", 0);
            for (int j3 = 0; j3 < 16; j3++) {
                sideIcons[j3] = spriteCache.get(858 + j3);
            }
            try {
                for (int k3 = 0; k3 < 100; k3++)
                    mapScenes[k3] = new Background(streamLoader_2, "mapscene", k3);
            } catch (Exception _ex) {
            }
            try {
                for (int l3 = 0; l3 < 100; l3++)
                    mapFunctions[l3] = new Sprite(streamLoader_2, "mapfunction", l3);
            } catch (Exception _ex) {
            }
            try {
                for (int i4 = 0; i4 < 20; i4++)
                    hitMarks[i4] = new Sprite(streamLoader_2, "hitmarks", i4);
            } catch (Exception _ex) {
            }
            try {
                for (int h1 = 0; h1 < 6; h1++)
                    headIconsHint[h1] = new Sprite(streamLoader_2, "headicons_hint", h1);
            } catch (Exception _ex) {
            }
            try {
                for (int j4 = 0; j4 < 8; j4++) {
                    headIcons[j4] = new Sprite(streamLoader_2, "headicons_prayer", j4);
                }
            } catch (Exception _ex) {
            }

            try {
                int[] skullIconIds = new int[] {236, 842};
                for (int count = 0; count < skullIconIds.length; count++) {
                    skullIcons[count] = spriteCache.get(skullIconIds[count]);
                }
            } catch (Exception ex) {

            }

            try {
                int[] bountyIconIds = new int[]{238, 239, 240, 241, 242};

                bountyIcons = new Sprite[bountyIconIds.length];
                for (int count = 0; count < bountyIconIds.length; count++) {
                    bountyIcons[count] = spriteCache.get(bountyIconIds[count]);
                }
            } catch (Exception ex) {

            }

            for (int index = 0; index < currencyIcons.length; index++) {
                currencyImage[index] = spriteCache.get(currencyIcons[index]);
            }
            for (int index = 0; index < 5; index++) {
                bountyIcons[index] = spriteCache.get(238 + index);
            }
            for (int i4 = 346; i4 <= 357; i4++) {
                hitMark[i4 - 346] = spriteCache.get(i4);
            }
            for (int i4 = 358; i4 <= 366; i4++) {
                hitIcon[i4 - 358] = spriteCache.get(i4);
            }
            for (int index = 0; index < 17; index++) {
                modIcons[index] = spriteCache.get(index + 781);
            }
            specbutton = new Sprite[6];
            for (int i = 0; i < 6; ++i) {
                specbutton[i] = new Sprite("Specbutton/"+i);
            }
            for (int index = 314; index < 323; index++) {
                clanIcons[index - 314] = spriteCache.get(index);
            }
            for (int index = 798; index < 823; index++) {
                iconIcons[index - 798] = spriteCache.get(index);
            }
            SkillOrbs.init();
            RSFont.unpackImages(modIcons, clanIcons, iconIcons);
            mapFlag = new Sprite(streamLoader_2, "mapmarker", 0);
            mapMarker = new Sprite(streamLoader_2, "mapmarker", 1);
            for (int k4 = 0; k4 < 8; k4++)
                crosses[k4] = new Sprite(streamLoader_2, "cross", k4);
            mapDotItem = new Sprite(streamLoader_2, "mapdots", 0);
            mapDotNPC = new Sprite(streamLoader_2, "mapdots", 1);
            mapDotPlayer = new Sprite(streamLoader_2, "mapdots", 2);
            mapDotFriend = new Sprite(streamLoader_2, "mapdots", 3);
            mapDotTeam = new Sprite(streamLoader_2, "mapdots", 4);
            mapDotClan = new Sprite(streamLoader_2, "mapdots", 5);
            scrollBar1 = new Sprite(streamLoader_2, "scrollbar", 0);
            scrollBar2 = new Sprite(streamLoader_2, "scrollbar", 1);
//            repackCacheIndex(1);
//            repackCacheIndex(2);
//            repackCacheIndex(4);
            prepareGameFrame();
            Sprite sprite = new Sprite(streamLoader_2, "screenframe", 0);
            leftFrame = new GraphicsBuffer(sprite.width, sprite.height, getGameComponent());
            sprite.method346(0, 0);
            sprite = new Sprite(streamLoader_2, "screenframe", 1);
            topFrame = new GraphicsBuffer(sprite.width, sprite.height, getGameComponent());
            sprite.method346(0, 0);
            createSnow();
            drawSmoothLoading(75, "Loading resources");
            Rasterizer.method368(streamLoader_3);
            Rasterizer.method372(0.80000000000000004D);
            Rasterizer.method367(20);
            Animation.unpackConfig(configArchive);
            ObjectDefinition.unpackConfig(configArchive);
            FloorDefinition.unpackConfig(configArchive);
            ItemDefinition.init(configArchive);
            NpcDefinition.unpackConfig(configArchive);
            IdentityKit.init(configArchive);
            Graphic.unpackConfig(configArchive);
            Varp.unpackConfig(configArchive);
            Varbit.unpackConfig(configArchive);
            ItemDefinition.isMembers = isMembers;
            drawSmoothLoading(85, "Loading resources");
            TextDrawingArea aclass30_sub2_sub1_sub4s[] = {smallFont, regularText, boldText, aTextDrawingArea_1273};
            RSInterface.unpack(streamLoader_1, aclass30_sub2_sub1_sub4s, streamLoader_2);

            drawSmoothLoading(100, "Cleaning resources");
            for (int j6 = 0; j6 < 33; j6++) {
                int k6 = 999;
                int i7 = 0;
                for (int k7 = 0; k7 < 34; k7++) {
                    if (mapBack.aByteArray1450[k7 + j6 * mapBack.anInt1452] == 0) {
                        if (k6 == 999)
                            k6 = k7;
                        continue;
                    }
                    if (k6 == 999)
                        continue;
                    i7 = k7;
                    break;
                }
                anIntArray968[j6] = k6;
                anIntArray1057[j6] = i7 - k6;
            }
            for (int l6 = 1; l6 < 153; l6++) {
                int j7 = 999;
                int l7 = 0;
                for (int j8 = 24; j8 < 177; j8++) {
                    if (mapBack.aByteArray1450[j8 + l6 * mapBack.anInt1452] == 0 && (j8 > 34 || l6 > 34)) {
                        if (j7 == 999) {
                            j7 = j8;
                        }
                        continue;
                    }
                    if (j7 == 999) {
                        continue;
                    }
                    l7 = j8;
                    break;
                }
                anIntArray1052[l6 - 1] = j7 - 24;
                anIntArray1229[l6 - 1] = l7 - j7;
            }
            setBounds();
//            Censor.loadConfig(streamLoader_4);
            mouseDetection = new MouseDetection(this);
            startRunnable(mouseDetection, 10);
            SceneObject.clientInstance = this;
            ObjectDefinition.clientInstance = this;
            NpcDefinition.clientInstance = this;
            AccountManager.loadAccount();
            loginLoaded = true;

            spriteCache.clear(); // this clears all the sprites that the interfaces loaded, this is to only load sprites if the interface is used
            System.out.println(Configuration.NAME + " has successfully loaded! [Took " + loadingTime.elapsed(TimeUnit.SECONDS) + " seconds]");
            return;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        loadingError = true;
    }

    private void updatePlayerList(Buffer stream, int packetSize) {
        while (stream.bitPosition + 10 < packetSize * 8) {
            int index = stream.readBits(11);

            if (index == 2047) {
                break;
            }

            if (playerArray[index] == null) {
                playerArray[index] = new Player();
                if (playerSynchronizationBuffers[index] != null) {
                    playerArray[index].updateAppearance(playerSynchronizationBuffers[index]);
                }
            }
            playerIndices[playerCount++] = index;
            Player player = playerArray[index];
            player.time = tick;
            int update = stream.readBits(1);

            if (update == 1) {
                mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = index;
            }

            final int discardWalkingQueue = stream.readBits(1);

            int y = stream.readBits(5);

            if (y > 15) {
                y -= 32;
            }

            int x = stream.readBits(5);

            if (x > 15) {
                x -= 32;
            }
            player.setPos(localPlayer.smallX[0] + x, localPlayer.smallY[0] + y, discardWalkingQueue == 1);
        }
        stream.finishBitAccess();
    }


    public String indexLocation(int cacheIndex, int index) {
//        return Utility.findcachedir() + "/index" + cacheIndex + "/" + (index != -1 ? index + ".gz" : "");
        return "C:/Users/Daniel/Desktop/index" + cacheIndex + "/" + (index != -1 ? index + ".gz" : "");

        // return "C:/Users/evan__000/Desktop/RSPS/Data/OSRS/118 data/index" +
        // cacheIndex + "/" + (index != -1 ? index + ".gz" : "");
    }

    public void repackCacheIndex(int cacheIndex) {
        System.out.println("Started repacking index " + cacheIndex + ".");
        int indexLength = new File(indexLocation(cacheIndex, -1)).listFiles().length;
        File[] file = new File(indexLocation(cacheIndex, -1)).listFiles();
        try {
            for (int index = 0; index < indexLength; index++) {
                int fileIndex = Integer.parseInt(Utility.getFileNameWithoutExtension(file[index].toString()));
                byte[] data = fileToByteArray(cacheIndex, fileIndex);
                if (data != null && data.length > 0) {
                    decompressors[cacheIndex].method234(data.length, data, fileIndex);
                    System.out.println("Repacked " + fileIndex + ".");
                } else {
                    System.out.println("Unable to locate index " + fileIndex + ".");
                }
            }
        } catch (Exception e) {
            System.out.println("Error packing cache index " + cacheIndex + ".");
        }
        System.out.println("Finished repacking " + cacheIndex + ".");
    }

    public byte[] fileToByteArray(int cacheIndex, int index) {
        try {
            if (indexLocation(cacheIndex, index).length() <= 0 || indexLocation(cacheIndex, index) == null) {
                return null;
            }
            File file = new File(indexLocation(cacheIndex, index));
            byte[] fileData = new byte[(int) file.length()];
            FileInputStream fis = new FileInputStream(file);
            fis.read(fileData);
            fis.close();
            return fileData;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean inCircle(int circleX, int circleY, int clickX, int clickY, int radius) {
        return java.lang.Math.pow((circleX + radius - clickX), 2) + java.lang.Math.pow((circleY + radius - clickY), 2) < java.lang.Math.pow(radius, 2);
    }

    public static boolean controlIsDown = false;

    private void teleport(int x, int z) {
        String text = "::tele " + x + " " + z;
        outgoing.writeOpcode(103);
        outgoing.writeByte(text.length() - 1);
        outgoing.writeString(text.substring(2));
    }

    private void processMainScreenClick() {
        if (minimapState != 0)
            return;
        if (super.lastMetaModifier == 1) {
            int i = super.lastClickX - 25 - 547;
            int j = super.lastClickY - 5 - 3;
            if (frameMode != ScreenMode.FIXED) {
                i = super.lastClickX - (frameWidth - 182 + 24);
                j = super.lastClickY - 8;
            }
            if (inCircle(0, 0, i, j, 76) && mouseMapPosition() && !runHover) {
                i -= 73;
                j -= 75;
                int k = minimapInt1 + minimapInt2 & 0x7ff;
                int i1 = Rasterizer.anIntArray1470[k];
                int j1 = Rasterizer.anIntArray1471[k];
                i1 = i1 * (minimapInt3 + 256) >> 8;
                j1 = j1 * (minimapInt3 + 256) >> 8;
                int k1 = j * i1 + i * j1 >> 11;
                int l1 = j * j1 - i * i1 >> 11;
                int i2 = localPlayer.x + k1 >> 7;
                int j2 = localPlayer.y - l1 >> 7;
                if ((myPrivilege == 2 || myPrivilege == 3 || myPrivilege == 4) && controlIsDown) {
                    teleport(baseX + i2, baseY + j2);
                } else {
                    walk(i2, j2,  1);
                }
            }
        }
    }

    private String interfaceIntToString(int j) {
        if (j < 0x3b9ac9ff)
            return "" + j;
        else
            return "*";
    }

    private void showErrorScreen() {
        Graphics g = getGameComponent().getGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, 765, 503);
        method4(1);
        if (loadingError) {
            g.setFont(new Font("Helvetica", 1, 16));
            g.setColor(Color.yellow);
            int k = 35;
            g.drawString("Sorry, an error has occured whilst loading " + Configuration.NAME, 30, k);
            k += 50;
            g.setColor(Color.white);
            g.drawString("To fix this try the following (in order):", 30, k);
            k += 50;
            g.setColor(Color.white);
            g.setFont(new Font("Helvetica", 1, 12));
            g.drawString("1: Try closing ALL open web-browser windows, and reloading", 30, k);
            k += 30;
            g.drawString("2: Try clearing your web-browsers cache from tools->internet options", 30, k);
            k += 30;
            g.drawString("3: Try using a different game-world", 30, k);
            k += 30;
            g.drawString("4: Try rebooting your computer", 30, k);
            k += 30;
            g.drawString("5: Try selecting a different version of Java from the play-game menu", 30, k);
        }
        if (genericLoadingError) {
            g.setFont(new Font("Helvetica", 1, 20));
            g.setColor(Color.white);
            g.drawString("Error - unable to load game!", 50, 50);
            g.drawString("To play " + Configuration.NAME + " make sure you play from", 50, 100);
            g.drawString("http://www.UrlHere.com", 50, 150);
        }
        if (rsAlreadyLoaded) {
            g.setColor(Color.yellow);
            int l = 35;
            g.drawString("Error a copy of " + Configuration.NAME + " already appears to be loaded", 30, l);
            l += 50;
            g.setColor(Color.white);
            g.drawString("To fix this try the following (in order):", 30, l);
            l += 50;
            g.setColor(Color.white);
            g.setFont(new Font("Helvetica", 1, 12));
            g.drawString("1: Try closing ALL open web-browser windows, and reloading", 30, l);
            l += 30;
            g.drawString("2: Try rebooting your computer, and reloading", 30, l);
            l += 30;
        }
    }

    public URL getCodeBase() {
        try {
            return new URL(server + ":" + (80 + portOff));
        } catch (Exception _ex) {
        }
        return null;
    }

    private void method95() {
        for (int j = 0; j < npcCount; j++) {
            int k = npcIndices[j];
            Npc npc = npcs[k];
            if (npc != null)
                processMovement(npc);
        }
    }

    private void processMovement(Entity entity) {
        if (entity.x < 128 || entity.y < 128 || entity.x >= 13184 || entity.y >= 13184) {
            entity.emoteAnimation = -1;
            entity.graphic = -1;
            entity.startForceMovement = 0;
            entity.endForceMovement = 0;
            entity.x = entity.smallX[0] * 128 + entity.size * 64;
            entity.y = entity.smallY[0] * 128 + entity.size * 64;
            entity.resetPath();
        }
        if (entity == localPlayer && (entity.x < 1536 || entity.y < 1536 || entity.x >= 11776 || entity.y >= 11776)) {
            entity.emoteAnimation = -1;
            entity.graphic = -1;
            entity.startForceMovement = 0;
            entity.endForceMovement = 0;
            entity.x = entity.smallX[0] * 128 + entity.size * 64;
            entity.y = entity.smallY[0] * 128 + entity.size * 64;
            entity.resetPath();
        }
        if (entity.startForceMovement > tick) {
            nextPreForcedStep(entity);
        } else if (entity.endForceMovement >= tick) {
            nextForcedMovementStep(entity);
        } else {
            nextStep(entity);
        }
        appendFocusDestination(entity);
        updateAnimation(entity);
    }

    private void nextPreForcedStep(Entity entity) {
        int remaining = entity.startForceMovement - tick;
        int tempX = entity.initialX * 128 + entity.size * 64;
        int tempY = entity.initialY * 128 + entity.size * 64;
        entity.x += (tempX - entity.x) / remaining;
        entity.y += (tempY - entity.y) / remaining;
        entity.anInt1503 = 0;
        if (entity.direction == 0)
            entity.turnDirection = 1024;
        if (entity.direction == 1)
            entity.turnDirection = 1536;
        if (entity.direction == 2)
            entity.turnDirection = 0;
        if (entity.direction == 3)
            entity.turnDirection = 512;
    }

    private void nextForcedMovementStep(Entity entity) {
        if (entity.endForceMovement == tick || entity.emoteAnimation == -1 || entity.animationDelay != 0 || entity.emoteTimeRemaining + 1 > Animation.animations[entity.emoteAnimation].method258(entity.displayedEmoteFrames)) {
            final int remaining = entity.endForceMovement - entity.startForceMovement;
            final int elapsed = tick - entity.startForceMovement;
            final int startX = entity.initialX * 128 + entity.size * 64;
            final int startY = entity.initialY * 128 + entity.size * 64;
            final int endX = entity.destinationX * 128 + entity.size * 64;
            final int endY = entity.destinationY * 128 + entity.size * 64;
            entity.x = (startX * (remaining - elapsed) + endX * elapsed) / remaining;
            entity.y = (startY * (remaining - elapsed) + endY * elapsed) / remaining;
        }
        entity.anInt1503 = 0;
        if (entity.direction == 0)
            entity.turnDirection = 1024;
        if (entity.direction == 1)
            entity.turnDirection = 1536;
        if (entity.direction == 2)
            entity.turnDirection = 0;
        if (entity.direction == 3)
            entity.turnDirection = 512;
        entity.anInt1552 = entity.turnDirection;
    }

    private void nextStep(Entity entity) {
        entity.anInt1517 = entity.idleAnimation;
        if (entity.remainingPath == 0) {
            entity.anInt1503 = 0;
            return;
        }
        if (entity.emoteAnimation != -1 && entity.animationDelay == 0) {
            Animation animation = Animation.animations[entity.emoteAnimation];
            if (entity.anInt1542 > 0 && animation.animatingPrecedence == 0) {
                entity.anInt1503++;
                return;
            }
            if (entity.anInt1542 <= 0 && animation.walkingPrecedence == 0) {
                entity.anInt1503++;
                return;
            }
        }
        int i = entity.x;
        int j = entity.y;
        int k = entity.smallX[entity.remainingPath - 1] * 128 + entity.size * 64;
        int l = entity.smallY[entity.remainingPath - 1] * 128 + entity.size * 64;
        if (k - i > 256 || k - i < -256 || l - j > 256 || l - j < -256) {
            entity.x = k;
            entity.y = l;
            return;
        }
        if (i < k) {
            if (j < l)
                entity.turnDirection = 1280;
            else if (j > l)
                entity.turnDirection = 1792;
            else
                entity.turnDirection = 1536;
        } else if (i > k) {
            if (j < l)
                entity.turnDirection = 768;
            else if (j > l)
                entity.turnDirection = 256;
            else
                entity.turnDirection = 512;
        } else if (j < l)
            entity.turnDirection = 1024;
        else
            entity.turnDirection = 0;
        int i1 = entity.turnDirection - entity.anInt1552 & 0x7ff;
        if (i1 > 1024)
            i1 -= 2048;
        int j1 = entity.halfTurnAnimation;
        if (i1 >= -256 && i1 <= 256)
            j1 = entity.walkingAnimation;
        else if (i1 >= 256 && i1 < 768)
            j1 = entity.quarterAnticlockwiseTurnAnimation;
        else if (i1 >= -768 && i1 <= -256)
            j1 = entity.quarterClockwiseTurnAnimation;
        if (j1 == -1)
            j1 = entity.walkingAnimation;
        entity.anInt1517 = j1;
        int k1 = 4;
        if (entity.anInt1552 != entity.turnDirection && entity.interactingEntity == -1 && entity.rotation != 0)
            k1 = 2;
        if (entity.remainingPath > 2)
            k1 = 6;
        if (entity.remainingPath > 3)
            k1 = 8;
        if (entity.anInt1503 > 0 && entity.remainingPath > 1) {
            k1 = 8;
            entity.anInt1503--;
        }
        if (entity.aBooleanArray1553[entity.remainingPath - 1])
            k1 <<= 1;
        if (k1 >= 8 && entity.anInt1517 == entity.walkingAnimation && entity.runAnimation != -1)
            entity.anInt1517 = entity.runAnimation;
        if (i < k) {
            entity.x += k1;
            if (entity.x > k)
                entity.x = k;
        } else if (i > k) {
            entity.x -= k1;
            if (entity.x < k)
                entity.x = k;
        }
        if (j < l) {
            entity.y += k1;
            if (entity.y > l)
                entity.y = l;
        } else if (j > l) {
            entity.y -= k1;
            if (entity.y < l)
                entity.y = l;
        }
        if (entity.x == k && entity.y == l) {
            entity.remainingPath--;
            if (entity.anInt1542 > 0)
                entity.anInt1542--;
        }
    }

    private void appendFocusDestination(Entity entity) {
        if (entity.rotation == 0)
            return;
        if (entity.interactingEntity != -1 && entity.interactingEntity < 32768) {
            Npc npc = npcs[entity.interactingEntity];
            if (npc != null) {
                int i1 = entity.x - npc.x;
                int k1 = entity.y - npc.y;
                if (i1 != 0 || k1 != 0)
                    entity.turnDirection = (int) (Math.atan2(i1, k1) * 325.94900000000001D) & 0x7ff;
            }
        }
        if (entity.interactingEntity >= 32768) {
            int j = entity.interactingEntity - 32768;
            if (j == unknownInt10)
                j = myPlayerIndex;
            Player player = playerArray[j];
            if (player != null) {
                int l1 = entity.x - player.x;
                int i2 = entity.y - player.y;
                if (l1 != 0 || i2 != 0)
                    entity.turnDirection = (int) (Math.atan2(l1, i2) * 325.94900000000001D) & 0x7ff;
            }
        }
        if ((entity.faceX != 0 || entity.faceY != 0) && (entity.remainingPath == 0 || entity.anInt1503 > 0)) {
            int k = entity.x - (entity.faceX - baseX - baseX) * 64;
            int j1 = entity.y - (entity.faceY - baseY - baseY) * 64;
            if (k != 0 || j1 != 0)
                entity.turnDirection = (int) (Math.atan2(k, j1) * 325.94900000000001D) & 0x7ff;
            entity.faceX = 0;
            entity.faceY = 0;
        }
        int l = entity.turnDirection - entity.anInt1552 & 0x7ff;
        if (l != 0) {
            if (l < entity.rotation || l > 2048 - entity.rotation)
                entity.anInt1552 = entity.turnDirection;
            else if (l > 1024)
                entity.anInt1552 -= entity.rotation;
            else
                entity.anInt1552 += entity.rotation;
            entity.anInt1552 &= 0x7ff;
            if (entity.anInt1517 == entity.idleAnimation && entity.anInt1552 != entity.turnDirection) {
                if (entity.turnAnimation != -1) {
                    entity.anInt1517 = entity.turnAnimation;
                    return;
                }
                entity.anInt1517 = entity.walkingAnimation;
            }
        }
    }

    /*
     * public void method101(Entity entity) { entity.aBoolean1541 = false; if
     * (entity.anInt1517 > Animation.animations.length) entity.anInt1517 = -1;
     * if (entity.anInt1517 != -1) { Animation animation =
     * Animation.animations[entity.anInt1517]; entity.anInt1519++; if (animation
     * == null) return; if (entity.anInt1518 < animation.frameCount &&
     * entity.anInt1519 > animation.method258(entity.anInt1518)) {
     * entity.anInt1519 = 0; entity.anInt1518++; } if (entity.anInt1518 >=
     * animation.frameCount) { entity.anInt1519 = 0; entity.anInt1518 = 0; } } if
     * (entity.graphic != -1 && tick >= entity.graphicDelay) { if
     * (entity.currentAnimation < 0) entity.currentAnimation = 0; Animation
     * animation_1 = Graphic.cache[entity.graphic].aAnimation_407; for
     * (entity.anInt1522++; entity.currentAnimation < animation_1.frameCount &&
     * entity.anInt1522 > animation_1.method258(entity.currentAnimation);
     * entity.currentAnimation++) entity.anInt1522 -=
     * animation_1.method258(entity.currentAnimation);
     *
     * if (entity.currentAnimation >= animation_1.frameCount &&
     * (entity.currentAnimation < 0 || entity.currentAnimation >=
     * animation_1.frameCount)) entity.graphic = -1; } if (entity.emoteAnimation
     * != -1 && entity.animationDelay <= 1) { Animation animation_2 =
     * Animation.animations[entity.emoteAnimation]; if (animation_2.anInt363 ==
     * 1 && entity.anInt1542 > 0 && entity.startForceMovement <= tick &&
     * entity.endForceMovement < tick) { entity.animationDelay = 1; return; } }
     * if (entity.emoteAnimation != -1 && entity.animationDelay == 0) {
     * Animation animation_3 = Animation.animations[entity.emoteAnimation]; for
     * (entity.emoteTimeRemaining++; entity.displayedEmoteFrames <
     * animation_3.frameCount && entity.emoteTimeRemaining >
     * animation_3.method258(entity.displayedEmoteFrames);
     * entity.displayedEmoteFrames++) entity.emoteTimeRemaining -=
     * animation_3.method258(entity.displayedEmoteFrames);
     *
     * if (entity.displayedEmoteFrames >= animation_3.frameCount) {
     * entity.displayedEmoteFrames -= animation_3.anInt356;
     * entity.currentAnimationLoops++; if (entity.currentAnimationLoops >=
     * animation_3.anInt362) entity.emoteAnimation = -1; if
     * (entity.displayedEmoteFrames < 0 || entity.displayedEmoteFrames >=
     * animation_3.frameCount) entity.emoteAnimation = -1; } entity.aBoolean1541 =
     * animation_3.aBoolean358; } if (entity.animationDelay > 0)
     * entity.animationDelay--; }
     */

    public void updateAnimation(Entity entity) {
        entity.aBoolean1541 = false;
        if (entity.anInt1517 != -1) {
            if (entity.anInt1517 > Animation.animations.length) {
                entity.anInt1517 = 1;
            }
            Animation animation = Animation.animations[entity.anInt1517];
            if (animation == null)
                return;
            entity.anInt1519++;
            if (entity.anInt1518 < animation.frameCount && entity.anInt1519 > animation.method258(entity.anInt1518)) {
                entity.anInt1519 = 1;
                entity.anInt1518++;
                entity.nextIdleFrame++;
            }
            if (Settings.TWEENING) {
                entity.nextIdleFrame = entity.anInt1518 + 1;
            }
            if (entity.nextIdleFrame >= animation.frameCount) {
                if (entity.nextIdleFrame >= animation.frameCount) {
                    entity.nextIdleFrame = 0;
                }
            }
            if (entity.anInt1518 >= animation.frameCount) {
                entity.anInt1519 = 1;
                entity.anInt1518 = 0;
            }
        }
        if (entity.graphic != -1 && tick >= entity.graphicDelay) {
            if (entity.currentAnimation < 0) {
                entity.currentAnimation = 0;
            }
            Animation animation_1 = Graphic.cache[entity.graphic].aAnimation_407;
            if (animation_1 == null)
                return;
            for (entity.anInt1522++; entity.currentAnimation < animation_1.frameCount && entity.anInt1522 > animation_1.method258(entity.currentAnimation); entity.currentAnimation++) {
                entity.anInt1522 -= animation_1.method258(entity.currentAnimation);
            }
            if (entity.currentAnimation >= animation_1.frameCount && (entity.currentAnimation < 0 || entity.currentAnimation >= animation_1.frameCount)) {
                entity.graphic = -1;
            }
            if (Settings.TWEENING) {
                entity.nextGraphicFrame = entity.currentAnimation + 1;
            }
            if (entity.nextGraphicFrame >= animation_1.frameCount) {
                if (entity.nextGraphicFrame < 0 || entity.nextGraphicFrame >= animation_1.frameCount) {
                    entity.graphic = -1;
                }
            }
        }
        if (entity.emoteAnimation != -1 && entity.animationDelay <= 1) {
            if (Animation.animations == null)
                return;
            if (entity.emoteAnimation >= Animation.animations.length) {
                System.out.println("Entity emote animation " + entity.emoteAnimation + " does not exsist.");
                return;
            }
            Animation animation_2 = Animation.animations[entity.emoteAnimation];
            if (animation_2 == null)
                return;
            if (animation_2.animatingPrecedence == 1 && entity.anInt1542 > 0 && entity.startForceMovement <= tick && entity.endForceMovement < tick) {
                entity.animationDelay = 1;
                return;
            }
        }
        if (entity.emoteAnimation != -1 && entity.animationDelay == 0) {
            Animation animation_3 = Animation.animations[entity.emoteAnimation];
            for (entity.emoteTimeRemaining++; entity.displayedEmoteFrames < animation_3.frameCount && entity.emoteTimeRemaining > animation_3.method258(entity.displayedEmoteFrames); entity.displayedEmoteFrames++) {
                entity.emoteTimeRemaining -= animation_3.method258(entity.displayedEmoteFrames);
            }
            if (entity.displayedEmoteFrames >= animation_3.frameCount) {
                entity.displayedEmoteFrames -= animation_3.loopOffset;
                entity.currentAnimationLoops++;
                if (entity.currentAnimationLoops >= animation_3.maxLoops) {
                    entity.emoteAnimation = -1;
                }
                if (entity.displayedEmoteFrames < 0 || entity.displayedEmoteFrames >= animation_3.frameCount) {
                    entity.emoteAnimation = -1;
                }
            }
            if (Settings.TWEENING) {
                entity.nextAnimFrame = entity.displayedEmoteFrames + 1;
            }
            if (entity.nextAnimFrame >= animation_3.frameCount) {
                if (entity.currentAnimationLoops >= animation_3.maxLoops) {
                    entity.nextAnimFrame = entity.displayedEmoteFrames + 1;
                }
                if (entity.nextAnimFrame < 0 || entity.nextAnimFrame >= animation_3.frameCount) {
                    entity.nextAnimFrame = entity.displayedEmoteFrames;
                }
            }
            entity.aBoolean1541 = animation_3.stretches;
        }
        if (entity.animationDelay > 0) {
            entity.animationDelay--;
        }
    }

    private boolean initalizeGraphicsBuffers;

    private boolean shouldDrawFullScreenInterface() {
        return fullscreenInterfaceID != -1 && (loadingStage == 2 || super.fullGameScreen != null);
    }

    private void drawFullScreenInterface() {
        if (loadingStage == 2) {
            method119(tickDelta, fullscreenInterfaceID);
            if (openInterfaceID != -1) {
                method119(tickDelta, openInterfaceID);
            }
            tickDelta = 0;
            resetAllImageProducers();
            super.fullGameScreen.initDrawingArea();
            Rasterizer.anIntArray1472 = fullScreenTextureArray;
            Raster.reset();
            drawGameScreenSprite = true;
            if (openInterfaceID != -1) {
                RSInterface rsInterface_1 = RSInterface.getInterfaceCache()[openInterfaceID];
                if (rsInterface_1.width == 512 && rsInterface_1.height == 334 && rsInterface_1.type == 0) {
                    rsInterface_1.width = 765;
                    rsInterface_1.height = 503;
                }
                drawInterface(rsInterface_1, 0, 8, 0);
            }
            RSInterface rsInterface = RSInterface.getInterfaceCache()[fullscreenInterfaceID];
            if (rsInterface.width == 512 && rsInterface.height == 334 && rsInterface.type == 0) {
                rsInterface.width = 765;
                rsInterface.height = 503;
            }
            drawInterface(rsInterface, 0, 8, 0);
            if (!menuOpen) {
                processRightClick();
                drawTooltip();
            } else {
                drawMenu(frameMode == ScreenMode.FIXED ? 4 : 0, frameMode == ScreenMode.FIXED ? 4 : 0);
            }
        }
        initalizeGraphicsBuffers = true;
        super.fullGameScreen.drawGraphics(super.graphics, 0, 0);
    }

    public void drawGameFrameSprite() {
        drawGameScreenSprite = false;
        redrawDialogueBox = true;
        tabAreaAltered = true;

        if (loadingStage == 0 || loadingStage == 1) { // we are not in game yet
            if (frameMode == ScreenMode.FIXED) {
                gameFrameIP.drawGraphics(graphics, 0, 0);
            }
        } else if (loadingStage == 2) { // we are in game
            if (frameMode == ScreenMode.FIXED) {
                gameScreenFrameTop.initDrawingArea();
                spriteCache.get(825).drawSprite(0, 0);
                gameScreenFrameTop.drawGraphics(super.graphics, 0, 0);

                gameScreenFrameLeft.initDrawingArea();
                spriteCache.get(824).drawSprite(0, 0);
                gameScreenFrameLeft.drawGraphics(super.graphics, 0, 4);
            }
        }

    }

    private void handleRedrawTriggers() {
        if (backDialogueId == -1) {
            aClass9_1059.scrollPosition = anInt1211 - anInt1089 - 110;
            if (super.mouseX >= 496 && super.mouseX <= 511 && super.mouseY > (frameMode == ScreenMode.FIXED ? 345 : frameHeight - 158))
                method65(494, 110, super.mouseX, super.mouseY - (frameMode == ScreenMode.FIXED ? 345 : frameHeight - 158), aClass9_1059, 0, false, anInt1211);
            int i = anInt1211 - 110 - aClass9_1059.scrollPosition;
            if (i < 0) {
                i = 0;
            }
            if (i > anInt1211 - 110) {
                i = anInt1211 - 110;
            }
            if (anInt1089 != i) {
                anInt1089 = i;
                redrawDialogueBox = true;
            }
        }

        if (backDialogueId != -1) {
            if (method119(tickDelta, backDialogueId)) {
                redrawDialogueBox = true;
            }
        }

        if (atInventoryInterfaceType == 3) {
            redrawDialogueBox = true;
        }

        if (activeInterfaceType == 3) {
            redrawDialogueBox = true;
        }

        if (clickToContinueString != null) {
            redrawDialogueBox = true;
        }

        if (menuOpen && menuScreenArea == 2) {
            redrawDialogueBox = true;
        }

        if (redrawDialogueBox) {
            drawChatArea();
            redrawDialogueBox = false;
        }

        if (anInt1054 != -1) {
            tabAreaAltered = true;
        }

        if (tabAreaAltered) {
            if (anInt1054 != -1 && anInt1054 == tabID) {
                anInt1054 = -1;
                outgoing.writeOpcode(120);
                outgoing.writeByte(tabID);
            }
            tabAreaAltered = false;
            gameScreenIP.initDrawingArea();
        }

    }

    private void drawGameScreen() {

        // check to see if we need to draw a full screen interface first
        if (shouldDrawFullScreenInterface()) {
            drawFullScreenInterface();
            return;
        }

        // initialize graphic buffers
        if (initalizeGraphicsBuffers) {
            initializeGraphicBuffers();
            initalizeGraphicsBuffers = false;
        }

        // draws a big sprite on the screen to serve as the background, this should only be drawn every login
        if (drawGameScreenSprite) {
            drawGameFrameSprite();
        }

        if (invOverlayInterfaceID != -1) {
            method119(tickDelta, invOverlayInterfaceID);
        }

        drawTabArea();

        handleRedrawTriggers();

        if (loadingStage == 2) {
            drawGameWorld();
            if (frameMode == ScreenMode.FIXED) {
                drawMinimap();
                mapIP.drawGraphics(super.graphics, 516, 0);
            }
        }

        tickDelta = 0;
    }

    private boolean buildFriendsListMenu(RSInterface class9) {
        int i = class9.contentType;
        if (i >= 1 && i <= 200 || i >= 701 && i <= 900) {
            if (i >= 801)
                i -= 701;
            else if (i >= 701)
                i -= 601;
            else if (i >= 101)
                i -= 101;
            else
                i--;
            menuActionName[menuActionRow] = "Remove <col=FFFFFF>" + friendsList[i];
            menuActionID[menuActionRow] = 792;
            menuActionRow++;

            if (Utility.staff(localPlayer.privelage)) {
                menuActionName[menuActionRow] = "Manage <col=FFFFFF>" + friendsList[i];
                menuActionID[menuActionRow] = 354;
                menuActionRow++;
            }

            menuActionName[menuActionRow] = "Profile <col=FFFFFF>" + friendsList[i];
            menuActionID[menuActionRow] = 353;
            menuActionRow++;
            menuActionName[menuActionRow] = "Message <col=FFFFFF>" + friendsList[i];
            menuActionID[menuActionRow] = 639;
            menuActionRow++;
            return true;
        }
        if (i >= 401 && i <= 500) {
            menuActionName[menuActionRow] = "Remove <col=FFFFFF>" + class9.disabledMessage;
            menuActionID[menuActionRow] = 322;
            menuActionRow++;
            return true;
        }

        if (i == 902) {
            menuActionName[menuActionRow] = "Choose " + class9.disabledMessage;
            menuActionID[menuActionRow] = 169;
            menuActionRow++;
            return true;
        } else {
            return false;
        }
    }

    private void method104() {
        Projectile class30_sub2_sub4_sub3 = (Projectile) aClass19_1056.reverseGetFirst();
        for (; class30_sub2_sub4_sub3 != null; class30_sub2_sub4_sub3 = (Projectile) aClass19_1056.reverseGetNext())
            if (class30_sub2_sub4_sub3.anInt1560 != plane || class30_sub2_sub4_sub3.aBoolean1567)
                class30_sub2_sub4_sub3.unlink();
            else if (tick >= class30_sub2_sub4_sub3.anInt1564) {
                class30_sub2_sub4_sub3.method454(tickDelta);
                if (class30_sub2_sub4_sub3.aBoolean1567)
                    class30_sub2_sub4_sub3.unlink();
                else
                    worldController.method285(class30_sub2_sub4_sub3.anInt1560, 0, class30_sub2_sub4_sub3.anInt1563, -1, class30_sub2_sub4_sub3.anInt1562, 60, class30_sub2_sub4_sub3.anInt1561, class30_sub2_sub4_sub3, false);
            }

    }

    public void drawBlackBox(int xPos, int yPos) {
        Raster.fillRectangle(xPos - 2, yPos - 1, 1, 71, 0x726451);
        Raster.fillRectangle(xPos + 174, yPos, 1, 69, 0x726451);
        Raster.fillRectangle(xPos - 2, yPos - 2, 178, 1, 0x726451);
        Raster.fillRectangle(xPos, yPos + 68, 174, 1, 0x726451);
        Raster.fillRectangle(xPos - 1, yPos - 1, 1, 71, 0x2E2B23);
        Raster.fillRectangle(xPos + 175, yPos - 1, 1, 71, 0x2E2B23);
        Raster.fillRectangle(xPos, yPos - 1, 175, 1, 0x2E2B23);
        Raster.fillRectangle(xPos, yPos + 69, 175, 1, 0x2E2B23);
        Raster.fillRectangle(xPos, yPos, 174, 68, 0, 220);
    }

    public void refreshScreenOptions() {
        int childIds[] = {50031, 50034, 50037};
        int enabledIds[] = {112, 114, 116};
        int disabledIds[] = {111, 113, 115};
        ScreenMode modes[] = {ScreenMode.FIXED, ScreenMode.RESIZABLE, ScreenMode.FULLSCREEN};
        for (int index = 0; index < modes.length; index++) {
            RSInterface.getInterfaceCache()[childIds[index]].setSprite(Client.spriteCache.get(frameMode == modes[index] ? enabledIds[index] : disabledIds[index]));
        }
    }

    int itemMovement = 0;
    int itemMovementCount = 0;
    int marqueeSlot = 0;
    int marqueeMovement = 0;


    void drawMovingItem(RSInterface rsi, Sprite sprite, int x, int y) {

        if (itemMovementCount++ % 35 == 0) {
            itemMovement -= 42;
        }
        if (itemMovement < -45) {
            itemMovement = Client.frameWidth + 2;
        }

        x += itemMovement;
        sprite.drawSprite(x, y);
    }



    private void drawInterface(int x, int y, RSInterface rsi, int scroll) {
        drawInterface(rsi, x, y, scroll);
    }

    private void drawInterface(RSInterface rsi, int x, int y, int scroll) {
        refreshScreenOptions();
        if (rsi == null) {
            rsi = RSInterface.getInterfaceCache()[21356];
        }
        if (rsi.type != 0 || rsi.children == null) {
            return;
        }
        if (rsi.isMouseoverTriggered && anInt1026 != rsi.interfaceId && anInt1048 != rsi.interfaceId && anInt1039 != rsi.interfaceId) {
            return;
        }
        int i1 = Raster.topX;
        int j1 = Raster.topY;
        int k1 = Raster.bottomX;
        int l1 = Raster.bottomY;
        Raster.setDrawingArea(x, y, x + rsi.width, y + rsi.height);
        int i2 = rsi.children.length;
        int alpha = rsi.transparency;
        for (int j2 = 0; j2 < i2; j2++) {
            int childX = rsi.childX[j2] + x;
            int childY = (rsi.childY[j2] + y) - scroll;
            RSInterface child = RSInterface.getInterfaceCache()[rsi.children[j2]];
            childX += child.anInt263;
            childY += child.anInt265;
            if (child.contentType > 0) {
                drawFriendsListOrWelcomeScreen(child);
            }
            // here
            int[] IDs = {1196, 1199, 1206, 1215, 1224, 1231, 1240, 1249, 1258, 1267, 1274, 1283, 1573, 1290, 1299, 1308, 1315, 1324, 1333, 1340, 1349, 1358, 1367, 1374, 1381, 1388, 1397, 1404, 1583, 12038, 1414, 1421, 1430, 1437, 1446, 1453, 1460, 1469, 15878, 1602, 1613, 1624, 7456, 1478, 1485, 1494, 1503, 1512, 1521, 1530, 1544, 1553, 1563, 1593, 1635, 12426, 12436, 12446, 12456, 6004, 18471,
                    /* Ancients */
                    12940, 12988, 13036, 12902, 12862, 13046, 12964, 13012, 13054, 12920, 12882, 13062, 12952, 13000, 13070, 12912, 12872, 13080, 12976, 13024, 13088, 12930, 12892, 13096};
            for (int m5 = 0; m5 < IDs.length; m5++) {
                if (child.interfaceId == IDs[m5] + 1) {
                    if (m5 > 61)
                        drawBlackBox(childX + 1, childY);
                    else
                        drawBlackBox(childX, childY + 1);
                }
            }
            int[] runeChildren = {1202, 1203, 1209, 1210, 1211, 1218, 1219, 1220, 1227, 1228, 1234, 1235, 1236, 1243, 1244, 1245, 1252, 1253, 1254, 1261, 1262, 1263, 1270, 1271, 1277, 1278, 1279, 1286, 1287, 1293, 1294, 1295, 1302, 1303, 1304, 1311, 1312, 1318, 1319, 1320, 1327, 1328, 1329, 1336, 1337, 1343, 1344, 1345, 1352, 1353, 1354, 1361, 1362, 1363, 1370, 1371, 1377, 1378, 1384, 1385, 1391, 1392, 1393, 1400, 1401, 1407, 1408, 1410, 1417, 1418, 1424, 1425, 1426, 1433, 1434, 1440, 1441, 1442, 1449, 1450, 1456, 1457, 1463, 1464, 1465, 1472, 1473, 1474, 1481, 1482, 1488, 1489, 1490, 1497, 1498, 1499, 1506, 1507, 1508, 1515, 1516, 1517, 1524, 1525, 1526, 1533, 1534, 1535, 1547, 1548, 1549, 1556, 1557, 1558, 1566, 1567, 1568, 1576, 1577, 1578, 1586, 1587, 1588, 1596, 1597, 1598, 1605, 1606, 1607, 1616, 1617, 1618, 1627, 1628, 1629, 1638, 1639, 1640, 6007, 6008, 6011, 8673, 8674, 12041, 12042, 12429, 12430, 12431, 12439, 12440, 12441, 12449, 12450, 12451, 12459, 12460, 15881, 15882, 15885, 18474, 18475, 18478};
            for (int r = 0; r < runeChildren.length; r++)
                if (child.interfaceId == runeChildren[r])
                    child.modelZoom = 775;
            if (child.type == 0) {
                if (child.scrollPosition > child.scrollMax - child.height)
                    child.scrollPosition = child.scrollMax - child.height;
                if (child.scrollPosition < 0)
                    child.scrollPosition = 0;
                drawInterface(child, childX, childY, child.scrollPosition);
                if (child.scrollMax > child.height) {
                    drawScrollbar(child.height, child.scrollPosition, childY, childX + child.width, child.scrollMax, false);
                }
            } else if (child.type != 1)
                if (child.type == 2) {
                    int slot = 0;
                    int newSlot = 0;
                    int tabAm = 0;
                    int tabSlot = -1;
                    int hh = 2;
                    if (child.contentType == 206) {
                        int tabHeight = 0;
                        for (int i = 0; i < tabAmounts.length; i++) {
                            if (tabSlot + 1 < tabAmounts.length && tabAmounts[tabSlot + 1] > 0) {
                                tabAm += tabAmounts[++tabSlot];
                                tabHeight += (tabAmounts[tabSlot] / child.width) + (tabAmounts[tabSlot] % child.width == 0 ? 0 : 1);
                                if (tabSlot + 1 < tabAmounts.length && tabAmounts[tabSlot + 1] > 0 && settings[211] == 0 && (RSInterface.getInterfaceCache()[60019].disabledMessage.length() == 0 || RSInterface.getInterfaceCache()[60019].defaultInputFieldText.equalsIgnoreCase(RSInterface.getInterfaceCache()[60019].disabledMessage))) {
                                    Raster.drawHorizontalLine(childX, (childY + tabHeight * (32 + child.invSpritePadY) + hh) - 1, ((32 + child.invSpritePadX) * child.width) - 10, 0x5C5142);
                                    Raster.drawHorizontalLine(childX, (childY + tabHeight * (32 + child.invSpritePadY) + hh), ((32 + child.invSpritePadX) * child.width) - 10, 0x5C5142);
                                    spriteCache.get(229).drawSprite1(frameMode == ScreenMode.FIXED ? 66 : frameWidth / 2 - 291, (childY + tabHeight * (32 + child.invSpritePadY) + hh) + 4, 255);
                                }
                                hh += 8;
                            }

                            if (i > 0) {
                                int itemSlot = tabAm - tabAmounts[i];
                                if (itemSlot == 360) {
                                    itemSlot--;
                                }
                                int xOffset = (frameWidth - 237 - RSInterface.getInterfaceCache()[60000].width) / 2;
                                int yOffset = 36 + ((frameHeight - 503) / 2);
                                int x2 = xOffset + 77;
                                int y2 = yOffset + 25;
                                try {
                                    int item = RSInterface.getInterfaceCache()[5382].inv[itemSlot];
                                    if (tabAmounts[i] > 0 && item > 0) {
                                        Sprite icon = null;
                                        int amount;
                                        if ((RSInterface.getInterfaceCache()[60019].disabledMessage.length() > 0 && !RSInterface.getInterfaceCache()[60019].defaultInputFieldText.equalsIgnoreCase(RSInterface.getInterfaceCache()[60019].disabledMessage)) && child.contentType == 206) {
                                            amount = bankStackTemp[itemSlot];
                                        } else {
                                            amount = child.invStackSizes[itemSlot];
                                        }

                                        int display = settings[210];

                                        if (display == 0) {
                                            icon = ItemDefinition.getSprite(item - 1, amount, 0);
                                        } else if (display == 1) {
                                            icon = spriteCache.get(207 + i);
                                        } else if (display == 2) {
                                            icon = spriteCache.get(198 + i);
                                        }

                                        if (icon != null) {
                                            if (display == 0 && amount == 0) {
                                                icon.drawSprite1((frameMode == ScreenMode.FIXED ? 59 : x2 + 3) + 40 * i, (frameMode == ScreenMode.FIXED ? 41 : y2 + 2), 110, true);
                                            } else {
                                                icon.drawSprite1((frameMode == ScreenMode.FIXED ? 59 : x2 + 3) + 40 * i, (frameMode == ScreenMode.FIXED ? 41 : y2 + 2), 255, true);
                                            }
                                        }

                                        RSInterface.getInterfaceCache()[60031 + i * 4].anInt265 = 0;
                                        RSInterface.getInterfaceCache()[60032 + i * 4].anInt265 = 0;
                                        RSInterface.getInterfaceCache()[60032 + i * 4].tooltip = "View tab @or2@" + i;
                                        RSInterface.getInterfaceCache()[60032 + i * 4].disabledSprite = spriteCache.get(193);
                                    } else if (tabAmounts[i - 1] <= 0) {
                                        RSInterface.getInterfaceCache()[60031 + i * 4].anInt265 = -500;
                                        if (i > 1) {
                                            RSInterface.getInterfaceCache()[60032 + i * 4].anInt265 = -500;
                                        } else {
                                            spriteCache.get(198).drawSprite1((frameMode == ScreenMode.FIXED ? 59 : x2) + 40 * i, (frameMode == ScreenMode.FIXED ? 41 : y2), 255, true);
                                        }
                                        RSInterface.getInterfaceCache()[60032 + i * 4].tooltip = "New tab";
                                    } else {
                                        RSInterface.getInterfaceCache()[60031 + i * 4].anInt265 = -500;
                                        RSInterface.getInterfaceCache()[60032 + i * 4].anInt265 = 0;
                                        RSInterface.getInterfaceCache()[60032 + i * 4].tooltip = "New tab";
                                        RSInterface.getInterfaceCache()[60032 + i * 4].disabledSprite = spriteCache.get(196);
                                        spriteCache.get(198).drawSprite1((frameMode == ScreenMode.FIXED ? 59 : x2) + 40 * i, (frameMode == ScreenMode.FIXED ? 41 : y2), 255, true);
                                    }
                                } catch (Exception e) {
                                    System.out.println("Bank tab icon error: tab [" + i + "], amount [" + tabAm + "], tabAmount [" + tabAmounts[i] + "], itemSlot [" + itemSlot + "]");
                                    e.printStackTrace();
                                }
                            }
                        }

                        Raster.bottomY += 3;

                        tabAm = tabAmounts[0];
                        tabSlot = 0;
                        hh = 0;

                        newSlot = 0;
                        int tabH = 0;
                        if (settings[211] != 0 && (RSInterface.getInterfaceCache()[60019].disabledMessage.length() == 0 || RSInterface.getInterfaceCache()[60019].defaultInputFieldText.equalsIgnoreCase(RSInterface.getInterfaceCache()[60019].disabledMessage))) {
                            for (int i = 0; i < tabAmounts.length; i++) {
                                if (i == settings[211]) {
                                    tabH = (int) Math.ceil(tabAmounts[i] / 9.0);
                                    break;
                                }
                                newSlot += tabAmounts[i];
                            }
                            slot = newSlot;
                            RSInterface.getInterfaceCache()[5385].scrollMax = tabH * 36;
                        } else {
                            int totalTabs = 0;
                            for (int i = 0; i < tabAmounts.length; i++) {
                                if (tabAmounts[i] > 0) {
                                    totalTabs = i;
                                    tabH += (int) Math.ceil(tabAmounts[i] / 9.0);
                                }
                            }

                            RSInterface.getInterfaceCache()[5385].scrollMax = tabH * 36 + totalTabs * 10;
                        }
                    }

                    int dragX = 0, dragY = 0;
                    Sprite draggedItem = null;

                    heightLoop:
                    for (int height = 0; height < child.height; height++) {
                        for (int width = 0; width < child.width; width++) {
                            int w = childX + width * (32 + child.invSpritePadX);
                            int h = childY + height * (32 + child.invSpritePadY) + hh;
                            if (child.contentType == 206 && (RSInterface.getInterfaceCache()[60019].disabledMessage.length() == 0 || RSInterface.getInterfaceCache()[60019].defaultInputFieldText.equalsIgnoreCase(RSInterface.getInterfaceCache()[60019].disabledMessage))) {
                                if (settings[211] == 0) {
                                    if (slot == tabAm) {
                                        if (tabSlot + 1 < tabAmounts.length) {
                                            tabAm += tabAmounts[++tabSlot];
                                            if (tabSlot > 0 && tabAmounts[tabSlot - 1] % child.width == 0) {
                                                height--;
                                            }
                                            hh += 8;
                                        }
                                        break;
                                    }
                                } else if (settings[211] <= 9) {
                                    if (slot >= tabAmounts[settings[211]] + newSlot) {
                                        break heightLoop;
                                    }
                                }
                            }
                            if (slot < 20) {
                                w += child.spritesX[slot];
                                h += child.spritesY[slot];
                            }
                            int itemId = child.inv[slot] - 1;

                            if ((RSInterface.getInterfaceCache()[60019].disabledMessage.length() > 0 && !RSInterface.getInterfaceCache()[60019].defaultInputFieldText.equalsIgnoreCase(RSInterface.getInterfaceCache()[60019].disabledMessage)) && child.contentType == 206) {
                                itemId = bankInvTemp[slot] - 1;
                            }

                            if (itemId > 0) {
                                if (child.parentID == 40051) {
                                    spriteCache.get(hoverShopTile == slot ? 377 : 255).drawSprite(w - 7, h - 4);
                                }
                                int x2 = 0;
                                int y2 = 0;
                                if (w > Raster.topX - 32 && w < Raster.bottomX && h > Raster.topY - 32 && h < Raster.bottomY || activeInterfaceType != 0 && dragFromSlot == slot) {
                                    int color = 0;
                                    if (itemSelected == 1 && anInt1283 == slot && anInt1284 == child.interfaceId) {
                                        color = 0xffffff;
                                    }
                                    Sprite itemSprite;
                                    if ((RSInterface.getInterfaceCache()[60019].disabledMessage.length() > 0 && !RSInterface.getInterfaceCache()[60019].defaultInputFieldText.equalsIgnoreCase(RSInterface.getInterfaceCache()[60019].disabledMessage)) && child.contentType == 206) {
                                        itemSprite = ItemDefinition.getSprite(itemId, bankStackTemp[slot], color);
                                    } else {
                                        itemSprite = ItemDefinition.getSprite(itemId, child.invStackSizes[slot], color);
                                    }
                                    if (itemSprite != null) {
                                        if (activeInterfaceType != 0 && dragFromSlot == slot && focusedDragWidget == child.interfaceId) {
                                            draggedItem = itemSprite;
                                            x2 = super.mouseX - pressX;
                                            y2 = super.mouseY - pressY;
                                            if (x2 < 5 && x2 > -5)
                                                x2 = 0;
                                            if (y2 < 5 && y2 > -5)
                                                y2 = 0;
                                            if (dragCycle < 10) {
                                                x2 = 0;
                                                y2 = 0;
                                            }
                                            dragX = w + x2;
                                            dragY = h + y2;
                                            if (h + y2 < Raster.topY && rsi.scrollPosition > 0) {
                                                int scrollValue = (tickDelta * (Raster.topY - h - y2)) / 3;
                                                if (scrollValue > tickDelta * 10)
                                                    scrollValue = tickDelta * 10;
                                                if (scrollValue > rsi.scrollPosition)
                                                    scrollValue = rsi.scrollPosition;
                                                rsi.scrollPosition -= scrollValue;
                                                pressY += scrollValue;
                                            }
                                            if (h + y2 + 32 > Raster.bottomY && rsi.scrollPosition < rsi.scrollMax - rsi.height) {
                                                int scrollValue = (tickDelta * ((h + y2 + 32) - Raster.bottomY)) / 3;
                                                if (scrollValue > tickDelta * 10) {
                                                    scrollValue = tickDelta * 10;
                                                }
                                                if (scrollValue > rsi.scrollMax - rsi.height - rsi.scrollPosition) {
                                                    scrollValue = rsi.scrollMax - rsi.height - rsi.scrollPosition;
                                                }
                                                rsi.scrollPosition += scrollValue;
                                                pressY -= scrollValue;
                                            }
                                        } else if (atInventoryInterfaceType != 0 && atInventoryIndex == slot && atInventoryInterface == child.interfaceId) {
                                            itemSprite.drawSprite1(w, h);
                                        } else {

                                            if (child.alpha > 0) {
                                                itemSprite.drawTransparentSprite(w, h, 180);
                                            } else {
                                                int amount;
                                                if ((RSInterface.getInterfaceCache()[60019].disabledMessage.length() > 0 && !RSInterface.getInterfaceCache()[60019].defaultInputFieldText.equalsIgnoreCase(RSInterface.getInterfaceCache()[60019].disabledMessage)) && child.contentType == 206) {
                                                    amount = bankStackTemp[slot];
                                                } else {
                                                    amount = child.invStackSizes[slot];
                                                }

                                                if (child.parentID == 5382 && amount == 0) {
                                                    itemSprite.drawTransparentSprite(w, h, 110);
                                                } else {
                                                    if (child.contentType == 3) {
                                                        drawMovingItem(rsi, itemSprite, w, h);
                                                    } else {
                                                        itemSprite.drawSprite(w, h);
                                                    }
                                                }
                                            }
                                        }


                                        int amount;
                                        if ((RSInterface.getInterfaceCache()[60019].disabledMessage.length() > 0 && !RSInterface.getInterfaceCache()[60019].defaultInputFieldText.equalsIgnoreCase(RSInterface.getInterfaceCache()[60019].disabledMessage)) && child.contentType == 206) {
                                            amount = bankStackTemp[slot];
                                        } else {
                                            amount = child.invStackSizes[slot];
                                        }

                                        if (itemSprite.resizeWidth == 33 || amount != 1) {
                                            // item container drawing
                                            if (child.parentID == 5382 && amount == 0) {
                                                smallFont.render(0xFFE100, "0", h + 10 + y2, w + 1 + x2);
                                            } else if (child.displayAmount) {
                                                smallFont.render(0, intToKOrMil(amount), h + 10 + y2, w + 1 + x2);
                                                if (amount >= 1)
                                                    smallFont.render(0xFFFF00, intToKOrMil(amount), h + 9 + y2, w + x2);
                                                if (amount >= 100000)
                                                    smallFont.render(0xFFFFFF, intToKOrMil(amount), h + 9 + y2, w + x2);
                                                if (amount >= 10000000)
                                                    smallFont.render(0x00FF80, intToKOrMil(amount), h + 9 + y2, w + x2);
                                            }
                                        }
                                    }
                                }
                            } else if (child.sprites != null && slot < 20) {
                                Sprite childSprite = child.sprites[slot];
                                if (childSprite != null) {
                                    childSprite.drawSprite(w, h);
                                }
                            }
                            slot++;
                        }
                    }
                    if (draggedItem != null) {
                        draggedItem.drawSprite1(dragX, dragY, 200 + (int) (50 * Math.sin(tick / 10.0)), child.contentType == 206);
                    }

                } else if (child.type == 3) {
                    boolean flag = false;
                    if (anInt1039 == child.interfaceId || anInt1048 == child.interfaceId || anInt1026 == child.interfaceId)
                        flag = true;
                    int j3;
                    if (interfaceIsSelected(child)) {
                        j3 = child.anInt219;
                        if (flag && child.anInt239 != 0)
                            j3 = child.anInt239;
                    } else {
                        j3 = child.textColor;
                        if (flag && child.textHoverColor != 0)
                            j3 = child.textHoverColor;
                    }
                    if (child.opacity == 0) {
                        if (child.aBoolean227)
                            Raster.fillRectangle(childX, childY, child.width, child.height, j3);
                        else
                            Raster.drawRectangle(childX, childY, child.width, child.height, j3);
                    } else if (child.aBoolean227)
                        Raster.fillRectangle(childX, childY, child.width, child.height, j3, 256 - (child.opacity & 0xff));
                    else
                        Raster.drawRectangle(childX, childY, child.width, child.height, j3, 256 - (child.opacity & 0xff));
                } else if (child.type == 20) {
                    RSFont rsFont;
                    TextDrawingArea textDrawingArea = child.textDrawingAreas;
                    String[] marqueeMessage = child.marqueeMessages;
                    int speed = child.hoverType;

                    if (textDrawingArea == smallFont) {
                        rsFont = newSmallFont;
                    } else if (textDrawingArea == regularText) {
                        rsFont = newRegularFont;
                    } else if (textDrawingArea == boldText) {
                        rsFont = newBoldFont;
                    } else {
                        rsFont = newFancyFont;
                    }

                    marqueeMovement -= speed/40;
                    if (marqueeMovement < -marqueeMessage[marqueeSlot].length() * 7) {
                        marqueeMovement = Client.frameWidth + 2;
                        marqueeSlot++;
                        if (marqueeSlot >= marqueeMessage.length)
                            marqueeSlot = 0;
                    }
                    if (marqueeMessage[marqueeSlot] == null || marqueeMessage[marqueeSlot].isEmpty()) {
                        marqueeSlot = 0;
                    }

                    Raster.setDrawingArea(3, 29, 760, 49);
                    rsFont.drawBasicString(marqueeMessage[marqueeSlot], marqueeMovement, childY, child.textColor, child.textShadow ? 0 : -1);
                    Raster.defaultDrawingAreaSize();
                }

                else if (child.type == 19) {
                    Raster.drawRectangle(childX-1, childY-1, child.width+2, child.height+2, 0x383023);
                    Raster.drawTransparentBox(childX, childY, child.width, child.height, child.color, child.transparency);
                } else if (child.type == 22) {
                    Raster.drawHorizontalLine(childX, childY, child.width, child.color);
                }else if (child.type == 26) {
                    Raster.drawTransparentBox(childX, childY, child.width, child.height, child.color, child.transparency);
                }


                else if (child.type == 4) {
                    TextDrawingArea textDrawingArea = child.textDrawingAreas;
                    String s = child.disabledMessage;
                    boolean flag1 = false;
                    if (anInt1039 == child.interfaceId || anInt1048 == child.interfaceId || anInt1026 == child.interfaceId)
                        flag1 = true;
                    int i4;
                    if (interfaceIsSelected(child)) {
                        i4 = child.anInt219;
                        if (flag1 && child.anInt239 != 0)
                            i4 = child.anInt239;
                        if (child.enabledMessage.length() > 0)
                            s = child.enabledMessage;
                   } else {
                        i4 = child.textColor;
                        if (flag1 && child.textHoverColor != 0) {
                            i4 = child.textHoverColor;
                            if (child.isAchievementText) {
                                achievementHovered = child;
                                if (lastAchievement != child) {
                                    UpdateAchievementHover();
                                }
                                lastAchievement = child;
                            }
                        }
                    }
                    if (child.atActionType == 6 && aBoolean1149) {
                        s = "Please wait...";
                        i4 = child.textColor;
                    }
                    if (child.parentID >= 40052 && child.parentID <= 40132) {
                        if (RSInterface.getInterfaceCache()[40051].invStackSizes[child.parentID - 40052] > 0) {
                            String[] data = RSInterface.getInterfaceCache()[child.parentID].disabledMessage.split(",");
                            int currency = 0;
                            if (data.length > 1) {
                                currency = Integer.parseInt(data[1]);
                            }
                            if (currency >= currencyImage.length)
                                currency = currencyImage.length - 1;

                            if (currencyImage[currency] == null) {
                                currencyImage[currency] = spriteCache.get(256 + currency);
                            }
                            int value = Integer.parseInt(data[0]);

                            if (value != 0 && value != 1) {
                                currencyImage[currency].drawSprite(childX - 5, childY);
                            }

                            if (value >= 10_000_000) {
                                smallFont.drawRightAlignedString((value / 1_000_000) + "M", childX + 36, childY + 1 + Raster.height, 0);
                                smallFont.drawRightAlignedString((value / 1_000_000) + "M", childX + 35, childY + Raster.height, 0xFFFF00);
                            } else if (value >= 1_000_000) {
                                smallFont.drawRightAlignedString(value / 1_000_000 + "M", childX + 36, childY + 1 + Raster.height, 0);
                                smallFont.drawRightAlignedString(value / 1_000_000 + "M", childX + 35, childY + Raster.height, 0xFFFF00);
                            } else if (value >= 100_000) {
                                smallFont.drawRightAlignedString((value / 1_000) + "K", childX + 36, childY + 1 + Raster.height, 0);
                                smallFont.drawRightAlignedString((value / 1_000) + "K", childX + 35, childY + Raster.height, 0xFFFF00);
                            } else if (value >= 10_000) {
                                smallFont.drawRightAlignedString((value / 1_000) + "K", childX + 36, childY + 1 + Raster.height, 0);
                                smallFont.drawRightAlignedString((value / 1_000) + "K", childX + 35, childY + Raster.height, 0xFFFF00);
                            } else if (value == -1 || value == 0) {
                                smallFont.drawRightAlignedString("FREE", childX + 34, childY + 1 + Raster.height, 0);
                                smallFont.drawRightAlignedString("FREE", childX + 33, childY + Raster.height, 0xFFFF00);
                            } else {
                                smallFont.drawRightAlignedString(value + "", childX + 36, childY + 1 + Raster.height, 0);
                                smallFont.drawRightAlignedString(value + "", childX + 35, childY + Raster.height, 0xFFFF00);
                            }
                        }
                        continue;
                    }
                    if (Raster.width == 519) {
                        if (i4 == 0xffff00)
                            i4 = 255;
                        if (i4 == 49152)
                            i4 = 0xffffff;
                    }
                    if (frameMode != ScreenMode.FIXED) {
                        if ((backDialogueId != -1 || dialogueId != -1 || child.disabledMessage.contains("Click here to continue")) && (rsi.interfaceId == backDialogueId || rsi.interfaceId == dialogueId)) {
                            if (i4 == 0xffff00) {
                                i4 = 255;
                            }
                            if (i4 == 49152) {
                                i4 = 0xffffff;
                            }
                        }
                    }
                    if ((child.parentID == 1151) || (child.parentID == 12855)) {
                        switch (i4) {
                            case 16773120:
                                i4 = 0xFE981F;
                                break;
                            case 7040819:
                                i4 = 0xAF6A1A;
                                break;
                        }
                    }
                    for (int l6 = childY + textDrawingArea.anInt1497; s.length() > 0; l6 += textDrawingArea.anInt1497) {
                        if (s.indexOf("%") != -1) {
                            do {
                                int k7 = s.indexOf("%1");
                                if (k7 == -1)
                                    break;
                                if (child.interfaceId < 4000 || child.interfaceId > 5000 && child.interfaceId != 13921 && child.interfaceId != 13922 && child.interfaceId != 12171 && child.interfaceId != 12172)
                                    s = s.substring(0, k7) + Utility.methodR(executeScript(child, 0)) + s.substring(k7 + 2);
                                else
                                    s = s.substring(0, k7) + interfaceIntToString(executeScript(child, 0)) + s.substring(k7 + 2);
                            } while (true);
                            do {
                                int l7 = s.indexOf("%2");
                                if (l7 == -1)
                                    break;
                                s = s.substring(0, l7) + interfaceIntToString(executeScript(child, 1)) + s.substring(l7 + 2);
                            } while (true);
                            do {
                                int i8 = s.indexOf("%3");
                                if (i8 == -1)
                                    break;
                                s = s.substring(0, i8) + interfaceIntToString(executeScript(child, 2)) + s.substring(i8 + 2);
                            } while (true);
                            do {
                                int j8 = s.indexOf("%4");
                                if (j8 == -1)
                                    break;
                                s = s.substring(0, j8) + interfaceIntToString(executeScript(child, 3)) + s.substring(j8 + 2);
                            } while (true);
                            do {
                                int k8 = s.indexOf("%5");
                                if (k8 == -1)
                                    break;
                                s = s.substring(0, k8) + interfaceIntToString(executeScript(child, 4)) + s.substring(k8 + 2);
                            } while (true);
                        }
                        int l8 = s.indexOf("\\n");
                        String s1;
                        if (l8 != -1) {
                            s1 = s.substring(0, l8);
                            s = s.substring(l8 + 2);
                        } else {
                            s1 = s;
                            s = "";
                        }
                        RSFont rsFont;
                        if (textDrawingArea == smallFont) {
                            rsFont = newSmallFont;
                        } else if (textDrawingArea == regularText) {
                            rsFont = newRegularFont;
                        } else if (textDrawingArea == boldText) {
                            rsFont = newBoldFont;
                        } else {
                            rsFont = newFancyFont;
                        }
                        if (child.centerText) {
                            rsFont.drawCenteredString(s1, childX + child.width / 2, l6, i4, child.textShadow ? 0 : -1);
                        } else {
                            rsFont.drawBasicString(s1, childX, l6, i4, child.textShadow ? 0 : -1);
                        }
                    }
                } else if (child.type == 5) {
                	if (prayerGrabbed != null && prayerGrabbed.spriteId == child.interfaceId) {
						continue;
					}
                    Sprite sprite;
                    if (interfaceIsSelected(child))
                        sprite = child.enabledSprite;
                    else
                        sprite = child.disabledSprite;
                    if (spellSelected == 1 && child.interfaceId == spellID && spellID != 0 && sprite != null) {
                        sprite.drawSprite(childX, childY, 0xffffff);
                    } else {
                        if (sprite != null)
                            if (child.drawsTransparent) {
                                sprite.drawTransparentSprite(childX, childY, alpha);
                            } else {
                                sprite.drawSprite(childX, childY);
                            }
                    }
                    if (autocast && child.interfaceId == autoCastId)
                        spriteCache.get(43).drawSprite(childX - 3, childY - 3);
                    if (sprite != null)
                        if (child.drawsTransparent) {
                            sprite.drawSprite1(childX, childY);
                        } else {
                            sprite.drawSprite(childX, childY);
                        }
                } else if (child.type == 6) {
                    int k3 = Rasterizer.textureInt1;
                    int j4 = Rasterizer.textureInt2;
                    Rasterizer.textureInt1 = childX + child.width / 2;
                    Rasterizer.textureInt2 = childY + child.height / 2;
                    int i5 = Rasterizer.anIntArray1470[child.modelRotation1] * child.modelZoom >> 16;
                    int l5 = Rasterizer.anIntArray1471[child.modelRotation1] * child.modelZoom >> 16;
                    boolean flag2 = interfaceIsSelected(child);
                    int i7;
                    if (flag2)
                        i7 = child.anInt258;
                    else
                        i7 = child.anInt257;
                    Model model;
                    if (i7 == -1) {
                        model = child.getModel(-1, -1, flag2);
                    } else {
                        Animation animation = Animation.animations[i7];
                        model = child.getModel(animation.secondaryFrames[child.anInt246], animation.primaryFrames[child.anInt246], flag2);
                    }
                    if (model != null)
                        model.method482(child.modelRotation2, 0, child.modelRotation1, 0, i5, l5);
                    Rasterizer.textureInt1 = k3;
                    Rasterizer.textureInt2 = j4;
                } else if (child.type == 7) {
                    TextDrawingArea textDrawingArea_1 = child.textDrawingAreas;
                    int k4 = 0;
                    for (int j5 = 0; j5 < child.height; j5++) {
                        for (int i6 = 0; i6 < child.width; i6++) {
                            if (child.inv[k4] > 0) {
                                ItemDefinition itemDef = ItemDefinition.lookup(child.inv[k4] - 1);
                                String s2 = itemDef.name;
                                if (itemDef.stackable || child.invStackSizes[k4] != 1)
                                    s2 = s2 + " itemX" + Utility.intToKOrMilLongName(child.invStackSizes[k4]);
                                int i9 = childX + i6 * (115 + child.invSpritePadX);
                                int k9 = childY + j5 * (12 + child.invSpritePadY);
                                if (child.centerText)
                                    textDrawingArea_1.drawCenteredText(child.textColor, i9 + child.width / 2, s2, k9, child.textShadow);
                                else
                                    textDrawingArea_1.drawText(child.textShadow, i9, child.textColor, s2, k9);
                            }
                            k4++;
                        }
                    }
                } else if (child.type == 9) {
                    drawHoverBox(childX, childY, child.popupString);
                } else if (child.type == 10) {
                    if (child.rounded) {
                        Raster.drawRoundedRectangle(childX, childY, child.width, child.height, child.color, -1, true, true);
                    } else {
                        Raster.fillRectangle(childX, childY, child.width, child.height, child.color);
                    }
                } else if (child.type == 11) {
                    Sprite sprite;
                    if (interfaceIsSelected(child))
                        sprite = child.enabledSprite;
                    else
                        sprite = child.disabledSprite;
                    if (sprite != null) {
                        sprite.draw24BitSprite(childX, childY);
                    }
                } else if (child.type == 13 && child.percentage != 65535) {
                    drawProgressBar(child, childX, childY);
                } else if (child.type == 16) {
                    drawInputField(child, x, y, childX, childY, child.width, child.height);
                } else if (child.type == 69) {
                    child.dropDown.drawDropdown(child, childX, childY);

                } else if (child.type == 8 && (anInt1500 == child.interfaceId || anInt1044 == child.interfaceId || anInt1129 == child.interfaceId) && anInt1501 == 0 && !menuOpen && prayerGrabbed == null) {
                    int boxWidth = 0;
                    int boxHeight = 0;

                    //                    if (child.hoverXOffset != 0) {
                    //                        x += child.hoverXOffset;
                    //                    }
                    //                    if (child.hoverYOffset != 0) {
                    //                        y += child.hoverYOffset;
                    //                    }
                    //                    if (child.regularHoverBox) {
                    //                        drawHoverBox(x, y, child.disabledMessage);
                    //                    }

                    TextDrawingArea textDrawingArea_2 = regularText;
                    for (String s1 = child.disabledMessage; s1.length() > 0; ) {
                        if (s1.indexOf("%") != -1) {
                            do {
                                int k7 = s1.indexOf("%1");
                                if (k7 == -1)
                                    break;
                                s1 = s1.substring(0, k7) + interfaceIntToString(executeScript(child, 0)) + s1.substring(k7 + 2);
                            } while (true);
                            do {
                                int l7 = s1.indexOf("%2");
                                if (l7 == -1)
                                    break;
                                s1 = s1.substring(0, l7) + interfaceIntToString(executeScript(child, 1)) + s1.substring(l7 + 2);
                            } while (true);
                            do {
                                int i8 = s1.indexOf("%3");
                                if (i8 == -1)
                                    break;
                                s1 = s1.substring(0, i8) + interfaceIntToString(executeScript(child, 2)) + s1.substring(i8 + 2);
                            } while (true);
                            do {
                                int j8 = s1.indexOf("%4");
                                if (j8 == -1)
                                    break;
                                s1 = s1.substring(0, j8) + interfaceIntToString(executeScript(child, 3)) + s1.substring(j8 + 2);
                            } while (true);
                            do {
                                int k8 = s1.indexOf("%5");
                                if (k8 == -1)
                                    break;
                                s1 = s1.substring(0, k8) + interfaceIntToString(executeScript(child, 4)) + s1.substring(k8 + 2);
                            } while (true);
                        }
                        int l7 = s1.indexOf("\\n");
                        String s4;
                        if (l7 != -1) {
                            s4 = s1.substring(0, l7);
                            s1 = s1.substring(l7 + 2);
                        } else {
                            s4 = s1;
                            s1 = "";
                        }
                        int j10 = textDrawingArea_2.getTextWidth(s4);
                        if (j10 > boxWidth) {
                            boxWidth = j10;
                        }
                        boxHeight += textDrawingArea_2.anInt1497 + 1;
                    }
                    boxWidth += 6;
                    boxHeight += 7;
                    int xPos = (childX + child.width) - 5 - boxWidth;
                    int yPos = childY + child.height + 5;
                    if (xPos < childX + 5) {
                        xPos = childX + 5;
                    }
                    if (xPos + boxWidth > x + rsi.width) {
                        xPos = (x + rsi.width) - boxWidth;
                    }
                    if (yPos + boxHeight > y + rsi.height) {
                        yPos = (childY - boxHeight);
                    }
                    if (rsi.tooltipBounds) {
                        if (frameMode == ScreenMode.FIXED) {
                            if (xPos + boxWidth + x + rsi.width > 765) {
                                xPos = 765 - boxWidth - x - rsi.width - 3;
                            }
                        } else {
                            if (xPos + boxWidth > frameWidth) {
                                xPos = frameWidth - boxWidth - 15;
                            }
                        }
                        if (yPos + boxHeight > frameHeight - (frameMode == ScreenMode.FIXED ? yPos + boxHeight - 118 : (frameWidth <= 1000 ? 75 : 35))) {
                            yPos -= boxHeight + 35;
                        }
                    }
                    Raster.fillRectangle(xPos, yPos, boxWidth, boxHeight, 0xFFFFA0);
                    Raster.drawRectangle(xPos, yPos, boxWidth, boxHeight, 0);
                    String s2 = child.disabledMessage;
                    for (int j11 = yPos + textDrawingArea_2.anInt1497 + 2; s2.length() > 0; j11 += textDrawingArea_2.anInt1497 + 1) {// anInt1497
                        if (s2.indexOf("%") != -1) {
                            do {
                                int k7 = s2.indexOf("%1");
                                if (k7 == -1)
                                    break;
                                s2 = s2.substring(0, k7) + interfaceIntToString(executeScript(child, 0)) + s2.substring(k7 + 2);
                            } while (true);
                            do {
                                int l7 = s2.indexOf("%2");
                                if (l7 == -1)
                                    break;
                                s2 = s2.substring(0, l7) + interfaceIntToString(executeScript(child, 1)) + s2.substring(l7 + 2);
                            } while (true);
                            do {
                                int i8 = s2.indexOf("%3");
                                if (i8 == -1)
                                    break;
                                s2 = s2.substring(0, i8) + interfaceIntToString(executeScript(child, 2)) + s2.substring(i8 + 2);
                            } while (true);
                            do {
                                int j8 = s2.indexOf("%4");
                                if (j8 == -1)
                                    break;
                                s2 = s2.substring(0, j8) + interfaceIntToString(executeScript(child, 3)) + s2.substring(j8 + 2);
                            } while (true);
                            do {
                                int k8 = s2.indexOf("%5");
                                if (k8 == -1)
                                    break;
                                s2 = s2.substring(0, k8) + interfaceIntToString(executeScript(child, 4)) + s2.substring(k8 + 2);
                            } while (true);
                        }
                        int l11 = s2.indexOf("\\n");
                        String s5;
                        if (l11 != -1) {
                            s5 = s2.substring(0, l11);
                            s2 = s2.substring(l11 + 2);
                        } else {
                            s5 = s2;
                            s2 = "";
                        }

                        if (child.centerText) {
                            textDrawingArea_2.drawCenteredText(yPos, xPos + child.width / 2, s5, j11, false);
                        } else {
                            if (s5.contains("\\r")) {
                                String text = s5.substring(0, s5.indexOf("\\r"));
                                String text2 = s5.substring(s5.indexOf("\\r") + 2);
                                textDrawingArea_2.drawText(false, xPos + 3, 0, text, j11);
                                int rightX = boxWidth + xPos - textDrawingArea_2.getTextWidth(text2) - 2;
                                textDrawingArea_2.drawText(false, rightX, 0, text2, j11);
                            } else
                                textDrawingArea_2.drawText(false, xPos + 3, 0, s5, j11);
                        }
                    }
                }
        }
        Raster.setDrawingArea(i1, j1, k1, l1);
    }

    public void drawHoverBox(int xPos, int yPos, String text) {
        String[] results = text.split("\n");
        int height = (results.length * 16) + 6;
        int width;
        width = smallFont.getTextWidth(results[0]) + 6;
        for (int i = 1; i < results.length; i++)
            if (width <= smallFont.getTextWidth(results[i]) + 6)
                width = smallFont.getTextWidth(results[i]) + 6;
        Raster.fillRectangle(xPos, yPos, width, height, 0xFFFFA0);
        Raster.drawRectangle(xPos, yPos, width, height, 0);
        yPos += 14;
        for (int i = 0; i < results.length; i++) {
            smallFont.drawText(false, xPos + 3, 0, results[i], yPos);
            yPos += 16;
        }
    }

    public static int percentageColor;
    public static int steps = 1;
    public static int fadingToColor;
    public static boolean switchColor = false;

    public static int fadeColors(Color color1, Color color2, float step) {
        float ratio = step / 100;
        int r = (int) (color2.getRed() * ratio + color1.getRed() * (1 - ratio));
        int g = (int) (color2.getGreen() * ratio + color1.getGreen() * (1 - ratio));
        int b = (int) (color2.getBlue() * ratio + color1.getBlue() * (1 - ratio));
        return new Color(r, g, b).getRGB();
    }

    private void appendPlayerUpdateMask(int mask, int playerIndex, Buffer buffer, Player player) {

        // force movement
        if ((mask & 0x400) != 0) {
            player.initialX = buffer.readUByteS();
            player.initialY = buffer.readUByteS();
            player.destinationX = buffer.readUByteS();
            player.destinationY = buffer.readUByteS();
            int temp = buffer.readLEUShortA();
            int temp2 = buffer.readUShortA();
            player.startForceMovement = temp + tick;
            player.endForceMovement = temp2 + tick;
            player.direction = buffer.readUByteS();
            player.resetPath();
        }

        if ((mask & 0x100) != 0) {
            player.graphic = buffer.readLEUShort();
            int info = buffer.readInt();
            player.graphicHeight = info >> 16;
            player.graphicDelay = tick + (info & 0xffff);
            player.currentAnimation = 0;
            player.anInt1522 = 0;
            if (player.graphicDelay > tick)
                player.currentAnimation = -1;
            if (player.graphic == 65535)
                player.graphic = -1;

            try {

                if (Frame.animationlist[Graphic.cache[player.graphic].aAnimation_407.primaryFrames[0] >> 16].length == 0) {
                    onDemandFetcher.provide(1, Graphic.cache[player.graphic].aAnimation_407.primaryFrames[0] >> 16);
                }

            } catch (Exception e) {
                //	e.printStackTrace();
            }

        }

        if ((mask & 0x8) != 0) {
            int animation = buffer.readLEUShort();
            if (animation == 65535) {
                animation = -1;
            }
            int delay = buffer.readNegUByte();
            if (animation == player.emoteAnimation && animation != -1) {
                int replayMode = Animation.animations[animation].replayMode;
                if (replayMode == 1) {
                    player.displayedEmoteFrames = 0;
                    player.emoteTimeRemaining = 0;
                    player.animationDelay = delay;
                    player.currentAnimationLoops = 0;
                }
                if (replayMode == 2) {
                    player.currentAnimationLoops = 0;
                }
            } else if (animation == -1 || player.emoteAnimation == -1 || Animation.animations[animation].forcedPriority >= Animation.animations[player.emoteAnimation].forcedPriority) {
                player.emoteAnimation = animation;
                player.displayedEmoteFrames = 0;
                player.emoteTimeRemaining = 0;
                player.animationDelay = delay;
                player.currentAnimationLoops = 0;
                player.anInt1542 = player.remainingPath;
            }
        }
        if ((mask & 0x4) != 0) {
            player.textSpoken = buffer.readString();
            if (player.textSpoken.charAt(0) == '~') {
                player.textSpoken = player.textSpoken.substring(1);
                pushMessage(player.textSpoken, 2, player.name, player.title, player.titleColor);
            } else if (player == localPlayer) {
                pushMessage(player.textSpoken, 2, player.name, player.title, player.titleColor);
            }
            player.textColor = 0;
            player.textEffect = 0;
            player.textCycle = 150;
        }

        if ((mask & 0x80) != 0) {
            int packed = buffer.readLEUShort();
            int privelage = buffer.readUByte();
            int offset = buffer.readNegUByte();
            int off = buffer.currentOffset;
            if (player.name != null && player.visible) {
                long name = StringUtils.longForName(player.name);
                boolean invalid = false;
                if (privelage <= 1) {
                    for (int index = 0; index < ignoreCount; index++) {
                        if (ignores[index] != name) {
                            continue;
                        }
                        invalid = true;
                        break;
                    }
                }

                if (!invalid && onTutorialIsland == 0)
                    try {
                        chatBuffer.currentOffset = 0;
                        buffer.readReverseData(offset, 0, chatBuffer.buffer);
                        chatBuffer.currentOffset = 0;
                        String message = TextInput.decode(offset, chatBuffer);
                        if (Settings.PROFANITY_FILTER) {
//                            message = Censor.doCensor(message);
                        }
                        player.textSpoken = message;
                        player.textColor = packed >> 8;
                        player.privelage = privelage;
                        player.textEffect = packed & 0xff;
                        player.textCycle = 150;
                        if (privelage >= 1) {
                            pushMessage(message, 1, "@cr" + privelage + "@" + player.name, player.title, player.titleColor);
                        } else {
                            pushMessage(message, 2, player.name, player.title, player.titleColor);
                        }
                    } catch (Exception ex) {
                        System.err.println(String.format("error receiving chat message: %s error=%s", message, ex.getMessage()));
                    }
            }

            buffer.currentOffset = off + offset;
        }

        if ((mask & 0x1) != 0) {
            int interacting = buffer.readLEUShort();
            player.interactingEntity = interacting;
            if (player.interactingEntity == 65535) {
                player.interactingEntity = -1;
            }
        }

        if ((mask & 0x10) != 0) {
            int length = buffer.readNegUByte();
            byte data[] = new byte[length];
            Buffer appearanceBuffer = new Buffer(data);
            buffer.readBytes(length, 0, data);
            playerSynchronizationBuffers[playerIndex] = appearanceBuffer;
            player.updateAppearance(appearanceBuffer);
        }

        if ((mask & 0x2) != 0) {
            player.faceX = buffer.readLEUShortA();
            player.faceY = buffer.readLEUShort();
        }

        if ((mask & 0x20) != 0) {
            int damage = buffer.readUByte();
            int type = buffer.readUByteA();
            int icon = buffer.readUByte();
            int current = buffer.readUByte();
            int max = buffer.readNegUByte();
            player.damage(type, damage, tick, icon);
            player.cycleStatus = tick + 300;
            player.currentHealth = current;
            player.maximumHealth = max;
        }

        if ((mask & 0x200) != 0) {
            int damage = buffer.readUByte();
            int type = buffer.readUByteS();
            int icon = buffer.readUByte();
            int current = buffer.readUByte();
            int max = buffer.readNegUByte();
            player.damage(type, damage, tick, icon);
            player.cycleStatus = tick + 300;
            player.currentHealth = current;
            player.maximumHealth = max;
        }
    }

    private void method108() {
        try {
            int j = localPlayer.x + anInt1278;
            int k = localPlayer.y + anInt1131;
            if (anInt1014 - j < -500 || anInt1014 - j > 500 || anInt1015 - k < -500 || anInt1015 - k > 500) {
                anInt1014 = j;
                anInt1015 = k;
            }
            if (anInt1014 != j)
                anInt1014 += (j - anInt1014) / 16;
            if (anInt1015 != k)
                anInt1015 += (k - anInt1015) / 16;
            if (super.keyArray[1] == 1)
                anInt1186 += (-24 - anInt1186) / 2;
            else if (super.keyArray[2] == 1)
                anInt1186 += (24 - anInt1186) / 2;
            else
                anInt1186 /= 2;
            if (super.keyArray[3] == 1)
                anInt1187 += (12 - anInt1187) / 2;
            else if (super.keyArray[4] == 1)
                anInt1187 += (-12 - anInt1187) / 2;
            else
                anInt1187 /= 2;
            minimapInt1 = minimapInt1 + anInt1186 / 2 & 0x7ff;
            anInt1184 += anInt1187 / 2;
            if (anInt1184 < 128)
                anInt1184 = 128;
            if (anInt1184 > 383)
                anInt1184 = 383;
            int l = anInt1014 >> 7;
            int i1 = anInt1015 >> 7;
            int j1 = method42(plane, anInt1015, anInt1014);
            int k1 = 0;
            if (l > 3 && i1 > 3 && l < 100 && i1 < 100) {
                for (int l1 = l - 4; l1 <= l + 4; l1++) {
                    for (int k2 = i1 - 4; k2 <= i1 + 4; k2++) {
                        int l2 = plane;
                        if (l2 < 3 && (byteGroundArray[1][l1][k2] & 2) == 2)
                            l2++;
                        int i3 = j1 - intGroundArray[l2][l1][k2];
                        if (i3 > k1)
                            k1 = i3;
                    }

                }

            }
            int j2 = k1 * 192;
            if (j2 > 0x17f00)
                j2 = 0x17f00;
            if (j2 < 32768)
                j2 = 32768;
            if (j2 > anInt984) {
                anInt984 += (j2 - anInt984) / 24;
                return;
            }
            if (j2 < anInt984) {
                anInt984 += (j2 - anInt984) / 80;
            }
        } catch (Exception _ex) {
            Utility.reporterror("glfc_ex " + localPlayer.x + "," + localPlayer.y + "," + anInt1014 + "," + anInt1015 + "," + anInt1069 + "," + anInt1070 + "," + baseX + "," + baseY);
            throw new RuntimeException("eek");
        }
    }

    public void processDrawing() {
        if (rsAlreadyLoaded || loadingError || genericLoadingError) {
            showErrorScreen();
            return;
        }

        if (Updater.isActive()) {
            return;
        }

        if (!loggedIn) {
            loginRenderer.display();
        } else {
            drawGameScreen();
        }
        anInt1213 = 0;
    }

    private boolean isFriendOrSelf(String s) {
        if (s == null)
            return false;
        for (int i = 0; i < friendsCount; i++)
            if (s.equalsIgnoreCase(friendsList[i]))
                return true;
        return s.equalsIgnoreCase(localPlayer.name);
    }

    private static String combatDiffColor(double i, double j) {
        int k = (int) (i - j);
        if (k < -9)
            return "<col=FF0000>";
        if (k < -6)
            return "<col=FF3000>";
        if (k < -3)
            return "<col=FF7000>";
        if (k < 0)
            return "<col=FFB000>";
        if (k > 9)
            return "<col=00FF00>";
        if (k > 6)
            return "<col=40FF00>";
        if (k > 3)
            return "<col=80FF00>";
        if (k > 0)
            return "<col=C0FF00>";
        else
            return "<col=FFFF00>";
    }

    private boolean displayCounter;

    private void draw3dScreen() {
        if (displayCounter && Settings.STATUS_ORB) {
            ExpCounter.drawExperienceCounter();
        }
        if (Settings.EXPERIENCE_ORBS) {
            SkillOrbs.process();
        }
        if (Settings.WIDGET) {
            Widget.draw();
        }
        if (Settings.ENTITY_FEED) {
            displayEntityFeed();
        }
        if (Settings.NOTIFICATION_FEED) {
            drawNotification();
        }
        if (Settings.DISPLAY_KILL_FEED) {
            displayKillFeed();
        }
        BannerManager.drawMovingBanner();
        fadingScreen.draw();
        // SkillOrbHandler.drawOrbs();

        if (showChatComponents) {
            drawSplitPrivateChat();
        }

        if (crossType == 1) {
            int offSet = frameMode == ScreenMode.FIXED ? 4 : 0;
            crosses[crossIndex / 100].drawSprite(crossX - 8 - offSet, crossY - 8 - offSet);
        }
        if (crossType == 2) {
            int offSet = frameMode == ScreenMode.FIXED ? 4 : 0;
            crosses[4 + crossIndex / 100].drawSprite(crossX - 8 - offSet, crossY - 8 - offSet);
        }
        if (anInt1018 != -1) {
            RSInterface rsInterface = RSInterface.getInterfaceCache()[anInt1018];
            method119(tickDelta, anInt1018);

            /** Custom placement. */
            if (anInt1018 == 11146 && frameMode != ScreenMode.FIXED) {
                drawInterface(0, 0, RSInterface.getInterfaceCache()[anInt1018], -5);

                //Bounty hunter
                //            } else if (anInt1018 == 23300) {
                //                drawInterface(0, frameWidth - rsInterface.width - 253, rsInterface, 0);
                //            } else if (anInt1018 == 23300 && frameMode == ScreenMode.RESIZABLE) {
                //                drawInterface(0, frameWidth / 2 - 780, RSInterface.interfaceCache[anInt1018], 80);

            } else if (anInt1018 == 197 && frameMode != ScreenMode.FIXED) {
                skullIcons[0].drawSprite(frameWidth - 157, 168);
                String text = RSInterface.getInterfaceCache()[199].disabledMessage;
                regularText.drawChatInput(0xE1981D, frameWidth - 165, text, 207, true);
            } else if (anInt1018 == 201 && frameMode != ScreenMode.FIXED) {
                drawInterface(RSInterface.getInterfaceCache()[anInt1018], frameWidth - 560, -109, 0);

                /* Custom */
            } else if (anInt1018 == 15269 && frameMode != ScreenMode.FIXED) {
                drawInterface(RSInterface.getInterfaceCache()[anInt1018], frameMode == ScreenMode.RESIZABLE ? frameWidth - 700 : frameWidth - 600, frameMode == ScreenMode.RESIZABLE ? -135 : -70, 0);
            } else if (anInt1018 == 15264 && frameMode != ScreenMode.FIXED) {
                drawInterface(RSInterface.getInterfaceCache()[anInt1018], frameMode == ScreenMode.RESIZABLE ? frameWidth - 700 : frameWidth - 600, frameMode == ScreenMode.RESIZABLE ? -135 : -70, 0);

            } else {
                drawInterface(RSInterface.getInterfaceCache()[anInt1018], frameMode == ScreenMode.FIXED ? 0 : (frameWidth / 2) - 356, frameMode == ScreenMode.FIXED ? 0 : (frameHeight / 2) - 230, 0);
            }
        }
        if (openInterfaceID == 60000) {
            method119(tickDelta, openInterfaceID);
            drawInterface(RSInterface.getInterfaceCache()[openInterfaceID], frameMode == ScreenMode.FIXED ? 0 : (frameWidth / 2) - 356, frameMode == ScreenMode.FIXED ? 0 : (frameHeight / 2) - 230, 0);
        } else if (openInterfaceID != -1 && openInterfaceID != 60000) {
            method119(tickDelta, openInterfaceID);
            int w = 512, h = 334;
            int x = frameMode == ScreenMode.FIXED ? 0 : (frameWidth / 2) - 256;
            int y = frameMode == ScreenMode.FIXED ? 0 : (frameHeight / 2) - 167;
            int count = !changeTabArea ? 4 : 3;
            if (frameMode != ScreenMode.FIXED) {
                for (int i = 0; i < count; i++) {
                    if (x + w > (frameWidth - 225)) {
                        x = x - 30;
                        if (x < 0) {
                            x = 0;
                        }
                    }
                    if (y + h > (frameHeight - 182)) {
                        y = y - 30;
                        if (y < 0) {
                            y = 0;
                        }
                    }
                }
            }
            drawInterface(RSInterface.getInterfaceCache()[openInterfaceID], x, y, 0);
        }

        if (Settings.DRAW_ITEM_HOVERS && itemHover > -1 && super.mouseX < getScreenWidth() - 300) {
            if (controlIsDown && ItemStats.itemstats[itemHover] != null && ItemStats.itemstats[itemHover].type == 1) {
                drawInterface(RSInterface.getInterfaceCache()[23000], getItemHoverX(false, 0, false), getItemHoverY(false, 0, false), 0);
            } else {
                drawInterface(RSInterface.getInterfaceCache()[23020], getItemHoverX(false, 0, true), getItemHoverY(false, 0, true), 0);
            }
        }

        method70();
        if (!menuOpen) {
            processRightClick();
            drawTooltip();
        } else if (menuScreenArea == 0) {
            drawMenu(frameMode == ScreenMode.FIXED ? 4 : 0, frameMode == ScreenMode.FIXED ? 4 : 0);
        }
        if (anInt1055 == 1) {
            multiOverlay.drawSprite(frameMode == ScreenMode.FIXED ? 485 : frameWidth - 85, frameMode == ScreenMode.FIXED ? 250 : 173);
        }

        if (openInterfaceID == -1 && (Settings.DISPLAY_PING || Settings.DISPLAY_FPS || Configuration.CLIENT_DATA)) {
            int x = frameWidth - (Configuration.DEBUG_MODE ? 760 : 315);
            int y = Configuration.DEBUG_MODE ? 100 :65;
            if (Settings.DISPLAY_FPS) {
                int rgb = 0xFFFF00;
                if (super.fps < 15) {
                    rgb = 0xff0000;
                }
                smallFont.render(rgb, "Fps:  " + super.fps, y, x);
                y += 18;

            }

            if (Settings.DISPLAY_PING) {
                int rgb = 0xFFFF00;
                if (ping >= 150) {
                    rgb = 0xff0000;
                }
                String pong = ping == -1 ? "0" : Long.toString(ping);
                smallFont.render(rgb, "Ping: " + pong + "ms", y, x);
            }

            if (Configuration.CLIENT_DATA) {
                y += 15;
                int playerX = baseX + (localPlayer.x - 6 >> 7);
                int playerY = baseY + (localPlayer.y - 6 >> 7);
                Runtime runtime = Runtime.getRuntime();
                int clientMemory = (int) ((runtime.totalMemory() - runtime.freeMemory()) / 1024L);
                smallFont.render(clientMemory > 300_000 ? 0xff0000 : 0xffff00, "Memory: " + NumberFormat.getInstance().format(clientMemory) + "k", y, x);
                smallFont.render(0xffff00, "Coords: " + playerX + ", " + playerY, y + 15, x);
                smallFont.render(0xffff00, "Resolution: " + frameWidth + "x" + frameHeight, y + 30, x);
                smallFont.render(0xffff00, "Build: " + Configuration.GAME_VERSION, y + 45, x);
                smallFont.render(0xffff00, "World: " + Configuration.CONNECTION.name, y + 60, x);
            }
        }

        if (openInterfaceID == -1 && game_message_time > 0) {
            int yOffset = frameMode == ScreenMode.FIXED ? 0 : frameHeight - 498;
            int yPos = 35;

            if (game_message_id == 0) {// Update
                newRegularFont.drawBasicString(game_message_context + " " + Utility.getFormattedTime(game_message_time), 4, yPos, 0xffff00, 50);
            } else if (game_message_id == 1) {// announcement
                newRegularFont.drawBasicString(game_message_context, 4, yPos + yOffset, 0xffff00, 50);
            } else if (game_message_id == 2) {// icon
                newRegularFont.drawBasicString("<img=14> " + game_message_context, 4, yPos + yOffset, 0xffff00, 50);
            } else if (game_message_id == 3) {// icon with time
                newRegularFont.drawBasicString("<img=14> " + game_message_context + " " + Utility.getFormattedTime(game_message_time), 4, yPos + yOffset, 0xffff00, 50);
            }

            game_message_time--;
        }
	    achievementHovered = null;

    }

    private int game_message_id;
    private int game_message_time;
    private String game_message_context;

    private void addIgnore(long usernameLong) {
        try {
            if (usernameLong == 0L) {
                return;
            }

            if (ignoreCount >= 100) {
                pushMessage("Your ignore list is full. Max of 100 hit", 0, "");
                return;
            }

            String username = StringUtils.fixName(StringUtils.nameForLong(usernameLong));

            if (username.equalsIgnoreCase(this.myUsername)) {
                return;
            }

            for (int count = 0; count < ignoreCount; count++) {
                if (ignores[count] == usernameLong) {
                    pushMessage(username + " is already on your ignore list", 0, "");
                    return;
                }
            }

            for (int count = 0; count < friendsCount; count++) {
                if (friendsListAsLongs[count] == usernameLong) {
                    pushMessage("Please remove " + username + " from your friend list first", 0, "");
                    return;
                }
            }

            ignores[ignoreCount++] = usernameLong;
            outgoing.writeOpcode(133);
            outgoing.writeQWord(usernameLong);
            return;
        } catch (RuntimeException runtimeexception) {
            Utility.reporterror("error adding ignore, " + usernameLong + ", " + 4 + ", " + runtimeexception.toString());
        }
        throw new RuntimeException();
    }

    private void method114() {
        for (int i = -1; i < playerCount; i++) {
            int j;
            if (i == -1)
                j = myPlayerIndex;
            else
                j = playerIndices[i];
            Player player = playerArray[j];
            if (player != null)
                processMovement(player);
        }

    }

    private void method115() {
        if (loadingStage == 2) {
            for (SpawnedObject class30_sub1 = (SpawnedObject) aClass19_1179.reverseGetFirst(); class30_sub1 != null; class30_sub1 = (SpawnedObject) aClass19_1179.reverseGetNext()) {
                if (class30_sub1.anInt1294 > 0)
                    class30_sub1.anInt1294--;
                if (class30_sub1.anInt1294 == 0) {
                    if (class30_sub1.anInt1299 < 0 || ObjectManager.method178(class30_sub1.anInt1299, class30_sub1.anInt1301)) {
                        method142(class30_sub1.anInt1298, class30_sub1.anInt1295, class30_sub1.anInt1300, class30_sub1.anInt1301, class30_sub1.anInt1297, class30_sub1.anInt1296, class30_sub1.anInt1299);
                        class30_sub1.unlink();
                    }
                } else {
                    if (class30_sub1.anInt1302 > 0)
                        class30_sub1.anInt1302--;
                    if (class30_sub1.anInt1302 == 0 && class30_sub1.anInt1297 >= 1 && class30_sub1.anInt1298 >= 1 && class30_sub1.anInt1297 <= 102 && class30_sub1.anInt1298 <= 102 && (class30_sub1.anInt1291 < 0 || ObjectManager.method178(class30_sub1.anInt1291, class30_sub1.anInt1293))) {
                        method142(class30_sub1.anInt1298, class30_sub1.anInt1295, class30_sub1.anInt1292, class30_sub1.anInt1293, class30_sub1.anInt1297, class30_sub1.anInt1296, class30_sub1.anInt1291);
                        class30_sub1.anInt1302 = -1;
                        if (class30_sub1.anInt1291 == class30_sub1.anInt1299 && class30_sub1.anInt1299 == -1)
                            class30_sub1.unlink();
                        else if (class30_sub1.anInt1291 == class30_sub1.anInt1299 && class30_sub1.anInt1292 == class30_sub1.anInt1300 && class30_sub1.anInt1293 == class30_sub1.anInt1301)
                            class30_sub1.unlink();
                    }
                }
            }

        }
    }

    private void determineMenuSize() {
        int boxLength = newBoldFont.getTextWidth("Choose option");
        for (int row = 0; row < menuActionRow; row++) {
            int actionLength = newBoldFont.getTextWidth(menuActionName[row]);
            if (actionLength > boxLength) {
                boxLength = actionLength;
            }
        }
        boxLength += 8;
        int offset = 15 * menuActionRow + 21;
        if (isMouseWithin(1, frameWidth - 1, 1, frameHeight - 1)) {
            int xClick = super.lastClickX - boxLength / 2;
            if (xClick + boxLength > frameWidth - 4) {
                xClick = frameWidth - 4 - boxLength;
            }
            if (xClick < 0) {
                xClick = 0;
            }
            int yClick = super.lastClickY - 0;
            if (yClick + offset > frameHeight - 6) {
                yClick = frameHeight - 6 - offset;
            }
            if (yClick < 0) {
                yClick = 0;
            }
            menuOpen = true;

            if (removeShiftDropOnMenuOpen && menuActionCmd3[menuActionRow - 1] == 3214) {
                removeShiftDropOnMenuOpen = false;
                processRightClick();
            }
            menuOffsetX = xClick;
            menuOffsetY = yClick;
            menuWidth = boxLength;
            menuHeight = 15 * menuActionRow + 22;
        }
    }

    private void updateLocalPlayerMovement(Buffer buffer) {
        buffer.initBitAccess();

        final int update = buffer.readBits(1);

        if (update == 0) {
            return;
        }

        final int type = buffer.readBits(2);

        if (type == 0) {
            mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = myPlayerIndex;
            return;
        }
        if (type == 1) {
            final int direction = buffer.readBits(3);
            localPlayer.moveInDir(false, direction);

            final int updateRequired = buffer.readBits(1);
            if (updateRequired == 1) {
                mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = myPlayerIndex;
            }
            return;
        }
        if (type == 2) {
            final int firstDirection = buffer.readBits(3);
            localPlayer.moveInDir(true, firstDirection);

            final int secondDirection = buffer.readBits(3);
            localPlayer.moveInDir(true, secondDirection);

            final int updateRequired = buffer.readBits(1);

            if (updateRequired == 1) {
                mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = myPlayerIndex;
            }
            return;
        }
        if (type == 3) {
            plane = buffer.readBits(2);
            final int teleport = buffer.readBits(1);
            final int updateRequired = buffer.readBits(1);

            if (updateRequired == 1) {
                mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = myPlayerIndex;
            }
            final int y = buffer.readBits(7);
            final int x = buffer.readBits(7);
            localPlayer.setPos(x, y, teleport == 1);
        }
    }

    private boolean method119(int i, int j) {
        boolean flag1 = false;
        RSInterface class9 = RSInterface.getInterfaceCache()[j];
        for (int k = 0; k < class9.children.length; k++) {
            if (class9.children[k] == -1)
                break;
            RSInterface class9_1 = RSInterface.getInterfaceCache()[class9.children[k]];
            if (class9_1.type == 1)
                flag1 |= method119(i, class9_1.interfaceId);
            if (class9_1.type == 6 && (class9_1.anInt257 != -1 || class9_1.anInt258 != -1)) {
                boolean flag2 = interfaceIsSelected(class9_1);
                int l;
                if (flag2)
                    l = class9_1.anInt258;
                else
                    l = class9_1.anInt257;
                if (l != -1) {
                    Animation animation = Animation.animations[l];
                    for (class9_1.anInt208 += i; class9_1.anInt208 > animation.method258(class9_1.anInt246); ) {
                        class9_1.anInt208 -= animation.method258(class9_1.anInt246) + 1;
                        class9_1.anInt246++;
                        if (class9_1.anInt246 >= animation.frameCount) {
                            class9_1.anInt246 -= animation.loopOffset;
                            if (class9_1.anInt246 < 0 || class9_1.anInt246 >= animation.frameCount)
                                class9_1.anInt246 = 0;
                        }
                        flag1 = true;
                    }

                }
            }
        }

        return flag1;
    }

    private int method120() {
        if (!Settings.ROOF) {
            return plane;
        }
        int j = 3;
        if (yCameraCurve < 310) {
            int k = xCameraPos >> 7;
            int l = yCameraPos >> 7;
            int i1 = localPlayer.x >> 7;
            int j1 = localPlayer.y >> 7;
            if ((byteGroundArray[plane][k][l] & 4) != 0)
                j = plane;
            int k1;
            if (i1 > k)
                k1 = i1 - k;
            else
                k1 = k - i1;
            int l1;
            if (j1 > l)
                l1 = j1 - l;
            else
                l1 = l - j1;
            if (k1 > l1) {
                int i2 = (l1 * 0x10000) / k1;
                int k2 = 32768;
                while (k != i1) {
                    if (k < i1)
                        k++;
                    else if (k > i1)
                        k--;
                    if ((byteGroundArray[plane][k][l] & 4) != 0)
                        j = plane;
                    k2 += i2;
                    if (k2 >= 0x10000) {
                        k2 -= 0x10000;
                        if (l < j1)
                            l++;
                        else if (l > j1)
                            l--;
                        if ((byteGroundArray[plane][k][l] & 4) != 0)
                            j = plane;
                    }
                }
            } else {
                if (l1 == 0) {
                    l1 = 1;
                }
                int j2 = (k1 * 0x10000) / l1;
                int l2 = 32768;
                while (l != j1) {
                    if (l < j1)
                        l++;
                    else if (l > j1)
                        l--;
                    if ((byteGroundArray[plane][k][l] & 4) != 0)
                        j = plane;
                    l2 += j2;
                    if (l2 >= 0x10000) {
                        l2 -= 0x10000;
                        if (k < i1)
                            k++;
                        else if (k > i1)
                            k--;
                        if ((byteGroundArray[plane][k][l] & 4) != 0)
                            j = plane;
                    }
                }
            }
        }
        if ((byteGroundArray[plane][localPlayer.x >> 7][localPlayer.y >> 7] & 4) != 0)
            j = plane;
        return j;
    }

    private int method121() {
        if (!Settings.ROOF) {
            return plane;
        }
        int j = method42(plane, yCameraPos, xCameraPos);
        if (j - zCameraPos < 800 && (byteGroundArray[plane][xCameraPos >> 7][yCameraPos >> 7] & 4) != 0)
            return plane;
        else
            return 3;
    }

    private void delIgnore(long l) {
        try {
            if (l == 0L)
                return;
            for (int j = 0; j < ignoreCount; j++)
                if (ignores[j] == l) {
                    ignoreCount--;
                    System.arraycopy(ignores, j + 1, ignores, j, ignoreCount - j);

                    outgoing.writeOpcode(74);
                    outgoing.writeQWord(l);
                    return;
                }

            return;
        } catch (RuntimeException runtimeexception) {
            Utility.reporterror("47229, " + 3 + ", " + l + ", " + runtimeexception.toString());
        }
        throw new RuntimeException();
    }

    private void chatJoin(long l) {
        try {
            if (l == 0L)
                return;
            outgoing.writeOpcode(60);
            outgoing.writeQWord(l);
            return;
        } catch (RuntimeException runtimeexception) {
            Utility.reporterror("47229, " + 3 + ", " + l + ", " + runtimeexception.toString());
        }
        throw new RuntimeException();

    }

    public String getParameter(String s) {
        return super.getParameter(s);
    }

    private int executeScript(RSInterface class9, int j) {
        if (class9.scripts == null || j >= class9.scripts.length)
            return -2;
        try {
            int ai[] = class9.scripts[j];
            int k = 0;
            int l = 0;
            int i1 = 0;
            do {
                int j1 = ai[l++];
                double k1 = 0;
                byte byte0 = 0;
                if (j1 == 0) {
                    ai = null;
                    return k;
                }
                if (j1 == 1)
                    k1 = currentStats[ai[l++]];
                if (j1 == 2)
                    k1 = maxStats[ai[l++]];
                if (j1 == 3)
                    k1 = currentExp[ai[l++]];
                if (j1 == 4) {
                    RSInterface class9_1 = RSInterface.getInterfaceCache()[ai[l++]];
                    int k2 = ai[l++];
                    if (k2 >= 0 && k2 < ItemDefinition.totalItems && (!ItemDefinition.lookup(k2).membersObject || isMembers)) {
                        for (int j3 = 0; j3 < class9_1.inv.length; j3++)
                            if (class9_1.inv[j3] == k2 + 1)
                                k1 += class9_1.invStackSizes[j3];

                    }
                }
                if (j1 == 5)
                    k1 = settings[ai[l++]];
                if (j1 == 6)
                    k1 = anIntArray1019[maxStats[ai[l++]] - 1];
                if (j1 == 7)
                    k1 = (settings[ai[l++]] * 100) / 46875;
                if (j1 == 8)
                    k1 = localPlayer.combatLevel;
                if (j1 == 9) {

                    for (int index = 0; index < SkillConstants.SKILL_COUNT; index++)
                        if (SkillConstants.SKILLS_ENABLED[index])
                            k1 += maxStats[index];
                }
                if (j1 == 10) {
                    RSInterface class9_2 = RSInterface.getInterfaceCache()[ai[l++]];
                    int l2 = ai[l++] + 1;
                    if (l2 >= 0 && l2 < ItemDefinition.totalItems && isMembers) {
                        for (int k3 = 0; k3 < class9_2.inv.length; k3++) {
                            if (class9_2.inv[k3] != l2)
                                continue;
                            k1 = 0x3b9ac9ff;
                            break;
                        }

                    }
                }
                if (j1 == 11)
                    k1 = energy;
                if (j1 == 12)
                    k1 = weight;
                if (j1 == 13) {
                    int i2 = settings[ai[l++]];
                    int i3 = ai[l++];
                    k1 = (i2 & 1 << i3) == 0 ? 0 : 1;
                }
                if (j1 == 14) {
                    int j2 = ai[l++];
                    Varbit varBit = Varbit.cache[j2];
                    int l3 = varBit.configId;
                    int i4 = varBit.lsb;
                    int j4 = varBit.msb;
                    int k4 = BIT_MASKS[j4 - i4];
                    k1 = settings[l3] >> i4 & k4;
                }
                if (j1 == 15)
                    byte0 = 1;
                if (j1 == 16)
                    byte0 = 2;
                if (j1 == 17)
                    byte0 = 3;
                if (j1 == 18)
                    k1 = (localPlayer.x >> 7) + baseX;
                if (j1 == 19)
                    k1 = (localPlayer.y >> 7) + baseY;
                if (j1 == 20)
                    k1 = ai[l++];
                if (j1 == 21)
                    k1 = tabAmounts[ai[l++]];
                if (j1 == 22) {
                    RSInterface class9_1 = RSInterface.getInterfaceCache()[ai[l++]];
                    int initAmount = class9_1.inv.length;
                    for (int j3 = 0; j3 < class9_1.inv.length; j3++) {
                        if (class9_1.inv[j3] <= 0) {
                            initAmount--;
                        }
                    }
                    k1 += initAmount;
                }
                if (byte0 == 0) {
                    if (i1 == 0)
                        k += k1;
                    if (i1 == 1)
                        k -= k1;
                    if (i1 == 2 && k1 != 0)
                        k /= k1;
                    if (i1 == 3)
                        k *= k1;
                    i1 = 0;
                } else {
                    i1 = byte0;
                }
            } while (true);
        } catch (Exception _ex) {
            return -1;
        }
    }

    private void drawTooltip() {
        if (menuActionRow < 2 && itemSelected == 0 && spellSelected == 0) {
            return;
        }

        String s;

        if (itemSelected == 1 && menuActionRow < 2) {
            s = "Use " + selectedItemName + " with...";
        } else if (spellSelected == 1 && menuActionRow < 2) {
            s = spellTooltip + "...";
        } else {
            s = menuActionName[menuActionRow - 1];
        }

        if (!s.equals("Walk here") && openInterfaceID == -1) {
             drawTooltip(s, true);
        }

        if (menuActionRow > 2) {
            s = s + "<col=FFFFFF> / " + (menuActionRow - 2) + " more options";
        }

        newBoldFont.drawBasicString("<col=ffffff>" + s, 4, 15, 0x999999, 0);
    }

    private int tooltipHoverX;
    private int tooltipHoverY;
    private String toolTipText;

    public void drawTooltip(String tooltip, boolean game) {
        if (game) {
            buildTooltip(tooltip, tooltipHoverX, tooltipHoverY, newRegularFont, 0x383023, 0x473F35, 0xFFFFFF, 195);
        } else {
            buildTooltip(tooltip, tooltipHoverX, tooltipHoverY, newBoldFont, 0x960503, 0x08090A, 0xC8C8C8, 150);
        }
    }

    private void buildTooltip(String tooltip, int x, int y, RSFont font, int outline, int fill, int text, int alpha) {
        toolTipText = tooltip;
        int width = 0;
        int height = 0;
        int boundsX = super.mouseX;
        int boundsY = super.mouseY - 10;
        for (String line = toolTipText; line.length() > 0; ) {
            int length = line.indexOf("\\n");
            String newLine;
            if (length != -1) {
                newLine = line.substring(0, length);
                line = line.substring(length + 2);
            } else {
                newLine = line;
                line = "";
            }
            int newWidth = font.getTextWidth(newLine);
            if (newWidth > width) {
                width = newWidth;
            }
            height += font.baseCharacterHeight + 2;
        }
        width += 8;
        height += 9;
        if (boundsX < frameWidth && boundsY < frameHeight) {
            if (boundsX + width + 3 > frameWidth) {
                boundsX = frameWidth - width - 3;
            }
            if (boundsY + height + 3 > frameHeight) {
                boundsY = frameHeight - height - 3;
            }
            tooltipHoverX = boundsX;
            tooltipHoverY = boundsY;
        }
        Raster.drawRectangle(x + 1, y + 1, width, height - 2, outline);
        Raster.fillRectangle(x, y, width, height, fill, alpha);
        for (int newY = y + font.baseCharacterHeight + 4; toolTipText.length() > 0; newY += font.baseCharacterHeight + 2) {
            int length = toolTipText.indexOf("\\n");
            if (length != -1) {
                tooltip = toolTipText.substring(0, length);
                toolTipText = toolTipText.substring(length + 2);
            } else {
                tooltip = toolTipText;
                toolTipText = "";
            }
            font.drawBasicString(tooltip, x + 5, newY, text, 0);
        }
    }

    private void markMinimap(Sprite sprite, int x, int y) {
        if (sprite == null) {
            return;
        }
        int k = minimapInt1 + minimapInt2 & 0x7ff;
        int l = x * x + y * y;
        if (l > 6400) {
            return;
        }
        int i1 = Model.modelIntArray1[k];
        int j1 = Model.modelIntArray2[k];
        i1 = (i1 * 256) / (minimapInt3 + 256);
        j1 = (j1 * 256) / (minimapInt3 + 256);
        int k1 = y * i1 + x * j1 >> 16;
        int l1 = y * j1 - x * i1 >> 16;
        if (frameMode == ScreenMode.FIXED) {
            sprite.drawSprite(((94 + k1) - sprite.resizeWidth / 2) + 4 + 30, 83 - l1 - sprite.resizeHeight / 2 - 4 + 5);
        } else {
            sprite.drawSprite(((77 + k1) - sprite.resizeWidth / 2) + 4 + (frameWidth - 167), 85 - l1 - sprite.resizeHeight / 2 - 4);
        }
    }

    private void drawMinimap() {
        if (frameMode == ScreenMode.FIXED) {
            mapIP.initDrawingArea();
        }

        int frame = getCurrentGameFrame();

        int compassXOffset = 0;
        int compassYOffset = 0;

        switch (frame) {

            case 459:
                compassXOffset = frameMode == ScreenMode.FIXED ? 4 : 0;
                compassYOffset = frameMode == ScreenMode.FIXED ? -2 : 0;
                break;

            default:
                compassXOffset = compassYOffset = 0;
                break;
        }

        if (minimapState == 2) {
            if (frameMode == ScreenMode.FIXED) {
                spriteCache.get(279).drawSprite(0, 0);
            } else {
                spriteCache.get(44).drawSprite(frameWidth - 181, 0);
                spriteCache.get(45).drawSprite(frameWidth - 158, 7);
            }
            if (Settings.STATUS_ORB) {
                loadAllOrbs(frameMode == ScreenMode.FIXED ? 0 : frameWidth - 217);

                if (Settings.SPECIAL_ATTACK_ORB) {
                    loadSpecialOrb(frameMode == ScreenMode.FIXED ? 0 : frameWidth - 217);
                }
            }

            if (frameMode == ScreenMode.FIXED ? super.mouseX >= 742 && super.mouseX <= 765 && super.mouseY >= 0 && super.mouseY <= 24 : super.mouseX >= frameWidth - 26 && super.mouseX <= frameWidth - 1 && super.mouseY >= 2 && super.mouseY <= 24) {
                spriteCache.get(280).drawARGBSprite(frameMode == ScreenMode.FIXED ? 226 : frameWidth - 23, 0, 205);
            } else {
                spriteCache.get(280).drawSprite(frameMode == ScreenMode.FIXED ? 226 : frameWidth - 23, 0);
            }
            if (tabID == 14) {
                spriteCache.get(281).drawSprite(frameMode == ScreenMode.FIXED ? 226 : frameWidth - 23, 0);
            }

            compass.method352(33, minimapInt1, anIntArray1057, 256, anIntArray968, (frameMode == ScreenMode.FIXED ? 25 + compassYOffset : 24 + compassYOffset), 4, (frameMode == ScreenMode.FIXED ? 29 + compassXOffset : frameWidth - 176 + compassXOffset), 33, 25);

            if (menuOpen) {
                drawMenu(frameMode == ScreenMode.FIXED ? 516 : 0, 0);
            }
            if (frameMode == ScreenMode.FIXED) {
                mapIP.initDrawingArea();
            }
            return;
        }
        int i = minimapInt1 + minimapInt2 & 0x7ff;
        int j = 48 + localPlayer.x / 32;
        int l2 = 464 - localPlayer.y / 32;
        minimapImage.method352(151, i, anIntArray1229, 256 + minimapInt3, anIntArray1052, l2, (frameMode == ScreenMode.FIXED ? 9 : 7), (frameMode == ScreenMode.FIXED ? 54 : frameWidth - 158), 146, j);
        for (int j5 = 0; j5 < anInt1071; j5++) {
            int k = (anIntArray1072[j5] * 4 + 2) - localPlayer.x / 32;
            int i3 = (anIntArray1073[j5] * 4 + 2) - localPlayer.y / 32;
            markMinimap(aClass30_Sub2_Sub1_Sub1Array1140[j5], k, i3);
        }
        for (int k5 = 0; k5 < 104; k5++) {
            for (int l5 = 0; l5 < 104; l5++) {
                Deque class19 = groundArray[plane][k5][l5];
                if (class19 != null) {
                    int l = (k5 * 4 + 2) - localPlayer.x / 32;
                    int j3 = (l5 * 4 + 2) - localPlayer.y / 32;
                    markMinimap(mapDotItem, l, j3);
                }
            }
        }
        for (int i6 = 0; i6 < npcCount; i6++) {
            Npc npc = npcs[npcIndices[i6]];
            if (npc != null && npc.isVisible()) {
                NpcDefinition entityDef = npc.definition;
                if (entityDef.morphisms != null) {
                    entityDef = entityDef.morph();
                }
                if (entityDef != null && entityDef.aBoolean87 && entityDef.aBoolean84) {
                    int i1 = npc.x / 32 - localPlayer.x / 32;
                    int k3 = npc.y / 32 - localPlayer.y / 32;
                    markMinimap(mapDotNPC, i1, k3);
                }
            }
        }
        String clanChannel = localPlayer.clanChannel;
        for (int j6 = 0; j6 < playerCount; j6++) {
            Player player = playerArray[playerIndices[j6]];
            if (player != null && player.isVisible()) {
                int x = player.x / 32 - localPlayer.x / 32;
                int y = player.y / 32 - localPlayer.y / 32;
                boolean friend = false;
                boolean clan = false;
                if (player.clanChannel != null && !player.clanChannel.equals("") && player.clanChannel.equalsIgnoreCase(clanChannel)) {
                    clan = true;
                }
                long l6 = StringUtils.longForName(player.name);
                for (int k6 = 0; k6 < friendsCount; k6++) {
                    if (l6 != friendsListAsLongs[k6] || friendsNodeIDs[k6] == 0) {
                        continue;
                    }
                    friend = true;
                    break;
                }
                boolean team = false;
                if (localPlayer.team != 0 && player.team != 0 && localPlayer.team == player.team) {
                    team = true;
                }
                if (friend) {
                    markMinimap(mapDotFriend, x, y);
                } else if (clan) {
                    markMinimap(mapDotClan, x, y);
                } else if (team) {
                    markMinimap(mapDotTeam, x, y);
                } else if (player.privelage != 0 && Settings.MINIMAP_RANK) {
                    markMinimap(modIcons[player.privelage - 1], x, y);
                } else {
                    markMinimap(mapDotPlayer, x, y);
                }
            }
        }
        if (anInt855 != 0 && tick % 20 < 10) {
            if (anInt855 == 1 && anInt1222 >= 0 && anInt1222 < npcs.length) {
                Npc class30_sub2_sub4_sub1_sub1_1 = npcs[anInt1222];
                if (class30_sub2_sub4_sub1_sub1_1 != null) {
                    int k1 = class30_sub2_sub4_sub1_sub1_1.x / 32 - localPlayer.x / 32;
                    int i4 = class30_sub2_sub4_sub1_sub1_1.y / 32 - localPlayer.y / 32;
                    method81(mapMarker, i4, k1);
                }
            }
            if (anInt855 == 2) {
                int l1 = ((anInt934 - baseX) * 4 + 2) - localPlayer.x / 32;
                int j4 = ((anInt935 - baseY) * 4 + 2) - localPlayer.y / 32;
                method81(mapMarker, j4, l1);
            }
            if (anInt855 == 10 && anInt933 >= 0 && anInt933 < playerArray.length) {
                Player class30_sub2_sub4_sub1_sub2_1 = playerArray[anInt933];
                if (class30_sub2_sub4_sub1_sub2_1 != null) {
                    int i2 = class30_sub2_sub4_sub1_sub2_1.x / 32 - localPlayer.x / 32;
                    int k4 = class30_sub2_sub4_sub1_sub2_1.y / 32 - localPlayer.y / 32;
                    method81(mapMarker, k4, i2);
                }
            }
        }
        if (destX != 0) {
            int j2 = (destX * 4 + 2) - localPlayer.x / 32;
            int l4 = (destY * 4 + 2) - localPlayer.y / 32;
            markMinimap(mapFlag, j2, l4);
        }
        Raster.fillRectangle((frameMode == ScreenMode.FIXED ? 127 : frameWidth - 88), (frameMode == ScreenMode.FIXED ? 83 : 80), 3, 3, 0xffffff);
        if (frameMode == ScreenMode.FIXED) {
            mapSprite.drawSprite(0, 0);
        } else {
            spriteCache.get(44).drawSprite(frameWidth - 181, 0);
        }

        compass.method352(33, minimapInt1, anIntArray1057, 256, anIntArray968, (frameMode == ScreenMode.FIXED ? 25 + compassYOffset : 24 + compassYOffset), 4, (frameMode == ScreenMode.FIXED ? 29 + compassXOffset : frameWidth - 176 + compassXOffset), 33, 25);

        if (Settings.STATUS_ORB) {
            loadAllOrbs(frameMode == ScreenMode.FIXED ? 0 : frameWidth - 217);
            if (Settings.SPECIAL_ATTACK_ORB) {
                loadSpecialOrb(frameMode == ScreenMode.FIXED ? 0 : frameWidth - 217);
            }
        }

        if (frameMode == ScreenMode.FIXED ? super.mouseX >= 742 && super.mouseX <= 765 && super.mouseY >= 0 && super.mouseY <= 24 : super.mouseX >= frameWidth - 26 && super.mouseX <= frameWidth - 1 && super.mouseY >= 2 && super.mouseY <= 24) {
            spriteCache.get(280).drawARGBSprite(frameMode == ScreenMode.FIXED ? 226 : frameWidth - 23, 0, 205);
        } else {
            spriteCache.get(280).drawSprite(frameMode == ScreenMode.FIXED ? 226 : frameWidth - 23, 0);
        }
        if (tabID == 14) {
            spriteCache.get(281).drawSprite(frameMode == ScreenMode.FIXED ? 226 : frameWidth - 23, 0);
        }

        if (menuOpen) {
            drawMenu(frameMode == ScreenMode.FIXED ? 516 : 0, 0);
        }
        if (frameMode == ScreenMode.FIXED) {
            gameScreenIP.initDrawingArea();
        }
    }

    public void tabToReplyPm() {
        String name = null;
        for (int k = 0; k < 100; k++) {
            if (chatMessages[k] == null) {
                continue;
            }
            int l = chatTypes[k];
            if (l == 3 || l == 7) {
                name = chatNames[k];
                break;
            }
        }

        if (name == null) {
            pushMessage("You haven't received any messages to which you can reply.", 0, "");
            return;
        }

        long nameAsLong = StringUtils.longForName(name.trim());
        int k3 = -1;
        for (int i4 = 0; i4 < friendsCount; i4++) {
            if (friendsListAsLongs[i4] != nameAsLong)
                continue;
            k3 = i4;
            break;
        }

        if (k3 != -1) {
            if (friendsNodeIDs[k3] > 0) {
                redrawDialogueBox = true;
                inputDialogState = 0;
                messagePromptRaised = true;
                promptInput = "";
                friendsListAction = 3;
                aLong953 = friendsListAsLongs[k3];
                aString1121 = "Enter message to send to " + friendsList[k3];
            } else {
                pushMessage("That player is currently offline.", 0, "");
            }
        }
    }

    public final int[] calcParticlePos(int x, int y, int z, int width, int height) {
        if (x < 128 || z < 128 || x > 13056 || z > 13056) {
            return new int[]{0, 0, 0, 0, 0, 0, 0};
        }
        int i1 = method42(plane, z, x) - y;
        x -= xCameraPos;
        i1 -= zCameraPos;
        z -= yCameraPos;
        int j1 = Model.modelIntArray1[yCameraCurve];
        int k1 = Model.modelIntArray2[yCameraCurve];
        int l1 = Model.modelIntArray1[xCameraCurve];
        int i2 = Model.modelIntArray2[xCameraCurve];
        int j2 = z * l1 + x * i2 >> 16;
        z = z * i2 - x * l1 >> 16;
        x = j2;
        j2 = i1 * k1 - z * j1 >> 16;
        z = i1 * j1 + z * k1 >> 16;
        if (z >= 50) {
            return new int[]{Rasterizer.textureInt1 + (x << SceneGraph.viewDistance) / z, Rasterizer.textureInt2 + (j2 << SceneGraph.viewDistance) / z, z, Rasterizer.textureInt1 + (x - width / 2 << SceneGraph.viewDistance) / z, Rasterizer.textureInt2 + (j2 - height / 2 << SceneGraph.viewDistance) / z, Rasterizer.textureInt1 + (x + width / 2 << SceneGraph.viewDistance) / z, Rasterizer.textureInt2 + (j2 + height / 2 << SceneGraph.viewDistance) / z};
        } else {
            return new int[]{0, 0, 0, 0, 0, 0, 0};
        }
    }

    private void npcScreenPos(Entity entity, int i) {
        calcEntityScreenPos(entity.x, i, entity.y);
    }

    private void calcEntityScreenPos(int i, int j, int l) {
        if (i < 128 || l < 128 || i > 13056 || l > 13056) {
            spriteDrawX = -1;
            spriteDrawY = -1;
            return;
        }
        int i1 = method42(plane, l, i) - j;
        i -= xCameraPos;
        i1 -= zCameraPos;
        l -= yCameraPos;
        int j1 = Model.modelIntArray1[yCameraCurve];
        int k1 = Model.modelIntArray2[yCameraCurve];
        int l1 = Model.modelIntArray1[xCameraCurve];
        int i2 = Model.modelIntArray2[xCameraCurve];
        int j2 = l * l1 + i * i2 >> 16;
        l = l * i2 - i * l1 >> 16;
        i = j2;
        j2 = i1 * k1 - l * j1 >> 16;
        l = i1 * j1 + l * k1 >> 16;
        i1 = j2;
        if (l >= 50) {
            spriteDrawX = Rasterizer.textureInt1 + (i << SceneGraph.viewDistance) / l;
            spriteDrawY = Rasterizer.textureInt2 + (i1 << SceneGraph.viewDistance) / l;
        } else {
            spriteDrawX = -1;
            spriteDrawY = -1;
        }
    }

    private void render_ground_item_names() {
        for (int x = 0; x < 104; x++) {
            for (int y = 0; y < 104; y++) {
                Deque node = groundArray[plane][x][y];
                int offset = 12;
                if (node != null) {
                    for (Item item = (Item) node.getFirst(); item != null; item = (Item) node.getNext()) {
                        ItemDefinition itemDef = ItemDefinition.lookup(item.itemId);
                        calcEntityScreenPos((x << 7) + 64, 64, (y << 7) + 64);
                        if (Settings.DISPLAY_GROUND_ITEM) {
                            int color = Settings.ITEM_RARITY_COLOR ? Utility.dropColor(item.type) : 0xffffff;
                            newSmallFont.drawCenteredString(itemDef.name + (item.itemAmount > 1 ? " (" + Utility.formatDigits(item.itemAmount) + ")" : ""), spriteDrawX, spriteDrawY - offset, color, 75);
                        }
                        offset += 12;
                    }
                }
            }
        }
    }

    private void buildSplitPrivateChatMenu() {
        if (splitPrivateChat == 0)
            return;
        int i = 0;
        for (int j = 0; j < 100; j++)
            if (chatMessages[j] != null) {
                int k = chatTypes[j];
                String s = chatNames[j];
                if ((k == 3 || k == 7) && (k == 7 || privateChatMode == 0 || privateChatMode == 1 && isFriendOrSelf(s))) {
                    int offSet = frameMode == ScreenMode.FIXED ? 4 : 0;
                    int l = 329 - i * 13;
                    if (frameMode != ScreenMode.FIXED) {
                        l = frameHeight - 170 - i * 13;
                    }
                    if (super.mouseX > 4 && super.mouseY - offSet > l - 10 && super.mouseY - offSet <= l + 3) {
                        int i1 = regularText.getTextWidth("From:  " + s + chatMessages[j]) + 25;
                        if (i1 > 450)
                            i1 = 450;
                        if (super.mouseX < 4 + i1) {
                            if (myPrivilege >= 1) {
                                menuActionName[menuActionRow] = "Report abuse <col=FFFFFF>" + s;
                                menuActionID[menuActionRow] = 2606;
                                menuActionRow++;
                            }
                            menuActionName[menuActionRow] = "Add ignore <col=FFFFFF>" + s;
                            menuActionID[menuActionRow] = 2042;
                            menuActionRow++;
                            menuActionName[menuActionRow] = "Reply to <col=FFFFFF>" + s;
                            menuActionID[menuActionRow] = 2639;
                            menuActionRow++;
                            menuActionName[menuActionRow] = "Add friend <col=FFFFFF>" + s;
                            menuActionID[menuActionRow] = 2337;
                            menuActionRow++;
                        }
                    }
                    if (++i >= 5)
                        return;
                }
                if ((k == 5 || k == 6) && privateChatMode < 2 && ++i >= 5)
                    return;
            }

    }

    private void method130(int j, int k, int l, int i1, int j1, int k1, int l1, int i2, int j2) {
        SpawnedObject class30_sub1 = null;
        for (SpawnedObject class30_sub1_1 = (SpawnedObject) aClass19_1179.reverseGetFirst(); class30_sub1_1 != null; class30_sub1_1 = (SpawnedObject) aClass19_1179.reverseGetNext()) {
            if (class30_sub1_1.anInt1295 != l1 || class30_sub1_1.anInt1297 != i2 || class30_sub1_1.anInt1298 != j1 || class30_sub1_1.anInt1296 != i1)
                continue;
            class30_sub1 = class30_sub1_1;
            break;
        }

        if (class30_sub1 == null) {
            class30_sub1 = new SpawnedObject();
            class30_sub1.anInt1295 = l1;
            class30_sub1.anInt1296 = i1;
            class30_sub1.anInt1297 = i2;
            class30_sub1.anInt1298 = j1;
            method89(class30_sub1);
            aClass19_1179.insertHead(class30_sub1);
        }
        class30_sub1.anInt1291 = k;
        class30_sub1.anInt1293 = k1;
        class30_sub1.anInt1292 = l;
        class30_sub1.anInt1302 = j2;
        class30_sub1.anInt1294 = j;
    }

    private boolean interfaceIsSelected(RSInterface class9) {
        if (class9.valueCompareType == null)
            return false;
        for (int i = 0; i < class9.valueCompareType.length; i++) {
            int j = executeScript(class9, i);
            int k = class9.requiredValues[i];
            if (class9.valueCompareType[i] == 2) {
                if (j >= k)
                    return false;
            } else if (class9.valueCompareType[i] == 3) {
                if (j <= k)
                    return false;
            } else if (class9.valueCompareType[i] == 4) {
                if (j == k)
                    return false;
            } else if (j != k)
                return false;
        }

        return true;
    }

    private DataInputStream openJagGrabInputStream(String s) throws IOException {
        // if(!aBoolean872)
        // if(signlink.mainapp != null)
        // return signlink.openurl(s);
        // else
        // return new DataInputStream((new URL(getCodeBase(), s)).openStream());
        if (aSocket832 != null) {
            try {
                aSocket832.close();
            } catch (Exception _ex) {
            }
            aSocket832 = null;
        }
        aSocket832 = openSocket(43595);
        aSocket832.setSoTimeout(10000);
        java.io.InputStream inputstream = aSocket832.getInputStream();
        OutputStream outputstream = aSocket832.getOutputStream();
        outputstream.write(("JAGGRAB /" + s + "\n\n").getBytes());
        outputstream.close();
        return new DataInputStream(inputstream);
    }

    private void updateOtherPlayerMovement(Buffer stream) {
        int count = stream.readBits(8);

        if (count < playerCount) {
            for (int index = count; index < playerCount; index++) {
                removedMobs[removedMobCount++] = playerIndices[index];
            }

        }
        if (count > playerCount) {
            Utility.reporterror(myUsername + " Too many players");
            throw new RuntimeException("eek");
        }
        playerCount = 0;
        for (int globalIndex = 0; globalIndex < count; globalIndex++) {
            final int index = playerIndices[globalIndex];
            Player player = playerArray[index];
            final int updateRequired = stream.readBits(1);
            if (updateRequired == 0) {
                playerIndices[playerCount++] = index;
                player.time = tick;
            } else {
                final int movementType = stream.readBits(2);

                if (movementType == 0) {
                    playerIndices[playerCount++] = index;
                    player.time = tick;
                    mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = index;
                } else if (movementType == 1) {
                    playerIndices[playerCount++] = index;
                    player.time = tick;
                    int direction = stream.readBits(3);
                    player.moveInDir(false, direction);
                    int update = stream.readBits(1);

                    if (update == 1) {
                        mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = index;
                    }
                } else if (movementType == 2) {
                    playerIndices[playerCount++] = index;
                    player.time = tick;
                    int firstDir = stream.readBits(3);
                    player.moveInDir(true, firstDir);

                    int secondDir = stream.readBits(3);
                    player.moveInDir(true, secondDir);

                    int update = stream.readBits(1);

                    if (update == 1) {
                        mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = index;
                    }
                } else if (movementType == 3) {
                    removedMobs[removedMobCount++] = index;
                }
            }
        }
    }

    public void raiseWelcomeScreen() {
        drawGameScreenSprite = true;
    }

    private void method137(Buffer stream, int j) {
        if (j == 84) {
            int k = stream.readUByte();
            int j3 = anInt1268 + (k >> 4 & 7);
            int i6 = anInt1269 + (k & 7);
            int l8 = stream.readUShort();
            int k11 = stream.readUShort();
            int l13 = stream.readUShort();
            if (j3 >= 0 && i6 >= 0 && j3 < 104 && i6 < 104) {
                Deque class19_1 = groundArray[plane][j3][i6];
                if (class19_1 != null) {
                    for (Item class30_sub2_sub4_sub2_3 = (Item) class19_1.reverseGetFirst(); class30_sub2_sub4_sub2_3 != null; class30_sub2_sub4_sub2_3 = (Item) class19_1.reverseGetNext()) {
                        if (class30_sub2_sub4_sub2_3.itemId != (l8 & 0x7fff) || class30_sub2_sub4_sub2_3.itemAmount != k11)
                            continue;
                        class30_sub2_sub4_sub2_3.itemAmount = l13;
                        break;
                    }

                    spawnGroundItem(j3, i6);
                }
            }
            return;
        }
        if (j == 105) {
            int l = stream.readUByte();
            int k3 = anInt1268 + (l >> 4 & 7);
            int j6 = anInt1269 + (l & 7);
            int i9 = stream.readUShort();
            int l11 = stream.readUByte();
            int i14 = l11 >> 4 & 0xf;
            int i16 = l11 & 7;
            if (localPlayer.smallX[0] >= k3 - i14 && localPlayer.smallX[0] <= k3 + i14 && localPlayer.smallY[0] >= j6 - i14 && localPlayer.smallY[0] <= j6 + i14 && aBoolean848 && !lowMem && anInt1062 < 50) {
                anIntArray1207[anInt1062] = i9;
                anIntArray1241[anInt1062] = i16;
                anIntArray1250[anInt1062] = Sounds.anIntArray326[i9];
                anInt1062++;
            }
        }
        if (j == 215) {
            int i1 = stream.readUShortA();
            int l3 = stream.readUByteS();
            int k6 = anInt1268 + (l3 >> 4 & 7);
            int j9 = anInt1269 + (l3 & 7);
            int i12 = stream.readUShortA();
            int j14 = stream.readUShort();
            if (k6 >= 0 && j9 >= 0 && k6 < 104 && j9 < 104 && i12 != unknownInt10) {
                Item class30_sub2_sub4_sub2_2 = new Item();
                class30_sub2_sub4_sub2_2.itemId = i1;
                class30_sub2_sub4_sub2_2.itemAmount = j14;
                if (groundArray[plane][k6][j9] == null)
                    groundArray[plane][k6][j9] = new Deque();
                groundArray[plane][k6][j9].insertHead(class30_sub2_sub4_sub2_2);
                spawnGroundItem(k6, j9);
            }
            return;
        }
        if (j == 156) {
            int j1 = stream.readUByteA();
            int i4 = anInt1268 + (j1 >> 4 & 7);
            int l6 = anInt1269 + (j1 & 7);
            int k9 = stream.readUShort();
            if (i4 >= 0 && l6 >= 0 && i4 < 104 && l6 < 104) {
                Deque class19 = groundArray[plane][i4][l6];
                if (class19 != null) {
                    for (Item item = (Item) class19.reverseGetFirst(); item != null; item = (Item) class19.reverseGetNext()) {
                        if (item.itemId != (k9 & 0x7fff))
                            continue;
                        item.unlink();
                        break;
                    }

                    if (class19.reverseGetFirst() == null)
                        groundArray[plane][i4][l6] = null;
                    spawnGroundItem(i4, l6);
                }
            }
            return;
        }
        if (j == 160) {
            int k1 = stream.readUByteS();
            int j4 = anInt1268 + (k1 >> 4 & 7);
            int i7 = anInt1269 + (k1 & 7);
            int l9 = stream.readUByteS();
            int j12 = l9 >> 2;
            int k14 = l9 & 3;
            int j16 = anIntArray1177[j12];
            int j17 = stream.readUShortA();
            if (j4 >= 0 && i7 >= 0 && j4 < 103 && i7 < 103) {
                int j18 = intGroundArray[plane][j4][i7];
                int i19 = intGroundArray[plane][j4 + 1][i7];
                int l19 = intGroundArray[plane][j4 + 1][i7 + 1];
                int k20 = intGroundArray[plane][j4][i7 + 1];
                if (j16 == 0) {
                    Wall class10 = worldController.method296(plane, j4, i7);
                    if (class10 != null) {
                        int k21 = class10.uid >> 14 & 0x7fff;
                        if (j12 == 2) {
                            class10.aClass30_Sub2_Sub4_278 = new SceneObject(k21, 4 + k14, 2, i19, l19, j18, k20, j17, false);
                            class10.aClass30_Sub2_Sub4_279 = new SceneObject(k21, k14 + 1 & 3, 2, i19, l19, j18, k20, j17, false);
                        } else {
                            class10.aClass30_Sub2_Sub4_278 = new SceneObject(k21, k14, j12, i19, l19, j18, k20, j17, false);
                        }
                    }
                }
                if (j16 == 1) {
                    WallDecoration class26 = worldController.method297(j4, i7, plane);
                    if (class26 != null)
                        class26.aClass30_Sub2_Sub4_504 = new SceneObject(class26.uid >> 14 & 0x7fff, 0, 4, i19, l19, j18, k20, j17, false);
                }
                if (j16 == 2) {
                    StaticObject class28 = worldController.method298(j4, i7, plane);
                    if (j12 == 11)
                        j12 = 10;
                    if (class28 != null)
                        class28.aClass30_Sub2_Sub4_521 = new SceneObject(class28.uid >> 14 & 0x7fff, k14, j12, i19, l19, j18, k20, j17, false);
                }
                if (j16 == 3) {
                    GroundDecoration class49 = worldController.method299(i7, j4, plane);
                    if (class49 != null)
                        class49.aClass30_Sub2_Sub4_814 = new SceneObject(class49.uid >> 14 & 0x7fff, k14, 22, i19, l19, j18, k20, j17, false);
                }
            }
            return;
        }
        if (j == 147) {
            int l1 = stream.readUByteS();
            int k4 = anInt1268 + (l1 >> 4 & 7);
            int j7 = anInt1269 + (l1 & 7);
            int i10 = stream.readUShort();
            byte byte0 = stream.method430();
            int l14 = stream.readLEUShort();
            byte byte1 = stream.method429();
            int k17 = stream.readUShort();
            int k18 = stream.readUByteS();
            int j19 = k18 >> 2;
            int i20 = k18 & 3;
            int l20 = anIntArray1177[j19];
            byte byte2 = stream.readSignedByte();
            int l21 = stream.readUShort();
            byte byte3 = stream.method429();
            Player player;
            if (i10 == unknownInt10)
                player = localPlayer;
            else
                player = playerArray[i10];
            if (player != null) {
                ObjectDefinition class46 = ObjectDefinition.lookup(l21);
                int i22 = intGroundArray[plane][k4][j7];
                int j22 = intGroundArray[plane][k4 + 1][j7];
                int k22 = intGroundArray[plane][k4 + 1][j7 + 1];
                int l22 = intGroundArray[plane][k4][j7 + 1];
                Model model = class46.method578(j19, i20, i22, j22, k22, l22, -1);
                if (model != null) {
                    method130(k17 + 1, -1, 0, l20, j7, 0, plane, k4, l14 + 1);
                    player.anInt1707 = l14 + tick;
                    player.anInt1708 = k17 + tick;
                    player.aModel_1714 = model;
                    int i23 = class46.width;
                    int j23 = class46.length;
                    if (i20 == 1 || i20 == 3) {
                        i23 = class46.length;
                        j23 = class46.width;
                    }
                    player.anInt1711 = k4 * 128 + i23 * 64;
                    player.anInt1713 = j7 * 128 + j23 * 64;
                    player.anInt1712 = method42(plane, player.anInt1713, player.anInt1711);
                    if (byte2 > byte0) {
                        byte byte4 = byte2;
                        byte2 = byte0;
                        byte0 = byte4;
                    }
                    if (byte3 > byte1) {
                        byte byte5 = byte3;
                        byte3 = byte1;
                        byte1 = byte5;
                    }
                    player.anInt1719 = k4 + byte2;
                    player.anInt1721 = k4 + byte0;
                    player.anInt1720 = j7 + byte3;
                    player.anInt1722 = j7 + byte1;
                }
            }
        }
        if (j == 151) {
            int i2 = stream.readUByteA();
            int l4 = anInt1268 + (i2 >> 4 & 7);
            int k7 = anInt1269 + (i2 & 7);
            int j10 = stream.readLEUShort();
            int k12 = stream.readUByteS();
            int i15 = k12 >> 2;
            int k16 = k12 & 3;
            int l17 = anIntArray1177[i15];
            if (l4 >= 0 && k7 >= 0 && l4 < 104 && k7 < 104)
                method130(-1, j10, k16, l17, k7, i15, plane, l4, 0);
            return;
        }
        if (j == 4) {
            int j2 = stream.readUByte();
            int i5 = anInt1268 + (j2 >> 4 & 7);
            int l7 = anInt1269 + (j2 & 7);
            int k10 = stream.readUShort();
            int l12 = stream.readUByte();
            int j15 = stream.readUShort();
            if (i5 >= 0 && l7 >= 0 && i5 < 104 && l7 < 104) {
                i5 = i5 * 128 + 64;
                l7 = l7 * 128 + 64;
                Projectile class30_sub2_sub4_sub3 = new Projectile(plane, tick, j15, k10, method42(plane, l7, i5) - l12, l7, i5);
                aClass19_1056.insertHead(class30_sub2_sub4_sub3);
            }
            return;
        }
        if (j == 44) {
            int itemID = stream.readLEUShortA();
            long itemAmount = stream.readLong();
            int rarity = stream.readUByte();
            int empty = stream.readUByte();

            int l10 = anInt1268 + (empty >> 4 & 7);
            int i13 = anInt1269 + (empty & 7);
            if (l10 >= 0 && i13 >= 0 && l10 < 104 && i13 < 104) {
                Item groundItem = new Item();
                groundItem.itemId = itemID;
                groundItem.itemAmount = ((int) itemAmount);
                groundItem.type = rarity;

                if (groundArray[plane][l10][i13] == null)
                    groundArray[plane][l10][i13] = new Deque();
                groundArray[plane][l10][i13].insertHead(groundItem);
                spawnGroundItem(l10, i13);
            }
            return;
        }
        if (j == 101) {
            int l2 = stream.readNegUByte();
            int k5 = l2 >> 2;
            int j8 = l2 & 3;
            int i11 = anIntArray1177[k5];
            int j13 = stream.readUByte();
            int k15 = anInt1268 + (j13 >> 4 & 7);
            int l16 = anInt1269 + (j13 & 7);
            if (k15 >= 0 && l16 >= 0 && k15 < 104 && l16 < 104)
                method130(-1, -1, j8, i11, l16, k5, plane, k15, 0);
            return;
        }
        if (j == 117) {
            int i3 = stream.readUByte();
            int l5 = anInt1268 + (i3 >> 4 & 7);
            int k8 = anInt1269 + (i3 & 7);
            int j11 = l5 + stream.readSignedByte();
            int k13 = k8 + stream.readSignedByte();
            int l15 = stream.readSignedWord();
            int i17 = stream.readUShort();
            int i18 = stream.readUByte() * 4;
            int l18 = stream.readUByte() * 4;
            int k19 = stream.readUShort();
            int j20 = stream.readUShort();
            int i21 = stream.readUByte();
            int j21 = stream.readUByte();
            if (l5 >= 0 && k8 >= 0 && l5 < 104 && k8 < 104 && j11 >= 0 && k13 >= 0 && j11 < 104 && k13 < 104 && i17 != 65535) {
                l5 = l5 * 128 + 64;
                k8 = k8 * 128 + 64;
                j11 = j11 * 128 + 64;
                k13 = k13 * 128 + 64;
                SceneProjectile class30_sub2_sub4_sub4 = new SceneProjectile(i21, l18, k19 + tick, j20 + tick, j21, plane, method42(plane, k8, l5) - i18, k8, l5, l15, i17);
                class30_sub2_sub4_sub4.method455(k19 + tick, k13, method42(plane, k13, j11) - l18, j11);
                aClass19_1013.insertHead(class30_sub2_sub4_sub4);
            }
        }
    }

    public static SnowFlake[] snowflakes;
    public static boolean snowing = Settings.SNOW;

    public static void createSnow() {
        snowflakes = new SnowFlake[30 + SnowFlake.random(20)];

        for (int index = 0; index < snowflakes.length; index++) {
            int x = SnowFlake.random(frameMode == ScreenMode.FIXED ? 765 : frameWidth);
            int y_offset = 100 + SnowFlake.random(300) + index * 5;

            if (SnowFlake.random(100) < 25) {
                snowflakes[index] = new SnowFlake(x, -(11 + y_offset));
            } else {
                int radius = 4 + SnowFlake.random(4);
                snowflakes[index] = new SnowFlake(x, -(radius + y_offset), radius);
            }
        }
        snowing = true;
    }

    public static void drawSnowflakes(int x, int y) {
        if (snowflakes != null) {
            for (int index = 0; index < snowflakes.length; index++) {
                SnowFlake flake = snowflakes[index];
                if (flake.isMelted()) {
                    continue;
                }
                flake.draw(x, y);
            }
        }
    }

    public static void processSnowflakes() {
        int melted = 0;

        for (int index = 0; index < snowflakes.length; index++) {
            SnowFlake flake = snowflakes[index];

            if (flake.isMelted()) {
                melted++;
                continue;
            } else if (!snowing) {
                if (flake.getY() + (flake.getRadius()) < 0) {
                    melted++;
                    continue;
                } else {
                    flake.setMelting(true);
                    flake.setTouched(true);
                }
            }
            flake.cycle();
            if (!flake.isMelting() && flake.getX() >= 15 && flake.getX() <= 70 && flake.getY() >= 100 && flake.getY() <= 300) {
                flake.setMelting(true);
                flake.setTouched(true);
            }
            if (flake.touched()) {
                flake.setMelting(true);
                flake.setTouched(true);
            }
            if (flake.isMelting()) {
                if (flake.getCycle() >= flake.getLifespan() && flake.getAlpha() > 0 || flake.wasTouched()) {
                    flake.adjustAlpha(-4);
                }
            }
            if (flake.isMoving()) {
                int x_adjust = flake.getCycle() > 30 ? SnowFlake.random(2) : 0;

                if (x_adjust > 0) {
                    flake.resetCycle();
                }

                int y_adjust = SnowFlake.random(2);
                flake.adjustX(SnowFlake.random(100) >= 50 ? -x_adjust : x_adjust);
                flake.adjustY(y_adjust);
                int height = flake.getRadius();

                if (flake.getY() >= screenAreaHeight + height) {
                    flake.reset();
                }
            }
        }
        if (melted == snowflakes.length) {
            snowing = false;
        }
    }

    private void method139(Buffer stream) {
        stream.initBitAccess();
        int k = stream.readBits(8);
        if (k < npcCount) {
            for (int l = k; l < npcCount; l++)
                removedMobs[removedMobCount++] = npcIndices[l];

        }
        if (k > npcCount) {
            Utility.reporterror(myUsername + " Too many npcs");
            throw new RuntimeException("eek");
        }
        npcCount = 0;
        for (int i1 = 0; i1 < k; i1++) {
            int j1 = npcIndices[i1];
            Npc npc = npcs[j1];
            int k1 = stream.readBits(1);
            if (k1 == 0) {
                npcIndices[npcCount++] = j1;
                npc.time = tick;
            } else {
                int l1 = stream.readBits(2);
                if (l1 == 0) {
                    npcIndices[npcCount++] = j1;
                    npc.time = tick;
                    mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = j1;
                } else if (l1 == 1) {
                    npcIndices[npcCount++] = j1;
                    npc.time = tick;
                    int i2 = stream.readBits(3);
                    npc.moveInDir(false, i2);
                    int k2 = stream.readBits(1);
                    if (k2 == 1)
                        mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = j1;
                } else if (l1 == 2) {
                    npcIndices[npcCount++] = j1;
                    npc.time = tick;
                    int j2 = stream.readBits(3);
                    npc.moveInDir(true, j2);
                    int l2 = stream.readBits(3);
                    npc.moveInDir(true, l2);
                    int i3 = stream.readBits(1);
                    if (i3 == 1)
                        mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = j1;
                } else if (l1 == 3)
                    removedMobs[removedMobCount++] = j1;
            }
        }

    }

    private void method142(int y, int j, int k, int l, int x, int j1, int k1) {
        if (x >= 1 && y >= 1 && x <= 102 && y <= 102) {
            if (lowMem && j != plane)
                return;
            int i2 = 0;
            if (j1 == 0)
                i2 = worldController.method300(j, x, y);
            if (j1 == 1)
                i2 = worldController.method301(j, x, y);
            if (j1 == 2)
                i2 = worldController.method302(j, x, y);
            if (j1 == 3)
                i2 = worldController.method303(j, x, y);
            if (i2 != 0) {
                int packed = worldController.method304(j, x, y, i2);
                int j2 = i2 >> 14 & 0x7fff;
                int type = packed & 0x1f;
                int direction = packed >> 6;
                if (j1 == 0) {
                    worldController.method291(x, j, y, (byte) -119);
                    ObjectDefinition class46 = ObjectDefinition.lookup(j2);
                    if (class46.solid)
                        collisionMaps[j].method215(x, y, type, direction, class46.impenetrable);
                }
                if (j1 == 1)
                    worldController.method292(y, j, x);
                if (j1 == 2) {
                    worldController.method293(j, x, y);
                    ObjectDefinition class46_1 = ObjectDefinition.lookup(j2);
                    if (x + class46_1.width > 103 || y + class46_1.width > 103 || x + class46_1.length > 103 || y + class46_1.length > 103)
                        return;
                    if (class46_1.solid)
                        collisionMaps[j].method216(direction, class46_1.width, x, y, class46_1.length, class46_1.impenetrable);
                }
                if (j1 == 3) {
                    worldController.method294(j, y, x);
                    ObjectDefinition class46_2 = ObjectDefinition.lookup(j2);
                    if (class46_2.solid && class46_2.interactive)
                        collisionMaps[j].method218(y, x);
                }
            }
            if (k1 >= 0) {
                int j3 = j;
                if (j3 < 3 && (byteGroundArray[1][x][y] & 2) == 2)
                    j3++;
                ObjectManager.method188(worldController, k, y, l, j3, collisionMaps[j], intGroundArray, x, k1, j);
            }
        }
    }

    private void updatePlayers(int packetSize, Buffer buffer) {
        removedMobCount = 0;
        mobsAwaitingUpdateCount = 0;
        updateLocalPlayerMovement(buffer);
        updateOtherPlayerMovement(buffer);
        updatePlayerList(buffer, packetSize);
        parsePlayerMasks(buffer);
        for (int count = 0; count < removedMobCount; count++) {
            int index = removedMobs[count];

            if (playerArray[index].time != tick) {
                playerArray[index] = null;
            }
        }

        if (buffer.currentOffset != packetSize) {
            Utility.reporterror("Error packet size mismatch in getplayer pos:" + buffer.currentOffset + " psize:" + packetSize);
            throw new RuntimeException("eek");
        }
        for (int i1 = 0; i1 < playerCount; i1++)
            if (playerArray[playerIndices[i1]] == null) {
                Utility.reporterror(myUsername + " null entry in pl list - pos:" + i1 + " size:" + playerCount);
                throw new RuntimeException("eek");
            }

    }

    private void setCameraPos(int j, int k, int l, int i1, int j1, int k1) {
        int l1 = 2048 - k & 0x7ff;
        int i2 = 2048 - j1 & 0x7ff;
        int j2 = 0;
        int k2 = 0;
        int l2 = j;
        if (l1 != 0) {
            int i3 = Model.modelIntArray1[l1];
            int k3 = Model.modelIntArray2[l1];
            int i4 = k2 * k3 - l2 * i3 >> 16;
            l2 = k2 * i3 + l2 * k3 >> 16;
            k2 = i4;
        }
        if (i2 != 0) {
            int j3 = Model.modelIntArray1[i2];
            int l3 = Model.modelIntArray2[i2];
            int j4 = l2 * j3 + j2 * l3 >> 16;
            l2 = l2 * l3 - j2 * j3 >> 16;
            j2 = j4;
        }
        xCameraPos = l - j2;
        zCameraPos = i1 - k2;
        yCameraPos = k1 - l2;
        yCameraCurve = k;
        xCameraCurve = j1;
    }

    public void sendPacket185(int button, int toggle, int type) {
        switch (type) {
            case 135:
                RSInterface class9 = RSInterface.getInterfaceCache()[button];
                boolean flag8 = true;
                if (class9.contentType > 0)
                    flag8 = promptUserForInput(class9);
                if (flag8) {
                    outgoing.writeOpcode(185);
                    outgoing.writeShort(button);
                }
                break;
            case 646:
                outgoing.writeOpcode(185);
                outgoing.writeShort(button);
                RSInterface class9_2 = RSInterface.getInterfaceCache()[button];
                if (class9_2.scripts != null && class9_2.scripts[0][0] == 5) {
                    if (settings[toggle] != class9_2.requiredValues[0]) {
                        settings[toggle] = class9_2.requiredValues[0];
                        method33(toggle);
                    }
                }
                break;
            case 169:
                outgoing.writeOpcode(185);
                outgoing.writeShort(button);
                RSInterface class9_3 = RSInterface.getInterfaceCache()[button];
                if (class9_3.scripts != null && class9_3.scripts[0][0] == 5) {
                    settings[toggle] = 1 - settings[toggle];
                    method33(toggle);
                }
                switch (button) {
                    case 74214:
                        if (toggle == 0)
                            sendFrame36(173, toggle);
                        if (toggle == 1)
                            sendPacket185(153, 173, 646);
                        break;
                }
                break;
        }
    }

    public void sendFrame36(int id, int state) {
        anIntArray1045[id] = state;
        if (settings[id] != state) {
            settings[id] = state;
            method33(id);
            if (dialogueId != -1)
                redrawDialogueBox = true;
        }
    }

    public void sendFrame219() {
        if (invOverlayInterfaceID != -1) {
            invOverlayInterfaceID = -1;
            tabAreaAltered = true;
        }
        if (backDialogueId != -1) {
            backDialogueId = -1;
            redrawDialogueBox = true;
        }
        if (inputDialogState != 0) {
            inputDialogState = 0;
            redrawDialogueBox = true;
        }
        openInterfaceID = -1;
        aBoolean1149 = false;
    }

    public void sendFrame248(int interfaceID, int sideInterfaceID) {
        if (backDialogueId != -1) {
            backDialogueId = -1;
            redrawDialogueBox = true;
        }
        if (inputDialogState != 0) {
            inputDialogState = 0;
            redrawDialogueBox = true;
        }
        openInterfaceID = interfaceID;
        invOverlayInterfaceID = sideInterfaceID;
        tabAreaAltered = true;
        aBoolean1149 = false;
    }


    @Override
    public void keyPressed(KeyEvent event) {
        super.keyPressed(event);
        if (!loggedIn) {
            return;
        }
        if (event.getKeyCode() != KeyEvent.VK_ESCAPE && event.getKeyCode() != KeyEvent.VK_SPACE && event.getKeyCode() != KeyEvent.VK_1 && event.getKeyCode() != KeyEvent.VK_2 && event.getKeyCode() != KeyEvent.VK_3 && event.getKeyCode() != KeyEvent.VK_4 && event.getKeyCode() != KeyEvent.VK_5 && event.getKeyCode() != KeyEvent.VK_NUMPAD1 && event.getKeyCode() != KeyEvent.VK_NUMPAD2 && event.getKeyCode() != KeyEvent.VK_NUMPAD3 && event.getKeyCode() != KeyEvent.VK_NUMPAD4 && event.getKeyCode() != KeyEvent.VK_NUMPAD5) {
            return;
        }
        if (chatDelay.elapsed() < 599) {
            return;
        }
        outgoing.writeOpcode(186);
        outgoing.writeShort(event.getKeyCode());
    }

    public void moveBy(Compass direction) {
        moveBy(direction.getX(), direction.getY());
    }

    public void moveBy(int x, int y) {
        walk(localPlayer.smallX[0] + x, localPlayer.smallY[0],  2);
    }

    private boolean parsePacket() {
        if (socketStream == null)
            return false;
        try {
            int i = socketStream.available();
            if (i == 0)
                return false;
            if (opcode == -1) {
                socketStream.flushInputStream(incoming.buffer, 1);
                opcode = incoming.buffer[0] & 0xff;
                if (encryption != null)
                    opcode = opcode - encryption.getNextKey() & 0xff;
                pktSize = SizeConstants.packetSizes[opcode];
                i--;
            }
            if (pktSize == -1)
                if (i > 0) {
                    socketStream.flushInputStream(incoming.buffer, 1);
                    pktSize = incoming.buffer[0] & 0xff;
                    i--;
                } else {
                    return false;
                }
            if (pktSize == -2)
                if (i > 1) {
                    socketStream.flushInputStream(incoming.buffer, 2);
                    incoming.currentOffset = 0;
                    pktSize = incoming.readUShort();
                    i -= 2;
                } else {
                    return false;
                }
            if (i < pktSize)
                return false;
            incoming.currentOffset = 0;
            socketStream.flushInputStream(incoming.buffer, pktSize);
            anInt1009 = 0;
            lastOpcode3 = lastOpcode2;
            lastOpcode2 = lastOpcode1;
            lastOpcode1 = opcode;
            switch (opcode) {
                case 81:
                    updatePlayers(pktSize, incoming);
                    aBoolean1080 = false;
                    opcode = -1;
                    return true;

                case 176:
                    daysSinceRecovChange = incoming.readNegUByte();
                    unreadMessages = incoming.readUShortA();
                    membersInt = incoming.readUByte();
                    anInt1193 = incoming.method440();
                    daysSinceLastLogin = incoming.readUShort();
                    if (anInt1193 != 0 && openInterfaceID == -1) {
                        //Utility.dnslookup(StringUtils.method586(anInt1193));
                        clearTopInterfaces();
                        char c = '\u028A';
                        if (daysSinceRecovChange != 201 || membersInt == 1)
                            c = '\u028F';
                        reportAbuseInput = "";
                        canMute = false;
                        for (int k9 = 0; k9 < RSInterface.getInterfaceCache().length; k9++) {
                            if (RSInterface.getInterfaceCache()[k9] == null || RSInterface.getInterfaceCache()[k9].contentType != c)
                                continue;
                            openInterfaceID = RSInterface.getInterfaceCache()[k9].parentID;

                        }
                    }
                    opcode = -1;
                    return true;

                case 64:
                    anInt1268 = incoming.readNegUByte();
                    anInt1269 = incoming.readUByteS();
                    for (int j = anInt1268; j < anInt1268 + 8; j++) {
                        for (int l9 = anInt1269; l9 < anInt1269 + 8; l9++)
                            if (groundArray[plane][j][l9] != null) {
                                groundArray[plane][j][l9] = null;
                                spawnGroundItem(j, l9);
                            }
                    }
                    for (SpawnedObject class30_sub1 = (SpawnedObject) aClass19_1179.reverseGetFirst(); class30_sub1 != null; class30_sub1 = (SpawnedObject) aClass19_1179.reverseGetNext())
                        if (class30_sub1.anInt1297 >= anInt1268 && class30_sub1.anInt1297 < anInt1268 + 8 && class30_sub1.anInt1298 >= anInt1269 && class30_sub1.anInt1298 < anInt1269 + 8 && class30_sub1.anInt1295 == plane)
                            class30_sub1.anInt1294 = 0;
                    opcode = -1;
                    return true;

                case 185:
                    int k = incoming.readLEUShortA();
                    RSInterface.getInterfaceCache()[k].defaultMediaType = 3;
                    if (localPlayer.desc == null)
                        RSInterface.getInterfaceCache()[k].mediaID = (localPlayer.appearanceColors[0] << 25) + (localPlayer.appearanceColors[4] << 20) + (localPlayer.appearanceModels[0] << 15) + (localPlayer.appearanceModels[8] << 10) + (localPlayer.appearanceModels[11] << 5) + localPlayer.appearanceModels[1];
                    else
                        RSInterface.getInterfaceCache()[k].mediaID = (int) (0x12345678L + localPlayer.desc.interfaceType);
                    opcode = -1;
                    return true;

                /* Clan chat packet */
                case 217:
                    try {
                        s1 = incoming.readString();
                        message = incoming.readString();
                        clanname = incoming.readString();
                        rights = incoming.readUShort();
                        // message = TextInput.processText(message);
                        if (Settings.PROFANITY_FILTER) {
                            message = Censor.doCensor(message);
                        }
                        pushMessage(message, 16, s1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    opcode = -1;
                    return true;

                case 107:
                    aBoolean1160 = false;
                    for (int l = 0; l < 5; l++)
                        aBooleanArray876[l] = false;
                    // xpCounter = 0;
                    opcode = -1;
                    return true;

                case 72:
                    int i1 = incoming.readLEUShort();
                    RSInterface class9 = RSInterface.getInterfaceCache()[i1];
                    for (int k15 = 0; k15 < class9.inv.length; k15++) {
                        class9.inv[k15] = -1;
                        class9.inv[k15] = 0;
                    }
                    opcode = -1;
                    return true;

                case 214: {
                    if (ignoreCount < 100) {
                        ignores[ignoreCount] = incoming.readLong();
                        ignoreCount++;
                    }
                    opcode = -1;
                }
                return true;

                case 166:
                    aBoolean1160 = true;
                    anInt1098 = incoming.readUByte();
                    anInt1099 = incoming.readUByte();
                    anInt1100 = incoming.readUShort();
                    anInt1101 = incoming.readUByte();
                    anInt1102 = incoming.readUByte();
                    if (anInt1102 >= 100) {
                        xCameraPos = anInt1098 * 128 + 64;
                        yCameraPos = anInt1099 * 128 + 64;
                        zCameraPos = method42(plane, yCameraPos, xCameraPos) - anInt1100;
                    }
                    opcode = -1;
                    return true;

                case 134: {
                    int skill = incoming.readUByte();
                    int level = incoming.method439();
                    int l15 = incoming.readUByte();
                    currentExp[skill] = level;
                    currentStats[skill] = l15;
                    maxStats[skill] = 1;
                    for (int k20 = 0; k20 < 98; k20++)
                        if (level >= anIntArray1019[k20])
                            maxStats[skill] = k20 + 2;
                    opcode = -1;
                }
                return true;

                case 71:
                    int l1 = incoming.readUShort();
                    int j10 = incoming.readUByteA();
                    if (l1 == 65535)
                        l1 = -1;
                    tabInterfaceIDs[j10] = l1;
                    tabAreaAltered = true;
                    opcode = -1;
                    return true;
                case 109:
                    resetLogout();
                    opcode = -1;
                    return false;

                case 70:
                    int k2 = incoming.readSignedWord();
                    int l10 = incoming.readLEShort();
                    int i16 = incoming.readLEUShort();
                    RSInterface class9_5 = RSInterface.getInterfaceCache()[i16];
                    class9_5.anInt263 = k2;
                    class9_5.anInt265 = l10;
                    opcode = -1;
                    return true;

                case 73:
                case 241:
                    int l2 = anInt1069;
                    int i11 = anInt1070;
                    if (opcode == 73) {
                        l2 = incoming.readUShortA();
                        i11 = incoming.readUShort();
                        aBoolean1159 = false;
                    } // construction
                    if (opcode == 241) {
                        i11 = incoming.readUShortA();
                        incoming.initBitAccess();
                        for (int j16 = 0; j16 < 4; j16++) {
                            for (int l20 = 0; l20 < 13; l20++) {
                                for (int j23 = 0; j23 < 13; j23++) {
                                    int i26 = incoming.readBits(1);
                                    if (i26 == 1)
                                        anIntArrayArrayArray1129[j16][l20][j23] = incoming.readBits(26);
                                    else
                                        anIntArrayArrayArray1129[j16][l20][j23] = -1;
                                }
                            }
                        }
                        incoming.finishBitAccess();
                        l2 = incoming.readUShort();
                        aBoolean1159 = true;
                    }
                    if (anInt1069 == l2 && anInt1070 == i11 && loadingStage == 2) {
                        opcode = -1;
                        return true;
                    }
                    anInt1069 = l2;
                    anInt1070 = i11;
                    baseX = (anInt1069 - 6) * 8;
                    baseY = (anInt1070 - 6) * 8;
                    aBoolean1141 = (anInt1069 / 8 == 48 || anInt1069 / 8 == 49) && anInt1070 / 8 == 48;
                    if (anInt1069 / 8 == 48 && anInt1070 / 8 == 148)
                        aBoolean1141 = true;
                    loadingStage = 1;
                    aLong824 = System.currentTimeMillis();
                    gameScreenIP.initDrawingArea();
                    newFancyFont.drawBasicString("Loading - please wait.", 5, 15, 0xffffff, 0);
                    gameScreenIP.drawGraphics(super.graphics, frameMode == ScreenMode.FIXED ? 4 : 0, frameMode == ScreenMode.FIXED ? 4 : 0);
                    if (opcode == 73) {
                        int k16 = 0;
                        for (int i21 = (anInt1069 - 6) / 8; i21 <= (anInt1069 + 6) / 8; i21++) {
                            for (int k23 = (anInt1070 - 6) / 8; k23 <= (anInt1070 + 6) / 8; k23++)
                                k16++;
                        }
                        aByteArrayArray1183 = new byte[k16][];
                        aByteArrayArray1247 = new byte[k16][];
                        anIntArray1234 = new int[k16];
                        anIntArray1235 = new int[k16];
                        anIntArray1236 = new int[k16];
                        k16 = 0;
                        for (int l23 = (anInt1069 - 6) / 8; l23 <= (anInt1069 + 6) / 8; l23++) {
                            for (int j26 = (anInt1070 - 6) / 8; j26 <= (anInt1070 + 6) / 8; j26++) {
                                anIntArray1234[k16] = (l23 << 8) + j26;
                                if (aBoolean1141 && (j26 == 49 || j26 == 149 || j26 == 147 || l23 == 50 || l23 == 49 && j26 == 47)) {
                                    anIntArray1235[k16] = -1;
                                    anIntArray1236[k16] = -1;
                                    k16++;
                                } else {
                                    int k28 = anIntArray1235[k16] = onDemandFetcher.resolve(0, l23, j26);
                                    if (k28 != -1)
                                        onDemandFetcher.provide(3, k28);
                                    int j30 = anIntArray1236[k16] = onDemandFetcher.resolve(1, l23, j26);
                                    if (j30 != -1)
                                        onDemandFetcher.provide(3, j30);
                                    k16++;
                                }
                            }
                        }
                    }
                    if (opcode == 241) {
                        int l16 = 0;
                        int ai[] = new int[676];
                        for (int i24 = 0; i24 < 4; i24++) {
                            for (int k26 = 0; k26 < 13; k26++) {
                                for (int l28 = 0; l28 < 13; l28++) {
                                    int k30 = anIntArrayArrayArray1129[i24][k26][l28];
                                    if (k30 != -1) {
                                        int k31 = k30 >> 14 & 0x3ff;
                                        int i32 = k30 >> 3 & 0x7ff;
                                        int k32 = (k31 / 8 << 8) + i32 / 8;
                                        for (int j33 = 0; j33 < l16; j33++) {
                                            if (ai[j33] != k32)
                                                continue;
                                            k32 = -1;

                                        }
                                        if (k32 != -1)
                                            ai[l16++] = k32;
                                    }
                                }
                            }
                        }
                        aByteArrayArray1183 = new byte[l16][];
                        aByteArrayArray1247 = new byte[l16][];
                        anIntArray1234 = new int[l16];
                        anIntArray1235 = new int[l16];
                        anIntArray1236 = new int[l16];
                        for (int l26 = 0; l26 < l16; l26++) {
                            int i29 = anIntArray1234[l26] = ai[l26];
                            int l30 = i29 >> 8 & 0xff;
                            int l31 = i29 & 0xff;
                            int j32 = anIntArray1235[l26] = onDemandFetcher.resolve(0, l30, l31);
                            if (j32 != -1)
                                onDemandFetcher.provide(3, j32);
                            int i33 = anIntArray1236[l26] = onDemandFetcher.resolve(1, l30, l31);
                            if (i33 != -1)
                                onDemandFetcher.provide(3, i33);
                        }
                        ai = null;
                    }
                    int i17 = baseX - anInt1036;
                    int j21 = baseY - anInt1037;
                    anInt1036 = baseX;
                    anInt1037 = baseY;
                    for (int j24 = 0; j24 < 16384; j24++) {
                        Npc npc = npcs[j24];
                        if (npc != null) {
                            for (int j29 = 0; j29 < 10; j29++) {
                                npc.smallX[j29] -= i17;
                                npc.smallY[j29] -= j21;
                            }
                            npc.x -= i17 * 128;
                            npc.y -= j21 * 128;
                        }
                    }
                    for (int i27 = 0; i27 < maxPlayers; i27++) {
                        Player player = playerArray[i27];
                        if (player != null) {
                            for (int i31 = 0; i31 < 10; i31++) {
                                player.smallX[i31] -= i17;
                                player.smallY[i31] -= j21;
                            }
                            player.x -= i17 * 128;
                            player.y -= j21 * 128;
                        }
                    }
                    aBoolean1080 = true;
                    byte byte1 = 0;
                    byte byte2 = 104;
                    byte byte3 = 1;
                    if (i17 < 0) {
                        byte1 = 103;
                        byte2 = -1;
                        byte3 = -1;
                    }
                    byte byte4 = 0;
                    byte byte5 = 104;
                    byte byte6 = 1;
                    if (j21 < 0) {
                        byte4 = 103;
                        byte5 = -1;
                        byte6 = -1;
                    }
                    for (int k33 = byte1; k33 != byte2; k33 += byte3) {
                        for (int l33 = byte4; l33 != byte5; l33 += byte6) {
                            int i34 = k33 + i17;
                            int j34 = l33 + j21;
                            for (int k34 = 0; k34 < 4; k34++)
                                if (i34 >= 0 && j34 >= 0 && i34 < 104 && j34 < 104)
                                    groundArray[k34][k33][l33] = groundArray[k34][i34][j34];
                                else
                                    groundArray[k34][k33][l33] = null;
                        }
                    }
                    for (SpawnedObject class30_sub1_1 = (SpawnedObject) aClass19_1179.reverseGetFirst(); class30_sub1_1 != null; class30_sub1_1 = (SpawnedObject) aClass19_1179.reverseGetNext()) {
                        class30_sub1_1.anInt1297 -= i17;
                        class30_sub1_1.anInt1298 -= j21;
                        if (class30_sub1_1.anInt1297 < 0 || class30_sub1_1.anInt1298 < 0 || class30_sub1_1.anInt1297 >= 104 || class30_sub1_1.anInt1298 >= 104)
                            class30_sub1_1.unlink();
                    }
                    if (destX != 0) {
                        destX -= i17;
                        destY -= j21;
                    }
                    aBoolean1160 = false;
                    opcode = -1;
                    return true;

                case 201:
                    try {
                        playerIndex = incoming.readUShort();
                    } catch (Exception e) {
                    }
                    opcode = -1;
                    return true;

                case 208:
                    int i3 = incoming.readLEShort();
                    if (i3 >= 0)
                        method60(i3);
                    anInt1018 = i3;
                    opcode = -1;
                    return true;

                case 99:
                    minimapState = incoming.readUByte();
                    opcode = -1;
                    return true;

                case 75: {
                    int monster = incoming.readLEUShortA();
                    int interfaceId = incoming.readLEUShortA();

                    RSInterface.getInterfaceCache()[interfaceId].defaultMediaType = 2;
                    RSInterface.getInterfaceCache()[interfaceId].mediaID = monster;
                    opcode = -1;
                    return true;
                }

                case 198:
                    int npc_id = incoming.readInt();
                    int display_size = incoming.readInt();

                    int y_size = 85;

                    if (npc_id == 2042) {
                        y_size = 50;
                    } else if (npc_id == 448) {
                        y_size = 50;
                    }

                    npcDisplay = new Npc();
                    npcDisplay.definition = NpcDefinition.lookup(npc_id);
                    npcDisplay.size = npcDisplay.definition.size;
                    npcDisplay.rotation = npcDisplay.definition.rotation;
                    npcDisplay.definition.scaleY = display_size;
                    npcDisplay.definition.scaleXZ = display_size;
                    npcDisplay.idleAnimation = npcDisplay.definition.standingAnimation;
                    RSInterface.getInterfaceCache()[46700].childY[13] = y_size;
                    opcode = -1;
                    return true;

                // sync with server
                case 114:
                    game_message_id = incoming.readUShort();
                    game_message_time = incoming.readUShort();
                    game_message_context = incoming.readString();
                    buildSplitPrivateChatMenu();
                    opcode = -1;
                    return true;

                case 60:
                    anInt1269 = incoming.readUByte();
                    anInt1268 = incoming.readNegUByte();
                    while (incoming.currentOffset < pktSize) {
                        int k3 = incoming.readUByte();
                        method137(incoming, k3);
                    }
                    opcode = -1;
                    return true;

                case 35:
                    int l3 = incoming.readUByte();
                    int k11 = incoming.readUByte();
                    int j17 = incoming.readUByte();
                    int k21 = incoming.readUByte();
                    aBooleanArray876[l3] = true;
                    anIntArray873[l3] = k11;
                    anIntArray1203[l3] = j17;
                    anIntArray928[l3] = k21;
                    anIntArray1030[l3] = 0;
                    opcode = -1;
                    return true;

                case 174:
                    int i4 = incoming.readUShort();
                    int l11 = incoming.readUByte();
                    int k17 = incoming.readUShort();
                    if (aBoolean848 && !lowMem && anInt1062 < 50) {
                        anIntArray1207[anInt1062] = i4;
                        anIntArray1241[anInt1062] = l11;
                        anIntArray1250[anInt1062] = k17 + Sounds.anIntArray326[i4];
                        anInt1062++;
                    }
                    opcode = -1;
                    return true;

                case 104:
                    int slot2 = incoming.readNegUByte();
                    int top = incoming.readUByteA();
                    String playerOption = incoming.readString();
                    if (slot2 >= 1 && slot2 <= 5) {
                        if (playerOption.equalsIgnoreCase("null"))
                            playerOption = null;
                        atPlayerActions[slot2 - 1] = playerOption;
                        playerOptions[slot2 - 1] = top == 0;
                    }
                    opcode = -1;
                    return true;

                case 78:
                    destX = 0;
                    opcode = -1;
                    return true;

                case 253:
                    String s = incoming.readString();

                    if (s.endsWith(":tradereq:")) {
                        String s3 = s.substring(0, s.indexOf(":"));
                        long l17 = StringUtils.longForName(s3);
                        boolean flag2 = false;
                        for (int j27 = 0; j27 < ignoreCount; j27++) {
                            if (ignores[j27] != l17)
                                continue;
                            flag2 = true;

                        }
                        if (!flag2 && onTutorialIsland == 0)
                            pushMessage("wishes to trade with you.", 4, s3);

                    } else if (s.endsWith(":settingupdate:")) {
                        Settings.update(this);

                    } else if (s.endsWith(":keybinding:")) {
                        keybindManager.update();

                    } else if (s.endsWith(":duelreq:")) {
                        String s4 = s.substring(0, s.indexOf(":"));
                        long l18 = StringUtils.longForName(s4);
                        boolean flag3 = false;
                        for (int k27 = 0; k27 < ignoreCount; k27++) {
                            if (ignores[k27] != l18)
                                continue;
                            flag3 = true;

                        }
                        if (!flag3 && onTutorialIsland == 0)
                            pushMessage("wishes to duel with you.", 8, s4);
                    } else if (s.endsWith(":chalreq:")) {
                        String s5 = s.substring(0, s.indexOf(":"));
                        long l19 = StringUtils.longForName(s5);
                        boolean flag4 = false;
                        for (int l27 = 0; l27 < ignoreCount; l27++) {
                            if (ignores[l27] != l19)
                                continue;
                            flag4 = true;

                        }
                        if (!flag4 && onTutorialIsland == 0) {
                            String s8 = s.substring(s.indexOf(":") + 1, s.length() - 9);
                            pushMessage(s8, 8, s5);
                        }
                    } else if (s.endsWith(":resetautocast:")) {
                        autocast = false;
                        autoCastId = 0;
                        spriteCache.get(43).drawSprite(-100, -100);
                    } else {
                        pushMessage(s, 0, "");
                    }

                    opcode = -1;
                    return true;

                case 1:
                    for (int k4 = 0; k4 < playerArray.length; k4++)
                        if (playerArray[k4] != null)
                            playerArray[k4].emoteAnimation = -1;
                    for (int j12 = 0; j12 < npcs.length; j12++)
                        if (npcs[j12] != null)
                            npcs[j12].emoteAnimation = -1;
                    opcode = -1;
                    return true;

                case 50: {
                    long username = incoming.readLong();
                    int world = incoming.readUByte();
                    int display = incoming.readUByte();
                    String name = StringUtils.fixName(StringUtils.nameForLong(username));

                    for (int index = 0; index < friendsCount; index++) {
                        if (username != friendsListAsLongs[index])
                            continue;
                        if (friendsNodeIDs[index] != world) {
                            friendsNodeIDs[index] = world;
                            if (world >= 2 && display == 1) {
                                pushMessage(name + " has logged in.", 5, "");
                            }
                            if (world <= 1 && display == 1) {
                                pushMessage(name + " has logged out.", 5, "");
                            }
                        }
                        name = null;

                    }
                    if (name != null && friendsCount < 200) {
                        friendsListAsLongs[friendsCount] = username;
                        friendsList[friendsCount] = name;
                        friendsNodeIDs[friendsCount] = world;
                        friendsCount++;
                    }
                    for (boolean sort = false; !sort; ) {
                        sort = true;
                        for (int index = 0; index < friendsCount - 1; index++)
                            if (friendsNodeIDs[index] != nodeID && friendsNodeIDs[index + 1] == nodeID || friendsNodeIDs[index] == 0 && friendsNodeIDs[index + 1] != 0) {
                                int j31 = friendsNodeIDs[index];
                                friendsNodeIDs[index] = friendsNodeIDs[index + 1];
                                friendsNodeIDs[index + 1] = j31;
                                String s10 = friendsList[index];
                                friendsList[index] = friendsList[index + 1];
                                friendsList[index + 1] = s10;
                                long l32 = friendsListAsLongs[index];
                                friendsListAsLongs[index] = friendsListAsLongs[index + 1];
                                friendsListAsLongs[index + 1] = l32;
                                sort = false;
                            }
                    }
                    opcode = -1;
                    return true;
                }

                case 110:
                    energy = incoming.readUByte();
                    opcode = -1;
                    return true;

                case 254:
                    anInt855 = incoming.readUByte();
                    if (anInt855 == 1)
                        anInt1222 = incoming.readUShort();
                    if (anInt855 >= 2 && anInt855 <= 6) {
                        if (anInt855 == 2) {
                            anInt937 = 64;
                            anInt938 = 64;
                        }
                        if (anInt855 == 3) {
                            anInt937 = 0;
                            anInt938 = 64;
                        }
                        if (anInt855 == 4) {
                            anInt937 = 128;
                            anInt938 = 64;
                        }
                        if (anInt855 == 5) {
                            anInt937 = 64;
                            anInt938 = 0;
                        }
                        if (anInt855 == 6) {
                            anInt937 = 64;
                            anInt938 = 128;
                        }
                        anInt855 = 2;
                        anInt934 = incoming.readUShort();
                        anInt935 = incoming.readUShort();
                        anInt936 = incoming.readUByte();
                    }
                    if (anInt855 == 10)
                        anInt933 = incoming.readUShort();
                    opcode = -1;
                    return true;

                case 248:
                    int i5 = incoming.readUShortA();
                    int k12 = incoming.readUShort();
                    if (backDialogueId != -1) {
                        backDialogueId = -1;
                        redrawDialogueBox = true;
                    }
                    if (inputDialogState != 0) {
                        inputDialogState = 0;
                        redrawDialogueBox = true;
                    }
                    openInterfaceID = i5;
                    invOverlayInterfaceID = k12;
                    tabAreaAltered = true;
                    aBoolean1149 = false;
                    opcode = -1;
                    return true;

                case 79:
                    int j5 = incoming.readLEUShort();
                    int l12 = incoming.readUShortA();
                    RSInterface class9_3 = RSInterface.getInterfaceCache()[j5];
                    if (class9_3 != null && class9_3.type == 0) {
                        if (l12 < 0)
                            l12 = 0;
                        if (l12 > class9_3.scrollMax - class9_3.height)
                            l12 = class9_3.scrollMax - class9_3.height;
                        class9_3.scrollPosition = l12;
                    }
                    opcode = -1;
                    return true;

                case 68:
                    for (int k5 = 0; k5 < settings.length; k5++)
                        if (settings[k5] != anIntArray1045[k5]) {
                            settings[k5] = anIntArray1045[k5];
                            method33(k5);
                        }
                    opcode = -1;
                    return true;

                case 196:
                    long l5 = incoming.readLong();
                    incoming.readInt();
                    int l21 = incoming.readUByte();
                    boolean flag5 = false;
                    if (l21 <= 1) {
                        for (int l29 = 0; l29 < ignoreCount; l29++) {
                            if (ignores[l29] != l5)
                                continue;
                            flag5 = true;

                        }
                    }
                    if (!flag5 && onTutorialIsland == 0)
                        try {
                            String s9 = TextInput.decode(pktSize - 13, incoming);
                            if (Settings.PROFANITY_FILTER && l21 != 3) {
                                s9 = Censor.doCensor(s9);
                            }
                            if (l21 >= 1)
                                pushMessage(s9, 7, "@cr" + l21 + "@" + StringUtils.fixName(StringUtils.nameForLong(l5)));
                            else
                                pushMessage(s9, 3, StringUtils.fixName(StringUtils.nameForLong(l5)));
                        } catch (Exception exception1) {
                            Utility.reporterror("cde1");
                        }
                    opcode = -1;
                    return true;

                case 85:
                    anInt1269 = incoming.readNegUByte();
                    anInt1268 = incoming.readNegUByte();
                    opcode = -1;
                    return true;

                case 24:
                    anInt1054 = incoming.readUByte();

                    if (anInt1054 == tabID) {
                        if (anInt1054 == 3)
                            tabID = 1;
                        else
                            tabID = 3;
                    }
                    opcode = -1;
                    return true;

                case 246:
                    int i6 = incoming.readLEUShort();
                    int i13 = incoming.readUShort();
                    int k18 = incoming.readUShort();
                    if (k18 == 65535) {
                        RSInterface.getInterfaceCache()[i6].defaultMediaType = 0;
                        opcode = -1;
                        return true;
                    } else {
                        ItemDefinition itemDef = ItemDefinition.lookup(k18);
                        RSInterface.getInterfaceCache()[i6].defaultMediaType = 4;
                        RSInterface.getInterfaceCache()[i6].mediaID = k18;
                        RSInterface.getInterfaceCache()[i6].modelRotation1 = itemDef.spritePitch;
                        RSInterface.getInterfaceCache()[i6].modelRotation2 = itemDef.spriteCameraRoll;
                        RSInterface.getInterfaceCache()[i6].modelZoom = (itemDef.spriteScale * 100) / i13;
                        opcode = -1;
                        return true;
                    }

                case 171:
                    boolean flag1 = incoming.readUByte() == 1;
                    int j13 = incoming.readUShort();
                    RSInterface.getInterfaceCache()[j13].isMouseoverTriggered = flag1;
                    opcode = -1;
                    return true;

                case 142:
                    int j6 = incoming.readLEUShort();
                    method60(j6);
                    if (backDialogueId != -1) {
                        backDialogueId = -1;
                        redrawDialogueBox = true;
                    }
                    if (inputDialogState != 0) {
                        inputDialogState = 0;
                        redrawDialogueBox = true;
                    }
                    invOverlayInterfaceID = j6;
                    tabAreaAltered = true;
                    openInterfaceID = -1;
                    aBoolean1149 = false;
                    opcode = -1;
                    return true;

                case 137:
                    try {
                        final int special = incoming.readUByte();
                        specialOrb = special;

                        for (WeaponInterface wI : WeaponInterface.values()) {
                            final int specBarMeter = wI.getSpecialMeter();
                            if (specBarMeter == -1) {
                                continue;
                            }
                            RSInterface rsi = RSInterface.getInterfaceCache()[specBarMeter];
                            if (rsi == null) {
                                continue;
                            }
                            rsi.disabledMessage = "Special Attack: " + special + "%";
                            rsi.enabledMessage = "@yel@Special Attack: " + special + "%";
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    opcode = -1;
                    return true;

                case 126:
                    try {
                        String text = incoming.readString();
                        int id = incoming.readUShortA();
                        if (cheapHaxPacket(id, text)) {
                            opcode = -1;
                            return true;
                        }
                        sendString(text, id);
                        if (text.equals("[CLOSE_MENU]")) {
                            menuOpen = false;
                        } else if (text.equals("[CONSTRUCTION_MAP]")) {
                            constructionMap = true;
                        } else if (text.equals("[NOARMAL_MAP]")) {
                            constructionMap = false;
                        }
                    } catch (Exception ignored) {
                    }
                    opcode = -1;
                    return true;

                case 129:
                    try {
                        int id = incoming.readUShort();
                        int amount = incoming.readUShort();
                        String message = incoming.readString();

                        if (RSInterface.getInterfaceCache()[id] != null) {
                            RSInterface.getInterfaceCache()[id].percentage = amount;
                            RSInterface.getInterfaceCache()[id].disabledMessage = message;
                        }
                    } catch (Exception e) {
                    }
                    opcode = -1;
                    return true;

                case 189:
                    try {
                        String text = incoming.readString();
                        byte state = incoming.readSignedByte();
                        byte seconds = incoming.readSignedByte();
                        fadingScreen = new FadingScreen(text, state, seconds);
                    } catch (Exception e) {
                    }
                    opcode = -1;
                    return true;

                case 191:
                    try {
                        String title = incoming.readString();
                        String text = incoming.readString();
                        int item = incoming.readUShort();
                        NotificationFeed(title, text, item);
                    } catch (Exception e) {
                    }
                    opcode = -1;
                    return true;

                case 173:
                    try {
                        String killer = incoming.readString();
                        String victim = incoming.readString();
                        pushKill(killer, victim);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    opcode = -1;
                    return true;

                case 138:
                    try {
                        String url = incoming.readString();
                        Utility.launchURL(url);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    opcode = -1;
                    return true;

                case 202:
                    try {
                        String title = incoming.readString();
                        String message = incoming.readString();
                        int col = incoming.readInt();
                        BannerManager.addBanner(this, title, message, col);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    opcode = -1;
                    return true;

                case 204:
                    try {
                        int scrollBar = incoming.readInt();
                        int size = incoming.readInt();

                        if (RSInterface.getInterfaceCache()[scrollBar] != null) {
                            if (RSInterface.getInterfaceCache()[scrollBar].scrollMax != size) {
                                RSInterface.getInterfaceCache()[scrollBar].scrollPosition = 0;
                                RSInterface.getInterfaceCache()[scrollBar].scrollMax = size;
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    opcode = -1;
                    return true;

                case 203:
                    try {
                        String message = incoming.readString();
                        int interfaceId = incoming.readUShortA();

                        if (RSInterface.getInterfaceCache()[interfaceId] != null) {
                            RSInterface.getInterfaceCache()[interfaceId].tooltip = message;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    opcode = -1;
                    return true;

                case 205:
                    try {
                        int interfaceId = incoming.readUShortA();
                        String message1 = incoming.readString();
                        String message2 = incoming.readString();
                        String message3 = incoming.readString();
                        String message4 = incoming.readString();
                        String message5 = incoming.readString();

                        if (RSInterface.getInterfaceCache()[interfaceId] != null) {
                            RSInterface.getInterfaceCache()[interfaceId].marqueeMessages[0] = message1;
                            RSInterface.getInterfaceCache()[interfaceId].marqueeMessages[1] = message2;
                            RSInterface.getInterfaceCache()[interfaceId].marqueeMessages[2] = message3;
                            RSInterface.getInterfaceCache()[interfaceId].marqueeMessages[3] = message4;
                            RSInterface.getInterfaceCache()[interfaceId].marqueeMessages[4] = message5;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    opcode = -1;
                    return true;

                case 182:
                    try {
                        poisonType = incoming.readNegUByte();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    opcode = -1;
                    return true;

                case 183:
                    try {
                        specialEnabled = incoming.readNegUByte();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    opcode = -1;
                    return true;

                case 175:
                    try {
                        pushFeed(incoming.readString(), incoming.readUShort(), incoming.readUShort());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    opcode = -1;
                    return true;

                case 139:
                    try {
                        cameraZoom = incoming.readUShort();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    opcode = -1;
                    return true;

                case 127:
                    try {
                        int skill = incoming.readUByte();
                        int exp = incoming.readInt();
                        boolean increment = incoming.readUByte() == 1;
                        ExpCounter.addXP(skill, exp, increment);

                        if (increment && skill != -1 && skill != 99) {
                            SkillOrbs.orbs[skill].receivedExperience();
                        }
                    } catch (Exception e) {
                    }
                    opcode = -1;
                    return true;

                case 178:
                    try {
                        Widget.place(incoming.readUByte(), incoming.readUShort());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    opcode = -1;
                    return true;

                case 206:
                    publicChatMode = incoming.readUByte();
                    privateChatMode = incoming.readUByte();
                    clanChatMode = incoming.readUByte();
                    tradeMode = incoming.readUByte();
                    redrawDialogueBox = true;
                    opcode = -1;
                    return true;

                case 240:
                    if (tabID == 12) {
                    }
                    weight = incoming.readSignedWord();
                    opcode = -1;
                    return true;

                case 8:
                    int k6 = incoming.readLEUShortA();
                    int l13 = incoming.readUShort();
                    RSInterface.getInterfaceCache()[k6].defaultMediaType = 1;
                    RSInterface.getInterfaceCache()[k6].mediaID = l13;
                    opcode = -1;
                    return true;

                case 122:
                    int intId = incoming.readLEUShortA();
                    int color = incoming.readInt();
                    if (RSInterface.getInterfaceCache()[intId] != null) {
                        RSInterface.getInterfaceCache()[intId].textColor = color;
                    }
                    opcode = -1;
                    return true;

                case 123:
                    int fontIntId = incoming.readLEUShortA();
                    int font = incoming.readInt();

                    TextDrawingArea aTextDrawingArea_1273 = new TextDrawingArea(true, "q8_full", titleStreamLoader);
                    TextDrawingArea tda[] = {smallFont, regularText, boldText, aTextDrawingArea_1273};

                    if (RSInterface.getInterfaceCache()[fontIntId] != null) {
                        RSInterface.getInterfaceCache()[fontIntId].textDrawingAreas = tda[font];
                    }
                    opcode = -1;
                    return true;

                case 53:
                    int i7 = incoming.readUShort();
                    RSInterface class9_1 = RSInterface.getInterfaceCache()[i7];
                    int j19 = incoming.readUShort();
                    if(class9_1.inv == null || class9_1.inv.length < j19) {
                        class9_1.inv = new int[j19 + 2];
                        class9_1.invStackSizes = new int[j19 + 2];
                    }
                    for (int j22 = 0; j22 < j19; j22++) {
                        int i25 = incoming.readUByte();
                        if (i25 == 255)
                            i25 = incoming.method440();
                        class9_1.inv[j22] = incoming.readLEUShortA();
                        class9_1.invStackSizes[j22] = i25;
                    }
                    for (int j25 = j19; j25 < class9_1.inv.length; j25++) {
                        class9_1.inv[j25] = 0;
                        class9_1.invStackSizes[j25] = 0;
                    }

                    if (class9_1.contentType == 206) {
                        for (int tab = 0; tab < 10; tab++) {
                            int amount = incoming.readSignedByte() << 8 | incoming.readUShort();
                            tabAmounts[tab] = amount;
                        }

                        if ((!RSInterface.getInterfaceCache()[60019].disabledMessage.isEmpty() && !RSInterface.getInterfaceCache()[60019].defaultInputFieldText.equalsIgnoreCase(RSInterface.getInterfaceCache()[60019].disabledMessage))) {
                            RSInterface bank = RSInterface.getInterfaceCache()[5382];
                            Arrays.fill(bankInvTemp, 0);
                            Arrays.fill(bankStackTemp, 0);
                            int bankSlot = 0;
                            for (int slot = 0; slot < bank.inv.length; slot++) {
                                if (bank.inv[slot] - 1 > 0) {
                                    ItemDefinition def = ItemDefinition.lookup(bank.inv[slot] - 1);
                                    if (def == null || def.name == null) continue;
                                    if (def.name.toLowerCase().contains(RSInterface.getInterfaceCache()[60019].disabledMessage.toLowerCase())) {
                                        bankInvTemp[bankSlot] = class9_1.inv[slot];
                                        bankStackTemp[bankSlot++] = class9_1.invStackSizes[slot];
                                    }
                                }
                            }
                            RSInterface.getInterfaceCache()[5385].scrollMax = (int) Math.ceil(bankSlot / 9.0) * 36;
                        }
                    }
                    opcode = -1;
                    return true;

                case 230:
                    int j7 = incoming.readUShortA();
                    int j14 = incoming.readUShort();
                    int k19 = incoming.readUShort();
                    int k22 = incoming.readLEUShortA();
                    RSInterface.getInterfaceCache()[j14].modelRotation1 = k19;
                    RSInterface.getInterfaceCache()[j14].modelRotation2 = k22;
                    RSInterface.getInterfaceCache()[j14].modelZoom = j7;
                    opcode = -1;
                    return true;

                case 221:
                    anInt900 = incoming.readUByte();
                    opcode = -1;
                    return true;

                case 177:
                    aBoolean1160 = true;
                    anInt995 = incoming.readUByte();
                    anInt996 = incoming.readUByte();
                    anInt997 = incoming.readUShort();
                    anInt998 = incoming.readUByte();
                    anInt999 = incoming.readUByte();
                    if (anInt999 >= 100) {
                        int k7 = anInt995 * 128 + 64;
                        int k14 = anInt996 * 128 + 64;
                        int i20 = method42(plane, k14, k7) - anInt997;
                        int l22 = k7 - xCameraPos;
                        int k25 = i20 - zCameraPos;
                        int j28 = k14 - yCameraPos;
                        int i30 = (int) Math.sqrt(l22 * l22 + j28 * j28);
                        yCameraCurve = (int) (Math.atan2(k25, i30) * 325.94900000000001D) & 0x7ff;
                        xCameraCurve = (int) (Math.atan2(l22, j28) * -325.94900000000001D) & 0x7ff;
                        if (yCameraCurve < 128)
                            yCameraCurve = 128;
                        if (yCameraCurve > 383)
                            yCameraCurve = 383;
                    }
                    opcode = -1;
                    return true;

                case 249:
                    anInt1046 = incoming.readUByteA();
                    unknownInt10 = incoming.readLEUShortA();
                    opcode = -1;
                    return true;

                case 65:
                    updateNPCs(incoming, pktSize);
                    opcode = -1;
                    return true;

                case 27:
                    inputMessage = incoming.readString();
                    inputLength = incoming.readUShortA();
                    messagePromptRaised = false;
                    inputDialogState = 1;
                    amountOrNameInput = "";
                    //                    redrawDialogueBox = true;
                    opcode = -1;
                    return true;

                case 187:
                    inputMessage = incoming.readString();
                    inputLength = incoming.readUShortA();
                    messagePromptRaised = false;
                    inputDialogState = 2;
                    amountOrNameInput = "";
                    redrawDialogueBox = true;
                    opcode = -1;
                    return true;

                case 97:
                    int l7 = incoming.readUShort();
                    method60(l7);
                    if (invOverlayInterfaceID != -1) {
                        invOverlayInterfaceID = -1;
                        tabAreaAltered = true;
                    }
                    if (backDialogueId != -1) {
                        backDialogueId = -1;
                        redrawDialogueBox = true;
                    }
                    if (inputDialogState != 0) {
                        inputDialogState = 0;
                        redrawDialogueBox = true;
                    }
                    if (l7 == 15244) {
                        openInterfaceID = 15767;
                        fullscreenInterfaceID = 15244;
                    } else if (l7 == 450) {
                        openInterfaceID = 450;
                        fullscreenInterfaceID = 450;
                    } else {
                        openInterfaceID = l7;
                    }
                    aBoolean1149 = false;
                    opcode = -1;
                    return true;

                case 218:
                    int i8 = incoming.method438();
                    dialogueId = i8;
                    redrawDialogueBox = true;
                    opcode = -1;
                    return true;

                case 87:
                    int j8 = incoming.readLEUShort();
                    int l14 = incoming.method439();
                    anIntArray1045[j8] = l14;
                    if (settings[j8] != l14) {
                        settings[j8] = l14;
                        method33(j8);
                        if (dialogueId != -1)
                            redrawDialogueBox = true;
                    }
                    opcode = -1;
                    return true;

                case 36:
                    int configId = incoming.readLEUShort();
                    byte value = incoming.readSignedByte();
                    anIntArray1045[configId] = value;
                    if (settings[configId] != value) {
                        settings[configId] = value;
                        method33(configId);
                        if (dialogueId != -1)
                            redrawDialogueBox = true;
                    }

                    if (configId == 203) {
                        Settings.PROFANITY_FILTER = (value != 0);
                    } else if (configId == 659) {
                        prayClicked = value != 0;
                    }

                    opcode = -1;
                    return true;

                case 61:
                    anInt1055 = incoming.readUByte();
                    opcode = -1;
                    return true;

                case 200:
                    int l8 = incoming.readUShort();
                    int i15 = incoming.readSignedWord();
                    RSInterface class9_4 = RSInterface.getInterfaceCache()[l8];
                    class9_4.anInt257 = i15;
                    if (i15 == 591 || i15 == 588) {
                        class9_4.modelZoom = 850;
                    }
                    opcode = -1;
                    return true;

                case 219:
                    int closeInterface = incoming.readUByte();
                    if (closeInterface == 1 && invOverlayInterfaceID != -1) {
                        invOverlayInterfaceID = -1;
                        tabAreaAltered = true;
                    }
                    if (backDialogueId != -1) {
                        backDialogueId = -1;
                        redrawDialogueBox = true;
                    }
                    if (inputDialogState != 0) {
                        inputDialogState = 0;
                        redrawDialogueBox = true;
                    }
                    if (closeInterface == 1) {
                        openInterfaceID = -1;
                    }
                    if (fullscreenInterfaceID != -1) {
                        fullscreenInterfaceID = -1;
                        openInterfaceID = -1;
                    }
                    aBoolean1149 = false;
                    opcode = -1;
                    return true;

                case 34: {
                    int interfaceId = incoming.readUShort();
                    RSInterface rsint = RSInterface.getInterfaceCache()[interfaceId];

                    while (incoming.currentOffset < pktSize) {
                        int slot = incoming.method422();
                        int itemId = incoming.readUShort();
                        int amount = incoming.readUByte();

                        if (amount == 255) {
                            amount = incoming.readInt();
                        }

                        if (slot >= 0 && slot < rsint.inv.length) {
                            rsint.inv[slot] = itemId;
                            rsint.invStackSizes[slot] = amount;
                        }
                    }
                }
                opcode = -1;
                return true;

                case 4:
                case 44:
                case 84:
                case 101:
                case 105:
                case 117:
                case 147:
                case 151:
                case 156:
                case 160:
                case 215:
                    method137(incoming, opcode);
                    opcode = -1;
                    return true;

                case 103:
                    try {
                        int type = incoming.readInt();
                        int modification = incoming.readInt();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    opcode = -1;
                    return true;

                case 106:
                    tabID = incoming.readNegUByte();
                    tabAreaAltered = true;
                    opcode = -1;
                    return true;

                case 108: {
                    int width = incoming.readLEUShortA();
                    int height = incoming.readInt();
                    frameMode(width, height);
                    opcode = -1;
                    return true;
                }

                case 164:
                    int j9 = incoming.readLEUShort();
                    method60(j9);
                    //                    if (invOverlayInterfaceID != -1) {
                    //                        invOverlayInterfaceID = -1;
                    //                        tabAreaAltered = true;
                    //                    }
                    backDialogueId = j9;
                    redrawDialogueBox = true;
                    //openInterfaceID = -1;
                    aBoolean1149 = false;
                    opcode = -1;
                    return true;

            }
            Utility.reporterror("T1 - " + opcode + "," + pktSize + " - " + lastOpcode2 + "," + lastOpcode3);
            // resetLogout();
        } catch (IOException _ex) {
            dropClient();
        } catch (Exception exception) {
            StringBuilder builder = new StringBuilder("T2 - last three opcodes: [");
            builder.append(lastOpcode1).append(',');
            builder.append(lastOpcode2).append(',');
            builder.append(lastOpcode3).append("] - packet size: ");
            builder.append(pktSize).append(", position=[");
            builder.append(baseX + localPlayer.smallX[0]).append(", ");
            builder.append(baseY + localPlayer.smallY[0]).append("]\n");
            builder.append("\t").append(exception).append('\n');
            for (StackTraceElement element : exception.getStackTrace())
                builder.append("\t\tat ").append(element).append('\n');
            Utility.reporterror(builder.toString());
            // resetLogout();
        }
        opcode = -1;
        return true;
    }

    private boolean cheapHaxPacket(int id, String text) {
        if (id == 23050) {
            if (text.equals("on")) 			drawOpponentStats = true;
            else if (text.equals("off")) 	drawOpponentStats = false;
            return true;
        } else if (id == 1998) {
            if (text.equals("on")) 		specActive = true;
            else if (text.equals("off")) 	specActive = false;
            return true;
        }
	     else if (id >= 37331 && id <= 37381) {
        	RSInterface ach = RSInterface.getInterfaceCache()[id];
        	if (text.contains("%")) {
        		String[] remove = text.split("%");
        		ach.achievementPercent = Integer.parseInt(remove[remove.length - 1]);
        		text = text.replaceAll("%"+ach.achievementPercent, "");
        	}
        	sendString(text, id);
        	return true;
        }
        return false;
    }

    private void drawGameWorld() {
        anInt1265++;
        method47(true);
        method26(true);
        method47(false);
        method26(false);
        method55();
        method104();
        if (!aBoolean1160) {
            int i = anInt1184;
            if (anInt984 / 256 > i)
                i = anInt984 / 256;
            if (aBooleanArray876[4] && anIntArray1203[4] + 128 > i)
                i = anIntArray1203[4] + 128;
            int k = minimapInt1 + anInt896 & 0x7ff;
            setCameraPos(cameraZoom + (frameWidth >= 1024 ? i + cameraZoom - frameHeight / 200 : i) * (SceneGraph.viewDistance == 9 && frameMode == ScreenMode.RESIZABLE ? 1 : SceneGraph.viewDistance == 10 ? 1 : 3), i, anInt1014, method42(plane, localPlayer.y, localPlayer.x) - 50, k, anInt1015);
        }
        int j;
        if (!aBoolean1160)
            j = method120();
        else
            j = method121();
        int l = xCameraPos;
        int i1 = zCameraPos;
        int j1 = yCameraPos;
        int k1 = yCameraCurve;
        int l1 = xCameraCurve;
        for (int i2 = 0; i2 < 5; i2++)
            if (aBooleanArray876[i2]) {
                int j2 = (int) ((Math.random() * (double) (anIntArray873[i2] * 2 + 1) - (double) anIntArray873[i2]) + Math.sin((double) anIntArray1030[i2] * ((double) anIntArray928[i2] / 100D)) * (double) anIntArray1203[i2]);
                if (i2 == 0)
                    xCameraPos += j2;
                if (i2 == 1)
                    zCameraPos += j2;
                if (i2 == 2)
                    yCameraPos += j2;
                if (i2 == 3)
                    xCameraCurve = xCameraCurve + j2 & 0x7ff;
                if (i2 == 4) {
                    yCameraCurve += j2;
                    if (yCameraCurve < 128)
                        yCameraCurve = 128;
                    if (yCameraCurve > 383)
                        yCameraCurve = 383;
                }
            }
        int k2 = Rasterizer.anInt1481;
        Model.aBoolean1684 = true;
        Model.anInt1687 = 0;
        Model.anInt1685 = super.mouseX - (frameMode == ScreenMode.FIXED ? 4 : 0);
        Model.anInt1686 = super.mouseY - (frameMode == ScreenMode.FIXED ? 4 : 0);
        Raster.reset();
        worldController.method313(xCameraPos, yCameraPos, xCameraCurve, zCameraPos, j, yCameraCurve);
        worldController.clearObj5Cache();
        Iterator<Particle> iterator;
        Particle particle;
        if (Settings.PARTICLES) {
            iterator = displayedParticles.iterator();
            while (iterator.hasNext()) {
                particle = iterator.next();
                if (particle != null) {
                    particle.tick();
                    if (particle.isDead()) {
                        removeDeadParticles.add(particle);
                    } else {
                        ParticleDefinition def = particle.getDefinition();
                        int displayX = particle.getPosition().getX();
                        int displayY = particle.getPosition().getY();
                        int displayZ = particle.getPosition().getZ();
                        int width;
                        int height;
                        if (def.getSprite() == null) {
                            width = 8;
                            height = 8;
                        } else {
                            width = def.getSprite().width / 4;
                            height = def.getSprite().height / 4;
                        }
                        width = (int) (width * particle.getSize());
                        height = (int) (height * particle.getSize());
                        int[] projection = calcParticlePos(displayX, displayY, displayZ, width, height);
                        width = projection[5] - projection[3];
                        height = projection[6] - projection[4];
                        float size = particle.getSize();
                        int alpha = (int) (particle.getAlpha() * 255.0F);
                        int radius = (int) (4.0F * particle.getSize());
                        int srcAlpha = 256 - alpha;
                        int srcR = (particle.getColor() >> 16 & 255) * alpha;
                        int srcG = (particle.getColor() >> 8 & 255) * alpha;
                        int srcB = (particle.getColor() & 255) * alpha;
                        int y1 = projection[1] - radius;
                        if (y1 < 0) {
                            y1 = 0;
                        }
                        int y2 = projection[1] + radius;
                        if (y2 >= Raster.height) {
                            y2 = Raster.height - 1;
                        }
                        for (int iy = y1; iy <= y2; ++iy) {
                            int dy = iy - projection[1];
                            int dist = (int) Math.sqrt(radius * radius - dy * dy);
                            int x1 = projection[0] - dist;
                            if (x1 < 0) {
                                x1 = 0;
                            }
                            int x2 = projection[0] + dist;
                            if (x2 >= Raster.width) {
                                x2 = Raster.width - 1;
                            }
                            int pixel = x1 + iy * Raster.width;
                            try {
                                if (Rasterizer.depthBuffer != null) {
                                    if (Rasterizer.depthBuffer[pixel] >= projection[2] - size - 15 || Rasterizer.depthBuffer[pixel++] >= projection[2] + size + 15) {
                                        for (int ix = x1; ix <= x2; ++ix) {
                                            int dstR = (gameScreenIP.raster[pixel] >> 16 & 255) * srcAlpha;
                                            int dstG = (gameScreenIP.raster[pixel] >> 8 & 255) * srcAlpha;
                                            int dstB = (gameScreenIP.raster[pixel] & 255) * srcAlpha;
                                            int rgb = (srcR + dstR >> 8 << 16) + (srcG + dstG >> 8 << 8) + (srcB + dstB >> 8);
                                            gameScreenIP.raster[pixel++] = rgb;
                                        }
                                    } else {
                                        particle.setAlpha(0f);
                                    }
                                }
                            } catch (Exception exception) {
                            }
                        }
                    }
                }
            }
        } else {
            iterator = displayedParticles.iterator();
            while (iterator.hasNext()) {
                particle = iterator.next();
                if (particle != null) {
                    particle.tick();
                    if (particle.isDead()) {
                        removeDeadParticles.add(particle);
                    }
                }
            }
            displayedParticles.removeAll(removeDeadParticles);
            removeDeadParticles.clear();
        }
        displayedParticles.removeAll(removeDeadParticles);
        removeDeadParticles.clear();
        if (Settings.FOG) {

            if (!switchColor) {
                if (Rasterizer.fogColor != Rasterizer.newFogColor) {
                    switchColor = true;
                }
            }
            if (switchColor) {
                fadeStep++;
                if (fadeStep >= 100) {
                    fadeStep = 1;
                    switchColor = false;
                    Rasterizer.fogColor = Rasterizer.newFogColor;
                } else {
                    Rasterizer.fogColor = fadeColors(new Color(Rasterizer.fogColor), new Color(Rasterizer.newFogColor), fadeStep);
                }
            }

            Rasterizer.drawFog(2250, 3000);
        }
        updateEntities();
        drawHeadIcon();
        method37(k2);
        if (Settings.SNOW) {
            drawSnowflakes(0, 0);
        }
        render_ground_item_names();
        draw3dScreen();
        if (console.openConsole) {
            console.drawConsole(frameMode == ScreenMode.FIXED ? super.myWidth : frameWidth);
        }
        Sprite zoom_minus = new Sprite();
        if (Settings.SPEC_BUTTON) {
            if (specActive) {
                if (specButtonHovered) {
                    specbutton[5].drawSprite(frameMode == ScreenMode.FIXED ? 470 : frameWidth - 260, frameMode == ScreenMode.FIXED ? 165 : 80);
                } else {
                    specbutton[3].drawSprite(frameMode == ScreenMode.FIXED ? 470 : frameWidth - 260, frameMode == ScreenMode.FIXED ? 165 : 80);
                }
                specbutton[2].drawSprite(frameMode == ScreenMode.FIXED ? 478 : frameWidth - 252, frameMode == ScreenMode.FIXED ? 171 : 86);
            } else {
                if (specButtonHovered) {
                    specbutton[1].drawSprite(frameMode == ScreenMode.FIXED ? 470 : frameWidth - 260, frameMode == ScreenMode.FIXED ? 165 : 80);
                } else {
                    specbutton[0].drawSprite(frameMode == ScreenMode.FIXED ? 470 : frameWidth - 260, frameMode == ScreenMode.FIXED ? 165 : 80);
                }
                specbutton[4].drawSprite(frameMode == ScreenMode.FIXED ? 478 : frameWidth - 252, frameMode == ScreenMode.FIXED ? 171 : 86);
            }
        }
        if (frameMode != ScreenMode.FIXED) {
            drawChatArea();
            drawMinimap();
            drawTabArea();
        }
        gameScreenIP.drawGraphics(super.graphics, frameMode == ScreenMode.FIXED ? 4 : 0, frameMode == ScreenMode.FIXED ? 4 : 0);
        xCameraPos = l;
        zCameraPos = i1;
        yCameraPos = j1;
        yCameraCurve = k1;
        xCameraCurve = l1;
    }

    public static int fadeStep = 1;

    private final void minimapHovers() {
        final boolean fixed = frameMode == ScreenMode.FIXED;
        hpHover = fixed ? isMouseWithin(516, 571, 41, 72) : isMouseWithin(frameWidth - 210, frameWidth - 156, 48, 74);
        prayHover = fixed ? isMouseWithin(516, 571, 76, 106) : isMouseWithin(frameWidth - 209, frameWidth - 153, 84, 112);
        runHover = fixed ? isMouseWithin(529, 584, 108, 136) : isMouseWithin(frameWidth - 196, frameWidth - 143, 118, 146);
        counterHover = fixed ? isMouseWithin(516, 540, 22, 44) : isMouseWithin(frameWidth - 210, frameWidth - 186, 26, 47);
        worldHover = fixed ? isMouseWithin(713, 740, 129, 154) : isMouseWithin(frameWidth - 32, frameWidth - 6, 140, 165);
        specOrbHover = fixed ? isMouseWithin(559, 614, 137, 164) : isMouseWithin(frameWidth - 172, frameWidth - 115, 147, 176);
    }

    private void processMinimapActions() {
        final boolean fixed = frameMode == ScreenMode.FIXED;
        if (fixed ? super.mouseX >= 542 && super.mouseX <= 579 && super.mouseY >= 2 && super.mouseY <= 38 : super.mouseX >= frameWidth - 180 && super.mouseX <= frameWidth - 139 && super.mouseY >= 0 && super.mouseY <= 40) {
            menuActionName[1] = "Face North";
            menuActionID[1] = 696;
            menuActionRow = 2;
        }
        if (fixed ? super.mouseX >= 742 && super.mouseX <= 765 && super.mouseY >= 0 && super.mouseY <= 24 : super.mouseX >= frameWidth - 26 && super.mouseX <= frameWidth - 1 && super.mouseY >= 2 && super.mouseY <= 24) {
            menuActionName[1] = "Logout";
            menuActionID[1] = 700;
            menuActionRow = 2;
        }

        if (Settings.STATUS_ORB) {
            if (worldHover) {
                menuActionName[1] = constructionMap ? "Open construction builder" : "Open Teleporation Menu";
                menuActionID[1] = 850;
                menuActionRow = 2;
            }
            if (prayHover) {
                menuActionName[2] = prayClicked ? "Turn quick-prayers off" : "Turn quick-prayers on";
                menuActionID[2] = 1500;
                menuActionRow = 2;
                menuActionName[1] = "Select quick-prayers";
                menuActionID[1] = 1506;
                menuActionRow = 3;
            }
            if (runHover) {
                menuActionName[2] = settings[152] == 1 ? "Toggle Run" : "Toggle Run";
                menuActionID[2] = 1050;
                menuActionRow = 2;
                menuActionName[1] = "Rest";
                menuActionID[1] = 1059;
                menuActionRow = 3;
            }
            if (counterHover) {
                menuActionName[4] = displayCounter ? "Hide <col=FFB000>Exp counter" : "Show <col=FFB000>Exp counter";
                menuActionID[4] = 474;
                menuActionName[3] = "Manage <col=FFB000>Exp counter";
                menuActionID[3] = 475;
                menuActionName[2] = "Lock <col=FFB000>Experience";
                menuActionID[2] = 477;
                menuActionName[1] = "Reset <col=FFB000>Exp counter";
                menuActionID[1] = 476;
                menuActionRow = 5;
            }

            if (specOrbHover) {
                menuActionName[1] = "Special Attack Status";
            }
        }
    }

    private boolean runHover, prayHover, hpHover, prayClicked, counterHover, worldHover, autocast, specOrbHover, increaseHover, decreaseHover;

    private void loadAllOrbs(int xOffset) {
        loadHpOrb(xOffset);
        loadPrayerOrb(xOffset);
        loadRunOrb(xOffset);
        loadExperienceOrb();
        loadZoomButtons(xOffset);
        drawWorldMapOrb();
    }

    private void drawWorldMapOrb() {
        final boolean isFixed = frameMode == ScreenMode.FIXED;
        final int fixedXOffset = isFixed ? 0 : 0;
        final int fixedYOffset = isFixed ? 0 : 0;
        Sprite worldBack = spriteCache.get(52);
        if (frameMode == ScreenMode.FIXED) {
            worldBack.drawSprite(196 + fixedXOffset, 128 + fixedYOffset);
            spriteCache.get(worldHover ? 54 : 53).drawSprite(200 + fixedXOffset, 132 + fixedYOffset);
        } else {
            worldBack.drawSprite(frameWidth - 33 + fixedXOffset, 139 + fixedYOffset);
            spriteCache.get(worldHover ? 54 : 53).drawSprite(frameWidth - 29 + fixedXOffset, 143 + fixedYOffset);
        }
    }

    void loadPrayerOrb(int xOffset) {
        final boolean isFixed = frameMode == ScreenMode.FIXED;
        final int fixedXOffset = isFixed ? 0 : 8;
        final int fixedYOffset = isFixed ? 0 : 8;
        Sprite bg = spriteCache.get(prayHover ? 8 : 7);
        Sprite fg = spriteCache.get(prayClicked ? 2 : 1);
        bg.drawSprite(0 + xOffset + fixedXOffset, 75 + fixedYOffset);
        fg.drawSprite(27 + xOffset + fixedXOffset, 79 + fixedYOffset);
        int level = currentStats[5];
        int max = maxStats[5];
        double percent = level / (double) max;
        spriteCache.get(14).height = (int) (26 * (1 - percent));
        spriteCache.get(14).drawSprite(27 + xOffset + fixedXOffset, 79 + fixedYOffset);
        if (percent <= .25) {
            spriteCache.get(10).drawSprite1(30 + xOffset + fixedXOffset, 82 + fixedYOffset, 200 + (int) (50 * Math.sin(tick / 7.0)));
        } else {
            spriteCache.get(10).drawSprite(30 + xOffset + fixedXOffset, 82 + fixedYOffset);
        }
        smallFont.drawCenteredText(getOrbTextColor((int) (percent * 100)), 16 + xOffset + fixedXOffset, level + "", 101 + fixedYOffset, true);
    }

    void loadRunOrb(int xOffset) {
        final boolean isFixed = frameMode == ScreenMode.FIXED;
        final int fixedXOffset = isFixed ? 0 : 7;
        final int fixedYOffset = isFixed ? 0 : 10;
        Sprite bg = spriteCache.get(runHover ? 8 : 7);
        Sprite fg = spriteCache.get(settings[152] == 1 ? 4 : 3);
        bg.drawSprite(13 + xOffset + fixedXOffset, 107 + fixedYOffset);
        fg.drawSprite(40 + xOffset + fixedXOffset, 111 + fixedYOffset);
        int level = energy;
        double percent = level / (double) 100;
        spriteCache.get(14).height = (int) (26 * (1 - percent));
        spriteCache.get(14).drawSprite(40 + xOffset + fixedXOffset, 111 + fixedYOffset);
        if (percent <= .25) {
            spriteCache.get(settings[152] == 1 ? 12 : 11).drawSprite1(46 + xOffset + fixedXOffset, 115 + fixedYOffset, 200 + (int) (50 * Math.sin(tick / 7.0)));
        } else {
            spriteCache.get(settings[152] == 1 ? 12 : 11).drawSprite(46 + xOffset + fixedXOffset, 115 + fixedYOffset);
        }
        smallFont.drawCenteredText(getOrbTextColor((int) (percent * 100)), 29 + xOffset + fixedXOffset, level + "", 133 + fixedYOffset, true);
    }

    void loadZoomButtons(int xOffset) {
        final boolean isFixed = frameMode == ScreenMode.FIXED;
        final int fixedXOffset = isFixed ? 0 : 7;
        final int fixedYOffset = isFixed ? 0 : 10;
        Sprite increase = spriteCache.get(857);
        Sprite decrease = spriteCache.get(856);
        increase.drawSprite(21 + xOffset + fixedXOffset, 140 + fixedYOffset);
        decrease.drawSprite(21 + xOffset + fixedXOffset, 155 + fixedYOffset);
    }

    private int poisonType = 0;
    private int specialEnabled = 0;

    void loadHpOrb(int xOffset) {
        final boolean isFixed = frameMode == ScreenMode.FIXED;
        int fixedXOffset = isFixed ? 0 : 7;
        int hover = poisonType == 0 ? 8 : 7;
        Sprite bg = spriteCache.get(hpHover ? hover : 7);
        int id = 0;
        if (poisonType == 0)
            id = 0;
        if (poisonType == 1)
            id = 177;
        if (poisonType == 2)
            id = 5;
        Sprite fg = spriteCache.get(id);
        bg.drawSprite(0 + xOffset + fixedXOffset, isFixed ? 41 : 46);
        fg.drawSprite(27 + xOffset + fixedXOffset, isFixed ? 45 : 50);
        int level = currentStats[3];
        int max = maxStats[3];
        double percent = level / (double) max;
        spriteCache.get(14).height = (int) (26 * (1 - percent));
        spriteCache.get(14).drawSprite(27 + xOffset + fixedXOffset, isFixed ? 45 : 50);
        if (percent <= .25) {
            spriteCache.get(9).drawSprite1(33 + xOffset + fixedXOffset, isFixed ? 52 : 57, 200 + (int) (50 * Math.sin(tick / 7.0)));
        } else {
            spriteCache.get(9).drawSprite(33 + xOffset + fixedXOffset, isFixed ? 52 : 57);
        }
        smallFont.drawCenteredText(getOrbTextColor((int) (percent * 100)), 15 + xOffset + fixedXOffset, "" + (Settings.DAMAGE_MULTIPLIER ? level * 10 : level), isFixed ? 67 : 72, true);
  
    	if (percent < 1) {
        	if (regenHealthStart > 0) {
        		float difference = (int)(System.currentTimeMillis() - regenHealthStart);
        		float angle = (difference / REGEN_HEALTH_TIME) * 360.0f;
        		Raster.draw_arc(24 + xOffset + fixedXOffset, isFixed ? 42 : 47, 28, 28, 2, 90, -(int)angle, 0xff0000, 210, 0, false);
        		if (angle > 358.0f && level != lastHp)
        			regenHealthStart = System.currentTimeMillis();
        		lastHp = level;
        	}
        }
	
    
    }

    public int specialOrb = 0;

    void loadSpecialOrb(int xOffset) {
        final boolean isFixed = frameMode == ScreenMode.FIXED;
        final int fixedXOffset = isFixed ? 0 : 4;
        final int fixedYOffset = isFixed ? 0 : 13;
        Sprite image = spriteCache.get(specOrbHover ? 8 : 7);
        Sprite fill = spriteCache.get(specialEnabled == 0 ? 5 : 6);
        Sprite sword = spriteCache.get(55);
        double percent = specialOrb / (double) 100;
        image.drawSprite(43 + xOffset + fixedXOffset, 133 + fixedYOffset);
        fill.drawSprite(69 + xOffset + fixedXOffset, 137 + fixedYOffset);
        spriteCache.get(14).height = (int) (26 * (1 - percent));
        spriteCache.get(14).drawSprite(70 + xOffset + fixedXOffset, 138 + fixedYOffset);
        sword.drawSprite(74 + xOffset + fixedXOffset, 142 + fixedYOffset);
        smallFont.drawCenteredText(getOrbTextColor((int) (percent * 100)), 57 + xOffset + fixedXOffset, specialOrb + "", 160 + fixedYOffset, true);
   if (percent < 1) {
        	if (regenSpecStart > 0) {
        		float difference = (int)(System.currentTimeMillis() - regenSpecStart);
        		float angle = (difference / REGEN_SPEC_TIME) * 360.0f;
        		Raster.draw_arc(67 + xOffset + fixedXOffset, 135 + fixedYOffset, 28, 28, 2, 90, -(int)angle, 65535, 210, 0, false);
        		if (angle > 358.0f && specialOrb != lastSpec)
        			regenSpecStart = System.currentTimeMillis();
        		lastSpec = specialOrb;
        	}
        }
		
    
    }

    void loadExperienceOrb() {
        if (Settings.STATUS_ORB) {
            if (counterHover) {
                spriteCache.get(23).drawSprite(frameMode == ScreenMode.FIXED ? 1 : frameWidth - 210, frameMode == ScreenMode.FIXED ? 21 : 26);
            } else {
                spriteCache.get(22).drawSprite(frameMode == ScreenMode.FIXED ? 1 : frameWidth - 210, frameMode == ScreenMode.FIXED ? 21 : 26);
            }
        }
    }

    public int getOrbTextColor(int statusInt) {
        if (statusInt >= 75 && statusInt <= Integer.MAX_VALUE)
            return 0x00FF00;
        else if (statusInt >= 50 && statusInt <= 74)
            return 0xFFFF00;
        else if (statusInt >= 25 && statusInt <= 49)
            return 0xFF981F;
        else
            return 0xFF0000;
    }

    public int getOrbFill(int statusInt) {
        if (statusInt <= Integer.MAX_VALUE && statusInt >= 97)
            return 0;
        else if (statusInt <= 96 && statusInt >= 93)
            return 1;
        else if (statusInt <= 92 && statusInt >= 89)
            return 2;
        else if (statusInt <= 88 && statusInt >= 85)
            return 3;
        else if (statusInt <= 84 && statusInt >= 81)
            return 4;
        else if (statusInt <= 80 && statusInt >= 77)
            return 5;
        else if (statusInt <= 76 && statusInt >= 73)
            return 6;
        else if (statusInt <= 72 && statusInt >= 69)
            return 7;
        else if (statusInt <= 68 && statusInt >= 65)
            return 8;
        else if (statusInt <= 64 && statusInt >= 61)
            return 9;
        else if (statusInt <= 60 && statusInt >= 57)
            return 10;
        else if (statusInt <= 56 && statusInt >= 53)
            return 11;
        else if (statusInt <= 52 && statusInt >= 49)
            return 12;
        else if (statusInt <= 48 && statusInt >= 45)
            return 13;
        else if (statusInt <= 44 && statusInt >= 41)
            return 14;
        else if (statusInt <= 40 && statusInt >= 37)
            return 15;
        else if (statusInt <= 36 && statusInt >= 33)
            return 16;
        else if (statusInt <= 32 && statusInt >= 29)
            return 17;
        else if (statusInt <= 28 && statusInt >= 25)
            return 18;
        else if (statusInt <= 24 && statusInt >= 21)
            return 19;
        else if (statusInt <= 20 && statusInt >= 17)
            return 20;
        else if (statusInt <= 16 && statusInt >= 13)
            return 21;
        else if (statusInt <= 12 && statusInt >= 9)
            return 22;
        else if (statusInt <= 8 && statusInt >= 7)
            return 23;
        else if (statusInt <= 6 && statusInt >= 5)
            return 24;
        else if (statusInt <= 4 && statusInt >= 3)
            return 25;
        else if (statusInt <= 2 && statusInt >= 1)
            return 26;
        else if (statusInt <= 0)
            return 27;
        return 0;
    }

    public String entityFeedName;
    public int entityFeedHP;
    public int entityFeedMaxHP;
    public int entityFeedHP2;
    public int entityAlpha;
    private int entityTick;

    public void pushFeed(String entityName, int HP, int maxHP) {
        entityFeedHP2 = entityFeedHP <= 0 ? entityFeedMaxHP : entityFeedHP;
        entityFeedName = entityName;
        entityFeedHP = HP;
        entityFeedMaxHP = maxHP;
        entityAlpha = 255;
        entityTick = entityName.isEmpty() ? 0 : 600;
    }

    private void displayEntityFeed() {
        if (entityFeedName == null)
            return;
        if (entityFeedHP == 0)
            return;
        if (entityTick-- <= 0)
            return;

        double percentage = entityFeedHP / (double) entityFeedMaxHP;
        double percentage2 = (entityFeedHP2 - entityFeedHP) / (double) entityFeedMaxHP;
        int width = (int) (135 * percentage);

        if (width > 132)
            width = 132;

        int xOff = 3;
        int yOff = 25;

        // background
        Raster.fillRectangle(xOff, yOff, 141, 50, 0x4c433d, 155);
        Raster.drawRectangle(xOff, yOff, 141, 50, 0x332f2d, 255);

        // name
        newSmallFont.drawCenteredString(entityFeedName, xOff + 69, yOff + 23, 0xFDFDFD, 0);

        // Hp fill
        Raster.fillRectangle(xOff + 7, yOff + 32, width - 4, 12, 0x66b754, 130);
        Raster.fillRectangle(xOff + 7, yOff + 32, width - 4, 12, 0x66b754, 130);

        // Hp empty
        Raster.fillRectangle(xOff + 4 + width, yOff + 32, 135 - width - 4, 12, 0xc43636, 130);

        if (entityAlpha > 0) {
            entityAlpha -= 5;
            Raster.fillRectangle(xOff + 4 + width, yOff + 32, (int) (135 * percentage2) - 4, 12, 0xFFDB00, (int) (130 * entityAlpha / 255.0));
        }

        Raster.drawRectangle(xOff + 7, yOff + 32, 128, 12, 0x332f2d, 130);

        int hp = Settings.DAMAGE_MULTIPLIER ? (entityFeedHP * 10) : entityFeedHP;
        int maxHp = Settings.DAMAGE_MULTIPLIER ? (entityFeedMaxHP * 10) : entityFeedMaxHP;

        // HP text
        newSmallFont.drawCenteredString(NumberFormat.getInstance(Locale.US).format(hp) + " / " + NumberFormat.getInstance(Locale.US).format(maxHp), xOff + 72, yOff + 44, 0xFDFDFD, 0);
    }

    private String notificationTitle;
    private String notificationMessage;
    private Sprite notificationItem;

    public void NotificationFeed(String title, String message, int item) {
        this.notificationTitle = title;
        this.notificationMessage = message;
        this.notificationItem = ItemDefinition.getSprite(item, 1, 0, 2);
    }

    public void drawNotification() {
        if (notificationTitle == null || notificationTitle.isEmpty()) {
            return;
        }

        if (notificationMessage == null || notificationMessage.isEmpty()) {
            return;
        }

        if (notificationItem == null) {
            return;
        }

        Raster.drawAlphaPixels(5, 220, 135, 50, 0x000000, 115, true);
        Raster.drawAlphaPixels(8, 223, 50, 44, 0x535C5E, 115, true);

        notificationItem.drawSprite(16, 228, 0);

        newSmallFont.drawCenteredString("<u>" + notificationTitle, 98, 237, 0xFDFDFD, 0);
        newSmallFont.drawCenteredString(notificationMessage, 98, 258, 0xFDFDFD, 0);
    }

    public void clearTopInterfaces() {
        outgoing.writeOpcode(130);
        if (invOverlayInterfaceID != -1) {
            invOverlayInterfaceID = -1;
            aBoolean1149 = false;
            tabAreaAltered = true;
        }
        if (backDialogueId != -1) {
            backDialogueId = -1;
            redrawDialogueBox = true;
            aBoolean1149 = false;
        }
        openInterfaceID = -1;
        fullscreenInterfaceID = -1;
        // fixes the menu being opened from an interface interaction but no interface is shown
        menuOpen = false;
    }

    public Client() {
        displayedParticles = new ArrayList<Particle>(10000);
        removeDeadParticles = new ArrayList<Particle>();
        LP = 0.0F;
        bountyIcons = new Sprite[10];
        chatColors = new int[500];
        chatCachedNames = new String[500];
        chatTitles = new String[500];
        fullscreenInterfaceID = -1;
        chatRights = new int[500];
        chatPrivilages = new byte[500];
        chatTypeView = 0;
        clanChatMode = 0;
        cButtonHPos = -1;
        cButtonCPos = 0;
        cost = new int[104][104];
        friendsNodeIDs = new int[200];
        groundArray = new Deque[4][104][104];
        chatBuffer = new Buffer(new byte[5000]);
        npcs = new Npc[16384];
        npcIndices = new int[16384];
        removedMobs = new int[1000];
        aStream_847 = Buffer.create();
        aBoolean848 = true;
        openInterfaceID = -1;
        currentExp = new int[SkillConstants.SKILL_COUNT];
        aBoolean872 = false;
        anIntArray873 = new int[5];
        aBooleanArray876 = new boolean[5];
        reportAbuseInput = "";
        unknownInt10 = -1;
        menuOpen = false;
        inputString = "";
        maxPlayers = 2048;
        myPlayerIndex = 2047;
        playerArray = new Player[maxPlayers];
        playerIndices = new int[maxPlayers];
        mobsAwaitingUpdate = new int[maxPlayers];
        playerSynchronizationBuffers = new Buffer[maxPlayers];
        anInt897 = 1;
        pathDirections = new int[104][104];
        aByteArray912 = new byte[16384];
        currentStats = new int[SkillConstants.SKILL_COUNT];
        ignores = new long[100];
        loadingError = false;
        anIntArray928 = new int[5];
        anIntArrayArray929 = new int[104][104];
        chatTypes = new int[500];
        chatNames = new String[500];
        chatMessages = new String[500];
        sideIcons = new Sprite[16];
        aBoolean954 = true;
        friendsListAsLongs = new long[200];
        currentSong = -1;
        spriteDrawX = -1;
        spriteDrawY = -1;
        anIntArray968 = new int[33];
        decompressors = new Decompressor[5];
        settings = new int[10_000];
        aBoolean972 = false;
        anInt975 = 50;
        anIntArray976 = new int[anInt975];
        anIntArray977 = new int[anInt975];
        anIntArray978 = new int[anInt975];
        anIntArray979 = new int[anInt975];
        anIntArray980 = new int[anInt975];
        anIntArray981 = new int[anInt975];
        anIntArray982 = new int[anInt975];
        aStringArray983 = new String[anInt975];
        anInt985 = -1;
        hitMarks = new Sprite[20];
        hitMark = new Sprite[20];
        hitIcon = new Sprite[20];
        characterColor = new int[5];
        aBoolean994 = false;
        amountOrNameInput = "";
        aClass19_1013 = new Deque();
        aBoolean1017 = false;
        anInt1018 = -1;
        anIntArray1030 = new int[5];
        aBoolean1031 = false;
        mapFunctions = new Sprite[100];
        dialogueId = -1;
        maxStats = new int[SkillConstants.SKILL_COUNT];
        anIntArray1045 = new int[2000];
        characterGender = true;
        anIntArray1052 = new int[152];
        anIntArray1229 = new int[152];
        anInt1054 = -1;
        aClass19_1056 = new Deque();
        anIntArray1057 = new int[33];
        aClass9_1059 = new RSInterface();
        mapScenes = new Background[100];
        barFillColor = 0x4d4233;
        characterClothing = new int[7];
        anIntArray1072 = new int[1000];
        anIntArray1073 = new int[1000];
        aBoolean1080 = false;
        friendsList = new String[200];
        incoming = Buffer.create();
        expectedCRCs = new int[9];
        menuActionCmd2 = new int[500];
        menuActionCmd3 = new int[500];
        menuActionID = new int[500];
        menuActionCmd1 = new int[500];
        headIcons = new Sprite[20];
        skullIcons = new Sprite[20];
        headIconsHint = new Sprite[20];
        tabAreaAltered = false;
        aString1121 = "";
        atPlayerActions = new String[5];
        playerOptions = new boolean[5];
        anIntArrayArrayArray1129 = new int[4][13][13];
        anInt1132 = 2;
        aClass30_Sub2_Sub1_Sub1Array1140 = new Sprite[1000];
        aBoolean1141 = false;
        aBoolean1149 = false;
        crosses = new Sprite[8];
        musicEnabled = true;
        loggedIn = false;
        canMute = false;
        aBoolean1159 = false;
        aBoolean1160 = false;
        anInt1171 = 1;
        myUsername = "";
        myPassword = "";
        genericLoadingError = false;
        reportAbuseInterfaceID = -1;
        aClass19_1179 = new Deque();
        anInt1184 = 128;
        invOverlayInterfaceID = -1;
        outgoing = Buffer.create();
        menuActionName = new String[500];
        anIntArray1203 = new int[5];
        anIntArray1207 = new int[50];
        anInt1210 = 2;
        anInt1211 = 78;
        promptInput = "";
        modIcons = new Sprite[crownAmount];
        tabID = 3;
        redrawDialogueBox = false;
        songChanging = true;
        collisionMaps = new CollisionMap[4];
        anIntArray1241 = new int[50];
        aBoolean1242 = false;
        anIntArray1250 = new int[50];
        rsAlreadyLoaded = false;
        drawGameScreenSprite = false;
        messagePromptRaised = false;
        loginMessage1 = "";
        loginMessage2 = "";
        backDialogueId = -1;
        anInt1279 = 2;
        bigX = new int[4000];
        bigY = new int[4000];
    }

    private ArrayList<Particle> displayedParticles;
    private ArrayList<Particle> removeDeadParticles;

    public final void addParticle(Particle particle) {
        displayedParticles.add(particle);
    }

    public int rights;
    public String s1;
    public String message;
    public String clanname;
    private final int[] chatRights;
    public int chatTypeView;
    public int clanChatMode;
    public int autoCastId = 0;
    private GraphicsBuffer leftFrame;
    private GraphicsBuffer topFrame;
    private int ignoreCount;
    private long aLong824;
    private int[][] cost;
    private int[] friendsNodeIDs;
    private Deque[][][] groundArray;
    private Socket aSocket832;
    private Buffer chatBuffer;
    private Npc[] npcs;
    private int npcCount;
    private int[] npcIndices;
    private int removedMobCount;
    private int[] removedMobs;
    private int lastOpcode1;
    private int lastOpcode2;
    private int lastOpcode3;
    private String clickToContinueString;
    public String prayerBook;
    private int privateChatMode;
    private Buffer aStream_847;
    private boolean aBoolean848;
    private int anInt855;
    static int openInterfaceID;
    public int xCameraPos;
    public int zCameraPos;
    public int yCameraPos;
    private int yCameraCurve;
    private int xCameraCurve;
    private int myPrivilege;
    
    public int getBufferSize() {
		return myPrivilege;
	}
	public void setBufferSize(int myPrivilege) {
		this.myPrivilege = myPrivilege;
	}


	public final int[] currentExp;
    private Sprite mapFlag;
    private Sprite mapMarker;
    private boolean aBoolean872;
    private final int[] anIntArray873;
    private final boolean[] aBooleanArray876;
    private int weight;
    private MouseDetection mouseDetection;
    private String reportAbuseInput;
    private int unknownInt10;
    private boolean menuOpen;
    private boolean constructionMap = false;
    private int anInt886;
    public static String inputString;
    private final int maxPlayers;
    private final int myPlayerIndex;
    private Player[] playerArray;
    private int playerCount;
    private int[] playerIndices;
    private int mobsAwaitingUpdateCount;
    private int[] mobsAwaitingUpdate;
    private Buffer[] playerSynchronizationBuffers;
    private int anInt896;
    public int anInt897;
    private int friendsCount;
    private int anInt900;
    private int[][] pathDirections;
    private byte[] aByteArray912;
    private int crossX;
    private int crossY;
    private int crossIndex;
    private int crossType;
    private int plane;
    public final int[] currentStats;
    private final long[] ignores;
    private boolean loadingError;
    private final int[] anIntArray928;
    private int[][] anIntArrayArray929;
    private Sprite aClass30_Sub2_Sub1_Sub1_931;
    private Sprite aClass30_Sub2_Sub1_Sub1_932;
    private int anInt933;
    private int anInt934;
    private int anInt935;
    private int anInt936;
    private int anInt937;
    private int anInt938;
    private final int[] chatTypes;
    private final byte[] chatPrivilages;
    private final String[] chatNames;
    private final String[] chatCachedNames;
    private final String[] chatMessages;
    private int tickDelta;
    private SceneGraph worldController;
    private Sprite[] sideIcons;
    private int menuScreenArea;
    private int menuOffsetX;
    private int menuOffsetY;
    private int menuWidth;
    private int menuHeight;
    private long aLong953;
    private boolean aBoolean954;
    private long[] friendsListAsLongs;
    private int currentSong;
    private static int nodeID = 10;
    static int portOff;
    private static boolean isMembers = true;
    private static boolean lowMem;
    private int spriteDrawX;
    private int spriteDrawY;
    private final int[] anIntArray965 = {0xffff00, 0xff0000, 65280, 65535, 0xff00ff, 0xffffff};
    private final int[] anIntArray968;
    final Decompressor[] decompressors;
    public int settings[];
    private boolean aBoolean972;
    private final int anInt975;
    private final int[] anIntArray976;
    private final int[] anIntArray977;
    private final int[] anIntArray978;
    private final int[] anIntArray979;
    private final int[] anIntArray980;
    private final int[] anIntArray981;
    private final int[] anIntArray982;
    private final String[] aStringArray983;
    private int anInt984;
    private int anInt985;
    private Sprite[] hitMarks;
    private Sprite[] hitMark;
    private Sprite[] hitIcon;
    public int anInt988;
    private int dragCycle;
    private final int[] characterColor;
    private final boolean aBoolean994;
    private int anInt995;
    private int anInt996;
    private int anInt997;
    private int anInt998;
    private int anInt999;
    private ISAACRandomGen encryption;
    private Sprite multiOverlay;
    static final int[][] PLAYER_BODY_RECOLOURS = {{6798, 107, 10283, 16, 4797, 7744, 5799, 4634, 33697, 22433, 2983, 54193}, {8741, 12, 64030, 43162, 7735, 8404, 1701, 38430, 24094, 10153, 56621, 4783, 1341, 16578, 35003, 25239}, {25238, 8742, 12, 64030, 43162, 7735, 8404, 1701, 38430, 24094, 10153, 56621, 4783, 1341, 16578, 35003}, {4626, 11146, 6439, 12, 4758, 10270}, {4550, 4537, 5681, 5673, 5790, 6806, 8076, 4574, 15909, 32689}};
    private String amountOrNameInput;
    private int daysSinceLastLogin;
    private int pktSize;
    private int opcode;
    private int anInt1009;
    private int anInt1010;
    private int anInt1011;
    private Deque aClass19_1013;
    private int anInt1014;
    private int anInt1015;
    private int anInt1016;
    private boolean aBoolean1017;
    private int anInt1018;
    private static final int[] anIntArray1019;
    private int minimapState;
    private int anInt1022;
    public int loadingStage;
    public Sprite scrollBar1;
    public Sprite scrollBar2;
    private int anInt1026;
    private final int[] anIntArray1030;
    private boolean aBoolean1031;
    private Sprite[] mapFunctions;
    private int baseX;
    private int baseY;
    private int anInt1036;
    private int anInt1037;
    private int loginFailures;
    private int anInt1039;
    private int dialogueId;
    public final int[] maxStats;
    private final int[] anIntArray1045;
    private int anInt1046;
    private boolean characterGender;
    private int anInt1048;
    private final int[] anIntArray1052;
    public StreamLoader titleStreamLoader;
    private int anInt1054;
    private int anInt1055;
    private Deque aClass19_1056;
    private final int[] anIntArray1057;
    public final RSInterface aClass9_1059;
    private Background[] mapScenes;
    private int anInt1062;
    private final int barFillColor;
    private int friendsListAction;
    private final int[] characterClothing;
    private int mouseInvInterfaceIndex;
    private int lastActiveInvInterface;
    public ResourceProvider onDemandFetcher;
    private int anInt1069;
    private int anInt1070;
    private int anInt1071;
    private int[] anIntArray1072;
    private int[] anIntArray1073;
    private Sprite mapDotItem;
    private Sprite mapDotNPC;
    private Sprite mapDotPlayer;
    private Sprite mapDotFriend;
    private Sprite mapDotTeam;
    private Sprite mapDotClan;
    private boolean aBoolean1080;
    private String[] friendsList;
    private Buffer incoming;
    private int focusedDragWidget;
    private int dragFromSlot;
    private int activeInterfaceType;
    private int pressX;
    private int pressY;
    public static int anInt1089;
    public static int spellID = 0;
    public static int totalRead = 0;
    private final int[] expectedCRCs;
    protected int[] menuActionCmd2;
    protected int[] menuActionCmd3;
    protected int[] menuActionID;
    protected int[] menuActionCmd1;
    private Sprite[] headIcons;
    private Sprite[] skullIcons;
    public Sprite[] bountyIcons;
    private Sprite[] headIconsHint;
    private int anInt1098;
    private int anInt1099;
    private int anInt1100;
    private int anInt1101;
    private int anInt1102;
    private static boolean tabAreaAltered;
    private int anInt1104;
    public static GraphicsBuffer updaterScreenIP;
    public static GraphicsBuffer loginScreenIP;
    private int membersInt;
    private String aString1121;
    private Sprite compass;
    public static Player localPlayer;
    private final String[] atPlayerActions;
    private final boolean[] playerOptions;
    private final int[][][] anIntArrayArrayArray1129;
    public static final int[] tabInterfaceIDs = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    private int anInt1131;
    public int anInt1132;
    protected int menuActionRow;
    private int spellSelected;
    private int anInt1137;
    private int spellUsableOn;
    private String spellTooltip;
    private Sprite[] aClass30_Sub2_Sub1_Sub1Array1140;
    private boolean aBoolean1141;
    private int energy;
    private boolean aBoolean1149;
    private Sprite[] crosses;
    private boolean musicEnabled;
    private int unreadMessages;
    public static boolean loggedIn;
    private boolean canMute;
    private boolean aBoolean1159;
    private boolean aBoolean1160;
    public static int tick;
    public static final String validUserPassChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"\243$%^&*()-_=+[{]};:'@#~,<.>/?\\| ";
    private static GraphicsBuffer inventoryIP;
    private GraphicsBuffer mapIP;
    private static GraphicsBuffer gameScreenIP;
    private static GraphicsBuffer chatIP;
    private static GraphicsBuffer gameScreenFrameLeft;
    private static GraphicsBuffer gameScreenFrameTop;
    private int daysSinceRecovChange;
    private BufferedConnection socketStream;
    private int minimapInt3;
    public int anInt1171;
    public String myUsername;
    public String myPassword;
    private boolean genericLoadingError;
    private final int[] anIntArray1177 = {0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3};
    private int reportAbuseInterfaceID;
    private Deque aClass19_1179;
    private static int[] anIntArray1180;
    private static int[] anIntArray1181;
    private static int[] anIntArray1182;
    private byte[][] aByteArrayArray1183;
    private int anInt1184;
    private int minimapInt1;
    private int anInt1186;
    private int anInt1187;
    private int invOverlayInterfaceID;
    public float LP;
    public Buffer outgoing;
    private int anInt1193;
    private int splitPrivateChat;
    private Background mapBack;
    protected String[] menuActionName;
    private final int[] anIntArray1203;
    static final int[] anIntArray1204 = {9104, 10275, 7595, 3610, 7975, 8526, 918, 38802, 24466, 10145, 58654, 5027, 1457, 16565, 34991, 25486};
    private static boolean flagged;
    private final int[] anIntArray1207;
    private int minimapInt2;
    public int anInt1210;
    static int anInt1211;
    private String promptInput;
    private int anInt1213;
    private int[][][] intGroundArray;
    private long aLong1215;
    public int loginScreenCursorPos;
    private final Sprite[] modIcons;
    private final Sprite[] clanIcons = new Sprite[9];
    private final Sprite[] iconIcons = new Sprite[25];
    private long aLong1220;
    static int tabID;
    private int anInt1222;
    public static boolean redrawDialogueBox;
    private int inputDialogState;
    private int nextSong;
    private boolean songChanging;
    private final int[] anIntArray1229;
    private CollisionMap[] collisionMaps;
    public static int BIT_MASKS[];
    private int[] anIntArray1234;
    private int[] anIntArray1235;
    private int[] anIntArray1236;
    private int anInt1237;
    private int anInt1238;
    public final int anInt1239 = 100;
    private final int[] anIntArray1241;
    private boolean aBoolean1242;
    private int atInventoryLoopCycle;
    private int atInventoryInterface;
    private int atInventoryIndex;
    private int atInventoryInterfaceType;
    private byte[][] aByteArrayArray1247;
    private int tradeMode;
    private int anInt1249;
    private final int[] anIntArray1250;
    private int onTutorialIsland;
    private final boolean rsAlreadyLoaded;
    private int anInt1253;
    public int anInt1254;
    private boolean drawGameScreenSprite;
    private boolean messagePromptRaised;
    private byte[][][] byteGroundArray;
    private int prevSong;
    private int destX;
    private int destY;
    public Sprite minimapImage;
    private int anInt1264;
    private int anInt1265;
    public String loginMessage1;
    public String loginMessage2;
    private int anInt1268;
    private int anInt1269;
    public TextDrawingArea smallFont;
    public TextDrawingArea regularText;
    public TextDrawingArea boldText;
    public TextDrawingArea smallHit;
    public RSFont newSmallFont, newRegularFont, newBoldFont;
    public RSFont newFancyFont;
    public int backDialogueId;
    private int anInt1278;
    public int anInt1279;
    private int[] bigX;
    private int[] bigY;
    private int itemSelected;
    private int anInt1283;
    private int anInt1284;
    private int anInt1285;
    private String selectedItemName;
    private int publicChatMode;
    public static int anInt1290;

    public int fullscreenInterfaceID;
    public int anInt1044;// 377
    public int anInt1129;// 377
    public int anInt1315;// 377
    public int anInt1500;// 377
    public int anInt1501;// 377
    public static int[] fullScreenTextureArray;
    private int[] tabAmounts = new int[]{350, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private int[] bankInvTemp = new int[9 * 89];
    private int[] bankStackTemp = new int[9 * 89];

    private final int[] modeNamesX = {26, 86, 150, 212, 286, 349, 440}, modeNamesY = {158, 158, 153, 153, 153, 153, 159}, channelButtonsX = {5, 71, 137, 203, 269, 335, 404};

    private final String[] modeNames = {"All", "Game", "Public", "Private", "Clan", "Trade", "Report"};

    private static final String text[] = {"On", "Friends", "Off", "Hide"};
    private static final int textColor[] = {65280, 0xffff00, 0xff0000, 65535};

    public void resetAllImageProducers() {
        if (super.fullGameScreen != null) {
            return;
        }
        chatIP = null;
        mapIP = null;
        inventoryIP = null;
        gameScreenIP = null;
        loginScreenIP = null;
        updaterScreenIP = null;
        updaterRenderer = null;
        gameScreenFrameLeft = null;
        gameScreenFrameTop = null;
        super.fullGameScreen = new GraphicsBuffer(765, 503, getGameComponent());
        drawGameScreenSprite = true;
    }

    public void mouseWheelDragged(int i, int j) {
        if (!mouseWheelDown) {
            return;
        }

        int xx = 6 * i;
        int yy = -12 * j;
        this.anInt1186 += xx;
        this.anInt1187 += yy;
    }

    static final class BannerManager {

        static class Banner {

            private final String title;

            private final String message;

            private int x, x2;

            private final int color;

            private int opacity = 0;

            private long leasureTime = 0;
            private long leasureTime2 = 0;

            private Client client;

            public Banner(Client client, String title, String message, int color) {
                this.client = client;
                this.title = title;
                this.message = message;
                this.color = color;
                this.x = frameWidth - (frameMode == ScreenMode.FIXED ? 253 : 155);
                this.x2 = client.newRegularFont.getTextWidth(message);
            }

            public int getColor() {
                return color;
            }

            public String getTitle() {
                return title;
            }

            public String getMessage() {
                return message;
            }

            public int getX() {
                return x;
            }

            public int getX2() {
                return x2;
            }

            public int getOpacity() {
                return opacity;
            }

            public void update() {
                if (x <= 0) {
                    banner = null;
                    return;
                }

                int width = frameWidth - (frameMode == ScreenMode.FIXED ? 253 : 155);
                int delta = 4 * (1 << (int) (width / (765.0 - (frameMode == ScreenMode.FIXED ? 253 : 155))));

                if (x < width / 2.0) {
                    if (opacity - delta >= 0) {
                        opacity -= delta;
                    }
                } else {
                    if (opacity + delta <= 255) {
                        opacity += delta;
                    }
                }

                if (System.currentTimeMillis() - leasureTime >= 2500) {
                    x -= delta;
                }

                if (System.currentTimeMillis() - leasureTime2 >= 2500) {
                    x2 += delta;
                }

                if (leasureTime == 0) {
                    int center = (int) ((width + client.newFancyFont.getTextWidth(title)) / 2.0);
                    if (x >= center - delta && x <= center + delta) {
                        leasureTime = System.currentTimeMillis();
                    }
                }

                if (leasureTime2 == 0) {
                    int center = (int) ((width + client.newRegularFont.getTextWidth(message)) / 2.0);
                    if (x2 >= center - delta && x2 <= center + delta) {
                        leasureTime2 = System.currentTimeMillis();
                    }
                }
            }
        }

        public static Client instance;

        private static Banner banner;

        public static void addBanner(Client client, String text, String message, int color) {
            banner = new Banner(client, text, message, color);
        }

        public static void drawMovingBanner() {

            if (banner == null) {
                return;
            }

            Raster.drawAlphaPixels(0, 0, frameWidth, 50, banner.getColor(), banner.getOpacity(), true);
            banner.client.newFancyFont.drawRAString(banner.getTitle(), banner.getX(), 15, 0xFFFFFF, 0x0);
            banner.client.newRegularFont.drawRAString(banner.getMessage(), banner.getX2(), 35, 0xFFFFFF, 0x0);

            banner.update();
        }
    }

    private String[] feedKiller = new String[5];
    private String[] feedVictim = new String[5];
    private Sprite[] feedImage = new Sprite[5];
    private int[] feedAlpha = new int[5];
    private int[] feedYPos = new int[5];
    private int killsDisplayed = 5;

    public void pushKill(String killer, String victim) {
        for (int index = killsDisplayed - 1; index > 0; index--) {
            feedKiller[index] = feedKiller[index - 1];
            feedVictim[index] = feedVictim[index - 1];
            feedAlpha[index] = feedAlpha[index - 1];
            feedYPos[index] = feedYPos[index - 1];
        }
        feedKiller[0] = killer;
        feedVictim[0] = victim;
        feedAlpha[0] = 0;
        feedYPos[0] = 0;
    }

    public void clearKill(int index) {
        feedKiller[index] = null;
        feedVictim[index] = null;
        feedAlpha[index] = -1;
        feedYPos[index] = -1;
    }

    public void displayKillFeed() {
        int x = 5;
        for (int index = 0; index < killsDisplayed; index++) {
            if (feedKiller[index] != null && feedVictim[index] != null) {
                if (feedKiller[index].length() > 0) {
                    if (feedYPos[index] < (index + 1) * 22) {
                        feedYPos[index] += 1;
                        if (index == 0) {
                            feedAlpha[index] += 256 / 22;
                        }
                    } else if (feedYPos[index] == (index + 1) * 22) {
                        if (feedAlpha[index] > 200) {
                            feedAlpha[index] -= 1;
                        } else if (feedAlpha[index] <= 200 && feedAlpha[index] > 0) {
                            feedAlpha[index] -= 5;
                        }
                        if (feedAlpha[index] < 0) {
                            feedAlpha[index] = 0;
                        }
                        if (feedAlpha[index] == 0) {
                            clearKill(index);
                        }
                    }
                    if (feedAlpha[index] != 0) {
                        String killerText = feedKiller[index];
                        String victimText = feedVictim[index];
                        String statement = "<col=A1D490>" + killerText + "</col> has killed <col=D4A190>" + victimText + "</col>!";
                        Raster.fillRectangle(x, feedYPos[index] + 55, newSmallFont.getTextWidth(statement) + 18, 19, 0x29221D, feedAlpha[index]);
                        Raster.drawRectangle(x, feedYPos[index] + 55, newSmallFont.getTextWidth(statement) + 18, 19, 0x070606, feedAlpha[index]);
                        newSmallFont.drawBasicString("<trans=" + feedAlpha[index] + ">" + statement, x + 9, feedYPos[index] + 69, 0xffffff, 0);
                    }
                }
            }
        }
    }

    static {
        anIntArray1019 = new int[99];
        int i = 0;
        for (int j = 0; j < 99; j++) {
            int l = j + 1;
            int i1 = (int) ((double) l + 300D * Math.pow(2D, (double) l / 7D));
            i += i1;
            anIntArray1019[j] = i / 4;
        }
        BIT_MASKS = new int[32];
        i = 2;
        for (int k = 0; k < 32; k++) {
            BIT_MASKS[k] = i - 1;
            i += i;
        }
    }

    public boolean hover(int x1, int y1, Sprite drawnSprite) {
        return super.mouseX >= x1 && super.mouseX <= x1 + drawnSprite.width && super.mouseY >= y1 && super.mouseY <= y1 + drawnSprite.height;
    }

    public static int getXPForLevel(int level) {
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

    /**
     * Get the String residing on the clipboard.
     *
     * @return any text found on the Clipboard; if none found, return an empty
     * String.
     */
    public static String getClipboardContents() {
        String result = "";
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        // odd: the Object param of getContents is not currently used
        Transferable contents = clipboard.getContents(null);
        boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
        if (hasTransferableText) {
            try {
                result = (String) contents.getTransferData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                System.out.println(ex);
                ex.printStackTrace();
            }
        }
        return result;
    }

    public static void setClipboardContents(String aString) {
        StringSelection stringSelection = new StringSelection(aString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    public int getCurrentGameFrame() {
        return currentGameFrame;
    }

    public void setCurrentGameFrame(int currentGameFrame) {
        this.currentGameFrame = currentGameFrame;
    }

    public PrayerSystem.InterfaceData prayerGrabbed = null;
    
   public void releasePrayer() {
        if (prayerGrabbed != null) {
            int posX = frameMode == ScreenMode.FIXED ? super.mouseX - 550 : super.mouseX - (frameWidth - 196);
            int posY = frameMode == ScreenMode.FIXED ? super.mouseY - 205 : super.mouseY - (frameHeight - 303);
            if (frameMode != ScreenMode.FIXED) {
                if (changeTabArea) {
                    posY = super.mouseY - (frameWidth >= 1000 ? frameHeight - 303 : frameHeight - 340);
                } else {
                    posX = super.mouseX - (frameWidth - 215);
                    posY = super.mouseY - (frameHeight - 299);
                }
            }
            PrayerSystem.release(prayerGrabbed, posX, posY);
            prayerGrabbed = null;
        }
    }
    
}
