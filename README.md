# Campeggio Backend

Backend REST API per la gestione di un campeggio reale, sviluppato con **Spring Boot 3** e **PostgreSQL**.
Progetto finale del corso Backend Programming — EPICODE Institute of Technology.

---

## Panoramica del progetto

Il sistema gestisce tutte le operazioni di un campeggio:
- Autenticazione e autorizzazione degli utenti (JWT, 3 ruoli)
- Gestione degli alloggi: 10 chalet, 67 piazzole fisse, 7 piazzole stagionali, 5 camper stop
- Prenotazioni con calcolo automatico del prezzo
- Pagamenti online tramite **Stripe**
- Noleggio attrezzatura (biciclette, ombrelloni, sdraio...)
- Ordini bar con tracciamento stato
- Check-in e check-out degli ospiti
- Meteo in tempo reale con **OpenWeatherMap**
- Email automatiche con **SendGrid** (conferma prenotazione, reminder, scadenza contratti)
- Report amministrativi (occupazione, fatturato mensile)

---

## Stack tecnologico

| Componente | Tecnologia |
|---|---|
| Framework | Spring Boot 3.3.5 |
| Linguaggio | Java 21 |
| Database | PostgreSQL 16+ |
| ORM | Hibernate 6 / Spring Data JPA |
| Sicurezza | Spring Security + JWT (jjwt 0.12.6) |
| Build | Maven |
| API esterne | Stripe, SendGrid, OpenWeatherMap |

---

## Requisiti

- Java 21+
- PostgreSQL 16+ in esecuzione su `localhost:5432`
- (Opzionale) API key per Stripe, SendGrid, OpenWeatherMap

---

## Avvio rapido

### 1. Clona il repository

```bash
git clone <url-repository>
cd campeggio-backend
```

### 2. Crea il database

```sql
CREATE DATABASE campeggio_db;
```

### 3. Configura le variabili d'ambiente

Copia il file di esempio e compilalo:

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Modifica `application.properties` con le tue credenziali (vedi sezione [Variabili d'ambiente](#variabili-dambiente)).

### 4. Avvia l'applicazione

```bash
./mvnw spring-boot:run
```

oppure da IntelliJ IDEA: tasto destro su `CampeggioApplication` → **Run**.

L'applicazione si avvia su `http://localhost:8080`.

Al primo avvio, il `DataInitializer` crea automaticamente:
- Utenti di default (admin, staff, ospite di esempio)
- 89 alloggi (chalet, piazzole fisse, stagionali, camper stop)
- 10 articoli noleggio

---

## Variabili d'ambiente

| Variabile | Descrizione | Default |
|---|---|---|
| `DB_USERNAME` | Username PostgreSQL | `postgres` |
| `DB_PASSWORD` | Password PostgreSQL | `postgres` |
| `JWT_SECRET` | Chiave segreta JWT (min. 256 bit) | valore di sviluppo |
| `STRIPE_SECRET_KEY` | Chiave segreta Stripe | *(opzionale)* |
| `STRIPE_WEBHOOK_SECRET` | Segreto webhook Stripe | *(opzionale)* |
| `WEATHER_API_KEY` | API key OpenWeatherMap | *(opzionale, usa mock)* |
| `WEATHER_CITY` | Città per il meteo | `Castello Tesino,IT` |
| `SENDGRID_API_KEY` | API key SendGrid | *(opzionale, usa log)* |
| `SENDGRID_FROM_EMAIL` | Email mittente | `noreply@campeggio.it` |

> **Nota:** senza le API key esterne, l'applicazione funziona ugualmente — Stripe e SendGrid vengono simulati nel log, il meteo restituisce dati mock.

---

## Credenziali di default

| Ruolo | Email | Password |
|---|---|---|
| Admin | `admin@campeggio.it` | `Admin123!` |
| Staff (reception) | `reception@campeggio.it` | `Staff123!` |
| Staff (bar) | `bar@campeggio.it` | `Staff123!` |
| Ospite | `ospite@example.com` | `Ospite123!` |

---

## Architettura

Il progetto segue un'architettura **feature-based** (DDD-lite): ogni dominio di business ha il proprio package con entity, repository, service, controller e DTO.

```
src/main/java/com/campeggio/
├── auth/           # Registrazione e login
├── users/          # Gestione utenti (Admin, Staff, Ospite)
├── accommodations/ # Alloggi (Chalet, Piazzola, PiazzolaFissa)
├── reservations/   # Prenotazioni
├── payments/       # Pagamenti Stripe
├── rentals/        # Noleggio attrezzatura
├── bar/            # Ordini bar
├── checkins/       # Check-in / Check-out
├── weather/        # Meteo OpenWeatherMap
├── email/          # Email SendGrid
├── reports/        # Report admin
├── security/       # JWT filter e configurazione
├── exceptions/     # Gestione errori globale
└── config/         # WebClient, DataInitializer
```

### Modello dati

Il sistema include **2 gerarchie di ereditarietà JPA** con strategia `JOINED`:

**Utenti:** `User` (astratta) → `Admin`, `Staff`, `Ospite`

**Alloggi:** `Accommodation` (astratta) → `Bungalow` (chalet), `Piazzola`, `PiazzolaFissa`

---

## API REST

Tutti gli endpoint sono disponibili nella **Postman Collection** inclusa in `docs/postman/campeggio-api.postman_collection.json`.

### Autenticazione

| Metodo | Endpoint | Accesso | Descrizione |
|---|---|---|---|
| POST | `/api/auth/register` | Pubblico | Registra un nuovo ospite |
| POST | `/api/auth/login` | Pubblico | Login, restituisce JWT |

### Utenti

| Metodo | Endpoint | Accesso | Descrizione |
|---|---|---|---|
| GET | `/api/users/me` | Autenticato | Profilo utente corrente |
| PATCH | `/api/users/me/avatar` | Autenticato | Aggiorna avatar |
| GET | `/api/admin/users` | Admin | Lista tutti gli utenti |
| GET | `/api/admin/users/{id}` | Admin | Dettaglio utente |
| DELETE | `/api/admin/users/{id}` | Admin | Elimina utente |

### Alloggi

| Metodo | Endpoint | Accesso | Descrizione |
|---|---|---|---|
| GET | `/api/accommodations` | Pubblico | Lista tutti gli alloggi |
| GET | `/api/accommodations/{id}` | Pubblico | Dettaglio alloggio |
| GET | `/api/accommodations/available` | Pubblico | Ricerca disponibilità |
| POST | `/api/accommodations` | Admin | Crea alloggio |
| PUT | `/api/accommodations/{id}` | Admin | Aggiorna alloggio |
| PATCH | `/api/accommodations/{id}/status` | Admin/Staff | Cambia stato |
| DELETE | `/api/accommodations/{id}` | Admin | Elimina alloggio |

### Prenotazioni

| Metodo | Endpoint | Accesso | Descrizione |
|---|---|---|---|
| GET | `/api/reservations` | Autenticato | Lista prenotazioni (ospite: solo le proprie) |
| GET | `/api/reservations/{id}` | Autenticato | Dettaglio prenotazione |
| POST | `/api/reservations` | Ospite | Crea prenotazione |
| PATCH | `/api/reservations/{id}/confirm` | Admin/Staff | Conferma → invia email |
| PATCH | `/api/reservations/{id}/cancel` | Autenticato | Cancella prenotazione |

### Pagamenti

| Metodo | Endpoint | Accesso | Descrizione |
|---|---|---|---|
| POST | `/api/payments/intent/{prenotazioneId}` | Autenticato | Crea PaymentIntent Stripe |
| POST | `/api/payments/webhook` | Pubblico | Webhook Stripe |
| GET | `/api/payments/prenotazione/{id}` | Autenticato | Stato pagamento |

### Noleggi

| Metodo | Endpoint | Accesso | Descrizione |
|---|---|---|---|
| GET | `/api/rentals/items` | Pubblico | Catalogo articoli disponibili |
| GET | `/api/rentals` | Autenticato | Lista noleggi |
| POST | `/api/rentals` | Ospite | Crea noleggio |
| PATCH | `/api/rentals/{id}/return` | Admin/Staff | Registra restituzione |

### Bar

| Metodo | Endpoint | Accesso | Descrizione |
|---|---|---|---|
| GET | `/api/bar/orders` | Admin/Staff | Tutti gli ordini |
| GET | `/api/bar/orders/active` | Admin/Staff | Ordini attivi |
| GET | `/api/bar/orders/mine` | Ospite | I miei ordini |
| POST | `/api/bar/orders` | Ospite | Crea ordine |
| PATCH | `/api/bar/orders/{id}/status` | Admin/Staff | Aggiorna stato |
| PATCH | `/api/bar/orders/{id}/cancel` | Autenticato | Annulla ordine |

### Check-in / Check-out

| Metodo | Endpoint | Accesso | Descrizione |
|---|---|---|---|
| GET | `/api/checkins` | Admin/Staff | Lista registrazioni |
| POST | `/api/checkins/in` | Admin/Staff | Registra check-in |
| POST | `/api/checkins/out` | Admin/Staff | Registra check-out → segna COMPLETATA |

### Meteo

| Metodo | Endpoint | Accesso | Descrizione |
|---|---|---|---|
| GET | `/api/weather` | Pubblico | Meteo città di default (Castello Tesino) |
| GET | `/api/weather/{city}` | Pubblico | Meteo per città specifica |

### Report Admin

| Metodo | Endpoint | Accesso | Descrizione |
|---|---|---|---|
| GET | `/api/admin/reports/occupancy` | Admin | Tasso occupazione in un periodo |
| GET | `/api/admin/reports/revenue` | Admin | Fatturato per mese |

---

## Gestione degli errori

Tutti gli errori sono gestiti centralmente da `GlobalExceptionHandler` (`@RestControllerAdvice`) e restituiscono sempre un JSON strutturato:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Prenotazione non trovata con id: 99",
  "path": "/api/reservations/99",
  "timestamp": "2025-07-01T10:30:00"
}
```

| Eccezione | HTTP Status |
|---|---|
| `ResourceNotFoundException` | 404 |
| `ConflictException` | 409 |
| `UnauthorizedException` | 403 |
| `MethodArgumentNotValidException` | 400 |
| `Exception` (generica) | 500 |

---

## API di terze parti

### 1. OpenWeatherMap
Fornisce il meteo in tempo reale per la città del campeggio. Esposto su `GET /api/weather`. Se la API key non è configurata, restituisce dati mock.

### 2. Stripe
Gestisce i pagamenti online. Il flusso è:
1. Il server crea un `PaymentIntent` e restituisce il `clientSecret`
2. Il frontend completa il pagamento con Stripe.js
3. Stripe notifica l'esito tramite webhook su `/api/payments/webhook` (verificato con firma HMAC)

### 3. SendGrid
Invia email transazionali:
- Conferma prenotazione (al momento della conferma da parte dello staff)
- Reminder check-in (per integrazioni future)
- Avviso scadenza contratto piazzola fissa

Se la API key non è configurata, le email vengono simulate nel log applicativo.

---

## Test

Il progetto include unit test con **JUnit 5** e **Mockito**:

```
src/test/java/com/campeggio/
├── auth/AuthServiceTest.java             (4 test)
├── reservations/PrenotazioneServiceTest.java  (6 test)
└── accommodations/AccommodationServiceTest.java (8 test)
```

Per eseguire i test da IntelliJ: tasto destro su `src/test/java` → **Run All Tests**.

---

## Postman Collection

La collection completa si trova in:
```
docs/postman/campeggio-api.postman_collection.json
```

Importarla in Postman tramite drag & drop o File → Import. Il token JWT viene salvato automaticamente dopo login/register grazie agli script pre-configurati.

---

## Autore

Progetto sviluppato da **Michael Mezzanotte** per il corso Backend Programming — EPICODE Institute of Technology.
