package com.abc.luntan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Data
@Document(indexName = "article",createIndex = true)
@NoArgsConstructor
@AllArgsConstructor
public class EsArticleDTO {
    /**
     * 文章id
     */
    @Id
    private Integer articleId;

    /**
     * 文章标题
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String articleTitle;

    /**
     * 文章内容
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String articleContent;

    /**
     * 创建时间
     */
    @Field(type = FieldType.Date, format = DateFormat.year_month_day)
    private Date createdTime;

    /**
     * 外键，对应category_id
     */
    @Field(type = FieldType.Integer)
    private Integer articleCategoryId;

    /**
     * 分类表中对应category_name
     */
    @Field(type = FieldType.Keyword)
    private String articleCategoryName;

}
