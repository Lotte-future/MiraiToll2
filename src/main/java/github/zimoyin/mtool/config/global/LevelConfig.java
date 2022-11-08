package github.zimoyin.mtool.config.global;

import com.alibaba.fastjson.JSONObject;
import github.zimoyin.mtool.dao.JsonSerializeUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

/**
 * 配置命令等级管理器
 */
@Slf4j
public class LevelConfig {
//    private static final Logger logger = LoggerFactory.getLogger(LevelConfig.class);
    private static LevelConfig config;
    private LevelConfig.LevelConfigInfo info;

    private LevelConfig() throws Exception {
        String read = JsonSerializeUtil.read("data/config/gloval/level.json");
        //反序列化
        info = JSONObject.parseObject(read, LevelConfig.LevelConfigInfo.class);
    }

    public synchronized static LevelConfig getInstance() {
        if (config == null) {
            try {
                config = new LevelConfig();
            } catch (Exception e) {
                log.error("无法加载到登录信息文件", e);
            }
        }
        return config;
    }

    public LevelConfig.LevelConfigInfo getCommandConfigInfo(){
        return info;
    }

    @Data
    public static class LevelConfigInfo{
        //系统级权限
        private ArrayList<Long> systems;
        //权限列表
        private ArrayList<LevelConfigItem> levels;
    }

    @Data
    public static class LevelConfigItem{
        //群ID
        private Long groupid;
        //各级权限列表
        private ArrayList<Long> roots;
        private ArrayList<Long> firsts;
        private ArrayList<Long> seconds;
    }
}
