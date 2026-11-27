# DemoMyBank - Projet d'exemple

## 📋 Vue d'ensemble

Projet pédagogique démontrant la mise en œuvre d'une application bancaire simplifiée avec Spring Boot et une
architecture hexagonale (Ports & Adapters).

L'application permet de gérer des clients bancaires et leurs comptes bancaires avec des opérations CRUD via une API REST
entièrement documentée avec Swagger/OpenAPI.

## 🏗️ Architecture

### Architecture Hexagonale (Ports & Adapters)

Ce projet suit les principes de l'**architecture hexagonale** (aussi appelée Ports & Adapters), qui vise à isoler le
domaine métier des détails d'implémentation technique.

```mermaid
---
config:
  theme: 'base'
  themeVariables:
    primaryColor: '#BB2528'
    primaryTextColor: '#fff'
    primaryBorderColor: '#7C0000'
    lineColor: '#F8B229'
    secondaryColor: '#006100'
    tertiaryColor: '#fff'
---
graph TB
    subgraph Infrastructure["🔷 Infrastructure Layer"]
        subgraph Driving["⬅️ Driving Adapters (Primary/Left)"]
            Web["🌐 Web (Thymeleaf)"]
            REST["🔌 REST API + SSE"]
            KafkaIn["📥 Kafka Consumer"]
        end

        subgraph Driven["➡️ Driven Adapters (Secondary/Right)"]
            JPA["💾 Adapter JPA"]
            JDBC["💾 Adapter JDBC"]
            KafkaOut["📤 Kafka Producer"]
        end
    end

    subgraph Domain["🎯 Domain Layer (Core Business Logic)"]
        UseCases["📋 Use Cases"]
        Models["🏗️ Domain Models"]
        Ports["🔌 Ports (Interfaces)"]
    end

    Web -->|Uses| UseCases
    REST -->|Uses| UseCases
    KafkaIn -->|Uses| UseCases
    REST -->|Publishes| KafkaOut
    UseCases -->|Manipulates| Models
    UseCases -.->|Depends on| Ports
    JPA -.->|Implements| Ports
    JDBC -.->|Implements| Ports
    style Domain fill: #e1f5ff, stroke: #01579b, stroke-width: 3px
    style Infrastructure fill: #fff3e0, stroke: #e65100, stroke-width: 2px
    style UseCases fill: #4a148c, stroke: #4a148c, stroke-width: 2px
    style Models fill: #1b5e20, stroke: #1b5e20, stroke-width: 2px
    style Ports fill: #f57f17, stroke: #f57f17, stroke-width: 2px
```

**Légende :**

- **🎯 Domain Layer** : Cœur métier, indépendant de toute technologie
- **⬅️ Driving Adapters** : Points d'entrée (Web, REST API, consommation Kafka)
- **➡️ Driven Adapters** : Points de sortie (Base de données, publication Kafka)
- **🔌 Ports** : Interfaces définissant les contrats
- **➡️ Flèches pleines** : Dépendances directes
- **⤏ Flèches pointillées** : Implémentation d'interface

### Structure du projet

```
src/main/
├── java/com/example/mybank/
│   ├── domain/                          # Couche Domaine (cœur métier)
│   │   ├── model/                       # Modèles du domaine
│   │   ├── ports/                       # Interfaces (contrats)
│   │   │   ├── driven/
│   │   └── usecase/                     # Cas d'usage métier
│   │       ├── client/
│   │       ├── account/
│   │       ├── user/
│   │       └── transaction/             # Create, List et Stream (SSE)
│   │
│   ├── infrastructure/                  # Couche Infrastructure
│   │   ├── application/                 # Configuration de l'application
│   │   │   └── config/
│   │   │       └── UseCaseConfiguration.java
│   │   ├── driven/                      # Adaptateurs sortants (bases de données, etc.)
│   │   │   ├── jpa/                     # Entités JPA et repositories
│   │   │   ├── jdbc/                    # Implémentation JDBC
│   │   │   └── kafka/                   # Publication des transactions (profil producer)
│   │   ├── driving/                     # Adaptateurs entrants (API, UI, etc.)
│   │   │   ├── web/                     # Contrôleurs web (Thymeleaf)
│   │   │   ├── kafka/                   # Consommation des transactions (profil consumer)
│   │   │   ├── batch/                   # Job Spring Batch d'import CSV (config, dto, processor, writer)
│   │   │   └── rest/
│   │   │       ├── GlobalExceptionHandler.java
│   │   │       ├── security/            # Sécurité (JWT, filtres)
│   │   │       └── dto/                 # Data Transfer Objects
│   │   └── messaging/                   # TransactionEvent (message Kafka)
│   │
│   └── DemoMyBankApplication.java       # Point d'entrée Spring Boot
│
├── resources/
│   ├── application.yaml                 # Configuration commune (Kafka)
│   ├── application-consumer.yaml        # BDD, JWT, binding Kafka entrant
│   ├── application-producer.yaml        # Port 8081, binding Kafka sortant
│   ├── import/transactions.csv          # Fichier importé par le job Spring Batch
│   ├── templates/                       # Templates Thymeleaf
│   │   └── error/                       # Pages d'erreur personnalisées
│   ├── static/                          # Ressources statiques (JS, CSS)
│   │   └── js/
│   └── db/changelog/                    # Migrations Liquibase
```

### Principes appliqués

1. **Séparation des préoccupations** : Le domaine métier est complètement isolé de l'infrastructure
2. **Inversion de dépendances** : L'infrastructure dépend du domaine, jamais l'inverse
3. **Ports** : Interfaces définissant les contrats (`ClientRepository`, `AccountRepository`)
4. **Adapters** : Implémentations concrètes (JPA, REST Controllers, etc.)
5. **Value Objects** : Objets immuables encapsulant la logique métier (`Client.Name`, `Client.Id`, `Amount`)
6. **Records Java** : Utilisation des records pour des modèles immuables et concis
7. **DTOs** : Séparation entre les modèles du domaine et les représentations API

## 🛠️ Technologies et outils

### Framework & Langage

- **Java 25** : Version moderne du langage avec support des records et pattern matching
- **Spring Boot 4.0.0** : Framework d'application avec autoconfiguration
    - `spring-boot-starter-web` : Pour l'API REST et serveur web
    - `spring-boot-starter-thymeleaf` : Moteur de templates pour le rendu HTML côté serveur
    - `spring-boot-starter-logging` : Gestion des logs (SLF4J + Logback)
    - `spring-boot-starter-validation` : Validation Bean Validation (JSR-303)
    - `spring-boot-starter-data-jpa` : ORM avec Hibernate
    - `spring-boot-starter-jdbc` : Accès aux bases de données
    - `spring-boot-devtools` : Rechargement automatique en développement
    - `spring-boot-docker-compose` : Intégration Docker Compose
    - `spring-boot-starter-webflux` : `Flux` et Sinks Reactor pour le streaming SSE
- **Spring Cloud Stream 2025.1** (compatible Spring Boot 4) : Messaging avec le binder Kafka
    - `spring-cloud-stream` et `spring-cloud-stream-binder-kafka`
- **Spring Batch 6** (`spring-boot-starter-batch-jdbc`) : Import de transactions depuis un CSV, historique des jobs
  en base

### Frontend

- **Thymeleaf** : Moteur de templates Java pour le rendu server-side
- **Alpine.js 3.x** : Framework JavaScript réactif minimaliste (~15kb)
- **Pico CSS 2** : Framework CSS sans classes pour un design moderne
- **Fetch API** : Communication AJAX avec l'API REST

### Build & Gestion de dépendances

- **Gradle 8.x** (Kotlin DSL) : Outil de build moderne avec gestion des dépendances
- **Gradle Wrapper** : Version de Gradle incluse dans le projet pour reproductibilité

### Bibliothèques principales

- **ULID Creator** (`com.github.f4b6a3:ulid-creator:5.2.3`) : Génération d'identifiants ULID (ULIDv1, triables
  chronologiquement, alternartive aux UUID)
- **Jakarta Validation** : Support de la validation Bean Validation (JSR-303)
- **SpringDoc OpenAPI** (`springdoc-openapi-starter-webmvc-ui:2.8.13`) : Génération automatique de la documentation
  Swagger/OpenAPI avec interface Swagger UI
- **Spring Data JPA** : ORM pour la persistence des données avec Hibernate
- **PostgreSQL Driver** : Driver JDBC pour PostgreSQL
- **Liquibase** : Gestion des migrations de base de données (versioning du schéma)

### Infrastructure

- **Docker Compose** : Orchestration des conteneurs
- **PostgreSQL 15.2** : Base de données relationnelle
- **Kafka 7.4.4 (Confluent) + Zookeeper** : Broker de messages pour les transactions

## 🚀 Démarrage rapide

### Prérequis

- Java 25 ou supérieur
- Docker et Docker Compose
- Git

### Installation et exécution

#### 1. Cloner le projet

```bash
git clone <repository-url>
cd demoMyBank
```

#### 2. Lancer les services Docker (optionnel)

```bash
docker-compose up -d
```

Cela démarre PostgreSQL sur le port 5432 avec les credentials :

- Utilisateur : `myuser`
- Base de données : `mydatabase`
- Mot de passe : `secret`

Ainsi que Kafka sur **localhost:29092** (et Zookeeper).

#### 3. Lancer l'application

Le même code démarre en **deux applications** selon le profil Spring actif :

| Profil     | Port | Rôle                                                                     |
|------------|------|--------------------------------------------------------------------------|
| `consumer` | 8080 | Application principale : UI, API, BDD, consomme les transactions Kafka   |
| `producer` | 8081 | API minimale qui publie les transactions dans Kafka (pas de BDD)         |

La variable d'environnement `JWT_SECRET` est obligatoire pour le profil `consumer`.

**Option 1 : Depuis l'IDE IntelliJ IDEA (recommandé)**

- Lancer les configurations `Run Consumer` et `Run Producer` (dossier `.run/`), simultanément

**Option 2 : Avec Gradle Wrapper**

```bash
# Terminal 1
JWT_SECRET=localtest ./gradlew bootRun --args='--spring.profiles.active=consumer'
# Terminal 2
./gradlew bootRun --args='--spring.profiles.active=producer'
```

**Option 3 : Compiler et exécuter**

```bash
./gradlew build
JWT_SECRET=localtest java -jar build/libs/mybank-0.0.1-SNAPSHOT.jar --spring.profiles.active=consumer
java -jar build/libs/mybank-0.0.1-SNAPSHOT.jar --spring.profiles.active=producer
```

L'application principale démarre sur **http://localhost:8080**, le producer sur **http://localhost:8081**.

> ⚠️ Après son démarrage, le consumer met jusqu'à ~30 s à rejoindre le topic Kafka (log
> `partitions assigned`). Une transaction envoyée avant est ignorée.

#### 4. Vérifier que l'application fonctionne

```bash
curl http://localhost:8080/api/clients
```

Ou simplement ouvrir **http://localhost:8080** dans votre navigateur.

## 🎨 Interface Web

### Technologies Frontend

L'application dispose d'une **interface web moderne** construite avec :

- **Thymeleaf** : Moteur de templates côté serveur pour le rendu HTML
- **Alpine.js 3.x** : Framework JavaScript léger pour l'interactivité (alternative à Vue.js/React)
- **Pico CSS 2** : Framework CSS minimaliste sans classes pour un design élégant
- **Fetch API** : Appels AJAX vers l'API REST

### Pages disponibles

#### 1. Page d'accueil - Liste des clients (`/`)

**Rendu** : Server-side avec Thymeleaf  
**Features** :

- Affichage de tous les clients dans un tableau
- Lien vers la page des comptes de chaque client
- Navigation simple et intuitive

**Template** : `src/main/resources/templates/index.html`

#### 2. Page des comptes (`/clients/{clientId}/accounts`)

**Rendu** : Client-side avec Alpine.js  
**Features** :

- Récupération automatique du `clientId` depuis l'URL
- Liste dynamique des comptes du client
- Formulaire de création de compte avec validation
- États de chargement et gestion d'erreurs
- Mise à jour en temps réel après création

**Template** : `src/main/resources/templates/accounts.html`

#### 3. Page des transactions (`/transactions?accountId=...`)

**Rendu** : Client-side avec Alpine.js  
**Features** :

- Accessible via le lien « Voir transactions » de chaque compte
- Chargement des transactions existantes (`GET /api/transactions`)
- Réception en temps réel des nouvelles transactions via `EventSource` (SSE), sans rafraîchir la page
- Le JWT est passé en query param `authorization` car `EventSource` ne permet pas d'envoyer de header

**Template** : `src/main/resources/templates/transactions.html`

#### 4. Pages d'erreur personnalisées

**Design** : Pico CSS avec messages clairs en français  
**Features** :

- Messages explicatifs adaptés à chaque erreur
- Boutons d'action (Retour à l'accueil, Page précédente, Réessayer)
- Icônes emoji pour une meilleure compréhension
- Design responsive et cohérent avec l'application

**Pages disponibles** :

- **403 Forbidden** : `/error/403` - 🚫 Accès interdit
- **404 Not Found** : `/error/404` - 🔍 Page introuvable
- **500 Server Error** : `/error/500` - ⚠️ Erreur serveur interne
- **Erreur générique** : `/error/generic` - ❌ Erreur non catégorisée

**Templates** : `src/main/resources/templates/error/*.html`

**Redirection automatique** depuis JavaScript en cas d'erreur API.

### Gestion des erreurs côté client

Le fichier `app.js` contient une fonction utilitaire `fetchWithRedirect()` qui :

- Gère automatiquement les erreurs HTTP
- Redirige vers les pages d'erreur appropriées
- Supporte les appels avec ou sans redirection

```javascript
async function fetchWithRedirect(url, options = {}, withRedirect = true) {
    try {
        const res = await fetch(url, options);
        if (!res.ok) {
            // Redirection automatique selon le code HTTP
            if (res.status === 404) window.location.href = '/error/404';
            if (res.status >= 500) window.location.href = '/error/500';
        }
        return res.json();
    } catch (e) {
        // Erreur réseau ou CORS
        window.location.href = '/error/generic';
    }
}
```

## 📨 Transactions temps réel (SSE + Kafka)

Une transaction est créée sur le **producer**, transite par **Kafka**, puis est enregistrée et diffusée en direct par
le **consumer** :

```mermaid
sequenceDiagram
    participant Client as Client HTTP
    participant Producer as Producer :8081
    participant Kafka as Kafka (topic transactions)
    participant Consumer as Consumer :8080
    participant DB as PostgreSQL
    participant Browser as Page /transactions
    Browser ->> Consumer: GET /api/transactions/stream (EventSource)
    Client ->> Producer: POST /api/producer/transactions
    Producer -->> Client: 202 Accepted
    Producer ->> Kafka: TransactionEvent (Supplier<Flux>)
    Kafka ->> Consumer: TransactionEvent (Consumer<T>)
    Consumer ->> DB: CreateTransaction → save
    Consumer -->> Browser: data:{transaction} (SSE)
```

**Composants** :

- **Producer** : `TransactionProducerController` → `TransactionProducer` (Sink) → `TransactionProducerConfig`
  (`Supplier<Flux<TransactionEvent>>`, binding `transactions-out-0`)
- **Consumer** : `TransactionConsumerConfig` (`Consumer<TransactionEvent>`, binding `transactions-in-0`, groupe
  `mybank-transactions`) → use case `CreateTransaction`
- **SSE** : `CreateTransaction` publie dans un `Sinks.Many<Transaction>` partagé ; `StreamTransactions` expose ce Sink
  filtré sur le compte demandé
- **Profils** : les beans existants (UI, API, BDD, sécurité JWT) sont annotés `@Profile("consumer")`, ceux de
  publication `@Profile("producer")`

## 📦 Import de transactions (Spring Batch)

Le consumer expose un job Spring Batch `transactionImportJob` qui importe `src/main/resources/import/transactions.csv` :

```csv
accountId,amountCents,type,date
01K661WHAJYZ8GK0G4MXZ8DFY7,15075,CREDIT,2024-01-15 10:30:00
```

**Step `transactionImportStep`** (chunks de 100) :

- **Reader** : `FlatFileItemReader<TransactionImportDto>` (record, en-tête ignoré)
- **Processor** : `TransactionImportProcessor` valide la ligne et la transforme en `Transaction` ; une ligne invalide
  (champ manquant, type différent de `CREDIT`/`DEBIT`, date hors format `yyyy-MM-dd HH:mm:ss`) est **filtrée** et
  loggée
- **Writer** : `TransactionImportWriter` passe par le use case `CreateTransaction` (les transactions importées sont
  donc aussi diffusées en SSE)

**Lancement et suivi** (port 8080, sans authentification) :

```bash
curl -X POST http://localhost:8080/api/batch/import-transactions   # lance le job, retourne jobId et statut
curl http://localhost:8080/api/batch/job-status/1                   # lus / écrits / filtrés / ignorés
curl http://localhost:8080/api/batch/jobs                           # historique des exécutions
```

Les tables de métadonnées Spring Batch (`BATCH_*`) sont créées par Liquibase (changeset `005-spring-batch`, script
officiel `schema-postgresql.sql`). Le job n'est pas lancé au démarrage (`spring.batch.job.enabled: false`).

> ⚠️ Les `accountId` du CSV doivent exister en base : un compte inconnu viole la clé étrangère et fait échouer le
> job entier. Remplacez les identifiants du fichier par ceux de vos comptes avant de lancer l'import.

## 🔐 Sécurité

### Authentification et Autorisation

L'application utilise **Spring Security** avec authentification **JWT (JSON Web Token)** pour sécuriser l'accès aux
ressources.

#### Composants de sécurité

```
infrastructure/driving/rest/security/
├── JwtAuthenticationFilter.java       # Filtre pour valider les tokens JWT
├── JwtTokenProvider.java              # Génération et validation des tokens
└──  SecurityConfig.java                # Configuration Spring Security
```

#### Flow d'authentification

```mermaid
sequenceDiagram
    participant User as 👤 Utilisateur
    participant Client as 🌐 Client (Browser)
    participant API as 🔌 API
    participant Security as 🛡️ Spring Security
    participant JWT as 🎫 JWT Provider
    participant DB as 🗄️ Database
    User ->> Client: Entre username/password
    Client ->> API: POST /api/auth/login
    API ->> Security: Authentifier
    Security ->> DB: Vérifier credentials
    DB -->> Security: User trouvé
    Security ->> JWT: Générer token
    JWT -->> API: Token JWT
    API -->> Client: {token, user}
    Client ->> Client: Stocke token (localStorage)
    Note over Client, API: Requêtes suivantes
    Client ->> API: GET /api/clients<br/>Header: Authorization: Bearer {token}
    API ->> Security: Valider token
    Security ->> JWT: Vérifier signature & expiration
    JWT -->> Security: Token valide
    Security -->> API: Utilisateur authentifié
    API ->> DB: Récupérer données
    DB -->> API: Données
    API -->> Client: 200 OK + données
```

### Configuration de sécurité

#### Endpoints publics (sans authentification)

- `POST /api/auth/login` - Connexion
- `POST /api/clients/register` - Inscription
- `GET /error/**` - Pages d'erreur
- `/swagger-ui.html`, `/v3/api-docs/**` - Documentation API
- `/api/batch/**` - Lancement et suivi des jobs Spring Batch

#### Endpoints protégés (JWT requis)

- `GET /api/clients` - Liste des clients
- `POST /api/clients` - Créer un client
- `GET /api/clients/accounts` - Liste des comptes
- `POST /api/clients/accounts` - Créer un compte
- `GET /api/transactions?accountId=...` - Liste des transactions d'un compte (403 si le compte n'appartient pas au client)
- `GET /api/transactions/stream?accountId=...` - Flux SSE (token en header ou en query param `authorization`)
- Tous les autres endpoints `/api/**`

#### Producer (port 8081)

- `POST /api/producer/transactions` - Public, aucune authentification

### JWT (JSON Web Token)

#### Structure du token

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.    ← Header
eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6Ikp.  ← Payload
SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw  ← Signature
```

**Contenu du payload** :

```json
{
  "sub": "username",
  "username": "username",
  "clientId": "01KB5409F8BXVDYJJR27SS6HSC",
  "iat": 1732791000,
  // Issued At
  "exp": 1732794600
  // Expiration (1h)
}
```

#### Configuration

- **Algorithme** : HS256 (HMAC avec SHA-256)
- **Secret** : Clé secrète stockée dans les variables d'environnement
- **Durée de validité** : 1 heure (3600 secondes)

### Stockage du token côté client

```javascript
// Après login réussi
const response = await fetch('/api/auth/login', {...});
const data = await response.json();

// Stocker le token
localStorage.setItem('token', data.token);

// Utiliser le token dans les requêtes
fetch('/api/clients', {
    headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
    }
});
```

### Protection CSRF

- **Désactivé pour l'API REST** : Utilisation de tokens JWT (stateless)
- **Activé pour les pages web** : Protection automatique Spring Security pour Thymeleaf

### Headers de sécurité

Spring Security configure automatiquement les headers HTTP de sécurité :

```http
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 1; mode=block
Strict-Transport-Security: max-age=31536000; includeSubDomains
```

### Validation des données

#### Côté serveur (Spring Validation)

```java

@PostMapping("/register")
public ResponseEntity<ClientDTO> register(@Valid @RequestBody RegisterRequest request) {
    // ...
}
```

**Annotations utilisées** :

- `@NotBlank` : Champ non vide
- `@Email` : Format email valide
- `@Size(min = 8)` : Longueur minimum du mot de passe
- `@Pattern` : Expression régulière personnalisée

#### Côté client (Alpine.js)

```html
<input type="email" x-model="credentials.username" required>
<input type="password" x-model="credentials.password" minlength="8" required>
```

### Gestion des mots de passe

- **Encodage** : BCrypt avec facteur de coût 12
- **Pas de stockage en clair** : Jamais de mot de passe en base de données non encodé
- **Validation** : Minimum 8 caractères (extensible avec règles de complexité)

```java

@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
}
```

### Gestion des erreurs d'authentification

#### 401 Unauthorized

Retourné quand :

- Token JWT manquant
- Token JWT invalide ou expiré
- Credentials incorrects lors du login

#### 403 Forbidden

Retourné quand :

- Utilisateur authentifié mais sans les permissions nécessaires
- Accès à une ressource interdite

### Bonnes pratiques implémentées

✅ **Authentification stateless** : JWT pour scalabilité  
✅ **Passwords sécurisés** : BCrypt avec salt automatique  
✅ **Token expiration** : Limite la durée de validité  
✅ **HTTPS recommandé** : En production pour chiffrer les communications  
✅ **Validation stricte** : Côté serveur et client  
✅ **Headers de sécurité** : Protection XSS, clickjacking, MIME sniffing  
✅ **Gestion d'erreurs** : Messages d'erreur non informatifs pour la sécurité

### Améliorations futures

🔄 **Refresh tokens** : Renouvellement automatique sans re-login  
🔄 **Rate limiting** : Limitation des tentatives de connexion  
🔄 **2FA/MFA** : Authentification à deux facteurs  
🔄 **OAuth 2.0** : Connexion avec Google, GitHub, etc.  
🔄 **Audit logging** : Traçabilité des actions sensibles  
🔄 **Role-Based Access Control (RBAC)** : Permissions granulaires par rôle

## 🔗 URLs Utiles

### Interface Web (Frontend)

| URL                                | Description                                |
|------------------------------------|--------------------------------------------|
| **http://localhost:8080/**         | 🏠 Page d'accueil - Liste des clients      |
| **http://localhost:8080/accounts** | 💰 Page de gestion des comptes d'un client |
| **http://localhost:8080/transactions?accountId=...** | 💸 Transactions d'un compte en temps réel |
| **http://localhost:8080/login**    | 🔑 Page de connexion utilisateur           |
| **http://localhost:8080/register** | 📝 Page d'inscription/création de compte   |
| **http://localhost:8080/login**    | 🔑 Page de connexion utilisateur           |
| **http://localhost:8080/register** | 📝 Page d'inscription/création de compte   |

### Pages d'erreur personnalisées

| URL                                     | Description                               |
|-----------------------------------------|-------------------------------------------|
| **http://localhost:8080/error/403**     | 🚫 Accès interdit (Forbidden)             |
| **http://localhost:8080/error/404**     | 🔍 Page introuvable (Not Found)           |
| **http://localhost:8080/error/500**     | ⚠️ Erreur serveur (Internal Server Error) |
| **http://localhost:8080/error/generic** | ❌ Erreur générique (fallback)             |

### Documentation et Exploration de l'API

| URL                                        | Description                                                       |
|--------------------------------------------|-------------------------------------------------------------------|
| **http://localhost:8080/swagger-ui.html**  | 🔍 Interface Swagger UI - Exploration et test interactif de l'API |
| **http://localhost:8080/v3/api-docs**      | 📄 Documentation OpenAPI au format JSON                           |
| **http://localhost:8080/v3/api-docs.yaml** | 📄 Documentation OpenAPI au format YAML                           |
| **http://localhost:8081/swagger-ui.html**  | 🔍 Swagger UI du producer                                          |

### Endpoints de l'application

| Méthode | Endpoint                | Description                    | Codes de réponse   |
|---------|-------------------------|--------------------------------|--------------------|
| `GET`   | `/api/clients`          | Lister tous les clients        | 200, 401           |
| `POST`  | `/api/clients`          | Créer un nouveau client        | 201, 400, 401, 409 |
| `GET`   | `/api/clients/accounts` | Lister les comptes d'un client | 200, 401           |
| `POST`  | `/api/clients/accounts` | Créer un compte pour un client | 201, 400, 401, 404 |
| `POST`  | `/api/auth/login`       | Authentifier l'utilisateur     | 200, 400, 401      |
| `POST`  | `/api/clients/register` | Créer un utilisateur           | 201, 400, 409      |
| `POST`  | `/api/auth/login`       | Authentifier l'utilisateur     | 200, 400, 401      |
| `POST`  | `/api/clients/register` | Créer un utilisateur           | 200, 400, 401, 409 |
| `GET`   | `/api/transactions?accountId=...`        | Lister les transactions d'un compte       | 200, 401, 403, 404 |
| `GET`   | `/api/transactions/stream?accountId=...` | Flux SSE des nouvelles transactions       | 200, 401, 403, 404 |
| `POST`  | `:8081/api/producer/transactions`        | Publier une transaction dans Kafka        | 202, 400           |
| `POST`  | `/api/batch/import-transactions`         | Lancer l'import CSV (Spring Batch)        | 200, 500           |
| `GET`   | `/api/batch/job-status/{jobId}`          | Statut et compteurs d'une exécution       | 200, 404           |
| `GET`   | `/api/batch/jobs`                        | Historique des exécutions                 | 200                |

## 📡 Détails des Endpoints API

### 1. Lister tous les clients

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

### 2. Créer un nouveau client

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

**Erreurs possibles** :

- `400 Bad Request` : Données invalides (ex: champs vides ou invalides)
  ```json
  {
    "timestamp": "2025-11-26T10:30:00Z",
    "status": 400,
    "error": "Bad Request",
    "message": "Validation failed",
    "path": "/api/clients",
    "validationErrors": {
      "firstName": "must not be blank",
      "lastName": "must not be blank"
    }
  }
  ```
- `409 Conflict` : Client déjà existant avec ce nom et prénom
  ```json
  {
    "timestamp": "2025-11-26T10:30:00Z",
    "status": 409,
    "error": "Conflict",
    "message": "Client already exists: Public Client",
    "path": "/api/clients",
    "validationErrors": null
  }
  ```

### 3. Lister les comptes d'un client

```http
GET /api/clients/{clientId}/accounts
Content-Type: application/json
```

**Paramètres** :

- `clientId` (path) : ID ULID du client (ex: 01JDEXAMPLE123456789)

**Réponse** : `200 OK`

```json
[
  {
    "id": "01JDEXAMPLE123456789",
    "name": "Compte courant",
    "type": "COMPTE_COURANT",
    "amountCents": 150000
  }
]
```

### 4. Créer un compte pour un client

```http
POST /api/clients/{clientId}/accounts
Content-Type: application/json

{
  "name": "Compte courant",
  "type": "COMPTE_COURANT",
  "amountCents": 0
}
```

**Paramètres** :

- `clientId` (path) : ID ULID du client
- `name` (body) : Nom du compte (string)
- `type` (body) : Type de compte - valeurs autorisées : `COMPTE_COURANT`, `LIVRET_A`, `LDD`, `PEA`, `CTO`, `PEL`
- `amountCents` (body) : Montant en centimes (long)

**Réponse** : `201 CREATED`

```json
{
  "id": "01JDEXAMPLE987654321",
  "name": "Compte courant",
  "type": "COMPTE_COURANT",
  "amountCents": 0
}
```

**Erreurs possibles** :

- `400 Bad Request` : Données invalides
- `404 Not Found` : Client non trouvé

### 5. Authentifier un utilisateur (Login)

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "john.doe@example.com",
  "password": "motdepasse123"
}
```

**Paramètres** :

- `username` (body) : Email ou nom d'utilisateur (string)
- `password` (body) : Mot de passe (string)

**Réponse** : `200 OK`

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "expiresIn": 3600,
  "user": {
    "id": "01JDEXAMPLE123456789",
    "username": "john.doe@example.com",
    "firstName": "John",
    "lastName": "Doe"
  }
}
```

**Erreurs possibles** :

- `400 Bad Request` : Données manquantes ou invalides
  ```json
  {
    "timestamp": "2025-11-28T10:30:00Z",
    "status": 400,
    "error": "Bad Request",
    "message": "Validation failed",
    "path": "/api/auth/login",
    "validationErrors": {
      "username": "must not be blank",
      "password": "must not be blank"
    }
  }
  ```
- `401 Unauthorized` : Identifiants incorrects
  ```json
  {
    "timestamp": "2025-11-28T10:30:00Z",
    "status": 401,
    "error": "Unauthorized",
    "message": "Invalid username or password",
    "path": "/api/auth/login",
    "validationErrors": null
  }
  ```

### 6. Créer un utilisateur (Register)

```http
POST /api/clients/register
Content-Type: application/json

{
  "username": "john.doe@example.com",
  "password": "motdepasse123",
  "firstName": "John",
  "lastName": "Doe"
}
```

**Paramètres** :

- `username` (body) : Email ou nom d'utilisateur unique (string)
- `password` (body) : Mot de passe (min 8 caractères) (string)
- `firstName` (body) : Prénom (string)
- `lastName` (body) : Nom de famille (string)

**Réponse** : `201 CREATED`

```json
{
  "id": "01JDEXAMPLE123456789",
  "username": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "createdAt": "2025-11-28T10:30:00Z"
}
```

**Erreurs possibles** :

- `400 Bad Request` : Données invalides
  ```json
  {
    "timestamp": "2025-11-28T10:30:00Z",
    "status": 400,
    "error": "Bad Request",
    "message": "Validation failed",
    "path": "/api/clients/register",
    "validationErrors": {
      "username": "must be a valid email",
      "password": "must be at least 8 characters",
      "firstName": "must not be blank",
      "lastName": "must not be blank"
    }
  }
  ```
- `409 Conflict` : Utilisateur déjà existant
  ```json
  {
    "timestamp": "2025-11-28T10:30:00Z",
    "status": 409,
    "error": "Conflict",
    "message": "User already exists with username: john.doe@example.com",
    "path": "/api/clients/register",
    "validationErrors": null
  }
  ```

### Fichiers de test HTTP

Les requêtes sont disponibles dans `doc/api/client.http`, `doc/api/transaction.http` (création via le producer,
liste et flux SSE via le consumer) et `doc/api/batch.http` (import Spring Batch) pour une utilisation avec :

- **IntelliJ IDEA REST Client** (intégré à l'IDE)
- **VS Code REST Client** (extension)

## 🎯 Concepts clés pour le cours

### 1. Architecture Hexagonale

L'architecture hexagonale permet de :

- Isoler le domaine métier des détails techniques
- Tester le domaine indépendamment de l'infrastructure
- Changer facilement d'implémentation (ex: passer de InMemory à PostgreSQL/JPA)
- Respecter les principes SOLID (notamment le **DIP** - Dependency Inversion Principle)
- Favoriser la maintenabilité et l'évolutivité

### 2. Domain-Driven Design (DDD)

- **Entités** : `Client`, `Account` avec une identité unique
- **Value Objects** : `Client.Name`, `Client.Id`, `Amount` (immuables, comparés par valeur)
- **Use Cases** : Logique métier encapsulée (`CreateClient`, `ListClients`, `CreateAccount`, `ListAccounts`)
- **Repository Pattern** : Abstraction de la persistence (`ClientRepository`, `AccountRepository`)
- **Exceptions métier** : `ClientAlreadyExistsException` pour les règles métier violées

### 3. Patterns utilisés

- **Dependency Injection** : Via Spring (`@Bean`, `@Configuration`, `@Autowired`)
- **Repository Pattern** : Interface `ClientRepository` (port) avec implémentations (adapters)
- **DTO Pattern** : Séparation entre modèles du domaine (`Client`, `Account`) et représentations API (`ClientDTO`,
  `AccountDTO`)
- **Exception Handler Pattern** : Gestion centralisée avec `@RestControllerAdvice`
- **Adapter Pattern** : Controllers, Services, Repositories comme adaptateurs
- **Strategy Pattern** : Implémentations multiples possible du Repository

### 4. Records Java

Les records simplifient la création de classes immuables :

```java
public record Client(Id id, Name lastName, Name firstName) {
    // Validation dans le compact constructor
    public Client {
        Objects.requireNonNull(id, "id is required");
        // ...
    }
}
```

**Avantages** :

- Immutabilité par défaut
- `equals()`, `hashCode()`, `toString()` générés automatiquement
- Code plus concis et lisible
- Meilleur support pour les patterns matching (Java 16+)

### 5. Value Objects

Les Value Objects encapsulent des domaines de valeur avec :

- **Immuabilité** : Pas de setters, données finales
- **Validation** : Dans le constructeur
- **Comparaison par valeur** : Basée sur le contenu, pas la référence
- **Isolation** : Logique métier encapsulée

Exemple :

```java
public record Amount(long value) {
    public Amount {
        if (value < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
    }
}
```

## 🧪 Commandes utiles

### Build et compilation

```bash
# Compiler le projet
./gradlew build

# Compiler sans les tests
./gradlew build -x test

# Nettoyer les artifacts compilés
./gradlew clean
```

### Exécution

```bash
# Démarrer l'application principale (consumer) en mode développement
JWT_SECRET=localtest ./gradlew bootRun --args='--spring.profiles.active=consumer'

# Démarrer le producer en mode développement
./gradlew bootRun --args='--spring.profiles.active=producer'

# Démarrer l'application en production (JAR)
JWT_SECRET=... java -jar build/libs/mybank-0.0.1-SNAPSHOT.jar --spring.profiles.active=consumer
```

### Tests

```bash
# Exécuter tous les tests
./gradlew test

# Exécuter un test spécifique
./gradlew test --tests "com.example.mybank.DemoMyBankApplicationTests"
```

### Docker Compose

```bash
# Démarrer tous les services
docker-compose up -d

# Arrêter tous les services
docker-compose down

# Voir les logs
docker-compose logs -f

# Redémarrer un service spécifique
docker-compose restart database
```

## 🏆 Bonnes pratiques appliquées

✅ **Clean Code** : Noms explicites, petites fonctions, une responsabilité par classe  
✅ **SOLID Principles** : SRP, OCP, LSP, ISP, DIP  
✅ **Architecture propre** : Séparation couches, inversion des dépendances  
✅ **Immutabilité** : Records, Value Objects immuables  
✅ **Validation** : Règles métier centralisées dans le domaine  
✅ **Documentation** : Swagger/OpenAPI
✅ **Logging** : Logs structurés, niveaux appropriés  
✅ **Gestion d'erreurs** : Exceptions métier, handling centralisé

## 📚 Ressources

### Architecture et Design

- [Architecture Hexagonale - Alistair Cockburn](https://alistair.cockburn.us/hexagonal-architecture/)
- [Domain-Driven Design - Eric Evans](https://www.domainlanguage.com/ddd/)
- [Refactoring Guru - Design Patterns](https://refactoring.guru/design-patterns)

### Technologies Backend

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [Spring Security JWT](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)
- [SpringDoc OpenAPI](https://springdoc.org/)
- [ULID Specification](https://github.com/ulid/spec)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Liquibase Documentation](https://docs.liquibase.com/)

### Technologies Frontend

- [Alpine.js Documentation](https://alpinejs.dev/)
- [Alpine.js GitHub](https://github.com/alpinejs/alpine)
- [Pico CSS Documentation](https://picocss.com/)
- [MDN - Fetch API](https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API)

### Bonnes pratiques

- [The Twelve-Factor App](https://12factor.net/)
- [Clean Code - Robert C. Martin](https://www.oreilly.com/library/view/clean-code-a/9780136083238/)
- [Building Microservices - Sam Newman](https://samnewman.io/books/building_microservices_2nd_edition/)

## 📋 Checklist de démarrage pour les développeurs

- [ ] Java 25+ installé et configuré
- [ ] Gradle Wrapper fonctionne (`./gradlew --version`)
- [ ] Docker Desktop en cours d'exécution
- [ ] `docker-compose up -d` exécuté
- [ ] Applications lancées : `Run Consumer` (8080) et `Run Producer` (8081)
- [ ] Interface web accessible à http://localhost:8080/
- [ ] Swagger accessible à http://localhost:8080/swagger-ui.html
- [ ] Premier endpoint API testé (ex: GET /api/clients)
- [ ] **Authentification testée** (login/register fonctionnent)
- [ ] **Token JWT reçu** après connexion réussie
- [ ] Page des comptes testée avec un clientId valide
- [ ] Transaction créée via le producer et visible en direct sur la page des transactions
- [ ] Import CSV lancé via `POST /api/batch/import-transactions` (avec des `accountId` existants) et job `COMPLETED`
- [ ] Pages d'erreur testées (404, 500, etc.)
- [ ] Diagrammes Mermaid visibles dans le README
- [ ] Alpine.js fonctionne correctement (interactivité sur la page des comptes)
- [ ] IDE configuré avec les bons settings
- [ ] Gradle et dépendances synchronisées

---