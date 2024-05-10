package edu.domain.model;

public record User(int id, String name, String email, String passwordHash) {
}
