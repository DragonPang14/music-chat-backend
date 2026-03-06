package com.electrocardiogram.musicchatbackend.controller;

import com.electrocardiogram.musicchatbackend.entity.ChatMessage;
import com.electrocardiogram.musicchatbackend.entity.RoomMusic;
import com.electrocardiogram.musicchatbackend.service.ChatService;
import com.electrocardiogram.musicchatbackend.service.RoomService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MusicChatRestController {

    @Resource
    private ChatService chatService;
    @Resource
    private RoomService roomService;

    @GetMapping("/messages/{roomId}")
    public List<ChatMessage> getMessages(@PathVariable String roomId) {
        return chatService.getMessages(roomId);
    }

    @GetMapping("/room/{roomId}/music")
    public RoomMusic getRoomMusic(@PathVariable String roomId) {
        return roomService.getRoomMusic(roomId);
    }
}