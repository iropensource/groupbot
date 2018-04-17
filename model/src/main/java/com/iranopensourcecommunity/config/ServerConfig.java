package com.iranopensourcecommunity.config;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;


@Table(name = "application_config")
@Entity
public class ServerConfig {

    @Id
    @Column(name = "key")
    @Enumerated(EnumType.STRING)
    private ServerEnumKey key;

    @Column(name = "value")
    @NotEmpty(message = "Value field can not be blank")
    @Length(min = 1, max = 500, message = "Name filed length must be between 1 to 500 character")
    private String value;

    public ServerConfig() {
    }

    public ServerConfig(ServerEnumKey key, String value) {
        this.key = key;
        this.value = value;
    }

    public ServerEnumKey getKey() {
        return key;
    }

    public void setKey(ServerEnumKey key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
