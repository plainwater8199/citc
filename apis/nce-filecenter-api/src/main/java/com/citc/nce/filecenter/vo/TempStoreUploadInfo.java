package com.citc.nce.filecenter.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bydud
 * @since 2024/2/29
 */
@Getter
@Setter
public class TempStoreUploadInfo {
    private String xid;
    private String userId;
    private List<String> fileUUidList = new ArrayList<>();
    private String failureReason;

    public void add(String tid) {
        this.fileUUidList.add(tid);
    }
}
