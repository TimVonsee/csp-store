# CSPStore
This application stores all FIX messages received from the CSP.
## Dependencies
Note that the dependecies in the `./libs/` folder need to be manually added to the class path (or let the IDE do this).
Other dependecies are managed through Maven.

## Classes

- **CSPStoreApplication**, this class bootstraps the CSP writer application
    1. Creates a connection to the CSP device/or local file (in our case just the local file but parsing remains the same)
    2. Creates a connection to the Cassandra Cluster
    3. Creates a DFA (either stroing *all* data (fix) or bid, trade or ask)
- **CassandraFactory**, responsible for configuring the Cassandra network connection
- **DfaFactory**, responsible configuring the DFA that parse and persist the data
    - **FixDfaFactory**, setups a DFA based on tokens that are dynamically loaded from `tokens.properties`
        - **FixMessage** This class used for storage as the DFA transitions through it states and stores the data. Note, that this does not use getter or setters (violating encapsulation) but is reused each time (when in its final state the DFA "resets" all data)
        - **EndOfFixCommand**  This class is used when the DFA arrives in its final state and thus successfully parsed a FIX message. The FixMessage contains the tokens and data and is batched
            - When the batch limited is reached all objects are persisted through a bulkloader
        - **BulkLoader** a interface is used to decouple driver specific code from the main application
            - **CassandraBulkLoader** batch of insert are persisted to the Cassandra cluster asynchronously
       