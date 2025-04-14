# Certy
A certificate validation tool. Uses Microservice architecture. Implements factory, singleton and adaptor design patterns. An OOAD project


### Validation Engine
To test the validation service, run `server.sh` first and then `run.sh` (as a separate process).
To run CertificateTest script, execute `java -cp "out:lib/bcprov-jdk18on-1.80.jar" CertificateTest`

#### Compile source
From Validation Service root: `cd Validation`
```bash
javac -cp "lib/bcprov-jdk18on-1.80.jar" -d out src/*.java
```

