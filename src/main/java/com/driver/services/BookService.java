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
        Author author = book.getAuthor();
        if(author != null){
            if(author.getBooksWritten() == null){
                author.setBooksWritten(new ArrayList<>());
            }
            author.getBooksWritten().add(book);
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
       List<Book> books = new ArrayList<>();
       if(available){
           if(genre != null && author != null){
               books.addAll(bookRepository2.findBooksByGenreAuthor(genre,author,true));
           }
           else if(genre != null && author == null){
               books.addAll(bookRepository2.findBooksByGenre(genre,true));
           }
           else if(genre == null && author != null){
               books.addAll(bookRepository2.findBooksByAuthor(author,true));
           }
           else {
               books.addAll(bookRepository2.findByAvailability(true));
           }
       }
       else{
           if(genre != null && author != null){
               books.addAll(bookRepository2.findBooksByGenreAuthor(genre,author,false));
           }
           else if(genre != null && author == null){
               books.addAll(bookRepository2.findBooksByGenre(genre,false));
           }
           else if(genre == null && author != null){
               books.addAll(bookRepository2.findBooksByAuthor(author,false));
           }
           else{
               books.addAll(bookRepository2.findByAvailability(false));
           }
       }
       return  books;
   }
}