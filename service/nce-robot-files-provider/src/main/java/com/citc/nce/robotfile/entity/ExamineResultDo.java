package com.citc.nce.robotfile.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户上传素材流程：
 * <ol>
 *   <li><b>百度审核</b>：提交素材进行审核。</li>
 *   <li><b>上传到MinIO</b>：素材上传至MinIO存储服务。</li>
 *   <li><b>保存至tb_file_manage表</b>：
 *       <ul>
 *           <li><b>fileUuid</b>：此时随机生成的UUID。</li>
 *           <li><b>fileUrl</b>：素材在MinIO中的URL地址。</li>
 *       </ul>
 *   </li>
 *   <li><b>自动送审，保存送审记录到tb_examine_result表</b>：
 *       <ul>
 *           <li><b>fileUuid</b>：与<code>tb_file_manage</code>表中的<code>fileUuid</code>字段关联。</li>
 *           <li><b>fileId</b>：运营商处的文件ID，来源于上传文件返回值中的<code>tid</code>字段。</li>
 *           <li><b>审核状态字段fileStatus</b>：
 *               <ul>
 *                   <li><code>1</code>（待审核）：表示素材尚未审核。</li>
 *                   <li><code>2</code>（审核成功）：审核通过。</li>
 *                   <li><code>3</code>（失败）：审核未通过。</li>
 *                   <li><code>4</code>（无状态）：状态未知。</li>
 *                   <li><code>5</code>（审核中）：蜂动渠道的状态为审核中。</li>
 *               </ul>
 *           </li>
 *       </ul>
 *   </li>
 *   <li><b>修改账号素材容量</b>：根据上传结果修改<code>tb_available_quantity</code>表中对应机器人的素材总量和使用量。</li>
 *   <li><b>调用对应素材类型的save方法保存素材</b>：
 *       <ul>
 *           <li>图片：调用 <code>/material/picture/save</code> 接口。</li>
 *           <li>音频：调用 <code>/material/audio/save</code> 接口。</li>
 *           <li>视频：调用 <code>/material/video/save</code> 接口。</li>
 *           <li>在对应素材表中（<code>tb_*</code>），将<code>fileUuid</code>保存到<code>*urlId</code>字段。</li>
 *       </ul>
 *   </li>
 *   <li><b>文件审核状态回调</b>：
 *       <ul>
 *           <li>回调接口：<code>/{chatbotId}/delivery/mediaStatus</code>。</li>
 *           <li>之后转发到RocketMQ的指定Topic。</li>
 *       </ul>
 *   </li>
 *   <li><b>MediaStatusListener消费素材审核状态回调的MQ消息</b>：
 *       <ul>
 *           <li>根据回调的审核状态修改<code>tb_examine_result</code>表中的<code>fileStatus</code>：
 *               <ul>
 *                   <li><code>2</code>（审核成功）：如果审核通过，状态设置为成功。</li>
 *                   <li><code>3</code>（审核失败）：如果审核未通过，状态设置为失败。</li>
 *               </ul>
 *           </li>
 *           <li>设置素材的过期时间：
 *               <ul>
 *                   <li><b>蜂动渠道固定为</b> <code>2099-12-31 23:59:59</code>。</li>
 *                   <li>其他供应商的过期时间由返回数据提供。</li>
 *               </ul>
 *           </li>
 *       </ul>
 *   </li>
 * </ol>
 */


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_examine_result")
public class ExamineResultDo extends BaseDo<ExamineResultDo> implements Serializable {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private String fileUuid;

    private String chatbotId;

    private String fileId;

    private String operator;

    // 1待审核  2审核成功  3失败  4无状态 5审核中
    private Integer fileStatus;

    private Date deleteTime;

    private Integer deleted;

    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Date validity;

    private String appId;

    private String chatbotName;

    private String chatbotAccountId;
    //缩略图Id
    private String thumbnailTid;
    /**
     * 供应商tag fontdo蜂动  owner 自有
     */
    private String supplierTag;
    /**
     * 统计时用，不存数据库
     *
     */
    @TableField(exist = false)
    private Long fileCount;

    private Long accountId;
}
