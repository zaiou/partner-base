package com.zaiou.web.vo.system;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @Description: 文件信息返回
 * @auther: LB 2018/11/6 15:33
 * @modify: LB 2018/11/6 15:33
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class SysAttachmentResp {
    private String id;
    private String fileName;
    private String filePath;
    private String mimeType;
}
