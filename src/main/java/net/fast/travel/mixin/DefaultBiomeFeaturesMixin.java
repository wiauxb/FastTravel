package net.fast.travel.mixin;

import net.fast.travel.FastTravel;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DefaultBiomeFeatures.class)
public class DefaultBiomeFeaturesMixin {

    @Inject(method = "addPlainsFeatures(Lnet/minecraft/world/biome/GenerationSettings$Builder;)V", at = @At("TAIL"))
    private static void addPlainsFeatures(GenerationSettings.Builder builder, CallbackInfo ci) {
        builder.structureFeature(FastTravel.TELEPORTER_TEMPLE_CONFIGURED);
    }

    @Inject(method = "addSwampFeatures(Lnet/minecraft/world/biome/GenerationSettings$Builder;)V", at = @At("TAIL"))
    private static void addSwampFeatures(GenerationSettings.Builder builder, CallbackInfo ci) {
        builder.structureFeature(FastTravel.TELEPORTER_TEMPLE_CONFIGURED);
    }

    @Inject(method = "addMushroomFieldsFeatures(Lnet/minecraft/world/biome/GenerationSettings$Builder;)V", at = @At("TAIL"))
    private static void addMushroomFieldsFeatures(GenerationSettings.Builder builder, CallbackInfo ci) {
        builder.structureFeature(FastTravel.TELEPORTER_TEMPLE_CONFIGURED);
    }

    @Inject(method = "addDesertFeatures(Lnet/minecraft/world/biome/GenerationSettings$Builder;)V", at = @At("TAIL"))
    private static void addDesertFeatures(GenerationSettings.Builder builder, CallbackInfo ci) {
        builder.structureFeature(FastTravel.TELEPORTER_TEMPLE_CONFIGURED);
    }

    @Inject(method = "addGiantTaigaGrass(Lnet/minecraft/world/biome/GenerationSettings$Builder;)V", at = @At("TAIL"))
    private static void addGiantTaigaGrass(GenerationSettings.Builder builder, CallbackInfo ci) {
        builder.structureFeature(FastTravel.TELEPORTER_TEMPLE_CONFIGURED);
    }

}
