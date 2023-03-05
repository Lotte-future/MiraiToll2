package github.zimoyin.cli.shell;

import github.zimoyin.cli.annotation.Shell;
import github.zimoyin.cli.command.CommandManager;
import github.zimoyin.cli.command.CommandObject;
import github.zimoyin.cli.command.IShell;

import java.util.List;
import java.util.function.BiConsumer;

@Shell(value = "help",description = "帮助",help = "help 其他命令主语(只能是主语，子命令无法使用)")
public class CommandHelp implements IShell {
    @Shell.Parameter("-name")
    private String command = null;
    @Override
    public void execute() {
        CommandManager manager = CommandManager.getInstance();
        if (command == null) {
            StringBuffer buffer = new StringBuffer("有关某个命令的详细信息，请键入 HELP 命令名\n");
            manager.forEach((aClass, commandObject) -> {
                String description = commandObject.getDescription();
                String name = commandObject.getName();
                buffer.append(name).append("\t\t\t\t").append(description).append("\n");
            });
            buffer.append("有关工具的详细信息，请参阅联机帮助中的命令行参考。");
            System.out.println(buffer);
            return;
        }
        System.out.println("未实现");
    }
}
