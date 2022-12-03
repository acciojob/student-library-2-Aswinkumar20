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

import java.util.List;
import java.util.Optional;

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

        Card card2 = new Card();








        Card card1 = cardRepository5.findById(cardId);

        if(card1.getBooks().size() > getMax_allowed_days){
            throw new Exception("Book limit has reached for this card");
        }

        // if all passing then we good to go for next step to add it to the transaction

        //If the transaction is successful, save the transaction to the list of transactions and return the id

        Transaction transaction1 = new Transaction();
        transaction1.setCard(card1);
        transaction1.setBook(book1);
        transaction1.setFineAmount(fine_per_day);
        transaction1.setIssueOperation(true);
        transaction1.setTransactionStatus(SUCCESSFUL);
        book1.setAvailable(false);

        // adding transaction to respective book
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

        Transaction returnBookTransaction  = null;
        return returnBookTransaction; //return the transaction after updating all details
    }
}
