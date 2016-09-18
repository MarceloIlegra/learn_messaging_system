var kafka = require('kafka-node'),
  Consumer = kafka.Consumer,
  client = new kafka.Client(),
  consumer = new Consumer(
    client,
    [{ topic: 'my-replicated-topic', partition: 0 }],
    {autoCommit: true}
  );

consumer.on('message', function (message) {
    console.log(message);
});
