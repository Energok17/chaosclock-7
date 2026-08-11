package com.chaosclock.event;

import com.chaosclock.ChaosClockMod;
import com.chaosclock.registry.ModEffects;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ChaosEvents {
    private static final Random RANDOM = new Random();

    // "Earthy" ground blocks, swapped among themselves
    private static final Block[] EARTH_SOURCE = new Block[]{
            Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.PODZOL, Blocks.MYCELIUM,
            Blocks.COARSE_DIRT, Blocks.ROOTED_DIRT, Blocks.MUD, Blocks.SAND,
            Blocks.RED_SAND, Blocks.GRAVEL, Blocks.CLAY, Blocks.SOUL_SAND
    };
    // Stone-family blocks, swapped among themselves
    private static final Block[] STONE_SOURCE = new Block[]{
            Blocks.STONE, Blocks.COBBLESTONE, Blocks.MOSSY_COBBLESTONE, Blocks.ANDESITE,
            Blocks.DIORITE, Blocks.GRANITE, Blocks.CALCITE, Blocks.TUFF,
            Blocks.DEEPSLATE, Blocks.BLACKSTONE, Blocks.BASALT
    };
    // Stone can also turn into something a bit more exciting
    private static final Block[] STONE_TARGET = new Block[]{
            Blocks.STONE, Blocks.COBBLESTONE, Blocks.MOSSY_COBBLESTONE, Blocks.ANDESITE,
            Blocks.DIORITE, Blocks.GRANITE, Blocks.TUFF, Blocks.DEEPSLATE,
            Blocks.BLACKSTONE, Blocks.BASALT,
            Blocks.COAL_ORE, Blocks.GOLD_ORE, Blocks.IRON_ORE, Blocks.DIAMOND_ORE,
            Blocks.EMERALD_ORE, Blocks.LAPIS_ORE, Blocks.REDSTONE_ORE, Blocks.OBSIDIAN
    };
    private static final Block[] WOOD_PLANKS = new Block[]{
            Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS,
            Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS, Blocks.MANGROVE_PLANKS, Blocks.CHERRY_PLANKS,
            Blocks.CRIMSON_PLANKS, Blocks.WARPED_PLANKS
    };
    private static final Block[] WOOD_LOGS = new Block[]{
            Blocks.OAK_LOG, Blocks.SPRUCE_LOG, Blocks.BIRCH_LOG, Blocks.JUNGLE_LOG,
            Blocks.ACACIA_LOG, Blocks.DARK_OAK_LOG, Blocks.MANGROVE_LOG, Blocks.CHERRY_LOG,
            Blocks.CRIMSON_STEM, Blocks.WARPED_STEM
    };
    private static final Block[] LEAVES = new Block[]{
            Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.BIRCH_LEAVES, Blocks.JUNGLE_LEAVES,
            Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.MANGROVE_LEAVES, Blocks.CHERRY_LEAVES
    };
    private static final Block[] GENERAL_CHAOS_TARGET = new Block[]{
            Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.MUD, Blocks.SAND,
            Blocks.STONE, Blocks.COBBLESTONE, Blocks.GRANITE, Blocks.TUFF,
            Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_LOG, Blocks.CHERRY_PLANKS,
            Blocks.COAL_ORE, Blocks.GOLD_ORE, Blocks.IRON_ORE, Blocks.EMERALD_ORE,
            Blocks.MELON, Blocks.PUMPKIN, Blocks.BOOKSHELF, Blocks.HAY_BLOCK,
            Blocks.LAPIS_ORE, Blocks.REDSTONE_ORE, Blocks.GRAVEL, Blocks.GLASS
    };
    private static final EntityType<?>[] PASSIVE_POOL = new EntityType[]{
            EntityType.COW, EntityType.PIG, EntityType.SHEEP, EntityType.CHICKEN,
            EntityType.RABBIT, EntityType.MOOSHROOM, EntityType.FOX, EntityType.TURTLE
    };
    private static final EntityType<?>[] HOSTILE_SOURCE = new EntityType[]{
            EntityType.ZOMBIE, EntityType.SKELETON, EntityType.SPIDER, EntityType.CREEPER
    };
    private static final EntityType<?>[] CALM_POOL = new EntityType[]{
            EntityType.FOX, EntityType.MOOSHROOM, EntityType.RABBIT
    };
    private static final EntityType<?>[] HOOFED_POOL = new EntityType[]{
            EntityType.CAMEL, EntityType.HORSE, EntityType.DONKEY, EntityType.MULE, EntityType.LLAMA
    };
    private static final Block[] GIFT_BLOCK_POOL = new Block[]{
            Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.MUD, Blocks.OAK_PLANKS,
            Blocks.SPRUCE_PLANKS, Blocks.BIRCH_LOG, Blocks.STONE, Blocks.COBBLESTONE,
            Blocks.MELON, Blocks.COAL_ORE, Blocks.GOLD_ORE, Blocks.IRON_ORE
    };
    private static final Item[] GIFT_WEAPON_POOL = new Item[]{
            Items.IRON_SWORD, Items.IRON_AXE, Items.BOW, Items.SHIELD
    };
    private static final MobEffect[] POTION_POOL = new MobEffect[]{
            MobEffects.MOVEMENT_SPEED, MobEffects.JUMP, MobEffects.REGENERATION,
            MobEffects.WATER_BREATHING, MobEffects.FIRE_RESISTANCE, MobEffects.NIGHT_VISION
    };
    private static final Item[] TOOL_POOL = new Item[]{
            Items.IRON_AXE, Items.IRON_PICKAXE, Items.IRON_SHOVEL, Items.IRON_HOE
    };
    private static final List<WeightedEvent> EVENTS = new ArrayList<WeightedEvent>();

    private static Component rainbow(String text) {
        MutableComponent result = Component.literal("");
        int len = text.length();
        for (int i = 0; i < len; ++i) {
            float hue = (float) i / (float) Math.max(1, len);
            int rgb = Color.HSBtoRGB(hue, 1.0f, 1.0f) & 0xFFFFFF;
            result.append(Component.literal(String.valueOf(text.charAt(i)))
                    .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(rgb))));
        }
        return result;
    }

    private static void broadcast(ServerLevel level, Rarity rarity, String message) {
        String full = "[ChaosClock] " + message;
        Component component = rarity == Rarity.DIVINE
                ? ChaosEvents.rainbow(full)
                : Component.literal(full).withStyle(rarity.color);
        level.getServer().getPlayerList().broadcastSystemMessage(component, false);
    }

    private static <T> T randomOf(T[] pool) {
        return pool[RANDOM.nextInt(pool.length)];
    }

    private static boolean chance(int percent) {
        return RANDOM.nextInt(100) < percent;
    }

    private static void swapBlocks(ServerLevel level, ServerPlayer player, Block[] source, Block[] target, int radius, int yMin, int yMax) {
        BlockPos center = player.blockPosition();
        for (int x = -radius; x <= radius; ++x) {
            for (int z = -radius; z <= radius; ++z) {
                block2: for (int y = yMin; y <= yMax; ++y) {
                    BlockPos pos = center.offset(x, y, z);
                    Block currentBlock = level.getBlockState(pos).getBlock();
                    for (Block src : source) {
                        if (currentBlock != src) continue;
                        level.setBlockAndUpdate(pos, ChaosEvents.randomOf(target).defaultBlockState());
                        continue block2;
                    }
                }
            }
        }
    }

    private static void transformMobs(ServerLevel level, ServerPlayer player, EntityType<?>[] source, EntityType<?>[] target) {
        AABB box = player.getBoundingBox().inflate(40.0);
        List<Mob> mobs = level.getEntitiesOfClass(Mob.class, box);
        for (Mob mob : mobs) {
            Entity newEntity;
            boolean matches = false;
            for (EntityType<?> t : source) {
                if (mob.getType() != t) continue;
                matches = true;
                break;
            }
            if (!matches || (newEntity = ChaosEvents.randomOf(target).create(level)) == null) continue;
            newEntity.moveTo(mob.getX(), mob.getY(), mob.getZ(), mob.getYRot(), mob.getXRot());
            level.addFreshEntity(newEntity);
            mob.discard();
        }
    }

    private static void spawnNear(ServerLevel level, ServerPlayer player, EntityType<?> type, int count, int radius) {
        BlockPos center = player.blockPosition();
        for (int i = 0; i < count; ++i) {
            Entity entity = type.create(level);
            if (entity == null) continue;
            double x = center.getX() + RANDOM.nextInt(radius * 2 + 1) - radius;
            double z = center.getZ() + RANDOM.nextInt(radius * 2 + 1) - radius;
            entity.moveTo(x, (double) center.getY(), z, 0.0f, 0.0f);
            level.addFreshEntity(entity);
        }
    }

    private static ItemStack randomFirework() {
        ItemStack rocket = new ItemStack((ItemLike) Items.FIREWORK_ROCKET);
        CompoundTag fireworks = new CompoundTag();
        ListTag explosions = new ListTag();
        CompoundTag explosion = new CompoundTag();
        explosion.putByte("Type", (byte) RANDOM.nextInt(5));
        int[] palette = new int[]{0xFF0000, 65280, 255, 0xFFFF00, 0xFF00FF, 65535, 16753920, 0xFFFFFF};
        explosion.putIntArray("Colors", new int[]{palette[RANDOM.nextInt(palette.length)], palette[RANDOM.nextInt(palette.length)]});
        explosions.add((Tag) explosion);
        fireworks.put("Explosions", (Tag) explosions);
        fireworks.putByte("Flight", (byte) 1);
        rocket.getOrCreateTag().put("Fireworks", (Tag) fireworks);
        return rocket;
    }

    private static void chickensToCows(ServerLevel level, ServerPlayer player) {
        ChaosEvents.transformMobs(level, player, new EntityType[]{EntityType.CHICKEN}, new EntityType[]{EntityType.COW});
        ChaosEvents.broadcast(level, Rarity.COMMON, "\ud83d\udc14\u27a1\ud83d\udc04 \u041a\u0443\u0440\u0438 \u0441\u0442\u0430\u043b\u0438 \u043a\u043e\u0440\u043e\u0432\u0430\u043c\u0438!");
    }

    private static void cowsToChickens(ServerLevel level, ServerPlayer player) {
        ChaosEvents.transformMobs(level, player, new EntityType[]{EntityType.COW}, new EntityType[]{EntityType.CHICKEN});
        ChaosEvents.broadcast(level, Rarity.COMMON, "\ud83d\udc04\u27a1\ud83d\udc14 \u041a\u043e\u0440\u043e\u0432\u0438 \u0441\u0442\u0430\u043b\u0438 \u043a\u0443\u0440\u043c\u0438!");
    }

    private static void cowsToPigs(ServerLevel level, ServerPlayer player) {
        ChaosEvents.transformMobs(level, player, new EntityType[]{EntityType.COW}, new EntityType[]{EntityType.PIG});
        ChaosEvents.broadcast(level, Rarity.UNCOMMON, "\ud83d\udc04\u27a1\ud83d\udc16 \u041a\u043e\u0440\u043e\u0432\u0438 \u0441\u0442\u0430\u043b\u0438 \u0441\u0432\u0438\u043d\u044f\u043c\u0438!");
    }

    private static void pigsToSheep(ServerLevel level, ServerPlayer player) {
        ChaosEvents.transformMobs(level, player, new EntityType[]{EntityType.PIG}, new EntityType[]{EntityType.SHEEP});
        ChaosEvents.broadcast(level, Rarity.UNCOMMON, "\ud83d\udc16\u27a1\ud83d\udc11 \u0421\u0432\u0438\u043d\u0456 \u0441\u0442\u0430\u043b\u0438 \u0432\u0456\u0432\u0446\u044f\u043c\u0438!");
    }

    private static void animalShuffle(ServerLevel level, ServerPlayer player) {
        ChaosEvents.transformMobs(level, player, PASSIVE_POOL, PASSIVE_POOL);
        ChaosEvents.broadcast(level, Rarity.UNCOMMON, "\ud83c\udfb2 \u041c\u0438\u0440\u043d\u0456 \u043c\u043e\u0431\u0438 \u043f\u0435\u0440\u0435\u043c\u0456\u0448\u0430\u043b\u0438\u0441\u044c!");
    }

    private static void hostileToCalm(ServerLevel level, ServerPlayer player) {
        ChaosEvents.transformMobs(level, player, HOSTILE_SOURCE, CALM_POOL);
        ChaosEvents.broadcast(level, Rarity.RARE, "\ud83d\udc1d \u0410\u0433\u0440\u0435\u0441\u0438\u0432\u043d\u0438\u0445 \u043c\u043e\u0431\u0456\u0432 \u0437\u0430\u0441\u043f\u043e\u043a\u043e\u0457\u043b\u043e!");
    }

    private static void earthBlockSwap(ServerLevel level, ServerPlayer player) {
        ChaosEvents.swapBlocks(level, player, EARTH_SOURCE, EARTH_SOURCE, 5, -4, -1);
        ChaosEvents.broadcast(level, Rarity.UNCOMMON, "\ud83c\udfb2 \u0417\u0435\u043c\u043b\u044f \u043d\u0430\u0432\u043a\u043e\u043b\u043e \u0437\u043c\u0456\u043d\u0438\u043b\u0430\u0441\u044c!");
    }

    private static void stoneBlockSwap(ServerLevel level, ServerPlayer player) {
        ChaosEvents.swapBlocks(level, player, STONE_SOURCE, STONE_TARGET, 5, -4, -1);
        ChaosEvents.broadcast(level, Rarity.UNCOMMON, "\ud83e\udea8 \u041a\u0430\u043c\u0456\u043d\u044c \u043d\u0430\u0432\u043a\u043e\u043b\u043e \u0437\u043c\u0456\u043d\u0438\u0432\u0441\u044f!");
    }

    private static void treeSpeciesSwap(ServerLevel level, ServerPlayer player) {
        ChaosEvents.swapBlocks(level, player, WOOD_PLANKS, WOOD_PLANKS, 6, -2, 6);
        ChaosEvents.swapBlocks(level, player, WOOD_LOGS, WOOD_LOGS, 6, -2, 10);
        ChaosEvents.swapBlocks(level, player, LEAVES, LEAVES, 6, -2, 10);
        ChaosEvents.broadcast(level, Rarity.UNCOMMON, "\ud83c\udf33 \u0414\u0435\u0440\u0435\u0432\u0430 \u043f\u043e\u043c\u0456\u043d\u044f\u043b\u0438 \u043f\u043e\u0440\u043e\u0434\u0443!");
    }

    private static void generalBlockChaos(ServerLevel level, ServerPlayer player) {
        BlockPos center = player.blockPosition();
        int radius = 5;
        for (int x = -radius; x <= radius; ++x) {
            for (int z = -radius; z <= radius; ++z) {
                for (int y = -4; y <= -1; ++y) {
                    BlockPos pos = center.offset(x, y, z);
                    BlockState current = level.getBlockState(pos);
                    if (current.isAir() || !current.getFluidState().isEmpty() || current.getDestroySpeed((BlockGetter) level, pos) < 0.0f) continue;
                    level.setBlockAndUpdate(pos, ChaosEvents.randomOf(GENERAL_CHAOS_TARGET).defaultBlockState());
                }
            }
        }
        ChaosEvents.broadcast(level, Rarity.RARE, "\ud83c\udf00 \u0425\u0430\u043e\u0441-\u0431\u043b\u043e\u043a\u0438 \u043d\u0430\u0432\u043a\u043e\u043b\u043e!");
    }

    private static void reinforcementsEvent(ServerLevel level, ServerPlayer player) {
        int i;
        BlockPos center = player.blockPosition();
        for (i = 0; i < 2; ++i) {
            IronGolem golem = (IronGolem) EntityType.IRON_GOLEM.create(level);
            if (golem == null) continue;
            golem.moveTo((double) (center.getX() + RANDOM.nextInt(5) - 2), (double) center.getY(), (double) (center.getZ() + RANDOM.nextInt(5) - 2), 0.0f, 0.0f);
            level.addFreshEntity((Entity) golem);
        }
        for (i = 0; i < 4; ++i) {
            SnowGolem snowGolem = (SnowGolem) EntityType.SNOW_GOLEM.create(level);
            if (snowGolem == null) continue;
            snowGolem.moveTo((double) (center.getX() + RANDOM.nextInt(7) - 3), (double) center.getY(), (double) (center.getZ() + RANDOM.nextInt(7) - 3), 0.0f, 0.0f);
            level.addFreshEntity((Entity) snowGolem);
        }
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 4));
        ChaosEvents.broadcast(level, Rarity.EPIC, "\u043f\u0456\u0434\u043a\u0440\u0456\u043f\u043b\u0435\u043d\u043d\u044f \u043f\u0440\u0438\u0431\u0443\u043b\u043e");
    }

    private static void birthdayCake(ServerLevel level, ServerPlayer player) {
        player.getInventory().add(new ItemStack((ItemLike) Items.CAKE));
        ChaosEvents.broadcast(level, Rarity.COMMON, "\u0432 \u0442\u0435\u0431\u0435 \u0434\u0435\u043d\u044c \u043d\u0430\u0440\u043e\u0434\u0436\u0435\u043d\u043d\u044f \u043d\u0456 \u043d\u0443 \u0432\u0441\u0435\u043e\u0434\u043d\u043e \u043d\u0430 \u0442\u043e\u0440\u0442");
    }

    private static void snackTime(ServerLevel level, ServerPlayer player) {
        player.getInventory().add(new ItemStack((ItemLike) Items.BREAD, 10));
        player.getInventory().add(new ItemStack((ItemLike) Items.COOKED_BEEF, 5));
        ChaosEvents.broadcast(level, Rarity.COMMON, "\u043f\u043e\u0440\u0430 \u043f\u0456\u0434\u043a\u0440\u0456\u043f\u0438\u0442\u0438\u0441\u044c");
    }

    private static void toolRefresh(ServerLevel level, ServerPlayer player) {
        player.getInventory().add(new ItemStack((ItemLike) ChaosEvents.randomOf(TOOL_POOL)));
        ChaosEvents.broadcast(level, Rarity.UNCOMMON, "\u043f\u043e\u0440\u0430 \u043f\u043e\u043d\u043e\u0432\u0438\u0442\u0438 \u0456\u043d\u0441\u0442\u0440\u0443\u043c\u0435\u043d\u0442");
    }

    private static void newOutfit(ServerLevel level, ServerPlayer player) {
        Item[] itemArray;
        boolean leather = RANDOM.nextBoolean();
        if (leather) {
            itemArray = new Item[]{Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS};
        } else {
            itemArray = new Item[]{Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_BOOTS};
        }
        Item[] pieces = itemArray;
        player.getInventory().add(new ItemStack((ItemLike) ChaosEvents.randomOf(pieces)));
        ChaosEvents.broadcast(level, Rarity.UNCOMMON, "\u043d\u043e\u0432\u0435 \u0432\u0431\u0440\u0430\u043d\u043d\u044f");
    }

    private static void imPoor(ServerLevel level, ServerPlayer player) {
        player.getInventory().add(new ItemStack((ItemLike) Blocks.DIAMOND_ORE, 2));
        ChaosEvents.broadcast(level, Rarity.LEGENDARY, "\u044f \u0431\u0456\u0434\u043d\u0438\u0439");
    }

    private static void protectionNeeded(ServerLevel level, ServerPlayer player) {
        player.getInventory().add(new ItemStack((ItemLike) Items.SHIELD));
        player.getInventory().add(new ItemStack((ItemLike) Items.WOODEN_SWORD));
        ChaosEvents.broadcast(level, Rarity.UNCOMMON, "\u0437\u0430\u0445\u0438\u0441\u0442 \u043f\u043e\u0442\u0440\u0456\u0431\u0435\u043d");
    }

    private static void letsTrade(ServerLevel level, ServerPlayer player) {
        player.getInventory().add(new ItemStack((ItemLike) Blocks.EMERALD_ORE, 3));
        ChaosEvents.broadcast(level, Rarity.RARE, "\u043f\u043e \u0442\u043e\u0440\u0433\u0443\u0454\u043c\u043e");
    }

    private static void specialApple(ServerLevel level, ServerPlayer player) {
        player.getInventory().add(new ItemStack((ItemLike) Items.GOLDEN_APPLE, 5));
        ChaosEvents.broadcast(level, Rarity.RARE, "\u044f\u0431\u043b\u043e\u0447\u043a\u043e \u0456 \u043d\u0435 \u043e\u0431\u0438\u0447\u043d\u0435");
    }

    private static void proApple(ServerLevel level, ServerPlayer player) {
        player.getInventory().add(new ItemStack((ItemLike) Items.ENCHANTED_GOLDEN_APPLE, 5));
        ChaosEvents.broadcast(level, Rarity.EPIC, "\u044f\u0431\u043b\u043e\u0447\u043a\u043e \u043f\u0440\u043e");
    }

    private static void godTierEvent(ServerLevel level, ServerPlayer player) {
        ItemStack helmet = new ItemStack((ItemLike) Items.NETHERITE_HELMET);
        ItemStack chest = new ItemStack((ItemLike) Items.NETHERITE_CHESTPLATE);
        ItemStack legs = new ItemStack((ItemLike) Items.NETHERITE_LEGGINGS);
        ItemStack boots = new ItemStack((ItemLike) Items.NETHERITE_BOOTS);
        for (ItemStack piece : new ItemStack[]{helmet, chest, legs, boots}) {
            piece.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 2 + RANDOM.nextInt(2));
        }
        ItemStack sword = new ItemStack((ItemLike) Items.NETHERITE_SWORD);
        sword.enchant(Enchantments.SHARPNESS, 1);
        sword.enchant(Enchantments.FIRE_ASPECT, 1);
        sword.enchant(Enchantments.MOB_LOOTING, 2 + RANDOM.nextInt(2));
        player.getInventory().add(helmet);
        player.getInventory().add(chest);
        player.getInventory().add(legs);
        player.getInventory().add(boots);
        player.getInventory().add(sword);
        player.getInventory().add(new ItemStack((ItemLike) Items.SHIELD));
        for (int i = 0; i < 6; ++i) {
            double x = player.getX() + (double) RANDOM.nextInt(7) - 3.0;
            double y = player.getY() + (double) RANDOM.nextInt(3);
            double z = player.getZ() + (double) RANDOM.nextInt(7) - 3.0;
            level.addFreshEntity((Entity) new FireworkRocketEntity((Level) level, x, y, z, ChaosEvents.randomFirework()));
        }
        ChaosEvents.broadcast(level, Rarity.DIVINE, "\u0411\u043e\u0433 \u0442\u043e \u044f");
    }

    private static void smallRes(ServerLevel level, ServerPlayer player) {
        int pick = RANDOM.nextInt(5);
        player.getInventory().add(switch (pick) {
            case 0 -> new ItemStack((ItemLike) ChaosEvents.randomOf(WOOD_PLANKS), 32);
            case 1 -> new ItemStack((ItemLike) Blocks.COARSE_DIRT, 32);
            case 2 -> new ItemStack((ItemLike) Blocks.STONE, 32);
            case 3 -> new ItemStack((ItemLike) Blocks.COBBLESTONE, 32);
            default -> new ItemStack((ItemLike) Items.STICK, 32);
        });
        ChaosEvents.broadcast(level, Rarity.COMMON, "\u0442\u0440\u0456\u0448\u043a\u0438 \u0440\u0435\u0441\u0456\u0432");
    }

    private static void kitStart(ServerLevel level, ServerPlayer player) {
        Item[] pieces;
        for (Item piece : pieces = new Item[]{Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS}) {
            ItemStack stack = new ItemStack((ItemLike) piece);
            if (ChaosEvents.chance(25)) {
                stack.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 1);
            }
            player.getInventory().add(stack);
        }
        player.getInventory().add(new ItemStack((ItemLike) Items.WOODEN_SWORD));
        player.getInventory().add(new ItemStack((ItemLike) Items.WOODEN_PICKAXE));
        player.getInventory().add(new ItemStack((ItemLike) Items.WOODEN_AXE));
        ChaosEvents.broadcast(level, Rarity.UNCOMMON, "\u043a\u0456\u0442 \u0441\u0442\u0430\u0440\u0442");
    }

    private static void brainsEvent(ServerLevel level, ServerPlayer player) {
        ChaosEvents.spawnNear(level, player, EntityType.ZOMBIE, 5, 6);
        ChaosEvents.broadcast(level, Rarity.COMMON, "\u043c\u043e\u0437\u0433\u0438\u0438\u0438\u0438");
    }

    private static void skeletonsComing(ServerLevel level, ServerPlayer player) {
        ChaosEvents.spawnNear(level, player, EntityType.SKELETON, 5, 6);
        ChaosEvents.broadcast(level, Rarity.COMMON, "\u0441\u043a\u0435\u043b\u0435\u0442\u0438 \u0439\u0434\u0443\u0442\u044c");
    }

    private static void kokoko(ServerLevel level, ServerPlayer player) {
        ChaosEvents.spawnNear(level, player, EntityType.CHICKEN, 6, 6);
        ChaosEvents.broadcast(level, Rarity.COMMON, "\u043a\u043e-\u043a\u043e-\u043a\u043e");
    }

    private static void animalsComing(ServerLevel level, ServerPlayer player) {
        ChaosEvents.spawnNear(level, player, EntityType.COW, 3, 6);
        ChaosEvents.spawnNear(level, player, EntityType.PIG, 2, 6);
        ChaosEvents.spawnNear(level, player, EntityType.CHICKEN, 5, 6);
        ChaosEvents.spawnNear(level, player, EntityType.SHEEP, 3, 6);
        ChaosEvents.broadcast(level, Rarity.UNCOMMON, "\u0442\u0432\u0430\u0440\u0438\u043d\u0438 \u0439\u0434\u0443\u0442\u044c");
    }

    private static void theyAreComingEvent(ServerLevel level, ServerPlayer player) {
        ChaosEvents.broadcast(level, Rarity.EPIC, "\u0432\u043e\u043d\u0438 \u0439\u0434\u0443\u0442\u044c \u0437\u0430 \u0442\u043e\u0431\u043e\u044e");
        for (int i = 10; i >= 0; --i) {
            int count = i;
            ChaosScheduler.schedule((10 - i) * 20, () -> ChaosEvents.broadcast(level, Rarity.EPIC, String.valueOf(count)));
        }
        ChaosScheduler.schedule(220, () -> ChaosEvents.spawnGoldenZombies(level, player));
    }

    private static void spawnGoldenZombies(ServerLevel level, ServerPlayer player) {
        BlockPos center = player.blockPosition();
        for (int i = 0; i < 10; ++i) {
            Zombie zombie = (Zombie) EntityType.ZOMBIE.create(level);
            if (zombie == null) continue;
            zombie.setItemSlot(EquipmentSlot.HEAD, new ItemStack((ItemLike) Items.GOLDEN_HELMET));
            zombie.setItemSlot(EquipmentSlot.CHEST, new ItemStack((ItemLike) Items.GOLDEN_CHESTPLATE));
            zombie.setItemSlot(EquipmentSlot.LEGS, new ItemStack((ItemLike) Items.GOLDEN_LEGGINGS));
            zombie.setItemSlot(EquipmentSlot.FEET, new ItemStack((ItemLike) Items.GOLDEN_BOOTS));
            double x = center.getX() + RANDOM.nextInt(11) - 5;
            double z = center.getZ() + RANDOM.nextInt(11) - 5;
            zombie.moveTo(x, (double) center.getY(), z, 0.0f, 0.0f);
            level.addFreshEntity((Entity) zombie);
        }
        ChaosEvents.broadcast(level, Rarity.EPIC, "\u0432\u043e\u043d\u0438 \u043f\u0440\u0438\u0439\u0448\u043b\u0438!");
    }

    private static void metalQuestion(ServerLevel level, ServerPlayer player) {
        player.getInventory().add(new ItemStack((ItemLike) Items.IRON_INGOT, 16));
        ChaosEvents.broadcast(level, Rarity.UNCOMMON, "\u043c\u0435\u0442\u0430\u043b?");
    }

    private static void ewEvent(ServerLevel level, ServerPlayer player) {
        player.getInventory().add(new ItemStack((ItemLike) Items.ROTTEN_FLESH, 2));
        ChaosEvents.broadcast(level, Rarity.COMMON, "\u0444\u0443\u0443\u0443\u0443");
    }

    private static void cactusEvent(ServerLevel level, ServerPlayer player) {
        player.getInventory().add(new ItemStack((ItemLike) Blocks.CACTUS, 10));
        ChaosEvents.broadcast(level, Rarity.COMMON, "\u043a\u0430\u043a\u0442\u0443\u0441!?");
    }

    private static void traderArrived(ServerLevel level, ServerPlayer player) {
        ChaosEvents.spawnNear(level, player, EntityType.WANDERING_TRADER, 1, 5);
        ChaosEvents.broadcast(level, Rarity.RARE, "\u0442\u043e\u0440\u0433\u043e\u0432\u0435\u0446\u044c \u043f\u0440\u0438\u0431\u0443\u0432");
    }

    private static void dogsEvent(ServerLevel level, ServerPlayer player) {
        BlockPos center = player.blockPosition();
        for (int i = 0; i < 10; ++i) {
            Wolf wolf = (Wolf) EntityType.WOLF.create(level);
            if (wolf == null) continue;
            wolf.setTame(true);
            wolf.setOwnerUUID(player.getUUID());
            double x = center.getX() + RANDOM.nextInt(9) - 4;
            double z = center.getZ() + RANDOM.nextInt(9) - 4;
            wolf.moveTo(x, (double) center.getY(), z, 0.0f, 0.0f);
            level.addFreshEntity((Entity) wolf);
        }
        ChaosEvents.broadcast(level, Rarity.RARE, "\u0445\u0430\u0437\u044f\u0438\u0438\u0438\u043d\u043d\u043d");
    }

    private static void shineEvent(ServerLevel level, ServerPlayer player) {
        player.getInventory().add(new ItemStack((ItemLike) Items.GOLDEN_HELMET));
        player.getInventory().add(new ItemStack((ItemLike) Items.GOLDEN_CHESTPLATE));
        player.getInventory().add(new ItemStack((ItemLike) Items.GOLDEN_LEGGINGS));
        player.getInventory().add(new ItemStack((ItemLike) Items.GOLDEN_BOOTS));
        player.getInventory().add(new ItemStack((ItemLike) Items.GOLDEN_SWORD));
        player.getInventory().add(new ItemStack((ItemLike) Items.GOLDEN_PICKAXE));
        player.getInventory().add(new ItemStack((ItemLike) Items.GOLDEN_AXE));
        player.getInventory().add(new ItemStack((ItemLike) Items.GOLDEN_SHOVEL));
        player.getInventory().add(new ItemStack((ItemLike) Items.GOLDEN_HOE));
        player.getInventory().add(new ItemStack((ItemLike) Items.GOLD_INGOT, 1));
        player.getInventory().add(new ItemStack((ItemLike) Blocks.GOLD_ORE, 1));
        ChaosEvents.broadcast(level, Rarity.EPIC, "\u0441\u0438\u044f\u0439");
    }

    private static void magicEvent(ServerLevel level, ServerPlayer player) {
        int dur = 3600;
        player.addEffect(new MobEffectInstance(MobEffects.JUMP, dur, 1));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, dur, 0));
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, dur, 1));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, dur, 1));
        ChaosEvents.broadcast(level, Rarity.EPIC, "\u043c\u0430\u0433\u0456\u044f\u044f\u044f\u044f");
    }

    private static void trashEvent(ServerLevel level, ServerPlayer player) {
        for (int i = 0; i < 30; ++i) {
            player.getInventory().add(new ItemStack((ItemLike) Items.BONE_MEAL, 1));
        }
        ChaosEvents.broadcast(level, Rarity.EPIC, "\u043c\u0443\u0441\u043e\u0440\u043a\u0430");
    }

    private static void loserEvent(ServerLevel level, ServerPlayer player) {
        BlockPos center = player.blockPosition();
        Entity warden = EntityType.WARDEN.create(level);
        if (warden != null) {
            warden.moveTo((double) center.getX(), (double) center.getY(), (double) center.getZ(), 0.0f, 0.0f);
            level.addFreshEntity(warden);
        }
        ChaosEvents.broadcast(level, Rarity.DIVINE, "\u043b\u043e\u0448\u0430\u0440\u0430");
    }

    private static void pigRainEvent(ServerLevel level, ServerPlayer player) {
        ChaosEvents.spawnNear(level, player, EntityType.PIG, 20, 8);
        ChaosEvents.broadcast(level, Rarity.EPIC, "\u0445\u0440\u044e");
    }

    private static void welfareEvent(ServerLevel level, ServerPlayer player) {
        player.getInventory().add(new ItemStack((ItemLike) Items.IRON_INGOT, 32));
        player.getInventory().add(new ItemStack((ItemLike) Items.GOLD_INGOT, 32));
        player.getInventory().add(new ItemStack((ItemLike) ChaosEvents.randomOf(WOOD_PLANKS), 64));
        player.getInventory().add(new ItemStack((ItemLike) Items.ARROW, 10));
        player.getInventory().add(new ItemStack((ItemLike) Items.GOLD_NUGGET, 6));
        player.getInventory().add(new ItemStack((ItemLike) Items.TORCH, 20));
        player.getInventory().add(new ItemStack((ItemLike) Items.STICK, 32));
        player.getInventory().add(new ItemStack((ItemLike) Items.APPLE, 14));
        ChaosEvents.broadcast(level, Rarity.EPIC, "\u0441\u043e\u0446 \u0434\u043e\u043f\u043e\u043c\u043e\u0433\u0430");
    }

    private static void boneEvent(ServerLevel level, ServerPlayer player) {
        player.getInventory().add(new ItemStack((ItemLike) Items.BONE, 2));
        ChaosEvents.broadcast(level, Rarity.RARE, "\u043d\u0430 \u043f\u043e\u0433\u0440\u0438\u0437\u0438");
    }

    private static void bananaFishEvent(ServerLevel level, ServerPlayer player) {
        player.getInventory().add(new ItemStack((ItemLike) Blocks.MELON, 1));
        ChaosEvents.broadcast(level, Rarity.EPIC, "\u0431\u0430\u043d\u0430\u043d\u043e\u0432\u0430 \u0440\u0438\u0431\u0430");
    }

    private static void cocoonEvent(ServerLevel level, ServerPlayer player) {
        level.setBlockAndUpdate(player.blockPosition(), Blocks.COBWEB.defaultBlockState());
        ChaosEvents.spawnNear(level, player, EntityType.SPIDER, 5, 6);
        ChaosEvents.broadcast(level, Rarity.RARE, "\u0432 \u043a\u043e\u043a\u043e\u043d");
    }

    private static void cursedEvent(ServerLevel level, ServerPlayer player) {
        ChaosEvents.broadcast(level, Rarity.LEGENDARY, "\u0442\u0438 \u043f\u0440\u043e\u043a\u043b\u044f\u0442");
        for (int i = 10; i >= 0; --i) {
            int count = i;
            ChaosScheduler.schedule((10 - i) * 20, () -> ChaosEvents.broadcast(level, Rarity.LEGENDARY, String.valueOf(count)));
        }
        ChaosScheduler.schedule(220, () -> {
            int dur = 2400;
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, dur, 0));
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, dur, 0));
            player.addEffect(new MobEffectInstance(MobEffects.HUNGER, dur, 0));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, dur, 0));
            ChaosEvents.broadcast(level, Rarity.LEGENDARY, "\u043f\u0440\u043e\u043a\u043b\u044f\u0442\u0442\u044f \u043f\u043e\u0434\u0456\u044f\u043b\u043e!");
        });
    }

    private static void healEvent(ServerLevel level, ServerPlayer player) {
        ItemStack potion = PotionUtils.setPotion(new ItemStack((ItemLike) Items.POTION), (Potion) Potions.HEALING);
        player.getInventory().add(potion);
        ChaosEvents.broadcast(level, Rarity.UNCOMMON, "\u0445\u0456\u043b");
    }

    private static void blindEvent(ServerLevel level, ServerPlayer player) {
        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 300, 0));
        ChaosEvents.broadcast(level, Rarity.RARE, "\u044f \u043e\u0441\u043b\u0435\u043f");
    }

    private static void ouchEvent(ServerLevel level, ServerPlayer player) {
        DamageSource generic = level.damageSources().generic();
        player.hurt(generic, 6.0f);
        player.hurt(generic, 6.0f);
        ChaosEvents.broadcast(level, Rarity.EPIC, "\u043f\u0430\u043b\u044c\u0446\u0435\u043c \u043e\u0431 \u0442\u0443\u043c\u0431\u043e\u0447\u043a\u0443");
    }

    private static void shawarmaEvent(ServerLevel level, ServerPlayer player) {
        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 600, 0));
        ChaosEvents.broadcast(level, Rarity.RARE, "\u0448\u0430\u0443\u0440\u043c\u0430 \u0437 \u0430\u0432\u0442\u043e\u0432\u043e\u043a\u0437\u0430\u043b\u0443");
    }

    private static void pillagersEvent(ServerLevel level, ServerPlayer player) {
        ChaosEvents.spawnNear(level, player, EntityType.PILLAGER, 10, 8);
        ChaosEvents.broadcast(level, Rarity.EPIC, "\u0442\u0443 \u0442\u0443 \u0442\u0443\u0443\u0443\u0443");
    }

    private static void witchesEvent(ServerLevel level, ServerPlayer player) {
        ChaosEvents.spawnNear(level, player, EntityType.WITCH, 3, 6);
        ChaosEvents.broadcast(level, Rarity.EPIC, "\u043f\u043e\u0440\u0447\u0443 \u043d\u0430\u0432\u0435\u043b\u0438");
    }

    private static void theOneEvent(ServerLevel level, ServerPlayer player) {
        BlockPos center = player.blockPosition();
        Horse horse = (Horse) EntityType.HORSE.create(level);
        if (horse != null) {
            horse.setTamed(true);
            horse.setOwnerUUID(player.getUUID());
            horse.moveTo((double) (center.getX() + 1), (double) center.getY(), (double) center.getZ(), 0.0f, 0.0f);
            level.addFreshEntity((Entity) horse);
        }
        player.getInventory().add(new ItemStack((ItemLike) Items.SADDLE));
        ItemStack helmet = new ItemStack((ItemLike) Items.DIAMOND_HELMET);
        ItemStack chest = new ItemStack((ItemLike) Items.DIAMOND_CHESTPLATE);
        ItemStack legs = new ItemStack((ItemLike) Items.DIAMOND_LEGGINGS);
        ItemStack boots = new ItemStack((ItemLike) Items.DIAMOND_BOOTS);
        for (ItemStack piece : new ItemStack[]{helmet, chest, legs, boots}) {
            piece.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 2 + RANDOM.nextInt(3));
            piece.enchant(Enchantments.UNBREAKING, 2 + RANDOM.nextInt(3));
            piece.enchant(Enchantments.THORNS, 2 + RANDOM.nextInt(3));
        }
        ItemStack sword = new ItemStack((ItemLike) Items.DIAMOND_SWORD);
        sword.enchant(Enchantments.MOB_LOOTING, 3 + RANDOM.nextInt(3));
        sword.enchant(Enchantments.SWEEPING_EDGE, 3 + RANDOM.nextInt(3));
        sword.enchant(Enchantments.FIRE_ASPECT, 1 + RANDOM.nextInt(2));
        player.getInventory().add(helmet);
        player.getInventory().add(chest);
        player.getInventory().add(legs);
        player.getInventory().add(boots);
        player.getInventory().add(sword);
        player.getInventory().add(new ItemStack((ItemLike) Items.SHIELD));
        player.getInventory().add(new ItemStack((ItemLike) Items.ENCHANTED_GOLDEN_APPLE, 10));
        player.getInventory().add(new ItemStack((ItemLike) Items.TOTEM_OF_UNDYING, 3));
        for (int i = 0; i < 10; ++i) {
            double x = player.getX() + (double) RANDOM.nextInt(9) - 4.0;
            double y = player.getY() + (double) RANDOM.nextInt(3);
            double z = player.getZ() + (double) RANDOM.nextInt(9) - 4.0;
            level.addFreshEntity((Entity) new FireworkRocketEntity((Level) level, x, y, z, ChaosEvents.randomFirework()));
        }
        ChaosEvents.broadcast(level, Rarity.DIVINE, "\u0442\u043e\u0439 \u0441\u0430\u043c\u0438\u0439");
    }

    private static void elytraEvent(ServerLevel level, ServerPlayer player) {
        player.getInventory().add(new ItemStack((ItemLike) Items.ELYTRA));
        for (int i = 0; i < 20; ++i) {
            player.getInventory().add(ChaosEvents.randomFirework());
        }
        ChaosEvents.broadcast(level, Rarity.LEGENDARY, "\u043b\u0438\u0442\u0438");
    }

    private static void fishRainEvent(ServerLevel level, ServerPlayer player) {
        BlockPos center = player.blockPosition();
        for (int i = 0; i < 20; ++i) {
            double x = center.getX() + RANDOM.nextInt(15) - 7;
            double y = center.getY() + 10 + RANDOM.nextInt(8);
            double z = center.getZ() + RANDOM.nextInt(15) - 7;
            Entity fish = EntityType.COD.create(level);
            if (fish == null) continue;
            fish.moveTo(x, y, z, 0.0f, 0.0f);
            level.addFreshEntity(fish);
        }
        ChaosEvents.broadcast(level, Rarity.EPIC, "\u0440\u0438\u0431\u043d\u0438\u0439 \u0434\u043e\u0436\u0434\u044c");
    }

    private static void princeEvent(ServerLevel level, ServerPlayer player) {
        BlockPos center = player.blockPosition();
        Pig pig = (Pig) EntityType.PIG.create(level);
        if (pig != null) {
            pig.moveTo((double) (center.getX() + RANDOM.nextInt(5) - 2), (double) center.getY(), (double) (center.getZ() + RANDOM.nextInt(5) - 2), 0.0f, 0.0f);
            level.addFreshEntity((Entity) pig);
        }
        player.getInventory().add(new ItemStack((ItemLike) Items.SADDLE));
        player.getInventory().add(new ItemStack((ItemLike) Items.GOLDEN_CARROT));
        ChaosEvents.broadcast(level, Rarity.RARE, "\u043f\u0440\u0438\u043d\u0446 \u0437 \u0430\u043b\u0456\u0435\u043a\u0441\u043f\u0440\u0435\u0441");
    }

    private static void pearlsEvent(ServerLevel level, ServerPlayer player) {
        player.getInventory().add(new ItemStack((ItemLike) Items.ENDER_PEARL, 7));
        ChaosEvents.broadcast(level, Rarity.RARE, "\u0434\u0454\u043c\u0447\u0443\u0436\u0454\u043d\u0438");
    }

    private static void myArmyEvent(ServerLevel level, ServerPlayer player) {
        ChaosEvents.spawnNear(level, player, EntityType.SNOW_GOLEM, 20, 10);
        ChaosEvents.spawnNear(level, player, EntityType.IRON_GOLEM, 5, 10);
        ChaosEvents.broadcast(level, Rarity.LEGENDARY, "\u043c\u043e\u044f \u0430\u0440\u043c\u0456\u044f");
    }

    private static void invertTreesEvent(ServerLevel level, ServerPlayer player) {
        BlockPos center = player.blockPosition();
        int radius = 6;
        ArrayList<BlockPos> leafPositions = new ArrayList<BlockPos>();
        ArrayList<BlockPos> logPositions = new ArrayList<BlockPos>();
        for (int x = -radius; x <= radius; ++x) {
            for (int z = -radius; z <= radius; ++z) {
                block2: for (int y = -2; y <= 10; ++y) {
                    BlockPos pos = center.offset(x, y, z);
                    Block b = level.getBlockState(pos).getBlock();
                    for (Block leaf : LEAVES) {
                        if (b != leaf) continue;
                        leafPositions.add(pos);
                        break;
                    }
                    for (Block log : WOOD_LOGS) {
                        if (b != log) continue;
                        logPositions.add(pos);
                        continue block2;
                    }
                }
            }
        }
        for (BlockPos pos : leafPositions) {
            level.setBlockAndUpdate(pos, ChaosEvents.randomOf(WOOD_LOGS).defaultBlockState());
        }
        for (BlockPos pos : logPositions) {
            level.setBlockAndUpdate(pos, ChaosEvents.randomOf(LEAVES).defaultBlockState());
        }
        ChaosEvents.broadcast(level, Rarity.EPIC, "\u043d\u0430\u043e\u0431\u043e\u0440\u043e\u0442");
    }

    private static void robEvent(ServerLevel level, ServerPlayer player) {
        Inventory inv = player.getInventory();
        ArrayList<Integer> nonEmpty = new ArrayList<Integer>();
        for (int i = 0; i < inv.getContainerSize(); ++i) {
            if (inv.getItem(i).isEmpty()) continue;
            nonEmpty.add(i);
        }
        if (!nonEmpty.isEmpty()) {
            int slot = (Integer) nonEmpty.get(RANDOM.nextInt(nonEmpty.size()));
            inv.setItem(slot, ItemStack.EMPTY);
        }
        ChaosEvents.broadcast(level, Rarity.RARE, "\u043f\u043e\u0433\u0440\u0430\u0431\u0443\u0432\u0430\u043b\u0438");
    }

    private static void swapEarthStoneEvent(ServerLevel level, ServerPlayer player) {
        BlockPos center = player.blockPosition();
        int radius = 5;
        ArrayList<BlockPos> earthPositions = new ArrayList<BlockPos>();
        ArrayList<BlockPos> stonePositions = new ArrayList<BlockPos>();
        for (int x = -radius; x <= radius; ++x) {
            for (int z = -radius; z <= radius; ++z) {
                block2: for (int y = -4; y <= -1; ++y) {
                    BlockPos pos = center.offset(x, y, z);
                    Block b = level.getBlockState(pos).getBlock();
                    for (Block e : EARTH_SOURCE) {
                        if (b != e) continue;
                        earthPositions.add(pos);
                        break;
                    }
                    for (Block s : STONE_SOURCE) {
                        if (b != s) continue;
                        stonePositions.add(pos);
                        continue block2;
                    }
                }
            }
        }
        for (BlockPos pos : earthPositions) {
            level.setBlockAndUpdate(pos, ChaosEvents.randomOf(STONE_SOURCE).defaultBlockState());
        }
        for (BlockPos pos : stonePositions) {
            level.setBlockAndUpdate(pos, ChaosEvents.randomOf(EARTH_SOURCE).defaultBlockState());
        }
        ChaosEvents.broadcast(level, Rarity.UNCOMMON, "\u043c\u0456\u043d\u044f\u0454\u043c\u043e");
    }

    private static void hoofedEvent(ServerLevel level, ServerPlayer player) {
        ChaosEvents.spawnNear(level, player, ChaosEvents.randomOf(HOOFED_POOL), 1, 5);
        ChaosEvents.broadcast(level, Rarity.COMMON, "\u043f\u0430\u0440\u043d\u043e\u043a\u043e\u043f\u0438\u0442\u043d\u0456");
    }

    private static void flagEvent(ServerLevel level, ServerPlayer player) {
        ItemStack banner = new ItemStack((ItemLike) Items.WHITE_BANNER);
        CompoundTag blockEntityTag = new CompoundTag();
        ListTag patterns = new ListTag();
        CompoundTag pattern = new CompoundTag();
        pattern.putString("Pattern", "mc");
        pattern.putInt("Color", DyeColor.YELLOW.getId());
        patterns.add((Tag) pattern);
        blockEntityTag.put("Patterns", (Tag) patterns);
        banner.getOrCreateTag().put("BlockEntityTag", (Tag) blockEntityTag);
        player.getInventory().add(banner);
        ChaosEvents.broadcast(level, Rarity.UNCOMMON, "\u0444\u043b\u0430\u0433 \u0442\u043e\u0431\u0456 \u0432 \u0440\u0443\u043a\u0438");
    }

    private static void catsEvent(ServerLevel level, ServerPlayer player) {
        BlockPos center = player.blockPosition();
        for (int i = 0; i < 5; ++i) {
            Cat cat = (Cat) EntityType.CAT.create(level);
            if (cat == null) continue;
            cat.setTame(true);
            cat.setOwnerUUID(player.getUUID());
            double x = center.getX() + RANDOM.nextInt(7) - 3;
            double z = center.getZ() + RANDOM.nextInt(7) - 3;
            cat.moveTo(x, (double) center.getY(), z, 0.0f, 0.0f);
            level.addFreshEntity((Entity) cat);
        }
        ChaosEvents.broadcast(level, Rarity.RARE, "\u043c\u044f\u0443");
    }

    private static void giveRandomBlockStack(ServerLevel level, ServerPlayer player) {
        Block block = ChaosEvents.randomOf(GIFT_BLOCK_POOL);
        player.getInventory().add(new ItemStack((ItemLike) block, 1));
        ChaosEvents.broadcast(level, Rarity.COMMON, "\ud83c\udf81 \u041a\u043e\u043c\u0443\u0441\u044c \u0432\u0438\u043f\u0430\u0432 \u0431\u043b\u043e\u043a: " + block.getName().getString());
    }

    private static void giveRandomWeapon(ServerLevel level, ServerPlayer player) {
        Item item = ChaosEvents.randomOf(GIFT_WEAPON_POOL);
        player.getInventory().add(new ItemStack((ItemLike) item, 1));
        ChaosEvents.broadcast(level, Rarity.UNCOMMON, "\u2694 \u041a\u043e\u043c\u0443\u0441\u044c \u0432\u0438\u043f\u0430\u043b\u0430 \u0437\u0431\u0440\u043e\u044f: " + item.getDescription().getString());
    }

    private static void randomPotionEffect(ServerLevel level, ServerPlayer player) {
        MobEffect effect = ChaosEvents.randomOf(POTION_POOL);
        player.addEffect(new MobEffectInstance(effect, 600, 1));
        ChaosEvents.broadcast(level, Rarity.COMMON, "\ud83e\uddea \u041a\u043e\u043c\u0443\u0441\u044c \u0434\u0456\u0441\u0442\u0430\u0432\u0441\u044f \u043a\u043e\u0440\u0438\u0441\u043d\u0438\u0439 \u0435\u0444\u0435\u043a\u0442 \u0437\u0456\u043b\u043b\u044f!");
    }

    private static void arrowRain(ServerLevel level, ServerPlayer player) {
        BlockPos center = player.blockPosition();
        for (int i = 0; i < 40; ++i) {
            double x = center.getX() + RANDOM.nextInt(21) - 10;
            double y = center.getY() + 15 + RANDOM.nextInt(10);
            double z = center.getZ() + RANDOM.nextInt(21) - 10;
            Arrow arrow = new Arrow((Level) level, x, y, z);
            arrow.setDeltaMovement(0.0, -1.2, 0.0);
            level.addFreshEntity((Entity) arrow);
        }
        ChaosEvents.broadcast(level, Rarity.RARE, "\u2614 \u0414\u043e\u0449 \u0437\u0456 \u0441\u0442\u0440\u0456\u043b!");
    }

    private static void lightningStorm(ServerLevel level, ServerPlayer player) {
        BlockPos center = player.blockPosition();
        for (int i = 0; i < 3; ++i) {
            BlockPos pos = center.offset(RANDOM.nextInt(21) - 10, 0, RANDOM.nextInt(21) - 10);
            Entity bolt = EntityType.LIGHTNING_BOLT.create(level);
            if (bolt == null) continue;
            bolt.moveTo(Vec3.atCenterOf(pos));
            level.addFreshEntity(bolt);
        }
        ChaosEvents.broadcast(level, Rarity.RARE, "\u26a1 \u0411\u043b\u0438\u0441\u043a\u0430\u0432\u043a\u0438!");
    }

    private static void yeetUp(ServerLevel level, ServerPlayer player) {
        player.teleportTo(player.getX(), player.getY() + 25.0, player.getZ());
        ChaosEvents.broadcast(level, Rarity.UNCOMMON, "\ud83d\ude80 \u041a\u043e\u0433\u043e\u0441\u044c \u043f\u0456\u0434\u043a\u0438\u043d\u0443\u043b\u043e \u0432 \u043d\u0435\u0431\u043e!");
    }

    /**
     * Triggers a random event of the exact given rarity.
     * Returns true if an event was successfully triggered.
     */
    public static boolean triggerEventOfRarity(ServerLevel level, ServerPlayer player, Rarity targetRarity) {
        List<WeightedEvent> pool = new ArrayList<>();
        for (WeightedEvent we : EVENTS) {
            if (we.rarity == targetRarity) {
                pool.add(we);
            }
        }
        if (pool.isEmpty()) {
            return false;
        }
        int totalWeight = 0;
        for (WeightedEvent e : pool) {
            totalWeight += e.weight;
        }
        if (totalWeight <= 0) return false;

        int n = RANDOM.nextInt(totalWeight);
        int cumulative = 0;
        for (WeightedEvent e : pool) {
            cumulative += e.weight;
            if (n < cumulative) {
                try {
                    e.action.accept(level, player);
                    return true;
                } catch (Exception ex) {
                    ChaosClockMod.LOGGER.error("Помилка під час хаос-події", ex);
                    return false;
                }
            }
        }
        return false;
    }

    public static void triggerRandomEvent(ServerLevel level, ServerPlayer player) {
        if (EVENTS.isEmpty()) {
            return;
        }
        int karma = com.chaosclock.karma.KarmaHandler.getKarma(player);
        boolean potionLucky = player.hasEffect((MobEffect) ModEffects.LUCK_SURGE.get());
        boolean pureLucky = player.hasEffect((MobEffect) ModEffects.PURE_LUCK_SURGE.get());

        List<WeightedEvent> pool = new ArrayList<WeightedEvent>();

        for (WeightedEvent we : EVENTS) {
            int ord = we.rarity.ordinal();
            boolean allow = true;

            // Karma thresholds
            if (karma >= 300) {
                // High karma: no more COMMON / UNCOMMON events, only RARE+
                allow = ord >= Rarity.RARE.ordinal();
            } else if (karma <= -300) {
                // Low karma: no more EPIC / LEGENDARY / DIVINE events, only up to RARE
                allow = ord <= Rarity.RARE.ordinal();
            }
            // else: all allowed

            // Pure Luck potion forces EPIC+
            if (pureLucky && ord < Rarity.EPIC.ordinal()) {
                allow = false;
            }
            // Regular Fortune potion forces at least RARE+
            else if (potionLucky && ord < Rarity.RARE.ordinal()) {
                allow = false;
            }

            if (allow) {
                pool.add(we);
            }
        }

        if (pool.isEmpty()) {
            pool = EVENTS; // safety fallback
        }

        int totalWeight = 0;
        for (WeightedEvent e : pool) {
            totalWeight += e.weight;
        }
        if (totalWeight <= 0) {
            return;
        }
        int n = RANDOM.nextInt(totalWeight);
        int cumulative = 0;
        for (WeightedEvent e : pool) {
            if (n >= (cumulative += e.weight)) continue;
            try {
                e.action.accept(level, player);
            }
            catch (Exception ex) {
                ChaosClockMod.LOGGER.error("Помилка під час хаос-події", (Throwable) ex);
            }
            return;
        }
    }

    static {
        EVENTS.add(new WeightedEvent(Rarity.COMMON, ChaosEvents::chickensToCows));
        EVENTS.add(new WeightedEvent(Rarity.COMMON, ChaosEvents::cowsToChickens));
        EVENTS.add(new WeightedEvent(Rarity.UNCOMMON, ChaosEvents::cowsToPigs));
        EVENTS.add(new WeightedEvent(Rarity.UNCOMMON, ChaosEvents::pigsToSheep));
        EVENTS.add(new WeightedEvent(Rarity.UNCOMMON, ChaosEvents::animalShuffle));
        EVENTS.add(new WeightedEvent(Rarity.RARE, ChaosEvents::hostileToCalm));
        EVENTS.add(new WeightedEvent(Rarity.UNCOMMON, ChaosEvents::earthBlockSwap));
        EVENTS.add(new WeightedEvent(Rarity.UNCOMMON, ChaosEvents::stoneBlockSwap));
        EVENTS.add(new WeightedEvent(Rarity.UNCOMMON, ChaosEvents::treeSpeciesSwap));
        EVENTS.add(new WeightedEvent(Rarity.RARE, ChaosEvents::generalBlockChaos));
        EVENTS.add(new WeightedEvent(Rarity.COMMON, ChaosEvents::giveRandomBlockStack));
        EVENTS.add(new WeightedEvent(Rarity.UNCOMMON, ChaosEvents::giveRandomWeapon));
        EVENTS.add(new WeightedEvent(Rarity.COMMON, ChaosEvents::randomPotionEffect));
        EVENTS.add(new WeightedEvent(Rarity.RARE, ChaosEvents::arrowRain));
        EVENTS.add(new WeightedEvent(Rarity.RARE, ChaosEvents::lightningStorm));
        EVENTS.add(new WeightedEvent(Rarity.UNCOMMON, ChaosEvents::yeetUp));
        EVENTS.add(new WeightedEvent(Rarity.EPIC, ChaosEvents::reinforcementsEvent));
        EVENTS.add(new WeightedEvent(Rarity.COMMON, ChaosEvents::birthdayCake));
        EVENTS.add(new WeightedEvent(Rarity.COMMON, ChaosEvents::snackTime));
        EVENTS.add(new WeightedEvent(Rarity.UNCOMMON, ChaosEvents::toolRefresh));
        EVENTS.add(new WeightedEvent(Rarity.UNCOMMON, ChaosEvents::newOutfit));
        EVENTS.add(new WeightedEvent(Rarity.LEGENDARY, ChaosEvents::imPoor));
        EVENTS.add(new WeightedEvent(Rarity.UNCOMMON, ChaosEvents::protectionNeeded));
        EVENTS.add(new WeightedEvent(Rarity.RARE, ChaosEvents::letsTrade));
        EVENTS.add(new WeightedEvent(Rarity.RARE, ChaosEvents::specialApple));
        EVENTS.add(new WeightedEvent(Rarity.EPIC, ChaosEvents::proApple));
        EVENTS.add(new WeightedEvent(Rarity.DIVINE, ChaosEvents::godTierEvent));
        EVENTS.add(new WeightedEvent(Rarity.COMMON, ChaosEvents::smallRes));
        EVENTS.add(new WeightedEvent(Rarity.UNCOMMON, ChaosEvents::kitStart));
        EVENTS.add(new WeightedEvent(Rarity.COMMON, ChaosEvents::brainsEvent));
        EVENTS.add(new WeightedEvent(Rarity.COMMON, ChaosEvents::skeletonsComing));
        EVENTS.add(new WeightedEvent(Rarity.COMMON, ChaosEvents::kokoko));
        EVENTS.add(new WeightedEvent(Rarity.UNCOMMON, ChaosEvents::animalsComing));
        EVENTS.add(new WeightedEvent(Rarity.EPIC, ChaosEvents::theyAreComingEvent));
        EVENTS.add(new WeightedEvent(Rarity.UNCOMMON, ChaosEvents::metalQuestion));
        EVENTS.add(new WeightedEvent(Rarity.COMMON, ChaosEvents::ewEvent));
        EVENTS.add(new WeightedEvent(Rarity.COMMON, ChaosEvents::cactusEvent));
        EVENTS.add(new WeightedEvent(Rarity.RARE, ChaosEvents::traderArrived));
        EVENTS.add(new WeightedEvent(Rarity.RARE, ChaosEvents::dogsEvent));
        EVENTS.add(new WeightedEvent(Rarity.EPIC, ChaosEvents::shineEvent));
        EVENTS.add(new WeightedEvent(Rarity.EPIC, ChaosEvents::magicEvent));
        EVENTS.add(new WeightedEvent(Rarity.EPIC, ChaosEvents::trashEvent));
        EVENTS.add(new WeightedEvent(Rarity.DIVINE, ChaosEvents::loserEvent));
        EVENTS.add(new WeightedEvent(Rarity.EPIC, ChaosEvents::pigRainEvent));
        EVENTS.add(new WeightedEvent(Rarity.EPIC, ChaosEvents::welfareEvent));
        EVENTS.add(new WeightedEvent(Rarity.RARE, ChaosEvents::boneEvent));
        EVENTS.add(new WeightedEvent(Rarity.EPIC, ChaosEvents::bananaFishEvent));
        EVENTS.add(new WeightedEvent(Rarity.RARE, ChaosEvents::cocoonEvent));
        EVENTS.add(new WeightedEvent(Rarity.LEGENDARY, ChaosEvents::cursedEvent));
        EVENTS.add(new WeightedEvent(Rarity.UNCOMMON, ChaosEvents::healEvent));
        EVENTS.add(new WeightedEvent(Rarity.RARE, ChaosEvents::blindEvent));
        EVENTS.add(new WeightedEvent(Rarity.EPIC, ChaosEvents::ouchEvent));
        EVENTS.add(new WeightedEvent(Rarity.RARE, ChaosEvents::shawarmaEvent));
        EVENTS.add(new WeightedEvent(Rarity.EPIC, ChaosEvents::pillagersEvent));
        EVENTS.add(new WeightedEvent(Rarity.EPIC, ChaosEvents::witchesEvent));
        EVENTS.add(new WeightedEvent(Rarity.DIVINE, ChaosEvents::theOneEvent));
        EVENTS.add(new WeightedEvent(Rarity.LEGENDARY, ChaosEvents::elytraEvent));
        EVENTS.add(new WeightedEvent(Rarity.EPIC, ChaosEvents::fishRainEvent));
        EVENTS.add(new WeightedEvent(Rarity.RARE, ChaosEvents::princeEvent));
        EVENTS.add(new WeightedEvent(Rarity.RARE, ChaosEvents::pearlsEvent));
        EVENTS.add(new WeightedEvent(Rarity.LEGENDARY, ChaosEvents::myArmyEvent));
        EVENTS.add(new WeightedEvent(Rarity.EPIC, ChaosEvents::invertTreesEvent));
        EVENTS.add(new WeightedEvent(Rarity.RARE, ChaosEvents::robEvent));
        EVENTS.add(new WeightedEvent(Rarity.UNCOMMON, ChaosEvents::swapEarthStoneEvent));
        EVENTS.add(new WeightedEvent(Rarity.COMMON, ChaosEvents::hoofedEvent));
        EVENTS.add(new WeightedEvent(Rarity.UNCOMMON, ChaosEvents::flagEvent));
        EVENTS.add(new WeightedEvent(Rarity.RARE, ChaosEvents::catsEvent));
    }

    public static enum Rarity {
        COMMON(ChatFormatting.WHITE, 30),
        UNCOMMON(ChatFormatting.GREEN, 15),
        RARE(ChatFormatting.BLUE, 8),
        EPIC(ChatFormatting.DARK_PURPLE, 4),
        LEGENDARY(ChatFormatting.GOLD, 2),
        DIVINE(null, 1);

        final ChatFormatting color;
        final int weight;

        private Rarity(ChatFormatting color, int weight) {
            this.color = color;
            this.weight = weight;
        }
    }

    private static class WeightedEvent {
        final Rarity rarity;
        final int weight;
        final BiConsumer<ServerLevel, ServerPlayer> action;

        WeightedEvent(Rarity rarity, BiConsumer<ServerLevel, ServerPlayer> action) {
            this.rarity = rarity;
            this.weight = rarity.weight;
            this.action = action;
        }
    }
}
