package com.klx.ebookbackend.repository;

import com.klx.ebookbackend.entity.Tag;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagRepository extends Neo4jRepository<Tag, Long> {

    // 查询某标签及其两跳范围内的相关标签，包括自身。注意：是双向的而不是单向的！！！
    @Query("""
       MATCH (t:Tag {name: $name})
       OPTIONAL MATCH (t)-[:HAS_SUBTAG*0..2]-(related:Tag)
       RETURN DISTINCT related
       """)
    List<Tag> findTagsWithinTwoHopsIncludingSelf(@Param("name") String name);

    @Query("MATCH (t:Tag) WHERE t.name CONTAINS $name RETURN t")
    List<Tag> findByNameContaining(@Param("name") String name);

    boolean existsByName(String name);
}