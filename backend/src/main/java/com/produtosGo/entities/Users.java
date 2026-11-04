package com.produtosGo.entities;

import com.produtosGo.enums.TypeUsers;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.Timestamp;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor @NoArgsConstructor
public class Users {
    @Id
    UUID id;
    @Column(nullable = false)
    String name;
    @Column(nullable = false)
    String email;
    @Column(nullable = false)
    String password_hash;
    @Column(nullable = false)
    TypeUsers type;
    boolean active;
    Timestamp created_at;
}
