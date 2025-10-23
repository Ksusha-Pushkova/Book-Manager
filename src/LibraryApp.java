import javax.swing.*;

public class LibraryApp {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("-console")) {
            ConsoleInterface console = new ConsoleInterface();
            console.run();
        } else {
            SwingUtilities.invokeLater(() -> {
                GUIInterface gui = new GUIInterface();
                gui.setVisible(true);
            });
        }
    }
}