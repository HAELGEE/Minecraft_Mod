/*package Generators;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.item.context.BlockPlaceContext;

public class LavaGeneratorBlock extends BaseEntityBlock {
    public static final IntegerProperty ENERGY_LEVEL = IntegerProperty.create("energy", 0, 10);
    public static final BooleanProperty HAS_LAVA = BooleanProperty.create("has_lava");

    public LavaGeneratorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(ENERGY_LEVEL, 0)
                .setValue(HAS_LAVA, false));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LavaGeneratorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, GeneratorMod.LAVA_GENERATOR_BE.get(), LavaGeneratorBlockEntity::tick);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && player instanceof ServerPlayer) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof LavaGeneratorBlockEntity) {
                NetworkHooks.openScreen((ServerPlayer) player, (LavaGeneratorBlockEntity) blockEntity, buf -> buf.writeBlockPos(pos));
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

     STUFF FOR VISUAL ON BLOCK SIDES UNDER HERE
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ENERGY_LEVEL, HAS_LAVA);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(ENERGY_LEVEL, 0)
                .setValue(HAS_LAVA, false);
    }

    // Helper method to update block state
    public void updateBlockState(Level level, BlockPos pos, int energy, boolean hasLava) {
        if (!level.isClientSide) return;

        BlockState currentState = level.getBlockState(pos);
        if (currentState.getBlock() instanceof LavaGeneratorBlock) {
            int energyLevel = Math.min(energy / 10000, 10); // Convert 0-100000 FE to 0-10 levels
            boolean hasLavaState = hasLava;

            BlockState newState = currentState
                    .setValue(ENERGY_LEVEL, energyLevel)
                    .setValue(HAS_LAVA, hasLavaState);

            if (!currentState.equals(newState)) {
                level.setBlock(pos, newState, 3);
            }
        }
    }
}*/

package generators;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.item.context.BlockPlaceContext;

public class LavaGeneratorBlock extends BaseEntityBlock {
    public static final IntegerProperty ENERGY_LEVEL = IntegerProperty.create("energy", 0, 10);
    public static final BooleanProperty HAS_LAVA = BooleanProperty.create("has_lava");

    public LavaGeneratorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(ENERGY_LEVEL, 0)
                .setValue(HAS_LAVA, false));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LavaGeneratorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, GeneratorMod.LAVA_GENERATOR_BE.get(), LavaGeneratorBlockEntity::tick);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && player instanceof ServerPlayer) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof LavaGeneratorBlockEntity) {
                NetworkHooks.openScreen((ServerPlayer) player, (LavaGeneratorBlockEntity) blockEntity, buf -> buf.writeBlockPos(pos));
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    /* STUFF FOR VISUAL ON BLOCK SIDES UNDER HERE */
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(ENERGY_LEVEL, HAS_LAVA);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(ENERGY_LEVEL, 0)
                .setValue(HAS_LAVA, false);
    }

    // Helper method to update block state
    public void updateBlockState(Level level, BlockPos pos, int energy, boolean hasLava) {
        if (level.isClientSide) return; // Only update on server side

        BlockState currentState = level.getBlockState(pos);
        if (currentState.getBlock() instanceof LavaGeneratorBlock) {
            int energyLevel = Math.min(energy / 10000, 10); // Convert 0-100000 FE to 0-10 levels
            boolean hasLavaState = hasLava;

            BlockState newState = currentState
                    .setValue(ENERGY_LEVEL, energyLevel)
                    .setValue(HAS_LAVA, hasLavaState);

            if (!currentState.equals(newState)) {
                level.setBlock(pos, newState, 3);
            }
        }
    }

}