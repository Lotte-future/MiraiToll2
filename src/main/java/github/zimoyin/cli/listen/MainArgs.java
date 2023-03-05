package github.zimoyin.cli.listen;

import github.zimoyin.cli.command.CommandObject;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class MainArgs extends Listener {
    private final CommandObject commandObject;
    private final Class<?> clazz;
    public MainArgs(Class<?> cls, String[] args) throws IllegalAccessException {
        super(false);
        this.clazz = cls;
        commandObject = new CommandObject(cls, null);
        List<String> arg = new ArrayList<String>();
        arg.add(commandObject.getName());
        arg.addAll(Arrays.asList(args));
        //构建参数
        Object instance = getCommandInstance(commandObject);
        setParameter(commandObject, instance, arg.toArray(new String[0]));
        //执行命令
        commandObject.execute(instance);
    }
}
