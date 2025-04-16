package Core.Utils;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.*;
import java.util.List;
import java.util.Map;

import static Core.Utils.Config.*;

public class DBUtil
{
    private static final Logger logger = LogManager.getLogger(DBUtil.class);

    public static Connection conn;

    /***
     *
     * @param url
     * @param userName
     * @param password
     * @return
     * @throws SQLException
     */
    public static Connection getConnection(String url, String userName, String password)
    {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
        dataSource.setInitialSize(DB_POOL_INITIAL_SIZE);
        dataSource.setMaxActive(DB_POOL_MAX_ACTIVE);
        dataSource.setMinIdle(DB_POOL_MIN_IDLE);
        dataSource.setMaxWait(DB_POOL_MAX_WAIT);
        try
        {
            conn = dataSource.getConnection();
            return conn;
        }
        catch (Exception e)
        {
            logger.error("connect DB failed:", e);
        }
        return conn;
    }

    /***
     * @param con
     */
    public static void close(Connection con)
    {
        try
        {
            if (con != null)
            {
                con.close();
            }
        }
        catch (SQLException e)
        {
            logger.error("close DB connection failed: ", e);
        }
    }


    /***
     * execute query
     * @param connection
     * @param querySQL
     * @throws SQLException
     */

    public static List<Map<String, Object>> executeQuery(Connection connection, String querySQL)
    {
        QueryRunner queryRunner = new QueryRunner();
        List<Map<String, Object>> resultList = null;
        try
        {
            resultList = queryRunner.query(connection, querySQL, new MapListHandler());

        }
        catch (Exception e)
        {
            logger.error("execute query sql failed: ", e);
        }
        finally
        {
            close(connection);
        }
        return resultList;
    }

    /***
     * execute update
     * @param updateSQL
     * @throws SQLException
     */
    public static int executeUpdate(Connection connection, String updateSQL)
    {
        QueryRunner queryRunner = new QueryRunner();
        int result = 0;
        try
        {
            result = queryRunner.update(connection, updateSQL);
        }
        catch (SQLException e)
        {
            logger.error("execute update sql failed: ", e);
        }
        finally
        {
            close(connection);
        }
        return result;
    }
}
