package me.duncanruns.icarus.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.duncanruns.icarus.Icarus;
import net.minecraft.command.arguments.ItemStackArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

    public ServerPlayerEntityMixin(World world, BlockPos blockPos, GameProfile gameProfile) {
        super(world, blockPos, gameProfile);
    }

    @Shadow
    @Final
    private ServerStatHandler statHandler;

    private ItemStack itemStackFromString(String string,int count) throws CommandSyntaxException{
        return new ItemStackArgumentType()
                    .parse(new StringReader(string)).createStack(count, false);
    }

    @Inject(at = @At("TAIL"), method = "<init>")
    private void init(CallbackInfo info) throws CommandSyntaxException {
        if (statHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.PLAY_ONE_MINUTE)) == 0) {
            Icarus.log(Level.INFO, "New player detected, activating Icarus.");
            ItemStack wings = itemStackFromString("minecraft:elytra{Unbreakable:1b}",1);
            ItemStack rockets = itemStackFromString("minecraft:firework_rocket{Fireworks:{Flight:3b}}",64);

            inventory.armor.set(2, wings);
            inventory.main.set(0, rockets);
        }
    }
}
