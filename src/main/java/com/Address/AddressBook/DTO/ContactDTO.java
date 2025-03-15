package com.Address.AddressBook.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ContactDTO {

    private Long id;

    @NotBlank(message = "Name is required")  // Ensures the name is not blank
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Name must contain only letters and spaces") // Ensures the name only contains letters and spaces
    private String name;

    private String email;
    private String phone;
    private String city;
}