package github.zimoyin.mtool.config.global;


import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 机器人的配置信息,加载配置文件
 * 加载resources/config/config.json(请在里面注册你的配置信息模板)
 */
@Data
@Slf4j
public  class Config {
   public static void init(){
       try {
           InitConfig.init();
       } catch (IOException e) {
           throw new RuntimeException(e);
       }
   }

    private ArrayList<JSONObject> config;
}
