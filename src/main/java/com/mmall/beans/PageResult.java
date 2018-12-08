package com.mmall.beans;

import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class PageResult<T> {

    private List<T> data = Lists.newArrayList();

    private int total = 0;
}
