package com.fastcampus.projectboard.Controller;

import com.fastcampus.projectboard.Service.ArticleCommentService;
import com.fastcampus.projectboard.Service.UserService;
import com.fastcampus.projectboard.Util.ControllerUtil;
import com.fastcampus.projectboard.Util.CookieUtil;
import com.fastcampus.projectboard.Util.TokenProvider;
import com.fastcampus.projectboard.domain.forms.CommentForm;
import com.fastcampus.projectboard.dto.request.ArticleCommentRequest;
import com.fastcampus.projectboard.dto.security.BoardPrincipal;
import com.fastcampus.projectboard.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.logging.Logger;

@RequiredArgsConstructor
@RestController
@RequestMapping("/articles/comments")
public class ArticleCommentController {
    private final Logger log = Logger.getLogger(this.getClass().getName());

    private final ControllerUtil controllerUtil;
    private final ArticleCommentService articleCommentService;

    @GetMapping("/{articleId}")
    public ResponseEntity<?> getComments(@PathVariable Long articleId) {
        return new ResponseEntity<>(articleCommentService.searchArticleComments(articleId), HttpStatus.OK);
    }

    @GetMapping("/c/{commentId}")
    public ResponseEntity<?> getComment(@PathVariable Long commentId) {
        return new ResponseEntity<>(articleCommentService.getArticleComment(commentId), HttpStatus.OK);
    }


    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{articleId}")
    public ResponseEntity<?> writeArticleComment(@PathVariable Long articleId, @RequestBody @Valid ArticleCommentRequest dto,
                                              BindingResult bindingResult,
                                              @AuthenticationPrincipal BoardPrincipal principal) {
        if (bindingResult.hasErrors()) {
            log.info("bindingResult.hasErrors()");
            Map<String, String> errorMap = controllerUtil.getErrors(bindingResult);
            System.out.println(new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST));

            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
        }

        if (principal == null) {
            return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.BAD_REQUEST);
        }
        articleCommentService.saveArticleComment(dto.toDto(principal.toDto()));
        return new ResponseEntity<>("등록되었습니다.", HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/reply")
    public ResponseEntity<?> writeChildrenComment(@RequestBody @Valid ArticleCommentRequest dto,BindingResult bindingResult,  @AuthenticationPrincipal BoardPrincipal principal) {
        if(bindingResult.hasErrors()) {
            return new ResponseEntity<>(controllerUtil.getErrors(bindingResult),HttpStatus.BAD_REQUEST);
        }
        if (principal == null) {
            return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.BAD_REQUEST);
        }
        articleCommentService.saveChildrenComment(dto.parentId(), dto.toDto(principal.toDto()));
        return new ResponseEntity<>("등록되었습니다.", HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping
    public ResponseEntity<String> deleteArticleComment(@RequestBody Map<String,String> articleCommentId,
                                       @AuthenticationPrincipal BoardPrincipal principal) {
        Long id = Long.parseLong(articleCommentId.get("articleCommentId"));
        if (principal == null) {
            return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.BAD_REQUEST);
        }
        if (articleCommentService.getArticleComment(id).userAccountDto().userId().equals(principal.getUsername())) {
            articleCommentService.deleteArticleComment(id);
            return new ResponseEntity<>("articleCommentDeleting Success", HttpStatus.OK);
        } else if (principal.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            articleCommentService.deleteArticleComment(id);
            return new ResponseEntity<>("articleCommentDeleting Success", HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>("삭제 권한이 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping
    public ResponseEntity<?> updateArticleComment(@RequestBody @Valid ArticleCommentRequest dto,BindingResult result,
                                       @AuthenticationPrincipal BoardPrincipal principal) {
        Long articleCommentId = dto.articleCommentId();
        String content = dto.content();
        if (result.hasErrors()) {
            return new ResponseEntity<>(controllerUtil.getErrors(result), HttpStatus.BAD_REQUEST);
        }
        if (principal == null) {
            return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.BAD_REQUEST);
        } else {
            if (articleCommentService.getArticleComment(articleCommentId).userAccountDto().userId().equals(principal.getUsername())) {
                articleCommentService.updateArticleComment(articleCommentId, content);
                return new ResponseEntity<>("수정되었습니다.", HttpStatus.OK);

            }
        }
        return new ResponseEntity<>("수정에 실패하였습니다.", HttpStatus.BAD_REQUEST);
    }


}
