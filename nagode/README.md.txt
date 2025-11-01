# 🚀 Nagode - API Spring Boot Sécurisée

![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.0-green)
![Security](https://img.shields.io/badge/Security-JWT-orange)
![MySQL](https://img.shields.io/badge/Database-MySQL-lightblue)

Application backend Nagode développée avec Spring Boot, sécurisée par JWT et système de refresh token.

## 📦 Fonctionnalités

- 🔐 **Authentification JWT** avec tokens sécurisés
- 🔄 **Système de Refresh Token** pour une sécurité renforcée
- 🛡️ **Spring Security 6** avec configuration avancée
- 🗄️ **Persistence des données** avec Spring Data JPA & MySQL
- 📡 **API RESTful** complète
- 🏗️ **Architecture modulaire** et maintenable

## 🛠️ Stack Technique

- **Backend:** Spring Boot 3.x
- **Sécurité:** Spring Security 6 + JWT
- **Base de données:** MySQL 8+
- **Build Tool:** Maven
- **Java:** Version 17+
- **Gestion des dépendances:** Spring Boot Starter

## 📋 Prérequis

Avant de commencer, assurez-vous d'avoir installé :

- ☕ **Java 17** ou supérieur
- 🗄️ **MySQL 8.0** ou supérieur
- 🛠️ **Maven 3.6** ou supérieur
- 📧 **Git** pour cloner le projet

## 🚀 Installation & Démarrage

### 1. Cloner le projet
```bash
git clone https://github.com/adammoukit/nagode.git

-- Se connecter à MySQL et exécuter :
CREATE DATABASE nagodeDB;
-- Ou utilisez votre outil de gestion MySQL préféré (phpMyAdmin, MySQL Workbench, etc.)

# Copier le template de configuration locale
cp src/main/resources/application-local.properties.template src/main/resources/application-local.properties

# Éditer le fichier avec vos paramètres
# Utilisez votre éditeur favori :
nano src/main/resources/application-local.properties
# ou
code src/main/resources/application-local.properties  # VS Code
# ou ouvrez avec IntelliJ/Eclipse