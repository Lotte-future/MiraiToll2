package github.zimoyin.cli.test;

import github.zimoyin.cli.annotation.Shell;
import github.zimoyin.cli.command.IShell;

@Shell(value = "login",alias={})
public class ShellTest implements IShell {

    @Shell.Parameter("-user")
    private String user;

    @Override
    public void execute() {
        System.out.println("login: "+user);
    }
}
