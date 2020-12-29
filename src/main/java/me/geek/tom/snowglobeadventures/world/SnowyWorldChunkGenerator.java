package me.geek.tom.snowglobeadventures.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.geek.tom.snowglobeadventures.world.generator.SnowglobeStructurePlacer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.feature.StructureFeature;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;

public class SnowyWorldChunkGenerator extends ChunkGenerator {

    public static final Codec<SnowyWorldChunkGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Biome.REGISTRY_CODEC.stable().fieldOf("biome").forGetter(g -> g.biome),
            SnowglobeStructurePlacer.CODEC.stable().fieldOf("structures").forGetter(g -> g.structurePlacer)
    ).apply(instance, instance.stable(SnowyWorldChunkGenerator::new)));

    private final Supplier<Biome> biome;
    private final SnowglobeStructurePlacer structurePlacer;

    public SnowyWorldChunkGenerator(Supplier<Biome> biome, SnowglobeStructurePlacer placer) {
        super(new FixedBiomeSource(biome), new StructuresConfig(Optional.empty(), Collections.emptyMap()));
        this.biome = biome;
        this.structurePlacer = placer;
    }

    public SnowyWorldChunkGenerator(Registry<Biome> biomeRegistry, RegistryKey<Biome> biome, SnowglobeStructurePlacer placer) {
        this(() -> biomeRegistry.get(biome), placer);
    }

    public SnowyWorldChunkGenerator(Registry<Biome> biomeRegistry, SnowglobeStructurePlacer placer) {
        this(biomeRegistry, BiomeKeys.SNOWY_TUNDRA, placer);
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public ChunkGenerator withSeed(long seed) {
        return this;
    }

    @Override
    public void setStructureStarts(DynamicRegistryManager registryManager, StructureAccessor accessor, Chunk chunk, StructureManager manager, long seed) {
    }

    @Override
    public void addStructureReferences(StructureWorldAccess world, StructureAccessor accessor, Chunk chunk) {
    }

    @Override
    public void populateNoise(WorldAccess world, StructureAccessor accessor, Chunk chunk) {
        BlockPos.Mutable cursor = new BlockPos.Mutable();
        for (int x = 0; x < 16; x++) {
            cursor.setX(x);
            for (int z = 0; z < 16; z++) {
                cursor.setZ(z);

                cursor.setY(0);
                chunk.setBlockState(cursor, Blocks.BEDROCK.getDefaultState(), false);

                for (int y = 1; y < 60; y++) {
                    cursor.setY(y);
                    chunk.setBlockState(cursor, Blocks.STONE.getDefaultState(), false);
                }
                for (int y = 60; y < 69; y++) {
                    cursor.setY(y);
                    chunk.setBlockState(cursor, Blocks.DIRT.getDefaultState(), false);
                }

                cursor.setY(69);
                chunk.setBlockState(cursor, Blocks.GRASS_BLOCK.getDefaultState(), false);
                cursor.setY(70);
                chunk.setBlockState(cursor, Blocks.SNOW.getDefaultState(), false);
            }
        }
    }

    @Override
    public void buildSurface(ChunkRegion region, Chunk chunk) {
    }

    @Override
    public void carve(long seed, BiomeAccess access, Chunk chunk, GenerationStep.Carver carver) {
    }

    @Override
    public void generateFeatures(ChunkRegion region, StructureAccessor accessor) {
        int centerChunkX = region.getCenterChunkX();
        int centerChunkZ = region.getCenterChunkZ();
        int centerX = centerChunkX * 16;
        int centerZ = centerChunkZ * 16;
        BlockPos center = new BlockPos(centerX, 0, centerZ);

        this.structurePlacer.placeStructure(new ChunkPos(center), region);
    }

    @Override
    public void populateEntities(ChunkRegion region) {
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmapType) {
        return 71;
    }

    @Nullable
    @Override
    public BlockPos locateStructure(ServerWorld world, StructureFeature<?> feature, BlockPos center, int radius, boolean skipExistingChunks) {
        return null;
    }

    @Override
    public boolean isStrongholdStartingChunk(ChunkPos chunkPos) {
        return false;
    }

    @Override
    public BlockView getColumnSample(int x, int z) {
        BlockState[] states = new BlockState[256];
        Arrays.fill(states, Blocks.AIR.getDefaultState());

        states[0] = Blocks.BEDROCK.getDefaultState();

        for (int y = 1; y < 60; y++) {
            states[y] = Blocks.STONE.getDefaultState();
        }
        for (int y = 60; y < 69; y++) {
            states[y] = Blocks.DIRT.getDefaultState();
        }
        states[69] = Blocks.GRASS_BLOCK.getDefaultState();
        states[70] = Blocks.SNOW.getDefaultState();
        return new VerticalBlockSample(states);
    }
}
