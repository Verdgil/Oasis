const express = require('express');
const router = express.Router();

function logErrors(err, req, res, next) {
    console.error(err.stack);
    next(err);
}

function clientErrorHandler(err, req, res, next) {
    if (req.xhr) {
        res.status(500).json({error: ''});
    } else {
        next(err);
    }
}

function errorHandler(err, req, res, next) {
    res.status(500);
    res.render('error', { error: err });
}

router.use(logErrors);
router.use(clientErrorHandler);
router.use(errorHandler);
module.exports = router;