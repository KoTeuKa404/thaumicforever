package com.koteuka404.thaumicforever;

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.crafting.IDustTrigger;
import thaumcraft.api.items.ItemsTC;

public class NodeJarDustTrigger implements IDustTrigger {

    private static final BlockPos[] CUBE_OFFSETS = java.util.stream.Stream.of(
    new BlockPos(-1,-1,-1), new BlockPos(-1,-1, 0), new BlockPos(-1,-1, 1),
    new BlockPos(-1, 0,-1), new BlockPos(-1, 0, 0), new BlockPos(-1, 0, 1),
    new BlockPos(-1, 1,-1), new BlockPos(-1, 1, 0), new BlockPos(-1, 1, 1),
    new BlockPos( 0,-1,-1), new BlockPos( 0,-1, 0), new BlockPos( 0,-1, 1),
    new BlockPos( 0, 0,-1), new BlockPos( 0, 0, 0), new BlockPos( 0, 0, 1),
    new BlockPos( 0, 1,-1), new BlockPos( 0, 1, 0), new BlockPos( 0, 1, 1),
    new BlockPos( 1,-1,-1), new BlockPos( 1,-1, 0), new BlockPos( 1,-1, 1),
    new BlockPos( 1, 0,-1), new BlockPos( 1, 0, 0), new BlockPos( 1, 0, 1),
    new BlockPos( 1, 1,-1), new BlockPos( 1, 1, 0), new BlockPos( 1, 1, 1)
).toArray(BlockPos[]::new);

    @Override
    public Placement getValidFace(World world, EntityPlayer player, BlockPos pos, EnumFacing face) {
        for (BlockPos rel : CUBE_OFFSETS) {
            BlockPos nodePos = pos.add(rel);
            if (isValidStructureAt(world, nodePos)) {
                List<EntityAuraNode> nodes = world.getEntitiesWithinAABB(EntityAuraNode.class, new AxisAlignedBB(nodePos));
                if (!nodes.isEmpty()) {
                    ItemStack held = player.getHeldItemMainhand();
                    if (!held.isEmpty() && held.getItem() == ItemsTC.salisMundus) {
                        return new Placement(rel.getX(), rel.getY(), rel.getZ(), face);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void execute(World world, EntityPlayer player, BlockPos pos, Placement placement, EnumFacing side) {
        BlockPos nodePos = pos.add(placement.xOffset, placement.yOffset, placement.zOffset);

        List<EntityAuraNode> nodes = world.getEntitiesWithinAABB(EntityAuraNode.class, new AxisAlignedBB(nodePos));
        if (nodes.isEmpty()) return;

        for (EntityAuraNode node : nodes) {
            if (!isValidStructureAt(world, nodePos)) continue;

            NBTTagCompound nodeNBT = new NBTTagCompound();
            node.writeToNBT(nodeNBT);

            if (!nodeNBT.hasKey("nodeAspects", 10) && nodeNBT.hasKey("aspects", 10)) {
                nodeNBT.setTag("nodeAspects", nodeNBT.getTag("aspects"));
            }

            // if (nodeNBT.hasKey("size", 3)) { // 3 = integer
            //     int oldSize = nodeNBT.getInteger("size");
            //     int newSize = Math.max(1, (int)Math.floor(oldSize * 2.0 / 3.0));
            //     nodeNBT.setInteger("size", newSize);
            // }

            node.setDead();
            world.setBlockState(nodePos, ModBlocks.JARRED_NODE.getDefaultState());
            TileEntity te = world.getTileEntity(nodePos);
            if (te instanceof TileEntityJarredNode) {
                ((TileEntityJarredNode)te).setNodeNBT(nodeNBT);
                te.markDirty();
                if (!world.isRemote) {
                    world.notifyBlockUpdate(nodePos, world.getBlockState(nodePos), world.getBlockState(nodePos), 3);
                }
            }

            for (int x = -1; x <= 1; x++)
            for (int y = -1; y <= 1; y++)
            for (int z = -1; z <= 1; z++) {
                if (x == 0 && y == 0 && z == 0) continue;
                BlockPos check = nodePos.add(x, y, z);
                IBlockState state = world.getBlockState(check);
                if (state.getBlock() instanceof BlockGlass) {
                    world.setBlockToAir(check);
                }
            }
            for (int x = -1; x <= 1; x++)
            for (int z = -1; z <= 1; z++) {
                BlockPos check = nodePos.add(x, 2, z);
                IBlockState state = world.getBlockState(check);
                if (state.getBlock() instanceof BlockSlab) {
                    world.setBlockToAir(check);
                }
            }

            ItemStack held = player.getHeldItemMainhand();
            held.shrink(1);
            break; 
        }
    }

    @Override
    public List<BlockPos> sparkle(World world, EntityPlayer player, BlockPos pos, Placement placement) {
        BlockPos nodePos = pos.add(placement.xOffset, placement.yOffset, placement.zOffset);
        return Arrays.asList(nodePos);
    }

    private boolean isValidStructureAt(World world, BlockPos nodePos) {
        for (int x = -1; x <= 1; x++)
        for (int y = 0; y <= 1; y++) 
        for (int z = -1; z <= 1; z++) {
            if (x == 0 && y == 0 && z == 0) continue;
            BlockPos check = nodePos.add(x, y, z);
            IBlockState state = world.getBlockState(check);
            if (!(state.getBlock() instanceof BlockGlass)) {
                return false;
            }
        }
        for (int x = -1; x <= 1; x++)
        for (int z = -1; z <= 1; z++) {
            BlockPos check = nodePos.add(x, 2, z);
            IBlockState state = world.getBlockState(check);
            if (!(state.getBlock() instanceof BlockSlab)) {
                return false;
            }
        }
        return true;
    }
}
