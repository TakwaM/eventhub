const amqp = require("amqplib");
const Analytics = require("./models/Analytics");

async function startRabbitMQ() {
  try {
    const connection = await amqp.connect(process.env.RABBITMQ_URL);
    const channel = await connection.createChannel();

    const queue = "analytics.queue";
    await channel.assertQueue(queue, { durable: true });

    console.log("📡 Waiting for messages in queue:", queue);

    channel.consume(queue, async (msg) => {
      try {
        // 🔥 Correction : double parsing
        let raw = JSON.parse(msg.content.toString());
        let data = typeof raw === "string" ? JSON.parse(raw) : raw;

        console.log("📥 Analytics received:", data);

        // Vérification minimale
        if (!data.eventId || !data.userId || !data.action) {
          console.log("⏭️ Ignored non-analytics message");
          return channel.ack(msg);
        }

        // 🔥 Correction timestamp
        let cleanTimestamp = new Date();
        if (data.timestamp) {
          cleanTimestamp = new Date(data.timestamp.split('.')[0] + "Z");
        }

        await Analytics.create({
          eventId: data.eventId,
          eventName: data.eventName,
          userId: data.userId,
          userName: data.userName,
          timestamp: cleanTimestamp,
          action: data.action
        });

        console.log("📊 Analytics saved");
        channel.ack(msg);

      } catch (err) {
        console.error("❌ Error processing message:", err);
        channel.ack(msg);
      }
    });

  } catch (err) {
    console.error("❌ RabbitMQ connection error:", err);
  }
}

module.exports = startRabbitMQ;
