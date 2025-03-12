package com.mediBuddy.medicos.controllers.chat;

import com.mediBuddy.medicos.Advice.ApiResponseProject;
import com.mediBuddy.medicos.dto.DomainDTO;
import com.mediBuddy.medicos.service.chatService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class chat {

    private  final chatService cs;

    @GetMapping("/{message}")
    public ResponseEntity<ApiResponseProject<String>> getMessage(@PathVariable String message , @RequestParam(value = "language",defaultValue = "English") String language){
        String s= cs.response(message,language);
        ApiResponseProject<String> response = new ApiResponseProject<>("Success","All Domain Get Succ",s);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
