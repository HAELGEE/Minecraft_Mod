package generators;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod("generators")
public class GeneratorMod {
    public static final String MODID = "generators";

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);

    public static final RegistryObject<Block> LAVA_GENERATOR_BLOCK = BLOCKS.register("lava_generator",
            () -> new LavaGeneratorBlock(Block.Properties.of().strength(3.5f).requiresCorrectToolForDrops()));

    public static final RegistryObject<Item> LAVA_GENERATOR_ITEM = ITEMS.register("lava_generator",
            () -> new BlockItem(LAVA_GENERATOR_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<BlockEntityType<LavaGeneratorBlockEntity>> LAVA_GENERATOR_BE = BLOCK_ENTITIES.register("lava_generator",
            () -> BlockEntityType.Builder.of(LavaGeneratorBlockEntity::new, LAVA_GENERATOR_BLOCK.get()).build(null));

    public static final RegistryObject<MenuType<LavaGeneratorMenu>> LAVA_GENERATOR_MENU = MENUS.register("lava_generator",
            () -> IForgeMenuType.create(LavaGeneratorMenu::new));

    public GeneratorMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        MENUS.register(modEventBus);

        // Registrera screens på klienten

    }


}