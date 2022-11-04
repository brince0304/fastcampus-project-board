package com.fastcampus.projectboard.dto;

import com.fastcampus.projectboard.domain.Article;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A DTO for the {@link com.fastcampus.projectboard.domain.ArticleComment} entity
 */
@Getter
@Setter

@NoArgsConstructor
public class ArticleWithCommentDto {
            Long id ;
            UserAccountDto userAccountDto ;
            Set<ArticleCommentDto> articleCommentDtos ;
            String title ;
            String content ;
            LocalDateTime createdAt ;
            String createdBy ;
            LocalDateTime modifiedAt ;
            String modifiedBy ;
            Set<HashtagDto> hashtags ;
            String deleted ;
            Integer viewCount ;

    public ArticleWithCommentDto(Long id, UserAccountDto userAccountDto, Set<ArticleCommentDto> articleCommentDtos, String title, String content, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy, Set<HashtagDto> hashtags, String deleted, Integer viewCount) {
        this.id = id;
        this.userAccountDto = userAccountDto;
        this.articleCommentDtos = articleCommentDtos;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.modifiedAt = modifiedAt;
        this.modifiedBy = modifiedBy;
        this.hashtags = hashtags;
        this.deleted = deleted;
        this.viewCount = viewCount;
    }

    public static ArticleWithCommentDto of(Long id, UserAccountDto userAccountDto, Set<ArticleCommentDto> articleCommentDtos, String title, String content, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy, Set<HashtagDto> hashtags, String deleted, Integer viewCount) {
        return new ArticleWithCommentDto(id, userAccountDto, articleCommentDtos, title, content,  createdAt, createdBy, modifiedAt, modifiedBy,hashtags,deleted, viewCount);
    }
    public static ArticleWithCommentDto of(Long id, UserAccountDto userAccountDto, Set<ArticleCommentDto> articleCommentDtos, String title, String content, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy, String deleted, Integer viewCount) {
        return new ArticleWithCommentDto(id, userAccountDto, articleCommentDtos, title, content,  createdAt, createdBy, modifiedAt, modifiedBy,null,deleted, viewCount);
    }

    public static ArticleWithCommentDto from(Article entity) {
        return new ArticleWithCommentDto(
                entity.getId(),
                UserAccountDto.from(entity.getUserAccount()),
                entity.getArticleComments().stream()
                        .map(ArticleCommentDto::from)
                        .collect(Collectors.toCollection(LinkedHashSet::new)),
                entity.getTitle(),
                entity.getContent(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy(),
                null,
                entity.getDeleted(),
                entity.getViewCount()
        );
    }

}