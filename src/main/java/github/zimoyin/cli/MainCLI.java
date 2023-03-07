package github.zimoyin.cli;

import github.zimoyin.cli.annotation.Shell;
import github.zimoyin.cli.command.CommandManager;
import github.zimoyin.cli.command.CommandObject;
import github.zimoyin.cli.listen.Listener;
import github.zimoyin.cli.listen.MainArgs;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.BiConsumer;

public class MainCLI {
    public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException {
        run(null,null);
    }

    /**
     *
     * @param cls 处理程序运行时 args 参数的类
     * @param args args 参数
     * @param classes 命令类列表，如果没有可以不填
     */
    public static Listener run(Class<?> cls, String[] args,Class<?>...classes) throws NoSuchMethodException, IllegalAccessException {
        if (cls != null && args != null && args.length > 0) new MainArgs(cls, args);
        CommandManager.initialize(classes);
        return new Listener(System.in);
    }
}
