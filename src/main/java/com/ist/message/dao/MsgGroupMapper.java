package com.ist.message.dao;

import com.ist.message.domain.MsgGroup;
import com.ist.message.domain.MsgGroupExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MsgGroupMapper {
    long countByExample(MsgGroupExample example);

    int deleteByExample(MsgGroupExample example);

    int deleteByPrimaryKey(Long groupid);

    int insert(MsgGroup record);

    int insertSelective(MsgGroup record);

    List<MsgGroup> selectByExample(MsgGroupExample example);

    MsgGroup selectByPrimaryKey(Long groupid);

    int updateByExampleSelective(@Param("record") MsgGroup record, @Param("example") MsgGroupExample example);

    int updateByExample(@Param("record") MsgGroup record, @Param("example") MsgGroupExample example);

    int updateByPrimaryKeySelective(MsgGroup record);

    int updateByPrimaryKey(MsgGroup record);
}