package com.aijuejin.webchat.entity.po;

/**
 * @Description:
 * @Title: ProxySSEPO
 * @Package com.aijuejin.webchat.entity.po
 * @Author: zhaozhiyong
 * @Copyright 版权归**企业（或个人）所有
 * @CreateTime: 2023/5/20 18:20
 */
public class ProxySSEPO {

    private static final String DONE_DATA = "[DONE]";

    private final String data;

    public ProxySSEPO(String data){
        this.data = data;
    }

    public String getData(){
        return this.data;
    }

    public byte[] toBytes(){
        return String.format("data: %s\n\n", this.data).getBytes();
    }

    public boolean isDone(){
        return DONE_DATA.equalsIgnoreCase(this.data);
    }
}
