import json
import time

import torchaudio
import whisper
import torch

import numpy as np
from sklearn.cluster import AgglomerativeClustering
import datetime

import sys
sys.path.append("./")

from utils.feature_embed import SpeakerEmbedding
from utils.sql_connector import SQLConnector

# max_speakers = 6
# language = "zh"
# model_size = "medium"
#
# start_time = datetime.datetime.now() - datetime.timedelta(minutes=1)
#
# previous_file = "../data/objects/23b064ed-c66a-45d9-8a9d-1ee9efed7af6"
# previous_time = start_time - (datetime.datetime.now() - datetime.timedelta(seconds=10))
#
# start_time = start_time - datetime.timedelta(seconds=10)
#
# audio_file = "../data/objects/23b064ed-c66a-45d9-8a9d-1ee9efed7af6"
#
# features = [
#     "../data/objects/xdd.npy",
#     "../data/objects/xdd.npy",
#     "../data/objects/xdd.npy",
#     "../data/objects/xdd.npy",
# ]
#
# speaker_name = [
#     "AAA",
#     "BBB",
#     "CCC",
#     "DDD"
# ]
# start_time = datetime.datetime.now()
#
# model = whisper.load_model(model_size, device='cuda' if torch.cuda.is_available() else 'cpu')
# speaking_embedding = SpeakerEmbedding()
#
# waveform, sample_rate = torchaudio.load(audio_file)
# previous_waveform, previous_sample_rate = torchaudio.load(previous_file)
#
# waveform = torch.cat([previous_waveform[:, int(previous_sample_rate * -previous_time.total_seconds()):], waveform], dim=1)
#
# # if sample_rate != 16000:
# #     sample_rate = 16000
# #     resampler = torchaudio.transforms.Resample(orig_freq=sample_rate, new_freq=16000)
# #     waveform = resampler(waveform)
# duration = waveform.shape[1] / sample_rate
# torchaudio.save("../data/.whisper_temp.wav", waveform, sample_rate)
#
# segments = model.transcribe("../data/.whisper_temp.wav", language=language)['segments']
#
# embeddings = [np.load(f) for f in features]
# for segment in segments:
#     start = int(segment["start"] * sample_rate)
#     end = int(min(duration, segment["end"]) * sample_rate)
#
#     segment_waveform = waveform[:, start:end]
#
#     segment_waveform = speaking_embedding.average_channels(segment_waveform)
#     embedding = speaking_embedding.embedding_model(segment_waveform[None])
#
#     embeddings.append(embedding)
# embeddings = np.concatenate(embeddings, axis=0)
#
# clustering = AgglomerativeClustering(max_speakers).fit(embeddings)
# labels = clustering.labels_
#
# results = []
# end_duration = 0
#
# labels = labels[len(features):]
# for i in range(len(segments)):
#     if segments[i]["end"] < duration:
#
#         result = {
#             "speaker": None,
#             "start": (start_time + datetime.timedelta(seconds=round(segments[i]["start"]))).strftime("%m/%d/%YT%H:%M:%S"),
#             "end": (start_time + datetime.timedelta(seconds=round(segments[i]["end"]))).strftime("%m/%d/%YT%H:%M:%S"),
#             "text": segments[i]["text"],
#         }
#         if labels[i] < len(speaker_name):
#             result["speaker"] = speaker_name[labels[i]]
#         else:
#             result["speaker"] = f"Unknown {labels[i] - len(speaker_name) + 1}"
#
#         results.append(result)
#
#         print(result)

MAX_SPEAKERS = 6


def main():
    print("Task - Whisper Transcript is RUNNING")

    with open("configs/whisper-transcript.json", "r") as f:
        config = json.load(f)

    sql_connector = SQLConnector.get_connection()
    model = whisper.load_model(config['model_size'], device='cuda' if torch.cuda.is_available() else 'cpu')
    speaking_embedding = SpeakerEmbedding()

    while True:

        unprocessed_audio = sql_connector.query(
            "SELECT admission_id, audio_uid, previous_audio_uid, start_at, end_at "
            + "FROM transcript_audio "
            + "WHERE processed_at IS NULL "
            + "ORDER BY start_at "
            + "LIMIT 1"
        )

        if unprocessed_audio is None or len(unprocessed_audio) == 0:
            time.sleep(1)
            continue

        admission_id = unprocessed_audio[0][0]
        audio_path = "data/objects/" + unprocessed_audio[0][1]
        previous_audio_path = "data/objects/" + unprocessed_audio[0][2] if unprocessed_audio[0][2] is not None else None
        start_time = unprocessed_audio[0][3]

        features = sql_connector.query(
            "SELECT nf.feature_id, f.dim_uid "
            + "FROM nearby_feature AS nf "
            + "INNER JOIN feature AS f ON nf.feature_id = f.feature_id "
            + "WHERE nf.audio_uid = %s OR nf.audio_uid = %s "
            + "GROUP BY nf.feature_id, f.name, f.dim_uid "
            + "ORDER BY nf.feature_id",
            (unprocessed_audio[0][1], unprocessed_audio[0][2])
        )

        waveform, sample_rate = torchaudio.load(audio_path)
        duration = waveform.shape[1] / sample_rate

        if unprocessed_audio[0][2] is not None:
            previous_audio = sql_connector.query(
                "SELECT start_at, end_at "
                + "FROM transcript_audio "
                + "WHERE audio_uid = %s",
                (unprocessed_audio[0][2],)
            )[0]

            previous_duration = (previous_audio[1] - previous_audio[0]).total_seconds()
            start_time = start_time - datetime.timedelta(seconds=previous_duration)

            pre_waveform, pre_sample_rate = torchaudio.load(previous_audio_path)
            waveform = torch.cat([pre_waveform[:, int(pre_sample_rate * -previous_duration):], waveform], dim=1)
            duration = waveform.shape[1] / sample_rate

        torchaudio.save("data/.whisper_temp.wav", waveform, sample_rate)
        print("Task - Whisper Transcript is Transcribing...")
        segments = model.transcribe("data/.whisper_temp.wav", language=config['language'])['segments']

        embeddings = [np.load(f"data/objects/{str(col[1])}") for col in features]
        for segment in segments:
            start = int(segment["start"] * sample_rate)
            end = int(min(duration, segment["end"]) * sample_rate)

            segment_waveform = waveform[:, start:end]
            print(segment_waveform.shape)

            segment_waveform = speaking_embedding.average_channels(segment_waveform)
            embedding = speaking_embedding.embedding_model(segment_waveform)
            embeddings.append(embedding)

        labels = None
        if len(embeddings) > 0:
            embeddings = np.concatenate(embeddings, axis=0)
        
            if embeddings.shape[0] > 1:
                print("Task - Whisper Transcript is Clustering...")
                clustering = AgglomerativeClustering(min(config['max_speakers'], embeddings.shape[0])).fit(embeddings)
                labels = clustering.labels_

        results = []
        # start time to date string
        end_time = start_time.strftime("%m/%d/%YT%H:%M:%S")
        labels = labels[len(features):] if labels is not None else None
        for i in range(len(segments)):
            if segments[i]["end"] < duration:

                result = [
                    admission_id,
                    None,
                    (start_time + datetime.timedelta(seconds=round(segments[i]["start"]))).strftime("%m/%d/%YT%H:%M:%S"),
                    segments[i]["text"],
                    unprocessed_audio[0][1]
                ]
                end_time = (start_time + datetime.timedelta(seconds=round(segments[i]["end"]))).strftime("%m/%d/%YT%H:%M:%S"),
                if labels is not None and labels[i] < len(features):
                    result[1] = features[labels[i]][0]

                results.append(str(tuple(result)).replace("None", "NULL"))

        if len(results) != 0:
            sql_connector.execute(
                f"INSERT INTO transcript_record (admission_id, feature_id, datetime, content, audio_uid) VALUES {', '.join(results)}",
            )

        sql_connector.execute(
            "UPDATE transcript_audio SET end_at = %s, processed_at = NOW() WHERE audio_uid = %s",
            (end_time, unprocessed_audio[0][1])
        )

        print("Task - Whisper Transcript is DONE Successfully")


def run():
    main()


if __name__ == "__main__":
    main()
