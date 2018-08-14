package com.nardah.updater;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.nardah.updater.screen.UpdateComponent;
import com.nardah.updater.screen.UpdaterRenderer;
import com.nardah.updater.screen.impl.*;
import com.nardah.Client;
import com.nardah.Configuration;
import com.nardah.Utility;

/**
 * An update handler that updates the client and cache files.
 *
 * @author Michael | Chex
 */
public class Updater {

	/**
	 * The client version.
	 */
	private static final String CLIENT_VERSION = "http://nardah.com/clientVersion.txt";

	/**
	 * The cache version.
	 */
	private static final String CACHE_VERSION = "http://nardah.com/cacheVersion.txt";

	/**
	 * The cache download url link.
	 */
	public static final String CACHE_LINK = "http://nardah.com/NR_dev.zip";

	/**
	 * The client download url link.
	 */
	private static final String CLIENT_LINK = "http://nardah.com/nardah-client.jar";

	/**
	 * The cache directory.
	 */
	private static final File CACHE_DIRECTORY = new File(Utility.findcachedir());

	/**
	 * The current update state.
	 */
	public UpdateState state = UpdateState.CHECK_FOR_UPDATES;

	/**
	 * An instance of updater
	 */
	private static final Updater INSTANCE = new Updater();

	/**
	 * Constructs a new {@link Updater}.
	 */
	private Updater() {
	    /* can't initialize this class. */
	}

	public void setup(Client client) {
		client.updaterRenderer = new UpdaterRenderer(client, client.graphics);
		if (Client.virgin) {
			state = UpdateState.WELCOME_SCREEN;
			client.updaterRenderer.setScreen(new WelcomeUpdateScreen());
		} else {
			state = UpdateState.DEFAULT_SCREEN;
			client.updaterRenderer.setScreen(new DefaultUpdateScreen());
		}
	}

	public void checkForUpdates() {
		Client client = Client.instance;
		if (checkClientUpdates()) {
			UpdateComponent screen = new ClientUpdateScreen();
			client.updaterRenderer.setScreen(screen);

			try {
				final String path = getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
				System.out.println(path);
				updateClient(path.substring(path.lastIndexOf("/") + 1, path.indexOf(".jar")), screen);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		} else if (checkCacheUpdate()) {
			UpdateComponent screen = new CacheUpdateScreen();
			client.updaterRenderer.setScreen(screen);
			try {
				updateCache(screen);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		finish();
	}

	public void finish() {
		if (Client.virgin) {
			UpdateComponent screen = new GoodbyeUpdateScreen();
			Client.instance.updaterRenderer.setScreen(screen);
			return;
		}

		if (state != UpdateState.FINISHED) {
			Client client = Client.instance;
			Client.updaterScreenIP = null;
			client.updaterRenderer.finish();
			client.updaterRenderer = null;
			System.gc();
			System.runFinalization();
			client.startUp();
			state = UpdateState.FINISHED;
		}
	}

	public static boolean isActive() {
		return get().state != UpdateState.FINISHED;
	}

	/**
	 * Checks the web server for client jar updates.
	 */
	private boolean checkClientUpdates() {
		if (Configuration.DEBUG_MODE) {
			return false;
		}

		int clientVersion = Integer.parseInt(Utility.getNewestVersion(CLIENT_VERSION));
		int current = Configuration.CLIENT_VERSION;

		System.out.println(clientVersion + " " + current);

		// Incorrect client version
		if (current != clientVersion) {
			System.out.println("Client update required!");
			state = UpdateState.UPDATE_CLIENT;
		}
		return state == UpdateState.UPDATE_CLIENT;
	}

	/**
	 * Checks the web server for cache updates.
	 */
	private boolean checkCacheUpdate() {
		if (Configuration.DEBUG_MODE) {
			return false;
		}

		File dat = new File(Utility.findcachedir() + Configuration.SPRITE_FILE_NAME + ".dat");
		File idx = new File(Utility.findcachedir() + Configuration.SPRITE_FILE_NAME + ".idx");

		// do the sprite files exist?
		if (!dat.exists() || !idx.exists()) {
			System.out.println("Cache sprite files do not exist!");
			state = UpdateState.UPDATE_CACHE;
			return true;
		}

		File versionFile = new File(Utility.findcachedir() + "version.dat");

		// check if the version file exists
		if (!versionFile.exists()) {
			System.out.println("Version file does not exist!");
			state = UpdateState.UPDATE_CACHE;
			return true;
		}

		String version = Utility.getNewestVersion(CACHE_VERSION);
		String current = Utility.getCurrentVersion(Utility.findcachedir() + "version.dat");

		// does the version of the cache match the updated version
		if (!current.equalsIgnoreCase(version)) {
			System.out.println("Version file mismatch!");
			state = UpdateState.UPDATE_CACHE;
			return true;
		}

		return state == UpdateState.UPDATE_CACHE;
	}

	/**
	 * Updates the current client jar file with the new downloaded one.
	 *
	 * @param jarName the name of this jar file
	 */
	private void updateClient(String jarName, UpdateComponent screen) {
		File client = new File(System.getProperty("user.dir") + File.separator + jarName + ".jar");
		try {
			File temp = File.createTempFile("tmp", Long.toString(System.nanoTime()));
			temp.deleteOnExit();
			download(CLIENT_LINK, temp, screen);
			replaceData(temp, client);
			Runtime.getRuntime().exec("java -jar " + jarName + ".jar");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	public void updateCache(UpdateComponent screen) throws IOException {
		File temp = File.createTempFile("tmp", Long.toString(System.nanoTime()));
		temp.deleteOnExit();
		download(CACHE_LINK, temp, screen);
		unZipFile(temp, CACHE_DIRECTORY, (CacheUpdateScreen) screen);
		try {
			File file = new File(Utility.findcachedir() + File.separator + "version.dat");
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write("" + Configuration.CACHE_VERSION);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		state = UpdateState.UP_TO_DATE;
		Client.instance.updaterRenderer.setScreen(new UpToDateUpdateScreen());
		System.out.println("Cache was downloaded and extracted successfully.");
	}

	private void download(String link, File target, UpdateComponent screen) {
		try {
			URL url = new URL(link);
			URLConnection conn = url.openConnection();
			BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
			FileOutputStream out = new FileOutputStream(target);
			if (state == UpdateState.UPDATE_CLIENT) {
				screen.setLabel("Downloading client files");
			} else if (state == UpdateState.UPDATE_CACHE) {
				screen.setLabel("Downloading cache files");
			}
			int length;
			long downloaded = 0;
			int total = conn.getContentLength();
			byte[] bytes = new byte[1024];
			while ((length = in.read(bytes)) != -1) {
				out.write(bytes, 0, length);
				downloaded += length;
				if (state == UpdateState.UPDATE_CLIENT) {
					((ClientUpdateScreen) screen).setProgress((int) ((downloaded / (double) total * 100)));
				} else if (state == UpdateState.UPDATE_CACHE) {
					((CacheUpdateScreen) screen).setProgress((int) ((downloaded / (double) total * 100)));
				}
			}
			in.close();
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void unZipFile(File zip, File directory, CacheUpdateScreen screen) throws IOException {
		screen.setLabel("Extracting cache files");
		screen.setProgress(0);

		byte[] bytes = new byte[1024];

		ZipEntry entry;
		ZipInputStream in = new ZipInputStream(new BufferedInputStream(new FileInputStream(zip)));

		while ((entry = in.getNextEntry()) != null) {
			File file = new File(directory, entry.getName());
			if (entry.isDirectory()) {
				if (!file.mkdirs()) {
					System.err.println("Could not create directory '" + file.toPath() + "'!");
				}
			} else {
				FileOutputStream out = new FileOutputStream(file);
				int len;
				long unzipped = 0;
				long total = entry.getSize();
				while ((len = in.read(bytes)) > -1) {
					out.write(bytes, 0, len);
					unzipped += len;
					screen.setProgress((int) (unzipped / (double) total * 100));
				}
				out.flush();
				out.close();
			}
		}
		in.close();
	}

	/**
	 * Replaces the contents of the source file into the target file.
	 *
	 * @param source the source file
	 * @param target the target file
	 * @throws IOException if there was an exception replacing the files
	 */
	private static void replaceData(File source, File target) throws IOException {
		ZipEntry entry;
		int length;
		byte[] buffer = new byte[1024];
		try (ZipInputStream in = new ZipInputStream(new BufferedInputStream(new FileInputStream(source))); ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(target)))) {
			while ((entry = in.getNextEntry()) != null) {
				if (entry.isDirectory())
					continue;

				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				while ((length = in.read(buffer, 0, buffer.length)) > -1) {
					bytes.write(buffer, 0, length);
				}

				out.putNextEntry(entry);
				out.write(bytes.toByteArray());
				out.closeEntry();
			}
			out.finish();
		}
	}

	public static Updater get() {
		return INSTANCE;
	}
}
