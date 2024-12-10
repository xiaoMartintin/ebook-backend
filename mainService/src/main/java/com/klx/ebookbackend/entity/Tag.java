package com.klx.ebookbackend.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Node("Tag") // 对应 Neo4j 中的节点类型
public class Tag {

    @Id
    @GeneratedValue
    private Long id; // Neo4j 自动生成的主键

    private String name; // 标签名称

    private List<Integer> bookIDs = new ArrayList<>(); // 与此标签关联的书籍 ID 列表

    @Relationship(type = "HAS_SUBTAG", direction = Relationship.Direction.OUTGOING)
    private Set<Tag> subtags = new HashSet<>(); // 子标签的关系

    // 添加子标签
    public void addSubtag(Tag subtag) {
        if (this.subtags == null) {
            this.subtags = new HashSet<>();
        }
        this.subtags.add(subtag);
    }

    // 添加书籍 ID
    public void addBookID(Integer bookID) {
        if (this.bookIDs == null) {
            this.bookIDs = new ArrayList<>();
        }
        if (!this.bookIDs.contains(bookID)) {
            this.bookIDs.add(bookID);
        }
    }
}