# 音乐聊天室后端 API 文档

## 1. 项目概述

音乐聊天室后端是一个基于 Spring Boot 的应用，提供了音乐播放控制、聊天消息管理和WebSocket实时通信功能。该系统支持用户在不同房间内进行聊天，并同步控制音乐播放。

## 2. 技术栈

- **后端框架**: Spring Boot
- **WebSocket**: Spring WebSocket
- **数据库**: H2 (内存数据库)
- **ORM**: JPA
- **依赖管理**: Maven
- **音乐搜索API**: 第三方音乐API (https://music-api.gdstudio.xyz/api.php)

## 3. API 端点

### 3.1 REST API 端点

| 方法 | 路径 | 描述 | 请求参数 | 响应 |
|------|------|------|----------|------|
| GET | /api/messages/{roomId} | 获取指定房间的聊天消息 | roomId: 房间ID | 聊天消息列表 |
| GET | /api/room/{roomId}/music | 获取指定房间的当前音乐信息 | roomId: 房间ID | 房间音乐信息 |

#### 3.1.1 GET /api/messages/{roomId}

**功能**: 获取指定房间的聊天消息，按时间倒序排列

**请求参数**:
- `roomId`: 房间ID (路径参数)

**响应示例**:
```json
[
  {
    "id": 1,
    "username": "快乐的小猫",
    "content": "大家好！",
    "timestamp": "2026-03-09T10:00:00",
    "roomId": "room1"
  },
  {
    "id": 2,
    "username": "悲伤的小狗",
    "content": "欢迎来到音乐聊天室！",
    "timestamp": "2026-03-09T10:01:00",
    "roomId": "room1"
  }
]
```

#### 3.1.2 GET /api/room/{roomId}/music

**功能**: 获取指定房间的当前音乐信息

**请求参数**:
- `roomId`: 房间ID (路径参数)

**响应示例**:
```json
{
  "roomId": "room1",
  "musicId": "123456",
  "musicName": "晴天",
  "artistName": "周杰伦",
  "musicUrl": "https://example.com/music/123456.mp3",
  "currentTime": 120000,
  "isPlaying": true,
  "volume": 50
}
```

### 3.2 WebSocket API 端点

| 消息路径 | 目标路径 | 描述 | 请求参数 | 响应 |
|---------|---------|------|----------|------|
| /connect | /queue/connection | 连接到聊天室 | roomId: 房间ID | 包含用户名和房间音乐信息 |
| /chat | /topic/chat/{roomId} | 发送聊天消息 | roomId: 房间ID<br>username: 用户名<br>content: 消息内容 | 包含用户名、内容和时间戳 |
| /search | /queue/search | 搜索音乐 | keyword: 关键词 | 音乐搜索结果 |
| /play | 无 (直接发送) | 播放音乐 | roomId: 房间ID<br>musicId: 音乐ID<br>musicName: 音乐名称<br>artistName: 艺术家名称<br>musicUrl: 音乐URL | 无直接响应，通过 /topic/music/{roomId} 广播 |
| /control | 无 (直接发送) | 控制音乐播放 | roomId: 房间ID<br>action: 操作类型 (play/pause/volume/time)<br>volume: 音量 (当action为volume时)<br>currentTime: 当前时间 (当action为time时) | 无直接响应，通过 /topic/music/{roomId} 广播 |

#### 3.2.1 /connect

**功能**: 连接到聊天室，获取随机生成的用户名和房间音乐信息

**请求参数**:
```json
{
  "roomId": "room1"
}
```

**响应示例**:
```json
{
  "username": "快乐的小猫",
  "roomMusic": {
    "roomId": "room1",
    "musicId": "123456",
    "musicName": "晴天",
    "artistName": "周杰伦",
    "musicUrl": "https://example.com/music/123456.mp3",
    "currentTime": 120000,
    "isPlaying": true,
    "volume": 50
  }
}
```

#### 3.2.2 /chat

**功能**: 发送聊天消息，消息会广播到指定房间的所有用户

**请求参数**:
```json
{
  "roomId": "room1",
  "username": "快乐的小猫",
  "content": "大家好！"
}
```

**响应示例**:
```json
{
  "username": "快乐的小猫",
  "content": "大家好！",
  "timestamp": 1710000000000
}
```

#### 3.2.3 /search

**功能**: 搜索音乐

**请求参数**:
```json
{
  "keyword": "周杰伦"
}
```

**响应示例**:
```json
{
  "results": [
    {
      "id": "123456",
      "name": "晴天",
      "artist": "周杰伦",
      "url": "https://example.com/music/123456.mp3"
    },
    {
      "id": "123457",
      "name": "七里香",
      "artist": "周杰伦",
      "url": "https://example.com/music/123457.mp3"
    }
  ]
}
```

#### 3.2.4 /play

**功能**: 播放指定音乐，会更新房间音乐信息并广播给所有用户

**请求参数**:
```json
{
  "roomId": "room1",
  "musicId": "123456",
  "musicName": "晴天",
  "artistName": "周杰伦",
  "musicUrl": "https://example.com/music/123456.mp3"
}
```

**广播示例** (/topic/music/room1):
```json
{
  "roomId": "room1",
  "musicId": "123456",
  "musicName": "晴天",
  "artistName": "周杰伦",
  "musicUrl": "https://example.com/music/123456.mp3",
  "currentTime": 0,
  "isPlaying": true,
  "volume": 50
}
```

#### 3.2.5 /control

**功能**: 控制音乐播放状态，包括播放/暂停、调整音量、设置当前播放时间

**请求参数** (播放/暂停):
```json
{
  "roomId": "room1",
  "action": "play"
}
```

**请求参数** (调整音量):
```json
{
  "roomId": "room1",
  "action": "volume",
  "volume": 70
}
```

**请求参数** (设置当前时间):
```json
{
  "roomId": "room1",
  "action": "time",
  "currentTime": 180000
}
```

**广播示例** (/topic/music/room1):
```json
{
  "roomId": "room1",
  "musicId": "123456",
  "musicName": "晴天",
  "artistName": "周杰伦",
  "musicUrl": "https://example.com/music/123456.mp3",
  "currentTime": 180000,
  "isPlaying": true,
  "volume": 70
}
```

## 4. 数据结构

### 4.1 ChatMessage

| 字段 | 类型 | 描述 |
|------|------|------|
| id | Long | 消息ID |
| username | String | 发送者用户名 |
| content | String | 消息内容 |
| timestamp | LocalDateTime | 发送时间 |
| roomId | String | 房间ID |

### 4.2 RoomMusic

| 字段 | 类型 | 描述 |
|------|------|------|
| roomId | String | 房间ID |
| musicId | String | 音乐ID |
| musicName | String | 音乐名称 |
| artistName | String | 艺术家名称 |
| musicUrl | String | 音乐URL |
| currentTime | long | 当前播放时间 (毫秒) |
| isPlaying | boolean | 是否正在播放 |
| volume | int | 音量 (0-100) |

### 4.3 MusicInfo

| 字段 | 类型 | 描述 |
|------|------|------|
| id | String | 音乐ID |
| name | String | 音乐名称 |
| artist | String | 艺术家名称 |
| url | String | 音乐URL |

## 5. 业务逻辑

### 5.1 聊天功能

- 用户连接时，系统会自动生成一个随机的匿名用户名（例如："快乐的小猫"）
- 聊天消息会被保存到数据库中，并按时间倒序排列
- 当用户发送聊天消息时，消息会通过WebSocket广播给房间内的所有用户

### 5.2 音乐功能

- 用户可以搜索音乐，系统会调用第三方音乐API获取搜索结果
- 用户可以选择播放音乐，系统会更新房间的当前音乐信息
- 音乐播放状态（播放/暂停、音量、当前时间）会实时同步到房间内的所有用户

### 5.3 房间管理

- 当用户首次访问一个房间时，系统会自动创建该房间的音乐信息记录
- 房间音乐信息会持久化到数据库中，确保重启后状态不丢失

## 6. 配置信息

- **应用名称**: music-chat-backend
- **数据库**: H2内存数据库
- **H2控制台**: http://localhost:8080/h2-console
- **音乐API地址**: https://music-api.gdstudio.xyz/api.php

## 7. 项目结构

```
music-chat-backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── electrocardiogram/
│   │   │           └── musicchatbackend/
│   │   │               ├── config/
│   │   │               │   ├── RestTemplateConfig.java
│   │   │               │   └── WebSocketConfig.java
│   │   │               ├── controller/
│   │   │               │   ├── MusicChatRestController.java
│   │   │               │   └── WebSocketController.java
│   │   │               ├── entity/
│   │   │               │   ├── ChatMessage.java
│   │   │               │   └── RoomMusic.java
│   │   │               ├── repository/
│   │   │               │   ├── ChatMessageRepository.java
│   │   │               │   └── RoomMusicRepository.java
│   │   │               ├── service/
│   │   │               │   ├── ChatService.java
│   │   │               │   ├── MusicService.java
│   │   │               │   └── RoomService.java
│   │   │               └── MusicChatBackendApplication.java
│   │   └── resources/
│   │       └── application.yaml
│   └── test/
│       └── java/
│           └── com/
│               └── electrocardiogram/
│                   └── musicchatbackend/
│                       └── MusicChatBackendApplicationTests.java
├── .gitattributes
├── .gitignore
├── HELP.md
├── mvnw
├── mvnw.cmd
└── pom.xml
```

## 8. 启动说明

1. 克隆项目到本地
2. 确保安装了JDK 11或更高版本
3. 运行 `mvn spring-boot:run` 启动项目
4. 项目默认运行在 http://localhost:8080

## 9. 注意事项

- 该项目使用H2内存数据库，重启后数据会丢失
- 音乐搜索功能依赖于第三方API，需要确保网络连接正常
- WebSocket连接需要前端配合实现，建议使用STOMP协议客户端