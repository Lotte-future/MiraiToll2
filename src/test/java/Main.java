import github.zimoyin.application.dao.table.CreateTable;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class Main {
    public static void main(String[] args) {
        new CreateTable().create();
    }


}
