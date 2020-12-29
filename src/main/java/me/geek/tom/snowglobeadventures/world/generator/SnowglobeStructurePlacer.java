package me.geek.tom.snowglobeadventures.world.generator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkRegion;

import java.util.Locale;
import java.util.Random;

import static me.geek.tom.snowglobeadventures.SnowglobeAdventures.modIdentifier;
import static net.minecraft.block.LecternBlock.HAS_BOOK;

public class SnowglobeStructurePlacer {

    public static final Codec<SnowglobeStructurePlacer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("house").xmap(HouseType::valueOf, HouseType::name).forGetter(o -> o.house),
            Codec.STRING.fieldOf("decorations").xmap(DecorationType::valueOf, DecorationType::name).forGetter(o -> o.decoration),
            Codec.STRING.fieldOf("tree").xmap(TreeType::valueOf, TreeType::name).forGetter(o -> o.tree)
    ).apply(instance, instance.stable(SnowglobeStructurePlacer::new)));

    private final HouseType house;
    private final DecorationType decoration;
    private final TreeType tree;

    public SnowglobeStructurePlacer(HouseType house, DecorationType decoration, TreeType tree) {
        this.house = house;
        this.decoration = decoration;
        this.tree = tree;
    }

    public void placeStructure(ChunkPos chunk, ChunkRegion region) {
        if (house.shouldPlaceIn(chunk)) {
            generateStructure(region, chunk.getStartPos().add(1, 69, 1), house.structureId);
        }

        if (decoration.shouldPlaceIn(chunk)) {
            generateStructure(region, chunk.getStartPos().add(1, 69, 1), decoration.structureId);
        }

        if (tree.shouldPlaceIn(chunk)) {
            generateStructure(region, chunk.getStartPos().add(1, 69, 1), tree.structureId);
        }
    }

    private void generateStructure(ChunkRegion region, BlockPos start, Identifier structureId) {
        region.toServerWorld().getStructureManager().getStructure(structureId).place(
                region, start, new StructurePlacementData(), new Random()
        );
    }

    private void placeLectern(String message, BlockPos pos, ChunkRegion region) {
        ItemStack book = new ItemStack(Items.WRITABLE_BOOK);
        ListTag pages = new ListTag();
        pages.add(StringTag.of(message));
        book.putSubTag("pages", pages);
        region.setBlockState(pos, Blocks.LECTERN.getDefaultState().with(HAS_BOOK, true), 2);
        BlockEntity be = region.getBlockEntity(pos);
        if (be instanceof LecternBlockEntity) {
            ((LecternBlockEntity) be).setBook(book);
        }
    }

    public enum HouseType implements StringIdentifiable {
        COSY_COTTAGE(1, -1, modIdentifier("house/cottage")),
        SANTAS_GROTTO(-1, 1, modIdentifier("house/santa")),
        ;

        private final int chunkPosX;
        private final int chunkPosZ;

        private final Identifier structureId;

        HouseType(int cX, int cZ, Identifier structureId) {
            this.chunkPosX = cX;
            this.chunkPosZ = cZ;
            this.structureId = structureId;
        }

        public boolean shouldPlaceIn(ChunkPos chunk) {
            return chunk.x == this.chunkPosX && chunk.z == this.chunkPosZ;
        }

        @Override
        public String asString() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    public enum DecorationType implements StringIdentifiable {
        CANDYCANES(-1, 0, modIdentifier("decoration/candycanes")),
        SNOWMAN(0, -1, modIdentifier("decoration/snowmen")),
        LITTLE_GROWING_TREES(-1, -1, modIdentifier("decoration/lil_trees")),
        ;

        private final int chunkPosX;
        private final int chunkPosZ;

        private final Identifier structureId;

        DecorationType(int cX, int cZ, Identifier structureId) {
            this.chunkPosX = cX;
            this.chunkPosZ = cZ;
            this.structureId = structureId;
        }

        public boolean shouldPlaceIn(ChunkPos chunk) {
            return chunk.x == this.chunkPosX && chunk.z == this.chunkPosZ;
        }

        @Override
        public String asString() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    public enum TreeType implements StringIdentifiable {
        HUGE_TREE(1, 0, modIdentifier("tree/decorated_tree")),
        LIT_TREE(0, 1, modIdentifier("tree/lit_tree")),
        ;

        private final int chunkPosX;
        private final int chunkPosZ;

        private final Identifier structureId;

        TreeType(int cX, int cZ, Identifier structureId) {
            this.chunkPosX = cX;
            this.chunkPosZ = cZ;
            this.structureId = structureId;
        }

        public boolean shouldPlaceIn(ChunkPos chunk) {
            return chunk.x == this.chunkPosX && chunk.z == this.chunkPosZ;
        }

        @Override
        public String asString() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
