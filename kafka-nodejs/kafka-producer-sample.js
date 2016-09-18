
var producer_options = {
    // Configuration for when to consider a message as acknowledged, default 1
    requireAcks: 1,
    // The amount of time in milliseconds to wait for all acks before considered, default 100ms
    ackTimeoutMs: 100,
    // Partitioner type (default = 0, random = 1, cyclic = 2, keyed = 3), default 0
    partitionerType: 2
};

var kafka = require('kafka-node'),
    client = new kafka.Client('localhost:2181/', 'nodejs-app-producer'),
    producer = new kafka.Producer(client, producer_options);

var messages = [0,1,2,3,4,5,6,7,8,9,10];

producer.on('ready', function(){
  for (var i = 0, len = messages.length; i < len; i++) {
    var payload = {
      topic: 'my-replicated-topic',
      messages: messages[i],
      partition: 0
    };
    producer.send([payload], function(err, data){
      console.log(data);
    })
  }
});

producer.on('error', function (err) {
  console.log(err);
})
