package com.github.lunatrius.ingameinfo.tag;

import com.github.lunatrius.ingameinfo.tag.registry.TagRegistry;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TagMouseOver extends Tag {
    @Override
    public String getCategory() {
        return "mouseover";
    }

    public static class Name extends TagMouseOver {
        @Override
        public String getValue() {
            MovingObjectPosition objectMouseOver = minecraft.objectMouseOver;
            if (objectMouseOver != null) {
                if (objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                    return objectMouseOver.entityHit.func_145748_c_().getFormattedText();
                } else if (objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    Block block = world.getBlock(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
                    if (block != null) {
                        ItemStack pickBlock = block.getPickBlock(objectMouseOver, world, objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
                        if (pickBlock != null) {
                            return pickBlock.getDisplayName();
                        }
                        return block.getLocalizedName();
                    }
                }
            }
            return "";
        }
    }

    public static class UniqueName extends TagMouseOver {
        @Override
        public String getValue() {
            MovingObjectPosition objectMouseOver = minecraft.objectMouseOver;
            if (objectMouseOver != null) {
                if (objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                    String name = EntityList.getEntityString(objectMouseOver.entityHit);
                    if (name != null) {
                        return name;
                    }
                } else if (objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    Block block = world.getBlock(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
                    if (block != null) {
                        return GameData.getBlockRegistry().getNameForObject(block);
                    }
                }
            }
            return "";
        }
    }

    public static class Id extends TagMouseOver {
        @Override
        public String getValue() {
            MovingObjectPosition objectMouseOver = minecraft.objectMouseOver;
            if (objectMouseOver != null) {
                if (objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                    return String.valueOf(objectMouseOver.entityHit.getEntityId());
                } else if (objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    Block block = world.getBlock(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
                    if (block != null) {
                        return String.valueOf(GameData.getBlockRegistry().getId(block));
                    }
                }
            }
            return "0";
        }
    }

    public static class Metadata extends TagMouseOver {
        @Override
        public String getValue() {
            MovingObjectPosition objectMouseOver = minecraft.objectMouseOver;
            if (objectMouseOver != null) {
                if (objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    return String.valueOf(world.getBlockMetadata(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ));
                }
            }
            return "0";
        }
    }

    public static class PowerWeak extends TagMouseOver {
        @Override
        public String getValue() {
            MovingObjectPosition objectMouseOver = minecraft.objectMouseOver;
            if (objectMouseOver != null) {
                if (objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    int power = -1;
                    for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
                        final int x = objectMouseOver.blockX + side.offsetX;
                        final int y = objectMouseOver.blockY + side.offsetY;
                        final int z = objectMouseOver.blockZ + side.offsetZ;
                        power = Math.max(power, world.getBlock(x, y, z).isProvidingWeakPower(world, x, y, z, side.ordinal()));

                        if (power >= 15) {
                            break;
                        }
                    }
                    return String.valueOf(power);
                }
            }
            return "-1";
        }
    }

    public static class PowerStrong extends TagMouseOver {
        @Override
        public String getValue() {
            MovingObjectPosition objectMouseOver = minecraft.objectMouseOver;
            if (objectMouseOver != null) {
                if (objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    final Block block = world.getBlock(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
                    int power = -1;
                    for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
                        power = Math.max(power, block.isProvidingStrongPower(world, objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ, side.ordinal()));

                        if (power >= 15) {
                            break;
                        }
                    }
                    return String.valueOf(power);
                }
            }
            return "-1";
        }
    }

    public static class PowerInput extends TagMouseOver {
        @Override
        public String getValue() {
            MovingObjectPosition objectMouseOver = minecraft.objectMouseOver;
            if (objectMouseOver != null) {
                if (objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    return String.valueOf(world.getBlockPowerInput(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ));
                }
            }
            return "-1";
        }
    }

    public static void register() {
        TagRegistry.INSTANCE.register(new Name().setName("mouseovername"));
        TagRegistry.INSTANCE.register(new UniqueName().setName("mouseoveruniquename"));
        TagRegistry.INSTANCE.register(new Id().setName("mouseoverid"));
        TagRegistry.INSTANCE.register(new Metadata().setName("mouseovermetadata"));
        TagRegistry.INSTANCE.register(new PowerWeak().setName("mouseoverpowerweak"));
        TagRegistry.INSTANCE.register(new PowerStrong().setName("mouseoverpowerstrong"));
        TagRegistry.INSTANCE.register(new PowerInput().setName("mouseoverpowerinput"));
    }
}
