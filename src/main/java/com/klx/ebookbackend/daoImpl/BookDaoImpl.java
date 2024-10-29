package com.klx.ebookbackend.daoImpl;

import com.klx.ebookbackend.dao.BookDao;
import com.klx.ebookbackend.entity.Book;
import com.klx.ebookbackend.repository.BookRepository;
import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.klx.ebookbackend.utils.RedisUtils;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class BookDaoImpl implements BookDao {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private RedisUtils redisUtils;

    private static final String BOOK_KEY_PREFIX = "book";

    /**
     * 获取所有书籍信息。
     * 目前不使用缓存，因为数据量较大，若缓存需求高频访问的书籍可以在此实现。
     */
    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    /**
     * 通过书籍ID获取书籍信息，首先检查缓存。
     * 若缓存中存在数据则直接返回，否则从数据库中查找并写入缓存。
     * @param id 书籍ID
     * @return 书籍对象
     */
//    @Override
//    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
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
                // 将书籍信息缓存到Redis
                redisUtils.set(redisKey, JSONArray.toJSON(book));
            }
            System.out.println("通过数据库获取书籍信息：" + id);
        } else {
            // 缓存中存在数据，直接从缓存返回
            book = JSONArray.parseObject(cachedBook.toString(), Book.class);
            System.out.println("通过Redis获取书籍信息：" + id);
        }
        return book;
    }


    /**
     * 保存或更新书籍信息。优先更新缓存中的数据，再将数据保存到数据库。
     * @param book 书籍对象
     * @return 保存后的书籍对象
     */

    @Override
    @Transactional
    public Book saveBook(Book book) {
        String redisKey = BOOK_KEY_PREFIX + book.getId();

        // 更新缓存中的书籍信息
        redisUtils.set(redisKey, JSONArray.toJSON(book));
        System.out.println("缓存中更新书籍信息：" + redisKey);

        // 保存到数据库
        Book savedBook = bookRepository.save(book);
        System.out.println("书籍已保存到数据库：" + savedBook.getId());
        return savedBook;
    }




    /**
     * 删除书籍信息。首先删除缓存中的数据，再删除数据库中的记录。
     * @param id 书籍ID
     */

    @Override
    public void deleteBook(int id) {
        String redisKey = BOOK_KEY_PREFIX + id;

        // 删除缓存中的书籍信息
        redisUtils.del(redisKey);
        System.out.println("缓存中删除书籍信息：" + redisKey);

        // 从数据库中删除
        bookRepository.deleteById(id);
        System.out.println("数据库中删除书籍记录：" + id);
    }

    /**
     * 根据关键词搜索书籍，支持分页。
     * @param keyword 搜索关键词
     * @param pageIndex 页码
     * @param pageSize 每页数量
     * @return 符合条件的书籍列表
     */
    @Override
    public List<Book> searchBooks(String keyword, int pageIndex, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);
        Page<Book> bookPage = bookRepository.findByKeyword(keyword, pageRequest);
        return bookPage.getContent();
    }

    /**
     * 获取畅销书列表。
     * @return 畅销书列表
     */
    @Override
    public List<Book> getTopSellingBooks() {
        List<Object[]> results = bookRepository.findTopSellingBooks();
        return results.stream().map(result -> {
            Book book = new Book();
            book.setId((Integer) result[0]);
            book.setTitle((String) result[1]);
            book.setAuthor((String) result[2]);
            book.setDescription((String) result[3]);
            book.setPrice((Double) result[4]);
            book.setCover((String) result[5]);
            book.setSales(((Number) result[6]).intValue());
            return book;
        }).collect(Collectors.toList());
    }

    /**
     * 获取符合关键词的书籍总数。
     * @param keyword 搜索关键词
     * @return 符合条件的书籍数量
     */
    @Override
    public int getTotalBooksCount(String keyword) {
        return (int) bookRepository.countByKeyword(keyword);
    }
}
