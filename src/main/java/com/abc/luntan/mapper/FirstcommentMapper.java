package com.abc.luntan.mapper;

import com.abc.luntan.entity.Firstcomment;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;

/**
 * @Entity com.abc.luntan.entity.Firstcomment
 */
public interface FirstcommentMapper extends BaseMapper<Firstcomment> {
    IPage<Firstcomment> listJoinInfoPages(IPage<Firstcomment> page, @Param(Constants.WRAPPER) Wrapper wrapper);
}




