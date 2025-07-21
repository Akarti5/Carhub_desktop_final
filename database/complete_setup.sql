-- CarHub Database Complete Setup Script
-- Generated for IntelliJ IDEA integration

-- Create database and user
CREATE DATABASE carhub
    WITH OWNER = postgres
    ENCODING = 'UTF8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

-- Connect to carhub database
\c carhub;

-- Create user
CREATE USER carhub_user WITH PASSWORD 'carhub123';
GRANT ALL PRIVILEGES ON DATABASE carhub TO carhub_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO carhub_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO carhub_user;

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create sequences
CREATE SEQUENCE admin_seq START 1;
CREATE SEQUENCE car_seq START 1;
CREATE SEQUENCE client_seq START 1;
CREATE SEQUENCE sale_seq START 1;
CREATE SEQUENCE car_image_seq START 1;
CREATE SEQUENCE system_setting_seq START 1;

-- ADMINS TABLE
CREATE TABLE admins (
    id BIGINT PRIMARY KEY DEFAULT nextval('admin_seq'),
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'ADMIN',
    is_active BOOLEAN NOT NULL DEFAULT true,
    last_login TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    avatar_url VARCHAR(255),
    phone_number VARCHAR(20)
);

-- CARS TABLE
CREATE TABLE cars (
    id BIGINT PRIMARY KEY DEFAULT nextval('car_seq'),
    brand VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    year INTEGER NOT NULL CHECK (year >= 1900 AND year <= 2030),
    price DECIMAL(12,2) NOT NULL CHECK (price >= 0),
    cost_price DECIMAL(12,2) CHECK (cost_price >= 0),
    mileage INTEGER CHECK (mileage >= 0),
    fuel_type VARCHAR(20) DEFAULT 'PETROL',
    transmission VARCHAR(20) DEFAULT 'MANUAL',
    engine_size VARCHAR(10),
    color VARCHAR(30),
    vin_number VARCHAR(17) UNIQUE,
    license_plate VARCHAR(15),
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    condition VARCHAR(20) DEFAULT 'USED',
    description TEXT,
    location VARCHAR(100),
    days_in_stock INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sold_at TIMESTAMP,
    created_by BIGINT REFERENCES admins(id)
);

-- CLIENTS TABLE
CREATE TABLE clients (
    id BIGINT PRIMARY KEY DEFAULT nextval('client_seq'),
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone_number VARCHAR(20) NOT NULL,
    address TEXT,
    city VARCHAR(50),
    postal_code VARCHAR(10),
    country VARCHAR(50) DEFAULT 'Madagascar',
    date_of_birth DATE,
    gender VARCHAR(10),
    preferred_contact VARCHAR(20) DEFAULT 'PHONE',
    notes TEXT,
    customer_type VARCHAR(20) DEFAULT 'INDIVIDUAL',
    credit_score INTEGER CHECK (credit_score >= 0 AND credit_score <= 850),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT REFERENCES admins(id)
);

-- SALES TABLE
CREATE TABLE sales (
    id BIGINT PRIMARY KEY DEFAULT nextval('sale_seq'),
    car_id BIGINT NOT NULL REFERENCES cars(id),
    client_id BIGINT NOT NULL REFERENCES clients(id),
    admin_id BIGINT NOT NULL REFERENCES admins(id),
    sale_price DECIMAL(12,2) NOT NULL CHECK (sale_price >= 0),
    profit DECIMAL(12,2),
    payment_method VARCHAR(20) NOT NULL DEFAULT 'CASH',
    payment_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    down_payment DECIMAL(12,2) DEFAULT 0,
    financing_amount DECIMAL(12,2) DEFAULT 0,
    monthly_payment DECIMAL(10,2) DEFAULT 0,
    loan_term_months INTEGER DEFAULT 0,
    sale_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    delivery_date TIMESTAMP,
    invoice_number VARCHAR(20) UNIQUE NOT NULL,
    warranty_months INTEGER DEFAULT 12,
    notes TEXT,
    discount_amount DECIMAL(10,2) DEFAULT 0,
    tax_amount DECIMAL(10,2) DEFAULT 0,
    total_amount DECIMAL(12,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- CAR IMAGES TABLE
CREATE TABLE car_images (
    id BIGINT PRIMARY KEY DEFAULT nextval('car_image_seq'),
    car_id BIGINT NOT NULL REFERENCES cars(id) ON DELETE CASCADE,
    image_path VARCHAR(255) NOT NULL,
    image_name VARCHAR(100) NOT NULL,
    image_size BIGINT,
    is_primary BOOLEAN DEFAULT false,
    display_order INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- SYSTEM SETTINGS TABLE
CREATE TABLE system_settings (
    id BIGINT PRIMARY KEY DEFAULT nextval('system_setting_seq'),
    setting_key VARCHAR(50) NOT NULL UNIQUE,
    setting_value TEXT NOT NULL,
    setting_type VARCHAR(20) NOT NULL DEFAULT 'STRING',
    description TEXT,
    is_editable BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Car features table for ElementCollection
CREATE TABLE car_features (
    car_id BIGINT NOT NULL REFERENCES cars(id) ON DELETE CASCADE,
    feature VARCHAR(255) NOT NULL,
    PRIMARY KEY (car_id, feature)
);

-- CREATE INDEXES FOR PERFORMANCE
CREATE INDEX idx_cars_brand_model ON cars(brand, model);
CREATE INDEX idx_cars_price ON cars(price);
CREATE INDEX idx_cars_status ON cars(status);
CREATE INDEX idx_cars_year ON cars(year);
CREATE INDEX idx_cars_created_at ON cars(created_at);

CREATE INDEX idx_sales_sale_date ON sales(sale_date);
CREATE INDEX idx_sales_car_id ON sales(car_id);
CREATE INDEX idx_sales_client_id ON sales(client_id);
CREATE INDEX idx_sales_admin_id ON sales(admin_id);

CREATE INDEX idx_clients_name ON clients(first_name, last_name);
CREATE INDEX idx_clients_email ON clients(email);
CREATE INDEX idx_clients_phone ON clients(phone_number);

CREATE INDEX idx_car_images_car_id ON car_images(car_id);
CREATE INDEX idx_car_images_primary ON car_images(car_id, is_primary);

-- CREATE VIEWS FOR REPORTING
CREATE VIEW v_car_inventory AS
SELECT 
    c.id,
    c.brand,
    c.model,
    c.year,
    c.price,
    c.status,
    c.days_in_stock,
    c.created_at,
    COUNT(ci.id) as image_count,
    a.full_name as added_by
FROM cars c
LEFT JOIN car_images ci ON c.id = ci.car_id
LEFT JOIN admins a ON c.created_by = a.id
GROUP BY c.id, a.full_name;

CREATE VIEW v_sales_summary AS
SELECT 
    s.id,
    s.invoice_number,
    s.sale_date,
    s.sale_price,
    s.profit,
    s.payment_status,
    c.brand || ' ' || c.model as car_name,
    cl.first_name || ' ' || cl.last_name as client_name,
    a.full_name as sold_by
FROM sales s
JOIN cars c ON s.car_id = c.id
JOIN clients cl ON s.client_id = cl.id
JOIN admins a ON s.admin_id = a.id;

-- INSERT SAMPLE DATA
-- Default admin user (password is 'password' hashed with BCrypt)
INSERT INTO admins (username, email, password_hash, full_name, role) VALUES 
('admin', 'admin@carhub.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'System Administrator', 'SUPER_ADMIN'),
('dealer1', 'dealer1@carhub.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'John Dealer', 'ADMIN'),
('sales1', 'sales1@carhub.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Jane Sales', 'SALES');

-- Sample cars data
INSERT INTO cars (brand, model, year, price, cost_price, mileage, fuel_type, transmission, color, status, condition, created_by) VALUES
('Toyota', 'Camry', 2022, 25000.00, 22000.00, 15000, 'PETROL', 'AUTOMATIC', 'Silver', 'AVAILABLE', 'USED', 1),
('Honda', 'Civic', 2023, 22000.00, 19500.00, 8000, 'PETROL', 'MANUAL', 'Black', 'AVAILABLE', 'USED', 1),
('Ford', 'Mustang', 2021, 35000.00, 30000.00, 25000, 'PETROL', 'AUTOMATIC', 'Red', 'AVAILABLE', 'USED', 1),
('BMW', 'X5', 2023, 55000.00, 48000.00, 12000, 'DIESEL', 'AUTOMATIC', 'White', 'AVAILABLE', 'USED', 1),
('Mercedes', 'C-Class', 2022, 45000.00, 40000.00, 18000, 'PETROL', 'AUTOMATIC', 'Blue', 'SOLD', 'USED', 1);

-- Sample clients
INSERT INTO clients (first_name, last_name, email, phone_number, address, city, created_by) VALUES
('Rakoto', 'Andry', 'rakoto@email.com', '+261-34-123-4567', '123 Analakely Street', 'Antananarivo', 1),
('Rasoa', 'Marie', 'rasoa@email.com', '+261-33-987-6543', '456 Ankorondrano Avenue', 'Antananarivo', 1),
('Jean', 'Pierre', 'jean@email.com', '+261-32-555-7890', '789 Ambohipo Road', 'Antananarivo', 1);

-- Sample sale
INSERT INTO sales (car_id, client_id, admin_id, sale_price, profit, payment_method, payment_status, invoice_number, total_amount) VALUES
(5, 1, 1, 45000.00, 5000.00, 'CASH', 'COMPLETED', 'INV-2024-001', 45000.00);

-- Update sold car status
UPDATE cars SET status = 'SOLD', sold_at = CURRENT_TIMESTAMP WHERE id = 5;

-- System settings
INSERT INTO system_settings (setting_key, setting_value, setting_type, description) VALUES
('company_name', 'CarHub', 'STRING', 'Company name for invoices and branding'),
('company_address', '123 Business Street, Antananarivo, Madagascar', 'STRING', 'Company address'),
('company_phone', '+261-20-123-4567', 'STRING', 'Company phone number'),
('company_email', 'info@carhub.com', 'STRING', 'Company email address'),
('tax_rate', '20.0', 'DECIMAL', 'Default tax rate percentage'),
('currency', 'MGA', 'STRING', 'Default currency'),
('invoice_prefix', 'INV', 'STRING', 'Invoice number prefix'),
('warranty_months', '12', 'INTEGER', 'Default warranty period in months');

-- Grant permissions to sequences
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO carhub_user;

COMMIT;
