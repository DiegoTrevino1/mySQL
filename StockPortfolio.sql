
-- CS420 - Stock Portfolio Management System
-- SQL Script: CREATE TABLE + ALTER TABLE + Sample Data
CREATE DATABASE IF NOT EXISTS stock_portfolio;
USE stock_portfolio;

-- STEP 1: CREATE TABLES (bare, no FK constraints yet)

CREATE TABLE IF NOT EXISTS INVESTOR (
    InvestorID   INT          NOT NULL AUTO_INCREMENT,
    FirstName    VARCHAR(50)  NOT NULL,
    LastName     VARCHAR(50)  NOT NULL,
    Email        VARCHAR(100) NOT NULL,
    Country      VARCHAR(50)  NOT NULL,
    Phone        VARCHAR(20),
    PRIMARY KEY (InvestorID)
);

CREATE TABLE IF NOT EXISTS BROKERAGE_ACCOUNT (
    AccountID    INT          NOT NULL AUTO_INCREMENT,
    AccountType  VARCHAR(50)  NOT NULL,
    BrokerName   VARCHAR(100) NOT NULL,
    Balance      DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    InvestorID   INT          NOT NULL,
    PRIMARY KEY (AccountID)
);

CREATE TABLE IF NOT EXISTS COMPANY (
    CompanyID    INT          NOT NULL AUTO_INCREMENT,
    CompanyName  VARCHAR(100) NOT NULL,
    Industry     VARCHAR(100) NOT NULL,
    Country      VARCHAR(50)  NOT NULL,
    FoundedYear  INT,
    PRIMARY KEY (CompanyID)
);

CREATE TABLE IF NOT EXISTS STOCK (
    StockID        INT          NOT NULL AUTO_INCREMENT,
    TickerSymbol   VARCHAR(10)  NOT NULL,
    ExchangeName   VARCHAR(50)  NOT NULL,
    CurrentPrice   DECIMAL(10,2) NOT NULL,
    CompanyID      INT          NOT NULL,
    PRIMARY KEY (StockID)
);

CREATE TABLE IF NOT EXISTS TRADE_TRANSACTION (
    TransactionID  INT          NOT NULL AUTO_INCREMENT,
    TradeDate      DATE         NOT NULL,
    TradeType      VARCHAR(4)   NOT NULL CHECK (TradeType IN ('BUY','SELL')),
    Quantity       INT          NOT NULL,
    PricePerShare  DECIMAL(10,2) NOT NULL,
    AccountID      INT          NOT NULL,
    StockID        INT          NOT NULL,
    PRIMARY KEY (TransactionID)
);

-- STEP 2: ALTER TABLE - Add Foreign Key Constraints

ALTER TABLE BROKERAGE_ACCOUNT
    ADD CONSTRAINT fk_account_investor
    FOREIGN KEY (InvestorID) REFERENCES INVESTOR(InvestorID);

ALTER TABLE STOCK
    ADD CONSTRAINT fk_stock_company
    FOREIGN KEY (CompanyID) REFERENCES COMPANY(CompanyID);

ALTER TABLE TRADE_TRANSACTION
    ADD CONSTRAINT fk_transaction_account
    FOREIGN KEY (AccountID) REFERENCES BROKERAGE_ACCOUNT(AccountID);

ALTER TABLE TRADE_TRANSACTION
    ADD CONSTRAINT fk_transaction_stock
    FOREIGN KEY (StockID) REFERENCES STOCK(StockID);

-- STEP 3: INSERT Sample Data

-- Investors (5 rows)
INSERT INTO INVESTOR (FirstName, LastName, Email, Country, Phone) VALUES
('Emma',    'Johnson',  'ejohnson@email.com',  'USA',     '509-555-1001'),
('Liam',    'Brown',    'lbrown@email.com',    'USA',     '509-555-1002'),
('Sophia',  'Davis',    'sdavis@email.com',    'Canada',  '604-555-1003'),
('Noah',    'Wilson',   'nwilson@email.com',   'USA',     '206-555-1004'),
('Olivia',  'Martin',   'omartin@email.com',   'UK',      NULL);

-- Brokerage Accounts (5 rows)
INSERT INTO BROKERAGE_ACCOUNT (AccountType, BrokerName, Balance, InvestorID) VALUES
('Individual', 'Fidelity',     15000.00, 1),
('Roth IRA',   'Vanguard',     32000.00, 2),
('Individual', 'Charles Schwab',8500.00, 3),
('401k',       'Fidelity',     75000.00, 4),
('Individual', 'TD Ameritrade',12000.00, 5);

-- Companies (5 rows)
INSERT INTO COMPANY (CompanyName, Industry, Country, FoundedYear) VALUES
('Apple Inc.',       'Technology',   'USA', 1976),
('Tesla Inc.',       'Automotive',   'USA', 2003),
('Amazon.com Inc.',  'E-Commerce',   'USA', 1994),
('JPMorgan Chase',   'Finance',      'USA', 1799),
('Pfizer Inc.',      'Healthcare',   'USA', 1849);

-- Stocks (5 rows)
INSERT INTO STOCK (TickerSymbol, ExchangeName, CurrentPrice, CompanyID) VALUES
('AAPL', 'NASDAQ', 189.50, 1),
('TSLA', 'NASDAQ', 245.30, 2),
('AMZN', 'NASDAQ', 178.90, 3),
('JPM',  'NYSE',   198.75, 4),
('PFE',  'NYSE',    28.40, 5);

-- Trade Transactions (5 rows)
INSERT INTO TRADE_TRANSACTION (TradeDate, TradeType, Quantity, PricePerShare, AccountID, StockID) VALUES
('2026-01-15', 'BUY',  10, 185.00, 1, 1),
('2026-02-03', 'BUY',   5, 240.00, 2, 2),
('2026-02-20', 'SELL',  3, 192.00, 1, 1),
('2026-03-10', 'BUY',   8, 175.00, 3, 3),
('2026-04-05', 'SELL',  2, 250.00, 2, 2);

-- STEP 4: Verification Queries

SELECT * FROM INVESTOR;
SELECT * FROM BROKERAGE_ACCOUNT;
SELECT * FROM COMPANY;
SELECT * FROM STOCK;
SELECT * FROM TRADE_TRANSACTION;
