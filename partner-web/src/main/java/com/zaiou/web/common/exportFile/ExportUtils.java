package com.zaiou.web.common.exportFile;

import com.zaiou.common.enums.ExportTempFileEnum;
import com.zaiou.common.enums.ResultInfo;
import com.zaiou.common.exception.BussinessException;
import com.zaiou.common.utils.FileUtils;
import com.zaiou.common.utils.StreamUtils;
import com.zaiou.common.utils.StringUtils;
import com.zaiou.common.utils.TokenUtils;
import com.zaiou.web.common.bean.CurrentUser;
import com.zaiou.web.vo.system.SysAttachmentResp;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jxls.area.Area;
import org.jxls.builder.AreaBuilder;
import org.jxls.builder.xls.XlsCommentAreaBuilder;
import org.jxls.common.CellRef;
import org.jxls.common.Context;
import org.jxls.transform.Transformer;
import org.jxls.transform.poi.PoiContext;
import org.jxls.transform.poi.PoiTransformer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

/**
 * @Description:导出excel文件
 * @auther: LB 2018/11/6 11:47
 * @modify: LB 2018/11/6 11:47
 */
@Component
@Slf4j
public class ExportUtils {
    private static String fileSuffix = ".xls";// 导出excel文件后缀
    private static String fileSuffixPdf = ".xls";// 导出pdf文件后缀
    private static int excelMaxRowSize = 5000; // 模板导出最大行数

    @Value("${export.template.path}")
    private String exportPath;// 导出模板路径

    @Value("${temp.file.path}")
    private String tempPath;// 临时文件路径

    /**
     *  此方法直接导出时使用 将数据压入到临时模板，输出到前端
     * @param params
     * @param exportTemp
     * @param response
     * @param listName
     * @throws Exception
     */
    public void exportExcelMultipleFile(HashMap<String, Object> params, ExportTempFileEnum exportTemp,
                                        HttpServletResponse response, String listName) throws Exception {
        Object object = params.get(listName);
        long startTime = System.nanoTime();
        if (object instanceof List) {
            try {
                List list = (List) object;
                // 计算导出excel文件的个数
                int fileCount = (int) Math.ceil(Double.valueOf(list.size()) / excelMaxRowSize);
                // 计算余数
                int mod = list.size() % excelMaxRowSize;
                // 存放临时文件数据
                List<SysAttachmentResp> attaList = new ArrayList<SysAttachmentResp>();
                if (fileCount > 1) {
                    for (int i = 0; i < fileCount; i++) {
                        try {
                            List temp = new ArrayList<>();
                            if (i < fileCount - 1) {// 第一次与中间次数数据
                                temp.addAll(list.subList(i * excelMaxRowSize, i * excelMaxRowSize + excelMaxRowSize));
                            } else if (i == fileCount - 1) {// 最后一次数据
                                temp.addAll(list.subList(i * excelMaxRowSize, i * excelMaxRowSize + mod));
                            }
                            params.put(listName, temp);
                            log.debug(String.format("导出模板文件名称：%s\r\n", exportTemp.getName() + fileSuffix));
                            String fileName = exportTemp.getType() + TokenUtils.getOnlyPK() + "(" + (i + 1) + ")"
                                    + fileSuffix;
                            // 模板路径
                            String filePath = exportPath + File.separator + exportTemp.getName() + fileSuffix;
                            File file = new File(filePath);
                            if (!file.exists()) {
                                throw new BussinessException(ResultInfo.WEB_COMMON_FILASIZE_LIMIT_0006);
                            }
                            log.debug(String.format("导出模板文件路径：%s\r\n", filePath));
                            log.debug(String.format("生成文件存放路径：%s\r\n", tempPath + fileName));
                            // 判断临时目录是否存在，不存在则创建临时目录temp
                            FileUtils.createDir(tempPath);
                            // 将数据导入到excel中
                            this.exportExcel(filePath, tempPath + fileName, params);
                            attaList.add(new SysAttachmentResp(null, fileName, tempPath + fileName, null));
                            log.info("导出数据成功");
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                            throw e;
                        }
                    }
                    // 压缩数据
                    Map<String, InputStream> inMap = new HashMap<>(attaList.size());
                    for (SysAttachmentResp resp : attaList) {
                        inMap.put(resp.getFileName(), new FileInputStream(resp.getFilePath()));
                    }
                    // 设置浏览器返回体的内容以及编码、文件名字
                    response.setContentType("application/octet-stream");
                    response.setHeader("Content-Disposition",
                            "attachment; filename=" + URLEncoder.encode(exportTemp.getType(), "utf-8") + ".zip");
                    // 创建ZipOutputStream对象，先是获取到response对象的输出流对象，把它转成ZipOutputStream对象，
                    // 然后给ZipOutputStream流里写入文件的信息，就会同步设置在response的输出流里了
                    ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream());
                    inMap.forEach((fileName, is) -> {
                        try {
                            FileUtils.addToZip(is, zipOut, fileName);
                            is.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    zipOut.flush();
                    zipOut.close();
                    // 删除临时文件
                    for (SysAttachmentResp resp : attaList) {
                        File file = new File(resp.getFilePath());
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                } else {
                    exportExcel(params, exportTemp, response);
                }
            } catch (BussinessException e) {
                log.error(e.getMessage(), e);
                throw e;
            } catch (Exception e) {
                log.debug(e.getMessage());
                throw new BussinessException(ResultInfo.BOSS_COMMON_EXP_FAIL_0007);
            } finally {
                long endTime = System.nanoTime();
                log.info("Sheet Xlsx export time (s): " + (endTime - startTime) / 1000000000);
            }
        } else {
            throw new BussinessException(ResultInfo.BOSS_COMMON_EXP_FAIL_0007);
        }
    }

    /**
     *  此方法直接导出时使用 将数据压入到临时模板，输出到前端
     * @param params
     * @param exportTemp
     * @param response
     * @throws Exception
     */
    public void exportExcelToPdf(HashMap<String, Object> params, ExportTempFileEnum exportTemp,
                                 HttpServletResponse response) throws Exception {
        File outFile = null;
        File outPdfFile = null;
        try {
            log.debug(String.format("导出模板文件名称：%s\r\n", exportTemp.getName() + fileSuffixPdf));

            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode(exportTemp.getType(), "utf-8") + ".pdf");

            String fileName = TokenUtils.getOnlyPK() + fileSuffixPdf;
            // 模板路径
            String filePath = exportPath + File.separator + exportTemp.getName() + fileSuffixPdf;
            log.debug(String.format("导出模板文件路径：%s\r\n", filePath));
            log.debug(String.format("生成文件存放路径：%s\r\n", tempPath + fileName));
            // 将数据导入到excel中
            this.exportExcel(filePath, tempPath + fileName, params);
            String startPath = tempPath + fileName; // excel 临时文件路径
            String endPath = tempPath + TokenUtils.getOnlyPK() + ".pdf"; // pdf
            // 临时文件输出路径
            outFile = new File(tempPath + fileName);// excel 临时文件
            // 即将 excel 转换成pdf 导出
            Excel2Pdf.excelToPdf(startPath, endPath);
            outPdfFile = new File(endPath);
            // 将数据输出到浏览器客户端
            FileUtils.writeFileToClient(new FileInputStream(outPdfFile.getAbsolutePath()), response.getOutputStream());
            log.info("导出数据成功");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            if (StringUtils.isNotEmpty(outFile)) {
                outFile.delete();// 删除导出的临时模板文件
                outPdfFile.delete();// 删除导出的临时模板文件
            }
        }
    }

    /**
     *  此方法直接导出时使用 将数据压入到临时模板
     * @param params
     * @param exportTemp
     * @param response
     * @throws Exception
     */
    public void exportExcel(HashMap<String, Object> params, ExportTempFileEnum exportTemp, HttpServletResponse response)
            throws Exception {
        File outFile = null;
        try {
            log.debug(String.format("导出模板文件名称：%s\r\n", exportTemp.getName() + fileSuffix));

            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/vnd.ms-excel");
            response.addHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode(exportTemp.getType(), "utf-8") + fileSuffix);

            String fileName = TokenUtils.getOnlyPK() + fileSuffix;
            // 模板路径
            String filePath = exportPath + File.separator + exportTemp.getName() + fileSuffix;
            File file = new File(filePath);
            if (!file.exists()) {
                log.info(ResultInfo.WEB_COMMON_FILASIZE_LIMIT_0006.getMsg());
                throw new BussinessException(ResultInfo.WEB_COMMON_FILASIZE_LIMIT_0006);
            }
            log.debug(String.format("导出模板文件路径：%s\r\n", filePath));
            log.debug(String.format("生成文件存放路径：%s\r\n", tempPath + fileName));
            // 判断临时目录是否存在，不存在则创建临时目录temp
            FileUtils.createDir(tempPath);
            // 将数据导入到excel中
            this.exportExcel(filePath, tempPath + fileName, params);
            outFile = new File(tempPath + fileName);
            // 将数据输出到浏览器客户端
            FileUtils.writeFileToClient(new FileInputStream(outFile.getAbsolutePath()), response.getOutputStream());
            log.info("导出数据信息成功");
        } catch (BussinessException e) {
            log.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            if (StringUtils.isNotEmpty(outFile)) {
                outFile.delete();// 删除导出的临时模板文件
            }
        }
    }

    /**
     * 数据导入到excel中
     * @param tempFilePath 模板路径
     * @param outFilePath 导出文件路径
     * @param params 需要导出的数据
     * @throws Exception
     */
    public void exportExcel(String tempFilePath, String outFilePath, HashMap<String, Object> params) throws Exception {
        try (InputStream is = StreamUtils.getFileInputStream(tempFilePath)) {
            try (OutputStream os = new FileOutputStream(outFilePath)) {
                long startTime = System.nanoTime();
                Transformer transformer = PoiTransformer.createTransformer(is, os);
                AreaBuilder areaBuilder = new XlsCommentAreaBuilder(transformer);
                List<Area> xlsAreaList = areaBuilder.build();
                Area xlsArea = xlsAreaList.get(0);
                Context context = new PoiContext();
                if (StringUtils.isNotEmpty(params)) {
                    for (Map.Entry<String, Object> entry : params.entrySet()) {
                        context.putVar(entry.getKey(), entry.getValue());
                    }
                }
                // 设置数据导入到哪个Sheet，默认Sheet1
                if (StringUtils.isNotEmpty(params) && StringUtils.isNotEmpty(params.get("xlsArea"))) {
                    xlsArea.applyAt(new CellRef(String.valueOf(params.get("xlsArea"))), context);
                } else {
                    xlsArea.applyAt(new CellRef("Sheet1!A1"), context);
                }
                xlsArea.processFormulas();
                transformer.write();
                long endTime = System.nanoTime();
                log.info("Sheet Xlsx time (s): " + (endTime - startTime) / 1000000000);
            } catch (InvalidFormatException e) {
                log.error(" InvalidFormatException " + e.getMessage());
                throw e;
            }
        } catch (IOException e) {
            log.error(" IOException " + e.getMessage());
            throw e;
        }
    }


    /**
     *  根据导入数据结果模板生成文件，再将文件存储到临时表中
     * @param user
     * @param params
     * @param exportTemp
     * @return
     * @throws Exception
     */
    public String exportExcel(CurrentUser user, HashMap<String, Object> params, ExportTempFileEnum exportTemp)
            throws Exception {
        String fileId = "";
        File outFile = null;
        try {
            log.debug(String.format("导出模板文件名称：%s\r\n", exportTemp.getName() + fileSuffix));
            String fileName = exportTemp.getType() + TokenUtils.getOnlyPK() + fileSuffix;
            // 模板路径
            String filePath = exportPath + File.separator + exportTemp.getName() + fileSuffix;
            log.debug(String.format("导出模板文件路径：%s\r\n", filePath));
            log.debug(String.format("生成文件存放路径：%s\r\n", tempPath + fileName));
            // 将数据导入到excel中
            this.exportExcel(filePath, tempPath + fileName, params);
            outFile = new File(tempPath + fileName);
            // TODO: 2018/11/6
//            fileId = attachmentTempService.addExportAttachment(user, outFile, exportTemp.getType());
            log.info("导出数据成功");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            if (StringUtils.isNotEmpty(outFile)) {
                outFile.delete();// 删除导出的临时模板文件
            }
        }
        return fileId;
    }
}
