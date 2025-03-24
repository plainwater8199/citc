package com.citc.nce.auth.invoice.service.impl;

import com.citc.nce.auth.invoice.domain.InvoiceManageFile;
import com.citc.nce.auth.invoice.dao.InvoiceManageFileMapper;
import com.citc.nce.auth.invoice.service.IInvoiceManageFileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * csp上传的发票 服务实现类
 * </p>
 *
 * @author bydud
 * @since 2024-03-08 04:03:34
 */
@Service
public class InvoiceManageFileServiceImpl extends ServiceImpl<InvoiceManageFileMapper, InvoiceManageFile> implements IInvoiceManageFileService {

    @Override
    public List<InvoiceManageFile> listByImId(Long imId) {
        return lambdaQuery().eq(InvoiceManageFile::getImId, imId)
                .orderByDesc(InvoiceManageFile::getCreateTime)
                .list();
    }
}
