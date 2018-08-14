package com.nardah;

import java.io.*;

public class Saving {


	public static void save() {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(Utility.findcachedir()+ "Prayers.ini"));
			writer.write("[CLIENT SETTINGS]", 0, 17);
			writer.newLine();
			String toWrite = "";
			
			toWrite = "PrayerOrder=";
			for (int i = 0; i < 29; ++i) {
				toWrite += ""+PrayerSystem.positions[i].name()+", ";
			}
			writer.write(toWrite, 0, toWrite.length());
			writer.newLine();


			writer.close();
		} catch (IOException localIOException) {
		}
	}


	public static void load() {
		BufferedReader reader = null;
		try {
			try {
				reader = new BufferedReader(new FileReader(Utility.findcachedir()+ "Prayers.ini"));
			} catch (FileNotFoundException localFileNotFoundException) {
				return;
			}
			String line = "";
			try {
				line = reader.readLine();
			} catch (IOException localIOException2) {
			}
			try {
				while((line = reader.readLine()) != null) {

					String[] splitz = line.split("=");

					String newPositions[] = new String[29];
					if (line.startsWith("PrayerOrder")) {
						String[] sp = splitz[1].split(", ");
						for (int i = 0; i < sp.length; i++) {
							newPositions[i] = sp[i];
						}
						PrayerSystem.load(newPositions);
					}

				}
			}
			finally {
				reader.close();
			}
		}catch (IOException ex){
			ex.printStackTrace();
		}
	}

}