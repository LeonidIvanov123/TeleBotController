FROM mysql:latest

EXPOSE 3306
ENV TZ="Europe/Amsterdam"
#ENV MYSQL_ROOT_PASSWORD="IamBottelegramm"
#tmp Пустой пароль для roota
ENV MYSQL_ALLOW_EMPTY_PASSWORD=true
ENV MYSQL_DATABASE="myDBforbot"
ENV MYSQL_USER="BotApplication"
ENV MYSQL_PASSWORD="IamBottelegramm"

# mysql -h 172.17.0.2 -P 3306 -D myDBforbot -u BotApplication -p'IamBottelegramm'

#COPY out/artifacts/TeleBotController_jar /tmp
#WORKDIR /tmp
#RUN en_US.UTF-8
#RUN LANG=en_US.UTF-8 LC_MESSAGES=POSIX
#CMD update-locale LANG=ru_RU.UTF-8

#CMD java -jar TeleBotController.jar

#put in initializeBD.sql file and execute in dockerfile. init BD for bot
#COPY out/artifacts/initializeBD.sql /docker-entrypoint-initdb.d/
COPY out/artifacts/initializeBD.sql /tmp
WORKDIR /tmp
RUN mysql -u "BotApplication" -p "IamBottelegramm" initializeBD.sql

#C.UTF-8