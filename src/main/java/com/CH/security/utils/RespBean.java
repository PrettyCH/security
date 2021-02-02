package com.CH.security.utils;

import com.CH.security.enums.AjaxStatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * 后端给前端返回的对象
 * @param <T>
 */
@Data
public class RespBean<T> {
    private Integer status;
    private String msg = "操作成功";
    private T data;
    @JsonIgnore
    private transient Boolean ok;
    @JsonIgnore
    private transient Boolean error;

    public RespBean() {
    }

    public RespBean(Integer status) {
        this.status = status;
    }

    public RespBean(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public RespBean(T data) {
        this.data = data;
    }

    public RespBean(Integer status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public static RespBean<String> ok() {
        return new RespBean(AjaxStatusEnum.SUCCESS.getCode());
    }

    public static <E> RespBean<E> ok(E E) {
        RespBean<E> ajaxResult = new RespBean(E);
        ajaxResult.setStatus(AjaxStatusEnum.SUCCESS.getCode());
        return ajaxResult;
    }

    public static RespBean<String> error(String msg) {
        RespBean<String> ajaxResult = new RespBean(AjaxStatusEnum.ERROR.getCode());
        ajaxResult.setMsg(msg);
        return ajaxResult;
    }

    public static <E> RespBean<E> error(E E, String msg) {
        RespBean<E> ajaxResult = new RespBean(E);
        ajaxResult.setStatus(AjaxStatusEnum.ERROR.getCode());
        ajaxResult.setMsg(msg);
        ajaxResult.setData(E);
        return ajaxResult;
    }

    public static RespBean<String> noLogin() {
        RespBean<String> ajaxResult = new RespBean(AjaxStatusEnum.NOLOGIN.getCode());
        ajaxResult.setMsg("用户未登陆");
        return ajaxResult;
    }

    public static RespBean<String> noLogin(String msg) {
        RespBean<String> ajaxResult = new RespBean(AjaxStatusEnum.NOLOGIN.getCode());
        ajaxResult.setMsg(msg);
        return ajaxResult;
    }

    public static RespBean<String> notFound() {
        RespBean<String> ajaxResult = new RespBean(AjaxStatusEnum.ERROR.getCode());
        ajaxResult.setMsg("资源未找到");
        return ajaxResult;
    }


    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public boolean isOk() {
        if (this.status == null) {
            this.ok = false;
        }

        this.ok = this.status == AjaxStatusEnum.SUCCESS.getCode();
        return this.ok;
    }

    public Boolean getOk() {
        return this.ok;
    }

    public void setOk(Boolean ok) {
        this.ok = ok;
    }

    public boolean isError() {
        if (this.status == null) {
            this.error = true;
        }

        this.error = this.status != AjaxStatusEnum.SUCCESS.getCode();
        return this.error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }
}
