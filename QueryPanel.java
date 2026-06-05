package StockPortfolio;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * Tab 3 – Data Query.
 *
 * Two query patterns, each on its own sub-tab:
 *
 *  Pattern 1 – Transactions by Investor
 *      Filters : Investor (dropdown), Trade Type (All / BUY / SELL),
 *                Sort (Date newest first / oldest first / Quantity high→low)
 *      Shows   : Trade Date, Trade Type, Ticker Symbol, Company Name,
 *                Industry, Quantity, Price per Share
 *
 *  Pattern 2 – Top N Stocks by Total Trade Volume
 *      Filters : Industry (dropdown or "All"), Top N (5 / 15 / 25 / All)
 *      Shows   : Rank, Ticker Symbol, Company Name, Industry,
 *                Total Shares Traded, Number of Transactions
 */
public class QueryPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    // ── Pattern 1
    private JComboBox<String> cmbP1Investor;
    private JComboBox<String> cmbP1TradeType;
    private JComboBox<String> cmbP1Sort;
    private JTable            tblP1;
    private DefaultTableModel mdlP1;
    private JLabel            lblP1Count;

    // ── Pattern 2
    private JComboBox<String> cmbP2Industry;
    private JComboBox<String> cmbP2TopN;
    private JTable            tblP2;
    private DefaultTableModel mdlP2;
    private JLabel            lblP2Count;

    public QueryPanel() {
        setLayout(new BorderLayout(0, 0));

        JLabel hint = new JLabel("  Set filters and click Run Query to display results.");
        hint.setFont(new Font("SansSerif", Font.ITALIC, 11));
        hint.setForeground(Color.GRAY);
        hint.setBorder(BorderFactory.createEmptyBorder(6, 8, 4, 8));
        add(hint, BorderLayout.NORTH);

        JTabbedPane subTabs = new JTabbedPane(JTabbedPane.LEFT);
        subTabs.addTab("Transactions by Investor", buildPattern1());
        subTabs.addTab("Top N by Trade Volume",    buildPattern2());

        add(subTabs, BorderLayout.CENTER);
    }

    /** Called by StockPortfolioGUI when this tab gains focus. */
    public void refreshCombos() {
        loadInvestorsIntoCombo(cmbP1Investor);
        loadIndustriesIntoCombo(cmbP2Industry);
    }

    //  Pattern 1 – Transactions by Investor
    private JPanel buildPattern1() {
        JPanel outer = new JPanel(new BorderLayout(0, 6));
        outer.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        filters.setBorder(new TitledBorder("Filters"));

        cmbP1Investor  = new JComboBox<>();
        cmbP1TradeType = new JComboBox<>(new String[]{"All", "BUY", "SELL"});
        cmbP1Sort      = new JComboBox<>(new String[]{
            "Date (Newest First)",
            "Date (Oldest First)",
            "Quantity (High → Low)"
        });

        loadInvestorsIntoCombo(cmbP1Investor);

        filters.add(new JLabel("Investor:"));   filters.add(cmbP1Investor);
        filters.add(Box.createHorizontalStrut(16));
        filters.add(new JLabel("Trade Type:")); filters.add(cmbP1TradeType);
        filters.add(Box.createHorizontalStrut(16));
        filters.add(new JLabel("Sort by:"));    filters.add(cmbP1Sort);

        JButton btnRun = runButton();
        filters.add(Box.createHorizontalStrut(16));
        filters.add(btnRun);

        outer.add(filters, BorderLayout.NORTH);

        mdlP1 = new DefaultTableModel(
            new String[]{"Trade Date", "Trade Type", "Ticker", "Company Name", "Industry", "Quantity", "Price/Share"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblP1 = styledTable(mdlP1);
        outer.add(new JScrollPane(tblP1), BorderLayout.CENTER);

        lblP1Count = countLabel();
        outer.add(lblP1Count, BorderLayout.SOUTH);

        btnRun.addActionListener(e -> runPattern1());
        return outer;
    }

    private void runPattern1() {
        String investorItem = (String) cmbP1Investor.getSelectedItem();
        if (investorItem == null) return;

        boolean allInvestors = investorItem.equals("All Investors");
        String  tradeType    = (String) cmbP1TradeType.getSelectedItem();
        boolean allTypes     = "All".equals(tradeType);

        String orderBy;
        switch (cmbP1Sort.getSelectedIndex()) {
            case 1:  orderBy = "tt.TradeDate ASC";   break;
            case 2:  orderBy = "tt.Quantity DESC";   break;
            default: orderBy = "tt.TradeDate DESC";  break;
        }

        StringBuilder sql = new StringBuilder(
            "SELECT tt.TradeDate, tt.TradeType, s.TickerSymbol, c.CompanyName, c.Industry, " +
            "tt.Quantity, tt.PricePerShare " +
            "FROM TRADE_TRANSACTION tt " +
            "JOIN BROKERAGE_ACCOUNT ba ON tt.AccountID = ba.AccountID " +
            "JOIN INVESTOR i  ON ba.InvestorID = i.InvestorID " +
            "JOIN STOCK s     ON tt.StockID = s.StockID " +
            "JOIN COMPANY c   ON s.CompanyID = c.CompanyID " +
            "WHERE 1=1 "
        );
        if (!allInvestors) sql.append("AND i.InvestorID = ? ");
        if (!allTypes)     sql.append("AND tt.TradeType = ? ");
        sql.append("ORDER BY ").append(orderBy);

        mdlP1.setRowCount(0);
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql.toString())) {
            int param = 1;
            if (!allInvestors) ps.setInt(param++,    extractId(investorItem));
            if (!allTypes)     ps.setString(param++, tradeType);
            ResultSet rs = ps.executeQuery();
            int count = 0;
            while (rs.next()) {
                mdlP1.addRow(new Object[]{
                    rs.getString("TradeDate"),
                    rs.getString("TradeType"),
                    rs.getString("TickerSymbol"),
                    rs.getString("CompanyName"),
                    rs.getString("Industry"),
                    rs.getInt("Quantity"),
                    String.format("$%.2f", rs.getDouble("PricePerShare"))
                });
                count++;
            }
            lblP1Count.setText("  " + count + " record(s) returned.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Query error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //  Pattern 2 – Top N Stocks by Total Trade Volume
    private JPanel buildPattern2() {
        JPanel outer = new JPanel(new BorderLayout(0, 6));
        outer.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        filters.setBorder(new TitledBorder("Filters"));

        cmbP2Industry = new JComboBox<>();
        cmbP2TopN     = new JComboBox<>(new String[]{"Top 5", "Top 15", "Top 25", "All"});
        cmbP2TopN.setSelectedIndex(0);

        loadIndustriesIntoCombo(cmbP2Industry);

        filters.add(new JLabel("Industry:")); filters.add(cmbP2Industry);
        filters.add(Box.createHorizontalStrut(16));
        filters.add(new JLabel("Show:"));     filters.add(cmbP2TopN);

        JButton btnRun = runButton();
        filters.add(Box.createHorizontalStrut(16));
        filters.add(btnRun);

        outer.add(filters, BorderLayout.NORTH);

        mdlP2 = new DefaultTableModel(
            new String[]{"Rank", "Ticker", "Company Name", "Industry", "Total Shares Traded", "# Transactions"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblP2 = styledTable(mdlP2);
        outer.add(new JScrollPane(tblP2), BorderLayout.CENTER);

        lblP2Count = countLabel();
        outer.add(lblP2Count, BorderLayout.SOUTH);

        btnRun.addActionListener(e -> runPattern2());
        return outer;
    }

    private void runPattern2() {
        String industryItem = (String) cmbP2Industry.getSelectedItem();
        boolean allIndustries = industryItem == null || industryItem.equals("All Industries");

        String topNStr  = (String) cmbP2TopN.getSelectedItem();
        boolean limitAll = "All".equals(topNStr);
        int limit = limitAll ? Integer.MAX_VALUE : Integer.parseInt(topNStr.replace("Top ", ""));

        StringBuilder sql = new StringBuilder(
            "SELECT s.TickerSymbol, c.CompanyName, c.Industry, " +
            "SUM(tt.Quantity) AS TotalShares, COUNT(tt.TransactionID) AS NumTransactions " +
            "FROM STOCK s " +
            "JOIN COMPANY c             ON s.CompanyID = c.CompanyID " +
            "JOIN TRADE_TRANSACTION tt  ON tt.StockID  = s.StockID "
        );
        if (!allIndustries) sql.append("WHERE c.Industry = ? ");
        sql.append("GROUP BY s.StockID, s.TickerSymbol, c.CompanyName, c.Industry ");
        sql.append("ORDER BY TotalShares DESC ");
        if (!limitAll) sql.append("LIMIT ?");

        mdlP2.setRowCount(0);
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql.toString())) {
            int paramIdx = 1;
            if (!allIndustries) ps.setString(paramIdx++, industryItem);
            if (!limitAll)      ps.setInt(paramIdx,      limit);
            ResultSet rs = ps.executeQuery();
            int rank = 1, count = 0;
            while (rs.next()) {
                mdlP2.addRow(new Object[]{
                    rank++,
                    rs.getString("TickerSymbol"),
                    rs.getString("CompanyName"),
                    rs.getString("Industry"),
                    String.format("%,d", rs.getInt("TotalShares")),
                    rs.getInt("NumTransactions")
                });
                count++;
            }
            lblP2Count.setText("  " + count + " record(s) returned.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Query error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //  DB loaders
    private void loadInvestorsIntoCombo(JComboBox<String> cmb) {
        cmb.removeAllItems();
        cmb.addItem("All Investors");
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT InvestorID, FirstName, LastName FROM INVESTOR ORDER BY LastName")) {
            while (rs.next())
                cmb.addItem(rs.getInt(1) + " – " + rs.getString(2) + " " + rs.getString(3));
        } catch (SQLException ex) { cmb.addItem("(connection error)"); }
    }

    private void loadIndustriesIntoCombo(JComboBox<String> cmb) {
        cmb.removeAllItems();
        cmb.addItem("All Industries");
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT DISTINCT Industry FROM COMPANY ORDER BY Industry")) {
            while (rs.next()) cmb.addItem(rs.getString(1));
        } catch (SQLException ex) { /* keep "All Industries" only */ }
    }

    //  Utilities
    private int extractId(String item) {
        return Integer.parseInt(item.split(" – ")[0].trim());
    }

    private JButton runButton() {
        JButton btn = new JButton("Run Query");
        btn.setBackground(new Color(60, 130, 200));
        btn.setForeground(Color.blue);
        btn.setFocusPainted(false);
        return btn;
    }

    private JLabel countLabel() {
        JLabel lbl = new JLabel("Run a query to see results.");
        lbl.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lbl.setForeground(Color.GRAY);
        lbl.setBorder(BorderFactory.createEmptyBorder(4, 0, 2, 0));
        return lbl;
    }

    private JTable styledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.setRowHeight(24);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(new Color(220, 220, 220));
        table.setShowGrid(true);
        return table;
    }
}
