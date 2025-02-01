package umc.codeplay.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import umc.codeplay.apiPayLoad.code.status.ErrorStatus;
import umc.codeplay.apiPayLoad.exception.GeneralException;
import umc.codeplay.domain.Member;
import umc.codeplay.dto.EmailCodeDTO;
import umc.codeplay.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final MemberRepository memberRepository;
    // application.yml에 발신 메일 설정 필요
    private final JavaMailSender mailSender;

    // 인증번호 저장
    private final Map<String, EmailCodeDTO.VerificationCode> verificationCodes =
            new ConcurrentHashMap<>();

    // 비밀번호 찾기 인증번호 메일 보내기
    public void sendCode(String email) throws MessagingException {
        Member member =
                memberRepository
                        .findByEmail(email)
                        .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        // 인증번호 생성(5자리)
        Random random = new Random();
        final String code = String.format("%05d", random.nextInt(100000));

        // 인증번호 저장 및 만료시간 설정
        EmailCodeDTO.VerificationCode verificationCode =
                new EmailCodeDTO.VerificationCode(code, LocalDateTime.now().plusMinutes(5));
        verificationCodes.put(email, verificationCode);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            String text =
                    "<html>"
                            + "<body>"
                            + "<h2>안녕하세요, CodePlay입니다.</h2>"
                            + "<p>비밀번호 재설정 코드입니다.</p>"
                            + "<p><strong>인증번호: "
                            + code
                            + "</strong></p>"
                            + "<p>이 코드는 5분간 유효합니다.</p>"
                            + "<p>CodePlay 팀 드림</p>"
                            + "</body>"
                            + "</html>";

            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email); // 수신자 이메일
            helper.setSubject("CodePlay 비밀번호 재설정 코드"); // 이메일 제목
            helper.setText(text, true); // HTML 본문을 true로 설정

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new GeneralException(ErrorStatus.EMAIL_SEND_ERROR);
        }
    }

    // 코드 검증
    public boolean verifyCode(String email, String code) {
        EmailCodeDTO.VerificationCode verificationCode = verificationCodes.get(email);
        if (verificationCode == null) {
            return false;
        }
        if (verificationCode.getExpires().isBefore(LocalDateTime.now())) {
            verificationCodes.remove(email); // 만료 코드 삭제
            return false;
        }
        return verificationCode.getCode().equals(code);
    }
}
