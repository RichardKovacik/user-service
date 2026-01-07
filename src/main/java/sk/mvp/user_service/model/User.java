package sk.mvp.user_service.model;

import jakarta.persistence.*;
import sk.mvp.user_service.util.GenderConverter;
import java.util.Set;

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

    @Column(nullable = false, unique = true)
    private String username;

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
    @Column(nullable = false)
    @Convert(converter = GenderConverter.class)
    private Gender gender;

    private boolean activated;

    @Column(name = "activation_token")
    private String activationToken;

    @Column(name ="token_version", nullable = false)
    private int tokenVersion;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    public User() {
    }

    public User(String username, String password, Contact contact, Gender gender) {
        this.username = username;
        this.password = password;
        this.contact = contact;
        this.gender = gender;
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

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String[] getRolesAsString() {
        return this.getRoles().stream()
                .map(Role::getName)
                .toArray(String[]::new);
    }

    public int getTokenVersion() {
        return tokenVersion;
    }

    public void setTokenVersion(int tokenVersion) {
        this.tokenVersion = tokenVersion;
    }
}
