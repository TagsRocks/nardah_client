package com.nardah;

public class Linkable {

	public final void unlink() {
		if (next == null) {

		} else {
			next.prev = prev;
			prev.next = next;
			prev = null;
			next = null;
		}
	}

	public long id;
	public Linkable prev;
	public Linkable next;
}
