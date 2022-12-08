package com.fastcampus.projectboard.Service;

import com.fastcampus.projectboard.Util.FileUtil;
import com.fastcampus.projectboard.domain.Article;
import com.fastcampus.projectboard.domain.SaveFile;
import com.fastcampus.projectboard.domain.UserAccount;
import com.fastcampus.projectboard.repository.ArticleRepository;
import com.fastcampus.projectboard.repository.SaveFileRepository;
import com.fastcampus.projectboard.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserAccountRepository userAccountRepository;

    private final ArticleRepository articleRepository;
    private final SaveFileRepository fileRepository;

    @Value("${com.example.upload.path.profileImg}") // application.properties의 변수
    private String uploadPath;



    public String saveUserAccount(UserAccount.SignupDto user, SaveFile.FileDto fileDto) throws IOException {
        String password = user.getPassword1();
        UserAccount account = userAccountRepository.save(user.toEntity());
        account.setUserPassword(new BCryptPasswordEncoder().encode(password));
        account.setProfileImg(fileDto.toEntity());
        return user.getUserId();
    }

    public SaveFile.FileDto changeAccountProfileImg(String id, SaveFile.FileDto fileDto) throws IOException {
        UserAccount account = userAccountRepository.findById(id).orElseThrow(()->new IllegalArgumentException("해당 유저가 없습니다."));
        if(account.getProfileImg()!=null && !account.getProfileImg().getFileSize().equals(0L)){
            FileUtil.deleteFile(SaveFile.FileDto.from(account.getProfileImg()));
        }
        SaveFile.FileDto currentImg = SaveFile.FileDto.from(account.getProfileImg());
        account.setProfileImg(fileDto.toEntity());
        return currentImg;
    }




    @Transactional(readOnly = true)
    public boolean isUserExists(String userId){
        return userAccountRepository.existsById(userId);
    }



    // 토큰 재발급 관련 메서드


    // 권한 가져오기
    @Transactional(readOnly = true)
    public String getAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }
    public void updateUserAccount(String userId,UserAccount.UserAccountUpdateRequestDto userDto) {
        try {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            UserAccount userAccount = userAccountRepository.findById(userId).orElseThrow(()->new EntityNotFoundException("해당 유저가 없습니다."));
            if (userDto.password1() != null) { userAccount.setUserPassword(passwordEncoder.encode(userDto.password1())); }
            if (userDto.nickname() != null) { userAccount.setNickname(userDto.nickname()); }
            if (userDto.email() != null) { userAccount.setEmail(userDto.email()); }
            if (userDto.memo() != null) { userAccount.setMemo(userDto.memo()); }
        } catch (Exception e) {
            throw new IllegalArgumentException("회원정보 수정에 실패했습니다");
        }
    }

    @Transactional(readOnly = true)
    public UserAccount.UserAccountDto getUserAccount(String userId) {
        try {
            UserAccount userAccount = userAccountRepository.findById(userId).orElseThrow(()->new EntityNotFoundException("해당 유저가 없습니다."));
            return UserAccount.UserAccountDto.from(userAccount);
        } catch (Exception e) {
            throw new IllegalArgumentException("회원정보 조회에 실패했습니다");
        }
    }

    public void deleteUserAccount(String userId) {
        try {
            userAccountRepository.deleteById(userId);
        } catch (Exception e) {
            throw new IllegalArgumentException("회원정보 삭제에 실패했습니다");
        }
    }
    @Transactional(readOnly = true)
    public boolean isExistUser(String userId) {
        return userAccountRepository.existsByUserId(userId);
    }

    @Transactional(readOnly = true)
    public boolean isExistNickname(String nickname) {
        return userAccountRepository.existsByNickname(nickname);
    }
    @Transactional(readOnly = true)
    public boolean isExistEmail(String email) {
        return userAccountRepository.existsByEmail(email);
    }

    public UserAccount.BoardPrincipal loginByUserNameAndPassword(String username, String password) {
          UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(username, password);
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        return UserAccount.BoardPrincipal.from(UserAccount.UserAccountDto.from(userAccountRepository.findById(username).orElseThrow()));
    }

    public Set<Article.ArticleDto> getMyArticles(String username) {
        Set<Article> articles = articleRepository.findAllByUserAccountUserId(username);
        Set<Article.ArticleDto> articleDtos = new HashSet<>();
        for (Article article : articles) {
            articleDtos.add(Article.ArticleDto.from(article));
        }
        return articleDtos;
    }

    public String saveUserAccountWithoutProfile(UserAccount.SignupDto user) throws IOException {
        SaveFile defaultImg = fileRepository.findByFileName("default.png");
        String password = user.getPassword1();
        UserAccount account = userAccountRepository.save(user.toEntity());
        account.setUserPassword(new BCryptPasswordEncoder().encode(password));
        if(defaultImg!=null){
            account.setProfileImg(defaultImg);
        }
        else{
            SaveFile saveFile = SaveFile.builder()
                    .fileName("default.jpg")
                    .filePath(uploadPath+"/default.jpg")
                    .fileSize(0L)
                    .fileType("jpg")
                    .uploadUser(account.getUserId())
                    .build();
            fileRepository.save(saveFile);
            account.setProfileImg(saveFile);
        }
        return account.getUserId();
    }
}
