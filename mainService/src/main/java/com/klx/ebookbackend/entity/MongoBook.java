package com.klx.ebookbackend.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@Document(collection = "book_images") // 对应 MongoDB 的集合名称
public class MongoBook {

    @Id
    private String id; // 对应 MongoDB 的 `_id` 字段

    @Field("book_id") // 显式映射 MongoDB 的 `book_id` 字段
    private Integer bookId;

    @Field("image_base64") // 显式映射 MongoDB 的 `image_base64` 字段
    private String imageBase64;

    @Field("description") // 显式映射 MongoDB 的 `description` 字段
    private String description;
}