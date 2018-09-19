package com.zaiou.common.db;

import com.zaiou.common.mybatis.po.Po;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * @Description: Dao通用接口，利用mybatis框架实现通用的CRUD方法，可以给其他mybatis mapper接口继承
 * 使用通用的CRUD方法可以在相关的PO类中用注解定义表名和字段名.
 * 否则直接使用PO类名作为表名，属性名作为数据库字段名。
 * @auther: LB 2018/9/19 16:09
 * @modify: LB 2018/9/19 16:09
 */
public interface BaseDao {

    /**
     *  根据传入的PO对象，其中不为空的属性作为AND查询条件，返回查询的一条结果。
     * @param po
     * @return
     */
    @SelectProvider(type = DBProvider.class, method = "selectNotNullAnd")
    Map<String, Object> findOne(Po po);

    /**
     *  根据传入的PO对象，其中不为空的属性作为AND查询条件，返回查询结果列表
     * @param po
     * @return
     */
    @SelectProvider(type = DBProvider.class, method = "selectNotNullAnd")
    List<Map<String, Object>> findList(Po po);

    /**
     *  根据传入的PO对象，其中不为空的属性作为查询条件进行分页查询，返回查询结果列表
     * @param po
     * @param offset
     * @param limit
     * @return
     */
    @SelectProvider(type = DBProvider.class, method = "selectPageNotNull")
    List<Map<String, Object>> findPage(@Param("po") Po po, @Param("offset") int offset, @Param("limit") int limit);

    /**
     *  插入一条新的数据
     * @param po
     */
    @InsertProvider(type = DBProvider.class, method = "insertNotNull")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "ID")
    void save(Po po);

    /**
     *  以id为条件，更新PO属性不为空的数据
     * @param po
     * @return
     */
    @UpdateProvider(type = DBProvider.class, method = "updateByIdNotNull")
    @Options(useGeneratedKeys = false, keyProperty = "pk1", keyColumn = "PK1")
    int update(Po po);

    /**
     *  删除Po属性不为空的条件数据
     * @param po
     * @return
     */
    @DeleteProvider(type = DBProvider.class, method = "deleteNotNull")
    int delete(Po po);

    /**
     *  根据传入的PO对象，其中不为空的属性作为AND查询条件，返回count结果值。
     * @param po
     * @return
     */
    @SelectProvider(type = DBProvider.class, method = "countNotNullAnd")
    int count(Po po);
}
