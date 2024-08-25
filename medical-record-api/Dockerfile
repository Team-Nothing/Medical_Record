FROM pytorch/pytorch:latest

ADD ./requirements.txt /app/scripts/requirements.txt

RUN apt-get update \
    && pip install --no-cache-dir -r /app/scripts/requirements.txt

WORKDIR /app/scripts
