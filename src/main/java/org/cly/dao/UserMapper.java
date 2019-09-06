package org.cly.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.cly.entity.User;

import java.util.List;

@Mapper
public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    List<User> selectByPrimaryKey();

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int insertSelect(@Param("ids") List<String> ids ,@Param("tableName") String tableName);


    List<String> selectUsers();
}