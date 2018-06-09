# FTOOP Semesterarbeit: Dame Spiel
## Beschreibung
In diesem Projekt wurde ein komplettes [Dame-Spiel](https://de.wikipedia.org/wiki/Dame_(Spiel)) implementiert.
Es hat folgende Features:
* Grafische Benutzeroberfläche mit JavaFX (mit eigenen Grafiken)
* Computergegner (Multithreaded)
* Konfigurierbare Spielparameter, darunter Spielfeldgrösse (es kann zu Internationaler Dame gewechselt werden) und Spielregen (Compulsory Jump Rule und mehr).
* Komplette JUnit-Tests aller Spielregeln, Integrationstest der ein kompletes Spiel gegen sich selbst spielt
* Dokumentation der öffentlichen API der Spiel-Engine
* Konfigurierbares Logging (automatische Log-Rotation, Logging in ein File)

### Konfigurierbare und implemntierte Spielregeln
* Der Spieler darf nur eigene Steine spielen (keine des Gegners)
* Der Spieler muss bei seinem Zug einen Stein spielen, er darf nicht aussetzen
* Der Spieler darf nur Steine die noch auf dem Feld sind spielen, gewonnene Steine dürfen nicht mehr gespielt werden
* Der Spieler darf Steine nur auf den Dunklen Feldern plazieren
* Der Spieler darf Steine nur auf freie Felder plazieren
* Der Spieler muss den Stein immer vorwärts, sprich in die Richtung des Gegners spielen (ausser wenn er eine Dame hat)
* Der Spieler muss den Stein immer diagonal spielen, vertikale oder horizontale Züge sind nicht erlaubt
* Der Spieler darf den Stein nur eine bestimmte Distanz spielen, es darf z.B. nur eine Position weiterrücken, nicht quer über das Feld
* Der Spieler darf nur über gegnerische Steine springen
* Erweitert: Der Spieler muss immer über einen gegnerischen Stein springen, wenn dieser ein Nachbar ist; es darf nicht "ausgewichen" werden (Compulsory Jump Rule)
* Erweitert: Der Spieler kann Doppelsprünge machen, bei dem mehrere gegnerische Steine übernommen werden können

Die Spielregeln können im File `game.properties` konfiguriert werden.

## Projektstruktur
Das Projekt verwendet [Gradle](https://gradle.org/) um Tasks wie das Kompilieren des Java-Codes, das Ausführen von JUnit-Tests oder das generieren von ausführbaren JAR-Files zu automatisieren ohne dass dafür Eclipse verwendet werden muss.
Aufgebaut ist es nach dem [Standard Directory-Layout](https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html).
```
.
├── README.md             --> Diese Datei; Kurze Projektdokumentation
├── build                
│   └── ...               --> Kompilierte Artifakte (JAR-Files etc)
├── build.gradle          --> Gradle Task-Konfiguration und Plugins
├── gradle
│   └── ...               --> Gradle Installation
├── gradlew               --> Gradle Wrapper Script (*nix, macOS)
├── gradlew.bat           --> Gradle Wrapper Script (Windows)
├── gradlew.properties    --> Gradle Einstellungen und Projekt-Meta-Informationen
├── lib
│   └── ...               --> Libraries, die nicht aus dem Internet geladen werden können (student.jar)
├── settings.gradle       --> Gradle Modul-Build-Einstellungen (nur Multi-Modul-Projekte)
└── src
    ├── main
    │   ├── java          --> Java Produktionscode
    │   └── resources     --> Ressourcen für Produktionscode
    └── test
        ├── java          --> JUnit Test Code
        └── resources     --> Ressourcen für Unit-Tests
```
## How To
Um mit der Gradle Installation des Projekts zu interagieren, werden Wrapper-Scripts verwendet. Diese sind jeweils für Windows (`gradlew.bat`) und für *nix, macOS (`gradlew`) verfügbar. 
Nachfolgende Befehle verwenden aus Konsistenzgründen das *nix-Script; auf einem Windows-System kann jedoch einfach der `gradlew`-Teil mit `gradlew.bat` ersetzt werden. 

### Eclipse Projekt erstellen
Da Gradle nicht das Eclipse-spezifische Projektformat verwendet, muss zuerst ein Eclipse-Projekt generiert werden, damit sich das Projekt einfacher in der IDE öffnen lässt.
```
./gradlew eclipse
```

Anschliessend kann das Projekt in Eclipse über `File -> Import… -> Existing Projects into Workspace` importiert werden.

### All-in-One: Projekt "bauen"
```
./gradlew build
```

Dieser Befehl führt verschiedene Tasks aus: Kompilieren des Java-Codes, Ausführen der JUnit-Tests sowie Generierung des Build-Artifakts (JAR File). 

### JUnit-Tests ausführen
```
./gradlew test
```

### Code Coverage messen
```
./gradlew jacocoTestReport

```

Die Ergebnisse können in einem Test-Report angesehen werden: [`./build/reports/jacoco/test/html/index.html`](./build/reports/jacoco/test/html/index.html).

### Ausführbares JAR-File erstellen
```
./gradlew jar
```

Das ausführbare JAR-File befindet sich dann in `build/libs/<projekt-name>-<version>.jar`.

### Javadoc Dokumentation generieren
```
./gradlew javadoc
```
Die generierte Dokumnetation befindet sich in: [`build/docs/javadoc/index.html`](./build/docs/javadoc/index.html).


### Analyse von Code Smells mit SonarQube
Code Smells könnenn mit [SonarQube](https://www.sonarqube.org/) analysiert werden. Da SonarQube für die Analyse
eine externe Engine (sprich eine externe Instanz der Software) benötigt, muss diese zuerst gestartet werden.

Am einfachsten, kann dies mit Docker umgesetzt werden. Nachfolgende Schritte zeigen, wie SonarQube mit Docker verwendet werden kann:

1. [Docker](https://www.docker.com/get-docker) installieren
2. `docker run -d --name sonarqube -p 9000:9000 -p 9092:9092 sonarqube:7.1-alpine`

Dies started SonarQube auf [`http://localhost:9000`](http://localhost:9000) (wenn es nicht geht einen moment warten, bis SonarQube gestartet ist). 

Es kann dann mit dem Benutzernamen `admin` und dem Passwort `admin` über den Knopf `Login` eingelogged werden.
Nach dem Login wird SonarQube einen Welcome-Assistenten anzeigen, in welchem ein Token für die Verbindung zwischen dem Gradle-Agent lokal und der Engine in Docker generiert wird.
Dazu muss einfach ein Namen für den Token eingegeben werden, beispielsweise `ftoop-dame`. Ist der Token generiert, sollte dieser an einem sicheren Ort **abgespeichert** werden, da er später noch gebraucht wird!


Anschliessend kann eine Analye mit SonarQube durchgeführt werden:
1. `./gradlew sonarqube -Dsonar.login=<token>` (wo <token> mit dem vorhin generierten Token ersetzt werden muss)
2. Die Ergebnisse der Analyse können unter [`http://localhost:9000/dashboard?id=Dame`](http://localhost:9000/dashboard?id=Dame) angesehen werden.
3. Wenn die Code Smells und/oder Bugs behoben wurden, kann der erste Befehl nocheinmals für eine Neuanalyse aufgerufen werden.

Sobald die Analyse mit SonarQube beendet ist, kann der Docker-Container wieder heruntergefahren werden: `docker stop sonarqube`.

### Verfügbare Gradle-Tasks anzeigen
```
./gradlew tasks
```
