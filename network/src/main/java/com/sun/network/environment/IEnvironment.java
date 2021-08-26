package com.sun.network.environment;

/**
 * Date: 2020/8/9
 * Author: SunBinKang
 * Description:环境切换
 */
public interface IEnvironment {

    //返回正式环境的URL
    String getFormal();

    //返回测试环境的URL
    String getTest();

}
