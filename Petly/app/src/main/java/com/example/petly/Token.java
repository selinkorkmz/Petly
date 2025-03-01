package com.example.petly;

public class Token {
    private String tokenValue;

    public Token() {
        // Default constructor
    }

    public Token(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }
}
