package webmagic.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * @version V1.0
 * @author: lin_shen
 * @date: 2018/6/11
 * @Description: TODO
 */

public class TaoBaoMMRepoPageProcessor implements PageProcessor {
    private static Logger logger=LoggerFactory.getLogger(TaoBaoMMRepoPageProcessor.class);
    //列表页正则表达式
    private static final String REGEX_PAGE_URL = "https://mm\\.taobao\\.com/json/request_top_list\\.htm\\?page=\\w+";

    // 爬取的列表页，页数。
    private static final int PAGE_SIZE = 10;

    //配置
    private Site site = Site.me();

    //爬取
    @Override
    public void process(Page page) {
        /**
         * 把所有的列表页都添加到 爬取的目标URL中
         */
        List<String> targetRequests = new ArrayList<String>();
        for (int i = 1;i < PAGE_SIZE; i++){
            targetRequests.add("https://mm.taobao.com/json/request_top_list.htm?page=" + i);
        }
        page.addTargetRequests(targetRequests);
        //用正则匹配是否是列表页
        logger.info("page.getUrl():"+page.getUrl());
        logger.info("REGEX_PAGE_URL:"+REGEX_PAGE_URL);
        if (page.getUrl().regex(REGEX_PAGE_URL).match()) {
            /**
             * 如果是，获取 class为 lady-name 的a 标签 的所有链接(详情页)。
             */
            List<String> urls = page.getHtml().xpath("//a[@class=\"lady-name\"]").links().all();
            for (String url:urls) {
                /**
                 * 获取到的详情页链接，是找不到图片的URL,利用Chrome的控制台，发现数据是从https://mm.taobao.com/self/info/model_info_show.htm?user_id=46599595。下发的。
                 * 把URL替换下，添加到爬取的目标URL,中。
                 */
                page.addTargetRequest(url.replace("self/model_card.htm","self/info/model_info_show.htm"));
            }
        } else {
            // 如果不是,则
            // 获取 class为mm-p-info-cell clearfix 的ul /li /span/文本，作为图片保存图片名。
            String nickname = page.getHtml().xpath("//ul[@class=\"mm-p-info-cell clearfix\"]/li/span/text()").toString();
            // 获取 class为mm-p-modelCard 的div /a /img  src 值，此为图片URL.
            String imgUrl = page.getHtml().xpath("//div[@class=\"mm-p-modelCard\"]/a").css("img","src").toString();
            try {
                // 根据图片URL 下载图片方法
                /**
                 * String 图片URL地址
                 * String 图片名称
                 * String 保存路径
                 */
                logger.info("执行downloa");

                download("http:" + imgUrl,nickname + ".jpg","F:\\image\\");
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public Site getSite() {
        return site;
    }


    public  void download(String urlString, String filename,String savePath) throws Exception {
        logger.info("执行download,URLString:"+urlString+" filename:"+filename+" savePath:"+savePath);
            // 构造URL
            URL url = new URL(urlString);
            // 打开连接
            URLConnection con = url.openConnection();
            //设置请求超时为5s
            con.setConnectTimeout(5*1000);
            // 输入流
            InputStream is = con.getInputStream();

            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            // 输出的文件流
            File sf=new File(savePath);
            if(!sf.exists()){
                sf.mkdirs();
            }
            OutputStream os = new FileOutputStream(sf.getPath()+"\\"+filename);
            // 开始读取
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            // 完毕，关闭所有链接
            os.close();
            is.close();
    }

    public static void main(String[] args) {
        logger.info("开始爬虫------------");
        //启动
        Spider.create(new TaoBaoMMRepoPageProcessor())
                //添加初始化的URL
                .addUrl("https://mm.taobao.com/json/request_top_list.htm?page=1")
                //启动10个线程
                .thread(10)
                //运行
                .run();
    }
}
