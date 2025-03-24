package com.citc.nce.im.util.db.service;



import java.io.IOException;


public interface CheckDBService {

    void checkBDForService(String local) throws IOException, ClassNotFoundException;
}
