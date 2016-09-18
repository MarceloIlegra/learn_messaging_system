var kafka = require('kafka-node');

var client = new kafka.Client();
var payloads = [{ topic: 'my-replicated-topic', partition: 0, fromOffset: -1 }];
var options = {
    groupId: 'kafka-node-group',//consumer group id, default `kafka-node-group`
    // Auto commit config
    autoCommit: false,
    autoCommitIntervalMs: 0,
    // The max wait time is the maximum amount of time in milliseconds to block waiting if insufficient data is available at the time the request is issued, default 100ms
    fetchMaxWaitMs: 100,
    // This is the minimum number of bytes of messages that must be available to give a response, default 1 byte
    fetchMinBytes: 1,
    // The maximum bytes to include in the message set for this partition. This helps bound the size of the response.
    fetchMaxBytes: 1024 * 1024,
    // If set true, consumer will fetch message from the given offset in the payloads
    fromOffset: false,
    // If set to 'buffer', values will be returned as raw buffer objects.
    encoding: 'utf8'
};

/* Print latest offset. */
var offset = new kafka.Offset(client);

offset.fetch([{ topic: 'my-replicated-topic', partition: 0, time: -1 }], function (err, data) {
        var latestOffset = data['my-replicated-topic']['0'][0];
        console.log("Consumer current offset: " + latestOffset);
});

var consumer = new kafka.Consumer(client, payloads, options);

consumer.on('message', function (message) {
    console.log('\n------------------\n');
    console.log(message);
    consumer.commit(function(err, data){
      console.log(data);
      console.log(err);
    })
    console.log('------------------');
});
