import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.security.*;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.swing.*;

public class EnhancedHybridEncryptionApp extends JFrame {
    private JTextField inputTextField;
    private JTextArea outputTextArea;
    private JButton encryptButton, decryptButton, copyButton;

    private SecretKey aesKey;
    private PublicKey rsaPublicKey;
    private PrivateKey rsaPrivateKey;

    public EnhancedHybridEncryptionApp() {
        setTitle("Enhanced Hybrid Encryption and Decryption App");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main Panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Top, Left, Bottom, Right
        mainPanel.setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel();
        JLabel headerLabel = new JLabel("Hybrid Encryption and Decryption Tool");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(headerLabel);

        // Input Panel
        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        JLabel inputLabel = new JLabel("Input Text:");
        inputTextField = new JTextField(20); // Reduced size
        inputPanel.add(inputLabel, BorderLayout.WEST);
        inputPanel.add(inputTextField, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10)); // Adjusted margins
        encryptButton = new JButton("Encrypt");
        decryptButton = new JButton("Decrypt");
        styleButton(encryptButton, new Color(60, 179, 113), Color.WHITE); // Green button
        styleButton(decryptButton, new Color(255, 0, 0), Color.WHITE); // Red button
        buttonsPanel.add(encryptButton);
        buttonsPanel.add(decryptButton);

        // Output Panel
        JPanel outputPanel = new JPanel(new BorderLayout(10, 10));
        JLabel outputLabel = new JLabel("Output:");
        outputTextArea = new JTextArea(10, 30);
        outputTextArea.setLineWrap(true);
        outputTextArea.setWrapStyleWord(true);
        outputTextArea.setEditable(false);
        copyButton = new JButton("Copy");
        styleButton(copyButton, new Color(255, 200, 100), Color.WHITE); // Copy button

        JPanel copyPanel = new JPanel();
        copyPanel.add(copyButton);

        outputPanel.add(outputLabel, BorderLayout.NORTH);
        outputPanel.add(new JScrollPane(outputTextArea), BorderLayout.CENTER);
        outputPanel.add(copyPanel, BorderLayout.SOUTH);

        // Add Panels to Main Panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.EAST);
        mainPanel.add(outputPanel, BorderLayout.SOUTH);

        // Add Main Panel to Frame
        add(mainPanel);

        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            aesKey = keyGen.generateKey();

            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(2048);
            KeyPair keyPair = keyPairGen.generateKeyPair();
            rsaPublicKey = keyPair.getPublic();
            rsaPrivateKey = keyPair.getPrivate();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // Button Actions
        encryptButton.addActionListener(e -> handleEncryption());
        decryptButton.addActionListener(e -> handleDecryption());
        copyButton.addActionListener(e -> copyToClipboard()); 
    }

    private void styleButton(JButton button, Color background, Color foreground) {
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding inside buttons
    }

    private void handleEncryption() {
        try {
            String plainText = inputTextField.getText();
            if (plainText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter text to encrypt.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Encrypt with RSA first
            String rsaEncrypted = rsaEncrypt(plainText);
            // Encrypt with AES next
            String hybridEncrypted = aesEncrypt(rsaEncrypted);
            outputTextArea.setText(hybridEncrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error during encryption: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDecryption() {
        try {
            String encryptedText = outputTextArea.getText();
            if (encryptedText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No encrypted text found to decrypt.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Decrypt with AES first
            String aesDecrypted = aesDecrypt(encryptedText);
            // Decrypt with RSA next
            String hybridDecrypted = rsaDecrypt(aesDecrypted);
            outputTextArea.setText(hybridDecrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error during decryption: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void copyToClipboard() {
        String text = outputTextArea.getText();
        if (!text.isEmpty()) {
            StringSelection stringSelection = new StringSelection(text);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
            JOptionPane.showMessageDialog(this, "Text copied to clipboard.", "Info", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No text to copy.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private String aesEncrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    private String aesDecrypt(String encryptedText) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, aesKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        return new String(decryptedBytes);
    }

    private String rsaEncrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    private String rsaDecrypt(String encryptedText) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        return new String(decryptedBytes);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EnhancedHybridEncryptionApp().setVisible(true));
    }
}
