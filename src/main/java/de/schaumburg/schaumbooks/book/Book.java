package de.schaumburg.schaumbooks.book;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;



@Entity
public class Book{
        private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;        
        private @NotEmpty String title;
        private @NotEmpty String author;
        private BookStatus status;

        Book(){

        }


        public Book(Long id, @NotEmpty String title, @NotEmpty String author, BookStatus status) {
                this.id = id;
                this.title = title;
                this.author = author;
                this.status = status;
        }


        public Boolean isLend(){
                if (this.status == BookStatus.LENT) return true;
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


        public String getAuthor() {
                return author;
        }


        public void setAuthor(String author) {
                this.author = author;
        }


        public BookStatus getStatus() {
                return status;
        }


        public void setStatus(BookStatus status) {
                this.status = status;
        }


        @Override
        public int hashCode() {
                final int prime = 31;
                int result = 1;
                result = prime * result + ((id == null) ? 0 : id.hashCode());
                result = prime * result + ((title == null) ? 0 : title.hashCode());
                result = prime * result + ((author == null) ? 0 : author.hashCode());
                result = prime * result + ((status == null) ? 0 : status.hashCode());
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
                if (author == null) {
                        if (other.author != null)
                                return false;
                } else if (!author.equals(other.author))
                        return false;
                if (status != other.status)
                        return false;
                return true;
        }


        @Override
        public String toString() {
                return "Book [id=" + id + ", title=" + title + ", author=" + author + ", status=" + status + "]";
        }
}
