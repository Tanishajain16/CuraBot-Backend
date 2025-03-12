package com.mediBuddy.medicos.model;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document
@Getter
@AllArgsConstructor
@Builder
@Setter
@NoArgsConstructor
public class SOS {
    @MongoId
    String id;
    String email;
    String userId;
}
