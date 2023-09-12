package com.sunset.discjockey.block.BlockEntity.Controller.Audio;

import com.sunset.discjockey.client.audio.SpeakerSound;
import com.sunset.discjockey.util.MusicMisc.MusicFileManager;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.DistExecutor;

import java.util.concurrent.CompletableFuture;

public class ControllerAudio {
    public ControllerAudioManager manager;
    public String url;

    public SpeakerSound speakerSound = null;//only on level clientside

    public boolean isPlayingOnServer = false;

    public int elapsedTimeOnServer = 0;


    public ControllerAudio(ControllerAudioManager controllerAudioManager, String url) {
        this.manager = controllerAudioManager;
        this.url = url;
        if (this.manager.controller.getLevel().isClientSide()) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::setupOnClient);
        }
    }

    public CompoundTag getCompoundTag() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("url", this.url);
        compoundTag.putBoolean("isPlayingOnServer", this.isPlayingOnServer);
        compoundTag.putInt("elapsedTimeOnServer", this.elapsedTimeOnServer);
        return compoundTag;
    }

    public void writeCompoundTag(CompoundTag compoundTag) {
        this.url = compoundTag.getString("url");
        this.isPlayingOnServer = compoundTag.getBoolean("isPlayingOnServer");
        this.elapsedTimeOnServer = compoundTag.getInt("elapsedTimeOnServer");
        if (this.speakerSound != null) {
            this.speakerSound.isPlaying = this.isPlayingOnServer;
            this.speakerSound.elapsedTime.set(this.elapsedTimeOnServer);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void setupOnClient() {
        CompletableFuture.runAsync(() ->
                {
                    try {
                        if (!MusicFileManager.checkURL(this.url)) {
                            throw new Exception("Can't Play it!");
                        }
                        this.speakerSound = new SpeakerSound(this.manager.controller.getBlockPos(), this.url);
                        this.speakerSound.elapsedTime.set(this.elapsedTimeOnServer);
                        Minecraft.getInstance().getSoundManager().play(this.speakerSound);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                , Util.backgroundExecutor());
    }

    public void onLevelTick(TickEvent.LevelTickEvent event) {
        if (!event.level.isClientSide()) {
            if (this.isPlayingOnServer) {
                this.elapsedTimeOnServer++;
            }
            this.manager.controller.markDirty();
        }
    }
}
