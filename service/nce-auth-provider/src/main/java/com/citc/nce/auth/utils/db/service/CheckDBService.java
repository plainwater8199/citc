package com.citc.nce.auth.utils.db.service;



import java.io.IOException;


public interface CheckDBService {

    void checkBDForService(String local) throws IOException, ClassNotFoundException;
}
