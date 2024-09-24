package com.rss.nest.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.rometools.rome.feed.rss.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Date;

@Slf4j
@ApiSupport(order = 1)
@Api(tags = "RSS订阅")
@RestController
@RequestMapping("/rss/feed")
public class RssController {

    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "RSS测试", notes = "")
    @GetMapping(path = "/rss1")
    public Channel rss() {
        Channel channel = new Channel();
        channel.setFeedType("rss_2.0");
        channel.setTitle("HowToDoInJava Feed");
        channel.setDescription("Different Articles on latest technology");
        channel.setLink("https://howtodoinjava.com");
        channel.setUri("https://howtodoinjava.com");
        channel.setGenerator("In House Programming");

        Image image = new Image();
        image.setUrl("https://howtodoinjava.com/wp-content/uploads/2015/05/howtodoinjava_logo-55696c1cv1_site_icon-32x32.png");
        image.setTitle("HowToDoInJava Feed");
        image.setHeight(32);
        image.setWidth(32);
        channel.setImage(image);

        Date postDate = new Date();
        channel.setPubDate(postDate);

        Item item = new Item();
        item.setAuthor("Lokesh Gupta");
        item.setLink("https://howtodoinjava.com/spring5/webmvc/spring-mvc-cors-configuration/");
        item.setTitle("Spring CORS Configuration Examples");
        item.setUri("https://howtodoinjava.com/spring5/webmvc/spring-mvc-cors-configuration/");
        item.setComments("https://howtodoinjava.com/spring5/webmvc/spring-mvc-cors-configuration/#respond");

        com.rometools.rome.feed.rss.Category category = new com.rometools.rome.feed.rss.Category();
        category.setValue("CORS");
        item.setCategories(Collections.singletonList(category));

        Description descr = new Description();
        descr.setValue(
                "CORS helps in serving web content from multiple domains into browsers who usually have the same-origin security policy. In this example, we will learn to enable CORS support in Spring MVC application at method and global level."
                        + "The post <a rel=\"nofollow\" href=\"https://howtodoinjava.com/spring5/webmvc/spring-mvc-cors-configuration/\">Spring CORS Configuration Examples</a> appeared first on <a rel=\"nofollow\" href=\"https://howtodoinjava.com\">HowToDoInJava</a>.");
        item.setDescription(descr);
        item.setPubDate(postDate);

        channel.setItems(Collections.singletonList(item));
        //Like more Entries here about different new topics
        return channel;
    }

}
