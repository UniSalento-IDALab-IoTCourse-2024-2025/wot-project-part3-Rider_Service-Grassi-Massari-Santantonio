# FastGo - Piattaforma di Delivery IoT & Blockchain

![Architettura del Sistema](https://github.com/UniSalento-IDALab-IoTCourse-2024-2025/wot-project-2024-2025-part1-Grassi-Massari-Santantonio/blob/main/Arc_semplice3.jpeg?raw=true)

## Panoramica del Progetto
FastGo è una piattaforma logistica e di food delivery di nuova generazione che integra tecnologie Internet of Things (IoT), Machine Learning e Blockchain per garantire l'integrità delle merci trasportate. A differenza dei servizi di consegna tradizionali, che si limitano a tracciare la posizione geografica del pacco, FastGo sposta il focus sulla **garanzia della qualità** del trasporto. Il sistema monitora attivamente le condizioni fisiche della spedizione (vibrazioni, urti, orientamento e temperatura) durante l'intero processo, utilizzando sensori embedded. Questi dati telemetrici vengono analizzati per calcolare un "Damage Score" (Punteggio di Danno), assicurando che i clienti ricevano i loro ordini in condizioni ottimali e fornendo prove tangibili in caso di deterioramento della merce.

Oltre al monitoraggio, la piattaforma introduce un innovativo livello di gamification trasparente: i corrieri (Rider) vengono valutati non tramite recensioni soggettive, ma sulla base della qualità oggettiva della loro guida e della cura nel trasporto. L'eccellenza operativa viene premiata attraverso certificati digitali immutabili (NFT) coniati sulla blockchain di Ethereum. Questo crea un sistema di reputazione "trustless", dove i Rider possono dimostrare professionalmente le proprie competenze e accedere a livelli di servizio superiori, mentre i commercianti e i clienti ottengono una trasparenza senza precedenti sulla filiera distributiva.

## Architettura del Sistema
L'ecosistema FastGo è costruito su una **Architettura a Microservizi** modulare, progettata per garantire scalabilità, tolleranza ai guasti e una netta separazione delle responsabilità. L'infrastruttura backend è composta da cinque servizi core sviluppati in **Spring Boot** (Auth, Client, Rider, Shop e Blockchain), ognuno dei quali gestisce il proprio database **MongoDB** dedicato, aderendo rigorosamente al pattern architetturale *Database-per-Service* per assicurare il disaccoppiamento dei dati.

La comunicazione tra i servizi sfrutta un approccio di messaggistica ibrido e resiliente:
1.  **RabbitMQ (AMQP):** Gestisce la sincronizzazione asincrona dei dati tra i microservizi e l'orchestrazione dei processi in stile RPC (Remote Procedure Call), garantendo la coerenza eventuale dell'intero sistema distribuito anche in caso di picchi di carico.
2.  **Mosquitto (MQTT):** Gestisce i flussi di dati provenienti dai dispositivi IoT e invia aggiornamenti di stato in tempo reale alle interfacce frontend tramite WebSockets, permettendo un tracking fluido e reattivo.

Il livello IoT è costituito dal dispositivo **ST Microelectronics SensorTile Box Pro**, controllato da un **RaspberryPi 5** tramite un firmware custom in C++. Questi dispositivi operano nell'edge, acquisendo dati ambientali e inerziali che vengono trasmessi via Bluetooth Low Energy (BLE) all'applicazione mobile del Rider. I dati grezzi vengono poi processati da un **Motore di Inferenza dedicato in Python** a bordo dello stesso RaspberryPi 5, che utilizza un modello Random Forest pre-addestrato per classificare eventi critici di trasporto (come cadute, impatti o ribaltamenti) e calcolare le metriche di stabilità termica.

Infine, il **Web3 Gateway** agisce come ponte sicuro verso la tecnologia decentralizzata, interagendo con la testnet **Ethereum Sepolia** tramite la libreria **Web3j**. Questo modulo gestisce l'esecuzione degli Smart Contracts per la notarizzazione degli ordini (rendendo i log di consegna immutabili) e per la distribuzione dei Badge ERC-721, i cui metadati sono ancorati in modo permanente su **IPFS** tramite Pinata. L'esperienza utente è erogata attraverso una dashboard web in React per i clienti, i rider e i negozianti, e un'applicazione mobile cross-platform in React Native che permette ai clienti di ordinare e ai rider di gestire le consegne.

### Schema Tecnico dei Flussi Dati
![Diagramma Tecnico Microservizi e IoT](https://github.com/UniSalento-IDALab-IoTCourse-2024-2025/wot-project-2024-2025-part1-Grassi-Massari-Santantonio/blob/main/Archittetura.png?raw=true)


## Ecosistema FastGo - Progetti Correlati

Di seguito la lista completa dei repository che compongono il sistema IoT FastGo.

### Backend & Infrastruttura
* [**Auth Service**](https://github.com/UniSalento-IDALab-IoTCourse-2024-2025/wot-project-part1-Auth_Service-Grassi-Massari-Santantonio) - Gestisce registrazione, login (JWT) e sincronizzazione utenti.
* [**Client Service**](https://github.com/UniSalento-IDALab-IoTCourse-2024-2025/wot-project-part2-Client_Service-Grassi-Massari-Santantonio) - Gestisce i profili dei clienti e lo storico ordini.
* [**Rider Service**](https://github.com/UniSalento-IDALab-IoTCourse-2024-2025/wot-project-part3-Rider_Service-Grassi-Massari-Santantonio) - Gestisce i corrieri, la geolocalizzazione e l'assegnazione ordini.
* [**Shop Service**](https://github.com/UniSalento-IDALab-IoTCourse-2024-2025/wot-project-part5-Shop_Service-Grassi-Massari-Santantonio) - Gestisce i ristoranti, i menu e il ciclo di vita dell'ordine.
* [**Message Broker**](https://github.com/UniSalento-IDALab-IoTCourse-2024-2025/wot-project-part4-Message_Broker-Grassi-Massari-Santantonio) - Configurazione Docker per RabbitMQ e Mosquitto (MQTT).

### Frontend & Mobile
* [**Frontend Web**](https://github.com/UniSalento-IDALab-IoTCourse-2024-2025/wot-project-part8-Frontend-Grassi-Massari-Santantonio) - Dashboard React per Amministratori, Ristoratori e Clienti.
* [**App Rider**](https://github.com/UniSalento-IDALab-IoTCourse-2024-2025/wot-project-part15-App_Rider-rassi-Massari-Santantonio) - App mobile per corrieri con connessione BLE al sensore e gestione consegne.
* [**App User**](https://github.com/UniSalento-IDALab-IoTCourse-2024-2025/wot-project-part15-App_Rider-Grassi-Massari-Santantonio) - App mobile per clienti per ordinare e tracciare le consegne in tempo reale.

### IoT, AI & Sensori
* [**Sensor Tile Firmware**](https://github.com/UniSalento-IDALab-IoTCourse-2024-2025/wot-project-part14-Sensor_Tile-Grassi-Massari-Santantonio) - Codice C++ per l'acquisizione dati dal dispositivo SensorTile Box Pro.
* [**Bluetooth Gateway**](https://github.com/UniSalento-IDALab-IoTCourse-2024-2025/wot-project-part11-Bluetooth-Grassi-Massari-Santantonio) - Servizio Python per interfacciare il sensore BLE con il cloud tramite MQTT.
* [**Inference Engine**](https://github.com/UniSalento-IDALab-IoTCourse-2024-2025/wot-project-part12-Inference-Grassi-Massari-Santantonio) - Modulo di analisi dati per valutare la qualità del trasporto.
* [**AI Training**](https://github.com/UniSalento-IDALab-IoTCourse-2024-2025/wot-project-part13-Training-Grassi-Massari-Santantonio) - Script per la generazione del dataset e l'addestramento del modello ML.

### Blockchain & Web3
* [**Blockchain Service**](https://github.com/UniSalento-IDALab-IoTCourse-2024-2025/wot-project-part7-BlockchainService-Grassi-Massari-Santantonio) - Gateway Java/Web3j per la notarizzazione e gestione NFT.
* [**Smart Contract: Tracking**](https://github.com/UniSalento-IDALab-IoTCourse-2024-2025/wot-project-part10-Contract_Blockchain-Grassi-Massari-Santantonio) - Contratto Solidity per la registrazione immutabile degli ordini.
* [**Smart Contract: NFT**](https://github.com/UniSalento-IDALab-IoTCourse-2024-2025/wot-project-part9-Contract_NFT-Grassi-Massari-Santantonio) - Contratto ERC-721 per la gestione dei Badge premio per i rider.

[**Project Presentation Page**](https://unisalento-idalab-iotcourse-2024-2025.github.io/wot-project-2024-2025-part1-Grassi-Massari-Santantonio/)



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

