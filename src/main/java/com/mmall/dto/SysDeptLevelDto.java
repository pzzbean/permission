package com.mmall.dto;

import com.google.common.collect.Lists;
import com.mmall.model.SysDept;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Getter
@Setter
@ToString
public class SysDeptLevelDto extends SysDept {

    private List<SysDeptLevelDto> deptList = Lists.newArrayList();

    //对部门进行封装
    public static SysDeptLevelDto adapt(SysDept dept){
        SysDeptLevelDto dto = new SysDeptLevelDto();
        BeanUtils.copyProperties(dept,dto);
        return dto;
    }

}
