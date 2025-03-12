package com.mediBuddy.medicos.controllers.counslingsession;

import com.mediBuddy.medicos.Advice.ApiResponseProject;
import com.mediBuddy.medicos.dto.CounslingSessionDTO;
import com.mediBuddy.medicos.model.CounslingSession;
import com.mediBuddy.medicos.service.counslingSessionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping("/counslingsession")
@RestController
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class counslingsession {

    private final counslingSessionService cs;
    // create The CounslingSession
    @PostMapping("/create")
    public ResponseEntity<ApiResponseProject<CounslingSessionDTO>> createCounslingSession(@RequestBody CounslingSessionDTO c){
        CounslingSessionDTO  cdto=  cs.createCounslingSession(c);
        ApiResponseProject<CounslingSessionDTO> response = new ApiResponseProject<>("success","counsling session creaded",cdto);
          return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @PostMapping("/analyze/text")
    public ResponseEntity<ApiResponseProject<CounslingSessionDTO>> analyzeText(
            @RequestParam("file") MultipartFile file,
            @RequestParam("sessionId") String sessionId,
            @RequestParam(value = "language",defaultValue = "English") String language
    ){
        CounslingSessionDTO c= cs.analyzeText(file,sessionId,language);
        ApiResponseProject<CounslingSessionDTO> response = new ApiResponseProject<>("success","counsling session sumarized",c);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

 @GetMapping("/details/{sessionId}")
public  ResponseEntity<ApiResponseProject<CounslingSessionDTO>> getDetails(@PathVariable String sessionId){
     CounslingSessionDTO cdto =cs.getDetails(sessionId);
     ApiResponseProject<CounslingSessionDTO> response = new ApiResponseProject<>("success","Mail Sent Succ",cdto);
     return new ResponseEntity<>(response, HttpStatus.CREATED);
 }




    @GetMapping("/generateReport/{sessionId}")
    public ResponseEntity<ApiResponseProject<String>> generateReport(
            @PathVariable("sessionId") String sessionId
    ){
        cs.generateReport(sessionId);
        ApiResponseProject<String> response = new ApiResponseProject<>("success","Mail Sent Succ","Chek Your Mail Indox");
    return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @GetMapping("/mysession/{userId}")
    public  ResponseEntity<ApiResponseProject<List<CounslingSessionDTO>>> getAllMyCounslingSession(@PathVariable  String userId){
        List<CounslingSessionDTO> l = cs.getAllMySession(userId);
        ApiResponseProject<List<CounslingSessionDTO>> response = new ApiResponseProject<>("sucess","My All Session get succ",l);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


}
