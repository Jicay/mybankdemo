package com.example.mybank.infrastructure.driven.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @Column(nullable = false, updatable = false)
    private String id;

    @Column(nullable = false, length = 100)
    private String login;

    @Column(nullable = false, length = 200)
    private String password;

    @Column(name = "client_id", nullable = false, length = 26)
    private String clientId;

    public UserEntity() {
    }

    public UserEntity(String id, String login, String password, String clientId) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.clientId = clientId;
    }

    public String getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getClientId() {
        return clientId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
