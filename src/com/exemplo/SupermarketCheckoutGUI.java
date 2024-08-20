import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

class Product {
    String name;
    double price;

    Product(String name, double price) {
        this.name = name;
        this.price = price;
    }

    @Override
    public String toString() {
        return name + " - R$" + String.format("%.2f", price);
    }
}

public class SupermarketCheckoutGUI {
    private static final Map<Integer, Product> productsDatabase = new HashMap<>();
    private static double total = 0.0;
    private static final Map<String, Integer> purchasedProducts = new HashMap<>();

    static {
        // Adding some sample products to the database
        productsDatabase.put(1, new Product("Arroz", 5.50));
        productsDatabase.put(2, new Product("Feijão", 4.30));
        productsDatabase.put(3, new Product("Macarrão", 3.00));
        productsDatabase.put(4, new Product("Azeite", 15.00));
        productsDatabase.put(5, new Product("Leite", 2.50));
    }

    private JFrame frame;
    private JTextArea productsTextArea;
    private JTextField productNumberField;
    private JTextField productQuantityField;
    private JTextArea purchasedProductsTextArea;
    private JLabel totalLabel;

    public SupermarketCheckoutGUI() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Checkout GUI");
        frame.setBounds(100, 100, 700, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(2, 1, 10, 10));
        frame.getContentPane().add(topPanel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(2, 2, 10, 10));
        topPanel.add(inputPanel);

        // Labels and Fields
        JLabel lblProductNumber = new JLabel("Número do produto:");
        lblProductNumber.setFont(new Font("Arial", Font.BOLD, 16));
        lblProductNumber.setHorizontalAlignment(SwingConstants.CENTER);
        inputPanel.add(lblProductNumber);

        productNumberField = new JTextField();
        productNumberField.setFont(new Font("Arial", Font.PLAIN, 16));
        inputPanel.add(productNumberField);

        JLabel lblProductQuantity = new JLabel("Quantidade:");
        lblProductQuantity.setFont(new Font("Arial", Font.BOLD, 16));
        lblProductQuantity.setHorizontalAlignment(SwingConstants.CENTER);
        inputPanel.add(lblProductQuantity);

        productQuantityField = new JTextField();
        productQuantityField.setFont(new Font("Arial", Font.PLAIN, 16));
        inputPanel.add(productQuantityField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2, 10, 10));
        topPanel.add(buttonPanel);

        JButton addButton = new JButton("Adicionar");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addProductToTotal();
            }
        });
        buttonPanel.add(addButton);

        JButton finalizeButton = new JButton("Finalizar Compra");
        finalizeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                finalizePurchase();
            }
        });
        buttonPanel.add(finalizeButton);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(1, 2, 10, 10));
        frame.getContentPane().add(centerPanel, BorderLayout.CENTER);

        // Products List
        JPanel productsListPanel = new JPanel();
        productsListPanel.setLayout(new BorderLayout(10, 10));
        centerPanel.add(productsListPanel);

        productsTextArea = new JTextArea();
        productsTextArea.setEditable(false);
        productsTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane productsScrollPane = new JScrollPane(productsTextArea);
        productsListPanel.add(productsScrollPane, BorderLayout.CENTER);

        JLabel productsListLabel = new JLabel("Lista de Produtos");
        productsListLabel.setFont(new Font("Arial", Font.BOLD, 14));
        productsListPanel.add(productsListLabel, BorderLayout.NORTH);

        // Purchased Products List
        JPanel purchasedProductsPanel = new JPanel();
        purchasedProductsPanel.setLayout(new BorderLayout(10, 10));
        centerPanel.add(purchasedProductsPanel);

        purchasedProductsTextArea = new JTextArea();
        purchasedProductsTextArea.setEditable(false);
        purchasedProductsTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane purchasedProductsScrollPane = new JScrollPane(purchasedProductsTextArea);
        purchasedProductsPanel.add(purchasedProductsScrollPane, BorderLayout.CENTER);

        JLabel purchasedProductsLabel = new JLabel("Compras Adicionadas");
        purchasedProductsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        purchasedProductsPanel.add(purchasedProductsLabel, BorderLayout.NORTH);

        totalLabel = new JLabel("Total: R$0.00");
        totalLabel.setHorizontalAlignment(SwingConstants.CENTER);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        frame.getContentPane().add(totalLabel, BorderLayout.SOUTH);

        updateProductsTextArea();

        // Set up key bindings
        setupKeyBindings(addButton);
    }

    private void setupKeyBindings(JButton addButton) {
        // Map the "Enter" key to the add button
        InputMap inputMap = addButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = addButton.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("ENTER"), "addProduct");
        actionMap.put("addProduct", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addProductToTotal();
            }
        });
    }

    private void addProductToTotal() {
        try {
            int productNumber = Integer.parseInt(productNumberField.getText());
            int productQuantity = Integer.parseInt(productQuantityField.getText());
            Product product = productsDatabase.get(productNumber);

            if (product != null && productQuantity > 0) {
                double subtotal = product.price * productQuantity;
                total += subtotal;
                totalLabel.setText("Total: R$" + String.format("%.2f", total));

                purchasedProducts.put(product.name, purchasedProducts.getOrDefault(product.name, 0) + productQuantity);
                updatePurchasedProductsTextArea();

                JOptionPane.showMessageDialog(frame, productQuantity + " x " + product.name + " foram adicionados. Total parcial: R$" + String.format("%.2f", total));
            } else {
                JOptionPane.showMessageDialog(frame, "Produto não encontrado ou quantidade inválida.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Por favor, digite um número válido.");
        }
        productNumberField.setText("");
        productQuantityField.setText("");
        productNumberField.requestFocus();
    }

    private void finalizePurchase() {
        StringBuilder purchaseDetails = new StringBuilder();
        purchaseDetails.append("Data: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())).append("\n");
        for (Map.Entry<String, Integer> entry : purchasedProducts.entrySet()) {
            purchaseDetails.append(entry.getKey()).append(" - ").append(entry.getValue()).append(" unidades\n");
        }
        purchaseDetails.append("Total: R$").append(String.format("%.2f", total)).append("\n\n");

        int option = JOptionPane.showConfirmDialog(frame, "Deseja calcular o troco?", "Calcular Troco", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            double paymentAmount = 0.0;
            boolean validPayment = false;

            while (!validPayment) {
                String paymentInput = JOptionPane.showInputDialog(frame, "Digite o valor pago pelo cliente:");
                try {
                    paymentAmount = Double.parseDouble(paymentInput);
                    if (paymentAmount >= total) {
                        validPayment = true;
                    } else {
                        JOptionPane.showMessageDialog(frame, "O valor pago é menor que o total. Tente novamente.");
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(frame, "Por favor, digite um valor válido.");
                }
            }

            double change = paymentAmount - total;
            purchaseDetails.append("Pago: R$").append(String.format("%.2f", paymentAmount)).append("\n");
            purchaseDetails.append("Troco: R$").append(String.format("%.2f", change)).append("\n\n");

            JOptionPane.showMessageDialog(frame, "Compra finalizada. Total: R$" + String.format("%.2f", total) + "\nPago: R$" + String.format("%.2f", paymentAmount) + "\nTroco: R$" + String.format("%.2f", change));
        } else {
            JOptionPane.showMessageDialog(frame, "Compra finalizada. Total: R$" + String.format("%.2f", total));
        }

        try (FileWriter fileWriter = new FileWriter("compras_diarias.txt", true);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            printWriter.println(purchaseDetails.toString());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Erro ao salvar as compras: " + e.getMessage());
        }

        total = 0.0;
        totalLabel.setText("Total: R$0.00");
        purchasedProducts.clear();
        updatePurchasedProductsTextArea();
    }

    private void updateProductsTextArea() {
        StringBuilder productsText = new StringBuilder();
        for (Map.Entry<Integer, Product> entry : productsDatabase.entrySet()) {
            productsText.append(entry.getKey()).append(". ").append(entry.getValue()).append("\n");
        }
        productsTextArea.setText(productsText.toString());
    }

    private void updatePurchasedProductsTextArea() {
        StringBuilder purchasedText = new StringBuilder();
        for (Map.Entry<String, Integer> entry : purchasedProducts.entrySet()) {
            purchasedText.append(entry.getKey()).append(" - ").append(entry.getValue()).append(" unidades\n");
        }
        purchasedProductsTextArea.setText(purchasedText.toString());
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    SupermarketCheckoutGUI window = new SupermarketCheckoutGUI();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
