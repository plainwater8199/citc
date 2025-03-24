package com.citc.nce.robot.Consts;

/**
 * @author yy
 * @date 2024-03-25 19:35:48
 */
public interface Constants {
    /**
     *  关联账号修改状态 0 原始状态或发布前修改，或修改后已发布
     */

    public int Scene_Account_Change_status_Normal=0;
    /**
     *  关联账号修改状态    1发布后再次修改过账号
     */

    public int Scene_Account_Change_status_Changed=1;
}
