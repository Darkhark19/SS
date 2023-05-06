# Software Security project
Academic project for Software Security class.
## Authors
* Diogo Ye
* Tiago Vieira 57719

## Run instructions:
1. Change the `LOCAL_PATH` constant in the `src/authenticator/AuthenticatorClass.java` to the absolute path of the directory where you cloned the project.
2. `mkdir ./web/WEB-INF/classes`
3. Compile classes by running the following command:
```javac -cp "lib/*" -d "web/WEB-INF/classes" @paths.txt```
4. `cp -R ./lib ./web/WEB-INF/`
5. Copy the the `/web` folder to your Tomcat webapps directory.
6. Run Tomcat.

The pre-defined password for root user is `1234`.
