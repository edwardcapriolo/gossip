Running The Examples
====================

Teknek Gossip is designed to run as a library embedded by other applications. These examples illustrate basic cluster
membership, CRDT data propagation, and datacenter/rack-aware behavior.

Initial Setup
-------------

Install Java and Maven, then build the project from the repository root:

```sh
mvn install -DskipTests
```

Basic Node Example
------------------

Run a single node:

```sh
cd gossip-examples
mvn exec:java -Dexec.mainClass=io.teknek.gossip.examples.StandAloneNode -Dexec.args="udp://localhost:10000 0 udp://localhost:10000 0"
```

Run a second node in another terminal:

```sh
cd gossip-examples
mvn exec:java -Dexec.mainClass=io.teknek.gossip.examples.StandAloneNode -Dexec.args="udp://localhost:10001 1 udp://localhost:10000 0"
```

Run a third node:

```sh
cd gossip-examples
mvn exec:java -Dexec.mainClass=io.teknek.gossip.examples.StandAloneNode -Dexec.args="udp://localhost:10002 2 udp://localhost:10000 0"
```

CRDT Example
------------

Run three instances of `io.teknek.gossip.examples.StandAloneNodeCrdtOrSet` with the same arguments as the basic node
example. Commands typed into any node update local state and then propagate through the cluster.

Supported commands:

```text
a string
r string
```

Datacenter And Rack Example
---------------------------

`io.teknek.gossip.examples.StandAloneDatacenterAndRack` accepts two additional arguments: datacenter id and rack id.

Example:

```sh
cd gossip-examples
mvn exec:java -Dexec.mainClass=io.teknek.gossip.examples.StandAloneDatacenterAndRack -Dexec.args="udp://localhost:10000 0 udp://localhost:10000 0 1 2"
```
