package com.ist.message.dao;

import com.ist.message.domain.ShopApp;
import com.ist.message.domain.ShopAppExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShopAppMapper {
    long countByExample(ShopAppExample example);

    int deleteByExample(ShopAppExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ShopApp record);

    int insertSelective(ShopApp record);

    List<ShopApp> selectByExample(ShopAppExample example);

    ShopApp selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") ShopApp record, @Param("example") ShopAppExample example);

    int updateByExample(@Param("record") ShopApp record, @Param("example") ShopAppExample example);

    int updateByPrimaryKeySelective(ShopApp record);

    int updateByPrimaryKey(ShopApp record);
}