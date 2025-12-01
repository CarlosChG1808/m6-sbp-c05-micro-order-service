-- ============================================
-- Migration: V4__CREATE_ORDERS_TABLES
-- Description: Crear tablas orders y order_items
-- Database: H2
-- ============================================

-- Tabla de órdenes
CREATE TABLE orders
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(50)     NOT NULL UNIQUE,
    user_id      BIGINT          NOT NULL,
    status       VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    total_amount DECIMAL(10, 2)  NOT NULL DEFAULT 0.00,
    created_at   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_status CHECK (status IN ('PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED')),
    CONSTRAINT chk_total_positive CHECK (total_amount >= 0)
);

-- Tabla de items
CREATE TABLE order_items
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id   BIGINT         NOT NULL,
    product_id BIGINT         NOT NULL,
    quantity   INTEGER        NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    subtotal   DECIMAL(10, 2) NOT NULL,

    CONSTRAINT fk_order FOREIGN KEY (order_id)
        REFERENCES orders (id) ON DELETE CASCADE,
    CONSTRAINT chk_quantity_positive CHECK (quantity > 0),
    CONSTRAINT chk_unit_price_positive CHECK (unit_price >= 0),
    CONSTRAINT chk_subtotal_positive CHECK (subtotal >= 0)
);

-- Comentarios
COMMENT ON TABLE orders IS 'Órdenes del sistema';
COMMENT ON TABLE order_items IS 'Items de las órdenes';