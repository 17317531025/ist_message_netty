package com.ist.message.common.util;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MapperUtils {
    /**
     * 默认字段工厂
     */
    private static final MapperFactory MAPPER_FACTORY = new DefaultMapperFactory.Builder().mapNulls(false).build();

    /**
     * 默认字段实例
     */
    private static final MapperFacade MAPPER_FACADE = MAPPER_FACTORY.getMapperFacade();

    /**
     * 默认字段实例集合
     */
    private static Map<String, MapperFacade> CACHE_MAPPER_FACADE_MAP = new ConcurrentHashMap<>();


    /**
     * 映射实体（默认字段）
     *
     * @param source 源数据
     * @param dest 目标数据
     * @return 映射类对象
     */
    public static <S, D> void map(S source, D dest) {
        MAPPER_FACADE.map(source, dest);
    }

    /**
     * 映射实体（默认字段）
     *
     * @param source 源数据
     * @param destClass 目标类
     * @return 映射类对象
     */
    public static <S, D> D map(S source, Class<D> destClass) {
        return MAPPER_FACADE.map(source, destClass);
    }

    /**
     * 映射实体（自定义配置）
     *
     * @param source 源数据
     * @param destClass 目标类
     * @param configMap 自定义配置
     * @return 映射类对象
     */
    public static <S, D> D map(S source, Class<D> destClass, Map<String, String> configMap) {
        MapperFacade mapperFacade = getMapperFacade(source.getClass(), destClass, configMap);
        return mapperFacade.map(source, destClass);
    }

    /**
     * 映射集合（默认字段）
     *
     * @param source 源数据（集合）
     * @param destClass 目标类
     * @return 映射类对象
     */
    public static <S, D> List<D> mapAsList(Collection<S> source, Class<D> destClass) {
        return MAPPER_FACADE.mapAsList(source, destClass);
    }


    /**
     * 映射集合（自定义配置）
     *
     * @param source 源数据（集合）
     * @param destClass 目标类
     * @param configMap 自定义配置
     * @return 映射类对象
     */
    public static <S, D> List<D> mapAsList(Collection<S> source, Class<D> destClass, Map<String, String> configMap) {
        S t = source.stream().findFirst().orElse(null);
        if(t!=null) {
            MapperFacade mapperFacade = getMapperFacade(t.getClass(), destClass, configMap);
            return mapperFacade.mapAsList(source, destClass);
        }else{
            return new ArrayList<>();
        }
    }

    public static <S> Map<String, Object> mapAsMap(S source){
        Map<String, Object> map=new HashMap<String, Object>();
        if(source!=null){
            Field[] fields = source.getClass().getDeclaredFields();
            for(Field field:fields){
                field.setAccessible(true);
                try {
                    map.put(field.getName(), field.get(source));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }


    /**
     * 获取自定义映射
     *
     * @param sourceClass 源数据类
     * @param destClass 目标类
     * @param configMap 自定义配置
     * @return 映射类对象
     */
    private static <S, D> MapperFacade getMapperFacade(Class<S> sourceClass, Class<D> destClass, Map<String, String> configMap) {
        String mapKey = sourceClass.getCanonicalName() + "_" + destClass.getCanonicalName();
        MapperFacade mapperFacade = CACHE_MAPPER_FACADE_MAP.get(mapKey);
        if (Objects.isNull(mapperFacade)) {
            MapperFactory factory = new DefaultMapperFactory.Builder().build();
            ClassMapBuilder classMapBuilder = factory.classMap(sourceClass, destClass);
            configMap.forEach(classMapBuilder::field);
            classMapBuilder.byDefault().register();
            mapperFacade = factory.getMapperFacade();
            CACHE_MAPPER_FACADE_MAP.put(mapKey, mapperFacade);
        }
        return mapperFacade;
    }

}
