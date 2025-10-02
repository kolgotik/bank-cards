package com.example.bankcards.entity.user;

import com.example.bankcards.entity.card.Card;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Represents a bank user entity that stores information like username, password,
 * full name, role, and associated cards.
 */
@Table(name = "users")
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
public class BankUser implements UserDetails {

    public BankUser() {
    }

    /**
     * The unique identifier of the user in the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The username of the user (used for login).
     * Must be unique and non-null.
     */
    private String username;

    private String firstName;
    private String lastName;

    private String password;

    /**
     * The role assigned to the user (e.g., USER, ADMIN).
     * Stored as a string in the database.
     */
    @Enumerated(EnumType.STRING)
    private Role role;

    /**
     * A set of cards owned by this user.
     * This is a one-to-many relationship with lazy fetching.
     */
    @OneToMany(mappedBy = "bankUser", fetch = FetchType.LAZY)
    private Set<Card> cards;

    /**
     * Returns the authorities granted to the user.
     * Used by Spring Security for role-based access control.
     *
     * @return A collection containing the user's role.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(role);
    }
}
