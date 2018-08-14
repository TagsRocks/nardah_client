package com.nardah;

final class Item extends Renderable {

	public Item() {
	}

	public final Model getRotatedModel() {
		ItemDefinition itemDef = ItemDefinition.lookup(itemId);
		return itemDef.method201(itemAmount);
	}

	public int itemId;
	public int itemAmount;
	public int type;
}
