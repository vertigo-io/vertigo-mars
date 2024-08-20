# Mars is the Vertigo demo app powered by Vertigo-ui.

Vertigo-UI is built over Vue.js, Quasar and SpringMVC.

In order to test it on your side, you might deploy all the used parts (ElasticSearch, Redis, PostgreSQL, ...) 
but the simplest way is to change its activeFlags from configuration.

1- Edit web.xml
```XML
<context-param>
  <param-name>boot.activeFlags</param-name>
  <param-value>klee</param-value>
</context-param>
```

2- Change param-value to `home`. With this flags all components switch to an local hostable version (base H2, memory cache, embedded ES, ...)

3- Change the value in the `mars.yaml` for `url: ${user.home}\mars\marsconf\marsApiKeys.properties`  
Here is a template propertie file
```properties
iftttApiUrl=http://localhost
iftttApiKey=0
```

4- Boot your app server

5- You’ll find logins in the `neutral/userAccounts.txt` file
adupont / VertigoMarsDemo

6- Enjoy !!
