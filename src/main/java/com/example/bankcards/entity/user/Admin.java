package com.example.bankcards.entity.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name = "admins")
@Entity
@Getter
@Setter
public class Admin {

    public Admin() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

}
