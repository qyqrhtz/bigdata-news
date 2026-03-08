package com.example.bigdata_news.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "news")
public class News {

    @Id
    @Column(length = 64)
    private String uniquekey;

    @Column(length = 500)
    private String title;

    private LocalDateTime date;

    @Column(length = 50)
    private String category;

    @Column(name = "author_name", length = 100)
    private String authorName;

    @Column(length = 500)
    private String url;

    @Column(name = "thumbnail_pic_s", length = 500)
    private String thumbnailPicS;

    @Column(name = "thumbnail_pic_s02", length = 500)
    private String thumbnailPicS02;

    @Column(name = "thumbnail_pic_s03", length = 500)
    private String thumbnailPicS03;

    @Column(name = "is_content")
    private Integer isContent;
}