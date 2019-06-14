#!/bin/sh

# First parameter is the embeddings model, either:
# a) `n2c2-fasttext.bin` (manually generated by `train_embeddings.sh`);
# b) `BioWordVec_PubMed_MIMICIII_d200.bin` (downloaded from https://github.com/ncbi-nlp/BioSentVec)
# `vocab.txt` should be first generated by `VocabularyDumper.java`

fasttext print-word-vectors $1 < vocab.txt > vectors.tsv

# The `.vec` file loaded by deeplearning4j requires an extra line with the input dimensions.
LINES=27875
DIM=200
echo "$LINES $DIM" | cat - vectors.tsv > vectors.vec