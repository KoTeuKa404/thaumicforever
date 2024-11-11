// так тепер вони 3д но раніше в мене була анімація можеш у цей код свій додати анімацію як тут?
// package com.koteuka404.thaumicforever;

// import org.lwjgl.opengl.GL11;

// import baubles.api.BaubleType;
// import baubles.api.BaublesApi;
// import baubles.api.IBauble;
// import baubles.api.render.IRenderBauble;
// import net.minecraft.client.Minecraft;
// import net.minecraft.client.renderer.BufferBuilder;
// import net.minecraft.client.renderer.GlStateManager;
// import net.minecraft.client.renderer.Tessellator;
// import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
// import net.minecraft.creativetab.CreativeTabs;
// import net.minecraft.entity.EntityLivingBase;
// import net.minecraft.entity.player.EntityPlayer;
// import net.minecraft.inventory.EntityEquipmentSlot;
// import net.minecraft.item.EnumRarity;
// import net.minecraft.item.ItemArmor;
// import net.minecraft.item.ItemStack;
// import net.minecraft.util.ResourceLocation;
// import net.minecraftforge.fml.relauncher.Side;
// import net.minecraftforge.fml.relauncher.SideOnly;
// import thaumcraft.api.items.IGoggles;
// import thaumcraft.api.items.IRevealer;
// import thaumcraft.api.items.IVisDiscountGear;

// public class ItemAquareiaGoggles extends ItemArmor implements IRevealer, IGoggles, IVisDiscountGear, IBauble, IRenderBauble {

//     // Текстура для Bauble
//     public static final ResourceLocation BAUBLE_TEXTURE = new ResourceLocation(ThaumicForever.MODID, "textures/items/aquareia_goggles.png");

//     // Конструктор
//     public ItemAquareiaGoggles(String name, ArmorMaterial mat) {
//         super(mat, 3, EntityEquipmentSlot.HEAD);
//         this.setUnlocalizedName("aquareia_goggles");
//         this.setRegistryName("aquareia_goggles");
//         this.setMaxStackSize(1);
//         this.setCreativeTab(CreativeTabs.TOOLS); // або інший таб
//     }

//     // Показувати додаткові ігрові попапи
//     @Override
//     public boolean showIngamePopups(ItemStack itemstack, EntityLivingBase player) {
//         return true; // Постійно показує додаткову інформацію
//     }

//     // Знижка на Vis
//     @Override
//     public int getVisDiscount(ItemStack stack, EntityPlayer player) {
//         return 15; // Надає 15% знижки на Vis
//     }

//     // Тип Bauble
//     @Override
//     public BaubleType getBaubleType(ItemStack itemstack) {
//         return BaubleType.HEAD; // Вказує, що цей предмет розміщується на голові
//     }

//     // Рідкість предмета
//     @Override
//     public EnumRarity getRarity(ItemStack stack) {
//         return EnumRarity.RARE; // Встановлення рідкості на RARE
//     }

//     // Показувати вузли (nodes)
//     @Override
//     public boolean showNodes(ItemStack itemstack, EntityLivingBase player) {
//         return true; // Постійно показує вузли
//     }

//     @Override
//     @SideOnly(Side.CLIENT)
//     public void onPlayerBaubleRender(ItemStack stack, EntityPlayer player, RenderType type, float ticks) {
//         if (type == IRenderBauble.RenderType.HEAD) {
//             Minecraft.getMinecraft().renderEngine.bindTexture(BAUBLE_TEXTURE); // Підключаємо текстуру
//             IRenderBauble.Helper.translateToHeadLevel(player);
//             IRenderBauble.Helper.translateToFace();
//             IRenderBauble.Helper.defaultTransforms();

//             // Масштабування залишаємо таким самим
//             float scale = 1.0F; // Залишаємо масштаб 100%
//             GlStateManager.scale(scale, scale, scale);

//             // Переміщення окулярів трохи нижче і ближче до обличчя
//             GlStateManager.translate(0.0F, -0.6F, 0.0F); // Знижуємо окуляри та наближаємо до обличчя

//             // Повертаємо окуляри, щоб вони виглядали правильно
//             GlStateManager.rotate(180.0F, 0.0F, 1.0F, -0.1F);

//             // Отримуємо кількість кадрів
//             int frameCount = 8; // У вас 8 кадрів у текстурі 16x128
//             int frameHeight = 16; // Висота одного кадру (16 пікселів)
//             int textureHeight = 128; // Загальна висота текстури

//             // Рахуємо, який кадр треба показати (анімація кадрів)
//             int currentFrame = (int)(Minecraft.getMinecraft().world.getTotalWorldTime() / 5 % frameCount);

//             // Обчислюємо UV-координати для вибору кадру
//             float minV = (float)(currentFrame * frameHeight) / (float)textureHeight;
//             float maxV = (float)((currentFrame + 1) * frameHeight) / (float)textureHeight;

//             // Використовуємо ці UV-координати для рендерингу тільки одного кадру
//             Tessellator tessellator = Tessellator.getInstance();
//             BufferBuilder buffer = tessellator.getBuffer();

//             buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
//             buffer.pos(-0.5D, 0.0D, 0.0D).tex(0.0F, minV).endVertex(); // Верхній лівий кут
//             buffer.pos(0.5D, 0.0D, 0.0D).tex(1.0F, minV).endVertex(); // Верхній правий кут
//             buffer.pos(0.5D, 1.0D, 0.0D).tex(1.0F, maxV).endVertex(); // Нижній правий кут
//             buffer.pos(-0.5D, 1.0D, 0.0D).tex(0.0F, maxV).endVertex(); // Нижній лівий кут
//             tessellator.draw();
//         }
//     }

//     // Чи слід показувати HUD
//     public static boolean shouldRenderHud(EntityPlayer player) {
//         return player.inventory.armorItemInSlot(3).getItem() == ModItems.ItemAquareiaGoggles
//                 || BaublesApi.isBaubleEquipped(player, ModItems.ItemAquareiaGoggles) != -1;
//     }
// }
