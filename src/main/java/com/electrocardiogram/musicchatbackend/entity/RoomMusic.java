package com.electrocardiogram.musicchatbackend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class RoomMusic {

    @Id
    private String roomId;

    private String musicId;
    private String musicName;
    private String artistName;
    private String musicUrl;
    private long currentTime;
    private boolean isPlaying;
    private int volume;
}