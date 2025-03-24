package com.citc.nce.misc.dictionary.vo.req;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DataDictionaryListReq {
    private List<DataDictionaryTypeReq> list;
}
