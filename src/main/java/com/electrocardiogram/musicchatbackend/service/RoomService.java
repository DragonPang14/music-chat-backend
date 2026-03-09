package com.electrocardiogram.musicchatbackend.service;

import com.electrocardiogram.musicchatbackend.entity.RoomMusic;
import com.electrocardiogram.musicchatbackend.repository.RoomMusicRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class RoomService {

    @Resource
    private RoomMusicRepository roomMusicRepository;

    public RoomMusic getRoomMusic(String roomId) {
        return roomMusicRepository.findById(roomId).orElseGet(() -> {
            RoomMusic roomMusic = new RoomMusic();
            roomMusic.setRoomId(roomId);
            roomMusic.setMusicId("");
            roomMusic.setMusicName("");
            roomMusic.setArtistName("");
            roomMusic.setMusicUrl("");
            roomMusic.setPlaybackTime(0);
            roomMusic.setPlaying(false);
            roomMusic.setVolume(50);
            return roomMusicRepository.save(roomMusic);
        });
    }

    public void updateRoomMusic(String roomId, String musicId, String musicName, String artistName, String musicUrl) {
        RoomMusic roomMusic = getRoomMusic(roomId);
        roomMusic.setMusicId(musicId);
        roomMusic.setMusicName(musicName);
        roomMusic.setArtistName(artistName);
        roomMusic.setMusicUrl(musicUrl);
        roomMusic.setPlaybackTime(0);
        roomMusic.setPlaying(true);
        roomMusicRepository.save(roomMusic);
    }

    public void updatePlayStatus(String roomId, boolean isPlaying) {
        RoomMusic roomMusic = getRoomMusic(roomId);
        roomMusic.setPlaying(isPlaying);
        roomMusicRepository.save(roomMusic);
    }

    public void updatePlaybackTime(String roomId, long playbackTime) {
        RoomMusic roomMusic = getRoomMusic(roomId);
        roomMusic.setPlaybackTime(playbackTime);
        roomMusicRepository.save(roomMusic);
    }

    public void updateVolume(String roomId, int volume) {
        RoomMusic roomMusic = getRoomMusic(roomId);
        roomMusic.setVolume(volume);
        roomMusicRepository.save(roomMusic);
    }
}