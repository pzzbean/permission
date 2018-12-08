package com.mmall.beans;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Email {
    private String subject;

    private String message;

    private Set<String> receivers;
}
