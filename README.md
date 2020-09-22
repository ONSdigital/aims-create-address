# aims-create-address

An API for loading addresses into an Elasticsearch index. Addresses can be loaded by either a GCP PubSub message or a CSV file. The API is written in Java and uses the Spring Boot Reactive stack.

### Running the Create API Locally

1. Clone the [Parser API](https://github.com/ONSdigital/aims-address-parser) and this repo.
2. Run a local instance of Elasticsearch.
3. Run a local instance of the parser. It will default to <http://localhost:8081>
4. Create a PubSub Topic and Subscription. You can either [emulate PubSub](https://cloud.google.com/pubsub/docs/emulator) or create a Topic and Subscription in your own GCP project.
   - Call the topic `new-addresses`.
   - Call the new subscription `new-address-subscription` and keep the default settings. A dead letter topic can also be setup but is not necessary for testing.
5. Update the `spring.cloud.gcp.project-id` attribute in the `application.yml` file to the GCP project you created the PubSub topic in.
6. Run the Spring Boot app and send a test message. The PubSub message should use the following format:

  ```json
    {
      "event": {
        "type": "NEW_ADDRESS_ENHANCED",
        "source": "CASE_PROCESSOR",
        "channel": "RM",
        "dateTime": "2020-08-05T12:11:54.008964Z",
        "transactionId": "d9126d67-2830-4aac-8e52-47fb8f84d3b9"
      },
      "payload": {
        "newAddress": {
          "sourceCaseId": null,
          "collectionCase": {
            "id": "1d63aacf-a89e-4d9f-96e4-9b31fdb6f5ec",
            "caseType": "SPG",
            "survey": "CENSUS",
            "address": {
              "addressLine1": "123",
              "addressLine2": "Fake caravan park",
              "addressLine3": "The long road",
              "townName": "Trumpton",
              "postcode": "SO190PG",
              "region": "E00001234",
              "latitude": "50.917428",
              "longitude": "-1.238193",
              "uprn": "9998397697247",
              "estabUprn": null,
              "abpCode": null,
              "addressType": "SPG",
              "addressLevel": "U",
              "estabType": null,
              "organisationName": null
            },
            "handDelivery": false,
            "skeleton": false,
            "surveyLaunched": false
          }
        }
      }
    }
  ```
7. To run a test of the CSV loader open the home page: <http://localhost:8080>. The CSV should use the following format:

  |UPRN|ESTAB_UPRN|ADDRESS_TYPE|ESTAB_TYPE|ADDRESS_LEVEL|ABP_CODE|ORGANISATION_NAME|ADDRESS_LINE1|ADDRESS_LINE2|ADDRESS_LINE3|TOWN_NAME|POSTCODE|LATITUDE|LONGITUDE|OA|LSOA|MSOA|LAD|REGION|HTC_WILLINGNESS|HTC_DIGITAL|TREATMENT_CODE|FIELDCOORDINATOR_ID|FIELDOFFICER_ID|CE_EXPECTED_CAPACITY|CE_SECURE|PRINT_BATCH|
  |---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
  |1|1|HH|Household|U|RD03||56 Some Avenue|||Townbury|AB12 3CD|51.4732839|-2.5219149|E00073888|E01014624|E02003029|E06000023|E12000009|2|2|HH_LFNR1E|TWH1-HA||0|1||
  |2|2|HH|Household|U|RD04||8 Some Street|||Townbury|AB12 3CD|51.4694158|-2.563189|E00073597|E01014569|E02006890|E06000023|E12000009|4|2|HH_LFNR2E|TWH1-HA||0|0||

### Running the Create API Locally with Docker

It is possible to run the entire suite of apps required for the create API in a local environment using the `docker-compose` script included.
The script uses a PubSub emulator as described in [this](https://github.com/marcelcorso/gcloud-pubsub-emulator) GitHub repo. It also uses this [wait-for-it](https://github.com/vishnubob/wait-for-it) script before deploying dependent apps. The Elasticsearch deployment uses volumes to persist data over restarts and allows snapshots to be created. You will have to create the Address Index app images locally:

```
	docker build --build-arg JAR_FILE=build/libs/*.jar -t address-index-parser .
	docker build --build-arg JAR_FILE=build/libs/*.jar -t address-index-create-api .
	docker build --build-arg JAR_FILE=build/libs/*.jar -t address-index-pubsub-publisher .
```
The `docker-compose` script will deploy the following apps:

|App|Access|
|---|---|
|[Elasticsearch 7.3.1](https://www.elastic.co/guide/en/elasticsearch/reference/7.3/release-notes-7.3.1.html)|Access via Cerebro or Kibana.|
|[Cerebro](https://github.com/lmenezes/cerebro)|<http://localhost:1234> and then <http://es:9200>|
|[Kibana 7.3.1](https://www.elastic.co/guide/en/kibana/7.3/release-notes-7.3.1.html)|<http://localhost:5601>|
|[address-index-parser](https://github.com/ONSdigital/aims-address-parser)|<http://localhost:8081/tokens?address=Acme%20Flowers%20Ltd%20First%20And%20Second%20Floor%20Flat%2039b%20Cranbrook%20Road%20Windleybury%20GU166DE>|
|address-index-create-api|<http://localhost:8080/>|
|[address-index-pubsub-publisher](https://github.com/ONSdigital/aims-pubsub-publisher)|<http://localhost:8082/>|
|[pubsub-emulator](https://github.com/marcelcorso/gcloud-pubsub-emulator)|<http://localhost:8681/>|
