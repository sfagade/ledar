package net.icsl.ledar.web.util;

import org.apache.shiro.authc.AuthenticationToken;

public class UsernameToken implements AuthenticationToken {

    private String username;

    public UsernameToken(String username) {
        this.username = username;
    }

    @Override
    public String getPrincipal() {
        return getUsername();
    }

    @Override
    public String getCredentials() {
        return getUsername();
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
