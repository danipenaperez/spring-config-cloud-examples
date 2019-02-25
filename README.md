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

```
spring.cloud.config.server.git.uri=https://github.com/danipenaperez/spring-config-cloud-examples.git
spring.cloud.config.server.git.username=$yourusername
spring.cloud.config.server.git.password=$yourpasssss
#The location
spring.cloud.config.server.git.searchPaths=spring-cloud-config-server/configurationsFolder
#for basic authentication spring security
spring.security.user.name=root
spring.security.user.password=123456
```

After that, start it, and visit the already visited urls on our local Configuration Server:
```
GET http://root:123456@localhost:8888/myAppA/dev , you will get
{
  "name": "myAppA",
  "profiles": ["dev"],
  "label": null,
  "version": "d14b07e689f579a5a7d9e2113d978873a1cefa40",
  "state": null,
  "propertySources": [{
    "name": "https://github.com/danipenaperez/spring-config-cloud-examples.git/spring-cloud-config-server/configurationsFolder/myAppA-dev.properties",
    "source": {
      "grettingMessage": "hello from master"
    }
  }]
}
```
You can request using the master tag building the URL in this way 
http://root:123456@localhost:8888/${serviceName}/${profile}/${gitbranch}

```
GET http://root:123456@localhost:8888/myAppA/dev/master , you will get
{
  "name": "myAppA",
  "profiles": ["dev"],
  "label": "master",
  "version": "d14b07e689f579a5a7d9e2113d978873a1cefa40",
  "state": null,
  "propertySources": [{
    "name": "https://github.com/danipenaperez/spring-config-cloud-examples.git/spring-cloud-config-server/configurationsFolder/myAppA-dev.properties",
    "source": {
      "grettingMessage": "hello from master"
    }
  }]
}
```

Yeah , sounds good. As you can notice, we are getting the value of spring-cloud-config-server/configurationsFolder/{servicename}-{profile}.properties on our github repo, but in **master branch**.

Create new branch (git checkout -b version2) , change the values on properties and push the commit. And retry the requet using version2 branch name:

```
http://root:123456@localhost:8888/myAppA/dev/version2
{
  "name": "myAppA",
  "profiles": ["dev"],
  "label": "version2",
  "version": "b1b963a1e8fe8458d41a49bb786d68f3ad977872",
  "state": null,
  "propertySources": [{
    "name": "https://github.com/danipenaperez/spring-config-cloud-examples.git/spring-cloud-config-server/configurationsFolder/myAppA-dev.properties",
    "source": {
      "grettingMessage": "message from version2 "
    }
  }]
}
```
**Now you have a centralized server that obtain the values from a Versioning Repository.**


## REFRESHING PROPERTIES
##Refreshing properties from locally Folder
The Server has a watch mechanish, that if you change the value on some property file, the value will be updated quickly and served without restart.
Change the local property file with another value
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

## Client
The client must be notified if a property has changed on the Configuration server. The way to resolve this problem is to use spring-cloud-bus, to share events between different apps.





