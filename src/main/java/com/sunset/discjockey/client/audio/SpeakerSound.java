package com.sunset.discjockey.client.audio;

import com.sunset.discjockey.block.BlockEntity.BlockEntityDDJ400;
import com.sunset.discjockey.block.BlockEntity.Controller.AbstractController;
import com.sunset.discjockey.util.MusicMisc.MusicFileManager;
import com.sunset.discjockey.util.RegistryCollection.SoundEventCollection;
import com.sunset.discjockey.util.SpecialType.Property;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.concurrent.CompletableFuture;

//should be async
@OnlyIn(Dist.CLIENT)
public class SpeakerSound extends AbstractTickableSoundInstance {
    public Integer soundTime;

    public boolean isPlaying = false;

    public Property<Integer> elapsedTime = new Property<>(
            ov -> {
                return Math.toIntExact(1L * this.soundTime * this.fileAudioStream.offset / this.fileAudioStream.array.length);
            },
            (ov, nv) -> {
                this.fileAudioStream.offset = Math.toIntExact(1L * this.fileAudioStream.array.length * nv / this.soundTime);
            }
    );

    public FileAudioStream fileAudioStream;
    public final BlockPos pos;


    public SpeakerSound(BlockPos pos, String url) {
        super(SoundEventCollection.SOUND_EVENT.get(), SoundSource.RECORDS, SoundInstance.createUnseededRandom());
        this.fileAudioStream = new FileAudioStream(url);
        this.soundTime = MusicFileManager.getSongTime(url);
        //xyz determines where the sound will play
        this.x = pos.getX() + 0.5f;
        this.y = pos.getY() + 0.5f;
        this.z = pos.getZ() + 0.5f;
        this.pos = pos;
        this.volume = 0.3f;
    }

    @Override
    public void tick() {
        Level world = Minecraft.getInstance().level;
        BlockEntity blockEntity = world.getBlockEntity(this.pos);
        if (blockEntity instanceof AbstractController controller) {
            this.fileAudioStream.isPlaying = this.isPlaying;
        } else {
            this.stop();
        }
    }

    public void play() {
        this.isPlaying = true;
    }

    public void pause() {
        this.isPlaying = false;
    }

    public void destroy() {
        this.stop();
    }

    @Override
    public CompletableFuture<AudioStream> getStream(SoundBufferLibrary soundBuffers, Sound sound, boolean looping) {
        return CompletableFuture.supplyAsync(() -> {
            return this.fileAudioStream;
        }, Util.backgroundExecutor());
    }
}
