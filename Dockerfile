FROM ubuntu:latest

LABEL author="David Orme <djo@coconut-palm-software.com>"

RUN apt-get -y update && apt-get -y install openjdk-8-jdk-headless curl
RUN apt-get -y clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*
RUN bash -c "cd /usr/bin && curl -fsSLo boot https://github.com/boot-clj/boot-bin/releases/download/latest/boot.sh && chmod 755 boot"

ENV BOOT_AS_ROOT yes

CMD ["boot", "web-dev"]