package github.zimoyin.cli.command;

import github.zimoyin.cli.annotation.Shell;
import github.zimoyin.cli.uilt.FindClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CommandLoader {
    private static final CommandManager manager = CommandManager.getInstance();
    private final List<Class<?>> subCommandClasses = new ArrayList<Class<?>>();

    public CommandLoader(Class<?>... classes) {
        if (classes == null) classes = FindClass.getResultsToClasses().toArray(new Class[0]);
        //构建命令
        Arrays.stream(classes)
                .filter((Predicate<Class<?>>) aClass -> aClass.getAnnotation(Shell.class) != null)
                .forEach(this::buildCommandObject);
        //排序，让二、三级子命令最后被构建
        sorted();
        //构建子命令
        subCommandClasses.forEach(this::buildSubCommandObject);
    }

    private void sorted() {
        for (Class<?> sub : subCommandClasses) {
            Class<?> parent = sub.getAnnotation(Shell.class).parentCommand();
            if (subCommandClasses.contains(parent)) {
                int parIndex = subCommandClasses.indexOf(parent);
                int subIndex = subCommandClasses.indexOf(sub);

                if (subIndex < parIndex) {
                    subCommandClasses.set(parIndex, sub);
                    subCommandClasses.set(subIndex, parent);
                }
            }
        }
    }

    public void buildCommandObject(Class<?> cls) {
        //是否是子命令
        boolean sub = !cls.getAnnotation(Shell.class).parentCommand().equals(IShell.class);
        if (sub) {
            subCommandClasses.add(cls);
        } else {
            CommandObject object = new CommandObject(cls, null);
            manager.put(cls, object);
        }
    }

    public void buildSubCommandObject(Class<?> cls) {
        Class<?> parentCommand = cls.getAnnotation(Shell.class).parentCommand();
        //是否是子命令
        boolean sub = !parentCommand.equals(IShell.class);
        if (!sub) return;

        CommandObject parent = manager.get(parentCommand);
        CommandObject object = new CommandObject(cls, parent);
        parent.setChildCommand(object);
        manager.putSub(cls, object);
    }
}
