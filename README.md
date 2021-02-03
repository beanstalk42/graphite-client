# graphite-client

A kotlin client for [Graphite](https://graphiteapp.org/) to send data to [Carbon and Carbon-Relay](https://graphite.readthedocs.io/en/latest/carbon-daemons.html)   

cf. https://graphite.readthedocs.io/en/latest/feeding-carbon.html#the-plaintext-protocol

## usage:
```kotlin
val pickleClient = PickleGraphiteClient(tcpConnectionFactory("127.0.0.1", 2004))
pickleClient.send(Metric("beanstalk.pickle.value", 42, OffsetDateTime.now()))
```

Use `null` as timestamp for **now** (translates to `-1`).

## Default ports:

plain text: 2003

pickle: 2004
