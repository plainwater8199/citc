package com.citc.nce.readingLetter.config;

import com.citc.nce.tenant.vo.req.CreateCspTableReq;
import com.citc.nce.tenant.vo.req.RefreshActualNodesReq;

public interface CspTableManageService {
    void refreshActualNodes(RefreshActualNodesReq req);
}
