package com.mmall.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.mmall.dao.SysDeptMapper;
import com.mmall.dto.SysDeptLevelDto;
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
        Collections.sort(rootList, new Comparator<SysDeptLevelDto>() {
            @Override
            public int compare(SysDeptLevelDto o1, SysDeptLevelDto o2) {
                return o1.getSeq() - o2.getSeq();
            }
        });

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

}
