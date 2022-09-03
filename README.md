# Project Activity Tracking

![Version](https://img.shields.io/badge/version-1.2.0-success)
![License](https://img.shields.io/github/license/Phantonull/ProjectActivityTracker)
![Stars](https://img.shields.io/github/stars/PhantoNull/ProjectActivityTracker)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=PhantoNull_ProjectActivityTracker&metric=bugs)](https://sonarcloud.io/dashboard?id=UnimibSoftEngCourse2022_progetto-monopoly-1-gangoffour2)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=PhantoNull_ProjectActivityTracker&metric=code_smells)](https://sonarcloud.io/dashboard?id=UnimibSoftEngCourse2022_progetto-monopoly-1-gangoffour2)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=PhantoNull_ProjectActivityTracker&metric=coverage)](https://sonarcloud.io/dashboard?id=UnimibSoftEngCourse2022_progetto-monopoly-1-gangoffour2)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=PhantoNull_ProjectActivityTracker&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=UnimibSoftEngCourse2022_progetto-monopoly-1-gangoffour2)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=PhantoNull_ProjectActivityTracker&metric=ncloc)](https://sonarcloud.io/dashboard?id=UnimibSoftEngCourse2022_progetto-monopoly-1-gangoffour2)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=PhantoNull_ProjectActivityTracker&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=UnimibSoftEngCourse2022_progetto-monopoly-1-gangoffour2)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=PhantoNull_ProjectActivityTracker&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=UnimibSoftEngCourse2022_progetto-monopoly-1-gangoffour2)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=PhantoNull_ProjectActivityTracker&metric=security_rating)](https://sonarcloud.io/dashboard?id=UnimibSoftEngCourse2022_progetto-monopoly-1-gangoffour2)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=PhantoNull_ProjectActivityTracker&metric=sqale_index)](https://sonarcloud.io/dashboard?id=UnimibSoftEngCourse2022_progetto-monopoly-1-gangoffour2)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=PhantoNull_ProjectActivityTracker&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=UnimibSoftEngCourse2022_progetto-monopoly-1-gangoffour2)

![Rationence.png](src/main/resources/public/images/Logo.png?raw=true)


## Cos'è 

PAT(Project Activity Tracking) è un software per il tracciamento delle attività di consulenti.


## Come eseguire il progetto

### Prerequisiti

- [Java 17](https://www.oracle.com/it/java/technologies/javase/jdk17-archive-downloads.html)
- [Maven](https://maven.apache.org/install.html) (3.1 or >)
- [Git](https://git-scm.com/downloads)

### Passi

- `git clone https://github.com/PhantoNull/ProjectActivityTracker.git`: Esegue la clone del repository localmente.

*I seguenti comandi sono da eseguire all'interno della root del repository*

- `mvn clean package`: Esegue la build del progetto e i relativi test.
  Se ha successo copia i file generati in `target/`, dove `target/` è la cartella in cui risiedono i file compilati del server.
  Infine, crea il pacchetto .jar completo.

- `java -jar target/PAT-VERSION.jar`: Esegue il file .jar generato in precedenza.
  `VERSION` è il numero di versione presente nel file `pom.xml` alla path `project.version`.
  Attualmente è `1.2`.

- Il server è ora accessibile all'indirizzo `localhost`.
