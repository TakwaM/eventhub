require('dotenv').config();
const express = require('express');
const cors = require('cors');
const connectDB = require('./src/database');
const analyticsRoutes = require('./src/routes/analytics.routes');
const startRabbitMQ = require('./src/rabbitmq');

const app = express();
const port = process.env.PORT || 4000;

// Middlewares
app.use(cors());
app.use(express.json());

// Connect MongoDB
connectDB();

// 🔥 Lancer le consumer RabbitMQ
startRabbitMQ();

// Nettoyage des headers CORS parasites
app.use((req, res, next) => {
  try { res.removeHeader('Access-Control-Allow-Origin'); } catch (e) {}
  next();
});

// 🔥🔥🔥 CORRECTION ICI : bon chemin vers analytics-stats
const analyticsStatsRoutes = require('./src/routes/analytics-stats');
app.use('/analytics/stats', analyticsStatsRoutes);

// Health check
app.get('/analytics/health', (req, res) => {
  res.json({
    status: 'OK',
    service: 'analytics-service',
    time: new Date().toISOString()
  });
});

// Routes principales
app.use('/analytics', analyticsRoutes);

// Start server
app.listen(port, () => {
  console.log(`Analytics service listening on port ${port}`);
});