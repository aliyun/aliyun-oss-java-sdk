import java.util.ArrayList;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.SetBucketCORSRequest;
import com.aliyun.oss.model.SetBucketCORSRequest.CORSRule;

public class OSSCorsSample{
	public static String bucketName = "<your bucket name>";
	public static String accessKeyId = "<your access key id>";
	public static String accessKeySecret = "<your access key secret>";
	public static String endPoint = "http://oss.aliyuncs.com/";
	public static void main(String[] args) {
		OSSClient oss = new OSSClient(endPoint, accessKeyId, accessKeySecret);
		oss.createBucket(bucketName);
		//put
		SetBucketCORSRequest request = new SetBucketCORSRequest();
        request.setBucketName(bucketName);
        ArrayList<CORSRule> putCorsRules = new ArrayList<CORSRule>();
        CORSRule corRule = new CORSRule();     //CORS规则的容器,每个bucket最多允许10条规则 
        ArrayList<String> allowedOrigin = new ArrayList<String>();
        allowedOrigin.add( "http://www.b.com");//指定允许跨域请求的来源
        ArrayList<String> allowedMethod = new ArrayList<String>();
        allowedMethod.add("GET");              //指定允许的跨域请求方法(GET/PUT/DELETE/POST/HEAD)
        ArrayList<String> allowedHeader = new ArrayList<String>();
        allowedHeader.add("x-oss-test");       //控制在OPTIONS预取指令中Access-Control-Request-Headers头中指定的header是否允许。
        ArrayList<String> exposedHeader = new ArrayList<String>();
        exposedHeader.add("x-oss-test1");      //指定允许用户从应用程序中访问的响应头
        corRule.setAllowedMethods(allowedMethod);
        corRule.setAllowedOrigins(allowedOrigin);
        corRule.setAllowedHeaders(allowedHeader);
        corRule.setExposeHeaders(exposedHeader);
        corRule.setMaxAgeSeconds(10);          //指定浏览器对特定资源的预取(OPTIONS)请求返回结果的缓存时间,单位为秒。

        putCorsRules.add(corRule);             //最多允许10条规则 
        request.setCorsRules(putCorsRules);
        oss.setBucketCORS(request);
        
        //get
        ArrayList<CORSRule> corsRules;
        corsRules =  (ArrayList<CORSRule>) oss.getBucketCORSRules(bucketName);
        for (CORSRule rule : corsRules) {
            for (String allowedOrigin1 : rule.getAllowedOrigins()) {
                System.out.println(allowedOrigin1);
            }
            for (String allowedMethod1 : rule.getAllowedMethods()) {
                System.out.println(allowedMethod1);
            }   
            
            if (rule.getAllowedHeaders().size() > 0){
                for (String allowedHeader1 : rule.getAllowedHeaders()){
                    System.out.println(allowedHeader1);
                }
            }
            
            if (rule.getExposeHeaders().size() > 0){
                for (String exposeHeader : rule.getExposeHeaders()){
                    System.out.println(exposeHeader);
                }
            }
      
            if ( null != rule.getMaxAgeSeconds()){
                System.out.println(rule.getMaxAgeSeconds());
            }
        } 
        //delete
        oss.deleteBucketCORSRules(bucketName);
	}
}
/*Output:
 *http://www.b.com
 *GET
 *x-oss-test
 *10
 */




