package com.mediBuddy.medicos.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class Emaillist {
    String email;
    Emaillist(String email){
        this.email=email;
    }
}
