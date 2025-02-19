package com.shujiai.ka.model;

import java.io.Serializable;

/**
 * @author hxh
 * @date 2023/11/4
 * @time 下午7:5747
 */

public class BaseMsgModel implements Serializable {

    private static final long serialVersionUID = -3865362025332415153L;

    private String msgKey;

    public String getMsgKey() {
        return msgKey;
    }

    public void setMsgKey(String msgKey) {
        this.msgKey = msgKey;
    }
}
