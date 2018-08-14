package com.nardah;

/**
 * The account data class.
 *
 * @author Daniel.
 */
public class AccountData {

	/** The account username. */
	public String username;

	/** The account password. */
	public String password;

	/** The account created date. */
	public String created;

	/** The account avatar. */
	public int avatar;

	/**  The account rank. */
	public int rank;

	/** The account usage amount.  */
	public int uses;

	/** Constructs a new <code>AccountData</code>.  */
	AccountData(String username, String password, String created, int avatar, int rank, int uses) {
		this.username = username;
		this.password = password;
		this.created = created;
		this.avatar = avatar;
		this.rank = rank;
		this.uses = uses;
	}

	/** Constructs a new <code>AccountData</code>.  */
	AccountData(String username, String password) {
		this(username, password, Utility.getDate(), 534, -1, 0);
	}

	@Override
	public String toString() {
		return "[Username: " + username + " | Password: " + password + " | Created: " + created + " | Avatar: " + avatar + " | Rank: " + rank + " | Uses: " + uses + "]";
	}
}