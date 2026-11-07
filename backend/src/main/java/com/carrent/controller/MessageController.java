package com.carrent.controller;

import com.carrent.common.PageResult;
import com.carrent.common.Result;
import com.carrent.dto.MessageCreateRequest;
import com.carrent.service.MessageService;
import com.carrent.vo.MessageVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户留言接口
 */
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public Result<Void> submit(@Valid @RequestBody MessageCreateRequest req) {
        messageService.submit(req.content());
        return Result.success("留言提交成功", null);
    }

    @GetMapping("/my")
    public Result<PageResult<MessageVO>> myMessages(
            @RequestParam(required = false) Integer current,
            @RequestParam(required = false) Integer size) {
        return Result.success(messageService.myMessages(current, size));
    }
}
