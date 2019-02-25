# spring-cloud-config-examples
Playing with Spring Boot Cloud Config (server and client side)

## Getting Started

This is a spring Cloud config example proyect.

Have 2 modules, Server and Client.

### Server (spring-cloud-config-server)

This module acts as Centralized Configuration Server for different Applications 
myAppA  (develop and producction environmetns
myAppB  (develop and integration environtments

RUN
>mvn spring-boot:run

Will server properties from files stored in Folder ./configurationsFolder usign nomenclature (appName-profile.properties):
myAppA-dev.properties  
myAppA-prod.properties  
myAppB-dev.properties  
myAppB-int.properties

after run it locally you wiĺl be able to request this urls  http://root:123456@localhost:8888/${appName}/${profile}:
```
GET http://root:123456@localhost:8888/myAppA/dev
{
  "name": "myAppA",
  "profiles": ["dev"],
  "label": null,
  "version": null,
  "state": null,
  "propertySources": [{
    "name": "file:configurationsFolder/myAppA-dev.properties",
    "source": {
      "grettingMessage": "Value for MyAppA on Dev Environment!!"
    }
  }]
}
GET http://root:123456@localhost:8888/myAppA/prod
{
  "name": "myAppA",
  "profiles": ["prod"],
  "label": null,
  "version": null,
  "state": null,
  "propertySources": [{
    "name": "file:configurationsFolder/myAppA-prod.properties",
    "source": {
      "grettingMessage": "Value for MyAppA on Production Environment!!"
    }
  }]
}
http://root:123456@localhost:8888/myAppB/dev
{
  "name": "myAppB",
  "profiles": ["dev"],
  "label": null,
  "version": null,
  "state": null,
  "propertySources": [{
    "name": "file:configurationsFolder/myAppB-dev.properties",
    "source": {
      "grettingMessage": "Value for MyAppB on Develop Environment!!"
    }
  }]
}
http://root:123456@localhost:8888/myAppB/int
{
  "name": "myAppB",
  "profiles": ["int"],
  "label": null,
  "version": null,
  "state": null,
  "propertySources": [{
    "name": "file:configurationsFolder/myAppB-int.properties",
    "source": {
      "grettingMessage": "Value for MyAppB on Integration Environment!!"
    }
  }]
}
```
The main magic between this features is:
1. Add dependencies :
```	
	<dependency>
		<groupId>org.springframework.cloud</groupId>
		<artifactId>spring-cloud-config-server</artifactId>
	</dependency>
	<!-- It is optional, but it is a good practice, and this enable default Basic Security using properties 
			spring.security.user.name=root
			spring.security.user.password=123456
			without any code -->
	<dependency>
	    <groupId>org.springframework.boot</groupId>
	    <artifactId>spring-boot-starter-security</artifactId>
	</dependency>
```
2.The main class add the annotation @EnableConfigServer
```
	@SpringBootApplication
	@EnableConfigServer
	public class CentralizedConfigServerApplication {

		public static void main(String[] arguments) {
		SpringApplication.run(CentralizedConfigServerApplication.class, arguments);
	    }

	}
```
3.Define access to look for configurations, In this case will look for a LocalDirectory (would be better to use a git repository, in which you can store diferent property files versions, and so on..)
```
	#to use local directory it is a mus to use profile=native :-(
	spring.profiles.active=native    
	#Directory routes.
	spring.cloud.config.server.native.searchLocations=./configurationsFolder
```

### Client
On startup (bootstrap) the client will ask the configurationServer for properties (@see spring-cloud-context).

The main magic behind this behaviour is :
1.Dependencies in pom.xml

```
	<dependency>
		<groupId>org.springframework.cloud</groupId>
		<artifactId>spring-cloud-starter-config</artifactId>
	</dependency>

	<!--But also added this, to get a Rest Server to test the functionallity -->
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-web</artifactId>
	</dependency>
```

2.Create the bootstrap.properties (property file bind to this phase)

```
	spring.application.name=myAppA
	spring.profiles.active=dev
	spring.cloud.config.uri=http://localhost:8888
	spring.cloud.config.username=root
	spring.cloud.config.password=123456
```

Automatically the spring-cloud-starter-config look for this file (bootstrap.properties ) and use these properties to request the configuration server(@see spring-cloud-context).

3.Dont need anything more, but this is the main Starter Class:
```
	@SpringBootApplication
	@RestController
	public class SpringCloudConfigClientApplication {

		@Value(value = "${grettingMessage}")
		private String property;
		
		@RequestMapping("/")
	    public String home() {
	        return "On this environmet the value is "+property;
	    }
		
		public static void main(String[] args) {
			SpringApplication.run(SpringCloudConfigClientApplication.class, args);
		}

	}
```

Configure as Web Server with a Restcontroller on "/" that will serve a message built using the requested property "grettinMessage" obtained from centralized configuration Server.

After run it locally (ensure that the config server is up) you wiĺl be able to request this urls  

```
GET -> http://localhost:8080/	

response: On this environmet the value is Value for MyAppA on Dev Environment!!
```


## USING A GIT REPOSITORY
Lets try to use current repository, only have to change the config Server configuration on application.properties.



## REFRESHING PROPERTIES
The Server
Has a watch mechanish, that if you change the value on some property file, the value will be updated quickly and served without restart.

The client project request for properties after initialization, but if the configuration server change the properties values, the client needs to be notified, it is a problem.
Stop the Configuration server, and chage the file myApp-dev.properties, in this way (f.e):

```
grettingMessage=this site is offline
```
Does not require to restart the Configuration server, try the URL and you will see the new value. 

```
GET -> http://root:123456@localhost:8888/myAppA/dev

{
  "name": "myAppA",
  "profiles": ["dev"],
  "label": null,
  "version": null,
  "state": null,
  "propertySources": [{
    "name": "file:configurationsFolder/myAppA-dev.properties",
    "source": {
      "grettingMessage": "No this site is offline"
    }
  }]
}
```

but the client still have the same response on 


```
GET -> http://localhost:8080/	

response: On this environmet the value is Value for MyAppA on Dev Environment!!
```
The normal behaivour on refreshing properties using @RefreshScope only works on with application properties, not based on bootstrap cycle. So you need to deep into spring-cloud-bus dependency, in way that the main Configuration Server publish a message and all receivers will the event and ask for the new configuration.





