package com.ist.message.config.kafka;

import lombok.Data;

import java.util.List;

@Data
public class ChatMsg {
    private String receiverId;
    private Integer type;
    private List<MsgData> data;
}
