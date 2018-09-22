package webmagic.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * @version V1.0
 * @author: lin_shen
 * @date: 2018/6/12
 * @Description: TODO
 */

public class GachaPageProcessor implements PageProcessor {
    private static Logger logger=LoggerFactory.getLogger(GachaPageProcessor.class);

    public static final String URL_LIST = "http://gacha.163.com/ranking/pic/day/2018-06-09";

    public static final String URL_POST = "detail/post/";
    private Site site = Site
            .me()
            .setSleepTime(3000);
//            .setUserAgent(
//                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
    @Override
    public void process(Page page) {
        String photoURL = page.getHtml().xpath("//div[@class='m-ikon-first f-fl']/a/div").css("img", "src").toString();
        logger.info("photoURL: "+photoURL);
//        //列表页
//        if (page.getUrl().regex(URL_LIST).match()) {
//            page.addTargetRequests(page.getHtml().xpath("//div[@class=\"m-ikon-first f-fl\"]").links()..regex(URL_POST).all());
//            page.addTargetRequests(page.getHtml().links().regex(URL_LIST).all());
//            //文章页
//        } else {
//            page.putField("title", page.getHtml().xpath("//div[@class='articalTitle']/h2"));
//            page.putField("content", page.getHtml().xpath("//div[@id='articlebody']//div[@class='articalContent']"));
//            page.putField("date",
//                    page.getHtml().xpath("//div[@id='articlebody']//span[@class='time SG_txtc']").regex("\\((.*)\\)"));
//        }
    }

    @Override
    public Site getSite() {
        return site;
    }
    public static void main(String[] args) {
        Spider.create(new GachaPageProcessor()).addUrl("http://gacha.163.com/ranking/pic/day/2018-06-10")
                .run();
    }

}
