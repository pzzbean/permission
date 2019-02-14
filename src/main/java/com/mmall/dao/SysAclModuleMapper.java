package com.mmall.dao;

import com.mmall.model.SysAclModule;
import com.mmall.model.SysDept;
import com.mmall.param.AclModuleParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysAclModuleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysAclModule record);

    int insertSelective(SysAclModule record);

    SysAclModule selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysAclModule record);

    int updateByPrimaryKey(SysAclModule record);

    List<SysAclModule> getAllAclModule();

    List<SysAclModule> getChildAclModuleListByLevel(@Param("level") String level);

    //批量更新level
    void batchUpdateLevel(@Param("sysAclModuleList") List<SysAclModule> sysAclModuleList);

    int countByNameAndParentId(@Param("parentId") int parentId, @Param("name") String name, @Param("id") Integer id);

    int countByParentId(@Param("aclModuleId") int aclModuleId);
}