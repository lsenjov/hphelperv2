FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/hphelperv2.jar /hphelperv2/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/hphelperv2/app.jar"]
