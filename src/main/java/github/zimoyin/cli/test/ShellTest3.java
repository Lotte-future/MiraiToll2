package github.zimoyin.cli.test;

import github.zimoyin.cli.annotation.Shell;

@Shell(value = "woc",parentCommand = ShellTest2.class)
public class ShellTest3 extends ShellTest2{
    @Shell.Parameter("-a")
    private boolean isLogin = false;

    @Shell.Main
    @Override
    public void execute() {
        super.execute();
    }
}
