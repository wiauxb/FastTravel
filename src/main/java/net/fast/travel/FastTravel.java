package net.fast.travel;


import net.fabricmc.api.ModInitializer;
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
	- la commande "moveToWorld" ne semble pas adaptée aux items et blocks tombants (glitch à gogo)
	- [INCONSISTANT] probleme avec les ticking entity, lorsqu'on change de dimension (ou plutôt qu'on revient dans l'ancienne)
 	- lier un teleporteur dans un chunk non chargé ne sauve pas son tag
 */
