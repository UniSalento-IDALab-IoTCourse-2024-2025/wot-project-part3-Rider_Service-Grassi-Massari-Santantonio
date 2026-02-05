# FastGo Rider Service

Questo repository contiene il microservizio dedicato alla gestione dei corrieri (Rider) della piattaforma FastGo. Il servizio è sviluppato in Java Spring Boot e si occupa della gestione dei profili rider, della geolocalizzazione degli ordini disponibili e del flusso di accettazione e consegna tramite comunicazione asincrona e RPC con RabbitMQ.

## Struttura del Progetto

.
├── src/main/java/com/fastgo/rider/fastgo_rider/
│   ├── component/          # Listener RabbitMQ (OrderListener, SyncListener)
│   ├── config/             # Configurazione RabbitMQ e Security
│   ├── service/            # Logica di business (Geocoding, Distanze, Gestione Ordini)
│   ├── domain/             # Entità MongoDB (Orders, Rider, Vehicle)
│   ├── repositories/       # Interfacce MongoRepository
│   ├── restControllers/    # Endpoint API per l'App Mobile del Rider
│   └── dto/                # Data Transfer Objects
├── src/main/resources/
│   └── application.properties # Configurazione applicativa
├── docker-compose.yml      # Orchestrazione container (App + MongoDB)
└── build.gradle            # Gestione dipendenze Gradle

## Prerequisiti

* Java JDK 21 (basato sull'immagine Docker `eclipse-temurin:21-jdk`)
* MongoDB
* RabbitMQ

## Configurazione

Il servizio richiede la configurazione delle seguenti proprietà nel file `src/main/resources/application.properties`:

1. Database (MongoDB):
   spring.data.mongodb.uri=mongodb://riderdb:27017/rider

2. RabbitMQ (Messaging):
   spring.rabbitmq.host=172.31.12.119 (o IP container)
   spring.rabbitmq.port=5672
   spring.rabbitmq.username=guest
   spring.rabbitmq.password=guest

## Compilazione e Avvio

### Metodo 1: Tramite Gradle (Locale)

1. Pulizia e build del progetto:
   ./gradlew clean build

2. Avvio dell'applicazione:
   ./gradlew bootRun

### Metodo 2: Tramite Docker Compose

Il file `docker-compose.yml` incluso avvia sia il microservizio che un'istanza dedicata di MongoDB.

1. Generare il file JAR:
   ./gradlew clean build

2. Avviare i container:
   docker-compose up -d


## Funzionalità Principali

### 1. Gestione Ordini Basata sulla Posizione
Il servizio implementa un algoritmo intelligente (`OrderService.getOrdersByPosition`) che filtra gli ordini disponibili in base alla posizione attuale del rider e al suo mezzo di trasporto.
* Calcolo distanza: Formula di Haversine.
* Raggi di azione:
  * Bici: max 5km (Rider-Negozio) / 10km (Negozio-Cliente)
  * Moto: max 10km / 10km
  * Auto: max 15km / 25km

### 2. Geocoding
Integrazione con **Nominatim (OpenStreetMap)** tramite `GeoCodingService` per convertire gli indirizzi testuali in coordinate (Latitudine/Longitudine) necessarie per il calcolo delle distanze.

### 3. Gestione Stati e RPC
Il servizio utilizza RabbitMQ in modalità RPC (Remote Procedure Call) per garantire la coerenza transazionale distribuita:
* Accettazione Ordine: Il rider invia richiesta di accettazione -> RabbitMQ -> Shop Service -> Risposta (OK/FAIL).
* Aggiornamento Stato: (DELIVERING -> COMPLETED).

## API Endpoints

### Gestione Ordini (/order)
* POST /order/getByPosition
  Restituisce gli ordini disponibili nelle vicinanze in base alle coordinate GPS inviate.
* GET /order/active
  Restituisce l'ordine attualmente in carico al rider.
* GET /order/history
  Restituisce lo storico delle consegne effettuate.
* POST /order/updateStatus
  Permette al rider di aggiornare lo stato (es. "In Consegna", "Consegnato").

### Gestione Rider (/rider)
* POST /rider/accept
  Endpoint per accettare un ordine specifico.
* GET /rider/picture
  Recupera l'URL della foto profilo del rider.

## Integrazione Messaging

* **OrderListener:** Ascolta la creazione di nuovi ordini dal sistema Shop e li salva localmente con le coordinate calcolate.
* **SyncListener:** Ascolta la creazione di nuovi profili Rider dal servizio di Autenticazione e crea l'anagrafica locale.