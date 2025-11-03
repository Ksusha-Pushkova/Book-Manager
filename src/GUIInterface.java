import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class GUIInterface extends JFrame {
    private LibraryManager bookKeeper;
    private JTable booksDisplay;
    private DefaultTableModel tableData;
    
    private final Color BG_COLOR = new Color(255, 240, 245);
    private final Color PANEL_COLOR = new Color(255, 182, 193);
    private final Color ACTION_COLOR = new Color(255, 105, 180);
    private final Color INPUT_COLOR = new Color(255, 228, 225);
    private final Color DARK_PINK = new Color(219, 112, 147);
    private final Color LIGHT_PINK = new Color(255, 250, 250);
    
    private JTextField titleField, authorField, yearField, categoryField;
    private JCheckBox availableCheck;
    
    public GUIInterface() {
        bookKeeper = new LibraryManager();
        initializeWindow();
    }
    
    private void initializeWindow() {
        setTitle("My Library");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 600);
        centerOnScreen();
        
        getContentPane().setBackground(BG_COLOR);
        
        setupMenu();
        setupContent();
        refreshBookList();
    }
    
    private void centerOnScreen() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width - getWidth()) / 2, (screenSize.height - getHeight()) / 2);
    }
    
    private void setupMenu() {
        JMenuBar topMenu = new JMenuBar();
        topMenu.setBackground(PANEL_COLOR);
        
        JMenu fileMenu = createMenu("File", Color.WHITE);
        JMenuItem saveOption = new JMenuItem("Backup Data");
        JMenuItem loadOption = new JMenuItem("Restore Data");
        
        saveOption.addActionListener(e -> backupData());
        loadOption.addActionListener(e -> restoreData());
        
        fileMenu.add(saveOption);
        fileMenu.add(loadOption);
        
        JMenu booksMenu = createMenu("Books", Color.WHITE);
        JMenuItem addOption = new JMenuItem("New Book Entry");
        JMenuItem findOption = new JMenuItem("Find Books");
        
        addOption.addActionListener(e -> openAddWindow());
        findOption.addActionListener(e -> openSearchWindow());
        
        booksMenu.add(addOption);
        booksMenu.add(findOption);
        
        topMenu.add(fileMenu);
        topMenu.add(booksMenu);
        
        setJMenuBar(topMenu);
    }
    
    private JMenu createMenu(String text, Color textColor) {
        JMenu menu = new JMenu(text);
        menu.setForeground(textColor);
        return menu;
    }
    
    private void setupContent() {
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(BG_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        contentPanel.add(createButtonPanel(), BorderLayout.NORTH);
        contentPanel.add(createTablePanel(), BorderLayout.CENTER);
        contentPanel.add(createStatusPanel(), BorderLayout.SOUTH);
        
        add(contentPanel);
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttonPanel.setBackground(BG_COLOR);
        
        String[] buttonLabels = {"New Book", "Modify", "Remove", "Find", "Update"};
        Color[] buttonColors = {ACTION_COLOR, DARK_PINK, new Color(199, 21, 133), 
                               new Color(255, 182, 193), new Color(255, 20, 147)};
        
        for (int i = 0; i < buttonLabels.length; i++) {
            JButton button = createActionButton(buttonLabels[i], buttonColors[i]);
            buttonPanel.add(button);
        }
        
        return buttonPanel;
    }
    
    private JButton createActionButton(String label, Color baseColor) {
        JButton button = new JButton(label);
        button.setBackground(baseColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(baseColor.darker(), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor);
            }
        });
        
        if (label.contains("New")) {
            button.addActionListener(e -> openAddWindow());
        } else if (label.contains("Modify")) {
            button.addActionListener(e -> openEditWindow());
        } else if (label.contains("Remove")) {
            button.addActionListener(e -> deleteSelectedBook());
        } else if (label.contains("Find")) {
            button.addActionListener(e -> openSearchWindow());
        } else if (label.contains("Update")) {
            button.addActionListener(e -> refreshBookList());
        }
        
        return button;
    }
    
    private JScrollPane createTablePanel() {
        String[] columns = {"Book Title", "Author", "Year", "Category", "Status"};
        tableData = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 4 ? Boolean.class : String.class;
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        booksDisplay = new JTable(tableData);
        customizeTable();
        
        JScrollPane scrollPane = new JScrollPane(booksDisplay);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PANEL_COLOR, 2), 
            "Book Collection (" + bookKeeper.countBooks() + " books) "
        ));
        scrollPane.getViewport().setBackground(LIGHT_PINK);
        
        return scrollPane;
    }
    
    private void customizeTable() {
        booksDisplay.setRowHeight(28);
        booksDisplay.setBackground(LIGHT_PINK);
        booksDisplay.setGridColor(new Color(255, 182, 193));
        booksDisplay.setSelectionBackground(new Color(255, 228, 225));
        booksDisplay.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        booksDisplay.setShowGrid(true);
        booksDisplay.setIntercellSpacing(new Dimension(1, 1));
        
        JTableHeader tableHeader = booksDisplay.getTableHeader();
        tableHeader.setBackground(PANEL_COLOR);
        tableHeader.setForeground(Color.WHITE);
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
    }
    
    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(BG_COLOR);
        
        JLabel statusLabel = new JLabel("Ready to manage your books ----- Use buttons above to work with");
        statusLabel.setForeground(DARK_PINK);
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        
        statusPanel.add(statusLabel);
        return statusPanel;
    }
    
    private void refreshBookList() {
        tableData.setRowCount(0);
        List<Book> currentBooks = bookKeeper.getAllMyBooks();
        
        for (Book book : currentBooks) {
            tableData.addRow(new Object[]{
                book.getBookName(),
                book.fetchWriter(),
                String.valueOf(book.getPublishDate()),
                book.getCategory(),
                book.isInStock()
            });
        }
        
        updateTableTitle(currentBooks.size());
        repaint();
    }
    
    private void updateTableTitle(int bookCount) {
        Component centerComponent = getContentPane().getComponent(0);
        if (centerComponent instanceof JPanel) {
            JPanel mainPanel = (JPanel) centerComponent;
            Component tableComponent = mainPanel.getComponent(1);
            if (tableComponent instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) tableComponent;
                scrollPane.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(PANEL_COLOR, 2), 
                    " My Book Collection (" + bookCount + " books) "
                ));
            }
        }
    }
    
    private void openAddWindow() {
        showBookDialog("Add New Book to Collection", null);
    }
    
    private void openEditWindow() {
        int selectedRow = booksDisplay.getSelectedRow();
        if (selectedRow == -1) {
            showMessage("Please choose a book first", "Selection Needed");
            return;
        }
        
        String bookTitle = (String) tableData.getValueAt(selectedRow, 0);
        Book foundBook = bookKeeper.locateBookByTitle(bookTitle);
        
        if (foundBook != null) {
            showBookDialog("Edit Book Details", foundBook);
        }
    }
    
    private void showBookDialog(String windowTitle, Book existingBook) {
        JDialog dialogWindow = new JDialog(this, windowTitle, true);
        dialogWindow.setSize(420, 380);
        dialogWindow.setLocationRelativeTo(this);
        dialogWindow.getContentPane().setBackground(BG_COLOR);
        
        JPanel mainForm = new JPanel(new BorderLayout(10, 10));
        mainForm.setBackground(BG_COLOR);
        mainForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        mainForm.add(createFormFields(existingBook), BorderLayout.CENTER);
        mainForm.add(createDialogButtons(dialogWindow, existingBook), BorderLayout.SOUTH);
        
        dialogWindow.add(mainForm);
        dialogWindow.setVisible(true);
    }
    
    private JPanel createFormFields(Book existingBook) {
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 8, 12));
        formPanel.setBackground(BG_COLOR);
        
        titleField = createStyledTextField();
        authorField = createStyledTextField();
        yearField = createStyledTextField();
        categoryField = createStyledTextField();
        availableCheck = new JCheckBox("Available for borrowing", true);
        availableCheck.setBackground(BG_COLOR);
        availableCheck.setForeground(DARK_PINK);
        
        if (existingBook != null) {
            titleField.setText(existingBook.getBookName());
            authorField.setText(existingBook.fetchWriter());
            yearField.setText(String.valueOf(existingBook.getPublishDate()));
            categoryField.setText(existingBook.getCategory());
            availableCheck.setSelected(existingBook.isInStock());
        }
        
        formPanel.add(createFormLabel("Book Title:"));
        formPanel.add(titleField);
        formPanel.add(createFormLabel("Author:"));
        formPanel.add(authorField);
        formPanel.add(createFormLabel("Publication Year:"));
        formPanel.add(yearField);
        formPanel.add(createFormLabel("Category:"));
        formPanel.add(categoryField);
        formPanel.add(new JLabel(""));
        formPanel.add(availableCheck);
        
        return formPanel;
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setBackground(INPUT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PANEL_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        return field;
    }
    
    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(75, 0, 130));
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return label;
    }
    
    private JPanel createDialogButtons(JDialog dialog, Book existingBook) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(BG_COLOR);
        
        JButton confirmButton = createActionButton("Confirm", ACTION_COLOR);
        JButton cancelButton = createActionButton("Cancel", DARK_PINK);
        
        confirmButton.addActionListener(e -> handleBookSave(dialog, existingBook));
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);
        
        return buttonPanel;
    }
    
    private void handleBookSave(JDialog dialog, Book existingBook) {
        try {
            String titleText = titleField.getText().trim();
            String authorText = authorField.getText().trim();
            String yearText = yearField.getText().trim();
            String categoryText = categoryField.getText().trim();
            boolean availableStatus = availableCheck.isSelected();
            
            if (titleText.isEmpty() || authorText.isEmpty()) {
                showMessage("Book title and author are required! ✨", "Missing Info");
                return;
            }
            
            int publicationYear;
            try {
                publicationYear = Integer.parseInt(yearText);
                if (publicationYear < 1000 || publicationYear > 2025) {
                    showMessage("Please enter a valid year between 1000 and 2025", "Invalid Year");
                    return;
                }
            } catch (NumberFormatException ex) {
                showMessage("Publication year should be a number", "Year Error");
                return;
            }
            
            Book newBookEntry = new Book(titleText, authorText, publicationYear, categoryText);
            newBookEntry.setInStock(availableStatus);
            
            boolean operationSuccess;
            if (existingBook == null) {
                operationSuccess = bookKeeper.addBookToCollection(newBookEntry);
                if (!operationSuccess) {
                    showMessage("This book already exists", "Duplicate Book");
                    return;
                }
            } else {
                operationSuccess = bookKeeper.updateExistingBook(existingBook, newBookEntry);
                if (!operationSuccess) {
                    showMessage("Update failed! Possibly a duplicate book.", "Update Error");
                    return;
                }
            }
            
            if (operationSuccess) {
                refreshBookList();
                dialog.dispose();
            }
        } catch (Exception ex) {
            showMessage("Something went wrong: " + ex.getMessage(), "Error");
        }
    }
    
    private void deleteSelectedBook() {
        int selectedIndex = booksDisplay.getSelectedRow();
        if (selectedIndex == -1) {
            showMessage("Please pick a book to remove", "No Selection");
            return;
        }
        
        String bookTitle = (String) tableData.getValueAt(selectedIndex, 0);
        Book bookToRemove = bookKeeper.locateBookByTitle(bookTitle);
        
        if (bookToRemove != null) {
            int userChoice = JOptionPane.showConfirmDialog(this, 
                "Remove '" + bookTitle + "' from your collection?", 
                "Confirm Deletion", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
                
            if (userChoice == JOptionPane.YES_OPTION) {
                if (bookKeeper.removeBookFromCollection(bookToRemove)) {
                    refreshBookList();
                    showMessage("Book was successfully removed", "Book Removed");
                }
            }
        }
    }
    
    private void openSearchWindow() {
        JDialog searchDialog = new JDialog(this, "Find Books in Collection", true);
        searchDialog.setSize(450, 300);
        searchDialog.setLocationRelativeTo(this);
        searchDialog.getContentPane().setBackground(BG_COLOR);
        
        JPanel mainSearchPanel = new JPanel(new BorderLayout(10, 10));
        mainSearchPanel.setBackground(BG_COLOR);
        mainSearchPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        mainSearchPanel.add(createSearchFields(), BorderLayout.CENTER);
        mainSearchPanel.add(createSearchButtons(searchDialog), BorderLayout.SOUTH);
        
        searchDialog.add(mainSearchPanel);
        searchDialog.setVisible(true);
    }
    
    private JPanel createSearchFields() {
        JPanel searchPanel = new JPanel(new GridLayout(4, 2, 10, 12));
        searchPanel.setBackground(BG_COLOR);
        
        JTextField titleSearch = createStyledTextField();
        JTextField authorSearch = createStyledTextField();
        JTextField categorySearch = createStyledTextField();
        JTextField yearSearch = createStyledTextField();
        
        searchPanel.add(createFormLabel("Title contains:"));
        searchPanel.add(titleSearch);
        searchPanel.add(createFormLabel("Author contains:"));
        searchPanel.add(authorSearch);
        searchPanel.add(createFormLabel("Category contains:"));
        searchPanel.add(categorySearch);
        searchPanel.add(createFormLabel("Exact year:"));
        searchPanel.add(yearSearch);
        
        return searchPanel;
    }
    
    private JPanel createSearchButtons(JDialog dialog) {
        JPanel searchButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        searchButtonPanel.setBackground(BG_COLOR);
        
        JButton searchAction = createActionButton("Start Search", ACTION_COLOR);
        JButton clearAction = createActionButton("Clear Fields", DARK_PINK);
        JButton cancelAction = createActionButton("Close", new Color(199, 21, 133));
        
        searchAction.addActionListener(e -> performSearch(dialog));
        clearAction.addActionListener(e -> clearSearchFields(dialog));
        cancelAction.addActionListener(e -> dialog.dispose());
        
        searchButtonPanel.add(searchAction);
        searchButtonPanel.add(clearAction);
        searchButtonPanel.add(cancelAction);
        
        return searchButtonPanel;
    }
    
    private void performSearch(JDialog dialog) {
        try {
            Component searchComponent = ((JPanel)dialog.getContentPane().getComponent(0)).getComponent(0);
            if (!(searchComponent instanceof JPanel)) return;
            
            JPanel searchPanel = (JPanel) searchComponent;
            String titleTerm = ((JTextField)searchPanel.getComponent(1)).getText().trim();
            String authorTerm = ((JTextField)searchPanel.getComponent(3)).getText().trim();
            String categoryTerm = ((JTextField)searchPanel.getComponent(5)).getText().trim();
            String yearText = ((JTextField)searchPanel.getComponent(7)).getText().trim();
            
            Integer exactYear = null;
            if (!yearText.isEmpty()) {
                try {
                    exactYear = Integer.parseInt(yearText);
                } catch (NumberFormatException ex) {
                    showMessage("Please enter a valid year number", "Year Format Error");
                    return;
                }
            }
            
            List<Book> searchResults = bookKeeper.searchWithFilters(
                titleTerm.isEmpty() ? null : titleTerm,
                authorTerm.isEmpty() ? null : authorTerm,
                categoryTerm.isEmpty() ? null : categoryTerm,
                exactYear
            );
            
            displaySearchResults(searchResults);
            dialog.dispose();
        } catch (Exception ex) {
            showMessage("Search error: " + ex.getMessage(), "Search Problem");
        }
    }
    
    private void clearSearchFields(JDialog dialog) {
        Component searchComponent = ((JPanel)dialog.getContentPane().getComponent(0)).getComponent(0);
        if (searchComponent instanceof JPanel) {
            JPanel searchPanel = (JPanel) searchComponent;
            for (int i = 1; i < 8; i += 2) {
                ((JTextField)searchPanel.getComponent(i)).setText("");
            }
        }
    }
    
    private void displaySearchResults(List<Book> foundBooks) {
        if (foundBooks.isEmpty()) {
            showMessage("No books match your search criteria", "No Results");
            return;
        }
        
        JDialog resultsWindow = new JDialog(this, "Search Results - " + foundBooks.size() + " books found", true);
        resultsWindow.setSize(750, 450);
        resultsWindow.setLocationRelativeTo(this);
        resultsWindow.getContentPane().setBackground(BG_COLOR);
        
        String[] resultColumns = {"Book Title", "Author", "Year", "Category", "Available"};
        DefaultTableModel resultsModel = new DefaultTableModel(resultColumns, 0);
        
        for (Book book : foundBooks) {
            resultsModel.addRow(new Object[]{
                book.getBookName(),
                book.fetchWriter(),
                String.valueOf(book.getPublishDate()),
                book.getCategory(),
                book.isInStock()
            });
        }
        
        JTable resultsTable = new JTable(resultsModel);
        resultsTable.setBackground(LIGHT_PINK);
        resultsTable.setGridColor(new Color(255, 182, 193));
        resultsTable.setRowHeight(25);
        resultsTable.setEnabled(false);
        
        JTableHeader resultsHeader = resultsTable.getTableHeader();
        resultsHeader.setBackground(PANEL_COLOR);
        resultsHeader.setForeground(Color.WHITE);
        
        JScrollPane resultsScroll = new JScrollPane(resultsTable);
        resultsScroll.getViewport().setBackground(LIGHT_PINK);
        
        resultsWindow.add(resultsScroll);
        resultsWindow.setVisible(true);
    }
    
    private void backupData() {
        if (bookKeeper.saveMyLibrary()) {
            showMessage("Your library data has been backed up successfully", "Backup Complete");
        } else {
            showMessage("Failed to create backup", "Backup Error");
        }
    }
    
    private void restoreData() {
        if (bookKeeper.loadMyLibrary()) {
            refreshBookList();
            showMessage("Your library data has been restored", "Restore Complete");
        } else {
            showMessage("Restore failed or no backup file exists", "Restore Error");
        }
    }
    
    private void showMessage(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
}