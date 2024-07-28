package com.online.shopping.assist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    private String content;
    private String url;

    public Message(String content) {
        this.content = content;
    }

}
