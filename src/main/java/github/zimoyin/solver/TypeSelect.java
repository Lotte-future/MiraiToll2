package github.zimoyin.solver;

import github.zimoyin.solver.gui.LoginSolverGuiRun;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TypeSelect {
   private TypeSelect(){
   }

    /**
     * 根据系统选择gui或cli
     */
   public static void open(){
       LoginSolverGuiRun.getInstance();
   }
}
