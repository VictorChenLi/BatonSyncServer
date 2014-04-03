package com.baton.syncserver.infrastructure.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ConfigHelper {
	
    /**
     * ��־����
     */
    private static Logger logger = Logger.getLogger(ConfigHelper.class);
    
    private static Properties configProperties;

	/**
     * ��ȡ��ame.properties�����ļ���ȡ������
     * @param key ����������
     * @return ������ֵ
     */
    public static final String getConfig(String key)
    {
        if (configProperties == null)
        {
            logger.error("configProperties not init");
            return null;
        }
        String value = configProperties.getProperty(key);
        if (value == null || value.trim().isEmpty())
        {
            logger.error("value == null,key = " + key);
            return null;
        }
        return value.trim();
    }

    public static final Properties getConfigProperties()
    {
        return configProperties;
    }
    
    private static String getHomeDir()
    {
        String value = System.getenv("Baton_HOME");
        return value == null ? "C:/Users/foxwe_000/git/BatonSyncServer" : value;
    }
    
    /**
     * 获取配置文件的路�?
     * @return 配置文件的路�?
     */
    public static String getConfigFilePath()
    {
        return getHomeDir() + "/config/config.properties";
    }
    
    public static void loadConfig()
    {
    	
        configProperties = new Properties();
        String proPath = getConfigFilePath();
        logger.debug("config file is " + proPath);

        try
        {
            FileInputStream fileInputStream = new FileInputStream(new File(
                    proPath));
            configProperties.load(fileInputStream);
            fileInputStream.close();
        }
        catch (FileNotFoundException e)
        {
            String errorText = "load config file fail! lost environment variable CONFIG_HOME?";
            logger.error(errorText, e);
            throw new RuntimeException(errorText, e);
        }
        catch (IOException e)
        {
            String errorText = "load config file fail!";
            logger.error(errorText, e);
            throw new RuntimeException(errorText, e);
        }
    }
}
