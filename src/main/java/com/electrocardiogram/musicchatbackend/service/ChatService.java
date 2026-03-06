package com.electrocardiogram.musicchatbackend.service;

import com.electrocardiogram.musicchatbackend.entity.ChatMessage;
import com.electrocardiogram.musicchatbackend.repository.ChatMessageRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class ChatService {

    @Resource
    private ChatMessageRepository chatMessageRepository;
    private final Random random = new Random();

    public String generateAnonymousName() {
        String[] adjectives = {"快乐的", "悲伤的", "兴奋的", "平静的", "神秘的", "可爱的", "勇敢的", "聪明的"};
        String[] nouns = {"小猫", "小狗", "小鸟", "小鱼", "小熊", "小兔子", "小老虎", "小狮子"};
        return adjectives[random.nextInt(adjectives.length)] + nouns[random.nextInt(nouns.length)];
    }

    public void saveMessage(String username, String content, String roomId) {
        ChatMessage message = new ChatMessage();
        message.setUsername(username);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        message.setRoomId(roomId);
        chatMessageRepository.save(message);
    }

    public List<ChatMessage> getMessages(String roomId) {
        return chatMessageRepository.findByRoomIdOrderByTimestampDesc(roomId);
    }
}