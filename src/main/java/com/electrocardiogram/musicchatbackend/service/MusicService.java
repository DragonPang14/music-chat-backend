package com.electrocardiogram.musicchatbackend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;

import java.util.ArrayList;
import java.util.List;

@Service
public class MusicService {

    @Resource
    private RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${music.api.url}")
    private String API_URL;

    public List<MusicInfo> searchMusic(String keyword) {
        String url = API_URL + "?type=search&name=" + keyword;
        String response = restTemplate.getForObject(url, String.class);
        List<MusicInfo> musicList = new ArrayList<>();

        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode data = root.get("data");
            if (data != null && data.isArray()) {
                for (JsonNode item : data) {
                    MusicInfo musicInfo = new MusicInfo();
                    musicInfo.setId(item.get("id").asText());
                    musicInfo.setName(item.get("name").asText());
                    musicInfo.setArtist(item.get("artist").asText());
                    musicInfo.setUrl(item.get("url").asText());
                    musicList.add(musicInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return musicList;
    }

    public static class MusicInfo {
        private String id;
        private String name;
        private String artist;
        private String url;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getArtist() {
            return artist;
        }

        public void setArtist(String artist) {
            this.artist = artist;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}