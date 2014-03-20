package com.baton.syncserver.infrastructure.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.baton.syncserver.infrastructure.utility.ConfigHelper;

/**
 * The basic database access class
 * @author j66969 2011-4-1
 * @see
 * @since 1.0 2011-4-1
 */
public class BaseDBAccess
{
    private static Logger logger = Logger.getLogger(BaseDBAccess.class);

    private static String dbDRIVER;

    private static String dbURL;

    private static String dbUSER;

    private static String dbPASSWORD;

    /**
     * the connection pool
     */
    private static ConnectionPool connectionPool;

    static
    {
        try
        {
            dbDRIVER = ConfigHelper.getConfig("jdbc.driver");
            dbURL = ConfigHelper.getConfig("jdbc.url");
            dbUSER = ConfigHelper.getConfig("jdbc.user");
            dbPASSWORD = ConfigHelper.getConfig("jdbc.password");
            if (dbDRIVER == null || dbURL == null || dbUSER == null
                    || dbPASSWORD == null)
            {
                logger.error("Initialize connection pool error." + "url = "
                        + dbURL + "user = " + dbUSER + "password = "
                        + dbPASSWORD);

                throw new RuntimeException();
            }

            connectionPool = new ConnectionPool(dbDRIVER, dbURL, dbUSER,
                    dbPASSWORD);
            connectionPool.createPool();
        }
        catch (Exception e)
        {
            logger.error("Initialize connection pool error." + "url = "
                    + dbURL + "user = " + dbUSER + "password = "
                    + dbPASSWORD + e, e);
        }
    }

    /**
     * get a db connection form the pool
     * @return db connection
     */
    public static Connection getConnection()
    {
        if (connectionPool == null)
        {
            return null;
        }

        Connection con=null;
		while(true)
		{
			con = connectionPool.getConnection();
			try {
				if(con != null&&!con.isClosed())
					break;
				logger.error("This connnection is broken "+con.getCatalog());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        return con;
    }

    /**
     * release the connection from the pool
     * @param connection the connection want to release
     */
    public static void releaseConnection(Connection connection)
    {
        if (connectionPool == null)
        {
            logger.error("Release connection failed,connection pool is null");
            return;
        }
        connectionPool.releaseConnection(connection);
    }

    /**
     * build up the value statement
     * @param values 
     * @return sql value
     */
    public static String getSqlValuesString(String[] values)
    {
        StringBuffer sb = new StringBuffer();
        sb.append("(");
        for (int i = 0; i < values.length; i++)
        {
            sb.append("'");
            sb.append(values[i]);
            sb.append("'");
            if (i < values.length - 1)
            {
                sb.append(",");
            }
        }
        sb.append(")");
        return sb.toString();
    }
    
    public static String getSqlAndWhereString(String[] field, String[] values)
    {
    	StringBuffer sb = new StringBuffer();
        sb.append("where ");
        for(int i=0; i< field.length; i++)
        {
        	sb.append(field[i]);
        	sb.append("='");
        	sb.append(values[i]);
        	sb.append("'");
        	if (i < values.length - 1)
            {
                sb.append(" and ");
            }
        }
        return sb.toString();
    }

    /**
     * run one sql statement without parameter
     * @param sql 
     * @return whether sql run success or not
     */
    public static boolean runSQL(String sql)
    {
        return runSQL(sql,null);
    }
    
    /**
     * 
     * @param sql sql语句
     * @return 是否执行成功
     */
    public static boolean runSQL(String sql,List<String> colValues)
    {
        Connection connection = getConnection();
        PreparedStatement pState=null;
        try
        {
            checkConnection(connection);
            pState = connection.prepareStatement(sql);
            if(colValues!=null&&colValues.size()!=0)
            {
	            for(int i=0;i<colValues.size();i++)
	            {
	            	pState.setString(i+1, colValues.get(i));
	            }
            }
            pState.execute();
//            connection.commit();
            if (logger.isDebugEnabled())
            {
                logger.debug("Run sql success:" + sql);
            }
            
        }
        catch (SQLException e)
        {
            logger.error("Run sql exception:" + sql, e);
            //logger.error(e);
            return false;
        }
        finally
        {
            if (null != pState)
            {
                try
                {
                	pState.close();
                }
                catch (SQLException e)
                {
                    logger.error(e, e);
                } 
            }
            
            releaseConnection(connection);
        }
        return true;
    }

    /**
     * 执行只返回一条记录的sql语句，如果获取数据库连接失败，返回的结果则为null
     * @param sql sql语句
     * @return 执行结果
     */
    public static String getOneResult(String sql)
    {
        Connection connection = getConnection();

        Statement state = null;
        ResultSet rs = null;
        
        try
        {
            checkConnection(connection);
            state = connection.createStatement();
            rs = state.executeQuery(sql);
//            connection.commit();
            if (rs.next())
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Run sql success:" + sql);
                }
               
                return rs.getString(1);
            }
            logger.error("result is null");
           
            return null;
        }
        catch (SQLException e)
        {
            logger.error("run get one result sql exception:" + sql, e);
            // logger.error(e);
            return null;
        }
        finally
        {
            if (null != state)
            {
                try
                {
                    state.close();
                }
                catch (SQLException e)
                {
                    logger.error(e, e);
                } 
            }
            releaseConnection(connection);
        }
    }

    /**
     * Check connection whether healthly
     * @param connection
     * @throws SQLException
     */
    private static void checkConnection(Connection connection)
            throws SQLException
    {
        if (connection == null || connection.isClosed())
        {
//        	connectionPool.releaseConnection(connection);
//        	connection=connectionPool.getConnection();
        	logger.error("The Active con have "+connectionPool.getNumActive());
        	logger.error("The Idle con have "+connectionPool.getNumIdle());
            logger.error("Get a connection failed. connection is " + connection);
            throw new SQLException("Get a connection failed. connection is "
                    + connection);
        }
    }

    /**
     * 根据指定列个数查�?
     * @param sql 查询语句
     * @return DTable
     */ 
    public static DTable getSQLResult(String sql)
    {
        Connection connection = getConnection();
        
        Statement state = null;
        
        try
        {
            checkConnection(connection);
            state = connection.createStatement();
            ResultSet rs = state.executeQuery(sql);
//            connection.commit();
            ResultSetMetaData metaData = rs.getMetaData();
            int colNum = metaData.getColumnCount();
            DTable dTable = new DTable(metaData);
            Map<String, Object> row;
            while (rs.next())
            {
                row = new HashMap<String,Object>();
                for (int i = 1; i <= colNum; i++)
                {
                	String colName = metaData.getColumnLabel(i);
                	row.put(colName, rs.getObject(i));
                }
                dTable.addRow(row);
            }
            if (logger.isDebugEnabled())
            {
                logger.debug("getSQLResult;column=" + colNum);
                logger.debug("Run sql success:" + sql);
            }
            //state.close();
            return dTable;
        }
        catch (SQLException e)
        {
            logger.error("run get result sql exception:" + sql, e);
            //logger.error(e);
            return null;
        }
        finally
        {
            if (null != state)
            {
                try
                {
                    state.close();
                }
                catch (SQLException e)
                {
                    logger.error(e, e);
                } 
            }
            
            releaseConnection(connection);
        }

    }

    /**
     * close connection pool
     */
    public static void closeConnectionPool()
    {
        if (connectionPool == null)
        {
            logger.error("Close connection pool failed,beacause connectionPool == null.");
            return;
        }

        connectionPool.closeConnectionPool();
    }
/**
 * 批量向Metric表插入数�?
 * @param batchPutSQL 批量插入语句
 * @return 成功或失�?
 */
    public static boolean runBatchPutSQL(List<String> batchPutSQL)
    {
        // TODO Auto-generated method stub
        Connection connection = getConnection();
        Statement state = null;
        try
        {
            checkConnection(connection);
            state = connection.createStatement();
            
            for (String sql : batchPutSQL)
            {
                state.addBatch(sql);
            }
            state.executeBatch();
//            connection.commit();
            state.clearBatch();
            if (logger.isDebugEnabled())
            {
                logger.debug("Run sql success:" + batchPutSQL);
            }
            //state.close();
        }
        catch (SQLException e)
        {
            logger.error("Run sql exception:" + batchPutSQL, e);
            //logger.error(e);
            return false;
        }
        finally
        {
            if (null != state)
            {
                try
                {
                    state.close();
                }
                catch (SQLException e)
                {
                    logger.error(e, e);
                } 
            }
            
            releaseConnection(connection);
        }
        return true;
    }
}
