package com.github.lunatrius.ingameinfo.tag;

import com.github.lunatrius.ingameinfo.tag.registry.TagRegistry;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class TagNearbyPlayer extends Tag {
	private static final Comparator<EntityPlayer> PLAYER_DISTANCE_COMPARATOR = new Comparator<EntityPlayer>() {
		@Override
		public int compare(EntityPlayer playerA, EntityPlayer playerB) {
			if (Tag.player == null) {
				return 0;
			}

			double distanceA = Tag.player.getDistanceSqToEntity(playerA);
			double distanceB = Tag.player.getDistanceSqToEntity(playerB);
			if (distanceA > distanceB) {
				return 1;
			} else if (distanceA < distanceB) {
				return -1;
			}
			return 0;
		}
	};
	protected static EntityPlayer[] nearbyPlayers = null;
	protected final int index;

	public TagNearbyPlayer(int index) {
		this.index = index;
	}

	protected static void updateNearbyPlayers() {
		if (nearbyPlayers == null) {
			List<EntityPlayer> playerList = new ArrayList<EntityPlayer>();
			for (EntityPlayer player : (List<EntityPlayer>) world.playerEntities) {
				if (player != Tag.player && !player.isSneaking()) {
					playerList.add(player);
				}
			}

			Collections.sort(playerList, PLAYER_DISTANCE_COMPARATOR);
			nearbyPlayers = playerList.toArray(new EntityPlayer[playerList.size()]);
		}
	}

	public static class Name extends TagNearbyPlayer {
		public Name(int index) {
			super(index);
		}

		@Override
		public String getValue() {
			updateNearbyPlayers();
			if (nearbyPlayers.length > this.index) {
				return nearbyPlayers[this.index].func_145748_c_().getFormattedText();
			}
			return "";
		}
	}

	public static class Distance extends TagNearbyPlayer {
		public Distance(int index) {
			super(index);
		}

		@Override
		public String getValue() {
			updateNearbyPlayers();
			if (nearbyPlayers.length > this.index) {
				return String.format("%.2f", nearbyPlayers[this.index].getDistanceToEntity(player));
			}
			return "-1";
		}
	}

	public static void register() {
		for (int i = 0; i < 16; i++) {
			TagRegistry.INSTANCE.register(new Name(i), "nearbyplayername" + i);
			TagRegistry.INSTANCE.register(new Distance(i), "nearbyplayerdistance" + i);
		}
	}

	public static void releaseResources() {
		nearbyPlayers = null;
	}
}
