package com.nardah.updater;

/**
 * A {@code UpdateState} represents the current state of a file update.
 *
 * @author Michael | Chex
 */
public enum UpdateState {

	/**
	 * The default screen state.
	 */
	DEFAULT_SCREEN,

	/**
	 * The welcome screen state.
	 */
	WELCOME_SCREEN,

	/**
	 * The state to check for updates.
	 */
	CHECK_FOR_UPDATES,

	/**
	 * The cache updating state.
	 */
	UPDATE_CACHE,

	/**
	 * The client updating state.
	 */
	UPDATE_CLIENT,

	/**
	 * The state where no cache or client updates are required.
	 */
	UP_TO_DATE,

	/**
	 * The finished state.
	 */
	FINISHED

}
