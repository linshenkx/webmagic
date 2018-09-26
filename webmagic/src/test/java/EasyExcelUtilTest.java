import com.alibaba.excel.metadata.Font;
import com.alibaba.excel.metadata.Table;
import com.alibaba.excel.metadata.TableStyle;
import com.alibaba.excel.support.ExcelTypeEnum;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.junit.Test;
import webmagic.util.EasyExcelUtil;
import webmagic.util.ExcelInfo.ETUInfo;

import java.io.*;
import java.util.List;

/**
 * @version V1.0
 * @author: lin_shen
 * @date: 2018/9/26
 * @Description: TODO
 */

public class EasyExcelUtilTest {
    @Test
    public void testReadExcelWithStringList(){
        try(
                InputStream inputStream=new FileInputStream("C:\\git\\webmagic\\target\\etherscan-page1-1000.xls");
                OutputStream outputStream=new FileOutputStream("C:\\git\\webmagic\\target\\etherscan-page1-1000.xlsx")
        ) {
            //读入文件,每一行对应一个List<String>
            List<List<String>> stringLists = EasyExcelUtil.readExcelWithStringList(inputStream, ExcelTypeEnum.XLS);

            //定义Excel正文背景颜色
            TableStyle tableStyle=new TableStyle();
            tableStyle.setTableContentBackGroundColor(IndexedColors.WHITE);

            //定义Excel正文字体大小
            Font font=new Font();
            font.setFontHeightInPoints((short) 10);

            tableStyle.setTableContentFont(font);

            Table table=new Table(0);
            table.setTableStyle(tableStyle);

            EasyExcelUtil.writeExcelWithStringList(outputStream,stringLists,table,ExcelTypeEnum.XLSX);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testReadExcelWithModel(){
        try (
                InputStream inputStream=new FileInputStream("C:\\git\\webmagic\\target\\etherscan-page1-1000.xls");
                OutputStream outputStream=new FileOutputStream("C:\\git\\webmagic\\target\\etherscan-page1-1000model.xlsx") ;
        ) {
            //读入文件,每一行对应一个 Model ,获取Model 列表
            List<Object> objectList = EasyExcelUtil.readExcelWithModel(inputStream,ETUInfo.class,ExcelTypeEnum.XLS);
            List<ETUInfo> etuInfoList=(List)objectList;

            TableStyle tableStyle=new TableStyle();
            tableStyle.setTableContentBackGroundColor(IndexedColors.WHITE);

            Font font=new Font();
            font.setFontHeightInPoints((short) 10);
            tableStyle.setTableContentFont(font);

            Table table=new Table(0);
            table.setTableStyle(tableStyle);

            EasyExcelUtil.writeExcelWithModel(outputStream,etuInfoList,table,ETUInfo.class,ExcelTypeEnum.XLSX);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
