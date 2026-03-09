package com.electrocardiogram.musicchatbackend.controller;

import com.electrocardiogram.musicchatbackend.entity.RoomMusic;
import com.electrocardiogram.musicchatbackend.service.ChatService;
import com.electrocardiogram.musicchatbackend.service.MusicService;
import com.electrocardiogram.musicchatbackend.service.RoomService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import jakarta.annotation.Resource;

import java.util.Map;

@Controller
public class WebSocketController {

    @Resource
    private ChatService chatService;
    @Resource
    private MusicService musicService;
    @Resource
    private RoomService roomService;
    @Resource
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/connect")
    @SendTo("/queue/connection")
    public Map<String, Object> connect(Map<String, String> message) {
        String roomId = message.get("roomId");
        String username = chatService.generateAnonymousName();
        RoomMusic roomMusic = roomService.getRoomMusic(roomId);
        
        return Map.of(
            "username", username,
            "roomMusic", roomMusic
        );
    }

    @MessageMapping("/chat")
    @SendTo("/topic/chat/{roomId}")
    public Map<String, Object> chat(Map<String, String> message) {
        String roomId = message.get("roomId");
        String username = message.get("username");
        String content = message.get("content");
        
        chatService.saveMessage(username, content, roomId);
        
        return Map.of(
            "username", username,
            "content", content,
            "timestamp", System.currentTimeMillis()
        );
    }

    @MessageMapping("/search")
    @SendTo("/queue/search")
    public Map<String, Object> search(Map<String, String> message) {
        String keyword = message.get("keyword");
        return Map.of("results", musicService.searchMusic(keyword));
    }

    @MessageMapping("/play")
    public void play(Map<String, Object> message) {
        String roomId = (String) message.get("roomId");
        String musicId = (String) message.get("musicId");
        String musicName = (String) message.get("musicName");
        String artistName = (String) message.get("artistName");
        String musicUrl = (String) message.get("musicUrl");
        
        roomService.updateRoomMusic(roomId, musicId, musicName, artistName, musicUrl);
        RoomMusic roomMusic = roomService.getRoomMusic(roomId);
        
        messagingTemplate.convertAndSend("/topic/music/" + roomId, roomMusic);
    }

    @MessageMapping("/control")
    public void control(Map<String, Object> message) {
        String roomId = (String) message.get("roomId");
        String action = (String) message.get("action");
        
        if ("play".equals(action) || "pause".equals(action)) {
            boolean isPlaying = "play".equals(action);
            roomService.updatePlayStatus(roomId, isPlaying);
        } else if ("volume".equals(action)) {
            int volume = (int) message.get("volume");
            roomService.updateVolume(roomId, volume);
        } else if ("time".equals(action)) {
            long playbackTime = (long) message.get("currentTime");
            roomService.updatePlaybackTime(roomId, playbackTime);
        }
        
        RoomMusic roomMusic = roomService.getRoomMusic(roomId);
        messagingTemplate.convertAndSend("/topic/music/" + roomId, roomMusic);
    }
}