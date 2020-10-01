package learn.hibern.beghibern4.ch04lifecycle.mapped;

import javax.persistence.*;

@Entity(name = "Message2")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String content;

    @OneToOne
    private Email email;

    public Message() {
    }

    public Message(String content) {
        setContent(content);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", email=" + (email != null ? email.getSubject() : "null") +
                '}';
    }
}
