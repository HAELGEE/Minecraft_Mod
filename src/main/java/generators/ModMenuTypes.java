package generators;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    // Registreringscontainer för alla menyer i din mod
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, GeneratorMod.MODID);

    // Själva MenuType för din LavaGenerator
    public static final RegistryObject<MenuType<LavaGeneratorMenu>> LAVA_GENERATOR_MENU =
            MENUS.register("lava_generator_menu",
                    () -> IForgeMenuType.create(LavaGeneratorMenu::new));
}