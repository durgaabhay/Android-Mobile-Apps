const mongoose = require('mongoose');

const feedbackSchema = mongoose.Schema({
    _id : mongoose.Schema.Types.ObjectId,
    phoneNumber : String,
    feedback : String
});

module.exports = mongoose.model('UserFeedback',feedbackSchema);