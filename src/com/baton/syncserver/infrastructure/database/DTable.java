package com.baton.syncserver.infrastructure.database;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * �?��SQL返回结果的数据表格类
 *
 * <p>detailed comment
 * @author j66969 Create on 2011-4-1
 * @see
 * @since 1.0
 */
public class DTable
{
    /**
     * 表格列集�?
     */
    private List<Map<String,Object>> rows = new ArrayList<Map<String,Object>>();
    
    /**
     * 数据表列的类型和属�?信息
     */
    private ResultSetMetaData metaData;
    
    /**
     * 构�?函数
     * @param metaData 元数�?
     */
    public DTable(ResultSetMetaData metaData)
    {
        super();
        this.metaData = metaData;
    }

    /**
     * 增加 �?
     * @param row �?
     */
    public void addRow(Map<String,Object> row)
    {
        rows.add(row);
    }
    
    /**
     * 根据行和列数获取数据结果
     * @param row 行数
     * @param col 列数
     * @return 结果�?
     */
    public String getValue(int row,int col)
    {
        return (String)rows.get(row).get(col);
    }
    
    public Map<String,Object> getRow(int row)
    {
    	return rows.get(row);
    }
    
    /**
     * 获取数据表格的�?行数
     * @return 数据表格的�?行数
     */
    public int getRowLength()
    {
        return rows.size();
    }

    /**
     * 获取元数�?
     * @return the metaData
     */
    public ResultSetMetaData getMetaData()
    {
        return metaData;
    }
    
    
}
