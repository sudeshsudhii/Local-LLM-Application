package com.sudhii.model;

import lombok.Data;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document("chat_history")
public class DeepSeekDBModel {

    @Id
    String id;
    String title;
    List<Conversation> converstionList;

    @LastModifiedDate
    LocalDateTime lastModifiedDate;
    @CreatedDate
    LocalDateTime createdDate;
    @CreatedBy
    String createdBy;
    @LastModifiedBy
    String lastModifiedBy;

    @Data
    public static class Conversation {
        String question;
        String answer;
    }
}
