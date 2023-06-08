package com.abc.luntan.service;

import com.abc.luntan.dto.FirstcommentDTO;
import com.abc.luntan.entity.Firstcomment;
import com.abc.luntan.utils.api.CommonResult;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 */
public interface FirstcommentService extends IService<Firstcomment> {

    CommonResult queryNewFirstComment(Integer current, Integer articleId);

    CommonResult queryHotFirstComment(Integer current, Integer articleId);

    CommonResult detailComment(Long id);

    CommonResult createFirstComment(FirstcommentDTO firstcommentDTO);

    CommonResult likeFirstComment(Long id);
}
