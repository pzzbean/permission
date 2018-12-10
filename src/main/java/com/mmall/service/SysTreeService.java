package com.mmall.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.mmall.dao.SysAclModuleMapper;
import com.mmall.dao.SysDeptMapper;
import com.mmall.dto.AclModuleLevelDto;
import com.mmall.dto.SysDeptLevelDto;
import com.mmall.model.SysAclModule;
import com.mmall.model.SysDept;
import com.mmall.util.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class SysTreeService {

    @Resource
    private SysDeptMapper sysDeptMapper;

    @Resource
    private SysAclModuleMapper sysAclModuleMapper;

    public List<AclModuleLevelDto> aclModuleTree(){
        List<SysAclModule> aclModuleList = sysAclModuleMapper.getAllAclModule();

        List<AclModuleLevelDto> dtoList = Lists.newArrayList();

        for(SysAclModule aclModule : aclModuleList){
            AclModuleLevelDto dto = AclModuleLevelDto.adapt(aclModule);
            dtoList.add(dto);
        }
        return aclModuleListToTree(dtoList);
    }

    public List<AclModuleLevelDto> aclModuleListToTree(List<AclModuleLevelDto> aclModuleLevelList){
        if (CollectionUtils.isEmpty(aclModuleLevelList)){
            return  Lists.newArrayList();
        }

        // level --> [aclModule1,aclModule2] //树的核心组装
        Multimap<String,AclModuleLevelDto> levelAclModuleMap = ArrayListMultimap.create();
        List<AclModuleLevelDto> rootList = Lists.newArrayList();

        for (AclModuleLevelDto dto : aclModuleLevelList){
            levelAclModuleMap.put(dto.getLevel(),dto);
            if (LevelUtil.ROOT.equals(dto.getLevel())){
                rootList.add(dto);
            }
        }

        //按seq从小到大排序
        Collections.sort(rootList, aclModuleSeqComparator);

        //递归生成树
        transformAclModuleTree(rootList, LevelUtil.ROOT , levelAclModuleMap);
        return  rootList;
    }

    public void transformAclModuleTree(List<AclModuleLevelDto> aclModuleLevelList, String level, Multimap<String,AclModuleLevelDto> levelAclModuleMap){

        for(int i = 0; i< aclModuleLevelList.size(); i++){
            //遍历该层每个元素
            AclModuleLevelDto aclModuleLevelDto = aclModuleLevelList.get(i);
            //处理当前层级的数据
            String nextLevel = LevelUtil.calculateLevel(level, aclModuleLevelDto.getId());
            //处理下一层
            List<AclModuleLevelDto> tempList = (List<AclModuleLevelDto>)levelAclModuleMap.get(nextLevel);
            if (CollectionUtils.isNotEmpty(tempList)){
                //排序
                Collections.sort(tempList,aclModuleSeqComparator);
                //设置下一层部门
                aclModuleLevelDto.setAclModuleList(tempList);
                //进入下一层处理
                transformAclModuleTree(tempList,nextLevel,levelAclModuleMap);
            }
        }
    }



    public List<SysDeptLevelDto> deptTree(){
        List<SysDept> deptList = sysDeptMapper.getAllDept();

        List<SysDeptLevelDto> dtoList = Lists.newArrayList();

        for(SysDept dept : deptList){
            SysDeptLevelDto dto = SysDeptLevelDto.adapt(dept);
            dtoList.add(dto);
        }

        return deptListToTree(dtoList);
    }

    public List<SysDeptLevelDto> deptListToTree(List<SysDeptLevelDto> deptLevelList){
        if (CollectionUtils.isEmpty(deptLevelList)){
            return  Lists.newArrayList();
        }

        // level --> [dept1,dept2] //树的核心组装
        Multimap<String,SysDeptLevelDto> levelDeptMap = ArrayListMultimap.create();
        List<SysDeptLevelDto> rootList = Lists.newArrayList();

        for (SysDeptLevelDto dto : deptLevelList){
            levelDeptMap.put(dto.getLevel(),dto);
            if (LevelUtil.ROOT.equals(dto.getLevel())){
                rootList.add(dto);
            }
        }

        //按seq从小到大排序
        Collections.sort(rootList, deptSeqComparator);

        //递归生成树
        transformDeptTree(rootList, LevelUtil.ROOT , levelDeptMap);
        return  rootList;
    }

    public void transformDeptTree(List<SysDeptLevelDto> deptLevelList, String level, Multimap<String,SysDeptLevelDto> levelDeptMap){

        for(int i = 0; i< deptLevelList.size(); i++){
            //遍历该层每个元素
            SysDeptLevelDto deptLevelDto = deptLevelList.get(i);
            //处理当前层级的数据
            String nextLevel = LevelUtil.calculateLevel(level, deptLevelDto.getId());
            //处理下一层
            List<SysDeptLevelDto> tempDeptList = (List<SysDeptLevelDto>)levelDeptMap.get(nextLevel);
            if (CollectionUtils.isNotEmpty(tempDeptList)){
                //排序
                Collections.sort(tempDeptList,deptSeqComparator);
                //设置下一层部门
                deptLevelDto.setDeptList(tempDeptList);
                //进入下一层处理
                transformDeptTree(tempDeptList,nextLevel,levelDeptMap);
            }
        }
    }

    public Comparator<SysDeptLevelDto> deptSeqComparator = new Comparator<SysDeptLevelDto>() {
        @Override
        public int compare(SysDeptLevelDto o1, SysDeptLevelDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    };

    public Comparator<AclModuleLevelDto> aclModuleSeqComparator = new Comparator<AclModuleLevelDto>() {
        @Override
        public int compare(AclModuleLevelDto o1, AclModuleLevelDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    };


}
