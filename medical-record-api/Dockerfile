FROM pytorch/pytorch:latest

ENV TZ=Asia/Taipei
ADD ./requirements.txt /app/scripts/requirements.txt

RUN apt update && apt install ffmpeg cython3 -y
RUN apt-get update && apt-get install -y tzdata
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN apt-get install -y git

RUN pip install --no-cache-dir -r /app/scripts/requirements.txt

WORKDIR /app/scripts
