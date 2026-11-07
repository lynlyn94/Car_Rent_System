package com.carrent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carrent.common.BusinessException;
import com.carrent.common.PageResult;
import com.carrent.entity.Message;
import com.carrent.entity.User;
import com.carrent.mapper.MessageMapper;
import com.carrent.mapper.UserMapper;
import com.carrent.service.MessageService;
import com.carrent.util.UserContext;
import com.carrent.vo.MessageVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 留言服务实现
 */
@Service
public class MessageServiceImpl implements MessageService {

    private final MessageMapper messageMapper;
    private final UserMapper userMapper;

    public MessageServiceImpl(MessageMapper messageMapper, UserMapper userMapper) {
        this.messageMapper = messageMapper;
        this.userMapper = userMapper;
    }

    @Override
    public void submit(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new BusinessException("留言内容不能为空");
        }
        Message message = new Message();
        message.setUserId(UserContext.getUserId());
        message.setContent(content.trim());
        message.setCreateTime(LocalDateTime.now());
        message.setStatus(0);
        messageMapper.insert(message);
    }

    @Override
    public PageResult<MessageVO> myMessages(Integer current, Integer size) {
        int cur = current == null || current < 1 ? 1 : current;
        int siz = size == null || size < 1 ? 10 : size;
        Page<Message> page = new Page<>(cur, siz);
        LambdaQueryWrapper<Message> qw = new LambdaQueryWrapper<>();
        qw.eq(Message::getUserId, UserContext.getUserId()).orderByDesc(Message::getId);
        Page<Message> result = messageMapper.selectPage(page, qw);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(),
                toVOList(result.getRecords()));
    }

    @Override
    public PageResult<MessageVO> adminPage(Integer current, Integer size, Integer status) {
        int cur = current == null || current < 1 ? 1 : current;
        int siz = size == null || size < 1 ? 10 : size;
        Page<Message> page = new Page<>(cur, siz);
        LambdaQueryWrapper<Message> qw = new LambdaQueryWrapper<>();
        qw.eq(status != null, Message::getStatus, status).orderByDesc(Message::getId);
        Page<Message> result = messageMapper.selectPage(page, qw);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(),
                toVOList(result.getRecords()));
    }

    @Override
    public void reply(Long id, String reply) {
        if (reply == null || reply.trim().isEmpty()) {
            throw new BusinessException("回复内容不能为空");
        }
        Message message = messageMapper.selectById(id);
        if (message == null) {
            throw new BusinessException("留言不存在");
        }
        message.setReply(reply.trim());
        message.setReplyTime(LocalDateTime.now());
        message.setStatus(1);
        messageMapper.updateById(message);
    }

    private List<MessageVO> toVOList(List<Message> messages) {
        List<MessageVO> vos = new ArrayList<>();
        if (messages.isEmpty()) {
            return vos;
        }
        List<Long> userIds = messages.stream().map(Message::getUserId).distinct().collect(Collectors.toList());
        Map<Long, User> userMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        for (Message m : messages) {
            MessageVO vo = new MessageVO();
            BeanUtils.copyProperties(m, vo);
            User u = userMap.get(m.getUserId());
            if (u != null) {
                vo.setUserName(u.getName() != null ? u.getName() : u.getUsername());
            }
            vos.add(vo);
        }
        return vos;
    }
}
