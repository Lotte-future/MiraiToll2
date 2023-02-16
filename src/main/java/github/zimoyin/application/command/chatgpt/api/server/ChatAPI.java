package github.zimoyin.application.command.chatgpt.api.server;

import github.zimoyin.application.command.chatgpt.api.cofig.ChatGPTConfig;
import github.zimoyin.application.command.chatgpt.api.cofig.ChatGPTQuota;
import github.zimoyin.mtool.uilt.net.httpclient.HttpClientResult;
import github.zimoyin.mtool.uilt.net.httpclient.HttpClientUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
public class ChatAPI {
    @Getter
    private final ChatGPTConfig config = new ChatGPTConfig();
    @Getter
    private final ChatGPTQuota info = new ChatGPTQuota();
    private final String URL = "https://api.openai.com/v1/completions";
    private volatile static ChatAPI INSTANCE;
    private final HashMap<String, ArrayList<String>> caches = new HashMap<String, ArrayList<String>>();
    @Getter
    private final HashMap<String, Integer> cachesCount = new HashMap<>();

    private ChatAPI() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                log.trace("保存配置文件");
                try {
                    info.save();
                } catch (IOException e) {
                    log.warn("无法保存GPT信息配置文件",e);
                }

                try {
                    config.save();
                } catch (IOException e) {
                    log.warn("无法保存GPT配置文件",e);
                }
            }
        }, 20*1000 , 20*1000);
    }

    public static ChatAPI getInstance() {
        if (INSTANCE == null) synchronized (ChatAPI.class) {
            if (INSTANCE == null) INSTANCE = new ChatAPI();
        }
        return INSTANCE;
    }

    private HashMap<String, String> buildHeaders() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("Content-Type", "application/json;charset=utf-8");
        hashMap.put("Authorization", "Bearer " + config.getKey());
        return hashMap;
    }

    /**
     * @param name 会话ID
     * @return true 则是符合要求
     */
    public boolean preChat(String name) {
        ArrayList<String> list = caches.get(name);
        if (list == null) return true;
        if (list.size() >= 10) return false;
        return cachesCount.get(name) < 20;
    }

    public String chat(String text, String name) throws IOException {
        //计数
        cachesCount.put(name, cachesCount.getOrDefault(name, 1));
        info.add();
        //配置
        UUID uuid = UUID.randomUUID();
        ChatGPTConfig copyConfig = copyChatGPTConfig();
        copyConfig.setPrompt(text);
        copyConfig.setUser(name);
        log.debug("GPT({}) [{}]-> {}", name, uuid, copyConfig.toJson());
        copyConfig.setPrompt(getList(text, name).toString());
        //构建参数体
        StringEntity stringEntity = new StringEntity(copyConfig.toJson().toString(), StandardCharsets.UTF_8);
        stringEntity.setContentType("application/json;charset=utf-8");
        stringEntity.setContentEncoding("UTF-8");
//        System.exit(0);
        //响应
        HttpClientResult httpClientResult = HttpClientUtils.doPost(URL, buildHeaders(), null, stringEntity);
        String content = httpClientResult.getContent().trim();
        log.debug("GPT({}) [{}]<- {}", name, uuid, content);
        return content;
    }

    public ChatGPTConfig getChatGPTConfig() {
        return config;
    }

    public ChatGPTConfig copyChatGPTConfig() {
        return config.clone();
    }

    /**
     * 重置缓存
     *
     * @return 缓存内容
     */
    public ArrayList<String> reset(String name) {
        return caches.remove(name);
    }

    private ArrayList<String> getList(String text, String name) {
        ArrayList<String> list = caches.getOrDefault(name, new ArrayList<String>());
        list.add(text);
        caches.put(name, list);
        return list;
    }
}
