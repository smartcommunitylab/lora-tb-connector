# lora-tb-connector
Lora to ThingsBoard connector.

This component performs a "lightway" integration between one specific **Lora App Server's user**, and a specific **ThingsBoard tenant user**.
The integration allows to:
* create in ThingsBoard the devices defined into the Lora Server
* send the uplink application payload retrived from Lora devices to the corrisponding ThingsBoard devices

## 1. Installation Requirements
* Java 1.8+
* MongodDB 3.4+
* Apache Maven (3.0+)

## 2. Configuration
The component configuration is stored in `src\main\resources\application.properties` file.
In this file you can set several properties:
* **spring.data.mongodb** : the MongoDB configuration (host, port, db name)
* **tb** : the ThingsBoard instance configuration
  * **endpoint** : the ThingsBoard installation endpoint (for example http://localhost:8080/)
  * **user** : a tenant user (tenant@thingsboard.org in the demo dataset)
  * **password** : the tenant user password (tenat in the demo dataset)
* **lora** : the Lora server configuration
  * **endpoint** : the Lora Serve App endpoint 
  * **user** : the Lora user
  * **mqtt.endpoint** : the user password
  * **mqtt.topic** : the topic for MQTT message listening (`#` is a wildcard for every messages) 
  * **mqtt.threads** : number of threads that can manage the messages dispatcing


## 3. Execution
The component is a Java Spring Boot app. You can use Maven tool in order to compile and run the application.
To compile the application run the Maven install command from the project root folder

`mvn install`

To run the application, run the jar that you can find in the target folder.

`java -jar lora-tb-connector-<ver>.jar`
