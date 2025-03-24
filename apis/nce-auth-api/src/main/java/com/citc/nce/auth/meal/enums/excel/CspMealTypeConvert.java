package com.citc.nce.auth.meal.enums.excel;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.citc.nce.auth.meal.enums.CspMealType;

/**
 * bydud
 * 2024/1/22
 **/
public class CspMealTypeConvert implements Converter<CspMealType> {
    public CspMealTypeConvert() {

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
    public CspMealType convertToJavaData(CellData cellData, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return CspMealType.valueOf(cellData.getDataFormatString());
    }

    @Override
    public CellData convertToExcelData(CspMealType cspMealType, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return new CellData<>(cspMealType.getDesc());
    }
}
