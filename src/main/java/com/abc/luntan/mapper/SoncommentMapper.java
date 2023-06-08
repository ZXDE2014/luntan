package com.abc.luntan.mapper;

import com.abc.luntan.entity.Soncomment;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;

/**
 * @Entity com.abc.luntan.entity.Soncomment
 */
public interface SoncommentMapper extends BaseMapper<Soncomment> {
    IPage<Soncomment> listJoinInfoPages(IPage<Soncomment> page, @Param(Constants.WRAPPER) Wrapper<Soncomment> queryWrapper);
}




