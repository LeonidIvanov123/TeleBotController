FROM mysql:latest
EXPOSE 3306
ENV TZ="Europe/Moscow"
ENV MYSQL_ROOT_PASSWORD="IamBottelegramm"
#ENV MYSQL_ALLOW_EMPTY_PASSWORD=true
ENV MYSQL_DATABASE="myDBforbot"
ENV MYSQL_USER="BotApplication"
ENV MYSQL_PASSWORD="IamBottelegramm"
#put in createDBforbot.sql file for init BD for bot
COPY /createDBforbot.sql /docker-entrypoint-initdb.d/