
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import github.zimoyin.application.command.chatgpt.api.cofig.ChatGPTConfig;
import github.zimoyin.application.command.chatgpt.api.cofig.ChatGPTConfig3;
import github.zimoyin.application.command.chatgpt.api.cofig.ChatGPTQuota;
import github.zimoyin.application.command.chatgpt.api.server.ChatAPI;
import github.zimoyin.application.dao.table.CreateTable;
import github.zimoyin.application.server.thesaurus.ThesaurusCenter;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;


import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
public class Main2 {
    public static void main(String[] args) throws NoSuchFieldException, InstantiationException, IllegalAccessException, IOException {

        System.out.println(ChatAPI.getInstance().chat2("你好", ChatAPI.Role.system));
    }


}
