package com.okeyifee.hrservice.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity(name = "feedbacks")
public class Feedback extends Base {
    @NotBlank(message = "feedback message should not be empty")
    private String message;

    @ManyToOne
    private Products product;
}