# Property Manager

Web application leveraging Elasticsearch that allows a user to add/remove/modify a property and its attributes and to their associated price.

## Getting Started

Build the maven project with :

```
mvn clean install
```

Deploy war located in 'property-manager\property-manager-ui\target' to a servlet container (eg. Tomcat)

Go to url :

```
http://localhost:8080/property-manager-ui/properties
```

### Prerequisites

Start an empty instance of Elasticsearch 6.

Go to 'property-manager\property-manager-dal\src\main\resources\esconfig'

And post the template.json file with the following command

```
curl -XPOST 'http://localhost:9200/_template/property_template' -H
"Content-Type: application/json" -d @template.json
```

## License

This project is licensed under the Apache License V2 - see the [LICENSE.md](LICENSE.md) file for details
