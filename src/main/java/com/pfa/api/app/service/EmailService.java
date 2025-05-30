package com.pfa.api.app.service;

import com.pfa.api.app.entity.Project;
import com.pfa.api.app.entity.user.User;

public interface EmailService {
    void sendConfirmationEmail(String name,String to,String token);
    void sendRejectionEmail(User to,String token);
    void sendNotificationEmailToHeadOfBranch(User headOfBranchName , User to,String token);
    void sendProjectApprovalEmail(Project project);
    void sendInformingEmail(User to, String message);
    void sendForgotPasswordEmail(User user);
    void sendInformingEmailToNewUser(User user,String msg);
}
