package com.driver.services;

import com.driver.models.Author;
import com.driver.models.Book;
import com.driver.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
//import java.util.List;
import java.util.List;
import java.util.Optional;


@Service
public class BookService {


    @Autowired
    BookRepository bookRepository2;

    public void createBook(Book book){
        Author author1 = book.getAuthor();
        if(author1 == null){
            assert false;
            if(author1.getBooksWritten() == null){
                author1.setBooksWritten(new ArrayList<>());
            }
            author1.getBooksWritten().add(book);
        }

        bookRepository2.save(book);
    }


   // For example:
   // i) If genre=”X”, availability = true, and author=null;
   // we require the list of all books which are available and have genre “X”.
   // Note that these books can be written by any author.
   //
   // ii) If genre=”Y”, availability = false, and author=”A”;
   // we require the list of all books which are written by author “A”, have genre “Y”,
   // and are currently unavailable.
    public List<Book> getBooks(String genre, boolean available, String author){

        List<Book> books = null; //find the elements of the list by yourself

        if(genre != null && available && author == null){
            books = bookRepository2.findBooksByGenre(genre, available);
        }else if(genre != null && !available && author != null){
            books = bookRepository2.findBooksByGenreAuthor(genre, author, available);
        }

        return books;
    }
}
