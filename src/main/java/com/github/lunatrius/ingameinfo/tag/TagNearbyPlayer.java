package com.github.lunatrius.ingameinfo.tag;

import com.github.lunatrius.ingameinfo.tag.registry.TagRegistry;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public abstract class TagNearbyPlayer extends Tag {
    public static final int MAXIMUM_INDEX = 16;

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

    @Override
    public String getName() {
        return super.getName() + this.index;
    }

    @Override
    public String[] getAliases() {
        String[] aliases = super.getAliases();
        String[] aliasesIndexed = new String[aliases.length];
        for (int i = 0; i < aliases.length; i++) {
            aliasesIndexed[i] = aliases[i] + this.index;
        }
        return aliasesIndexed;
    }

    @Override
    public boolean isIndexed() {
        return true;
    }

    @Override
    public int getMaximumIndex() {
        return MAXIMUM_INDEX - 1;
    }

    @Override
    public String getCategory() {
        return "nearbyplayer";
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
                return nearbyPlayers[this.index].getDisplayName().getFormattedText();
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
                return String.format(Locale.ENGLISH, "%.2f", nearbyPlayers[this.index].getDistanceToEntity(player));
            }
            return "-1";
        }
    }

    public static void register() {
        for (int i = 0; i < MAXIMUM_INDEX; i++) {
            TagRegistry.INSTANCE.register(new Name(i).setName("nearbyplayername"));
            TagRegistry.INSTANCE.register(new Distance(i).setName("nearbyplayerdistance"));
        }
    }

    public static void releaseResources() {
        nearbyPlayers = null;
    }
}
