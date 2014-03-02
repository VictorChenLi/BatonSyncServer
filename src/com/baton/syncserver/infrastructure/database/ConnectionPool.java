package com.baton.syncserver.infrastructure.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;

/**
 * 数据库连接池�?
 * 
 * <p>
 * 维持数据库连接，可以动�?增加数据库连接，分配数据库连接，基于apache的连接池库实�?
 * @author j66969 Create on 2011-4-1
 * @see
 * @since 1.0
 */
class ConnectionPool
{

    private static Logger logger = Logger.getLogger(ConnectionPool.class);

    /**
     * 连接池对�?
     */
    private static ObjectPool connectionPool = null;

    /**
     * dbcp pooling driver
     */
    private static final String POOLING_DREIVER = "org.apache.commons.dbcp.PoolingDriver";

    /**
     * dbcp driver
     */
    private static final String DBCP_DRIVER = "jdbc:apache:commons:dbcp:";

    /**
     * 数据库连接池名称
     */
    private static final String POOL_NAME = "DB_POOL";

    /**
     * 数据库连接池URL
     */
    private static final String POOL_URL = DBCP_DRIVER + POOL_NAME;

    /**
     * 在�?出对象时是否进行有效性检�?
     */
    boolean testOnBorrow = false;

    /**
     * 在归还对象时是否进行有效性检�?
     */
    boolean testOnReturn = false;

    /**
     * Evict线程执行间隔毫秒数，非正数表示Evict线程不执�?
     */
    long timeBetweenEvictionRunsMillis = 30000;

    /**
     * Evict线程每次执行时检查最大对象个数，当设置为-n时，每次�?��大概(空闲对象个数/n)�?
     */
    int numTestsPerEvictionRun = 10;
    /**
     * 可被Evict线程清除前该连接的空闲时长，默认30分钟
     */
    long minEvictableIdleTimeMillis = 1800000;

    /**
     * Evict线程清理时，是否还对没有过期的池内对象进行有效�?�?��
     */
    boolean testWhileIdle = true;

    /**
     * 可被Evict线程清除前该连接的空闲时长，并且minIdle个连接必须保�?
     */
    long softMinEvictableIdleTimeMillis = -1L;
    
    /**
     * 数据库驱�?
     */
    private String jdbcDriver;

    /**
     * 数据URL
     */
    private String dbUrl;

    /**
     * 数据库用户名
     */
    private String dbUsername;

    /**
     * 数据库用户密�?
     */
    private String dbPassword;

    /**
     * 连接池的�?��活动连接�?
     */
    private int maxActives = 10;

    /**
     * 连接池�?尽时的行为，取�?有WHEN_EXHAUSTED_FAIL�?
     * WHEN_EXHAUSTED_GROW和WHEN_EXHAUSTED_BLOCK�?
     */
    private byte whenExhaustedAction = GenericObjectPool.WHEN_EXHAUSTED_GROW;

    /**
     * 当whenExhaustedAction的取值是WHEN_EXHAUSTED_BLOCK时等待的�?��时间，非正数时将无限等待
     */
    private long maxWait = 0;

    /**
     * 连接池中�?��的空闲连接数，取负数时表示没有限�?
     */
    private int maxIdle = 50;

    /**
     * 连接池中�?��的空闲连接数
     */
    private int minIdle = 0;
    
    /**
     * 测试语句
     */
    private String testQury="";

    /**
     * 构�?函数
     * @param jdbcDriver 驱动类串
     * @param dbUrl 数据库URL
     * @param dbUsername 连接数据库用户名
     * @param dbPassword 连接数据库用户的密码
     */
    public ConnectionPool(String jdbcDriver, String dbUrl, String dbUsername,
            String dbPassword)
    {
        this.jdbcDriver = jdbcDriver;
        this.dbUrl = dbUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
    }

    /**
     * 
     * 关闭连接池中�?��的连接，并清空连接池�?
     * 
     */
    public synchronized void closeConnectionPool()
    {
        try
        {
            PoolingDriver driver = (PoolingDriver) DriverManager
                    .getDriver(DBCP_DRIVER);
            driver.closePool(POOL_NAME);
        }
        catch (SQLException e)
        {
            logger.error("Close connection pool failed.", e);
            logger.error(e);
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Close connection pool success.");
        }
    }

    /**
     * 创建�?��数据库连接池，连接池中的可用连接的数量采用类成员initialConnections 中设置的�?
     * @throws Exception
     */
    public synchronized void createPool() throws Exception
    {
        // 如果己经创建，则返回
        if (connectionPool != null)
        {
            return;
        }

        // 初始化JDBC驱动
        Class.forName(jdbcDriver);

        // 创建�?��对象池来保存数据库连�?
        connectionPool = new GenericObjectPool(null, maxActives,
                whenExhaustedAction, maxWait, maxIdle, minIdle, testOnBorrow,
                testOnReturn, timeBetweenEvictionRunsMillis,
                numTestsPerEvictionRun, minEvictableIdleTimeMillis,
                testWhileIdle, softMinEvictableIdleTimeMillis);

        // 创建�?�� DriverManagerConnectionFactory对象,连接池将用它来获取一个连�?
        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(
                dbUrl, dbUsername, dbPassword);

        // 创建�?��PoolableConnectionFactory 对象
        new PoolableConnectionFactory(connectionFactory, connectionPool, null,
        		testQury, false, true);

        // 注册PoolingDriver
        Class.forName(POOLING_DREIVER);
        PoolingDriver driver = (PoolingDriver) DriverManager
                .getDriver(DBCP_DRIVER);
        driver.registerPool(POOL_NAME, connectionPool);

        if (logger.isDebugEnabled())
        {
            logger.debug("Initialize connection pool success." + "url = "
                    + dbUrl + ",user = " + dbUsername + ",password = "
                    + dbPassword + ",jdbcDriver = " + jdbcDriver);
        }
    }

    /**
     * 获取可用的数据库连接，如果当前没有可用的数据库连接，并且更多的数据库连接不能�?建（如连接池大小的限制），此函数等待�?��再尝试获取�?
     * @return 可用的数据库连接对象
     */
    public synchronized Connection getConnection()
    {
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(POOL_URL);
//            connection.setAutoCommit(false);
        }
        catch (SQLException e)
        {
            logger.error("Get a connection failed.", e);
            logger.error(e);
        }
        return connection;

    }

    /**
     * 释放数据连接，并把此连接置为空闲 �?��使用连接池获得的数据库连接均应在不使用此连接时调用此函数释放
     */
    public void releaseConnection(Connection connection)
    {
        try
        {
            if (connection != null && !connection.isClosed())
            {
                connection.close();
            }
        }
        catch (SQLException e)
        {
            logger.error(e, e);
        }
    }

    /**
     * 获取连接池中的空闲连接数�?
     * @return 连接池中的空闲连接数�?
     */
    public int getNumIdle()
    {
        return connectionPool.getNumIdle();
    }

    /**
     * 获取连接池中活动连接数量
     * @return 连接池中的活动连接数�?
     */
    public int getNumActive()
    {
        return connectionPool.getNumActive();
    }
}
