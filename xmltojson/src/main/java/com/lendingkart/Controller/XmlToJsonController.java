package com.lendingkart.Controller;

import com.lendingkart.Utility.UtilityConverter;
import com.lendingkart.Utility.XmlToJsonConverter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class XmlToJsonController {


    @PostMapping(value = "/convert", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> convertXmlToJson(@RequestBody String xml) {
        try {
            String json = UtilityConverter.convertXmlToJsonString(xml);
            return new ResponseEntity<>(json, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error converting XML to JSON: " + e.getMessage());
        }
    }

}
