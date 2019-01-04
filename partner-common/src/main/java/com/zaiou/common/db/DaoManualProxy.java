package com.zaiou.common.db;

import com.zaiou.common.mybatis.mapper.BaseDaoMapper;
import com.zaiou.common.mybatis.po.Po;
import com.zaiou.common.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description:Dao代理类<br>
 *  对mybatis的dao接口中的通用方法进行代理，实现返回具体的PO对象
 * @auther: LB 2018/11/29 15:09
 * @modify: LB 2018/11/29 15:09
 */
@Slf4j
public class DaoManualProxy {

    /**
     *  根据PO对象查询列表，并返回PO对象列表
     * @param sqlSession
     * @param po
     * @return
     */
    @SuppressWarnings("rawtypes")
    public List findList(SqlSession sqlSession, final Po po) {
        BaseDaoMapper mapper = (BaseDaoMapper) sqlSession.getMapper(BaseDaoMapper.class);
        return toPOList(mapper.findList(po), po.getClass());
    }

    /**
     * 根据PO对象查询列表，并返回PO分页对象列表
     * @param sqlSession
     * @param po
     * @param offset
     * @param limit
     * @return
     */
    public List<Po> findPage(SqlSession sqlSession, final Po po, int offset, int limit) {
        BaseDaoMapper mapper = (BaseDaoMapper) sqlSession.getMapper(BaseDaoMapper.class);
        return (List<Po>) toPOList(mapper.findPage(po, offset, limit), po.getClass());
    }

    /**
     *
     * 根据PO对象查询单条数据，并返回PO对象
     * @param sqlSession
     * @param po
     * @return
     */
    public Po findOne(SqlSession sqlSession, final Po po) {
        BaseDaoMapper mapper = (BaseDaoMapper) sqlSession.getMapper(BaseDaoMapper.class);
        Map<String, Object> map = mapper.findOne(po);
        if (null != map && !map.isEmpty()) {
            return toPO(map, po.getClass());
        }
        return null;
    }

    /**
     * 保存一个PO对象
     * @param sqlSession
     * @param po
     */
    public void save(SqlSession sqlSession, final Po po) {
        BaseDaoMapper mapper = (BaseDaoMapper) sqlSession.getMapper(BaseDaoMapper.class);
        mapper.save(po);
    }

    /**
     * 保存一个PO对象
     * @param sqlSession
     * @param po
     * @return
     */
    public int delete(SqlSession sqlSession, final Po po) {
        BaseDaoMapper mapper = (BaseDaoMapper) sqlSession.getMapper(BaseDaoMapper.class);
        return mapper.delete(po);
    }

    /**
     *  更新一个PO对象
     * @param sqlSession
     * @param po
     * @return
     */
    public int update(SqlSession sqlSession, final Po po) {
        BaseDaoMapper mapper = (BaseDaoMapper) sqlSession.getMapper(BaseDaoMapper.class);
        return mapper.update(po);
    }

    /**
     *  返回对象总数
     * @param sqlSession
     * @param po
     * @return
     */
    public int count(SqlSession sqlSession, final Po po) {
        BaseDaoMapper mapper = (BaseDaoMapper) sqlSession.getMapper(BaseDaoMapper.class);
        return mapper.count(po);
    }

    /**
     * 将Map对象转换成PO对象
     *
     * @param map
     * @param cls
     * @return
     */
    private <T extends Po> T toPO(final Map<String, Object> map, final Class<T> cls) {
        BeanInfo beanInfo = null;
        T bean = null;
        String propertyName = null;
        try {
            beanInfo = Introspector.getBeanInfo(cls);
            bean = cls.newInstance(); // 创建 JavaBean 对象
            // 给 JavaBean 对象的属性赋值
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (int i = 0; i < propertyDescriptors.length; i++) {
                PropertyDescriptor descriptor = propertyDescriptors[i];
                propertyName = descriptor.getName().toUpperCase();

                if (map.containsKey(propertyName)) {
                    // 下面一句可以 try 起来，这样当一个属性赋值失败的时候就不会影响其他属性赋值。
                    Object value = map.get(propertyName);
                    if (value instanceof Timestamp && descriptor.getPropertyType().getName().equals("java.lang.String")) {
                        value = DateUtils.format(new Date(((Timestamp) value).getTime()), "yyyy-MM-dd HH:mm:ss");
                    } else if (value instanceof java.sql.Date
                            && descriptor.getPropertyType().getName().equals("java.lang.String")) {
                        value = value.toString();
                    } else if (value instanceof java.sql.Time
                            && descriptor.getPropertyType().getName().equals("java.lang.String")) {
                        value = value.toString();
                    }
                    if (value instanceof Boolean && descriptor.getPropertyType().getName().equals("java.lang.Integer")) {
                        value = ((Boolean) value) ? 1 : 0;
                    }
                    if (value instanceof Boolean && descriptor.getPropertyType().getName().equals("java.lang.Byte")) {
                        value = ((Boolean) value) ? (byte)1 : (byte)0;
                    }
                    if (value instanceof Integer && descriptor.getPropertyType().getName().equals("java.lang.Byte")) {
                        if (null != value) {
                            value =  ((Integer) value).byteValue();
                        }
                    }
                    Object[] args = new Object[1];
                    args[0] = value;
                    descriptor.getWriteMethod().invoke(bean, args);
                }
            }
        } catch (Exception e) {
            log.error("Map转换为PO对象失败. 字段[" + propertyName + "]", e);
        }
        return bean;
    }

    /**
     * 将List<Map>对象转换成List<PO>对象
     *
     * @param mapList
     * @param cls
     * @return
     */
    private List<Po> toPOList(final List<Map<String, Object>> mapList, final Class<? extends Po> cls) {
        List<Po> resultList = new ArrayList<Po>();
        for (Map<String, Object> m : mapList) {
            resultList.add(toPO(m, cls));
        }
        return resultList;
    }
}
