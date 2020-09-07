package net.fast.travel;


import net.fabricmc.api.ModInitializer;
import net.fast.travel.blocks.Teleporter;
import net.fast.travel.blocks.TeleporterEntity;
import net.fast.travel.blocks.TeleporterItem;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FastTravel implements ModInitializer {

    public static final Block TELEPORTER = new Teleporter();
    public static final BlockItem TELEPORTER_ITEM = new TeleporterItem();
    public static BlockEntityType<TeleporterEntity> TELEPORTER_ENTITY;


    @Override
    public void onInitialize() {
        Registry.register(Registry.BLOCK, new Identifier("fast-travel", "teleporter"), TELEPORTER);
        Registry.register(Registry.ITEM, new Identifier("fast-travel", "teleporter"), TELEPORTER_ITEM);
        TELEPORTER_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "fast-travel:teleporter",
                BlockEntityType.Builder.create(TeleporterEntity::new, TELEPORTER).build(null));
    }
}
/*TODO générale:
	V l'inventaire "disparait" à cause de la commande "moveToWorld" mais recharger la partie le fait revenir
	V recharger la partie et utiliser un Teleporter fait crasher le jeu (NullpointerException, sans doute problème de chargement de tag)
 	V lier un teleporteur dans un chunk non chargé ne sauve pas son tag
 	V systême de selection doit être étendu au multijoueur
 	V Chunk chargé par la selection doit être déchargé et la selection effacée à la déconnexion
 	o teleport à la position relative au teleporter
 	V le passage end -> overworld ne teleport pas au bon endroit car le passage à l'overworld n'est pas fini lorsqu'on tp aux bonnes positions
 	V probleme avec les ticking entity: lié au passage nether, si le joueur n'a jamais utilisé de netherportal NullPointer exception ServerPlayerEntity.java:702 (lié à moveToWorld, ServerPlayerEntity.java:613, Entity.java:2222)
    V les items disparaisses si le teleporter est dans un chunk non chargé
    - joueur sort du minecart coté client mais pas coté server: -> bug général pour rideable
    V minecart ne tp qu'une fois
    o teleportation detectable par observer
 */
