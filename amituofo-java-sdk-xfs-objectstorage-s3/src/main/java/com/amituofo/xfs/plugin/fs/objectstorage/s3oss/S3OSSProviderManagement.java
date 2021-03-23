package com.amituofo.xfs.plugin.fs.objectstorage.s3oss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amituofo.common.util.StringUtils;

public class S3OSSProviderManagement {
	private final static String ossProviderString = 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Default Endpoint,oss-cn-hangzhou,oss-cn-hangzhou.aliyuncs.com,华东1（杭州）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Default Endpoint,oss-cn-shanghai,oss-cn-shanghai.aliyuncs.com,华东2（上海）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Default Endpoint,oss-cn-qingdao,oss-cn-qingdao.aliyuncs.com,华北1（青岛）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Default Endpoint,oss-cn-beijing,oss-cn-beijing.aliyuncs.com,华北2（北京）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Default Endpoint,oss-cn-zhangjiakou,oss-cn-zhangjiakou.aliyuncs.com,华北 3（张家口）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Default Endpoint,oss-cn-huhehaote,oss-cn-huhehaote.aliyuncs.com,华北5（呼和浩特）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Default Endpoint,oss-cn-wulanchabu,oss-cn-wulanchabu.aliyuncs.com,华北6（乌兰察布）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Default Endpoint,oss-cn-shenzhen,oss-cn-shenzhen.aliyuncs.com,华南1（深圳）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Default Endpoint,oss-cn-heyuan,oss-cn-heyuan.aliyuncs.com,华南2（河源）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Default Endpoint,oss-cn-chengdu,oss-cn-chengdu.aliyuncs.com,西南1（成都）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Default Endpoint,oss-cn-hongkong,oss-cn-hongkong.aliyuncs.com,中国（香港）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Default Endpoint,oss-us-west-1,oss-us-west-1.aliyuncs.com,美国西部1（硅谷）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Default Endpoint,oss-us-east-1,oss-us-east-1.aliyuncs.com,美国东部1（弗吉尼亚）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Default Endpoint,oss-ap-southeast-1,oss-ap-southeast-1.aliyuncs.com,亚太东南1（新加坡）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Default Endpoint,oss-ap-southeast-2,oss-ap-southeast-2.aliyuncs.com,亚太东南2（悉尼）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Default Endpoint,oss-ap-southeast-3,oss-ap-southeast-3.aliyuncs.com,亚太东南3（吉隆坡）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Default Endpoint,oss-ap-southeast-5,oss-ap-southeast-5.aliyuncs.com,亚太东南5（雅加达）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Default Endpoint,oss-ap-northeast-1,oss-ap-northeast-1.aliyuncs.com,亚太东北1（日本）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Default Endpoint,oss-ap-south-1,oss-ap-south-1.aliyuncs.com,亚太南部1（孟买）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Default Endpoint,oss-eu-central-1,oss-eu-central-1.aliyuncs.com,欧洲中部1（法兰克福）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Default Endpoint,oss-eu-west-1,oss-eu-west-1.aliyuncs.com,英国（伦敦）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Default Endpoint,oss-me-east-1,oss-me-east-1.aliyuncs.com,中东东部1（迪拜）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Accelerate Endpoint,oss-cn-accelerate,oss-accelerate.aliyuncs.com,全球传输加速,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Accelerate Endpoint,oss-cn-accelerate,oss-accelerate.aliyuncs.com,海外传输加速,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Ecs Endpoint,oss-cn-hangzhou,oss-cn-hangzhou-internal.aliyuncs.com,华东1（杭州）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Ecs Endpoint,oss-cn-shanghai,oss-cn-shanghai-internal.aliyuncs.com,华东2（上海）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Ecs Endpoint,oss-cn-qingdao,oss-cn-qingdao-internal.aliyuncs.com,华北1（青岛）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Ecs Endpoint,oss-cn-beijing,oss-cn-beijing-internal.aliyuncs.com,华北2（北京）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Ecs Endpoint,oss-cn-zhangjiakou,oss-cn-zhangjiakou-internal.aliyuncs.com,华北 3（张家口）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Ecs Endpoint,oss-cn-huhehaote,oss-cn-huhehaote-internal.aliyuncs.com,华北5（呼和浩特）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Ecs Endpoint,oss-cn-wulanchabu,oss-cn-wulanchabu-internal.aliyuncs.com,华北6（乌兰察布）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Ecs Endpoint,oss-cn-shenzhen,oss-cn-shenzhen-internal.aliyuncs.com,华南1（深圳）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Ecs Endpoint,oss-cn-heyuan,oss-cn-heyuan-internal.aliyuncs.com,华南2（河源）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Ecs Endpoint,oss-cn-chengdu,oss-cn-chengdu-internal.aliyuncs.com,西南1（成都）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Ecs Endpoint,oss-cn-hongkong,oss-cn-hongkong-internal.aliyuncs.com,中国（香港）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Ecs Endpoint,oss-us-west-1,oss-us-west-1-internal.aliyuncs.com,美国西部1（硅谷）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Ecs Endpoint,oss-us-east-1,oss-us-east-1-internal.aliyuncs.com,美国东部1（弗吉尼亚）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Ecs Endpoint,oss-ap-southeast-1,oss-ap-southeast-1-internal.aliyuncs.com,亚太东南1（新加坡）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Ecs Endpoint,oss-ap-southeast-2,oss-ap-southeast-2-internal.aliyuncs.com,亚太东南2（悉尼）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Ecs Endpoint,oss-ap-southeast-3,oss-ap-southeast-3-internal.aliyuncs.com,亚太东南3（吉隆坡）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Ecs Endpoint,oss-ap-southeast-5,oss-ap-southeast-5-internal.aliyuncs.com,亚太东南5（雅加达）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Ecs Endpoint,oss-ap-northeast-1,oss-ap-northeast-1-internal.aliyuncs.com,亚太东北1（日本）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Ecs Endpoint,oss-ap-south-1,oss-ap-south-1-internal.aliyuncs.com,亚太南部1（孟买）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Ecs Endpoint,oss-eu-central-1,oss-eu-central-1-internal.aliyuncs.com,欧洲中部1（法兰克福）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Ecs Endpoint,oss-eu-west-1,oss-eu-west-1-internal.aliyuncs.com,英国（伦敦）,,\r\n" + 
			  "Aliyun,Aliyun Object Storage Service（阿里对象存储）,Ecs Endpoint,oss-me-east-1,oss-me-east-1-internal.aliyuncs.com,中东东部1（迪拜）,,\r\n" + 
			  "Tencent,Tencent Cloud Object Storage（腾讯对象存储COS）,Default Endpoint,ap-beijing-1,cos.ap-beijing-1.myqcloud.com,北京一区,,\r\n" + 
			  "Tencent,Tencent Cloud Object Storage（腾讯对象存储COS）,Default Endpoint,ap-beijing,cos.ap-beijing.myqcloud.com,北京,,\r\n" + 
			  "Tencent,Tencent Cloud Object Storage（腾讯对象存储COS）,Default Endpoint,ap-nanjing,cos.ap-nanjing.myqcloud.com,南京,,\r\n" + 
			  "Tencent,Tencent Cloud Object Storage（腾讯对象存储COS）,Default Endpoint,ap-shanghai,cos.ap-shanghai.myqcloud.com,上海,,\r\n" + 
			  "Tencent,Tencent Cloud Object Storage（腾讯对象存储COS）,Default Endpoint,ap-guangzhou,cos.ap-guangzhou.myqcloud.com,广州,,\r\n" + 
			  "Tencent,Tencent Cloud Object Storage（腾讯对象存储COS）,Default Endpoint,ap-chengdu,cos.ap-chengdu.myqcloud.com,成都,,\r\n" + 
			  "Tencent,Tencent Cloud Object Storage（腾讯对象存储COS）,Default Endpoint,ap-chongqing,cos.ap-chongqing.myqcloud.com,重庆,,\r\n" + 
			  "Tencent,Tencent Cloud Object Storage（腾讯对象存储COS）,Default Endpoint,ap-shenzhen-fsi,cos.ap-shenzhen-fsi.myqcloud.com,深圳金融,,\r\n" + 
			  "Tencent,Tencent Cloud Object Storage（腾讯对象存储COS）,Default Endpoint,ap-shanghai-fsi,cos.ap-shanghai-fsi.myqcloud.com,上海金融,,\r\n" + 
			  "Tencent,Tencent Cloud Object Storage（腾讯对象存储COS）,Default Endpoint,ap-beijing-fsi,cos.ap-beijing-fsi.myqcloud.com,北京金融,,\r\n" + 
			  "Tencent,Tencent Cloud Object Storage（腾讯对象存储COS）,Default Endpoint,ap-hongkong,cos.ap-hongkong.myqcloud.com,中国香港,,\r\n" + 
			  "Tencent,Tencent Cloud Object Storage（腾讯对象存储COS）,Default Endpoint,ap-singapore,cos.ap-singapore.myqcloud.com,新加坡,,\r\n" + 
			  "Tencent,Tencent Cloud Object Storage（腾讯对象存储COS）,Default Endpoint,ap-mumbai,cos.ap-mumbai.myqcloud.com,孟买,,\r\n" + 
			  "Tencent,Tencent Cloud Object Storage（腾讯对象存储COS）,Default Endpoint,ap-seoul,cos.ap-seoul.myqcloud.com,首尔,,\r\n" + 
			  "Tencent,Tencent Cloud Object Storage（腾讯对象存储COS）,Default Endpoint,ap-bangkok,cos.ap-bangkok.myqcloud.com,曼谷,,\r\n" + 
			  "Tencent,Tencent Cloud Object Storage（腾讯对象存储COS）,Default Endpoint,ap-tokyo,cos.ap-tokyo.myqcloud.com,东京,,\r\n" + 
			  "Tencent,Tencent Cloud Object Storage（腾讯对象存储COS）,Default Endpoint,na-siliconvalley,cos.na-siliconvalley.myqcloud.com,北美,,\r\n" + 
			  "Tencent,Tencent Cloud Object Storage（腾讯对象存储COS）,Default Endpoint,na-ashburn,cos.na-ashburn.myqcloud.com,弗吉尼亚,,\r\n" + 
			  "Tencent,Tencent Cloud Object Storage（腾讯对象存储COS）,Default Endpoint,na-toronto,cos.na-toronto.myqcloud.com,多伦多,,\r\n" + 
			  "Tencent,Tencent Cloud Object Storage（腾讯对象存储COS）,Default Endpoint,eu-frankfurt,cos.eu-frankfurt.myqcloud.com,法兰克福,,\r\n" + 
			  "Tencent,Tencent Cloud Object Storage（腾讯对象存储COS）,Default Endpoint,eu-moscow,cos.eu-moscow.myqcloud.com,莫斯科,,\r\n" + 
			  "Huaweicloud,Huaweicloud Object Storage Service（华为对象存储OSS）,Default Endpoint,obs-default,obs.myhwclouds.com,华为对象存储服务（默认）,,\r\n" + 
			  "Baidu,Baidu Object Storage（百度对象存储BOS）,Default Endpoint,BJ,s3.bj.bcebos.com,北京,,\r\n" + 
			  "Baidu,Baidu Object Storage（百度对象存储BOS）,Default Endpoint,GZ,s3.gz.bcebos.com,广州,,\r\n" + 
			  "Baidu,Baidu Object Storage（百度对象存储BOS）,Default Endpoint,SU,s3.su.bcebos.com,苏州,,\r\n" + 
			  "163yun,Netease Object Storage（网易云对象存储服务NOS）,Default Endpoint,eastchina1,nos-eastchina1.126.net,华东1,,\r\n" + 
			  "163yun,Netease Object Storage（网易云对象存储服务NOS）,Internal Endpoint,eastchina1,nos-eastchina1-i.netease.com,华东1,,\r\n" + 
			  "163yun,Netease Object Storage（网易云对象存储服务NOS）,Default Endpoint,eastchina3,nos-eastchina3.126.net,华东3,,\r\n" + 
			  "163yun,Netease Object Storage（网易云对象存储服务NOS）,Internal Endpoint,eastchina3,nos-eastchina3-i.netease.com,华东3,,\r\n" + 
			  "QingCloud,QingStor（青云对象存储）,Default Endpoint,pek3a,pek3a.qingstor.com,北京3区-A,,\r\n" + 
			  "QingCloud,QingStor（青云对象存储）,Default Endpoint,sh1a,sh1a.qingstor.com,上海1区-A,,\r\n" + 
			  "QingCloud,QingStor（青云对象存储）,Default Endpoint,pek3B,pek3b.qingstor.com,北京3区-B,,\r\n" + 
			  "QingCloud,QingStor（青云对象存储）,Default Endpoint,gd2,gd2.qingstor.com,广东2区,,\r\n" + 
			  "QingCloud,QingStor（青云对象存储）,Default Endpoint,ap3,ap3.qingstor.com,雅加达区,,\r\n" + 
			  "Qiniu,Kodo（七牛云海量存储系统）,Default Endpoint,cn-east-1,s3-cn-east-1.qiniucs.com,华东,,\r\n" + 
			  "Qiniu,Kodo（七牛云海量存储系统）,Default Endpoint,cn-north-1,s3-cn-north-1.qiniucs.com,华北,,\r\n" + 
			  "Qiniu,Kodo（七牛云海量存储系统）,Default Endpoint,cn-south-1,s3-cn-south-1.qiniucs.com,华南,,\r\n" + 
			  "Qiniu,Kodo（七牛云海量存储系统）,Default Endpoint,us-north-1,s3-us-north-1.qiniucs.com,北美,,\r\n" + 
			  "Qiniu,Kodo（七牛云海量存储系统）,Default Endpoint,ap-southeast-1,s3-ap-southeast-1.qiniucs.com,东南亚,,\r\n" + 
			  "Ksyun,Kingsoft Standard Storage Service (金山云对象存储KS3),Default Endpoint,BEIJING,ks3-cn-beijing.ksyun.com,中国（北京）,,\r\n" + 
			  "Ksyun,Kingsoft Standard Storage Service (金山云对象存储KS3),Default Endpoint,SHANGHAI,ks3-cn-shanghai.ksyun.com,中国（上海）,,\r\n" + 
			  "Ksyun,Kingsoft Standard Storage Service (金山云对象存储KS3),Default Endpoint,GUANGZHOU,ks3-cn-guangzhou.ksyun.com,中国（广州）,,\r\n" + 
			  "Ksyun,Kingsoft Standard Storage Service (金山云对象存储KS3),Default Endpoint,QINGDAO,ks3-cn-qingdao.ksyun.com,中国（青岛）,,\r\n" + 
			  "Ksyun,Kingsoft Standard Storage Service (金山云对象存储KS3),Default Endpoint,HONGKONG,ks3-cn-hk-1.ksyun.com,中国（香港）,,\r\n" + 
			  "Ksyun,Kingsoft Standard Storage Service (金山云对象存储KS3),Default Endpoint,HANGZHOU,kss.ksyun.com,中国（杭州）,,\r\n" + 
			  "Ksyun,Kingsoft Standard Storage Service (金山云对象存储KS3),Default Endpoint,RUSSIA,ks3-rus.ksyun.com,俄罗斯,,\r\n" + 
			  "Ksyun,Kingsoft Standard Storage Service (金山云对象存储KS3),Default Endpoint,SINGAPORE,ks3-sgp.ksyun.com,新加坡,,\r\n" + 
			  "Ksyun,Kingsoft Standard Storage Service (金山云对象存储KS3),Default Endpoint,JR_BEIJING,ks3-jr-beijing.ksyun.com,金融专区（北京）,,\r\n" + 
			  "Ksyun,Kingsoft Standard Storage Service (金山云对象存储KS3),Default Endpoint,JR_SHANGHAI,ks3-jr-shanghai.ksyun.com,金融专区（上海）,,\r\n" + 
			  "Ksyun,Kingsoft Standard Storage Service (金山云对象存储KS3),Default Endpoint,GOV_BEIJING,ks3-gov-beijing.ksyun.com,政务专区（北京）,,\r\n" + 
			  "Ksyun,Kingsoft Standard Storage Service (金山云对象存储KS3),Internal Endpoint,BEIJING,ks3-cn-beijing-internal.ksyun.com,中国（北京）,,\r\n" + 
			  "Ksyun,Kingsoft Standard Storage Service (金山云对象存储KS3),Internal Endpoint,SHANGHAI,ks3-cn-shanghai-internal.ksyun.com,中国（上海）,,\r\n" + 
			  "Ksyun,Kingsoft Standard Storage Service (金山云对象存储KS3),Internal Endpoint,GUANGZHOU,ks3-cn-guangzhou-internal.ksyun.com,中国（广州）,,\r\n" + 
			  "Ksyun,Kingsoft Standard Storage Service (金山云对象存储KS3),Internal Endpoint,QINGDAO,ks3-cn-qingdao-internal.ksyun.com,中国（青岛）,,\r\n" + 
			  "Ksyun,Kingsoft Standard Storage Service (金山云对象存储KS3),Internal Endpoint,HONGKONG,ks3-cn-hk-1-internal.ksyun.com,中国（香港）,,\r\n" + 
			  "Ksyun,Kingsoft Standard Storage Service (金山云对象存储KS3),Internal Endpoint,HANGZHOU,kss-internal.ksyun.com,中国（杭州）,,\r\n" + 
			  "Ksyun,Kingsoft Standard Storage Service (金山云对象存储KS3),Internal Endpoint,RUSSIA,ks3-rus-internal.ksyun.com,俄罗斯,,\r\n" + 
			  "Ksyun,Kingsoft Standard Storage Service (金山云对象存储KS3),Internal Endpoint,SINGAPORE,ks3-sgp-internal.ksyun.com,新加坡,,\r\n" + 
			  "Ksyun,Kingsoft Standard Storage Service (金山云对象存储KS3),Internal Endpoint,JR_BEIJING,ks3-jr-beijing-internal.ksyun.com,金融专区（北京）,,\r\n" + 
			  "Ksyun,Kingsoft Standard Storage Service (金山云对象存储KS3),Internal Endpoint,JR_SHANGHAI,ks3-jr-shanghai-internal.ksyun.com,金融专区（上海）,,\r\n" + 
			  "Ksyun,Kingsoft Standard Storage Service (金山云对象存储KS3),Internal Endpoint,GOV_BEIJING,ks3-gov-beijing-internal.ksyun.com,政务专区（北京）,,\r\n" + 
			  "";
	
	// private static Map<String, Map<String, List<S3Endpoint>>> ossProviderMap = new HashMap<String, Map<String, List<S3Endpoint>>>();
	private static Map<String, S3OSSProvider> ossProviderMap = new HashMap<String, S3OSSProvider>();
	private static List<S3OSSProvider> ossProviderList = new ArrayList<S3OSSProvider>();

	static {
		String[] rows = ossProviderString.split("\r\n");
		for (String row : rows) {
			if (StringUtils.isNotEmpty(row)) {
				String[] columns = row.split(",");
				if (columns.length >= 6) {
					String provider = columns[0].trim();
					String providerName = columns[1].trim();
					String type = columns[2].trim();
					String endpointShort = columns[3].trim();
					String endpoint = columns[4].trim();
					String description = columns[5].trim();
					boolean supportHTTP = true;
					if (columns.length > 6) {
						supportHTTP = !"false".equals(columns[6].trim());
					}
					boolean supportHTTPS = true;
					if (columns.length > 7) {
						supportHTTP = !"false".equals(columns[7].trim());
					}

					S3OSSProvider ossProvider = ossProviderMap.get(provider);
					if (ossProvider == null) {
						ossProvider = new S3OSSProvider(provider, providerName);
						ossProviderMap.put(provider, ossProvider);
						ossProviderList.add(ossProvider);
					}

					S3OSSEndpoint s3endpoint = new S3OSSEndpoint(ossProvider, type, endpointShort, endpoint, description, supportHTTP, supportHTTPS);
					ossProvider.addEndpoint(type, s3endpoint);
				}
			}
		}
	}

	// public final static CloudOSSProviderManagement instance = new CloudOSSProviderManagement();

	public S3OSSProviderManagement() {

	}

	public static S3OSSProvider[] getOSSProviders() {
		return ossProviderList.toArray(new S3OSSProvider[ossProviderList.size()]);
	}

	public static S3OSSProvider getOSSProvider(String ossName) {
		return ossProviderMap.get(ossName);
	}

}
