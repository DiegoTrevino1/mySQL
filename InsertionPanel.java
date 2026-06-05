package StockPortfolio;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * Tab 1 – Data Insertion.
 * Sub-tabs: Investor | Company | Stock.
 * Fields marked (*) are required; others may be left blank.
 */
public class InsertionPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    // ── Investor fields ──────────────────────────────────────────
    private JTextField txtInvestorFirstName;
    private JTextField txtInvestorLastName;
    private JTextField txtInvestorEmail;
    private JTextField txtInvestorCountry;
    private JTextField txtInvestorPhone;
    private JLabel     lblInvestorStatus;

    // ── Company fields ───────────────────────────────────────────
    private JTextField txtCompanyName;
    private JTextField txtCompanyIndustry;
    private JTextField txtCompanyCountry;
    private JTextField txtCompanyFoundedYear;
    private JLabel     lblCompanyStatus;

    // ── Stock fields ─────────────────────────────────────────────
    private JComboBox<String> cmbStockCompany;
    private JTextField        txtStockTicker;
    private JTextField        txtStockExchange;
    private JTextField        txtStockPrice;
    private JLabel            lblStockStatus;

    public InsertionPanel() {
        setLayout(new BorderLayout(0, 0));

        JLabel hint = new JLabel("  Fields marked with * are required. Others may be left blank.");
        hint.setFont(new Font("SansSerif", Font.ITALIC, 11));
        hint.setForeground(Color.GRAY);
        hint.setBorder(BorderFactory.createEmptyBorder(6, 8, 4, 8));
        add(hint, BorderLayout.NORTH);

        JTabbedPane subTabs = new JTabbedPane(JTabbedPane.LEFT);
        subTabs.addTab("Investor", buildInvestorPanel());
        subTabs.addTab("Company",  buildCompanyPanel());
        subTabs.addTab("Stock",    buildStockPanel());

        // Reload company combo when the user switches to Stock sub-tab
        subTabs.addChangeListener(e -> {
            if (subTabs.getSelectedIndex() == 2) loadCompaniesIntoCombo(cmbStockCompany);
        });

        add(subTabs, BorderLayout.CENTER);
    }

    //  Investor panel
    private JPanel buildInvestorPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new TitledBorder("Insert New Investor"));
        GridBagConstraints gbc = defaultGBC();

        txtInvestorFirstName = new JTextField(24);
        txtInvestorLastName  = new JTextField(24);
        txtInvestorEmail     = new JTextField(24);
        txtInvestorCountry   = new JTextField(24);
        txtInvestorPhone     = new JTextField(24);

        addRow(p, gbc, 0, "First Name *", txtInvestorFirstName);
        addRow(p, gbc, 1, "Last Name *",  txtInvestorLastName);
        addRow(p, gbc, 2, "Email *",      txtInvestorEmail);
        addRow(p, gbc, 3, "Country *",    txtInvestorCountry);
        addRow(p, gbc, 4, "Phone",        txtInvestorPhone);

        JButton btnInsert = new JButton("Insert Investor");
        btnInsert.setBackground(new Color(60, 130, 200));
        btnInsert.setForeground(Color.black);
        btnInsert.setFocusPainted(false);
        JButton btnClear = new JButton("Clear");

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.add(btnInsert);
        btnRow.add(btnClear);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.insets = new Insets(14, 8, 4, 8);
        p.add(btnRow, gbc);

        lblInvestorStatus = new JLabel(" ");
        lblInvestorStatus.setFont(new Font("SansSerif", Font.ITALIC, 11));
        gbc.gridy = 6; gbc.insets = new Insets(2, 10, 8, 8);
        p.add(lblInvestorStatus, gbc);

        btnInsert.addActionListener(e -> insertInvestor());
        btnClear.addActionListener(e -> {
            clearFields(txtInvestorFirstName, txtInvestorLastName, txtInvestorEmail,
                        txtInvestorCountry, txtInvestorPhone);
            lblInvestorStatus.setText(" ");
        });
        return p;
    }

    private void insertInvestor() {
        String firstName = txtInvestorFirstName.getText().trim();
        String lastName  = txtInvestorLastName.getText().trim();
        String email     = txtInvestorEmail.getText().trim();
        String country   = txtInvestorCountry.getText().trim();
        String phone     = txtInvestorPhone.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || country.isEmpty()) {
            showStatus(lblInvestorStatus, "First Name, Last Name, Email, and Country are required.", Color.RED);
            return;
        }

        String sql = "INSERT INTO INVESTOR (FirstName, LastName, Email, Country, Phone) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, email);
            ps.setString(4, country);
            ps.setString(5, phone.isEmpty() ? null : phone);
            ps.executeUpdate();
            showStatus(lblInvestorStatus, "Investor " + firstName + " " + lastName + " inserted successfully.", new Color(0, 140, 0));
            clearFields(txtInvestorFirstName, txtInvestorLastName, txtInvestorEmail,
                        txtInvestorCountry, txtInvestorPhone);
        } catch (SQLException ex) {
            showStatus(lblInvestorStatus, "DB Error: " + ex.getMessage(), Color.RED);
        }
    }

    //  Company panel
    private JPanel buildCompanyPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new TitledBorder("Insert New Company"));
        GridBagConstraints gbc = defaultGBC();

        txtCompanyName        = new JTextField(24);
        txtCompanyIndustry    = new JTextField(24);
        txtCompanyCountry     = new JTextField(24);
        txtCompanyFoundedYear = new JTextField(8);

        addRow(p, gbc, 0, "Company Name *", txtCompanyName);
        addRow(p, gbc, 1, "Industry *",     txtCompanyIndustry);
        addRow(p, gbc, 2, "Country *",      txtCompanyCountry);
        addRow(p, gbc, 3, "Founded Year",   txtCompanyFoundedYear);

        JButton btnInsert = new JButton("Insert Company");
        btnInsert.setBackground(new Color(60, 130, 200));
        btnInsert.setForeground(Color.black);
        btnInsert.setFocusPainted(false);
        JButton btnClear = new JButton("Clear");

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.add(btnInsert);
        btnRow.add(btnClear);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(14, 8, 4, 8);
        p.add(btnRow, gbc);

        lblCompanyStatus = new JLabel(" ");
        lblCompanyStatus.setFont(new Font("SansSerif", Font.ITALIC, 11));
        gbc.gridy = 5; gbc.insets = new Insets(2, 10, 8, 8);
        p.add(lblCompanyStatus, gbc);

        btnInsert.addActionListener(e -> insertCompany());
        btnClear.addActionListener(e -> {
            clearFields(txtCompanyName, txtCompanyIndustry, txtCompanyCountry, txtCompanyFoundedYear);
            lblCompanyStatus.setText(" ");
        });
        return p;
    }

    private void insertCompany() {
        String name       = txtCompanyName.getText().trim();
        String industry   = txtCompanyIndustry.getText().trim();
        String country    = txtCompanyCountry.getText().trim();
        String yearStr    = txtCompanyFoundedYear.getText().trim();

        if (name.isEmpty() || industry.isEmpty() || country.isEmpty()) {
            showStatus(lblCompanyStatus, "Company Name, Industry, and Country are required.", Color.RED);
            return;
        }

        String sql = "INSERT INTO COMPANY (CompanyName, Industry, Country, FoundedYear) VALUES (?,?,?,?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, industry);
            ps.setString(3, country);
            if (yearStr.isEmpty()) ps.setNull(4, Types.INTEGER);
            else ps.setInt(4, Integer.parseInt(yearStr));
            ps.executeUpdate();
            showStatus(lblCompanyStatus, "Company " + name + " inserted successfully.", new Color(0, 140, 0));
            clearFields(txtCompanyName, txtCompanyIndustry, txtCompanyCountry, txtCompanyFoundedYear);
        } catch (NumberFormatException ex) {
            showStatus(lblCompanyStatus, "Founded Year must be an integer.", Color.RED);
        } catch (SQLException ex) {
            showStatus(lblCompanyStatus, "DB Error: " + ex.getMessage(), Color.RED);
        }
    }

    //  Stock panel
    private JPanel buildStockPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new TitledBorder("Insert New Stock"));
        GridBagConstraints gbc = defaultGBC();

        cmbStockCompany = new JComboBox<>();
        txtStockTicker  = new JTextField(24);
        txtStockExchange= new JTextField(24);
        txtStockPrice   = new JTextField(12);

        loadCompaniesIntoCombo(cmbStockCompany);

        addRow(p, gbc, 0, "Company *",       cmbStockCompany);
        addRow(p, gbc, 1, "Ticker Symbol *", txtStockTicker);
        addRow(p, gbc, 2, "Exchange *",      txtStockExchange);
        addRow(p, gbc, 3, "Current Price *", txtStockPrice);

        JButton btnInsert = new JButton("Insert Stock");
        btnInsert.setBackground(new Color(60, 130, 200));
        btnInsert.setForeground(Color.black);
        btnInsert.setFocusPainted(false);
        JButton btnClear = new JButton("Clear");

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.add(btnInsert);
        btnRow.add(btnClear);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(14, 8, 4, 8);
        p.add(btnRow, gbc);

        lblStockStatus = new JLabel(" ");
        lblStockStatus.setFont(new Font("SansSerif", Font.ITALIC, 11));
        gbc.gridy = 5; gbc.insets = new Insets(2, 10, 8, 8);
        p.add(lblStockStatus, gbc);

        btnInsert.addActionListener(e -> insertStock());
        btnClear.addActionListener(e -> {
            clearFields(txtStockTicker, txtStockExchange, txtStockPrice);
            lblStockStatus.setText(" ");
        });
        return p;
    }

    private void insertStock() {
        String selected = (String) cmbStockCompany.getSelectedItem();
        String ticker   = txtStockTicker.getText().trim();
        String exchange = txtStockExchange.getText().trim();
        String priceStr = txtStockPrice.getText().trim();

        if (selected == null || ticker.isEmpty() || exchange.isEmpty() || priceStr.isEmpty()) {
            showStatus(lblStockStatus, "All fields are required for Stock.", Color.RED);
            return;
        }
        int companyId;
        double price;
        try {
            companyId = extractId(selected);
            price     = Double.parseDouble(priceStr);
            if (price <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            showStatus(lblStockStatus, "Price must be a positive number.", Color.RED);
            return;
        }

        String sql = "INSERT INTO STOCK (TickerSymbol, ExchangeName, CurrentPrice, CompanyID) VALUES (?,?,?,?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, ticker);
            ps.setString(2, exchange);
            ps.setDouble(3, price);
            ps.setInt(4, companyId);
            ps.executeUpdate();
            showStatus(lblStockStatus, "Stock " + ticker + " inserted successfully.", new Color(0, 140, 0));
            clearFields(txtStockTicker, txtStockExchange, txtStockPrice);
        } catch (SQLException ex) {
            showStatus(lblStockStatus, "DB Error: " + ex.getMessage(), Color.RED);
        }
    }

    //  DB loaders
    private void loadCompaniesIntoCombo(JComboBox<String> cmb) {
        cmb.removeAllItems();
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT CompanyID, CompanyName FROM COMPANY ORDER BY CompanyName")) {
            while (rs.next()) cmb.addItem(rs.getInt(1) + " – " + rs.getString(2));
        } catch (SQLException ex) {
            cmb.addItem("(connection error)");
        }
    }

    //  Utilities
    private int extractId(String item) {
        return Integer.parseInt(item.split(" – ")[0].trim());
    }

    private void showStatus(JLabel lbl, String msg, Color color) {
        lbl.setText(msg);
        lbl.setForeground(color);
    }

    private void clearFields(JTextField... fields) {
        for (JTextField f : fields) f.setText("");
    }

    private GridBagConstraints defaultGBC() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }

    private void addRow(JPanel p, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0;
        gbc.insets = new Insets(6, 10, 6, 4);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        p.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        gbc.insets = new Insets(6, 4, 6, 12);
        p.add(field, gbc);
    }
}
