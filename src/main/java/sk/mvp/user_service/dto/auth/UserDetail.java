package sk.mvp.user_service.dto.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import sk.mvp.user_service.model.Role;
import sk.mvp.user_service.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UserDetail implements UserDetails {
    private long id;
    private String username;
    private String password;
    private List<SimpleGrantedAuthority> authorities;

    public UserDetail(String username, List<String> roles) {
        this.username = username;
        this.authorities = mapRolesStringToAuthorities(roles);
    }

    public UserDetail(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.authorities = mapRolesToAuthorities(user.getRoles());

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public long getId() {
        return id;
    }

    private List<SimpleGrantedAuthority> mapRolesToAuthorities(Set<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .toList();
    }

    private List<SimpleGrantedAuthority> mapRolesStringToAuthorities(List<String> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role))
                .toList();
    }
}
