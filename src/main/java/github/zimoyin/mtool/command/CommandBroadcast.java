package github.zimoyin.mtool.command;

import github.zimoyin.mtool.annotation.Controller;
import github.zimoyin.mtool.annotation.EventType;
import github.zimoyin.mtool.annotation.ThreadSpace;
import github.zimoyin.mtool.command.filter.CommandFilter;
import github.zimoyin.mtool.config.global.CommandConfig;
import github.zimoyin.mtool.uilt.NewThreadPoolUtils;
import net.mamoe.mirai.event.events.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

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
        try {
            broadcast(event);
        }catch (Exception e){
            logger.error("无法对命令进行广播",e);
        }
    }

    private void broadcast(MessageEvent event){
        CommandConfig.CommandConfigInfo info = CommandConfig.getInstance().getCommandConfigInfo();
        //命令信息
        CommandData commandData = new CommandData(event);
        if (!commandData.isCommand()) return;//如果该事件没有包含命令主语则退出方法体
        //命令对象: 命令主语和参数之间有空格
        CommandObj commandObj = CommandSet.getInstance().get(commandData);
        if (commandObj == null && info.isSpace()) {
            logger.warn("无法找到命令: {}", commandData.getHeader());
            return;
        }
        if (commandObj == null) {
            List<String> commandList = CommandSet.getInstance().keySet().stream().filter(name -> commandData.getHeader().indexOf(name) == 0).collect(Collectors.toList());
            if (commandList.size() > 1) logger.warn("匹配到多个符合该{} 命令的命令对象", commandData.getHeader());
            if (commandList.size() == 0) logger.warn("无法找到命令的执行方法: {}", commandData.getHeader());
            if (commandList.size() == 0) return;
            String header = commandList.get(0);
            String text = commandData.getTextMessage().replaceFirst(info.getCommandPrefix()+header,info.getCommandPrefix()+header+" ");
            commandData.setHeader(header);
            commandData.initParams(CommandParsing.commandParsing(text));
            commandObj = CommandSet.getInstance().get(commandData);
        }
        if (commandObj == null) {
            logger.warn("无法找到命令: {}", commandData.getHeader());
            return;
        }
        //执行过滤器
        boolean filter = new CommandFilter(commandData).execute();
        if (!filter) {
            logger.info("拦截命令：{}", commandData.getTextMessage());
            return;
        }
        logger.debug("执行命令：{}", commandData.getTextMessage().replace("\n", "\\n"));

        //如果有这注解就在线程内运行
        ThreadSpace annotation1 = commandObj.getCommandClass().getAnnotation(ThreadSpace.class);
        ThreadSpace annotation = commandObj.getMethod().getAnnotation(ThreadSpace.class);
        //执行
        if (annotation == null && annotation1 == null) execute(commandData);
        else NewThreadPoolUtils.getInstance().execute(() -> execute(commandData));
    }

    private void execute(CommandData commandData) {
        new CommandExecute(commandData).execute();
    }
}
