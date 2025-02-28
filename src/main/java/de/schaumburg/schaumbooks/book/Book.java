package de.schaumburg.schaumbooks.book;

import de.schaumburg.schaumbooks.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotEmpty;

@Entity
public record Book(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id,
    @NotEmpty String title,
    @NotEmpty String verlag,
    @NotEmpty String isbn,
    @Enumerated(EnumType.STRING) BookStatus status,
    @ManyToOne @JoinColumn(name = "user_id") User user
) {
    public Book() {
        this(null, "", "", "", BookStatus.AVAILABLE, null);
    }

    public Boolean isLend() {
        return this.status == BookStatus.LENT;
    }
}
