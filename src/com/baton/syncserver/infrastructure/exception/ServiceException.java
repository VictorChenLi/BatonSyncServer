package com.baton.syncserver.infrastructure.exception;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.baton.syncserver.infrastructure.utility.JsonHelper;

/**
 * WebService基本异常
 */
public class ServiceException extends Exception
{
    private static final long serialVersionUID = -3577598432600336223L;

    private String errorCode = null;

    private String errorType = null;

    private int statusCode = 200;

    /**
     * 
     * 构�?函数
     * @param message 异常信息
     * @param cause 异常原因
     * @param errorCode 错误�?
     * @param errorType 错误类型
     * @param statusCode 状�?�?
     */
    public ServiceException(String message, Throwable cause, String errorCode,
            String errorType, int statusCode)
    {
        super(message, cause);
        this.errorCode = errorCode;
        this.errorType = errorType;
        this.statusCode = statusCode;
    }

    /**
     * 
     * 构�?函数
     * @param message 异常信息
     * @param errorCode 错误�?
     * @param errorType 错误类型
     * @param statusCode 状�?�?
     */
    public ServiceException(String message, String errorCode, String errorType,
            int statusCode)
    {
        super(message);
        this.errorCode = errorCode;
        this.errorType = errorType;
        this.statusCode = statusCode;
    }

    /**
     * 
     * 构�?函数
     * @param message 异常信息
     * @param cause 异常原因
     */
    public ServiceException(String message, Throwable cause)
    {
        super(message, cause);
        this.errorCode = "0";
        this.errorType = "OTHER";
        this.statusCode = HttpServletResponse.SC_BAD_REQUEST;
    }
    
    public ServiceException(String message, String errorCode)
    {
        super(message);
        this.errorCode = errorCode;
        this.errorType="OTHER";
        this.statusCode=HttpServletResponse.SC_BAD_REQUEST;
    }

    /**
     * 
     * 构�?函数
     * @param message 异常信息
     */
    public ServiceException(String message)
    {
        super(message);
        this.errorCode = "0";
        this.errorType = "OTHER";
        this.statusCode = HttpServletResponse.SC_BAD_REQUEST;
    }
    
    public ServiceException()
    {
        super("");
        this.errorCode = "0";
        this.errorType = "OTHER";
        this.statusCode = HttpServletResponse.SC_BAD_REQUEST;
    }
    

    /**
     * 
     * 构�?函数
     * @param errorCode 错误�?
     * @param errorType 错误类型
     * @param statusCode 状�?�?
     */
    public ServiceException(String errorCode, String errorType,
            int statusCode)
    {
        super("");
        this.errorCode = errorCode;
        this.errorType = errorType;
        this.statusCode = statusCode;
    }
    
    public String toJson()
    {
    	ServiceException output = new ServiceException();
    	output.setErrorCode(this.getErrorCode());
    	output.setErrorType(this.getErrorType());
    	output.setStatusCode(this.getStatusCode());
    	return JsonHelper.serialize(output);
    }

    public String getErrorCode()
    {
        return errorCode;
    }

    public void setErrorCode(String errorCode)
    {
        this.errorCode = errorCode;
    }

    public String getErrorType()
    {
        return errorType;
    }

    public void setErrorType(String errorType)
    {
        this.errorType = errorType;
    }

    public int getStatusCode()
    {
        return statusCode;
    }

    public void setStatusCode(int statusCode)
    {
        this.statusCode = statusCode;
    }

}
