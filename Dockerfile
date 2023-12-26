FROM openjdk:17-alpine

COPY /target/Circus_bot-v1.jar /tg_bot/Circus_bot.jar

ENTRYPOINT ["java", "-jar", "/tg_bot/Circus_bot.jar"]