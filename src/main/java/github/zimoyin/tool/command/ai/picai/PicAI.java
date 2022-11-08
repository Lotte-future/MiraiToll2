package github.zimoyin.tool.command.ai.picai;

import github.zimoyin.mtool.annotation.Command;
import github.zimoyin.mtool.annotation.CommandClass;
import github.zimoyin.mtool.annotation.ThreadSpace;
import github.zimoyin.mtool.command.CommandData;
import github.zimoyin.tool.server.ai.pic.PicAIServer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CommandClass
public class PicAI {
    @Command("pic")
    @ThreadSpace
    public void pic(CommandData commandData){
        PicAIServer.getInstance().add(commandData);
    }
}
