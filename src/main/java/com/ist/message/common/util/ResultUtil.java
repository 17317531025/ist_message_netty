package com.ist.message.common.util;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletResponse;

import com.ist.message.common.pojo.ResultVO;
import com.ist.message.config.MessageEnum;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ResultUtil {

    public static void buildResponseForExcel(HttpServletResponse response, String fileName, HSSFWorkbook wb){
        //响应到客户端
        try {
            fileName = new String(fileName.getBytes(), StandardCharsets.ISO_8859_1);
            response.setContentType("application/octet-stream;charset=ISO-8859-1");
            response.setHeader("Content-Disposition", "attachment;filename="+ fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
            OutputStream os = response.getOutputStream();
            wb.write(os);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ResultVO okResultVO(Object data){
        return new ResultVO(data);
    }
    public static ResultVO errorResultVO(MessageEnum.ResultEnum resultEnum, String msg){
    	return new ResultVO(resultEnum.OPERATE_FAIL.getCode()+"", msg);
    }


}
