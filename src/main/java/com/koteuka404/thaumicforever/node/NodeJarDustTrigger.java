package com.koteuka404.thaumicforever.node;

import com.koteuka404.thaumicforever.registry.ModBlocks;

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
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IDustTrigger;
import thaumcraft.api.items.ItemsTC;
import com.koteuka404.thaumicforever.entity.EntityAuraNode;
import com.koteuka404.thaumicforever.tile.TileEntityJarredNode;

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
    new BlockPos( 1, 1,-1), new BlockPos( 1, 1, 0), new BlockPos( 1, 1, 1))
    .toArray(BlockPos[]::new);

    @Override
    public Placement getValidFace(World world, EntityPlayer player, BlockPos pos, EnumFacing face) {
        for (BlockPos rel : CUBE_OFFSETS) {
            BlockPos nodePos = pos.add(rel);
            if (isValidStructureAt(world, nodePos)) {
                EntityAuraNode targetNode = findCaptureNode(world, nodePos);
                if (targetNode != null) {
                    return new Placement(rel.getX(), rel.getY(), rel.getZ(), face);
                }
            }
        }
        return null;
    }

    @Override
    public void execute(World world, EntityPlayer player, BlockPos pos, Placement placement, EnumFacing side) {
        if (world.isRemote) return; 
    
        BlockPos nodePos = pos.add(placement.xOffset, placement.yOffset, placement.zOffset);
        EntityAuraNode node = findCaptureNode(world, nodePos);
        if (node == null) return;
        if (!isValidStructureAt(world, nodePos)) return;
    
        // 75% to 33% reduce
        if (!node.getEntityData().getBoolean("tf_scaled75") && world.rand.nextFloat() < 0.55f) {
            scaleAspectList(node.getNodeAspects(), 2.0 / 3.0);
            scaleAspectList(node.getOriginalAspects(), 2.0 / 3.0);
            node.updateSyncAspects(); 
            node.getEntityData().setBoolean("tf_scaled75", true);
        }

        NBTTagCompound nodeNBT = new NBTTagCompound();
        node.writeToNBT(nodeNBT);
        if (!nodeNBT.hasKey("nodeAspects", 10) && nodeNBT.hasKey("aspects", 10)) {
            nodeNBT.setTag("nodeAspects", nodeNBT.getTag("aspects"));
        }

        world.setBlockState(nodePos, ModBlocks.JARRED_NODE.getDefaultState());

        TileEntity te = world.getTileEntity(nodePos);
        if (!(te instanceof TileEntityJarredNode)) {
            world.setBlockToAir(nodePos);
            return;
        }

        ((TileEntityJarredNode) te).setNodeNBT(nodeNBT);
        te.markDirty();
        world.notifyBlockUpdate(nodePos, world.getBlockState(nodePos), world.getBlockState(nodePos), 3);

        // Remove node only after jar is successfully initialized.
        if (!node.isDead) {
            node.setDead();
        }

        // Dismantle structure only after successful capture.
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

        consumeOneSalisMundus(player);
    }
private static void scaleAspectList(AspectList list, double factor) {
    for (Aspect a : list.getAspects()) {
        int oldAmt = list.getAmount(a);
        int newAmt = (int)Math.max(1, Math.floor(oldAmt * factor));
        int diff = newAmt - oldAmt;
        if (diff < 0) list.reduce(a, -diff);
        else if (diff > 0) list.add(a, diff);
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

    public static void scaleAspectCompound(NBTTagCompound parent, String key, double factor) {
        if (parent == null || !parent.hasKey(key, 10)) return; // 10 = TAG_Compound
        NBTTagCompound container = parent.getCompoundTag(key);
        NBTTagCompound aspects = container.hasKey("aspects", 10) ? container.getCompoundTag("aspects") : container;

        java.util.List<String> tags = new java.util.ArrayList<>(aspects.getKeySet());
        for (String aTag : tags) {
            if (!aspects.hasKey(aTag, 3)) continue; // 3 = TAG_Int
            int oldAmt = aspects.getInteger(aTag);
            if (oldAmt <= 0) continue;
            int newAmt = (int) Math.floor(oldAmt * (factor));
            if (newAmt <= 0) newAmt = 1;
            aspects.setInteger(aTag, newAmt);
        }
    }

    private EntityAuraNode findCaptureNode(World world, BlockPos nodePos) {
        Vec3d center = new Vec3d(nodePos).addVector(0.5, 0.5, 0.5);
        AxisAlignedBB search = new AxisAlignedBB(nodePos).grow(0.35);
        List<EntityAuraNode> nodes = world.getEntitiesWithinAABB(EntityAuraNode.class, search);

        EntityAuraNode best = null;
        double bestDist = Double.MAX_VALUE;
        for (EntityAuraNode node : nodes) {
            if (node == null || node.isDead) continue;
            double d = node.getDistanceSq(center.x, center.y, center.z);
            if (d < bestDist) {
                bestDist = d;
                best = node;
            }
        }
        return best;
    }

    private boolean hasSalisMundus(EntityPlayer player) {
        if (player == null) return false;
        ItemStack main = player.getHeldItemMainhand();
        if (!main.isEmpty() && main.getItem() == ItemsTC.salisMundus) return true;
        ItemStack off = player.getHeldItemOffhand();
        return !off.isEmpty() && off.getItem() == ItemsTC.salisMundus;
    }

    private void consumeOneSalisMundus(EntityPlayer player) {
        if (player == null) return;
        ItemStack main = player.getHeldItemMainhand();
        if (!main.isEmpty() && main.getItem() == ItemsTC.salisMundus) {
            main.shrink(1);
            return;
        }
        ItemStack off = player.getHeldItemOffhand();
        if (!off.isEmpty() && off.getItem() == ItemsTC.salisMundus) {
            off.shrink(1);
        }
    }

}
