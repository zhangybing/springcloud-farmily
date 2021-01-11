package com.yibing.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 脱离spring cloud,使用Hystrix
 *
 * @author Administrator
 */
public class HystrixClass extends HystrixCommand {

    protected HystrixClass(HystrixCommandGroupKey group) {
        super(group);
    }

    public static void main(String[] args) {
        Future<String> futureResult = new HystrixClass(HystrixCommandGroupKey.Factory.asKey("ext")).queue();
        String result = "";
        try {
            result = futureResult.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println("程序结果：" + result);
    }

    /**
     * 相当于是try操作
     */
    @Override
    protected Object run() throws Exception {
        System.out.println("执行逻辑");
        int i = 1 / 0;
        return "ok";
    }

    /**
     * 相当于是catch操作,即兜底方案
     */
    @Override
    protected Object getFallback() {
        return "降级了，推出兜底方案了......";
    }

}