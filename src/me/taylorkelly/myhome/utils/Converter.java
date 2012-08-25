package me.taylorkelly.myhome.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

import me.taylorkelly.myhome.data.Home;
import me.taylorkelly.myhome.data.HomeList;
import me.taylorkelly.myhome.sql.ConnectionManager;

import org.bukkit.*;
import org.bukkit.entity.Player;

public class Converter {
	public static void convert(Player player, Server server, HomeList lister) {
		File file = new File("homes.txt");
		PreparedStatement ps = null;
		Connection conn;
		try {
			conn = ConnectionManager.getConnection();
			ps = conn.prepareStatement("INSERT INTO homeTable (id, name, world, x, y, z, yaw, pitch, publicAll, permissions, welcomeMessage) VALUES (?,?,?,?,?,?,?,?,?,?,?)");

			Scanner scanner = new Scanner(file);
			int size = 0;
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				if (line.equals("")) {
					continue;
				}
				String[] pieces = line.split(":");
				if (pieces.length == 6) {
					String name = pieces[0];
					double x = Double.parseDouble(pieces[1]);
					double y = Double.parseDouble(pieces[2]);
					double z = Double.parseDouble(pieces[3]);
					double yaw = Double.parseDouble(pieces[4]);
					double pitch = Double.parseDouble(pieces[5]);

					if (lister.homeExists(name)) {
						player.sendMessage(ChatColor.RED + name + " already has a home. Skipping extra entry.");
						continue;
					}

					yaw = (yaw < 0) ? (360 + (yaw % 360)) : (yaw % 360);

					World world = server.getWorlds().get(0);
					Location location = new Location(world, x, y, z, (float) yaw, (float) pitch);
					Home warp = new Home(name, location);
					lister.blindAdd(warp);

					ps.setInt(1, warp.index);
					ps.setString(2, warp.name);
					ps.setString(3, warp.world);
					ps.setDouble(4, warp.x);
					ps.setDouble(5, warp.y);
					ps.setDouble(6, warp.z);
					ps.setInt(7, warp.yaw);
					ps.setInt(8, warp.pitch);
					ps.setInt(9, warp.publicAll);
					ps.setString(10, warp.permissionsString());
					ps.setString(11, warp.welcomeMessage);
					ps.addBatch();
					size++;
				} else {
					if (pieces.length > 0) {
						player.sendMessage(ChatColor.RED + pieces[0] + " is a corrupted home. Skipping.");
						System.out.println("[MYHOME] " + pieces[0] + " is a corrupted home.");
					}
				}
			}
			ps.executeBatch();
			conn.commit();
			file.delete();
			player.sendMessage("Successfully imported " + size + " homes.");
		} catch (FileNotFoundException e) {
			player.sendMessage(ChatColor.RED + "Error: 'homes.txt' doesn't exist.");
		} catch (SQLException e) {
			player.sendMessage(ChatColor.RED + "Error: SQLite Exception");
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException ex) {
				player.sendMessage(ChatColor.RED + "Error: SQLite Exception (on close)");
			}
		}
	}
	public static void convert(Server server, HomeList lister) {
		File file = new File("homes.txt");
		PreparedStatement ps = null;
		Connection conn;
		try {
			conn = ConnectionManager.getConnection();
			ps = conn.prepareStatement("INSERT INTO homeTable (id, name, world, x, y, z, yaw, pitch, publicAll, permissions, welcomeMessage) VALUES (?,?,?,?,?,?,?,?,?,?,?)");

			Scanner scanner = new Scanner(file);
			int size = 0;
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				if (line.equals("")) {
					continue;
				}
				String[] pieces = line.split(":");
				if (pieces.length == 6) {
					String name = pieces[0];
					double x = Double.parseDouble(pieces[1]);
					double y = Double.parseDouble(pieces[2]);
					double z = Double.parseDouble(pieces[3]);
					double yaw = Double.parseDouble(pieces[4]);
					double pitch = Double.parseDouble(pieces[5]);

					if (lister.homeExists(name)) {
						HomeLogger.warning(name + " already has a home. Skipping extra entry.");
						continue;
					}

					yaw = (yaw < 0) ? (360 + (yaw % 360)) : (yaw % 360);

					World world = server.getWorlds().get(0);
					Location location = new Location(world, x, y, z, (float) yaw, (float) pitch);
					Home warp = new Home(name, location);
					lister.blindAdd(warp);

					ps.setInt(1, warp.index);
					ps.setString(2, warp.name);
					ps.setString(3, warp.world);
					ps.setDouble(4, warp.x);
					ps.setDouble(5, warp.y);
					ps.setDouble(6, warp.z);
					ps.setInt(7, warp.yaw);
					ps.setInt(8, warp.pitch);
					ps.setInt(9, warp.publicAll);
					ps.setString(10, warp.permissionsString());
					ps.setString(11, warp.welcomeMessage);
					ps.addBatch();
					size++;
				} else {
					if (pieces.length > 0) {
						HomeLogger.warning(pieces[0] + " is a corrupted home. Skipping.");
					}
				}
			}
			ps.executeBatch();
			conn.commit();
			file.delete();
			HomeLogger.info("Successfully imported " + size + " homes.");
		} catch (FileNotFoundException e) {
			HomeLogger.severe("Error: 'homes.txt' doesn't exist.");
		} catch (SQLException e) {
			HomeLogger.severe("Error: SQLite Exception");
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException ex) {
				HomeLogger.severe("Error: SQLite Exception (on close)");
			}
		}
	}
	
}
