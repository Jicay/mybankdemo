# DemoMyBank - Projet d'exemple

## 📋 Vue d'ensemble

Projet pédagogique démontrant la mise en œuvre d'une application bancaire simplifiée avec Spring Boot et une
architecture hexagonale (Ports & Adapters).

L'application permet de gérer des clients bancaires avec des opérations CRUD basiques via une API REST.

## 🏗️ Architecture

### Architecture Hexagonale (Ports & Adapters)

Ce projet suit les principes de l'**architecture hexagonale** (aussi appelée Ports & Adapters), qui vise à isoler le
domaine métier des détails d'implémentation technique.

```
┌─────────────────────────────────────────────────────────────┐
│                    Infrastructure Layer                      │
│                                                               │
│  ┌──────────────────┐              ┌──────────────────┐    │
│  │  Driving (API)   │              │  Driven (Data)   │    │
│  │                  │              │                  │    │
│  │ ClientController │              │ InMemoryClient   │    │
│  │     (REST)       │              │   Repository     │    │
│  └────────┬─────────┘              └────────▲─────────┘    │
│           │                                  │               │
└───────────┼──────────────────────────────────┼───────────────┘
            │                                  │
            │        ┌──────────────────────┐  │
            │        │   Domain Layer       │  │
            │        │                      │  │
            └───────►│   Use Cases:        │  │
                     │   - CreateClient    │◄─┘
                     │   - ListClients     │
                     │                      │
                     │   Model:             │
                     │   - Client           │
                     │                      │
                     │   Ports:             │
                     │   - ClientRepository │
                     └──────────────────────┘
```

### Structure du projet

```
src/main/java/com/example/mybank/
├── domain/                          # Couche Domaine (cœur métier)
│   ├── model/                       # Modèles du domaine
│   │   └── Client.java              # Entité Client avec Value Objects
│   ├── ports/                       # Interfaces (contrats)
│   │   └── ClientRepository.java
│   └── usecase/                     # Cas d'usage métier
│       └── client/
│           ├── CreateClient.java    # Création d'un client
│           └── ListClients.java     # Listage des clients
│
├── infrastructure/                  # Couche Infrastructure
│   ├── application/                 # Configuration de l'application
│   │   └── config/
│   │       └── UseCaseConfiguration.java
│   ├── driven/                      # Adaptateurs sortants (bases de données, etc.)
│   │   └── inmemory/
│   │       └── InMemoryClientRepository.java
│   └── driving/                     # Adaptateurs entrants (API, UI, etc.)
│       └── rest/
│           ├── ClientController.java
│           └── dto/                 # Data Transfer Objects
│               ├── ClientDTO.java
│               └── CreateClientRequest.java
│
└── DemoMyBankApplication.java       # Point d'entrée Spring Boot
```

### Principes appliqués

1. **Séparation des préoccupations** : Le domaine métier est complètement isolé de l'infrastructure
2. **Inversion de dépendances** : L'infrastructure dépend du domaine, jamais l'inverse
3. **Ports** : Interfaces définissant les contrats (`ClientRepository`)
4. **Adapters** : Implémentations concrètes (`InMemoryClientRepository`, `ClientController`)
5. **Value Objects** : Objets immuables encapsulant la logique métier (`Client.Name`, `Client.Id`)
6. **Records Java** : Utilisation des records pour des modèles immuables et concis

## 🛠️ Technologies et outils

### Framework & Langage

- **Java 25** : Version moderne du langage avec support des records et pattern matching
- **Spring Boot 4.0.0** : Framework d'application avec autoconfiguration
    - `spring-boot-starter-web` : Pour l'API REST
    - `spring-boot-starter-logging` : Gestion des logs (SLF4J + Logback)
    - `spring-boot-devtools` : Rechargement automatique en développement
    - `spring-boot-docker-compose` : Intégration Docker Compose

### Build & Gestion de dépendances

- **Gradle 8.x** (Kotlin DSL) : Outil de build moderne
- **Gradle Wrapper** : Version de Gradle incluse dans le projet

### Bibliothèques

- **ULID Creator** (`com.github.f4b6a3:ulid-creator:5.2.3`) : Génération d'identifiants ULID (alternative aux UUID,
  triables chronologiquement)

## 🚀 Démarrage rapide

### Prérequis

- Java 25 ou supérieur
- Docker et Docker Compose (optionnel, pour PostgreSQL)

### Lancer l'application

```bash
# Avec Gradle Wrapper (recommandé)
./gradlew bootRun

# Ou compiler puis exécuter
./gradlew build
java -jar build/libs/demoMyBank-0.0.1-SNAPSHOT.jar
```

L'application démarre sur **http://localhost:8080**

### Lancer les services Docker

```bash
docker-compose up -d
```

Cela démarre PostgreSQL sur le port 5432.

## 📡 API REST

### Endpoints disponibles

#### Lister tous les clients

```http
GET /api/clients
Content-Type: application/json
```

**Réponse** : `200 OK`

```json
[
  {
    "id": "01JDEXAMPLE123456789",
    "lastName": "Public",
    "firstName": "Client"
  }
]
```

#### Créer un nouveau client

```http
POST /api/clients
Content-Type: application/json

{
  "lastName": "Public",
  "firstName": "Client"
}
```

**Réponse** : `201 CREATED`

```json
{
  "id": "01JDEXAMPLE123456789",
  "lastName": "Public",
  "firstName": "Client"
}
```

**Erreur possible** : `500 Internal Server Error` si le client existe déjà

### Fichiers de test HTTP

Les requêtes sont disponibles dans `doc/api/client.http` pour une utilisation avec les clients HTTP IntelliJ IDEA ou VS
Code REST Client.
