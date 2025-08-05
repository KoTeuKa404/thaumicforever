package com.koteuka404.thaumicforever;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class ItemBlockJarredNode extends ItemBlock {
    public ItemBlockJarredNode(Block block) {
        super(block);
        // setRegistryName(block.getRegistryName());
        setMaxStackSize(1);
        // setTileEntityItemStackRenderer(new JarredNodeItemRenderer());

    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos,
                                EnumFacing side, float hitX, float hitY, float hitZ, IBlockState state) {
        boolean placed = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, state);
        if (placed) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityJarredNode && stack.hasTagCompound()) {
                NBTTagCompound nodeData = stack.getTagCompound().getCompoundTag("nodeData");
                ((TileEntityJarredNode)te).setNodeNBT(nodeData);
                te.markDirty();
                if (!world.isRemote) {
                    world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
                }
            }
        }
        return placed;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("nodeData")) {
            NBTTagCompound nodeNBT = stack.getTagCompound().getCompoundTag("nodeData");
    
            byte type = nodeNBT.getByte("type");
            String locKey = "nodetype." + type;
            String locText = net.minecraft.client.resources.I18n.format(locKey);
    
            tooltip.add("Node Type: " + locText);
            tooltip.add("Size: " + nodeNBT.getInteger("size"));
    
            Aspect mainAspect = getMainAspect(nodeNBT);
            if (mainAspect != null) {
                String hexColor = String.format("#%06X", mainAspect.getColor() & 0xFFFFFF);
                tooltip.add("Main Aspect: " + mainAspect.getName() + " " + hexColor);
            } else {
                tooltip.add("Main Aspect: ?");
            }
        }
    }
    
   public static Aspect getMainAspect(NBTTagCompound nodeNBT) {
    if (nodeNBT == null) return null;
    if (!nodeNBT.hasKey("nodeAspects", 10)) return null;

    NBTTagCompound aspectsNBT = nodeNBT.getCompoundTag("nodeAspects");
    AspectList aspectList = new AspectList();
    aspectList.readFromNBT(aspectsNBT);

    Aspect[] aspects = aspectList.getAspectsSortedByAmount();
    if (aspects.length == 0) return null;
    return aspects[0];
}
        
        
}
