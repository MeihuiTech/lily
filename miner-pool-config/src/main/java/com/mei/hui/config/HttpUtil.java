package com.mei.hui.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: HttpPostCommon
 * @Description: TODO(接口调用)
 * @author baohj
 * @date 2014年11月28日 下午1:30:09
 */
@Slf4j
public class HttpUtil {

	private static PoolingHttpClientConnectionManager connMgr;  
	private static RequestConfig requestConfig;  
	private static final int MAX_TIMEOUT = 60000;  

	static {
		// 设置连接池  
		connMgr = new PoolingHttpClientConnectionManager();  
		// 设置连接池大小  
		connMgr.setMaxTotal(100);  
		connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());  
		RequestConfig.Builder configBuilder = RequestConfig.custom();  
		// 设置连接超时  
		configBuilder.setConnectTimeout(MAX_TIMEOUT);  
		// 设置读取超时  
		configBuilder.setSocketTimeout(MAX_TIMEOUT);  
		// 设置从连接池获取连接实例的超时  
		configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);  
		requestConfig = configBuilder.build();  
	}  

	/** 
	 * 发送 GET 请求（支持HTTPS）K-V形式
	 * @param url 
	 * @param params 
	 * @return 
	 */  
	public static String doGet(String url, Map<String, String> params) {
		//组装参数
		StringBuffer param = new StringBuffer();
		if(params != null){
			int i = 0;
			for (String key : params.keySet()) {
				if (i == 0)
					param.append("?");
				else
					param.append("&");
				param.append(key).append("=").append(params.get(key));
				i++;
			}
		}
		return doGet(url,param.toString());
	}

	/**
	 * 发送 GET 请求（支持HTTPS）
	 * @param url
	 * @param queryStr
	 * @return
	 */
	public static String doGet(String url,String queryStr) {
		log.info("@请求地址:"+url);
		String result = null;
		CloseableHttpResponse response = null;
		CloseableHttpClient httpClient = null;
		try {
			httpClient = HttpClients.createDefault();
			if(isHttps(url)){
				httpClient = wrapClient(httpClient);
			}
			url += "?"+queryStr;
			log.info("@请求参数:"+url);
			HttpGet httpget = new HttpGet(url);
			response = httpClient.execute(httpget);
			int statusCode = response.getStatusLine().getStatusCode();
			log.info("@响应状态码：statusCode = " + statusCode);
			if(statusCode == 200){
				HttpEntity entity = response.getEntity();
				result = EntityUtils.toString(entity, "UTF-8");
				log.info("@响应包体：" + result);
			}
		} catch (Exception e) {
			log.error("请求失败", e);
		}finally {
			if(response != null){
				closeResponse(response);
			}
			if(httpClient != null){
				closeClient(httpClient);
			}
		}
		return result;
	}

	/** 
	 * 发送 POST 请求（支持HTTP和https），K-V形式 
	 * @param url API接口URL
	 * @param params 参数map 
	 * @return 
	 */  
	public static String doPost(String url, Map<String, String> params) {
		log.info("@请求地址:"+url);
		String httpStr = null;  
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		try {  
			httpClient = HttpClients.createDefault();
			if(isHttps(url)){
				httpClient = wrapClient(httpClient);
			}
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			if(params != null){
				for (String key : params.keySet()) {
					nameValuePairs.add(new BasicNameValuePair(key,params.get(key)));  
				}
			}
			log.info("@请求参数:"+url);
			System.out.println("@请求参数:"+url);
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
			httpPost.setConfig(requestConfig);  
			response = httpClient.execute(httpPost);  
			int statusCode = response.getStatusLine().getStatusCode();
			log.info("@响应状态码：statusCode = " + statusCode);
	
			if(statusCode == 200){
				HttpEntity entity = response.getEntity();  
				httpStr = EntityUtils.toString(entity, "UTF-8");
				log.info("@响应包体：" + httpStr);
				System.out.println("@响应包体：" + httpStr);
			}
		} catch (Exception e) {
			log.error("请求失败", e);
		} finally {  
			if(response != null){
				closeResponse(response);
			}
			if(httpClient != null){
				closeClient(httpClient);
			}
		}  
		return httpStr;  
	}  
	
	
	public static String doPost(String url,String json,Map<String, String> heads) {
		log.info("@请求地址:"+url);
		log.info("@请求包体:"+json);
		String httpStr = null;  
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;  
		try { 
			httpClient = HttpClients.createDefault();
			if(isHttps(url)){
				httpClient = wrapClient(httpClient);
			}
			HttpPost httpPost = new HttpPost(url); 
			//头信息
			if(heads != null){
				for (String key : heads.keySet()) {
					httpPost.setHeader(key, heads.get(key));
				}
			}
			httpPost.setConfig(requestConfig);  
			if(StringUtils.isNotEmpty(json)){
				StringEntity stringEntity = new StringEntity(json,"UTF-8");//解决中文乱码问题
				stringEntity.setContentEncoding("UTF-8");  
				stringEntity.setContentType("application/json");  
				httpPost.setEntity(stringEntity);
			}
			response = httpClient.execute(httpPost);  
			int statusCode = response.getStatusLine().getStatusCode();
			log.info("@响应状态码：statusCode = {}",statusCode);
			if(statusCode == 200){
				HttpEntity entity = response.getEntity();  
				httpStr = EntityUtils.toString(entity, "UTF-8");
				log.info("@响应包体：{}",httpStr);
			}
		} catch (Exception e) {
			log.error("请求失败", e);
		} finally {  
			if(response != null){
				closeResponse(response);
			}
			if(httpClient != null){
				closeClient(httpClient);
			}
		}  
		return httpStr; 
	}

	/** 
	 * 发送 POST 请求（支持HTTP和https），JSON形式 
	 * @param url
	 * @param json json对象 
	 * @return 
	 */  
	public static String doPost(String url, String json) {
		return doPost(url,json,null);
	}
	

	/** 
	 * 避免HttpClient的”SSLPeerUnverifiedException: peer not authenticated”异常 
	 * 不用导入SSL证书
	 * @return 
	 */  
	public static CloseableHttpClient wrapClient(CloseableHttpClient httpclient) {  
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");  
			X509TrustManager tm = new X509TrustManager() {  
				public X509Certificate[] getAcceptedIssuers() {  
					return null;  
				}  

				public void checkClientTrusted(X509Certificate[] arg0,  
						String arg1) throws CertificateException {  
				}  

				public void checkServerTrusted(X509Certificate[] arg0,  
						String arg1) throws CertificateException {  
				}  
			};  
			ctx.init(null, new TrustManager[] { tm }, null);  
			SSLConnectionSocketFactory ssf = new SSLConnectionSocketFactory(ctx,NoopHostnameVerifier.INSTANCE);  
			httpclient = HttpClients.custom().setSSLSocketFactory(ssf).build();  
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return httpclient;
	}  

	/**
	 * 判断网路访问是否为https,如果是则返回true
	 * @param url
	 * @return
	 */
	private static boolean isHttps(String url){
		String scheme = url.substring(0,url.indexOf(":"));
		if("HTTPS".equals(scheme.toUpperCase())){
			return true;
		}
		return false;
	}
	/**
	 * 
	 * @Description: TODO(关闭CloseableHttpClient对象) 
	 * @param @param httpClient    
	 * @return void    返回类型 
	 * @throws
	 */
	public static void closeClient(CloseableHttpClient httpClient){
		if(httpClient != null){
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 
	 * @Description: TODO(关闭CloseableHttpResponse对象) 
	 * @param @param response    
	 * @return void    返回类型 
	 * @throws
	 */
	public static void closeResponse(CloseableHttpResponse response){
		if(response != null){
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

