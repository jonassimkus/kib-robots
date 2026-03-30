package com.joniski.kibrobots.block;

import java.util.function.Supplier;

import com.joniski.kibrobots.KibRobots;
import com.joniski.kibrobots.block.custom.AdvancedSolarPanel;
import com.joniski.kibrobots.block.custom.BatteryCharger;
import com.joniski.kibrobots.block.custom.RobotStation;
import com.joniski.kibrobots.block.custom.SolarPanel;
import com.joniski.kibrobots.item.ModItems;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(KibRobots.MODID);

    public static final DeferredBlock<Block> SOLAR_PANEL = registerBlock("solar_panel",
     () -> new SolarPanel(BlockBehaviour.Properties.of()
        .strength(0.25f)
        .sound(SoundType.STONE)
        .noOcclusion()
    ));

    public static final DeferredBlock<Block> ADVANCED_SOLAR_PANEL = registerBlock("advanced_solar_panel",
     () -> new AdvancedSolarPanel(BlockBehaviour.Properties.of()
        .strength(0.25f)
        .sound(SoundType.STONE)
        .noOcclusion()
    ));

    public static final DeferredBlock<Block> ROBOT_STATION = registerBlock("robot_station",
     () -> new RobotStation(BlockBehaviour.Properties.of()
        .strength(0.25f)
        .sound(SoundType.STONE)
    ));

    public static final DeferredBlock<Block> BATTERY_CHARGER = registerBlock("battery_charger",
     () -> new BatteryCharger(BlockBehaviour.Properties.of()
        .strength(0.25f)
        .sound(SoundType.STONE)
    ));

    public static final DeferredBlock<Block> COBALT_ORE = registerBlock("cobalt_ore",
     () -> new Block(BlockBehaviour.Properties.of()
        .strength(3f)
        .requiresCorrectToolForDrops()
        .sound(SoundType.STONE)
    ));

    public static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block){
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        ModItems.ITEMS.register(name, () -> new BlockItem(toReturn.get(), new Item.Properties()));
        return toReturn;
    }

    // TODO: make crafting recipes for all this
    public static void register(IEventBus modEventBus){
        BLOCKS.register(modEventBus);

    }
}
