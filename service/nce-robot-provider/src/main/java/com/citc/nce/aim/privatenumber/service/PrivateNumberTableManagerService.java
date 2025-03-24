package com.citc.nce.aim.privatenumber.service;

public interface PrivateNumberTableManagerService {
    void createAimPrivateNumberTable();

    void refreshShardingActualNodes(boolean isCreate);
}
