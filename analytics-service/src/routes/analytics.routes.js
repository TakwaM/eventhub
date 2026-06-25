const express = require('express');
const Analytics = require('../models/Analytics');

const router = express.Router();

// Ajouter une entrée de test
router.post('/add', async (req, res) => {
  try {
    const entry = await Analytics.create(req.body);
    res.json(entry);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Récupérer toutes les entrées
router.get('/all', async (req, res) => {
  try {
    const data = await Analytics.find().sort({ timestamp: -1 });
    res.json(data);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

module.exports = router;
