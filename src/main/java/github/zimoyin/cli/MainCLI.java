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
        System.out.println(Arrays.toString(args));
        new MainArgs(ShellTest.class,args);
        System.exit(0);
        CommandManager.initialize().forEach((aClass, commandObject) -> System.out.println(commandObject));
        Listener listener = new Listener(System.in);
    }
}
