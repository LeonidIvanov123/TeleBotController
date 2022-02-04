FROM openjdk:17
#COPY out/production/TeleBotController/ru/ivan/leon /tmp
EXPOSE 3306
COPY out/artifacts/TeleBotController_jar /tmp
WORKDIR /tmp
#RUN en_US.UTF-8
#RUN LANG=en_US.UTF-8 LC_MESSAGES=POSIX
CMD update-locale LANG=ru_RU.UTF-8

CMD java -jar TeleBotController.jar

#C.UTF-8