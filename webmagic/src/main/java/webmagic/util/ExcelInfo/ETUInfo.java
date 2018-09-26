package webmagic.util.ExcelInfo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;

/**
 * @version V1.0
 * @author: lin_shen
 * @date: 2018/9/26
 * @Description: TODO
 */

@Data
public class ETUInfo extends BaseRowModel {
    @ExcelProperty(index = 0)
    private String txHash;

    @ExcelProperty(index = 1)
    private String date;

    @ExcelProperty(index = 2)
    private String from;

    @ExcelProperty(index = 3)
    private String to;

    @ExcelProperty(index = 4)
    private String quantity;
}
