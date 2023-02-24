package github.zimoyin.application;

import github.zimoyin.mtool.annotation.EventType;
import github.zimoyin.mtool.command.filter.FilterTable;
import github.zimoyin.mtool.config.global.LevelConfig;
import github.zimoyin.mtool.control.EventTask;
import github.zimoyin.mtool.control.ListenerObj;
import github.zimoyin.mtool.control.ListeningRegistration;
import github.zimoyin.mtool.run.RunMain;
import github.zimoyin.application.dao.table.CreateTable;
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
//        info.getLevels().forEach(System.out::println);
//        RunMain.initAll();
        createTable();
//        RunMain.initLevel();
        //网络图片上传
        //本地图片上传
        //byte的图片上传
        //流的图片上传

        //图片下载

        //文件同上
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
