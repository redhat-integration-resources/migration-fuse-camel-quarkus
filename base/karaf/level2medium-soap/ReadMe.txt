Dependencies

Download:
 >  jboss-fuse-karaf-6.3.0.redhat-187.zip

Install dependencies:
features:install fabric-hawtio-swagger
features:install camel-swagger
features:install camel-netty-http
features:install camel-http
features:install camel-jackson
features:install camel-jacksonxml


## Running JUnits

The service includes a JUnit to showcase how unit testing can be implemented for Camel Quarkus implementations.

Run the JUnits with the command below:

```
mvn clean test
```

## Running the service locally

>**Note**: stop Fuse if running, otherwise they will enter in conflict. 

>**Note**: the stubs need to be up and running for a successful end-to-end execution. Refer to the stub's Readme doc for instructions to run it. The configuration properties in the `blueprint.xml` also need to match the stubs ports. 

Run the service locally by executing the command below:

```
mvn clean camel:run
```


## Test with cURL

You can send a `POST` request with the following `curl` command:

```
curl \
-H "content-type: application/json" \
-d '{"id":"123"}' \
http://localhost:20000/camel/subscriber/details
```


## Deploying in Fuse

First, build and install the project in your local Maven: 
```
mvn clean install -DskipTests=true
```

Then, follow the steps below to prepare a Fuse instance.

Download:
 >  jboss-fuse-karaf-6.3.0.redhat-187.zip

Choose where to unzip (install) Fuse and run:
```
unzip jboss-fuse-karaf-6.3.0.redhat-187.zip
```

Run Fuse with the following command:
```
cd jboss-fuse-6.3.0.redhat-187/bin
./fuse
```

From the Fuse console, run the commands below to install dependencies:
```
features:install fabric-hawtio-swagger
features:install camel-swagger
features:install camel-netty-http
features:install camel-http
features:install camel-jackson
features:install camel-jacksonxml

```

Now, edit and run (in Fuse's console) the commands below to configure the service:
```
config:edit demo.medium
config:propset rest.host localhost
config:propset rest.port 20000
config:propset api.backend1.host localhost:10000
config:propset api.backend1.path /camel/subscriber/details
config:propset api.backend2.host localhost:9000
config:update

```

Now, from the Fuse console, run the following command to install the service:
```
install -s mvn:org.demo.karaf/level2medium-soap/1.0.0
```

Check all bundles are correctly started:
```
list
```

You should see in Fuse the service successfully started:
````
[ 305] [Active     ] [Created     ] [       ] [   80] Medium service (1.0.0)
````


## Test the service from Swagger

Open from a browser tab the following URL:
 - http://localhost:8181/hawtio-swagger

In Swagger, Explore the following URL:
 	> http://localhost:8181/rest/api-docs/subscriber

You should see in Fuse the service successfully started:
````
{"id":"123"} 
```


MIGRATION:

Java:
  > Define the CXF Soap endpoint.

OpenApi:
  > Create Definition (to replace CAMEL's DSL)

Camel Routes:
  > Copy routes over
 <> replace attribute [headerName]   for [name]
 <> replace attribute [propertyName] for [name]
 <> update [direct:json2soap]   (Camel 4 uses XSLT 3.0)
 <> update [direct:mapResponse] (Camel 4 uses XSLT 3.0)

WSDL:
  > Copy Definition

XSLTs:
  > common/j2x.xsl:  copy JSON/XML transformation rule
  > request.xsl:     copy mapping and update XPATH expressions (Camel 4 uses XSLT 3.0)
  > response.xsl:
      - copy/update input variables (Camel 4 uses XSLT 3.0)
      - copy client data mapping
      - copy subscriptions data mapping
      - copy/update XML/JSON serialisation (Camel 4 uses XSLT 3.0)

JUnit:
  > add reconfigureEndpoints()
  > add Camel routes for JUnit in RouteBuilder
  > copy test unit testServiceGetDetails
  > update rest entrypoint address 
 <> update assertions (move message from first to last argument)

SUMMARY:

Blueprint:
 <  remove all beans
 >  add Java bean for CXF endpoint
 <  remove REST DSL
 >  add OpenApi specification
 <> replace node <CamelContext> for <Routes>
 <> replace attribute [headerName]   for [name]
 <> replace attribute [propertyName] for [name]
 <> update [direct:json2soap]   (Camel 4 uses XSLT 3.0)
 <> update [direct:mapResponse] (Camel 4 uses XSLT 3.0)
 <  remove [direct:json2xml] (out-of-the-box in XML 3.0)
 <  remove [direct:xml2json] (out-of-the-box in XML 3.0)
 <  remove Saxon's Java custom extension

JUnits
 <  remove getBlueprintDescriptor()
 <  remove getRandomPort()
 <  remove setConfigAdminInitialConfiguration()
 >  add    reconfigureEndpoints()
 >  add    invocation to reconfigureEndpoints() from RouteBuilder
 <> update assertions (move message from first to last argument)




