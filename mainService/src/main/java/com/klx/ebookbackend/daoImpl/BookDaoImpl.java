package com.klx.ebookbackend.daoImpl;

import com.klx.ebookbackend.dao.BookDao;
import com.klx.ebookbackend.entity.Book;
import com.klx.ebookbackend.entity.MongoBook;
import com.klx.ebookbackend.entity.Tag;
import com.klx.ebookbackend.repository.BookRepository;
import com.klx.ebookbackend.repository.MongoBookRepository;
import com.alibaba.fastjson.JSONArray;
import com.klx.ebookbackend.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.klx.ebookbackend.utils.RedisUtils;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class BookDaoImpl implements BookDao {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MongoBookRepository mongoBookRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private RedisUtils redisUtils;

    private static final String BOOK_KEY_PREFIX = "book";

    /**
     * 获取所有书籍信息。
     * 从 MySQL 获取基础信息，并从 MongoDB 获取图片和描述。
     */
    @Override
    public List<Book> getAllBooks() {
        List<Book> books = bookRepository.findAll();

        // 补充 MongoDB 的图片和描述
        books.forEach(book -> {
            MongoBook mongoBook = mongoBookRepository.findByBookId(book.getId());
            if (mongoBook != null) {
                book.setCover(mongoBook.getImageBase64());
                book.setDescription(mongoBook.getDescription());
            }
        });

        return books;
    }

    /**
     * 通过书籍ID获取书籍信息，屏蔽 MySQL 和 MongoDB 的差异。
     * 从 Redis 缓存、MySQL 和 MongoDB 分别获取数据。
     */
    @Override
    @Transactional(readOnly = true)
    public Book getBookById(int id) {
        Book book;
        String redisKey = BOOK_KEY_PREFIX + id;

        // 从缓存中查找书籍信息
        Object cachedBook = redisUtils.get(redisKey);

        if (cachedBook == null) {
            // 如果缓存中不存在，则从数据库中获取
            book = bookRepository.findById(id).orElse(null);

            if (book != null) {
                // 从 MongoDB 获取图片和描述信息
                MongoBook mongoBook = mongoBookRepository.findByBookId(id);
                if (mongoBook != null) {
                    System.out.println("MongoDB Data: " + mongoBook);
                    book.setCover(mongoBook.getImageBase64());
                    book.setDescription(mongoBook.getDescription());
                } else {
                    System.out.println("No MongoDB Data found for bookId: " + id);
                }

                // 将书籍信息缓存到 Redis
                redisUtils.set(redisKey, JSONArray.toJSON(book));
            }
            System.out.println("通过数据库获取书籍信息：" + id);
        } else {
            // 缓存中存在数据，直接从缓存返回
            book = JSONArray.parseObject(cachedBook.toString(), Book.class);
            System.out.println("通过 Redis 获取书籍信息：" + id);
        }

        return book;
    }

    /**
     * 保存或更新书籍信息。
     * 将基础信息保存到 MySQL，将图片和描述保存到 MongoDB，同时更新 Redis 缓存。
     */
    @Override
    @Transactional
    public Book saveBook(Book book) {
        String redisKey = BOOK_KEY_PREFIX + book.getId();

        // 保存到 MySQL
        Book savedBook = bookRepository.save(book);

        // 保存到 MongoDB
        MongoBook mongoBook = new MongoBook();
        mongoBook.setBookId(savedBook.getId());
        mongoBook.setImageBase64(savedBook.getCover());
        mongoBook.setDescription(savedBook.getDescription());
        mongoBookRepository.save(mongoBook);

        // 更新缓存
        redisUtils.set(redisKey, JSONArray.toJSON(savedBook));
        System.out.println("缓存中更新书籍信息：" + redisKey);

        return savedBook;
    }

    /**
     * 删除书籍信息。
     * 删除 MySQL 和 MongoDB 中的记录，同时清除 Redis 缓存。
     */
    @Override
    public void deleteBook(int id) {
        String redisKey = BOOK_KEY_PREFIX + id;

        // 删除 MySQL 中的记录
        bookRepository.deleteById(id);

        // 删除 MongoDB 中的记录
        MongoBook mongoBook = mongoBookRepository.findByBookId(id);
        if (mongoBook != null) {
            mongoBookRepository.delete(mongoBook);
        }

        // 删除缓存
        redisUtils.del(redisKey);
        System.out.println("缓存中删除书籍信息：" + redisKey);
    }

    /**
     * 根据关键词搜索书籍，支持分页。
     * 仅从 MySQL 中搜索基础数据，并从 MongoDB 补充图片和描述。
     */
    @Override
    public List<Book> searchBooks(String keyword, int pageIndex, int pageSize) {
        // 如果是标签，执行标签搜索
        if (isTag(keyword)) {
            return findBooksByTagRelation(keyword);
        }

        // 关键字搜索逻辑
        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);
        Page<Book> bookPage = bookRepository.findByKeyword(keyword, pageRequest);

        // 补充 MongoDB 的图片和描述
        List<Book> books = bookPage.getContent();
        books.forEach(book -> {
            MongoBook mongoBook = mongoBookRepository.findByBookId(book.getId());
            if (mongoBook != null) {
                book.setCover(mongoBook.getImageBase64());
                book.setDescription(mongoBook.getDescription());
            }
        });

        return books;
    }

    @Override
    public boolean isTag(String keyword) {
        try {
            List<Tag> tags = tagRepository.findByNameContaining(keyword);
            return !tags.isEmpty();
        } catch (Exception e) {
            // 在此处进行异常处理，可以选择记录日志
            // logger.error("查询标签时发生异常", e);
            return false;
        }
    }

    /**
     * 获取畅销书列表。
     * 从 MySQL 获取基础信息，并从 MongoDB 补充图片和描述。
     */
    @Override
    public List<Book> getTopSellingBooks() {
        List<Object[]> results = bookRepository.findTopSellingBooks();
        return results.stream().map(result -> {
            Book book = new Book();
            book.setId((Integer) result[0]);
            book.setTitle((String) result[1]);
            book.setAuthor((String) result[2]);
            book.setPrice((Double) result[3]);
            book.setSales(((Number) result[4]).intValue());

            // 补充 MongoDB 的图片和描述
            MongoBook mongoBook = mongoBookRepository.findByBookId(book.getId());
            if (mongoBook != null) {
                book.setCover(mongoBook.getImageBase64());
                book.setDescription(mongoBook.getDescription());
            }

            return book;
        }).collect(Collectors.toList());
    }

    /**
     * 获取符合关键词的书籍总数。
     * 仅从 MySQL 中获取数量信息。
     */
    @Override
    public int getTotalBooksCount(String keyword) {
        return (int) bookRepository.countByKeyword(keyword);
    }


    /**
     * 根据标签搜索书籍。
     * 首先从 Neo4j 获取给定标签及其两跳范围内的相关标签。
     * 然后从 MySQL 获取所有与这些标签关联的书籍详情。
     * 最后，从 MongoDB 补充书籍的图片和描述信息。
     *
     * @param tagName 标签名称
     * @return 符合条件的书籍列表
     */
    @Override
    public List<Book> findBooksByTagRelation(String tagName) {
        // 查询标签及其两跳范围内的相关标签，包括自身
        List<Tag> relatedTags = tagRepository.findByNameContaining(tagName)
                .stream()
                .flatMap(t -> tagRepository.findTagsWithinTwoHopsIncludingSelf(t.getName()).stream())
                .distinct()
                .collect(Collectors.toList());

        // 收集所有关联标签的书籍 ID
        Set<Integer> bookIDs = new HashSet<>();
        for (Tag tag : relatedTags) {
            if (tag.getBookIDs() != null) {
                bookIDs.addAll(tag.getBookIDs());
            }
        }

        // 如果没有关联的书籍 ID，返回空列表
        if (bookIDs.isEmpty()) {
            return new ArrayList<>();
        }

        // 使用 MySQL 的 BookRepository 查询书籍详情
        List<Book> books = bookRepository.findAllById(bookIDs);

        // 补充 MongoDB 信息
        books.forEach(book -> {
            MongoBook mongoBook = mongoBookRepository.findByBookId(book.getId());
            if (mongoBook != null) {
                book.setCover(mongoBook.getImageBase64());
                book.setDescription(mongoBook.getDescription());
            }
        });

        return books;
    }
}