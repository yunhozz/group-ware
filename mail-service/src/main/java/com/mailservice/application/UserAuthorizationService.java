package com.mailservice.application;

import com.mailservice.application.exception.EmailSendFailException;
import com.mailservice.common.util.RedisUtils;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserAuthorizationService {

    private final JavaMailSender mailSender;
    private final RedisUtils redisUtils;

    public void sendEmailWithCode(String email) {
        MimeMessage message = mailSender.createMimeMessage();
        String code = generateRandomCode();
        String text = createText(code);

        try {
            message.setFrom("qkrdbsgh1121@naver.com");
            message.setRecipients(Message.RecipientType.TO, email);
            message.setSubject("회원가입 인증 메일입니다.");
            message.setText(text, "UTF-8", "HTML");

            mailSender.send(message);
            redisUtils.saveValue(email, code, Duration.ofMinutes(5));

        } catch (MessagingException e) {
            throw new EmailSendFailException();
        }
    }

    public void verifyCode(String email, String code) {
        String verifyCode = redisUtils.getValue(email)
                .orElseThrow(() -> new IllegalStateException("인증 코드가 만료되었거나 존재하지 않습니다. 다시 발급해주세요."));
        if (!verifyCode.equals(code)) {
            throw new IllegalStateException("입력하신 코드와 일치하지 않습니다.");
        }

        redisUtils.deleteValue(email);
        // TODO: 2023-03-05 유저의 권한 변경 : GUEST -> USER
    }

    private String generateRandomCode() {
        Random random = new Random();
        char[] code = new char[8];

        for (int i = 0; i < code.length; i++) {
            int num = random.nextInt(3); // 0: 숫자, 1: 대문자, 2: 소문자
            switch (num) {
                case 0 : code[i] = (char) (random.nextInt(10) + '0');
                case 1 : code[i] = (char) (random.nextInt(26) + 65);
                case 2 : code[i] = (char) (random.nextInt(26) + 97);
            }
        }

        return String.valueOf(code);
    }

    private String createText(String code) {
        String text = "";
        text += "<div style='margin:100px;'>";
        text += "<h1> 안녕하세요! 운영자입니다. </h1>";
        text += "<br>";
        text += "<p>아래 코드를 5분 내로 입력해주세요<p>";
        text += "<br>";
        text += "<p>감사합니다!<p>";
        text += "<br>";
        text += "<div align='center' style='border:1px solid black; font-family:verdana';>";
        text += "<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>";
        text += "<div style='font-size:130%'>";
        text += "CODE : <strong>";
        text += code + "</strong><div><br/> ";
        text += "</div>";

        return text;
    }
}