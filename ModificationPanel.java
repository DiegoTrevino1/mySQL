package StockPortfolio;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * Tab 2 – Data Modification.
 * Pattern: enter an ID → Search → fields populate → edit → Update.
 * Primary Keys are shown but locked (non-editable).
 * Sub-tabs: Investor | Company | Stock.
 */
public class ModificationPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    // Investor fields
    private JTextField txtInvestorId;
    private JTextField txtInvestorFirstName, txtInvestorLastName;
    private JTextField txtInvestorEmail, txtInvestorCountry, txtInvestorPhone;
    private JLabel     lblInvestorStatus;

    // Company fields
    private JTextField txtCompanyId;
    private JTextField txtCompanyName, txtCompanyIndustry;
    private JTextField txtCompanyCountry, txtCompanyFoundedYear;
    private JLabel     lblCompanyStatus;

    // Stock fields
    private JTextField        txtStockId;
    private JTextField        txtStockTicker, txtStockExchange, txtStockPrice;
    private JComboBox<String> cmbStockCompany;
    private JLabel            lblStockStatus;

    public ModificationPanel() {
        setLayout(new BorderLayout(0, 0));

        JLabel hint = new JLabel("  Enter an ID and click Search to load a record. Primary Keys are locked.");
        hint.setFont(new Font("SansSerif", Font.ITALIC, 11));
        hint.setForeground(Color.GRAY);
        hint.setBorder(BorderFactory.createEmptyBorder(6, 8, 4, 8));
        add(hint, BorderLayout.NORTH);

        JTabbedPane subTabs = new JTabbedPane(JTabbedPane.LEFT);
        subTabs.addTab("Investor", buildInvestorPanel());
        subTabs.addTab("Company",  buildCompanyPanel());
        subTabs.addTab("Stock",    buildStockPanel());

        subTabs.addChangeListener(e -> {
            if (subTabs.getSelectedIndex() == 2) loadCompaniesIntoCombo(cmbStockCompany);
        });

        add(subTabs, BorderLayout.CENTER);
    }

    /** Called by StockPortfolioGUI when this tab gains focus. */
    public void refreshCombos() {
        if (cmbStockCompany != null) loadCompaniesIntoCombo(cmbStockCompany);
    }

    //  Investor panel
    private JPanel buildInvestorPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new TitledBorder("Modify Investor"));
        GridBagConstraints gbc = defaultGBC();

        // Search row
        txtInvestorId = new JTextField(8);
        JButton btnSearch = new JButton("Search");
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        searchRow.add(new JLabel("Investor ID:"));
        searchRow.add(txtInvestorId);
        searchRow.add(btnSearch);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        p.add(searchRow, gbc);

        gbc.gridy = 1; gbc.insets = new Insets(0, 8, 8, 8);
        p.add(new JSeparator(), gbc);

        txtInvestorFirstName = new JTextField(24);
        txtInvestorLastName  = new JTextField(24);
        txtInvestorEmail     = new JTextField(24);
        txtInvestorCountry   = new JTextField(24);
        txtInvestorPhone     = new JTextField(24);
        setFieldsEnabled(false, txtInvestorFirstName, txtInvestorLastName,
                                txtInvestorEmail, txtInvestorCountry, txtInvestorPhone);

        addRow(p, gbc, 2, "First Name *", txtInvestorFirstName);
        addRow(p, gbc, 3, "Last Name *",  txtInvestorLastName);
        addRow(p, gbc, 4, "Email *",      txtInvestorEmail);
        addRow(p, gbc, 5, "Country *",    txtInvestorCountry);
        addRow(p, gbc, 6, "Phone",        txtInvestorPhone);

        JButton btnUpdate = new JButton("Update Investor");
        btnUpdate.setBackground(new Color(60, 160, 90));
        btnUpdate.setForeground(Color.black);
        btnUpdate.setFocusPainted(false);
        btnUpdate.setEnabled(false);
        JButton btnClear = new JButton("Clear");

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.add(btnUpdate);
        btnRow.add(btnClear);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        gbc.insets = new Insets(14, 8, 4, 8);
        p.add(btnRow, gbc);

        lblInvestorStatus = new JLabel(" ");
        lblInvestorStatus.setFont(new Font("SansSerif", Font.ITALIC, 11));
        gbc.gridy = 8; gbc.insets = new Insets(2, 10, 8, 8);
        p.add(lblInvestorStatus, gbc);

        btnSearch.addActionListener(e -> {
            if (searchInvestor(txtInvestorId.getText().trim())) {
                setFieldsEnabled(true, txtInvestorFirstName, txtInvestorLastName,
                                       txtInvestorEmail, txtInvestorCountry, txtInvestorPhone);
                btnUpdate.setEnabled(true);
            }
        });
        btnUpdate.addActionListener(e -> updateInvestor(txtInvestorId.getText().trim(), btnUpdate));
        btnClear.addActionListener(e -> {
            txtInvestorId.setText("");
            clearFields(txtInvestorFirstName, txtInvestorLastName,
                        txtInvestorEmail, txtInvestorCountry, txtInvestorPhone);
            setFieldsEnabled(false, txtInvestorFirstName, txtInvestorLastName,
                                    txtInvestorEmail, txtInvestorCountry, txtInvestorPhone);
            btnUpdate.setEnabled(false);
            lblInvestorStatus.setText(" ");
        });
        return p;
    }

    private boolean searchInvestor(String idStr) {
        if (idStr.isEmpty()) {
            showStatus(lblInvestorStatus, "Enter an Investor ID.", Color.RED);
            return false;
        }
        try {
            int id = Integer.parseInt(idStr);
            String sql = "SELECT FirstName, LastName, Email, Country, Phone FROM INVESTOR WHERE InvestorID = ?";
            try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    txtInvestorFirstName.setText(rs.getString("FirstName"));
                    txtInvestorLastName.setText(rs.getString("LastName"));
                    txtInvestorEmail.setText(rs.getString("Email"));
                    txtInvestorCountry.setText(rs.getString("Country"));
                    txtInvestorPhone.setText(rs.getString("Phone") != null ? rs.getString("Phone") : "");
                    showStatus(lblInvestorStatus, "Investor found. Edit fields and click Update.", new Color(0, 100, 180));
                    return true;
                } else {
                    showStatus(lblInvestorStatus, "No investor found with ID " + id + ".", Color.RED);
                    return false;
                }
            }
        } catch (NumberFormatException ex) {
            showStatus(lblInvestorStatus, "Investor ID must be an integer.", Color.RED);
        } catch (SQLException ex) {
            showStatus(lblInvestorStatus, "DB Error: " + ex.getMessage(), Color.RED);
        }
        return false;
    }

    private void updateInvestor(String idStr, JButton btnUpdate) {
        String firstName = txtInvestorFirstName.getText().trim();
        String lastName  = txtInvestorLastName.getText().trim();
        String email     = txtInvestorEmail.getText().trim();
        String country   = txtInvestorCountry.getText().trim();
        String phone     = txtInvestorPhone.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || country.isEmpty()) {
            showStatus(lblInvestorStatus, "First Name, Last Name, Email, and Country are required.", Color.RED);
            return;
        }

        String sql = "UPDATE INVESTOR SET FirstName=?, LastName=?, Email=?, Country=?, Phone=? WHERE InvestorID=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, email);
            ps.setString(4, country);
            ps.setString(5, phone.isEmpty() ? null : phone);
            ps.setInt(6, Integer.parseInt(idStr));
            int rows = ps.executeUpdate();
            if (rows > 0) {
                showStatus(lblInvestorStatus, "Investor updated successfully.", new Color(0, 140, 0));
                btnUpdate.setEnabled(false);
                setFieldsEnabled(false, txtInvestorFirstName, txtInvestorLastName,
                                        txtInvestorEmail, txtInvestorCountry, txtInvestorPhone);
            }
        } catch (SQLException ex) {
            showStatus(lblInvestorStatus, "DB Error: " + ex.getMessage(), Color.RED);
        }
    }

    //  Company panel
    private JPanel buildCompanyPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new TitledBorder("Modify Company"));
        GridBagConstraints gbc = defaultGBC();

        txtCompanyId = new JTextField(8);
        JButton btnSearch = new JButton("Search");
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        searchRow.add(new JLabel("Company ID:"));
        searchRow.add(txtCompanyId);
        searchRow.add(btnSearch);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        p.add(searchRow, gbc);

        gbc.gridy = 1; gbc.insets = new Insets(0, 8, 8, 8);
        p.add(new JSeparator(), gbc);

        txtCompanyName        = new JTextField(24);
        txtCompanyIndustry    = new JTextField(24);
        txtCompanyCountry     = new JTextField(24);
        txtCompanyFoundedYear = new JTextField(8);
        setComponentEnabled(false, txtCompanyName, txtCompanyIndustry,
                                   txtCompanyCountry, txtCompanyFoundedYear);

        addRow(p, gbc, 2, "Company Name *", txtCompanyName);
        addRow(p, gbc, 3, "Industry *",     txtCompanyIndustry);
        addRow(p, gbc, 4, "Country *",      txtCompanyCountry);
        addRow(p, gbc, 5, "Founded Year",   txtCompanyFoundedYear);

        JButton btnUpdate = new JButton("Update Company");
        btnUpdate.setBackground(new Color(60, 160, 90));
        btnUpdate.setForeground(Color.black);
        btnUpdate.setFocusPainted(false);
        btnUpdate.setEnabled(false);
        JButton btnClear = new JButton("Clear");

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.add(btnUpdate); btnRow.add(btnClear);
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.insets = new Insets(14, 8, 4, 8);
        p.add(btnRow, gbc);

        lblCompanyStatus = new JLabel(" ");
        lblCompanyStatus.setFont(new Font("SansSerif", Font.ITALIC, 11));
        gbc.gridy = 7; gbc.insets = new Insets(2, 10, 8, 8);
        p.add(lblCompanyStatus, gbc);

        btnSearch.addActionListener(e -> {
            if (searchCompany(txtCompanyId.getText().trim())) {
                setComponentEnabled(true, txtCompanyName, txtCompanyIndustry,
                                          txtCompanyCountry, txtCompanyFoundedYear);
                btnUpdate.setEnabled(true);
            }
        });
        btnUpdate.addActionListener(e -> updateCompany(txtCompanyId.getText().trim(), btnUpdate));
        btnClear.addActionListener(e -> {
            txtCompanyId.setText("");
            clearFields(txtCompanyName, txtCompanyIndustry, txtCompanyCountry, txtCompanyFoundedYear);
            setComponentEnabled(false, txtCompanyName, txtCompanyIndustry,
                                       txtCompanyCountry, txtCompanyFoundedYear);
            btnUpdate.setEnabled(false);
            lblCompanyStatus.setText(" ");
        });
        return p;
    }

    private boolean searchCompany(String idStr) {
        if (idStr.isEmpty()) { showStatus(lblCompanyStatus, "Enter a Company ID.", Color.RED); return false; }
        try {
            int id = Integer.parseInt(idStr);
            String sql = "SELECT CompanyName, Industry, Country, FoundedYear FROM COMPANY WHERE CompanyID = ?";
            try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    txtCompanyName.setText(rs.getString("CompanyName"));
                    txtCompanyIndustry.setText(rs.getString("Industry"));
                    txtCompanyCountry.setText(rs.getString("Country"));
                    txtCompanyFoundedYear.setText(rs.getObject("FoundedYear") != null ?
                            String.valueOf(rs.getInt("FoundedYear")) : "");
                    showStatus(lblCompanyStatus, "Company found. Edit and click Update.", new Color(0, 100, 180));
                    return true;
                } else {
                    showStatus(lblCompanyStatus, "No company found with ID " + id + ".", Color.RED);
                }
            }
        } catch (NumberFormatException ex) {
            showStatus(lblCompanyStatus, "Company ID must be an integer.", Color.RED);
        } catch (SQLException ex) {
            showStatus(lblCompanyStatus, "DB Error: " + ex.getMessage(), Color.RED);
        }
        return false;
    }

    private void updateCompany(String idStr, JButton btnUpdate) {
        String name    = txtCompanyName.getText().trim();
        String industry= txtCompanyIndustry.getText().trim();
        String country = txtCompanyCountry.getText().trim();
        String yearStr = txtCompanyFoundedYear.getText().trim();

        if (name.isEmpty() || industry.isEmpty() || country.isEmpty()) {
            showStatus(lblCompanyStatus, "Company Name, Industry, and Country are required.", Color.RED);
            return;
        }
        try {
            String sql = "UPDATE COMPANY SET CompanyName=?, Industry=?, Country=?, FoundedYear=? WHERE CompanyID=?";
            try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
                ps.setString(1, name);
                ps.setString(2, industry);
                ps.setString(3, country);
                if (yearStr.isEmpty()) ps.setNull(4, Types.INTEGER);
                else ps.setInt(4, Integer.parseInt(yearStr));
                ps.setInt(5, Integer.parseInt(idStr));
                ps.executeUpdate();
                showStatus(lblCompanyStatus, "Company updated successfully.", new Color(0, 140, 0));
                btnUpdate.setEnabled(false);
                setComponentEnabled(false, txtCompanyName, txtCompanyIndustry,
                                           txtCompanyCountry, txtCompanyFoundedYear);
            }
        } catch (NumberFormatException ex) {
            showStatus(lblCompanyStatus, "Founded Year must be an integer.", Color.RED);
        } catch (SQLException ex) {
            showStatus(lblCompanyStatus, "DB Error: " + ex.getMessage(), Color.RED);
        }
    }

    //  Stock panel
    private JPanel buildStockPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new TitledBorder("Modify Stock"));
        GridBagConstraints gbc = defaultGBC();

        txtStockId = new JTextField(8);
        JButton btnSearch = new JButton("Search");
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        searchRow.add(new JLabel("Stock ID:"));
        searchRow.add(txtStockId);
        searchRow.add(btnSearch);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        p.add(searchRow, gbc);

        gbc.gridy = 1; gbc.insets = new Insets(0, 8, 8, 8);
        p.add(new JSeparator(), gbc);

        cmbStockCompany  = new JComboBox<>();
        txtStockTicker   = new JTextField(24);
        txtStockExchange = new JTextField(24);
        txtStockPrice    = new JTextField(12);
        loadCompaniesIntoCombo(cmbStockCompany);
        setComponentEnabled(false, cmbStockCompany, txtStockTicker, txtStockExchange, txtStockPrice);

        addRow(p, gbc, 2, "Company",         cmbStockCompany);
        addRow(p, gbc, 3, "Ticker Symbol *", txtStockTicker);
        addRow(p, gbc, 4, "Exchange *",      txtStockExchange);
        addRow(p, gbc, 5, "Current Price *", txtStockPrice);

        JButton btnUpdate = new JButton("Update Stock");
        btnUpdate.setBackground(new Color(60, 160, 90));
        btnUpdate.setForeground(Color.black);
        btnUpdate.setFocusPainted(false);
        btnUpdate.setEnabled(false);
        JButton btnClear = new JButton("Clear");

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.add(btnUpdate); btnRow.add(btnClear);
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.insets = new Insets(14, 8, 4, 8);
        p.add(btnRow, gbc);

        lblStockStatus = new JLabel(" ");
        lblStockStatus.setFont(new Font("SansSerif", Font.ITALIC, 11));
        gbc.gridy = 7; gbc.insets = new Insets(2, 10, 8, 8);
        p.add(lblStockStatus, gbc);

        btnSearch.addActionListener(e -> {
            if (searchStock(txtStockId.getText().trim())) {
                setComponentEnabled(true, cmbStockCompany, txtStockTicker, txtStockExchange, txtStockPrice);
                btnUpdate.setEnabled(true);
            }
        });
        btnUpdate.addActionListener(e -> updateStock(txtStockId.getText().trim(), btnUpdate));
        btnClear.addActionListener(e -> {
            txtStockId.setText("");
            clearFields(txtStockTicker, txtStockExchange, txtStockPrice);
            setComponentEnabled(false, cmbStockCompany, txtStockTicker, txtStockExchange, txtStockPrice);
            btnUpdate.setEnabled(false);
            lblStockStatus.setText(" ");
        });
        return p;
    }

    private boolean searchStock(String idStr) {
        if (idStr.isEmpty()) { showStatus(lblStockStatus, "Enter a Stock ID.", Color.RED); return false; }
        try {
            int id = Integer.parseInt(idStr);
            String sql = "SELECT TickerSymbol, ExchangeName, CurrentPrice, CompanyID FROM STOCK WHERE StockID = ?";
            try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    txtStockTicker.setText(rs.getString("TickerSymbol"));
                    txtStockExchange.setText(rs.getString("ExchangeName"));
                    txtStockPrice.setText(String.valueOf(rs.getDouble("CurrentPrice")));
                    selectComboById(cmbStockCompany, rs.getInt("CompanyID"));
                    showStatus(lblStockStatus, "Stock found. Edit and click Update.", new Color(0, 100, 180));
                    return true;
                } else {
                    showStatus(lblStockStatus, "No stock found with ID " + id + ".", Color.RED);
                }
            }
        } catch (NumberFormatException ex) {
            showStatus(lblStockStatus, "Stock ID must be an integer.", Color.RED);
        } catch (SQLException ex) {
            showStatus(lblStockStatus, "DB Error: " + ex.getMessage(), Color.RED);
        }
        return false;
    }

    private void updateStock(String idStr, JButton btnUpdate) {
        String ticker   = txtStockTicker.getText().trim();
        String exchange = txtStockExchange.getText().trim();
        String priceStr = txtStockPrice.getText().trim();
        String company  = (String) cmbStockCompany.getSelectedItem();

        if (ticker.isEmpty() || exchange.isEmpty() || priceStr.isEmpty() || company == null) {
            showStatus(lblStockStatus, "All stock fields are required.", Color.RED); return;
        }
        try {
            double price     = Double.parseDouble(priceStr);
            int    companyId = extractId(company);
            String sql = "UPDATE STOCK SET TickerSymbol=?, ExchangeName=?, CurrentPrice=?, CompanyID=? WHERE StockID=?";
            try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
                ps.setString(1, ticker);
                ps.setString(2, exchange);
                ps.setDouble(3, price);
                ps.setInt(4, companyId);
                ps.setInt(5, Integer.parseInt(idStr));
                ps.executeUpdate();
                showStatus(lblStockStatus, "Stock updated successfully.", new Color(0, 140, 0));
                btnUpdate.setEnabled(false);
                setComponentEnabled(false, cmbStockCompany, txtStockTicker, txtStockExchange, txtStockPrice);
            }
        } catch (NumberFormatException ex) {
            showStatus(lblStockStatus, "Price must be a valid number.", Color.RED);
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
        } catch (SQLException ex) { cmb.addItem("(connection error)"); }
    }

    //  Utilities
    private int extractId(String item) {
        return Integer.parseInt(item.split(" – ")[0].trim());
    }

    private void selectComboById(JComboBox<String> cmb, int targetId) {
        for (int i = 0; i < cmb.getItemCount(); i++) {
            if (extractId(cmb.getItemAt(i)) == targetId) { cmb.setSelectedIndex(i); return; }
        }
    }

    private void showStatus(JLabel lbl, String msg, Color color) {
        lbl.setText(msg);
        lbl.setForeground(color);
    }

    private void clearFields(JTextField... fields) {
        for (JTextField f : fields) f.setText("");
    }

    private void setFieldsEnabled(boolean enabled, JTextField... fields) {
        for (JTextField f : fields) f.setEnabled(enabled);
    }

    private void setComponentEnabled(boolean enabled, JComponent... comps) {
        for (JComponent c : comps) c.setEnabled(enabled);
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
