package com.zaiou.web.controller.system;

import com.zaiou.common.enums.ExportTempFileEnum;
import com.zaiou.web.common.exportFile.ExportUtils;
import com.zaiou.web.vo.ExportResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Description: 导出控制类
 * @auther: LB 2018/11/6 16:24
 * @modify: LB 2018/11/6 16:24
 */
@RestController
@Slf4j
@RequestMapping("/export")
public class ExportController {
    @Autowired
    private ExportUtils exportUtils;

    /**
     * 导出excel测试
     * @param
     * @param request
     * @param response
     */
    @RequestMapping(value = "/testExport", method = { RequestMethod.GET })
    public void testExport(HttpServletRequest request,HttpServletResponse response) {
        ExportResp exportResp = new ExportResp();
        exportResp.setCustAge("23");
        exportResp.setCustName("刘彬");
        List<ExportResp> list = new ArrayList<>();
        list.add(exportResp);

        HashMap<String, Object> params = new HashMap<>(2);
        params.put("data", list);
        try {
            // 导出excel
            exportUtils.exportExcelMultipleFile(params, ExportTempFileEnum.export_1001, response,"data");
            // 导出pdf
            exportUtils.exportExcelToPdf(params, ExportTempFileEnum.export_1001, response);
        } catch (Exception e) {
            log.info("导出失败");
            e.printStackTrace();
        }
    }
}
