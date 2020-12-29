package me.geek.tom.snowglobeadventures;

import me.geek.tom.snowglobeadventures.block.BaubleBlock;
import me.geek.tom.snowglobeadventures.block.CandycaneBlock;
import me.geek.tom.snowglobeadventures.block.SnowglobeBlock;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.geek.tom.snowglobeadventures.SnowglobeAdventures.modIdentifier;

public class Registration {

    public static final SnowglobeBlock SNOWGLOBE_BLOCK = register(Registry.BLOCK, "snowglobe", new SnowglobeBlock(
            FabricBlockSettings.of(Material.GLASS).sounds(BlockSoundGroup.GLASS).nonOpaque().breakInstantly().luminance(12)));

    public static final CandycaneBlock CANDYCANE_BLOCK = register(Registry.BLOCK, "candycane", new CandycaneBlock(
            FabricBlockSettings.of(Material.BAMBOO, MaterialColor.FOLIAGE).sounds(BlockSoundGroup.BAMBOO)
                    .nonOpaque().breakInstantly().strength(1.0F)));

    public static final BlockItem SNOWGLOBE_BLOCK_ITEM = register(Registry.ITEM, "snowglobe", new BlockItem(SNOWGLOBE_BLOCK,
            new FabricItemSettings().group(ItemGroup.DECORATIONS)));

    public static final BlockItem CANDYCANE_BLOCK_ITEM = register(Registry.ITEM, "candycane", new BlockItem(CANDYCANE_BLOCK,
            new FabricItemSettings().group(ItemGroup.DECORATIONS)));

    public static final List<BaubleBlock> BAUBLE_BLOCKS = new ArrayList<>();

    public static final List<BlockItem> BAUBLE_ITEMS = new ArrayList<>();

    private static void registerBaubles() {
        Arrays.stream(BaubleBlock.Colour.values()).forEach(
                c -> {
                    FabricBlockSettings bSettings = FabricBlockSettings.of(Material.GLASS).sounds(BlockSoundGroup.GLASS).nonOpaque().breakInstantly().luminance(12);
                    FabricItemSettings settings = new FabricItemSettings().group(ItemGroup.DECORATIONS);

                    String name = "bauble_" + c.asString();
                    BaubleBlock block = register(Registry.BLOCK, name, new BaubleBlock(bSettings));
                    BlockItem item = register(Registry.ITEM, name, new BlockItem(block, settings));

                    BAUBLE_BLOCKS.add(block);
                    BAUBLE_ITEMS.add(item);
                });
    }

    public static <T> T register(Registry<? super T> reg, String name, T t) {
        return Registry.register(reg, modIdentifier(name), t);
    }

    // Used to trigger classload and populate static fields
    public static void init() {
        registerBaubles();
    }
}
