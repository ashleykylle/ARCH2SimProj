FROM openjdk:11-jdk

WORKDIR /app

ENV PATH=$PATH:/usr/lib/jvm/java-11-openjdk/bin

COPY . /app

RUN javac -d bin src/*.java

RUN apt-get update && \
    apt-get install -y nodejs && \
    apt-get install -y npm && \
    rm -rf /var/lib/apt/lists/*

RUN npm install

CMD node app.js