package com.zing.test.dubbo.service;

import lombok.Data;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private T data;

    private boolean success;

    private String msg;

}
