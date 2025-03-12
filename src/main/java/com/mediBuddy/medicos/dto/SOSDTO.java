package com.mediBuddy.medicos.dto;

import com.mediBuddy.medicos.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class SOSDTO {
    String id;
    String email;
    User Userid;
}
