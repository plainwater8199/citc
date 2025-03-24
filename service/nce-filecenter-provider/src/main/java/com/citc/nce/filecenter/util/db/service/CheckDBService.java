package com.citc.nce.filecenter.util.db.service;



import java.io.IOException;


public interface CheckDBService {

    void checkBDForService(String local) throws IOException, ClassNotFoundException;
}
