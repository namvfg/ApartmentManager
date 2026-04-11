package com.and.apartmentmanager.helper;

import com.and.apartmentmanager.BuildConfig;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Gửi OTP qua Gmail SMTP (JavaMail). Mật khẩu ứng dụng lấy từ {@code local.properties}
 * (build vào {@link BuildConfig}), không commit lên git.
 */
public final class OtpEmailSender {

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";

    private OtpEmailSender() {
    }

    public static boolean isConfigured() {
        String u = BuildConfig.SMTP_OTP_USER;
        String p = BuildConfig.SMTP_OTP_PASSWORD;
        return u != null && !u.trim().isEmpty() && p != null && !p.trim().isEmpty();
    }

    /**
     * Gửi email chứa mã OTP. Phải gọi trên luồng nền (không phải main thread).
     *
     * @return true nếu gửi thành công
     */
    public static boolean sendOtpEmail(String toEmail, String otp, OtpEmailKind kind) {
        if (!isConfigured()) {
            return false;
        }
        String user = BuildConfig.SMTP_OTP_USER.trim();
        String pass = BuildConfig.SMTP_OTP_PASSWORD;

        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, pass);
            }
        });

        try {
            String subject;
            String body;
            if (kind == OtpEmailKind.PASSWORD_RESET) {
                subject = "Mã OTP đặt lại mật khẩu — Apartment Manager";
                body = "Mã OTP của bạn là: " + otp + "\n\nMã có hiệu lực trong 5 phút. Nếu bạn không yêu cầu, hãy bỏ qua email này.";
            } else {
                subject = "Mã OTP xác thực đăng ký — Apartment Manager";
                body = "Mã OTP của bạn là: " + otp + "\n\nMã có hiệu lực trong 5 phút.";
            }

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail.trim()));
            message.setSubject(subject, "UTF-8");
            message.setText(body, "UTF-8");

            Transport.send(message);
            return true;
        } catch (Exception e) {
            android.util.Log.e("OtpEmailSender", "SMTP send failed", e);
            return false;
        }
    }

    public enum OtpEmailKind {
        PASSWORD_RESET,
        REGISTRATION
    }
}
