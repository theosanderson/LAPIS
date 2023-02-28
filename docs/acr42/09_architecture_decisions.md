# Architecture Decisions

##Inhouse database

**Context**
The metadata and sequence data have to be stored in a database to query for specific entries.

The stored data should be as small as possible, because we want to store more than ??? sets of metadata-sequence pairs. Each sequence has in uncrompressed from a size of (???). 

The sequence data access should be fast. There are many queries, such that on the n-th position of each sequence there is a specific nucleotide.

**Decision**
We write our own database (SILO), that can store the specific set of data in a columnar style and utilizes compression on each column.

**Consequences**
The database must me maintained and tested, which takes up development time.



##Queries to SILO 

**Context**
?

**Decision**
SILO will be accessed by JSON queries.

**Consequences**
A conversion to standart SQL is complicated.



##The main programming language for SILO

**Context**
???


**Decision**
SILO will be written in C++.

**Consequences**




##The main programming language for LAPIS

**Context**
LAPIS should be an REST-API to receive user queries, analyze and convert them, so it can a database to retreive the ordered data.

The existing version of LAPIS uses Java. Java has problems ...?

Possible soultions are:

Typescript:
- pros:
    - frontend in TS
    - easier packet manager
- cons:

Kotlin:
- pros:
    - easy transition of existing code (Java)
    - can be used with simple Spring boot
- cons:
    - built tool gradle is harder to use

**Decision**
LAPIS will be written in Kotlin and use Spring Boot for the API.

**Consequences**
???


##LAPIS and SILO have their own github repository

**Context**

**Decision**

**Consequences**
