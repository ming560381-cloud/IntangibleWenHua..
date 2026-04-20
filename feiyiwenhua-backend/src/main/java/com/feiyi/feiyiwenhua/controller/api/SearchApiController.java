package com.feiyi.feiyiwenhua.controller.api;

import com.feiyi.feiyiwenhua.dto.SearchResultItem;
import com.feiyi.feiyiwenhua.entity.Activity;
import com.feiyi.feiyiwenhua.entity.Heritage;
import com.feiyi.feiyiwenhua.entity.Master;
import com.feiyi.feiyiwenhua.entity.Post;
import com.feiyi.feiyiwenhua.service.ActivityService;
import com.feiyi.feiyiwenhua.service.HeritageService;
import com.feiyi.feiyiwenhua.service.MasterService;
import com.feiyi.feiyiwenhua.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/search")
public class SearchApiController {

    @Autowired
    private HeritageService heritageService;

    @Autowired
    private MasterService masterService;

    @Autowired
    private PostService postService;

    @Autowired
    private ActivityService activityService;

    @GetMapping
    public ResponseEntity<List<SearchResultItem>> search(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "type", required = false) String type
    ) {
        String normalizedKeyword = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        String normalizedType = type == null ? "" : type.trim().toLowerCase(Locale.ROOT);

        List<SearchResultItem> results = new ArrayList<>();
        if (normalizedKeyword.isEmpty()) {
            return ResponseEntity.ok(results);
        }

        if (normalizedType.isEmpty() || "heritage".equals(normalizedType)) {
            for (Heritage heritage : heritageService.findAll()) {
                if (contains(heritage.getName(), normalizedKeyword)
                        || contains(heritage.getDescription(), normalizedKeyword)
                        || contains(heritage.getHistory(), normalizedKeyword)
                        || contains(heritage.getProcess(), normalizedKeyword)) {
                    SearchResultItem item = new SearchResultItem();
                    item.setType("heritage");
                    item.setId(heritage.getId());
                    item.setTitle(heritage.getName());
                    item.setSummary(heritage.getDescription());
                    item.setExtra("resource");
                    results.add(item);
                }
            }
        }

        if (normalizedType.isEmpty() || "master".equals(normalizedType)) {
            for (Master master : masterService.findAll()) {
                if (contains(master.getName(), normalizedKeyword)
                        || contains(master.getIntroduction(), normalizedKeyword)
                        || contains(master.getAchievements(), normalizedKeyword)) {
                    SearchResultItem item = new SearchResultItem();
                    item.setType("master");
                    item.setId(master.getId());
                    item.setTitle(master.getName());
                    item.setSummary(master.getIntroduction());
                    item.setExtra("master");
                    results.add(item);
                }
            }
        }

        if (normalizedType.isEmpty() || "activity".equals(normalizedType)) {
            for (Activity activity : activityService.findPublished()) {
                if (contains(activity.getTitle(), normalizedKeyword)
                        || contains(activity.getSummary(), normalizedKeyword)
                        || contains(activity.getDescription(), normalizedKeyword)
                        || contains(activity.getLocation(), normalizedKeyword)) {
                    SearchResultItem item = new SearchResultItem();
                    item.setType("activity");
                    item.setId(activity.getId());
                    item.setTitle(activity.getTitle());
                    item.setSummary(activity.getSummary());
                    item.setExtra("activity");
                    results.add(item);
                }
            }
        }

        if (normalizedType.isEmpty() || "post".equals(normalizedType)) {
            for (Post post : postService.findPublishedPosts()) {
                if (contains(post.getTitle(), normalizedKeyword)
                        || contains(post.getContent(), normalizedKeyword)
                        || contains(post.getTags(), normalizedKeyword)) {
                    SearchResultItem item = new SearchResultItem();
                    item.setType("post");
                    item.setId(post.getId());
                    item.setTitle(post.getTitle());
                    item.setSummary(post.getContent());
                    item.setExtra("community");
                    results.add(item);
                }
            }
        }

        return ResponseEntity.ok(results);
    }

    private boolean contains(String source, String keyword) {
        return source != null && source.toLowerCase(Locale.ROOT).contains(keyword);
    }
}