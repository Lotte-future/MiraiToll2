package github.zimoyin.cli.listen;

import github.zimoyin.cli.command.CommandObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainArgs extends Listener {
    public MainArgs(Class<?> cls, String[] args) throws IllegalAccessException {
        super(false);
        CommandObject command = new CommandObject(cls, null);
        List<String> arg = new ArrayList<String>();
        arg.add(command.getName());
        arg.addAll(Arrays.asList(args));
        //构建参数
        Object instance = getCommandInstance(command);
        setParameter(command, instance, arg.toArray(new String[0]));
        //执行命令
        command.execute(instance);
    }
}
