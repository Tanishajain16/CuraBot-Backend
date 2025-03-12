package com.mediBuddy.medicos.controllers.SOS;

import com.mediBuddy.medicos.Advice.ApiResponseProject;
import com.mediBuddy.medicos.dto.Emaillist;
import com.mediBuddy.medicos.dto.QuestionDTO;
import com.mediBuddy.medicos.model.SOS;
import com.mediBuddy.medicos.service.sosservice;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sos")
@CrossOrigin("*")
@AllArgsConstructor
public class sos {

    private  final sosservice ss;

    @GetMapping("/add")
    public ResponseEntity<ApiResponseProject<String>> createSOS(@RequestParam String userid, @RequestParam String email){
String s= ss.addSOSPerson(userid,email);
        ApiResponseProject<String> response = new ApiResponseProject<>("success","SOS Created",s);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("/all/{userid}")
    public ResponseEntity<ApiResponseProject<List<SOS>>> getSOS(@PathVariable String userid) {
        System.out.println("user id is "+userid);
        List<SOS> s = ss.getSos(userid);
        System.out.println(s.toString());
        ApiResponseProject<List<SOS>> response = new ApiResponseProject<>("success","Question For This Domain Geted",s);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/send/{userid}")
    public  ResponseEntity<ApiResponseProject<String>> sendsos(@PathVariable String  userid){
        String s= ss.sossend(userid);
        ApiResponseProject<String> response = new ApiResponseProject<>("success","SOS Created",s);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
