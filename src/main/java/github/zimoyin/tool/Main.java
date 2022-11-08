package github.zimoyin.tool;

import github.zimoyin.mtool.run.RunMain;
import github.zimoyin.tool.dao.table.CreateTable;
import github.zimoyin.tool.dao.table.SelectTable;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;

import java.sql.SQLException;

@Slf4j
public class Main extends RunMain {
    public static void main(String[] args) {
        RunMain.run();
        createTable();
        cache();
    }

    /**
     * 创建表
     */
    public static void createTable(){
        for (Bot bot : Bot.getInstances()) {
            new CreateTable().create(bot.getId());
        }
    }
    /**
     * 读取缓存
     */
    public static void cache(){
        for (Bot bot : Bot.getInstances()) {
            try {
                new SelectTable().select(bot.getId());
            } catch (SQLException e) {
                log.error("无法获取到数据库数据",e);
            }
        }
    }
}
