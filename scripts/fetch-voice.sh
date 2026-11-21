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

# The Hindi storytellers: Kokoro v1.0, shipped as the on-demand Play asset pack
# "hindivoice". Only the four Hindi voices are kept; the model expects all 54
# slots, so the others are written as zeros (they compress to almost nothing).
mkdir -p hindivoice/src/main/assets/kokoro-hi
curl -L -o /tmp/kokoro-v1.tar.bz2 https://github.com/k2-fsa/sherpa-onnx/releases/download/tts-models/kokoro-int8-multi-lang-v1_0.tar.bz2
tar xjf /tmp/kokoro-v1.tar.bz2 -C /tmp
V=/tmp/kokoro-int8-multi-lang-v1_0
cp "$V/model.int8.onnx" "$V/tokens.txt" "$V/LICENSE" hindivoice/src/main/assets/kokoro-hi/
python3 - "$V/voices.bin" hindivoice/src/main/assets/kokoro-hi/voices.bin <<'PY'
import sys
size = 510 * 256 * 4
data = open(sys.argv[1], "rb").read()
keep = (31, 32, 33, 34)  # hf_alpha, hf_beta, hm_omega, hm_psi
open(sys.argv[2], "wb").write(b"".join(data[i*size:(i+1)*size] if i in keep else bytes(size) for i in range(54)))
PY
