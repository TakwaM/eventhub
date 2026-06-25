const mongoose = require('mongoose');

const AnalyticsSchema = new mongoose.Schema({
  eventId: Number,
  eventName: String,
  userId: String,
  userName: String,
  timestamp: Date,
  action: String
}, { timestamps: true });

module.exports = mongoose.model('Analytics', AnalyticsSchema);