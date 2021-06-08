package com.zing.springcloudalibaba.controller;

import com.zing.springcloudalibaba.domain.Blog;
import com.zing.springcloudalibaba.service.BlogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Zing
 * @date 2020-07-13
 */
@Slf4j
@RestController
@RequestMapping("/blog")
public class BlogController {

    @Autowired
    private BlogService blogService;

    @GetMapping("/{id}")
    public Blog blog(@PathVariable Long id) {
        return blogService.getById(id);
    }

    @PostMapping("/post")
    public String postBlog(@RequestBody Blog blog) {
        blogService.postBlog(blog);
        return "SUCCESS";
    }

    @PostMapping("/post2")
    public String postBlog2(@RequestBody Blog blog) {
        blogService.postBlog2(blog);
        return "SUCCESS";
    }

}
