package com.feiyi.feiyiwenhua.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostLikeResponse {
    private Long postId;
    private Integer likeCount;
    private Boolean liked;
}
