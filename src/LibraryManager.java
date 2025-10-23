import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LibraryManager {
    private List<Book> myBooks;
    private static final String DATA_FILE = "books_collection.store";
    
    public LibraryManager() {
        myBooks = new ArrayList<>();
    }
    
    public boolean addBookToCollection(Book newBook) {
        for (Book existing : myBooks) {
            if (existing.equals(newBook)) {
                return false;
            }
        }
        myBooks.add(newBook);
        return true;
    }
    
    public boolean updateExistingBook(Book oldVersion, Book newVersion) {
        int position = myBooks.indexOf(oldVersion);
        if (position >= 0) {
            myBooks.set(position, newVersion);
            return true;
        }
        return false;
    }
    
    public List<Book> getAllMyBooks() {
        return new ArrayList<>(myBooks);
    }
    
    public Book locateBookByTitle(String title) {
        if (title == null || title.trim().isEmpty()) return null;
        
        for (Book book : myBooks) {
            if (book.getBookName().equalsIgnoreCase(title.trim())) {
                return book;
            }
        }
        return null;
    }
    
    public List<Book> findBooks(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return new ArrayList<>(myBooks);
        }
        
        List<Book> found = new ArrayList<>();
        for (Book book : myBooks) {
            if (book.matchesSearch(searchTerm)) {
                found.add(book);
            }
        }
        return found;
    }
    
    public List<Book> searchWithFilters(String titlePart, String authorPart, String categoryPart, Integer exactYear) {
        List<Book> results = new ArrayList<>();
        
        for (Book book : myBooks) {
            boolean matches = true;
            
            if (titlePart != null && !titlePart.isEmpty()) {
                matches = book.getBookName().toLowerCase().contains(titlePart.toLowerCase());
            }
            
            if (matches && authorPart != null && !authorPart.isEmpty()) {
                matches = book.fetchWriter().toLowerCase().contains(authorPart.toLowerCase());
            }
            
            if (matches && categoryPart != null && !categoryPart.isEmpty()) {
                matches = book.getCategory().toLowerCase().contains(categoryPart.toLowerCase());
            }
            
            if (matches && exactYear != null) {
                matches = book.getPublishDate() == exactYear;
            }
            
            if (matches) {
                results.add(book);
            }
        }
        return results;
    }
    
    public boolean saveMyLibrary() {
        try {
            FileOutputStream fileOut = new FileOutputStream(DATA_FILE);
            ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
            objOut.writeObject(myBooks);
            objOut.close();
            fileOut.close();
            return true;
        } catch (IOException e) {
            System.err.println("Couldn't save: " + e.getMessage());
            return false;
        }
    }
    
    @SuppressWarnings("unchecked")
    public boolean loadMyLibrary() {
        File dataFile = new File(DATA_FILE);
        if (!dataFile.exists()) {
            return false;
        }
        
        try {
            FileInputStream fileIn = new FileInputStream(DATA_FILE);
            ObjectInputStream objIn = new ObjectInputStream(fileIn);
            myBooks = (List<Book>) objIn.readObject();
            objIn.close();
            fileIn.close();
            return true;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Couldn't load: " + e.getMessage());
            return false;
        }
    }
    
    public boolean removeBookFromCollection(Book toRemove) {
        return myBooks.remove(toRemove);
    }
    
    public int countBooks() {
        return myBooks.size();
    }
    
    public List<Book> getOldBooks() {
        return myBooks.stream()
                     .filter(Book::isOldBook)
                     .collect(Collectors.toList());
    }
}