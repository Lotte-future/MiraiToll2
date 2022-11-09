package github.zimoyin.application;

import github.zimoyin.mtool.run.RunMain;
import github.zimoyin.application.dao.table.CreateTable;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;

@Slf4j
public class Main extends RunMain {
    public static void main(String[] args) {
        RunMain.run();
        createTable();
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
