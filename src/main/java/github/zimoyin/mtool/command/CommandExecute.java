package github.zimoyin.mtool.command;

import github.zimoyin.mtool.config.global.CommandConfig;
import github.zimoyin.mtool.uilt.message.MessageData;
import net.mamoe.mirai.event.events.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 命令执行器
 */
public class CommandExecute {
    private CommandData commandData;
    private CommandSet<String, CommandObj> commandSet = CommandSet.getInstance();
    private CommandObj commandObj;
    private Logger logger = LoggerFactory.getLogger(CommandExecute.class);
    private MessageEvent event;

    public CommandExecute(CommandData commandData) {
        this.commandData = commandData;
        this.commandObj = getCommandObject();
        event = commandData.getEvent();
    }

    /**
     * 执行 命令封装
     * @return
     */
    public boolean execute() {
        try {
            //执行命令
            if (CommandConfig.getInstance().getCommandConfigInfo().isAT()){
                if (MessageData.getAt(event) != null) commandObj.execute(event);
                else logger.warn("405 该命令未能在AT机器人的情况下执行，已经被终止执行此命令：{}", commandData.getHeader());
            }else {
                commandObj.execute(event);
            }
        } catch (Exception e) {
            logger.warn("捕获到命令实例类抛出未处理异常");
            logger.error("500 命令执行失败：{}",commandData.getHeader(),e);
            event.getSubject().sendMessage("服务器内部错误：命令执行失败");
            return false;
        }
        return true;
    }

    private CommandObj getCommandObject() {
        return commandSet.get(commandData);
    }
}
