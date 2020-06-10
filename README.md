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
     "event":{
        "type":"NEW_ADDRESS_CONFIRMED",
        "source":"ADDRESS_RESOLUTION",
        "channel":"AR",
        "dateTime":"2011-08-12T20:17:46.384Z",
        "transactionId":"c45de4dc-3c3b-11e9-b210-d663bd873d93"
     },
     "payload":{
        "newAddress":{
          "collectionCase" : {
              "id":"bbd55984-0dbf-4499-bfa7-0aa4228700e9",
              "caseType":"SPG",
              "survey":"CENSUS",
              "fieldCoordinatorId":"SO_23",
              "fieldOfficerId":"SO_23_123",
              "address":{
                  "addressLine1":"100",
                  "addressLine2":"Kanes caravan park",
                  "addressLine3":"fairoak road",
                  "townName":"southampton",
                  "postcode":"SO190PG",
                  "region":"E",
                  "addressType":"SPG",
                  "addressLevel":"U",
                  "estabType":"Residential Caravaner",
                  "latitude":"50.917428",
                  "longitude":"-1.238193",
                  "uprn":"123456789"
              },
              "oa":"",
              "lsoa":"",
              "msoa":"",
              "lad":"",
              "htcWillingness":"1",
              "htcDigital":"1",
              "treatmentCode":"SPG_QPHSE"
          }
        }
     }
  }
  ```
7. To run a test of the CSV loader open the home page: <http://localhost:8080>. The CSV should use the following format:

  |ARID|ESTAB_ARID|UPRN|ADDRESS_TYPE|ESTAB_TYPE|ADDRESS_LEVEL|ABP_CODE|ORGANISATION_NAME|ADDRESS_LINE1|ADDRESS_LINE2|ADDRESS_LINE3|TOWN_NAME|POSTCODE|LATITUDE|LONGITUDE|OA|LSOA|MSOA|LAD|REGION|HTC_WILLINGNESS|HTC_DIGITAL|TREATMENT_CODE|FIELDCOORDINATOR_ID|FIELDOFFICER_ID|CE_EXPECTED_CAPACITY|
  |---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
  |DDR200314000000009201|DDR200314000000009201|1|HH|Household|U|RD03||56 Some Avenue|||Townbury|AB12 3CD|51.4732839|-2.5219149|E00073888|E01014624|E02003029|E06000023|E12000009|2|2|HH_LFNR1E|TWH1-HA||0|
  |DDR200314000000009202|DDR200314000000009202|2|HH|Household|U|RD04||8 Some Street|||Townbury|AB12 3CD|51.4694158|-2.563189|E00073597|E01014569|E02006890|E06000023|E12000009|4|2|HH_LFNR2E|TWH1-HA||0|
