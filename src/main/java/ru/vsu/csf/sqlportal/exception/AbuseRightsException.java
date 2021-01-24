package ru.vsu.csf.sqlportal.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AbuseRightsException extends RuntimeException {
    //    private static final Logger log = Logger.getLogger(ResourceNotFoundException.class);
    private String login;

    public AbuseRightsException(String login) {
        super(String.format("User %s exceeded rights", login));
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
