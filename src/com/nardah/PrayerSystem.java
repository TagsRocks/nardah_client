package com.nardah;


public class PrayerSystem {
	
	public static InterfaceData positions[] = null;
	
	public enum InterfaceData {
		THICK_SKIN(5609, 22638, 5632, 3, 2),
		BURST_OF_STRENGTH(5610, 22639, 5633, 5, 4),
		CLARITY_OF_THOUGHT(5611, 22640, 5634, 3, 5),
		SHARP_EYE(19812, 22641, 19813, 3, 5),
		MYSTIC_WILL(19814, 22642, 19815, 3, 5),
		ROCK_SKIN(5612, 20253, 5635, 3, 2),
		SUPERHUMAN_STRENGTH(5613, 20254, 5636, 5, 4),
		IMPROVED_REFLEXES(5614, 20255, 5637, 3, 5),
		RAPID_RESTORE(5615, 20256, 5638, 3, 5),
		RAPID_HEAL(5616, 20257, 5639, 3, 5),
		PROTECT_ITEM(5617, 21141, 5640, 2, 2),
		HAWK_EYE(19816, 21142, 19817, 3, 4),
		MYSTIC_LORE(19818, 21143, 19820, 3, 4),
		STEEL_SKIN(5618, 21144, 5641, 3, 1),
		ULTIMATE_STRENGTH(5619, 21145, 5642, 5, 3),
		INCREDIBLE_REFLEXES(5620, 21146, 5643, 3, 5),
		PROTECT_FROM_MAGIC(5621, 21147, 5644, 4, 3),
		PROTECT_FROM_MISSILES(5622, 21148, 686, 6, 4),
		PROTECT_FROM_MELEE(5623, 21149, 5645, 2, 2),
		EAGLE_EYE(19821, 21150, 19822, 3, 4),
		MYSTIC_MIGHT(19823, 21135, 19824, 3, 5),
		RETRIBUTION(683, 21136, 5649, 2, 2),
		REDEMPTION(684, 21137, 5647, 3, 5),
		SMITE(685, 21138, 5648, 2, 2),
		PRESERVE(28001, 28003, 28002, 3, 0),
		CHIVALRY(19825, 21139, 19826, 7, 2),
		PIETY(19827, 21140, 19828, 2, 10),
		RIGOUR(28004, 28006, 28005, 4, 1),
		AUGURY(28007, 28009, 28008, 4, 1);
		public final int buttonId;
		public final int tooltipId;
		public final int spriteId;
		public final int spriteX;
		public final int spriteY;
		private InterfaceData(int backButtonId, int tooltipId, int spriteId, int spriteX, int spriteY) {
			this.buttonId = backButtonId;
			this.tooltipId = tooltipId;
			this.spriteId = spriteId;
			this.spriteX = spriteX;
			this.spriteY = spriteY;
		}
		public static InterfaceData searchByButton(int button) {
			for (InterfaceData i : PrayerSystem.positions) {
				if (i.buttonId == button) {
					return i;
				}
			}
			return null;
		}
		public static InterfaceData searchByName(String name) {
			for (InterfaceData i : InterfaceData.values()) {
				if (i.name().equals(name)) {
					return i;
				}
			}
			return null;
		}
	}
	
	
	public static void release(InterfaceData data, int x, int y) {
		InterfaceData other = null;
		int otherSlot = -1;
		int searchX = 4, searchY = 10;
		for (InterfaceData d : positions) {
			otherSlot++;
			if (x >= searchX && y >= searchY && x <= searchX + 34 && y <= searchY + 34) {
				other = d;
				break;
			}
			searchX += 37;
			if (searchX > 160) {
				searchX = 4;
				searchY += 37;
			}
		}
		if (other != null) {
			int dataSlot = getPositionSlot(data);
			positions[otherSlot] = data;
			positions[dataSlot] = other;
			prayerPlacement();
			Saving.save();
		}
	}
	
	public static int getPositionSlot(InterfaceData d) {
		for (int i = 0; i < 29; ++i) {
			if (positions[i] == d) {
				return i;
			}
		}
		return -1;
	}
	
	
	public static void prayerPlacement() {
		if (positions == null) {
			positions = new InterfaceData[29];
			for (InterfaceData i : InterfaceData.values()) {
				positions[i.ordinal()] = i;
			}
		}
		RSInterface tab = RSInterface.interfaceCache[5608];
		tab.totalChildren(89);
		int child = 0;
		tab.child(child++, 687, 84, 241);
		tab.child(child++, 5651, 63, 239);
		int x = 4, y = 10;
		for (int i = 0; i < 29; ++i) {
			tab.child(child++, positions[i].buttonId, x, y);
			x += 37;
			if (x > 160) {
				x = 4;
				y += 37;
			}
		}
		x = 4; y = 10;
		for (int i = 0; i < 29; ++i) {
			tab.child(child++, positions[i].spriteId, x + positions[i].spriteX, y + positions[i].spriteY);
			x += 37;
			if (x > 160) {
				x = 4;
				y += 37;
			}
		}
		x = -1; y = 10;
		for (int i = 0; i < 29; ++i) {
			tab.child(child++, positions[i].tooltipId, x, y);
			x += 37;
			if (x > 155) {
				x = 4;
				y += 37;
			}
		}
	}
	
	
	public static void load(String[] order) {
		boolean corrupt = false;
		positions = new InterfaceData[29];
		for (int i = 0; i < 29; ++i) {
			positions[i] = InterfaceData.searchByName(order[i]);
			if (positions[i] == null)
				corrupt = true;
		}
		if (corrupt) {
			positions = new InterfaceData[29];
			for (InterfaceData i : InterfaceData.values()) {
				positions[i.ordinal()] = i;
			}
		}
	}
	

}
