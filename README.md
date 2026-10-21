# Stock Portfolio Management System

A Java desktop app backed by a MySQL database for tracking investors, brokerage accounts, companies, stocks, and trades. I built it for my Database Management course at Central Washington University (CS 420, Spring 2026). I used AI-assisted coding tools for parts of the Java code, and I designed the database and wrote the SQL.

## What it does

The app opens a Swing window with three tabs.

**Data Insertion** adds new investors, companies, and stocks to the database.

**Data Modification** searches for an existing record and lets you edit it.

**Data Query** runs two reports, each with its own filters:
- Transactions by Investor: joins trades, accounts, investors, stocks, and companies. You can filter by investor and trade type (BUY or SELL) and sort by date or quantity.
- Top Stocks by Trade Volume: groups trades by stock and ranks them by total shares traded. You can filter by industry and choose how many results to show.

Screenshots of each step are in this repo (for example `Transactions by Investor.png` and the Modifying screenshots).

## Database design

The schema is in `StockPortfolio.sql`, with an ER diagram (`ER diagram Assignment 5.jpg`) and a relational schema (`StockPortfolio_RelationalSchema.pdf`).

There are five tables:
- `INVESTOR`
- `BROKERAGE_ACCOUNT` (belongs to an investor)
- `COMPANY`
- `STOCK` (belongs to a company)
- `TRADE_TRANSACTION` (links a brokerage account to a stock, with a CHECK so the trade type can only be BUY or SELL)

Primary keys are auto-increment IDs, and the relationships are enforced with named foreign keys. The script also inserts sample data (5 rows per table) so the queries return something right away.

## Tech

Java (Swing), JDBC, MySQL

## Files

- `StockPortfolio.sql`: creates the `stock_portfolio` database, tables, and sample data
- `StockPortfolioGUI.java`: main window and tabs
- `InsertionPanel.java`, `ModificationPanel.java`, `QueryPanel.java`: the three tabs
- `DatabaseConnection.java`: opens and closes the MySQL connection
- `Assignment4.sql`, `student_lab.sql`: other course SQL work
- PDFs and images: assignment write-ups and screenshots

## Running it

1. Run `StockPortfolio.sql` in MySQL to create and fill the database.
2. Put your own MySQL username and password in `DatabaseConnection.java`.
3. Add the MySQL JDBC driver (Connector/J) to your classpath, then compile and run `StockPortfolioGUI.java`.
