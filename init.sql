DROP TABLE IF EXISTS currency_exchange;
CREATE TABLE currency_exchange (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  currency_from CHAR(3)  DEFAULT NULL,
  currency_to CHAR(3) DEFAULT NULL,
  exchange_rate DECIMAL(5,2) DEFAULT NULL,
  environment VARCHAR(25) DEFAULT NULL
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

INSERT INTO currency_exchange (currency_from, currency_to, exchange_rate, environment)
VALUES ('USD', 'CNY', 7.24, 'db'),
       ('USD', 'EUR', 0.84, 'db'),
       ('USD', 'JPY', 104.94, 'db'),
       ('USD', 'TWD', 28.50, 'db');
