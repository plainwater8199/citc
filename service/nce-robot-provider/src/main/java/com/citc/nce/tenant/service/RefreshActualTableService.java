package com.citc.nce.tenant.service;

import com.citc.nce.tenant.vo.req.CreateTableReq;

public interface RefreshActualTableService {
    void refreshActualTable(CreateTableReq req);
}
