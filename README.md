## Telegram Bot @CircusTicketBot

Booting steps:

*To bootstrap this project you need to have a Docker Desktop application on your PC.*

#### 1.The first step is to create/clone **docker-compose.yml** file.

#### 2. The second one is to open CLI and navigate to the directory with this file.

#### 3. The third point is to execute the `docker-compose up` command and wait for this application to start.

----------------------------------------------------------------------

### Notes:
    - Operator id could be configured by changing OPERATOR_TELEGRAM_ID environment value;

    - By executing command docker-compose two containers will be created and launched: 
    the bot itself and database (MongoDB in this case);

    - Make sure that port 27017 is application free on your machine. Otherwise 
    project wouldn't be started