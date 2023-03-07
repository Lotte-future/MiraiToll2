package github.zimoyin.application;

import github.zimoyin.cli.MainCLI;
import github.zimoyin.cli.listen.Listener;
import github.zimoyin.mtool.annotation.EventType;
import github.zimoyin.mtool.command.filter.FilterTable;
import github.zimoyin.mtool.config.global.LevelConfig;
import github.zimoyin.mtool.control.EventTask;
import github.zimoyin.mtool.control.ListenerObj;
import github.zimoyin.mtool.control.ListeningRegistration;
import github.zimoyin.mtool.run.RunMain;
import github.zimoyin.application.dao.table.CreateTable;
import github.zimoyin.mtool.uilt.FindClass;
import kotlin.jvm.functions.Function1;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.BotEvent;
import net.mamoe.mirai.event.events.BotOnlineEvent;
import net.mamoe.mirai.event.events.MessageEvent;

import java.util.function.Consumer;

@Slf4j
public class Main extends RunMain {
    public static void main(String[] args) {
        RunMain.run();
        createTable();
        try {
            Listener listener = MainCLI.run(null, args, FindClass.getResultsToClasses().toArray(new Class[0]));
            listener.setPrefix("");
            System.out.println("控制台启动成功,输入 help 来查看更多命令。");
            listener.run();
        } catch (Exception e) {
            log.error("CLI ERROR" , e);
        }
    }

    /**
     * 创建表
     */
    public static void createTable(){
        for (Bot bot : Bot.getInstances()) {
            new CreateTable().create(bot.getId());
        }
    }
}
