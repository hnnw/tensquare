package com.tensquare.base.controller;

import entity.Result;
import entity.StatusCode;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.bind.annotation.RestControllerAdvice;




@RestControllerAdvice
@Slf4j
public class BaseExceptionHandler {


    @ExceptionHandler(value = Exception.class)
    public Result error(Exception e) {
        return new Result(false, StatusCode.ERROR, e.getMessage());
    }
}
