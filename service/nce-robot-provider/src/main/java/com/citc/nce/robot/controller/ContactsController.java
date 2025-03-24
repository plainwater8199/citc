package com.citc.nce.robot.controller;

import com.citc.nce.robot.ContactsApi;
import com.citc.nce.robot.service.impl.ContactsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
public class ContactsController implements ContactsApi {

    @Resource
    ContactsService contactsService;

    @PostMapping("/im/message/checkContacts")
    @Override
    public Boolean checkContacts(@RequestPart(value = "file") MultipartFile file) {
        return contactsService.checkContacts(file);
    }
}
