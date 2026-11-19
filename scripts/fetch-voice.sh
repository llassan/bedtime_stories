#!/bin/sh
# Restores the files that are too large for git: the sherpa-onnx Android library
# and the Kokoro neural voice model (both Apache-2.0; espeak-ng-data is GPL-3.0).
set -e
cd "$(dirname "$0")/.."
mkdir -p app/libs app/src/main/assets/kokoro
curl -L -o app/libs/sherpa-onnx-1.13.8.aar https://github.com/k2-fsa/sherpa-onnx/releases/download/v1.13.8/sherpa-onnx-1.13.8.aar
curl -L -o /tmp/kokoro.tar.bz2 https://github.com/k2-fsa/sherpa-onnx/releases/download/tts-models/kokoro-int8-en-v0_19.tar.bz2
tar xjf /tmp/kokoro.tar.bz2 -C /tmp
cp -R /tmp/kokoro-int8-en-v0_19/model.int8.onnx /tmp/kokoro-int8-en-v0_19/voices.bin /tmp/kokoro-int8-en-v0_19/tokens.txt /tmp/kokoro-int8-en-v0_19/espeak-ng-data /tmp/kokoro-int8-en-v0_19/LICENSE app/src/main/assets/kokoro/
