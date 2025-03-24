package com.citc.nce.readingLetter;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.citc.nce.auth.readingLetter.dataStatistics.ReadingLetterDataStatisticsApi;
import com.citc.nce.auth.readingLetter.dataStatistics.dto.DataStatisticsReq;
import com.citc.nce.auth.readingLetter.dataStatistics.dto.FifthDataStatisticsReq;
import com.citc.nce.auth.readingLetter.dataStatistics.vo.FifthReadingLetterDailyReportSelectVo;
import com.citc.nce.auth.readingLetter.dataStatistics.vo.ReadingLetterDailyReportSelectVo;
import com.citc.nce.common.constants.CarrierEnum;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URLEncoder;
import java.util.List;

/**
 * 阅信解析统计
 * 创建者:zhujinyu
 * 创建时间:2024/7/19 16:54
 * 描述:
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class ReadingLetterDataStatisticsController {

    @Resource
    private ReadingLetterDataStatisticsApi dataStatisticsApi;

    @ApiOperation("阅信+数据统计")
    @PostMapping("/dataStatistics")
    public List<ReadingLetterDailyReportSelectVo> dataStatistics(@RequestBody @Valid DataStatisticsReq req) {
        return dataStatisticsApi.dataStatistics(req);
    }

    @ApiOperation("5G阅信数据统计")
    @PostMapping("/fifthDataStatistics")
    public List<FifthReadingLetterDailyReportSelectVo> fifthDataStatistics(@RequestBody @Valid FifthDataStatisticsReq req) {
        return dataStatisticsApi.fifthDataStatistics(req);
    }

    @PostMapping("/fifthDataStatisticsExport")
    @ApiOperation("5G阅信数据统计导出")
    public void fifthDataStatisticsExport(@RequestBody @Valid FifthDataStatisticsReq req, HttpServletResponse response) {
        List<FifthReadingLetterDailyReportSelectVo> fifthReadingLetterDailyReportSelectVos = dataStatisticsApi.fifthDataStatistics(req);
        if (ObjectUtil.isNotEmpty(fifthReadingLetterDailyReportSelectVos)) {
            fifthReadingLetterDailyReportSelectVos.forEach(item -> {
                if (2 == item.getSourceType()) {
                    item.setFrom("开发者服务");
                }
                item.setMessageType("5G阅信");
                item.setOperatorName(CarrierEnum.getCarrierEnum(item.getOperatorCode()).getName());
            });
        }
        export(fifthReadingLetterDailyReportSelectVos, response, String.format("5G阅信解析统计%s-%s", req.getStartTime(), req.getEndTime()), FifthReadingLetterDailyReportSelectVo.class);

    }

    @PostMapping("/dataStatisticsExport")
    @ApiOperation("阅信+数据统计导出")
    public void dataStatisticsExport(@RequestBody DataStatisticsReq req, HttpServletResponse response) {
        List<ReadingLetterDailyReportSelectVo> letterDailyReportSelectVos = dataStatisticsApi.dataStatistics(req);
        if (ObjectUtil.isNotEmpty(letterDailyReportSelectVos)) {

            letterDailyReportSelectVos.forEach(item -> {
                item.setOperatorName(CarrierEnum.getCarrierEnum(item.getOperatorCode()).getName());
            });
        }
        export(letterDailyReportSelectVos, response, String.format("5G阅信+解析统计%s-%s", req.getStartTime(), req.getEndTime()), ReadingLetterDailyReportSelectVo.class);

    }

    @SneakyThrows
    public <T> void export(List<T> datas, HttpServletResponse response, String title, Class<T> tClass) {
        // 设置响应头
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 设置防止中文名乱码
        String fileName = String.format("%s.xlsx", title);
        String fileNameURL = URLEncoder.encode(fileName, "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileNameURL + ";" + "filename*=utf-8''" + fileNameURL);
        //获取数据列表
        // 写入数据到excel
        ServletOutputStream os = response.getOutputStream();
        EasyExcel.write(os, tClass)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet("统计")
                .doWrite(datas);
        if (null != os) {
            os.flush();
            os.close();
        }
    }

}
