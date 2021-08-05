//package com.mei.hui.miner.service;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.mei.hui.miner.model.ProductInfo;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.velocity.Template;
//import org.apache.velocity.VelocityContext;
//import org.apache.velocity.app.Velocity;
//import org.apache.velocity.runtime.RuntimeConstants;
//import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
//import org.apache.velocity.tools.generic.NumberTool;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Service;
//import javax.annotation.PostConstruct;
//import javax.mail.internet.MimeMessage;
//import java.io.IOException;
//import java.io.StringWriter;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Properties;
//
//@Service
//@Slf4j
//public class SendMailServiceImpl {
//
//    @Autowired
//    private JavaMailSender javaMailSender;
//
//    /**
//     velocity模板
//     */
//    private Template template;
//
//    /**
//     * 邮件发送者
//     */
//    @Value("${spring.mail.username}")
//    private String Sender;
//
//    /**
//     * 模板路径
//     */
//    @Value("${template:templates/mail.vm}")
//    private String templateFile;
//
//    /**
//     * 初始化velocity模板
//     */
//    @PostConstruct
//    private void init() {
//        Properties p = new Properties();
//        p.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
//        p.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
//        Velocity.init(p);
//        VelocityContext context = new VelocityContext();
//        context.put("numberTool", new NumberTool());
//        template = Velocity.getTemplate(templateFile, "UTF-8");
//    }
//
//    public List<ProductInfo> getMailData(){
//
//        /**
//         * 获取json数据
//         */
//      /*  String url = "http://sms-agent-cloud.vpc.tencentyun.com/callLogAnalyze/logAnalyze/analyzeAll.do";
//        MultiValueMap<String,String> paramMap = new LinkedMultiValueMap<>();
//        List<String> list = new ArrayList<>();
//        HttpHeaders httpHeaders = new HttpHeaders();
//        String json = HttpClient.sendGetRequest(url, paramMap, httpHeaders);
//*/
//
//        ArrayList<ProductInfo> productInfos = null;
//        String json = "[{\"period\":\"整个放音\",\"averageCostMsec\":9046.88,\"maxCostMsec\":14330,\"minCostSec\":4445,\"distributing\":[{\"from\":null,\"to\":1000,\"count\":0,\"desc\":\"1000毫秒以下\"},{\"from\":1000,\"to\":10000,\"count\":13,\"desc\":\"1000至10000毫秒\"},{\"from\":10000,\"to\":20000,\"count\":12,\"desc\":\"10000至20000毫秒\"},{\"from\":20000,\"to\":30000,\"count\":0,\"desc\":\"20000至30000毫秒\"},{\"from\":30000,\"to\":null,\"count\":0,\"desc\":\"30000毫秒以上\"}]},{\"period\":\"启动识别到检测到声音\",\"averageCostMsec\":8115.895348837209,\"maxCostMsec\":23151,\"minCostSec\":992,\"distributing\":[{\"from\":null,\"to\":1000,\"count\":1,\"desc\":\"1000毫秒以下\"},{\"from\":1000,\"to\":5000,\"count\":36,\"desc\":\"1000至5000毫秒\"},{\"from\":5000,\"to\":10000,\"count\":20,\"desc\":\"5000至10000毫秒\"},{\"from\":10000,\"to\":20000,\"count\":26,\"desc\":\"10000至20000毫秒\"},{\"from\":20000,\"to\":null,\"count\":3,\"desc\":\"20000毫秒以上\"}]},{\"period\":\"整个呼叫\",\"averageCostMsec\":0,\"maxCostMsec\":0,\"minCostSec\":0,\"distributing\":[{\"from\":null,\"to\":10000,\"count\":9,\"desc\":\"10000毫秒以下\"},{\"from\":10000,\"to\":20000,\"count\":0,\"desc\":\"10000至20000毫秒\"},{\"from\":20000,\"to\":40000,\"count\":0,\"desc\":\"20000至40000毫秒\"},{\"from\":40000,\"to\":60000,\"count\":0,\"desc\":\"40000至60000毫秒\"},{\"from\":60000,\"to\":null,\"count\":0,\"desc\":\"60000毫秒以上\"}]},{\"period\":\"任务引擎调用\",\"averageCostMsec\":171.62790697674419,\"maxCostMsec\":200,\"minCostSec\":140,\"distributing\":[{\"from\":null,\"to\":100,\"count\":0,\"desc\":\"100毫秒以下\"},{\"from\":100,\"to\":200,\"count\":38,\"desc\":\"100至200毫秒\"},{\"from\":200,\"to\":300,\"count\":5,\"desc\":\"200至300毫秒\"},{\"from\":300,\"to\":400,\"count\":0,\"desc\":\"300至400毫秒\"},{\"from\":400,\"to\":null,\"count\":0,\"desc\":\"400毫秒以上\"}]},{\"period\":\"整个识别\",\"averageCostMsec\":8773.581395348838,\"maxCostMsec\":23301,\"minCostSec\":1912,\"distributing\":[{\"from\":null,\"to\":1000,\"count\":0,\"desc\":\"1000毫秒以下\"},{\"from\":1000,\"to\":5000,\"count\":18,\"desc\":\"1000至5000毫秒\"},{\"from\":5000,\"to\":15000,\"count\":18,\"desc\":\"5000至15000毫秒\"},{\"from\":15000,\"to\":25000,\"count\":7,\"desc\":\"15000至25000毫秒\"},{\"from\":25000,\"to\":null,\"count\":0,\"desc\":\"25000毫秒以上\"}]}]";
//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            //解析json
//            JsonNode arrNode = new ObjectMapper().readTree(json);
//            productInfos = new ArrayList<>();
//            if(arrNode.isArray()){
//                for (JsonNode node : arrNode) {
//                    ProductInfo productInfo = mapper.readValue(node.toString(), ProductInfo.class);
//                    productInfos.add(productInfo);
//                }
//            }
//        } catch (IOException e) {
//            log.error("getMailData<|>resultJson:"+json,e.getMessage(),e);
//        }
//        return productInfos;
//    }
//
//    /**
//     * 发送velocity模板(HTML)邮件
//     */
//    public void sendMail(String ToMail) {
//
//        System.out.println("接收者:"+ToMail);
//        List<ProductInfo> mailData = getMailData();
//        //填充模板,作为内容
//        final String content = render(mailData);
//        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//        MimeMessageHelper helper = null;
//        try {
//            helper = new MimeMessageHelper(mimeMessage, true);
//
//            helper.setFrom(Sender);
//            helper.setTo(ToMail);
//            helper.setSubject("xx先生请查收!");
//            helper.setText(content, true);
//            javaMailSender.send(mimeMessage);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 填充velocity模板内容
//     * @param productInfo
//     *          内容详情
//     * @return
//     */
//    public String render(List<ProductInfo> productInfo) {
//        VelocityContext context = new VelocityContext();
//        context.put("numberTool", new NumberTool());
//        context.put("appInfos", productInfo);
//        StringWriter sw = new StringWriter();
//        this.template.merge(context,sw);
//        return sw.toString();
//    }
//
//
//}