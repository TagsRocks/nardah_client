package com.nardah;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RSInterface {

	public RSInterface() {
	}

	/* Random */
	public Sprite sprites[];
	public Sprite disabledSprite;
	public Sprite enabledSprite;
	public static StreamLoader aClass44;
	private static Cache aMRUNodes_238;
	public TextDrawingArea textDrawingAreas;
	private static RSInterface[] interfaceCache;
	private static final Cache aMRUNodes_264 = new Cache(30);

	/* Strings */
	public String disabledMessage;
	public String tooltip;
	public String spellName;
	public String actions[];
	public String[] tooltips;
	public String popupString;
	public String enabledMessage;
	public String inputRegex = "";
	public String selectedActionName;
	public String defaultInputFieldText = "";

	/* Booleans */
	public boolean isInFocus;
	public boolean textShadow;
	public boolean centerText;
	public boolean aBoolean227;
	public boolean aBoolean235;
	public boolean aBoolean259;
	public boolean tooltipBounds;
	public boolean displayPercent;
	public boolean updateConfig = true;
	public boolean displayAmount = true;
	public boolean displayExamine = true;
	public boolean drawsTransparent;
	public boolean displayAsterisks;
	public boolean updatesEveryInput;
	public boolean usableItemInterface;
	public boolean isInventoryInterface;
	public boolean isMouseoverTriggered;
	public boolean endReached = false;

	/* Bytes */
	public byte opacity;

	/* Integers */
	public int interfaceId;
	public int type;
	public int inv[];
	public int width;
	public int alpha;
	public int height;
	public int mediaID;
	public int childY[];
	public int anInt208;
	public int textHoverColor;
	public int anInt219;
	public int defaultMediaType;
	public int parentID;
	public int anInt239;
	public int childX[];
	public int anInt246;
	public int anInt257;
	public int anInt258;
	public int anInt263;
	public int anInt265;
	public int modelZoom;
	public int textColor;
	public int hoverType;
	public int scrollMax;
	public int anInt255;
	public int anInt256;
	public int npcDisplay = 0;
	public int children[];
	public int spritesY[];
	public int characterLimit;
	public int spritesX[];
	public int contentType;
	public int atActionType;
	public int transparency;
	public int invSpritePadX;
	public int spellUsableOn;
	public int invSpritePadY;
	public int scrollPosition;
	public int scrollSpeed = 30;
	public int modelRotation1;
	public int modelRotation2;
	public int backgroundColor;
	public int requiredValues[];
	public int valueCompareType[];
	public int invStackSizes[];
	public int scripts[][];
	public int isMouseoverTriggereds;
	public static int currentInputFieldId;
	public int color;
	public boolean rounded;
	public double percentage;
	public String[] marqueeMessages = new String[5];
	public int pauseTicks = 20;

	public static final int BEGIN_READING_PRAYER_INTERFACE = 6;
	public static final int CUSTOM_PRAYER_HOVERS = 3;
	public static final int PRAYER_INTERFACE_CHILDREN = 80 + BEGIN_READING_PRAYER_INTERFACE + CUSTOM_PRAYER_HOVERS;
	public int hoverXOffset = 0;
	public int hoverYOffset = 0;
	public int spriteXOffset = 0;
	public int spriteYOffset = 0;
	public boolean regularHoverBox;

	/**
	 * Unpacks all the interfaces.
	 *
	 * @param streamLoader
	 * @param textDrawingAreas
	 * @param streamLoader_1
	 */
	public static void unpack(StreamLoader streamLoader, TextDrawingArea textDrawingAreas[], StreamLoader streamLoader_1) {
		aMRUNodes_238 = new Cache(50000);
		Buffer stream = new Buffer(streamLoader.getDataForName("data"));
		int i = -1;
		stream.readUShort();
		setInterfaceCache(new RSInterface[80_000]);
		while (stream.currentOffset < stream.buffer.length) {
			int k = stream.readUShort();
			if (k == 65535) {
				i = stream.readUShort();
				k = stream.readUShort();
			}
			RSInterface rsInterface = getInterfaceCache()[k] = new RSInterface();
			rsInterface.interfaceId = k;
			rsInterface.parentID = i;
			rsInterface.type = stream.readUByte();
			rsInterface.atActionType = stream.readUByte();
			rsInterface.contentType = stream.readUShort();
			rsInterface.width = stream.readUShort();
			rsInterface.height = stream.readUShort();
			rsInterface.opacity = (byte) stream.readUByte();
			rsInterface.hoverType = stream.readUByte();
			if (rsInterface.hoverType != 0) {
				rsInterface.hoverType = (rsInterface.hoverType - 1 << 8) + stream.readUByte();
			} else {
				rsInterface.hoverType = -1;
			}
			int operators = stream.readUByte();
			if (operators > 0) {
				rsInterface.valueCompareType = new int[operators];
				rsInterface.requiredValues = new int[operators];
				for (int index = 0; index < operators; index++) {
					rsInterface.valueCompareType[index] = stream.readUByte();
					rsInterface.requiredValues[index] = stream.readUShort();
				}

			}
			int scripts = stream.readUByte();
			if (scripts > 0) {
				rsInterface.scripts = new int[scripts][];
				for (int index = 0; index < scripts; index++) {
					int i3 = stream.readUShort();
					rsInterface.scripts[index] = new int[i3];
					for (int l4 = 0; l4 < i3; l4++)
						rsInterface.scripts[index][l4] = stream.readUShort();

				}

			}
			if (rsInterface.type == 0) {
				rsInterface.drawsTransparent = false;
				rsInterface.scrollMax = stream.readUShort();
				rsInterface.isMouseoverTriggered = stream.readUByte() == 1;
				int length = stream.readUShort();
				if (rsInterface.parentID == 5608) {
					rsInterface.children = new int[PRAYER_INTERFACE_CHILDREN];
					rsInterface.childX = new int[PRAYER_INTERFACE_CHILDREN];
					rsInterface.childY = new int[PRAYER_INTERFACE_CHILDREN];
					for (int index = 0; index < length; index++) {
						rsInterface.children[BEGIN_READING_PRAYER_INTERFACE + index] = stream.readUShort();
						rsInterface.childX[BEGIN_READING_PRAYER_INTERFACE + index] = stream.readSignedWord();
						rsInterface.childY[BEGIN_READING_PRAYER_INTERFACE + index] = stream.readSignedWord();
					}
				} else {
					rsInterface.children = new int[length];
					rsInterface.childX = new int[length];
					rsInterface.childY = new int[length];
					for (int j3 = 0; j3 < length; j3++) {
						rsInterface.children[j3] = stream.readUShort();
						rsInterface.childX[j3] = stream.readSignedWord();
						rsInterface.childY[j3] = stream.readSignedWord();
					}
				}
			}
			if (rsInterface.type == 1) {
				stream.readUShort();
				stream.readUByte();
			}
			if (rsInterface.type == 2) {
				rsInterface.inv = new int[rsInterface.width * rsInterface.height];
				rsInterface.invStackSizes = new int[rsInterface.width * rsInterface.height];
				rsInterface.aBoolean259 = stream.readUByte() == 1;
				rsInterface.isInventoryInterface = stream.readUByte() == 1;
				rsInterface.usableItemInterface = stream.readUByte() == 1;
				rsInterface.aBoolean235 = stream.readUByte() == 1;
				rsInterface.invSpritePadX = stream.readUByte();
				rsInterface.invSpritePadY = stream.readUByte();
				rsInterface.spritesX = new int[20];
				rsInterface.spritesY = new int[20];
				rsInterface.sprites = new Sprite[20];
				for (int j2 = 0; j2 < 20; j2++) {
					int k3 = stream.readUByte();
					if (k3 == 1) {
						rsInterface.spritesX[j2] = stream.readSignedWord();
						rsInterface.spritesY[j2] = stream.readSignedWord();
						String s1 = stream.readString();
						if (streamLoader_1 != null && s1.length() > 0) {
							int i5 = s1.lastIndexOf(",");
							rsInterface.sprites[j2] = method207(Integer.parseInt(s1.substring(i5 + 1)), streamLoader_1, s1.substring(0, i5));
						}
					}
				}
				rsInterface.actions = new String[5];
				for (int l3 = 0; l3 < 5; l3++) {
					rsInterface.actions[l3] = stream.readString();
					if (rsInterface.actions[l3].length() == 0) {
						rsInterface.actions[l3] = null;
					}
					if (rsInterface.parentID == 1644) {
						rsInterface.actions[2] = "Operate";
					}
					if (rsInterface.parentID == 3824) {
						rsInterface.actions[4] = "Buy 100";
					}
				}
			}
			if (rsInterface.type == 3) {
				rsInterface.aBoolean227 = stream.readUByte() == 1;
			}
			if (rsInterface.type == 4 || rsInterface.type == 1) {
				rsInterface.centerText = stream.readUByte() == 1;
				int k2 = stream.readUByte();
				if (textDrawingAreas != null) {
					rsInterface.textDrawingAreas = textDrawingAreas[k2];
				}
				rsInterface.textShadow = stream.readUByte() == 1;
			}
			if (rsInterface.type == 4) {
				rsInterface.disabledMessage = stream.readString();
				//rsInterface.disabledMessage = (rsInterface.interfaceId + " " + rsInterface.parentID);
				if (rsInterface.disabledMessage.contains("RuneScape")) {
					rsInterface.disabledMessage = rsInterface.disabledMessage.replace("RuneScape", Configuration.NAME);
				}
				rsInterface.enabledMessage = stream.readString();
				//rsInterface.enabledMessage = (rsInterface.interfaceId + " " + rsInterface.parentID);
			}
			if (rsInterface.type == 1 || rsInterface.type == 3 || rsInterface.type == 4)
				rsInterface.textColor = stream.readInt();
			if (rsInterface.type == 3 || rsInterface.type == 4) {
				rsInterface.anInt219 = stream.readInt();
				rsInterface.textHoverColor = stream.readInt();
				rsInterface.anInt239 = stream.readInt();
			}
			if (rsInterface.type == 5) {
				rsInterface.drawsTransparent = false;
				String s = stream.readString();
				if (streamLoader_1 != null && s.length() > 0) {
					int i4 = s.lastIndexOf(",");
					rsInterface.disabledSprite = method207(Integer.parseInt(s.substring(i4 + 1)), streamLoader_1, s.substring(0, i4));
				}
				s = stream.readString();
				if (streamLoader_1 != null && s.length() > 0) {
					int j4 = s.lastIndexOf(",");
					rsInterface.enabledSprite = method207(Integer.parseInt(s.substring(j4 + 1)), streamLoader_1, s.substring(0, j4));
				}
			}
			if (rsInterface.type == 6) {
				int l = stream.readUByte();
				if (l != 0) {
					rsInterface.defaultMediaType = 1;
					rsInterface.mediaID = (l - 1 << 8) + stream.readUByte();
				}
				l = stream.readUByte();
				if (l != 0) {
					rsInterface.anInt255 = 1;
					rsInterface.anInt256 = (l - 1 << 8) + stream.readUByte();
				}
				l = stream.readUByte();
				if (l != 0) {
					rsInterface.anInt257 = (l - 1 << 8) + stream.readUByte();
				} else {
					rsInterface.anInt257 = -1;
				}
				l = stream.readUByte();
				if (l != 0) {
					rsInterface.anInt258 = (l - 1 << 8) + stream.readUByte();
				} else {
					rsInterface.anInt258 = -1;
				}
				rsInterface.modelZoom = stream.readUShort();
				rsInterface.modelRotation1 = stream.readUShort();
				rsInterface.modelRotation2 = stream.readUShort();
			}
			if (rsInterface.type == 7) {
				rsInterface.inv = new int[rsInterface.width * rsInterface.height];
				rsInterface.invStackSizes = new int[rsInterface.width * rsInterface.height];
				rsInterface.centerText = stream.readUByte() == 1;
				int l2 = stream.readUByte();
				if (textDrawingAreas != null) {
					rsInterface.textDrawingAreas = textDrawingAreas[l2];
				}
				rsInterface.textShadow = stream.readUByte() == 1;
				rsInterface.textColor = stream.readInt();
				rsInterface.invSpritePadX = stream.readSignedWord();
				rsInterface.invSpritePadY = stream.readSignedWord();
				rsInterface.isInventoryInterface = stream.readUByte() == 1;
				rsInterface.actions = new String[5];
				for (int k4 = 0; k4 < 5; k4++) {
					rsInterface.actions[k4] = stream.readString();
					if (rsInterface.actions[k4].length() == 0)
						rsInterface.actions[k4] = null;
				}

			}
			if (rsInterface.atActionType == 2 || rsInterface.type == 2) {
				rsInterface.selectedActionName = stream.readString();
				rsInterface.spellName = stream.readString();
				rsInterface.spellUsableOn = stream.readUShort();
			}

			if (rsInterface.type == 8)
				rsInterface.disabledMessage = stream.readString();

			if (rsInterface.atActionType == 1 || rsInterface.atActionType == 4 || rsInterface.atActionType == 5 || rsInterface.atActionType == 6) {
				rsInterface.tooltip = stream.readString();
				if (rsInterface.tooltip.length() == 0) {
					if (rsInterface.atActionType == 1)
						rsInterface.tooltip = "Ok";
					if (rsInterface.atActionType == 4)
						rsInterface.tooltip = "Select";
					if (rsInterface.atActionType == 5)
						rsInterface.tooltip = "Select";
					if (rsInterface.atActionType == 6)
						rsInterface.tooltip = "Continue";
				}
			}
		}
		aClass44 = streamLoader;

		prayerBook();
		skills();
		configureLunar(textDrawingAreas);
		CustomInterface.decode(textDrawingAreas);
		repositionModernSpells();
		findFreeSlots();
		editAncientsTab(textDrawingAreas);
		cheapHackCodeGoesHere();
		itemHoverBox(textDrawingAreas);
		opponentStats(textDrawingAreas);
		achievementPopup(textDrawingAreas);
		newAchievementTab(textDrawingAreas);
		aMRUNodes_238 = null;
	}
	
	public static void newAchievementTab(TextDrawingArea[] tda) {
		RSInterface tab = addInterface(37300);
		String dir = "/NewAch/SPRITE";
        addSprite(37303, 5, dir);
        addText(37304, "Achievements", tda, 2, 0xff9040,  false, true);
        addText(37305, "Daily", tda, 0, 0xffffff,  true, true);
        addHoverButton(37306, dir, 10, 60, 16, "Previous", -1, 37307, 1);
        addHoveredButton(37307, dir, 11, 60, 16, 37308);
        addHoverButton(37309, dir, 12, 60, 16, "Next", -1, 37310, 1);
        addHoveredButton(37310, dir, 13, 60, 16, 37311);
        addText(37312, "The day ends in 00:00:00", tda, 0, 0xff9040,  true, true);
        addHoverButton(37313, dir, 15, 38, 38, "Rewards", -1, 37314, 1);
        addHoveredButton(37314, dir, 16, 38, 38, 37315);
        addButton(37316, 309, "Button");
        
        tab.totalChildren(16);
        tab.child(0, 37303, 3, 24);
        tab.child(1, 37330, 9, 48);
        tab.child(2, 37304, 20, 5);
        tab.child(3, 29410, 160, 5);
        tab.child(4, 29411, 160, 5);
        tab.child(5, 29413, 138, 5);
        tab.child(6, 29414, 138, 5);
        tab.child(7, 37306, 10, 31);
        tab.child(8, 37307, 10, 31);
        tab.child(9, 37309, 121, 31);
        tab.child(10, 37310, 121, 31);
        tab.child(11, 37305, 95, 32);
        tab.child(12, 37312, 95, 200);
        tab.child(13, 37313, 74, 220);
        tab.child(14, 37314, 74, 220);
        tab.child(15, 37316, 2, 4);
        
        
        
        RSInterface scroll = addInterface(37330);
        scroll.totalChildren(50);
        for (int i = 0; i < 50; ++i) {
        	addHoverText(37331 + i, "Achievement Name", "More Info", tda, 0, 0xff0000, 0xffffff, false, true, 140);
        	getInterfaceCache()[37331 + i].isAchievementText = true;
        	scroll.child(i, 37331 + i, 3, 2 + (i * 14));
        }
        
        scroll.width = 157;
        scroll.height = 141;
        scroll.scrollMax = 705;
        
        RSInterface hover = addInterface(37400);
        addOutlinedColorBox(37401, 0x3A3A3A, 136, 50, 200);
        addText(37402, "Progress:", tda, 1, 0x00c5ff,  true, true);
        addSprite(37403, 7, dir);
        addSprite(37404, 6, dir);
        addText(37405, "100%", tda, 0, 0xffffff,  true, true);
        int x = 4, y = 4;
        hover.totalChildren(5);
        hover.child(0, 37401, x, y);
        hover.child(1, 37402, 68 + x, 4 + y);
        hover.child(2, 37403, 15 + x, 28 + y);
        hover.child(3, 37404, 16 + x, 29 + y);
        hover.child(4, 37405, 68 + x, 30 + y);
	}
	public boolean isAchievementText = false;
	public int achievementPercent = 0;

	public static void achievementPopup(TextDrawingArea[] tda) {
		RSInterface tab = addInterface(36000);
		String dir = "/Achievements/SPRITE";
		addSprite(36001, 0, dir);
		addHoverButton(36002, dir, 1, 16, 16, "Close", -1, 36003, 1);
		addHoveredButton(36003, dir, 2, 16, 16, 36004);
		addText(36005, "Achievements", tda, 2, 0xFFA500, true, true);
		addText(36022, "Easy", tda, 2, 0xFFA500, true, true);
		addText(36250, "Rewards for completing this Achievement:", tda, 1, 0xFFA500, false, true);
		addSprite(36251, 5, dir);
		addSprite(36252, 6, dir);
		int x = 5, y = 5;
		tab.totalChildren(23);
		tab.child(0, 36001, x, y);
		tab.child(1, 36002, 473+x, 4+y);
		tab.child(2, 36003, 473+x, 4+y);
		tab.child(3, 36005, 248+x, 4+y);
		String[] titles = {"Easy", "Medium", "Hard", "Elite"};
		int xx = 8;
		for (int i = 0; i < 4; i++) {
			addHoverButton(36006 + i, dir, 3, 120, 22, "View", -1, 36010 + i, 1);
			addHoveredButton(36010 + i, dir, 4, 120, 22, 36014 + i);
			addText(36018 + i, titles[i], tda, 1, 0xff9040, true, true);
			tab.child(4 + i, 36006 + i, xx + x, 22 + y);
			tab.child(8 + i, 36010 + i, xx + x, 22 + y);
			tab.child(12 + i, 36018 + i, xx + x + 60, 23 + y);
			xx += 121;
		}
		tab.child(16, 36022, 74 + x, 67 + y);
		tab.child(17, 36023, 13 + x, 87 + y);
		tab.child(18, 36200, 152 + x, 62 + y);
		tab.child(19, 36220, 152 + x, 231 + y);
		tab.child(20, 36250, 176 + x, 210 + y);
		tab.child(21, 36251, 453 + x, 71 + y);
		tab.child(22, 36252, 453 + x, 71 + y);

		RSInterface info = addInterface(36200);
		addText(36201, "Achievement Name", tda, 2, 0xFFA500, true, true);
		addText(36202, "Progress: @gre@0% (0/0)", tda, 1, 0xffffff, false, true);
		info.totalChildren(8);
		info.child(0, 36201, 163, 6);
		info.child(1, 36202, 10, 30);
		for (int i = 0; i < 6; i++) {
			addText(36203 + i, "Description #"+i, tda, 0, 0xffffff, false, true);
			info.child(2 + i, 36203 + i, 10, 55 + (i*13));
		}

		RSInterface items = addInterface(36220);
		items.totalChildren(1);
		itemGroup(36221, 7, 2, 12, 6);
		fill(36221);
		items.child(0, 36221, 5, 2);
		items.width = 311;
		items.height = 74;
		items.scrollMax = 100;


		RSInterface scroll = addInterface(36023);
		scroll.totalChildren(100);
		for (int j = 0; j < 100; j++) {
			addHoverText(36037 + j, "Achievement: "+j, "Select", tda, 0, 0xff9040, false, true, 112);
			scroll.child(j, 36037 + j, 2, 2 + (j*13));
		}
		scroll.width = 112;
		scroll.height = 218;
		scroll.scrollMax = 1304;
	}

	public static void fill(int a) {
		RSInterface f = getInterfaceCache()[a];
		for (int i = 0; i < f.inv.length; i++) {
			f.inv[i] = 1039;
			f.invStackSizes[i] = 1;
		}
	}
	public static void itemGroup(int id, int w, int h, int x, int y) {
		RSInterface rsi = addInterface(id);
		rsi.width = w;
		rsi.height = h;
		rsi.inv = new int[w * h];
		rsi.invStackSizes = new int[w * h];
		rsi.usableItemInterface = false;
		rsi.isInventoryInterface = false;
		rsi.invSpritePadX = x;
		rsi.invSpritePadY = y;
		rsi.spritesX = new int[20];
		rsi.spritesY = new int[20];
		rsi.sprites = new Sprite[20];
		rsi.type = 2;
	}


   private static void opponentStats(TextDrawingArea[] tda) {
            RSInterface tab = addInterface(23050);
            addColorBox(23051, 0, 131, 114, 150);
            
            addText(23052, "Attack:", tda, 0, 0xffffff, false, true);
            addText(23053, "Strength:", tda, 0, 0xffffff, false, true);
            addText(23054, "Defence:", tda, 0, 0xffffff, false, true);
            addText(23055, "Hitpoints:", tda, 0, 0xffffff, false, true);
            addText(23056, "Ranged:", tda, 0, 0xffffff, false, true);
            addText(23057, "Magic:", tda, 0, 0xffffff, false, true);
            addText(23058, "Prayer:", tda, 0, 0xffffff, false, true);
            
            addText(23059, "You", tda, 0, 0xf8f107, true, true);
            addText(23060, "Them", tda, 0, 0xf80707, true, true);
            addLine(23061, 0xff9040, 19);
            addLine(23062, 0xff9040, 25);
            
            /* Your stats */
            addText(23063, "99", tda, 0, 0xffffff, true, true);
            addText(23064, "99", tda, 0, 0xffffff, true, true);
            addText(23065, "99", tda, 0, 0xffffff, true, true);
            addText(23066, "99", tda, 0, 0xffffff, true, true);
            addText(23067, "99", tda, 0, 0xffffff, true, true);
            addText(23068, "99", tda, 0, 0xffffff, true, true);
            addText(23069, "99", tda, 0, 0xffffff, true, true);
            
            /* Their stats */
            addText(23070, "99", tda, 0, 0xffffff, true, true);
            addText(23071, "99", tda, 0, 0xffffff, true, true);
            addText(23072, "99", tda, 0, 0xffffff, true, true);
            addText(23073, "99", tda, 0, 0xffffff, true, true);
            addText(23074, "99", tda, 0, 0xffffff, true, true);
            addText(23075, "99", tda, 0, 0xffffff, true, true);
            addText(23076, "99", tda, 0, 0xffffff, true, true);
            
            tab.totalChildren(26);
            tab.child(0, 23051, 0, 0);
            for (int i = 0; i < 7; ++i) {
                tab.child(1 + i, 23052 + i, 4, 16 + (i * 13));
            }
            tab.child(8, 23059, 75, 2);
            tab.child(9, 23060, 108, 2);
            tab.child(10, 23061, 67, 13);
            tab.child(11, 23062, 96, 13);
            for (int i = 0; i < 7; ++i) {
                tab.child(12 + i, 23063 + i, 75, 16 + (i * 13));
            }
            for (int i = 0; i < 7; ++i) {
                tab.child(19 + i, 23070 + i, 108, 16 + (i * 13));
            }
            
        }

	private static void itemHoverBox(TextDrawingArea[] tda) {
		RSInterface tab = addInterface(23000);
		addOutlinedColorBox(23001, 0x534a40, 145, 145, 200);
		addText(23002, "Item name", tda, 0, 0xff9040,  true, true);
		addText(23003, "Attack", tda, 0, 0xff9040,  false, true);
		addText(23004, "Defence", tda, 0, 0xff9040, false, true);
		addText(23005, "Stab:\\nSlash:\\nCrush:\\nMagic:\\nRange:", tda, 0, 0xff9040, false, true);
		addText(23006, "0\\n0\\n0\\n0\\n0", tda, 0, 0xffffff, true, true);
		addText(23007, "0\\n0\\n0\\n0\\n0", tda, 0, 0xffffff, true, true);
		addText(23008, "Other Bonuses", tda, 0, 0xff9040, false, true);
		addText(23009, "Strength Bonus:\\nPrayer Bonus:", tda, 0, 0xff9040, false, true);
		addText(23010, "0\\n0", tda, 0, 0xffffff, true, true);
		addLine(23011, 0xB7B7B7, 32);
		addLine(23012, 0xB7B7B7, 41);
		addLine(23013, 0xffffff, 74);
		int x = 8, y = 8;
		tab.totalChildren(13);
		tab.child(0, 23001, x, y);
		tab.child(1, 23002, 72 + x, 2 + y);
		tab.child(2, 23003, 48 + x, 16 + y);
		tab.child(3, 23004, 93 + x, 16 + y);
		tab.child(4, 23005, 2 + x, 30 + y);
		tab.child(5, 23006, 64 + x, 30 + y);
		tab.child(6, 23007, 114 + x, 30 + y);
		tab.child(7, 23008, 2 + x, 97 + y);
		tab.child(8, 23009, 2 + x, 110 + y);
		tab.child(9, 23010, 110 + x, 110 + y);
		tab.child(10, 23011, 48 + x, 27 + y);
		tab.child(11, 23012, 93 + x, 27 + y);
		tab.child(12, 23013, 2 + x, 109 + y);

		tab = addInterface(23020);
		addOutlinedColorBox(23021, 0x534a40, 145, 16, 200);
		addText(23022, "Press @gre@CTRL @whi@to view stats", tda, 0, 0xB7B7B7, false, true);
		tab.totalChildren(3);
		tab.child(0, 23021, x, y);
		tab.child(1, 23002, 72 + x, 2 + y);
		tab.child(2, 23022, 2 + x, 14 + y);
	}


	private static void editAncientsTab(TextDrawingArea[] tda) {
		RSInterface rsi = getInterfaceCache()[12855];

		//Smoke barrage
		rsi.childX[22] = 21;
		rsi.childY[22] = 154;

		//Shadow barrage
		rsi.childX[30] = 66;
		rsi.childY[30] = 154;

		//Annakarl teleport
		rsi.childX[44] = 111;
		rsi.childY[44] = 154;

		//Annakarl teleport
		rsi.childX[15] = 153;
		rsi.childY[15] = 154;

		//Ice barrage
		rsi.childX[7] = 21;
		rsi.childY[7] = 184;

		//Gharrock teleport
		rsi.childX[46] = 66;
		rsi.childY[46] = 184;

		extendChildren(rsi, 1);

		addNewSpell(18746, 9075, 564, 558, 0, 0, 0, 30013, 30007, 84, "Teleport to Bounty Target", "A teleportation spell", tda, 850, 850, 2, 2);

		rsi.child(74, 18746, 153, 125);

	}

	public static void extendChildren(RSInterface widget, int extendBy) {
		int[] childIds = new int[widget.children.length + extendBy];
		int[] childX = new int[widget.childX.length + extendBy];
		int[] childY = new int[widget.childY.length + extendBy];

		System.arraycopy(widget.children, 0, childIds, 0, widget.children.length);
		System.arraycopy(widget.childX, 0, childX, 0, widget.childX.length);
		System.arraycopy(widget.childY, 0, childY, 0, widget.childY.length);

		widget.totalChildren(childIds.length);

		System.arraycopy(childIds, 0, widget.children, 0, childIds.length - extendBy);
		System.arraycopy(childX, 0, widget.childX, 0, childX.length - extendBy);
		System.arraycopy(childY, 0, widget.childY, 0, childY.length - extendBy);
	}

	public static void addNewSpell(int ID, int r1, int r2, int r3, int ra1, int ra2, int ra3, int rune1, int rune2, int lvl, String name, String descr, TextDrawingArea[] TDA, int sid, int sid2, int suo, int type) {
		RSInterface rsInterface = addInterface(ID);
		rsInterface.interfaceId = ID;
		rsInterface.parentID = 1151;
		rsInterface.type = 5;
		rsInterface.atActionType = type;
		rsInterface.contentType = 0;
		rsInterface.hoverType = ID + 1;
		rsInterface.spellUsableOn = suo;
		rsInterface.selectedActionName = "Cast on";
		rsInterface.width = 20;
		rsInterface.height = 20;
		rsInterface.tooltip = "Cast <col=475154>" + name;
		rsInterface.spellName = name;
		rsInterface.valueCompareType = new int[4];
		rsInterface.requiredValues = new int[4];
		rsInterface.valueCompareType[0] = 3;
		rsInterface.requiredValues[0] = ra1;
		rsInterface.valueCompareType[1] = 3;
		rsInterface.requiredValues[1] = ra2;
		rsInterface.valueCompareType[2] = 3;
		rsInterface.requiredValues[2] = ra3;
		rsInterface.valueCompareType[3] = 3;
		rsInterface.requiredValues[3] = lvl;
		rsInterface.scripts = new int[4][];
		rsInterface.scripts[0] = new int[4];
		rsInterface.scripts[0][0] = 4;
		rsInterface.scripts[0][1] = 3214;
		rsInterface.scripts[0][2] = r1;
		rsInterface.scripts[0][3] = 0;
		rsInterface.scripts[1] = new int[4];
		rsInterface.scripts[1][0] = 4;
		rsInterface.scripts[1][1] = 3214;
		rsInterface.scripts[1][2] = r2;
		rsInterface.scripts[1][3] = 0;
		rsInterface.scripts[2] = new int[4];
		rsInterface.scripts[2][0] = 4;
		rsInterface.scripts[2][1] = 3214;
		rsInterface.scripts[2][2] = r3;
		rsInterface.scripts[2][3] = 0;
		rsInterface.scripts[3] = new int[3];
		rsInterface.scripts[3][0] = 1;
		rsInterface.scripts[3][1] = 6;
		rsInterface.scripts[3][2] = 0;
		rsInterface.disabledSprite = Client.spriteCache.get(sid);
		rsInterface.enabledSprite = Client.spriteCache.get(sid2);
		RSInterface INT = addInterface(ID + 1);
		INT.isMouseoverTriggered = true;
		INT.hoverType = -1;
		setChildren(9, INT);
		addLunarSprite(ID + 2, 580);
		setBounds(ID + 2, 0, 0, 0, INT);
		addText(ID + 3, "Level " + (lvl + 1) + ": " + name, 0xFF981F, true, true, 52, TDA, 1);
		setBounds(ID + 3, 90, 4, 1, INT);
		addText(ID + 4, descr, 0xAF6A1A, true, true, 52, TDA, 0);
		setBounds(ID + 4, 90, 21, 2, INT);
		setBounds(30016, 14, 48, 3, INT);
		setBounds(rune1, 74, 48, 4, INT);
		setBounds(rune2, 130, 48, 5, INT);
		addRuneText(ID + 5, ra1 + 1, r1, TDA);
		setBounds(ID + 5, 26, 79, 6, INT);
		addRuneText(ID + 6, ra2 + 1, r2, TDA);
		setBounds(ID + 6, 87, 79, 7, INT);
		addRuneText(ID + 7, ra3 + 1, r3, TDA);
		setBounds(ID + 7, 142, 79, 8, INT);
	}

	private static void cheapHackCodeGoesHere() {
		RSInterface crumbleUndead = getInterfaceCache()[19843];
		crumbleUndead.children = cheaphackftw(crumbleUndead.children);
		crumbleUndead.childX = cheaphackftw(crumbleUndead.childX);
		crumbleUndead.childY = cheaphackftw(crumbleUndead.childY);

		RSInterface lvl49 = getInterfaceCache()[20065];
		lvl49.children = cheaphackftw(lvl49.children);
		lvl49.childX = cheaphackftw(lvl49.childX);
		lvl49.childY = cheaphackftw(lvl49.childY);

		RSInterface lvl56 = getInterfaceCache()[20285];
		lvl56.children = cheaphackftw(lvl56.children);
		lvl56.childX = cheaphackftw(lvl56.childX);
		lvl56.childY = cheaphackftw(lvl56.childY);

		removeSomething(22862); // remove spec hover since its bugged
	}

	private static int[] cheaphackftw(int[] source) {
		List<Integer> list = new ArrayList<>();

		for (int i = 0; i < source.length; i++) {
			int childInterface = source[i];

			if (childInterface == 0) {
				continue;
			}

			list.add(childInterface);
		}

		int[] temp = new int[list.size()];

		for (int i = 0; i < list.size(); i++) {
			temp[i] = list.get(i);
		}

		return temp;
	}

	private static void repositionModernSpells() {
		getInterfaceCache()[19210].tooltip = "</col>Teleport @gre@Home";
		getInterfaceCache()[19220].disabledMessage = "Teleport to Edgeville";
		getInterfaceCache()[19222].disabledMessage = "Teleports you to Edgeville";

		RSInterface rsi = RSInterface.getInterfaceCache()[12424];
		for (int index = 0; index < rsi.children.length; index++) {
			switch (rsi.children[index]) {
				case 1185:
					rsi.childX[33] = 148;
					rsi.childY[33] = 150;
					break;
				case 1183: // wind wave
					rsi.childX[31] = 76;
					rsi.childY[31] = 149;
					break;
				case 1188: // earth wave
					rsi.childX[36] = 71;
					rsi.childY[36] = 172;
					break;
				case 1543:
					rsi.childX[46] = 96;
					rsi.childY[46] = 173;
					break;
				case 1193: // charge
					rsi.childX[41] = 49;
					rsi.childY[41] = 198;
					break;
				case 12435: // tele other falador
					rsi.childX[54] = 74;
					rsi.childY[54] = 198;
					break;
				case 12445: // teleblock
					rsi.childX[55] = 99;
					rsi.childY[55] = 198;
					break;
				case 6003: // lvl 6 enchant
					rsi.childX[57] = 122;
					rsi.childY[57] = 198;
					break;
				// 150 x is end of the line
				case 12455: // tele other camelot
					rsi.childX[56] = 147;
					rsi.childY[56] = 198;
					break;
			}
		}
	}

	private static void skills() {
		int next = 0;
		int[] ids = new int[]{8, 9, 11, 14, 17, 20, 23};
		String[] names = new String[]{"Attack", "Hitpoints", "Strength", "Defence", "Ranged", "Prayer", "Magic"};
		for (int id : ids) {
			String name = names[next++];
			int child = getInterfaceCache()[3917].children[id];
			getInterfaceCache()[child].tooltip = "Set @or1@" + name + "@whi@ level";
		}
	}

	public static void prayerBook() {

		/*RSInterface rsinterface = interfaceCache[5608];

		//Moves down chivalry
		rsinterface.childX[50 + BEGIN_READING_PRAYER_INTERFACE] = 4;
		rsinterface.childY[50 + BEGIN_READING_PRAYER_INTERFACE] = 194;

		rsinterface.childX[51 + BEGIN_READING_PRAYER_INTERFACE] = 11;
		rsinterface.childY[51 + BEGIN_READING_PRAYER_INTERFACE] = 196;

		rsinterface.childX[63 + BEGIN_READING_PRAYER_INTERFACE] = 7;
		rsinterface.childY[63 + BEGIN_READING_PRAYER_INTERFACE] = 190;

		//Moves piety to the right
		setBounds(19827, 41, 194, 52 + BEGIN_READING_PRAYER_INTERFACE, rsinterface);

		rsinterface.childX[53 + BEGIN_READING_PRAYER_INTERFACE] = 43;
		rsinterface.childY[53 + BEGIN_READING_PRAYER_INTERFACE] = 204;
		rsinterface.childX[64 + BEGIN_READING_PRAYER_INTERFACE] = 43;
		rsinterface.childY[64 + BEGIN_READING_PRAYER_INTERFACE] = 190;*/

		addPrayer(28001, "Activate @or1@Preserve", 31, 32, 690, -2, -1, 691, 692, 1, 708, 28003, 55);
		//setBounds(28001, 153, 158, 0, rsinterface); //Prayer glow sprite
		//setBounds(28002, 155, 158, 1, rsinterface); //Prayer sprites

		addPrayer(28004, "Activate @or1@Rigour", 31, 32, 690, -3, -5, 693, 694, 1, 710, 28006, 74);
		//setBounds(28004, 78, 194, 2, rsinterface); //Prayer glow sprite
		//setBounds(28005, 81, 198, 3, rsinterface); //Prayer sprites

		addPrayer(28007, "Activate @or1@Augury", 31, 32, 690, -3, -5, 695, 696, 1, 712, 28009, 77);
		//setBounds(28007, 115, 194, 4, rsinterface); //Prayer glow sprite
		//setBounds(28008, 118, 198, 5, rsinterface); //Prayer sprites

		addPrayerHover(28003, "Level 55\\nPreserve\\nBoosted stats last 50% longer.", -135, -60);
		//setBounds(28003, 153, 158, 86, rsinterface); //Hover box

		addPrayerHover(28006, "Level 74\\nRigour\\nIncreases your Ranged attack\\nby 20% and damage by 23%,\\nand your defence by 25%", -70, -100);
		//setBounds(28006, 84, 200, 87, rsinterface); //Hover box try change the x values

		addPrayerHover(28009, "Level 77\\nAugury\\nIncreases your Magic attack by\\n 25% and your defence by 25%", -110, -100);
		//setBounds(28009, 120, 198, 88, rsinterface); //Hover box
		
		PrayerSystem.prayerPlacement();
	}

	/**
	 * Begin of Methods
	 */
	public static void modelViewer(int interfaceId, int modelType, int entityId) {
		RSInterface rsi = createInterface(interfaceId);

		rsi.interfaceId = interfaceId;
		rsi.parentID = interfaceId;
		rsi.type = 6;
		rsi.atActionType = 0;
		rsi.contentType = 329;
		rsi.width = 136;
		rsi.height = 168;
		rsi.opacity = 0;
		rsi.hoverType = 0;
		rsi.modelZoom = 560;
		rsi.modelRotation1 = 150;
		rsi.modelRotation2 = 0;
		rsi.anInt257 = -1;
		rsi.anInt258 = -1;
		rsi.anInt255 = modelType;
		rsi.anInt256 = entityId;
		rsi.totalChildren(1);
		rsi.child(0, interfaceId + 1, 190, 150);
	}



	public static void addNpc(int ID, int npcId, int zoom) {
		RSInterface petCanvas = getInterfaceCache()[ID] = new RSInterface();
		petCanvas.interfaceId = ID;
		petCanvas.parentID = ID;
		petCanvas.type = 6;
		petCanvas.atActionType = 0;
		petCanvas.contentType = 3291;
		petCanvas.width = 136;
		petCanvas.height = 168;
		petCanvas.modelZoom = zoom;
		petCanvas.modelRotation1 = 150;
		petCanvas.modelRotation2 = 0;
		petCanvas.anInt257 = -1;
		petCanvas.anInt258 = -1;
		petCanvas.npcDisplay = npcId;
	}



	/* Add Npc */
	public static void addNpc(int ID) {
		RSInterface t = createInterface(ID);
		t.interfaceId = ID;
		t.parentID = ID;
		t.type = 6;
		t.atActionType = 0;
		t.contentType = 326;
		t.width = 136;
		t.height = 168;
		t.opacity = 0;
		t.hoverType = 0;
		t.modelZoom = 2500;
		t.modelRotation1 = 150;
		t.modelRotation2 = 0;
		t.anInt257 = -1;
		t.anInt258 = -1;
	}

	/* Add Head
	 * HAPPY_JOYFUL(588),
		CALM_TALK1(589),
		CALM_TALK2(590),
		DEFAULT(591),
		EVIL1(592),
		EVIL2(593),
		EVIL3(594),
		ANNOYED(595),
		DISTRESSED(596),
		DISTRESSED2(597),
		ALMOST_CRYING(598),
		BOWS_HEAD_SAD(598),
		DRUNK_LEFT(600),
		DRUNK_RIGHT(601),
		NOT_INTERESTED(602),
		SLEEPY(603),
		PLAIN_EVIL(604),
		LAUGH1(605),
		LAUGH2(606),
		LAUGH3(607),
		LAUGH4(608),
		EVIL_LAUGH(609),
		SAD(610),
		MORE_SAD(611),
		ON_ONE_HAND(612),
		NEARLY_CRYING(613),
		ANGRY1(614),
		ANGRY2(615),
		ANGRY3(616),
		ANGRY4(617);
	 *
	 * */
	static void addHead(int id, int type, int npcId, int animId, int width, int height, int zoom) {
		RSInterface rsinterface = addTab(id);
		rsinterface.type = 6;
		rsinterface.mediaID = npcId;
		rsinterface.defaultMediaType = type;
		rsinterface.modelZoom = zoom;
		rsinterface.modelRotation1 = 40;
		rsinterface.modelRotation2 = 1900;
		rsinterface.height = height;
		rsinterface.width = width;
		rsinterface.anInt258 = animId;
		rsinterface.anInt257 = animId;
	}

	private static RSInterface addTab(int id) {
		RSInterface Tab = createInterface(id);
		Tab.interfaceId = id;// 250
		Tab.parentID = id;// 236
		Tab.type = 0;// 262
		Tab.atActionType = 0;// 217
		Tab.contentType = 0;
		Tab.width = 512;// 220
		Tab.height = 334;// anint267
		Tab.opacity = (byte) 0;
		Tab.textColor = 0;
		return Tab;
	}

	/* Add Interface */
	public static RSInterface addInterface(int id) {
		RSInterface rsi = createInterface(id);
		rsi.interfaceId = id;
		rsi.parentID = id;
		rsi.width = 512;
		rsi.height = 334;
		return rsi;
	}

	public static RSInterface addFullScreenInterface(int id) {
		RSInterface rsi = createInterface(id);
		rsi.interfaceId = id;
		rsi.parentID = id;
		rsi.width = 765;
		rsi.height = 503;
		return rsi;
	}

	public static RSInterface addTabInterface(int id) {
		RSInterface tab = createInterface(id);
		tab.interfaceId = id;
		tab.parentID = id;
		tab.type = 0;
		tab.atActionType = 0;
		tab.contentType = 0;
		tab.width = 512;
		tab.height = 700;
		tab.opacity = (byte) 0;
		tab.hoverType = -1;
		return tab;
	}

	/* Add Sprite */
	public static void addSprite(int id, int spriteId) {
		RSInterface rsint = createInterface(id);
		rsint.interfaceId = id;
		rsint.parentID = id;
		rsint.type = 5;
		rsint.atActionType = 0;
		rsint.contentType = 0;
		rsint.opacity = 0;
		rsint.hoverType = 0;
		if (spriteId != -1) {
			rsint.disabledSprite = Client.spriteCache.get(spriteId);
			rsint.enabledSprite = Client.spriteCache.get(spriteId);
		}
		rsint.width = 0;
		rsint.height = 0;
		rsint.backgroundColor = -1;
	}

	private static void findFreeSlots() {
		for (int i = 0; i < getInterfaceCache().length; i++) {
			if (Configuration.DEBUG_INTERFACES && getInterfaceCache()[i] == null) {
				System.out.println("free slot: " + i);
			}
		}
	}

	private static RSInterface createInterface(int id) {
		if (Configuration.DEBUG_INTERFACES && getInterfaceCache()[id] != null) {
			System.out.println("overwritten interface: " + id);
		}
		RSInterface rsi = new RSInterface();
		getInterfaceCache()[id] = rsi;
		return rsi;
	}

	public static void addSprite(int id, Sprite sprite) {
		RSInterface tab = createInterface(id);
		tab.interfaceId = id;
		tab.parentID = id;
		tab.type = 5;
		tab.atActionType = 0;
		tab.contentType = 0;
		tab.opacity = (byte) 0;
		tab.hoverType = 52;
		tab.disabledSprite = sprite;
		tab.enabledSprite = sprite;
		tab.width = 512;
		tab.height = 334;
	}

	public static void addHDButton(int id, int sid, String tooltip) {
		RSInterface tab = createInterface(id);
		tab.interfaceId = id;
		tab.parentID = id;
		tab.type = 11;
		tab.atActionType = 1;
		tab.contentType = 0;
		tab.disabledSprite = Client.spriteCache.get(sid);
		tab.enabledSprite = Client.spriteCache.get(sid);
		tab.width = tab.disabledSprite.width;
		tab.height = tab.enabledSprite.height;
		tab.tooltip = tooltip;
	}

	public static void addColorBox(int id, int color, int width, int height, boolean rounded) {
		RSInterface tab = addInterface(id);
		tab.width = width;
		tab.height = height;
		tab.color = color;
		tab.rounded = rounded;
		tab.type = 10;
		tab.interfaceId = id;
		tab.contentType = 0;
	}



	public static void addSprite(int id, int spriteId, String spriteName) {
		RSInterface tab = createInterface(id);
		tab.interfaceId = id;
		tab.parentID = id;
		tab.type = 5;
		tab.atActionType = 0;
		tab.contentType = 0;
		tab.opacity = (byte) 0;
		tab.hoverType = 52;
		tab.disabledSprite = imageLoader(spriteId, spriteName);
		tab.enabledSprite = imageLoader(spriteId, spriteName);
		tab.width = 512;
		tab.height = 334;
	}

	/* Add Transparent Sprite */
	public static void addTransparentSprite(int id, int spriteId) {
		RSInterface widget = addInterface(id);
		widget.interfaceId = id;
		widget.parentID = id;
		widget.type = 5;
		widget.atActionType = 0;
		widget.contentType = 0;
		widget.hoverType = 52;
		widget.enabledSprite = Client.spriteCache.get(spriteId);
		widget.disabledSprite = Client.spriteCache.get(spriteId);
		widget.drawsTransparent = true;
		widget.opacity = 64;
		widget.width = 512;
		widget.height = 334;
	}

	public static void addTransparentSprite(int id, int spriteId, String spriteName, int transparency) {
		RSInterface tab = createInterface(id);
		tab.interfaceId = id;
		tab.parentID = id;
		tab.type = 5;
		tab.atActionType = 0;
		tab.contentType = 0;
		tab.transparency = (byte) transparency;
		tab.hoverType = 52;
		tab.disabledSprite = imageLoader(spriteId, spriteName);
		tab.enabledSprite = imageLoader(spriteId, spriteName);
		tab.width = 512;
		tab.height = 334;
		tab.drawsTransparent = true;
	}

	static void addTransparentSprite(int id, int sprite, int transparency) {
		RSInterface tab = createInterface(id);
		tab.interfaceId = id;
		tab.parentID = id;
		tab.type = 5;
		tab.atActionType = 0;
		tab.contentType = 0;
		tab.transparency = (byte) transparency;
		tab.hoverType = 52;
		tab.enabledSprite = Client.spriteCache.get(sprite);
		tab.disabledSprite = Client.spriteCache.get(sprite);
		tab.width = 512;
		tab.height = 334;
		tab.drawsTransparent = true;
	}

	static RSInterface addMovingContainer(int id, int width, int height, int xPad, int yPad, int opacity, boolean move, boolean displayAmount, boolean displayExamine, String... actions) {
		return addContainer(id, 3, width, height, xPad, yPad, opacity, move, displayAmount, displayExamine, actions);
	}

	/* Add Container */
	static RSInterface addContainer(int id, int contentType, int width, int height, int xPad, int yPad, int opacity, boolean move, boolean displayAmount, boolean displayExamine, String... actions) {
		RSInterface container = addTabInterface(id);
		container.parentID = id;
		container.type = 2;
		container.contentType = contentType;
		container.width = width;
		container.height = height;
		container.sprites = new Sprite[20];
		container.spritesX = new int[20];
		container.spritesY = new int[20];
		container.invSpritePadX = xPad;
		container.invSpritePadY = yPad;
		container.inv = new int[width * height];
		container.invStackSizes = new int[width * height];
		container.actions = actions;
		container.aBoolean259 = move;
		container.alpha = opacity;
		container.displayAmount = displayAmount;
		container.displayExamine = displayExamine;
		return container;
	}

	/* Add Container */
	static RSInterface addContainer(int id, int width, int height, int xPad, int yPad, boolean move, boolean displayAmount, boolean displayExamine, String... actions) {
		return addContainer(id, 0, width, height, xPad, yPad, 0, move, displayAmount, displayExamine, actions);
	}

	/* Add Container */
	static RSInterface addContainer(int id, int width, int height, int xPad, int yPad, boolean move, String... actions) {
		return addContainer(id, 0, width, height, xPad, yPad, 0, move, true, true, actions);
	}

	/* Add Text */
	static void addText(int id, String text, TextDrawingArea tda[], int idx, int color, boolean centered) {
		RSInterface rsi = createInterface(id);
		rsi.centerText = centered;
		rsi.textShadow = true;
		rsi.textDrawingAreas = tda[idx];
		rsi.disabledMessage = text;
		rsi.textColor = color;
		rsi.interfaceId = id;
		rsi.type = 4;
	}


	public static void addMarqueeText(int id, int color, boolean shadow, int speed, int font, TextDrawingArea[] TDA) {
		RSInterface rsinterface = addInterface(id);
		rsinterface.marqueeMessages = new String[]{"", "", "", "", ""};
		rsinterface.parentID = id;
		rsinterface.interfaceId = id;
		rsinterface.type = 20;
		rsinterface.atActionType = 0;
		rsinterface.width = 0;
		rsinterface.height = 0;
		rsinterface.contentType = 0;
		rsinterface.opacity = 0;
		rsinterface.hoverType = speed <= 0 ? 1 : speed;
		rsinterface.centerText = true;
		rsinterface.textShadow = shadow;
		rsinterface.textDrawingAreas = TDA[font];
		rsinterface.enabledMessage = "";
		rsinterface.textColor = color;
	}

	public static void addText(int id, String message, int color, boolean center, boolean shadow, int hoverType, TextDrawingArea[] TDA, int j) {
		RSInterface RSInterface = addInterface(id);
		RSInterface.parentID = id;
		RSInterface.interfaceId = id;
		RSInterface.type = 4;
		RSInterface.atActionType = 0;
		RSInterface.width = 0;
		RSInterface.height = 0;
		RSInterface.contentType = 0;
		RSInterface.opacity = 0;
		RSInterface.hoverType = hoverType;
		RSInterface.centerText = center;
		RSInterface.textShadow = shadow;
		RSInterface.textDrawingAreas = TDA[j];
		RSInterface.disabledMessage = message;
		RSInterface.enabledMessage = "";
		RSInterface.textColor = color;
	}

	public static void addText(int id, String text, TextDrawingArea tda[], int idx, int color, boolean center, boolean shadow) {
		RSInterface tab = addTabInterface(id);
		tab.parentID = id;
		tab.interfaceId = id;
		tab.type = 4;
		tab.atActionType = 0;
		tab.width = 0;
		tab.height = 11;
		tab.contentType = 0;
		tab.opacity = 0;
		tab.hoverType = -1;
		tab.centerText = center;
		tab.textShadow = shadow;
		tab.textDrawingAreas = tda[idx];
		tab.disabledMessage = text;
		tab.enabledMessage = "";
		tab.textColor = color;
		tab.anInt219 = 0;
		tab.textHoverColor = 0;
		tab.anInt239 = 0;
	}

	public static void addText(int id, String text, TextDrawingArea tda[], int idx, int color, boolean center, boolean shadow, int contentType, int actionType) {
		RSInterface tab = addTabInterface(id);
		tab.parentID = id;
		tab.interfaceId = id;
		tab.type = 4;
		tab.atActionType = actionType;
		tab.width = 0;
		tab.height = 11;
		tab.contentType = contentType;
		tab.opacity = 0;
		tab.hoverType = -1;
		tab.centerText = center;
		tab.textShadow = shadow;
		tab.textDrawingAreas = tda[idx];
		tab.disabledMessage = text;
		tab.textColor = color;
		tab.anInt219 = 0;
		tab.textColor = 0;
		tab.anInt239 = 0;
	}

	/* Add Hoverable Texts */
	public static void addHoverText(int id, String text, String tooltip, TextDrawingArea tda[], int idx, int color, boolean centerText, boolean textShadowed, int width) {
		RSInterface rsinterface = addInterface(id);
		rsinterface.interfaceId = id;
		rsinterface.parentID = id;
		rsinterface.type = 4;
		rsinterface.atActionType = 1;
		rsinterface.width = width;
		rsinterface.height = 11;
		rsinterface.contentType = 0;
		rsinterface.opacity = 0;
		rsinterface.hoverType = -1;
		rsinterface.centerText = centerText;
		rsinterface.textShadow = textShadowed;
		rsinterface.textDrawingAreas = tda[idx];
		rsinterface.disabledMessage = text;
		if (text.contains("\\n")) {
			rsinterface.height += 11;
		}
		rsinterface.enabledMessage = "";
		rsinterface.tooltip = tooltip;
		rsinterface.textColor = color;
		rsinterface.anInt219 = 0;
		rsinterface.textHoverColor = 0xFFFFFF;
		rsinterface.anInt239 = 0;
	}

	public static void addHoverText(int id, String text, String tooltip, TextDrawingArea tda[], int idx, int color, int hoverColor, boolean centerText, boolean textShadowed, int width) {
		RSInterface rsinterface = addInterface(id);
		rsinterface.interfaceId = id;
		rsinterface.parentID = id;
		rsinterface.type = 4;
		rsinterface.atActionType = 1;
		rsinterface.width = width;
		rsinterface.height = 11;
		rsinterface.contentType = 0;
		rsinterface.opacity = 0;
		rsinterface.hoverType = -1;
		rsinterface.centerText = centerText;
		rsinterface.textShadow = textShadowed;
		rsinterface.textDrawingAreas = tda[idx];
		rsinterface.disabledMessage = text;
		rsinterface.enabledMessage = "";
		rsinterface.tooltip = tooltip;
		rsinterface.textColor = color;
		rsinterface.anInt219 = 0;
		rsinterface.textHoverColor = hoverColor;
		rsinterface.anInt239 = 0;
	}

	/* Add Hover Buttons */
	protected static void addHoverButton(int id, int j, int width, int height, String text, int anInt214, int hoverOver, int aT) {
		RSInterface component = addTabInterface(id);
		component.interfaceId = id;
		component.parentID = id;
		component.type = 5;
		component.atActionType = aT;
		component.contentType = anInt214;
		component.opacity = 0;
		component.hoverType = hoverOver;
		if (j >= 0) {
			component.disabledSprite = Client.spriteCache.get(j);
			component.enabledSprite = Client.spriteCache.get(j);
		}
		component.width = width;
		component.height = height;
		component.tooltip = text;
	}

	public static void addHoverButton(int i, String imageName, int j, int width, int height, String text, int contentType, int hoverOver, int aT) {
		RSInterface tab = addTabInterface(i);
		tab.interfaceId = i;
		tab.parentID = i;
		tab.type = 5;
		tab.atActionType = aT;
		tab.contentType = contentType;
		tab.opacity = 0;
		tab.hoverType = hoverOver;
		tab.disabledSprite = imageLoader(j, imageName);
		tab.enabledSprite = imageLoader(j, imageName);
		tab.width = width;
		tab.height = height;
		tab.tooltip = text;
	}

	protected static void addHoveredButton(int i, int j, int w, int h, int IMAGEID) {
		RSInterface component = addTabInterface(i);
		component.parentID = i;
		component.interfaceId = i;
		component.type = 0;
		component.atActionType = 0;
		component.width = w;
		component.height = h;
		component.isMouseoverTriggered = true;
		component.opacity = 0;
		component.contentType = -1;
		component.scrollMax = 0;
		addHoverImage(IMAGEID, j, j);
		component.totalChildren(1);
		component.child(0, IMAGEID, 0, 0);
	}

	public static void addHoveredButton(int i, String imageName, int j, int w, int h, int IMAGEID) {
		RSInterface tab = addTabInterface(i);
		tab.parentID = i;
		tab.interfaceId = i;
		tab.type = 0;
		tab.atActionType = 0;
		tab.width = w;
		tab.height = h;
		tab.isMouseoverTriggered = true;
		tab.opacity = 0;
		tab.hoverType = -1;
		tab.scrollMax = 0;
		addHoverImage(IMAGEID, j, j, imageName);
		tab.totalChildren(1);
		tab.child(0, IMAGEID, 0, 0);
	}

	public static void addHoverImage(int i, int j, int k, String name) {
		RSInterface tab = addTabInterface(i);
		tab.interfaceId = i;
		tab.parentID = i;
		tab.type = 5;
		tab.atActionType = 0;
		tab.contentType = 0;
		tab.width = 512;
		tab.height = 334;
		tab.opacity = 0;
		tab.hoverType = 52;
		tab.disabledSprite = imageLoader(j, name);
		tab.enabledSprite = imageLoader(k, name);
	}

	/* Add Configs */
	public static void addConfigButton(int identification, int parentIdentification, int enabledSprite, int disabledSprite, int width, int height, String tooltip, int configIdentification, int actionType, int configFrame) {
		addConfigButton(identification, parentIdentification, enabledSprite, disabledSprite, width, height, tooltip, configIdentification, actionType, configFrame, true);
	}

	public static void addConfigButton(int identification, int parentIdentification, int disabledSprite, int enabledSprite, int width, int height, String tooltip, int configIdentification, int actionType, int configFrame, boolean updateConfig) {
		RSInterface Tab = addTabInterface(identification);
		Tab.parentID = parentIdentification;
		Tab.interfaceId = identification;
		Tab.type = 5;
		Tab.atActionType = actionType;
		Tab.contentType = 0;
		Tab.width = width;
		Tab.height = height;
		Tab.opacity = 0;
		Tab.hoverType = -1;
		Tab.valueCompareType = new int[1];
		Tab.requiredValues = new int[1];
		Tab.valueCompareType[0] = 1;
		Tab.requiredValues[0] = configIdentification;
		Tab.scripts = new int[1][3];
		Tab.scripts[0][0] = 5;
		Tab.scripts[0][1] = configFrame;
		Tab.scripts[0][2] = 0;
		Tab.disabledSprite = Client.spriteCache.get(disabledSprite);
		Tab.enabledSprite = Client.spriteCache.get(enabledSprite);
		Tab.tooltip = tooltip;
		Tab.updateConfig = updateConfig;
	}

	public static void addConfigSprite(int id, int spriteId, int spriteId2, int state, int config) {
		RSInterface widget = addTabInterface(id);
		widget.parentID = id;
		widget.interfaceId = id;
		widget.type = 5;
		widget.atActionType = 0;
		widget.contentType = 0;
		widget.width = 512;
		widget.height = 334;
		widget.opacity = 0;
		widget.hoverType = -1;
		widget.valueCompareType = new int[1];
		widget.requiredValues = new int[1];
		widget.valueCompareType[0] = 1;
		widget.requiredValues[0] = state;
		widget.scripts = new int[1][3];
		widget.scripts[0][0] = 5;
		widget.scripts[0][1] = config;
		widget.scripts[0][2] = 0;
		widget.enabledSprite = spriteId < 0 ? null : Client.spriteCache.get(spriteId);
		widget.disabledSprite = spriteId2 < 0 ? null : Client.spriteCache.get(spriteId2);
	}

	public static void addConfigButton(int identification, int parent, int bID, int bID2, String bName, int width, int height, String tT, int configID, int aT, int configFrame) {
		addConfigButton(identification, parent, bID, bID2, bName, width, height, tT, configID, aT, configFrame, true);
	}

	public static void addConfigButton(int identification, int parent, int bID, int bID2, String bName, int width, int height, String tT, int configID, int aT, int configFrame, boolean updateConfig) {
		RSInterface Tab = addTabInterface(identification);
		Tab.parentID = parent;
		Tab.interfaceId = identification;
		Tab.type = 5;
		Tab.atActionType = aT;
		Tab.contentType = 0;
		Tab.width = width;
		Tab.height = height;
		Tab.opacity = 0;
		Tab.hoverType = -1;
		Tab.valueCompareType = new int[1];
		Tab.requiredValues = new int[1];
		Tab.valueCompareType[0] = 1;
		Tab.requiredValues[0] = configID;
		Tab.scripts = new int[1][3];
		Tab.scripts[0][0] = 5;
		Tab.scripts[0][1] = configFrame;
		Tab.scripts[0][2] = 0;
		Tab.disabledSprite = imageLoader(bID, bName);
		Tab.enabledSprite = imageLoader(bID2, bName);
		Tab.tooltip = tT;
		Tab.updateConfig = updateConfig;
	}

	/* Add Character */
	public static void addChar(int ID, int zoom) {
		RSInterface t = createInterface(ID);
		t.interfaceId = ID;
		t.parentID = ID;
		t.type = 6;
		t.atActionType = 0;
		t.contentType = 328;
		t.width = 136;
		t.height = 168;
		t.opacity = 0;
		t.hoverType = 0;
		t.modelZoom = zoom;
		t.modelRotation1 = 150;
		t.modelRotation2 = 0;
		t.anInt257 = -1;
		t.anInt258 = -1;
	}

	public static void addOtherChar(int ID, int zoom) {
		RSInterface t = createInterface(ID);
		t.interfaceId = ID;
		t.parentID = ID;
		t.type = 6;
		t.atActionType = 0;
		t.contentType = 330;
		t.width = 136;
		t.height = 168;
		t.opacity = 0;
		t.hoverType = 0;
		t.modelZoom = zoom;
		t.modelRotation1 = 150;
		t.modelRotation2 = 0;
		t.anInt257 = -1;
		t.anInt258 = -1;
	}

	public static void addPrayer(int i, int configId, int configFrame, int requiredValues, int spriteID, String prayerName) {
		RSInterface tab = addTabInterface(i);
		tab.interfaceId = i;
		tab.parentID = 5608;
		tab.type = 5;
		tab.atActionType = 4;
		tab.contentType = 0;
		tab.opacity = 0;
		tab.isMouseoverTriggereds = -1;
		tab.enabledSprite = imageLoader(0, "PRAYERGLOW");
		tab.disabledSprite = imageLoader(1, "PRAYERGLOW");
		tab.width = 34;
		tab.height = 34;
		tab.valueCompareType = new int[1];
		tab.requiredValues = new int[1];
		tab.valueCompareType[0] = 1;
		tab.requiredValues[0] = configId;
		tab.scripts = new int[1][3];
		tab.scripts[0][0] = 5;
		tab.scripts[0][1] = configFrame;
		tab.scripts[0][2] = 0;
		tab.tooltip = "Toggle@or2@ " + prayerName;
		//        tab.updateConfig = false;
		RSInterface tab2 = addTabInterface(i + 1);
		tab2.interfaceId = i + 1;
		tab2.parentID = 5608;
		tab2.type = 5;
		tab2.atActionType = 0;
		tab2.contentType = 0;
		tab2.opacity = 0;
		tab2.isMouseoverTriggereds = -1;
		tab2.disabledSprite = imageLoader(spriteID, "Prayer/PRAYON");
		tab2.enabledSprite = imageLoader(spriteID, "Prayer/PRAYOFF");
		tab2.width = 34;
		tab2.height = 34;
		tab2.valueCompareType = new int[1];
		tab2.requiredValues = new int[1];
		tab2.valueCompareType[0] = 2;
		tab2.requiredValues[0] = requiredValues + 1;
		tab2.scripts = new int[1][3];
		tab2.scripts[0][0] = 2;
		tab2.scripts[0][1] = 5;
		tab2.scripts[0][2] = 0;
	}

	public static void addPrayerHover(int i, int hoverID, int prayerSpriteID, String hoverText) {
		RSInterface Interface = addTabInterface(i);
		Interface.interfaceId = i;
		Interface.parentID = 5608;
		Interface.type = 5;
		Interface.atActionType = 0;
		Interface.contentType = 0;
		Interface.opacity = 0;
		Interface.isMouseoverTriggereds = hoverID;
		Interface.disabledSprite = imageLoader(0, "tabs/prayer/hover/PRAYERH");
		Interface.enabledSprite = imageLoader(0, "tabs/prayer/hover/PRAYERH");
		Interface.width = 34;
		Interface.height = 34;
		Interface = addTabInterface(hoverID);
		Interface.interfaceId = hoverID;
		Interface.parentID = 5608;
		Interface.type = 0;
		Interface.atActionType = 0;
		Interface.contentType = 0;
		Interface.opacity = 0;
		Interface.isMouseoverTriggereds = -1;
		Interface.width = 512;
		Interface.height = 334;
		Interface.isMouseoverTriggered = true;
		addBox(hoverID + 1, 0, false, 0x000000, hoverText);
		setChildren(1, Interface);
		setBounds(hoverID + 1, 0, 0, 0, Interface);
	}

	protected static void addOldPrayer(int id, String prayerName) {
		RSInterface rsi = getInterfaceCache()[id];
		rsi.tooltip = "Toggle@or2@ " + prayerName;
	}

	public static void addPrayerHover(int i, int hoverID, int prayerSpriteID, int x) {
		RSInterface Interface = addTabInterface(i);
		Interface.interfaceId = i;
		Interface.parentID = 5608;
		Interface.type = 5;
		Interface.atActionType = 0;
		Interface.contentType = 0;
		Interface.opacity = 0;
		Interface.width = 34;
		Interface.height = 34;
		Interface = addTabInterface(hoverID);
		Interface.interfaceId = hoverID;
		Interface.parentID = 5608;
		Interface.type = 0;
		Interface.atActionType = 0;
		Interface.contentType = 0;
		Interface.opacity = 0;
		Interface.width = 512;
		Interface.height = 334;
		Interface.isMouseoverTriggered = true;
		addTooltipBox(hoverID, x);
	}

	public static void addTooltipBox(int id, int x) {
		RSInterface hover = addTabInterface(id);
		hover.type = 8;
		hover.tooltipBounds = true;
		hover.tooltip = "null";
		hover.contentType = x;
		hover.width = 34;
		hover.height = 34;
	}

	/* Tool tips */
	public static void addBox(int id, int byte1, boolean filled, int color, String text) {
		RSInterface Interface = addInterface(id);
		Interface.interfaceId = id;
		Interface.parentID = id;
		Interface.type = 9;
		Interface.popupString = text;
		Interface.opacity = (byte) byte1;
		Interface.aBoolean227 = filled;
		Interface.isMouseoverTriggereds = -1;
		Interface.atActionType = 0;
		Interface.contentType = 0;
		Interface.textColor = color;
	}

	public static void addOutlinedColorBox(int id, int color, int width, int height, int transparency) {
		RSInterface tab = addInterface(id);
		tab.width = width;
		tab.height = height;
		tab.color = color;
		tab.type = 19;
		tab.transparency = transparency;
		tab.contentType = 0;
	}
	public static void addColorBox(int id, int color, int width, int height, int transparency) {
		RSInterface tab = addInterface(id);
		tab.width = width;
		tab.height = height;
		tab.color = color;
		tab.type = 26;
		tab.transparency = transparency;
		tab.contentType = 0;
	}

	public static void addLine(int id, int color, int width) {
		RSInterface tab = addInterface(id);
		tab.width = width;
		tab.color = color;
		tab.type = 22;
		tab.contentType = 0;
	}

	public static void addTooltipBox(int id, String text) {
		RSInterface rsi = addInterface(id);
		rsi.interfaceId = id;
		rsi.parentID = id;
		rsi.type = 8;
		rsi.popupString = text;
	}

	public static void addTooltip(int id, String text) {
		RSInterface rsi = addInterface(id);
		rsi.interfaceId = id;
		rsi.type = 0;
		rsi.isMouseoverTriggered = true;
		rsi.hoverType = -1;
		addTooltipBox(id + 1, text);
		rsi.totalChildren(1);
		rsi.child(0, id + 1, 0, 0);
	}

	/* Add Hover Image */
	public static void addHoverImage(int i, int j, int k) {
		RSInterface tab = addTabInterface(i);
		tab.interfaceId = i;
		tab.parentID = i;
		tab.type = 5;
		tab.atActionType = 0;
		tab.contentType = 0;
		tab.width = 512;
		tab.height = 334;
		tab.opacity = 0;
		tab.hoverType = 52;
		tab.disabledSprite = Client.spriteCache.get(j);
		tab.enabledSprite = Client.spriteCache.get(k);
	}

	public static RSInterface[] getInterfaceCache() {
		return interfaceCache;
	}

	public static void setInterfaceCache(RSInterface[] interfaceCache) {
		RSInterface.interfaceCache = interfaceCache;
	}

	/* Swap Inventory */
	public void swapInventoryItems(int i, int j) {
		int k = inv[i];
		inv[i] = inv[j];
		inv[j] = k;
		k = invStackSizes[i];
		invStackSizes[i] = invStackSizes[j];
		invStackSizes[j] = k;
	}

	/* Text Size */
	public static void textSize(int id, TextDrawingArea tda[], int idx) {
		RSInterface rsi = getInterfaceCache()[id];
		rsi.textDrawingAreas = tda[idx];
	}

	/* Add Cache Sprite */
	public static void addCacheSprite(int id, int sprite1, int sprite2, String sprites) {
		RSInterface rsi = createInterface(id);
		rsi.disabledSprite = method207(sprite1, aClass44, sprites);
		rsi.enabledSprite = method207(sprite2, aClass44, sprites);
		rsi.parentID = id;
		rsi.interfaceId = id;
		rsi.type = 5;
	}

	/* Add percent bar */
	public static void addProgressBar(int id, double percentage, int width, int height, String message) {
		RSInterface rsi = addTabInterface(id);
		rsi.interfaceId = id;
		rsi.parentID = id;
		rsi.percentage = percentage;
		rsi.width = width;
		rsi.height = height;
		rsi.disabledMessage = message;
		rsi.type = 13;
	}

	public static void addProgressBar(int id, double percentage, int width, int height, int color, boolean displayPercent) {
		RSInterface rsi = addTabInterface(id);
		rsi.interfaceId = id;
		rsi.parentID = id;
		rsi.percentage = percentage;
		rsi.width = width;
		rsi.height = height;
		rsi.color = color;
		rsi.displayPercent = displayPercent;
		rsi.type = 13;
	}

	/* Action Button */
	public static void addActionButton(int id, int sprite, int sprite2, int width, int height, String s) {
		RSInterface rsi = createInterface(id);
		rsi.disabledSprite = CustomSpriteLoader(sprite, "");
		if (sprite2 == sprite) {
			rsi.enabledSprite = CustomSpriteLoader(sprite, "a");
		} else {
			rsi.enabledSprite = CustomSpriteLoader(sprite2, "");
		}
		rsi.tooltip = s;
		rsi.contentType = 0;
		rsi.atActionType = 1;
		rsi.width = width;
		rsi.hoverType = 52;
		rsi.parentID = id;
		rsi.interfaceId = id;
		rsi.type = 5;
		rsi.height = height;
	}

	/* Toggle Button */
	public static void addToggleButton(int id, int sprite, int setconfig, int width, int height, String s) {
		RSInterface rsi = addInterface(id);
		rsi.disabledSprite = CustomSpriteLoader(sprite, "");
		rsi.enabledSprite = CustomSpriteLoader(sprite, "a");
		rsi.requiredValues = new int[1];
		rsi.requiredValues[0] = 1;
		rsi.valueCompareType = new int[1];
		rsi.valueCompareType[0] = 1;
		rsi.scripts = new int[1][3];
		rsi.scripts[0][0] = 5;
		rsi.scripts[0][1] = setconfig;
		rsi.scripts[0][2] = 0;
		rsi.atActionType = 4;
		rsi.width = width;
		rsi.hoverType = -1;
		rsi.parentID = id;
		rsi.interfaceId = id;
		rsi.type = 5;
		rsi.height = height;
		rsi.tooltip = s;
	}

	/**
	 * Adds an item container layer.
	 */
	public static RSInterface addContainer(int id, int contentType, int width, int height, String... actions) {
		RSInterface container = addInterface(id);
		container.parentID = id;
		container.type = 2;
		container.contentType = contentType;
		container.width = width;
		container.height = height;
		container.sprites = new Sprite[20];
		container.spritesX = new int[20];
		container.spritesY = new int[20];
		container.invSpritePadX = 14;
		container.invSpritePadY = 4;
		container.inv = new int[width * height];
		container.invStackSizes = new int[width * height];
		container.aBoolean259 = true;
		container.actions = actions;
		return container;
	}

	/**
	 * Adds a button.
	 */
	public static void addButton(int id, Sprite enabled, Sprite disabled, String tooltip, int w, int h) {
		RSInterface tab = createInterface(id);
		tab.interfaceId = id;
		tab.parentID = id;
		tab.type = 5;
		tab.atActionType = 1;
		tab.contentType = 0;
		tab.opacity = (byte) 0;
		tab.hoverType = 52;
		tab.disabledSprite = disabled;
		tab.enabledSprite = enabled;
		tab.width = w;
		tab.height = h;
		tab.tooltip = tooltip;
	}

	public static void addButton(int id, int sid, String tooltip) {
		RSInterface tab = createInterface(id);
		tab.interfaceId = id;
		tab.parentID = id;
		tab.type = 5;
		tab.atActionType = 1;
		tab.contentType = 0;
		tab.opacity = (byte) 0;
		tab.hoverType = 52;
		tab.disabledSprite = Client.spriteCache.get(sid);
		tab.enabledSprite = Client.spriteCache.get(sid);
		tab.width = tab.disabledSprite.width;
		tab.height = tab.enabledSprite.height;
		tab.tooltip = tooltip;
	}

	/**
	 * Adds a config button hover layer.
	 */
	public static void addHoverConfigButton(int id, int hoverOver, int disabledID, int enabledID, int width, int height, String tooltip, int[] anIntArray245, int[] anIntArray212, int[][] valueIndexArray) {
		RSInterface rsint = addTabInterface(id);
		rsint.parentID = id;
		rsint.interfaceId = id;
		rsint.type = 5;
		rsint.atActionType = 5;
		rsint.contentType = 206;
		rsint.width = width;
		rsint.height = height;
		rsint.opacity = 0;
		rsint.hoverType = hoverOver;
		rsint.valueCompareType = anIntArray245;
		rsint.requiredValues = anIntArray212;
		rsint.scripts = valueIndexArray;
		rsint.disabledSprite = Client.spriteCache.get(disabledID);
		rsint.enabledSprite = Client.spriteCache.get(enabledID);
		rsint.tooltip = tooltip;
	}

	/**
	 * Adds a config button hover layer.
	 */
	static void addHoveredConfigButton(RSInterface original, int ID, int IMAGEID, int disabledID, int enabledID) {
		RSInterface rsint = addTabInterface(ID);
		rsint.parentID = original.interfaceId;
		rsint.interfaceId = ID;
		rsint.type = 0;
		rsint.atActionType = 0;
		rsint.contentType = 0;
		rsint.width = original.width;
		rsint.height = original.height;
		rsint.opacity = 0;
		rsint.hoverType = -1;
		RSInterface hover = addInterface(IMAGEID);
		hover.type = 5;
		hover.width = original.width;
		hover.height = original.height;
		hover.valueCompareType = original.valueCompareType;
		hover.requiredValues = original.requiredValues;
		hover.scripts = original.scripts;
		hover.disabledSprite = Client.spriteCache.get(disabledID);
		hover.enabledSprite = Client.spriteCache.get(enabledID);
		rsint.totalChildren(1);
		setBounds(IMAGEID, 0, 0, 0, rsint);
		rsint.tooltip = original.tooltip;
		rsint.isMouseoverTriggered = true;
	}

	/* Adds Input Field */
	static void addInputField(int identity, int characterLimit, int color, String text, int width, int height, boolean asterisks, boolean updatesEveryInput, String regex) {
		RSInterface field = addFullScreenInterface(identity);
		field.interfaceId = identity;
		field.type = 16;
		field.atActionType = 8;
		field.disabledMessage = text;
		field.width = width;
		field.height = height;
		field.characterLimit = characterLimit;
		field.textColor = color;
		field.displayAsterisks = asterisks;
		field.tooltip = text;
		field.defaultInputFieldText = text;
		field.updatesEveryInput = updatesEveryInput;
		field.inputRegex = regex;
	}

	public static void addInputField(int identity, int characterLimit, int color, String text, int width, int height, boolean asterisks, boolean updatesEveryInput) {
		RSInterface field = addFullScreenInterface(identity);
		field.interfaceId = identity;
		field.type = 16;
		field.atActionType = 8;
		field.disabledMessage = text;
		field.width = width;
		field.height = height;
		field.characterLimit = characterLimit;
		field.textColor = color;
		field.displayAsterisks = asterisks;
		field.defaultInputFieldText = text;
		field.tooltips = new String[]{"Clear", "Edit"};
		field.updatesEveryInput = updatesEveryInput;
	}

	public static void addInputField(int identity, int characterLimit, int color, String text, int width, int height, boolean asterisks) {
		RSInterface field = addFullScreenInterface(identity);
		field.interfaceId = identity;
		field.type = 16;
		field.atActionType = 8;
		field.disabledMessage = text;
		field.width = width;
		field.height = height;
		field.characterLimit = characterLimit;
		field.textColor = color;
		field.displayAsterisks = asterisks;
		field.defaultInputFieldText = text;
		field.tooltips = new String[]{"Clear", "Edit"};
	}

	/* Special Bar */
	public static void specialBarSprite(int id, int sprite) {
		RSInterface class9 = getInterfaceCache()[id];
		class9.disabledSprite = CustomSpriteLoader(sprite, "");
	}

	public void specialBar(int id) {
		addActionButton(id - 12, 7587, -1, 150, 26, "Use <col=475154>Special Attack");
		for (int i = id - 11; i < id; i++) {
			removeSomething(i);
		}
		RSInterface rsi = getInterfaceCache()[id - 12];
		rsi.width = 150;
		rsi.height = 26;
		rsi = getInterfaceCache()[id];
		rsi.width = 150;
		rsi.height = 26;
		rsi.child(0, id - 12, 0, 0);
		rsi.child(12, id + 1, 3, 7);
		rsi.child(23, id + 12, 16, 8);
		for (int i = 13; i < 23; i++) {
			rsi.childY[i] -= 1;
		}
		rsi = getInterfaceCache()[id + 1];
		rsi.type = 5;
		rsi.disabledSprite = CustomSpriteLoader(7600, "");
		for (int i = id + 2; i < id + 12; i++) {
			rsi = getInterfaceCache()[i];
			rsi.type = 5;
		}
		specialBarSprite(id + 2, 7601);
		specialBarSprite(id + 3, 7602);
		specialBarSprite(id + 4, 7603);
		specialBarSprite(id + 5, 7604);
		specialBarSprite(id + 6, 7605);
		specialBarSprite(id + 7, 7606);
		specialBarSprite(id + 8, 7607);
		specialBarSprite(id + 9, 7608);
		specialBarSprite(id + 10, 7609);
		specialBarSprite(id + 11, 7610);
	}

	/* Other */
	public static Sprite imageLoader(int i, String s) {
		long l = (StringUtils.method585(s) << 8) + (long) i;
		Sprite sprite = (Sprite) aMRUNodes_238.get(l);
		if (sprite != null)
			return sprite;
		try {
			sprite = new Sprite(s + " " + i);
			aMRUNodes_238.put(sprite, l);
		} catch (Exception exception) {
			return null;
		}
		return sprite;
	}

	private static Sprite CustomSpriteLoader(int id, String s) {
		long l = (StringUtils.method585(s) << 8) + (long) id;
		Sprite sprite = (Sprite) aMRUNodes_238.get(l);
		if (sprite != null) {
			return sprite;
		}
		try {
			sprite = new Sprite("/Attack/" + id + s);
			aMRUNodes_238.put(sprite, l);
		} catch (Exception exception) {
			return null;
		}
		return sprite;
	}

	public Model getModel(int j, int k, boolean flag) {
		Model model;
		if (flag) {
			model = getModel(anInt255, anInt256);
		} else {
			model = getModel(defaultMediaType, mediaID);
		}
		if (model == null) {
			return null;
		}
		if (k == -1 && j == -1 && model.anIntArray1640 == null) {
			return model;
		}
		Model model_1 = new Model(true, Frame.method532(k) & Frame.method532(j), false, model);
		if (k != -1 || j != -1) {
			model_1.skin();
		}
		if (k != -1) {
			model_1.method470(k);
		}
		if (j != -1) {
			model_1.method470(j);
		}
		model_1.light(64, 850, -30, -50, -30, true);
		return model_1;
	}

	public Model getModel(int i, int j) {
		Model model = (Model) aMRUNodes_264.get((i << 16) + j);
		if (model != null)
			return model;
		if (i == 1) {
			model = Model.getModel(j);
		}
		if (i == 2) {
			model = NpcDefinition.lookup(j).method160();
		}
		if (i == 3) {
			model = Client.localPlayer.model();
		}
		if (i == 4) {
			model = ItemDefinition.lookup(j).method202(50);
		}
		if (i == 5) {
			model = null;
		}
		if (model != null) {
			aMRUNodes_264.put(model, (i << 16) + j);
		}
		return model;
	}

	private static Sprite method207(int i, StreamLoader streamLoader, String s) {
		long l = (StringUtils.method585(s) << 8) + (long) i;
		Sprite sprite = (Sprite) aMRUNodes_238.get(l);
		if (sprite != null) {
			return sprite;
		}
		try {
			sprite = new Sprite(streamLoader, s, i);
			aMRUNodes_238.put(sprite, l);
		} catch (Exception _ex) {
			return null;
		}
		return sprite;
	}

	public static void method208(boolean flag, Model model) {
		int i = 0;
		int j = 5;
		if (flag) {
			return;
		}
		aMRUNodes_264.unlinkAll();
		if (model != null && j != 4) {
			aMRUNodes_264.put(model, (j << 16) + i);
		}
	}

	public void totalChildren(int id, int x, int y) {
		children = new int[id];
		childX = new int[x];
		childY = new int[y];
	}

	public static void removeSomething(int id) {
		createInterface(id);
	}

	public void child(int id, int interID, int x, int y) {
		children[id] = interID;
		childX[id] = x;
		childY[id] = y;
	}

	public void totalChildren(int t) {
		children = new int[t];
		childX = new int[t];
		childY = new int[t];
	}

	public void addChildren(int size) {
		children = Arrays.copyOf(children, children.length + size);
		childX = Arrays.copyOf(childX, childX.length + size);
		childY = Arrays.copyOf(childY, childY.length + size);
	}

	public static void setChildren(int total, RSInterface i) {
		i.children = new int[total];
		i.childX = new int[total];
		i.childY = new int[total];
	}

	public static void setBounds(int ID, int X, int Y, int frame, RSInterface RSinterface) {
		RSinterface.children[frame] = ID;
		RSinterface.childX[frame] = X;
		RSinterface.childY[frame] = Y;
	}

	/* Magic book */
	public static void addRuneText(int ID, int runeAmount, int RuneID, TextDrawingArea[] font) {
		RSInterface rsInterface = addInterface(ID);
		rsInterface.interfaceId = ID;
		rsInterface.parentID = 1151;
		rsInterface.type = 4;
		rsInterface.atActionType = 0;
		rsInterface.contentType = 0;
		rsInterface.width = 0;
		rsInterface.height = 14;
		rsInterface.opacity = 0;
		rsInterface.hoverType = -1;
		rsInterface.valueCompareType = new int[1];
		rsInterface.requiredValues = new int[1];
		rsInterface.valueCompareType[0] = 3;
		rsInterface.requiredValues[0] = runeAmount;
		rsInterface.scripts = new int[1][4];
		rsInterface.scripts[0][0] = 4;
		rsInterface.scripts[0][1] = 3214;
		rsInterface.scripts[0][2] = RuneID;
		rsInterface.scripts[0][3] = 0;
		rsInterface.centerText = true;
		rsInterface.textDrawingAreas = font[0];
		rsInterface.textShadow = true;
		rsInterface.disabledMessage = "%1/" + runeAmount + "";
		rsInterface.enabledMessage = "";
		rsInterface.textColor = 12582912;
		rsInterface.anInt219 = 49152;
	}

	public static void addLunarSprite(int i, int j) {
		RSInterface RSInterface = addInterface(i);
		RSInterface.interfaceId = i;
		RSInterface.parentID = i;
		RSInterface.type = 5;
		RSInterface.atActionType = 0;
		RSInterface.contentType = 0;
		RSInterface.opacity = 0;
		RSInterface.hoverType = 52;
		RSInterface.disabledSprite = Client.spriteCache.get(j);
		RSInterface.width = 500;
		RSInterface.height = 500;
		RSInterface.tooltip = "";
	}

	public static void drawRune(int i, int id) {
		RSInterface RSInterface = addInterface(i);
		RSInterface.type = 5;
		RSInterface.atActionType = 0;
		RSInterface.contentType = 0;
		RSInterface.opacity = 0;
		RSInterface.hoverType = 52;
		RSInterface.disabledSprite = Client.spriteCache.get(id);
		RSInterface.width = 500;
		RSInterface.height = 500;
	}

	/* Home Teleport */
	public static void homeTeleport() {
		RSInterface RSInterface = addInterface(30000);
		RSInterface.tooltip = "Cast <col=00dfff>Home Teleport";
		RSInterface.interfaceId = 30000;
		RSInterface.parentID = 30000;
		RSInterface.type = 5;
		RSInterface.atActionType = 5;
		RSInterface.contentType = 0;
		RSInterface.opacity = 0;
		RSInterface.hoverType = 30001;
		RSInterface.disabledSprite = Client.spriteCache.get(577);
		RSInterface.width = 20;
		RSInterface.height = 20;
		RSInterface Int = addInterface(30001);
		Int.isMouseoverTriggered = true;
		Int.hoverType = -1;
		setChildren(1, Int);
		addLunarSprite(30002, 578);
		setBounds(30002, 0, 0, 0, Int);
	}

	/* Lunars */
	public static void addLunar2RunesSmallBox(int ID, int r1, int r2, int ra1, int ra2, int rune1, int lvl, String name, String descr, TextDrawingArea[] TDA, int sid, int suo, int type) {
		RSInterface rsInterface = addInterface(ID);
		rsInterface.interfaceId = ID;
		rsInterface.parentID = 1151;
		rsInterface.type = 5;
		rsInterface.atActionType = type;
		rsInterface.contentType = 0;
		rsInterface.hoverType = ID + 1;
		rsInterface.spellUsableOn = suo;
		rsInterface.selectedActionName = "Cast On";
		rsInterface.width = 20;
		rsInterface.height = 20;
		rsInterface.tooltip = "Cast <col=475154>" + name;
		rsInterface.spellName = name;
		rsInterface.valueCompareType = new int[3];
		rsInterface.requiredValues = new int[3];
		rsInterface.valueCompareType[0] = 3;
		rsInterface.requiredValues[0] = ra1;
		rsInterface.valueCompareType[1] = 3;
		rsInterface.requiredValues[1] = ra2;
		rsInterface.valueCompareType[2] = 3;
		rsInterface.requiredValues[2] = lvl;
		rsInterface.scripts = new int[3][];
		rsInterface.scripts[0] = new int[4];
		rsInterface.scripts[0][0] = 4;
		rsInterface.scripts[0][1] = 3214;
		rsInterface.scripts[0][2] = r1;
		rsInterface.scripts[0][3] = 0;
		rsInterface.scripts[1] = new int[4];
		rsInterface.scripts[1][0] = 4;
		rsInterface.scripts[1][1] = 3214;
		rsInterface.scripts[1][2] = r2;
		rsInterface.scripts[1][3] = 0;
		rsInterface.scripts[2] = new int[3];
		rsInterface.scripts[2][0] = 1;
		rsInterface.scripts[2][1] = 6;
		rsInterface.scripts[2][2] = 0;//
		rsInterface.disabledSprite = Client.spriteCache.get(sid);
		rsInterface.enabledSprite = Client.spriteCache.get(sid + 39);
		RSInterface INT = addInterface(ID + 1);
		INT.isMouseoverTriggered = true;
		INT.hoverType = -1;
		setChildren(7, INT);
		addLunarSprite(ID + 2, 579);
		setBounds(ID + 2, 0, 0, 0, INT);
		addText(ID + 3, "Level " + (lvl + 1) + ": " + name, 0xFF981F, true, true, 52, TDA, 1);
		setBounds(ID + 3, 90, 4, 1, INT);
		addText(ID + 4, descr, 0xAF6A1A, true, true, 52, TDA, 0);
		setBounds(ID + 4, 90, 19, 2, INT);
		setBounds(30016, 37, 35, 3, INT);// Rune
		setBounds(rune1, 112, 35, 4, INT);// Rune
		addRuneText(ID + 5, ra1 + 1, r1, TDA);
		setBounds(ID + 5, 50, 66, 5, INT);
		addRuneText(ID + 6, ra2 + 1, r2, TDA);
		setBounds(ID + 6, 123, 66, 6, INT);
	}

	public static void addLunar3RunesSmallBox(int ID, int r1, int r2, int r3, int ra1, int ra2, int ra3, int rune1, int rune2, int lvl, String name, String descr, TextDrawingArea[] TDA, int sid, int suo, int type) {
		RSInterface rsInterface = addInterface(ID);
		rsInterface.interfaceId = ID;
		rsInterface.parentID = 1151;
		rsInterface.type = 5;
		rsInterface.atActionType = type;
		rsInterface.contentType = 0;
		rsInterface.hoverType = ID + 1;
		rsInterface.spellUsableOn = suo;
		rsInterface.selectedActionName = "Cast on";
		rsInterface.width = 20;
		rsInterface.height = 20;
		rsInterface.tooltip = "Cast <col=475154>" + name;
		rsInterface.spellName = name;
		rsInterface.valueCompareType = new int[4];
		rsInterface.requiredValues = new int[4];
		rsInterface.valueCompareType[0] = 3;
		rsInterface.requiredValues[0] = ra1;
		rsInterface.valueCompareType[1] = 3;
		rsInterface.requiredValues[1] = ra2;
		rsInterface.valueCompareType[2] = 3;
		rsInterface.requiredValues[2] = ra3;
		rsInterface.valueCompareType[3] = 3;
		rsInterface.requiredValues[3] = lvl;
		rsInterface.scripts = new int[4][];
		rsInterface.scripts[0] = new int[4];
		rsInterface.scripts[0][0] = 4;
		rsInterface.scripts[0][1] = 3214;
		rsInterface.scripts[0][2] = r1;
		rsInterface.scripts[0][3] = 0;
		rsInterface.scripts[1] = new int[4];
		rsInterface.scripts[1][0] = 4;
		rsInterface.scripts[1][1] = 3214;
		rsInterface.scripts[1][2] = r2;
		rsInterface.scripts[1][3] = 0;
		rsInterface.scripts[2] = new int[4];
		rsInterface.scripts[2][0] = 4;
		rsInterface.scripts[2][1] = 3214;
		rsInterface.scripts[2][2] = r3;
		rsInterface.scripts[2][3] = 0;
		rsInterface.scripts[3] = new int[3];
		rsInterface.scripts[3][0] = 1;
		rsInterface.scripts[3][1] = 6;
		rsInterface.scripts[3][2] = 0;
		rsInterface.disabledSprite = Client.spriteCache.get(sid);
		rsInterface.enabledSprite = Client.spriteCache.get(sid + 39);
		RSInterface INT = addInterface(ID + 1);
		INT.isMouseoverTriggered = true;
		INT.hoverType = -1;
		setChildren(9, INT);
		addLunarSprite(ID + 2, 579);
		setBounds(ID + 2, 0, 0, 0, INT);
		addText(ID + 3, "Level " + (lvl + 1) + ": " + name, 0xFF981F, true, true, 52, TDA, 1);
		setBounds(ID + 3, 90, 4, 1, INT);
		addText(ID + 4, descr, 0xAF6A1A, true, true, 52, TDA, 0);
		setBounds(ID + 4, 90, 19, 2, INT);
		setBounds(30016, 14, 35, 3, INT);
		setBounds(rune1, 74, 35, 4, INT);
		setBounds(rune2, 130, 35, 5, INT);
		addRuneText(ID + 5, ra1 + 1, r1, TDA);
		setBounds(ID + 5, 26, 66, 6, INT);
		addRuneText(ID + 6, ra2 + 1, r2, TDA);
		setBounds(ID + 6, 87, 66, 7, INT);
		addRuneText(ID + 7, ra3 + 1, r3, TDA);
		setBounds(ID + 7, 142, 66, 8, INT);
	}

	public static void addLunar3RunesBigBox(int ID, int r1, int r2, int r3, int ra1, int ra2, int ra3, int rune1, int rune2, int lvl, String name, String descr, TextDrawingArea[] TDA, int sid, int suo, int type) {
		RSInterface rsInterface = addInterface(ID);
		rsInterface.interfaceId = ID;
		rsInterface.parentID = 1151;
		rsInterface.type = 5;
		rsInterface.atActionType = type;
		rsInterface.contentType = 0;
		rsInterface.hoverType = ID + 1;
		rsInterface.spellUsableOn = suo;
		rsInterface.selectedActionName = "Cast on";
		rsInterface.width = 20;
		rsInterface.height = 20;
		rsInterface.tooltip = "Cast <col=475154>" + name;
		rsInterface.spellName = name;
		rsInterface.valueCompareType = new int[4];
		rsInterface.requiredValues = new int[4];
		rsInterface.valueCompareType[0] = 3;
		rsInterface.requiredValues[0] = ra1;
		rsInterface.valueCompareType[1] = 3;
		rsInterface.requiredValues[1] = ra2;
		rsInterface.valueCompareType[2] = 3;
		rsInterface.requiredValues[2] = ra3;
		rsInterface.valueCompareType[3] = 3;
		rsInterface.requiredValues[3] = lvl;
		rsInterface.scripts = new int[4][];
		rsInterface.scripts[0] = new int[4];
		rsInterface.scripts[0][0] = 4;
		rsInterface.scripts[0][1] = 3214;
		rsInterface.scripts[0][2] = r1;
		rsInterface.scripts[0][3] = 0;
		rsInterface.scripts[1] = new int[4];
		rsInterface.scripts[1][0] = 4;
		rsInterface.scripts[1][1] = 3214;
		rsInterface.scripts[1][2] = r2;
		rsInterface.scripts[1][3] = 0;
		rsInterface.scripts[2] = new int[4];
		rsInterface.scripts[2][0] = 4;
		rsInterface.scripts[2][1] = 3214;
		rsInterface.scripts[2][2] = r3;
		rsInterface.scripts[2][3] = 0;
		rsInterface.scripts[3] = new int[3];
		rsInterface.scripts[3][0] = 1;
		rsInterface.scripts[3][1] = 6;
		rsInterface.scripts[3][2] = 0;
		rsInterface.disabledSprite = Client.spriteCache.get(sid);
		rsInterface.enabledSprite = Client.spriteCache.get(sid + 39);
		RSInterface INT = addInterface(ID + 1);
		INT.isMouseoverTriggered = true;
		INT.hoverType = -1;
		setChildren(9, INT);
		addLunarSprite(ID + 2, 580);
		setBounds(ID + 2, 0, 0, 0, INT);
		addText(ID + 3, "Level " + (lvl + 1) + ": " + name, 0xFF981F, true, true, 52, TDA, 1);
		setBounds(ID + 3, 90, 4, 1, INT);
		addText(ID + 4, descr, 0xAF6A1A, true, true, 52, TDA, 0);
		setBounds(ID + 4, 90, 21, 2, INT);
		setBounds(30016, 14, 48, 3, INT);
		setBounds(rune1, 74, 48, 4, INT);
		setBounds(rune2, 130, 48, 5, INT);
		addRuneText(ID + 5, ra1 + 1, r1, TDA);
		setBounds(ID + 5, 26, 79, 6, INT);
		addRuneText(ID + 6, ra2 + 1, r2, TDA);
		setBounds(ID + 6, 87, 79, 7, INT);
		addRuneText(ID + 7, ra3 + 1, r3, TDA);
		setBounds(ID + 7, 142, 79, 8, INT);
	}

	public static void addLunar3RunesLargeBox(int ID, int r1, int r2, int r3, int ra1, int ra2, int ra3, int rune1, int rune2, int lvl, String name, String descr, TextDrawingArea[] TDA, int sid, int suo, int type) {
		RSInterface rsInterface = addInterface(ID);
		rsInterface.interfaceId = ID;
		rsInterface.parentID = 1151;
		rsInterface.type = 5;
		rsInterface.atActionType = type;
		rsInterface.contentType = 0;
		rsInterface.hoverType = ID + 1;
		rsInterface.spellUsableOn = suo;
		rsInterface.selectedActionName = "Cast on";
		rsInterface.width = 20;
		rsInterface.height = 20;
		rsInterface.tooltip = "Cast <col=475154>" + name;
		rsInterface.spellName = name;
		rsInterface.valueCompareType = new int[4];
		rsInterface.requiredValues = new int[4];
		rsInterface.valueCompareType[0] = 3;
		rsInterface.requiredValues[0] = ra1;
		rsInterface.valueCompareType[1] = 3;
		rsInterface.requiredValues[1] = ra2;
		rsInterface.valueCompareType[2] = 3;
		rsInterface.requiredValues[2] = ra3;
		rsInterface.valueCompareType[3] = 3;
		rsInterface.requiredValues[3] = lvl;
		rsInterface.scripts = new int[4][];
		rsInterface.scripts[0] = new int[4];
		rsInterface.scripts[0][0] = 4;
		rsInterface.scripts[0][1] = 3214;
		rsInterface.scripts[0][2] = r1;
		rsInterface.scripts[0][3] = 0;
		rsInterface.scripts[1] = new int[4];
		rsInterface.scripts[1][0] = 4;
		rsInterface.scripts[1][1] = 3214;
		rsInterface.scripts[1][2] = r2;
		rsInterface.scripts[1][3] = 0;
		rsInterface.scripts[2] = new int[4];
		rsInterface.scripts[2][0] = 4;
		rsInterface.scripts[2][1] = 3214;
		rsInterface.scripts[2][2] = r3;
		rsInterface.scripts[2][3] = 0;
		rsInterface.scripts[3] = new int[3];
		rsInterface.scripts[3][0] = 1;
		rsInterface.scripts[3][1] = 6;
		rsInterface.scripts[3][2] = 0;

		rsInterface.disabledSprite = Client.spriteCache.get(sid);
		rsInterface.enabledSprite = Client.spriteCache.get(sid + 39);
		RSInterface INT = addInterface(ID + 1);
		INT.isMouseoverTriggered = true;
		INT.hoverType = -1;
		setChildren(9, INT);
		addLunarSprite(ID + 2, 581);
		setBounds(ID + 2, 0, 0, 0, INT);
		addText(ID + 3, "Level " + (lvl + 1) + ": " + name, 0xFF981F, true, true, 52, TDA, 1);
		setBounds(ID + 3, 90, 4, 1, INT);
		addText(ID + 4, descr, 0xAF6A1A, true, true, 52, TDA, 0);
		setBounds(ID + 4, 90, 34, 2, INT);
		setBounds(30016, 14, 61, 3, INT);
		setBounds(rune1, 74, 61, 4, INT);
		setBounds(rune2, 130, 61, 5, INT);
		addRuneText(ID + 5, ra1 + 1, r1, TDA);
		setBounds(ID + 5, 26, 92, 6, INT);
		addRuneText(ID + 6, ra2 + 1, r2, TDA);
		setBounds(ID + 6, 87, 92, 7, INT);
		addRuneText(ID + 7, ra3 + 1, r3, TDA);
		setBounds(ID + 7, 142, 92, 8, INT);
	}

	public static void configureLunar(TextDrawingArea[] TDA) {
		homeTeleport();
		constructLunar();
		drawRune(30003, 582);
		drawRune(30004, 583);
		drawRune(30005, 584);
		drawRune(30006, 585);
		drawRune(30007, 586);
		drawRune(30008, 587);
		drawRune(30009, 588);
		drawRune(30010, 589);
		drawRune(30011, 590);
		drawRune(30012, 591);
		drawRune(30013, 592);
		drawRune(30014, 593);
		drawRune(30015, 594);
		drawRune(30016, 595);
		addLunar3RunesSmallBox(30017, 9075, 554, 555, 0, 4, 3, 30003, 30004, 64, "Bake Pie", "Bake pies without a stove", TDA, 596, 16, 2);
		addLunar2RunesSmallBox(30025, 9075, 557, 0, 7, 30006, 65, "Cure Plant", "Cure disease on farming patch", TDA, 597, 4, 2);
		addLunar3RunesBigBox(30032, 9075, 564, 558, 0, 0, 0, 30013, 30007, 65, "Monster Examine", "Detect the combat statistics of a\\nmonster", TDA, 598, 2, 2);
		addLunar3RunesSmallBox(30040, 9075, 564, 556, 0, 0, 1, 30013, 30005, 66, "NPC Contact", "Speak with varied NPCs", TDA, 599, 0, 2);
		addLunar3RunesSmallBox(30048, 9075, 563, 557, 0, 0, 9, 30012, 30006, 67, "Cure Other", "Cure poisoned players", TDA, 600, 8, 2);
		addLunar3RunesSmallBox(30056, 9075, 555, 554, 0, 2, 0, 30004, 30003, 67, "Humidify", "Fills certain vessels with water", TDA, 601, 0, 5);
		addLunar3RunesSmallBox(30064, 9075, 563, 557, 1, 0, 1, 30012, 30006, 68, "Moonclan Teleport", "Teleports you to moonclan island", TDA, 602, 0, 5);
		addLunar3RunesBigBox(30075, 9075, 563, 557, 1, 0, 3, 30012, 30006, 69, "Tele Group Moonclan", "Teleports players to Moonclan\\nisland", TDA, 603, 0, 5);
		addLunar3RunesSmallBox(30083, 9075, 563, 557, 1, 0, 5, 30012, 30006, 70, "Ourania Teleport", "Teleports you to ourania rune altar", TDA, 604, 0, 5);
		addLunar3RunesSmallBox(30091, 9075, 564, 563, 1, 1, 0, 30013, 30012, 70, "Cure Me", "Cures Poison", TDA, 605, 0, 5);
		addLunar2RunesSmallBox(30099, 9075, 557, 1, 1, 30006, 70, "Hunter Kit", "Get a kit of hunting gear", TDA, 606, 0, 5);
		addLunar3RunesSmallBox(30106, 9075, 563, 555, 1, 0, 0, 30012, 30004, 71, "Waterbirth Teleport", "Teleports you to Waterbirth island", TDA, 607, 0, 5);
		addLunar3RunesBigBox(30114, 9075, 563, 555, 1, 0, 4, 30012, 30004, 72, "Tele Group Waterbirth", "Teleports players to Waterbirth\\nisland", TDA, 608, 0, 5);
		addLunar3RunesSmallBox(30122, 9075, 564, 563, 1, 1, 1, 30013, 30012, 73, "Cure Group", "Cures Poison on players", TDA, 609, 0, 5);
		addLunar3RunesBigBox(30130, 9075, 564, 559, 1, 1, 4, 30013, 30008, 74, "Stat Spy", "Cast on another player to see their\\nskill levels", TDA, 610, 8, 2);
		addLunar3RunesBigBox(30138, 9075, 563, 554, 1, 1, 2, 30012, 30003, 74, "Barbarian Teleport", "Teleports you to the Barbarian\\noutpost", TDA, 611, 0, 5);
		addLunar3RunesBigBox(30146, 9075, 563, 554, 1, 1, 5, 30012, 30003, 75, "Tele Group Barbarian", "Teleports players to the Barbarian\\noutpost", TDA, 612, 0, 5);
		addLunar3RunesSmallBox(30154, 9075, 554, 556, 1, 5, 9, 30003, 30005, 76, "Superglass Make", "Make glass without a furnace", TDA, 613, 16, 2);
		addLunar3RunesSmallBox(30162, 9075, 563, 555, 1, 1, 3, 30012, 30004, 77, "Khazard Teleport", "Teleports you to Port khazard", TDA, 614, 0, 5);
		addLunar3RunesSmallBox(30170, 9075, 563, 555, 1, 1, 7, 30012, 30004, 78, "Tele Group Khazard", "Teleports players to Port khazard", TDA, 615, 0, 5);
		addLunar3RunesBigBox(30178, 9075, 564, 559, 1, 0, 4, 30013, 30008, 78, "Dream", "Take a rest and restore hitpoints 3\\n times faster", TDA, 616, 0, 5);
		addLunar3RunesSmallBox(30186, 9075, 557, 555, 1, 9, 4, 30006, 30004, 79, "String Jewellery", "String amulets without wool", TDA, 617, 0, 5);
		addLunar3RunesLargeBox(30194, 9075, 557, 555, 1, 9, 9, 30006, 30004, 80, "Stat Restore Pot\\nShare", "Share a potion with up to 4 nearby\\nplayers", TDA, 618, 0, 5);
		addLunar3RunesSmallBox(30202, 9075, 554, 555, 1, 6, 6, 30003, 30004, 81, "Magic Imbue", "Combine runes without a talisman", TDA, 619, 0, 5);
		addLunar3RunesBigBox(30210, 9075, 561, 557, 2, 1, 14, 30010, 30006, 82, "Fertile Soil", "Fertilise a farming patch with super\\ncompost", TDA, 620, 4, 2);
		addLunar3RunesBigBox(30218, 9075, 557, 555, 2, 11, 9, 30006, 30004, 83, "Boost Potion Share", "Shares a potion with up to 4 nearby\\nplayers", TDA, 621, 0, 5);
		addLunar3RunesSmallBox(30226, 9075, 563, 555, 2, 2, 9, 30012, 30004, 84, "Fishing Guild Teleport", "Teleports you to the fishing guild", TDA, 622, 0, 5);
		addLunar3RunesLargeBox(30234, 9075, 563, 555, 1, 2, 13, 30012, 30004, 85, "Tele Group Fishing Guild", "Teleports players to the Fishing\\nGuild", TDA, 623, 0, 5);
		addLunar3RunesSmallBox(30242, 9075, 557, 561, 2, 14, 0, 30006, 30010, 85, "Plank Make", "Turn Logs into planks", TDA, 624, 16, 5);
		addLunar3RunesSmallBox(30250, 9075, 563, 555, 2, 2, 9, 30012, 30004, 86, "Catherby Teleport", "Teleports you to Catherby", TDA, 625, 0, 5);
		addLunar3RunesSmallBox(30258, 9075, 563, 555, 2, 2, 14, 30012, 30004, 87, "Tele Group Catherby", "Teleports players to Catherby", TDA, 626, 0, 5);
		addLunar3RunesSmallBox(30266, 9075, 563, 555, 2, 2, 7, 30012, 30004, 88, "Ice Plateau Teleport", "Teleports you to Ice Plateau", TDA, 627, 0, 5);
		addLunar3RunesLargeBox(30274, 9075, 563, 555, 2, 2, 15, 30012, 30004, 89, "Tele Group Ice Plateau", "Teleports players to Ice Plateau", TDA, 628, 0, 5);
		addLunar3RunesBigBox(30282, 9075, 563, 561, 2, 1, 0, 30012, 30010, 90, "Energy Transfer", "Spend HP and SA energy to\\n give another SA and run energy", TDA, 629, 8, 2);
		addLunar3RunesBigBox(30290, 9075, 563, 565, 2, 2, 0, 30012, 30014, 91, "Heal Other", "Transfer up to 75% of hitpoints\\n to another player", TDA, 630, 8, 2);
		addLunar3RunesBigBox(30298, 9075, 560, 557, 2, 1, 9, 30009, 30006, 92, "Vengeance Other", "Allows another player to rebound\\ndamage to an opponent", TDA, 631, 8, 2);
		addLunar3RunesSmallBox(30306, 9075, 560, 557, 3, 1, 9, 30009, 30006, 93, "Vengeance", "Rebound damage to an opponent", TDA, 632, 0, 5);
		addLunar3RunesBigBox(30314, 9075, 565, 563, 3, 2, 5, 30014, 30012, 94, "Heal Group", "Transfer up to 75% of hitpoints\\n to a group", TDA, 633, 0, 5);
		addLunar3RunesBigBox(30322, 9075, 564, 563, 2, 1, 0, 30013, 30012, 95, "Spellbook Swap", "Change to another spellbook for 1\\nspell cast", TDA, 634, 0, 5);
	}

	public static void constructLunar() {
		RSInterface Interface = addTabInterface(29999);
		setChildren(80, Interface);
		setBounds(30000, 11, 10, 0, Interface);
		setBounds(30017, 40, 9, 1, Interface);
		setBounds(30025, 71, 12, 2, Interface);
		setBounds(30032, 103, 10, 3, Interface);
		setBounds(30040, 135, 12, 4, Interface);
		setBounds(30048, 165, 10, 5, Interface);
		setBounds(30056, 8, 38, 6, Interface);
		setBounds(30064, 39, 39, 7, Interface);
		setBounds(30075, 71, 39, 8, Interface);
		setBounds(30083, 103, 39, 9, Interface);
		setBounds(30091, 135, 39, 10, Interface);
		setBounds(30099, 165, 37, 11, Interface);
		setBounds(30106, 12, 68, 12, Interface);
		setBounds(30114, 42, 68, 13, Interface);
		setBounds(30122, 71, 68, 14, Interface);
		setBounds(30130, 103, 68, 15, Interface);
		setBounds(30138, 135, 68, 16, Interface);
		setBounds(30146, 165, 68, 17, Interface);
		setBounds(30154, 14, 97, 18, Interface);
		setBounds(30162, 42, 97, 19, Interface);
		setBounds(30170, 71, 97, 20, Interface);
		setBounds(30178, 101, 97, 21, Interface);
		setBounds(30186, 135, 98, 22, Interface);
		setBounds(30194, 168, 98, 23, Interface);
		setBounds(30202, 11, 125, 24, Interface);
		setBounds(30210, 42, 124, 25, Interface);
		setBounds(30218, 74, 125, 26, Interface);
		setBounds(30226, 103, 125, 27, Interface);
		setBounds(30234, 135, 125, 28, Interface);
		setBounds(30242, 164, 126, 29, Interface);
		setBounds(30250, 10, 155, 30, Interface);
		setBounds(30258, 42, 155, 31, Interface);
		setBounds(30266, 71, 155, 32, Interface);
		setBounds(30274, 103, 155, 33, Interface);
		setBounds(30282, 136, 155, 34, Interface);
		setBounds(30290, 165, 155, 35, Interface);
		setBounds(30298, 13, 185, 36, Interface);
		setBounds(30306, 42, 185, 37, Interface);
		setBounds(30314, 71, 184, 38, Interface);
		setBounds(30322, 104, 184, 39, Interface);
		setBounds(30001, 6, 184, 40, Interface);
		setBounds(30018, 5, 176, 41, Interface);
		setBounds(30026, 5, 176, 42, Interface);
		setBounds(30033, 5, 163, 43, Interface);
		setBounds(30041, 5, 176, 44, Interface);
		setBounds(30049, 5, 176, 45, Interface);
		setBounds(30057, 5, 176, 46, Interface);
		setBounds(30065, 5, 176, 47, Interface);
		setBounds(30076, 5, 163, 48, Interface);
		setBounds(30084, 5, 176, 49, Interface);
		setBounds(30092, 5, 176, 50, Interface);
		setBounds(30100, 5, 176, 51, Interface);
		setBounds(30107, 5, 176, 52, Interface);
		setBounds(30115, 5, 163, 53, Interface);
		setBounds(30123, 5, 176, 54, Interface);
		setBounds(30131, 5, 163, 55, Interface);
		setBounds(30139, 5, 163, 56, Interface);
		setBounds(30147, 5, 163, 57, Interface);
		setBounds(30155, 5, 176, 58, Interface);
		setBounds(30163, 5, 176, 59, Interface);
		setBounds(30171, 5, 176, 60, Interface);
		setBounds(30179, 5, 163, 61, Interface);
		setBounds(30187, 5, 176, 62, Interface);
		setBounds(30195, 5, 149, 63, Interface);
		setBounds(30203, 5, 176, 64, Interface);
		setBounds(30211, 5, 163, 65, Interface);
		setBounds(30219, 5, 163, 66, Interface);
		setBounds(30227, 5, 176, 67, Interface);
		setBounds(30235, 5, 149, 68, Interface);
		setBounds(30243, 5, 176, 69, Interface);
		setBounds(30251, 5, 5, 70, Interface);
		setBounds(30259, 5, 5, 71, Interface);
		setBounds(30267, 5, 5, 72, Interface);
		setBounds(30275, 5, 5, 73, Interface);
		setBounds(30283, 5, 5, 74, Interface);
		setBounds(30291, 5, 5, 75, Interface);
		setBounds(30299, 5, 5, 76, Interface);
		setBounds(30307, 5, 5, 77, Interface);
		setBounds(30323, 5, 5, 78, Interface);
		setBounds(30315, 5, 5, 79, Interface);
	}

	public static void addPrayer(int ID, String tooltip, int w, int h, int glowSprite, int glowX, int glowY, int disabledSprite, int enabledSprite, int config, int configFrame, int hover, int levelRequired) {
		RSInterface p = addTabInterface(ID);
		p.parentID = 5608;
		p.type = 5;
		p.atActionType = 1;
		p.width = w;
		p.height = h;
		p.requiredValues = new int[]{1};
		p.valueCompareType = new int[]{1};
		p.scripts = new int[][]{{5, configFrame, 0}};
		p.tooltip = tooltip;
		p.disabledMessage = tooltip;
		p.hoverType = 52;
		p.enabledSprite = Client.spriteCache.get(glowSprite);
		p.spriteXOffset = glowX;
		p.spriteYOffset = glowY;
		p = addTabInterface(ID + 1);
		p.parentID = 5608;
		p.type = 5;
		p.atActionType = 0;
		p.width = w;
		p.height = h;
		p.requiredValues = new int[]{levelRequired};
		p.valueCompareType = new int[]{3};
		p.scripts = new int[][]{{2, 5, 0}};
		p.tooltip = tooltip;
		p.disabledMessage = tooltip;
		p.enabledSprite = Client.spriteCache.get(enabledSprite); //imageLoader(disabledSprite, "s");
		p.disabledSprite = Client.spriteCache.get(disabledSprite); //imageLoader(enabledSprite, "s");
		p.hoverType = hover;
	}

	public static boolean hoverFix;

	public static void addPrayerHover(int ID, String hover, int xOffset, int yOffset) {
		RSInterface p = addTabInterface(ID);
		p.parentID = 5608;
		p.type = 8;
		p.width = 40;
		p.height = 32;
		p.disabledMessage = hover;
		p.tooltipBounds = true;
	}

	/* Side Bars */
	public static void Sidebar0(TextDrawingArea[] tda) {
		Sidebar0a(1698, 1701, 7499, "Chop", "Hack", "Smash", "Block", 42, 75, 127, 75, 39, 128, 125, 128, 122, 103, 40, 50, 122, 50, 40, 103, tda);
		Sidebar0a(2276, 2279, 7574, "Stab", "Lunge", "Slash", "Block", 43, 75, 124, 75, 41, 128, 125, 128, 122, 103, 40, 50, 122, 50, 40, 103, tda);
		Sidebar0a(2423, 2426, 7599, "Chop", "Slash", "Lunge", "Block", 42, 75, 125, 75, 40, 128, 125, 128, 122, 103, 40, 50, 122, 50, 40, 103, tda);
		Sidebar0a(3796, 3799, 7624, "Pound", "Pummel", "Spike", "Block", 39, 75, 121, 75, 41, 128, 125, 128, 122, 103, 40, 50, 122, 50, 40, 103, tda);
		Sidebar0a(4679, 4682, 7674, "Lunge", "Swipe", "Pound", "Block", 40, 75, 124, 75, 39, 128, 125, 128, 122, 103, 40, 50, 122, 50, 40, 103, tda);
		Sidebar0a(4705, 4708, 7699, "Chop", "Slash", "Smash", "Block", 42, 75, 125, 75, 39, 128, 125, 128, 122, 103, 40, 50, 122, 50, 40, 103, tda);
		Sidebar0a(5570, 5573, 7724, "Spike", "Impale", "Smash", "Block", 41, 75, 123, 75, 39, 128, 125, 128, 122, 103, 40, 50, 122, 50, 40, 103, tda);
		Sidebar0a(7762, 7765, 7800, "Chop", "Slash", "Lunge", "Block", 42, 75, 125, 75, 40, 128, 125, 128, 122, 103, 40, 50, 122, 50, 40, 103, tda);
		Sidebar0b(776, 779, "Reap", "Chop", "Jab", "Block", 42, 75, 126, 75, 46, 128, 125, 128, 122, 103, 122, 50, 40, 103, 40, 50, tda);
		Sidebar0c(425, 428, 7474, "Pound", "Pummel", "Block", 39, 75, 121, 75, 42, 128, 40, 103, 40, 50, 122, 50, tda);
		Sidebar0c(1749, 1752, 7524, "Accurate", "Rapid", "Longrange", 33, 75, 125, 75, 29, 128, 40, 103, 40, 50, 122, 50, tda);
		Sidebar0c(1764, 1767, 7549, "Accurate", "Rapid", "Longrange", 33, 75, 125, 75, 29, 128, 40, 103, 40, 50, 122, 50, tda);
		Sidebar0c(4446, 4449, 7649, "Accurate", "Rapid", "Longrange", 33, 75, 125, 75, 29, 128, 40, 103, 40, 50, 122, 50, tda);
		Sidebar0c(5855, 5857, 7749, "Punch", "Kick", "Block", 40, 75, 129, 75, 42, 128, 40, 50, 122, 50, 40, 103, tda);
		Sidebar0c(6103, 6132, 6117, "Bash", "Pound", "Block", 43, 75, 124, 75, 42, 128, 40, 103, 40, 50, 122, 50, tda);
		Sidebar0c(8460, 8463, 8493, "Jab", "Swipe", "Fend", 46, 75, 124, 75, 43, 128, 40, 103, 40, 50, 122, 50, tda);
		Sidebar0c(12290, 12293, 12323, "Flick", "Lash", "Deflect", 44, 75, 127, 75, 36, 128, 40, 50, 40, 103, 122, 50, tda);
		Sidebar0d(328, 331, "Bash", "Pound", "Focus", 42, 66, 39, 101, 41, 136, 40, 120, 40, 50, 40, 85, tda);

		RSInterface rsi = addInterface(19300);
		textSize(3983, tda, 0);
		addToggleButton(150, 150, 172, 150, 44, "Auto Retaliate");
		rsi.totalChildren(2, 2, 2);
		rsi.child(0, 3983, 52, 25);
		rsi.child(1, 150, 21, 153);
		rsi = getInterfaceCache()[3983];
		rsi.centerText = true;
		rsi.textColor = 0xff981f;
	}

	public static void Sidebar0a(int id, int id2, int id3, String text1, String text2, String text3, String text4, int str1x, int str1y, int str2x, int str2y, int str3x, int str3y, int str4x, int str4y, int img1x, int img1y, int img2x, int img2y, int img3x, int img3y, int img4x, int img4y, TextDrawingArea[] tda) {
		RSInterface rsi = addInterface(id);
		addText(id2, "-2", tda, 3, 0xff981f, true);
		addText(id2 + 11, text1, tda, 0, 0xff981f, false);
		addText(id2 + 12, text2, tda, 0, 0xff981f, false);
		addText(id2 + 13, text3, tda, 0, 0xff981f, false);
		addText(id2 + 14, text4, tda, 0, 0xff981f, false);

		rsi.specialBar(id3);
		rsi.width = 190;
		rsi.height = 261;
		int last = 15;
		int frame = 0;
		rsi.totalChildren(last, last, last);

		rsi.child(frame, id2 + 3, 21, 46);
		frame++;
		rsi.child(frame, id2 + 4, 104, 99);
		frame++;
		rsi.child(frame, id2 + 5, 21, 99);
		frame++;
		rsi.child(frame, id2 + 6, 105, 46);
		frame++;
		rsi.child(frame, id2 + 7, img1x, img1y);
		frame++;
		rsi.child(frame, id2 + 8, img2x, img2y);
		frame++;
		rsi.child(frame, id2 + 9, img3x, img3y);
		frame++;
		rsi.child(frame, id2 + 10, img4x, img4y);
		frame++;
		rsi.child(frame, id2 + 11, str1x, str1y);
		frame++;
		rsi.child(frame, id2 + 12, str2x, str2y);
		frame++;
		rsi.child(frame, id2 + 13, str3x, str3y);
		frame++;
		rsi.child(frame, id2 + 14, str4x, str4y);
		frame++;
		rsi.child(frame, 19300, 0, 0);
		frame++;
		rsi.child(frame, id2, 94, 4);
		frame++;
		rsi.child(frame, id3, 21, 205);
		frame++;

		for (int i = id2 + 3; i < id2 + 7; i++) {
			rsi = getInterfaceCache()[i];
			rsi.disabledSprite = CustomSpriteLoader(19301, "");
			rsi.enabledSprite = CustomSpriteLoader(19301, "a");
			rsi.width = 68;
			rsi.height = 44;
		}
	}

	public static void Sidebar0b(int id, int id2, String text1, String text2, String text3, String text4, int str1x, int str1y, int str2x, int str2y, int str3x, int str3y, int str4x, int str4y, int img1x, int img1y, int img2x, int img2y, int img3x, int img3y, int img4x, int img4y, TextDrawingArea[] tda) {
		RSInterface rsi = addInterface(id); // 2423
		addText(id2, "-2", tda, 3, 0xff981f, true);
		addText(id2 + 11, text1, tda, 0, 0xff981f, false);
		addText(id2 + 12, text2, tda, 0, 0xff981f, false);
		addText(id2 + 13, text3, tda, 0, 0xff981f, false);
		addText(id2 + 14, text4, tda, 0, 0xff981f, false);

		rsi.width = 190;
		rsi.height = 261;
		int last = 14;
		int frame = 0;
		rsi.totalChildren(last, last, last);

		rsi.child(frame, id2 + 3, 21, 46);
		frame++;
		rsi.child(frame, id2 + 4, 104, 99);
		frame++;
		rsi.child(frame, id2 + 5, 21, 99);
		frame++;
		rsi.child(frame, id2 + 6, 105, 46);
		frame++;
		rsi.child(frame, id2 + 7, img1x, img1y);
		frame++;
		rsi.child(frame, id2 + 8, img2x, img2y);
		frame++;
		rsi.child(frame, id2 + 9, img3x, img3y);
		frame++;
		rsi.child(frame, id2 + 10, img4x, img4y);
		frame++;
		rsi.child(frame, id2 + 11, str1x, str1y);
		frame++;
		rsi.child(frame, id2 + 12, str2x, str2y);
		frame++;
		rsi.child(frame, id2 + 13, str3x, str3y);
		frame++;
		rsi.child(frame, id2 + 14, str4x, str4y);
		frame++;
		rsi.child(frame, 19300, 0, 0);
		frame++;
		rsi.child(frame, id2, 94, 4);
		frame++;

		for (int i = id2 + 3; i < id2 + 7; i++) {
			rsi = getInterfaceCache()[i];
			rsi.disabledSprite = CustomSpriteLoader(19301, "");
			rsi.enabledSprite = CustomSpriteLoader(19301, "a");
			rsi.width = 68;
			rsi.height = 44;
		}
	}

	public static void Sidebar0c(int id, int id2, int id3, String text1, String text2, String text3, int str1x, int str1y, int str2x, int str2y, int str3x, int str3y, int img1x, int img1y, int img2x, int img2y, int img3x, int img3y, TextDrawingArea[] tda) {
		RSInterface rsi = addInterface(id);
		addText(id2, "-2", tda, 3, 0xff981f, true);
		addText(id2 + 9, text1, tda, 0, 0xff981f, false);
		addText(id2 + 10, text2, tda, 0, 0xff981f, false);
		addText(id2 + 11, text3, tda, 0, 0xff981f, false);
		rsi.specialBar(id3);
		rsi.width = 190;
		rsi.height = 261;
		int last = 12;
		int frame = 0;
		rsi.totalChildren(last, last, last);

		rsi.child(frame, id2 + 3, 21, 99);
		frame++;
		rsi.child(frame, id2 + 4, 105, 46);
		frame++;
		rsi.child(frame, id2 + 5, 21, 46);
		frame++;
		rsi.child(frame, id2 + 6, img1x, img1y);
		frame++;
		rsi.child(frame, id2 + 7, img2x, img2y);
		frame++;
		rsi.child(frame, id2 + 8, img3x, img3y);
		frame++;
		rsi.child(frame, id2 + 9, str1x, str1y);
		frame++;
		rsi.child(frame, id2 + 10, str2x, str2y);
		frame++;
		rsi.child(frame, id2 + 11, str3x, str3y);
		frame++;
		rsi.child(frame, 19300, 0, 0);
		frame++;
		rsi.child(frame, id2, 94, 4);
		frame++;
		rsi.child(frame, id3, 21, 205);
		frame++;

		for (int i = id2 + 3; i < id2 + 6; i++) {
			rsi = getInterfaceCache()[i];
			rsi.disabledSprite = CustomSpriteLoader(19301, "");
			rsi.enabledSprite = CustomSpriteLoader(19301, "a");
			rsi.width = 68;
			rsi.height = 44;
		}
	}

	public static void Sidebar0d(int id, int id2, String text1, String text2, String text3, int str1x, int str1y, int str2x, int str2y, int str3x, int str3y, int img1x, int img1y, int img2x, int img2y, int img3x, int img3y, TextDrawingArea[] tda) {
		RSInterface rsi = addInterface(id);
		addText(id2, "-2", tda, 3, 0xff981f, true);
		addText(id2 + 9, text1, tda, 0, 0xff981f, false);
		addText(id2 + 10, text2, tda, 0, 0xff981f, false);
		addText(id2 + 11, text3, tda, 0, 0xff981f, false);
		removeSomething(353);
		addText(354, "Spell", tda, 0, 0xff981f, false);
		addCacheSprite(337, 19, 0, "combaticons");
		addCacheSprite(338, 13, 0, "combaticons2");
		addCacheSprite(339, 14, 0, "combaticons2");
		removeSomething(349);
		addToggleButton(350, 350, 108, 68, 44, "Select");

		rsi.width = 190;
		rsi.height = 261;
		int last = 15;
		int frame = 0;
		rsi.totalChildren(last, last, last);

		rsi.child(frame, id2 + 3, 20, 115);
		frame++;
		rsi.child(frame, id2 + 4, 20, 80);
		frame++;
		rsi.child(frame, id2 + 5, 20, 45);
		frame++;
		rsi.child(frame, id2 + 6, img1x, img1y);
		frame++;
		rsi.child(frame, id2 + 7, img2x, img2y);
		frame++;
		rsi.child(frame, id2 + 8, img3x, img3y);
		frame++;
		rsi.child(frame, id2 + 9, str1x, str1y);
		frame++;
		rsi.child(frame, id2 + 10, str2x, str2y);
		frame++;
		rsi.child(frame, id2 + 11, str3x, str3y);
		frame++;
		rsi.child(frame, 349, 105, 46);
		frame++;
		rsi.child(frame, 350, 104, 106);
		frame++;
		rsi.child(frame, 353, 125, 74);
		frame++;
		rsi.child(frame, 354, 125, 134);
		frame++;
		rsi.child(frame, 19300, 0, 0);
		frame++;
		rsi.child(frame, id2, 94, 4);
		frame++;
	}

	public void setSprite(Sprite sprite) {
		disabledSprite = sprite;
	}

	public DropdownMenu dropDown;
	public boolean hovered = false;
	public RSInterface dropDownOpen;
	public int dropDownHover = -1;

	//0xFD961E

	public static void addDropdownMenu(int identification, int width, int defaultOption, boolean split, boolean center, Dropdown dropdown, String... options) {
		addDropdownMenu(identification, width, defaultOption, 0xFD961E, split, center, dropdown, options);
	}

	public static void addDropdownMenu(int identification, int width, int defaultOption, int textColor, boolean split, boolean center, Dropdown dropdown, String... options) {
		RSInterface dropdownMenu = addInterface(identification);
		dropdownMenu.type = 69;
		dropdownMenu.atActionType = 69;
		dropdownMenu.centerText = center;
		dropdownMenu.textColor = textColor;
		dropdownMenu.disabledMessage = "";
		dropdownMenu.dropDown = new DropdownMenu(width, defaultOption, dropdown, options);
	}
}
