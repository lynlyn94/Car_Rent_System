package com.carrent.service;

import com.carrent.common.PageResult;
import com.carrent.vo.MessageVO;

/**
 * 留言服务
 */
public interface MessageService {

    void submit(String content);

    PageResult<MessageVO> myMessages(Integer current, Integer size);

    PageResult<MessageVO> adminPage(Integer current, Integer size, Integer status);

    void reply(Long id, String reply);
}
