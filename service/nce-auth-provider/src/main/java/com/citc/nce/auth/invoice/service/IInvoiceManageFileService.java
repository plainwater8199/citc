package com.citc.nce.auth.invoice.service;

import com.citc.nce.auth.invoice.domain.InvoiceManageFile;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * csp上传的发票 服务类
 * </p>
 *
 * @author bydud
 * @since 2024-03-08 04:03:34
 */
public interface IInvoiceManageFileService extends IService<InvoiceManageFile> {

    List<InvoiceManageFile> listByImId(Long imId);
}
