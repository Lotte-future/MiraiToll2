package github.zimoyin.cli.test;

import github.zimoyin.cli.annotation.Shell;

@Shell(value = "zimo",parentCommand = ShellTest.class)
public class ShellTest2 extends ShellTest{
    @Shell.Parameter("-a")
    private boolean isLogin = false;

    @Shell.Main
    @Override
    public void execute() {
        super.execute();
    }
}
