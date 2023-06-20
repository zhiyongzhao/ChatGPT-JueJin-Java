package com.aijuejin.webchat.common.aspect;

import com.aijuejin.webchat.common.base.R;
import com.aijuejin.webchat.common.constant.ApiCodeConstant;
import com.aijuejin.webchat.common.exception.OpenAiApiRequestTooManyRRException;
import com.aijuejin.webchat.common.exception.RRException;
import com.aijuejin.webchat.common.exception.UnauthorizedRRException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:全局异常处理
 * @Title: GlobalExceptionAdvice
 * @Package com.aijuejin.webchat.common.aspect
 * @Author: zhaozhiyong
 * @Copyright 版权归**企业（或个人）所有
 * @CreateTime: 2023/5/26 23:30
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice {
    @ExceptionHandler(RRException.class)
    public ResponseEntity<R> map(RRException e) {
        return ResponseEntity.status(e.getHttpStatus()).body(R.error(e.getMessage()));
    }

    @ExceptionHandler(UnauthorizedRRException.class)
    public R map(UnauthorizedRRException e) {
        return R.builder()
                .status("Unauthorized")
                .msg(e.getMessage())
                .build();
    }

    @ExceptionHandler({OpenAiApiRequestTooManyRRException.class})
    public R limitTimesException(HttpServletRequest request, Exception e) throws Exception {
        log.info("Handler OpenAiApiRequestTooManyRRException ");
        return   R.error(ApiCodeConstant.LIMITERROR.getCode(),ApiCodeConstant.LIMITERROR.getMsg());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public R map(HttpMessageNotReadableException e) {
        return R.error("bad request");
    }

    @ExceptionHandler(BindException.class)
    public R map(BindException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        Map<String, Object> error = this.getValidError(fieldErrors);
        return R.error(error.get("errorMsg").toString());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R map(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        Map<String, Object> error = this.getValidError(fieldErrors);
        return R.error(error.get("errorMsg").toString());
    }

    private Map<String, Object> getValidError(List<FieldError> fieldErrors) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        List<String> errorList = new ArrayList<String>();
        StringBuffer errorMsg = new StringBuffer();
        for (FieldError error : fieldErrors) {
            errorList.add(error.getDefaultMessage());
            errorMsg.append(error.getDefaultMessage());
            // first
            break;
        }
        map.put("errorList", errorList);
        map.put("errorMsg", errorMsg);
        return map;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public R map(MethodArgumentTypeMismatchException e) {
        return R.error(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public R map(Exception e) {
        return R.error();
    }
}
