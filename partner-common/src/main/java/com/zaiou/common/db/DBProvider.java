package com.zaiou.common.db;

import com.zaiou.common.db.annotation.Column;
import com.zaiou.common.db.annotation.Table;
import com.zaiou.common.mybatis.po.Po;
import com.zaiou.common.utils.CamelCaseUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.SQL;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @Description: 拼接sql
 * @auther: LB 2018/9/19 16:24
 * @modify: LB 2018/9/19 16:24
 */
@Slf4j
public class DBProvider {

    /**
     *  插入数据PO对象不为空的属性
     * @param po
     * @return
     */
    public String insertNotNull(Po po) {
        // 获取表名
        String tableName = tableName(po);
        // 获取所有不为空的PO属性对应字段名的Map
        Map<String, String> notNullfieldMap = notNullFieldMap(po);

        if (null == tableName || notNullfieldMap.isEmpty()) {
            throw new RuntimeException("PO属性中未定义表名或属性字段！");
        }

        // 拼接查询的字段
        Set<String> keySet = notNullfieldMap.keySet();
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        for (String key : keySet) {
            if (columns.length() > 0) {
                columns.append(",");
                values.append(",");
            }
            columns.append(notNullfieldMap.get(key));
            if ("create_time".equals(notNullfieldMap.get(key))) {
                values.append("CURRENT_TIMESTAMP");
            } else {
                values.append("#{").append(key).append("}");
            }
        }
        SQL sql = new SQL();
        sql.INSERT_INTO(tableName);
        sql.VALUES(columns.toString(), values.toString());
        return sql.toString();
    }

    /**
     *  PO中属性不为null的进行AND条件查询
     * @param po
     * @return
     */
    public String selectNotNullAnd(Po po) {
        return selectNotNullAndOrder(po, null);
    }

    /**
     *  PO中属性不为null的进行AND条件查询，并排序
     * @param po
     * @param orderSql
     * @return
     */
    public String selectNotNullAndOrder(Po po, String orderSql) {
        // 获取表名
        String tableName = tableName(po);
        // 获取所有字段与PO属性对应Map
        Map<String, String> allFieldMap = allFieldMap(po);
        // 获取所有不为空的PO属性对应字段名的Map
        Map<String, String> notNullfieldMap = notNullFieldMap(po);

        if (null == tableName) {
            throw new RuntimeException("PO属性中未定义表名或属性字段！");
        }

        StringBuilder columns = new StringBuilder();
        StringBuilder where = new StringBuilder();

        // 拼接查询的字段
        Set<String> keySet = allFieldMap.keySet();
        for (String key : keySet) {
            if (columns.length() > 0) {
                columns.append(",");
            }
            columns.append(allFieldMap.get(key)).append(" as ").append(key.toUpperCase());
        }

        // 根据PO属性不为空拼接过滤条件
        Set<String> keySet2 = notNullfieldMap.keySet();
        for (String key : keySet2) {
            if (where.length() > 0) {
                where.append(" and ");
            }
            where.append(notNullfieldMap.get(key)).append(" = #{").append(key).append("}");
        }

        SQL sql = new SQL();
        sql.SELECT(columns.toString());
        sql.FROM(tableName);
        if (where.length() > 0) {
            sql.WHERE(where.toString());
        }
        if (orderSql != null && orderSql.length() > 0) {
            sql.ORDER_BY(orderSql);
        }
        return sql.toString();
    }

    /**
     *  PO中属性不为null的进行AND条件查询
     * @param param
     * @return
     */
    public String selectPageNotNull(Map<String, Object> param) {
        Po po = (Po) param.get("po");
        // 获取表名
        String tableName = tableName(po);
        // 获取所有字段与PO属性对应Map
        Map<String, String> allFieldMap = allFieldMap(po);
        // 获取所有不为空的PO属性对应字段名的Map
        Map<String, String> notNullfieldMap = notNullFieldMap(po);

        if (null == tableName) {
            throw new RuntimeException("PO属性中未定义表名或属性字段！");
        }

        StringBuilder columns = new StringBuilder();
        StringBuilder where = new StringBuilder();

        // 拼接查询的字段
        Set<String> keySet = allFieldMap.keySet();
        for (String key : keySet) {
            if (columns.length() > 0) {
                columns.append(",");
            }
            columns.append(allFieldMap.get(key)).append(" as ").append(key);
        }

        // 根据PO属性不为空拼接过滤条件
        Set<String> keySet2 = notNullfieldMap.keySet();
        try {
            for (String key : keySet2) {
                if (where.length() > 0) {
                    where.append(" and ");
                }
                where.append(notNullfieldMap.get(key)).append(" = #{").append(key).append("}");
                Field field = po.getClass().getDeclaredField(key);
                field.setAccessible(true);
                Object value = field.get(po);
                param.put(key, value);
            }
        } catch (Exception e) {
            log.error("无法获取Po属性值", e);
        }
        // 删除po
        param.remove("po");
        SQL sql = new SQL();
        sql.SELECT(columns.toString());
        sql.FROM(tableName);
        if (where.length() > 0) {
            sql.WHERE(where.toString());
        }
        return sql.toString() + " LIMIT #{limit} OFFSET #{offset} ";
    }

    /**
     *  插入数据PO对象不为空的属性
     * @param po
     * @return
     */
    public String deleteNotNull(Po po) {
        // 获取表名
        String tableName = tableName(po);
        // 获取所有不为空的PO属性对应字段名的Map
        Map<String, String> notNullfieldMap = notNullFieldMap(po);

        if (null == tableName || notNullfieldMap.isEmpty()) {
            throw new RuntimeException("PO属性中未定义表名或属性字段！");
        }

        StringBuilder where = new StringBuilder();
        // 根据PO属性不为空拼接过滤条件
        Set<String> keySet = notNullfieldMap.keySet();
        for (String key : keySet) {
            if (where.length() > 0) {
                where.append(" and ");
            }
            where.append(notNullfieldMap.get(key)).append(" = #{").append(key).append("}");
        }
        SQL sql = new SQL();
        sql.DELETE_FROM(tableName);
        sql.WHERE(where.toString());
        return sql.toString();
    }

    /**
     * 根据PO对象的ID属性更新操作
     * @param po
     * @return
     */
    public String updateByIdNotNull(Po po) {
        // 获取表名
        String tableName = tableName(po);
        // 获取所有不为空的PO属性对应字段名的Map
        Map<String, String> notNullfieldMap = notNullFieldMap(po);

        if (null == tableName || notNullfieldMap.size() < 1) {
            throw new RuntimeException("PO属性中未定义表名或更新的属性字段！");
        }

        if (!notNullfieldMap.containsKey("id")) {
            throw new RuntimeException("update操作必须存在id属性！");
        }

        String where = "id=#{id}";
        StringBuilder sets = new StringBuilder();
        // 根据PO属性不为空拼接过滤条件
        Set<String> keySet = notNullfieldMap.keySet();
        for (String key : keySet) {
            if (!key.equals("id")) {
                if (sets.length() > 0) {
                    sets.append(",");
                }
                sets.append(notNullfieldMap.get(key)).append("=#{").append(key).append("}");
            }
        }
        if (sets.length() < 1) {
            sets.append("id=#{id}");
        }
        SQL sql = new SQL();
        sql.UPDATE(tableName);
        sql.SET(sets.toString());
        sql.WHERE(where);
        return sql.toString();
    }

    /**
     *  PO中属性不为null的进行AND条件进行count数据
     * @param po
     * @return
     */
    public String countNotNullAnd(Po po) {
        // 获取表名
        String tableName = tableName(po);
        // 获取所有不为空的PO属性对应字段名的Map
        Map<String, String> notNullfieldMap = notNullFieldMap(po);

        if (null == tableName) {
            throw new RuntimeException("PO属性中未定义表名或属性字段！");
        }

        StringBuilder columns = new StringBuilder("count(1)");
        StringBuilder where = new StringBuilder();

        // 根据PO属性不为空拼接过滤条件
        Set<String> keySet2 = notNullfieldMap.keySet();
        for (String key : keySet2) {
            if (where.length() > 0) {
                where.append(" and ");
            }
            where.append(notNullfieldMap.get(key)).append(" = #{").append(key).append("}");
        }
        SQL sql = new SQL();
        sql.SELECT(columns.toString());
        sql.FROM(tableName);
        if (where.length() > 0) {
            sql.WHERE(where.toString());
        }
        return sql.toString();
    }

    /**
     * 根据po取表名
     * @param po
     * @return
     */
    private String tableName(Po po) {
        Table annotation = po.getClass().getAnnotation(Table.class);
        if (null == annotation) {
            // 如果不包含Table注解，直接取PO类名，转换成下划线命名
            String className = po.getClass().getSimpleName();
            if (className.endsWith("Po")) {
                // 如果Po结尾，去掉Po
                className = className.substring(0, className.length() - 2);
            }
            String tableName = CamelCaseUtils.toUnderlineName(className);
            if (tableName.endsWith("_po")) {
                return tableName.substring(0, tableName.indexOf("_po"));
            }
            return tableName;

        } else {
            // 如果包含Table注解，取注解名称
            return annotation.value();
        }
    }

    /**
     * 根据PO获取所有属性和表字段映射关系
     * @param po
     * @return
     */
    private Map<String, String> allFieldMap(Po po) {
        Map<String, String> fieldMap = new HashMap<String, String>();
        Field[] fields = po.getClass().getDeclaredFields();
        for (Field f : fields) {
            if (!f.getName().equals("serialVersionUID")) {
                fieldMap.put(f.getName(), getFieldName(f));
            }
        }
        return fieldMap;
    }

    /**
     *  根据PO获取所有不为空的属性和表字段映射关系
     * @param po
     * @return
     */
    private Map<String, String> notNullFieldMap(Po po) {
        Map<String, String> notNullfieldMap = new HashMap<String, String>();
        Field[] fields = getDeclaredField(po);
        boolean fieldIsNotNull;
        for (Field f : fields) {
            try {
                f.setAccessible(true);
                if (f.getName().equals("serialVersionUID")) {
                    continue;
                }
                fieldIsNotNull = (null != f.get(po));
                if (fieldIsNotNull) {
                    notNullfieldMap.put(f.getName(), getFieldName(f));
                }
            } catch (IllegalArgumentException e) {
                log.error("获取po对象值失败", e);
            } catch (IllegalAccessException e) {
                log.error("无法po获取对象值", e);
            }
        }
        return notNullfieldMap;
    }

    /**
     * 获取Po中所有字段
     * @param po
     * @return
     */
    public Field[] getDeclaredField(Po po) {
        Class<?> clazz = po.getClass();
        List<Field> fieldList = new ArrayList<Field>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field f : fields) {
            fieldList.add(f);
        }

        if (clazz.getSuperclass() != Object.class) {
            Field[] superFields = clazz.getSuperclass().getDeclaredFields();
            for (Field sf : superFields) {
                boolean isExist = false;
                for (Field f : fieldList) {
                    if (f.getName().equals(sf.getName())) {
                        isExist = true;
                        break;
                    }
                }
                if (isExist == false) {
                    fieldList.add(sf);
                } else {
                    isExist = false;
                }
            }
        }
        return fieldList.toArray(new Field[fieldList.size()]);
    }

    /**
     * 取字段对应数据表字段的名字
     * @param field
     * @return
     */
    private String getFieldName(Field field) {
        Column column = field.getAnnotation(Column.class);
        if (null != column) {
            // 字段包含Column注解的处理
            return column.value();
        } else {
            // 没有注解，直接取字段名转换成下划线命名字段
            return CamelCaseUtils.toUnderlineName(field.getName());
        }
    }
}
