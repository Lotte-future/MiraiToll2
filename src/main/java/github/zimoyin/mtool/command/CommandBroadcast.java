package github.zimoyin.mtool.command;

import github.zimoyin.mtool.annotation.Controller;
import github.zimoyin.mtool.annotation.EventType;
import github.zimoyin.mtool.annotation.ThreadSpace;
import github.zimoyin.mtool.command.filter.CommandFilter;
import github.zimoyin.mtool.uilt.NewThreadPoolUtils;
import net.mamoe.mirai.event.events.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 命令广播：监听信息事件发生，之后交给 CommandExecute 执行
 */
@Controller
public class CommandBroadcast {
    private final CommandSet<String, CommandObj> set = CommandSet.getInstance();
    private final Logger logger = LoggerFactory.getLogger(CommandBroadcast.class);

    /**
     * 当发生 MessageEvent 事件时则对该事件进行广播
     *
     * @param event
     */
    @EventType
    public void MessageEventBroadcast(MessageEvent event) {
        //命令信息
        CommandData commandData = new CommandData(event);
        if (!commandData.isCommand())return;//如果该事件没有包含命令主语则退出方法体
        //命令对象
        CommandObj commandObj = CommandSet.getInstance().get(commandData);
        if (commandObj == null) {
            logger.warn("无法找到命令: {}",commandData.getHeader());
            return;
        }
        //执行过滤器
        boolean filter = new CommandFilter(commandData).execute();
        if (!filter) {
            logger.info("拦截命令：{}",commandData.getTextMessage());
            return;
        }
        logger.debug("执行命令：{}",commandData.getTextMessage().replace("\n","\\n"));

        //如果有这注解就在线程内运行
        ThreadSpace annotation1 = commandObj.getCommandClass().getAnnotation(ThreadSpace.class);
        ThreadSpace annotation = commandObj.getMethod().getAnnotation(ThreadSpace.class);
        //执行
        if (annotation == null && annotation1 == null) execute(commandData);
        else NewThreadPoolUtils.getInstance().execute(() -> execute(commandData));
    }

    private boolean execute(CommandData commandData){
        return new CommandExecute(commandData).execute();
    }
}
