package com.citc.nce.readingLetter.config;

import com.citc.nce.readingLetter.service.ReadingLetterParseRecordService;
import com.citc.nce.seata.ShardingJdbcNodesAutoConfigurator;
import com.citc.nce.tenant.vo.req.RefreshActualNodesReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class CspTableManageServiceImpl implements CspTableManageService {

    private static final String DB_NAME = "record.";
    private static final String ROBOT_RECORD = "reading_letter_parse_record_";

    @Resource
    private ReadingLetterParseRecordService readingLetterParseRecordService;
    @Resource
    private ShardingJdbcNodesAutoConfigurator shardingJdbcNodesAutoConfigurator;


    @Override
    public void refreshActualNodes(RefreshActualNodesReq req) {
        Set<String> cspIdSet = req.getCspIdSet();
        List<String> recordActualNodes = new ArrayList<>();
        for(String cspId : cspIdSet){
            readingLetterParseRecordService.createTable(cspId);
            recordActualNodes.add(DB_NAME+ROBOT_RECORD+cspId);
        }
        shardingJdbcNodesAutoConfigurator.generateActualDataNodes("reading_letter_parse_record",recordActualNodes);
    }

}
