package cz.diamo.share.security;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import cz.diamo.share.dto.AppUserDto;


public class UserAuthentication implements Authentication {

  private static final long serialVersionUID = 1L;

  private final AppUserDto user;


  public UserAuthentication(AppUserDto user) {
    this.user = user;
  }

  @Override
  public String getName() {
    return this.user.getIdUzivatel();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.user.getAuthorities();
  }

  @Override
  public Object getCredentials() {
    return null;
  }

  @Override
  public Object getDetails() {
    return null;
  }

  @Override
  public Object getPrincipal() {
    return this.user;
  }

  @Override
  public boolean isAuthenticated() {
    return true;
  }

  @Override
  public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
    throw new UnsupportedOperationException("tento authentication object je vždy authenticated");
  }

}
