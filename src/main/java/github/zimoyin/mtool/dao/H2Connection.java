package github.zimoyin.mtool.dao;

import github.zimoyin.mtool.config.global.H2Config;
import lombok.extern.slf4j.Slf4j;
import org.h2.jdbcx.JdbcConnectionPool;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

/**
 * H2 数据库
 */
@Slf4j
public class H2Connection {
    private volatile static H2Connection INSTANCE;
    private final HashMap<Long, JdbcConnectionPool> Pools = new HashMap<>();
    private final JdbcConnectionPool Pool;

    private H2Connection() {
        H2Config.H2 h2 = H2Config.getInstance().getH2();
        Pool = JdbcConnectionPool.create(h2.getGlobal_jdbc(), h2.getUser(), h2.getPassword());
        log.info("JDBC 连接池创建成功；JDBC_URL:{}", h2.getGlobal_jdbc());
    }

    public static H2Connection getInstance() {
        if (INSTANCE == null) synchronized (H2Connection.class) {
            if (INSTANCE == null) INSTANCE = new H2Connection();
        }
        return INSTANCE;
    }

    private JdbcConnectionPool newPool(long botID) {
        H2Config.H2 h2 = H2Config.getInstance().getH2();
        String jdbc = h2.getJdbc() + "/" + botID;
        JdbcConnectionPool pool = JdbcConnectionPool.create(jdbc, h2.getUser(), h2.getPassword());
        Pools.put(botID, pool);
        log.info("JDBC 连接池创建成功；JDBC_URL:{}", jdbc);
        return pool;
    }

    public Statement getStatement(@NotNull Connection connection) throws SQLException {
        return new H2ExecuteProxyFactory(connection.createStatement()).getProxyInstance();
    }

    @Deprecated
    public Statement getStatement() throws SQLException {
        return new H2ExecuteProxyFactory(getConnection().createStatement()).getProxyInstance();
    }

    @Deprecated
    public Statement getStatement(long id) throws SQLException {
        return new H2ExecuteProxyFactory(getConnection(id).createStatement()).getProxyInstance();
    }

    public Connection getConnection() throws SQLException {
        Connection connection = Pool.getConnection();
        log.debug("Get JDBC Connection From ConnectionPool: {}", connection.getCatalog());
        return connection;
    }

    public Connection getConnection(long botID) throws SQLException {
        JdbcConnectionPool pool = Pools.get(botID);
        if (pool == null) pool = newPool(botID);
        Connection connection = pool.getConnection();
        log.debug("Get JDBC Connection From ConnectionPool: {}", connection.getCatalog());
        return connection;
    }

    public void execute(String sql) {
        Statement statement = null;
        Connection connection = null;

        try {
            connection = getConnection();
            statement = getStatement(connection);
            statement.execute(sql);
        } catch (Exception e) {
            log.error("执行 SQL 失败:{}", sql, e);
        } finally {
            close(statement, connection);
        }

    }

    public void execute(long botID, String sql) {
        Statement statement = null;
        Connection connection = null;

        try {
            connection = getConnection(botID);
            statement = getStatement(connection);
            statement.execute(sql);
        } catch (Exception e) {
            log.error("执行 SQL 失败:{}", sql, e);
        } finally {
            close(statement, connection);
        }
    }

    private void close(Statement statement, Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                log.error("[严重] 无法关闭的 JDBC 连接", e);
            }
        }

        if (statement != null) {
            try {
                statement.close();
            } catch (Exception e) {
                log.error("[严重] 无法关闭的 JDBC 流", e);
            }
        }
    }
}
