package com.citc.nce.auth.meal.enums.excel;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.citc.nce.auth.meal.enums.CspMealStatus;
import com.citc.nce.auth.meal.enums.CspMealType;

/**
 * bydud
 * 2024/1/22
 **/
public class CspMealStatusConvert implements Converter<CspMealStatus> {
    public CspMealStatusConvert() {

    }

    @Override
    public Class supportJavaTypeKey() {
        return CspMealType.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public CspMealStatus convertToJavaData(CellData cellData, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return CspMealStatus.valueOf(cellData.getDataFormatString());
    }

    @Override
    public CellData convertToExcelData(CspMealStatus cspMealType, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return new CellData(cspMealType.getDesc());
    }
}
