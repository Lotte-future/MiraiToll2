package github.zimoyin.mtool.login;

import github.zimoyin.mtool.config.BotConfigurationImpl;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.utils.BotConfiguration;


public class Login {
    public static void login(long id, String password){
        //创建机器人
        Bot bot = BotFactory.INSTANCE.newBot(id, password,new BotConfigurationImpl(id, true));
        //登录bot
        bot.login();
    }

    public static void login(long id, String password,boolean isShareDeviceInfo){
        //创建机器人
        Bot bot = BotFactory.INSTANCE.newBot(id, password,new BotConfigurationImpl(id,isShareDeviceInfo));
        //登录bot
        bot.login();
    }

    public static void login(long id, String password, BotConfiguration configuration){
        if (configuration == null) {
            login(id, password);
            return;
        }
        //创建机器人
        Bot bot = BotFactory.INSTANCE.newBot(id, password,configuration);
        //登录bot
        bot.login();
    }

}
