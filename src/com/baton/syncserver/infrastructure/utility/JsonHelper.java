package com.baton.syncserver.infrastructure.utility;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;
import net.sf.json.xml.XMLSerializer;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import flexjson.ObjectBinder;
import flexjson.ObjectFactory;

/**
 * 序列化与反序列化帮助�?
 * 
 */
public class JsonHelper
{
    /**
     * 日志工具
     */
    private static Logger logger = Logger.getLogger(JsonHelper.class);

    /**
     * 将对象序列化为字符串
     * @param obj 对象
     * @return 序列化后的字符串
     */
    public static String serializeWithClass(Object obj)
    {
        JSONSerializer serializer = new JSONSerializer();
        String out = serializer.deepSerialize(obj);
        return out;
    }

    /**
     * 反序列化
     * @param <T> 泛型
     * @param json json字符�?
     * @return 返回的泛型对�?
     */
    public static <T> T deserializeWithClass(String json)
    {
        JSONDeserializer<T> deserializer = new JSONDeserializer<T>();
        deserializer.use(Boolean.class, new ObjectFactory()
        {

            @SuppressWarnings("rawtypes")
            @Override
            public Object instantiate(ObjectBinder context, Object value,
                    Type targetType, Class targetClass)
            {
                if (value instanceof Boolean)
                {
                    return value;
                }
                else
                {
                    Boolean ret = Boolean.parseBoolean(value.toString());
                    return ret;
                }
            }
        });
        T out = deserializer.deserialize(json);
        return out;
    }

    /**
     * 将某个Bean对象转换为Json字符�?
     * @param obj 输入的Bean
     * @return 输出的json字符�?
     */
    public static String serialize(Object obj)
    {
        JSONSerializer serializer = new JSONSerializer();
        String out = serializer.exclude("*.class").deepSerialize(obj);
        return out;
    }

    /**
     * 反序列化
     * @param json json字符�?
     * @param clazz 类型
     * @return 序列化后的对�?
     */
    public static Object deserialize(String json, Class<?> clazz)
    {
        JSONObject jsonObj = (JSONObject) net.sf.json.JSONSerializer
                .toJSON(json);
        String reqClassName = clazz.getName();
        jsonObj.put("class", reqClassName);
        json = jsonObj.toString();

        JSONDeserializer<Object> deserializer = new JSONDeserializer<Object>();
        deserializer.use(Boolean.class, new ObjectFactory()
        {

            @Override
            public Object instantiate(ObjectBinder context, Object value,
                    Type targetType,
                    @SuppressWarnings("rawtypes") Class targetClass)
            {
                if (value instanceof Boolean)
                {
                    return value;
                }
                else
                {
                    Boolean ret = Boolean.parseBoolean(value.toString());
                    return ret;
                }
            }
        });
        Object out = deserializer.deserialize(json);
        return out;
    }

    /**
     * 将json字符串转换为bean对象
     * @param json json字符�?
     * @return bean对象
     */
    public static Object deserialize(String json)
    {
        JSONDeserializer<Object> deserializer = new JSONDeserializer<Object>();
        deserializer.use(Boolean.class, new ObjectFactory()
        {

            @Override
            public Object instantiate(ObjectBinder context, Object value,
                    Type targetType,
                    @SuppressWarnings("rawtypes") Class targetClass)
            {
                if (value instanceof Boolean)
                {
                    return value;
                }
                else
                {
                    Boolean ret = Boolean.parseBoolean(value.toString());
                    return ret;
                }
            }
        });
        Object out = deserializer.deserialize(json);
        return out;
    }

    /**
     * 将Json 节点转换为键�?
     * @param path Json 节点列表
     * @return 返回Json 节点对应的键�?
     */
    public static String path2Key(List<JsonNode> path)
    {
        String ret = "";
        for (JsonNode node : path)
        {
            if (!ret.equals(""))
            {
                ret += ".";
            }
            ret += node.getName();
        }
        return ret;
    }

    private static void traversalJsonInner(JsonNode node,
            TraversalJsonCallbacker callback, List<JsonNode> path, String name)
            throws Exception
    {
        if (node.getName() != null)
        {
            callback.operate(path, node);
        }
        if (!node.getKeys().isEmpty())
        {
            List<String> keys = node.getKeys();
            for (String key : keys)
            {
                JsonNode child = node.get(key);
                List<JsonNode> newPath = new ArrayList<JsonNode>(path);
                if (node.getName() != null)
                {
                    newPath.add(node);
                }
                traversalJsonInner(child, callback, newPath, key);
            }
        }
    }

    private static void copyJsonInner(final JSONObject src, final JSONObject des)
    {
        JsonNode jsonNode = new JsonNode(src, null);
        try
        {
            traversalJsonInner(jsonNode, new TraversalJsonCallbacker()
            {

                @Override
                public void operate(List<JsonNode> path, JsonNode cur)
                {
                    String key = path2Key(path) + "." + cur.getName();
                    if (cur.isValue())
                    {
                        putKeyPath2Json(key, cur.getValue(), des);
                    }
                }
            }, new ArrayList<JsonNode>(), null);
        }
        catch (Exception e)
        {
            logger.error(e);
        }
    }

    /**
     * 拷贝json对象
     * @param json 源json对象
     * @return json对象
     */
    public static JSONObject copyJson(final JSONObject json)
    {
        JSONObject newJson = new JSONObject();
        copyJsonInner(json, newJson);
        return newJson;
    }

    /**
     * 遍历json对象
     * @param json 源json对象
     * @param callback 回调对象
     * @throws Exception 异常
     */
    public static void traversalJson(JSONObject json,
            TraversalJsonCallbacker callback) throws Exception
    {
        JSONObject jsonCopy = copyJson(json);
        JsonNode node = new JsonNode(jsonCopy, null);
        traversalJsonInner(node, callback, new ArrayList<JsonNode>(), null);
    }

    /**
     * Json对象回调接口
     */
    public static interface TraversalJsonCallbacker
    {
        /**
         * 回调操作
         * @param path Json 节点列表
         * @param cur Json 节点
         * @throws Exception 异常
         */
        public void operate(List<JsonNode> path, JsonNode cur) throws Exception;
    }

    /**
     * 将某个Json对象转换为xml
     * @param json json对象（建立于net.sf.json的json-lib中间件）
     * @param root 根结�?
     * @param item 节点
     * @return 转换后的xml
     */
    public static String json2Xml(JSONObject json, String root, String item)
    {
        XMLSerializer xmlSerializer = new XMLSerializer();
        xmlSerializer.setObjectName(root);
        xmlSerializer.setElementName(item);
        xmlSerializer.setTypeHintsEnabled(false);
        String xml = xmlSerializer.write(json);
        int rowEnd = xml.indexOf("\n");
        xml = xml.substring(rowEnd + 1);
        return xml;
    }

    private static void json2MapInner(JsonNode json, String prefix,
            Map<String, String> map)
    {
        if (prefix == null)
        {
            prefix = "";
        }
        List<String> keys = json.getKeys();
        for (String key : keys)
        {
            JsonNode child = json.get(key);
            if (child.getKeys().size() <= 0)
            {
                String value = "";
                if (child.isValue())
                {
                    value = child.getValue();
                }
                map.put(prefix + key, value);
            }
            else
            {
                json2MapInner(child, prefix + key + ".", map);
            }
        }
    }

    /**
     * 将json对象转换为名值对的map
     * @param json json对象
     * @return 转换后的map对象
     */
    public static Map<String, String> json2Map(JSONObject json)
    {
        JsonNode jsonNode = new JsonNode(json, null);
        Map<String, String> map = new HashMap<String, String>();
        json2MapInner(jsonNode, "", map);
        return map;
    }

    /**
     * 将JSONArray转换为List<String>
     * @param jsonArray JsonArray 对象
     * @return 转换后的List<String>对象
     */
    public static List<String> jsonArray2List(JSONArray jsonArray)
    {
        //JsonNode jsonNode = new JsonNode(jsonArray, null);
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < jsonArray.size(); i++)
        {
            list.add(jsonArray.getString(i));
        }
        return list;
    }

    /**
     * 将名值对的map转换为json对象
     * @param map Map对象
     * @return 转换后的json对象
     */
    public static JSONObject map2Json(Map<String, String> map)
    {
        JSONObject json = new JSONObject();
        for (Entry<String, String> entry : map.entrySet())
        {
            putKeyPath2Json(entry.getKey(), entry.getValue(), json);
        }
        return json;
    }

    /**
     * 从某个json对象得到某个型如"xxx.xxx.3.xxx.0.xxx"的key路径指定的json对象
     * @param keyPath key路径
     * @param json json对象
     * @return 转换后的json对象
     */
    public static JsonNode getValueByKeyPath(String keyPath, JSONObject json)
    {
        String[] names = keyPath.split("\\.");
        int len = names.length;
        JsonNode curNode = new JsonNode(json, null);
        for (int i = 0; i < len; i++)
        {
            String key = names[i];
            if (!curNode.containKey(key))
            {
                return null;
            }
            curNode = curNode.get(key);
        }
        return curNode;
    }

    // /**
    // * 将某个型�?xxx.xxx.3.xxx.0.xxx"的key路径以及值放入json对象
    // * @param keyPath key路径
    // * @param value �?
    // * @param json json对象
    // */
    // public static void addKeyPath2Json(String keyPath, String value,
    // JSONObject json)
    // {
    // String[] names = keyPath.split("\\.");
    // int len = names.length;
    // JsonNode curNode = new JsonNode(json, null);
    // for (int i = 0; i < len; i++)
    // {
    // String key = names[i];
    // if (key.matches("\\d+"))
    // {
    // int keyInt = Integer.parseInt(key);
    // if (keyInt < 0)
    // {
    // throw new RuntimeException("Input parameter Subscript less than zero(" +
    // keyPath + ")");
    // }
    // }
    // if (!curNode.containKey(key))
    // {
    // if (i >= len -1)
    // {
    // curNode.put(key, value);
    // }
    // else
    // {
    // if (names[i + 1].matches("\\d+"))
    // {
    // curNode.put(key, new JSONArray());
    // }
    // else
    // {
    // curNode.put(key, new JSONObject());
    // }
    // }
    // }
    // curNode = curNode.get(key);
    // }
    // }

    /**
     * 将某个型�?xxx.xxx.3.xxx.0.xxx"的key路径以及值放入json对象
     * @param keyPath key路径
     * @param value �?
     * @param json json对象
     */
    public static void putKeyPath2Json(String keyPath, String value,
            JSONObject json)
    {
        String[] names = keyPath.split("\\.");
        int len = names.length;
        JsonNode curNode = new JsonNode(json, null);
        for (int i = 0; i < len; i++)
        {
            String key = names[i];
            if (key.matches("\\d+"))
            {
                int keyInt = Integer.parseInt(key);
                if (keyInt < 0)
                {
                    throw new RuntimeException(
                            "Input parameter Subscript less than zero("
                                    + keyPath + ")");
                }
            }
            if (i >= len - 1)
            {
                curNode.put(key, value);
            }
            else if (!curNode.containKey(key))
            {
                if (names[i + 1].matches("\\d+"))
                {
                    curNode.put(key, new JSONArray());
                }
                else
                {
                    curNode.put(key, new JSONObject());
                }
            }
            curNode = curNode.get(key);
        }
    }
    
    public static void main(String[] args)
    {
    	JSONObject json = new JSONObject();
    	json.put("test", "test1");
    }

    /**
     * 包装后的json类型，统�?son中的object、array、value各种节点的操�?
     */
    public static class JsonNode
    {
        /**
         * ARRAY常量字符�?
         */
        public static final String TYPE_JSON = "JSON";
        
        /**
         * JSON常量字符�?
         */
        public static final String TYPE_ARRAY = "ARRAY";

        /**
         * VALUE常量字符�?
         */
        public static final String TYPE_VALUE = "VALUE";

        private JSONObject json = null;

        private JSONArray array = null;

        private String value = null;

        private String name = null;

        private String type = null;

        /**
         * 构�?函数 根据json-lib中间件的对象构�?对象
         * object的子key是key名称，array的子key是下标（0�?��），value子key列表为空
         * @param obj 对象
         * @param name 对象名称
         */
        public JsonNode(Object obj, String name)
        {
            this.name = name;
            if (obj instanceof JSONObject)
            {
                this.json = (JSONObject) obj;
                this.type = TYPE_JSON;
            }
            else if (obj instanceof JSONArray)
            {
                this.array = (JSONArray) obj;
                this.type = TYPE_ARRAY;
            }
            else
            {
                if (obj instanceof JSONNull)
                {
                    this.value = null;
                }
                else
                {
                    this.value = obj.toString();
                }
                this.type = TYPE_VALUE;
            }
        }

        /**
         * 返回节点名称
         * @return 返回节点名称
         */
        public String getName()
        {
            return this.name;
        }

        public JSONObject getJson()
        {
            return this.json;
        }

        /**
         * 本对象是否是�?��json value
         * @return 是返回ture，否则返回false
         */
        public boolean isValue()
        {
            return this.type.equals(TYPE_VALUE);
        }

        /**
         * 本对象是否是�?��json object
         * @return 是返回true，否则返回false
         */
        public boolean isJson()
        {
            return this.type.equals(TYPE_JSON);
        }

        /**
         * 本对象是否是�?��json Array
         * @return 是返回true，否则返回false
         */
        public boolean isArray()
        {
            return this.type.equals(TYPE_ARRAY);
        }

        /**
         * 得到json value
         * @return 返回json value
         */
        public String getValue()
        {
            return this.value;
        }

        /**
         * 是否包含某个子key
         * @param key key
         * @return 是返回true，否则返回false
         */
        public boolean containKey(String key)
        {
            if (this.type.equals(TYPE_JSON))
            {
                return this.json.containsKey(key);
            }
            else if (this.type.equals(TYPE_ARRAY))
            {
                int index = Integer.parseInt(key);
                if (index >= this.array.size() || index < 0)
                {
                    return false;
                }
                return true;
            }
            else
            {
                return false;
            }
        }

        /**
         * 得到子key列表
         * @return 返回key列表
         */
        public List<String> getKeys()
        {
            List<String> keys = new ArrayList<String>();
            if (this.type.equals(TYPE_JSON))
            {
                Set<?> keySet = this.json.keySet();
                for (Object key : keySet)
                {
                    keys.add((String) key);
                }
            }
            else if (this.type.equals(TYPE_ARRAY))
            {
                int len = this.array.size();
                for (int i = 0; i < len; i++)
                {
                    keys.add(i + "");
                }
            }
            return keys;
        }

        /**
         * 得到子节�?
         * @param key key
         * @return 返回子节�?
         */
        public JsonNode get(String key)
        {
            if (!this.containKey(key))
            {
                return null;
            }
            if (this.type.equals(TYPE_JSON))
            {
                return new JsonNode(this.json.get(key), key);
            }
            else if (this.type.equals(TYPE_ARRAY))
            {
                return new JsonNode(this.array.get(Integer.parseInt(key)), key);
            }
            return null;
        }

        /**
         * 放置子节�?
         * @param key key
         * @param node json-lib中间件中的json对象
         */
        public void put(String key, Object node)
        {
            Object value1 = node;
            if (value1 instanceof String && JSONUtils.mayBeJSON((String) value1))
            {
                value1 = "\"" + value1 + "\"";
            }
            if (this.type.equals(TYPE_JSON))
            {
                this.json.put(key, value1);
            }
            else if (this.type.equals(TYPE_ARRAY))
            {
                int index = Integer.parseInt(key);
                int len = this.array.size();
                if (index < 0)
                {
                    return;
                }
                for (int i = len; i < index + 1; i++)
                {
                    this.array.add(value1);
                }
                this.array.set(index, value1);
            }
            else
            {
                // for error
                logger.error(" error type");
            }
        }
    }
    
    /**
     * 测试函数
     */
    public static void test1()
    {
        JSONObject json = new JSONObject();
        putKeyPath2Json("abc.aaa.ccc.0.ggg", "a", json);
        putKeyPath2Json("abc.aaa.bbb.ggg", "b", json);
        json.put("0", "123");
        Map<String, String> map = json2Map(json);
        for (Entry<String, String> entry : map.entrySet())
        {
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }
    }

    /**
     * 测试函数
     */
    public static void test2()
	{

	}

}
