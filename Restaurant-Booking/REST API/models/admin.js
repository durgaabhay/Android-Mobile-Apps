const mongoose = require('mongoose');

const adminSchema = new mongoose.Schema({
   _id: mongoose.Schema.Types.ObjectId,
   userName: {type: String},
   password: {type: String},
   userRole: {
       type: String,
       enum: ['FRONTDESK','MANAGER']
   }
});

module.exports = mongoose.model('Admin', adminSchema);