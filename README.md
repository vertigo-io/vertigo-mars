# Mars is the Vertigo demo app powered by Vertigo-ui.

Vertigo-UI is built over Vue.js, Quasar and SpringMVC.

To test it on your side, you might deploy all the required components (ElasticSearch, Redis, PostgreSQL, ...) , but the simplest way is to use the 'home' flag (default value), which uses an embedded Elasticsearch, a local database (h2), and stores cache in memory.

Secrets and API keys are not committed to this repository, so you have to create the following file : `${user.home}\mars\marsconf\marsApiKeys.properties` (referenced from `mars.yaml`).
Here is a template property file :

```properties
iftttApiUrl=http://localhost
iftttApiKey=0
openAiApiKey=demo
```

To boot the server, you can use Tomcat or boot directly from `BootMarsDev`, which uses an embedded Jetty server.

You’ll find logins in the `neutral/userAccounts.txt` file, for example :

```
adupont / VertigoMarsDemo
```
