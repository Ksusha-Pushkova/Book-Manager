import java.util.List;
import java.util.Scanner;

public class ConsoleInterface {
    private LibraryManager manager;
    private Scanner inputReader;
    
    public ConsoleInterface() {
        manager = new LibraryManager();
        inputReader = new Scanner(System.in);
    }
    
    public void run() {
        System.out.println("=== Book Collection Manager ===");
        
        while (true) {
            showOptions();
            int selection = getNumberInput("Choose action: ");
            
            switch (selection) {
                case 1 -> createBook();
                case 2 -> modifyBook();
                case 3 -> displayAll();
                case 4 -> findBook();
                case 5 -> storeCollection();
                case 6 -> loadCollection();
                case 7 -> advancedSearch();
                case 0 -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice!");
            }
        }
    }
    
    private void showOptions() {
        System.out.println("\n--- Options ---");
        System.out.println("1. Add new book");
        System.out.println("2. Modify book");
        System.out.println("3. Show all books");
        System.out.println("4. Find book by title");
        System.out.println("5. Save collection");
        System.out.println("6. Load collection");
        System.out.println("7. Advanced search");
        System.out.println("0. Exit");
    }
    
    private void createBook() {
        System.out.println("\n--- Add New Book ---");
        
        String title = getTextInput("Book title: ");
        String author = getTextInput("Author: ");
        int year = getNumberInput("Publication year: ");
        String genre = getTextInput("Category: ");
        
        Book newBook = new Book(title, author, year, genre);
        
        if (manager.addNewBook(newBook)) {
            System.out.println("Book added successfully!");
        } else {
            System.out.println("This book already exists!");
        }
    }
    
    private void modifyBook() {
        System.out.println("\n--- Modify Book ---");
        String title = getTextInput("Enter book title to modify: ");
        
        Book existing = manager.findBookByName(title);
        if (existing == null) {
            System.out.println("Book not found!");
            return;
        }
        
        System.out.println("Current: " + existing);
        
        String newTitle = getTextInput("New title (empty to keep): ");
        String newAuthor = getTextInput("New author (empty to keep): ");
        String yearInput = getTextInput("New year (empty to keep): ");
        String newGenre = getTextInput("New category (empty to keep): ");
        
        String finalTitle = newTitle.isEmpty() ? existing.getBookName() : newTitle;
        String finalAuthor = newAuthor.isEmpty() ? existing.getWriter() : newAuthor;
        int finalYear = yearInput.isEmpty() ? existing.getPublishDate() : Integer.parseInt(yearInput);
        String finalGenre = newGenre.isEmpty() ? existing.getCategory() : newGenre;
        
        Book updated = new Book(finalTitle, finalAuthor, finalYear, finalGenre);
        updated.setInStock(existing.isInStock());
        
        if (manager.updateBook(existing, updated)) {
            System.out.println("Book updated!");
        } else {
            System.out.println("Update failed!");
        }
    }
    
    private void displayAll() {
        System.out.println("\n--- Book Collection ---");
        List<Book> books = manager.getFullCollection();
        
        if (books.isEmpty()) {
            System.out.println("Collection is empty.");
        } else {
            for (int i = 0; i < books.size(); i++) {
                System.out.println((i + 1) + ". " + books.get(i));
            }
        }
    }
    
    private void findBook() {
        System.out.println("\n--- Find Book ---");
        String title = getTextInput("Enter book title: ");
        
        Book found = manager.findBookByName(title);
        if (found != null) {
            System.out.println("Found: " + found);
        } else {
            System.out.println("Book not found.");
        }
    }
    
    private void advancedSearch() {
        System.out.println("\n--- Advanced Search ---");
        
        String title = getTextInput("Title (skip if any): ");
        String author = getTextInput("Author (skip if any): ");
        String genre = getTextInput("Category (skip if any): ");
        String yearInput = getTextInput("Year (skip if any): ");
        
        Integer year = null;
        if (!yearInput.isEmpty()) {
            try {
                year = Integer.parseInt(yearInput);
            } catch (NumberFormatException e) {
                System.out.println("Invalid year format!");
                return;
            }
        }
        
        List<Book> results = manager.filterBooks(
            title.isEmpty() ? null : title,
            author.isEmpty() ? null : author,
            genre.isEmpty() ? null : genre,
            year
        );
        
        if (results.isEmpty()) {
            System.out.println("No books found.");
        } else {
            System.out.println("Found " + results.size() + " books:");
            for (int i = 0; i < results.size(); i++) {
                System.out.println((i + 1) + ". " + results.get(i));
            }
        }
    }
    
    private void storeCollection() {
        if (manager.storeData()) {
            System.out.println("Collection saved!");
        } else {
            System.out.println("Save failed!");
        }
    }
    
    private void loadCollection() {
        if (manager.retrieveData()) {
            System.out.println("Collection loaded!");
        } else {
            System.out.println("Load failed!");
        }
    }
    
    private String getTextInput(String prompt) {
        System.out.print(prompt);
        return inputReader.nextLine().trim();
    }
    
    private int getNumberInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(inputReader.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number!");
            }
        }
    }
}