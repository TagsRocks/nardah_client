package com.nardah;

public final class OnDemandData extends Cacheable {

	public OnDemandData() {
		incomplete = true;
	}

	int dataType;
	byte buffer[];
	int ID;
	boolean incomplete;
	int loopCycle;
}
