package net.wuxianjie.web.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MyBatisMapper {

    List<MyBatisData> selectAllData();

    void insertData(MyBatisData data);

    void truncateTable();
}
