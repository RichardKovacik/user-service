package sk.mvp.user_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Value;
import sk.mvp.user_service.util.GenderConverter;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name", length = 20)
    private String firstName;

    // spravit v db constraint na min dlzku 3, + DTO validacia, nahradim literaly konstantami
    @Column(name = "last_name", length = 30)
    private String lastName;

    @Column(length = 30)
    private String password;

    // User nemoze existovat bez kontaktu, ked sa vymaze user vymaze sa aj kontakt
    @OneToOne(mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            optional = false,
            fetch = FetchType.LAZY)
    private Contact contact;

    // constraint F alebo M
    @Convert(converter = GenderConverter.class)
    private Gender gender;

    private boolean activated;
    @Column(name = "activation_token")
    private String activationToken;

    public User() {
    }

    public Integer getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String name) {
        this.firstName = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Contact getContact() {
        return contact;
    }

    public Gender getGender() {
        return gender;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public String getActivationToken() {
        return activationToken;
    }

    public void setActivationToken(String activationToken) {
        this.activationToken = activationToken;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }
}
