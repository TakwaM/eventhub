const express = require('express');
const Analytics = require('../models/Analytics');

const router = express.Router();

// 🔥 Stats par utilisateur
router.get('/users/:userId', async (req, res) => {
  try {
    const userId = req.params.userId;

    const reserved = await Analytics.countDocuments({
      userId,
      action: 'reservation_created'
    });

    const cancelled = await Analytics.countDocuments({
      userId,
      action: 'reservation_cancelled'
    });

    res.json({
      userId,
      reserved,
      cancelled
    });

  } catch (err) {
    console.error("❌ Error stats:", err);
    res.status(500).json({ error: err.message });
  }
});

module.exports = router;
