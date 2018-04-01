package com.example.demodeal.utils;

import com.example.demodeal.domain.Result;
import com.example.demodeal.enums.ResultEnum;

public class ResultUtil {

    public static Result error(ResultEnum resultEnum){
        Result result = new Result();
        result.setCode(resultEnum.getCode());
        result.setMsg(resultEnum.getMsg());
        result.setData(null);
        return result;
    }

    public static Result success(ResultEnum resultEnum,Object data){
        Result result = new Result();
        result.setCode(resultEnum.getCode());
        result.setMsg(resultEnum.getMsg());
        result.setData(data);
        return result;
    }


    public static Result error(int i, String defaultMessage) {
        Result result = new Result();
        result.setCode(result.getCode());
        result.setMsg(result.getMsg());
        result.setData(null);
        return result;
    }
}
