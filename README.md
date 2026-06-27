# Teknek Gossip

Teknek Gossip is a small Java gossip library for cluster membership, liveness, and eventually-consistent data sharing. Gossip protocols let nodes periodically exchange their local view of the cluster until membership and data converge.

This project includes code derived from Apache Gossip (incubating), a retired Apache Incubator project. The code has been repackaged under `io.teknek.gossip` and modernized for current Java/Maven usage.

Original project: https://github.com/apache/incubator-retired-gossip

Runnable examples live in `gossip-examples`. This README focuses on embedding the library, using shared data, and choosing the available CRDTs.

## Basic Membership

To gossip, each node needs a local URI and one or more seed members. Seeds are only the initial contact points; nodes learn the rest of the cluster through gossip.

```java
GossipSettings settings = new GossipSettings();
String cluster = "example-cluster";
int seedNodes = 3;
List<Member> startupMembers = new ArrayList<>();

for (int i = 1; i <= seedNodes; i++) {
  URI uri = new URI("udp://127.0.0.1:" + (50000 + i));
  startupMembers.add(new RemoteMember(cluster, uri, "node-" + i));
}
```

Start several nodes and attach a membership listener:

```java
List<GossipManager> clients = new ArrayList<>();
int clusterMembers = 5;

for (int i = 1; i <= clusterMembers; i++) {
  URI uri = new URI("udp://127.0.0.1:" + (50000 + i));
  GossipManager manager = GossipManagerBuilder.newBuilder()
      .cluster(cluster)
      .uri(uri)
      .id("node-" + i)
      .gossipMembers(startupMembers)
      .gossipSettings(settings)
      .listener((member, state) -> System.out.println(member + " is " + state))
      .build();
  manager.init();
  clients.add(manager);
}
```

Later, each `GossipManager` exposes live and dead member snapshots:

```java
GossipManager manager = clients.get(0);
List<LocalMember> live = manager.getLiveMembers();
List<LocalMember> dead = manager.getDeadMembers();
```

Always shut nodes down when done:

```java
for (GossipManager manager : clients) {
  manager.shutdown();
}
```

## Usage With A Settings File

For a simple local node setup, create a JSON file like this:

```json
[{
  "cluster": "9f1e6ddf-8e1c-4026-8fc6-8585d0132f77",
  "id": "447c5bec-f112-492d-968b-f64c8e36dfd7",
  "uri": "udp://127.0.0.1:50001",
  "properties": {},
  "gossip_interval": 1000,
  "cleanup_interval": 10000,
  "window_size": 1000,
  "minimum_samples": 5,
  "convict_threshold": 2.6,
  "distribution": "exponential",
  "members": [
    {"cluster": "9f1e6ddf-8e1c-4026-8fc6-8585d0132f77", "uri": "udp://127.0.0.1:50000"}
  ]
}]
```

Fields:

* `cluster` is the cluster name. Nodes only exchange data with members in the same cluster.
* `id` is this node's unique id. Any stable string is valid; UUIDs are common.
* `uri` is this node's UDP address.
* `gossip_interval` is how often, in milliseconds, the node gossips membership and data.
* `cleanup_interval` is the dead-member cleanup interval in milliseconds.
* `window_size`, `minimum_samples`, `convict_threshold`, and `distribution` configure failure detection.
* `properties` is a string map used by replication policies such as datacenter-aware gossip.
* `members` is the initial seed member list.

Start and stop the node:

```java
StartupSettings startup = StartupSettings.fromJSONFile(new File("node_settings.json"));
GossipManager manager = GossipManagerBuilder.newBuilder()
    .startupSettings(startup)
    .build();
manager.init();

// later
manager.shutdown();
```

## Data Model

Teknek Gossip supports two data scopes.

`SharedDataMessage` stores cluster-wide data keyed by `key`. Every node eventually sees the same merged value for that key, subject to replication rules and expiration. This is the correct place for CRDTs.

`PerNodeDataMessage` stores data under `(nodeId, key)`. Each node owns its own row of data. Updates from the same node/key are last-write-wins by timestamp. This is useful for node-local metadata such as load, address hints, capabilities, or graceful shutdown state.

Both message types include:

* `key`: application-level key.
* `payload`: value to gossip. For CRDT shared data this must implement `Crdt` when using `GossipManager.merge(...)`.
* `timestamp`: application timestamp used by non-CRDT last-write-wins paths.
* `expireAt`: wall-clock expiration time in milliseconds since epoch. Use `Long.MAX_VALUE` for no practical expiration.
* `replicable`: optional replication policy, such as whitelist/blacklist/datacenter-aware rules.

## Shared Data

Use shared data when all nodes should converge on one value for a key.

For non-CRDT payloads, `gossipSharedData(...)` stores the latest message by timestamp:

```java
SharedDataMessage message = new SharedDataMessage();
message.setKey("cluster/config/version");
message.setPayload("2026-06-27");
message.setTimestamp(System.currentTimeMillis());
message.setExpireAt(Long.MAX_VALUE);

manager.gossipSharedData(message);
```

Read it back:

```java
SharedDataMessage current = manager.findSharedGossipData("cluster/config/version");
Object value = current == null ? null : current.getPayload();
```

Subscribe to shared-data changes:

```java
manager.registerSharedDataSubscriber((key, oldValue, newValue) -> {
  System.out.println("shared data changed: " + key + " " + oldValue + " -> " + newValue);
});
```

For CRDT payloads, prefer `merge(...)`. It requires a `Crdt` payload and merges with the existing shared value for the key:

```java
SharedDataMessage message = new SharedDataMessage();
message.setKey("cluster/features");
message.setPayload(new OrSet<>("tp", "jq4"));
message.setTimestamp(System.currentTimeMillis());
message.setExpireAt(Long.MAX_VALUE);

manager.merge(message);

@SuppressWarnings("unchecked")
OrSet<String> features = (OrSet<String>) manager.findCrdt("cluster/features");
Set<String> enabled = features == null ? Set.of() : features.value();
```

## Per-Node Data

Use per-node data when each node publishes its own value independently.

```java
PerNodeDataMessage message = new PerNodeDataMessage();
message.setKey("rank/endpoint");
message.setPayload("http://10.0.0.12:8080");
message.setTimestamp(System.currentTimeMillis());
message.setExpireAt(System.currentTimeMillis() + 60_000);

manager.gossipPerNodeData(message);
```

The manager fills in this node's id. Other nodes can read the value by node id and key:

```java
PerNodeDataMessage endpoint = manager.findPerNodeGossipData("node-3", "rank/endpoint");
String value = endpoint == null ? null : (String) endpoint.getPayload();
```

Subscribe to per-node data changes:

```java
manager.registerPerNodeDataSubscriber((nodeId, key, oldValue, newValue) -> {
  System.out.println(nodeId + " changed " + key + " from " + oldValue + " to " + newValue);
});
```

## CRDT Basics

All CRDTs implement `Crdt<SetType, MergeReturnType>`:

```java
MergeReturnType merge(MergeReturnType other);
SetType value();
MergeReturnType optimize();
```

Implementations are immutable from the caller's perspective: operations such as `add`, `remove`, `increment`, and `decrement` return new CRDT instances or builders used to create new instances. Store the returned instance in a `SharedDataMessage` and call `manager.merge(...)`.

The generic merge helper `CrdtBiFunctionMerge` merges two CRDT values of the same implementation class and handles `null` on either side. It is useful when integrating with generic maps or custom merge pipelines.

## GrowOnlySet

`GrowOnlySet<ElementType>` is a grow-only set. Merging is set union. Elements can never be removed.

Use it when data only accumulates: discovered capabilities, seen node ids, immutable feature flags, or monotonic facts.

```java
GrowOnlySet<String> a = new GrowOnlySet<>(Set.of("gpu", "jq4"));
GrowOnlySet<String> b = new GrowOnlySet<>(Set.of("tp"));
GrowOnlySet<String> merged = a.merge(b);

assert merged.value().equals(Set.of("gpu", "jq4", "tp"));
```

To publish:

```java
SharedDataMessage message = new SharedDataMessage();
message.setKey("cluster/capabilities");
message.setPayload(new GrowOnlySet<>(Set.of("gpu-output-projection")));
message.setTimestamp(System.currentTimeMillis());
message.setExpireAt(Long.MAX_VALUE);
manager.merge(message);
```

## TwoPhaseSet

`TwoPhaseSet<ElementType>` supports add and remove, but each element can only be added once and removed once. Once removed, the same element cannot be re-added.

Use it when removals are final: one-time task completion, revoked ids that must not return, or historical membership in a closed workflow.

```java
TwoPhaseSet<String> set = new TwoPhaseSet<String>()
    .add("rank-0")
    .add("rank-1")
    .remove("rank-1");

assert set.value().equals(Set.of("rank-0"));
```

## OrSet

`OrSet<E>` is an observed-remove set. Adds attach unique UUID tags to elements. Removes tombstone the tags that have been observed locally. This allows re-add after remove, because a later add creates a new UUID tag.

Use it as the general-purpose mutable set CRDT when concurrent add/remove behavior matters and re-adds must be allowed.

```java
OrSet<String> first = new OrSet<>("a", "b");
OrSet<String> second = first.remove("a");
OrSet<String> third = second.add("a");

assert third.value().contains("a");
```

Batch mutations can be built with `OrSet.Builder`:

```java
OrSet<String> current = (OrSet<String>) manager.findCrdt("cluster/features");
if (current == null) {
  current = new OrSet<>();
}

OrSet<String> updated = new OrSet<>(current, new OrSet.Builder<String>()
    .add("tensor-parallel")
    .remove("experimental-mode"));
```

## LwwSet

`LwwSet<ElementType>` is a last-write-wins element set. Adds and removes store timestamps from `SystemClock.nanoTime()`. Merge keeps the greatest add and remove timestamps per element. If add and remove timestamps are equal, add wins.

Use it when you want compact set state and your nodes have sufficiently synchronized clocks. Significant clock drift can cause data loss or unexpected winners.

```java
LwwSet<String> set = new LwwSet<String>()
    .add("model-a")
    .remove("model-a")
    .add("model-b");

Set<String> live = set.value();
```

Prefer `OrSet` if you cannot rely on clocks or need safer concurrent add/remove semantics.

## MaxChangeSet

`MaxChangeSet<ElementType>` stores a change counter per element. Odd counts mean present; even counts mean absent. Merge keeps the maximum count per element.

Use it when changes are infrequent relative to the gossip interval and you want compact state without tombstone sets.

```java
MaxChangeSet<String> set = new MaxChangeSet<String>()
    .add("rank-0")
    .remove("rank-0")
    .add("rank-0");

assert set.value().contains("rank-0");
```

Concurrent independent changes to the same element can collapse to the same counter value, so choose `OrSet` for safer high-contention set membership.

## GrowOnlyCounter

`GrowOnlyCounter` is a G-counter. Each node has its own monotonically increasing slot. Merge keeps the max slot value per node. The value is the sum of all slots.

Use it for monotonic counts: processed requests, completed tasks, bytes sent, or positive-only cluster metrics.

```java
GrowOnlyCounter current = (GrowOnlyCounter) manager.findCrdt("metrics/requests");
GrowOnlyCounter.Builder builder = new GrowOnlyCounter.Builder(manager).increment(5L);

GrowOnlyCounter next = current == null
    ? new GrowOnlyCounter(builder)
    : new GrowOnlyCounter(current, builder);

SharedDataMessage message = new SharedDataMessage();
message.setKey("metrics/requests");
message.setPayload(next);
message.setTimestamp(System.currentTimeMillis());
message.setExpireAt(Long.MAX_VALUE);
manager.merge(message);
```

Read the converged counter:

```java
GrowOnlyCounter counter = (GrowOnlyCounter) manager.findCrdt("metrics/requests");
long total = counter == null ? 0L : counter.value();
```

## PNCounter

`PNCounter` is a positive-negative counter. It contains two grow-only counters: one for increments and one for decrements. The value is `positive - negative`.

Use it for counts that can increase and decrease: in-flight requests, available slots, queue depth, or cluster resource deltas.

```java
PNCounter current = (PNCounter) manager.findCrdt("metrics/inflight");
PNCounter.Builder builder = new PNCounter.Builder(manager)
    .increment(10)
    .decrement(3);

PNCounter next = current == null
    ? new PNCounter(builder)
    : new PNCounter(current, builder);

SharedDataMessage message = new SharedDataMessage();
message.setKey("metrics/inflight");
message.setPayload(next);
message.setTimestamp(System.currentTimeMillis());
message.setExpireAt(Long.MAX_VALUE);
manager.merge(message);
```

Read it:

```java
PNCounter counter = (PNCounter) manager.findCrdt("metrics/inflight");
long inflight = counter == null ? 0L : counter.value();
```

## Serialization

CRDT payloads are serialized through Jackson. `CrdtModule` registers mixins for the built-in CRDTs and related replication/lock vote classes:

* `OrSet`
* `GrowOnlySet`
* `GrowOnlyCounter`
* `PNCounter`
* `LwwSet`
* `MaxChangeSet`
* `TwoPhaseSet`

If you use the provided protocol manager and gossip transport, the module is wired in by the library. If you serialize gossip data yourself, register `new CrdtModule()` on your `ObjectMapper`.

```java
ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new CrdtModule());
```

## Replication Controls

Both `SharedDataMessage` and `PerNodeDataMessage` can carry a `Replicable` policy. Policies decide whether a data message should be sent to a destination member.

Built-in policies include:

* `AllReplicable`: send to all members.
* `WhiteListReplicable`: send only to listed members.
* `BlackListReplicable`: send to all except listed members.
* `DataCenterReplicable`: send only to members in the same datacenter property.

Example:

```java
SharedDataMessage message = new SharedDataMessage();
message.setKey("rank/assignment");
message.setPayload(new OrSet<>("rank-0"));
message.setTimestamp(System.currentTimeMillis());
message.setExpireAt(Long.MAX_VALUE);
message.setReplicable(new DataCenterReplicable<>());
manager.merge(message);
```

## Choosing A CRDT

Use `GrowOnlySet` when elements only ever accumulate.

Use `TwoPhaseSet` when removal is final and re-add should be impossible.

Use `OrSet` for general mutable set membership when re-adds and concurrent operations are expected.

Use `LwwSet` when compact state is important and clocks are trustworthy enough.

Use `MaxChangeSet` when updates to the same element are infrequent compared to gossip propagation.

Use `GrowOnlyCounter` for positive-only monotonic metrics.

Use `PNCounter` for counters that need increments and decrements.

## Examples

The `gossip-examples` module includes `io.teknek.gossip.examples.StandAloneNodeCrdtOrSet`, which demonstrates shared CRDT data with `OrSet` and `GrowOnlyCounter`.

Run three instances as described in `gossip-examples/README.md`, then type commands into any node:

```text
a value   add value to the shared OrSet
r value   remove value from the shared OrSet
g 5       increment the shared GrowOnlyCounter by 5
l abc     listen for OrSet changes
l def     listen for counter changes
```
