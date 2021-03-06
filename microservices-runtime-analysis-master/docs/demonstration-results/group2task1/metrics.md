# Number of Services Involved in the Compound Service (NSIC)

## Description
Number of services involved in the compound service

## Interpretation
Value range: [0,14], lower is better

## Results

- mine: 8
- fanout: 7
- entry-cache: 1
- index: 1
- follow: 6
- timeline0: 6
- home: 7
- timeline1: 6
- search: 8
- post: 10
- reserve: 1
- api: 11
- timeline-shard: 1
- entry-store: 7


# Services Interdependence in the System (SIY)

## Description
Count of pairs of services which depend on each other

## Interpretation
Value range: [0,91], lower is better

## Result
2


# Absolute Importance of the Service (AIS)

## Description
Number of services which depend on a service

## Interpretation
Value range: [0,13], lower is better

## Results

- mine: 0
- fanout: 1
- entry-cache: 3
- index: 2
- follow: 5
- timeline0: 3
- home: 0
- timeline1: 3
- search: 0
- post: 1
- reserve: 3
- api: 0
- timeline-shard: 3
- entry-store: 2


# Absolute Dependence of the Service (ADS)

## Description
Number of other services a service depends on

## Interpretation
Value range: [0,13], lower is better

## Results

- mine: 2
- fanout: 1
- entry-cache: 0
- index: 0
- follow: 5
- timeline0: 2
- home: 3
- timeline1: 2
- search: 2
- post: 4
- reserve: 0
- api: 2
- timeline-shard: 0
- entry-store: 3


# Absolute Criticality of the Service (ACS)

## Description
Product of its absolute importance (AIS) and absolute dependence (ADS)

## Interpretation
Value range: [0,169], lower is better

## Results

- mine: 0
- fanout: 1
- entry-cache: 0
- index: 0
- follow: 25
- timeline0: 6
- home: 0
- timeline1: 6
- search: 0
- post: 4
- reserve: 0
- api: 0
- timeline-shard: 0
- entry-store: 6


# Number of Services (NS)

## Description
Number of services in the system

## Interpretation
Value range: [0,INF), lower is better

## Result
14


# Relative Coupling of Service (RCS)

## Description
Degree to which a service depends on the other services

## Interpretation
Value range: [0,100), lower is better

## Results

- mine: 14%
- fanout: 7%
- entry-cache: 0%
- index: 0%
- follow: 36%
- timeline0: 14%
- home: 21%
- timeline1: 14%
- search: 14%
- post: 28%
- reserve: 0%
- api: 14%
- timeline-shard: 0%
- entry-store: 21%


# Relative Importance of Service (RIS)

## Description
Degree to which the other services depend on a service

## Interpretation
Value range: [0,100), lower is better

## Results

- mine: 0%
- fanout: 7%
- entry-cache: 21%
- index: 14%
- follow: 36%
- timeline0: 21%
- home: 0%
- timeline1: 21%
- search: 0%
- post: 7%
- reserve: 21%
- api: 0%
- timeline-shard: 21%
- entry-store: 14%


# Service Coupling Factor (SCF)

## Description
Indicates the overall coupling in the system, the sum over all single coupling values is set in relation with the maximum couplings that could occur in the system

## Interpretation
Value range: [0,1], lower is better

## Result
0.14


# Weighted Service Interface Count (WSIC)

## Description
Weighted number of exposed interfaces or operations per service (all weights = 1.0)

## Interpretation
Value range: [0,INF), lower is better

## Results

- mine: 0.0
- fanout: 2.0
- entry-cache: 3.0
- index: 3.0
- follow: 2.0
- timeline0: 2.0
- home: 0.0
- timeline1: 2.0
- search: 0.0
- post: 1.0
- reserve: 2.0
- api: 0.0
- timeline-shard: 4.0
- entry-store: 2.0


# Service Composition Pattern (SCP)

## Description
Fraction of services which are composite

## Interpretation
Value range: [0,100], lower is better

## Result
71%


# Inverse of Average Number of Used Message (IAUM)

## Description
Ratio between number of services and message types

## Interpretation
Value range: (0,1], very low values should be avoided

## Result
0.61


# Service Interface Usage Cohesion (SIUC)

## Description
Quantifies cohesion of a service based on the number of operations invoked by every client

## Interpretation
Value range: (0,100], higher is better

## Results

- timeline1: 50%
- fanout: 100%
- post: 100%
- reserve: 100%
- entry-cache: 56%
- index: 50%
- follow: 50%
- timeline-shard: 75%
- timeline0: 50%
- entry-store: 50%


# Service Interface Data Cohesion (SIDC)

## Description
Quantifies cohesion of a service based on the number of operations sharing the same input parameter types

## Interpretation
Value range: [0,100], higher is better

## Results

- timeline1: 100%
- fanout: 0%
- post: 100%
- reserve: 100%
- entry-cache: 67%
- index: 67%
- follow: 100%
- timeline-shard: 100%
- timeline0: 100%
- entry-store: 100%


# Response for Operation (RFO)

## Description
Cardinality of the set of other service interfaces that can be executed in response to invocations with all possible parameters

## Interpretation
Value range: [0,INF), lower is better

## Results

- timeline1:
    - \[timeline:insert\]: 0
    - \[timeline:list\]: 1

- fanout:
    - \[fanout:entry\]: 1
    - \[info:entry\]: 1

- post:
    - \[post:entry\]: 1

- reserve:
    - \[reserve:remove\]: 0
    - \[reserve:create\]: 0

- entry-cache:
    - \[kind:entry, store:list\]: 0
    - \[kind:entry, store:save\]: 0
    - \[cache:true, kind:entry, store:\*\]: 0

- index:
    - \[info:entry\]: 0
    - \[search:insert\]: 0
    - \[search:query\]: 0

- follow:
    - \[follow:user\]: 2
    - \[follow:list\]: 1

- timeline-shard:
    - \[timeline:insert\]: 0
    - \[shard:0, timeline:\*\]: 0
    - \[shard:1, timeline:\*\]: 1
    - \[timeline:list\]: 1

- timeline0:
    - \[timeline:insert\]: 0
    - \[timeline:list\]: 1

- entry-store:
    - \[kind:entry, store:save\]: 1
    - \[kind:entry, store:list\]: 0

## Details

- follow (\[follow:user\]): \[kind:entry,store:\*, timeline:\*\]
- follow (\[follow:list\]): \[search:\*\]
- timeline-shard (\[shard:1, timeline:\*\]): \[follow:\*\]
- timeline-shard (\[timeline:list\]): \[follow:\*\]
- timeline1 (\[timeline:list\]): \[follow:\*\]
- timeline0 (\[timeline:list\]): \[follow:\*\]
- entry-store (\[kind:entry, store:save\]): \[info:entry\]
- post (\[post:entry\]): \[kind:entry,store:\*\]
- fanout (\[info:entry\]): \[follow:\*\]
- fanout (\[fanout:entry\]): \[follow:\*\]


# Total Response for Service (TRS)

## Description
Sum of RFO values for all operations

## Interpretation
Value range: [0,INF), lower is better

## Result
11


# Mean Absolute Importance/Dependence in the System (MAIDS)

## Description
Mean number of services which depend on a service / Mean number of services a service depends on

## Interpretation
Value range: [0,13], lower is better

## Result
1.86


# Mean Absolute Coupling in the System (MACS)

## Description
Mean number of services which depend on a service or a service depends on

## Interpretation
Value range: [0,26], lower is better

## Result
3.71


# Dynamic Relative Dependence of Service (DRDS)

## Description
Degree to which a service uses other services in terms of calls

## Interpretation
Value range: (0,100], higher is better

## Results

- mine:
    - entry-cache: 84%
    - entry-store: 16%

- timeline1:
    - reserve: 14%
    - follow: 86%

- search:
    - index: 50%
    - follow: 50%

- fanout:
    - follow: 100%

- post:
    - fanout: 28%
    - entry-cache: 28%
    - index: 28%
    - entry-store: 14%

- api:
    - post: 50%
    - follow: 50%

- follow:
    - timeline1: 16%
    - reserve: 16%
    - entry-cache: 13%
    - timeline-shard: 47%
    - timeline0: 8%

- timeline0:
    - reserve: 22%
    - follow: 78%

- entry-store:
    - timeline1: 13%
    - timeline-shard: 67%
    - timeline0: 20%

- home:
    - timeline1: 21%
    - timeline-shard: 67%
    - timeline0: 12%


# Dynamic Relative Importance of Service (DRIS)

## Description
Degree to which other services depend on a service in terms of calls

## Interpretation
Value range: (0,100], higher is better

## Results

- timeline1:
    - follow: 30%
    - entry-store: 10%
    - home: 60%

- fanout:
    - post: 100%

- post:
    - api: 100%

- reserve:
    - timeline1: 20%
    - follow: 60%
    - timeline0: 20%

- entry-cache:
    - mine: 57%
    - post: 28%
    - follow: 14%

- index:
    - search: 78%
    - post: 22%

- follow:
    - timeline1: 18%
    - search: 55%
    - fanout: 8%
    - api: 8%
    - timeline0: 11%

- timeline-shard:
    - follow: 27%
    - entry-store: 15%
    - home: 57%

- timeline0:
    - follow: 23%
    - entry-store: 23%
    - home: 54%

- entry-store:
    - mine: 44%
    - post: 56%


# Dynamic Relative Dependence of Service in the System (DRDSS)

## Description
Degree to which a service uses other services in terms of calls compared to all calls system-wide

## Interpretation
Value range: (0,100], lower is better

## Results

- mine:
    - entry-cache: 8%
    - entry-store: 1%

- timeline1:
    - reserve: 1%
    - follow: 4%

- search:
    - index: 13%
    - follow: 13%

- fanout:
    - follow: 2%

- post:
    - fanout: 4%
    - entry-cache: 4%
    - index: 4%
    - entry-store: 2%

- api:
    - post: 2%
    - follow: 2%

- follow:
    - timeline1: 2%
    - reserve: 2%
    - entry-cache: 2%
    - timeline-shard: 6%
    - timeline0: 1%

- timeline0:
    - reserve: 1%
    - follow: 3%

- entry-store:
    - timeline1: 1%
    - timeline-shard: 4%
    - timeline0: 1%

- home:
    - timeline1: 4%
    - timeline-shard: 14%
    - timeline0: 3%


# Dynamic Relative Importance of Service in the System (DRISS)

## Description
Degree to which other services depend on a service in terms of calls compared to all calls system-wide

## Interpretation
Value range: (0,100], lower is better

## Results

- timeline1:
    - follow: 2%
    - entry-store: 1%
    - home: 4%

- fanout:
    - post: 4%

- post:
    - api: 2%

- reserve:
    - timeline1: 1%
    - follow: 2%
    - timeline0: 1%

- entry-cache:
    - mine: 8%
    - post: 4%
    - follow: 2%

- index:
    - search: 13%
    - post: 4%

- follow:
    - timeline1: 4%
    - search: 13%
    - fanout: 2%
    - api: 2%
    - timeline0: 3%

- timeline-shard:
    - follow: 6%
    - entry-store: 4%
    - home: 14%

- timeline0:
    - follow: 1%
    - entry-store: 1%
    - home: 3%

- entry-store:
    - mine: 1%
    - post: 2%


# Cyclic Service Dependencies (CSD)

## Description
Number of cycles in the dependencies

## Interpretation
Value range: [0,18348340022], lower is better

## Result
0

## Details
