package com.driver.services;

import com.driver.models.Book;
import com.driver.models.Card;
import com.driver.models.Transaction;
import com.driver.models.TransactionStatus;
import com.driver.repositories.BookRepository;
import com.driver.repositories.CardRepository;
import com.driver.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.driver.models.TransactionStatus.SUCCESSFUL;

@Service
public class TransactionService {

    @Autowired
    BookRepository bookRepository5;

    @Autowired
    CardRepository cardRepository5;

    @Autowired
    TransactionRepository transactionRepository5;

    @Value("${books.max_allowed}")
    public int max_allowed_books;

    @Value("${books.max_allowed_days}")
    public int getMax_allowed_days;

    @Value("${books.fine.per_day}")
    public int fine_per_day;

    public String issueBook(int cardId, int bookId) throws Exception {
        //check whether bookId and cardId already exist
        //conditions required for successful transaction of issue book:
        //1. book is present and available
        // If it fails: throw new Exception("Book is either unavailable or not present");
        //2. card is present and activated
        // If it fails: throw new Exception("Card is invalid");
        //3. number of books issued against the card is strictly less than max_allowed_books
        // If it fails: throw new Exception("Book limit has reached for this card");
        //If the transaction is successful, save the transaction to the list of transactions and return the id

        Book book1 = bookRepository5.findById(bookId);

        if(!bookRepository5.existsById(bookId)){
            throw new Exception("Book is either unavailable or not present");
        }

        if(!cardRepository5.existsById(cardId)){
            throw new Exception("Card is invalid");
        }


        Card card1 = cardRepository5.findById(cardId);



        if(card1.getBooks().size() > getMax_allowed_days){
            throw new Exception("Book limit has reached for this card");
        }


        // if all passing then we good to go for next step to add it to the transaction

        //If the transaction is successful, save the transaction to the list of transactions and return the id

        book1.setCard(card1);
        card1.getBooks().add(book1);
        Transaction transaction1 = Transaction.builder()
                .card(card1)
                .book(book1)
                .fineAmount(0)
                .transactionId(String.valueOf(UUID.randomUUID()))
                .isIssueOperation(true)
                .transactionStatus(SUCCESSFUL)
                .build();

        book1.setAvailable(false);

        List<Book> books1 = card1.getBooks();

        // if the list of book is null then create

        if(books1 == null) books1 = new ArrayList<>();
        // adding transaction to respective book

        transactionRepository5.save(transaction1);
        books1.add(book1);
        card1.setBooks(books1);
        book1.getTransactions().add(transaction1);


        //Note that the error message should match exactly in all cases

       return transaction1.getTransactionId(); //return transactionId instead
    }

    public Transaction returnBook(int cardId, int bookId) throws Exception{

        List<Transaction> transactions = transactionRepository5.find(cardId, bookId, SUCCESSFUL, true);
        Transaction transaction = transactions.get(transactions.size() - 1);

        //for the given transaction calculate the fine amount considering the book has been returned exactly when this function is called
        //make the book available for other users
        //make a new transaction for return book which contains the fine amount as well

        Date currdate = transaction.getTransactionDate();
        Date curr = new Date();
        long differenceInTime
                = curr.getTime() - currdate.getTime();

        long differenceInDays
                = (differenceInTime
                / (1000 * 60 * 60 * 24))
                % 365;
        int fineDue = 0;
        if(differenceInDays > getMax_allowed_days){
            int finedays = (int) (getMax_allowed_days - differenceInDays);
            fineDue = finedays * fine_per_day;
        }


        //ceatin bokobject

        Book book1 = transaction.getBook();
        book1.setAvailable(true);

        Transaction returnBookTransaction  = Transaction.builder().book(book1)
                .card(transaction.getCard())
                .transactionId(String.valueOf(UUID.randomUUID()))
                .isIssueOperation(true)
                .fineAmount(fineDue)
                .build();

        transactionRepository5.save(returnBookTransaction);
        return returnBookTransaction;
        //return the transaction after updating all details
    }
}
