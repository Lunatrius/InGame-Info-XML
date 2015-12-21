package com.github.lunatrius.ingameinfo.tag;

import com.github.lunatrius.ingameinfo.tag.registry.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.GameData;

public abstract class TagMouseOver extends Tag {
    public static final FMLControlledNamespacedRegistry<Block> BLOCK_REGISTRY = GameData.getBlockRegistry();

    @Override
    public String getCategory() {
        return "mouseover";
    }

    public static class Name extends TagMouseOver {
        @Override
        public String getValue() {
            final MovingObjectPosition objectMouseOver = minecraft.objectMouseOver;
            if (objectMouseOver != null) {
                if (objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                    return objectMouseOver.entityHit.getDisplayName().getFormattedText();
                } else if (objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    final Block block = world.getBlockState(objectMouseOver.getBlockPos()).getBlock();
                    if (block != null) {
                        final ItemStack pickBlock = block.getPickBlock(objectMouseOver, world, objectMouseOver.getBlockPos(), player);
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
            final MovingObjectPosition objectMouseOver = minecraft.objectMouseOver;
            if (objectMouseOver != null) {
                if (objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                    final String name = EntityList.getEntityString(objectMouseOver.entityHit);
                    if (name != null) {
                        return name;
                    }
                } else if (objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    final Block block = world.getBlockState(objectMouseOver.getBlockPos()).getBlock();
                    if (block != null) {
                        return String.valueOf(BLOCK_REGISTRY.getNameForObject(block));
                    }
                }
            }
            return "";
        }
    }

    public static class Id extends TagMouseOver {
        @Override
        public String getValue() {
            final MovingObjectPosition objectMouseOver = minecraft.objectMouseOver;
            if (objectMouseOver != null) {
                if (objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                    return String.valueOf(objectMouseOver.entityHit.getEntityId());
                } else if (objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    final Block block = world.getBlockState(objectMouseOver.getBlockPos()).getBlock();
                    if (block != null) {
                        return String.valueOf(BLOCK_REGISTRY.getId(block));
                    }
                }
            }
            return "0";
        }
    }

    public static class Metadata extends TagMouseOver {
        @Override
        public String getValue() {
            final MovingObjectPosition objectMouseOver = minecraft.objectMouseOver;
            if (objectMouseOver != null) {
                if (objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    final IBlockState blockState = world.getBlockState(objectMouseOver.getBlockPos());
                    return String.valueOf(blockState.getBlock().getMetaFromState(blockState));
                }
            }
            return "0";
        }
    }

    public static class PowerWeak extends TagMouseOver {
        @Override
        public String getValue() {
            final MovingObjectPosition objectMouseOver = minecraft.objectMouseOver;
            if (objectMouseOver != null) {
                if (objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    int power = -1;
                    for (final EnumFacing side : EnumFacing.VALUES) {
                        final BlockPos pos = objectMouseOver.getBlockPos().offset(side);
                        final IBlockState blockState = world.getBlockState(pos);
                        power = Math.max(power, blockState.getBlock().getWeakPower(world, pos, blockState, side));

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
            final MovingObjectPosition objectMouseOver = minecraft.objectMouseOver;
            if (objectMouseOver != null) {
                if (objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    final IBlockState blockState = world.getBlockState(objectMouseOver.getBlockPos());
                    final Block block = blockState.getBlock();
                    int power = -1;
                    for (final EnumFacing side : EnumFacing.VALUES) {
                        power = Math.max(power, block.getStrongPower(world, objectMouseOver.getBlockPos(), blockState, side));

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
            final MovingObjectPosition objectMouseOver = minecraft.objectMouseOver;
            if (objectMouseOver != null) {
                if (objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    return String.valueOf(world.isBlockIndirectlyGettingPowered(objectMouseOver.getBlockPos()));
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
