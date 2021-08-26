//package com.sun.network.httpdns;
//
//import android.content.Context;
//
//import com.alibaba.sdk.android.httpdns.HttpDns;
//import com.alibaba.sdk.android.httpdns.HttpDnsService;
//
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//import java.util.Arrays;
//import java.util.List;
//
//import okhttp3.Dns;
//
///**
// * Date: 2020/8/9
// * Author: SunBinKang
// * Description:
// */
//public class AlibabaHttpDns implements Dns {
//
//    private final HttpDnsService httpDnsService;
//
//    public AlibabaHttpDns(Context context) {
//        httpDnsService = HttpDns.getService(context, "169929");
//    }
//
//    @Override
//    public List<InetAddress> lookup(String hostname) throws UnknownHostException {
//        //通过异步解析接口获取ip
//        String ip = httpDnsService.getIpByHostAsync(hostname);
//        if (ip != null) {
//            //如果IP不为null，直接使用该ip进行网络请求
//            List<InetAddress> inetAddresses = Arrays.asList(InetAddress.getAllByName(ip));
//            return inetAddresses;
//        }
//        //如果返回null，走系统DNS服务器解析域名
//        return Dns.SYSTEM.lookup(hostname);
//    }
//}
