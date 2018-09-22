package webmagic.processor;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;
import webmagic.util.ExcelUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * @version V1.0
 * @author: lin_shen
 * @date: 2018/6/12
 * @Description: TODO
 */

public class ETUPageProcessor implements PageProcessor {
    private static Logger logger=LoggerFactory.getLogger(ETUPageProcessor.class);

    /**
     * 页面URL的正则表达式
     * .是匹配所有的字符，//.表示只匹配一个，//.?同理
     */
    private static String REGEX_PAGE_URL="https://etherscan\\.io/token/generic-tokentxns2\\?contractAddress=0x765b0aaef0ecac2bf23065f559e5ca3b81067993&mode=&p=\\w+";

    private static int PAGE_END = 4000;

    private static int PAGE_BEGIN=2001;

    private static int PAGE_COUNT=25;

    private static Vector<Etu> etuVector=new Vector<>(PAGE_COUNT*(PAGE_END-PAGE_BEGIN+1));
    //配置
    @Override
    public Site getSite() {
        return Site.me().setTimeOut(100_000).setCycleRetryTimes(10).setRetryTimes(10);
    }

    @Override
    public void process(Page page) {
        List<String> targetURL = new ArrayList<String>();
        for (int i = PAGE_BEGIN; i <= PAGE_END; i++){
            //添加到目标url中
            targetURL.add("https://etherscan.io/token/generic-tokentxns2?contractAddress=0x765b0aaef0ecac2bf23065f559e5ca3b81067993&mode=&p=" + i);
        }

        //添加url到请求中
        page.addTargetRequests(targetURL);

        //是图片列表页面
        if (page.getUrl().regex(REGEX_PAGE_URL).match()) {

            for(int j=2;j<=26;j++){
                Selectable pageSelect= page.getHtml().xpath("//*[@id=\"maindiv\"]/table/tbody/tr["+j+"]");

                if(StringUtils.isBlank(pageSelect.toString())){
                    logger.warn("when etus.size="+etuVector.size()+"  pageSelect is blank !");
                    logger.warn(page.getUrl().toString());
                    logger.warn(pageSelect.toString());
                    continue;
                }

                Etu etu=new Etu();


                etu.txHash=pageSelect.xpath("/tr/td[1]/span/a/text()").get();
                if(StringUtils.isBlank(etu.txHash)){
                    logger.warn("when etus.size="+etuVector.size()+"  blank !");
                    continue;
                }
                etu.date=pageSelect.xpath("/tr/td[2]/span/@title").get();
                etu.from=pageSelect.xpath("/tr/td[3]/span/a/text()").get();
                etu.to=pageSelect.xpath("/tr/td[5]/span/a/text()").get();
                etu.quantity=pageSelect.xpath("/tr/td[6]/text()").get();

                etuVector.add(etu);
            }


        }

        System.out.println(etuVector.size());

    }

    class Etu{
        public String txHash;
        public String date;
        public String from;
        public String to;
        public String quantity;
    }

    public static  void main(String[]arv){
        //起始URL，开启的线程数为10个
        Spider.create(new ETUPageProcessor()).addUrl("https://etherscan.io/token/generic-tokentxns2").thread(3).run();

        HSSFWorkbook excel=getExcel(etuVector);

        try {
            excel.write(new File("etherscan-page"+PAGE_BEGIN+"-"+PAGE_END+".xls"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static HSSFWorkbook  getExcel(List<Etu> etus){
        System.out.println("开始执行getExcel,条数为:"+etus.size());
        //excel标题
        String[] title=new String[]{"TxHash","Date","From","To","Quantity"};
        String[][]content=new String[etus.size()][];
        for (int i = 0; i < etus.size(); i++) {
            content[i]=new String[title.length];
            content[i][0]= etus.get(i).txHash;
            content[i][1]= etus.get(i).date;
            content[i][2]= etus.get(i).from;
            content[i][3]= etus.get(i).to;
            content[i][4]= etus.get(i).quantity;
        }
        HSSFWorkbook excel = ExcelUtil.getHSSFWorkbook("用户信息表", title, content);
        return excel;
    }
}
