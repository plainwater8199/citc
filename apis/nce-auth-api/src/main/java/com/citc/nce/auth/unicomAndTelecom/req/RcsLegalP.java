package com.citc.nce.auth.unicomAndTelecom.req;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class RcsLegalP {
   private String legalName;
   private String legalIdentification;
   private List<String> identificationPic;

}
