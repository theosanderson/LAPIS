# Solution Strategy


**Requirement: Create user instance**

- A whole instance for a new virus specie can be generated from a single configuration file.
    - targets: Ease of use (Q:U2)

- Welches System wollen wir hier benutzen?

**Requirement: Read in data**
- write own preprocessing to process data
    - targets: Testability(Q:M5)?, User error protection (Q:U5)
    - attached to data storage
- provide validation to data
    - infer that sequence files are
        - correct fasta (Sequence characters, form)
        - aligned data has the same length
        - each sequence has only one entry
    - infer that metadata file
        - has fields, which are provided by config file
        - primary key is not empty and unique

**Requirement: Store data**
- write own database to store data and compress data (SILO)
    - targets: Time behaviour (Q:P1), Resource utilization (Q:P2), Scalability (Q:P4)
    - compresses sequence data
    - can filter for
        - metadata
        - mutations (A501T)
        - insertions of both aminoacids and nucleotides (ins_203:AAT)
        - allow maybe filter (some sequence reads are not distinct)
- add webinterface to database over http requests
    - easy to query database calls
    - request data with
        - filter 
        - action
            - aggregation (returns count occurrences which match filter)
            - details (returns meta data of sequences matching filter)
            - sequenceData (returns full sequence data)

**Requirement: Provide web access to data**

- use a REST API server, which handles user queries and translates them into database calls.
    - targets: Ease o use (Q:U1)
    - No specific knowlegde is necessary for calls to the api

- Provide caching of data for fast access
    - targets: Time behaviour (Q:P1)
    - Calls oftently repeat themselfes.

- Input validation returns specific error messages?
    - targets: User error protection (Q:U5)

**Requirement: Provide statistics**

- track incoming calls of the user
    - through which method or better left open?


