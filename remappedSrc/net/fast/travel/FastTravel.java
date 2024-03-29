package net.fast.travel;


import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.structure.v1.FabricStructureBuilder;
import net.fabricmc.fabric.impl.biome.modification.BiomeModificationImpl;
import net.fast.travel.blocks.Teleporter;
import net.fast.travel.blocks.TeleporterEntity;
import net.fast.travel.structure.TeleporterTempleFeature;
import net.fast.travel.structure.TeleporterTempleGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.*;

public class FastTravel implements ModInitializer {

    public static final Block TELEPORTER = new Teleporter();
    public static final BlockItem TELEPORTER_ITEM = new BlockItem(TELEPORTER, new Item.Settings()
                                                                .group(ItemGroup.DECORATIONS)
                                                                .maxCount(8));
    public static BlockEntityType<TeleporterEntity> TELEPORTER_ENTITY;

    public static final StructurePieceType TELEPORTER_TEMPLE_PIECE_TYPE = TeleporterTempleGenerator.Piece::new;
    public static final StructureFeature<DefaultFeatureConfig> TELEPORTER_TEMPLE = new TeleporterTempleFeature(DefaultFeatureConfig.CODEC);
    public static final ConfiguredStructureFeature<?, ?> TELEPORTER_TEMPLE_CONFIGURED = TELEPORTER_TEMPLE.configure(DefaultFeatureConfig.DEFAULT);


    @Override
    public void onInitialize() {
        Registry.register(Registry.BLOCK, new Identifier("fast-travel", "teleporter"), TELEPORTER);
        Registry.register(Registry.ITEM, new Identifier("fast-travel", "teleporter"), TELEPORTER_ITEM);
        TELEPORTER_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "fast-travel:teleporter",
                BlockEntityType.Builder.create(TeleporterEntity::new, TELEPORTER).build(null));

        Registry.register(Registry.STRUCTURE_PIECE, new Identifier("fast-travel", "teleporter_temple_piece"), TELEPORTER_TEMPLE_PIECE_TYPE);
        FabricStructureBuilder.create(new Identifier("fast-travel", "teleporter_temple"), TELEPORTER_TEMPLE)
                .step(GenerationStep.Feature.SURFACE_STRUCTURES)
                .defaultConfig(32, 8, 12345)
                .adjustsSurface()
                .register();

        BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, new Identifier("fast-travel", "teleporter_temple"),
                TELEPORTER_TEMPLE_CONFIGURED);

    }

    public static void print(String str){
        System.out.println("[FT]: "+str);
    }

}

/*TODO générale:
	V l'inventaire "disparait" à cause de la commande "moveToWorld" mais recharger la partie le fait revenir
	V recharger la partie et utiliser un Teleporter fait crasher le jeu (NullpointerException, sans doute problème de chargement de tag)
 	V lier un teleporteur dans un chunk non chargé ne sauve pas son tag
 	V systême de selection doit être étendu au multijoueur
 	V Chunk chargé par la selection doit être déchargé et la selection effacée à la déconnexion
 	X teleport à la position relative au teleporter
 	V le passage end -> overworld ne teleport pas au bon endroit car le passage à l'overworld n'est pas fini lorsqu'on tp aux bonnes positions
 	V probleme avec les ticking entity: lié au passage nether, si le joueur n'a jamais utilisé de netherportal NullPointer exception ServerPlayerEntity.java:702 (lié à moveToWorld, ServerPlayerEntity.java:613, Entity.java:2222)
    V les items disparaisses si le teleporter est dans un chunk non chargé
    V joueur sort du minecart coté client mais pas coté server: -> bug général pour rideable
    V minecart ne tp qu'une fois
    V teleportation detectable par observer
    V structure naturelles
    X certaines structures sont liées, d'autres non
        - la target semble ne pas avoir de world
        - n'a pas encore de suport pour la rotation
        - locateStructure trouve la structure de dépard
        + locate ne return jamais si on le tourne pas depuis la position de la structure en cours de création
            - ServerChunkManager::getChunk join à l'infini
 */
