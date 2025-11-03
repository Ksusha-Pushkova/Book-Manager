import java.io.Serializable;
import java.util.Objects;

public class Book implements Serializable {
    private static final long serialVersionUID = 42L;
    
    private String bookName;
    private String writer;
    private int publishDate;
    private String category;
    private boolean inStock;
    
    public Book(String bookName, String writer, int publishDate, String category) {
        this.bookName = bookName;
        this.writer = writer;
        this.publishDate = publishDate;
        this.category = category;
        this.inStock = true;
    }
    
    public String getBookName() { return bookName; }
    public String getWriter() { return writer; }
    public String fetchWriter() { return writer; }
    public int getPublishDate() { return publishDate; }
    public String getCategory() { return category; }
    public boolean isInStock() { return inStock; }
    
    public void renameBook(String newName) { this.bookName = newName; } 
    public void setWriter(String writer) { this.writer = writer; }
    public void setPublishDate(int publishDate) { this.publishDate = publishDate; }
    public void changeCategory(String category) { this.category = category; } 
    public void setInStock(boolean inStock) { this.inStock = inStock; }
    
    public boolean isOldBook() {
        return publishDate < 1950;
    }
    
    public String getShortInfo() {
        return bookName + " (" + publishDate + ")";
    }
    
    public boolean matchesSearch(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) return true;
        String searchLower = searchText.toLowerCase();
        
        return (bookName != null && bookName.toLowerCase().contains(searchLower)) ||
               (writer != null && writer.toLowerCase().contains(searchLower)) ||
               (category != null && category.toLowerCase().contains(searchLower)) ||
               String.valueOf(publishDate).contains(searchText);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof Book)) return false;
        Book other = (Book) obj;
        
        boolean nameEqual = (bookName == null && other.bookName == null) ||
                           (bookName != null && other.bookName != null && 
                            bookName.equalsIgnoreCase(other.bookName));
        boolean authorEqual = (writer == null && other.writer == null) ||
                             (writer != null && other.writer != null && 
                              writer.equalsIgnoreCase(other.writer));
        
        return nameEqual && authorEqual && publishDate == other.publishDate;
    }
    
    @Override
    public int hashCode() {
        String nameLower = bookName != null ? bookName.toLowerCase() : "";
        String authorLower = writer != null ? writer.toLowerCase() : "";
        return Objects.hash(nameLower, authorLower, publishDate);
    }
    
    @Override
    public String toString() {
        String status = inStock ? "✓ Available" : "✗ Checked out";
        return String.format("\"%s\" by %s | %d | %s | %s", 
                           bookName, writer, publishDate, category, status);
    }
}