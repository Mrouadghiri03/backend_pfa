package com.pfa.api.app.exception;

public class InitialPasswordChangeRequiredException extends RuntimeException {

    public InitialPasswordChangeRequiredException() {
        super("Changement de mot de passe initial requis");
    }

    public InitialPasswordChangeRequiredException(String message) {
        super(message);
    }
}
