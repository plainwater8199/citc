package com.citc.nce.im.util.db;

import com.citc.nce.authcenter.csp.CspApi;
import com.citc.nce.authcenter.csp.vo.QueryAllCspIdResp;
import com.citc.nce.im.util.db.mapper.MsgRecordDDLDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 当已经分表的表结构发生变化，调用的工具接口，可批量修改该逻辑表的所有真实表结构
 *
 * @author jcrenc
 * @since 2024/3/5 17:18
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/tenant/table/")
public class AlterTableController {

    private final MsgRecordDDLDao msgRecordDDLDao;
    private final CspApi cspApi;


    @RequestMapping("msg_record/refreshIndex")
    @Transactional(rollbackFor = Exception.class)
    public void refreshIndex(){

        QueryAllCspIdResp queried = cspApi.queryAllCspId();
        if (queried == null)
            return;
        List<String> cspIds = queried.getCspIds();
        List<String> indexList = Arrays.asList("idx_account_id", "idx_creator", "idx_message_id", "idx_phone_num", "idx_account_type", "idx_plan_detail_id", "idx_send_result");
//        for (String cspId : cspIds) {
            final String tableName = "msg_record_6514726698";//"msg_record_" + cspId;
            List<String> indexListCopy = new ArrayList<>(indexList);
            try {
                List<Map<String, Object>> indexListForTable = msgRecordDDLDao.getTableIndexes(tableName);
                for (Map<String, Object> index : indexListForTable) {
                    String indexName = (String) index.get("key_name");
//                    if (!"PRIMARY".equals(indexName)) {
//                        if(!indexList.contains(indexName)){
//                            msgRecordDDLDao.dropIndex(tableName,indexName);
//                        }else{
//                            indexListCopy.remove(indexName);
//                        }
//                    }
                }
                for (String indexName : indexListCopy) {
                    String index = indexName.replace("idx_", "");
                    msgRecordDDLDao.addIndex(tableName,indexName,index);
                }
            }catch (Exception e){
                log.error("表-{},更新索引失败: {}", tableName,e.getMessage());
            }
//        }
    }

}
