-- Création de la table des refresh tokens
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id CHAR(36) NOT NULL COMMENT 'UUID du refresh token',
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Date de création',
    updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT 'Date de mise à jour',

    token VARCHAR(255) NOT NULL COMMENT 'Token opaque unique',
    user_id CHAR(36) NOT NULL COMMENT 'Référence à l''utilisateur',
    expiry_date DATETIME NOT NULL COMMENT 'Date d''expiration du token',
    revoked TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'Indique si le token est révoqué (0=false, 1=true)',
    device_info VARCHAR(500) NULL COMMENT 'Informations sur l''appareil (user-agent)',
    ip_address VARCHAR(45) NULL COMMENT 'Adresse IP du client',

    PRIMARY KEY (id),
    UNIQUE KEY uk_refresh_tokens_token (token),

    -- Contrainte de clé étrangère vers la table users
    CONSTRAINT fk_refresh_tokens_user
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    -- Index pour les performances
    KEY idx_refresh_tokens_user_id (user_id),
    KEY idx_refresh_tokens_expiry_date (expiry_date),
    KEY idx_refresh_tokens_revoked (revoked)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci
COMMENT='Table de stockage des refresh tokens pour le renouvellement des JWT';