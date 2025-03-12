package com.mediBuddy.medicos.service;


import com.mediBuddy.medicos.utils.GoogleGemini;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class chatService {

    private final GoogleGemini gm;

    public  String response(String message,String language){
        return gm.generateChatResponse(message,language);
    }

}
