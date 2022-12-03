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

import java.text.SimpleDateFormat;
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



        if(card1.getBooks().size() >= getMax_allowed_days){
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

        String issuedDate = transaction.getTransactionDate().toString();
        String currDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());


        // now we want diifernece of current date and the issued date of book

        long daysBetween = calculatingDays(issuedDate , currDate);

        // now checking if the day is exceeding the limit:

        long extraDay = 0;    // extraday for putting fine
        if(daysBetween > getMax_allowed_days){
            extraDay = daysBetween - getMax_allowed_days;
        }

        int fineAmount = (int) (extraDay * fine_per_day);




        //ceatin bokobject

        Book book1 = transaction.getBook();
        book1.setAvailable(true);
        bookRepository5.save(book1);

        Transaction returnBookTransaction  = Transaction.builder().book(book1)
                .card(transaction.getCard())
                .transactionId(String.valueOf(UUID.randomUUID()))
                .isIssueOperation(true)
                .fineAmount(fineAmount)
                .build();

        transactionRepository5.save(returnBookTransaction);


        // now remove the book from card:

        Card card2 = cardRepository5.findById(cardId);
        List<Book> booksInCards = card2.getBooks();

        booksInCards.removeIf(curr -> curr.getId() == bookId);

        card2.setBooks(booksInCards);
        cardRepository5.save(card2);
        return returnBookTransaction;
        //return the transaction after updating all details
    }

    // calculating days
    private long calculatingDays(String issuedDate, String currDate) {
        int year1 = Integer.parseInt(issuedDate.substring(0,4));
        int month1 = Integer.parseInt(issuedDate.substring(5,7));
        int day1 = Integer.parseInt(issuedDate.substring(8,10));

        int year2 = Integer.parseInt(currDate.substring(0,4));
        int month2 = Integer.parseInt(currDate.substring(5,7));
        int day2 = Integer.parseInt(currDate.substring(8,10));

        //now we want to find each days inbetween the months
        int countDays1 = countAllDays(day1, month1, year1);
        int countDays2 = countAllDays(day2, month2, year2);

        return Math.abs(countDays1-countDays2);
    }


    // calculating all days
    // global variable for months
    private final int[] DaysInMonth = {0, 31,28,31,30,31,30,31,31,30,31,30,31};


    private int countAllDays(int day2, int month2, int year2) {
        int countDays = day2-1;
        int yearDiff = year2-1 - 2010;

        countDays += (365*yearDiff) + (yearDiff/4);

        for(int i=1;i<month2;i++){
            countDays += DaysInMonth[i];
        }
        if(month2>2 && isLeapYearOrNot(year2)) {
            ++countDays;
        }
        return countDays;
    }

    // checking leap year

    public boolean isLeapYearOrNot(int year){
        if(year%400==0) return true;
        if(year%100==0) return false;

        return year%4==0;
    }
}
