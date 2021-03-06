package com.mmall.service;

import com.google.common.collect.Lists;
import com.mmall.beans.CacheKeyConstants;
import com.mmall.common.RequestHolder;
import com.mmall.dao.SysAclMapper;
import com.mmall.dao.SysRoleAclMapper;
import com.mmall.dao.SysRoleUserMapper;
import com.mmall.model.SysAcl;
import com.mmall.model.SysUser;
import com.mmall.util.JsonMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SysCoreService {

    @Resource
    private SysAclMapper sysAclMapper;

    @Resource
    private SysRoleUserMapper sysRoleUserMapper;

    @Resource
    private SysRoleAclMapper sysRoleAclMapper;

    @Resource
    private SysCacheService sysCacheService;

    public List<SysAcl> getCurrentUserAclList(){
        int userId = RequestHolder.getCurrentUser().getId();
        return getUserAclList(userId);
    }

    public List<SysAcl> getRoleAclList(int roleId){
        List<Integer> aclIdList = sysRoleAclMapper.getAclIdListByRoleIdList(Lists.<Integer>newArrayList(roleId));
        if (CollectionUtils.isEmpty(aclIdList)){
            return Lists.newArrayList();
        }
        return sysAclMapper.getByIdList(aclIdList);
    }

    public List<SysAcl> getUserAclList(int userId){
        if (isSuperAdmin()){
            return sysAclMapper.getAll();
        }
        List<Integer> userRoleIdList = sysRoleUserMapper.getRoleIdListByUserId(userId);
        if(CollectionUtils.isEmpty(userRoleIdList)){
            return Lists.newArrayList();
        }
        List<Integer> userAclList = sysRoleAclMapper.getAclIdListByRoleIdList(userRoleIdList);
        if (CollectionUtils.isEmpty(userAclList)){
            return Lists.newArrayList();
        }
        return sysAclMapper.getByIdList(userAclList);
    }

    public boolean isSuperAdmin(){
        SysUser sysUser = RequestHolder.getCurrentUser();
        //这里是我们自定义的超级管理员规则  实际需要根据具体的项目来定制
        if (sysUser.getMail().contains("admin") || sysUser.getUsername().contains("admin")){
            return true;
        }
        return false;
    }

    //校验访问的url是否有访问权限
    public boolean hasUrlAcl(String url){
        if (isSuperAdmin()){
            return true;
        }
        List<SysAcl> aclList = sysAclMapper.getByUrl(url);
        if (CollectionUtils.isEmpty(aclList)){
            return true;
        }

        //获取当前用户的所有权限点
        List<SysAcl> userAclList = getCurrentUserAclListFromCache();
        Set<Integer> userAclIdSet = userAclList.stream().map(acl -> acl.getId()).collect(Collectors.toSet());
        boolean hasVaildAcl = false;
        //规则: 只要有一个权限点有权限，那么我们就认为有访问权限
        for(SysAcl acl : aclList){
            //判断一个用户是否具有某个权限点的访问权限
            if (acl == null || acl.getStatus() != 1){
                //权限无效
                continue;
            }
            hasVaildAcl = true;
            if(userAclIdSet.contains(acl.getId())){
                return true;
            }
        }
        if (!hasVaildAcl){
            return true;
        }
        return false;
    }

    public List<SysAcl> getCurrentUserAclListFromCache(){
        int userId = RequestHolder.getCurrentUser().getId();
        String cacheValue = sysCacheService.getFromCache(CacheKeyConstants.USER_ACLS, String.valueOf(userId));
        if (StringUtils.isBlank(cacheValue)){
            List<SysAcl> aclList = getCurrentUserAclList();
            if (CollectionUtils.isNotEmpty(aclList)){
                sysCacheService.saveCache(JsonMapper.obj2String(aclList), 600,CacheKeyConstants.USER_ACLS,String.valueOf(userId));
            }
            return aclList;
        }

        return JsonMapper.string2Obj(cacheValue, new TypeReference<List<SysAcl>>() {
        });
    }
}
