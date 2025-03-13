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
public class Book {
        private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
        private @NotEmpty String title;
        private @NotEmpty String verlag;
        private @NotEmpty String isbn;
        @Enumerated(EnumType.STRING)
        private BookStatus status = BookStatus.AVAILABLE;
        @ManyToOne
        @JoinColumn(name = "user_id")
        private User user;

        public Book(Long id, String title, String verlag, String isbn, BookStatus status,
                        User user) {
                this.id = id;
                this.title = title;
                this.verlag = verlag;
                this.isbn = isbn;
                this.status = status;
                this.user = user;
        }

        Book() {

        }

        public Boolean isLend() {
                if (this.status == BookStatus.LENT)
                        return true;
                return false;
        }

        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;

        }

        public String getTitle() {
                return title;
        }

        public void setTitle(String title) {
                this.title = title;
        }

        public String getIsbn() {
                return isbn;
        }

        public void setIsbn(String isbn) {
                this.isbn = isbn;
        }

        public BookStatus getStatus() {
                return status;
        }

        public void setStatus(BookStatus status) {
                this.status = status;
        }

        public void setUser(User user) {
                this.user = user;
        }

        public User getUser() {
                return user;
        }

        public String getVerlag() {
                return verlag;
        }

        public void setVerlag(String verlag) {
                this.verlag = verlag;
        }

        @Override
        public int hashCode() {
                final int prime = 31;
                int result = 1;
                result = prime * result + ((id == null) ? 0 : id.hashCode());
                result = prime * result + ((title == null) ? 0 : title.hashCode());
                result = prime * result + ((verlag == null) ? 0 : verlag.hashCode());
                result = prime * result + ((isbn == null) ? 0 : isbn.hashCode());
                result = prime * result + ((status == null) ? 0 : status.hashCode());
                result = prime * result + ((user == null) ? 0 : user.hashCode());
                return result;
        }

        @Override
        public boolean equals(Object obj) {
                if (this == obj)
                        return true;
                if (obj == null)
                        return false;
                if (getClass() != obj.getClass())
                        return false;
                Book other = (Book) obj;
                if (id == null) {
                        if (other.id != null)
                                return false;
                } else if (!id.equals(other.id))
                        return false;
                if (title == null) {
                        if (other.title != null)
                                return false;
                } else if (!title.equals(other.title))
                        return false;
                if (verlag == null) {
                        if (other.verlag != null)
                                return false;
                } else if (!verlag.equals(other.verlag))
                        return false;
                if (isbn == null) {
                        if (other.isbn != null)
                                return false;
                } else if (!isbn.equals(other.isbn))
                        return false;
                if (status != other.status)
                        return false;
                if (user == null) {
                        if (other.user != null)
                                return false;
                } else if (!user.equals(other.user))
                        return false;
                return true;
        }

        @Override
        public String toString() {
                return "Book [id=" + id + ", title=" + title + ", verlag=" + verlag + ", isbn=" + isbn + ", status="
                                + status + ", user=" + user + "]";
        }

}
