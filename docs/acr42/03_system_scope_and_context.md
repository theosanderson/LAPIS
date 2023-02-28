# System Scope and Context

![systemScopeAndContextBusiness](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/GenSpectrum/LAPIS/LAPISV2Arc42/docs/architecture/images/systemScopeAndContextBusiness.puml)

LAPIS:
- is provisioned by the maintainer, which provides a configuration file
- can be accessed by the end-user through a REST interface
- contains two subprograms
  - LAPIS-api
  - SILO
 
Configuration file:
- contains build info for LAPIS

Sequence data on disk:
- data provided GISAID/Genbank
- data preprocessed by Nextstrain
    - aligns nucleotide data
    - assigns amino acids
    - determines insertions

| File                               | content                                                                         | example                |
|------------------------------------|---------------------------------------------------------------------------------|------------------------|
| nucleotide_sequences.fasta         | sequences are unsorted, not aligned, divisioned by strain name, charset (UTFA?) | >BLA_1234 \n UTFAFATUT |
| nucleotide_sequences_aligned.fasta | same as nucleotide_sequence.fasta but aligned to reference genome               | >BLA_1234 \n UTFAFATUT |
| aminoacid_sequences.fasta          | amino acids are unsorted, not aligned,                                          |                        |
| aminoacid_sequences_aligned.fasta  |                                                                                 |                        |
| metadata_and_quality_control.tsv   |                                                                                 |                        |
| nucleotide_insertions.tsv          |                                                                                 |                        |
