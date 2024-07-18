package com.pfa.api.app.service.implementation;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.pfa.api.app.entity.Project;
import com.pfa.api.app.entity.user.User;
import com.pfa.api.app.service.EmailService;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImplementation implements EmailService{

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    

    @Override
    @Async
    public void sendConfirmationEmail(String name, String to, String token) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Registration Confirmation");

            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("confirmationURL", "http://localhost:8080/api/auth/verify?token=" + token); // Replace with your

            String htmlContent = templateEngine.process("email/emailConfirmationTemplate", context);
            helper.setText(htmlContent, true);
            javaMailSender.send(mimeMessage);

        } catch (Exception e) {
            System.out.println("message : " + e.getMessage());
            System.out.println("cause : " + e.getCause());
        }
    }

    @Override
    @Async
    public void sendNotificationEmailToHeadOfBranch(User headOfBranch, User newUser,String token) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(headOfBranch.getEmail());
            helper.setSubject("New User Notification");

            Context context = new Context();
            context.setVariable("headOfBranchName", headOfBranch.getFirstName());
            context.setVariable("userName", newUser.getFirstName()+" "+newUser.getLastName());
            context.setVariable("email", newUser.getEmail());
            context.setVariable("branch", newUser.getStudiedBranch().getName());
            context.setVariable("cin", newUser.getCin());
            context.setVariable("inscriptionNumber", newUser.getInscriptionNumber());
            context.setVariable("acceptURL", "http://localhost:8080/api/auth/accept/token" + token);
            context.setVariable("rejectURL", "http://localhost:8080/api/auth/reject/token" + token);


            String htmlContent = templateEngine.process("email/emailNotificationToHeadOfBranch", context);
            helper.setText(htmlContent, true);
            javaMailSender.send(mimeMessage);

        } catch (Exception e) {
            System.out.println("message : " + e.getMessage());
            System.out.println("cause : " + e.getCause());
        }
    }

	@Override
    @Async
	public void sendRejectionEmail(User to, String token) {
		try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            System.out.println(to.getEmail());
            helper.setTo(to.getEmail());
            helper.setSubject("Registration Rejection");

            Context context = new Context();
            context.setVariable("name", to.getFirstName());
            System.out.println(to.getStudiedBranch().getName());
            context.setVariable("branch", to.getStudiedBranch().getName() );
            
                                                                                                         
            String htmlContent = templateEngine.process("email/emailRejectionTemplate", context);
            helper.setText(htmlContent, true);
            javaMailSender.send(mimeMessage);

        } catch (Exception e) {
            System.out.println("message : " + e.getMessage());
            System.out.println("cause : " + e.getCause());
        }

	}

    @SuppressWarnings("null")
    @Override
    @Async
    public void sendProjectApprovalEmail(Project project) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            System.out.println(project.getBranch().getHeadOfBranch().getEmail());
            helper.setTo(project.getBranch().getHeadOfBranch().getEmail());
            helper.setSubject("Project Approval");

            Context context = new Context();
            context.setVariable("headOfBranch", project.getBranch().getHeadOfBranch().getFirstName());
            System.out.println(project.getBranch().getHeadOfBranch().getBranch().getName());
            context.setVariable("branch", project.getBranch().getName());
            context.setVariable("supervisor", project.getSupervisors().get(0).getFirstName() + " "
                                    +project.getSupervisors().get(0).getLastName());

            context.setVariable("acceptURL", "http://localhost:8080/api/projects/accept?token="+project.getApprovalToken() );
            context.setVariable("rejectURL", "http://localhost:8080/api/projects/reject?token=" +project.getApprovalToken());

            String htmlContent = templateEngine.process("email/emailProjectApprovalTemplate", context);
            helper.setText(htmlContent, true);
            javaMailSender.send(mimeMessage);

        } catch (Exception e) {
            System.out.println("message : " + e.getMessage());
            System.out.println("cause : " + e.getCause());
        }
    }

    @Override
    @Async
    public void sendInformingEmail(User to, String message) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to.getEmail());
            helper.setSubject("Information");

            Context context = new Context();
            context.setVariable("name", to.getFirstName());
            context.setVariable("message", message);

            String htmlContent = templateEngine.process("email/emailInformingTemplate", context);
            helper.setText(htmlContent, true);
            javaMailSender.send(mimeMessage);

        } catch (Exception e) {
            System.out.println("message : " + e.getMessage());
            System.out.println("cause : " + e.getCause());
        }
    }

    @Override
    @Async
    public void sendForgotPasswordEmail(User user) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(user.getEmail());
            helper.setSubject("Forgot Password");

            Context context = new Context();
            context.setVariable("name", user.getFirstName());
            context.setVariable("code", user.getResetCode());

            String htmlContent = templateEngine.process("email/emailForgotPasswordTemplate", context);
            helper.setText(htmlContent, true);
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            System.out.println("message : " + e.getMessage());
        }
    }

    

}
