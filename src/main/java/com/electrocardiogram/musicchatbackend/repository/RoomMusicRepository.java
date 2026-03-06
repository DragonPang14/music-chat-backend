package com.electrocardiogram.musicchatbackend.repository;

import com.electrocardiogram.musicchatbackend.entity.RoomMusic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomMusicRepository extends JpaRepository<RoomMusic, String> {
}