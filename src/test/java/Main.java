import com.alibaba.fastjson2.JSONObject;
import github.zimoyin.application.command.chatgpt.api.cofig.ChatGPTConfig;
import github.zimoyin.application.command.chatgpt.api.cofig.ChatGPTQuota;
import github.zimoyin.application.command.chatgpt.api.server.ChatAPI;
import github.zimoyin.mtool.command.filter.AbstractFilter;
import github.zimoyin.mtool.command.filter.GlobalFilterInitOrExecute;
import github.zimoyin.mtool.command.filter.impl.LevelFilter;
import github.zimoyin.solver.gui.LoginSolverGuiRun;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.function.BiConsumer;


@Slf4j
public class Main {
    public static void main(String[] args) throws IOException, NoSuchFieldException, InstantiationException, IllegalAccessException {

        ChatGPTQuota quota = new ChatGPTQuota();
        quota.add();
        quota.save();
        System.out.println(quota);
    }


}
