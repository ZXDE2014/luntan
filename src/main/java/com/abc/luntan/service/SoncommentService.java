package com.abc.luntan.service;

import com.abc.luntan.dto.SonCommentDTO;
import com.abc.luntan.entity.Soncomment;
import com.abc.luntan.utils.api.CommonResult;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 */
public interface SoncommentService extends IService<Soncomment> {

    CommonResult querySonComment(Integer current, Integer firstCommentId);

    CommonResult detailSonComment(Long id);

    CommonResult likeSonComment(Long id);

    CommonResult createSonComment(SonCommentDTO soncommentDTO);
}
