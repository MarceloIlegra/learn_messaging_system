var kafka = require('kafka-node'),
  Consumer = kafka.Consumer,
  client = new kafka.Client(),
  consumer = new Consumer(
    client,
    [{ topic: 'my-replicated-topic', partition: 0 }],
    {autoCommit: false}
  );

consumer.on('message', function (message) {
    console.log(message);
});
