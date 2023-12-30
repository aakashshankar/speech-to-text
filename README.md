# Medical dictation transcriber - Server
## Prerequisites
1. JDK 17 or greater
2. Gradle 8 or greater

<strong>Preferably, open this project in IntelliJ Idea, which autodetects the Gradle project structure.
</strong>

## Instructions
This server is hosted on [Railway](https://railway.app/). To run locally, a few environment variables need to be set:
1. `OPENAI_API_KEY`: The API key for OpenAI.
2. `DB_URL`: The URL for the postgres database hosted on [Supabase](https://supabase.com).
3. `DB_PASSWORD`: The password for the postgres database.

For convenience, I have already hardcoded these values in my application.yml file, and the Railway deployment. 
Since these are sensitive values, they will be passed dynamically via the command line.
4. Open the project in the IDE, and navigate to the `core` folder
5. Run the following command:

For MacOS / Linux
```shell
./gradlew bootRun
```

For Windows
```shell
gradlew.bat bootRun
```