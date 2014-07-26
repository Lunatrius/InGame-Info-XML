package com.github.lunatrius.ingameinfo.tag;

import com.github.lunatrius.core.util.vector.Vector3f;
import com.github.lunatrius.core.util.vector.Vector3i;
import com.github.lunatrius.ingameinfo.client.gui.Info;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import java.util.List;

public abstract class Tag {
	protected static final Minecraft minecraft = Minecraft.getMinecraft();
	protected static final Vector3i playerPosition = new Vector3i();
	protected static final Vector3f playerMotion = new Vector3f();
	protected static MinecraftServer server;
	protected static World world;
	protected static EntityClientPlayerMP player;
	protected static List<Info> info;
	protected static boolean hasSeed = false;
	protected static long seed = 0;

	public abstract String getValue();

	public static void setServer(MinecraftServer server) {
		Tag.server = server;

		try {
			setSeed(Tag.server.worldServerForDimension(0).getSeed());
		} catch (Exception e) {
			unsetSeed();
		}
	}

	public static void setSeed(long seed) {
		Tag.hasSeed = true;
		Tag.seed = seed;
	}

	public static void unsetSeed() {
		Tag.hasSeed = false;
		Tag.seed = 0;
	}

	public static void setWorld(World world) {
		Tag.world = world;
	}

	public static void setPlayer(EntityClientPlayerMP player) {
		Tag.player = player;

		if (player != null) {
			playerPosition.set((int) Math.floor(player.posX), (int) Math.floor(player.posY), (int) Math.floor(player.posZ));
			playerMotion.set((float) (player.posX - player.prevPosX), (float) (player.posY - player.prevPosY), (float) (player.posZ - player.prevPosZ));
		}
	}

	public static void setInfo(List<Info> info) {
		Tag.info = info;
	}

	public static void releaseResources() {
		setWorld(null);
		setPlayer(null);
		TagNearbyPlayer.releaseResources();
		TagPlayerPotion.releaseResources();
	}

	public static String getIconTag(Info info) {
		String str = "";
		for (int i = 0; i < info.getWidth() && minecraft.fontRenderer.getStringWidth(str) < info.getWidth(); i++) {
			str += " ";
		}
		return String.format("{ICON|%s}", str);
	}
}
