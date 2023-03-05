package github.zimoyin.cli;

import github.zimoyin.cli.annotation.Shell;
import github.zimoyin.cli.command.CommandManager;
import github.zimoyin.cli.command.CommandObject;
import github.zimoyin.cli.listen.Listener;
import github.zimoyin.cli.listen.MainArgs;
import github.zimoyin.cli.test.ShellTest;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.BiConsumer;

public class MainCLI {
    public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException {
        run(null,null);
    }
    public static Listener run(Class<?> cls, String[] args) throws NoSuchMethodException, IllegalAccessException {
        if (cls != null && args != null && args.length > 0) new MainArgs(cls, args);
        CommandManager.initialize();
        return new Listener(System.in);
    }
}
