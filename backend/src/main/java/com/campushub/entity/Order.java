package com.campushub.entity;

import com.campushub.common.enums.OrderType;

import java.util.ArrayList;

public class Order {
    private OrderType orderType;
    private ArrayList<String> args;
    //前端发来的指令类，参数约定：第一个参数为指令名称

    public OrderType getOrderType() {
        return orderType;
    }

    public ArrayList<String> getArgs() {
        return args;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public void setArgs(ArrayList<String> args) {
        this.args = args;
    }
}
