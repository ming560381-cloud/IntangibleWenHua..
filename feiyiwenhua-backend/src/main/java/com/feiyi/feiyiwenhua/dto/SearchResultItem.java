package com.feiyi.feiyiwenhua.dto;

import lombok.Data;

@Data
public class SearchResultItem {
    private String type;
    private Long id;
    private String title;
    private String summary;
    private String extra;
}
